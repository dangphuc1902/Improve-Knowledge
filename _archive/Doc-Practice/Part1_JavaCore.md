# PHẦN 1: JAVA CORE (NỀN TẢNG CHO GAME SERVER)

Chào các bạn, tôi là Senior Java Game Backend Engineer. Trong thế giới game online, Java không chỉ là ngôn ngữ để code, nó là một cỗ máy cần được tinh chỉnh (tuning) để đạt hiệu năng tối đa. Part 1 này chúng ta sẽ đi vào những thứ "xương máu" nhất.

---
## Sự khác biệt giữa JDK, JRE, JVM:
    JDK: (Java Development Kit): Chứa JRE (Java Runtime Environment): JVM(Java Virtual Machine) + Libraries Set + Development Tools(Java Compiler: Biên dịch, Java Debugger, ...)
        - Chứa JVM(Java Virtual Merchin môi trường máy ảo )
    - JDK: Một bộ công cụ phát triển phần mềm được sử dụng để xây dựng ứng dụng Java. Nó chứa JRE và một bộ công cụ phát triển. 
        - Bao gồm trình biên dịch. trình gỡ lỗi và các tiện ích như jar và javadoc.
        - Cung cấp JRE vì nó cho phép chạy các chương trình Java.
        - Các nhà phát triển yêu cầu viết biên dịch gỡ lỗi mã. 


## 1. OOP Pillars: 4 Tột đỉnh của Hướng đối tượng

### Nó là gì? (Lý thuyết chuẩn)
Dù bạn là Senior, 4 tính chất này vẫn là "kinh thánh" không thể quên:
1. **Encapsulation (Đóng gói)**: Che giấu dữ liệu bên trong bằng `private` và chỉ lộ ra qua các phương thức `public` (Getters/Setters). Giúp kiểm soát dữ liệu và giảm sự phụ thuộc giữa các class.
2. **Inheritance (Kế thừa)**: Cho phép một class con kế thừa lại các thuộc tính và hành vi của class cha (`extends`). Giúp tái sử dụng code.
3. **Polymorphism (Đa hình)**: Một đối tượng có thể đóng nhiều vai trò khác nhau. 
   - *Overriding*: Thay đổi logic hàm của cha ở lớp con.
   - *Overloading*: Cùng tên hàm nhưng khác tham số.
4. **Abstraction (Trừu tượng)**: Tập trung vào "Hệ thống làm được gì" thay vì "Hệ thống làm như thế nào". Đại diện bởi **Interface** và **Abstract Class**.

### Ví dụ code Thực chiến:
```java
// Abstraction: Định nghĩa khung kỹ năng
public abstract class Skill {
    public abstract void execute(Player target);
}

// Inheritance & Polymorphism: Triển khai cụ thể
public class Fireball extends Skill {
    @Override
    public void execute(Player target) {
        target.takeDamage(100); // Gây sát thương
    }
}

// Encapsulation: Bảo vệ dữ liệu Player
public class Player {
    private int hp = 1000;
    public void takeDamage(int dmg) { this.hp -= dmg; }
    public int getHp() { return this.hp; }
}
```

---

## 2. Senior Mindset: Composition over Inheritance

### Tại sao cần bước tiếp?
Kế thừa (Inheritance) rất mạnh nhưng nếu dùng sai sẽ tạo ra "Cái vòi bạch tuộc" cực kỳ khó gỡ. Trong Game, chúng ta ưu tiên **Composition (Đóng gói/Thành phần)**.

### Cơ chế hoạt động: Component-Based Design
Thay vì: `Boss extends Monster extends Entity`.
Ta dùng: `Boss` has `AIComponent`, `CombatComponent`, `RenderComponent`.
Khi cần update, ta chỉ cần lặp qua các component và thực thi logic của chúng.

### Sai lầm & Best Practice
- **Sai lầm**: Tạo cây kế thừa quá sâu (Deep Inheritance). Cực kỳ khó bảo trì và gây lãng phí bộ nhớ.
- **Best Practice**: **Composition over Inheritance**. Giữ cây kế thừa mỏng (max 2-3 cấp).
---

