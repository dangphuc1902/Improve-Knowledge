# PHẦN 4: MICROSERVICES & SYSTEM DESIGN TRONG GAME

Kiến trúc Microservices giúp một hệ thống game có thể gánh được hàng triệu người chơi (CCU) bằng cách chia nhỏ các thành phần và scale chúng độc lập.

---

## 1. Monolith vs Microservices: Chiến lược phân rã

### Nó là gì?
- **Monolith**: Tất cả logic (Auth, Lobby, Battle, Pay) nằm chung một project, chạy chung một process.
- **Microservices**: Chia nhỏ hệ thống thành các service độc lập, giao tiếp với nhau qua mạng (gRPC, Kafka).

### Dùng làm gì?
- **Monolith**: Dành cho giai đoạn Prototype, game nhỏ (Indie), hoặc số lượng Player ít (< 10k CCU).
- **Microservices**: Dành cho các game AAA có quy mô lớn, nhiều team phát triển cùng lúc.

### Dùng khi nào?
Khi một bộ phận (ví dụ: Battle) cần nhiều CPU, còn bộ phận khác (ví dụ: Pay) cần sự ổn định tuyệt đối. Việc tách ra giúp chúng ta có thể nâng cấp Battle mà không sợ làm sập hệ thống thanh toán.

### Cơ chế hoạt động: Service Discovery & Gateway
- **API Gateway**: Cửa ngõ duy nhất đón người chơi. Nó sẽ điều hướng: "Bạn muốn nạp tiền? Qua cổng A. Bạn muốn vào trận? Qua cổng B".
- **Service Discovery (Eureka/Consul)**: Bản đồ của hệ thống. Giúp các service tìm thấy IP của nhau một cách tự động.

---

## 2. Stateful vs Stateless: Nỗi đau của Game Server

### Nó là gì?
- **Stateless**: Mỗi request là độc lập. Server không nhớ gì cả (như Web API truyền thống).
- **Stateful**: Server lưu trạng thái trận đấu trong RAM. Player 1 bắn đạn, Player 2 phải nhận được ngay.

### Cơ chế hoạt động:
- Với Stateless: State nằm ở Database/Redis. Server chỉ việc query.
- Với Stateful: State nằm ở RAM của Server đó. Nếu server chết, trận đấu kết thúc. Đây là lý do tại sao Battle Server cực kỳ khó scale tự động.

### Ví dụ code Thực chiến:
```java
// Service định nghĩa bằng gRPC (Lấy thông tin ví tiền - Stateless)
public class WalletService extends WalletServiceGrpc.WalletServiceImplBase {
    @Override
    public void getBalance(WalletRequest req, StreamObserver<WalletResponse> res) {
        long balance = walletRepo.findBalance(req.getPlayerId());
        res.onNext(WalletResponse.newBuilder().setAmount(balance).build());
        res.onCompleted();
    }
}
```

### Sai lầm & Best Practice
- **Sai lầm**: Làm Battle Server theo kiểu Stateless (mỗi lần bắn đạn lại gọi vào DB). Trễ mạng sẽ làm game không thể chơi nổi.
- **Best Practice**: Dùng **Service Registry** để đánh dấu: "Server A đang chạy phòng X". Khi Player mất mạng, họ phải được đưa quay lại đúng Server A để tiếp tục trận đấu (**Session Stickiness**).

---

## 3. Communication: gRPC vs REST vs Kafka

### Nó là gì?
- **gRPC**: Giao tiếp nhanh, nhị phân, dùng cho các dịch vụ cần phản hồi ngay (Request - Response).
- **Kafka**: Giao tiếp bất đồng bộ, dùng cho các dịch vụ không cần phản hồi ngay (Event-driven).

### Dùng khi nào?
- Dùng **gRPC** khi Lobby cần hỏi Wallet: "Người chơi này có đủ tiền mua kiếm không?".
- Dùng **Kafka** khi Battle báo cho Quest: "Người chơi vừa giết 10 con quái, hãy trao quà đi!".

---

## 4. DNS & Load Balancing: Điều phối lưu lượng

### Cơ chế hoạt động của DNS (CƠ BẢN):
Khi người chơi nhập `game.com`, trình duyệt sẽ hỏi các **DNS Server** để lấy địa chỉ **IP** của máy chủ.
- **Senior Insight**: Để giảm tải, ta có thể dùng **DNS Load Balancing** để trả về các IP khác nhau cho người chơi ở các khu vực khác nhau.

### Load Balancing (Cân bằng tải):
- **Nó là gì**: Một thiết bị (Software hoặc Hardware) đứng trước các Microservices để chia đều request.
- **Thuật toán phổ biến**: 
    1. **Round Robin**: Chia lần lượt (Service A, B, C, A, B, C).
    2. **Least Connections**: Gửi request cho ông nào đang rảnh nhất.
    3. **IP Hash**: Luôn gửi người chơi A vào Service A (rất quan trọng cho Stateful Game Server).

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. "Saga Pattern" là gì và tại sao nó quan trọng trong Microservices?
- **Answer**: Dùng để quản lý các giao dịch phân tán (Distributed Transaction). Nếu Player mua hòm đồ: Trừ tiền (Service Pay) -> Thêm đồ (Service Inventory). Nếu thêm đồ lỗi, Saga sẽ tự động gọi lệnh "Cộng lại tiền" (Compensating Transaction) để đảm bảo dữ liệu không bị sai lệch.

### 2. Làm thế nào để giải quyết vấn đề "Service Cascading Failure"?
- **Answer**: Dùng **Circuit Breaker** (Cầu chì). Nếu Service A gọi Service B bị timeout liên tục, "cầu chì" sẽ ngắt, không cho gọi tiếp nữa để Service A không bị treo theo.

### 3. Phân biệt API Gateway và Load Balancer?
- **Answer**: Load Balancer chỉ chia tải (Layer 4/7). API Gateway làm được nhiều hơn: Bảo mật, Giới hạn request (Rate limit), Gộp nhiều request thành một (Request Aggregation).

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống **Global Market** (Chợ giao dịch vật phẩm).
- Yêu cầu: Player A treo bán kiếm, Player B mua.
- Hãy vẽ sơ đồ các microservices cần thiết (Auth, Market, Inventory, Wallet, Notification).
- Giải thích cách dùng gRPC và Kafka trong luồng giao dịch này.
