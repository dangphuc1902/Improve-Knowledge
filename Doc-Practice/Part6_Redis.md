# PHẦN 6: REDIS (CACHE & REAL-TIME DATA)

Trong Game Server, Redis không chỉ là cache. Nó là một cơ sở dữ liệu tốc độ cao (In-memory) đóng vai trò sống còn trong các tính năng real-time như Leaderboard, Matchmaking, và Session Management.

---

## 1. Redis vs Database: Chiến thuật In-memory

### Nó là gì?
Redis (Remote Dictionary Server) là hệ thống lưu trữ dữ liệu dạng Key-Value chạy hoàn toàn trên RAM.

### Dùng làm gì?
- **Speed up**: Giảm tải cho Database (MySQL/Mongo) bằng cách lưu các kết quả truy vấn vào RAM.
- **Real-time**: Lưu trữ các dữ liệu thay đổi cực nhanh (máu quái, vị trí người chơi trong phòng).

### Dùng khi nào?
- Khi bạn cần tốc độ phản hồi tính bằng micro-giây.
- Khi dữ liệu có thuộc tính "tạm thời" (TTL).

### Cơ chế (How it works): Single-threaded Event Loop
Khác với nhiều DB, Redis chạy **đơn luồng (Single-threaded)**. Nó dùng cơ chế **IO Multiplexing** để xử lý hàng vạn kết nối. Vì chạy đơn luồng, Redis không tốn thời gian cho việc tranh chấp khóa (Locking) hay đổi ngữ cảnh (Context switching), giúp nó đạt tốc độ cực cao (>100k request/giây).

---

## 2. Các cấu trúc dữ liệu "Sát thủ" trong Game

### Hash (Object Storage)
- **Nó là gì**: Lưu một map bên trong một key.
- **Dùng làm gì**: Lưu thông tin player (`hp`, `mp`, `gold`).
- **Ưu điểm**: Bạn có thể update chỉ riêng `hp` mà không cần ghi đè cả object player khổng lồ.

### Sorted Set - ZSet (Thứ bậc/BXH)
- **Nó là gì**: Một tập hợp các chuỗi kềm theo một số điểm (Score).
- **Dùng làm gì**: **Leaderboard**.
- **Cơ chế**: Redis dùng mảng kết hợp với **Skip List** để đảm bảo việc chèn và sắp xếp score luôn đạt độ phức tạp O(log N).

### Geo (Định vị)
- **Nó là gì**: Lưu kinh độ, vĩ độ.
- **Dùng làm gì**: Tìm kiếm "Người chơi ở gần đây" hoặc "Bang hội xung quanh".

---

## 5. Threading Model: Tại sao Single-thread vẫn nhanh?

### Cơ chế hoạt động:
1. **Lõi (Core)**: Redis thực hiện các lệnh (SET, GET, ...) bằng **duy nhất 1 thread**. Điều này giúp loại bỏ hoàn toàn việc tranh chấp khóa (Locking) và Race Condition.
2. **I/O (Mạng)**: Từ bản Redis 6.0+, Redis bắt đầu dùng **Multi-threads** để xử lý việc đọc/ghi dữ liệu từ Socket mạng. 

### IO Multiplexing (CƠ BẢN):
Redis dùng cơ chế "Thông báo sự kiện" (như `epoll` trên Linux). Thay vì ngồi đợi từng kết nối mạng, nó để hệ điều hành làm việc đó. Khi có dữ liệu thực sự đến, Redis mới nhảy vào xử lý.
- **Senior Insight**: Vì lõi vẫn là đơn luồng, bạn tuyệt đối không được chạy các lệnh nặng như `KEYS *` vì nó sẽ làm toàn bộ hệ thống "đứng hình".

---


### Nó là gì?
Cơ chế giúp Redis lưu dữ liệu từ RAM xuống Disk để không bị mất khi mất điện.

### Cách thức hoạt động:
1. **RDB (Snapshot)**: Chụp ảnh toàn bộ RAM và ghi xuống file định kỳ (ví dụ 15 phút một lần).
   - **Ưu**: Tốc độ khởi động lại cực nhanh.
   - **Nhược**: Dễ mất dữ liệu trong khoảng thời gian giữa 2 lần snapshot.
2. **AOF (Append Only File)**: Mỗi khi bạn thực hiện một lệnh (SET, DEL...), Redis ghi lệnh đó vào một file log.
   - **Ưu**: An toàn tuyệt đối, gần như không mất data.
   - **Nhược**: File log phình to rất nhanh, khởi động lại chậm vì phải chạy lại hàng triệu lệnh.

---

## 4. Sai lầm & Best Practice

- **Sai lầm: Big Keys**: Lưu một List/Hash có hàng triệu phần tử vào 1 key duy nhất. Khi bạn gọi lệnh lấy dữ liệu, Redis (đơn luồng) sẽ bị treo cứng cho đến khi xử lý xong key đó (**Blocking**).
- **Best Practice: Cache Aside Pattern**:
  1. Đọc Redis. Nếu có -> Trả về ngay.
  2. Nếu không có -> Đọc DB -> Ghi vào Redis -> Trả về.
- **Best Practice: Write-Behind**:
  Game Server update máu player vào Redis liên tục. Mỗi 5 phút, một thread ngầm mới lấy dữ liệu từ Redis đồng bộ vào Database một lần để giảm tải cho Disk IO.

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. "Redis is single-threaded", vậy tại sao nó vẫn có thể bị nghẽn (Blocking)?
- **Answer**: Do các lệnh có độ phức tạp cao như `KEYS *`, `SMEMBERS`, hoặc các lệnh trên Big Key. Vì đơn luồng, nếu một lệnh tốn 1 giây để chạy, toàn bộ hàng nghìn request phía sau phải đợi 1 giây đó.

### 2. Xử lý bài toán Cache Penetration (Yêu cầu vào những key không tồn tại)?
- **Answer**: Dùng **Bloom Filter** để kiểm tra nhanh sự tồn tại của key trước khi hỏi Redis/DB, hoặc đơn giản là cache luôn cả kết quả "NULL" với TTL ngắn.

### 3. Redis Cluster hoạt động như thế nào? (Sharding)
- **Answer**: Redis chia dữ liệu vào **16384 Hash Slots**. Mỗi node trong Cluster sẽ quản lý một dải slot. Khi Client gửi key, nó sẽ băm key (CRC16) để biết key đó thuộc slot nào và gửi thẳng tới node tương ứng.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống **Energy/Stamina** (Thể lực) cho game mobile.
- Player có tối đa 10 Energy.
- Cứ 5 phút tự hồi 1 Energy.
Yêu cầu: Sử dụng Redis để lưu và tính toán stamina sao cho server không cần dùng Timer chạy ngầm hàng vạn player.
- **Gợi ý**: Lưu `lastUpdateTimestamp` vào Redis và tính toán stamina dựa trên hiệu số thời gian khi player truy cập.
