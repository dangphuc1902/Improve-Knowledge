# 📡 Redis Pub/Sub — Toàn tập

---

## 1. Pub/Sub là gì?

**Pub/Sub = Publish / Subscribe** — mô hình messaging trong đó:
- **Publisher**: Gửi message vào một **channel** (không biết ai nhận)
- **Subscriber**: Đăng ký lắng nghe **channel** (không biết ai gửi)
- **Channel**: "Kênh" trung gian, tên tùy đặt (string)

```
Publisher                  Redis Server              Subscriber A
    │                          │                          │
    │── PUBLISH "news" "hello"►│── broadcast ────────────►│ nhận "hello"
    │                          │                          │
                               │── broadcast ────────────►│ Subscriber B
                                                          │ nhận "hello"
```

> **Key insight**: Publisher KHÔNG lưu message. Subscriber phải online mới nhận được.  
> → **Fire-and-forget**: Gửi xong là xong, không quan tâm có ai nhận không.

---

## 2. So sánh với Message Queue thông thường

| | **Pub/Sub** (Redis) | **Message Queue** (Redis List / Kafka) |
|---|---|---|
| Message lưu trữ | ❌ Không | ✅ Có (persistent) |
| Subscriber offline | ❌ Mất message | ✅ Nhận được khi online lại |
| 1 message → N receivers | ✅ Fan-out | ❌ Thường chỉ 1 consumer nhận |
| Ordering guarantee | ❌ | ✅ (Kafka partition) |
| Replay messages | ❌ | ✅ |
| Use case | Realtime broadcast | Task queue, event log |

---

## 3. Redis Pub/Sub — Cơ chế hoạt động

### 3.1 Commands cơ bản

```bash
# Terminal 1 — Subscriber lắng nghe channel "chat"
SUBSCRIBE chat
# → Waiting for messages...

# Terminal 2 — Publisher gửi message
PUBLISH chat "Hello World"
# → (integer) 1   ← số subscribers nhận được

# Terminal 1 nhận được:
# 1) "message"
# 2) "chat"           ← channel name
# 3) "Hello World"    ← message content
```

### 3.2 Pattern Subscribe (Wildcard)

```bash
# Subscribe nhiều channel cùng lúc bằng wildcard
PSUBSCRIBE order.*
# Nhận được message từ: order.created, order.updated, order.cancelled

PSUBSCRIBE user.*.notification
# Nhận được: user.123.notification, user.456.notification
```

### 3.3 Flow nội bộ của Redis

```
1. Subscriber gọi SUBSCRIBE "payments"
   → Redis lưu: { "payments": [conn1, conn2] }
   → Connection chuyển sang "subscribe mode" (chỉ nhận, không gửi lệnh khác)

2. Publisher gọi PUBLISH "payments" "txn-001"
   → Redis tìm tất cả connections subscribe "payments"
   → Push message sang từng connection ngay lập tức (synchronous)
   → Return số subscribers đã nhận

3. Nếu không có subscriber → message bị drop ngay lập tức
```

---

## 4. Java / Spring Boot Implementation

### 4.1 Dependencies (Maven)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 4.2 Publisher — Gửi message

```java
@Service
public class NotificationPublisher {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    // Publish message vào channel
    public void publishPaymentEvent(String transactionId, String status) {
        String message = String.format(
            "{\"txnId\":\"%s\", \"status\":\"%s\", \"timestamp\":%d}",
            transactionId, status, System.currentTimeMillis()
        );
        
        // PUBLISH channel message
        redisTemplate.convertAndSend("payment.events", message);
        
        log.info("Published to payment.events: {}", message);
    }
    
    // Publish với Object (auto serialize)
    public void publishUserEvent(String channel, Object event) {
        redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(event));
    }
}
```

### 4.3 Subscriber — Lắng nghe message

