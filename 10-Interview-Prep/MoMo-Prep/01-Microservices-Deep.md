# 🌐 Microservices Deep Dive — HTTP, gRPC, MQ, Load Balancer, API Gateway

> **Mức độ**: Senior Engineer perspective  
> **Mục tiêu**: Hiểu sâu internals, trade-offs, và kinh nghiệm thực chiến  
> **Context**: Fintech / Payment System tại MoMo

---

## PHẦN 1: HTTP — Sâu Hơn Bạn Nghĩ

### 1.1 HTTP là gì thực sự?

HTTP là **application-layer protocol** chạy trên TCP. Điều quan trọng cần hiểu:

```
Client                    Server
  |  ---- TCP Handshake -->  |   (SYN, SYN-ACK, ACK)
  |  ---- HTTP Request --->  |   (text-based)
  |  <--- HTTP Response ---  |
  |  ---- TCP Close -------> |   (hoặc keep-alive)
```

**HTTP/1.1 — Vấn đề Head-of-Line Blocking:**
```
Request 1: GET /api/user
Request 2: GET /api/orders  ← BỊ CHẶN, phải chờ Request 1 xong!
Request 3: GET /api/balance ← Bị chặn tiếp

→ Browser workaround: Mở 6 TCP connections/domain
→ Nhưng TCP connections tốn kém (memory, 3-way handshake latency)
```

**HTTP/2 — Multiplexing giải quyết HOL Blocking:**
```
Một TCP connection duy nhất:
Stream 1: [Request 1 DATA frame] → [Response 1 DATA frame]
Stream 2: [Request 2 DATA frame] → [Response 2 DATA frame]  ← song song!
Stream 3: [Request 3 DATA frame] → [Response 3 DATA frame]  ← song song!

Thêm:
- Binary protocol (nhanh hơn text parse)
- HPACK header compression (giảm bandwidth lặp lại header)
- Server Push (server chủ động push resource)
```

### 1.2 🏋️ Kinh nghiệm Senior: HTTP trong Microservices

---

### 🔴 Bài Học #1 — Connection Pool: Vấn đề thực tế và cách giải quyết

#### 📖 Bối cảnh & Vấn đề

Khi bạn gọi HTTP từ service này sang service khác (hoặc gọi External API như ngân hàng), **mỗi HTTP request đều cần một TCP connection**.

Thiết lập một TCP connection tốn 3 bước (3-way handshake):
```
Client ──── SYN ────────────→ Server     (bước 1)
Client ←─── SYN-ACK ──────── Server     (bước 2)
Client ──── ACK ────────────→ Server     (bước 3)
→ Mất ~1-5ms cho mỗi lần connect (tùy network latency)
```

**❌ Bài toán khi KHÔNG có Connection Pool:**

Tưởng tượng hệ thống Payment Service tại MoMo vào ngày sale 11/11:
```
Traffic: 500 request/giây đến Payment Service
Mỗi request: Payment Service gọi Bank API

Code sai (tạo RestTemplate mới mỗi lần):
  Request 1  → new RestTemplate() → TCP Handshake → Call Bank → Close TCP
  Request 2  → new RestTemplate() → TCP Handshake → Call Bank → Close TCP
  Request 3  → new RestTemplate() → TCP Handshake → Call Bank → Close TCP
  ...500 lần/giây

Hệ quả:
1. 500 TCP connections được mở rồi đóng mỗi giây
2. OS phải cấp phát và giải phóng socket file descriptor liên tục
3. Port exhaustion: OS có ~60,000 available ports per IP
   500 req/s × 30s (TIME_WAIT state) = 15,000 ports bị chiếm
   → Sau vài phút: "Cannot assign requested address" error!
4. Mỗi TCP handshake tốn 3-5ms → 500 × 5ms = 2.5 giây chỉ để connect
   (trong khi Bank API chỉ cần 50ms để trả lời)
5. GC pressure: Mỗi RestTemplate tạo ra nhiều objects → GC pause thường xuyên
```

**Cụ thể hơn — Timeline của sự cố:**
```
08:00 - Sale bắt đầu, traffic tăng đột biến từ 50 → 500 req/s
08:03 - Logs bắt đầu thấy: "Connection reset by peer"
08:05 - Error rate tăng lên 15%
08:07 - "Cannot assign requested address: connect" errors
08:10 - Payment Service không nhận request mới được → DOWN
08:10 - Khách hàng không thanh toán được → Revenue loss!

Nguyên nhân gốc (Root Cause):
  mỗi request mở 1 TCP connection mới
  → TIME_WAIT accumulate → Port exhaustion
```

**✅ Giải pháp: Connection Pool**

Connection Pool hoạt động như một "bể" connection tái sử dụng:
```
Lần đầu khởi động: Pool tạo sẵn N connections đến Bank API
(TCP handshake xảy ra chỉ 1 lần duy nhất khi tạo connection)

Request đến:
  → Mượn connection từ pool (instant, không cần handshake)
  → Dùng xong → Trả lại pool (connection không đóng, chờ dùng lại)
  → Request sau mượn tiếp (instant again)
```

