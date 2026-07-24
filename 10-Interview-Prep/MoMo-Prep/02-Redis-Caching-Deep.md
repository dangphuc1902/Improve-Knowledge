# 🔴 Redis & Caching Deep Dive — From Basics to Production

> **Mức độ**: Senior Engineer perspective  
> **Context**: Caching strategy trong hệ thống thanh toán high-traffic

---

## PHẦN 1: Redis Internals — Tại Sao Nhanh Đến Vậy?

### 1.1 Redis Architecture

```
Redis nhanh vì:

1. IN-MEMORY: Data lưu trong RAM (không có disk I/O)
   DB query: 5-50ms (disk seek + network)
   Redis GET: 0.1-1ms (RAM read + network)

2. SINGLE-THREADED (cho I/O operations):
   - Không cần lock/mutex → Không có thread contention overhead
   - Mỗi command là atomic → Không race condition
   - Nhưng vẫn có I/O multiplexing (epoll/kqueue) → Handle nhiều connections

3. NON-BLOCKING I/O (I/O Multiplexing):
   Dùng epoll (Linux) — Event-driven model
   1 thread xử lý hàng nghìn connections đồng thời

4. EFFICIENT DATA STRUCTURES:
   - Được implement bằng C với memory optimization
   - Skip List cho Sorted Set: O(log N) insert/search
   - Hash Table cho Hash, Set: O(1) average

Benchmark thực tế:
   Redis 7.x: ~1 million ops/second trên 1 node
   Với pipelining: 10+ million ops/second
```

### 1.2 Redis Data Structures và Use Cases

**String — Simplest, Most Versatile:**
```bash
# Basic get/set với TTL
SET user:session:123 "{...userdata...}" EX 3600   # EX = expire seconds
GET user:session:123

# Atomic increment (counter, rate limiting)
INCR page:view:homepage    # Thread-safe! Atomic operation
INCRBY revenue:2024-01 100000

# Set if not exists (distributed lock, idempotency)
SET lock:txn:ORD123 "instance-1" NX EX 30   # NX = only if Not eXists
# Returns OK (acquired) hoặc nil (already locked)

# Conditional SET (Redis 7+)
SET user:123 "data" XX   # XX = only if eXists (update only)
```

```java
// Java - Spring Data Redis:
@Service
public class CacheService {

    private final StringRedisTemplate redis;

    // Simple cache
    public Optional<User> getCachedUser(Long userId) {
        String json = redis.opsForValue().get("user:" + userId);
        if (json == null) return Optional.empty();
        return Optional.of(jsonMapper.readValue(json, User.class));
    }

    // Atomic increment (views counter)
    public long incrementPageView(String pageId) {
        return redis.opsForValue().increment("views:" + pageId);
    }

    // Idempotency key
    public boolean markAsProcessing(String requestId) {
        // Returns true nếu chưa tồn tại (first time)
        return redis.opsForValue().setIfAbsent(
            "idempotency:" + requestId,
            "PROCESSING",
            Duration.ofMinutes(5)
        );
    }
}
```

**Hash — Object Storage:**
```bash
# Lưu user object (không serialize toàn bộ)
HSET user:123 name "Phuc" email "phuc@gmail.com" balance "1000000" premium "true"
HGET user:123 name          # "Phuc"
HGETALL user:123            # Tất cả fields
HMGET user:123 name email   # Multiple fields

HINCRBY user:123 balance 50000   # Atomic increment cho field

# Tại sao Hash thay vì JSON String?
# → Cập nhật 1 field (balance) → Không cần overwrite cả object
# → HSET user:123 balance 1050000 (thay vì GET → modify → SET)
```

**List — Queue/Stack:**
```bash
# Queue (FIFO): RPUSH + LPOP
RPUSH notification:queue "msg1" "msg2"  # Push to right
LPOP notification:queue                  # Pop from left → FIFO

# Stack (LIFO): RPUSH + RPOP
RPUSH history:user:123 "action1"
RPOP history:user:123   # Last action first

# Blocking pop (consumer waits for item):
BLPOP notification:queue 30   # Block up to 30s
# → Consumer không cần polling, efficient!

LRANGE notification:queue 0 -1  # View all items
LLEN notification:queue          # Queue size
```

