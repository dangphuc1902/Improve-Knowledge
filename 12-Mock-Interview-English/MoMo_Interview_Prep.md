# 🎯 MoMo Interview Prep — Java Backend Developer
> **Vai trò interviewer**: Senior Software Engineer @ MoMo  
> **Ứng viên**: Đặng Trọng Phúc — Java Backend Developer  
> **Trọng tâm**: Microservices, Caching, Security, Transaction Handling  
> **Cập nhật**: 2026-07-24

---

## 📌 TỔNG QUAN PHỎNG VẤN MOMO

MoMo phỏng vấn theo cấu trúc:
1. **Round 1 – Technical Screen** (45–60 phút): Java Core + Spring + DB cơ bản
2. **Round 2 – Deep Technical** (60–90 phút): Microservices, Security, System Design
3. **Round 3 – System Design + Behavioral** (60 phút): Thiết kế hệ thống Payment + STAR stories

> **Lưu ý MoMo cụ thể**: Họ rất hay hỏi về xử lý giao dịch (transaction), bảo mật request, và kinh nghiệm với distributed system trong bối cảnh fintech.

---

## 🗂️ MODULE 1: MICROSERVICES CƠ BẢN

### 1.1 HTTP & REST

#### ❓ Câu hỏi phỏng vấn thường gặp:

**Q1**: *"Trong project của bạn, bạn design API như thế nào? Bạn có follow chuẩn REST không, và nếu có thì bạn handle versioning ra sao?"*

**Cách trả lời chuẩn:**
```
✅ Mention: RESTful resource naming (/api/v1/transactions/{id})
✅ Mention: HTTP verbs semantics (GET idempotent, POST không, PUT idempotent)
✅ Mention: Status codes đúng (200/201/400/401/403/404/409/500)
✅ Mention: Versioning strategy (URI versioning vs Header versioning)
✅ Liên hệ CV: "Ở dự án [X], tôi design API thanh toán theo chuẩn REST với versioning qua URI..."
```

**Q2**: *"HTTP/1.1 vs HTTP/2 khác nhau gì? Tại sao microservices nên dùng HTTP/2?"*

| Đặc điểm | HTTP/1.1 | HTTP/2 |
|---|---|---|
| Connection | 1 request/connection | Multiplexing nhiều request |
| Header | Text, lặp lại | HPACK compression |
| Priority | Không có | Có stream priority |
| Server Push | Không | Có |
| Performance | HOL Blocking | Giải quyết HOL Blocking |

**Trả lời nhanh**: HTTP/2 dùng binary protocol thay text, multiplexing giúp 1 TCP connection xử lý nhiều request song song → quan trọng trong microservices khi service gọi nhau liên tục.

---

### 1.2 gRPC

#### ❓ Câu hỏi phỏng vấn thường gặp:

**Q3**: *"Bạn đã dùng gRPC chưa? So sánh gRPC vs REST khi nào nên dùng cái nào?"*

**Trả lời chuẩn:**

| Tiêu chí | REST/JSON | gRPC/Protobuf |
|---|---|---|
| Payload | Text (JSON) nặng | Binary nhẹ hơn ~5-7x |
| Schema | Không bắt buộc | Contract-first (`.proto`) |
| Streaming | Không (trừ SSE/WS) | Bidirectional streaming native |
| Browser support | Tốt | Kém (cần grpc-web) |
| Use case | Public API, mobile | Internal service-to-service |

**Khi nào dùng gRPC tại MoMo context:**
- Internal: Payment Service → Risk Service (low latency, cần schema contract)
- Khi cần streaming: Real-time transaction status updates

**Q4**: *"Protobuf là gì? Tại sao nó nhanh hơn JSON?"*
```
Protobuf dùng schema (.proto file) để define message structure.
Serialize sang binary thay vì text → nhỏ hơn ~3-10x, parse nhanh hơn
vì không cần parse string sang typed value.

Ví dụ: {"amount": 100000} = 16 bytes JSON
vs protobuf field 1 (int32) = 3-4 bytes
```

---

### 1.3 Message Queue (RabbitMQ & Kafka)

#### ❓ Câu hỏi hay gặp nhất tại MoMo:

**Q5**: *"Bạn giải thích cơ chế của Kafka không? Tại sao MoMo dùng Kafka thay vì RabbitMQ?"*

**RabbitMQ vs Kafka — Bảng so sánh:**

| Tiêu chí | RabbitMQ | Kafka |
|---|---|---|
| Mô hình | Push (broker push đến consumer) | Pull (consumer tự pull) |
| Message retention | Xóa sau khi đã ACK | Lưu theo retention period (days) |
| Ordering | Per queue | Per partition |
| Throughput | Medium (~50K msg/s) | Very high (~1M+ msg/s) |
| Use case | Task queue, RPC | Event streaming, audit log |
| Consumer group | Competing consumers | Independent consumer groups |
| Replay | Không | Có (offset management) |

