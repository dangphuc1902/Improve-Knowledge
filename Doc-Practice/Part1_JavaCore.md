# PHẦN 1: JAVA CORE (NỀN TẢNG CHO GAME SERVER)

Chào các bạn, tôi là Senior Java Game Backend Engineer. Trong thế giới game online, Java không chỉ là ngôn ngữ để code, nó là một cỗ máy cần được tinh chỉnh (tuning) để đạt hiệu năng tối đa. Part 1 này chúng ta sẽ đi vào những thứ "xương máu" nhất.

---

## 1. OOP trong Game Server dùng thế nào?

### Nó là gì?
OOP (Object-Oriented Programming) là nền tảng để mô hình hóa mọi đối tượng trong thế giới game.

### Dùng để làm gì trong Game Backend?
- **Entity Management**: Player, NPC, Monster, Item, Bullet đều là các đối tượng.
- **Game Logic**: Xử lý va chạm, tính toán sát thương, kỹ năng.

### Khi nào nên dùng? (Real-case)
Dùng kế thừa (Inheritance) và Đa hình (Polymorphism) để quản lý hàng nghìn loại quái vật khác nhau.
- `Monster` (Parent class)
- `BossMonster`, `EliteMonster`, `NormalMonster` (Child classes)
- Gọi chung `monster.onTick()` để tất cả quái vật thực thi hành động mỗi giây.

### Ưu điểm / Nhược điểm
- **Ưu**: Dễ quản lý, code sạch, tái sử dụng tốt.
- **Nhược**: Quá lạm dụng kế thừa có thể gây memory overhead hoặc làm luồng logic trở nên khó hiểu (Deep hierarchy).

### Sai lầm phổ biến
Dùng kế thừa bừa bãi. Trong Game, **Composition over Inheritance** là kiến thức sống còn. Ví dụ: Đừng bắt `Player` kế thừa `Human`. Hãy dùng `Component` (Player has a HealthComponent, MovementComponent).

### Best Practice
Sử dụng **Entity Component System (ECS)** nếu game phức tạp. Nếu không, hãy giữ Hierarchy mỏng.

### So sánh với giải pháp khác
- **ECS (Data-oriented)**: Tách dữ liệu khỏi logic, cực nhanh cho game lớn nhưng khó triển khai ở Java hơn so với C++.

---

## 2. Memory Model (Stack vs Heap)

### Nó là gì?
- **Stack**: Lưu biến local, Tham số truyền vào hàm, Thông tin CallStack(Hàm nào gọi hàm nào) (Size nhỏ, tốc độ nhanh).
- **Heap**: Lưu tất cả các Object, Array, String (Size lớn, tốc độ chậm hơn, do GC quản lý). (Lưu ý: String cũng là Object nhưng được tối ưu hóa đặc biệt)

### Ảnh hưởng tới Game Server ra sao?
Game server thường có hàng chục nghìn `UserSession`. Nếu mỗi session tạo quá nhiều object tạm thời trên **Heap**, GC sẽ chạy liên tục gây hiện tượng **Lag/Spike** (Stop the World).
// GC (Garbage Collector) là gì? Là cơ chế tự động dọn dẹp các object không còn dùng đến trên Heap.

### Khi nào nên dùng?
- **Stack**: Cho các biến tính toán tạm thời trong game loop.
- **Heap**: Lưu trạng thái player (Inventory, Stats).

### Ưu điểm / Nhược điểm
- **Stack**: Tự giải phóng, không lo memory leak.
- **Heap**: Chứa được nhiều dữ liệu, nhưng cần quản lý vòng đời object kỹ.

### Sai lầm phổ biến
Tạo Object mới trong Game Loop (Tick). Ví dụ: `new Vector3D(x, y, z)` mỗi frame cho 10,000 player. Điều này sẽ giết chết Heap cực nhanh.

### Best Practice
**Object Pooling**: Tái sử dụng các object cũ thay vì `new` cái mới.

---

