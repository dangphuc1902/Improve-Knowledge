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
props.put("bootstrap.servers", "broker1:9092,broker2:9092");
props.put("acks", "all");
props.put("retries", 3);
props.put("retry.backoff.ms", 1000);
props.put("enable.idempotence", "true"); // Exactly-once producer
props.put("key.serializer", StringSerializer.class.getName());
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
props.put("group.instance.id", "consumer-instance-1");
// Consumer được gán ID cố định → broker không rebalance ngay khi consumer disconnect
// Chờ session.timeout.ms (default: 45s) mới rebalance
```

---

### 1.4 Offset Management

```java
// application.yml — Spring Kafka
spring:
  kafka:
    consumer:
      auto-offset-reset: earliest    # earliest | latest | none
      enable-auto-commit: false       # Tắt auto commit → manual control
      group-id: payment-processor

// Manual commit trong Spring Kafka:
@KafkaListener(topics = "transactions", containerFactory = "kafkaListenerContainerFactory")
public void processTransaction(ConsumerRecord<String, TransactionEvent> record,
                                Acknowledgment ack) {
    try {
        transactionService.process(record.value());
        ack.acknowledge(); // Commit sau khi xử lý thành công
    } catch (RetryableException ex) {
        // Không commit → Kafka sẽ re-deliver
        throw ex;
    } catch (NonRetryableException ex) {
        // Gửi vào DLT (Dead Letter Topic) rồi commit
        deadLetterTemplate.send("transactions.DLT", record.value());
        ack.acknowledge();
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
@Transactional
public void processAndProduce(TransactionEvent event) {
    // Spring @KafkaListener + @Transactional tự động handle:
    // 1. Consumer offset commit
    // 2. Producer send
    // Trong 1 atomic Kafka transaction
    walletService.updateBalance(event);
    kafkaTemplate.send("wallet-updated", new WalletUpdatedEvent(event.getWalletId()));
}
```

---

### 1.5 Partitioning Strategy & Order Guarantee

**Order chỉ được đảm bảo trong cùng partition:**
```java
// Gửi message với key → messages cùng key → cùng partition → có order
kafkaTemplate.send("transactions", 
    walletId.toString(),  // key → hash → partition
    transactionEvent);

// Không có key → round-robin → không có order guarantee
kafkaTemplate.send("transactions", transactionEvent);
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

    @Cacheable(value = "wallets", key = "#walletId", 
               unless = "#result == null")
    public WalletDTO getWallet(Long walletId) {
        return walletRepo.findById(walletId)
            .map(walletMapper::toDTO)
            .orElse(null);
    }

    @CacheEvict(value = "wallets", key = "#walletId")
    public void updateBalance(Long walletId, BigDecimal amount) {
        // Update DB → evict cache → next read sẽ reload từ DB
        walletRepo.updateBalance(walletId, amount);
    }
}
```

**Cache Stampede Prevention** (nhiều request cùng lúc hit cache MISS → tất cả query DB):
```java
public WalletDTO getWallet(Long walletId) {
    String key = "wallet:" + walletId;
    String cached = redisTemplate.opsForValue().get(key);
    if (cached != null) return deserialize(cached);

    // Dùng distributed lock để chỉ 1 thread query DB
    String lockKey = "lock:wallet:" + walletId;
    Boolean locked = redisTemplate.opsForValue()
        .setIfAbsent(lockKey, "1", Duration.ofSeconds(5));

    if (Boolean.TRUE.equals(locked)) {
        try {
            WalletDTO dto = walletRepo.findById(walletId)
                .map(walletMapper::toDTO).orElse(null);
            redisTemplate.opsForValue().set(key, serialize(dto), Duration.ofMinutes(30));
            return dto;
        } finally {
            redisTemplate.delete(lockKey);
        }
    } else {
        // Chờ ngắn rồi retry (lock holder đang populate cache)
        Thread.sleep(100);
        return getWallet(walletId);
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
// SET key value NX PX milliseconds
// NX = Only set if NOT eXists
// PX = expiry in milliseconds (tránh deadlock nếu client crash)

public boolean acquireLock(String resource, String lockValue, long ttlMs) {
    Boolean result = redisTemplate.opsForValue()
        .setIfAbsent(resource, lockValue, Duration.ofMillis(ttlMs));
    return Boolean.TRUE.equals(result);
}

public void releaseLock(String resource, String lockValue) {
    // Dùng Lua script để atomic check-and-delete
    // Tránh xóa nhầm lock của client khác
    String luaScript = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "   return redis.call('del', KEYS[1]) " +
        "else " +
        "   return 0 " +
        "end";
    redisTemplate.execute(
        new DefaultRedisScript<>(luaScript, Long.class),
        Collections.singletonList(resource),
        lockValue
    );
}
```

**Tại sao cần lockValue duy nhất?** Nếu client A acquire lock, ttl hết hạn, client B acquire lock cùng key. Khi client A release → xóa nhầm lock của B → race condition.

**Redisson (Production-grade distributed lock):**
```java
@Autowired RRedissonClient redissonClient;

public void processWalletUpdate(Long walletId, BigDecimal amount) {
    RLock lock = redissonClient.getLock("wallet:lock:" + walletId);
    
    boolean acquired = lock.tryLock(5, 30, TimeUnit.SECONDS);
    // tryLock(waitTime, leaseTime, unit)
    // waitTime: thời gian tối đa chờ để acquire
    // leaseTime: TTL của lock (-1 = watchdog auto-renewal)
    
    if (!acquired) {
        throw new LockAcquisitionException("Cannot acquire wallet lock");
    }
    
    try {
        // Critical section: update wallet balance
        Wallet wallet = walletRepo.findById(walletId).orElseThrow();
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepo.save(wallet);
    } finally {
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
-- Lua script (atomic execution trên Redis)
local key = KEYS[1]          -- e.g., "rate_limit:user:123"
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])  -- tokens per second
local now = tonumber(ARGV[3])          -- current timestamp (ms)
local requested = tonumber(ARGV[4])   -- tokens needed (usually 1)

local data = redis.call('HMGET', key, 'tokens', 'last_refill_time')
local tokens = tonumber(data[1]) or capacity
local last_time = tonumber(data[2]) or now

-- Refill tokens based on elapsed time
local elapsed = (now - last_time) / 1000  -- convert to seconds
local new_tokens = math.min(capacity, tokens + elapsed * refill_rate)

if new_tokens >= requested then
    -- Allow request
    redis.call('HMSET', key, 'tokens', new_tokens - requested, 'last_refill_time', now)
    redis.call('EXPIRE', key, 3600)
    return 1
else
    -- Reject request
    redis.call('HMSET', key, 'tokens', new_tokens, 'last_refill_time', now)
    redis.call('EXPIRE', key, 3600)
    return 0
end
```

```java
@Service
public class RateLimiterService {
    
    private final DefaultRedisScript<Long> rateLimitScript;
    
    public boolean isAllowed(String userId, int capacity, int refillRate) {
        String key = "rate_limit:user:" + userId;
        Long result = redisTemplate.execute(rateLimitScript,
            Collections.singletonList(key),
            String.valueOf(capacity),
            String.valueOf(refillRate),
            String.valueOf(System.currentTimeMillis()),
            "1"
        );
        return Long.valueOf(1L).equals(result);
    }
}
```

**Spring Cloud Gateway có built-in RequestRateLimiter filter** dùng Redis Token Bucket:
```yaml
filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 10   # tokens/second
      redis-rate-limiter.burstCapacity: 20   # max tokens
      key-resolver: "#{@userKeyResolver}"    # key per user
```

---

### 2.4 Redis Pub/Sub

```java
// Publisher
redisTemplate.convertAndSend("wallet-alerts", alertMessage);

// Subscriber
@Bean
RedisMessageListenerContainer container(RedisConnectionFactory factory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(factory);
    container.addMessageListener(
        (message, pattern) -> handleAlert(deserialize(message.getBody())),
        new PatternTopic("wallet-alerts")
    );
    return container;
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