## 3. Java Memory Model: Stack vs Heap

### Nó là gì?
Là cách Java phân bổ bộ nhớ cho các biến và đối tượng.
- **Stack**: Ngăn xếp lưu trữ các lời gọi hàm và biến cục bộ.
- **Heap**: Vùng nhớ khổng lồ lưu trữ tất cả các Object.

### Dùng để làm gì?
Hiểu cái này để tránh **Memory Leak** và **GC Spike** (Lag game).

### Dùng khi nào?
- **Stack**: Dùng cho tính toán cực nhanh trong loop (ví dụ: tính tọa độ X, Y tạm thời).
- **Heap**: Lưu trữ data lâu dài (ví dụ: Inventory của Player).

### Cơ chế (How it works):
1. Khi bạn gọi một hàm, một `Stack Frame` được tạo ra. Khi hàm kết thúc, Frame bị xóa ngay lập tức -> Tốc độ ánh sáng.
2. Khi bạn dùng `new Object()`, nó nằm trên Heap. Nó chỉ biến mất khi **Garbage Collector (GC)** đến quét.

### Ví dụ code Thực chiến:
```java
// Object Pooling: Tái sử dụng đối tượng Bullet (Đạn)
public class BulletPool {
    private Queue<Bullet> pool = new LinkedList<>();

    public Bullet getBullet() {
        return pool.isEmpty() ? new Bullet() : pool.poll();
    }

    public void returnBullet(Bullet b) {
        b.reset(); // Reset trạng thái trước khi trả lại
        pool.offer(b);
    }
}
```

---

## 4. Reflection & Dynamic Proxy: Ma thuật của Framework

### Nó là gì?
- **Reflection**: Khả năng "soi" vào nội bộ một Class khi code đang chạy (runtime) để lấy field, method bí mật.
- **Dynamic Proxy**: Kỹ thuật tạo ra một đối tượng "giả" để bao bọc đối tượng thật, nhằm chèn thêm logic (ví dụ: Logging, Transaction).

### Dùng để làm gì?
- Tự động hóa: Các framework như Spring Boss, Hibernate dùng cái này để "đọc" Annotation trên code của bạn.
- Game Server: Dùng để tự động đăng ký các `PacketHandler` mà không cần viết lệnh `if-else` dài dằng dặc.

### Cơ chế hoạt động:
Reflection truy cập vào `Meta-data` trong JVM. Dynamic Proxy (JDK Proxy hoặc CGLIB) tạo ra một class mới ngay trong lúc runtime để "đánh chặn" các lời gọi hàm.

### Ví dụ code Thực chiến:
```java
// Dùng Reflection tự động đăng ký Packet Handler
public void registerHandlers(Object handler) {
    for (Method m : handler.getClass().getDeclaredMethods()) {
        if (m.isAnnotationPresent(PacketMessage.class)) {
            // Lưu m vào một Map để gọi khi có gói tin tương ứng đến
            handlerMap.put(m.getParameterTypes()[0], m);
        }
    }
}
```

---

## 5. Functional Programming (Java 8+ Streams)

### Nó là gì?
Cách viết code tập trung vào "Dữ liệu chạy qua các ống lọc" thay vì viết vòng lặp `for` truyền thống.

### Dùng để làm gì?
- Xử lý danh sách player: Lọc (Filter), Chuyển đổi (Map), Thu gọn (Reduce).
- Viết code ngắn gọn, dễ đọc, lập luận song song (`parallelStream`) dễ dàng.

### Cơ chế (Inside Stream):
Stream không lưu trữ dữ liệu. Nó là một pipeline các thao tác. Nó chỉ thực thi khi bạn gọi các hàm "kết thúc" (Terminal operations) như `collect()` hoặc `findFirst()`.

### Ví dụ code Thực chiến:
```java
// Lọc danh sách cao thủ (Top Players)
List<Player> topPlayers = players.stream()
    .filter(p -> p.getLevel() > 50)  // Lọc người trên level 50
    .sorted(Comparator.comparing(Player::getLevel).reversed()) // Sắp xếp giảm dần
    .limit(10) // Lấy top 10
    .collect(Collectors.toList()); // Đóng gói vào danh sách mới
```

