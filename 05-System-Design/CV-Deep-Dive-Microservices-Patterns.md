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

```yaml
# application.yml — Resilience4j Circuit Breaker Config
resilience4j:
  circuitbreaker:
    instances:
      wallet-service:                           # Tên instance circuit breaker
        sliding-window-type: COUNT_BASED        # COUNT_BASED (đếm theo N call gần nhất) hoặc TIME_BASED (đếm theo N giây)
        sliding-window-size: 10                 # Lưu kết quả của 10 calls gần nhất để tính tỉ lệ lỗi
        failure-rate-threshold: 50              # Tỉ lệ lỗi >= 50% trong window → chuyển từ CLOSED sang OPEN (cắt mạch)
        slow-call-rate-threshold: 80            # 80% số request bị chậm → chuyển sang OPEN (bảo vệ service khỏi lag)
        slow-call-duration-threshold: 2000ms    # Request phản hồi > 2000ms (2s) được coi là "slow call"
        wait-duration-in-open-state: 10s        # Giữ ở trạng thái OPEN trong 10s trước khi tự chuyển sang HALF_OPEN
        permitted-number-of-calls-in-half-open-state: 3  # Cho phép thử 3 calls ở HALF_OPEN để kiểm tra downstream đã khỏe lại chưa
        minimum-number-of-calls: 5              # Phải có ít nhất 5 calls trong window mới bắt đầu tính failure rate (tránh mở nhầm khi ít request)
        automatic-transition-from-open-to-half-open-enabled: true  # Tự động chuyển sang HALF_OPEN sau khi hết 10s chờ
```

