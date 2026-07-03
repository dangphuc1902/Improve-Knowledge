# 🎯 Kế hoạch Chi tiết Từng Ngày & Buổi (8 Tuần - Sáng 4:30 & Tối 20:00)

Tài liệu này mở rộng chi tiết toàn bộ lộ trình học tập **8 tuần** (từ 26/06/2026 đến 20/08/2026). Mỗi ngày trong tuần đều được phân rã thành các block 30 phút/75 phút kèm theo **Ví dụ cụ thể (Concrete Examples)** và **Tiêu chí hoàn thành** nhằm đảm bảo tính thực chiến tuyệt đối.

---

## ⏰ Khung Thời Gian Chuẩn (Time Block Templates)

### 🌅 Khung Thứ 2 - Thứ 6 (Weekday Schedule)
| Khoảng thời gian | Block học | Nội dung chi tiết & Phương pháp học |
| :--- | :--- | :--- |
| **04:30 - 05:00** | 🇬🇧 English | Shadowing tech talk/mock interviews. Ghi chú 3-5 keywords chuyên ngành. |
| **05:00 - 06:00** | 📚 Deep Topic | Đọc tài liệu lý thuyết sâu (DB, Distributed Systems, Network) & Demo. |
| **06:00 - 06:45** | 📝 Java/Spring Deep | Đọc internals của Java Core, JVM hoặc Spring Boot. Tóm tắt bằng *Feynman*. |
| **06:45 - 07:00** | 📋 Review sáng | Note-taking nhanh, log review kiến thức. |
| *08:00 - 18:00* | *💼 Giờ đi làm* | *Focus công việc chính tại Gihot.* |
| **20:00 - 21:30** | 🧠 DSA Concept & Code | Đọc hint, vẽ thuật toán dry-run, code giải các bài Easy/Medium. |
| **21:30 - 22:00** | ⚡ LC Review & Push | Đọc giải pháp tối ưu trên LeetCode, push code sạch lên repo & Anki sync. |

---

### 📅 Khung Thứ 7 Ngày Chẵn (Nghỉ - Weekend Saturday Schedule)
* **04:30 - 05:00**: 🇬🇧 English Shadowing (Luyện nghe/nói qua podcast kiến trúc hệ thống).
* **05:00 - 07:00**: 📚 Deep Topics Sprint: Đọc sâu tài liệu lớn, sách DDIA, thiết kế sơ đồ kiến trúc hệ thống.
* **08:00 - 10:00**: 💻 LeetCode Marathon (90m timed block giải 3 bài liên tục để luyện sức bền + 30m review).
* **10:00 - 11:30**: 🏗️ System Design (Tự giải 1 bài toán lớn, phác thảo API & Data Model).
* **11:30 - 12:00**: 📝 STAR Stories Practice (Viết & cập nhật 1-2 câu chuyện dự án theo khung STAR).
* **13:30 - 15:30**: ☕ Java/Spring Deep: Đọc sâu cơ chế phức tạp (Concurrency, Memory, Filters) & Demo.
* **15:30 - 17:00**: 📄 CV & Apply prep (Tối ưu CV, viết cover letter, nộp đơn).
* **17:00 - 17:30**: 📋 Weekly Review (Chấm điểm tiến độ tuần).

---

### 📅 Khung Thứ 7 Ngày Lẻ (Đi làm - Saturday Workday Schedule)
* **04:30 - 05:00**: 🇬🇧 English Shadowing.
* **05:00 - 06:00**: 📚 Deep Topic (Theory & Practice).
* **06:00 - 06:45**: 📝 Java/Spring Deep (Theory & Practice).
* **06:45 - 07:00**: 📋 Review & Note-taking.
* *08:00 - 18:00*: *💼 Giờ đi làm.*
* **20:00 - 22:00**: 💻 LeetCode Marathon (Timed coding giải quyết các bài tập ôn luyện cuối tuần).

---

### 📅 Khung Chủ Nhật (Nghỉ - Sunday Schedule)
* **04:30 - 05:00**: 🇬🇧 English Writing (Viết 1 bài post ngắn chia sẻ kỹ thuật bằng tiếng Anh lên LinkedIn/GitHub).
* **05:00 - 07:00**: 💻 LeetCode Review & Optimize (Giải lại các bài bị stuck hoặc giải chậm, tối ưu code).
* **09:00 - 11:00**: 📖 Reading (DDIA) (Đọc 1 chương trong sách Designing Data-Intensive Applications).
* **11:00 - 12:30**: 📚 System Design (Lẻ) (Dành cho các tuần đi làm Thứ 7 lẻ: Bổ sung kiến thức thiết kế hệ thống).
* **14:00 - 16:00**: 🎤 Mock Interview (Giả lập phỏng vấn System Design/Coding & Đánh giá khuyết điểm).
* **16:00 - 17:00**: 📝 STAR/CV Prep (Lẻ) (Dành cho các tuần đi làm Thứ 7 lẻ: Viết STAR stories & CV).
* **17:00+**: 🧘 Rest & Recharge (Ngắt kết nối hoàn toàn, nghỉ ngơi lấy lại năng lượng).

---

## 📅 Lộ Trình 8 Tuần Chi Tiết Từng Ngày (Day-by-Day Plan)

### 📌 Phase 1: Foundation (Tuần 1 - Tuần 2)

<details>
<summary><b>Week 1 (26/06 - 02/07): DSA Basics, SQL Index & Joins, Java OOP & Collections</b></summary>

#### Thứ 6 (26/06) - Ngày 1
* **Sáng Deep Topic:** [indexes.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/indexes.md) (B-Tree vs Hash index).
  * *Ví dụ cụ thể:* Chạy `EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'test@gmail.com'` trong Postgres trước và sau khi đánh index để so sánh `Seq Scan` vs `Index Scan`.
* **Sáng Java:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) (OOP principles & SOLID basics).
  * *Ví dụ cụ thể:* Viết demo nhỏ vi phạm nguyên lý Single Responsibility Principle (tính toán hóa đơn và in PDF trong cùng một class) rồi tiến hành refactor tách chúng ra.