---

## 6. Collections Internals: HashMap & ArrayList

### Nó là gì?
- **HashMap**: Cấu trúc dữ liệu dạng Key-Value có tốc độ truy xuất gần như tức thì.
- **ArrayList**: Mảng động có thể tự co dãn kích thước.

### Cơ chế 

"Dưới nắp máy" (How it works):
1. **HashMap**: Sử dụng mảng các Buckets. Khi bạn `put(key, value)`, Java tính `hashCode()` của key để tìm index. Nếu nhiều key có cùng index (**Collision**), Java dùng LinkedList (hoặc Red-Black Tree ở Java 8+) để lưu trữ.
   - **Senior Insight**: Nếu `hashCode()` kém, HashMap sẽ biến thành một LinkedList chậm chạp (O(N)).
2. **ArrayList**: Thực chất là một mảng `Object[]`. Khi mảng đầy, nó tạo mảng mới to gấp 1.5 lần và copy dữ liệu sang. 
   - **Senior Insight**: Thao tác `remove(index)` ở giữa mảng cực kỳ tốn kém vì phải dịch chuyển hàng vạn phần tử phía sau.

### Sai lầm & Best Practice
- **Sai lầm**: Không khai báo `initialCapacity`. Nếu bạn định nhét 10,000 item vào ArrayList mà không khai báo, nó sẽ phải resize và copy mảng hàng chục lần.
- **Best Practice**: Luôn ước lượng kích thước dữ liệu trước khi khởi tạo Collection.

---

## 7. Exception Handling: Chiến lược xử lý lỗi

### Nó là gì?
- **Checked Exception**: Bắt buộc phải `try-catch` (Ví dụ: `IOException`).
- **Unchecked Exception**: Lỗi logic, không bắt buộc (Ví dụ: `NullPointerException`).

### Dùng khi nào?
- Dùng **Checked** cho các lỗi ngoại cảnh (mạng đứt, file hỏng) mà app có thể thử lại.
- Dùng **Unchecked** cho các lỗi lập trình (logic sai, truyền tham số null) – những lỗi này tốt nhất nên để app "chết sạch" để sớm phát hiện.

### Ví dụ code Thực chiến:
```java
// Global Exception Handler cho Netty Server
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error("Player {} error: ", playerId, cause);
    if (cause instanceof InventoryFullException) {
        ctx.writeAndFlush(new S_InventoryFull()); // Báo lỗi cho client
    } else {
        ctx.close(); // Đóng kết nối nếu lỗi nghiêm trọng
    }
}
```

---


---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Phân biệt `String literal` và `new String()`? Tại sao String lại Immutable?
- **Answer**: String literal nằm trong **String Pool** (Heap), giúp tiết kiệm bộ nhớ nếu nhiều biến cùng giá trị. String Immutable để đảm bảo an toàn cho Multi-threading và Security (không ai sửa được giá trị khi nó đang được truyền đi).

### 2. "Stop-The-World" trong GC là gì? Làm sao để giảm thiểu nó trong game 100k CCU?
- **Answer**: STW là lúc GC dừng toàn bộ app để dọn rác. Giảm thiểu bằng cách: 1. Dùng GC Latency thấp (ZGC/Shenandoah). 2. Tuning Heap size (không quá to, không quá nhỏ). 3. Hạn chế tạo rác (Object Pooling).

### 3. Java Generic có tồn tại ở Runtime không? (Type Erasure)
- **Answer**: Không. Java xóa bỏ thông tin Generic sau khi compile để tương thích ngược. Điều này dẫn đến việc bạn không thể `new T()` hoặc `instanceof T`.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế một hệ thống **EventBus** đơn giản bằng Reflection.
- Các hàm xử lý sự kiện sẽ được đánh dấu bằng `@Subscribe`.
- Khi gọi `eventBus.post(new PlayerLevelUpEvent())`, hệ thống tự tìm và gọi các hàm tương ứng.