```java
@Service
public class WalletServiceClient {

    // Kết hợp nhiều resilience patterns trên 1 method:
    // @CircuitBreaker: Ngắt mạch nếu failure rate vượt ngưỡng → gọi fallbackGetBalance
    // @Retry: Tự động thử lại khi gặp lỗi tạm thời (transient error) trước khi tính là failure cho CB
    // @TimeLimiter: Giới hạn thời gian execution (gắn timeout), bắt buộc return CompletableFuture
    @CircuitBreaker(name = "wallet-service", fallbackMethod = "fallbackGetBalance")
    @Retry(name = "wallet-service")
    @TimeLimiter(name = "wallet-service")
    public CompletableFuture<BigDecimal> getBalance(Long walletId) {
        // Thực thi asynchronous call qua Feign Client
        return CompletableFuture.supplyAsync(() ->
            walletFeignClient.getBalance(walletId)
        );
    }

    // Fallback method: bắt buộc cùng kiểu trả về và nhận thêm Throwable ex ở tham số cuối
    // Được kích hoạt khi: (1) Circuit breaker OPEN (fail fast), (2) Exception xảy ra ngoài tầm retry, (3) Timeout
    public CompletableFuture<BigDecimal> fallbackGetBalance(Long walletId, 
                                                             Throwable ex) {
        log.warn("Circuit open for wallet {}, returning cached balance. Cause: {}", 
                  walletId, ex.getMessage());
        // Trả về cached value từ Redis để ứng dụng vẫn hoạt động (Graceful Degradation)
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
// Lấy thông tin trạng thái Circuit Breaker qua Registry (để monitor hoặc expose custom metrics)
CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("wallet-service");
log.info("State: {}", cb.getState()); // Trả về enum: CLOSED, OPEN, HALF_OPEN
log.info("Failure rate: {}%", cb.getMetrics().getFailureRate()); // Tỉ lệ thất bại hiện tại
log.info("Buffered calls: {}", cb.getMetrics().getNumberOfBufferedCalls()); // Số calls đang lưu trong sliding window

// Lắng nghe sự kiện chuyển đổi trạng thái (State Transition Event Listener)
cb.getEventPublisher()
    .onStateTransition(event ->
        log.warn("Circuit Breaker transitioned: {} → {}", 
                  event.getStateTransition().getFromState(), // Trạng thái cũ (VD: CLOSED)
                  event.getStateTransition().getToState())   // Trạng thái mới (VD: OPEN)
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
// WalletService Consumer trong Choreography Saga Pattern
// Nhận event khởi tạo giao dịch từ Kafka topic
@KafkaListener(topics = "transaction.initiated")
public void onTransactionInitiated(TransactionInitiatedEvent event) {
    try {
        // Thực hiện Local Transaction 1: Trừ tiền ví nguồn
        walletService.debit(event.getFromWalletId(), event.getAmount());
        // Thành công → Publish event thành công để bước tiếp theo (Credit Wallet B) lắng nghe
        eventPublisher.publish(new WalletDebitedEvent(event.getTransactionId(),
                                                       event.getFromWalletId(),
                                                       event.getAmount()));
    } catch (InsufficientFundsException ex) {
        // Thất bại → Publish event thất bại để hủy/thông báo giao dịch
        eventPublisher.publish(new WalletDebitFailedEvent(event.getTransactionId(),
                                                           "INSUFFICIENT_FUNDS"));
    }
}

// Lắng nghe event khi bước sau (Credit Wallet B) bị thất bại
@KafkaListener(topics = "wallet.credit.failed")
public void onCreditFailed(WalletCreditFailedEvent event) {
    // COMPENSATING TRANSACTION (Giao dịch bù): Hoàn lại tiền cho ví nguồn đã bị trừ trước đó
    walletService.credit(event.getFromWalletId(), event.getAmount());
    // Khôi phục lại trạng thái nhất quán và phát event báo đã hoàn tất bù trừ
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

    // Orchestrator quản lý luồng điều phối tập trung (Centralized Workflow)
    public void execute(TransferCommand command) {
        // Lưu trạng thái ban đầu của Saga vào DB để theo dõi (State Management)
        SagaState saga = sagaRepository.create(command.getTransactionId());

        try {
            // Bước 1: Trừ tiền tài khoản A (Debit)
            saga.transition(DEBITING);
            walletServiceClient.debit(command.getFromWalletId(), command.getAmount());
            saga.transition(DEBITED); // Đánh dấu Bước 1 thành công

            // Bước 2: Cộng tiền tài khoản B (Credit)
            saga.transition(CREDITING);
            walletServiceClient.credit(command.getToWalletId(), command.getAmount());
            saga.transition(CREDITED); // Đánh dấu Bước 2 thành công

            // Bước 3: Ghi nhận nhật ký sổ cái (Ledger Record)
            saga.transition(RECORDING);
            ledgerServiceClient.record(command);
            saga.transition(COMPLETED); // Saga hoàn tất thành công

        } catch (CreditFailedException ex) {
            // Nếu Bước 2 hoặc 3 thất bại → Kích hoạt COMPENSATING ACTION
            saga.transition(COMPENSATING);
            // Bù lại Bước 1: Hoàn tiền lại cho tài khoản A
            walletServiceClient.refund(command.getFromWalletId(), command.getAmount());
            saga.transition(COMPENSATED); // Đánh dấu đã hoàn tác bù trừ thành công
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
// External gRPC response model (được sinh ra từ file .proto của bên ngoài)
// message WalletBalanceResponse { string wallet_id = 1; double balance = 2; string status_code = 3; }

// Internal domain model của ứng dụng hiện tại (chuẩn DDD & Clean Architecture)
public class WalletBalance {
    private Long walletId;       // Dùng Long thay vì String cho Type Safety
    private Money balance;       // Dùng Value Object Money thay vì double để tránh lỗi làm tròn tài chính
    private WalletStatus status; // Dùng Strong-typed Enum thay vì String code linh tinh
}

// Anti-Corruption Layer (ACL): Đóng vai trò Adapter/Translator bảo vệ Core Domain
@Component
public class WalletServiceAdapter {

    @Autowired private WalletServiceGrpcStub grpcStub;

    public WalletBalance getBalance(Long walletId) {
        // 1. Map/Translate từ Nội bộ (Domain Request) → Định dạng Bên ngoài (gRPC Contract)
        BalanceRequest grpcRequest = BalanceRequest.newBuilder()
            .setWalletId(walletId.toString()) // Convert Long → String
            .build();

        // 2. Thực hiện gọi service bên ngoài
        WalletBalanceResponse grpcResponse = grpcStub.getBalance(grpcRequest);

        // 3. Map/Translate từ ĐỊNH DẠNG BÊN NGOÀI → NỘI BỘ CORE DOMAIN (Cách ly sự thay đổi)
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
// ❌ CÁCH VIẾT NGUY HIỂM (KHÔNG ĐẢM BẢO DUAL-WRITE ATOMITY):
@Transactional
public void processTransaction(Transaction tx) {
    transactionRepo.save(tx);           // Bước 1: Commit vào DB thành công ✅
    kafkaTemplate.send("tx-events", tx); // Bước 2: Bắn sang Kafka Message Broker
    // RỦI RÔ:
    // - Nếu Kafka down sau khi DB commit → Event bị MẤT HOÀN TOÀN (Ghost State)
    // - Nếu Kafka gửi OK nhưng DB bị rollback do bước sau → DUPLICATE/GHOST EVENT gửi đi
}
```

