# 🔄 Distributed Transactions & Resilience Patterns

> **Phase:** 2 | **Time Block:** T7 13:30-15:30  
> **Quan trọng cho:** MoMo, VNPay, NAB, ZaloPay — Fintech cần consistency patterns

---

## 1. Distributed Transaction Patterns

### Tại sao không dùng 2PC?
```
2PC (Two-Phase Commit):
  Coordinator → Prepare all → Commit all
  
Vấn đề:
  - Blocking protocol — services lock resources chờ coordinator
  - Single point of failure — coordinator chết → tất cả block
  - Latency cao — multiple network round-trips
  - Không scale — tight coupling giữa services
  
→ Microservices KHÔNG dùng 2PC. Dùng Saga pattern thay thế.
```

### Saga Pattern — Choreography
```
Order Created → [Kafka] → Payment Service
                              ↓ Payment Success
                          [Kafka] → Inventory Service
                                        ↓ Stock Reserved
                                    [Kafka] → Notification Service
                                                    ↓ Email Sent
                                                [Kafka] → Order Service (COMPLETED)

Compensation (if failure):
Payment Failed → [Kafka] → Order Service (CANCELLED)
Stock Failed   → [Kafka] → Payment Service (REFUND) → Order Service (CANCELLED)
```

```java
// Choreography — mỗi service listen và publish events
@KafkaListener(topics = "order-created")
public void handleOrderCreated(OrderCreatedEvent event) {
    try {
        paymentService.processPayment(event.getUserId(), event.getAmount());
        kafkaTemplate.send("payment-success", new PaymentSuccessEvent(event.getOrderId()));
    } catch (Exception e) {
        kafkaTemplate.send("payment-failed", new PaymentFailedEvent(event.getOrderId(), e.getMessage()));
    }
}
```

### Saga Pattern — Orchestration
```java
// Orchestrator controls the flow
@Service
public class OrderSagaOrchestrator {
    
    public void execute(CreateOrderCommand command) {
        SagaBuilder.newSaga()
            .step("Create Order", () -> orderService.create(command))
            .compensate(() -> orderService.cancel(command.getOrderId()))
            
            .step("Process Payment", () -> paymentClient.charge(command))
            .compensate(() -> paymentClient.refund(command.getOrderId()))
            
            .step("Reserve Stock", () -> inventoryClient.reserve(command))
            .compensate(() -> inventoryClient.release(command.getOrderId()))
            
            .step("Send Notification", () -> notificationClient.send(command))
            // No compensation needed — notifications are idempotent
            
            .execute();
    }
}
```

### Choreography vs Orchestration
| Aspect | Choreography | Orchestration |
|:-------|:-------------|:--------------|
| Coupling | Loose — services independent | Tighter — orchestrator knows all |
| Visibility | Hard to trace flow | Easy — single orchestrator |
| Complexity | Simple per service | Complex orchestrator |
| Error handling | Distributed, hard to debug | Centralized, easier |
| Best for | Simple flows (2-3 steps) | Complex flows (4+ steps) |

---

## 2. Transactional Outbox Pattern

**Problem:** Service cần update DB VÀ publish event atomically. Nếu DB commit nhưng Kafka publish fail → inconsistency.

```
┌──────────────────────────────────────────────┐
│ Order Service                                │
│                                              │
│  @Transactional                              │
│  void createOrder(Order order) {             │
│    orderRepository.save(order);        ──┐   │
│    outboxRepository.save(                │   │
│      new OutboxEvent("order-created",    │   │ SAME DB TRANSACTION
│        order));                           │   │
│  }                                     ──┘   │
│                                              │
│  // Separate process (CDC/Polling)           │
│  OutboxPoller → Read outbox table            │
│              → Publish to Kafka              │
│              → Mark as published             │
└──────────────────────────────────────────────┘
```

```sql
-- Outbox table
CREATE TABLE outbox_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_type VARCHAR(100),
    aggregate_id VARCHAR(100),
    event_type VARCHAR(100),
    payload JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published BOOLEAN DEFAULT FALSE
);
```

### CDC (Change Data Capture) — Debezium
```
Database binlog → Debezium → Kafka → Consumers
```
- No polling overhead
- Guaranteed exactly-once delivery (at DB level)
- Used by: MoMo, VNPay cho payment events

---

## 3. Resilience Patterns — Resilience4j

