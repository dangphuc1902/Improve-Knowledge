# PHẦN 9: SYSTEM DESIGN (LEVEL SENIOR - THIẾT KẾ HỆ THỐNG TRIỆU NGƯỜI DÙNG)

Ở level Senior, bạn không chỉ code 1 tính năng. Bạn phải thiết kế toàn bộ hệ sinh thái đảm bảo: **High Availability (Sẵn sàng cao)**, **Scalability (Khả năng mở rộng)** và **Fault Tolerance (Chịu lỗi)**.

---

## 1. Thiết kế Hệ thống cho 1 triệu Player Online (CCU)

### Thách thức (Bottlenecks)
1.  **DB quá tải**: Quá nhiều read/write vào 1 con DB duy nhất.
2.  **Network Congestion**: Băng thông server không chịu nổi.
3.  **Chat & Notification**: Gửi 1 tin nhắn cho 1 triệu người cùng lúc là thảm họa.

### Giải pháp Senior
- **Database Sharding**: Chia database theo khu vực hoặc ID.
- **Microservices Isolation**: Tách biệt logic Battle và logic Lobby.
- **CDN**: Sử dụng CDN để phân phối tài nguyên game (ảnh, âm thanh, bản cập nhật) thay vì tải trực tiếp từ server.

---

## 2. Hệ thống Chat Toàn Cầu (Global Chat)

### Vấn đề
Nếu bạn dùng 1 WebSocket server cho 1 triệu người. Khi ai đó chat, server phải loop qua 1 triệu connection để gửi đi -> Treo CPU.

### Thiết kế chuẩn Senior
1.  **Pub/Sub với Redis/Kafka**: Khi Player A chat, message đẩy vào Redis Pub/Sub.
2.  **Chat Workers**: Các server chat khác subscribe vào Redis. Khi thấy tin nhắn, nó chỉ gửi cho những Player đang kết nối vào CHÍNH NÓ.
3.  **Filtering Service**: Trước khi phát tán, tin nhắn đi qua một service AI để lọc từ ngữ thô tục (Bad words filtering).

---

## 3. Hệ thống Matchmaking Quy mô lớn

### Logic tìm trận:
Không đơn giản là tìm 10 người. Cần dựa trên:
- **MMR (Match Making Rating)**: Trình độ.
- **Latency (Ping)**: Phải cùng vùng.
- **Wait time**: Nếu chờ quá lâu, nới lỏng điều kiện (Vàng có thể đánh với Bạc).

### Kiến trúc:
- **Pool Manager**: Lưu danh sách người chờ vào Redis (ZSet theo MMR).
- **Matchmaker Workers**: Các tiến trình chạy độc lập quét Redis, nhặt ra các cặp đấu và gửi "Match Found" cho người chơi qua WebSocket.

---

## 4. Phân tích Bottleneck & Scaling

### Scale Dọc (Vertical)
Mua server to hơn (nhiều RAM, CPU). 
- **Giới hạn**: Luôn có trần vật lý và giá cực đắt.

### Scale Ngang (Horizontal)
Mua thêm nhiều server nhỏ.
- **Ưu điểm**: Vô hạn. Game server hiện đại bắt buộc phải theo hướng này.

---

## 5. Case Study: Flash Sale / Event In-game cực lớn
Hàng triệu người cùng click "Nhận quà" vào đúng 8h tối. 

**Giải quyết:**
1.  **Rate Limiting** ở tầng Gateway: Chặn ngay các request spam.
2.  **Queueing (Kafka)**: Đưa request nhậm quà vào hàng đợi xử lý dần.
3.  **Idempotent API**: Đảm bảo nếu user click 10 lần, server chỉ xử lý đúng 1 lần dựa trên `request_id`.

---

## CÂU HỎI PHỎNG VẤN (Tư duy kiến trúc)

- **Q**: Làm thế nào để đảm bảo dữ liệu giữa Cache (Redis) và DB luôn đồng nhất?
- **A**: Không bao giờ có sự đồng nhất 100% (Strong Consistency) ở quy mô lớn mà không đánh đổi tốc độ. Ta chấp nhận **Eventual Consistency** (Đồng nhất sau một khoảng thời gian). Luôn ghi vào DB trước, sau đó xóa/update Cache.

- **Q**: Bạn sẽ làm gì khi Server Lobby bị lỗi khiến toàn bộ player không thể vào game, nhưng những người đang trong trận (Battle) vẫn chơi bình thường?
- **A**: Đây là ví dụ của **Isolating Failure**. 
    1. Cơ chế **Health Check** tự động ngắt Lobby server lỗi ra khỏi Load Balancer.
    2. Thông báo cho người chơi đang đợi ở Client.
    3. Vì Battle server tách biệt hoàn toàn về Network và Logic, nó không bị ảnh hưởng.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống "Bảng xếp hạng mùa giải" (Season Leaderboard).
- Cuối mỗi tháng, hệ thống phải chốt hạ Top 100 người cao nhất để trao thưởng.
- Trong lúc trao thưởng, player vẫn phải được đánh trận và cập nhật điểm mùa giải mới.
- Hãy mô tả cách bạn dùng Redis và DB để thực hiện việc này mà không làm gián đoạn game.
