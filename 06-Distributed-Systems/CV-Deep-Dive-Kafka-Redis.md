# Kafka & Redis — CV Deep Dive
> Technical reference cho kiến thức đã ghi trong CV.
> Kafka: 8 topics, real-time streaming, idempotency. Redis: caching, pub/sub, rate-limiting, distributed lock.

---

## 1. Apache Kafka — Internals

### 1.1 Kiến Trúc Cơ Bản

#### 🔑 Khái niệm trước khi đọc

**Message Queue (Hàng đợi tin nhắn)** = Trung gian giúp các service giao tiếp bất đồng bộ. Service A gửi message vào queue, Service B đọc ra sau — hai bên không cần biết nhau, không cần chạy cùng lúc.

**Kafka** = Distributed message streaming platform. Khác message queue thông thường ở chỗ:
- Lưu message dưới dạng **log** (append-only, có thể đọc lại nhiều lần)
- Message **không bị xóa** sau khi consumed (giữ theo `retention.ms`)
- Hiệu năng rất cao (hàng triệu msg/giây)

```
                           ┌─────────────────────┐
                           │   Kafka Broker Cluster    │
Producer → gửi message → │ [Topic: transactions]     │ → Consumer đọc message
                           │   [P0][P1][P2][P3]        │
                           └─────────────────────┘

Producer: Chương trình gửi message vào Kafka
Consumer: Chương trình đọc message từ Kafka
Broker:   Máy chủ Kafka. Cluster có nhiều broker.
Topic:    Kênh/Chủ đề của message (giống "folder"). Chia thành Partitions.
Partition: Phân mảnh của Topic. Log append-only, có thứ tự.
Offset:   Vị trí của message trong partition (số thứ tự, bắt đầu từ 0).
```

**Replication (nhân bản):**
```
Topic "transactions" — 3 partitions, replication factor = 3:
Partition 0: Leader=Broker1, Follower=Broker2, Follower=Broker3
Partition 1: Leader=Broker2, Follower=Broker3, Follower=Broker1
Partition 2: Leader=Broker3, Follower=Broker1, Follower=Broker2
```
Producer và Consumer luôn giao tiếp với **Partition Leader**. Follower sync từ leader (dự phòng). Nếu leader down → Kafka bầu follower mới làm leader.

---

### 1.2 Producer — Delivery Guarantees

#### 🔑 Delivery Guarantee (Bảo đảm giao tiếp) là gì?

Khi Producer gửi message → Kafka → Consumer xử lý, có 3 câu hỏi:
- Message có bao giờ **bị mất** không? *(at-most-once)*
- Message có bao giờ **bị duplicate** không? *(at-least-once)*
- Message được xử lý **đúng 1 lần** không? *(exactly-once)*

**`acks`** (acknowledgment = xác nhận) — Producer chờ Kafka xác nhận như thế nào trước khi tiếp tục:
- `acks=0`: Fire and forget. Không chờ broker xác nhận. Nhanh nhất, có thể mất message.
- `acks=1`: Chờ Partition Leader xác nhận ghi. Nếu leader crash trước khi follower sync → mất message.
- `acks=all` (hoặc `-1`): Chờ tất cả **ISR** (In-Sync Replicas = các replica đang sync đầy đủ) xác nhận. An toàn nhất.

```java
Properties props = new Properties();
// Danh sách Kafka brokers để kết nối (có thể liệt kê nhiều broker, phân cách bằng dấu phẩy)
// Client chỉ cần kết nối 1 broker → tự discover toàn bộ cluster
props.put("bootstrap.servers", "broker1:9092,broker2:9092");

// acks=all: Chờ TẤT CẢ ISR (In-Sync Replicas) xác nhận ghi thành công
// → An toàn nhất, không mất message dù leader crash
// Trade-off: chậm hơn acks=0 hoặc acks=1
props.put("acks", "all");

// Số lần retry khi gửi message thất bại (network error, leader not available...)
props.put("retries", 3);
// Thời gian chờ giữa các lần retry (ms) — tránh spam broker khi đang recovery
props.put("retry.backoff.ms", 1000);

// Idempotent Producer: Kafka gán PID + sequence number cho mỗi message
// → Nếu retry gửi trùng → broker detect duplicate → bỏ qua (dedup)
// → Đảm bảo exactly-once per partition per session
// ⚠️ Bắt buộc acks=all khi enable idempotence
props.put("enable.idempotence", "true");

// Serializer: convert key/value thành bytes để gửi qua network
// Key = String (VD: walletId) — dùng để hash → chọn partition
props.put("key.serializer", StringSerializer.class.getName());
// Value = JSON object (VD: TransactionEvent) — serialize thành JSON bytes
props.put("value.serializer", JsonSerializer.class.getName());
```