* **Tối DSA:** [Arrays](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Two Sum, Valid Anagram).
  * *Ví dụ cụ thể:* Code giải Two Sum dùng brute-force $O(N^2)$ rồi chuyển đổi sang dùng `HashMap` để tra cứu trong $O(1)$, đưa tổng thời gian về $O(N)$.

#### Thứ 7 (27/06) - Ngày Lẻ (Đi làm)
* **Sáng Deep Topic:** Đọc sâu về Transaction Lock và Deadlock trong PostgreSQL.
  * *Ví dụ cụ thể:* Viết script tạo Deadlock bằng cách cho Transaction A update Row 1 rồi Row 2, đồng thời Transaction B update Row 2 rồi Row 1.
* **Sáng Java:** Code demo deadlock, chạy thử nghiệm bằng JVM Tool/PostgreSQL PG_STAT_ACTIVITY.
* **Tối DSA (LeetCode Marathon):** Giải 3-4 bài Arrays & Linked List trong 2 tiếng timed coding không autocomplete.

#### Chủ Nhật (28/06) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài toán Linked List bị chạy quá 30 phút trong tuần.
* **DDIA Reading:** Đọc Chapter 1: Reliable, Scalable, and Maintainable Applications.
* **System Design (Lẻ):** Phân tích cấu trúc RESHADED thông qua bài toán thiết kế TinyURL (chuyển từ Thứ 7 lẻ).
* **Mock Interview:** Nhờ AI/bạn bè hỏi các câu hỏi Java Core OOP & Collection.
* **STAR/CV Prep (Lẻ):** Cập nhật 2 câu chuyện STAR về dự án cũ liên quan đến xử lý bất đồng bộ hoặc database (chuyển từ Thứ 7 lẻ).

#### Thứ 2 (29/06) - Ngày 4
* **Sáng Deep Topic:** [joins.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/joins.md) (Nested Loop, Hash Join, Merge Join).
  * *Ví dụ cụ thể:* Tạo 2 bảng có kích thước chênh lệch lớn, thực hiện INNER JOIN và xem Query Plan để hiểu khi nào database engine lựa chọn Hash Join thay vì Nested Loop Join.
* **Sáng Java:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) (Collections Framework internals).
  * *Ví dụ cụ thể:* So sánh tốc độ đọc ghi giữa `ArrayList` và `LinkedList` bằng cách thêm/xóa 100k phần tử ở đầu danh sách.
* **Tối DSA:** [Two Pointers](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (3Sum, Container With Most Water).
  * *Ví dụ cụ thể:* Dùng hai con trỏ `left` và `right` co dần khoảng cách để tìm diện tích lớn nhất mà không cần chạy 2 vòng lặp lồng nhau.

#### Thứ 3 (30/06) - Ngày 5
* **Sáng Deep Topic:** [transactions.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/transactions.md) (ACID, Isolation Levels, MVCC).
  * *Ví dụ cụ thể:* Mở 2 terminal psql chạy song song để mô phỏng lỗi `Non-repeatable Read` ở mức isolation `Read Committed` và xem cách `Repeatable Read` xử lý nó nhờ MVCC.
* **Sáng Java:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) (Generics & Wildcards).
  * *Ví dụ cụ thể:* Viết một Generic Method in ra danh sách các đối tượng kế thừa từ một class cha (`List<? extends Number>`) để hiểu cơ chế PECS (Producer Extends, Consumer Super).
* **Tối DSA:** [Stack](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Valid Parentheses, Min Stack).
  * *Ví dụ cụ thể:* Mô phỏng cơ chế push/pop dấu ngoặc vào Stack để kiểm tra ngoặc đóng mở hợp lệ.

#### Thứ 4 (01/07) - Ngày 6
* **Sáng Deep Topic:** [http.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/http.md) & [dns.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/dns.md).
  * *Ví dụ cụ thể:* Sử dụng lệnh `dig google.com` hoặc `nslookup` để phân tích các bản ghi DNS (A, AAAA, CNAME) và luồng phân giải từ Root Server.
* **Sáng Java:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) (Exception Handling best practices).
  * *Ví dụ cụ thể:* Viết code dùng `try-with-resources` tự động đóng `BufferedReader` để tránh rò rỉ tài nguyên (resource leak).
* **Tối DSA:** [HashMap](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Group Anagrams, Top K Frequent Elements).
  * *Ví dụ cụ thể:* Sử dụng chuỗi ký tự được sắp xếp hoặc mảng tần suất làm Key cho `HashMap` để nhóm các từ đồng âm (Anagrams).

#### Thứ 5 (02/07) - Ngày 7
* **Sáng Deep Topic:** [rest.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/rest.md) & [grpc.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/grpc.md) & [graphql.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/graphql.md).
  * *Ví dụ cụ thể:* Tạo file `.proto` đơn giản cho dịch vụ User, biên dịch ra Java class để thấy cấu trúc nhị phân của gRPC giúp truyền tải dữ liệu nhanh hơn JSON.
* **Sáng Java:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) (Concurrency basics, synchronized, volatile).  
  * *Ví dụ cụ thể:* Viết class tăng biến đếm dùng 10 threads chạy đồng thời. Chỉ ra lỗi Race Condition khi không đồng bộ và sửa bằng `AtomicInteger`.
* **Tối DSA:** [Linked List](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Reverse Linked List, Merge Two Sorted Lists).
  * *Ví dụ cụ thể:* Vẽ sơ đồ dịch chuyển các con trỏ `prev`, `curr`, `next` từng bước một để đảo ngược danh sách liên kết trên giấy trước khi gõ code.
</details>

<details>
<summary><b>Week 2 (03/07 - 09/07): Advanced Database, Redis, Spring Boot Basics, Binary Search & Trees</b></summary>

#### Thứ 6 (03/07) - Ngày 1
* **Sáng Deep Topic:** [02-postgresql-advanced.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/02-postgresql-advanced.md) (Recursive CTE & Window Functions).
  * *Ví dụ cụ thể:* Viết câu query CTE đệ quy để lấy toàn bộ danh mục cây sản phẩm (Catalog hierarchy) nhiều cấp.
