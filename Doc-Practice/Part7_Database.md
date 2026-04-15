# PHẦN 7: MONGODB & DATABASE DESIGN TRONG GAME

Cơ sở dữ liệu (Database) là nơi lưu giữ tất cả nỗ lực của người chơi. Trong game backend, chúng ta thường đứng trước lựa chọn: SQL (MySQL/PostgreSQL) hay NoSQL (MongoDB).

---

## 1. SQL vs NoSQL: Khi nào dùng MongoDB?

### Nó là gì?
- **SQL (Relational)**: Dữ liệu cấu trúc chặt chẽ, các bảng liên kết qua khóa ngoại (FK).
- **NoSQL (Document)**: Dữ liệu lưu dưới dạng JSON (BSON), linh hoạt, không cần khai báo schema trước.

### Dùng làm gì?
- **SQL**: Dùng cho billing, giao dịch tiền tệ, hệ thống nạp thẻ (Cần ACID và tính nhất quán tuyệt đối).
- **MongoDB**: Dùng cho Player Profile, Inventory, Quest Log, Mailbox.

### Dùng khi nào?
- Khi game của bạn có hàng ngàn vật phẩm với thuộc tính khác nhau. Việc thêm một thuộc tính mới không cần phải `ALTER TABLE` hàng triệu bản ghi (việc này có thể làm treo database hàng giờ).
- Khi cần mở rộng (Scale-out) dễ dàng bằng cách thêm server.

### Cách thức hoạt động: B-Tree & WiredTiger
MongoDB dùng công cụ lưu trữ **WiredTiger**. Nó tổ chức dữ liệu theo cấu trúc **B-Tree** giống SQL nhưng tối ưu cho việc ghi (Write-heavy) – rất quan trọng trong game khi player update trạng thái liên tục.

---

## 4. SQL Fundamentals: ACID & Joins

### ACID (CƠ BẢN):
Mọi Database SQL phải đảm bảo 4 tính chất:
1. **Atomicity**: Được ăn cả, ngã về không (Transaction).
2. **Consistency**: Dữ liệu luôn đúng quy tắc (Schema).
3. **Isolation**: Các transaction chạy song song không làm phiền nhau.
4. **Durability**: Đã ghi là không mất (lưu xuống đĩa).

### SQL Joins:
- **Inner Join**: Lấy phần giao giữa 2 bảng.
- **Left Join**: Lấy hết bảng trái, bảng phải không có thì để NULL.
- **Senior Insight**: Trong Game quy mô lớn, ta hạn chế Join quá nhiều bảng (trên 3-4 bảng) vì nó làm hiệu năng tụt dốc. Đôi khi ta chấp nhận lưu thừa dữ liệu (Phi bình thường hóa) để tránh Join.

---


### Nó là gì?
- **Embedding (Nhúng)**: Lưu tất cả stats, inventory vào chung một Document player.
- **Referencing (Tham chiếu)**: Lưu inventory ở một bảng riêng, liên kết qua `playerId`.

### Dùng làm gì?
- **Embedding**: Giảm số lượng query. Đọc 1 lần lấy được toàn bộ data để nạp vào RAM. Đây là Best Practice cho Game Profile.
- **Referencing**: Dùng khi dữ liệu có khả năng phình to vô hạn (ví dụ: Danh sách bạn bè, Lịch sử đấu).

### Sai lầm & Best Practice
- **Sai lầm**: Nhúng quá nhiều dữ liệu vào một Document vượt quá **16MB**.
- **Best Practice**: **Denormalization** (Phi bình thường hóa). Đừng sợ lặp dữ liệu, hãy ưu tiên tốc độ đọc. Trong game, tốc độ đọc-ghi quan trọng hơn việc tiết kiệm vài byte dung lượng ổ cứng.

---

## 3. Indexing: Chìa khóa của hiệu năng

### Nó là gì?
Là một cấu trúc dữ liệu giúp database tìm kiếm bản ghi cực nhanh thay vì phải quét toàn bộ bảng (**Collection Scan**).

### Cơ chế hoạt động:
Khi bạn đánh Index cho field `username`, MongoDB tạo ra một cây chỉ mục riêng biệt. Khi tìm kiếm, nó chỉ việc duyệt cây này (O(log N)) thay vì duyệt từng hàng (O(N)).

### Sai lầm & Best Practice
- **Sai lầm**: Đánh index cho mọi trường. Mỗi index làm chậm tốc độ Ghi vì database phải cập nhật cả cây index khi dữ liệu thay đổi.
- **Best Practice**: Dùng **Compound Index** (Index kết hợp). Ví dụ: `{server_id: 1, level: -1}` để lấy top player của từng server nhanh nhất.

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Tại sao nói MongoDB hỗ trợ Transaction nhưng vẫn khuyên không nên lạm dụng?
- **Answer**: Transaction trong MongoDB tốn rất nhiều tài nguyên để duy trì tính Snapshot Isolation. Nếu dùng quá nhiều, nó sẽ gây nghẽn IO và tăng độ trễ. Thiết kế chuẩn NoSQL là làm sao để 1 request chỉ cần update 1 Document duy nhất.

### 2. Shard Key là gì? Tại sao chọn Shard Key sai là "thảm họa"?
- **Answer**: Shard Key quyết định dữ liệu được chia sang máy chủ nào. Nếu chọn field có giá trị tăng dần (như Timestamp), toàn bộ dữ liệu mới sẽ dồn vào 1 server duy nhất (**Hotspot**), làm mất ý nghĩa của việc chia tải.

### 3. Read Preference trong MongoDB Cluster dùng để làm gì?
- **Answer**: Cho phép bạn chọn đọc dữ liệu từ node nào. `primary` (luôn đọc từ node chính - nhất quán cao), `secondary` (đọc từ node phụ - tăng tốc độ đọc nhưng dữ liệu có thể hơi cũ).

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế Schema cho hệ thống **Mailbox** (Hòm thư) trong game.
Yêu cầu:
- Một player có thể nhận hàng nghìn thư.
- Thư có trạng thái: Đã đọc, Chưa đọc, Đã nhận quà đính kèm.
- Viết JSON mẫu và giải thích tại sao bạn chọn nhúng hay tách bang (Referencing).