```java
// ✅ ĐÚNG: Singleton Bean với Apache HttpClient connection pool
@Configuration
public class HttpConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 1. Tạo Connection Pool Manager
        PoolingHttpClientConnectionManager connectionManager =
            new PoolingHttpClientConnectionManager();

        // Ý nghĩa các con số:
        // maxTotal = 200: Tổng số connections tối đa trong pool
        //   → Nếu tất cả 200 đang dùng, request thứ 201 phải CHỜ
        //   → Set quá cao → Tốn memory (mỗi connection ~64KB)
        //   → Set quá thấp → Bottleneck, request phải chờ
        connectionManager.setMaxTotal(200);

        // defaultMaxPerRoute = 50: Tối đa 50 connections đến 1 host
        //   Route = (host, port, protocol), ví dụ: (bankapi.vn, 443, HTTPS)
        //   Ngăn 1 downstream service chiếm hết toàn bộ pool
        connectionManager.setDefaultMaxPerRoute(50);

        // Với Bank API quan trọng hơn, cho phép nhiều connections hơn:
        HttpHost bankApiHost = new HttpHost("bankapi.vietcombank.vn", 443, "https");
        connectionManager.setMaxPerRoute(
            new HttpRoute(bankApiHost), 100); // Bank API được 100/200 connections

        // 2. Cấu hình Timeout (QUAN TRỌNG!)
        RequestConfig requestConfig = RequestConfig.custom()
            // connectTimeout: Thời gian tối đa để thiết lập TCP connection
            // Nếu bank server không respond trong 2s → TimeoutException
            .setConnectTimeout(2000)

            // socketTimeout (readTimeout): Thời gian tối đa GIỮA 2 data packets
            // Bank đang process → nếu 5s không gửi gì về → TimeoutException
            .setSocketTimeout(5000)

            // connectionRequestTimeout: Thời gian tối đa CHỜ connection từ pool
            // Nếu pool đầy và chờ 1s vẫn không có connection → TimeoutException
            .setConnectionRequestTimeout(1000)
            .build();

        // 3. Build HttpClient với eviction policy
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            // Evict connections bị idle > 30s (tránh stale connections)
            .evictIdleConnections(30L, TimeUnit.SECONDS)
            // Evict connections đã expired
            .evictExpiredConnections()
            .build();

        // 4. Wrap vào RestTemplate
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }

    // Nếu dùng WebClient (reactive/non-blocking) - modern approach:
    @Bean
    public WebClient webClient() {
        // Netty connection pool (better for async)
        ConnectionProvider provider = ConnectionProvider.builder("payment-pool")
            .maxConnections(200)
            .maxIdleTime(Duration.ofSeconds(30))
            .maxLifeTime(Duration.ofMinutes(5))
            .pendingAcquireTimeout(Duration.ofSeconds(1))
            .build();

        HttpClient nettyClient = HttpClient.create(provider)
            .responseTimeout(Duration.ofSeconds(5));

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(nettyClient))
            .build();
    }
}
```

**📊 So sánh trước/sau khi dùng Connection Pool:**

| Metric | Không có Pool | Có Pool (200 connections) |
|---|---|---|
| Latency per request | 50ms (API) + 5ms (handshake) | 50ms (API) + ~0.1ms (borrow) |
| Throughput tại 100 req/s | ~Ổn | ~Ổn |
| Throughput tại 500 req/s | ❌ Port exhaustion | ✅ Hoạt động tốt |
| Memory footprint | Tăng liên tục (leak) | Ổn định |
| GC pressure | Cao (nhiều object tạo/hủy) | Thấp |

---

### 🔴 Bài Học #2 — Timeout: Khi Không Có Timeout Thì Sao?

#### 📖 Bối cảnh & Vấn đề

**❌ Bài toán khi KHÔNG set Timeout:**

```
Hệ thống: Payment Service gọi Bank API của VietcomBank
Bình thường: Bank API trả về trong 200ms
Sự cố: Bank API đang bị slow (maintenance background job)
        Bank nhận request nhưng mất 120 giây mới trả lời
```

**Timeline của Cascading Failure (không có timeout):**

```
T+0s   : Request 1 đến Payment Service → gọi Bank API
T+0s   : Thread #1 của Tomcat bị BLOCK, chờ Bank API
T+5s   : Request 2, 3, 4, 5 đến → Thread #2,3,4,5 bị BLOCK
T+30s  : 30 requests đã đến → 30 threads bị BLOCK
T+60s  : 60 requests đã đến → 60 threads bị BLOCK
T+120s : Tomcat default thread pool = 200 threads
         200 threads đều bị BLOCK chờ Bank API

T+121s : Request 201 đến Payment Service
         → Tomcat không có thread nào free
         → Response: 503 Service Unavailable (hoặc timeout)

T+121s : TẤT CẢ requests đến Payment Service đều bị từ chối!
         Không chỉ request gọi Bank API,
         mà CẢ các request khác (check balance, get history...)!

Kết quả: Payment Service hoàn toàn DOWN dù:
  - Code không có bug
  - Database bình thường
  - Chỉ vì Bank API bị slow!
```

**Đây gọi là Cascading Failure / Downstream Dependency bringing you down.**