**Idempotent Producer (`enable.idempotence=true`):**
Broker gán cho mỗi producer một `PID` (Producer ID) và tracking `sequence number` theo partition. Nếu producer retry → broker detect duplicate sequence → bỏ qua (dedup). Đảm bảo **exactly-once per partition per session**.

---

### 1.3 Consumer Group & Rebalancing

#### 🔑 Consumer Group là gì?

**Consumer Group** = Nhóm các consumer cùng đọc chung 1 topic, nhưng mỗi partition chỉ do 1 consumer trong group xử lý. Mục đích: **scale out** — nhiều consumer xử lý song song.

```
Topic "transactions" — 4 partitions:
[P0] [P1] [P2] [P3]

Consumer Group "payment-processor" — 3 consumers:
Consumer-A → [P0, P1]   ← xử lý 2 partitions
Consumer-B → [P2]       ← xử lý 1 partition
Consumer-C → [P3]       ← xử lý 1 partition
```

**Quy tắc:**
- Mỗi partition chỉ được đọc bởi **1 consumer trong 1 group** tại 1 thời điểm
- Nếu số consumer > số partition → một số consumer idle
- Nếu consumer join hoặc leave group → **Rebalance** xảy ra

#### 🔑 Rebalance (Cân bằng lại) là gì?

**Rebalance** = quá trình Kafka phân công lại partition cho các consumer. Xảy ra khi:
- Consumer mới join group
- Consumer làm việc chết (đợt timeout)
- Số partition thay đổi

**Trong khi rebalance**: Tất cả consumer **ngừng đọc** (stop-the-world). Đây là điểm yếu cần biết.

**Rebalance Protocol:**
```
1. Consumer gửi JoinGroup request đến Group Coordinator (broker)
2. Coordinator gửi JoinGroup response, chỉ định 1 consumer làm Group Leader
3. Group Leader tính toán partition assignment
4. Group Leader gửi SyncGroup request với assignment
5. Coordinator phân phối assignment cho tất cả consumer
6. Consumers bắt đầu fetch từ partitions mới
```
Trong quá trình rebalance: tất cả consumer ngừng consume (**stop-the-world**).

**Static Membership** (giảm rebalance tần suất):
```java
// Static Membership: giảm rebalance không cần thiết
// Mặc định: consumer disconnect → Kafka rebalance NGAY LẬP TỨC (stop-the-world)
// Vấn đề: deploy/restart consumer → rebalance liên tục → downtime
props.put("group.instance.id", "consumer-instance-1");
// Gán ID cố định cho consumer → Kafka nhận diện đây là consumer CŨ quay lại
// → KHÔNG rebalance ngay khi disconnect
// → Chờ session.timeout.ms (default: 45s) mới rebalance
// → Nếu consumer quay lại trong 45s → lấy lại đúng partitions cũ → zero downtime
// Use case: rolling deployment — restart từng consumer mà không gây rebalance
```

---

### 1.4 Offset Management