**Set — Unique Collection:**
```bash
# Online users tracking
SADD online:users 101 102 103
SISMEMBER online:users 102   # Check if online → 1 (yes)
SCARD online:users           # Count online users
SMEMBERS online:users        # All online users

# Set operations (rất powerful):
SUNION online:users:server1 online:users:server2  # Unique users across servers
SINTER premium:users active:users                  # Premium AND active users
SDIFF all:users banned:users                       # Non-banned users
```

**Sorted Set (ZSet) — Leaderboard, Priority:**
```bash
# Leaderboard (score = transaction volume)
ZADD leaderboard 500000 "merchant-A"
ZADD leaderboard 750000 "merchant-B"
ZADD leaderboard 300000 "merchant-C"

ZREVRANGE leaderboard 0 9 WITHSCORES   # Top 10 (high to low)
ZRANK leaderboard "merchant-C"          # Rank (0-indexed) = 2
ZSCORE leaderboard "merchant-A"         # Score = 500000
ZINCRBY leaderboard 100000 "merchant-C" # Increment score

# Delay Queue (timestamp as score):
ZADD delay:queue 1706003600 "job:123"  # Execute at epoch timestamp
ZRANGEBYSCORE delay:queue 0 {now}      # Jobs ready to execute
```

**Bitmap — Flags at Scale:**
```bash
# Track daily login (1 bit per user per day)
SETBIT login:2024-01-01 userId 1   # User logged in today
GETBIT login:2024-01-01 userId     # Check login status
BITCOUNT login:2024-01-01          # Total logins today

# 100 million users = 100M bits = 12.5 MB! (vs 100M rows in DB)
# Perfect cho: Daily active users, feature flags, read receipts
```

---

## PHẦN 2: Cache Patterns — Senior Level

### 2.1 Cache-Aside (Lazy Loading) — Phổ biến nhất

```java
@Service
public class UserService {

    private final UserRepository userRepo;
    private final RedisTemplate<String, Object> redis;
    private static final Duration TTL = Duration.ofHours(1);

    public User getUser(Long userId) {
        String key = "user:" + userId;

        // 1. Check cache
        User cached = (User) redis.opsForValue().get(key);
        if (cached != null) {
            return cached; // Cache HIT
        }

        // 2. Cache MISS → Query DB
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        // 3. Store in cache
        redis.opsForValue().set(key, user, TTL);

        return user;
    }

    public void updateUser(Long userId, UpdateUserRequest request) {
        userRepo.save(mapToUser(request));

        // IMPORTANT: Invalidate cache sau khi update
        redis.delete("user:" + userId);
        // Cache-aside: Next read sẽ load từ DB và cache lại
    }
}
```

**Vấn đề của Cache-Aside:**
```
1. Cache Miss Storm: Cold start → Mọi request đều miss → DB quá tải
   Fix: Cache Warming (pre-populate cache trước khi traffic)

2. Stale Data: Cache TTL 1 giờ → Data thay đổi nhưng cache cũ
   Fix: Invalidate on write, hoặc shorter TTL, hoặc versioning

3. Cache Miss Latency: User nhận response chậm lần đầu
   Fix: Acceptable trong hầu hết cases, hoặc prefetch

Double-checked locking (chống stampede):
```java
public User getUserWithLock(Long userId) throws Exception {
    String key = "user:" + userId;
    
    User cached = (User) redis.opsForValue().get(key);
    if (cached != null) return cached;
    
    // Acquire distributed lock
    String lockKey = "lock:user:" + userId;
    String lockValue = UUID.randomUUID().toString();
    
    boolean acquired = redis.opsForValue()
        .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(10));
    
    if (!acquired) {
        // Someone else is fetching → Wait and retry
        Thread.sleep(50);
        return (User) redis.opsForValue().get(key); // Hopefully populated now
    }
    
    try {
        // Double check after acquiring lock
        cached = (User) redis.opsForValue().get(key);
        if (cached != null) return cached;
        
        User user = userRepo.findById(userId).orElseThrow();
        redis.opsForValue().set(key, user, TTL);
        return user;
        
    } finally {
        // Release lock (only if we own it)
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "  return redis.call('del', KEYS[1]) " +
            "else return 0 end";
        redis.execute(new DefaultRedisScript<>(script, Long.class),
            List.of(lockKey), lockValue);
    }
}
```

### 2.2 Write-Through — Strong Consistency

```java
@Service
public class AccountService {

