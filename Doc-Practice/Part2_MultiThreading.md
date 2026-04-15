# PHẦN 2: MULTI-THREADING (TRÁI TIM CỦA GAME SERVER)

Trong Game Server, Multi-threading là "con dao hai lưỡi". Dùng đúng thì server mượt mà, dùng sai thì Deadlock khiến hàng nghìn player văng ra khỏi game.

---

## 1. Thread, Runnable và ExecutorService

### Nó là gì?
- **Thread**: Đơn vị nhỏ nhất của tiến trình xử lý.
- **Runnable**: Nhiệm vụ cần thực thi.
- **ExecutorService**: Hệ thống quản lý Thread (Thread Pool).

### Dùng để làm gì trong game backend?
- **Main Game Loop**: Chạy logic game (Tick).
- **IO Threads**: Nhận/Gửi packet mạng (Netty dùng cái này).
- **Worker Threads**: Xử lý logic nặng (Save DB, Log, AI).

### Khi nào nên dùng?
Dùng `ExecutorService` (Thread Pool) thay vì `new Thread()` thủ công. Trong game, chúng ta thường chia Pool theo chức năng: `LoginPool`, `BattlePool`, `DBPool`.

---

## 2. Race Condition & Deadlock (Cơn ác mộng)

### Race Condition
Hai player cùng lúc nhặt 1 vật phẩm. Nếu không xử lý, cả 2 đều có thể sở hữu vật phẩm đó (Dupe item).

### Deadlock
Thread A giữ Lock 1 và đợi Lock 2. Thread B giữ Lock 2 và đợi Lock 1. Server "đứng hình" vĩnh viễn.

### Sai lầm phổ biến
Dùng `synchronized` lồng nhau (Nested locks). Ví dụ: Lock Player A -> Lock Player B để giao dịch. Nếu cùng lúc đó có giao dịch Lock B -> Lock A => **Deadlock**.

---

## 3. Lock vs Synchronized vs Atomic

### Synchronized
Dễ dùng nhưng nặng (Heavyweight). Khi chiếm lock thành công, các thread khác phải đợi (Block).

### ReentrantLock
Linh hoạt hơn, có `tryLock()` với timeout. Cực kỳ hữu ích để tránh treo thread quá lâu.

### Atomic (CAS - Compare And Swap)
Sử dụng `AtomicInteger`, `AtomicLong`. Không dùng lock (Lock-free). Tốc độ cực nhanh cho các biến đếm (Counter).

---

## 4. Game Context: Xử lý Match Battle & Tick Loop

### Tick Game Loop
Game server không chạy tự do. Nó chạy theo nhịp (Tick). Ví dụ: 1 giây có 20 Tick (50ms/tick).
Mỗi tick, server duyệt qua toàn bộ logic.

```java
ScheduledExecutorService gameLoop = Executors.newSingleThreadScheduledExecutor();
gameLoop.scheduleAtFixedRate(() -> {
    long startTime = System.currentTimeMillis();
    updatePhysics();
    updateAI();
    syncToClients();
    long duration = System.currentTimeMillis() - startTime;
    if (duration > 50) {
        System.err.println("SERVER LAGGING! Tick took: " + duration + "ms");
    }
}, 0, 50, TimeUnit.MILLISECONDS);
```

---

## 5. Case Study: Xử lý 10,000 Player Concurrent

Để xử lý 10k player, chúng ta không dùng 10k thread.
**Giải pháp: Actor Model hoặc Sharding.**
- Chia player vào các `Zone` hoặc `Room`.
- Mỗi `Room` được gán cho 1 thread cố định để xử lý logic bên trong.
- **Ưu điểm**: Trong phạm vi 1 room, worker thread chạy tuần tự -> **Không cần dùng Lock nội bộ**, performance tăng vọt.

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### Mid
- **Q**: Phân biệt `submit()` và `execute()` trong `ExecutorService`?
- **A**: `execute()` không trả về gì. `submit()` trả về `Future`, cho phép lấy kết quả hoặc bắt Exception từ thread con.

- **Q**: Tại sao không nên dùng `Executors.newCachedThreadPool()` cho Production?
- **A**: Vì nó tạo thread vô hạn. Nếu bị DOS (tăng đột biến request), server sẽ cháy CPU/RAM và sập. Luôn dùng `ThreadPoolExecutor` với `BoundedQueue`.

### Senior
- **Q**: Làm thế nào để giải quyết vấn đề Lock Contention (tranh chấp khóa) khi có quá nhiều player cùng ghi dữ liệu vào một `Global Leaderboard`?
- **A**: 
    1. **Lock Stripping**: Chia nhỏ lock (như cách ConcurrentHashMap làm).
    2. **Write-behind**: Thay vì ghi trực tiếp, đẩy vào một `ConcurrentQueue` và dùng 1 thread duy nhất để xử lý ghi.
    3. **LongAdder**: Thay cho `AtomicLong` nếu có cực nhiều thread cùng cộng vào 1 biến.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống "Chuyển tiền" (Gold Transfer) giữa 2 Player đảm bảo:
1. Không bị Race Condition (không bị mất tiền hoặc nhân bản tiền).
2. Không bị Deadlock (ngay cả khi 2 người cùng chuyển cho nhau cùng lúc).
3. Hiệu năng cao.

**Gợi ý mindset Senior**: Sắp xếp thứ tự ID của 2 player trước khi Lock. Luôn lock người có ID nhỏ trước.
