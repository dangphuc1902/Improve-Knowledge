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

**Bài học #1 — Connection Pool là BẮT BUỘC:**

```java
// ❌ SAI: Tạo RestTemplate mới mỗi request → Không có connection pooling
@Service
public class PaymentService {
    public void callBankApi() {
        RestTemplate rt = new RestTemplate(); // Tạo mới mỗi lần!
        rt.postForObject(url, request, Response.class);
    }
}

// ✅ ĐÚNG: Singleton Bean với Apache HttpClient connection pool
@Configuration
public class HttpConfig {
    @Bean
    public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager cm = 
            new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);           // Tổng tối đa 200 connections
        cm.setDefaultMaxPerRoute(50);  // 50 connections per host

        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(2000)   // 2s connect timeout
            .setSocketTimeout(5000)    // 5s read timeout
            .build();

        HttpClient client = HttpClients.custom()
            .setConnectionManager(cm)
            .setDefaultRequestConfig(config)
            .build();

        return new RestTemplate(
            new HttpComponentsClientHttpRequestFactory(client));
    }
}
```

> 💡 **Senior insight**: Thiếu connection pool → khi traffic tăng → "Connection refused" hoặc connection leak → service crash. Đây là bug phổ biến nhất của junior dev khi làm microservices.

**Bài học #2 — Timeout phải set ở MỌI nơi:**

```
Scenario không có timeout:
Payment Service gọi Bank API → Bank API bị slow
→ Payment Service thread bị block vô hạn
→ 100 requests: Thread pool exhausted (default 200 threads)
→ Mọi request mới bị từ chối → Cascading failure!

→ Luôn set: connectTimeout + readTimeout + circuit breaker timeout
```

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