    @Transactional
    public void updateBalance(Long userId, BigDecimal newBalance) {
        // 1. Write to DB
        accountRepo.updateBalance(userId, newBalance);
        
        // 2. Write to cache TRONG CÙNG business operation
        // (không phải DB transaction, nhưng cùng method call)
        redis.opsForHash().put("account:" + userId, "balance", 
            newBalance.toString());
        
        // Trade-off:
        // ✅ Cache luôn có data mới nhất
        // ✅ Không có stale reads
        // ❌ Write latency cao hơn (phải write cả DB + Redis)
        // ❌ Cache có nhiều data ít được đọc (cold data cũng cached)
    }
}
```

### 2.3 Cache-Aside vs Write-Through — Khi nào dùng?

```
Cache-Aside (Lazy Loading):
→ Read-heavy workload (đọc nhiều hơn write)
→ Cache miss là acceptable
→ Data có thể stale 1 chút
→ Ví dụ: User profile, product catalog, exchange rates

Write-Through:
→ Không thể chấp nhận stale data
→ Write frequency không quá cao
→ Ví dụ: Account balance (nhưng cần distributed TX!), inventory

Write-Behind (Write-Back):
→ Write-heavy workload
→ Data có thể bị lose nếu cache crash
→ Ví dụ: Analytics counters, non-critical data
→ KHÔNG DÙNG cho financial data!
```

---

## PHẦN 3: Cache Problems — HAY HỎI NHẤT

### 3.1 Cache Penetration

```
VẤN ĐỀ:
Request cho key KHÔNG TỒN TẠI:
GET /users/999999999 (userId không tồn tại)
→ Cache miss (key không có)
→ DB query (DB cũng không có)
→ Return null
→ Cache không lưu null (thường) → Lần sau lại miss!

ATTACK:
Attacker spam 10,000 requests với random invalid userId
→ 10,000 DB queries → DB quá tải → Outage!

GIẢI PHÁP 1: Cache null value
```java
public User getUser(Long userId) {
    String key = "user:" + userId;
    
    // Check cache (kể cả null marker)
    if (redis.hasKey(key)) {
        String val = (String) redis.opsForValue().get(key);
        if ("NULL".equals(val)) return null; // Đã biết không tồn tại
        return deserialize(val, User.class);
    }
    
    // Cache miss → DB
    Optional<User> userOpt = userRepo.findById(userId);
    
    if (userOpt.isPresent()) {
        redis.opsForValue().set(key, serialize(userOpt.get()), Duration.ofHours(1));
    } else {
        // Cache null! TTL ngắn (5 phút) để auto-recover nếu user tạo sau
        redis.opsForValue().set(key, "NULL", Duration.ofMinutes(5));
    }
    
    return userOpt.orElse(null);
}
```

```
GIẢI PHÁP 2: Bloom Filter (Tốt hơn cho production)

Bloom Filter là probabilistic data structure:
- Check xem key CÓ THỂ tồn tại không
- False Positive: Nói "có" nhưng thực ra "không có" (chấp nhận được - ~1%)
- False Negative: Nói "không có" → Chắc chắn không có! (không xảy ra)

Tại startup: Load tất cả valid userIds vào Bloom Filter
Mỗi request: bloomFilter.mightContain(userId)?
  → false: 100% không tồn tại → Return 404 ngay (không check cache/DB)
  → true: Có thể tồn tại → Check cache/DB như bình thường
```