```java
// Step 1: Implement MessageListener
@Component
@Slf4j
public class PaymentEventListener implements MessageListener {
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());   // tên channel
        String body    = new String(message.getBody());      // nội dung message
        
        log.info("Received on [{}]: {}", channel, body);
        
        // Parse và xử lý
        PaymentEvent event = objectMapper.readValue(body, PaymentEvent.class);
        handlePaymentEvent(event);
    }
    
    private void handlePaymentEvent(PaymentEvent event) {
        if ("SUCCESS".equals(event.getStatus())) {
            // Trigger notification, update UI, etc.
        }
    }
}
```

```java
// Step 2: Config — Kết nối Listener với Channel
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }
    
    // Message container — quản lý tất cả subscriptions
    @Bean
    public RedisMessageListenerContainer messageListenerContainer(
            RedisConnectionFactory connectionFactory,
            PaymentEventListener paymentEventListener,
            OrderEventListener orderEventListener) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // Subscribe channel cụ thể
        container.addMessageListener(
            paymentEventListener,
            new ChannelTopic("payment.events")
        );
        
        // Subscribe wildcard pattern
        container.addMessageListener(
            orderEventListener,
            new PatternTopic("order.*")     // order.created, order.updated, ...
        );
        
        return container;
    }
}
```

### 4.4 Reactive (Spring WebFlux + Lettuce)

```java
// Dùng ReactiveRedisTemplate cho non-blocking
@Service
public class ReactiveNotificationService {
    
    private final ReactiveRedisMessageListenerContainer listenerContainer;
    
    public Flux<String> listenToChannel(String channel) {
        return listenerContainer
            .receive(ChannelTopic.of(channel))
            .map(message -> new String(message.getMessage()))
            .doOnNext(msg -> log.info("Received: {}", msg));
    }
    
    // Kết hợp với SSE (Server-Sent Events) để push xuống browser
    @GetMapping(value = "/stream/payments", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamPayments() {
        return listenToChannel("payment.events")
            .map(msg -> ServerSentEvent.builder(msg).build());
    }
}
```

---

## 5. Keyspace Notification — Redis tự Publish

Tính năng đặc biệt: Redis **tự động publish** event khi key thay đổi.

```bash
# Enable trong redis.conf hoặc runtime
CONFIG SET notify-keyspace-events KEA
# K = Keyspace events
# E = Keyevent events  
# A = All events (g$lzxedt)
```

```
Key "otp:user123" expire → Redis tự PUBLISH:
  Channel: "__keyevent@0__:expired"
  Message: "otp:user123"
```

```java
// Use case: Tự động xử lý khi OTP hết hạn
@Component
public class KeyExpirationListener extends KeyExpirationEventMessageListener {
    
    public KeyExpirationListener(RedisMessageListenerContainer container) {
        super(container);
    }
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        
        if (expiredKey.startsWith("otp:")) {
            String userId = expiredKey.replace("otp:", "");
            log.info("OTP expired for user: {}", userId);
            // Cleanup, notification, etc.
        }
        
        if (expiredKey.startsWith("session:")) {
            // Auto logout user
            handleSessionExpiry(expiredKey);
        }
    }
}
```

---

## 6. Real-world Use Cases

### 6.1 Realtime Chat System

```
User A                Redis                 User B, C, D
   │                    │                       │
   │─ PUBLISH "room:general" "Hello" ──────────►│ (broadcast tới mọi người trong room)
   │                    │                       │
```

```java
// Chat room implementation
@RestController
public class ChatController {
    
    // Gửi message vào phòng
    @PostMapping("/chat/rooms/{roomId}/messages")
    public void sendMessage(@PathVariable String roomId, 
                            @RequestBody ChatMessage message) {
        String channel = "chat:room:" + roomId;
        redisTemplate.convertAndSend(channel, serialize(message));
    }
    
    // Stream messages về browser (SSE)
    @GetMapping(value = "/chat/rooms/{roomId}/stream", 
                produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessage>> streamMessages(
            @PathVariable String roomId) {
        return listenToChannel("chat:room:" + roomId)
            .map(msg -> ServerSentEvent.builder(deserialize(msg)).build());
    }
}
```

