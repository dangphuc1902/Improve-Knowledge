# CV Gaps — OAuth2, Kubernetes, SOLID, gRPC
> Kiến thức còn thiếu hoặc chưa đề cập trong CV nhưng JD yêu cầu hoặc CV ghi mà cần giải thích được.
> Viết đủ để trả lời phỏng vấn, không viết thừa.

---

## 1. OAuth2 — Authorization Framework

### 1.1 OAuth2 Là Gì? Khác JWT Như Thế Nào?

#### 🔑 Khái niệm nền tảng trước khi đọc

**Authentication (Xác thực)** = "Bạn là ai?" → Kiểm tra danh tính. Ví dụ: đăng nhập username/password.

**Authorization (Phân quyền)** = "Bạn được phép làm gì?" → Kiểm tra quyền truy cập. Ví dụ: user này có được xem wallet của người khác không?

**Token** = Chuỗi ký tự đại diện cho quyền truy cập. Thay vì gửi username/password với mỗi request, client dùng token. Server validate token → biết user là ai và có quyền gì.

**Resource Server** = Server chứa dữ liệu cần bảo vệ (API của bạn). Nhận request kèm token → validate → trả data.

**Auth Server** = Server cấp phát token (Keycloak, Auth0, Okta, Google...). Biết user là ai, biết user có quyền gì.

```
Analogy (so sánh thực tế):
  Token    = Vé xem phim
  Auth Server = Phòng vé (cấp vé)
  Resource Server = Nhân viên kiểm tra vé ở cửa rạp
```

---

**OAuth2**: Authorization **framework** — định nghĩa quy trình để lấy token (ai cấp, cấp cho ai, cấp gì).
**JWT**: Token **format** — định nghĩa cấu trúc token (header.payload.signature).

```
OAuth2 trả lời: "Làm thế nào để client lấy được access token một cách an toàn?"
JWT trả lời:    "Token trông như thế nào? Validate bằng cách nào?"
```

Có thể dùng OAuth2 với JWT (phổ biến nhất), hoặc OAuth2 với opaque token, hoặc JWT không qua OAuth2.

---


### 1.2 Authorization Code Flow (Cho User-Facing Apps)

**Khi nào dùng**: Web app, mobile app — user cần đăng nhập.

```
┌─────────┐     1. Redirect to Auth Server     ┌────────────┐
│  User   │──────────────────────────────────→ │Auth Server │
│ Browser │ ←── 2. Login page ─────────────── │(Keycloak,  │
│         │ ──── 3. Enter credentials ───────→ │ Google,    │
│         │ ←── 4. Redirect: code=AUTH_CODE ── │ Okta)      │
└─────────┘                                    └────────────┘
     │ 4. code                                       ↑
     ↓                                               │
┌─────────┐ 5. POST /token                           │
│  Your   │    (code + client_secret) ───────────────┘
│  App    │ ←── 6. {access_token, refresh_token} ─────────────
│(Backend)│                                    ┌────────────┐
│         │ ────── 7. GET /api/resource ──────→│  Resource  │
│         │         Authorization: Bearer AT   │  Server    │
│         │ ←────── 8. Protected data ──────── │(Your API)  │
└─────────┘                                    └────────────┘
```

**Tại sao code được exchange ở back-channel (step 5)?**
- Code được redirect qua URL (front-channel) → có thể bị intercept trong browser history, referrer headers
- `client_secret` KHÔNG được expose ở front-channel
- Client gửi code + client_secret qua HTTPS trực tiếp đến Auth Server → an toàn

**PKCE (Proof Key for Code Exchange)** — cho mobile/SPA (không có client_secret):
```
1. App tạo random code_verifier
2. App tạo code_challenge = BASE64URL(SHA256(code_verifier))
3. Gửi code_challenge kèm Authorization request
4. Auth Server lưu code_challenge
5. Khi exchange code, gửi code_verifier
6. Auth Server verify: SHA256(code_verifier) == stored code_challenge
```

---

### 1.3 Client Credentials Flow (Machine-to-Machine)