## 3. Garbage Collection (GC) - "Kẻ thù" của Real-time Game

### Nó là gì?
Cơ chế tự động dọn dẹp các object không còn dùng đến trên Heap.

### Ảnh hưởng tới Game Server ra sao?
Lỗi kinh điển: **Stop-The-World (STW)**. Khi GC dọn dẹp, toàn bộ thread của game server bị đứng lại. Player sẽ thấy game bị "khựng" (Spike).

### Khi nào nên dùng (Tuning)?
Cần chọn GC phù hợp với game behavior:
- **ZGC / Shenandoah**: Phù hợp cho Game Server cần latency cực thấp (< 1ms pause time).
- **G1GC**: Mặc định từ Java 9, khá ổn cho hầu hết game Mid-size.

### Ưu điểm / Nhược điểm
- **Ưu**: Developer không cần `free()` tay (tránh leak dễ hơn C++).
- **Nhược**: Khó kiểm soát thời điểm nó chạy.

### Sai lầm phổ biến
Không monitor GC. Thấy lag là đổi server to hơn (Vertical Scale) mà không biết do GC đang nghẽn.

---

## 4. Collection (HashMap vs ConcurrentHashMap)

### So sánh trong Game context
| Tiêu chí | HashMap | ConcurrentHashMap |
| :--- | :--- | :--- |
| **Thread-safe** | Không | Có (Segment Locking / CAS) |
| **Performance** | Rất cao (Single thread) | Cao (Multi-thread) |
| **Trường hợp dùng** | Data cục bộ trong 1 room/match | Quản lý list Player toàn server |

### Sai lầm phổ biến
Dùng `HashMap` cho danh sách `OnlinePlayers` mà lại dùng nhiều thread để truy cập. Kết quả: Infinite Loop hoặc `ConcurrentModificationException`.

---

## BUG THỰC TẾ & CODE MINH HỌA

### Bài toán: Memory Leak trong Game Session
**Bug:** Player logout nhưng dữ liệu vẫn còn trong `OnlineMap`. Sau 1 tuần, Server crash vì OutOfMemory (OOM).

```java
public class GameServer {
    // Sai lầm: Quên remove player khi logout
    private static Map<Long, Player> players = new ConcurrentHashMap<>();

    public void onPlayerLogin(Player p) {
        players.put(p.getId(), p);
    }

    // Nếu không gọi hàm này, player object sẽ tồn tại mãi mãi trên Heap
    public void onPlayerLogout(long playerId) {
        players.remove(playerId);
    }
}
```

---

## CÂU HỎI PHỎNG VẤN (Interview Prep)

### Junior
- **Q**: Phân biệt `String`, `StringBuilder` và `StringBuffer`? 
- **A**: `StringBuilder` dùng để ghép chuỗi trong game loop (tốc độ nhanh nhất vì ko thread-safe). `String` là immutable, dùng nhiều gây tốn mảng char trên Heap.

### Mid
- **Q**: Tại sao trong Game Server nên dùng `primitive types` (int, long) thay vì `Wrapper classes` (Integer, Long)?
- **A**: Để tiết kiệm bộ nhớ (Integer tốn ~16-24 bytes, int tốn 4 bytes) và tránh **Autoboxing** tạo object rác liên tục.

### Senior
- **Q**: Làm thế nào để loại bỏ hoàn toàn GC Pause cho một Game Server Real-time?
- **A**: 1. Dùng **Off-heap memory** (DirectBuffer) để lưu data lớn. 2. Triệt để **Object Pooling**. 3. Sử dụng **ZGC** với cấu hình heap rộng. 4. Hạn chế tối đa tạo rác trong main loop.

---

## MINI PROJECT / BÀI TẬP
**Đề bài:** Viết một class `SimpleObjectPool<T>` để quản lý đối tượng `Bullet` (Đạn) trong game bắn súng. Yêu cầu:
1. Tránh `new Bullet()` khi player bắn.
2. Tái sử dụng Bullet khi nó bay ra ngoài màn hình.
