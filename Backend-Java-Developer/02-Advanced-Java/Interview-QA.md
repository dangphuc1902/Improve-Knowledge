# ❓ Java Nâng Cao - Câu Hỏi Phỏng Vấn

---

## Collections

### Q1: ArrayList vs LinkedList - khi nào dùng cái nào?

**ArrayList:** Dùng khi cần:
- Truy cập ngẫu nhiên nhanh (`get(index)`) → O(1)
- Duyệt list tuần tự
- Ít insert/delete ở giữa list

**LinkedList:** Dùng khi cần:
- Thêm/xóa phần tử ở đầu/đuôi → O(1)
- Implement Stack hoặc Queue
- Không cần random access

---

### Q2: HashMap hoạt động như thế nào?

**Trả lời:**
1. Khi `put(key, value)`, Java tính `hashCode()` của key
2. Hash code được chuyển thành bucket index
3. Nếu bucket trống → lưu Entry mới
4. Nếu collision → dùng Linked List (Java 7) hoặc Red-Black Tree khi ≥ 8 entries (Java 8+)
5. Khi `get(key)`, tính hash → tìm bucket → dùng `equals()` để tìm exact match

**Load Factor (mặc định 0.75):** Khi HashMap filled 75% → resize (double capacity, rehash).

---

### Q3: Collection nào không cho phép duplicate? Cho phép null?

| Collection | Duplicate | Null |
|-----------|----------|------|
| ArrayList | ✅ Cho phép | ✅ |
| HashSet | ❌ Không | ✅ 1 null |
| TreeSet | ❌ Không | ❌ |
| HashMap | Keys: ❌, Values: ✅ | ✅ 1 null key |
| TreeMap | Keys: ❌ | ❌ null key |
| LinkedHashSet | ❌ Không | ✅ |

---

### Q4: HashSet vs TreeSet vs LinkedHashSet?

- **HashSet**: O(1) add/remove/contains, **không có thứ tự**
- **TreeSet**: O(log n), **sorted order** (natural hoặc Comparator)
- **LinkedHashSet**: O(1), **insertion order** được giữ lại

---

### Q5: Iterator và ListIterator khác nhau thế nào?

- **Iterator**: Duyệt forward, có `hasNext()`, `next()`, `remove()`
- **ListIterator**: Duyệt cả 2 chiều, có `hasPrevious()`, `previous()`, `add()`, `set()`

---

### Q6: Fail-fast vs Fail-safe Iterator?

- **Fail-fast**: Throw `ConcurrentModificationException` nếu collection bị modify trong khi iterate (ArrayList, HashMap)
- **Fail-safe**: Iterate trên copy, không throw exception (ConcurrentHashMap, CopyOnWriteArrayList)

```java
List<String> list = new ArrayList<>(Arrays.asList("A","B","C"));
for (String s : list) {
    list.remove(s);  // ConcurrentModificationException!
}

// Fix: Dùng Iterator.remove()
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    it.next();
    it.remove();  // Safe
}
```

---

## Generics

### Q7: Generics là gì? Tại sao dùng Generics?

**Generics** cho phép viết code với type parameter → type-safe, reusable code.

**Lợi ích:**
- **Type safety**: Phát hiện lỗi tại compile time, không phải runtime
- **Elimination of casts**: Không cần ép kiểu thủ công
- **Code reuse**: 1 algorithm cho nhiều types

---

### Q8: Giải thích Wildcards: `? extends T` vs `? super T`?

- **`? extends T` (Upper Bounded)**: Chấp nhận T và subtype. Dùng để **READ**. Producer → Extends
- **`? super T` (Lower Bounded)**: Chấp nhận T và supertype. Dùng để **WRITE**. Consumer → Super
- **PECS Principle**: **P**roducer **E**xtends, **C**onsumer **S**uper

```java
void copy(List<? super T> dest, List<? extends T> src); // Classic example
```

---

## Concurrency

### Q9: Thread-safe là gì? Cách đạt được thread-safety?

**Thread-safe**: Nhiều thread có thể access đồng thời mà không gây data corruption.

**Cách đạt:**
1. `synchronized` keyword (method hoặc block)
2. `ReentrantLock` (explicit locking)
3. `Atomic` classes (AtomicInteger, AtomicReference)
4. Thread-local variables (`ThreadLocal<T>`)
5. Immutable objects
6. Concurrent collections (ConcurrentHashMap)

---

### Q10: Deadlock là gì? Cách ngăn chặn?

**Deadlock:** Thread A giữ lock1, chờ lock2. Thread B giữ lock2, chờ lock1 → cả 2 chờ nhau mãi.

**Điều kiện xảy ra Deadlock (4 điều kiện Coffman):**
1. Mutual Exclusion
2. Hold and Wait
3. No Preemption
4. Circular Wait

**Cách ngăn:**
- Luôn acquire locks theo **cùng thứ tự**
- Dùng `tryLock(timeout)` thay vì `lock()` block mãi
- Minimize synchronized scope
- Dùng Lock-free algorithms (Atomic classes)

---

### Q11: `volatile` keyword làm gì?

`volatile` đảm bảo:
1. **Visibility**: Thay đổi của một thread **ngay lập tức visible** với thread khác
2. **Ordering**: Ngăn instruction reordering với volatile variable

**KHÔNG đảm bảo atomicity** (dùng AtomicInteger cho compound operations):
```java
private volatile int count = 0;
count++;  // NOT atomic! (read-modify-write)
// Dùng AtomicInteger.incrementAndGet() thay thế
```

---

### Q12: `synchronized` vs `ReentrantLock`?

| Tiêu chí | synchronized | ReentrantLock |
|----------|-------------|---------------|
| Syntax | keyword | explicit API |
| Fairness | Không | Có (fair mode) |
| tryLock | Không | Có |
| Condition | wait/notify | Condition.await/signal |
| Interruptible | Không | Có |
| Performance | Tương đương | Tốt hơn khi contention cao |

---

### Q13: ExecutorService là gì? Tại sao dùng thay vì tạo Thread trực tiếp?

**ExecutorService** quản lý thread pool, tái sử dụng threads.

**Lợi ích:**
- Tránh overhead tạo/destroy thread liên tục
- Kiểm soát số lượng thread (tránh resource exhaustion)
- Dễ dàng submit task và lấy kết quả (Future)
- Graceful shutdown

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
Future<Integer> future = pool.submit(() -> computeExpensiveTask());
Integer result = future.get();  // Blocks until done
pool.shutdown();
```

---

## Java 8+ Features

### Q14: Stream API là gì? Khi nào dùng?

**Stream**: Sequence of elements hỗ trợ sequential/parallel aggregate operations.

**Đặc điểm:**
- **Lazy evaluation**: Intermediate operations không execute ngay
- **Không thay đổi source**: Stream operations tạo new stream
- **Single use**: Mỗi stream chỉ dùng 1 lần

```java
// Intermediate (lazy): filter, map, sorted, distinct, limit
// Terminal (triggers execution): collect, forEach, reduce, count, findFirst
```

---

### Q15: Optional là gì? Dùng khi nào?

**Optional<T>** là wrapper tránh `NullPointerException`. Buộc caller phải xử lý trường hợp "không có giá trị".

**Nên dùng:** Return type của method khi giá trị có thể vắng mặt  
**Không dùng:** Field của class, method parameter, Collection elements

---

*📌 Xem tiếp: [Practice-Exercises.md](Practice-Exercises.md)*