```java
// Guava Bloom Filter (in-memory):
@Component
public class UserBloomFilter {
    
    private BloomFilter<Long> filter;
    
    @PostConstruct
    public void init() {
        // Load all user IDs from DB (1 lần khi startup)
        List<Long> userIds = userRepo.findAllIds();
        
        // Create Bloom Filter: 10M elements, 1% false positive rate
        filter = BloomFilter.create(
            Funnels.longFunnel(),
            10_000_000,  // Expected insertions
            0.01          // 1% false positive rate
        );
        
        userIds.forEach(filter::put);
        log.info("Bloom filter loaded with {} users", userIds.size());
    }
    
    public boolean mightExist(Long userId) {
        return filter.mightContain(userId);
    }
    
    // Call khi user mới được tạo
    public void addUser(Long userId) {
        filter.put(userId);
    }
}

// Service:
public User getUser(Long userId) {
    // Fast-fail với Bloom Filter
    if (!bloomFilter.mightExist(userId)) {
        throw new UserNotFoundException(userId); // Return 404 without cache/DB hit
    }
    
    // Tiếp tục cache-aside như bình thường
    ...
}
```

### 3.2 Cache Stampede (Thundering Herd)

```
VẤN ĐỀ:
Hot key expires (TTL hết) đột ngột
→ 1000 requests cùng lúc → Cache miss
→ 1000 requests cùng query DB
→ DB quá tải!

SCENARIO thực tế:
Flash sale product: 10,000 users cùng xem
Cache TTL = 1 giờ
TTL hết vào 12:00:00 đúng lúc flash sale bắt đầu
→ 10,000 DB queries trong milliseconds → DB crash!

GIẢI PHÁP 1: Mutex Lock (đã implement ở trên trong double-checked locking)

GIẢI PHÁP 2: Probabilistic Early Expiration
Thay vì đợi TTL hết, refresh sớm một cách ngẫu nhiên:
```

```java
@Service
public class SmartCacheService {

    // Probabilistic Early Expiration (PER)
    public Product getProduct(Long productId) {
        String key = "product:" + productId;
        CacheEntry<Product> entry = getCacheEntry(key);
        
        if (entry == null) {
            return fetchAndCache(productId);
        }
        
        // Check if we should refresh early
        // Xác suất refresh tăng dần khi gần hết TTL
        if (shouldEarlyRefresh(entry)) {
            // Async refresh, không block current request
            CompletableFuture.runAsync(() -> fetchAndCache(productId));
        }
        
        return entry.getValue(); // Return existing value ngay lập tức
    }
    
    private boolean shouldEarlyRefresh(CacheEntry<?> entry) {
        long remainingTtl = entry.getExpiresAt() - System.currentTimeMillis();
        long totalTtl = entry.getTtl();
        
        // Khi còn 20% TTL → Xác suất refresh tăng dần
        if (remainingTtl > totalTtl * 0.2) return false;
        
        // Xác suất refresh = 1 - (remainingTtl/totalTtl/0.2)
        double probability = 1.0 - (double)remainingTtl / (totalTtl * 0.2);
        return Math.random() < probability;
    }
}
```

### 3.3 Cache Avalanche

```
VẤN ĐỀ:
Nhiều cache keys CÙNG HẾT HẠN → Mass cache miss → DB overload

Nguyên nhân thường gặp:
1. Cache server restart → Tất cả keys mất
2. Tất cả keys được set với cùng TTL (ví dụ: startup warming với TTL=1h)
→ 1 giờ sau: TẤT CẢ expire cùng lúc!

GIẢI PHÁP 1: Random TTL Jitter
```

```java
public void cacheProduct(Long productId, Product product) {
    // Thay vì fixed TTL = 3600s
    // Dùng TTL = 3600 ± random(300) seconds
    long baseTtl = 3600;
    long jitter = (long)(Math.random() * 600 - 300); // ±300s
    long ttl = baseTtl + jitter;
    
    redis.opsForValue().set("product:" + productId, product, 
        Duration.ofSeconds(ttl));
}
```