**Tại sao MoMo dùng Kafka:**
- Cần audit log của mọi transaction (replay được)
- Throughput cao (hàng triệu giao dịch/ngày)
- Multiple consumer groups độc lập (Analytics service + Notification service cùng consume 1 topic)

**Q6**: *"Kafka partition là gì? Consumer group hoạt động như thế nào?"*

```
Partition: Kafka topic được chia thành nhiều partition (shards).
- Message trong cùng partition được ordered
- Mỗi partition chỉ được consume bởi 1 consumer trong 1 group tại 1 thời điểm

Consumer Group:
- Group A (Analytics): partition 0→consumer1, partition 1→consumer2
- Group B (Notification): partition 0→consumer3, partition 1→consumer4
→ 2 group độc lập, mỗi group nhận đủ tất cả messages

Rebalancing: Khi consumer join/leave group → Kafka phân phối lại partition
```

**Q7**: *"Làm sao đảm bảo exactly-once trong Kafka?"*
```
3 delivery semantics:
1. At-most-once: Commit offset trước khi process → có thể mất message
2. At-least-once: Process xong mới commit → có thể duplicate
3. Exactly-once: Dùng idempotent producer + transactional API

Trong fintech:
- Producer: enable.idempotence=true + transactional.id
- Consumer: isolation.level=read_committed
- Business logic: Idempotency key trên database side
```

**Q8**: *"Dead Letter Queue (DLQ) là gì? Bạn handle failed messages như thế nào?"*
```
DLQ: Queue/Topic chứa messages không thể xử lý được sau N lần retry.

Flow:
Message → Consumer → Failure → Retry (3 lần) → DLQ → Alert → Manual review

RabbitMQ: x-dead-letter-exchange + x-message-ttl
Kafka: Dùng separate topic "_dlq" + custom retry topic với backoff delay

Tại MoMo context: Transaction event fail → DLQ → alert team → 
manual reconciliation hoặc auto-retry sau khi fix bug
```

---

### 1.4 Load Balancer

**Q9**: *"Bạn hiểu Load Balancer như thế nào? Các thuật toán LB thường dùng?"*

```
Các thuật toán Load Balancing:

1. Round Robin: Request đến server theo vòng tròn
   → Tốt khi servers có capacity tương đương

2. Weighted Round Robin: Server mạnh hơn nhận nhiều request hơn
   → Phù hợp khi hardware không đồng đều

3. Least Connections: Route đến server ít connection nhất
   → Tốt cho long-lived connections (WebSocket)

4. IP Hash: Hash IP client → server cố định
   → Session affinity (stateful apps)

5. Least Response Time: Route đến server phản hồi nhanh nhất
   → Tốt nhất về performance thực tế
```

**Layer 4 vs Layer 7 Load Balancer:**
| | L4 (Transport) | L7 (Application) |
|---|---|---|
| Hoạt động ở | TCP/UDP level | HTTP/HTTPS level |
| Hiểu content | Không | Có (headers, URL, cookies) |
| Routing rule | IP + Port | URL path, Host header |
| Performance | Nhanh hơn | Chậm hơn (decode payload) |
| Example | AWS NLB, HAProxy L4 | AWS ALB, Nginx |

---

### 1.5 API Gateway

**Q10**: *"API Gateway là gì? Nó khác gì Load Balancer? Bạn đã implement gì ở API Gateway layer?"*

```
API Gateway = Smart entry point cho microservices ecosystem

Các chức năng API Gateway làm được mà LB không làm:
1. Authentication/Authorization (JWT validation)
2. Rate Limiting (giới hạn 100 req/s per user)
3. Request/Response transformation
4. SSL Termination
5. Logging & Monitoring
6. Circuit Breaker
7. Service Discovery integration

Ví dụ MoMo context:
- Client gọi /api/v1/payment → API Gateway
- Gateway kiểm tra JWT token
- Gateway rate limit (5 TXN/phút per user)
- Route đến Payment Service
- Log request để audit

Tools: Spring Cloud Gateway, Kong, AWS API Gateway, Nginx
```

---

## 🗂️ MODULE 2: CACHING (REDIS)

### 2.1 Redis Fundamentals

**Q11**: *"Redis là gì? Tại sao dùng Redis thay vì chỉ dùng database?"*

```
Redis = In-memory data store, single-threaded, sub-millisecond latency

Tại sao dùng Redis:
- DB query: ~5-50ms | Redis get: ~0.1-1ms
- Giảm tải database (DB là bottleneck)
- Session storage (horizontal scaling dễ hơn)
- Rate limiting (atomic increment)
- Pub/Sub messaging

Data structures:
- String: Simple cache (key-value)
- Hash: Object storage (HSET user:123 name "Phuc" age 25)
- List: Queue/Stack (LPUSH, RPUSH, LPOP)
- Set: Unique collection (SADD, SISMEMBER)
- Sorted Set: Leaderboard (ZADD, ZREVRANGE)
- Bitmap: Trạng thái user (active/inactive hàng triệu user)
- HyperLogLog: Count unique visitors (approximate)
```