### Circuit Breaker
```java
// States: CLOSED → OPEN → HALF_OPEN → CLOSED
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
public PaymentResponse processPayment(PaymentRequest request) {
    return paymentClient.charge(request);
}

public PaymentResponse paymentFallback(PaymentRequest request, Exception e) {
    // Graceful degradation
    return PaymentResponse.pending("Payment queued for retry");
}
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        failure-rate-threshold: 50        # Open after 50% failures
        wait-duration-in-open-state: 30s  # Wait 30s before half-open
        sliding-window-size: 10           # Evaluate last 10 calls
        minimum-number-of-calls: 5        # Need 5 calls minimum
        permitted-number-of-calls-in-half-open-state: 3
```

### Retry
```java
@Retry(name = "paymentRetry", fallbackMethod = "retryFallback")
public PaymentResponse processWithRetry(PaymentRequest request) {
    return paymentClient.charge(request);
}
```

```yaml
resilience4j:
  retry:
    instances:
      paymentRetry:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2  # 1s → 2s → 4s
        retry-exceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
        ignore-exceptions:
          - com.example.BusinessException  # Don't retry business errors
```

### Rate Limiter
```java
@RateLimiter(name = "loginRateLimiter", fallbackMethod = "rateLimitFallback")
public AuthResponse login(LoginRequest request) {
    return authService.authenticate(request);
}
```

```yaml
resilience4j:
  ratelimiter:
    instances:
      loginRateLimiter:
        limit-for-period: 5           # 5 requests
        limit-refresh-period: 5m      # per 5 minutes
        timeout-duration: 0s          # Fail immediately if exceeded
```

### Bulkhead (Thread Isolation)
```java
@Bulkhead(name = "heavyService", fallbackMethod = "bulkheadFallback")
public Report generateReport(ReportRequest request) {
    return reportService.generate(request);
}
```

```yaml
resilience4j:
  bulkhead:
    instances:
      heavyService:
        max-concurrent-calls: 10  # Max 10 parallel calls
        max-wait-duration: 500ms  # Wait max 500ms for a slot
```

### Combining Patterns
```java
// Order matters! Retry → CircuitBreaker → RateLimiter → Bulkhead
@Bulkhead(name = "payment")
@RateLimiter(name = "payment")
@CircuitBreaker(name = "payment", fallbackMethod = "fallback")
@Retry(name = "payment")
public PaymentResponse process(PaymentRequest req) {
    return paymentClient.charge(req);
}
```

> **Relate FPM Project:** Resilience4j Circuit Breaker (50% threshold, 30s open), Token Bucket rate limiting (5 req/5min login, 100 req/s standard), RabbitMQ retry with backoff.

---

## 4. Idempotency

```java
// Idempotency key — prevent duplicate processing
@PostMapping("/payments")
public PaymentResponse processPayment(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody PaymentRequest request) {
    
    // Check if already processed
    Optional<PaymentResponse> cached = redis.get("idempotency:" + idempotencyKey);
    if (cached.isPresent()) return cached.get();
    
    // Process
    PaymentResponse response = paymentService.process(request);
    
    // Cache result with TTL
    redis.set("idempotency:" + idempotencyKey, response, Duration.ofHours(24));
    return response;
}
```

---

## Câu Hỏi Phỏng Vấn

### Q1: Saga vs 2PC — tại sao microservices dùng Saga?
**A:** 2PC là blocking protocol (lock resources), single point of failure (coordinator), tight coupling, không scale. Saga là non-blocking, mỗi service commit local transaction rồi publish event. Trade-off: Saga chấp nhận eventual consistency thay vì strong consistency.

### Q2: Choreography vs Orchestration Saga?
**A:** Choreography: mỗi service listen/publish events, loose coupling, nhưng hard to trace flow. Orchestration: central coordinator, easy to understand/debug, nhưng single point of failure. Simple flows (2-3 steps) → choreography. Complex flows (4+) → orchestration.

### Q3: Circuit Breaker states?
**A:** CLOSED (normal) → count failures → khi failure rate > threshold → OPEN (reject all, return fallback) → sau wait-duration → HALF_OPEN (allow limited requests) → nếu success → CLOSED, nếu fail → OPEN lại.

### Q4: Outbox pattern giải quyết vấn đề gì?
**A:** Dual-write problem: update DB + publish message phải atomic. Outbox: save event vào DB cùng transaction với business data → separate process (CDC/polling) publish to Kafka. Guaranteed consistency between DB and message broker.

### Q5: Idempotency quan trọng thế nào trong distributed systems?
**A:** Network failures → retry → duplicate messages. Idempotency đảm bảo xử lý nhiều lần cùng request cho cùng kết quả. Implementation: idempotency key + Redis cache, hoặc database unique constraint.