**✅ Giải pháp đa tầng:**

```
Tầng 1: Connect Timeout   → Phát hiện Bank Server không phản hồi
Tầng 2: Read Timeout      → Phát hiện Bank đang xử lý quá lâu
Tầng 3: Circuit Breaker   → Dừng gọi Bank khi nhiều lần fail liên tiếp
Tầng 4: Thread Isolation  → Giới hạn số thread được phép gọi Bank
```

```java
@Configuration
public class ResilienceConfig {

    // ===== Tầng 1 & 2: Connect + Read Timeout (đã config ở trên) =====
    // connectTimeout=2s: Nếu Bank không accept TCP trong 2s → fail fast
    // readTimeout=5s:    Nếu Bank nhận request nhưng 5s không trả lời → fail fast

    // ===== Tầng 3: Circuit Breaker (Resilience4j) =====
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            // Sau 10 requests trong sliding window
            .slidingWindowSize(10)
            // Nếu > 50% request fail → Mở circuit (OPEN state)
            .failureRateThreshold(50)
            // Slow call: Request > 3s cũng tính là failure
            .slowCallDurationThreshold(Duration.ofSeconds(3))
            .slowCallRateThreshold(50)
            // Sau khi OPEN: Chờ 30s trước khi thử lại (HALF-OPEN)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            // Ở HALF-OPEN: Cho 3 request thử → Nếu OK thì CLOSE lại
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();
    }

    // ===== Tầng 4: Bulkhead - Thread Isolation =====
    @Bean
    public BulkheadConfig bulkheadConfig() {
        return BulkheadConfig.custom()
            // Tối đa 20 concurrent calls đến Bank API
            // Dù Payment Service có 200 threads, chỉ 20 được gọi Bank
            // 180 thread vẫn free phục vụ request khác!
            .maxConcurrentCalls(20)
            // Nếu 20 slots đều bận, request thứ 21 chờ tối đa 100ms
            // Sau đó throw BulkheadFullException → Fallback ngay
            .maxWaitDuration(Duration.ofMillis(100))
            .build();
    }
}

// Service với đầy đủ resilience:
@Service
public class BankApiClient {

    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;

    public PaymentResult callBankApi(PaymentRequest request) {
        // Kết hợp Circuit Breaker + Bulkhead
        Supplier<PaymentResult> decoratedCall = Decorators
            .ofSupplier(() -> doCallBankApi(request))
            .withCircuitBreaker(circuitBreaker)
            .withBulkhead(bulkhead)
            .withFallback(
                List.of(CallNotPermittedException.class,  // Circuit OPEN
                        BulkheadFullException.class,      // Too many concurrent
                        TimeoutException.class),          // Timeout
                ex -> handleFallback(request, ex)
            )
            .decorate();

        return decoratedCall.get();
    }

    private PaymentResult doCallBankApi(PaymentRequest request) {
        // Actual HTTP call (timeout đã config trong RestTemplate)
        return restTemplate.postForObject(
            "https://bankapi.vietcombank.vn/payment",
            request,
            PaymentResult.class
        );
    }

    // Fallback khi Bank API không available
    private PaymentResult handleFallback(PaymentRequest request, Throwable ex) {
        log.warn("Bank API unavailable for txn {}: {}",
            request.getTransactionId(), ex.getClass().getSimpleName());

        // Option 1: Queue for retry (Kafka)
        kafkaTemplate.send("payment.retry.queue", request);

        // Option 2: Return PENDING (xử lý async sau)
        return PaymentResult.builder()
            .status(PaymentStatus.PENDING)
            .message("Payment queued, will process shortly")
            .build();
    }
}
```

**Circuit Breaker State Machine:**
```
                 ┌─────────────────────────────────────────┐
                 │                                         │
    Failure rate < 50%        Failure rate >= 50%          │
         ┌────────────────────────────────────┐            │
         │                                   │            │
         ▼                                   ▼            │
   ┌──────────┐                        ┌──────────┐       │
   │  CLOSED  │                        │   OPEN   │       │
   │(Normal)  │                        │(Blocked) │       │
   └──────────┘                        └──────────┘       │
         ↑                                   │            │
         │                    30s timeout    │            │
         │                                   ▼            │
         │                           ┌─────────────┐      │
         │    3 test requests OK     │  HALF-OPEN  │      │
         └───────────────────────────│  (Testing)  │      │
                                     └─────────────┘      │
                                            │             │
                                   3 requests FAIL        │
                                            └─────────────┘
                                            (Back to OPEN)

CLOSED (Bình thường):
  Mọi request đi qua → Counting failures

OPEN (Bank API đang lỗi):
  Không cho request nào qua → Fail fast ngay lập tức
  → Không tốn thread chờ bank timeout
  → 180 thread vẫn phục vụ các request khác!

HALF-OPEN (Đang test xem bank OK chưa):
  Cho 3 request thử:
  - Nếu OK → CLOSED (bank đã recover)
  - Nếu fail → OPEN lại (bank vẫn còn lỗi)
```

**📊 Tác động của Circuit Breaker:**