### 2.2 Cache Patterns

**Q12**: *"Bạn implement caching như thế nào trong project? Cache-aside vs Write-through là gì?"*

**Cache-aside (Lazy Loading) — phổ biến nhất:**
```java
// 1. Check cache first
String cached = redis.get("user:" + userId);
if (cached != null) return deserialize(cached);

// 2. Cache miss → query DB
User user = userRepository.findById(userId);

// 3. Write to cache
redis.setex("user:" + userId, 3600, serialize(user));
return user;
```
✅ Ưu: Chỉ cache data được đọc thực sự  
❌ Nhược: Cache miss đầu tiên chậm, stale data có thể xảy ra

**Write-through:**
```
Khi write DB → đồng thời write cache
✅ Cache luôn fresh
❌ Write latency tăng, cache có nhiều data không đọc
```

**Write-behind (Write-back):**
```
Write vào cache trước → async flush sang DB sau
✅ Write nhanh
❌ Risk data loss nếu cache crash trước khi flush
```

### 2.3 Cache Problems (HAY HỎI TẠI MOMO)

**Q13**: *"Cache Stampede là gì? Bạn handle như thế nào?"*
```
Cache Stampede (Thundering Herd):
- TTL hết → Hàng trăm request cùng miss cache → cùng query DB → DB quá tải

Giải pháp:
1. Probabilistic Early Expiration: Refresh cache trước khi TTL hết một chút
2. Mutex/Lock: Chỉ 1 thread được query DB, các thread khác chờ
3. Stale-while-revalidate: Serve stale data, async refresh background

// Redis Lock solution (Redisson):
RLock lock = redisson.getLock("lock:user:" + userId);
if (lock.tryLock(1, 10, TimeUnit.SECONDS)) {
    try {
        // Double-check sau khi lock
        String cached = redis.get("user:" + userId);
        if (cached != null) return cached;
        
        User user = db.findById(userId);
        redis.setex("user:" + userId, 3600, serialize(user));
        return user;
    } finally {
        lock.unlock();
    }
}
```

**Q14**: *"Cache Penetration là gì?"*
```
Cache Penetration:
- Query cho key không tồn tại (ví dụ: user_id=-1)
- Cache miss → DB query → DB cũng không có → null
- Attacker spam request với random invalid IDs → bypass cache, hammer DB

Giải pháp:
1. Cache null value: redis.setex("user:-1", 60, "NULL")
2. Bloom Filter: Probabilistic structure biết key có tồn tại không TRƯỚC KHI query
   - False positive có thể xảy ra (nói có nhưng không có)
   - False negative không xảy ra (nói không là chắc chắn không có)
   
// Guava Bloom Filter:
BloomFilter<String> bloomFilter = BloomFilter.create(
    Funnels.stringFunnel(UTF_8), 1_000_000, 0.01);
// Load all valid user IDs vào bloom filter khi startup

if (!bloomFilter.mightContain(userId)) {
    return null; // Chắc chắn không tồn tại
}
// Tiếp tục check cache...
```

**Q15**: *"Cache Avalanche là gì?"*
```
Cache Avalanche:
- Nhiều cache key hết TTL cùng lúc → mass cache miss → DB overload

Giải pháp:
1. Random TTL jitter: TTL = baseTime + random(0, 300) seconds
2. Cache warming: Pre-populate cache trước khi traffic hits
3. Circuit Breaker: Nếu DB overload, trả về stale data
4. Redis Cluster: HA để tránh single point of failure
```

### 2.4 Redis trong Transaction Context

**Q16**: *"Bạn dùng Redis để làm gì trong hệ thống thanh toán?"*
```
1. Idempotency key storage:
   SET idempotency:{requestId} "PROCESSING" EX 300 NX
   → NX: chỉ set nếu key chưa tồn tại → atomic check-and-set

2. Session/Token storage:
   SET session:{userId} {tokenData} EX 3600

3. Rate limiting (Token Bucket):
   EVAL lua_script 1 "rate:user:{userId}" limit window

4. Distributed Lock (Redlock):
   Đảm bảo 1 transaction không bị process 2 lần

5. Leaderboard/Counter:
   INCR tx_count:{date}
   INCRBY revenue:{date} {amount}
```

---

## 🗂️ MODULE 3: SECURITY

### 3.1 Mã hóa RSA

**Q17**: *"RSA là gì? Nó được dùng như thế nào trong hệ thống của bạn?"*

```
RSA = Asymmetric encryption (2 key: public + private)

Nguyên lý:
- Public key: Ai cũng có thể biết
- Private key: Chỉ owner giữ (KHÔNG BAO GIỜ chia sẻ)

2 use case chính:
1. Encryption: Mã hóa bằng public key → Chỉ private key mới giải được
   → Dùng để gửi secret an toàn cho 1 người cụ thể
   
2. Signing: Ký bằng private key → Ai cũng verify được bằng public key
   → Dùng để chứng minh message đến từ mình

Trong context thanh toán:
- Bank/MoMo cấp public key cho merchant
- Merchant dùng private key để ký request
- MoMo verify bằng public key → chứng minh request thật sự từ merchant
```

