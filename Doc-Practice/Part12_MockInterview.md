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

### Mẹo trả lời (Code Insight):
Khi được hỏi về `ArrayList` vs `LinkedList`, hãy trả lời theo kiểu performance:
"Trong game server, danh sách Player Online nên dùng `ArrayList` (truy cập O(1)) hoặc `ConcurrentHashMap` vì chúng ta cần tốc độ đọc nhanh. `LinkedList` tốn nhiều RAM hơn (do con trỏ) và truy cập chậm (O(N))."

---

## 🟡 CẤP ĐỘ MID - KỸ NĂNG XỬ LÝ & TỐI ƯU

11. **Q**: Giải thích cơ chế `Executors.newFixedThreadPool()`. Điều gì xảy ra nếu hàng đợi (Queue) bị đầy?
12. **Q**: Phân biệt `synchronized` và `ReentrantLock`. Khi nào cần dùng `tryLock()`?
13. **Q**: Tại sao game backend cực kỳ ưa chuộng **Protobuf** thay vì JSON?
14. **Q**: `Deadlock` là gì? Hãy mô tả kịch bản deadlock khi 2 người cùng chuyển tiền cho nhau.

### Mẹo trả lời (Code Insight):
Hỏi về `Deadlock`, hãy đưa ra ví dụ code cụ thể:
"Deadlock xảy ra khi A giữ Lock X và đợi Lock Y, còn B giữ Lock Y và đợi Lock X. Để giải quyết, chúng ta có thể quy định **Thứ tự chiếm Lock** (ví dụ luôn lock ID nhỏ trước) hoặc dùng `tryLock()` với timeout."

---

## 🔴 CẤP ĐỘ SENIOR - TƯ DUY KIẾN TRÚC & HỆ THỐNG

21. **Q**: Thiết kế hệ thống Matchmaking cho 100,000 CCU. Làm sao để đảm bảo công bằng (MMR) và tốc độ?
22. **Q**: Bạn sẽ xử lý lỗi "Thundering Herd" thế nào khi server bảo trì xong và 1 triệu người cùng đăng nhập?
23. **Q**: Làm thế nào để giảm thiểu **GC Pause** xuống dưới 10ms cho một server logic nặng? (Tuning ZGC/Shenandoah).

### Mẹo trả lời (Architect Insight):
Khi thiết kế hệ thống, hãy dùng từ khóa **"Trade-off"**:
"Để đạt 1 triệu CCU, tôi sẽ dùng **Microservices** để phân rã layer Battle (Stateful) và Layer Lobby (Stateless). Layer Battle sẽ chạy trên các k8s Pods có hỗ trợ **Agones** để tránh bị kill khi đang có trận đấu. Đánh đổi lại là hệ thống monitor sẽ phức tạp hơn."

---

## 💡 LỜI KHUYÊN CUỐI CÙNG:
Senior không phải là người biết mọi câu trả lời, mà là người có thể phân tích các mặt **Trade-off** (Đánh đổi). 

**CHÚC BẠN THÀNH CÔNG TRÊN CON ĐƯỜNG CHINH PHỤC CÁC TỰA GAME ĐỈNH CAO!**
