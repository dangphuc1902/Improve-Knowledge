# PHẦN 8: KUBERNETES & CLOUD (VẬN HÀNH GAME QUY MÔ LỚN)

Ngày xưa, Game Backend chạy trên một vài "server vật lý" đặt dưới gầm bàn. Ngày nay, chúng ta dùng **Docker** và **Kubernetes (K8s)** để quản lý hàng nghìn máy chủ trên toàn cầu chỉ bằng code.

---

## 1. Container & Docker: Đóng gói thế giới

### Nó là gì?
- **Docker**: Công cụ giúp đóng gói code, môi trường (Java, OS) thành một "thùng container" (Image) duy nhất.
- **Container**: Một thực thể đang chạy của Image đó.

### Dùng làm gì?
Đảm bảo "Chuyển giao không lỗi". Nếu code chạy tốt trên máy của bạn, nó chắc chắn sẽ chạy tốt trên Production vì môi trường bên trong container là hoàn toàn giống hệt nhau.

### Dùng khi nào?
Luôn dùng Docker cho mọi Microservice ngay từ ngày đầu tiên.

### Cách thức hoạt động:
Docker dùng cơ chế **Containerization** (chia sẻ kernel của hệ điều hành) thay vì ảo hóa phần cứng hoàn toàn như Virtual Machine (VM). Điều này làm container cực nhẹ, khởi động chỉ mất vài giây thay vì vài phút.

---

## 5. VM vs Container: Tiến hóa của hạ tầng

### Virtual Machine (VM):
- **CƠ BẢN**: Một máy chủ vật lý được chia thành nhiều "máy ảo" độc lập. Mỗi máy ảo có hệ điều hành (OS) riêng.
- **Nhược điểm**: Nặng, tốn RAM/CPU để chạy các OS dư thừa. Khởi động chậm.

### Container (Docker):
- **CƠ BẢN**: Không chạy OS riêng. Nhiều container dùng chung 1 nhân (Kernel) của hệ điều hành vật lý.
- **Ưu điểm**: Cực nhẹ, bật tắt trong vài giây. 
- **Senior Insight**: Vì dùng chung Kernel, container có tính bảo mật kém hơn VM một chút (nếu 1 container chiếm được quyền Kernel, nó có thể ảnh hưởng tới các container khác). Tuy nhiên, trong Game Backend, lợi ích về tốc độ và khả năng scale vượt xa nhược điểm này.

---


### Nó là gì?
K8s là hệ thống điều phối (Orchestration) giúp quản lý hàng nghìn container tự động.

### Dùng làm gì?
- **Tự động phục hồi (Self-healing)**: Nếu một server game bị sập, K8s tự động bật lại một bản sao mới ngay lập tức.
- **Mở rộng tự động (Scaling)**: Tự động tăng số lượng server khi game đông khách (ví dụ lúc tối) và giảm bớt khi vắng khách (lúc đêm).
- **Service Discovery**: Giúp các service tự tìm thấy nhau mà không cần cấu hình IP cứng.

### Các thành phần chính:
- **Pod**: Một hoặc một nhóm container chạy chung. Đây là đơn vị nhỏ nhất K8s quản lý.
- **Deployment**: Quản lý số lượng Pod.
- **ConfigMap/Secret**: Lưu trữ cấu hình (IP Database, Mật khẩu) tách rời khỏi code.

### Ví dụ code Thực chiến (Deployment.yaml):
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: game-server
spec:
  replicas: 3
  selector:
    matchLabels:
      app: game
  template:
    metadata:
      labels:
        app: game
    spec:
      containers:
      - name: java-app
        image: my-game:v1
        ports:
        - containerPort: 8080
        resources:
          limits:
            cpu: "500m"
            memory: "512Mi"
```

---

## 3. Game Server trên K8s: Thử thách Stateful

### Nó là gì?
Web server thường là **Stateless** (không giữ trạng thái). Nhưng Game Battle Server là **Stateful** (giữ trạng thái trận đấu trong RAM).

### Tại sao lại khó?
Nếu K8s thấy server hết RAM và đột ngột tắt (kill) Pod đang có 10 người đánh nhau để chuyển sang server khác, trận đấu sẽ bị hủy. Người chơi sẽ cực kỳ ức chế.

### Giải pháp: Agones
Agones là một open-source chạy trên nền K8s, được thiết kế bởi Google và Ubisoft dành riêng cho Game Server.
- **Cách thức**: Agones đánh dấu Pod nào đang "Busy" (đang có trận đấu). K8s sẽ không bao giờ được phép tắt Pod đó cho đến khi trận đấu kết thúc.

### Ví dụ code Thực chiến (Service.yaml):
```yaml
apiVersion: v1
kind: Service
metadata:
  name: game-lb
spec:
  type: LoadBalancer
  selector:
    app: game
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

---

## 4. Sai lầm & Best Practice

- **Sai lầm**: Không đặt **Resources Limit**. Một service bị rò rỉ bộ nhớ (Memory Leak) có thể "ăn sạch" RAM của toàn bộ máy vật lý, làm sập các service khác ở bên cạnh.
- **Best Practice**: Luôn thiết lập **Liveness Probe** (Kiểm tra app còn sống không) và **Readiness Probe** (Kiểm tra app đã sẵn sàng nhận khách chưa).
- **Best Practice**: Sử dụng **Helm Chart** để quản lý các file cấu hình YAML phức tạp.

---

## CÂU HỎI PHỎNG VẤN (Senior Level)

### 1. Tại sao nói MongoDB hỗ trợ Transaction nhưng vẫn khuyên không nên lạm dụng?
- **Answer**:
    - **Rolling**: Tắt dần từng Pod cũ, bật dần từng Pod mới. Tiết kiệm tài nguyên nhưng có lúc cả 2 bản cũ-mới cùng tồn tại.
    - **Blue-Green**: Bật song song một cụm mới (Green) giống hệt cụm cũ (Blue), sau đó đổi hướng traffic 100% sang cụm mới. An toàn, rollback nhanh nhưng tốn gấp đôi tài nguyên.

### 2. Sidecar Pattern trong K8s dùng để làm gì?
- **Answer**: Là việc chạy thêm một container phụ bên cạnh container chính trong cùng 1 Pod. Thường dùng để: Collect log (Fluentd), Proxy (Envoy), hoặc Monitor metrics (Prometheus exporter).

### 3. Làm thế nào để K8s ưu tiên chạy các Pod Game Server trên những máy chủ có ổ cứng SSD hoặc CPU xung nhịp cao?
- **Answer**: Dùng **Node Selector** hoặc **Node Affinity**. Bạn gán nhãn (label) cho các máy vật lý (ví dụ: `disk=ssd`) và yêu cầu K8s chỉ deploy Pod lên những máy có nhãn đó.

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế một hệ thống tự động mở rộng (Auto-scaling) cho Lobby Service.
Yêu cầu:
- Khi CPU vượt quá 70% trong 2 phút, tự động tăng từ 3 Pod lên 10 Pod.
- Viết file `HPA (Horizontal Pod Autoscaler)` đơn giản.
- Giải thích tại sao Battle Server lại KHÔNG NÊN dùng HPA mặc định của K8s.