**Khi nào dùng**: Service-to-service, no user involved. (e.g., microservice A gọi microservice B)

```
┌────────────┐   POST /token                    ┌────────────┐
│ Service A  │─────────────────────────────────→│Auth Server │
│ (Client)   │   grant_type=client_credentials  │            │
│            │   client_id=service-a             │            │
│            │   client_secret=secret            │            │
│            │ ←── {access_token, expires_in} ── │            │
└────────────┘                                   └────────────┘
      │ Authorization: Bearer access_token
      ↓
┌────────────┐
│ Service B  │ Validate token → serve request
│ (Resource  │
│  Server)   │
└────────────┘
```

---

### 1.4 Token Types

**Access Token**: Short-lived (15 phút - 1 giờ). Dùng để access resources.

**Refresh Token**: Long-lived (days/weeks). Dùng để lấy access token mới khi expire.
```
POST /token
grant_type=refresh_token
refresh_token=REFRESH_TOKEN
client_id=...
→ {new access_token, new refresh_token}
```

**ID Token** (OpenID Connect extension of OAuth2): JWT chứa thông tin user (name, email, sub). Dành cho authentication, không dùng để gọi API.

---

### 1.5 Spring Security Resource Server (Validate JWT từ OAuth2)

```java
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()  // Endpoints công khai không cần auth
                .anyRequest().authenticated()                    // Tất cả endpoint khác bắt buộc có JWT hợp lệ
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    // Cấu hình URI để Spring Security tự động tải Public Key Set (JWKS) từ Auth Server (Keycloak/Auth0)
                    // Signature của JWT nhận được sẽ được verify bằng Public Key này mà không cần gọi HTTP sang Auth Server với mỗi request
                    .jwkSetUri("https://auth-server/.well-known/jwks.json")
                )
            )
            .build();
    }
}
```

```yaml
# application.yml — Spring Boot OAuth2 Resource Server Configuration
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: https://auth-server/protocol/openid-connect/certs  # URL lấy Public Key (JWKS) để verify chữ ký JWT
          issuer-uri: https://auth-server/realms/my-realm                  # Verify claim 'iss' (issuer) trong JWT phải khớp với URL này
```

**Scope-based authorization:**
```java
// Phân quyền theo OAuth2 Scopes trên Method Level (yêu cầu @EnableMethodSecurity):

// Yêu cầu JWT chứa scope "wallet:read" trong claim 'scope' hoặc 'scp'
@PreAuthorize("hasAuthority('SCOPE_wallet:read')")
public WalletDTO getWallet(Long id) { ... }

// Yêu cầu JWT chứa scope "wallet:write" mới được phép thực thi
@PreAuthorize("hasAuthority('SCOPE_wallet:write')")
public void updateWallet(Long id, WalletRequest req) { ... }
```

---

### 1.6 OAuth2 Interview Q&A

**Q: Tại sao không dùng Authorization Code trực tiếp, phải exchange lấy token?**
A: Authorization Code là short-lived và single-use. Nếu bị intercept → attacker không thể dùng vì cần client_secret. Tách biệt front-channel (browser) và back-channel (server-to-server) tăng security.

**Q: Refresh Token được lưu ở đâu?**
A: Server-side (backend) lưu secure. Mobile app lưu trong Keychain (iOS)/Keystore (Android). Browser SPA không nên lưu refresh token — dùng BFF (Backend For Frontend) pattern thay thế.

**Q: OAuth2 scope là gì?**
A: Scope định nghĩa quyền được granted. Client request specific scopes (`wallet:read`, `wallet:write`). User consent. Auth Server include scopes trong token. Resource Server validate scopes.

---

## 2. Kubernetes — Concepts Cơ Bản

### 2.1 Tại Sao Cần K8s Khi Đã Có Docker?

| | Docker / Docker Compose | Kubernetes |
|---|---|---|
| Scope | Single machine | Cluster nhiều machines |
| Scaling | Thủ công | Tự động (HPA) |
| Self-healing | Không | Tự restart container failed |
| Rolling update | Thủ công | Tự động (zero downtime) |
| Service discovery | Basic (compose networks) | Built-in DNS |
| Load balancing | Manual | Built-in |

