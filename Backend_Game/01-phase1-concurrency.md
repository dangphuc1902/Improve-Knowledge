# 🔧 PHASE 1.1 — Concurrency & Multithreading (C++)

> **Timeline**: Month 1, tuần 1-3  
> **Mục tiêu**: Nắm vững concurrency từ hardware level đến production pattern  
> **Tài liệu chính**: *C++ Concurrency in Action* (Anthony Williams)

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### 1. Memory Model & std::atomic

#### Tại sao quan trọng với game server?
Trong game server, hàng nghìn player cùng tương tác. Nếu không hiểu memory model, bạn sẽ gặp **heisenbugs** — bugs chỉ xảy ra dưới high load, không reproduce được.

#### Core Concepts

**Memory Ordering** — Compiler và CPU có thể reorder instructions để tối ưu. Memory ordering controls visibility giữa các threads.

```cpp
// ❌ BUG: Không đảm bảo thread B thấy data = 42
// Compiler có thể reorder: ready = true TRƯỚC data = 42
std::atomic<bool> ready{false};
int data = 0;

// Thread A
data = 42;
ready.store(true, std::memory_order_relaxed);  // ❌ relaxed không guarantee ordering

// Thread B  
while (!ready.load(std::memory_order_relaxed)) {}
assert(data == 42);  // CÓ THỂ FAIL!
```

```cpp
// ✅ CORRECT: release-acquire đảm bảo ordering
// Thread A
data = 42;
ready.store(true, std::memory_order_release);  // ✅ mọi write trước đây visible

// Thread B
while (!ready.load(std::memory_order_acquire)) {}  // ✅ sync với release
assert(data == 42);  // LUÔN PASS
```

**6 Memory Orders — Khi nào dùng gì:**

| Order | Guarantee | Use case | Performance |
|-------|-----------|----------|-------------|
| `relaxed` | Chỉ atomicity, không ordering | Counter, stats | Fastest |
| `acquire` | Đọc thấy mọi write trước release | Consumer side | Fast |
| `release` | Flush mọi write trước store | Producer side | Fast |
| `acq_rel` | Cả acquire + release | Read-modify-write | Medium |
| `seq_cst` | Total ordering (default) | Khi cần simple correctness | Slowest |

> [!CAUTION]
> **Production bug thực tế**: Một game server dùng `relaxed` cho flag "player is in combat". Thread xử lý damage đọc flag = false trong khi thread khác vừa set = true. Kết quả: player mất item vì bị PK khi "không trong combat". Fix: đổi sang `acquire/release`.

#### Happens-Before Relationship
```
Thread A:                    Thread B:
  write(x = 1)                 
  atomic_store(flag, release)  ──────→  atomic_load(flag, acquire)
                                        read(x)  // guaranteed x == 1
```

**Rule**: Mọi memory operation TRƯỚC `release store` sẽ **visible** cho thread đọc bằng `acquire load` trên cùng atomic variable.

---

### 2. Lock vs Spinlock

**Mutex (Heavy Lock)**
```cpp
std::mutex mtx;
{
    std::lock_guard<std::mutex> lock(mtx);
    // critical section
}
// Thread bị block → OS scheduler → context switch (~1-10µs)
```

**Spinlock (Busy Wait)**
```cpp
class Spinlock {
    std::atomic_flag flag = ATOMIC_FLAG_INIT;
public:
    void lock() {
        while (flag.test_and_set(std::memory_order_acquire)) {
            // busy wait — CPU vẫn chạy
            // Trick: thêm pause instruction giảm power
            #ifdef _MSC_VER
            _mm_pause();
            #else
            __builtin_ia32_pause();
            #endif
        }
    }
    void unlock() {
        flag.clear(std::memory_order_release);
    }
};
```

**Khi nào dùng gì?**

| Criteria | Mutex | Spinlock |
|----------|-------|----------|
| Critical section duration | > 1µs | < 1µs |
| Thread count vs core count | Threads > Cores | Threads ≤ Cores |
| Priority inversion risk | OS handles | Có risk |
| Game server use case | DB operation, file I/O | Update player position, stats counter |

