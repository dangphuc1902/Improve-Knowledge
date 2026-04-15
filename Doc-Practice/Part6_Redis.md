# PHẦN 6: REDIS (CACHE & REAL-TIME DATA)

Trong Game Server, Redis không chỉ là cache. Nó là một cơ sở dữ liệu tốc độ cao (In-memory) đóng vai trò sống còn trong các tính năng real-time.

---

## 1. Cache vs Database: Tại sao cần cả hai?

- **Database (MySQL/Mongo)**: Lưu trữ lâu dài, an toàn trên ổ đĩa. Tốc độ: ~ms (chậm).
- **Redis**: Lưu trữ trên RAM. Tốc độ: < 1ms (Cực nhanh). 
**Mindset Game Server**: Cái gì thay đổi liên tục (Máu quái, vị trí Player, Biến counter) -> Đẩy lên Redis. Cái gì cần persist (Tài sản, Level, Transaction) -> Đẩy vào DB.

---

## 2. Redis Data Structures trong Game

Redis không chỉ có Key-Value. Sức mạnh thực sự nằm ở các Data Structure:

1.  **String**: Cực tốt để lưu `Session Token` của player (TTL 1 tiếng).
2.  **Hash**: Lưu Object Player (Máu, Giáp, Tên). Truy xuất/Update từng field lẻ rất nhanh.
3.  **Sorted Set (ZSet)**: "Vũ khí tối thượng" để làm **Leaderboard**. Tự động sắp xếp theo Score.
4.  **Pub/Sub**: Hệ thống chat đơn giản hoặc thông báo sự kiện nội bộ.
5.  **List**: Hàng đợi ghép trận (Matchmaking queue).

---

## 3. Game Context: Ví dụ thực tế

### Toàn cầu Leaderboard (BXH)
Dùng `ZADD leaderboard 5000 "player_A"`. Để lấy Top 10: `ZREVRANGE leaderboard 0 9 WITHSCORES`.
- Hiệu năng: Sắp xếp 1 triệu đứa chỉ mất O(log N).

### Player Session & Lock
Khi player login, ta tạo một key `session:player123`. Nếu player login ở máy khác, ta check key này để kích máy cũ ra (Anti-multi-login).

### Distributed Lock (Redlock)
Dùng để tránh Race Condition khi nhiều server cùng can thiệp vào một tài nguyên duy nhất.

---

## 4. Lỗi thường gặp & Best Practice

### Redis RAM Full (OOM)
- **Lỗi**: Lưu Log quá đà vào Redis mà không set TTL (Time-to-live).
- **Fix**: Luôn set TTL cho các key rác. Dùng chính sách `allkeys-lru` (Dọn dẹp key ít dùng nhất khi đầy).

### Big Keys
- **Lỗi**: Lưu 1 List có 1 triệu item và gọi `LRANGE 0 -1`. Điều này sẽ làm Redis bị treo (Red-lock).
- **Fix**: Chia nhỏ dữ liệu ra (Sharding).

### Cache Aside vs Write Through
- **Game context**: Thường dùng **Write-behind**. Player đánh quái -> Update RAM -> 5 phút sau mới sync vào DB một lần để giảm tải cho DB.

---

## 5. So sánh Redis vs Database

| Tiêu chí | Redis | MySQL / MongoDB |
| :--- | :--- | :--- |
| **Lưu trữ** | RAM | Disk |
| **Tốc độ** | ~100k - 500k OPS | ~1k - 5k OPS |
| **Độ bền** | Có thể mất data nếu crash (AOF/RDB giúp đỡ phần nào) | Rất bền |
| **Giá thành** | Đắt (RAM đắt hơn Disk) | Rẻ |

---

## CÂU HỎI PHỎNG VẤN

### Mid
- **Q**: Tại sao Redis lại nhanh đến vậy mặc dù nó chạy **Single-thread**?
- **A**: 
    1. Chạy hoàn toàn trên RAM.
    2. Dùng cơ chế **IO Multiplexing** (epoll/kqueue) để xử lý hàng nghìn kết nối mà không bị block.
    3. Không tốn chi phí đổi ngữ cảnh (Context switch) giữa các thread.

- **Q**: Phân biệt `RDB` và `AOF`?
- **A**: 
    - `RDB`: Snapshot định kỳ (ví dụ 15p chụp ảnh 1 lần). Nhanh nhưng dễ mất data nếu sập giữa chừng.
    - `AOF`: Ghi log mỗi khi có lệnh mới. Chậm hơn tí nhưng an toàn dữ liệu hơn.

### Senior
- **Q**: Bạn xử lý thế nào với bài toán **Cache Avalanche** (Tuyết lở cache) khi 1 triệu key hết hạn cùng lúc?
- **A**: 
    1. Thêm một số giây ngẫu nhiên (Jitter) vào thời gian hết hạn (TTL).
    2. Dùng Cluster để phân tán key.
    3. Cơ chế **Circuit Breaker** (như Resilience4j) để ngắt request vào DB nếu thấy cache hỏng hàng loạt.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Hãy thiết kế hệ thống Chat Bang hội (Guild) dùng Redis.
- Yêu cầu: Lưu lại 50 tin nhắn gần nhất của mỗi Guild. Tin nhắn cũ hơn sẽ tự động bị xóa.
- Gợi ý: Dùng `LPUSH` kết hợp `LTRIM`.