| Tình huống | Không có CB | Có CB |
|---|---|---|
| Bank API timeout 30s × 200 threads | 200 threads block 30s | Fail fast ~0ms |
| Error rate khi bank down | 100% sau 30s | ~5% (chỉ requests trong CLOSED state) |
| Recovery time | Manual restart | Automatic (HALF-OPEN → CLOSED) |
| Resource usage khi bank down | 100% threads wasted | <5% threads affected |

---

### 🔴 Bài Học #3 — Connection Leak: Bug Khó Tìm Nhất

#### 📖 Vấn đề

Connection leak xảy ra khi connection được mượn từ pool nhưng **không bao giờ trả lại**.

```java
// ❌ CODE GÂY CONNECTION LEAK:
public String callExternalApi(String url) throws Exception {
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    conn.setRequestMethod("GET");

    if (conn.getResponseCode() != 200) {
        throw new RuntimeException("API failed"); // ← CONNECTION KHÔNG ĐƯỢC ĐÓNG!
    }

    // Đọc response...
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(conn.getInputStream()));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        response.append(line);
    }
    // Nếu exception xảy ra trong while loop → conn không được đóng!

    conn.disconnect(); // Chỉ đóng khi không có exception
    return response.toString();
}
```

**Timeline của Connection Leak:**

```
T+0h   : Service khởi động, Pool có 200 connections available
T+1h   : 5 connections bị leak (exceptions nhưng không đóng)
         Pool còn: 195 available
T+4h   : 20 connections bị leak
         Pool còn: 180 available
T+8h   : Hết giờ làm việc, ít traffic → ít leak hơn
T+9h   : Đêm, maintenance batch chạy → nhiều exceptions → nhiều leak
T+next morning : Pool còn ~50 connections
T+peak morning : Traffic tăng, pool cạn → requests chờ → timeout
                 Logs: "Timeout waiting for connection from pool"
                 System hiện tương đương DOWN trong peak hours!

Nguy hiểm: Vấn đề chỉ xuất hiện sau nhiều giờ (hoặc ngày)
            Không reproduce được dễ dàng → Khó debug!
```

**✅ Fix với try-with-resources:**

```java
// ✅ Dùng try-with-resources (Java 7+):
public String callExternalApi(String url) throws Exception {
    // Khi try block kết thúc (dù exception hay không),
    // Java TỰ ĐỘNG gọi conn.close()
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    try {
        conn.setRequestMethod("GET");

        int statusCode = conn.getResponseCode();
        if (statusCode != 200) {
            throw new RuntimeException("API failed with status: " + statusCode);
        } // ← Connection vẫn sẽ được đóng sau khi throw!

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } // ← reader.close() được gọi tự động
    } finally {
        conn.disconnect(); // ← Luôn luôn chạy, dù exception hay không
    }
}

// ✅ Với Apache HttpClient (pool-based), không cần lo connection leak:
// CloseableHttpResponse tự đóng khi dùng try-with-resources
public String callWithApacheClient(String url) throws IOException {
    try (CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
        // connection được trả về pool khi response.close() được gọi
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException("API failed: " + statusCode);
        }
        return EntityUtils.toString(response.getEntity());
    } // ← response.close() → connection trả về pool
}
```

**Cách detect Connection Leak trong production:**

```java
// 1. Monitoring Pool metrics với Actuator + Micrometer:
@Configuration
public class PoolMetricsConfig {

    @PostConstruct
    public void bindPoolMetrics() {
        // Expose pool stats qua Actuator /actuator/metrics
        Metrics.gauge("http.pool.available", connectionManager,
            cm -> cm.getTotalStats().getAvailable());
        Metrics.gauge("http.pool.leased", connectionManager,
            cm -> cm.getTotalStats().getLeased());
        Metrics.gauge("http.pool.pending", connectionManager,
            cm -> cm.getTotalStats().getPending());
    }
}

// 2. Alert khi pool sắp cạn:
// Prometheus alert rule:
// alert: HTTPConnectionPoolExhausted
// expr: http_pool_available < 10
// for: 2m
// → Cảnh báo sớm trước khi xảy ra vấn đề!

// 3. Apache HttpClient có built-in leak detection:
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
cm.setValidateAfterInactivity(5000); // Validate connection sau 5s idle
// → Tự detect và remove stale connections
```

---

### 1.3 HTTP Status Codes — Edge Cases

```
400 vs 422:
  400: Request body không parse được (invalid JSON, missing required field)
  422: JSON valid, field có, nhưng logic sai (amount=-100)

401 vs 403:
  401: "Tôi không biết bạn là ai" (no token / expired token)
  403: "Tôi biết bạn là ai nhưng bạn không được phép"

Fintech-specific:
  409 Conflict: Idempotency key đã tồn tại (duplicate payment)
  429 Too Many Requests: Rate limit exceeded (include Retry-After header)
```

---

## PHẦN 2: gRPC — Khi HTTP JSON Không Đủ Nhanh

### 2.1 Protobuf vs JSON — Tại sao nhỏ hơn?

```protobuf
// user.proto
syntax = "proto3";

message UserResponse {
    int64 user_id = 1;
    string name = 2;
    double balance = 3;
    bool is_active = 4;
}

service UserService {
    rpc GetUser (UserRequest) returns (UserResponse);
    // Server streaming: Real-time transaction updates
    rpc StreamTransactions (UserRequest) returns (stream TransactionEvent);
}
```