```java
// application.yml — Spring Kafka consumer config
spring:
  kafka:
    consumer:
      auto-offset-reset: earliest
      // earliest: Khi consumer group MỚI (chưa có committed offset)
      //           → đọc từ ĐẦU partition (tất cả message cũ)
      // latest:   → chỉ đọc message MỚI từ sau thời điểm join
      // none:     → throw exception nếu không có committed offset
      enable-auto-commit: false
      // false: TẮT auto commit offset → TA tự quyết khi nào commit
      // Tại sao: auto commit → commit trước khi xử lý xong → MẤT message
      // Manual commit → commit SAU khi xử lý thành công → at-least-once
      group-id: payment-processor
      // Consumer Group ID — Kafka dùng để track offset của nhóm consumer này
      // Tất cả consumer cùng group-id → chia nhau partitions

// Manual commit trong Spring Kafka:
// @KafkaListener: Spring tự tạo consumer, subscribe topic, gọi method khi có message
// ConsumerRecord: chứa key, value, partition, offset, timestamp của message
// Acknowledgment: dùng để commit offset thủ công
@KafkaListener(topics = "transactions", containerFactory = "kafkaListenerContainerFactory")
public void processTransaction(ConsumerRecord<String, TransactionEvent> record,
                                Acknowledgment ack) {
    try {
        transactionService.process(record.value());  // Xử lý business logic
        ack.acknowledge();  // ✅ Commit offset SAU khi xử lý thành công
        // → Kafka đánh dấu message này đã xử lý → không gửi lại
    } catch (RetryableException ex) {
        // Lỗi tạm thời (network timeout, DB connection lost...)
        // KHÔNG commit → offset không đẩy lên → Kafka GỬI LẠI message
        // → Consumer sẽ xử lý lại → at-least-once semantic
        throw ex;
    } catch (NonRetryableException ex) {
        // Lỗi vĩnh viễn (invalid data, business rule violation...)
        // Retry vô ích → gửi vào DLT (Dead Letter Topic) để xử lý sau
        deadLetterTemplate.send("transactions.DLT", record.value());
        // DLT = topic chứa message lỗi → team có thể review và fix manually
        ack.acknowledge();  // Commit để skip message lỗi, xử lý message tiếp theo
    }
}
```

**Delivery Semantics:**
| Semantics | Khi nào commit offset | Rủi ro |
|---|---|---|
| **At-most-once** | Trước khi xử lý | Message mất nếu consumer crash sau commit, trước xử lý |
| **At-least-once** | Sau khi xử lý | Message duplicate nếu consumer crash sau xử lý, trước commit |
| **Exactly-once** | Kafka Transactions API | Phức tạp, có overhead |

**Exactly-once với Kafka Transactions:**
```java
// Exactly-once semantic: consume → process → produce trong 1 atomic transaction
// @Transactional ở đây là Kafka Transaction (không phải DB transaction)
// Spring Kafka KafkaTransactionManager xử lý:
//   1. Begin Kafka transaction
//   2. Consumer offset commit
//   3. Producer send
//   4. Commit hoặc abort TẤT CẢ cùng lúc (atomic)
// → Nếu crash giữa chừng → toàn bộ bị abort → không mất, không duplicate
@Transactional
public void processAndProduce(TransactionEvent event) {
    walletService.updateBalance(event);  // Xử lý business logic
    kafkaTemplate.send("wallet-updated", new WalletUpdatedEvent(event.getWalletId()));
    // Cả offset commit + message send nằm trong 1 Kafka transaction
    // → Nếu send fail → offset không commit → consumer nhận lại message
    // → Nếu offset commit fail → message đã send sẽ bị abort
    // ⚠️ Cần config: spring.kafka.producer.transaction-id-prefix = tx-
}
```

---

### 1.5 Partitioning Strategy & Order Guarantee

**Order chỉ được đảm bảo trong cùng partition:**
```java
// CÓ key: messages cùng key LUÔN đi vào CÙNG partition
// Cơ chế: Kafka hash key → partition = hash(key) % numPartitions
// → Tất cả events của wallet 123 → cùng 1 partition → xử lý TUẦN TỰ
kafkaTemplate.send("transactions", 
    walletId.toString(),  // key = walletId → đảm bảo ordering per wallet
    transactionEvent);    // value = event data
// VD: wallet 123 → partition 2, wallet 456 → partition 0
// → Consumer xử lý tuần tự per partition → KHÔNG có race condition

// KHÔNG có key: Kafka dùng round-robin (hoặc sticky partition) → phân tải đều
// → Messages cùng wallet có thể vào partitions KHÁC NHAU → MẤT ordering
kafkaTemplate.send("transactions", transactionEvent);
// Use case: khi không cần ordering (VD: log events, metrics)
```

**Ví dụ FPM Project**: Wallet transaction events được send với `walletId` làm key → tất cả events của 1 wallet đi vào 1 partition → xử lý tuần tự → không có race condition ở consumer level.

---

### 1.6 Kafka vs RabbitMQ — Khi Nào Dùng Gì

| | Kafka | RabbitMQ |
|---|---|---|
| **Model** | Pull-based log | Push-based queue |
| **Retention** | Có thể replay (log retention) | Message xóa sau khi consumed |
| **Throughput** | Rất cao (millions msg/s) | Thấp hơn (tens of thousands) |
| **Ordering** | Per partition | Per queue |
| **Use case** | Event sourcing, audit log, stream processing | Task queue, RPC, routing phức tạp |
| **Routing** | Topic + partition | Exchange (direct, topic, fanout, headers) |

