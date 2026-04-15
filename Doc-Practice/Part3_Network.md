# PHẦN 3: NETWORK PROGRAMMING (GIAO THỨC TRUYỀN TẢI)

Mạng (Networking) là cầu nối duy nhất giữa Client (App game) và Server. Trong game, trễ (latency) 100ms có thể là khoảng cách giữa "Sống" và "Chết".

---

## 1. TCP vs UDP: Lựa chọn theo dòng Game

### Nó là gì?
- **TCP (Transmission Control Protocol)**: Giao thức hướng kết nối, đảm bảo dữ liệu đến đích 100% và đúng thứ tự.
- **UDP (User Datagram Protocol)**: Giao thức không hướng kết nối, gửi là quên, không cam kết gì cả.

### Dùng làm gì?
- **TCP**: Dùng để vận chuyển các dữ liệu "sống còn" như: Nạp tiền, Inventory, Đăng nhập, Chat.
- **UDP**: Dùng để truyền tọa độ di chuyển, hướng nhìn, các hành động diễn ra liên tục.

### Dùng khi nào?
- Chọn **TCP** cho game bài, turn-based (cờ), hoặc các RPG nhịp độ chậm.
- Chọn **UDP** cho các game đối kháng, bắn súng (FPS), và MOBA nhịp độ cao.

### Cơ chế hoạt động (How it works):
1. **TCP**: Bắt tay 3 bước (3-way handshake). Khi có gói tin mất, TCP sẽ dừng việc nhận các gói tin sau lại để đợi gửi lại gói cũ (**Head-of-line blocking**). Đây là nguyên nhân gây lag.
2. **UDP**: Không bắt tay. Dữ liệu đẩy ra socket là đi ngay. Nếu mất gói, server/client bỏ qua luôn và xử lý gói tiếp theo -> Luôn giữ được sự đồng bộ về thời gian thực.

---

## 2. Packet Design & Serialization (Protobuf)

### Nó là gì?
Quá trình chuyển đổi các Object trong code thành chuỗi byte nhị phân để gửi qua mạng.

### Dùng làm gì?
- Tiết kiệm băng thông (Bandwidth). Đối với game 100k CCU, giảm 10% size packet sẽ tiết kiệm hàng nghìn USD tiền server mỗi tháng.
- Tăng tốc độ xử lý: Parse nhị phân luôn nhanh hơn parse chuỗi (JSON/XML).

### Dùng khi nào?
- Luôn sử dụng **Protobuf** cho logic chiến đấu.
- Dùng **JSON/HTTP** cho các API cộng đồng, bản tin, bài viết (vì dễ debug).

### Sai lầm & Best Practice
- **Sai lầm**: Gửi quá nhiều thông tin thừa trong packet (ví dụ: gửi cả tên player trong packet cập nhật vị trí).
- **Best Practice**: Dùng **Protocol Buffer**. Chỉ gửi ID và các thông số thay đổi (Delta sync).

---

## 4. Socket & Port: Cửa ngõ kết nối

### Nó là gì?
- **IP**: Địa chỉ của tòa nhà (Máy chủ).
- **Port**: Số căn hộ trong tòa nhà (Ví dụ: Game Server 8080, Web 80).
- **Socket**: Sự kết hợp giữa `IP + Port`, tạo ra một đường ống truyền dẫn dữ liệu.

### Cơ chế TCP 3-Way Handshake (PHẢI NHỚ):
Trước khi truyền data, TCP phải thực hiện bắt tay để đồng bộ:
1. **SYN**: Client gửi yêu cầu: "Tôi muốn kết nối".
2. **SYN-ACK**: Server trả lời: "Tôi đã sẵn sàng, còn bạn?".
3. **ACK**: Client xác nhận: "OK, bắt đầu thôi!".
- **Senior Insight**: Mỗi lần bắt tay tốn thời gian (RTT). Đây là lý do tại sao Game Server thường giữ kết nối lâu dài (Persistent Connection) thay vì mở/đóng liên tục như Web.

---


### Nó là gì?
Netty là một NIO (Non-blocking IO) framework mạnh mẽ nhất cho Java, giúp xử lý hàng vạn kết nối đồng thời mà không tốn nhiều tài nguyên.

### Dùng làm gì?
- Xây dựng tầng Network cho game server. Thay thế hoàn toàn cho `java.io` cổ điển.
- Quản lý vòng đời kết nối: Connect, Disconnect, Idle timeout.

### Cách thức hoạt động: Event Loop Pattern
Netty dùng một số ít thread (vài chục) để quản lý hàng vạn socket. Khi có dữ liệu đến, nó mới báo cho thread xử lý. Điều này giúp server không bị chết vì Context Switching như cách dùng 1 thread/1 connection cổ điển.

### Best Practice
- Sử dụng **PooledByteBufAllocator** để tái sử dụng bộ nhớ đệm, tránh tạo rác (GC) cho Heap.
- Chia Handler thành 2 phần: `InboundHandler` (Giải mã packet) và `OutboundHandler` (Mã hóa và gửi đi).

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Phân biệt `Sticky Packet` (Gói tin dính) và `Partial Packet` (Gói tin bị cắt). Cách giải quyết trong Netty?
- **Answer**: 
    - **Sticky**: Hai packet nhỏ gửi gần nhau bị TCP gộp lại thành một.
    - **Partial**: Một packet lớn bị xẻ ra làm hai.
    - **Solution**: Dùng `LengthFieldBasedFrameDecoder`. Gắn thêm 2-4 byte độ dài vào đầu mỗi packet để bên nhận biết được đâu là điểm kết thúc của 1 packet.

### 2. KCP là gì? Tại sao các game như Genshin Impact lại dùng nó?
- **Answer**: KCP là một giao thức **Reliable UDP**. Nó kết hợp tốc độ của UDP và khả năng gửi lại (retransmit) của TCP nhưng thông minh hơn. Nó không bị tắc nghẽn toàn bộ luồng như TCP khi chỉ mất 1 gói nhỏ.

### 3. Client-side Prediction & Server Reconciliation là gì?
- **Answer**: 
    - **Prediction**: Client tự chạy nhân vật ngay khi bấm nút mà không đợi server trả lời (để tạo cảm giác mượt).
    - **Reconciliation**: Server trả về vị trí "chuẩn". Nếu Client đang ở vị trí sai so với server, Client phải tự "giật" về hoặc bù trừ lại.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế cấu trúc Packet cho một game Battle Royale (100 người).
- Packet 1: Cập nhật vị trí (tần suất 20 lần/giây).
- Packet 2: Nhặt trang bị (tần suất thấp, độ quan trọng cao).
Yêu cầu: Chọn giao thức (TCP/UDP) và cấu trúc dữ liệu tối ưu nhất.