> [!WARNING]
> **Bug thực tế**: Game server dùng spinlock cho function gọi Redis (10ms latency). 32 threads spin trên 8 cores → CPU 100%, server lag. Fix: đổi sang mutex + condition_variable.

---

### 3. Thread Pool Design

**Tại sao cần Thread Pool trong game server?**
- Tạo/hủy thread tốn ~100µs mỗi lần
- Game server xử lý 10K+ events/sec
- Thread pool reuse threads → giảm overhead 100x

**Architecture:**
```
                    ┌─────────────────────────────────────┐
                    │           THREAD POOL                │
                    │                                      │
  Task ──→ ┌──────────────┐    ┌──────┐ ┌──────┐         │
  Task ──→ │  Task Queue  │──→ │ W1   │ │ W2   │ ...     │
  Task ──→ │  (lock-free) │    │Worker│ │Worker│         │
           └──────────────┘    └──────┘ └──────┘         │
                    │           ┌──────┐ ┌──────┐         │
                    │           │ W3   │ │ W4   │         │
                    │           │Worker│ │Worker│         │
                    │           └──────┘ └──────┘         │
                    └─────────────────────────────────────┘
```

**Production-Ready Implementation (Key Decisions):**

```cpp
class ThreadPool {
public:
    explicit ThreadPool(size_t num_threads = std::thread::hardware_concurrency())
        : stop_(false) 
    {
        for (size_t i = 0; i < num_threads; ++i) {
            workers_.emplace_back([this, i] { this->workerLoop(i); });
        }
    }

    // Submit task với future để lấy result
    template<class F, class... Args>
    auto submit(F&& f, Args&&... args) 
        -> std::future<typename std::invoke_result_t<F, Args...>>
    {
        using return_type = typename std::invoke_result_t<F, Args...>;
        
        auto task = std::make_shared<std::packaged_task<return_type()>>(
            std::bind(std::forward<F>(f), std::forward<Args>(args)...)
        );
        
        std::future<return_type> result = task->get_future();
        {
            std::unique_lock<std::mutex> lock(queue_mutex_);
            if (stop_) throw std::runtime_error("submit on stopped ThreadPool");
            tasks_.emplace([task]() { (*task)(); });
        }
        condition_.notify_one();
        return result;
    }

    ~ThreadPool() {
        {
            std::unique_lock<std::mutex> lock(queue_mutex_);
            stop_ = true;
        }
        condition_.notify_all();
        for (auto& worker : workers_) {
            worker.join();
        }
    }

private:
    void workerLoop(size_t id) {
        while (true) {
            std::function<void()> task;
            {
                std::unique_lock<std::mutex> lock(queue_mutex_);
                condition_.wait(lock, [this] { 
                    return stop_ || !tasks_.empty(); 
                });
                if (stop_ && tasks_.empty()) return;
                task = std::move(tasks_.front());
                tasks_.pop();
            }
            task();  // Execute outside lock!
        }
    }

    std::vector<std::thread> workers_;
    std::queue<std::function<void()>> tasks_;
    std::mutex queue_mutex_;
    std::condition_variable condition_;
    bool stop_;
};
```

---

### 4. Producer-Consumer Pattern

**Game Server Use Case**: Event system — game events (player move, attack, chat) produced bởi network threads, consumed bởi game logic threads.

```cpp
template<typename T>
class BoundedQueue {
    std::queue<T> queue_;
    std::mutex mutex_;
    std::condition_variable not_full_;
    std::condition_variable not_empty_;
    size_t max_size_;

public:
    explicit BoundedQueue(size_t max_size) : max_size_(max_size) {}

    // Blocking push — wait nếu queue full
    void push(T item) {
        std::unique_lock<std::mutex> lock(mutex_);
        not_full_.wait(lock, [this] { return queue_.size() < max_size_; });
        queue_.push(std::move(item));
        not_empty_.notify_one();
    }

    // Blocking pop — wait nếu queue empty
    T pop() {
        std::unique_lock<std::mutex> lock(mutex_);
        not_empty_.wait(lock, [this] { return !queue_.empty(); });
        T item = std::move(queue_.front());
        queue_.pop();
        not_full_.notify_one();
        return item;
    }

    // Non-blocking try_pop cho game loop
    bool try_pop(T& item, std::chrono::milliseconds timeout) {
        std::unique_lock<std::mutex> lock(mutex_);
        if (!not_empty_.wait_for(lock, timeout, [this] { return !queue_.empty(); }))
            return false;
        item = std::move(queue_.front());
        queue_.pop();
        not_full_.notify_one();
        return true;
    }
};
```