**Mental model**: Docker = containers. K8s = orchestrate containers at scale.

---

### 2.2 Core Objects

**Pod**: Đơn vị nhỏ nhất. Chứa 1+ containers dùng chung network và storage.
```yaml
# Pod Manifest — Đơn vị chạy container nhỏ nhất trong Kubernetes
apiVersion: v1
kind: Pod
metadata:
  name: wallet-service-pod  # Tên đại diện của Pod
spec:
  containers:
    - name: wallet-service
      image: myrepo/wallet-service:1.0.0  # Container Image trên Registry
      ports:
        - containerPort: 8080             # Cổng app lắng nghe bên trong container
      env:
        - name: DB_URL                    # Biến môi trường inject từ Secret
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
```

**Deployment**: Manages ReplicaSet → manages Pods. Handles rolling updates và rollback.
```yaml
# Deployment Manifest — Quản lý vòng đời, scaling và Rolling Update cho các Pods
apiVersion: apps/v1
kind: Deployment
metadata:
  name: wallet-service
spec:
  replicas: 3               # Duy trì luôn có 3 bản sao (Pods) chạy song song
  selector:
    matchLabels:
      app: wallet-service   # Tìm và quản lý tất cả Pods có label 'app: wallet-service'
  strategy:
    type: RollingUpdate     # Chiến lược cập nhật không downtime (thay thế từng Pod cũ bằng Pod mới)
    rollingUpdate:
      maxUnavailable: 1     # Tối đa chỉ 1 pod bị down trong quá trình update
      maxSurge: 1           # Tối đa tạo thêm 1 pod tạm thời khi update (3 + 1 = 4 pods)
  template:
    metadata:
      labels:
        app: wallet-service # Gán label cho Pod để Deployment và Service nhận diện
    spec:
      containers:
        - name: wallet-service
          image: myrepo/wallet-service:1.0.0
          resources:
            requests:       # Mức tài nguyên tối thiểu K8s cam kết cấp cho Pod
              memory: "256Mi"
              cpu: "250m"   # 250 millicores (0.25 CPU Core)
            limits:         # Mức tài nguyên tối đa Pod được phép tiêu thụ (Vượt memory → OOMKilled)
              memory: "512Mi"
              cpu: "500m"
          readinessProbe:   # Kiểm tra container đã sẵn sàng nhận traffic chưa (chưa sẵn sàng → gỡ khỏi Load Balancer)
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 30 # Chờ 30s sau khi container start mới bắt đầu check
            periodSeconds: 10       # Chu kỳ kiểm tra 10s/lần
          livenessProbe:    # Kiểm tra container còn sống không (nếu fail → K8s kill container và restart Pod)
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
```

**Service**: Stable network endpoint. Pods có thể chết và tạo lại với IP mới, Service luôn có IP/DNS cố định.
```yaml
# Service Manifest — Đầu mối IP/DNS cố định để load balance traffic vào các Pods
apiVersion: v1
kind: Service
metadata:
  name: wallet-service
spec:
  selector:
    app: wallet-service     # Điều hướng traffic đến các Pods có nhãn 'app: wallet-service'
  ports:
    - port: 80              # Cổng của Service exposed bên trong Cluster
      targetPort: 8080      # Cổng thực tế của container bên trong Pod
  type: ClusterIP           # Loại Service: ClusterIP (Nội bộ), NodePort (Mở cổng Node), LoadBalancer (Cloud LB)
```

**Service Types:**
- `ClusterIP`: Internal only (default). Chỉ access được từ trong cluster.
- `NodePort`: Expose qua `NodeIP:NodePort`. Access từ ngoài cluster.
- `LoadBalancer`: Cloud provider tạo external load balancer (AWS ELB, GCP LB).