```
JSON:    {"user_id": 12345, "name": "Phuc", "balance": 100000.0}
         → 57 bytes (field names included, text encoding)

Protobuf: field 1 (varint 12345) + field 2 ("Phuc") + field 3 (double)
          → ~21 bytes (field names replaced by numbers, binary encoding)

Lý do nhỏ hơn:
- Không gửi field names (chỉ gửi field number: 1, 2, 3)
- Integers dùng varint encoding (nhỏ numbers = ít bytes hơn)
- Không có quotes, commas, whitespace
```

### 2.2 4 loại gRPC Streaming

```java
// 1. Unary (như HTTP thường)
rpc GetUser (UserRequest) returns (UserResponse);

// 2. Server Streaming (nhiều response từ 1 request)
rpc StreamMarketPrices (SubscribeRequest) returns (stream PriceUpdate);
// Use case: Real-time price updates, live transaction status

// 3. Client Streaming (client gửi nhiều, server trả 1)
rpc UploadTransactions (stream Transaction) returns (UploadResult);
// Use case: Batch import, file upload chunking

// 4. Bidirectional Streaming
rpc Chat (stream Message) returns (stream Message);
// Use case: Chat, game state sync, live collaboration
```

### 2.3 Java gRPC — Production Code

```java
// Server side:
@GrpcService
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Override
    public void processPayment(PaymentRequest request,
                               StreamObserver<PaymentResponse> observer) {
        try {
            PaymentResult result = paymentService.process(
                request.getUserId(), request.getAmount());

            observer.onNext(PaymentResponse.newBuilder()
                .setTransactionId(result.getTxnId())
                .setStatus(result.getStatus())
                .build());
            observer.onCompleted();

        } catch (InsufficientFundsException e) {
            observer.onError(Status.FAILED_PRECONDITION
                .withDescription("Insufficient funds")
                .asException());
        } catch (Exception e) {
            observer.onError(Status.INTERNAL
                .withDescription(e.getMessage())
                .asException());
        }
    }
}

// Client side:
@Service
public class PaymentClient {

    private final PaymentServiceGrpc.PaymentServiceBlockingStub stub;

    public PaymentClient(@GrpcClient("payment-service") Channel channel) {
        // QUAN TRỌNG: Luôn set deadline!
        this.stub = PaymentServiceGrpc.newBlockingStub(channel)
            .withDeadlineAfter(5, TimeUnit.SECONDS);
    }

    public PaymentResult processPayment(long userId, double amount) {
        try {
            PaymentResponse response = stub.processPayment(
                PaymentRequest.newBuilder()
                    .setUserId(userId)
                    .setAmount(amount)
                    .build());
            return mapToResult(response);

        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case DEADLINE_EXCEEDED: throw new TimeoutException();
                case UNAVAILABLE: throw new ServiceUnavailableException();
                case FAILED_PRECONDITION: throw new BusinessException(e.getMessage());
                default: throw new TechnicalException(e);
            }
        }
    }
}
```

### 2.4 🏋️ Kinh nghiệm Senior: gRPC Pitfalls

**Pitfall #1: Backward Compatibility — RẤT QUAN TRỌNG**
```protobuf
// ✅ Thêm field mới với field number MỚI (backward compatible):
message UserResponse {
    int64 user_id = 1;
    string name = 2;
    double balance = 3;       // Thêm mới → old client bỏ qua
}

// ❌ KHÔNG BAO GIỜ:
// - Đổi field number (1→5): Binary data không đọc được
// - Đổi data type (int→string): Corruption
// - Xóa field: Old message không parse được
// - Reuse field number cho field mới: Data corruption
```

**Pitfall #2: gRPC không tốt cho browsers**
```
gRPC dùng HTTP/2 binary frames → Browsers không thể đọc raw!
→ Phải dùng grpc-web (cần proxy translate)
→ Hoặc dùng REST cho public API, gRPC cho internal

Thực tế tại nhiều công ty:
- Public API: REST/JSON (mobile, web, third-party)
- Internal services: gRPC (performance, type-safety)
```

---

## PHẦN 3: Message Queue — RabbitMQ & Kafka

### 3.1 Tại sao cần Message Queue?

```
VẤN ĐỀ với synchronous chain:
Payment Service → HTTP → Notification → HTTP → Email → HTTP → Analytics
                                                              ↑
                                                         Nếu timeout 5s
                                                         → Toàn chain block!

GIẢI PHÁP với Message Queue:
Payment Service → DB + Kafka topic "payment.events"
                              ↓
                   ┌──────────┼──────────┐
                   ↓          ↓          ↓
             Notification   Email   Analytics
             (async)       (async)  (async)

Payment Service return ngay sau khi publish event!
→ Latency giảm từ ~500ms xuống ~50ms
→ Downstream failure không ảnh hưởng payment
```

### 3.2 RabbitMQ — Sâu về Exchanges và Routing