---

### 5. ABA Problem

**Giải thích**: Thread A đọc value = A, bị preempt. Thread B đổi A→B→A. Thread A resume, thấy vẫn = A, nghĩ "không ai đổi" → **sai**!

```
Timeline:
  Thread 1: Read head = Node_A
  Thread 1: (preempted)
  Thread 2: Pop Node_A, Pop Node_B, Push Node_A (recycled!)
  Thread 1: CAS(head, Node_A, ...) → SUCCESS ← ❌ BUG! Node_A giờ khác context
```

**Fix bằng tagged pointer:**
```cpp
struct TaggedPtr {
    Node* ptr;
    uint64_t tag;  // increment mỗi lần modify
};

// CAS kiểm tra cả ptr VÀ tag
// Dù ptr giống, tag khác → CAS fail → retry
std::atomic<TaggedPtr> head;
```

---

### 6. False Sharing

**Tại sao game server cần biết?**
Khi 2 threads cùng truy cập data nằm trên cùng cache line (64 bytes), cả hai đều bị cache miss mỗi lần write → **performance drop 10-50x**.

```cpp
// ❌ BAD: counters nằm cạnh nhau → cùng cache line
struct BadCounters {
    std::atomic<int> player_count;  // offset 0
    std::atomic<int> room_count;    // offset 4 — SAME cache line!
};

// ✅ GOOD: padding đẩy ra 2 cache lines khác nhau
struct GoodCounters {
    alignas(64) std::atomic<int> player_count;  // cache line 1
    alignas(64) std::atomic<int> room_count;    // cache line 2
};
```

**Benchmark proof:**
```
BadCounters:  4 threads incrementing → 150M ops/sec
GoodCounters: 4 threads incrementing → 800M ops/sec  (5.3x faster!)
```

---

### 7. Lock-Free Queue (SPSC)

**Single-Producer Single-Consumer** — phù hợp game server pipeline:

```cpp
template<typename T, size_t Size>
class SPSCQueue {
    static_assert((Size & (Size - 1)) == 0, "Size must be power of 2");
    
    alignas(64) std::atomic<size_t> head_{0};
    alignas(64) std::atomic<size_t> tail_{0};
    std::array<T, Size> buffer_;

public:
    bool push(const T& item) {
        size_t tail = tail_.load(std::memory_order_relaxed);
        size_t next = (tail + 1) & (Size - 1);
        
        if (next == head_.load(std::memory_order_acquire))
            return false;  // Queue full
        
        buffer_[tail] = item;
        tail_.store(next, std::memory_order_release);
        return true;
    }

    bool pop(T& item) {
        size_t head = head_.load(std::memory_order_relaxed);
        
        if (head == tail_.load(std::memory_order_acquire))
            return false;  // Queue empty
        
        item = buffer_[head];
        head_.store((head + 1) & (Size - 1), std::memory_order_release);
        return true;
    }
};
```

**Tại sao lock-free nhanh hơn?**
- Không có mutex → không context switch
- Không có system call
- SPSC queue throughput: **50M+ ops/sec** vs mutex queue: **5M ops/sec**

---

## ✅ CHECKLIST KIẾN THỨC

- [ ] Giải thích được 6 memory orders, khi nào dùng cái nào
- [ ] Vẽ được happens-before diagram
- [ ] Biết khi nào dùng mutex vs spinlock vs lock-free
- [ ] Implement thread pool production-ready
- [ ] Giải thích ABA problem + cách fix
- [ ] Detect và fix false sharing
- [ ] Implement SPSC lock-free queue
- [ ] Giải thích producer-consumer pattern
- [ ] Biết dùng `std::condition_variable` đúng cách
- [ ] Profile và benchmark concurrent code

---

## 🔨 PROJECTS THỰC HÀNH

