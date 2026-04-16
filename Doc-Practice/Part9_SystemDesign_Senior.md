# PHẦN 9: SYSTEM DESIGN (LEVEL SENIOR - THIẾT KẾ HỆ THỐNG TRIỆU NGƯỜI DÙNG)

Ở trình độ Senior, bạn không chỉ code một tính năng. Bạn phải thiết kế toàn bộ hệ sinh thái đảm bảo: **High Availability (Sẵn sàng cao)**, **Scalability (Khả năng mở rộng)** và **Fault Tolerance (Chịu lỗi)**.

---

## 1. High Availability (HA) & Fault Tolerance

### Nó là gì?
- **High Availability**: Đảm bảo hệ thống luôn hoạt động (ví dụ 99.99% thời gian), ngay cả khi một vài server bị cháy.
- **Fault Tolerance**: Khả năng hệ thống tiếp tục vận hành chính xác ngay cả khi có thành phần bị hỏng.

### Ví dụ code Thực chiến (Availability Check):
```java
// Logic của Gateway: Kiểm tra sức khỏe các Microservices (Health Check)
public void routeRequest(Request req) {
    ServiceInstance instance = loadBalancer.chooseAliveInstance("lobby-service");
    if (instance == null) {
        throw new ServiceUnavailableException("Hệ thống quá tải, vui lòng thử lại!");
    }
    // Gửi request tiếp
}
```

---

## 2. Scalability: Mở rộng quy mô triệu CCU

### Nó là gì?
Khả năng hệ thống xử lý lượng công việc tăng lên bằng cách thêm tài nguyên.

---

## 3. Hệ thống Chat & Notification: Bài toán Broadcast

### Nó là gì?
Gửi một thông tin từ 1 người tới hàng triệu người khác cùng lúc.

### Cách thức hoạt động: Pub/Sub & Fan-out
Nếu bạn dùng vòng lặp `for` để gửi tin nhắn cho 1 triệu người trên 1 server, server đó sẽ treo ngay lập tức.
**Giải pháp**: 
1. Tin nhắn được đẩy vào một Message Broker (Redis/Kafka).
2. Các "Chat Worker" (hàng chục server) lắng nghe và chỉ gửi cho những người đang kết nối trực tiếp với mình.

### Ví dụ code Thực chiến (Pub/Sub Broadcast):
```java
// Chat Worker: Nhận tin nhắn từ Redis và gửi cho người chơi Online trên server này
public void onChatMessageReceived(Message msg) {
    for (Session session : localSessions) {
        if (session.isInterestedIn(msg.getChannel())) {
            session.send(msg);
        }
    }
}
```

---

## 4. Rate Limiting & Backpressure

### Ví dụ code Thực chiến (Rate Limiter):
```java
// Dùng thư viện Bucket4j để giới hạn 5 request/giây cho mỗi Player
Bucket bucket = Bucket4j.builder()
    .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(1))))
    .build();

if (bucket.tryConsume(1)) {
    // Cho phép xử lý tiếp
} else {
    // Trả về lỗi 429: Too Many Requests
}
```

---

## 5. Availability & Scalability Basics (Nền tảng đo lường)

### Đo lường tính sẵn sàng (Availability):
- **Cơ chế**: Tính theo số lượng con số 9.
    - 99% (2 số 9): Cho phép sập 3.6 ngày/năm. (Kém)
    - 99.9% (3 số 9): Cho phép sập 8.7 giờ/năm. (Khá)
    - 99.99% (4 số 9): Cho phép sập 52 phút/năm. (Tiêu chuẩn Senior)

### Các bước Scale một hệ thống (CƠ BẢN):
1. **Optimize Code**: Tối ưu thuật toán, giảm thiểu object rác.
2. **Database Indexing**: Đánh index chuẩn để tránh treo DB.
3. **Caching**: Đưa dữ liệu hay dùng lên Redis.
4. **Vertical Scale**: Nâng cấp máy chủ.
5. **Horizontal Scale**: Thêm máy chủ, sharding database.

---

## CÂU HỎI PHỎNG VẤN (Tư duy kiến trúc)

### 1. Phân biệt "Strong Consistency" và "Eventual Consistency"? Trong Game chọn cái nào?
- **Answer**:
    - **Strong**: Dữ liệu phải giống hệt nhau ở mọi nơi ngay lập tức (Chậm, dùng cho Nạp tiền).
    - **Eventual**: Dữ liệu sẽ giống nhau sau một khoảng thời gian ngắn (Nhanh, dùng cho Bảng xếp hạng, Chat).
    - **Game**: Phần lớn dùng Eventual để ưu tiên tốc độ trải nghiệm.

### 2. Định lý CAP là gì? Tại sao không thể có cả 3?
- **Answer**: CAP gồm Consistency (Nhất quán), Availability (Sẵn sàng), Partition Tolerance (Chịu lỗi phân đoạn). Trong một hệ thống phân tán qua mạng (mạng có thể đứt - luôn có P), bạn buộc phải chọn giữa C hoặc A. 

### 3. Bạn sẽ làm gì khi hệ thống gặp tình trạng "Cascading Failure" (Lỗi dây chuyền)?
- **Answer**: Dùng **Circuit Breaker** (Cầu chì). Nếu Service A thấy Service B đang phản hồi chậm, nó sẽ chủ động ngắt kết nối và trả về lỗi nhanh (Fail-fast) thay vì tiếp tục đợi và làm treo chính mình.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống **World Boss** (Cả server cùng đánh 1 con quái).
Yêu cầu:
- Boss có 1 tỷ HP. Mỗi giây có 100,000 player cùng chém.
- Làm sao để hiển thị HP của Boss realtime cho tất cả mọi người mà không làm sập Database?
- Hãy mô tả kiến trúc dùng Redis, Kafka và WebSocket.