**Java code demo RSA:**
```java
// Key Generation
KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
generator.initialize(2048);
KeyPair pair = generator.generateKeyPair();
PublicKey publicKey = pair.getPublic();
PrivateKey privateKey = pair.getPrivate();

// Signing (server ký với private key)
Signature signature = Signature.getInstance("SHA256withRSA");
signature.initSign(privateKey);
signature.update(dataToSign.getBytes());
byte[] sig = signature.sign();

// Verification (client verify với public key)
signature.initVerify(publicKey);
signature.update(dataToSign.getBytes());
boolean valid = signature.verify(sig);
```

---

### 3.2 Chữ Ký Số (Digital Signature)

**Q18**: *"Chữ ký số hoạt động như thế nào? Tại sao MoMo cần chữ ký số trong API?"*

```
Digital Signature Flow:

SENDER (Merchant):
1. Tạo message M
2. Hash(M) → digest H
3. Encrypt H bằng PRIVATE key → Signature S
4. Gửi: {M, S} sang MoMo

RECEIVER (MoMo):
1. Nhận {M, S}
2. Decrypt S bằng PUBLIC key → H'
3. Hash(M) → H
4. So sánh H == H' → Valid nếu khớp

Đảm bảo:
✅ Authenticity: Message đến từ đúng sender (có private key)
✅ Integrity: Message không bị sửa giữa đường (hash mismatch)
✅ Non-repudiation: Sender không thể phủ nhận đã gửi
```

**Trong practice tại MoMo (Payment Request Signing):**
```
Request signing thường được implement như sau:
1. Sort parameters alphabetically
2. Concatenate: "amount=100000&orderId=ORD123&timestamp=1706000000"
3. HMAC-SHA256(secretKey, concatenated_string) → signature
4. Gửi signature trong header: X-Signature: {signature}
5. MoMo verify lại phía server
```

---

### 3.3 Hashing & Checksum

**Q19**: *"Hash function là gì? Phân biệt MD5, SHA-256, bcrypt, HMAC?"*

| Hash Function | Mục đích | Đặc điểm |
|---|---|---|
| MD5 | Checksum file | 128-bit, KHÔNG dùng cho security |
| SHA-256 | Integrity check, Signing | 256-bit, one-way, deterministic |
| HMAC-SHA256 | Message Authentication | SHA-256 + Secret key, prevent forgery |
| bcrypt | Password hashing | Adaptive cost (slow by design), salt built-in |
| Argon2 | Password hashing | Modern, memory-hard, winner of PHC |

**Q20**: *"Tại sao không dùng SHA-256 để hash password mà phải dùng bcrypt?"*
```
SHA-256 problems cho password:
1. Quá nhanh → GPU brute force 10 tỷ hash/giây
2. Deterministic → rainbow table attack
3. Không có salt → duplicate password → same hash

bcrypt giải quyết:
1. Designed to be SLOW (cost factor = số rounds)
2. Built-in salt (random per password)
3. Adaptive: Tăng cost theo thời gian khi hardware mạnh hơn

// Spring Security:
PasswordEncoder encoder = new BCryptPasswordEncoder(12); // cost=12
String hash = encoder.encode("password123");
boolean match = encoder.matches("password123", hash);
```

**Q21**: *"Checksum được dùng như thế nào để bảo vệ tính toàn vẹn của request?"*
```
Scenario: Merchant gọi API đặt hàng với amount=100000
Nếu không có checksum → Attacker có thể intercept và sửa amount=1

Solution với HMAC:
1. Merchant compute: HMAC-SHA256(secret, "amount=100000&orderId=ORD123") = "abc123"
2. Gửi request kèm header: X-Checksum: abc123
3. MoMo server compute lại HMAC với cùng parameters
4. So sánh: nếu khớp → request chưa bị sửa
5. Timestamp check: Nếu |now - request_time| > 5 min → reject (replay attack)
```

---

### 3.4 JWT Token (Authentication & Authorization)

**Q22**: *"Giải thích JWT structure và flow Authentication/Authorization?"*

**JWT Structure:**
```
Header.Payload.Signature

Header: {"alg": "HS256", "typ": "JWT"}
Payload: {
  "sub": "user123",
  "roles": ["USER", "PREMIUM"],
  "iat": 1706000000,
  "exp": 1706003600
}
Signature: HMAC-SHA256(base64(header) + "." + base64(payload), secret)
```

