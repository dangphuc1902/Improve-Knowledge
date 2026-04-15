# PHẦN 4: MICROSERVICES & SYSTEM DESIGN TRONG GAME

Tại sao các game lớn như Liên Minh Huyền Thoại hay PUBG không chạy trên 1 server duy nhất? Câu trả lời là để **Scale** (Mở rộng) và **Isolation** (Cô lập lỗi).

---

## 1. Monolith vs Microservices: Chiến lược phân rã

### Nó là gì?
- **Monolith**: Tất cả logic (Auth, Lobby, Battle, Pay) nằm chung một project, chạy chung một process.
- **Microservices**: Chia nhỏ hệ thống thành các service độc lập, giao tiếp với nhau qua mạng (gRPC, Kafka).

### Dùng làm gì?
- **Monolith**: Dành cho giai đoạn Prototype, game nhỏ (Indie), hoặc số lượng Player ít (< 10k CCU).
- **Microservices**: Dành cho game quy mô lớn, nhiều team phát triển đồng thời, cần tính sẵn sàng cao.

### Dùng khi nào? (Sự đánh đổi)
- Đừng chuyển sang Microservices quá sớm nếu team nhỏ (dưới 5 người). Chi phí quản lý hạ tầng (DevOps) sẽ giết chết tốc độ phát triển.
- Chuyển khi: Một phần game (ví dụ Chat) bị lỗi làm sập cả trận đấu, hoặc khi cần gán một team riêng để làm tính năng mới mà không muốn đụng vào code cũ.

### Cách thức hoạt động: API Gateway & Service Discovery
Trong thế giới microservices, Client không gọi trực tiếp các service. Nó gọi qua một **API Gateway** (như Spring Cloud Gateway). Gateway sẽ hỏi **Service Discovery** (như Eureka/Consul) để biết service đó đang nằm ở IP nào để forward request tới.

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


### Nó là gì?
- **Stateless**: Mỗi request là độc lập. Server không nhớ gì cả (như Web API truyền thống).
- **Stateful**: Server giữ trạng thái của đối tượng trong bộ nhớ (Memory).

### Dùng làm gì?
- **Stateless**: Dùng cho Auth, Shop, News, Leaderboard. Dễ dàng scale bằng cách bật thêm nhiều instance giống hệt nhau.
- **Stateful**: Dùng cho **Battle Server** (Phòng đấu). Server phải giữ vị trí 100 người, lượng máu, đạn... để tính toán realtime.

### Cơ chế hoạt động: Centralized State vs Local State
- Với Stateless: State nằm ở Database/Redis. Server chỉ việc query.
- Với Stateful: State nằm ở RAM của Server đó. Nếu server chết, trận đấu kết thúc. Đây là lý do tại sao Battle Server cực kỳ khó scale tự động.

### Sai lầm & Best Practice
- **Sai lầm**: Làm Battle Server theo kiểu Stateless (mỗi lần bắn đạn lại gọi vào DB). Trễ mạng sẽ làm game không thể chơi nổi.
- **Best Practice**: Dùng **Service Registry** để đánh dấu: "Server A đang chạy phòng X". Khi Player mất mạng, họ phải được đưa quay lại đúng Server A để tiếp tục trận đấu (**Session Stickiness**).

---

## 3. Communication: gRPC vs REST vs Kafka

### Dùng làm gì?
- **REST (JSON/HTTP)**: Giao tiếp giữa Client và Server cho các tác vụ đơn giản (Login, Get Profile).
- **gRPC**: Giao tiếp nội bộ giữa các Service (ví dụ: Lobby gọi Auth để check token). Tốc độ cực nhanh, ít tốn CPU.
- **Kafka**: Giao tiếp bất đồng bộ (ví dụ: Trận đấu kết thúc -> Gửi sự kiện để Service Report ghi log, Service Rank cộng điểm).

### Dùng khi nào?
- Dùng gRPC khi cần phản hồi ngay lập tức (Sync).
- Dùng Kafka khi chỉ cần báo tin và không muốn đợi kết quả (Async), giúp hệ thống không bị nghẽn dây chuyền.

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Giải quyết bài toán "Thundering Herd" khi Server vừa bảo trì xong và 1 triệu người cùng lúc nhấn Login?
- **Answer**: 
    1. **Exponential Backoff**: Client tự động thử lại sau một khoảng thời gian tăng dần và ngẫu nhiên (jitter).
    2. **Rate Limiting**: Giới hạn số request tại Gateway.
    3. **Queueing**: Đưa request vào hàng đợi xử lý dần thay vì cho "đâm sầm" vào Auth Service.

### 2. Sự khác biệt giữa Orchestration (như Spring Cloud) và Choreography (như Kafka) trong Microservices?
- **Answer**:
    - **Orchestration**: Có một "nhạc trưởng" điều phối (A gọi B, B gọi C). Dễ quản lý flow nhưng nhạc trưởng chết là hỏng.
    - **Choreography**: Mỗi service tự lắng nghe sự kiện để làm việc của mình. Linh hoạt, giảm phụ thuộc nhưng cực kỳ khó debug luồng đi của dữ liệu.

### 3. Làm thế nào để đảm bảo dữ liệu nhất quán (Consistency) giữa các service mà không dùng Distributed Transaction (2PC)?
- **Answer**: Dùng **Saga Pattern**. Chia transaction lớn thành một chuỗi các bước. Nếu bước 3 lỗi, ta thực hiện các "Compensation Transaction" để hoàn tác bước 1 và 2.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống **Global Leaderboard**.
Yêu cầu:
- Tách riêng service Leaderboard.
- Dữ liệu điểm số mượt mà, cập nhật realtime.
- Khi Player đánh thắng ở Battle Server, điểm phải được cập nhật vào Leaderboard Service. (Chọn gRPC hay Kafka? Giải thích tại sao).