**Ingress**: HTTP/HTTPS routing rules vào cluster.
```yaml
# Ingress Manifest — Lớp điều hướng HTTP/HTTPS Layer 7 từ ngoài internet vào các Services nội bộ
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: / # Rewrite URL path trước khi forward vào service
spec:
  rules:
    - host: api.example.com                       # Domain name tiếp nhận request
      http:
        paths:
          - path: /wallets                        # Tất cả request /wallets*
            pathType: Prefix
            backend:
              service:
                name: wallet-service              # Forward sang Service wallet-service
                port:
                  number: 80
          - path: /transactions                   # Tất cả request /transactions*
            pathType: Prefix
            backend:
              service:
                name: transaction-service         # Forward sang Service transaction-service
                port:
                  number: 80
```

**ConfigMap & Secret:**
```yaml
# ConfigMap — Lưu trữ các cấu hình không nhạy cảm dưới dạng Key-Value
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  KAFKA_BOOTSTRAP_SERVERS: "kafka:9092"
  REDIS_HOST: "redis-master"
  LOG_LEVEL: "INFO"

---
# Secret — Lưu trữ thông tin bảo mật/nhạy cảm (Mặc định mã hóa Base64)
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  DB_PASSWORD: cGFzc3dvcmQ=    # Chuỗi base64 của "password" (echo -n "password" | base64)
  JWT_SECRET: c2VjcmV0a2V5     # Chuỗi base64 của "secretkey"
```

---

### 2.3 Essential kubectl Commands

```bash
# Pods
kubectl get pods                          # List pods in current namespace
kubectl get pods -n production            # Specific namespace
kubectl get pods -w                       # Watch (real-time)
kubectl describe pod wallet-service-xyz   # Detailed info + events
kubectl logs wallet-service-xyz           # Logs
kubectl logs wallet-service-xyz --previous # Logs from crashed container
kubectl exec -it wallet-service-xyz -- /bin/bash  # Shell into container

# Deployments
kubectl get deployments
kubectl rollout status deployment/wallet-service
kubectl rollout history deployment/wallet-service
kubectl rollout undo deployment/wallet-service          # Rollback
kubectl rollout undo deployment/wallet-service --to-revision=2  # Specific version

# Apply/Delete
kubectl apply -f deployment.yaml
kubectl delete -f deployment.yaml
kubectl delete pod wallet-service-xyz  # Force restart (pod sẽ recreate)

# Scaling
kubectl scale deployment wallet-service --replicas=5

# Port forwarding (debug local)
kubectl port-forward svc/wallet-service 8080:80
kubectl port-forward pod/wallet-service-xyz 8080:8080

# Config
kubectl get configmap app-config -o yaml
kubectl get secret db-secret -o jsonpath='{.data.DB_PASSWORD}' | base64 -d
```

---

### 2.4 HPA — Horizontal Pod Autoscaler

```yaml
# HPA (Horizontal Pod Autoscaler) Manifest — Tự động tăng/giảm số lượng Pod theo tải
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: wallet-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: wallet-service        # Đối tượng cần autoscaling (Deployment wallet-service)
  minReplicas: 2                # Số Pods tối thiểu luôn duy trì
  maxReplicas: 10               # Số Pods tối đa cho phép scale out khi tải tăng vọt
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70  # Tự động tạo thêm Pod mới khi CPU trung bình toàn bộ Pods > 70%
```

---

## 3. SOLID Principles — Java Examples

### S — Single Responsibility Principle

**Vi phạm:**
```java
// ❌ VI PHẠM SRP: UserService ôm quá nhiều trách nhiệm không liên quan
@Service
public class UserService {
    public User createUser(UserRequest req) { ... }
    public void sendWelcomeEmail(User user) { ... }  // Vi phạm: Trách nhiệm gửi email
    public byte[] generateUserReport(Long userId) { ... } // Vi phạm: Trách nhiệm báo cáo
    public void exportUsersToCsv() { ... }           // Vi phạm: Trách nhiệm xuất dữ liệu
}
```