**Authentication vs Authorization Flow:**
```
AUTHENTICATION (Xác thực danh tính):
1. User POST /login với username/password
2. Server verify credentials
3. Server tạo JWT (access token 15min + refresh token 7 days)
4. Client lưu tokens

AUTHORIZATION (Phân quyền):
1. Client gọi GET /api/transactions với header: Authorization: Bearer {token}
2. API Gateway/Filter extract + verify JWT signature
3. Decode claims → check role/permission
4. Forward request hoặc return 403

Spring Security implementation:
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, ...) {
        String token = extractBearerToken(req);
        if (jwtService.isTokenValid(token)) {
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(req, res);
    }
}
```

**Q23**: *"Refresh Token Rotation là gì? Tại sao cần?"*
```
Vấn đề: Access token phải short-lived (15min) nhưng UX không thể bắt user login lại thường xuyên

Refresh Token Rotation:
1. Access token hết hạn → client gọi POST /refresh với refresh token
2. Server verify refresh token (check DB/Redis)
3. Server INVALIDATE token cũ, issue token MỚI
4. Client nhận token mới

Nếu refresh token bị stolen:
1. Attacker dùng stolen token → server issue new token, invalidate old
2. Real user cố refresh với old token → Server detect reuse → INVALIDATE ALL tokens
3. User bị logout → security breach detected

Redis implementation:
SET refresh:userId:tokenId {tokenHash} EX {7days}
// Khi rotate: DEL refresh:userId:oldTokenId + SET refresh:userId:newTokenId
```

**Q24**: *"JWT stateless có nghĩa là gì? Nhược điểm của stateless JWT?"*
```
Stateless: Server không lưu token → verify chỉ bằng signature
✅ Scale dễ (không cần shared session store)
✅ Performance tốt

Nhược điểm:
❌ Không revoke được token trước khi expire (user logout nhưng token vẫn valid)
❌ Nếu secret key bị lộ → tất cả token invalid

Giải pháp revocation:
1. Token Blacklist trong Redis: SET blacklist:{jti} 1 EX {remaining_ttl}
2. Short TTL cho access token (15 min)
3. Versioning: User có token_version, increment khi logout → all old tokens invalid
```

---

## 🗂️ MODULE 4: XỬ LÝ GIAO DỊCH

### 4.1 Transaction States

**Q25**: *"Hãy giải thích các trạng thái của một giao dịch trong hệ thống thanh toán?"*

```
Transaction State Machine:

PENDING → PROCESSING → SUCCESS
                    ↘ FAILED
                    ↘ TIMEOUT/HUNG (Treo)

Chi tiết:
- PENDING: Request đã nhận, chưa bắt đầu xử lý
- PROCESSING: Đang xử lý (gọi sang bank, third-party)
- SUCCESS: Giao dịch hoàn thành, đã nhận confirm từ bank
- FAILED: Xác nhận thất bại (insufficient fund, wrong OTP, etc.)
- TIMEOUT: Không nhận response sau N giây (treo)
- REVERSED: Đã hoàn tiền (refund)
- RECONCILED: Đã đối soát với ngân hàng
```

### 4.2 Giao Dịch Thành Công

**Q26**: *"Flow xử lý giao dịch thành công trong hệ thống MoMo diễn ra như thế nào?"*

```
Happy Path Flow:
1. User initiate payment → API Gateway → Payment Service
2. Payment Service:
   a. Validate request (amount, merchant, user balance)
   b. Check idempotency key (chống duplicate)
   c. CREATE transaction record (status=PENDING) → DB
   d. Publish event to Kafka: payment.initiated
3. Payment Processor Service consume event:
   a. Update status=PROCESSING
   b. Call Bank/NAPAS API
   c. Bank returns SUCCESS
   d. Update status=SUCCESS
   e. Publish: payment.completed
4. Notification Service consume payment.completed:
   a. Push notification to user
   b. Send receipt email
5. Analytics Service consume payment.completed:
   a. Update revenue statistics

Key: Dùng Outbox Pattern để đảm bảo DB write + Kafka publish là atomic
```

### 4.3 Giao Dịch Thất Bại

**Q27**: *"Bạn handle giao dịch thất bại như thế nào? Compensating transaction là gì?"*

```
Failure Scenarios:
1. Insufficient balance → FAILED ngay (synchronous)
2. Bank timeout → Retry với exponential backoff → FAILED sau N retries
3. Network error → Retry mechanism
4. OTP wrong → FAILED

Compensating Transaction:
Khi giao dịch fail mid-way (money deducted nhưng chưa credited):
1. Phát hiện failure
2. Chạy "undo" actions ngược lại (compensating)
3. Hoàn tiền (reverse) nếu đã deduct

Saga Pattern cho distributed transaction:
Payment Saga:
  Step 1: Deduct from wallet → OK
  Step 2: Call Bank API → FAILED
  
Compensating:
  Compensation 2: (không cần, bank không deduct)
  Compensation 1: Add back to wallet (refund)

Implementation với Kafka + Saga Orchestrator:
- Orchestrator lưu saga state trong DB
- Nếu bất kỳ step nào fail → orchestrator trigger compensating events
```