```
Producer → Exchange → [Binding với routing key] → Queue → Consumer

Exchange types và use cases:

1. DIRECT Exchange: Exact routing key match
   Payment producer publish key "payment.vn"
   → Chỉ đến queue "vietnam-payments"

2. TOPIC Exchange: Pattern matching (dùng nhiều nhất)
   "payment.#" → match tất cả bắt đầu bằng "payment."
   "*.success" → match "payment.success", "order.success"
   "#.critical" → match bất kỳ kết thúc bằng ".critical"

3. FANOUT Exchange: Broadcast đến tất cả bound queues
   Use case: System-wide event (maintenance announcement)
   Tất cả services nhận được cùng message

4. HEADERS Exchange: Route theo message header properties
   (ít phổ biến hơn)
```

**RabbitMQ Message Acknowledgment — CRITICAL:**
```java
@RabbitListener(queues = "payment.events", ackMode = "MANUAL")
public void handlePaymentEvent(Message message, Channel channel)
        throws IOException {
    long deliveryTag = message.getMessageProperties().getDeliveryTag();

    try {
        PaymentEvent event = objectMapper.readValue(
            message.getBody(), PaymentEvent.class);
        notificationService.sendReceipt(event);

        // SUCCESS: Xóa message khỏi queue
        channel.basicAck(deliveryTag, false);

    } catch (BusinessException e) {
        // Business error → Không retry, gửi vào DLQ
        log.error("Business error processing event", e);
        channel.basicNack(deliveryTag, false, false); // requeue=false

    } catch (DatabaseException e) {
        // Technical error (DB down) → Retry (requeue=true)
        log.warn("Temporary error, will retry", e);
        channel.basicNack(deliveryTag, false, true); // requeue=true
    }
}
```

**Dead Letter Queue Setup:**
```java
@Configuration
public class RabbitConfig {

    // Main queue với DLQ config
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable("payment.events")
            .withArgument("x-dead-letter-exchange", "payment.dlx")
            .withArgument("x-dead-letter-routing-key", "payment.dead")
            .withArgument("x-message-ttl", 30000)      // 30s TTL
            .withArgument("x-max-retries", 3)           // Max 3 retries
            .build();
    }

    // Dead Letter Queue (không tự xử lý, cần manual review hoặc alert)
    @Bean
    public Queue paymentDlq() {
        return QueueBuilder.durable("payment.dlq").build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("payment.dlx");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(paymentDlq())
            .to(dlxExchange()).with("payment.dead");
    }
}
```

### 3.3 Kafka — Internals Thực Sự

**Partition Key Strategy:**
```java
// Kafka guarantee ordering WITHIN a partition
// Key quyết định partition nào sẽ nhận message

// Use case Payment:
// userId làm key → Tất cả events của user X vào cùng partition
// → Events của user X được process theo đúng thứ tự!

ProducerRecord<String, PaymentEvent> record = new ProducerRecord<>(
    "payment-events",
    userId.toString(),   // ← Partition key (hash → partition number)
    paymentEvent
);
producer.send(record, (metadata, exception) -> {
    if (exception != null) {
        log.error("Failed to publish: {}", exception.getMessage());
        // Handle: DLQ hoặc retry
    } else {
        log.info("Published to partition {}, offset {}",
            metadata.partition(), metadata.offset());
    }
});
```

**Consumer Group và Rebalancing:**
```
Topic "payments" có 3 partitions:

Consumer Group "notification-service":
  3 instances → mỗi instance nhận 1 partition (optimal)
  2 instances → 1 instance nhận 2 partitions (sub-optimal nhưng OK)
  4 instances → 1 instance idle (waste, >partitions = no benefit)
  
Rebalancing trigger:
  - Consumer instance crash/leave
  - New consumer instance join
  - Topic partition count changes
  
Rebalancing vấn đề:
  - Trong thời gian rebalance → group DỪNG consume
  - Có thể mất 10-30 giây
  - Giải pháp: Static group membership (kafka.consumer.group-instance-id)
```

**Offset Management (QUAN TRỌNG trong fintech):**
```java
@KafkaListener(
    topics = "payment-events",
    groupId = "notification-service",
    containerFactory = "manualAckListenerContainerFactory"
)
public void processPaymentEvent(
        ConsumerRecord<String, PaymentEvent> record,
        Acknowledgment ack) {

    PaymentEvent event = record.value();
    String eventId = event.getEventId();

    // Idempotency check (at-least-once → may redeliver!)
    if (processedEvents.contains(eventId)) {
        log.warn("Duplicate event {}, skipping", eventId);
        ack.acknowledge(); // Vẫn ACK để tránh vòng lặp
        return;
    }

    try {
        notificationService.send(event);
        processedEvents.add(eventId); // Mark as processed
        ack.acknowledge();  // Commit offset chỉ sau khi thành công

    } catch (TemporaryException e) {
        // Không ACK → Kafka redeliver sau khi consumer restart
        log.error("Temporary failure for event {}", eventId, e);
        throw e; // Trigger redelivery
    }
}
```