### Project 1: Production-Ready Thread Pool
**Yêu cầu:**
- Fixed number of worker threads
- Task queue với priority support
- Graceful shutdown
- Task cancellation
- Performance metrics (queue depth, avg task latency)
- Work stealing (optional, nâng cao)

**Benchmark targets:**
- Submit throughput: > 1M tasks/sec
- Avg task latency overhead: < 5µs
- Zero memory leak (valgrind clean)

### Project 2: Lock-Free Ring Buffer
**Yêu cầu:**
- SPSC variant: network thread → game logic thread
- MPSC variant: multiple client threads → single game loop
- Power-of-2 size
- Formal proof tại sao không cần lock

**Benchmark:**
- SPSC throughput: > 50M ops/sec
- MPSC throughput: > 20M ops/sec
- So sánh với `std::queue` + `std::mutex`

### Project 3: Timer Wheel Scheduler
**Context**: Game server cần schedule events (buff expire, cooldown reset, session timeout)

**Yêu cầu:**
- Hierarchical timer wheel (second → minute → hour)
- O(1) insert & cancel
- Tick resolution: 1ms
- Handle > 100K concurrent timers

**Architecture:**
```
  ┌──────────────────────────────────────────────┐
  │              Timer Wheel                      │
  │  ┌─────┐  ┌─────┐  ┌─────┐       ┌─────┐   │
  │  │ 0ms │→ │ 1ms │→ │ 2ms │→ ... │255ms│   │
  │  └──┬──┘  └──┬──┘  └──┬──┘       └──┬──┘   │
  │     │        │        │              │       │
  │    [T1]     [T3]     [T5]          [T99]    │
  │    [T2]              [T6]                    │
  │                      [T7]                    │
  └──────────────────────────────────────────────┘
```

---

## 🎯 MILESTONE TEST — Concurrency

### Bài test lý thuyết (trả lời miệng trong 2 phút mỗi câu):
1. Hai thread cùng increment `std::atomic<int>` bằng `memory_order_relaxed`. Kết quả có chính xác không? Tại sao?
2. Thread A write `data=42` rồi `flag.store(true, release)`. Thread B `flag.load(acquire)` thấy true. Thread B có chắc chắn thấy `data==42`? Nếu đổi sang `relaxed` thì sao?
3. Server dùng spinlock cho function call HTTP (50ms). Có vấn đề gì? Đề xuất fix?
4. Giải thích ABA problem trong lock-free stack. Tại sao hazard pointer fix được?
5. Tại sao `alignas(64)` fix false sharing? 64 từ đâu ra?

### Bài test thực hành:
1. Viết thread pool handle 1M tasks trong < 2 giây
2. Tìm bug trong đoạn code concurrent (sẽ cung cấp)
3. Benchmark mutex vs spinlock vs lock-free cho game event queue

---

## 📊 KPI

| Metric | Target | Cách đo |
|--------|--------|---------|
| Thread pool throughput | > 1M tasks/sec | Benchmark code |
| Lock-free queue throughput | > 30M ops/sec | Benchmark code |
| Bug detection rate | 4/5 concurrent bugs | Code review exercise |
| Concept explanation | Giải thích 8/10 concepts rõ ràng | Self-test hoặc peer review |

---

## 📚 TÀI LIỆU ĐỌC

| Tài liệu | Chương cần đọc | Mục tiêu |
|-----------|----------------|----------|
| *C++ Concurrency in Action* | Ch 1-7 | Core concepts + implementation |
| [CppReference - Memory Model](https://en.cppreference.com/w/cpp/atomic/memory_order) | All | Reference |
| [Preshing on Programming](https://preshing.com/archives/) | Memory ordering series | Visual understanding |
| [1024cores.net](http://www.1024cores.net/) | Lock-free algorithms | Advanced patterns |

---

## 🚀 BÀI TẬP NÂNG CAP (Hướng Senior)

1. **Implement work-stealing thread pool** — mỗi worker có local deque, steal từ worker khác khi idle
2. **Lock-free MPMC queue** — Michael-Scott queue variant
3. **Hazard pointer implementation** — safe memory reclamation cho lock-free data structures
4. **Coroutine-based task scheduler** — C++20 coroutines integration