**Q28**: *"Idempotency trong thanh toán là gì? Implement như thế nào?"*
```
Idempotency: Gửi cùng request nhiều lần → chỉ có 1 kết quả duy nhất

Vấn đề: Network timeout → Client retry → Bị charge 2 lần!

Solution:
1. Client generate unique idempotency-key (UUID v4) per request
2. Gửi trong header: Idempotency-Key: {uuid}
3. Server:
   a. Check Redis: GET idempotency:{key}
   b. Nếu có → return cached response (đã xử lý rồi)
   c. Nếu không → SET idempotency:{key} "PROCESSING" NX EX 300
   d. Process transaction
   e. SET idempotency:{key} {response} EX 86400

// Code pattern:
@PostMapping("/transactions")
public ResponseEntity<?> createTransaction(
    @RequestHeader("Idempotency-Key") String idempotencyKey,
    @RequestBody TransactionRequest request) {
    
    String cached = redis.get("idempotency:" + idempotencyKey);
    if (cached != null) {
        return ResponseEntity.ok(deserialize(cached)); // Return cached
    }
    
    // Process...
    TransactionResponse response = processTransaction(request);
    redis.setex("idempotency:" + idempotencyKey, 86400, serialize(response));
    return ResponseEntity.ok(response);
}
```

### 4.4 Giao Dịch Treo (Hung Transaction)

**Q29**: *"Giao dịch treo là gì? Tại sao nó xảy ra và bạn xử lý như thế nào?"*

```
Giao dịch treo (Hung/Pending transaction):
- Trạng thái: PROCESSING nhưng không có kết quả (thành công hay thất bại)
- Nguyên nhân:
  a. Third-party API timeout (không trả lời trong 30s)
  b. Network partition giữa services
  c. Bug làm service crash giữa chừng
  d. External bank system downtime

Vấn đề:
- Tiền đã bị giữ (deducted từ ví user)
- Không biết bank đã nhận hay chưa
- User hoang mang, complain

Giải pháp:

1. TIMEOUT DETECTION (Scheduler Job):
@Scheduled(fixedDelay = 60000) // Chạy mỗi 60 giây
public void checkHungTransactions() {
    List<Transaction> hung = transactionRepo.findByStatusAndCreatedAtBefore(
        PROCESSING, 
        LocalDateTime.now().minusMinutes(5)); // Treo > 5 phút
    
    for (Transaction tx : hung) {
        checkWithBank(tx); // Hỏi lại ngân hàng
    }
}

2. INQUIRY API (Hỏi lại ngân hàng):
- Gọi bank's transaction status API với originalTransactionId
- Bank trả về: SUCCESS, FAILED, hoặc UNKNOWN
- Cập nhật trạng thái tương ứng

3. RECONCILIATION (Đối soát cuối ngày):
- Cuối ngày: Download sao kê từ ngân hàng
- So sánh với DB của mình
- Mọi transaction trong sao kê mà chưa có trong DB → lỗi cần xử lý
- Mọi transaction PROCESSING trong DB mà không có trong sao kê → FAILED

4. AUTO-REVERSAL (Tự động hoàn tiền):
- Nếu sau inquiry vẫn UNKNOWN + quá timeout
- Hệ thống tự động hoàn tiền (refund) cho user
- Đánh dấu transaction là REVERSED
- Alert team để manual investigation
```

**Q30**: *"Reconciliation (đối soát) là gì? Bạn implement như thế nào?"*

```
Reconciliation = Quá trình đối chiếu data giữa hệ thống mình và bên thứ 3

Tại sao cần:
- Network issue → mình nghĩ transaction fail nhưng bank xử lý thành công
- Bug → charge user nhưng không ghi nhận
- Số tiền không khớp

3 loại mismatch:
1. Chỉ có ở MoMo DB, không có ở Bank → Manual review
2. Chỉ có ở Bank, không có ở MoMo DB → Có thể bị charge double → Critical
3. Cả 2 đều có nhưng amount khác nhau → Critical

Reconciliation Job (chạy hàng ngày):
@Scheduled(cron = "0 2 * * *") // 2am hàng ngày
public void dailyReconciliation() {
    // 1. Download bank statement
    List<BankTransaction> bankTxns = bankClient.getStatement(yesterday);
    
    // 2. Fetch our records for same day
    List<Transaction> ourTxns = txnRepo.findByDate(yesterday);
    
    // 3. Match by bankReferenceId
    Map<String, BankTransaction> bankMap = bankTxns.stream()
        .collect(toMap(BankTransaction::getRefId, identity()));
    
    // 4. Find discrepancies
    for (Transaction our : ourTxns) {
        BankTransaction bank = bankMap.get(our.getBankRefId());
        if (bank == null) {
            // Found in our DB but not in bank → investigate
            alertService.createReconciliationAlert(our, MISSING_IN_BANK);
        } else if (!our.getAmount().equals(bank.getAmount())) {
            // Amount mismatch → Critical
            alertService.createReconciliationAlert(our, AMOUNT_MISMATCH);
        }
    }
}
```