**Kafka Exactly-Once (Fintech requirement):**
```java
// Producer config:
@Bean
public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);    // Chống duplicate từ retry
    props.put(ProducerConfig.ACKS_CONFIG, "all");                  // Wait all replicas
    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "payment-tx-1"); // Transactional

    return new DefaultKafkaProducerFactory<>(props);
}

// Dùng trong code:
@Transactional // Kết hợp DB transaction + Kafka transaction
public void publishPaymentEvent(Payment payment) {
    paymentRepo.save(payment);           // DB write
    kafkaTemplate.send("payments", event); // Kafka publish
    // Nếu bất kỳ bước nào fail → cả 2 đều rollback!
}
```

### 3.4 🏋️ Kinh nghiệm Senior: Message Queue Patterns

**Pattern 1: Outbox Pattern (Database + Kafka atomic)**
```java
// VẤN ĐỀ:
txnRepo.save(transaction);         // ✅ DB write OK
kafkaTemplate.send("payments", e); // ❌ CRASH! Kafka không nhận
// → DB nói SUCCESS, nhưng downstream không biết!

// OUTBOX PATTERN: Viết vào DB trước, publish sau
@Transactional
public void processPayment(PaymentRequest request) {
    Transaction txn = new Transaction(request);
    txn.setStatus(SUCCESS);
    txnRepo.save(txn);

    // Ghi vào outbox table trong CÙNG transaction:
    outboxRepo.save(OutboxEvent.builder()
        .topic("payment-events")
        .key(txn.getUserId().toString())
        .payload(serialize(txn))
        .status(PENDING)
        .build());
    // Nếu crash → cả 2 rollback → consistent!
}

// Background job publish từ outbox:
@Scheduled(fixedDelay = 500)
public void publishPendingEvents() {
    List<OutboxEvent> pending = outboxRepo.findByStatus(PENDING, limit(100));
    pending.forEach(event -> {
        kafkaTemplate.send(event.getTopic(), event.getKey(), event.getPayload());
        event.setStatus(PUBLISHED);
        outboxRepo.save(event);
    });
}
```

**Pattern 2: Consumer Idempotency**
```java
// Kafka at-least-once → Consumer nhận message nhiều lần khi failure
// → Consumer BẮT BUỘC phải idempotent!

// Ví dụ: Notification service gửi email
@KafkaListener(topics = "payment-events")
public void sendPaymentEmail(PaymentEvent event) {
    String dedupeKey = "email-sent:" + event.getTransactionId();

    // Redis SET NX: Chỉ set nếu chưa tồn tại
    Boolean isNew = redis.opsForValue()
        .setIfAbsent(dedupeKey, "1", Duration.ofDays(1));

    if (Boolean.FALSE.equals(isNew)) {
        log.info("Email already sent for txn {}", event.getTransactionId());
        return; // Idempotent: skip duplicate
    }

    emailService.sendReceipt(event.getUserEmail(), event);
}
```

---

## PHẦN 4: Load Balancer

### 4.1 Layers và Algorithms

```
Internet Traffic
      ↓
[DNS Load Balancing]        L3 — IP level round-robin
      ↓
[Layer 4 LB - AWS NLB]      TCP/UDP — Fast, no content inspection
      ↓
[Layer 7 LB - AWS ALB/Nginx] HTTP — Smart routing by URL, headers, cookies
      ↓
[Service Mesh - Istio]      Sidecar proxy level — Advanced traffic management
      ↓
Application Servers
```

**Thuật toán so sánh:**
```
Round Robin: 1→2→3→1→2→3
→ Vấn đề: Server 1 xử lý request nặng (5s), vẫn nhận request mới

Weighted Round Robin: Server 1 (8 core, weight=8), Server 2 (4 core, weight=4)
→ Server 1 nhận 2x request → Phù hợp khi hardware khác nhau

Least Connections: Route đến server ít active connections nhất
→ Tốt cho long-lived connections (WebSocket, gRPC streaming)

IP Hash: hash(client_ip) → server index
→ Session affinity: Same client → same server
→ Vấn đề: NAT (cả công ty = 1 IP) → 1 server quá tải

Least Response Time: Route đến server có response time thấp nhất
→ Best performance thực tế, nhưng cần health monitoring overhead
```

### 4.2 Health Check — Production Setup

```java
// Spring Boot Actuator (endpoint /actuator/health):
@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean allHealthy = true;

        // Check Database
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            details.put("database", "UP");
        } catch (Exception e) {
            details.put("database", "DOWN: " + e.getMessage());
            allHealthy = false;
        }

        // Check Redis
        try {
            redisTemplate.opsForValue().set("health", "ok", 10, SECONDS);
            details.put("redis", "UP");
        } catch (Exception e) {
            details.put("redis", "DOWN: " + e.getMessage());
            allHealthy = false;
        }

        // Check downstream critical service
        try {
            bankApiClient.ping();
            details.put("bankApi", "UP");
        } catch (Exception e) {
            details.put("bankApi", "DEGRADED"); // Non-critical
            // Không fail health check vì bank có thể temporary down
        }

        return allHealthy
            ? Health.up().withDetails(details).build()
            : Health.down().withDetails(details).build();
        // DOWN → Load Balancer ngừng gửi traffic đến instance này
    }
}
```

### 4.3 🏋️ Kinh nghiệm Senior: LB Gotchas

