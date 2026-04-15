# PHẦN 12: BỘ CÂU HỎI MOCK INTERVIEW (TỪ JUNIOR ĐẾN SENIOR)

Chào mừng bạn đến với vòng phỏng vấn giả lập. Dưới đây là bộ câu hỏi "sát sườn" mà các công ty game lớn (VNG, Garena, Amanotes, OneSoft, ...) hay dùng. 

> [!TIP]
> Đừng học thuộc lòng. Hãy tập trung giải thích **"Tại sao"** và **"Như thế nào"**.

---

## 🟢 CẤP ĐỘ JUNIOR - KIẾN THỨC NỀN TẢNG

1.  **Q**: Java 8 có những tính năng gì nổi bật? Tại sao `Optional` lại quan trọng?
2.  **Q**: Phân biệt `ArrayList` và `LinkedList`? Trong game server, danh sách Player Online nên dùng cái nào?
3.  **Q**: Tại sao không nên dùng `double` để lưu số tiền/vàng trong game? Nên dùng gì thay thế?
4.  **Q**: `static` keyword dùng để làm gì? Có nguy hiểm gì khi dùng `static` cho dữ liệu của Player không?
5.  **Q**: Garbage Collection (GC) là gì? Tại sao thỉnh thoảng game lại bị "khựng" (lag) 1-2 giây?
6.  **Q**: Redis lưu dữ liệu ở đâu? Tốc độ của nó so với MySQL thế nào?
7.  **Q**: HTTP GET và POST khác nhau thế nào? Login nên dùng cái nào?
8.  **Q**: Làm sao để so sánh 2 chuỗi trong Java mà không bị lỗi logic?
9.  **Q**: Git `merge` và `rebase` khác nhau gì?
10. **Q**: Một bản tin UDP có đảm bảo đến đích không? Nếu không, tại sao người ta vẫn dùng nó cho game bắn súng?

---

## 🟡 CẤP ĐỘ MID - KỸ NĂNG XỬ LÝ & TỐI ƯU

11. **Q**: Giải thích cơ chế `Executors.newFixedThreadPool()`. Điều gì xảy ra nếu hàng đợi (Queue) bị đầy?
12. **Q**: Phân biệt `synchronized` và `ReentrantLock`. Khi nào cần dùng `tryLock()`?
13. **Q**: Tại sao game backend cực kỳ ưa chuộng **Protobuf** thay vì JSON?
14. **Q**: `Deadlock` là gì? Hãy mô tả kịch bản deadlock khi 2 người cùng chuyển tiền cho nhau.
15. **Q**: Redis `Sorted Set` hoạt động thế nào? Độ phức tạp khi lấy Top 10 là bao nhiêu?
16. **Q**: Kafka `Consumer Group` giúp hệ thống scale ngang như thế nào?
17. **Q**: Phân biệt **Stateless** và **Stateful** service. Battle Server thuộc loại nào?
18. **Q**: Làm sao để chống hỏa hoạn (OOM) khi dùng Netty xử lý hàng vạn kết nối?
19. **Q**: Optimistic Lock và Pessimistic Lock khác nhau thế nào? Khi nào dùng cái nào trong DB?
20. **Q**: Tại sao không nên dùng `new Thread()` trong Game Loop?

---

## 🔴 CẤP ĐỘ SENIOR - TƯ DUY KIẾN TRÚC & HỆ THỐNG

21. **Q**: Thiết kế hệ thống Matchmaking cho 100,000 CCU. Làm sao để đảm bảo công bằng (MMR) và tốc độ?
22. **Q**: Bạn sẽ xử lý lỗi "Thundering Herd" thế nào khi server bảo trì xong và 1 triệu người cùng đăng nhập?
23. **Q**: Làm thế nào để giảm thiểu **GC Pause** xuống dưới 10ms cho một server logic nặng? (Tuning ZGC/Shenandoah).
24. **Q**: Bạn sẽ scale một Game Server **Stateful** thế nào trên Kubernetes? (Giải thích về Agones).
25. **Q**: Thiết kế hệ thống Chat Global có tính năng lọc từ thô tục realtime cho hàng triệu người.
26. **Q**: Làm thế nào để đảm bảo tính nhất quán (Consistency) giữa Redis và Database trong hệ thống phân tán?
27. **Q**: Cách xử lý bài toán **Hot Key** trong Redis (ví dụ 1 triệu người cùng soi hồ sơ của Top 1 server)?
28. **Q**: Tại sao dùng **gRPC** lại hiệu quả hơn REST cho giao tiếp nội bộ giữa các Microservices?
29. **Q**: Trình bày chiến lược **Blue-Green Deployment** cho hệ thống game đang chạy mà không làm ngắt quãng trận đấu.
30. **Q**: Bạn sẽ thiết kế hệ thống **Anti-Cheat** ở tầng Backend thế nào (Client-side Prediction & Server Validation)?

---

## 💡 LỜI KHUYÊN CUỐI CÙNG:
Senior không phải là người biết mọi câu trả lời, mà là người có thể phân tích các mặt **Trade-off** (Đánh đổi). 
- Ví dụ: "Nếu dùng giải pháp A thì nhanh nhưng tốn RAM, dùng giải pháp B thì an toàn nhưng trễ cao. Trong trường hợp game của mình, tôi chọn A vì...".

**CHÚC BẠN THÀNH CÔNG TRÊN CON ĐƯỜNG CHINH PHỤC CÁC TỰA GAME ĐỈNH CAO!**