---

## 🗂️ MODULE 5: CÂU HỎI XÉT CV (MoMo Context)

> ⚠️ Interviewer sẽ đào sâu vào từng dự án trong CV của bạn

### 5.1 Câu hỏi về Dự Án Tại Gihot

**Q31**: *"Hãy kể về project phức tạp nhất bạn làm. Bạn gặp vấn đề gì và giải quyết như thế nào?"*

```
Framework STAR:
S (Situation): Bối cảnh dự án, scale, team size
T (Task): Nhiệm vụ của bạn là gì
A (Action): Bạn đã làm gì cụ thể (technical details)
R (Result): Kết quả đo được được (số liệu cụ thể)

Ví dụ về performance issue:
"Ở [dự án X], hệ thống đang có vấn đề response time > 3s cho trang listing
vì query phức tạp join 5 bảng với 2M records.
Tôi analyze execution plan, thêm composite index và implement Redis cache
cho kết quả query. Response time giảm từ 3s xuống còn 200ms."
```

**Q32**: *"Bạn đã làm gì với Spring Boot? Giải thích transaction management bạn đã implement?"*

```
Chuẩn bị trả lời:
1. @Transactional annotation và propagation (REQUIRED, REQUIRES_NEW)
2. Khi nào REQUIRES_NEW: Audit log phải được lưu dù transaction chính fail
3. Self-invocation problem (@Transactional không hoạt động khi gọi trong cùng class)
4. Read-only transaction optimization (readOnly=true)

Ví dụ thực tế từ CV của bạn:
"Trong dự án booking, tôi cần đảm bảo khi payment fail, 
audit log vẫn phải được persist. 
Tôi dùng REQUIRES_NEW cho audit service để nó có transaction độc lập,
không bị rollback khi payment transaction roll back."
```

**Q33**: *"Bạn đã làm gì để optimize database performance trong project?"*

```
Checklist để trả lời:
□ Index strategy (B-tree, composite index, covering index)
□ Query optimization (EXPLAIN ANALYZE, tránh N+1)
□ Connection pooling (HikariCP configuration)
□ Read replica cho read-heavy operations
□ Caching layer (Redis)
□ Pagination (OFFSET vs cursor-based)
□ Database partitioning (nếu có data lớn)

Tips: Nên có số liệu cụ thể (query từ 500ms → 20ms, throughput tăng 3x)
```

### 5.2 Câu hỏi kỹ thuật về Stack hiện tại

**Q34**: *"Bạn dùng Spring Cloud gì? Làm sao service-to-service communication trong project của bạn?"*
```
Cần biết:
- OpenFeign vs RestTemplate vs WebClient
- Service Discovery: Eureka / Kubernetes Service DNS
- Circuit Breaker: Resilience4j
- Load Balancing: Spring Cloud LoadBalancer
- API Gateway: Spring Cloud Gateway

"Trong project, tôi dùng OpenFeign cho synchronous HTTP calls giữa services,
kết hợp Resilience4j circuit breaker để handle downstream service failures.
Khi Order Service gọi Payment Service và Payment timeout quá 3 lần,
circuit breaker OPEN và trả về fallback response."
```

---

## 🗂️ MODULE 6: SYSTEM DESIGN (MOMO SPECIFIC)

### 6.1 Design Payment System

**Q35 (Round 3)**: *"Hãy thiết kế hệ thống xử lý thanh toán có thể scale tới 1 triệu transaction/ngày?"*

```
Bước 1: Clarify Requirements
- Read/Write ratio? (Payment: write-heavy)
- Availability requirement? (99.99% = 52 phút downtime/năm)
- Consistency requirement? (Strong consistency bắt buộc - tiền!)
- Geography? (VN only vs International)
- Peak load? (Tết/Sale season x10 thường ngày)

Bước 2: High-Level Design
Client → API Gateway → Payment Service → [DB + Kafka]
                                       ↓
                              Bank/NAPAS Integration
                                       ↓
                              Notification Service

Bước 3: Database Design
Transaction table:
- id (UUID), userId, merchantId, amount, currency
- status (PENDING/PROCESSING/SUCCESS/FAILED)
- idempotencyKey (UNIQUE INDEX)
- bankRefId, createdAt, updatedAt

Bước 4: Scale considerations
- Horizontal scale: Multiple Payment Service instances
- DB: Master-Slave, connection pooling
- Cache: Redis cluster
- MQ: Kafka với partitioning theo userId
- Idempotency: Redis + DB constraint

Bước 5: Handle failures
- Outbox Pattern cho atomicity
- Saga Pattern cho distributed transaction
- Reconciliation job
- Timeout + retry với exponential backoff
```

---

## 📝 MODULE 7: QUICK REVIEW — CÂU HỎI HAY BẤT NGỜ