**Trong FPM Project:**
- **Kafka**: Real-time transaction streaming (8 topics) → cần high throughput, replay, audit
- **RabbitMQ**: Domain event routing (wallet balance update, threshold alerts) → cần flexible routing, low latency

---

## 2. Redis — Caching Patterns, Distributed Lock, Rate Limiting

### 2.1 Cache-Aside Pattern (Lazy Loading)

**Flow:**
```
1. Application check cache
2. Cache HIT → return data
3. Cache MISS → query DB → write to cache → return data
```

```java
@Service
public class WalletService {

    // @Cacheable: Spring AOP intercept method → check cache TRƯỚC khi gọi method
    //   value = "wallets": tên cache region (tương ứng 1 namespace trong Redis)
    //   key = "#walletId": cache key = giá trị tham số walletId
    //   → Redis key format: "wallets::123" (region::key)
    //   unless = "#result == null": KHÔNG cache nếu kết quả = null
    //   → Tránh cache null → mọi request tiếp tục hit DB (negative caching problem)
    @Cacheable(value = "wallets", key = "#walletId", 
               unless = "#result == null")
    public WalletDTO getWallet(Long walletId) {
        // Method chỉ chạy khi CACHE MISS (key không có trong Redis)
        // Khi CACHE HIT → Spring trả cached value, method KHÔNG chạy
        return walletRepo.findById(walletId)  // Query DB
            .map(walletMapper::toDTO)          // Entity → DTO
            .orElse(null);                     // Không tìm thấy → null (không cache)
    }

    // @CacheEvict: XÓA cache entry khi data thay đổi
    //   → Đảm bảo next read sẽ query DB mới → cache luôn đúng
    //   → Pattern: "Write to DB → Evict cache" (Cache-Aside)
    @CacheEvict(value = "wallets", key = "#walletId")
    public void updateBalance(Long walletId, BigDecimal amount) {
        walletRepo.updateBalance(walletId, amount);  // Update DB
        // Sau khi method chạy xong → Spring tự xóa key "wallets::walletId" trong Redis
        // → Next read → cache MISS → query DB → cache data mới
    }
}
```

**Cache Stampede Prevention** (nhiều request cùng lúc hit cache MISS → tất cả query DB):
```java
// Cache Stampede Prevention: tránh "bão" query DB khi cache expire
// Vấn đề: Cache key hết hạn → 1000 request đồng thời MISS → tất cả query DB → DB quá tải
// Giải pháp: Distributed lock → chỉ 1 thread query DB, còn lại chờ cache được populate
public WalletDTO getWallet(Long walletId) {
    String key = "wallet:" + walletId;  // Cache key trong Redis
    String cached = redisTemplate.opsForValue().get(key);  // Check cache
    if (cached != null) return deserialize(cached);  // CACHE HIT → return ngay, không query DB

    // CACHE MISS → cần query DB, nhưng chỉ cho 1 thread làm
    String lockKey = "lock:wallet:" + walletId;  // Lock key riêng cho từng walletId
    Boolean locked = redisTemplate.opsForValue()
        .setIfAbsent(lockKey, "1", Duration.ofSeconds(5));
    // setIfAbsent = SETNX: chỉ set nếu key CHƯA tồn tại (atomic)
    // Duration 5s: TTL cho lock — tránh deadlock nếu thread crash
    // → Thread đầu tiên: locked = true
    // → Các thread sau: locked = false (key đã tồn tại)

    if (Boolean.TRUE.equals(locked)) {
        // THREAD THẮNG LOCK → chịu trách nhiệm query DB và populate cache
        try {
            WalletDTO dto = walletRepo.findById(walletId)
                .map(walletMapper::toDTO).orElse(null);
            // Set cache với TTL 30 phút → sau 30 phút key tự expire
            redisTemplate.opsForValue().set(key, serialize(dto), Duration.ofMinutes(30));
            return dto;
        } finally {
            redisTemplate.delete(lockKey);  // Giải phóng lock → thread khác có thể acquire
        }
    } else {
        // THREAD THUA LOCK → chờ ngắn rồi retry
        // Khi retry → cache đã được populate bởi thread thắng → CACHE HIT
        Thread.sleep(100);  // Chờ 100ms
        return getWallet(walletId);  // Recursive retry → lần này sẽ hit cache
        // ⚠️ Production: nên giới hạn số retry để tránh infinite loop
    }
}
```