**Gotcha: Graceful Shutdown khi Rolling Deploy**
```yaml
# application.yml
server:
  shutdown: graceful              # Không cắt request giữa chừng

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # Chờ tối đa 30s cho in-flight requests

# Kubernetes lifecycle hook:
# preStop → Sleep 10s → Cho LB kịp deregister instance trước khi shutdown
lifecycle:
  preStop:
    exec:
      command: ["/bin/sh", "-c", "sleep 10"]
```

---

## PHẦN 5: API Gateway

### 5.1 API Gateway vs Load Balancer

```
Load Balancer:
  "Distribute traffic thông minh"
  Biết: IP, Port, TCP connection state
  Không biết: HTTP headers, JWT token, business logic

API Gateway:
  "Smart entry point cho microservices"
  Biết tất cả L7 information:
  - URL path → route đến đúng service
  - Authorization header → validate JWT
  - User role → authorize request
  - Rate limit counter → throttle nếu cần
  - Request body → validate/transform
```

### 5.2 Spring Cloud Gateway — Production Config

```java
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

            // Public: Không cần auth
            .route("auth-route", r -> r
                .path("/api/v1/auth/**")
                .uri("lb://user-service"))

            // Payment: Auth + Rate limit + Circuit breaker
            .route("payment-route", r -> r
                .path("/api/v1/payments/**")
                .filters(f -> f
                    .filter(jwtAuthFilter())        // Validate JWT
                    .requestRateLimiter(rl -> rl    // Rate limit
                        .setRateLimiter(rateLimiter())
                        .setKeyResolver(userKeyResolver()))
                    .circuitBreaker(cb -> cb        // Circuit breaker
                        .setName("payment-cb")
                        .setFallbackUri("forward:/fallback"))
                    .retry(retry -> retry           // Retry on 5xx
                        .setRetries(2)
                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE))
                    .addRequestHeader("X-Correlation-Id",
                        UUID.randomUUID().toString()) // Tracing
                )
                .uri("lb://payment-service"))

            .build();
    }

    @Bean
    public RedisRateLimiter rateLimiter() {
        // replenishRate: tokens/s
        // burstCapacity: max tokens at once
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    public KeyResolver userKeyResolver() {
        // Rate limit theo userId (từ JWT)
        return exchange -> Mono.justOrEmpty(
            exchange.getRequest().getHeaders().getFirst("X-User-Id")
        ).defaultIfEmpty("anonymous");
    }
}
```

**JWT Auth Filter:**
```java
@Component
@Order(-1) // Chạy trước tất cả
public class JwtAuthFilter implements GlobalFilter {

    private static final Set<String> PUBLIC = Set.of(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (PUBLIC.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange); // Skip auth
        }

        String authHeader = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.validateAndExtract(token);

            // Inject decoded info vào header → Downstream không cần validate lại
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-User-Roles", String.join(",",
                    claims.get("roles", List.class)))
                .header("X-User-Email", claims.get("email", String.class))
                .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (ExpiredJwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
```

### 5.3 🏋️ Kinh nghiệm Senior: API Gateway Best Practices

**Best Practice #1: Downstream không validate JWT lại**
```java
// ❌ Lãng phí: Mỗi service validate JWT (expensive crypto operation)
@GetMapping("/transactions")
public List<Transaction> getTransactions(
        @RequestHeader("Authorization") String token) {
    Claims claims = jwtUtil.validate(token); // Validate lại lần 2!
    Long userId = claims.getSubject();
    ...
}

// ✅ Gateway đã validate, downstream chỉ đọc header:
@GetMapping("/transactions")
public List<Transaction> getTransactions(
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Roles") String roles) {
    // Tin tưởng Gateway đã validate, chỉ đọc injected headers
    return txnService.getByUser(userId);
}
```

**Best Practice #2: API Versioning**
```
URI Versioning (phổ biến nhất):
GET /api/v1/payments → Old API
GET /api/v2/payments → New API (breaking change)

Khi nào increment version?
→ Breaking changes: Xóa field, đổi field type, đổi endpoint path
→ Non-breaking: Thêm field, thêm endpoint → KHÔNG cần increment

Deprecation strategy:
1. Release v2
2. Maintain v1 thêm 6 tháng
3. Log warnings khi client gọi v1
4. Coordinate với clients để migrate
5. Sunset v1
```

---

## 🎯 TỔNG KẾT — Quyết định khi nào dùng gì

```
Request/Response đồng bộ, external API:      → REST/HTTP
Request/Response đồng bộ, internal service:  → gRPC (performance)
Fire-and-forget, event notification:          → Kafka (scale + replay)
Task queue, complex routing, delay:           → RabbitMQ (flexibility)
Single entry point, cross-cutting concerns:   → API Gateway
Traffic distribution, failover:               → Load Balancer
```

> 💡 **Final Senior Tip**: Trong một cuộc phỏng vấn, đừng chỉ liệt kê tools. Hãy nói về **trade-offs**: "Tôi chọn Kafka vì cần replay và multiple consumer groups, nhưng trade-off là complexity cao hơn RabbitMQ". Đây là tư duy của Senior engineer.
