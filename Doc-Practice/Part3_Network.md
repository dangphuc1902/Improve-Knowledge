# PHẦN 3: NETWORK PROGRAMMING (GIAO THỨC TRUYỀN TẢI)

Mạng (Networking) là cầu nối duy nhất giữa Client (App game) và Server. Trong game, trễ (latency) 100ms có thể là khoảng cách giữa "Sống" và "Chết".

---

## 1. TCP vs UDP: Chọn phe nào?

### TCP (Transmission Control Protocol)
- **Đặc điểm**: Đảm bảo toàn vẹn dữ liệu, đúng thứ tự, có cơ chế bắt tay 3 bước.
- **Dùng trong game**: Game bài (Card game), Turn-based (Cờ tỷ phú), RPG có nhịp độ chậm.
- **Ưu**: Tin cậy tuyệt đối.
- **Nhược**: Head-of-line blocking (Nếu 1 gói tin mất, các gói sau phải đợi gói đó gửi lại xong mới được xử lý) -> Gây Lag.

### UDP (User Datagram Protocol)
- **Đặc điểm**: Gửi và quên. Không đảm bảo đến đích, không đảm bảo thứ tự.
- **Dùng trong game**: FPS (CS:GO), MOBA (Liên quân), Đua xe.
- **Ưu**: Tốc độ bàn thờ, không bao giờ bị nghẽn do mất gói.
- **Nhược**: Phải tự xử lý logic ở tầng ứng dụng (Reliable UDP).

---

## 2. Packet Design (Protocol Buffer vs JSON)

### JSON
- **Dùng khi nào**: Prototype nhanh, các API không quan trọng (News, Web-view).
- **Nhược**: Tốn băng thông (Text-based), tốn CPU để parse.

### Protocol Buffer (Protobuf)
- **Nó là gì**: Cơ chế serialization nhị phân của Google.
- **Tại sao Game Backend cực thích?**:
    1. **Size siêu nhỏ**: Nhỏ hơn JSON 3-5 lần.
    2. **Tốc độ Parse cực nhanh**.
    3. **Strong-type**: Có file `.proto` định nghĩa rõ ràng cho cả Client (Unity/C++) và Server (Java).

---

## 3. WebSocket (Giải pháp cho Web/Hybrid Game)

WebSocket là giao thức full-duplex chạy trên nền TCP, vượt qua được các rào cản Proxy/Firewall của trình duyệt. 
- **Real-case**: Các game .io, game chơi ngay trên Facebook/Telegram.

---

## 4. Sync State giữa Client - Server

### Interpolation & Extrapolation (Dành cho Senior)
- **Interpolation (Nội suy)**: Client nhận dữ liệu từ quá khứ (ví dụ 100ms trước) và "vẽ" hành động mượt mà.
- **Extrapolation (Ngoại suy)**: Vị trí dự đoán (Dùng trong game đua xe). Nếu mất mạng, xe vẫn chạy tiếp theo hướng cũ.

---

## 5. Netty Framework - Standard của Java Game Backend

Đừng bao giờ dùng `java.io` hay `java.nio` trực tiếp. Hãy dùng **Netty**.
- **Netty** xử lý IO đa luồng (Event Loop), giải quyết bài toán C10K (10,000 connection cùng lúc) cực tốt.

```java
// Ví dụ Packet Handler cơ bản trong Netty
public class GameServerHandler extends SimpleChannelInboundHandler<GamePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GamePacket msg) {
        // Xử lý logic game ở đây
        long playerId = getPlayerId(ctx.channel());
        GameLogicProcessor.enqueue(playerId, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

---

## CÂU HỎI PHỎNG VẤN

### Junior
- **Q**: Tại sao dùng TCP lại bị lag hơn UDP khi mạng yếu?
- **A**: Do Head-of-line blocking. Một gói tin mất sẽ làm toàn bộ các gói tin sau bị giữ lại trong buffer của hệ điều hành cho đến khi gói tin đó được gửi lại thành công.

### Mid
- **Q**: Làm thế nào để chống tấn công DOS/Flood Packet ở tầng Network cho Game Server?
- **A**: 
    1. Giới hạn số lượng packet/giây từ 1 IP.
    2. Kiểm tra `Packet Size` (Từ chối packet quá lớn).
    3. Dùng `ByteBuf` của Netty để tránh tạo quá nhiều object rác (Direct memory).

### Senior
- **Q**: Bạn sẽ thiết kế giao thức cho một game MOBA (như Liên Quân) thế nào?
- **A**: 
    - Dùng **UDP** (hoặc KCP - một loại Reliable UDP).
    - Dữ liệu di chuyển/kỹ năng gửi qua UDP.
    - Dữ liệu nạp tiền/vật phẩm gửi qua **TCP/HTTP**.
    - Sử dụng **Protobuf** để tối ưu băng thông.
    - Triển khai **Client-side Prediction** để người chơi thấy không bị delay phím bấm.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Viết một file `.proto` đơn giản mô tả hành động "Player sử dụng kỹ năng" bao gồm: `skillId`, `targetX`, `targetY`, `timestamp`, `listTargetIds`. 
Sau đó dùng lệnh protoc để generate ra code Java.