**Cache-aside vs Write-through vs Write-behind:**
| Pattern | Write vào | Đọc từ | Consistency |
|---|---|---|---|
| **Cache-aside** | DB trước, evict cache | Cache, fallback DB | Eventual |
| **Write-through** | Cache + DB đồng thời | Cache | Strong |
| **Write-behind** | Cache trước, async sync DB | Cache | Eventual (risk: data loss) |

---

### 2.2 Distributed Lock — SETNX

**Cú pháp đúng (atomic, có expiry):**
```java
// === DISTRIBUTED LOCK bằng Redis SETNX ===
// SET key value NX PX milliseconds
// NX = Only set if NOT eXists → chỉ 1 client acquire được
// PX = expiry in milliseconds → tránh deadlock nếu client crash (lock tự hết hạn)

// Acquire lock: trả true nếu lấy được lock, false nếu đã có client khác giữ
public boolean acquireLock(String resource, String lockValue, long ttlMs) {
    // resource = tên lock (VD: "wallet:lock:123")
    // lockValue = giá trị DUY NHẤT per client (VD: UUID)
    //   → Dùng để xác định AI đang giữ lock → tránh xóa nhầm lock của client khác
    // ttlMs = thời gian lock tồn tại → hết hạn = tự giải phóng
    Boolean result = redisTemplate.opsForValue()
        .setIfAbsent(resource, lockValue, Duration.ofMillis(ttlMs));
    // setIfAbsent = SETNX: atomic operation
    // → Nếu key chưa tồn tại → set thành công → return true (lấy được lock)
    // → Nếu key đã tồn tại → return false (client khác đang giữ lock)
    return Boolean.TRUE.equals(result);
}

// Release lock: XÓA lock, nhưng chỉ xóa nếu lock thuộc về client này
public void releaseLock(String resource, String lockValue) {
    // TẠI SAO dùng Lua script thay vì GET rồi DEL?
    // Vì giữa GET và DEL có thể lock expire → client khác acquire
    // → DEL xóa nhầm lock của client khác → race condition!
    // Lua script chạy ATOMIC trên Redis → không bị xen ngang
    String luaScript = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +  // Check: lock value có phải của mình?
        "   return redis.call('del', KEYS[1]) " +            // Đúng → xóa lock
        "else " +
        "   return 0 " +                                     // Sai → không xóa (lock của client khác)
        "end";
    redisTemplate.execute(
        new DefaultRedisScript<>(luaScript, Long.class),
        Collections.singletonList(resource),  // KEYS[1] = resource name
        lockValue                              // ARGV[1] = lock value của client này
    );
}
```

**Tại sao cần lockValue duy nhất?** Nếu client A acquire lock, ttl hết hạn, client B acquire lock cùng key. Khi client A release → xóa nhầm lock của B → race condition.

**Redisson (Production-grade distributed lock):**
```java
// Redisson: production-grade Redis client cho Java
// Cung cấp distributed lock tốt hơn SETNX thủ công:
//   - Watchdog auto-renewal (tránh lock expire khi đang xử lý)
//   - Reentrant lock (cùng thread acquire lại được)
//   - Fair lock, read-write lock, multi-lock...
@Autowired RRedissonClient redissonClient;

public void processWalletUpdate(Long walletId, BigDecimal amount) {
    // getLock(): tạo RLock object — chưa acquire, chỉ khai báo
    // Key format: "wallet:lock:" + walletId → mỗi wallet có lock riêng
    RLock lock = redissonClient.getLock("wallet:lock:" + walletId);
    
    // tryLock(waitTime, leaseTime, unit):
    //   waitTime = 5s: chờ tối đa 5 giây để acquire lock
    //     → Nếu sau 5s vẫn không lấy được → return false
    //   leaseTime = 30s: lock tự expire sau 30 giây (tránh deadlock)
    //     → Nếu leaseTime = -1 → Watchdog tự gia hạn lock mỗi 10s (default)
    //     → Watchdog dừng khi unlock() hoặc thread chết
    boolean acquired = lock.tryLock(5, 30, TimeUnit.SECONDS);
    
    if (!acquired) {
        // Không lấy được lock → wallet đang bị xử lý bởi thread/service khác
        throw new LockAcquisitionException("Cannot acquire wallet lock");
    }
    
    try {
        // === CRITICAL SECTION: chỉ 1 thread chạy đoạn này tại 1 thời điểm ===
        Wallet wallet = walletRepo.findById(walletId).orElseThrow();  // Đọc balance hiện tại
        wallet.setBalance(wallet.getBalance().add(amount));            // Cộng tiền
        walletRepo.save(wallet);                                       // Lưu lại DB
        // Không có race condition vì lock đảm bảo exclusive access
    } finally {
        // LUÔN unlock trong finally → đảm bảo giải phóng lock dù exception
        // Nếu KHÔNG unlock → lock phải chờ hết leaseTime mới tự giải phóng
        lock.unlock();
    }
}
```
Redisson Watchdog: Nếu `leaseTime = -1`, Redisson tự động renew lock TTL mỗi `lockWatchdogTimeout/3` giây, tránh lock expire khi đang xử lý. Khi unlock() gọi → watchdog dừng.