**Q36**: *"TLS/SSL hoạt động như thế nào?"*
```
TLS Handshake (simplified):
1. Client Hello: Client gửi supported cipher suites
2. Server Hello: Server chọn cipher suite + gửi certificate (có public key)
3. Client verify certificate với CA
4. Key Exchange: Client/Server agree trên session key (dùng asymmetric)
5. Từ đây dùng symmetric encryption (AES) vì nhanh hơn
```

**Q37**: *"Rate Limiting implement như thế nào trong API Gateway?"*
```
Token Bucket Algorithm trong Redis (Lua script):
- Bucket có N tokens
- Mỗi request consume 1 token
- Tokens được refill đều đặn theo rate

local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local current = redis.call("INCR", key)
if current == 1 then
    redis.call("EXPIRE", key, window)
end
if current > limit then
    return 0 -- Rate limited
end
return 1 -- Allow
```

**Q38**: *"HMAC vs Digital Signature khác nhau như thế nào?"*
```
HMAC (Hash-based Message Authentication Code):
- Dùng SHARED secret key
- Symmetric: cả 2 bên biết secret
- Nhanh hơn
- Không cần PKI
- Dùng khi: Bạn trust bên kia (internal API)

Digital Signature (RSA/ECDSA):
- Dùng asymmetric key pair
- Private key ký, public key verify
- Chậm hơn (asymmetric crypto)
- Non-repudiation: Không thể phủ nhận
- Dùng khi: Need proof (merchant signing payment request)
```

**Q39**: *"Distributed Lock là gì? Redlock algorithm?"*
```
Vấn đề: Nhiều instances cùng muốn process 1 transaction
→ Cần Distributed Lock

Redis Single Instance Lock (Redisson):
SET lock:txn123 {clientId} NX EX 30
// NX: chỉ set nếu chưa tồn tại
// EX 30: auto expire sau 30s để tránh deadlock

Redlock (Multi-instance Redis):
1. Try acquire lock trên 5 Redis nodes
2. If acquired on majority (3/5) và within time window → lock acquired
3. Release: Chỉ release nếu value khớp với clientId mình set
```

**Q40**: *"OWASP Top 10 bạn biết những gì?"*
```
Trong fintech context, quan trọng nhất:
1. Injection (SQL Injection): Dùng PreparedStatement/JPA query methods
2. Broken Authentication: JWT properly implemented
3. Sensitive Data Exposure: Encrypt at rest (PII), TLS in transit
4. Broken Access Control: RBAC/ABAC đúng, server-side authorization
5. Security Misconfiguration: Không expose stack trace, header security
6. CSRF: CSRF token hoặc SameSite cookie + check Origin header
7. XXE: Disable external entity processing trong XML parser
8. Insecure Deserialization: Validate serialized objects
```

---

## 🎯 CHECKLIST TRƯỚC NGÀY PHỎNG VẤN

### Technical Ready:
- [ ] Giải thích được JWT flow end-to-end (không nhìn note)
- [ ] Code được Idempotency key pattern
- [ ] Giải thích được Kafka consumer group và partitioning
- [ ] Mô tả được hung transaction flow và giải pháp
- [ ] Explain được RSA vs HMAC use cases
- [ ] Design được Payment System trong 10 phút (diagram mental model)

### Behavioral Ready (STAR Stories):
- [ ] Story 1: Xử lý bug production nghiêm trọng
- [ ] Story 2: Performance optimization với số liệu cụ thể
- [ ] Story 3: Conflict với teammate/lead và cách resolve
- [ ] Story 4: Học công nghệ mới và apply vào project
- [ ] Story 5: Khi deadline bị delay, bạn làm gì?

### Questions to Ask Interviewer:
- "Tech stack hiện tại của Payment Service team là gì?"
- "Bạn handle distributed transaction như thế nào trong architecture của MoMo?"
- "Team có on-call rotation không? Incident response process ra sao?"
- "Cơ hội mentoring/learning tại MoMo như thế nào?"

---

## 🔥 MoMo-SPECIFIC KNOWLEDGE

```
MoMo Tech Context (công khai):
- Java Spring Boot microservices
- Kubernetes (K8s) orchestration
- Kafka cho event streaming
- Redis cho caching và distributed lock
- PostgreSQL / MySQL
- Tích hợp với hơn 20 ngân hàng Việt Nam
- Hàng triệu giao dịch mỗi ngày
- Payment gateway: NAPAS, Visa, Mastercard integration

Điều MoMo quan tâm nhất:
1. Transaction integrity (tiền không được mất, không được charge 2 lần)
2. High availability (downtime = lost revenue)
3. Security (PCI DSS compliance mindset)
4. Scale (hàng triệu users, tăng x10 vào dịp Tết/sale)
5. Observability (logging, metrics, tracing)
```

---

*💡 Tip cuối: Với MoMo, luôn liên hệ câu trả lời với context fintech/payment. 
"Ở góc độ của một hệ thống thanh toán..." sẽ tạo impression tốt hơn rất nhiều.*