```
GIẢI PHÁP 2: Multi-level Cache (L1 local + L2 Redis)
L1 Cache (Caffeine in-memory, per instance): TTL = 5 phút
L2 Cache (Redis): TTL = 1 giờ

Request flow:
L1 Hit → Return (fastest)
L1 Miss → Check L2
L2 Hit → Populate L1 → Return
L2 Miss → Query DB → Populate L2 → Populate L1 → Return

Khi Redis down:
→ L1 vẫn serve (5 phút grace period)
→ DB không bị instant overload
```

```java
@Service
public class MultiLevelCacheService {

    private final Cache<String, Object> localCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(5, MINUTES)
        .build();
    
    private final RedisTemplate<String, Object> redis;
    private final UserRepository userRepo;

    public User getUser(Long userId) {
        String key = "user:" + userId;
        
        // L1: Local cache
        User l1 = (User) localCache.getIfPresent(key);
        if (l1 != null) return l1;
        
        // L2: Redis
        User l2 = (User) redis.opsForValue().get(key);
        if (l2 != null) {
            localCache.put(key, l2); // Populate L1
            return l2;
        }
        
        // DB
        User user = userRepo.findById(userId).orElseThrow();
        redis.opsForValue().set(key, user, Duration.ofHours(1));
        localCache.put(key, user);
        return user;
    }
}
```

---

## PHẦN 4: Redis trong Fintech — Advanced Use Cases

### 4.1 Distributed Lock (Redlock)

```java
@Component
public class RedisDistributedLock {

    private final StringRedisTemplate redis;

    /**
     * Acquire lock. Return lockValue nếu acquired, null nếu không.
     * lockValue dùng để release (chỉ release lock của mình, không release của người khác)
     */
    public String tryAcquire(String lockKey, Duration timeout) {
        String lockValue = UUID.randomUUID().toString();
        
        Boolean acquired = redis.opsForValue().setIfAbsent(
            "lock:" + lockKey,
            lockValue,
            timeout
        );
        
        return Boolean.TRUE.equals(acquired) ? lockValue : null;
    }

    /**
     * Release lock - chỉ release nếu value khớp (chứng minh là lock của mình)
     * Dùng Lua script để đảm bảo atomic check-and-delete
     */
    public boolean release(String lockKey, String lockValue) {
        String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

        Long result = redis.execute(
            new DefaultRedisScript<>(script, Long.class),
            List.of("lock:" + lockKey),
            lockValue
        );
        
        return Long.valueOf(1).equals(result);
    }
}

// Usage:
@Service
public class PaymentService {
    
    @Autowired
    private RedisDistributedLock lock;

    public void processPayment(String orderId, PaymentRequest request) {
        String lockValue = lock.tryAcquire(orderId, Duration.ofSeconds(30));
        
        if (lockValue == null) {
            throw new ConflictException("Payment already in progress for " + orderId);
        }
        
        try {
            // Critical section: Chỉ 1 instance xử lý payment này
            doProcessPayment(orderId, request);
        } finally {
            lock.release(orderId, lockValue);
        }
    }
}
```

### 4.2 Rate Limiting với Redis

**Token Bucket Algorithm:**
```java
@Component
public class RateLimiter {

    private final StringRedisTemplate redis;

    /**
     * Token Bucket Rate Limiter
     * @param userId User to rate limit
     * @param maxRequests Max requests per window
     * @param windowSeconds Time window in seconds
     * @return true nếu request được phép, false nếu bị throttle
     */
    public boolean isAllowed(String userId, int maxRequests, int windowSeconds) {
        String key = "rate:" + userId;
        
        // Lua script để đảm bảo atomic increment + check
        String script =
            "local current = redis.call('incr', KEYS[1]) " +
            "if current == 1 then " +
            "    redis.call('expire', KEYS[1], ARGV[1]) " +
            "end " +
            "if current > tonumber(ARGV[2]) then " +
            "    return 0 " +
            "else " +
            "    return 1 " +
            "end";

        Long allowed = redis.execute(
            new DefaultRedisScript<>(script, Long.class),
            List.of(key),
            String.valueOf(windowSeconds),
            String.valueOf(maxRequests)
        );

        return Long.valueOf(1).equals(allowed);
    }
}

// Spring Filter:
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, ...) throws Exception {
        String userId = req.getHeader("X-User-Id");
        
        if (!rateLimiter.isAllowed(userId, 100, 60)) { // 100 req/minute
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"error\": \"Rate limit exceeded\"}");
            return;
        }
        
        chain.doFilter(req, response);
    }
}
```