### 6.2 Cache Invalidation Across Multiple Instances

```
Vấn đề: App chạy 3 instances, mỗi instance có local cache (Caffeine/Guava)
         Instance 1 update DB → local cache của nó clear
         Instance 2, 3 vẫn còn cache cũ → Stale data!

Solution: Pub/Sub để broadcast cache invalidation
```

```java
@Service
public class CacheInvalidationService {
    
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();
    
    // Khi update DB → broadcast invalidation
    public void updateUser(User user) {
        userRepository.save(user);
        localCache.invalidate("user:" + user.getId());
        
        // Notify ALL instances
        redisTemplate.convertAndSend(
            "cache.invalidate", 
            "user:" + user.getId()
        );
    }
    
    // Mỗi instance lắng nghe và clear local cache
    @Component
    class CacheInvalidationListener implements MessageListener {
        @Override
        public void onMessage(Message message, byte[] pattern) {
            String cacheKey = new String(message.getBody());
            localCache.invalidate(cacheKey);
            log.info("Cache invalidated: {}", cacheKey);
        }
    }
}
```

### 6.3 Realtime Payment Notification (MoMo context)

```java
// Payment service → publish khi transaction complete
@Service
public class PaymentService {
    
    public TransactionResult processPayment(PaymentRequest request) {
        // ... xử lý payment logic ...
        
        TransactionResult result = saveTransaction(request);
        
        // Publish realtime event
        String channel = "payment.result." + request.getUserId();
        redisTemplate.convertAndSend(channel, serialize(result));
        
        return result;
    }
}

// API Gateway / WebSocket handler lắng nghe và push xuống mobile app
@Service
public class RealtimeNotificationService {
    
    public void subscribeUserPayments(String userId, WebSocketSession session) {
        String channel = "payment.result." + userId;
        
        listenerContainer.addMessageListener(
            (message, pattern) -> {
                TransactionResult result = deserialize(message.getBody());
                session.sendMessage(new TextMessage(serialize(result)));
            },
            new ChannelTopic(channel)
        );
    }
}
```

---

## 7. Giới hạn của Redis Pub/Sub (QUAN TRỌNG cho interview)

```
┌─────────────────────────────────────────────────────────────┐
│                   LIMITATIONS                               │
│                                                             │
│  1. NO PERSISTENCE                                          │
│     Subscriber offline → message LOST forever               │
│                                                             │
│  2. NO ACKNOWLEDGMENT                                       │
│     Redis không biết subscriber xử lý thành công hay không  │
│                                                             │
│  3. NO REPLAY                                               │
│     Không xem lại lịch sử messages (khác Kafka)             │
│                                                             │
│  4. AT-MOST-ONCE DELIVERY                                   │
│     Mỗi message deliver tối đa 1 lần (có thể 0 lần)        │
│                                                             │
│  5. SINGLE REDIS NODE BOTTLENECK                            │
│     Pub/Sub không scale theo cluster dễ như Kafka           │
└─────────────────────────────────────────────────────────────┘
```

### Khi nào KHÔNG dùng Redis Pub/Sub:

| Scenario | Dùng gì thay thế |
|---|---|
| Cần đảm bảo message không mất | Redis Streams / Kafka |
| Consumer xử lý chậm, cần buffer | Redis List (LPUSH/BRPOP) |
| Cần replay event history | Kafka |
| Cần exactly-once processing | Kafka + idempotent consumer |
| Audit log, compliance | Kafka (durable) |

---

## 8. Redis Streams — Pub/Sub "Pro" Version

Redis 5.0+ có **Streams**: Pub/Sub nhưng có persistence + consumer groups

```bash
# Producer
XADD payments * txnId txn-001 amount 100000 status SUCCESS
#              ^              ^^^^^^^^^^^^^^^^^^^^^^^^^^^
#              auto ID        fields (key-value pairs)

# Consumer
XREAD COUNT 10 STREAMS payments 0
# Đọc 10 messages từ đầu stream

# Consumer Group (như Kafka consumer group)
XGROUP CREATE payments my-group $ MKSTREAM
XREADGROUP GROUP my-group consumer1 COUNT 5 STREAMS payments >
```

