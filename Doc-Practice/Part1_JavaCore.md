# PHẦN 1: JAVA CORE (NỀN TẢNG CHO GAME SERVER)

Chào các bạn, tôi là Senior Java Game Backend Engineer. Trong thế giới game online, Java không chỉ là ngôn ngữ để code, nó là một cỗ máy cần được tinh chỉnh (tuning) để đạt hiệu năng tối đa. Part 1 này chúng ta sẽ đi vào những thứ "xương máu" nhất.

---

## 1. OOP Pillars: 4 Tột đỉnh của Hướng đối tượng

### Nó là gì? (Lý thuyết chuẩn)
Dù bạn là Senior, 4 tính chất này vẫn là "kinh thánh" không thể quên:
1. **Encapsulation (Đóng gói)**: Che giấu dữ liệu bên trong bằng `private` và chỉ lộ ra qua các phương thức `public` (Getters/Setters). Giúp kiểm soát dữ liệu và giảm sự phụ thuộc giữa các class.
2. **Inheritance (Kế thừa)**: Cho phép một class con kế thừa lại các thuộc tính và hành vi của class cha (`extends`). Giúp tái sử dụng code.
3. **Polymorphism (Đa hình)**: Một đối tượng có thể đóng nhiều vai trò khác nhau. 
   - *Overriding*: Thay đổi logic hàm của cha ở lớp con.
   - *Overloading*: Cùng tên hàm nhưng khác tham số.
4. **Abstraction (Trừu tượng)**: Tập trung vào "Hệ thống làm được gì" thay vì "Hệ thống làm như thế nào". Đại diện bởi **Interface** và **Abstract Class**.

### Dùng để làm gì trong Game Backend?
- **Abstraction (Trừu tượng)**: Định nghĩa một `Skill` chung. `Fireball` hay `Heal` sẽ tự triển khai logic `execute()` riêng.
- **Polymorphism (Đa hình)**: Bạn có thể lưu một danh sách `List<Entity>` chứa cả Player và NPC, rồi gọi `.update()` cho tất cả mà không cần biết chúng là gì.

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

### Sai lầm & Best Practice
- **Sai lầm**: `new` object trong vòng lặp game (Tick). Ví dụ: `new Vector3D()` 60 lần/giây cho 10,000 player => GC sẽ "vả" sập server.
- **Best Practice**: **Object Pooling**. Tạo sẵn 1,000 đối tượng Bullet, khi dùng thì lấy ra, dùng xong trả lại, đừng bao giờ `new` rồi vứt đi.

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

### Sai lầm & Best Practice
- **Sai lầm**: Dùng Reflection quá nhiều trong Game Loop. Reflection **chậm** hơn gọi trực tiếp khoảng 10-100 lần.
- **Best Practice**: Cache kết quả Reflection. Chỉ dùng Reflection lúc **Server Startup** để khởi tạo, tuyệt đối không dùng lúc đang chiến đấu.

---

## 5. Functional Programming (Java 8+ Streams)

### Nó là gì?
Cách viết code tập trung vào "Dữ liệu chạy qua các ống lọc" thay vì viết vòng lặp `for` truyền thống.

### Dùng để làm gì?
- Xử lý danh sách player: Lọc (Filter), Chuyển đổi (Map), Thu gọn (Reduce).
- Viết code ngắn gọn, dễ đọc, lập luận song song (`parallelStream`) dễ dàng.

### Cơ chế (Inside Stream):
Stream không lưu trữ dữ liệu. Nó là một pipeline các thao tác. Nó chỉ thực thi khi bạn gọi các hàm "kết thúc" (Terminal operations) như `collect()` hoặc `findFirst()`.

### Sai lầm & Best Practice
- **Sai lầm**: Dùng `parallelStream()` cho mọi thứ. Nó dùng chung `ForkJoinPool` toàn hệ thống, nếu dùng sai có thể làm treo toàn bộ server network.
- **Best Practice**: Dùng Stream cho logic nghiệp vụ (Admin tool, Report). Logic chiến đấu nhạy cảm nên dùng `for` truyền thống để đạt performance cao nhất.

---

## 6. Collections Internals: HashMap & ArrayList

### Nó là gì?
- **HashMap**: Cấu trúc dữ liệu dạng Key-Value có tốc độ truy xuất gần như tức thì.
- **ArrayList**: Mảng động có thể tự co dãn kích thước.

### Cơ chế "Dưới nắp máy" (How it works):
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

### Cơ chế trong Game Server: Global Exception Handler
Đừng bao giờ để `try-catch` rác rưởi khắp nơi. Hãy dùng một **Global Handler** để bắt các lỗi chưa được xử lý, log lại và thông báo cho người chơi thay vì để server sụp đổ im lặng.

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
