# Microservices Patterns — CV Deep Dive
> Circuit Breaker, Saga, Anti-Corruption Layer, Outbox Pattern, Idempotency.
> Tất cả đều có trong CV. Phải giải thích được cả mechanism lẫn trade-off.

---

## 1. Circuit Breaker — Resilience4j

### 1.1 Ba Trạng Thái

#### 🔑 Khái niệm trước khi đọc

**Cascade Failure (Lỗi dây chuyền)** = Khi 1 service bị sập, các service phụ thuộc vào nó cũng sập theo — giống đổ bài domino.

```
Gateway → gọi WalletService (bị sập)
         → mỗi request chờ timeout 30s
         → thread pool Gateway bị chiếm hết
         → Gateway sập luôn
         → tất cả service sau đó không vào được
```

**Circuit Breaker** (Động cắt) = giống cầu chì điện. Khi phát hiện downstream service liên tục fail → **cắt hẳn**, không gọi nữa (fail fast) → tránh cascade failure. Sau một thời gian, thử lại xem service đã recovered chưa.

**Fail fast** = thất bại ngay lập tức, không lãng phí thời gian chờ. Tốt hơn để request biết ngay là service unavailable thay vì chờ 30s rồi mới timeout.

**Fallback** = hành động thay thế khi service chính fail (ví dụ: trả cached data, giá trị mặc định, hoặc lỗi thân thiện).

```
         failures > threshold
CLOSED ─────────────────────→ OPEN
  ↑                              │
  │                              │ after waitDurationInOpenState
  │                              ↓
  └──────────────────────── HALF_OPEN
       success rate OK              │
                              failures → back to OPEN
```

**CLOSED (Đóng)**: Hình thường. Tất cả request đi qua. Resilience4j đo failure rate trong sliding window.

**OPEN (Mở)**: Circuit đã cắt. Tất cả request bị reject ngay lập tức với `CallNotPermittedException` (fail fast). Không gọi downstream service → tránh cascade failure.

**HALF_OPEN (Nửa mở)**: Thử một vài request xem service đã recovery chưa. Nếu success rate OK → back to CLOSED. Nếu vẫn fail → back to OPEN.

---

### 1.2 Configuration & Code (FPM Pattern)

```java
// application.yml
resilience4j:
  circuitbreaker:
    instances:
      wallet-service:
        sliding-window-type: COUNT_BASED          # COUNT_BASED | TIME_BASED
        sliding-window-size: 10                   # 10 recent calls
        failure-rate-threshold: 50                # 50% failures → OPEN
        slow-call-rate-threshold: 80              # 80% slow calls → OPEN
        slow-call-duration-threshold: 2000ms      # > 2s = slow call
        wait-duration-in-open-state: 10s          # Chờ 10s trước khi HALF_OPEN
        permitted-number-of-calls-in-half-open-state: 3
        minimum-number-of-calls: 5                # Cần ít nhất 5 calls để tính
        automatic-transition-from-open-to-half-open-enabled: true
```

```java
@Service
public class WalletServiceClient {

    @CircuitBreaker(name = "wallet-service", fallbackMethod = "fallbackGetBalance")
    @Retry(name = "wallet-service")
    @TimeLimiter(name = "wallet-service")
    public CompletableFuture<BigDecimal> getBalance(Long walletId) {
        return CompletableFuture.supplyAsync(() ->
            walletFeignClient.getBalance(walletId)
        );
    }

    // Fallback được gọi khi circuit OPEN hoặc exception xảy ra
    public CompletableFuture<BigDecimal> fallbackGetBalance(Long walletId, 
                                                             Throwable ex) {
        log.warn("Circuit open for wallet {}, returning cached balance. Cause: {}", 
                  walletId, ex.getMessage());
        // Trả về cached value hoặc default
        return CompletableFuture.supplyAsync(() ->
            redisTemplate.opsForValue().get("wallet:balance:" + walletId)
        );
    }
}
```