### 4.3 Redis Pub/Sub — Real-time Notifications

```java
// Publisher (Payment Service):
@Service
public class NotificationPublisher {
    
    private final RedisTemplate<String, String> redis;

    public void publishPaymentSuccess(PaymentEvent event) {
        redis.convertAndSend(
            "channel:user:" + event.getUserId(),
            jsonMapper.writeValueAsString(event)
        );
    }
}

// Subscriber (Notification Service):
@Configuration
public class RedisSubscriberConfig {

    @Bean
    public RedisMessageListenerContainer container(
            RedisConnectionFactory factory,
            MessageListenerAdapter listenerAdapter) {
        
        RedisMessageListenerContainer container = 
            new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        
        // Subscribe to all user channels
        container.addMessageListener(listenerAdapter,
            new PatternTopic("channel:user:*"));
        
        return container;
    }
    
    @Bean
    public MessageListenerAdapter listenerAdapter(PaymentNotificationListener listener) {
        return new MessageListenerAdapter(listener, "onPaymentEvent");
    }
}

@Component
public class PaymentNotificationListener {
    
    public void onPaymentEvent(String message, String channel) {
        String userId = channel.split(":")[2];
        PaymentEvent event = jsonMapper.readValue(message, PaymentEvent.class);
        
        // Send WebSocket/Push notification
        webSocketService.sendToUser(userId, event);
    }
}
```

### 4.4 Redis Sorted Set — Transaction Leaderboard

```java
@Service
public class LeaderboardService {

    private final RedisTemplate<String, String> redis;
    private static final String LEADERBOARD_KEY = "leaderboard:merchants";

    // Record transaction (increment merchant score)
    public void recordTransaction(String merchantId, BigDecimal amount) {
        redis.opsForZSet().incrementScore(
            LEADERBOARD_KEY,
            merchantId,
            amount.doubleValue()
        );
    }

    // Get top 10 merchants by volume
    public List<MerchantRank> getTopMerchants(int topN) {
        Set<ZSetOperations.TypedTuple<String>> results =
            redis.opsForZSet().reverseRangeWithScores(
                LEADERBOARD_KEY, 0, topN - 1);

        List<MerchantRank> ranks = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : results) {
            ranks.add(MerchantRank.builder()
                .rank(rank++)
                .merchantId(tuple.getValue())
                .totalVolume(BigDecimal.valueOf(tuple.getScore()))
                .build());
        }
        return ranks;
    }

    // Get merchant's rank
    public Long getMerchantRank(String merchantId) {
        Long rank = redis.opsForZSet().reverseRank(LEADERBOARD_KEY, merchantId);
        return rank != null ? rank + 1 : null; // Convert 0-indexed to 1-indexed
    }
}
```

---

## PHẦN 5: Redis Production — High Availability

### 5.1 Redis Persistence Options

```
RDB (Redis Database) — Point-in-time snapshots:
  save 900 1      # Snapshot nếu 1+ key thay đổi trong 900 giây
  save 300 10     # Snapshot nếu 10+ keys thay đổi trong 300 giây
  save 60 10000   # Snapshot nếu 10000+ keys thay đổi trong 60 giây
  
  ✅ Compact file size, fast restart
  ❌ Data loss: Khoảng cách giữa snapshots (tối đa vài phút)
  Use case: Acceptable data loss, background analytics

AOF (Append-Only File) — Log every write command:
  appendonly yes
  appendfsync everysec  # Flush to disk every second
  
  ✅ Near zero data loss (max 1 second)
  ❌ Larger file size, slower restart
  Use case: Financial data, cannot afford data loss

Hybrid (RDB + AOF):
  → Best of both worlds
  → Nên dùng trong production fintech!
```