Không thể có atomicity giữa DB transaction và Kafka publish.

### 4.2 Outbox Pattern

```java
// ✅ CÁCH GIẢI QUYẾT VỚI TRANSACTIONAL OUTBOX PATTERN:
@Transactional
public void processTransaction(Transaction tx) {
    transactionRepo.save(tx);  // 1. Save data nghiệp vụ chính vào DB

    // 2. Tạo record OutboxEvent và lưu cùng bảng DB "outbox" trong CÙNG TRANSACTON HỆ THỐNG
    OutboxEvent event = OutboxEvent.builder()
        .aggregateId(tx.getId().toString())
        .aggregateType("Transaction")
        .eventType("TransactionCompleted")
        .payload(serialize(tx))
        .status(OutboxStatus.PENDING)
        .createdAt(Instant.now())
        .build();
    outboxRepo.save(event); // Lưu thành công 100% nhờ ACDB local transaction
}

// 3. Tiến trình độc lập (Background Scheduler / CDC Debezium) thực hiện gửi Message
@Scheduled(fixedDelay = 1000)
public void publishOutboxEvents() {
    // Polling lấy danh sách sự kiện chưa gửi (PENDING)
    List<OutboxEvent> pending = outboxRepo.findByStatus(OutboxStatus.PENDING);
    for (OutboxEvent event : pending) {
        try {
            // Send sang Kafka broker synchronous
            kafkaTemplate.send("tx-events", event.getPayload()).get();
            // Đánh dấu đã gửi thành công
            event.setStatus(OutboxStatus.PUBLISHED);
            outboxRepo.save(event);
        } catch (Exception ex) {
            // Nếu gửi thất bại → tăng retry counter để thử lại ở chu kỳ sau (At-Least-Once Delivery)
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
            @RequestHeader("X-Idempotency-Key") String idempotencyKey, // Idempotency key do Client tạo (ví dụ: UUID)
            @RequestBody CreateTransactionRequest request) {

        // 1. Kiểm tra xem key này đã được xử lý trước đó chưa trong Redis
        String cached = redisTemplate.opsForValue().get("idempotency:" + idempotencyKey);
        if (cached != null) {
            // Đã xử lý rồi → Trả lại ngay response cũ mà KHÔNG thực thi lại nghiệp vụ (Idempotent response)
            return ResponseEntity.ok(deserialize(cached));
        }

        // 2. Nếu chưa xử lý → Thực thi giao dịch nghiệp vụ chính
        TransactionResponse response = transactionService.process(request);

        // 3. Lưu kết quả xử lý vào Redis kèm TTL (VD: 24 giờ) để chặn các request trùng lặp sau đó
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
// Thiết lập Unique Constraint trên Database Level đối với Idempotency Key
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"idempotency_key"}) // Đảm bảo DB chặn trùng ở mức Unique Index
})
public class Transaction {
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;
}

// Handling khi xử lý Service
public TransactionResponse processTransaction(CreateTransactionRequest req) {
    try {
        // Cố gắng insert transaction mới vào Database
        Transaction tx = transactionRepo.save(buildTransaction(req));
        return mapper.toResponse(tx);
    } catch (DataIntegrityViolationException ex) {
        // Nếu vi phạm Unique Constraint (bị trùng key do 2 request gửi đồng thời)
        // → Query lại thông tin transaction đã tồn tại từ trước và trả về
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