**Chaining Resilience4j annotations** (thứ tự thực thi từ ngoài vào trong):
```
TimeLimiter → CircuitBreaker → Retry → RateLimiter → Bulkhead → Function
```

---

### 1.3 Metrics & Monitoring

```java
// Programmatic access to state
CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("wallet-service");
log.info("State: {}", cb.getState()); // CLOSED, OPEN, HALF_OPEN
log.info("Failure rate: {}%", cb.getMetrics().getFailureRate());
log.info("Buffered calls: {}", cb.getMetrics().getNumberOfBufferedCalls());

// Event listener
cb.getEventPublisher()
    .onStateTransition(event ->
        log.warn("Circuit Breaker transitioned: {} → {}", 
                  event.getStateTransition().getFromState(),
                  event.getStateTransition().getToState())
    );
```

---

## 2. Saga Pattern — Distributed Transaction

### 2.1 Vấn Đề Cần Giải Quyết

#### 🔑 Khái niệm trước khi đọc

**ACID** = 4 tính chất của database transaction:
- **A**tomicity (Nguyên tử): Tất cả thành công hoặc tất cả rollback
- **C**onsistency (Nhất quán): Data luôn ở trạng thái hợp lệ
- **I**solation (Cô lập): Transaction riêng biệt, không ảnh hưởng nhau
- **D**urability (Bền vững): Sau khi commit, data không mất dù server crash

**Distributed Transaction (Giao dịch phân tán)** = Transaction trải qua nhiều service/database khác nhau. Vấn đề: **không thể** dùng DB transaction thông thường khi data nằm ở nhiều DB riêng biệt.

```
WalletDB (PostgreSQL)       LedgerDB (MySQL)
     │                           │
Trừ tiền Ví A             Ghi sổ cái
     │                           │
     └───── Làm sao đảm bảo cả 2 đều thành công? ─────┘
```

**Saga** = Chuỗi các local transaction. Mỗi bước commit vào DB của mình. Nếu bước nào fail → chạy **compensating transaction** (giao dịch bù) để hoàn tác các bước trước.

**Compensating Transaction** = Hành động "hoàn tác" ngược lại của một bước đã thành công. Ví dụ: Bước "Debit" → compensating = "Refund".

Trong microservices, không có distributed transaction như DB ACID. Khi cần đảm bảo tính nhất quán qua nhiều services → dùng Saga.

**Ví dụ: FPM Transfer Money**
```
User → TransactionEngine → [Step 1: Debit Wallet A] → [Step 2: Credit Wallet B] → [Step 3: Record Ledger]

Nếu Step 2 fail → phải UNDO Step 1 (compensating transaction = Refund Wallet A)
```

---


### 2.2 Choreography Saga

Services giao tiếp qua events. Không có central coordinator.

```
TransactionEngine publishes "TransactionInitiated"
          ↓
WalletService subscribes → Debit Wallet A
WalletService publishes "WalletDebited" (success) hoặc "WalletDebitFailed"
          ↓
WalletService subscribes "WalletDebited" → Credit Wallet B
WalletService publishes "WalletCredited" (success) hoặc "WalletCreditFailed"
          ↓
"WalletCreditFailed" → WalletService subscribes → Compensate: Refund Wallet A
```

```java
// WalletService consumer
@KafkaListener(topics = "transaction.initiated")
public void onTransactionInitiated(TransactionInitiatedEvent event) {
    try {
        walletService.debit(event.getFromWalletId(), event.getAmount());
        eventPublisher.publish(new WalletDebitedEvent(event.getTransactionId(),
                                                       event.getFromWalletId(),
                                                       event.getAmount()));
    } catch (InsufficientFundsException ex) {
        eventPublisher.publish(new WalletDebitFailedEvent(event.getTransactionId(),
                                                           "INSUFFICIENT_FUNDS"));
    }
}

@KafkaListener(topics = "wallet.credit.failed")
public void onCreditFailed(WalletCreditFailedEvent event) {
    // Compensating transaction: refund
    walletService.credit(event.getFromWalletId(), event.getAmount());
    eventPublisher.publish(new WalletRefundedEvent(event.getTransactionId()));
}
```