### 5.2 Redis Cluster vs Sentinel

```
Redis Sentinel (HA, không sharding):
  - 1 Master + N Replicas
  - Sentinel monitors: Tự động failover khi master down
  - Tất cả data trên 1 node (no horizontal scale for data)
  Use case: <32GB data, cần HA đơn giản

Redis Cluster (HA + Sharding):
  - N Masters, mỗi master có replicas
  - Data tự động distributed (16384 hash slots)
  - Master 1: slots 0-5460 (key hash % 16384)
  - Master 2: slots 5461-10922
  - Master 3: slots 10923-16383
  Use case: >32GB data, cần horizontal scale

  Trade-off:
  ✅ Scale write operations
  ✅ More storage
  ❌ Multi-key operations phức tạp (keys phải cùng slot)
  ❌ CROSS-SLOT transactions không support!
```

### 5.3 🏋️ Kinh nghiệm Senior: Redis Common Issues

**Issue #1: Redis OOM (Out of Memory)**
```yaml
# redis.conf:
maxmemory 8gb              # Giới hạn memory
maxmemory-policy allkeys-lru  # Evict ít-dùng-nhất key khi đầy
# Policies:
# noeviction: Reject writes (bad for cache!)
# allkeys-lru: Evict least recently used keys (best for pure cache)
# volatile-lru: Evict LRU keys với TTL
# allkeys-random: Random eviction
# volatile-ttl: Evict keys với lowest TTL

# Alert setup:
# Monitor: used_memory vs maxmemory
# Alert khi > 80%: Cần scale up hoặc optimize
```

**Issue #2: Hot Key Problem**
```
Một key cực kỳ hot (ví dụ: "product:flash-sale-item")
→ 100,000 requests/second đến 1 shard
→ Network bandwidth của 1 shard bị exhausted
→ Mặc dù cluster có 10 nodes, chỉ 1 node bị overload!

Giải pháp:
1. Local cache (Caffeine): Trước khi hit Redis, check local cache
2. Key replication: Tạo nhiều copies: "product:flash:1", "product:flash:2"...
   Đọc: random(1..N) → Distribute load across N copies
3. Read-through với replica: Direct reads đến replica nodes
```

**Issue #3: Redis Slow Log**
```bash
# Tìm slow commands:
redis-cli SLOWLOG GET 100
# Commands > 10ms là vấn đề

# Common slow operations:
# KEYS * → O(N) scan toàn bộ keyspace → NEVER dùng in production!
# SORT → Expensive
# LRANGE với large list → O(N)

# Thay KEYS * bằng SCAN:
# SCAN cursor MATCH pattern COUNT count
# Iterative, không block server
```

---

## 🎯 TỔNG KẾT — Redis Decision Framework

```
Cần gì?                          → Dùng gì?
────────────────────────────────────────────────
Simple cache (object)            → String + JSON serialize
Object với partial update        → Hash (update từng field)
Session storage                  → String + EX
Distributed lock                 → String NX + EX + Lua delete
Rate limiting                    → String + INCR + EXPIRE (Lua)
Idempotency key                  → String NX
Queue/Job queue                  → List (RPUSH + BLPOP)
Unique set tracking              → Set
Real-time leaderboard            → Sorted Set (ZSet)
Daily active users               → Bitmap
Real-time pub/sub                → Pub/Sub channels
Delay queue                      → ZSet (score = execute timestamp)
Bloom filter                     → Native từ Redis 7 (BF.ADD, BF.EXISTS)
```

> 💡 **Senior Cache Tip**: Cache là layer bổ sung, KHÔNG phải primary storage.  
> Design hệ thống sao cho Redis down → Application vẫn hoạt động (chậm hơn nhưng đúng).  
> Redis down mà application crash → Architecture có vấn đề nghiêm trọng!