**Correct:**
```java
// ✅ CHUẨN SRP: Chia nhỏ thành từng Service có duy nhất 1 lý do để thay đổi
@Service public class UserService { 
    public User createUser(UserRequest req) { ... } 
}
@Service public class EmailService { 
    public void sendWelcomeEmail(User user) { ... } 
}
@Service public class UserReportService { 
    public byte[] generateReport(Long userId) { ... } 
}
```

---

### O — Open/Closed Principle

**Vi phạm:**
```java
// ❌ VI PHẠM OCP: Mỗi khi thêm hình thức thanh toán mới → Phải vào sửa trực tiếp code if-else trong class
public class PaymentProcessor {
    public void process(Order order) {
        if (order.getPaymentType() == CREDIT_CARD) {
            processCreditCard(order);
        } else if (order.getPaymentType() == PAYPAL) {
            processPayPal(order);
        } else if (order.getPaymentType() == MOMO) {
            processMoMo(order);
        }
    }
}
```

**Correct: Open for extension, closed for modification:**
```java
// ✅ CHUẨN OCP: Dùng Strategy Pattern. Thêm payment mới chỉ cần tạo class implement PaymentStrategy mới
public interface PaymentStrategy {
    void process(Order order);
}

@Component public class CreditCardStrategy implements PaymentStrategy {
    public void process(Order order) { ... }
}
@Component public class MoMoStrategy implements PaymentStrategy {
    public void process(Order order) { ... }
}

@Service
public class PaymentProcessor {
    // Spring tự động inject tất cả các class thực thi PaymentStrategy vào Map này
    private final Map<PaymentType, PaymentStrategy> strategies;
    
    public void process(Order order) {
        strategies.get(order.getPaymentType()).process(order);
    }
}
```

---

### L — Liskov Substitution Principle

**Vi phạm:**
```java
// ❌ VI PHẠM LSP: Lớp con (Square) làm thay đổi hành vi/hợp đồng mong đợi của lớp cha (Rectangle)
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) { this.width = w; this.height = w; } // Vi phạm: Tự ý thay đổi cả height!
    @Override
    public void setHeight(int h) { this.width = h; this.height = h; }
}

// Hàm kiểm thử này sẽ chạy ĐÚNG với Rectangle nhưng FAIL sai lệch với Square:
void test(Rectangle r) {
    r.setWidth(5);
    r.setHeight(3);
    assert r.area() == 15; // ❌ Thất bại với Square vì area = 9 (3*3) chứ không phải 15 (5*3)!
}
```

**Correct**: Square và Rectangle không nên có hierarchy — chúng không substitutable.

---

### I — Interface Segregation Principle

**Vi phạm:**
```java
// Bad: Interface quá béo
interface WalletRepository {
    Wallet findById(Long id);
    List<Wallet> findAll();
    Wallet save(Wallet w);
    void delete(Long id);
    List<Wallet> findByUserId(Long userId);
    BigDecimal getTotalBalance();
    List<Wallet> findFrozenWallets();
    void bulkUpdate(List<Wallet> wallets);
}

// WalletReadService chỉ cần đọc nhưng phải implement cả write methods
```

**Correct:**
```java
interface WalletReader {
    Wallet findById(Long id);
    List<Wallet> findByUserId(Long userId);
}

interface WalletWriter {
    Wallet save(Wallet w);
    void delete(Long id);
}

// Class implement cả hai nếu cần
public class WalletRepositoryImpl implements WalletReader, WalletWriter { ... }
// ReadOnlyService chỉ cần WalletReader
public class WalletReadService { 
    private final WalletReader reader;
}
```

---

### D — Dependency Inversion Principle

**Vi phạm:**
```java
// ❌ VI PHẠM DIP: Module cấp cao (OrderService) phụ thuộc trực tiếp vào triển khai cụ thể cấp thấp (PostgresOrderRepository)
public class OrderService {
    private PostgresOrderRepository repository = new PostgresOrderRepository(); // Khởi tạo cứng → Không thể viết Unit Test Mock
    
    public void createOrder(Order order) {
        repository.save(order);
    }
}
```