---

### 2.3 Token Bucket Rate Limiting

**Thuật toán:**
- Bucket có capacity `C` tokens
- Mỗi giây thêm `R` tokens (refill rate)
- Mỗi request cần `N` tokens (thường = 1)
- Nếu không đủ tokens → reject

```lua
-- === TOKEN BUCKET RATE LIMITING — Lua Script ===
-- Toàn bộ script chạy ATOMIC trên Redis (không bị xen ngang bởi command khác)
-- Tại sao Lua? Vì cần đọc + tính toán + ghi trong 1 atomic operation

local key = KEYS[1]          -- Redis key per user, VD: "rate_limit:user:123"
local capacity = tonumber(ARGV[1])     -- Sức chứa tối đa của bucket (VD: 100 tokens)
local refill_rate = tonumber(ARGV[2])  -- Số tokens nạp mỗi giây (VD: 10 tokens/s)
local now = tonumber(ARGV[3])          -- Timestamp hiện tại (milliseconds)
local requested = tonumber(ARGV[4])    -- Số tokens cần cho request này (thường = 1)

-- Đọc trạng thái hiện tại từ Redis Hash
-- HMGET: lấy nhiều field cùng lúc từ 1 hash key
local data = redis.call('HMGET', key, 'tokens', 'last_refill_time')
local tokens = tonumber(data[1]) or capacity   -- Lần đầu → bucket đầy (= capacity)
local last_time = tonumber(data[2]) or now      -- Lần đầu → thời điểm hiện tại

-- Tính số tokens được nạp thêm dựa trên thời gian đã qua
local elapsed = (now - last_time) / 1000        -- Chuyển ms → giây
local new_tokens = math.min(capacity, tokens + elapsed * refill_rate)
-- math.min: không vượt quá capacity (bucket có giới hạn)
-- VD: elapsed=0.5s, refill_rate=10 → thêm 5 tokens

if new_tokens >= requested then
    -- ĐỦ tokens → CHO PHÉP request
    -- Trừ tokens đã dùng, cập nhật thời gian refill
    redis.call('HMSET', key, 'tokens', new_tokens - requested, 'last_refill_time', now)
    redis.call('EXPIRE', key, 3600)  -- TTL 1 giờ → tự xóa key nếu user không active
    return 1  -- 1 = ALLOWED
else
    -- KHÔNG ĐỦ tokens → TỪ CHỐI request (429 Too Many Requests)
    -- Vẫn cập nhật tokens (đã refill) và thời gian, nhưng không trừ
    redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill_time', now)
    redis.call('EXPIRE', key, 3600)
    return 0  -- 0 = REJECTED
end
```

```java
@Service
public class RateLimiterService {
    
    // DefaultRedisScript: Spring wrapper để load và cache Lua script
    // <Long>: kiểu return value của Lua script (1L = allowed, 0L = rejected)
    private final DefaultRedisScript<Long> rateLimitScript;
    
    // Kiểm tra user có được phép gửi request không
    // capacity: số tokens tối đa (VD: 100)
    // refillRate: tokens nạp mỗi giây (VD: 10)
    public boolean isAllowed(String userId, int capacity, int refillRate) {
        String key = "rate_limit:user:" + userId;  // Mỗi user 1 bucket riêng
        Long result = redisTemplate.execute(
            rateLimitScript,                              // Lua script đã load
            Collections.singletonList(key),                // KEYS[1]
            String.valueOf(capacity),                      // ARGV[1] = capacity
            String.valueOf(refillRate),                    // ARGV[2] = refill rate
            String.valueOf(System.currentTimeMillis()),    // ARGV[3] = timestamp hiện tại
            "1"                                            // ARGV[4] = tokens requested
        );
        return Long.valueOf(1L).equals(result);  // 1 = allowed, 0 = rejected
        // Dùng Long.valueOf(1L).equals() thay vì result == 1L để tránh NPE
    }
}
```

