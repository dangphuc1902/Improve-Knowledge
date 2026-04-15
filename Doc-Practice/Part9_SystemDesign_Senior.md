# PHẦN 9: SYSTEM DESIGN (LEVEL SENIOR - THIẾT KẾ HỆ THỐNG TRIỆU NGƯỜI DÙNG)

Ở trình độ Senior, bạn không chỉ code một tính năng. Bạn phải thiết kế toàn bộ hệ sinh thái đảm bảo: **High Availability (Sẵn sàng cao)**, **Scalability (Khả năng mở rộng)** và **Fault Tolerance (Chịu lỗi)**.

---

## 1. High Availability (HA) & Fault Tolerance

### Nó là gì?
- **High Availability**: Đảm bảo hệ thống luôn hoạt động (ví dụ 99.99% thời gian), ngay cả khi một vài server bị cháy.
- **Fault Tolerance**: Khả năng hệ thống tiếp tục vận hành chính xác ngay cả khi có thành phần bị hỏng.

### Dùng làm gì?
Giúp người chơi không bị văng game hay mất dữ liệu khi data center gặp sự cố.

### Cách thức hoạt động: Redundancy (Dư thừa)
Không bao giờ để "Single Point of Failure" (Điểm chết duy nhất). Nếu bạn có 1 Database, hãy có thêm 1 bản Sao (Slave/Replica). Nếu bạn có 1 Gateway, hãy chạy nó trên ít nhất 2 máy chủ khác nhau sau một **Load Balancer**.

### Sai lầm & Best Practice
- **Sai lầm**: Coi nhẹ việc Backup. Khi thảm họa xảy ra mới phát hiện file backup bị lỗi từ 1 năm trước.
- **Best Practice**: Triển khai **Health Check** và tự động chuyển hướng (Failover) traffic sang server dự phòng trong vòng vài giây.

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


### Nó là gì?
Khả năng hệ thống xử lý lượng công việc tăng lên bằng cách thêm tài nguyên.

### Dùng khi nào?
- **Scale Up (Dọc)**: Nâng cấp RAM/CPU cho 1 máy chủ. Dùng khi game mới ra mắt, lượng user ít.
- **Scale Out (Ngang)**: Thêm nhiều máy chủ chạy song song. Đây là cách duy nhất để đạt mức triệu CCU.

### Cơ chế hoạt động: Stateless & Sharding
- Với logic nghiệp vụ (Auth, Lobby): Làm cho chúng **Stateless** để có thể bật thêm hàng nghìn instance mà không sợ xung đột.
- Với dữ liệu (Database): Dùng **Sharding** (Chia nhỏ dữ liệu). Ví dụ: Player từ ID 1-1 triệu nằm ở DB1, 1 triệu-2 triệu nằm ở DB2.

### Sai lầm & Best Practice
- **Sai lầm**: Scale Up quá đà. Server càng to giá càng đắt theo cấp số nhân.
- **Best Practice**: Thiết kế theo hướng **Cloud-native** và Microservices ngay từ đầu để dễ dàng Scale Out.

---

## 3. Hệ thống Chat & Notification: Bài toán Broadcast

### Nó là gì?
Gửi một thông tin từ 1 người tới hàng triệu người khác cùng lúc.

### Cách thức hoạt động: Pub/Sub & Fan-out
Nếu bạn dùng vòng lặp `for` để gửi tin nhắn cho 1 triệu người trên 1 server, server đó sẽ treo ngay lập tức.
**Giải pháp**: 
1. Tin nhắn được đẩy vào một Message Broker (Redis/Kafka).
2. Các "Chat Worker" (hàng chục server) lắng nghe và chỉ gửi cho những người đang kết nối trực tiếp với mình.
=> Tải được chia đều cho nhiều máy.

---

## 4. Rate Limiting & Backpressure

### Nó là gì?
- **Rate Limiting**: Giới hạn số lượng yêu cầu từ 1 người chơi (ví dụ: tối đa 5 lần click mua đồ/giây).
- **Backpressure**: Cơ chế báo cho phía gửi (Client) biết là Server đang quá tải, đừng gửi thêm nữa.

### Dùng làm gì?
Chống tấn công DOS và bảo vệ Database không bị "ngập lụt" yêu cầu trong các sự kiện Flash Sale.

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
