# PHẦN 8: KUBERNETES & CLOUD (VẬN HÀNH GAME QUY MÔ LỚN)

Ngày xưa, Game Backend chạy trên một vài "server vật lý" đặt dưới gầm bàn. Ngày nay, chúng ta dùng **Docker** và **Kubernetes (K8s)** để quản lý hàng nghìn máy chủ trên toàn cầu chỉ bằng code.

---

## 1. Container & Docker cơ bản

### Container là gì?
Nó là một gói chứa tất cả mọi thứ để code của bạn chạy được: Java Runtime, Libs, Config... 
- **Lợi ích**: "Chạy được ở máy tôi thì cũng chạy được ở máy production".

### Docker trong Game
Mỗi Microservice (Auth, Matchmaking, Lobby) sẽ được build thành 1 Docker Image.

---

## 2. Kubernetes (K8s): Bộ não điều khiển

Nếu Docker là một "thùng container", thì K8s là cái "tàu chở hàng" khổng lồ điều phối các thùng đó.

### Các khái niệm quan trọng:
- **Pod**: Đơn vị nhỏ nhất, chứa 1 hoặc nhiều container.
- **Deployment**: Quản lý số lượng Pod (Ví dụ: "Tôi muốn luôn luôn có 5 Pod cho service Matchmaking").
- **Service**: Tạo một IP cố định để các Pod có thể gọi nhau.
- **ConfigMap/Secret**: Nơi để config (IP DB, Password) mà không cần sửa code.

---

## 3. Game Context: Scale Game Server & Zero Downtime

### Auto Scaling (HPA)
10h sáng game vắng khách: Chạy 2 Pod.
8h tối game cực đông: K8s tự động bật lên 20 Pod để gánh tải.
- **Lợi ích**: Tiết kiệm tiền cho công ty (chỉ trả tiền Cloud cho những gì thực sự dùng).

### Zero Downtime Deployment (Rolling Update)
Khi bạn có bản update mới cho service Lobby. K8s sẽ:
1. Bật 1 Pod phiên bản mới (v2).
2. Tắt 1 Pod phiên bản cũ (v1).
3. Lặp lại cho đến khi toàn bộ là v2.
Player hoàn toàn không biết server vừa được update.

---

## 4. Thử thách: Stateful Game Server với K8s

Đây là phần khó nhất. Các web server thường là **Stateless** (không giữ trạng thái), nên sập Pod này bật Pod khác là xong. 
Nhưng **Game Battle Server** là **Stateful**. Nếu 10 người đang đánh nhau mà Pod bị K8s tắt đi -> Trận đấu bị hủy.
- **Giải pháp**: 
    1. Dùng **Agones** (Một extension của K8s chuyên cho Game). 
    2. Agones đảm bảo K8s không bao giờ tắt một Pod đang có người chơi (In-game).

---

## 5. Flow deploy một Game Server lên K8s

1.  **Build Image**: `docker build -t my-game-server:v1 .`
2.  **Push Image**: Đẩy lên Docker Hub hoặc Google Container Registry.
3.  **Apply Manifest**: Chạy lệnh `kubectl apply -f deployment.yaml`.
4.  **Monitor**: Dùng Prometheus/Grafana để xem server có bị lag/full RAM không.

---

## CÂU HỎI PHỎNG VẤN

### Mid
- **Q**: Phân biệt `Deployment` và `StatefulSet` trong K8s?
- **A**: 
    - `Deployment`: Dùng cho các service không quan tâm danh tính Pod (như API). Pod chết bật cái mới là xong.
    - `StatefulSet`: Dùng cho các service cần danh tính cố định (như Database, Redis). Pod mới bật lên sẽ có tên và ổ đĩa (Volume) giống hệt Pod cũ.

- **Q**: Làm thế nào để config game server khác nhau giữa môi trường Dev và Production trong K8s?
- **A**: Dùng **Helm Chart** và **ConfigMap**. Mỗi môi trường sẽ có 1 file `values.yaml` riêng chứa các tham số config khác nhau.

### Senior
- **Q**: Làm thế nào để giảm thiểu độ trễ (Latency) cho người chơi ở khắp nơi trên thế giới khi dùng Cloud (Multi-region)?
- **A**:
    1. Triển khai cluster ở nhiều Region (Asia, Europe, US).
    2. Dùng **Anycast IP** hoặc Global Load Balancer để điều hướng player đến server gần họ nhất.
    3. Dùng kiến trúc **Distributed Database** (như CockroachDB hoặc Mongo Global Cluster) để sync dữ liệu giữa các vùng.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Viết một file `Deployment.yaml` đơn giản cho một service Java Game Backend. Yêu cầu:
- Chạy 3 bản sao (replicas).
- Đặt giới hạn tài nguyên (Resources Limit): CPU 500m, RAM 512MB.
- Thiết lập `LivenessProbe` để K8s tự động restart nếu app bị treo (Deadlock).