**Ưu điểm**: Loose coupling, không có single point of failure.
**Nhược điểm**: Khó debug, khó track trạng thái toàn bộ saga, cyclic dependencies.

---

### 2.3 Orchestration Saga

Central orchestrator (Saga Coordinator) điều phối từng bước.

```java
@Service
public class TransferSagaOrchestrator {

    public void execute(TransferCommand command) {
        SagaState saga = sagaRepository.create(command.getTransactionId());

        try {
            // Step 1: Debit
            saga.transition(DEBITING);
            walletServiceClient.debit(command.getFromWalletId(), command.getAmount());
            saga.transition(DEBITED);

            // Step 2: Credit
            saga.transition(CREDITING);
            walletServiceClient.credit(command.getToWalletId(), command.getAmount());
            saga.transition(CREDITED);

            // Step 3: Record
            saga.transition(RECORDING);
            ledgerServiceClient.record(command);
            saga.transition(COMPLETED);

        } catch (CreditFailedException ex) {
            // Compensate: Refund
            saga.transition(COMPENSATING);
            walletServiceClient.refund(command.getFromWalletId(), command.getAmount());
            saga.transition(COMPENSATED);
            throw new TransferFailedException(ex);
        }
    }
}
```

**Ưu điểm**: Dễ trace, centralized business logic, dễ debug.
**Nhược điểm**: Orchestrator có thể trở thành bottleneck, coupling giữa orchestrator và services.

---

## 3. Anti-Corruption Layer (ACL)

### 3.1 Vấn Đề

Khi tích hợp với external system (legacy API, third-party service), domain model của họ thường khác với domain model của bạn. Nếu dùng trực tiếp → external model "pollute" internal domain.

### 3.2 Pattern

```
Internal Domain ←→ [Anti-Corruption Layer] ←→ External System
                         (Adapters/Translators)
```

**Trong FPM Project: gRPC internal services với ACL**

```java
// External gRPC response model (proto-generated)
// message WalletBalanceResponse { string wallet_id = 1; double balance = 2; string status_code = 3; }

// Internal domain model
public class WalletBalance {
    private Long walletId;       // Long, not String
    private Money balance;       // Money value object, not double
    private WalletStatus status; // Enum, not string code
}

// ACL: Translator/Adapter
@Component
public class WalletServiceAdapter {

    @Autowired private WalletServiceGrpcStub grpcStub;

    public WalletBalance getBalance(Long walletId) {
        // 1. Translate internal request → external format
        BalanceRequest grpcRequest = BalanceRequest.newBuilder()
            .setWalletId(walletId.toString()) // Long → String
            .build();

        // 2. Call external service
        WalletBalanceResponse grpcResponse = grpcStub.getBalance(grpcRequest);

        // 3. Translate external response → internal domain
        return WalletBalance.builder()
            .walletId(Long.parseLong(grpcResponse.getWalletId()))
            .balance(Money.of(BigDecimal.valueOf(grpcResponse.getBalance()), "VND"))
            .status(WalletStatus.fromCode(grpcResponse.getStatusCode()))
            .build();
    }
}
```

**Khi nào cần ACL:**
- Integrate với legacy systems có model khác
- External API có thể thay đổi (ACL là firewall, protect internal)
- Domain model quá phức tạp để match trực tiếp

---

## 4. Transactional Outbox Pattern

### 4.1 Vấn Đề

```java
@Transactional
public void processTransaction(Transaction tx) {
    transactionRepo.save(tx);           // Step 1: Save to DB ✅
    kafkaTemplate.send("tx-events", tx); // Step 2: Publish to Kafka
    // Nếu Kafka unavailable sau khi DB commit → event bị mất ❌
    // Nếu Kafka OK nhưng DB rollback → duplicate event ❌
}
```

