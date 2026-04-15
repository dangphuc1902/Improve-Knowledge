# PHẦN 7: MONGODB & DATABASE DESIGN TRONG GAME

Cơ sở dữ liệu (Database) là nơi lưu giữ tất cả nỗ lực của người chơi. Trong game backend, chúng ta thường đứng trước lựa chọn: SQL (MySQL/PostgreSQL) hay NoSQL (MongoDB).

---

## 1. SQL vs NoSQL: Khi nào dùng MongoDB?

### SQL (Structured Query Language)
- **Đặc điểm**: Schema cứng nhắc, quan hệ chặt chẽ (Joins), ACID tuyệt đối.
- **Game context**: Dùng cho hệ thống tài chính, nạp thẻ, billing, lịch sử giao dịch. Những thứ sai 1 li đi một dặm.

### MongoDB (NoSQL - Document)
- **Đặc điểm**: Schema linh hoạt (Json-like), không Joins (thường nhúng dữ liệu), scale ngang dễ dàng.
- **Game context**: Cực kỳ phù hợp cho **Player Data** và **Inventory**.
    - Tại sao? Vì item trong game có thể thay đổi thuộc tính liên tục sau mỗi bản update. Thêm một thuộc tính "Kháng độc" cho kiếm không cần phải `ALTER TABLE` hàng triệu bản ghi.

---

## 2. Thiết kế Schema cho Game (Mindset Senior)

### Schema Nhúng (Embedded) vs Tham chiếu (Reference)

Trong Game, chúng ta ưu tiên **Embedded** để performance nhanh nhất.

**Ví dụ: Player Profile**
```json
{
  "_id": "player_123",
  "name": "Antigravity",
  "level": 50,
  "stats": { "hp": 1000, "mp": 500, "atk": 150 }, // Nhúng stats để lấy 1 lần là đủ
  "inventory": [
    { "itemId": "sword_01", "level": 10, "durability": 80 },
    { "itemId": "potion_05", "count": 20 }
  ]
}
```
- **Ưu điểm**: Chỉ cần 1 query là lấy được toàn bộ thông tin player để đưa vào game server memory. Không cần Join bảng trang bị, bảng chỉ số.

---

## 3. Khi nào Schema Nhúng (Embedded) là sai lầm?

Nếu danh sách item (Inventory) của player có thể lên đến hàng chục nghìn cái, việc nhúng vào 1 Document sẽ vượt giới hạn **16MB** của MongoDB.
- **Giải pháp**: Tách Inventory ra bảng riêng và dùng `playerId` làm index.

---

## 4. Best Practice khi dùng MongoDB trong Game

1.  **Index là sống còn**: Luôn đánh index cho các trường hay query như `name`, `email`, `level`.
2.  **Sử dụng Aggregation Framework**: Để làm các báo cáo phức tạp (Ví dụ: Trung bình level của player nạp tiền).
3.  **Atomic Updates**: Dùng `$inc`, `$set`, `$push` để cập nhật dữ liệu mà không cần đọc-rồi-ghi (tránh Race Condition).
    - Ví dụ cộng vàng: `db.players.updateOne({_id: 123}, {$inc: {gold: 100}})`

---

## 5. Lỗi thường gặp (Common Mistakes)

- **Lạm dụng Joins (Lookup)**: MongoDB không sinh ra để Join. Nếu bạn dùng `$lookup` quá nhiều, hãy xem lại thiết kế. Có thể bạn nên dùng SQL.
- **Cấu trúc Document quá sâu**: Khiến việc update các mảng lồng nhau trở nên cực kỳ phức tạp.

---

## CÂU HỎI PHỎNG VẤN

### Mid
- **Q**: MongoDB có hỗ trợ Transaction (Giao dịch) không?
- **A**: Có, từ bản 4.0 MongoDB đã hỗ trợ Multi-document Transaction giống SQL. Tuy nhiên nên hạn chế vì nó làm giảm hiệu năng. Ưu tiên thiết kế Document để chỉ cần update 1 chỗ.

- **Q**: Tại sao người ta lại nói MongoDB dễ scale ngang hơn MySQL?
- **A**: Do cơ chế **Sharding** có sẵn. MongoDB tự động chia nhỏ data sang nhiều máy chủ dựa trên 1 Key (Shard key) mà không làm gián đoạn ứng dụng.

### Senior
- **Q**: Bạn chọn Shard Key cho bảng Player như thế nào để đảm bảo dữ liệu trải đều trên cluster?
- **A**: 
    - Đừng chọn những trường có giá trị tăng dần (như Timestamp). Dữ liệu mới sẽ dồn hết vào 1 Shard cuối.
    - Nên chọn trường có độ phân tán cao và ổn định (Ví dụ: `HashedId` hoặc UUID).

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế Schema MongoDB cho một game Farm (Nông trại).
- Player có nhiều khu đất (Plots).
- Mỗi khu đất có thể trồng cây (Cây có thời gian chín, loại cây).
- Player có túi kho (Silo) chứa nông sản thu hoạch.
- Hãy viết cấu trúc JSON mẫu.