* **Sáng Java:** [07-java17-21-features.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/07-java17-21-features.md) (Streams API & Lambda).
  * *Ví dụ cụ thể:* Dùng `stream().filter().collect(Collectors.groupingBy())` để nhóm danh sách giao dịch theo loại tiền tệ và tính tổng số tiền giao dịch.
* **Tối DSA:** [Binary Search](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Search in Rotated Sorted Array).
  * *Ví dụ cụ thể:* Xác định xem nửa trái hay nửa phải của mảng đang được sắp xếp để quyết định hướng dịch chuyển con trỏ `mid`.

#### Thứ 7 (04/07) - Ngày Chẵn (Nghỉ)
* **Sáng Sprint:** Đọc hiểu cơ chế đồng bộ hóa dữ liệu giữa Redis Master-Replica và Sentinel.
  * *Ví dụ cụ thể:* Cài đặt Redis Docker Compose gồm 1 Master, 2 Replicas, kiểm tra ghi dữ liệu vào Master và đọc ra từ Replica.
* **LeetCode Marathon:** Giải 6 bài Binary Search và Tree trong 2 block timed.
* **Chiều SD:** Thiết kế hệ thống Đếm Lượt View bài viết (Read/Write heavy) sử dụng Redis làm bộ nhớ đệm chống quá tải Database.
* **Tiêu chí:** Hoàn thành tài liệu phân tích hệ thống cache cho CV.

#### Chủ Nhật (05/07) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài toán Sliding Window phức tạp (ví dụ: Minimum Window Substring).
* **DDIA Reading:** Đọc Chapter 2: Data Models and Query Languages.
* **Mock Interview:** Giả lập phỏng vấn kỹ năng thiết kế Database schema & Caching strategy.

#### Thứ 2 (06/07) - Ngày 4
* **Sáng Deep Topic:** [02-postgresql-advanced.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/02-postgresql-advanced.md) (JSONB usage & GIN Index).
  * *Ví dụ cụ thể:* Tạo một cột chứa dữ liệu JSONB trong bảng sản phẩm, viết query tìm kiếm theo thuộc tính bên trong JSON và đánh `GIN Index` để so sánh tốc độ.
* **Sáng Java:** [07-java17-21-features.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/07-java17-21-features.md) (Optional type & Clean Code).
  * *Ví dụ cụ thể:* Refactor code kiểm tra thông tin khách hàng nhiều tầng để thay thế loạt lệnh `if (x != null)` bằng `Optional.ofNullable(x).map(Customer::getAddress).orElseThrow()`.
* **Tối DSA:** [Sliding Window](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Longest Substring Without Repeating Characters).
  * *Ví dụ cụ thể:* Dùng `HashMap` lưu vị trí xuất hiện cuối cùng của các ký tự để dịch chuyển cạnh trái của cửa sổ (Sliding Window) hiệu quả.

#### Thứ 3 (07/07) - Ngày 5
* **Sáng Deep Topic:** [02-postgresql-advanced.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/02-postgresql-advanced.md) (Table Partitioning).
  * *Ví dụ cụ thể:* Tạo bảng `orders_partitioned` phân hoạch theo thời gian (Range Partitioning), chèn 1 triệu dòng và kiểm tra database chỉ quét phân vùng cần thiết khi query.
* **Sáng Java:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) (Java I/O vs NIO).
  * *Ví dụ cụ thể:* Viết chương trình copy một file video dung lượng 100MB bằng cách dùng luồng byte truyền thống vs dùng NIO `FileChannel` để đo sự khác biệt thời gian.
* **Tối DSA:** [Trees Theory](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Duyệt cây nhị phân BFS/DFS).
  * *Ví dụ cụ thể:* Viết code đệ quy duyệt cây nhị phân theo 3 thứ tự: In-order, Pre-order, Post-order.

#### Thứ 4 (08/07) - Ngày 6
* **Sáng Deep Topic:** [redis.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/redis.md) (Redis Data structures & TTL).
  * *Ví dụ cụ thể:* Sử dụng lệnh Redis CLI để quản lý Cache: `SET`, `GET` kèm `EX` (TTL), lưu cấu trúc phức tạp bằng Hash (`HSET`) để tránh trùng lặp Key.
* **Sáng Spring:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/Theory.md) (IoC, DI & Bean Lifecycle).
  * *Ví dụ cụ thể:* Viết class implement `BeanPostProcessor` để in log các giai đoạn khởi tạo Bean trong Spring ApplicationContext.
* **Tối DSA:** [Trees Practice](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Invert Binary Tree, Maximum Depth).
  * *Ví dụ cụ thể:* Viết hàm đảo ngược cây nhị phân bằng cách đổi chỗ hai node con trái/phải ở mỗi bước đệ quy.

#### Thứ 5 (09/07) - Ngày 7
* **Sáng Deep Topic:** [redis-production.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/redis.md) (Cache Stampede, Avalanche, Penetration).
  * *Ví dụ cụ thể:* Code giải quyết lỗi Cache Penetration bằng cách lưu giá trị rỗng (`Null Value`) kèm TTL ngắn khi không tìm thấy record trong Database.
* **Sáng Spring:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/Theory.md) (Auto-Configuration internals).
  * *Ví dụ cụ thể:* Tạo một Custom Annotation `@ConditionalOnProperty` để chỉ khởi chạy một service cụ thể khi biến cấu hình trong `application.properties` được set là true.
* **Tối DSA:** [Heap / Priority Queue](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Top K Frequent Elements).
  * *Ví dụ cụ thể:* Sử dụng một `Priority Queue` kích thước K để duy trì K phần tử lớn nhất, giảm độ phức tạp từ $O(N \log N)$ xuống $O(N \log K)$.
</details>

<details>
<summary><b>Week 3 (10/07 - 16/07): Distributed Systems, Spring Security JWT/OAuth2, Graphs & Dynamic Programming</b></summary>

#### Thứ 6 (10/07) - Ngày 1
* **Sáng Deep Topic:** [distributed_systems.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/distributed_systems.md) (CAP Theorem & PACELC).
  * *Ví dụ cụ thể:* Phân tích hệ thống MongoDB xem khi nào nó ưu tiên tính nhất quán (C) và khi nào ưu tiên tính sẵn sàng (A) dựa trên cấu hình Write Concern (`w: "majority"`).