**Spring Cloud Gateway có built-in RequestRateLimiter filter** dùng Redis Token Bucket:
```yaml
filters:
  - name: RequestRateLimiter     # Spring Cloud Gateway built-in filter
    args:
      redis-rate-limiter.replenishRate: 10   # Nạp 10 tokens/giây (sustained rate)
      redis-rate-limiter.burstCapacity: 20   # Bucket chứa tối đa 20 tokens (burst)
      # VD: User gửi 20 requests cùng lúc → OK (burst)
      #     Sau đó chỉ được 10 req/s (sustained)
      key-resolver: "#{@userKeyResolver}"
      # SpEL: reference đến bean userKeyResolver
      # Bean này quyết định "key" nào cho mỗi request
      # VD: key = userId → rate limit per user
      #     key = IP → rate limit per IP
      #     key = API key → rate limit per client
```

---

### 2.4 Redis Pub/Sub

```java
// === PUBLISHER: gửi message đến channel ===
// convertAndSend(channel, message): publish message đến tất cả subscriber đang listen
// Fire-and-forget: KHÔNG lưu lại, subscriber offline = MẤT message
// Use case: real-time notification, cache invalidation across instances
redisTemplate.convertAndSend("wallet-alerts", alertMessage);

// === SUBSCRIBER: lắng nghe message từ channel ===
// @Bean: đăng ký container như Spring Bean → Spring tự start/stop lifecycle
@Bean
RedisMessageListenerContainer container(RedisConnectionFactory factory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(factory);  // Kết nối Redis
    container.addMessageListener(
        // MessageListener: callback được gọi khi có message đến
        // message.getBody(): nội dung message (bytes)
        // pattern: pattern đã match (dùng khi subscribe bằng wildcard)
        (message, pattern) -> handleAlert(deserialize(message.getBody())),
        new PatternTopic("wallet-alerts")
        // PatternTopic: subscribe theo pattern (hỗ trợ wildcard: wallet-*)
        // Hoặc dùng ChannelTopic("wallet-alerts") cho exact match
    );
    return container;
    // Container tự quản lý connection, subscribe, và dispatch message đến listener
}
```

**Redis Pub/Sub vs Kafka:**
| | Redis Pub/Sub | Kafka |
|---|---|---|
| **Persistence** | Không (fire-and-forget) | Có (log-based, configurable retention) |
| **Replay** | Không | Có |
| **Consumer offline** | Mất message | Không mất (committed offset) |
| **Use case** | Real-time notification, cache invalidation | Event sourcing, audit, stream processing |

---

## 3. Interview Q&A

**Q: Kafka partition có thể tăng được không? Giảm được không?**
A: Tăng được (`kafka-topics.sh --alter`), nhưng sẽ ảnh hưởng đến key-based partitioning (cùng key có thể vào partition khác). Giảm **không được** — phải xóa và tạo lại topic.

**Q: Consumer lag là gì? Theo dõi như thế nào?**
A: Consumer lag = Latest offset - Committed offset. Đo được qua `kafka-consumer-groups.sh --describe` hoặc JMX metrics. High lag = consumer xử lý chậm hơn producer.

**Q: Redis là single-threaded, tại sao vẫn cần Lua script cho atomicity?**
A: Redis command đơn lẻ atomic, nhưng multi-command không atomic. Giữa 2 commands có thể có command khác xen vào. Lua script chạy trên Redis interpreter → toàn bộ script là atomic, không thể xen ngang.

**Q: Redlock (Redis distributed lock across multiple nodes) là gì?**
A: Algorithm của Antirez để acquire lock trên majority of N Redis masters (thường N=5). Client acquire lock trên ít nhất N/2+1 nodes trong thời gian < lock validity. Tranh cãi về correctness trong distributed systems. Trong production thường dùng Redisson (implements Redlock).
