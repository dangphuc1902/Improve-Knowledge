# 🎯 Momo Senior Engineer — FPM Project Interview Q&A

> **Vai trò:** Senior Engineer @ Momo đang phỏng vấn bạn  
> **Nguyên tắc:** Mọi câu trả lời đều có ví dụ cụ thể từ source code thực tế của dự án

---

## 📌 MỤC LỤC

1. [Architecture & Microservices](#1-architecture--microservices)
2. [API Gateway & Load Balancing](#2-api-gateway--load-balancing)
3. [JWT & Security](#3-jwt--security)
4. [Kafka & RabbitMQ](#4-kafka--rabbitmq)
5. [gRPC](#5-grpc)
6. [Redis & Caching](#6-redis--caching)
7. [Transaction Flow & Business Logic](#7-transaction-flow--business-logic)
8. [Câu hỏi bẫy / Deep Dive](#8-câu-hỏi-bẫy--deep-dive)

---

## 1. Architecture & Microservices

---

### ❓ Q1: Giới thiệu tổng quan kiến trúc dự án FPM của bạn?

**💬 Trả lời:**

FPM là ứng dụng **quản lý tài chính cá nhân** sử dụng **Cloud-Native Microservices Architecture**.

**Các services chính:**
| Service | Port | Vai trò |
|---------|------|--------|
| `api-gateway` | 8080 | Entry point duy nhất, JWT filter, Rate limit, Circuit Breaker |
| `user-auth-service` | 8081 | Authentication, JWT issue, Google OAuth2 |
| `wallet-service` | 8082 | Wallet CRUD, Balance management, gRPC server |
| `transaction-service` | 8083 | Transaction CRUD, publish Kafka event |
| `reporting-service` | 8084 | Reports, PDF/Excel/CSV, DDD, CQRS-lite |
| `notification-service` | 8085 | Firebase FCM push notification |

**Communication patterns:**
- **Synchronous:** REST (client → gateway → service), gRPC (service ↔ service)  
- **Asynchronous:** Kafka (transaction events → reporting), RabbitMQ (domain events)

**Infrastructure:** MySQL, Redis, Kafka, RabbitMQ, Eureka, Config Server

---

### ❓ Q2: Tại sao bạn dùng cả Kafka lẫn RabbitMQ? Dùng một cái thôi không được sao?

**💬 Trả lời:**

Đây là một **quyết định kiến trúc có chủ ý**, không phải "dùng cho có". Hai broker phục vụ hai nhu cầu khác nhau trong hệ thống:

---

#### 🔵 Kafka — Dùng cho Transaction Events → Reporting

**Kafka là gì?**  
Kafka là một **Event Streaming Platform** (nền tảng truyền phát sự kiện). Về bản chất, Kafka giống như một cuốn **nhật ký (log) phân tán** — mọi message được ghi vào đĩa và giữ lại trong một khoảng thời gian dài, không xóa ngay sau khi consumer đọc xong.

**Tại sao dùng Kafka cho transaction events?**

| Đặc điểm kỹ thuật | Giải thích bằng tiếng Việt | Tại sao quan trọng với FPM |
|---|---|---|
| **High Throughput** (thông lượng cao) | Kafka thiết kế để xử lý hàng triệu message/giây nhờ ghi dữ liệu tuần tự vào đĩa (log-structured) — tương tự ghi vào file thay vì random access | Giao dịch tài chính có thể xảy ra liên tục, cần hệ thống không bị nghẽn cổ chai |
| **Message Retention** (lưu trữ lâu dài) | Message **không bị xóa** sau khi consumer đọc xong. Có thể cấu hình giữ 7 ngày, 30 ngày, hoặc mãi mãi | Nếu reporting-service bị down, khi khởi động lại vẫn có thể **replay** (đọc lại) toàn bộ transaction events đã bỏ lỡ — không mất dữ liệu |
| **Offset-based consumption** (đọc theo vị trí) | Consumer tự quản lý vị trí đọc (offset). Có thể tua lại về bất kỳ thời điểm nào | Cho phép re-process lại dữ liệu khi cần tính toán lại báo cáo |
| **Consumer Group** (nhóm consumer) | Nhiều instance reporting-service cùng đọc từ một topic, mỗi partition chỉ do một instance xử lý → không duplicate | Scale reporting-service ngang mà không lo trùng dữ liệu |
| **Partition key = userId** | Message của cùng một user luôn vào cùng một partition | Đảm bảo **thứ tự xử lý** (ordering) cho từng user — transaction tạo trước phải được aggregate trước |

---

#### 🟠 RabbitMQ — Dùng cho Domain Events (Notification, Wallet Events)

**RabbitMQ là gì?**  
RabbitMQ là **Message Broker** truyền thống. Khác với Kafka, message trong RabbitMQ sẽ **bị xóa ngay sau khi consumer đọc và xác nhận (ACK)**. Điểm mạnh là hệ thống **Exchange → Routing → Queue** cực kỳ linh hoạt.

---

##### 📌 ACK (Acknowledgment) là gì? — Giải thích chi tiết

**ACK = "Acknowledgment" = Xác nhận đã nhận và xử lý xong.**

Hãy tưởng tượng bạn đặt đồ ăn qua app (shipper = consumer, nhà hàng = broker):

```
Nhà hàng (Broker) → Giao đơn hàng → Shipper (Consumer)
    └── Shipper nhận đơn → Xử lý (giao hàng) → Nhắn lại "Giao xong rồi" ← đây là ACK
    └── Broker nhận ACK → XÓA đơn đó khỏi hệ thống
```

**Trong RabbitMQ với code Java:**
```java
// Consumer (notification-service) nhận message từ RabbitMQ
@RabbitListener(queues = "notification.queue")
public void handleNotification(String message, Channel channel, 
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
    try {
        // Xử lý: gửi push notification qua Firebase
        firebaseService.sendPush(message);
        
        // ✅ GỬI ACK: "Tôi đã xử lý xong, broker có thể xóa message này"
        channel.basicAck(deliveryTag, false);
        
    } catch (Exception e) {
        // ❌ NACK: "Tôi xử lý THẤT BẠI, broker hãy gửi lại cho tôi"
        channel.basicNack(deliveryTag, false, true); // requeue=true
    }
}
```

**3 kịch bản có thể xảy ra:**

| Kịch bản | Điều gì xảy ra | Hệ quả |
|----------|---------------|--------|
| Consumer xử lý xong → gửi **ACK** | Broker xóa message | ✅ Bình thường |
| Consumer xử lý thất bại → gửi **NACK** | Broker đưa message về queue, gửi lại sau | ✅ Retry tự động |
| Consumer crash trước khi gửi ACK | Broker phát hiện connection đứt → tự đưa message lại queue | ✅ Không mất message |

> **Tóm lại:** ACK giống như việc ký nhận hàng. Nếu bạn chưa ký → người giao hàng biết là chưa nhận được → sẽ giao lại.

---

##### 📌 Exchange Types — Giải thích chi tiết bằng ví dụ thực tế

**Exchange là gì?** Exchange là "bộ định tuyến" (router) trong RabbitMQ. Producer không gửi message trực tiếp vào Queue — mà gửi vào Exchange, Exchange quyết định message đi đến Queue nào.

```
Producer → Exchange → (theo routing rule) → Queue 1
                                          → Queue 2
                                          → Queue 3
```

**Có 3 loại Exchange phổ biến:**

---

**1️⃣ Direct Exchange — "Bưu điện chính xác"**

> Giống như gửi thư có địa chỉ cụ thể. Thư chỉ đến đúng 1 người nhận.

```
Producer gửi: routing_key = "wallet.vietcombank"
                    ↓
              Direct Exchange
                    ↓
        Chỉ đến Queue có binding_key = "wallet.vietcombank"
```

**Dùng khi:** Mỗi loại event chỉ có đúng 1 handler xử lý. Ví dụ: email OTP chỉ đến email-service.

---

**2️⃣ Topic Exchange — "Bưu điện có wildcard"** ← FPM đang dùng cái này

> Giống như đặt báo: bạn đăng ký nhận "tất cả tờ báo về kinh tế" (`kinh-te.*`) thay vì từng tờ cụ thể.

Ký hiệu:
- `*` = thay cho đúng **1 từ**
- `#` = thay cho **0 hoặc nhiều từ**

```
Producer gửi: routing_key = "wallet.created"
                    ↓
              Topic Exchange
                    ↓
   ┌────────────────────────────────────────┐
   │ Queue "notification" → binding: wallet.*   ✅ MATCH (wallet.created)
   │ Queue "audit-log"   → binding: wallet.#   ✅ MATCH (wallet.created)
   │ Queue "report"      → binding: transaction.# ❌ NO MATCH
   └────────────────────────────────────────┘
```

**Ví dụ thực tế trong FPM:**
```java
// RabbitMQEventConfig.java — FPM dùng TopicExchange
@Bean
public TopicExchange walletExchange() {
    return new TopicExchange("wallet.exchange", true, false);
}

// Khi wallet được tạo:
rabbitTemplate.convertAndSend("wallet.exchange", "wallet.created", event);

// notification-service có thể bind: "wallet.*"  → nhận tất cả wallet events
// audit-service có thể bind:        "wallet.#"  → nhận kể cả "wallet.vib.created"
```

---

**3️⃣ Fanout Exchange — "Loa phóng thanh"**

> Giống như phát thanh viên đọc tin tức trên loa — tất cả người nghe (queue) đều nhận được, không cần routing key.

```
Producer gửi: (bất kỳ routing_key nào)
                    ↓
             Fanout Exchange
                    ↓
   ┌──────────────────────────────┐
   │ Queue "notification"  ✅     │
   │ Queue "audit-log"     ✅     │
   │ Queue "analytics"     ✅     │
   │ → TẤT CẢ đều nhận           │
   └──────────────────────────────┘
```

**Dùng khi:** Cần broadcast cùng 1 event đến nhiều service. Ví dụ: khi hệ thống maintenance, thông báo đến tất cả services.

---

**Tóm tắt 3 loại Exchange:**

| Exchange | Giống như | Routing | Use case |
|----------|-----------|---------|----------|
| **Direct** | Gửi thư có địa chỉ cụ thể | Exact key match | 1 event → 1 handler cố định |
| **Topic** | Đặt báo theo chủ đề | Wildcard (`*`, `#`) | 1 event → nhiều handler linh hoạt ← **FPM dùng** |
| **Fanout** | Loa phát thanh | Broadcast tất cả | 1 event → TẤT CẢ queues |

---

**Tại sao dùng RabbitMQ cho notification?**

| Đặc điểm kỹ thuật | Giải thích bằng tiếng Việt | Tại sao quan trọng với FPM |
|---|---|---|
| **Low Latency** (độ trễ thấp) | Message được push đến consumer ngay lập tức, không cần polling | Notification cần đến tay người dùng nhanh nhất có thể |
| **Flexible Routing** (định tuyến linh hoạt) | Dùng Topic Exchange để route message đến đúng queue dựa trên routing key pattern | Một sự kiện `wallet.created` có thể route đến cả notification queue lẫn audit queue — cùng lúc |
| **Exchange Types** | Direct (chính xác), Topic (wildcard), Fanout (broadcast) — như mô tả ở trên | Linh hoạt theo nghiệp vụ mà không cần code phức tạp |
| **Short-lived messages** (tin nhắn ngắn hạn) | Notification không cần lưu lại sau khi gửi — consume xong là hết vai trò | Tiết kiệm storage, phù hợp với use case "fire and forget" |
| **Consumer ACK** | Consumer xác nhận đã xử lý xong → broker xóa message. Nếu chưa ACK mà crash → broker gửi lại tự động | Đảm bảo notification không bị mất (xem giải thích ACK ở trên) |

---

#### 📊 So sánh tổng quan

| Tiêu chí | Kafka | RabbitMQ |
|----------|-------|----------|
| **Kiểu hệ thống** | Event Streaming (luồng sự kiện liên tục) | Message Broker (trung gian truyền tin) |
| **Lưu trữ message** | Lâu dài, có thể replay — đọc lại bất cứ lúc nào | Ngắn hạn — xóa ngay sau khi consumer ACK |
| **Thông lượng** | Rất cao — hàng triệu message/giây | Vừa phải — phù hợp với số lượng vừa và nhỏ |
| **Độ trễ** | Vài chục ms (do batch) | Rất thấp — gần như realtime |
| **Định tuyến** | Theo Topic + Partition key | Exchange/Queue/Routing Key — cực kỳ linh hoạt |
| **Use case trong FPM** | `transaction.created` → `reporting-service` tổng hợp báo cáo | `notification.*` → `notification-service` gửi push |

---

#### 💻 Code thực tế trong dự án

**`TransactionService.java`** — Nơi gửi cả Kafka và RabbitMQ:
```java
// Kafka: Publish event để reporting-service tổng hợp dữ liệu
// Key = userId → đảm bảo ordering: transaction của cùng 1 user vào cùng partition
kafkaTemplate.send("transaction.created", String.valueOf(userId), mapToResponse(saved));

// RabbitMQ: Gửi notification low-latency cho user biết giao dịch vừa thực hiện
// Exchange: "notification.exchange" → Routing key: "notification.routing.key"
rabbitTemplate.convertAndSend("notification.exchange", "notification.routing.key", msg);
```

**`KafkaProducerConfig.java`** — Cấu hình Kafka producer:
```java
config.put(ProducerConfig.ACKS_CONFIG, "all");              // Chờ tất cả replica confirm
config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Tránh duplicate message
config.put(ProducerConfig.LINGER_MS_CONFIG, 10);            // Batch 10ms để tăng throughput
config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Nén message để giảm size
```

**`RabbitMQEventConfig.java`** — Cấu hình Exchange:
```java
// FPM dùng TopicExchange → routing linh hoạt với wildcard
@Bean
public TopicExchange walletExchange() {
    return new TopicExchange("wallet.exchange", 
        true,   // durable = true: Exchange tồn tại sau khi RabbitMQ restart
        false); // autoDelete = false: không xóa Exchange khi không còn consumer
}
```

---

#### 🎯 Trả lời thẳng nếu interviewer hỏi "Dùng 1 cái không được à?"

> *"Được, hoàn toàn có thể dùng chỉ Kafka. Kafka thực tế đủ mạnh để thay thế cả hai. Nhưng trong dự án này, tôi chọn dùng cả hai để học và demo rõ ràng sự khác biệt về use case: Kafka cho streaming + analytics với retention dài, RabbitMQ cho domain events với routing linh hoạt và low-latency. Nếu production scale nhỏ, tôi sẽ consolidate về Kafka để đơn giản hóa infrastructure."*

---


### ❓ Q3: Service Discovery hoạt động thế nào trong hệ thống bạn?

**💬 Trả lời:**

Dùng **Spring Cloud Netflix Eureka**:

1. Mỗi service startup → đăng ký với Eureka Server (`:8761`) với service name (vd: `wallet-service`)
2. API Gateway dùng **client-side load balancing** với prefix `lb://`:
   ```java
   .uri("lb://wallet-service")  // RouteConfig.java
   ```
3. Spring Cloud LoadBalancer (mặc định với Spring Cloud 2022+) resolve `lb://wallet-service` → list IP:port thực của các instance
4. Nếu instance down → Eureka loại khỏi registry sau heartbeat timeout (default 90s)

---

### ❓ Q4: Spring Cloud Config Server hoạt động ra sao? Có nhược điểm gì không?

**💬 Trả lời:**

Config Server (`:8888`) sử dụng `native` profile, đọc YAML từ filesystem mount (`config/yml_service/`).

**Ưu điểm:** Thay đổi config không cần rebuild Docker image, override bằng env var dễ dàng.

**Nhược điểm tôi nhận thức được:**
- **Native profile** chỉ phù hợp dev/local — production nên dùng Git backend để có versioning, audit trail
- Không có encryption mặc định cho secret values (nên dùng Vault hoặc `{cipher}`)
- Service phải restart để pick up config mới (trừ khi dùng Spring Cloud Bus + `/actuator/refresh`)

---

## 2. API Gateway & Load Balancing

---

### ❓ Q5: API Gateway của bạn làm những gì? Tại sao không để từng service tự xử lý security?

**💬 Trả lời:**

Gateway (`api-gateway`) là **single entry point** thực hiện:

1. **JWT Authentication** — filter trước khi route request
2. **Rate Limiting** — dùng Redis Token Bucket qua `RedisRateLimiter`
3. **Circuit Breaker** — Resilience4j
4. **Load Balancing** — `lb://service-name`
5. **Header enrichment** — thêm `X-User-Id`, `X-User-Email` vào downstream

**Code từ `JwtAuthenticationFilter.java` (gateway):**
```java
// Sau khi validate JWT → inject userId vào header cho downstream services
ServerWebExchange modifiedExchange = exchange.mutate()
    .request(r -> r
        .header("X-User-Id", String.valueOf(userId))
        .header("X-User-Email", email))
    .build();
```

**Tại sao centralize?** Nếu để từng service tự handle JWT:
- Code duplicate → security bug dễ miss một service
- Khó enforce policy đồng nhất
- Cross-cutting concerns (logging, rate limit, CORS) phải duplicate mọi nơi

---

### ❓ Q6: Rate Limiting được implement thế nào? Tại sao dùng Redis?

**💬 Trả lời:**

Dùng **Redis Token Bucket** qua `RedisRateLimiter` của Spring Cloud Gateway:

```java
// RouteConfig.java
@Bean
public RedisRateLimiter loginRedisRateLimiter() {
    return new RedisRateLimiter(1, 5, 1); 
    // replenishRate=1 req/s, burstCapacity=5, requestedTokens=1
}

@Bean
public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(100, 120, 1); 
    // 100 req/s per user, burst up to 120
}
```

**Key resolver** dùng `X-User-Id` (sau auth) hoặc IP (trước auth):
```java
@Bean
public KeyResolver userKeyResolver() {
    return exchange -> Mono.just(
        exchange.getRequest().getHeaders().getFirst("X-User-Id") != null
            ? exchange.getRequest().getHeaders().getFirst("X-User-Id")
            : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
    );
}
```

**Tại sao Redis?** Stateless gateway → cần shared state. Redis atomic operations (INCR, EXPIRE) đảm bảo rate limit chính xác kể cả khi scale nhiều gateway instance.

---

### ❓ Q7: Circuit Breaker trong dự án cấu hình thế nào? Khi nào nó "mở"?

**💬 Trả lời:**

Dùng **Resilience4j** với cấu hình:
```java
// RouteConfig.java
CircuitBreakerConfig.custom()
    .slidingWindowSize(10)          // 10 requests gần nhất
    .minimumNumberOfCalls(5)        // Cần ít nhất 5 calls để tính
    .failureRateThreshold(50)       // >50% fail → OPEN
    .waitDurationInOpenState(Duration.ofSeconds(30)) // Đợi 30s trước HALF-OPEN
    .permittedNumberOfCallsInHalfOpenState(3)        // 3 calls thử nghiệm
    .build()
```

**3 trạng thái:**
- **CLOSED**: Bình thường, request đi qua
- **OPEN**: Service down, reject ngay → redirect `forward:/fallback`
- **HALF-OPEN**: Thử 3 request → nếu pass thì CLOSE lại, fail thì OPEN tiếp

**Tại sao quan trọng với Momo?** Khi payment service gặp sự cố, Circuit Breaker ngăn cascade failure, trả về fallback response thay vì timeout cả chain.

---

## 3. JWT & Security

---

### ❓ Q8: Giải thích JWT flow từ login đến request thông thường trong dự án của bạn?

**💬 Trả lời:**

**Bước 1 — Login:**
```
Client POST /api/v1/auth/login
→ user-auth-service validate credentials
→ BCrypt.matches(rawPassword, hashedPassword)
→ Tạo Access Token (HS512, exp: ngắn) + Refresh Token (exp: dài)
→ Trả về {accessToken, refreshToken}
```

**Bước 2 — Request thông thường:**
```
Client → Header: Authorization: Bearer {accessToken}
→ API Gateway JwtAuthenticationFilter (WebFlux/Reactive)
→ jwtTokenProvider.validateToken(token)
  - Verify signature HMAC-SHA512
  - Check expiration
  - Không trong blacklist Redis
→ Extract userId, email → inject X-User-Id header
→ Forward to microservice
→ Microservice JwtAuthenticationFilter (Servlet)
  - Re-validate token
  - Set SecurityContext với userId
→ Controller dùng userId từ SecurityContext
```

**Bước 3 — Token expired:**
```
Client POST /api/v1/auth/refresh với {refreshToken}
→ Validate refreshToken
→ Issue new accessToken
```

---

### ❓ Q9: Access Token và Refresh Token khác nhau thế nào trong code của bạn?

**💬 Trả lời:**

Từ `JwtTokenProvider.java`:

```java
// Access Token: chứa đầy đủ claims, expiration ngắn
public String generateAccessToken(Long userId, String email, Map<String, Object> additionalClaims) {
    claims.put("userId", userId);
    claims.put("email", email);
    claims.put("type", "ACCESS");    // ← đánh dấu type
    return Jwts.builder()
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
        .compact();
}

// Refresh Token: ít claims hơn, expiration dài hơn
public String generateRefreshToken(Long userId, String email) {
    return Jwts.builder()
        .claim("type", "REFRESH")    // ← chỉ có type, không có additionalClaims
        .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
        .compact();
}
```

**Phân biệt quan trọng:**
- Access Token: short-lived (vd: 15 phút), dùng để auth API
- Refresh Token: long-lived (vd: 7 ngày), chỉ dùng để xin Access Token mới
- Logout → Blacklist Access Token trong Redis theo `getRemainingExpiration()`

---

### ❓ Q10: Tại sao cả API Gateway lẫn microservice đều validate JWT? Double validation có cần thiết không?

**💬 Trả lời:**

**Có, cần thiết — Defense in Depth.**

**Gateway** (`JwtAuthenticationFilter.java` - WebFlux):
- First line of defense, reject unauthorized request sớm
- Inject `X-User-Id` header cho downstream

**Microservice** (`JwtAuthenticationFilter.java` - Servlet trong fpm-security lib):
```java
// Scenario A: user-auth-service có UserDetailsService thật
if (userDetailsService != null && !(userDetailsService instanceof InMemoryUserDetailsManager)) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    // Set full authentication với DB-loaded authorities
}

// Scenario B: Stateless microservices (wallet, transaction, reporting)
Long userId = jwtTokenProvider.extractUserId(token);
String role = (String) jwtTokenProvider.extractClaims(token).get("role");
// Set authentication chỉ từ token claims, không hit DB
```

**Tại sao stateless ở microservice?** Không cần DB call mỗi request. userId đã đáng tin vì Gateway đã validate signature, microservice chỉ cần parse claims.

**Rủi ro nếu bỏ microservice validation:** Internal service-to-service bypass → attacker gọi trực tiếp vào port 8082 mà không qua Gateway.

---

### ❓ Q11: Logout được xử lý thế nào? JWT stateless thì làm sao invalidate?

**💬 Trả lời:**

JWT stateless nên không thể "xóa" token server-side. Giải pháp: **Token Blacklist in Redis**.

**Flow:**
1. Client gọi `POST /api/v1/auth/logout` với token trong header
2. Server extract token, tính remaining TTL:
   ```java
   public long getRemainingExpiration(String token) {
       Date expirationDate = extractClaims(token).getExpiration();
       return Math.max(0, expirationDate.getTime() - System.currentTimeMillis());
   }
   ```
3. Lưu token vào Redis với TTL = remaining expiration:
   ```java
   redisTemplate.opsForValue().set("blacklist:" + token, "true", remainingTtl, TimeUnit.MILLISECONDS);
   ```
4. Mỗi request: sau khi validate signature → check Redis blacklist
   ```java
   Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + token);
   if (Boolean.TRUE.equals(isBlacklisted)) reject();
   ```

**Business rule BR-AUTH-06:** *"Logout → token blacklist vào Redis"*

---

### ❓ Q12: HMAC-SHA512 vs RSA — dự án bạn dùng cái nào, tại sao?

**💬 Trả lời:**

Dự án dùng **HMAC-SHA512** (`SignatureAlgorithm.HS512`):
```java
.signWith(getSigningKey(), SignatureAlgorithm.HS512)
// Key = HMAC từ secret string
Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8))
```

**So sánh:**
| | HMAC-SHA512 | RSA |
|--|-------------|-----|
| **Key** | Symmetric (1 key sign & verify) | Asymmetric (private sign, public verify) |
| **Use case** | Internal system, chỉ mình ta sign và verify | External parties cần verify token |
| **Performance** | Nhanh hơn | Chậm hơn (RSA operations tốn CPU) |
| **FPM phù hợp** | ✅ Tất cả services dùng chung secret | ❌ Overkill |

**Khi nào nên dùng RSA?** Khi có **third-party** cần verify token (như Momo partner APIs) — họ cần public key nhưng không được biết private key.

---

## 4. Kafka & RabbitMQ

---

### ❓ Q13: Giải thích Kafka configuration trong dự án — tại sao `acks=all` và `enable.idempotence=true`?

**💬 Trả lời:**

Từ `KafkaProducerConfig.java`:
```java
config.put(ProducerConfig.ACKS_CONFIG, "all");              // acks=all
config.put(ProducerConfig.RETRIES_CONFIG, 3);               // retry 3 lần
config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // idempotent producer
config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);        // 16KB batch
config.put(ProducerConfig.LINGER_MS_CONFIG, 10);            // đợi 10ms để batch
config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // compress
```

**`acks=all` (strongest durability):**
- Producer phải đợi ALL in-sync replicas (ISR) confirm write
- Đảm bảo message không bị mất kể cả broker leader fail
- Trade-off: latency cao hơn `acks=1`

**`enable.idempotence=true`:**
- Kafka gán sequence number mỗi message
- Nếu producer retry → broker biết là duplicate → bỏ qua
- Đảm bảo **exactly-once** ở producer side (không duplicate message)

**Kết hợp với `retries=3`:** Nếu network blip, producer retry an toàn vì có idempotence.

**Trong domain Momo:** transaction.created event phải không được duplicate (tránh double-count report) và không được mất (reporting phải có đủ data).

---

### ❓ Q14: Consumer group `reporting-group` hoạt động thế nào? Nếu có 2 instance reporting-service thì sao?

**💬 Trả lời:**

Từ `KafkaTransactionConsumer.java`:
```java
@KafkaListener(
    topics = {"transaction.created", "transaction.updated", "transaction.deleted"}, 
    groupId = "reporting-group"
)
```

**Consumer Group mechanics:**
- Kafka chia partitions của topic cho các consumers trong cùng group
- Nếu `transaction.created` topic có 3 partitions và 2 reporting instances:
  - Instance 1: partition 0, partition 1
  - Instance 2: partition 2
- Mỗi partition chỉ được consume bởi 1 consumer trong group → **không duplicate**

**Scale strategy tốt nhất:** Số consumer instances ≤ số partitions. Nếu instances > partitions → một số idle.

**Ordering:** Message cùng `userId` (partition key) luôn được process theo thứ tự → correct aggregation.

---

### ❓ Q15: RabbitMQ dùng Exchange type gì? Tại sao dùng TopicExchange?

**💬 Trả lời:**

Từ `RabbitMQEventConfig.java`:
```java
@Bean
public TopicExchange walletExchange() {
    return new TopicExchange(walletExchange, true, false);
    // durable=true, autoDelete=false
}
```

**4 Exchange types:**
| Type | Routing | Use case |
|------|---------|----------|
| **Direct** | Exact routing key match | Đơn giản, 1-to-1 |
| **Topic** | Pattern matching (`*`, `#`) | Flexible, FPM dùng |
| **Fanout** | Broadcast tất cả queues | Pub/Sub |
| **Headers** | Match theo header | Rare |

**Tại sao Topic?** Routing key pattern linh hoạt:
- `wallet.created` → notification queue
- `wallet.updated` → audit queue  
- `wallet.*` → catch tất cả wallet events
- `#` → catch mọi event

**Config notification trong code:**
```java
rabbitTemplate.convertAndSend("notification.exchange", "notification.routing.key", msg);
```

---

### ❓ Q16: Vấn đề gì có thể xảy ra nếu Kafka down khi createTransaction? Bạn xử lý thế nào?

**💬 Trả lời:**

Nhìn vào `TransactionService.java`:
```java
@Transactional
public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
    // 1. gRPC update balance (SYNC - nếu fail thì throw exception)
    WalletResponse walletResponse = walletGrpcStub.updateBalance(balanceRequest);
    
    // 2. Save to DB (trong @Transactional)
    TransactionEntity saved = transactionRepository.save(entity);
    
    // 3. Publish Kafka event (ASYNC - catch exception, không rethrow!)
    publishKafkaEvent("transaction.created", userId, saved);
    
    // 4. Send RabbitMQ notification (ASYNC - catch exception)
    sendNotification(userId, request.getType(), ...);
    
    return mapToResponse(saved);
}

private void publishKafkaEvent(String topic, Long userId, TransactionEntity saved) {
    try {
        kafkaTemplate.send(topic, ...);
    } catch (Exception e) {
        log.error("Kafka: Failed to publish {} event", topic, e); // ← chỉ log, không throw
    }
}
```

**Vấn đề hiện tại:** Kafka down → event bị mất → reporting không được update.

**Giải pháp tốt hơn:**
1. **Outbox Pattern:** Save event vào bảng `outbox_events` trong cùng DB transaction, có background job poll và publish
2. **Dead Letter Queue (DLQ):** Kafka config DLQ, retry failed messages
3. **Transactional Outbox với Debezium:** CDC từ outbox table → Kafka

**Đây là trade-off design có ý thức:** Ưu tiên availability của transaction core, report có thể lag nhưng eventually consistent.

---

## 5. gRPC

---

### ❓ Q17: Tại sao dùng gRPC cho Transaction → Wallet thay vì REST?

**💬 Trả lời:**

**gRPC advantages trong context này:**

1. **Type safety:** Proto contract enforce API
   ```protobuf
   // Thay vì JSON tự do, phải đúng schema
   message UpdateBalanceRequest {
     int64 wallet_id = 1;
     Money amount = 2;
     string operation = 3;  // "ADD" | "SUBTRACT"
   }
   ```

2. **Performance:** Protobuf binary ~5x nhỏ hơn JSON. Với `updateBalance` được gọi mỗi transaction, điều này quan trọng.

3. **Strongly-typed error:** gRPC status codes rõ ràng (NOT_FOUND, PERMISSION_DENIED, FAILED_PRECONDITION)

4. **Bi-directional streaming sẵn sàng:** Nếu cần realtime wallet balance stream

**Code thực tế** (`TransactionService.java`):
```java
// Khởi tạo blocking stub (synchronous)
this.walletGrpcStub = WalletGrpcServiceGrpc.newBlockingStub(
    ManagedChannelBuilder.forTarget(address)
        .usePlaintext()
        .build()
);

// Gọi synchronous - đợi response
WalletResponse walletResponse = walletGrpcStub.updateBalance(balanceRequest);
```

**Tại sao synchronous (BlockingStub)?** Cần biết ngay balance update thành công/thất bại trước khi save transaction. Nếu async → race condition.

---

### ❓ Q18: WalletServiceGrpcImpl handle race condition thế nào khi 2 transactions cùng lúc?

**💬 Trả lời:**

Nhìn vào `WalletServiceGrpcImpl.java`:
```java
@Override
public void updateBalance(UpdateBalanceRequest request, StreamObserver<WalletResponse> responseObserver) {
    WalletEntity wallet = walletRepository.findById(request.getWalletId())
        .orElseThrow(() -> new RuntimeException("Wallet not found"));

    if ("SUBTRACT".equalsIgnoreCase(request.getOperation())) {
        if (wallet.getBalance().compareTo(change) < 0) {
            throw new RuntimeException("Insufficient balance"); // ← BR-TXN-02
        }
        wallet.setBalance(wallet.getBalance().subtract(change));
    }
    walletRepository.save(wallet);
```

**Vấn đề hiện tại:** Đây là **Lost Update problem**!
- Thread A đọc balance = 1000
- Thread B đọc balance = 1000
- Thread A subtract 500 → save 500
- Thread B subtract 700 → check 1000 >= 700 OK → save 300 ← **SAI! Đúng là -200**

**Giải pháp tốt hơn:**
1. **Optimistic Locking:** Thêm `@Version` vào WalletEntity → JPA throw OptimisticLockException nếu version mismatch
2. **Pessimistic Locking:** `@Lock(LockModeType.PESSIMISTIC_WRITE)` trên repository query
3. **DB-level atomic update:** `UPDATE wallet SET balance = balance - ? WHERE id = ? AND balance >= ?`

> **⚠️ Đây là câu hỏi hay nhất Momo sẽ hỏi — hãy chủ động nêu vấn đề này!**

---

### ❓ Q19: Protobuf và JSON khác nhau thế nào? Khi nào dùng cái nào?

**💬 Trả lời:**

| Tiêu chí | Protobuf | JSON |
|----------|---------|------|
| **Format** | Binary | Text |
| **Size** | ~3-5x nhỏ hơn | Lớn hơn |
| **Schema** | Bắt buộc (.proto) | Optional |
| **Readability** | Không đọc được | Human readable |
| **Speed** | Nhanh hơn serialize/deserialize | Chậm hơn |
| **Backward compat** | Tốt (field number) | Cần care |

**FPM dùng Protobuf cho gRPC** (service-to-service internal):
```protobuf
message Money {
  double amount = 1;
  string currency = 2;
}
```

**Dùng JSON cho REST** (client-facing API) — human readable, dễ debug, tooling phong phú.

**Rule of thumb:** Internal microservice communication → Protobuf/gRPC. External API → REST/JSON.

---

## 6. Redis & Caching

---

### ❓ Q20: Redis được dùng mấy mục đích trong dự án? Giải thích từng cái?

**💬 Trả lời:**

Redis (`fpm-redis:6379`) được dùng **3 mục đích** chính:

**1. Token Blacklist (BR-AUTH-06):**
```java
// Key: "blacklist:{token}", TTL = remaining token expiration
redisTemplate.opsForValue().set("blacklist:" + token, "true", remainingTtl, MILLISECONDS);
```

**2. Report Caching (BR-REPORT-03: 5 phút):**
```java
// ReportingService.java dùng @Cacheable
@Cacheable(value = "reports", key = "#userId + ':' + #yearMonth")
public ReportResponse getMonthlyReport(Long userId, String yearMonth) { ... }
```

**3. Rate Limiting:**
```java
// RouteConfig.java - RedisRateLimiter (Token Bucket)
new RedisRateLimiter(100, 120, 1)
// Redis giữ state: tokens remaining per userId
```

**Bonus - KafkaTransactionConsumer:**
```java
@CacheEvict(value = "dashboard", allEntries = true) // Evict khi có new transaction
public void consumeTransactionEvent(Object event) { ... }
```

**Config từ `RedisConfig.java`:**
```java
// Key: StringRedisSerializer (human-readable)
// Value: GenericJackson2JsonRedisSerializer (JSON với type info)
template.setKeySerializer(new StringRedisSerializer());
template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
```

---

### ❓ Q21: Cache invalidation trong dự án xử lý thế nào? Có vấn đề gì không?

**💬 Trả lời:**

**Hiện tại:**
- Report cache: TTL 5 phút tự hết hạn (time-based)
- Dashboard cache: `@CacheEvict` khi Kafka nhận transaction event

```java
// KafkaTransactionConsumer.java
@KafkaListener(topics = {"transaction.created", "transaction.updated", "transaction.deleted"})
@CacheEvict(value = "dashboard", allEntries = true) // ← Evict ALL dashboard cache
public void consumeTransactionEvent(Object event) { ... }
```

**Vấn đề với `allEntries = true`:**
- Evict cache của TẤT CẢ users khi BẤT KỲ user nào có transaction mới
- Nên evict theo `userId` cụ thể:
  ```java
  @CacheEvict(value = "dashboard", key = "#event.userId")
  ```

**Cache Stampede problem:** Khi cache expire → nhiều request cùng hit DB → DB overload
- Giải pháp: Distributed lock (Redisson), hoặc probabilistic early expiration

---

### ❓ Q22: Redis serialization trong dự án dùng gì? Tại sao không dùng default?

**💬 Trả lời:**

Từ `RedisConfig.java`:
```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new JavaTimeModule());         // Java 8 date/time support
objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 string
objectMapper.activateDefaultTyping(                        // ← Type information
    objectMapper.getPolymorphicTypeValidator(),
    ObjectMapper.DefaultTyping.NON_FINAL
);

GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
```

**Tại sao không dùng default JdkSerializationRedisSerializer?**
- Default serialize Java objects → binary không readable
- Version-sensitive (class change → deserialization fail)
- Không portable (chỉ Java đọc được)

**Tại sao cần `activateDefaultTyping`?**
- Redis lưu JSON string, khi deserialize không biết exact type
- Type info được embed vào JSON: `{"@class":"com.fpm.TransactionResponse", ...}`
- Thiếu → deserialize về `LinkedHashMap` thay vì đúng class

**Trade-off:** Type info làm JSON lớn hơn → chấp nhận được vì Redis là internal cache.

---

## 7. Transaction Flow & Business Logic

---

### ❓ Q23: Walk me through luồng createTransaction từ client đến database, step by step?

**💬 Trả lời:**

```
1. Android Client 
   POST /api/v1/transactions
   Header: Authorization: Bearer {accessToken}
   Body: {walletId, amount, type: "EXPENSE", categoryId, description}

2. API Gateway (port 8080)
   - JwtAuthenticationFilter: validate token signature + expiry
   - Rate limit check (Redis Token Bucket, 100 req/min)
   - Inject X-User-Id: 123 vào header
   - Route đến transaction-service (lb://transaction-service)

3. transaction-service (port 8083)
   - JwtAuthenticationFilter (stateless): set SecurityContext
   - TransactionController → TransactionService.createTransaction(userId=123, request)

4. gRPC call đến wallet-service (SYNCHRONOUS - phải thành công trước)
   walletGrpcStub.updateBalance({walletId, amount: 100000, operation: "SUBTRACT"})
   
   wallet-service WalletServiceGrpcImpl:
   - Load wallet entity
   - Check balance >= 100000 (BR-TXN-02)
   - wallet.balance -= 100000
   - Save wallet → MySQL commit
   - Return WalletResponse

5. Save transaction to MySQL (trong @Transactional)
   entity.status = COMPLETED
   transactionRepository.save(entity)

6. Publish async events (không block response):
   - Kafka: "transaction.created" topic, key=userId
   - RabbitMQ: "notification.exchange" → notification-service

7. Return 201 Created {transactionId, amount, status: "COMPLETED"}

8. reporting-service consume Kafka event (async):
   - @CacheEvict(dashboard)
   - Update aggregate stats
```

---

### ❓ Q24: Điều gì xảy ra nếu gRPC update balance thành công nhưng save transaction vào DB thất bại?

**💬 Trả lời:**

**Đây là Distributed Transaction problem!**

Nhìn code:
```java
@Transactional
public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
    // Step 1: gRPC update wallet balance (NGOÀI @Transactional của service này!)
    WalletResponse walletResponse = walletGrpcStub.updateBalance(balanceRequest);
    
    // Step 2: Save transaction (TRONG @Transactional)
    TransactionEntity saved = transactionRepository.save(entity);
```

**Scenario thất bại:**
- Balance đã bị trừ ở wallet-service (committed trong MySQL của wallet-service)
- `transactionRepository.save()` throw exception
- `@Transactional` rollback transaction-service DB
- **Kết quả:** Balance bị trừ nhưng không có transaction record → **money lost!**

**Giải pháp enterprise:**

**1. Saga Pattern (Choreography):**
- Mỗi step publish event
- Nếu thất bại → publish compensating event (revert balance)

**2. Saga Pattern (Orchestration):**
- Có Saga Orchestrator điều phối steps
- Explicit rollback nếu step nào fail

**3. Dự án đã có partial solution:**
```java
private void revertWalletBalance(TransactionEntity entity) {
    // Khi deleteTransaction → revert balance
    String revertOp = entity.getType() == CategoryType.EXPENSE ? "ADD" : "SUBTRACT";
    walletGrpcStub.updateBalance(req); // Compensating transaction
}
```

**Trả lời thẳng:** Đây là known limitation. Production system cần Saga pattern với compensating transactions hoặc dùng 2PC (Two-Phase Commit) nếu dùng distributed transaction manager.

---

### ❓ Q25: updateTransaction xử lý balance change thế nào?

**💬 Trả lời:**

```java
@Transactional
public TransactionResponse updateTransaction(Long userId, Long transactionId, UpdateTransactionRequest request) {
    // Kiểm tra balance thay đổi
    boolean balanceChanged = (request.getAmount() != null && !request.getAmount().equals(entity.getAmount()))
            || (request.getType() != null && request.getType() != entity.getType());

    if (balanceChanged) {
        // 1. Revert old balance (compensating)
        revertWalletBalance(entity);      // ADD back old amount
        
        // 2. Apply new balance
        applyWalletBalance(entity.getWalletId(), 
            request.getAmount() != null ? request.getAmount() : entity.getAmount(),
            request.getType() != null ? request.getType() : entity.getType(),
            ...);
    }
    // 3. Update entity fields
    // 4. Publish transaction.updated to Kafka
}
```

**Ví dụ:** User sửa giao dịch từ EXPENSE 100k → EXPENSE 200k:
1. `revertWalletBalance` → ADD lại 100k (balance tăng)
2. `applyWalletBalance` → SUBTRACT 200k (balance giảm)
3. Net effect: balance giảm 100k

**Điểm yếu:** Hai gRPC calls riêng biệt → window of inconsistency giữa 2 calls.

---

## 8. Câu hỏi bẫy / Deep Dive

---

### ❓ Q26: 🪤 JWT của bạn dùng `HS512` với secret là String — vấn đề bảo mật nào ở đây?

**💬 Trả lời (chủ động nhận ra vấn đề):**

```java
private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
}
```

**Vấn đề 1 — Key length:** 
- HS512 cần secret ≥ 512 bits (64 bytes)
- Nếu secret ngắn (vd: "mySecret123") → `Keys.hmacShaKeyFor` throw WeakKeyException
- Cần secret ≥ 64 ký tự strong random

**Vấn đề 2 — Key rotation:**
- Hiện tại không có key rotation → nếu secret bị lộ, toàn bộ tokens valid là bị compromise
- Giải pháp: JWT `kid` (key ID) header, support multiple keys, rotation định kỳ

**Vấn đề 3 — Secret in config:**
- Secret nên ở environment variable hoặc HashiCorp Vault, KHÔNG hard-code trong yml
- Spring Cloud Config với `{cipher}` encryption, hoặc Kubernetes secrets

**Vấn đề 4 — Token trong header:**
- `Bearer` token có thể bị steal từ browser memory nếu XSS
- Với mobile app thì an toàn hơn (secure storage)

---

### ❓ Q27: 🪤 `@Transactional(readOnly = true)` ở class `ReportingService` có nghĩa gì? Sẽ ra sao khi method có `@Transactional` (không readOnly)?

**💬 Trả lời:**

```java
@Service
@Transactional(readOnly = true)  // ← class level
public class ReportingService {

    @Transactional  // ← method level, override class level
    public ReportResponse generateMonthlyReport(ReportRequest request) {
        // Có write operations (reportRepository.save)
    }
    
    // Các method khác: readOnly = true (từ class)
    public ReportResponse getMonthlyReport(...) { ... }  // read-only
}
```

**`readOnly = true` tác dụng:**
1. **Performance hint cho Hibernate:** Disable dirty checking → không scan entities để detect changes → nhanh hơn
2. **MySQL routing:** Read-only transaction có thể route đến read replica
3. **Prevent accidental writes:** Một số JPA providers throw exception nếu try write trong readOnly transaction

**Khi `@Transactional` không có `readOnly`:** Override class-level → default `readOnly=false` → full read-write transaction.

---

### ❓ Q28: 🪤 Circuit Breaker của bạn `failureRateThreshold(50)` — 50% failure là gì? Nếu chỉ có 4 requests, 2 fail → circuit mở không?

**💬 Trả lời:**

```java
CircuitBreakerConfig.custom()
    .slidingWindowSize(10)
    .minimumNumberOfCalls(5)    // ← đây là key
    .failureRateThreshold(50)
```

**`minimumNumberOfCalls(5)` = safety guard:**
- Circuit chỉ tính failure rate sau khi có ít nhất 5 calls
- Với 4 requests, 2 fail → **circuit KHÔNG mở** (chưa đủ minimum)
- Phải có ≥ 5 calls trong sliding window mới evaluate

**Sliding window (10 calls):**
- COUNT_BASED: track 10 calls gần nhất
- Nếu 5/10 calls fail → 50% → OPEN

**Tại sao `minimumNumberOfCalls`?** Tránh false positive — startup thời điểm ít traffic mà 1-2 calls timeout không nên mở circuit.

---

### ❓ Q29: 🪤 Tại sao `KafkaTransactionConsumer` dùng `allEntries = true` cho CacheEvict? Đây có phải good practice không?

**💬 Trả lời:**

```java
@KafkaListener(topics = {"transaction.created", "transaction.updated", "transaction.deleted"}, 
               groupId = "reporting-group")
@CacheEvict(value = "dashboard", allEntries = true)
public void consumeTransactionEvent(Object event) {
    log.info("Received transaction event, evicting dashboard cache...");
}
```

**Vấn đề:**
1. **`allEntries = true`** evict cache của TẤT CẢ users — quá aggressive
2. Mỗi transaction event → flush toàn bộ dashboard cache → cache miss storm nếu traffic cao
3. `Object event` không typed → không biết `userId` → không thể targeted eviction

**Cải thiện:**
```java
@KafkaListener(topics = {"transaction.created", "transaction.updated", "transaction.deleted"})
public void consumeTransactionEvent(TransactionEvent event) {
    // Evict chỉ cache của user đó
    cacheManager.getCache("dashboard").evict(event.getUserId());
    cacheManager.getCache("reports").evict(event.getUserId() + ":*");
}
```

**Note:** Đây là tôi **nhận thức được technical debt** và sẽ refactor. Trong dự án học tập thì acceptable để demo concept.

---

### ❓ Q30: 🎯 Nếu Momo hire bạn và yêu cầu làm thật sự production-ready, bạn sẽ cải thiện gì đầu tiên?

**💬 Trả lời:**

**Ưu tiên theo impact và risk:**

1. **🔴 Critical — Distributed Transaction:** Implement Saga Pattern với compensating transactions để tránh money lost khi gRPC balance update + DB save mất đồng bộ

2. **🔴 Critical — Concurrency:** Thêm Optimistic/Pessimistic locking cho wallet balance update để tránh race condition

3. **🟡 High — Outbox Pattern:** Đảm bảo Kafka events không bị mất khi broker down (lưu event vào DB trước, có background job publish)

4. **🟡 High — Secret Management:** Move JWT secret và DB credentials ra Vault/K8s secrets

5. **🟢 Medium — KafkaTransactionConsumer:** Type-safe event consumption + targeted cache eviction per userId

6. **🟢 Medium — Observability:** Distributed tracing (OpenTelemetry) để trace request across services

7. **🔵 Nice-to-have:** JWT key rotation, Config Server Git backend, Service Mesh (Istio) cho mTLS

---

## 💡 Lời khuyên thêm cho Interview

### Cách mở đầu ấn tượng:
> *"Dự án FPM là hệ thống quản lý tài chính cá nhân mà tôi tự thiết kế từ đầu với microservices architecture. Điều tôi tự hào nhất không phải là cái gì hoạt động đúng, mà là tôi hiểu rõ những gì chưa production-ready và tại sao."*

### Khi bị hỏi về điểm yếu:
- **Chủ động nêu** race condition trong WalletServiceGrpcImpl
- **Chủ động nêu** Distributed Transaction problem 
- **Chủ động nêu** missing Outbox pattern
- Momo thích engineers có **self-awareness** hơn là bảo vệ code mù quáng

### Câu hỏi nên hỏi ngược Momo:
- "Momo xử lý distributed transaction trong payment flow thế nào? Saga hay 2PC?"
- "Tech stack hiện tại của team là gì? Có dùng gRPC giữa services không?"
- "Làm sao team handle idempotency khi user double-click nút thanh toán?"

---

*📌 Artifact được tạo ngày 2026-07-26 | Dựa trực tiếp từ source code FPM Project*