```java
// Spring Data Redis Streams
@Service
public class PaymentStreamService {
    
    // Publish (produce)
    public void publishPayment(TransactionResult result) {
        Map<String, String> fields = new HashMap<>();
        fields.put("txnId", result.getId());
        fields.put("amount", String.valueOf(result.getAmount()));
        fields.put("status", result.getStatus());
        
        redisTemplate.opsForStream().add("payments", fields);
    }
    
    // Consumer (với ack)
    @StreamListener
    public void consumePayment(ObjectRecord<String, TransactionResult> record) {
        try {
            processPayment(record.getValue());
            // ACK sau khi xử lý thành công
            redisTemplate.opsForStream().acknowledge("payments", "my-group", record.getId());
        } catch (Exception e) {
            // Message ở lại pending → retry
            log.error("Failed to process: {}", record.getId());
        }
    }
}
```

---

## 9. Redis Pub/Sub vs Kafka — Chọn cái nào?

```
┌────────────────────────────────────────────────────────────────┐
│                     Decision Guide                             │
│                                                                │
│  Dùng Redis Pub/Sub khi:                                       │
│  ✅ Realtime broadcast (chat, live score, notifications)        │
│  ✅ Cache invalidation giữa nhiều instances                     │
│  ✅ Message volume thấp, latency ưu tiên                       │
│  ✅ Subscriber luôn online (persistent connection)             │
│  ✅ Không cần delivery guarantee                               │
│                                                                │
│  Dùng Kafka khi:                                               │
│  ✅ Event sourcing, audit log                                   │
│  ✅ Cần replay events                                           │
│  ✅ High throughput (millions/sec)                             │
│  ✅ Multiple consumer groups xử lý độc lập                     │
│  ✅ Cần exactly-once / at-least-once guarantee                 │
│  ✅ Message lớn, consumer có thể chậm                          │
└────────────────────────────────────────────────────────────────┘
```

---

## 10. Interview Q&A

### Q: "Redis Pub/Sub khác Message Queue như thế nào?"

> "Redis Pub/Sub là broadcast model — 1 message đến N subscribers đồng thời, nhưng không lưu trữ. Message Queue (như Redis List) là point-to-point — message được buffer cho đến khi consumer lấy. Pub/Sub ưu tiên realtime, Queue ưu tiên reliability."

### Q: "Subscriber offline thì sao?"

> "Message bị mất hoàn toàn — đây là limitation chính của Pub/Sub. Nếu cần durability, dùng Redis Streams hoặc Kafka. Trong hệ thống payment, chúng tôi dùng Kafka cho event chính, còn Pub/Sub chỉ dùng cho notification UI realtime vì nếu user không online thì push notification mobile sẽ handle."

### Q: "Pub/Sub scale thế nào trong Redis Cluster?"

> "Đây là limitation quan trọng: trong Redis Cluster, PUBLISH chỉ broadcast đến subscribers trên cùng node. Để broadcast toàn cluster cần dùng Pub/Sub trên Redis Sentinel (non-cluster) hoặc implement layer routing riêng. Đây là một lý do Kafka thường được ưu tiên cho large-scale distributed systems."

---

## Tóm tắt

```
Redis Pub/Sub:
  PUBLISH channel message  → gửi đến tất cả subscribers
  SUBSCRIBE channel        → nhận messages từ channel
  PSUBSCRIBE pattern*      → nhận từ nhiều channels (wildcard)

Properties:
  ✅ Realtime, low latency
  ✅ Fan-out broadcast (1 → N)
  ❌ No persistence (offline = lost)
  ❌ No ACK, no replay
  → Best for: chat, cache invalidation, realtime UI push

Redis Streams = Pub/Sub + persistence + consumer groups
  → Dùng khi cần reliability mà không muốn setup Kafka
```
