# PHẦN 2: MULTI-THREADING (TRÁI TIM CỦA GAME SERVER)

Trong Game Server, Multi-threading là "con dao hai lưỡi". Dùng đúng thì server mượt mà, dùng sai thì Deadlock khiến hàng nghìn player văng ra khỏi game.

---

## 1. Executors & ThreadPool: Nhà máy quản lý luồng

### Nó là gì?
Trong Game Server, chúng ta KHÔNG BAO GIỜ được gọi `new Thread()` thủ công trong vòng lặp game. Việc tạo và xóa Thread liên tục cực kỳ tốn tài nguyên (tốn RAM và CPU để tranh chấp khóa).
**Executors** là một bộ khung (Framework) giúp quản lý một số lượng "Công nhân" (Thread) cố định.

### Dùng làm gì?
- Chạy các tác vụ nặng (Lưu Database, Log, Gửi mail) ở luồng khác để Game Thread không bị lag.
- Giới hạn số lượng Thread chạy đồng thời để server không bị "treo".

### Dùng khi nào?
- Khi bạn có hàng ngàn request đồng thời từ người chơi.
- Khi cần thực hiện các tác vụ định kỳ (Game Tick, Hồi máu quái).

### Cơ chế hoạt động: ThreadPoolExecutor
Hãy tưởng tượng **ThreadPool** như một đội công nhân chuyên nghiệp:
- `corePoolSize`: Số công nhân trực chiến (Ví dụ: 8 người).
- `maxPoolSize`: Số công nhân tối đa khi việc quá nhiều (Ví dụ: 16 người).
- `WorkQueue`: Hàng đợi các yêu cầu đang chờ xử lý.

### Ví dụ code Thực chiến:
```java
// Dùng ScheduledExecutorService cho Vòng lặp Game (Game Tick)
ScheduledExecutorService gameLoop = Executors.newSingleThreadScheduledExecutor();

gameLoop.scheduleAtFixedRate(() -> {
    world.update(); // Cập nhật vị trí quái, AI, vật lý...
}, 0, 50, TimeUnit.MILLISECONDS); // Chạy mỗi 50ms (20 FPS)
```

### Sai lầm & Best Practice
- **Sai lầm**: Dùng `Executors.newFixedThreadPool()` cho các tác vụ biến động cực lớn. Nó dùng Queue **vô hạn**. Nếu công nhân làm chậm, hàng đợi sẽ phình to cho đến khi server cháy sạch RAM (**OOM Error**).
- **Best Practice**: Luôn dùng **Bounded Queue** (hàng đợi có giới hạn). Nếu hàng đợi đầy, hãy xác định **Rejection Policy** (ví dụ: thông báo lỗi cho player hoặc bỏ qua request cũ).

---

## 2. Java Memory Model (JMM) & Visibility

### Nó là gì?
Giải thích cách các Thread nhìn thấy dữ liệu của nhau. Trong Java, mỗi Thread có một bộ nhớ đệm (Cache) riêng. Nếu Thread A thay đổi giá trị biến, Thread B chưa chắc đã thấy ngay lập tức.

### Cơ chế: Từ khóa `volatile`
- **Nó là gì**: Khi đánh dấu biến là `volatile`, Java sẽ đảm bảo mọi Thread luôn đọc giá trị mới nhất từ vùng nhớ dùng chung (**Main Memory**), thay vì đọc từ cache riêng.

---

## 3. Race Condition & Locks: Tranh chấp tài nguyên

### Nó là gì?
Xảy ra khi 2 Thread cùng sửa một dữ liệu (ví dụ: cộng vàng cho Player) dẫn đến mất mát dữ liệu.
- **Giải pháp**: Dùng **Locks** để chỉ cho phép 1 người được sửa tại 1 thời điểm.

### Ví dụ code Thực chiến:
```java
// Dùng ReentrantLock để bảo vệ việc cộng vàng (Race Condition)
private final ReentrantLock lock = new ReentrantLock();

public void addGold(int amount) {
    lock.lock();
    try {
        this.gold += amount;
    } finally {
        lock.unlock(); // Luôn giải phóng lock trong khối finally
    }
}
```

### Sai lầm & Best Practice
- **Sai lầm**: Quên `unlock()` trong khối `finally`. Nếu code bị bug văng Exception trước khi unlock, tài nguyên đó sẽ bị khóa vĩnh viễn (**Deadlock**).
- **Best Practice**: Luôn ưu tiên dùng `tryLock()` với timeout để tránh treo thread vô hạn.

---

## 4. Atomic & Lock-free Logic (Performance cao nhất)

### Nó là gì?
Một phương thức xử lý tranh chấp mà không cần dùng khóa (Lock). Nó dùng cơ chế **CAS (Compare-And-Swap)** của CPU.

