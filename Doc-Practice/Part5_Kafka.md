# PHẦN 5: KAFKA (HỆ THỐNG EVENT-DRIVEN CHO GAME)

Trong game online, có hàng tỷ sự kiện (events) xảy ra mỗi phút: Player giết quái, nhặt đồ, thăng cấp, nạp tiền... Nếu mỗi lần như vậy ta đều gọi API hoặc DB trực tiếp, hệ thống sẽ sập nhanh chóng. **Kafka** sinh ra để giải quyết bài toán này.

---

## 1. Kafka là gì?

### Nó là gì?
Kafka là một **Distributed Streaming Platform** (Nền tảng truyền dữ liệu phân tán). Hãy tưởng tượng nó như một bầy ong thợ bận rộn vận chuyển thư giữa người gửi (Producer) và người nhận (Consumer) thông qua các hòm thư (Topic).

### Dùng làm gì?
- **Decoupling**: Giúp các service không cần biết về nhau. Game Server chỉ cần báo "Player thắng", các service như PointService, QuestService, AnalyticsService tự vào nghe và làm việc của mình.
- **Log Aggregation**: Thu thập hàng tỷ dòng log từ hàng nghìn game server về một nơi duy nhất.

### Dùng khi nào?
- Khi bạn có lượng dữ liệu khổng lồ (High throughput) cần xử lý bất đồng bộ.
- Khi cần xây dựng hệ thống "Event-driven" (xử lý dựa trên sự kiện).

### Cơ chế hoạt động: Internal Log & Partition
Kafka lưu trữ message dưới dạng các file log trên đĩa cứng (disk). 
- **Partition**: Một Topic được chia thành nhiều phần (Partition) để có thể ghi/đọc song song trên nhiều server.
- **Offset**: Mỗi message trong Partition có một số thứ tự duy nhất gọi là Offset. Consumer dùng Offset để biết mình đã đọc đến đâu.

---

## 4. Message Queue: Pub/Sub vs Log-based Streaming

### Nó là gì?
- **Pub/Sub (như RabbitMQ)**: Khi có tin nhắn, nó đẩy cho người nhận. Nếu người nhận không có mặt, tin nhắn có thể bị mất hoặc phải lưu tạm trong memory. Khi đọc xong, tin nhắn biến mất.
- **Log-based (như Kafka)**: Tin nhắn được ghi vào đĩa cứng (Disk) như một cuốn sổ nhật ký. Người nhận có thể vào đọc bất cứ lúc nào, đọc đi đọc lại nhiều lần.

### Tại sao Kafka nhanh dù ghi xuống Disk?
- **CƠ BẢN**: Do Kafka dùng **Sequential I/O** (Ghi tuần tự). Việc ghi tuần tự vào đĩa cứng đôi khi còn nhanh hơn ghi ngẫu nhiên vào RAM. 
- **Senior Insight**: Kafka tận dụng **Page Cache** của hệ điều hành. Dữ liệu thực chất nằm ở tầng RAM của OS trước khi thực sự được đẩy xuống đĩa vật lý.

---


### Producer (Người gửi)
Trong Game, Producer thường là các Battle Server. 
- **Cách thức**: Khi trận đấu kết thúc, Producer gửi một `Message` (chứa dữ liệu nhị phân - Protobuf) lên Kafka.
- **Hành động**: Bạn có thể gửi theo 3 chế độ: `acks=0` (không cần phản hồi), `acks=1` (chỉ cần Leader nhận), `acks=all` (tất cả các bản sao phải nhận). Cho game, thường dùng `acks=1` để cân bằng tốc độ và an toàn.

### Consumer & Consumer Group (Người nhận)
- **Consumer Group**: Một nhóm các service cùng loại (ví dụ 3 instance của RewardService). 
- **Cơ chế**: Kafka sẽ tự động chia các Partition cho các Consumer trong nhóm sao cho không ai bị trùng việc. Nếu 1 cái chết, cái khác sẽ nhảy vào gánh thay (**Rebalance**).

### Sai lầm & Best Practice
- **Sai lầm**: Dùng Kafka để giao tiếp Real-time thay cho Socket. Kafka có độ trễ (latency) nhất định (~10-50ms), không phù hợp để truyền tọa độ di chuyển nhân vật.
- **Best Practice**: Luôn chọn **Message Key** là `playerId` hoặc `matchId` để đảm bảo các sự kiện của cùng 1 người chơi luôn được xử lý theo đúng thứ tự thời gian.

---

## 3. Quản lý Lag và Throughput

### Nó là gì?
**Consumer Lag** là khoảng cách giữa message mới nhất và message mà Consumer đang đọc. Lag càng lớn nghĩa là hệ thống đang bị chậm.

### Dùng khi nào?
Cần giám sát Lag hàng giây. Nếu Lag tăng vọt, người chơi sẽ nhận quà rất chậm dù trận đấu đã xong từ lâu.

### Cách thức hoạt động:
Kafka không đẩy (push) data cho Consumer. Consumer tự kéo (pull) data về khi nó rảnh. Nếu code xử lý logic trong Consumer quá chậm (ví dụ: query DB quá nhiều), tốc độ kéo sẽ không kịp tốc độ đẩy của Producer.

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Kafka giải quyết bài toán "Exactly Once Processing" như thế nào?
- **Answer**: Kafka dùng **Idempotent Producer** (gắn số PID và Sequence Number cho mỗi message) kết hợp với **Transaction** (Begin -> Commit) để đảm bảo dù có gửi lại thì message cũng không bị trùng lặp.

### 2. "Zero Copy" trong Kafka là gì và tại sao nó lại nhanh đến thế?
- **Answer**: Kafka không copy data từ kernel space sang application space rồi mới ghi xuống disk. Nó dùng lệnh `sendfile` của OS để đẩy trực tiếp data từ Network Buffer sang Disk Buffer (và ngược lại), giúp tránh lãng phí CPU và RAM.

### 3. Điều gì xảy ra khi một Partition bị "Hot-spot" (quá tải ghi)?
- **Answer**: Thường do chọn Message Key không đều (ví dụ: tất cả player nạp tiền đều rơi vào 1 partition). Cần thay đổi thuật toán băm (Hashing) hoặc thêm muối (salting) vào Key để trải đều data ra các partition khác.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế hệ thống **Anti-Cheat** bằng Kafka.
- Producer: Game Server gửi mọi hành động nghi ngờ (Tốc độ chạy, Sát thương đột biến).
- Consumer 1: Kiểm tra log realtime để Kick player ngay lập tức.
- Consumer 2: Lưu vào Database để Admin kiểm tra lại (Ban sau).
Yêu cầu: Mô tả cách chia Topic và số lượng Partition dự kiến cho 1 triệu CCU.