**Correct:**
```java
// ✅ CHUẨN DIP: Cả Module cấp cao và cấp thấp đều phụ thuộc vào Abstraction (Interface)
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(Long id);
}

@Service
public class OrderService {
    private final OrderRepository repository; // Phụ thuộc vào Interface Abstraction

    // Constructor Injection: Spring container sẽ inject triển khai thực tế (PostgresOrderRepository) vào đây
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}

// Spring inject PostgresOrderRepository implements OrderRepository
// Hoặc trong test: inject MockOrderRepository
```

---

## 4. gRPC / Protobuf — CV Context

### 4.1 Protobuf Definition (FPM Pattern)

```protobuf
// Khai báo phiên bản Protobuf 3
syntax = "proto3";
package fpm.wallet;

// Cấu hình Package & Class name được sinh ra cho Java Code Generator
option java_package = "com.fpm.proto.wallet";
option java_outer_classname = "WalletProto";

// Định nghĩa gRPC Service Interface
service WalletService {
    // Unary RPC: Request - Response thông thường
    rpc GetBalance (BalanceRequest) returns (BalanceResponse);
    rpc Transfer (TransferRequest) returns (TransferResponse);
    
    // Server Streaming RPC: 1 Request - Trả về luồng dữ liệu liên tục (Stream)
    rpc StreamTransactions (TransactionStreamRequest) returns (stream Transaction);
}

// Định nghĩa Data Struct / Message (Binary format trên đường truyền)
message BalanceRequest {
    string wallet_id = 1;      // Field Tag Index (1-15 tốn 1 byte để encode header, nên ưu tiên cho các field phổ biến)
}

message BalanceResponse {
    string wallet_id = 1;
    string balance = 2;        // Khuyên dùng string cho số tiền (Decimal) để tránh sai số dấu phẩy động (float/double)
    string currency = 3;
    int64 timestamp = 4;
}

message TransferRequest {
    string from_wallet_id = 1;
    string to_wallet_id = 2;
    string amount = 3;
    string idempotency_key = 4;
}
```

### 4.2 gRPC vs REST — Khi Nào Dùng Gì

| | gRPC | REST |
|---|---|---|
| **Protocol** | HTTP/2 | HTTP/1.1 hoặc HTTP/2 |
| **Format** | Binary (Protobuf) | Text (JSON/XML) |
| **Schema** | Strict (proto file) | Loose (optional OpenAPI) |
| **Streaming** | Bidirectional native | SSE/WebSocket workaround |
| **Browser support** | Cần gRPC-Web proxy | Native |
| **Tooling** | Code gen tốt | Ecosystem lớn hơn |
| **Use case** | Internal microservices | External APIs, browser clients |

**Trong FPM**: Internal service communication → gRPC (sub-50ms latency). External REST API → Spring MVC REST Controller.

### 4.3 Schema Evolution (Protobuf)

```protobuf
// V1 Proto Message ban đầu:
message WalletResponse {
    string wallet_id = 1;
    string balance = 2;
}

// V2 Proto Message — Tiến hóa Schema đảm bảo Backward Compatibility (Tương thích ngược):
message WalletResponse {
    string wallet_id = 1;
    string balance = 2;
    string currency = 3;         // ✅ AN TOÀN: Thêm trường mới (Các client cũ chạy V1 sẽ tự bỏ qua trường này)
    // string old_field = 4;     // ❌ NGHÊM CẤM: Không bao giờ dùng lại Field Index (number 4) của trường đã bị xóa
    reserved 4;                  // ✅ Bắt buộc dùng `reserved` với tag number đã xóa để tránh ai đó vô tình đặt trùng
    reserved "old_field";        // ✅ Bắt buộc dùng `reserved` với field name cũ
}
```

**Rules:**
- **Không bao giờ** thay đổi field number của field đã tồn tại
- **Không bao giờ** reuse field number của field đã xóa → dùng `reserved`
- Thêm field mới là safe (old clients bỏ qua)
- Xóa field → đánh dấu `reserved`
- Đổi type → không safe (có thể break wire compatibility)
