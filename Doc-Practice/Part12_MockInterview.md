# PHẦN 12: BỘ CÂU HỎI MOCK INTERVIEW (TỪ JUNIOR ĐẾN SENIOR)

Chào mừng bạn đến với vòng phỏng vấn giả lập. Dưới đây là 60 câu hỏi "sát sườn" mà các công ty game lớn (VNG, Garena, Amanotes, OneSoft, ...) hay dùng.

---

## 🟢 CẤP ĐỘ JUNIOR (20 CÂU) - KIẾN THỨC NỀN TẢNG

1.  **Q**: Java 8 có những tính năng gì nổi bật? (Lambda, Stream API, Optional).
2.  **Q**: Phân biệt Abstract Class và Interface? Khi nào dùng cái nào?
3.  **Q**: Tại sao không nên dùng `double` để lưu số tiền/vàng trong game? (Lỗi làm tròn, dùng `long` hoặc `BigDecimal`).
4.  **Q**: `ArrayList` và `LinkedList` khác nhau thế nào? Trong game server hay dùng cái nào? (ArrayList vì truy cập theo index cực nhanh).
5.  **Q**: Làm thế nào để bắt lỗi Exception mà không làm sập Server?
6.  **Q**: SQL injection là gì? Làm sao để phòng chống?
7.  **Q**: HTTP GET và POST khác nhau thế nào?
8.  **Q**: Một bản tin TCP gửi đi có đảm bảo 100% đến đích không?
9.  **Q**: Tại sao cần dùng `private` cho các biến trong class? (Encapsulation).
10. **Q**: `static` keyword dùng để làm gì? Có nên dùng quá nhiều static biến trong game server không? (Khó unit test, tốn memory vĩnh viễn).
11. **Q**: Redis là gì? Nó lưu dữ liệu ở đâu?
12. **Q**: Git `merge` và `rebase` khác nhau gì?
13. **Q**: Tại sao cần đánh `Index` trong DB?
14. **Q**: Phân biệt Overriding và Overloading?
15. **Q**: Maven/Gradle dùng để làm gì?
16. **Q**: Garbage Collection là gì?
17. **Q**: Làm sao để so sánh 2 String trong Java? (Dùng `.equals()`, không dùng `==`).
18. **Q**: JSON là gì? Tại sao nó phổ biến?
19. **Q**: Nêu một số Design Pattern bạn biết? (Singleton, Factory).
20. **Q**: Bạn làm gì khi code của bạn chạy ở máy bạn nhưng không chạy ở máy leader? (Kiểm tra môi trường, dùng Docker).

---

## 🟡 CẤP ĐỘ MID (20 CÂU) - KỸ NĂNG XỬ LÝ VẤN ĐỀ

21. **Q**: Phân biệt `synchronized` và `ReentrantLock`?
22. **Q**: Làm thế nào để tránh `ConcurrentModificationException`?
23. **Q**: Phân biệt `TCP` và `UDP` trong ngữ cảnh game Real-time?
24. **Q**: Tại sao dùng Protobuf thay vì JSON?
25. **Q**: `Deadlock` là gì? Cách phát hiện và phòng tránh?
26. **Q**: Tại sao game server thường chọn **Netty** để viết Network layer?
27. **Q**: Redis `Sorted Set` hoạt động thế nào? (Skip list).
28. **Q**: Sharding Database là gì?
29. **Q**: Giải thích cơ chế `Handshake 3-way` của TCP?
30. **Q**: Phân biệt `Stateful` và `Stateless` service?
31. **Q**: Kafka `Consumer Group` hoạt động thế nào?
32. **Q**: Làm sao để xử lý `Race Condition` khi 2 người cùng mua 1 món đồ giới hạn số lượng?
33. **Q**: JWT là gì? Tại sao nó an toàn hơn Session truyền thống?
34. **Q**: Phân biệt `G1GC` và `ZGC`?
35. **Q**: Làm thế nào để Unit Test một đoạn code có gọi DB? (Mocking).
36. **Q**: Optimistic Lock và Pessimistic Lock khác nhau thế nào?
37. **Q**: `CompletableFuture` dùng để làm gì?
38. **Q**: Làm sao để monitor sức khỏe của một Java application? (VisualVM, Prometheus).
39. **Q**: Tại sao không nên dùng `new Thread()` trong vòng lặp?
40. **Q**: Docker Image và Docker Container khác nhau gì?

---

## 🔴 CẤP ĐỘ SENIOR (20 CÂU) - TƯ DUY KIẾN TRÚC & TỐI ƯU

41. **Q**: Thiết kế hệ thống Matchmaking cho 100,000 CCU?
42. **Q**: Làm thế nào để giảm thiểu **GC Pause** xuống dưới 10ms cho một server logic nặng?
43. **Q**: Bạn sẽ xử lý thế nào khi một Database Node bị chết trong cluster?
44. **Q**: Giải quyết bài toán "Thứ tự message" trong hệ thống phân tán dùng Kafka?
45. **Q**: Thiết kế hệ thống Chat Global có tính năng lọc từ thô tục realtime?
46. **Q**: Làm thế nào để chống Hack/Cheat đổi thông số Memory ở Client? (Mọi logic quan trọng phải nằm ở Server).
47. **Q**: Bạn sẽ scale một Game Server Stateful thế nào trên Kubernetes? (Dùng Agones).
48. **Q**: Phân tích ưu/nhược điểm của Microservices so với Monolith cho một dự án Game 5 năm?
49. **Q**: Làm thế nào để thực hiện **Blue-Green Deployment** cho Game Server?
50. **Q**: Thiết kế hệ thống nạp tiền (IAP) đảm bảo không bao giờ bị nhân đôi item hoặc mất giao dịch? (Idempotency + Transaction).
51. **Q**: Giải thích cơ chế **Event Loop** của Netty?
52. **Q**: Khi nào nên dùng **Off-heap memory** trong Java? (DirectBuffer).
53. **Q**: Bạn sẽ thiết kế giải thuật Leaderboard thế nào nếu BXH có tới 100 triệu record?
54. **Q**: Làm thế nào để cân bằng giữa **Consistency** và **Availability** (CAP Theorem) trong Game?
55. **Q**: Cách xử lý bài toán **Hot Key** trong Redis?
56. **Q**: Bạn sẽ tối ưu băng thông (Bandwidth) thế nào khi server gửi tọa độ của 1000 quái vật cho 100 người chơi mỗi 50ms? (Area of Interest - AOI, Delta Compression).
57. **Q**: Cơ chế `Reconnection` (Kết nối lại) nên được thiết kế thế nào để player không bị mất trận đấu?
58. **Q**: Lợi ích của việc dùng **gRPC** so với REST trong giao tiếp giữa các internal microservices?
59. **Q**: Bạn sẽ thực hiện code review thế nào cho một Junior vừa commit code có nguy cơ gây Memory Leak?
60. **Q**: Tầm nhìn của bạn về tương lai của Game Backend (Serverless, Cloud-native, AI)?

---

## 💡 LỜI KHUYÊN CUỐI CÙNG:
Đừng học thuộc lòng câu trả lời. Hãy hiểu bản chất. Khi phỏng vấn Senior, người ta muốn nghe **Trải nghiệm thực tế** (Tôi đã từng gặp bug X, tôi đã sửa nó bằng cách Y và kết quả là Z). 

**CHÚC BẠN THÀNH CÔNG TRÊN CON ĐƯỜNG CHINH PHỤC CÁC TỰA GAME ĐỈNH CAO!**
