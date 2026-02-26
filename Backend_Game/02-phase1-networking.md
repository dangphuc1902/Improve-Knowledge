# 🌐 PHASE 1.2 — Networking & Protocol Deep Dive

> **Timeline**: Month 1, tuần 4-5  
> **Mục tiêu**: Hiểu networking từ kernel level, build high-performance TCP server  
> **Tài liệu chính**: *High Performance Browser Networking* (Ilya Grigorik)

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### 1. TCP vs UDP — Trade-off cho Game Server

| Aspect | TCP | UDP | Game Choice |
|--------|-----|-----|-------------|
| Reliability | ✅ Guaranteed | ❌ Best-effort | TCP cho economy, UDP cho movement |
| Ordering | ✅ In-order | ❌ Unordered | TCP cho critical actions |
| Latency | Higher (handshake) | Lower | UDP cho real-time |
| Head-of-line blocking | ❌ có | ✅ không | UDP thắng cho fast-paced |

**CafePho Decision**: Social game → TCP đủ dùng cho tất cả (reliability > latency).

### 2. Nagle's Algorithm & TCP_NODELAY

Nagle buffer small packets → gửi batch. **Game server PHẢI TẮT** vì cần low latency.

```cpp
int flag = 1;
setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(flag));
```

> [!WARNING]
> **Bug thực tế**: Không set TCP_NODELAY → delay 200ms do Nagle + delayed ACK. Fix: set ở cả server VÀ client.

### 3. Backpressure

Producer nhanh hơn consumer → queue grows → OOM. Strategies:
- **Drop**: Bỏ non-critical messages (position updates)
- **Block**: Producer wait (economy transactions)  
- **Rate limit**: Giới hạn send rate (anti-flood chat)
- **Credit-based**: Consumer cấp credit cho producer

### 4. I/O Multiplexing

**Epoll (Linux)** — Edge-triggered + non-blocking = game server standard:
```cpp
int epfd = epoll_create1(0);
struct epoll_event ev;
ev.events = EPOLLIN | EPOLLET;  // Edge-triggered
epoll_ctl(epfd, EPOLL_CTL_ADD, client_fd, &ev);

while (running) {
    int n = epoll_wait(epfd, events, MAX_EVENTS, timeout_ms);
    for (int i = 0; i < n; ++i)
        handleEvent(events[i]);
}
```

**IOCP (Windows)** — Completion-based model cho CafePho:
```cpp
HANDLE iocp = CreateIoCompletionPort(INVALID_HANDLE_VALUE, NULL, 0, numThreads);
// Worker threads call GetQueuedCompletionStatus()
```

| Feature | Epoll | IOCP |
|---------|-------|------|
| Model | Readiness-based | Completion-based |
| Platform | Linux | Windows |
| Thread model | Event loop | Thread pool dispatch |

### 5. gRPC / HTTP2 / Protobuf

- HTTP/2 multiplexing: multiple RPC trên 1 TCP connection
- Protobuf ~5x smaller than JSON
- Server streaming cho game events

---

## 🔨 PROJECT: Mini High-Performance TCP Server

**Architecture:**
```
Acceptor (1 thread) → I/O Reactor (epoll/IOCP) → Worker Pool (N threads) → Send Queue
```

**Requirements:**
1. Handle 10K concurrent connections
2. Custom protocol: `[4-byte length][payload]`
3. Non-blocking I/O + thread pool
4. Backpressure mechanism
5. Graceful shutdown

**Benchmark Targets:**

| Metric | Target |
|--------|--------|
| Connections | 10K concurrent |
| QPS (echo) | > 100K req/sec |
| Latency P50 | < 1ms |
| Latency P99 | < 5ms |
| Memory/conn | < 4KB |

**Blocking vs Async comparison:**
- Blocking: max ~2000 conn, 50K QPS, 8MB/thread → 16GB
- Async: 100K+ conn, 200K+ QPS, 4KB/conn → 400MB

---

## ✅ CHECKLIST

- [ ] Giải thích TCP vs UDP trade-off cho game scenarios
- [ ] Biết khi nào set TCP_NODELAY và tại sao
- [ ] Implement backpressure mechanism
- [ ] Giải thích epoll ET vs LT
- [ ] Build server handle 10K connections
- [ ] Hiểu gRPC/HTTP2 internals
- [ ] Benchmark QPS và latency

## 🎯 MILESTONE TEST

1. Build echo server handle 10K connections, QPS > 100K
2. So sánh latency blocking vs async với benchmark data
3. Giải thích head-of-line blocking, Nagle + delayed ACK

## 📚 TÀI LIỆU: *High Performance Browser Networking* Ch 1-4, Beej's Guide to Network Programming

## 🚀 NÂNG CAO: QUIC protocol, custom reliable UDP, zero-copy networking, io_uring