* **Sáng Spring:** [02-spring-security-deep.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/02-spring-security-deep.md) (JWT Auth Filter).
  * *Ví dụ cụ thể:* Viết `OncePerRequestFilter` tùy chỉnh để chặn request, kiểm tra header `Authorization: Bearer <token>`, giải mã JWT và set Authentication vào `SecurityContextHolder`.
* **Tối DSA:** [Graphs](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (BFS / DFS implementation).
  * *Ví dụ cụ thể:* Viết thuật toán BFS tìm khoảng cách ngắn nhất từ một node gốc đến tất cả các node còn lại trong đồ thị không trọng số.

#### Thứ 7 (11/07) - Ngày Lẻ (Đi làm)
* **Sáng Deep Topic:** Đọc hiểu và triển khai Kafka Transactions (Exactly-Once Semantics).
  * *Ví dụ cụ thể:* Viết Java code cấu hình Producer với `enable.idempotence=true` và thực hiện gửi message trong khối Transaction.
* **Sáng Spring:** Viết code demo Kafka transaction listener, kiểm chứng rollback khi có lỗi runtime exception.
* **Tối DSA (LeetCode Marathon):** Giải 3-4 bài Graph và 1D-DP under time pressure.

#### Chủ Nhật (12/07) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài toán Graph tìm chu trình hoặc DP tối ưu hóa.
* **DDIA Reading:** Đọc Chapter 3: Storage and Retrieval (Cơ chế LSM-Tree vs B-Tree).
* **System Design (Lẻ):** Thiết kế hệ thống Notification đa kênh xử lý hàng triệu thông báo mỗi ngày dùng Kafka làm hàng đợi đệm (chuyển từ Thứ 7 lẻ).
* **Mock Interview:** Giả lập phỏng vấn sâu về cơ chế Security JWT và Kafka Internals.
* **STAR/CV Prep (Lẻ):** Cập nhật CV với kỹ năng tích hợp Spring Cloud & Kafka (chuyển từ Thứ 7 lẻ).

#### Thứ 2 (13/07) - Ngày 4
* **Sáng Deep Topic:** [cap-consistency-models.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/cap-consistency-models.md) (Consistency Models).
  * *Ví dụ cụ thể:* Kịch bản mất đồng bộ dữ liệu khi dùng Eventual Consistency trong hệ thống giỏ hàng và cách xử lý bằng Conflict-free Replicated Data Types (CRDT).
* **Sáng Spring:** [02-spring-security-deep.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/02-spring-security-deep.md) (OAuth2 Login).
  * *Ví dụ cụ thể:* Cấu hình Spring Boot Client kết nối với GitHub làm OAuth2 Provider để lấy thông tin email người dùng đăng nhập.
* **Tối DSA:** [Graphs Practice](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Clone Graph, Course Schedule).
  * *Ví dụ cụ thể:* Sử dụng thuật toán Topological Sort (Kahn's algorithm) dùng In-degree (bậc vào) để phát hiện chu trình phụ thuộc môn học.

#### Thứ 3 (14/07) - Ngày 5
* **Sáng Deep Topic:** [kafka-deep-dive-game.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/kafka-deep-dive-game.md) (Kafka Broker & Partitioning).
  * *Ví dụ cụ thể:* Khởi động Kafka cụm 3 broker bằng Docker Compose, tạo topic có 3 partition và 2 replica để kiểm tra khả năng chịu lỗi khi tắt 1 broker.
* **Sáng Spring:** [02-spring-security-deep.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/02-spring-security-deep.md) (RBAC vs ABAC).
  * *Ví dụ cụ thể:* Thiết lập `@PreAuthorize("hasRole('ADMIN')")` cho Admin controller, viết custom Security Expression Handler để phân quyền dựa trên thuộc tính phòng ban (ABAC).
* **Tối DSA:** [1D-DP Theory](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Memoization vs Tabulation).
  * *Ví dụ cụ thể:* Giải bài toán Fibonacci bằng 3 cách: đệ quy thường $O(2^N)$, đệ quy có nhớ (Memoization) $O(N)$ và khử đệ quy dùng mảng (Tabulation) $O(N)$.

#### Thứ 4 (15/07) - Ngày 6
* **Sáng Deep Topic:** [kafka-deep-dive-game.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/kafka-deep-dive-game.md) (Consumer Groups & Rebalance).
  * *Ví dụ cụ thể:* Chạy 2 consumer instance thuộc cùng một Consumer Group để quan sát Kafka phân chia partition, sau đó tắt 1 instance để xem luồng Rebalancing chuyển partition sang consumer còn lại.
* **Sáng Spring:** [04-spring-cloud.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/04-spring-cloud.md) (Spring Cloud Gateway).
  * *Ví dụ cụ thể:* Cấu hình route định tuyến request `/api/v1/orders/**` tới Order Service, viết custom Gateway Filter để thêm header `X-Request-Id` cho mỗi request đi qua.
* **Tối DSA:** [1D-DP Practice](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Coin Change, Climbing Stairs).
  * *Ví dụ cụ thể:* Định nghĩa trạng thái `dp[i]` là số cách tối thiểu để đổi được số tiền `i`, viết công thức chuyển trạng thái từ các mệnh giá tiền có sẵn.

#### Thứ 5 (16/07) - Ngày 7
* **Sáng Deep Topic:** [02-distributed-transactions-resilience.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/02-distributed-transactions-resilience.md) (Saga Pattern).
  * *Ví dụ cụ thể:* Thiết kế sơ đồ Saga Orchestrator cho giao dịch đặt vé máy bay: Booking Service -> Payment Service -> Ticket Service. Nếu thanh toán lỗi, gửi command rollback hoàn trả trạng thái Booking.
* **Sáng Spring:** [04-spring-cloud.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/04-spring-cloud.md) (Eureka Service Discovery).
  * *Ví dụ cụ thể:* Cấu hình cho Order Service tự động đăng ký với Eureka Server. Viết Feign Client trong Customer Service để gọi API của Order Service bằng tên service (`http://order-service/orders/`).
* **Tối DSA:** [Backtracking](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Subsets, Permutations).
  * *Ví dụ cụ thể:* Sử dụng đệ quy quay lui để sinh tất cả các hoán vị của một mảng số nguyên, vẽ cây quyết định (Decision Tree) để hiểu cách quay lui khôi phục trạng thái cũ.
</details>

<details>
<summary><b>Week 4 (17/07 - 23/07): Distributed Resilience, Cloud (AWS Core), Advanced Testing, Advanced DSA</b></summary>

#### Thứ 6 (17/07) - Ngày 1
* **Sáng Deep Topic:** [02-distributed-transactions-resilience.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/02-distributed-transactions-resilience.md) (Outbox Pattern & CDC).
  * *Ví dụ cụ thể:* Cấu hình Debezium lắng nghe WAL (Write-Ahead Log) của PostgreSQL để tự động đẩy sự thay đổi ở bảng `outbox` lên Kafka Topic tương ứng.
* **Sáng Spring:** [04-spring-cloud.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/04-spring-cloud.md) (Spring Cloud Config Server).
  * *Ví dụ cụ thể:* Tạo một Config Server đọc cấu hình từ một git repository riêng tư, thiết lập các client service lấy cấu hình tự động khi startup.
* **Tối DSA:** [Trie](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Implement Trie).
  * *Ví dụ cụ thể:* Thiết kế class `TrieNode` có mảng con 26 phần tử đại diện cho bảng chữ cái và cờ `isEndOfWord`. Viết hàm chèn từ và tìm kiếm tiền tố.

#### Thứ 7 (18/07) - Ngày Chẵn (Nghỉ)
* **Sáng Sprint:** Cài đặt toàn diện môi trường tích hợp CICD GitHub Actions tự động build project Spring Boot, chạy Testcontainers, đóng gói Docker và deploy lên môi trường giả lập AWS EC2.
* **LeetCode Marathon:** Giải 6 bài 2D-DP và Intervals dưới áp lực thời gian.
* **Chiều SD:** Thiết kế hệ thống Đặt Vé xem phim với yêu cầu xử lý giao dịch thanh toán đồng thời (Concurrency control) và giữ vé trong 15 phút.
* **Tiêu chí:** Có 1 sơ đồ thiết kế kiến trúc hoàn thiện đính kèm thư mục dự án.

#### Chủ Nhật (19/07) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài toán 2D-DP phức tạp liên quan tới chuỗi (như Edit Distance).
* **DDIA Reading:** Đọc Chapter 5: Replication (Single-leader, Multi-leader, Leaderless).
* **Mock Interview:** Thực hiện mock interview chuyên sâu về AWS Infrastructure và các chiến thuật thiết kế Integration Testing hiệu quả.

#### Thứ 2 (20/07) - Ngày 4
* **Sáng Deep Topic:** [02-distributed-transactions-resilience.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/02-distributed-transactions-resilience.md) (Circuit Breaker Resilience4j).
  * *Ví dụ cụ thể:* Cấu hình Resilience4j trong Spring Boot để bọc cuộc gọi HTTP sang bên thứ ba. Giả lập lỗi kết nối để xem Circuit Breaker chuyển từ `CLOSED` sang `OPEN` và trả về fallback data.
* **Sáng Spring:** [05-spring-testing.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/05-spring-testing.md) (Unit Testing with Mockito).
  * *Ví dụ cụ thể:* Viết Unit Test cho `OrderService` dùng `@ExtendWith(MockitoExtension.class)` và `@Mock` để giả lập dữ liệu trả về từ `OrderRepository`.
* **Tối DSA:** [Advanced Graphs](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Number of Islands, Graph Valid Tree).
  * *Ví dụ cụ thể:* Dùng thuật toán DFS quét qua các điểm xung quanh để gộp các ô đất liền kề nhau thành 1 hòn đảo, đánh dấu các điểm đã thăm bằng mảng boolean.

#### Thứ 3 (21/07) - Ngày 5
* **Sáng Deep Topic:** [02-distributed-transactions-resilience.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/02-distributed-transactions-resilience.md) (Rate Limiting).
  * *Ví dụ cụ thể:* Viết script Lua chạy trong Redis để triển khai thuật toán Token Bucket giới hạn tối đa 5 request mỗi phút cho mỗi API Key.
* **Sáng Spring:** [05-spring-testing.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/05-spring-testing.md) (Integration Testing with MockMvc).
  * *Ví dụ cụ thể:* Viết Integration Test sử dụng `@WebMvcTest(UserController.class)` để gửi request giả lập HTTP POST tạo tài khoản và assert kết quả JSON trả về.
* **Tối DSA:** [2D-DP](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Unique Paths, Longest Common Subsequence).
  * *Ví dụ cụ thể:* Xây dựng bảng ma trận `dp[i][j]` lưu số lượng đường đi từ góc trên bên trái tới ô `(i, j)`, viết công thức quy hoạch động: `dp[i][j] = dp[i-1][j] + dp[i][j-1]`.

#### Thứ 4 (22/07) - Ngày 6
* **Sáng Deep Topic:** [01-aws-core-services.md](file:///d:/WorkSpace/Document/Improve-Knowledge/07-Cloud-DevOps/01-aws-core-services.md) (AWS EC2 & S3).
  * *Ví dụ cụ thể:* Viết kịch bản AWS CLI tạo một EC2 Instance chạy Ubuntu, sau đó tạo một private S3 Bucket và cấp quyền truy cập thông qua IAM Role.
* **Sáng Spring:** [05-spring-testing.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/05-spring-testing.md) (Testcontainers Integration).
  * *Ví dụ cụ thể:* Viết test kế thừa Class chứa cấu hình `@Container PostgreSQLContainer` để chạy kiểm thử database trên database PostgreSQL Docker thật khi build Maven.
* **Tối DSA:** [Intervals & Greedy](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Merge Intervals).
  * *Ví dụ cụ thể:* Sắp xếp các đoạn interval theo điểm bắt đầu, lặp qua mảng và gộp hai đoạn trùng khít nếu điểm bắt đầu của đoạn sau nhỏ hơn điểm kết thúc của đoạn trước.

#### Thứ 5 (23/07) - Ngày 7
* **Sáng Deep Topic:** [01-aws-core-services.md](file:///d:/WorkSpace/Document/Improve-Knowledge/07-Cloud-DevOps/01-aws-core-services.md) (AWS RDS & Lambda).
  * *Ví dụ cụ thể:* Tạo một function AWS Lambda resize ảnh tải lên từ S3 Bucket và lưu link kết quả vào cơ sở dữ liệu Postgres RDS.
* **Sáng Java:** [07-java17-21-features.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/07-java17-21-features.md) (Java 17 Records & Pattern Matching).
  * *Ví dụ cụ thể:* Refactor cấu trúc model cũ sang sử dụng Java `record` giúp tự động sinh constructor, getter, kết hợp pattern matching trong switch case.
* **Tối DSA:** [Bit Manipulation](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/) (Single Number, Number of 1 Bits).
  * *Ví dụ cụ thể:* Dùng phép toán XOR (`^`) để tìm ra số duy nhất xuất hiện lẻ lần trong mảng số nguyên mà không cần dùng thêm bộ nhớ phụ.
</details>

<details>
<summary><b>Week 5 (24/07 - 30/07): Cloud Messaging, Containerization, Java 21, Design Patterns & STAR Stories</b></summary>

#### Thứ 6 (24/07) - Ngày 1
* **Sáng Deep Topic:** [01-aws-core-services.md](file:///d:/WorkSpace/Document/Improve-Knowledge/07-Cloud-DevOps/01-aws-core-services.md) (AWS SQS/SNS vs Kafka).
  * *Ví dụ cụ thể:* Phân tích bài toán gửi OTP: Sử dụng Amazon SNS đẩy tin nhắn tức thời tới SMS Gateway, so sánh với việc ghi log vào Kafka Stream xử lý hàng loạt.
* **Sáng Java:** [07-java17-21-features.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/07-java17-21-features.md) (Java 21 Virtual Threads).
  * *Ví dụ cụ thể:* Viết chương trình so sánh hiệu năng của Virtual Threads vs Thread Pool truyền thống khi thực hiện 10k cuộc gọi I/O nghẽn.
* **Tối DSA:** Timed Practice (3 bài Medium thuộc các chủ đề Arrays, Tree, Queue) trong 90 phút.

#### Thứ 7 (25/07) - Ngày Lẻ (Đi làm)
* **Sáng Deep Topic:** Cài đặt và triển khai Hexagonal Architecture (Ports and Adapters) cho Module quản lý User.
  * *Ví dụ cụ thể:* Viết Domain layer tách biệt hoàn toàn khỏi Database entity và Spring dependencies.
* **Sáng Spring:** Viết các Adapter implementation cho HTTP controller (Inbound) và Spring Data JPA repository (Outbound).
* **Tối DSA (LeetCode Marathon):** Ôn luyện các bài tập thuộc nhóm yếu (weak topics) ghi nhận trong tuần.

#### Chủ Nhật (26/07) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài toán DSA nâng cao trong tuần.
* **DDIA Reading:** Đọc Chapter 6: Partitioning (Sharding, Key-value data partitioning).
* **System Design (Lẻ):** Thiết kế hệ thống Đặt Đồ ăn trực tuyến (GrabFood/Baemin) tập trung vào khâu phân phối đơn hàng cho tài xế gần nhất (chuyển từ Thứ 7 lẻ).
* **Mock Interview:** Thực hành mock interview tiếng Anh toàn diện về các câu hỏi hành vi (Behavioral Questions) dựa trên 6 câu chuyện STAR đã chuẩn bị.
* **STAR/CV Prep (Lẻ):** Hoàn thiện và đóng gói 6 câu chuyện STAR (chuyển từ Thứ 7 lẻ).

#### Thứ 2 (27/07) - Ngày 4
* **Sáng Deep Topic:** [ci_cd.md](file:///d:/WorkSpace/Document/Improve-Knowledge/07-Cloud-DevOps/ci_cd.md) & [docker_basics.md](file:///d:/WorkSpace/Document/Improve-Knowledge/07-Cloud-DevOps/docker/docker_basics.md).
  * *Ví dụ cụ thể:* Viết Multi-stage Dockerfile cho ứng dụng Java Spring Boot để giảm dung lượng file build image.
* **Sáng Security:** [jwt.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/jwt.md) & [oauth.md](file:///d:/WorkSpace/Document/Improve-Knowledge/08-Networking-Security/oauth.md) deep dive.
  * *Ví dụ cụ thể:* Triển khai cơ chế Refresh Token Rotation lưu trữ token đã dùng vào Redis blacklist.
* **Tối DSA:** Timed Practice (3 bài Medium thuộc các chủ đề Graph, Dynamic Programming) trong 90 phút.

#### Thứ 3 (28/07) - Ngày 5
* **Sáng Deep Topic:** [01-solid-clean-architecture.md](file:///d:/WorkSpace/Document/Improve-Knowledge/09-Design-Patterns/01-solid-clean-architecture.md) (SOLID deep dive).
  * *Ví dụ cụ thể:* Refactor code vi phạm nguyên lý Liskov Substitution Principle (LSP).
* **Sáng Prep:** [star-stories.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/star-stories.md) (STAR Stories #1 & #2).
  * *Ví dụ cụ thể:* Viết nháp câu chuyện khắc phục lỗi OOM ở production & thiết kế sync real-time dùng Kafka.
* **Tối DSA:** Contest Simulation (Tham gia làm các đề thi ảo trên LeetCode/Hackerrank).

#### Thứ 4 (29/07) - Ngày 6
* **Sáng Deep Topic:** [01-solid-clean-architecture.md](file:///d:/WorkSpace/Document/Improve-Knowledge/09-Design-Patterns/01-solid-clean-architecture.md) (Clean Architecture / Hexagonal).
  * *Ví dụ cụ thể:* Thiết kế lại Domain layer của hệ thống Order, đảm bảo không có library import ngoài pure Java.
* **Sáng Prep:** [star-stories.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/star-stories.md) (STAR Stories #3 & #4).
  * *Ví dụ cụ thể:* Soạn câu chuyện mâu thuẫn giải pháp với Lead và giải quyết dự án bị trễ hạn do thay đổi spec.
* **Tối DSA:** Review các dạng bài toán đã làm và hệ thống hóa cách tối ưu bộ nhớ.

#### Thứ 5 (30/07) - Ngày 7
* **Sáng Deep Topic:** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/09-Design-Patterns/Theory.md) (Gang of Four Patterns).
  * *Ví dụ cụ thể:* Sử dụng Factory và Strategy để tích hợp đa kênh thanh toán (Momo, VNPay, ShopeePay).
* **Sáng Prep:** [star-stories.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/star-stories.md) (STAR Stories #5 & #6).
  * *Ví dụ cụ thể:* Soạn câu chuyện thiết kế phân quyền dự án và cải tiến công nghệ mới giúp giảm chi phí AWS.
* **Tối DSA:** Mock Coding Interview (Thực hiện mock coding trực tiếp với bạn học).
</details>

<details>
<summary><b>Week 6 (31/07 - 06/08): System Design (1-5), Leetcode Maintenance, Behavioral Mocks</b></summary>

#### Thứ 6 (31/07) - Ngày 1
* **Sáng Deep Topic:** [system-design-methodology.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/system-design-methodology.md) & [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #1: URL Shortener).
  * *Ví dụ cụ thể:* Sơ đồ kiến trúc TinyURL: Base62 keygen, Cassandra clustering, replication.
* **Tối DSA:** Giải duy trì 2 bài toán mỗi tối (LC 238, LC 56).

#### Thứ 7 (01/08) - Ngày Lẻ (Đi làm)
* **Sáng Deep Topic:** Review lại toàn bộ sơ đồ thiết kế hệ thống của SD #1 và chuẩn bị lý thuyết cho SD #2 (Rate Limiter).
* **Sáng Spring:** Ôn tập thiết kế Rate Limiter logic sử dụng Filter trong Spring Boot.
* **Tối DSA (LeetCode Marathon):** Giải 6 bài tập Medium/Hard liên tục trong 2-3 tiếng không debugger.

#### Chủ Nhật (02/08) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài tập DSA liên quan tới Dynamic Programming và Graph.
* **DDIA Reading:** Đọc Chapter 7: Transactions (ACID, Concurrency Control).
* **System Design (Lẻ):** Thiết kế hệ thống Đăng ký học phần tín chỉ đại học với lượng truy cập tăng đột biến (High Concurrency & Flash Sale model - chuyển từ Thứ 7 lẻ).
* **Mock Interview:** Tham gia buổi Mock Interview đầy đủ (45 phút System Design + 45 phút Coding).
* **STAR/CV Prep (Lẻ):** Quay video thử giọng trả lời các câu hỏi Behavioral bằng tiếng Anh (chuyển từ Thứ 7 lẻ).

#### Thứ 2 (03/08) - Ngày 4
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #2: Rate Limiter).
  * *Ví dụ cụ thể:* Token Bucket vs Sliding Window Counter dùng Redis Cluster.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 102, LC 199).

#### Thứ 3 (04/08) - Ngày 5
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #3: Chat System).
  * *Ví dụ cụ thể:* WebSocket Server, Kafka messaging queue, Wide-column NoSQL.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 200, LC 207).

#### Thứ 4 (05/08) - Ngày 6
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Design/00-problems-overview.md) (SD #4: Notification System).
  * *Ví dụ cụ thể:* Priority queues cho SMS/Email/Push, Idempotent consumer.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 322) & Mock Behavioral.

#### Thứ 5 (06/08) - Ngày 7
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #5: News Feed System).
  * *Ví dụ cụ thể:* Push Model vs Pull Model cho Feed generation.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 124) & Mock Coding.
</details>

<details>
<summary><b>Week 7 (07/08 - 13/08): Complex System Design (6-10), Job Application Setup, Advanced Mocks</b></summary>

#### Thứ 6 (07/08) - Ngày 1
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #6: Payment System).
  * *Ví dụ cụ thể:* Idempotency Key, Reconciliation Service so khớp log.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 139, LC 300).

#### Thứ 7 (08/08) - Ngày Chẵn (Nghỉ)
* **Sáng Sprint:** Tổng duyệt 10 bài System Design. Tập vẽ và giải thích chi tiết sơ đồ thiết kế cho 1 bài toán bất kỳ trong 45 phút không chuẩn bị trước.
* **LeetCode Marathon:** Giải 6 bài tập khó (Hard) liên quan tới Graph và Dynamic Programming.
* **Chiều SD:** Thiết kế hệ thống Streaming Video giống Netflix (CDN distribution, video transcoding pipeline).
* **Tiêu chí:** Tối ưu hóa CV cá nhân bản tiếng Anh, cấu trúc rõ ràng các từ khóa Java/Spring/Kafka/AWS.

#### Chủ Nhật (09/08) - Weekend Review & Mock
* **Sáng Review:** Re-solve các bài toán khó đã bị chạy lố giờ trong tuần.
* **DDIA Reading:** Đọc Chapter 8: The Trouble with Distributed Systems.
* **Mock Interview:** Thực hiện mock interview tiếng Anh chuẩn format phỏng vấn tập đoàn đa quốc gia.

#### Thứ 2 (10/08) - Ngày 4
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #7: Gaming Leaderboard).
  * *Ví dụ cụ thể:* Sử dụng Sorted Set (ZSET) trong Redis, `ZADD` và `ZREVRANGE`.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 208, LC 211).

#### Thứ 3 (11/08) - Ngày 5
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #8: Distributed Cache).
  * *Ví dụ cụ thể:* Consistent Hashing, Consistent Hashing Ring, Virtual Nodes.
* **Sáng Prep:** [company-research.md](file:///d:/WorkSpace/Document/Improve-Knowledge/Plan/company-research.md).
  * *Ví dụ cụ thể:* Phân tích NAB Innovation tech stack và các câu hỏi phỏng vấn kỹ thuật liên quan.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 212).

#### Thứ 4 (12/08) - Ngày 6
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #9: Event-Driven Microservices).
  * *Ví dụ cụ thể:* Outbox Pattern, Event-driven communication, CDC.
* **Sáng Prep:** Bắt đầu chuẩn bị danh sách CV nộp đợt 1 ( NAB, MoMo, VNPay).
* **Tối DSA:** Giải duy trì 2 bài toán (LC 295).

#### Thứ 5 (13/08) - Ngày 7
* **Sáng Deep Topic:** [00-problems-overview.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/00-problems-overview.md) (SD #10: Search / Autocomplete).
  * *Ví dụ cụ thể:* Trie on memory, sync định kỳ sang Elasticsearch.
* **Sáng Prep:** Bắt đầu nộp CV vào các công ty Target Tier 2.
* **Tối DSA:** Giải duy trì 2 bài toán (LC 76).
</details>

<details>
<summary><b>Week 8 (14/08 - 20/08): Company Prep, Fast LC Warmups, Interviews & Salary Negotiation</b></summary>

#### Thứ 6 (14/08) - Ngày 1
* **Sáng Deep Topic:** Ôn tập tech stack của MoMo (Java, Spring Boot, Microservices, Kubernetes).
  * *Ví dụ cụ thể:* Đọc cách xử lý transaction phân tán và cấu hình kết nối database trong microservices lớn.
* **Tối DSA:** Giải duy trì 1-2 bài toán Easy/Medium (LC 20, LC 121).

#### Thứ 7 (15/08) - Ngày Lẻ (Đi làm)
* **Sáng Sprint:** Đọc lướt qua tất cả tài liệu tóm tắt trong repo. Sắp xếp lại cấu trúc thư mục sạch đẹp.
* **Sáng Java:** Làm ấm (Warm-up) bằng cách giải nhanh 4 bài toán Easy để tạo cảm giác tự tin.
* **Tối DSA (LeetCode Marathon):** Giải duy trì 1-2 bài toán trung bình/khó.

#### Chủ Nhật (16/08) - Weekend Review & Rest
* **Sáng Review:** Đọc lại các slide tóm tắt hệ phân tán & DDIA.
* **System Design (Lẻ):** Ôn lại kiến thức 10 bài System Design cốt lõi, tự tin với khung sườn RESHADED (chuyển từ Thứ 7 lẻ).
* **Mock Interview:** Luyện tập đàm phán lương (Salary Negotiation): Soạn sẵn kịch bản deal lương khi nhận được offer.
* **STAR/CV Prep (Lẻ):** Hoàn thiện 100% hồ sơ ứng tuyển và danh sách các headhunter liên lạc (chuyển từ Thứ 7 lẻ).
* **Chiều/Tối:** Nghỉ ngơi hoàn toàn (Rest & Recharge), ngủ sớm chuẩn bị năng lượng tốt nhất cho các vòng phỏng vấn thực tế!

#### Thứ 2 (17/08) - Ngày 4
* **Sáng Deep Topic:** Ôn tập tech stack của VNPay (Java, Spring, Database tuning, bảo mật cổng thanh toán).
  * *Ví dụ cụ thể:* Chuẩn bị các phương án trả lời về cách tối ưu SQL query và cách phòng chống lỗi SQL Injection, CSRF, XSS.
* **Tối DSA:** Giải duy trì 1-2 bài toán (LC 141, LC 206).

#### Thứ 3 (18/08) - Ngày 5
* **Sáng Deep Topic:** Ôn tập tech stack của NAB Innovation (Java, Spring Boot, AWS Services).
  * *Ví dụ cụ thể:* Đọc kỹ tài liệu AWS IAM, cấu hình VPC bảo mật và auto-scaling EC2 instances.
* **Sáng Prep:** Nộp CV vào các công ty Target Tier 1 (MoMo, NAB Innovation, VNPay, Tiki).
* **Tối DSA:** Giải duy trì 1-2 bài toán (LC 104).

#### Thứ 4 (19/08) - Ngày 6
* **Sáng Deep Topic:** Ôn tập tech stack của Money Forward (Ruby/Go/Java, Cloud infrastructure).
  * *Ví dụ cụ thể:* Quy trình giải thích thiết kế sạch sẽ (Clean Code, SOLID) bằng sơ đồ vẽ tay nhanh.
* **Tối DSA:** Giải duy trì 1-2 bài toán (LC 70).

#### Thứ 5 (20/08) - Ngày 7
* **Sáng Deep Topic:** Tổng duyệt lại tất cả các cheat-sheet đã soạn thảo cho từng công ty mục tiêu.
  * *Ví dụ cụ thể:* Đọc lại file chứa các lỗi thường gặp nhất bản thân ghi nhận trong suốt 7 tuần qua.
* **Sáng Prep:** Phỏng vấn thử (Mock Interview) dưới áp lực cao với AI/bạn học.
* **Tối DSA:** Giải duy trì 1-2 bài toán (LC 15).
</details>

---

## 📋 Hướng dẫn thực thi hàng buổi (Session Execution Guide)

Mỗi khi bắt đầu một block học, bạn cần tuân thủ nghiêm ngặt quy trình sau để tối ưu hiệu quả:

1. **Chuẩn bị (5 phút trước buổi):** Tắt toàn bộ thông báo điện thoại, đóng các tab trình duyệt không liên quan. Mở đúng folder tài liệu cần học (ví dụ: `04-Database/`).
2. **Active learning:** Khi đọc lý thuyết, không đọc thụ động. Đọc 10 phút, nhắm mắt lại tự tóm tắt ý chính trong đầu (Active Recall). Sau đó mở DB/Code lên gõ trực tiếp để kiểm chứng (Hands-on).
3. **Note-taking nhanh:** Viết note ngắn gọn trực tiếp vào các file tương ứng trong workspace, không viết lan man. Mỗi buổi học viết tối đa 5-10 dòng summary và ghi nhận đúng 1 bài học rút ra.
4. **Anki sync:** Chuyển các câu hỏi ôn tập quan trọng thành dạng thẻ nhớ (Flashcard) trên Anki vào cuối ngày để thực hiện ôn tập ngắt quãng (Spaced Repetition).