Không thể có atomicity giữa DB transaction và Kafka publish.

### 4.2 Outbox Pattern

```java
@Transactional
public void processTransaction(Transaction tx) {
    transactionRepo.save(tx);  // Save business data

    // Save event vào outbox table trong CÙNG transaction
    OutboxEvent event = OutboxEvent.builder()
        .aggregateId(tx.getId().toString())
        .aggregateType("Transaction")
        .eventType("TransactionCompleted")
        .payload(serialize(tx))
        .status(OutboxStatus.PENDING)
        .createdAt(Instant.now())
        .build();
    outboxRepo.save(event); // Cùng DB transaction
}

// Separate process: CDC (Change Data Capture) hoặc polling
@Scheduled(fixedDelay = 1000)
public void publishOutboxEvents() {
    List<OutboxEvent> pending = outboxRepo.findByStatus(OutboxStatus.PENDING);
    for (OutboxEvent event : pending) {
        try {
            kafkaTemplate.send("tx-events", event.getPayload()).get();
            event.setStatus(OutboxStatus.PUBLISHED);
            outboxRepo.save(event);
        } catch (Exception ex) {
            event.setRetryCount(event.getRetryCount() + 1);
            outboxRepo.save(event);
        }
    }
}
```

---

## 5. Idempotency

### 5.1 Idempotency Key Pattern (FPM)

```java
@RestController
public class TransactionController {

    @PostMapping("/api/transactions")
    public ResponseEntity<TransactionResponse> create(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody CreateTransactionRequest request) {

        // Check if already processed
        String cached = redisTemplate.opsForValue().get("idempotency:" + idempotencyKey);
        if (cached != null) {
            return ResponseEntity.ok(deserialize(cached)); // Return same response
        }

        // Process transaction
        TransactionResponse response = transactionService.process(request);

        // Store result with TTL (24 hours typical)
        redisTemplate.opsForValue().set(
            "idempotency:" + idempotencyKey,
            serialize(response),
            Duration.ofHours(24)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### 5.2 Database-level Idempotency

```java
// Unique constraint trên business key
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"idempotency_key"})
})
public class Transaction {
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;
}

// Service
public TransactionResponse processTransaction(CreateTransactionRequest req) {
    try {
        Transaction tx = transactionRepo.save(buildTransaction(req));
        return mapper.toResponse(tx);
    } catch (DataIntegrityViolationException ex) {
        // Duplicate idempotency key → return existing transaction
        Transaction existing = transactionRepo
            .findByIdempotencyKey(req.getIdempotencyKey())
            .orElseThrow();
        return mapper.toResponse(existing);
    }
}
```

---

## 6. Interview Q&A

**Q: Saga Choreography vs Orchestration — khi nào dùng cái nào?**
A: Choreography khi services đơn giản, ít steps, team muốn loose coupling. Orchestration khi business flow phức tạp, nhiều steps, cần central monitoring và rollback logic rõ ràng. Trong financial systems (như FPM) → thường chọn Orchestration vì cần traceability rõ ràng.

**Q: Circuit Breaker khác gì Retry?**
A: Retry cố gắng lại request khi fail (assume: lỗi tạm thời). Circuit Breaker stop sending requests khi service downstream overloaded (assume: service đang down). Kết hợp cả 2: Retry cho transient errors, Circuit Breaker khi failure rate cao để protect downstream.

**Q: Outbox Pattern có nhược điểm gì?**
A: At-least-once delivery → consumer cần idempotent. Có thêm table outbox và polling overhead. CDC (Debezium) phức tạp hơn polling nhưng real-time hơn.

**Q: Tại sao gRPC nhanh hơn REST?**
A: (1) Protobuf binary encoding nhỏ hơn JSON text. (2) HTTP/2 multiplexing nhiều requests trên 1 connection. (3) Strongly typed → không cần parse JSON runtime. Sub-50ms latency trong FPM đến từ combination của này.