### Ví dụ code Thực chiến:
```java
// Dùng AtomicInteger cho bộ đếm người chơi Online
private AtomicInteger onlineCount = new AtomicInteger(0);

public void onPlayerLogin() {
    onlineCount.incrementAndGet(); // Tương đương ++i nhưng Thread-safe
}
```

### Sai lầm & Best Practice
- **Sai lầm**: Dùng `Atomic` cho các logic phức tạp (ví dụ: vừa check vừa update nhiều biến cùng lúc). Atomic chỉ bảo vệ 1 biến duy nhất.
- **Best Practice**: Với các phép toán cộng dồn cực lớn (như Global Log), hãy dùng `LongAdder` để đạt hiệu năng cao hơn `AtomicLong`.

---

## 5. CompletableFuture: Xử lý bất đồng bộ hiện đại

### Nó là gì?
Là bản nâng cấp của `Future` trong Java 8+, giúp xử lý các chuỗi tác vụ bất đồng bộ liên tiếp nhau cực kỳ sạch sẽ (Chain logic).

### Ví dụ code Thực chiến:
```java
// Dùng CompletableFuture để nạp dữ liệu player từ DB mà không làm block Game Thread
CompletableFuture.supplyAsync(() -> db.findPlayer(id))
    .thenAccept(player -> {
        // Xử lý sau khi nạp xong
        logger.info("Player {} loaded!", player.getName());
    });
```

### Sai lầm & Best Practice
- **Sai lầm**: Gọi `join()` hoặc `get()` ngay lập tức ở Game Thread. Việc này sẽ khiến server bị "đứng hình" chờ DB, mất ý nghĩa của bất đồng bộ.
- **Best Practice**: Sử dụng `callback` (`thenAccept`, `thenApply`) để xử lý kết quả khi nó sẵn sàng.

---

## 6. Thread Lifecycle & Interruption: Nền tảng cốt lõi

### Nó là gì?
- **Thread Lifecycle**: Các trạng thái mà một thread trải qua từ lúc sinh ra đến lúc chết.
- **Interruption**: Cơ chế lịch sự để yêu cầu một thread dừng lại.

### Các trạng thái của Thread (CƠ BẢN CẦN NHỚ):
1. **NEW**: Vừa `new Thread()`, chưa gọi `start()`.
2. **RUNNABLE**: Đang chạy hoặc đang đợi CPU cấp phát thời gian.
3. **BLOCKED**: Đang đợi chiếm Lock (đợi vào vùng `synchronized`).
4. **WAITING**: Đợi vô hạn cho đến khi thread khác gọi `notify()` hoặc `signal()`.
5. **TIMED_WAITING**: Đợi có thời hạn (như `Thread.sleep(1000)`).
6. **TERMINATED**: Đã chạy xong hoặc bị Exception văng ra.

### Ví dụ code Thực chiến (Interruption):
```java
public void run() {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            // Làm việc gì đó...
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Bị ngắt khi đang sleep -> Thoát loop
            Thread.currentThread().interrupt(); // Restore status
            break;
        }
    }
}
```

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Tại sao "False Sharing" có thể làm giảm hiệu năng của game server?
- **Answer**: CPU đọc dữ liệu theo Cache Line (thường là 64 bytes). Nếu hai biến (ví dụ `hp` và `mana`) nằm cạnh nhau trong bộ nhớ và thuộc cùng một Cache Line, khi Core 1 update `hp`, nó sẽ làm mất hiệu lực (invalidate) toàn bộ Cache Line đó ở Core 2 (đang giữ `mana`). Core 2 buộc phải nạp lại từ RAM dù `mana` không đổi. 
- **Giải pháp**: Dùng `@Contended` (từ Java 8) để tách chúng ra các Cache Line khác nhau.

### 2. Sự khác biệt giữa `Thread.sleep()` và `Object.wait()`?
- **Answer**: 
    - `sleep()` không giải phóng Lock (Monitor), `wait()` có giải phóng Lock để thread khác vào.
    - `sleep()` dùng được ở mọi nơi, `wait()` phải ở trong khối `synchronized`.

### 3. Làm thế nào để giải quyết vấn đề "ABA Problem" trong CAS?
- **Answer**: CAS so sánh giá trị cũ và mới. Nếu giá trị đổi từ A sang B rồi quay lại A, CAS sẽ không phát hiện ra sự thay đổi. Giải quyết bằng cách dùng **Versioning** (Ví dụ: `AtomicStampedReference` trong Java).

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống **Single-threaded Room Pattern**.
- Giải thích tại sao nhiều game AAA (như Liên Minh) lại ưu tiên xử lý mọi logic của 1 trận đấu (Room) trên **duy nhất 1 Thread**.
- Viết pseudo-code mô tả cách điều phối (Dispatching) các request từ nhiều player vào hàng đợi của Room đó.
