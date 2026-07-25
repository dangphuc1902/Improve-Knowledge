# 💳 Transaction Handling Deep Dive — Thành Công, Thất Bại, Treo

> **Mức độ**: Senior Engineer perspective  
> **Context**: Xử lý giao dịch tài chính trong hệ thống MoMo  
> **Đây là phần QUAN TRỌNG NHẤT trong phỏng vấn fintech!**

---

## PHẦN 1: Transaction State Machine

### 1.1 Trạng Thái Giao Dịch — Full Lifecycle

```
                    ┌─────────────────────────────────────────────┐
                    │            TRANSACTION STATES               │
                    └─────────────────────────────────────────────┘

                         User initiates payment
                                  │
                                  ▼
                           ┌─────────────┐
                           │   CREATED   │  Request received, validated
                           └──────┬──────┘
                                  │ Lock acquired, idempotency checked
                                  ▼
                           ┌─────────────┐
                           │   PENDING   │  In queue / waiting processing
                           └──────┬──────┘
                                  │ Processing started
                                  ▼
                          ┌──────────────┐
                          │  PROCESSING  │  Calling bank/NAPAS
                          └──────┬───────┘
                    ┌────────────┼────────────┐
                    │            │            │
                    ▼            ▼            ▼
             ┌──────────┐ ┌──────────┐ ┌──────────┐
             │ SUCCESS  │ │  FAILED  │ │ TIMEOUT  │
             └──────────┘ └────┬─────┘ └────┬─────┘
                               │            │
                               ▼            ▼
                         ┌──────────┐  ┌──────────┐
                         │ REFUNDED │  │ INQUIRY  │  Hỏi lại bank
                         └──────────┘  └────┬─────┘
                                            │
                               ┌────────────┼────────────┐
                               │            │            │
                               ▼            ▼            ▼
                          ┌────────┐  ┌────────┐  ┌──────────┐
                          │SUCCESS │  │ FAILED │  │ REVERSED │
                          └────────┘  └────────┘  └──────────┘
                          
Additional states:
RECONCILED: Đã đối soát với bank (cuối ngày)
DISPUTED: Khách hàng khiếu nại
CHARGEBACK: Bank forced reversal
```

### 1.2 Database Schema cho Transaction States

```sql
CREATE TABLE transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(128) UNIQUE NOT NULL,  -- Chống duplicate
    user_id         BIGINT NOT NULL,
    merchant_id     BIGINT,
    amount          DECIMAL(19, 4) NOT NULL,        -- Độ chính xác cao!
    currency        CHAR(3) NOT NULL DEFAULT 'VND',
    
    status          VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    -- CREATED | PENDING | PROCESSING | SUCCESS | FAILED | TIMEOUT
    -- REVERSED | REFUNDED | RECONCILED | DISPUTED
    
    status_reason   VARCHAR(500),                   -- Mô tả lý do fail
    bank_ref_id     VARCHAR(100),                   -- Bank's transaction ID
    bank_response   JSONB,                          -- Raw bank response
    
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at    TIMESTAMPTZ,                    -- Khi chuyển sang SUCCESS/FAILED
    expires_at      TIMESTAMPTZ,                    -- Sau đây → timeout
    
    retry_count     INT NOT NULL DEFAULT 0,
    version         BIGINT NOT NULL DEFAULT 0,      -- Optimistic locking!
    
    metadata        JSONB                           -- Extra data (device, IP, etc.)
);

-- Indices quan trọng:
CREATE INDEX idx_txn_user_id ON transactions(user_id);
CREATE INDEX idx_txn_status_created ON transactions(status, created_at)
    WHERE status IN ('PROCESSING', 'PENDING');  -- Partial index → nhỏ hơn, nhanh hơn
CREATE UNIQUE INDEX idx_txn_idempotency ON transactions(idempotency_key);

-- Audit table (KHÔNG BAO GIỜ xóa):
CREATE TABLE transaction_audit_log (
    id              BIGSERIAL PRIMARY KEY,
    transaction_id  UUID NOT NULL REFERENCES transactions(id),
    old_status      VARCHAR(20),
    new_status      VARCHAR(20) NOT NULL,
    changed_by      VARCHAR(100),               -- System/User/Scheduler
    reason          TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## PHẦN 2: Giao Dịch Thành Công — Happy Path

### 2.1 Full Flow với Outbox Pattern

```java
@Service
@Slf4j
public class PaymentService {

    private final TransactionRepository txnRepo;
    private final OutboxRepository outboxRepo;
    private final BankApiClient bankApi;
    private final RedisTemplate<String, String> redis;

    // ===== STEP 1: Create Transaction (Entry Point) =====
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {

        // 1. IDEMPOTENCY CHECK: Chống duplicate request
        String idempotencyKey = request.getIdempotencyKey();
        Transaction existing = txnRepo.findByIdempotencyKey(idempotencyKey);
        if (existing != null) {
            log.info("Duplicate request detected for key: {}", idempotencyKey);
            return mapToResponse(existing); // Return cached result
        }

        // 2. VALIDATE: Basic business rules
        validatePaymentRequest(request);

        // 3. DISTRIBUTED LOCK: Chống race condition cho cùng user
        String lockKey = "payment:lock:user:" + request.getUserId();
        String lockValue = acquireLock(lockKey);
        if (lockValue == null) {
            throw new ConflictException("Another payment is being processed");
        }

        try {
            // 4. CREATE TRANSACTION RECORD
            Transaction txn = Transaction.builder()
                .idempotencyKey(idempotencyKey)
                .userId(request.getUserId())
                .merchantId(request.getMerchantId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(TransactionStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .metadata(buildMetadata(request))
                .build();
            txnRepo.save(txn);

            // 5. PUBLISH EVENT (Outbox pattern - same DB transaction!)
            OutboxEvent event = OutboxEvent.builder()
                .topic("payment.initiated")
                .key(txn.getUserId().toString())
                .payload(buildInitiatedEvent(txn))
                .build();
            outboxRepo.save(event); // Same @Transactional → Atomic!

            log.info("Transaction {} created with status PENDING", txn.getId());
            return mapToResponse(txn);

        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    // ===== STEP 2: Process Payment (Async via Kafka) =====
    @KafkaListener(topics = "payment.initiated")
    @Transactional
    public void processPayment(PaymentInitiatedEvent event) {
        Transaction txn = txnRepo.findById(event.getTransactionId())
            .orElseThrow(() -> new TransactionNotFoundException(event.getTransactionId()));

        // Guard: Only process PENDING transactions
        if (txn.getStatus() != TransactionStatus.PENDING) {
            log.warn("Transaction {} is not in PENDING state ({}), skipping",
                txn.getId(), txn.getStatus());
            return;
        }

        // Update to PROCESSING (optimistic locking)
        txn.setStatus(TransactionStatus.PROCESSING);
        try {
            txnRepo.saveAndFlush(txn); // Will fail if version changed (concurrent update)
        } catch (OptimisticLockingFailureException e) {
            log.warn("Concurrent update detected for txn {}", txn.getId());
            return; // Another instance is processing it
        }

        try {
            // Call Bank API
            BankResponse bankResponse = bankApi.processPayment(
                BankRequest.from(txn),
                Duration.ofSeconds(30) // Strict timeout!
            );

            // SUCCESS
            handleSuccess(txn, bankResponse);

        } catch (BankException e) {
            // BUSINESS FAILURE: Insufficient funds, wrong OTP, etc.
            handleFailure(txn, e.getErrorCode(), e.getMessage());

        } catch (TimeoutException e) {
            // TIMEOUT: Bank không trả lời → HUNG transaction!
            handleTimeout(txn);
        }
    }

    // ===== SUCCESS HANDLER =====
    @Transactional
    private void handleSuccess(Transaction txn, BankResponse bankResponse) {
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setBankRefId(bankResponse.getReferenceId());
        txn.setBankResponse(bankResponse.toJson());
        txn.setProcessedAt(LocalDateTime.now());
        txnRepo.save(txn);

        // Outbox: Notify downstream
        outboxRepo.save(OutboxEvent.from("payment.completed", txn));

        log.info("Transaction {} completed successfully. Bank ref: {}",
            txn.getId(), bankResponse.getReferenceId());
    }
}
```

### 2.2 Optimistic Locking — Chống Race Condition

```java
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Version                    // JPA Optimistic Locking!
    private Long version;       // Auto-increment on each save
    // → Nếu 2 threads cùng save cùng version → OptimisticLockingFailureException
    // → Chỉ 1 thread win, thread kia phải retry hoặc fail gracefully

    // ... other fields
}

// Vấn đề optimistic locking:
// Thread A: Read txn (version=5, status=PENDING)
// Thread B: Read txn (version=5, status=PENDING)
// Thread A: Update status=PROCESSING, version becomes 6 → SUCCESS
// Thread B: Update status=PROCESSING, version=5 ≠ 6 → OptimisticLockingFailureException!
// → Thread B nhận exception, xử lý gracefully (skip, đã có thread A xử lý rồi)
```

---

## PHẦN 3: Giao Dịch Thất Bại — Failure Handling

### 3.1 Phân Loại Failures

```
1. BUSINESS FAILURE (Không retry):
   - Insufficient balance
   - Wrong OTP / Authentication failed
   - Account locked/blocked
   - Amount limit exceeded
   - Invalid card number / expired card
   → Set status=FAILED immediately, notify user

2. TECHNICAL FAILURE (Có thể retry):
   - Network timeout
   - Connection refused (bank temporarily down)
   - 503 Service Unavailable
   - Database connection error
   → Retry với exponential backoff

3. AMBIGUOUS FAILURE (Phải inquiry):
   - Request sent nhưng không nhận response
   - TCP connection reset sau khi send
   → KHÔNG BIẾT bank đã xử lý hay chưa!
   → Phải hỏi lại (inquiry) trước khi quyết định
```

### 3.2 Retry với Exponential Backoff

```java
@Component
public class PaymentRetryService {

    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 1000; // 1 second

    @KafkaListener(topics = "payment.retry")
    public void retryPayment(PaymentRetryEvent event) {
        Transaction txn = txnRepo.findById(event.getTransactionId()).orElseThrow();

        if (txn.getRetryCount() >= MAX_RETRIES) {
            log.error("Max retries exceeded for txn {}", txn.getId());
            handleFinalFailure(txn, "Max retries exceeded");
            return;
        }

        // Exponential backoff: 1s, 2s, 4s, 8s...
        long delayMs = BASE_DELAY_MS * (long) Math.pow(2, txn.getRetryCount());
        // Add jitter để tránh retry storm
        delayMs += (long)(Math.random() * BASE_DELAY_MS);

        log.info("Retry #{} for txn {} in {}ms",
            txn.getRetryCount() + 1, txn.getId(), delayMs);

        try {
            Thread.sleep(delayMs); // Hoặc dùng Delay Queue
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            BankResponse response = bankApi.processPayment(BankRequest.from(txn));
            handleSuccess(txn, response);

        } catch (TechnicalException e) {
            txn.setRetryCount(txn.getRetryCount() + 1);
            txnRepo.save(txn);

            // Schedule next retry
            kafka.send("payment.retry", PaymentRetryEvent.from(txn));

        } catch (BusinessException e) {
            // Business error → không retry
            handleFinalFailure(txn, e.getMessage());
        }
    }

    @Transactional
    private void handleFinalFailure(Transaction txn, String reason) {
        txn.setStatus(TransactionStatus.FAILED);
        txn.setStatusReason(reason);
        txn.setProcessedAt(LocalDateTime.now());
        txnRepo.save(txn);

        // Refund nếu money đã bị deduct
        if (txn.isMoneyDeducted()) {
            initiateRefund(txn);
        }

        // Notify user
        outboxRepo.save(OutboxEvent.from("payment.failed", txn));
    }
}
```

### 3.3 Compensating Transaction (Saga Pattern)

```
SCENARIO: Distributed transaction qua nhiều services

1. Payment Saga: Book vé máy bay
   Step 1: Reserve seat (Booking Service) ✅
   Step 2: Charge payment (Payment Service) ✅
   Step 3: Issue ticket (Ticket Service) ❌ FAILED (system error)

PROBLEM: Seat đã reserved, tiền đã charge, nhưng ticket không được issued!
→ Cần compensating transactions để rollback!

SAGA ORCHESTRATOR PATTERN:
```

```java
@Service
@Slf4j
public class BookingPaymentSaga {

    // Saga state machine
    public enum SagaStatus {
        STARTED, SEAT_RESERVED, PAYMENT_CHARGED, COMPLETED,
        COMPENSATING, SEAT_RELEASED, PAYMENT_REFUNDED, FAILED
    }

    @Transactional
    public void executeSaga(BookingRequest request) {
        SagaState saga = createSaga(request);

        try {
            // Step 1: Reserve Seat
            saga.setStatus(SagaStatus.STARTED);
            String reservationId = bookingService.reserveSeat(request.getSeatId());
            saga.setReservationId(reservationId);
            saga.setStatus(SagaStatus.SEAT_RESERVED);
            sagaRepo.save(saga);

            // Step 2: Charge Payment
            String transactionId = paymentService.charge(
                request.getUserId(), request.getAmount());
            saga.setTransactionId(transactionId);
            saga.setStatus(SagaStatus.PAYMENT_CHARGED);
            sagaRepo.save(saga);

            // Step 3: Issue Ticket
            String ticketId = ticketService.issueTicket(reservationId, transactionId);
            saga.setTicketId(ticketId);
            saga.setStatus(SagaStatus.COMPLETED);
            sagaRepo.save(saga);

            log.info("Booking saga {} completed successfully", saga.getId());

        } catch (Exception e) {
            log.error("Saga {} failed at status {}: {}", 
                saga.getId(), saga.getStatus(), e.getMessage());
            compensate(saga);
        }
    }

    @Transactional
    private void compensate(SagaState saga) {
        saga.setStatus(SagaStatus.COMPENSATING);
        sagaRepo.save(saga);

        // COMPENSATE IN REVERSE ORDER:

        // Compensate Step 2: Refund payment (if charged)
        if (saga.getTransactionId() != null && 
            saga.getStatus().ordinal() >= SagaStatus.PAYMENT_CHARGED.ordinal()) {
            try {
                paymentService.refund(saga.getTransactionId());
                saga.setStatus(SagaStatus.PAYMENT_REFUNDED);
            } catch (Exception e) {
                log.error("Failed to refund txn {}", saga.getTransactionId(), e);
                // Schedule manual intervention!
                alertService.criticalAlert(
                    "MANUAL REFUND NEEDED", saga.getTransactionId());
            }
        }

        // Compensate Step 1: Release seat reservation (if reserved)
        if (saga.getReservationId() != null) {
            try {
                bookingService.cancelReservation(saga.getReservationId());
                saga.setStatus(SagaStatus.SEAT_RELEASED);
            } catch (Exception e) {
                log.error("Failed to cancel reservation {}", saga.getReservationId(), e);
                alertService.criticalAlert(
                    "MANUAL CANCELLATION NEEDED", saga.getReservationId());
            }
        }

        saga.setStatus(SagaStatus.FAILED);
        sagaRepo.save(saga);
    }
}
```

---

## PHẦN 4: Giao Dịch Treo (Hung/Timeout Transactions)

### 4.1 Tại sao Transaction bị Treo?

```
CAUSES:
1. Network Timeout: Request gửi đến bank → TCP connection lost
   → Không biết bank đã nhận chưa
   → AMBIGUOUS STATE: Có thể SUCCESS hoặc FAILED phía bank

2. Bank System Downtime: NAPAS maintenance, bank's system slow
   → Không nhận response trong 30 giây

3. Application Bug: Exception không được catch
   → Service crash giữa chừng → Transaction mãi ở PROCESSING

4. Kafka Lag: Event published nhưng consumer chưa process
   → Transaction ở PENDING quá lâu

5. Database Connection Exhausted: Transaction chờ commit nhưng connection pool full
   → Deadlock/timeout ở DB level

HẬU QUẢ:
- User thấy: "Đang xử lý..." mãi mãi
- Tiền có thể bị deduct (phía ví) nhưng không đến merchant
- Báo cáo tài chính sai
- User complaint → Bad UX, trust issue
```

### 4.2 Detection: Scheduled Job Tìm Hung Transactions

```java
@Service
@Slf4j
public class HungTransactionDetector {

    private final TransactionRepository txnRepo;
    private final BankApiClient bankApi;
    private final PaymentService paymentService;
    private final AlertService alertService;

    // Chạy mỗi 1 phút
    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void detectAndHandleHungTransactions() {
        // Tìm transactions đang ở PROCESSING quá lâu
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        
        List<Transaction> hungTransactions = txnRepo.findHungTransactions(
            TransactionStatus.PROCESSING,
            cutoffTime,
            PageRequest.of(0, 100) // Process 100 at a time
        );

        log.info("Found {} hung transactions", hungTransactions.size());

        for (Transaction txn : hungTransactions) {
            try {
                handleHungTransaction(txn);
            } catch (Exception e) {
                log.error("Error handling hung transaction {}", txn.getId(), e);
            }
        }
    }

    @Transactional
    private void handleHungTransaction(Transaction txn) {
        log.warn("Processing hung transaction {} (status: {}, created: {})",
            txn.getId(), txn.getStatus(), txn.getCreatedAt());

        if (txn.getBankRefId() != null) {
            // Case 1: Request đã gửi đến bank → Inquiry
            inquireWithBank(txn);
        } else {
            // Case 2: Request chưa kịp gửi đến bank → Fail safe
            markAsFailedSafely(txn, "Processing timeout without bank contact");
        }
    }

    private void inquireWithBank(Transaction txn) {
        try {
            BankInquiryResponse inquiry = bankApi.inquireTransaction(
                txn.getBankRefId(),
                Duration.ofSeconds(10)
            );

            switch (inquiry.getStatus()) {
                case SUCCESS:
                    log.info("Inquiry: txn {} was SUCCESS at bank", txn.getId());
                    paymentService.handleSuccess(txn, inquiry.toBankResponse());
                    break;

                case FAILED:
                    log.info("Inquiry: txn {} was FAILED at bank: {}",
                        txn.getId(), inquiry.getFailureReason());
                    paymentService.handleFinalFailure(txn, inquiry.getFailureReason());
                    break;

                case PENDING:
                case UNKNOWN:
                    // Bank vẫn chưa xử lý xong hoặc không biết
                    if (txn.getRetryCount() < 3) {
                        txn.setRetryCount(txn.getRetryCount() + 1);
                        txnRepo.save(txn);
                        log.info("Bank inquiry for {} returned {}, retry {}/3",
                            txn.getId(), inquiry.getStatus(), txn.getRetryCount());
                    } else {
                        // After 3 inquiries → Auto-reverse
                        log.warn("Transaction {} exceeded max inquiries, auto-reversing",
                            txn.getId());
                        autoReverse(txn);
                    }
                    break;
            }

        } catch (Exception e) {
            log.error("Inquiry failed for txn {}", txn.getId(), e);
            // Inquiry itself failed → Alert team
            alertService.urgentAlert("INQUIRY_FAILED", txn.getId().toString(),
                "Manual investigation required");
        }
    }

    @Transactional
    private void autoReverse(Transaction txn) {
        // Hoàn tiền cho user vì không xác định được trạng thái
        txn.setStatus(TransactionStatus.REVERSED);
        txn.setStatusReason("Auto-reversed: Unable to confirm bank status after 3 inquiries");
        txnRepo.save(txn);

        // Hoàn tiền
        walletService.refund(txn.getUserId(), txn.getAmount(), txn.getId().toString());

        // Audit log
        auditService.log(txn, "AUTO_REVERSED", 
            "System auto-reversed hung transaction");

        // Alert ops team
        alertService.urgentAlert("AUTO_REVERSED", txn.getId().toString(),
            "Transaction auto-reversed - manual bank verification needed");

        // Notify user
        notificationService.sendHungTransactionNotification(txn.getUserId(), txn);
    }
}
```

### 4.3 Reconciliation — Đối Soát Cuối Ngày

```java
@Service
@Slf4j
public class ReconciliationService {

    // Chạy lúc 2:00 AM hàng ngày
    @Scheduled(cron = "0 0 2 * * *")
    public void dailyReconciliation() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Starting reconciliation for {}", yesterday);

        try {
            performReconciliation(yesterday);
        } catch (Exception e) {
            log.error("Reconciliation failed for {}", yesterday, e);
            alertService.criticalAlert("RECONCILIATION_FAILED", yesterday.toString());
        }
    }

    private void performReconciliation(LocalDate date) {
        // 1. Download bank statement
        List<BankStatement> bankStatements = bankApi.downloadStatement(date);
        log.info("Downloaded {} bank records", bankStatements.size());

        // 2. Load our records for same day
        List<Transaction> ourTransactions = txnRepo.findByDate(date,
            TransactionStatus.SUCCESS, TransactionStatus.FAILED,
            TransactionStatus.REVERSED);
        log.info("Found {} of our records", ourTransactions.size());

        // 3. Build maps for efficient lookup
        Map<String, BankStatement> bankMap = bankStatements.stream()
            .collect(Collectors.toMap(BankStatement::getRefId, identity()));

        Map<String, Transaction> ourMap = ourTransactions.stream()
            .filter(t -> t.getBankRefId() != null)
            .collect(Collectors.toMap(Transaction::getBankRefId, identity()));

        List<ReconciliationDiscrepancy> discrepancies = new ArrayList<>();

        // 4. Check: Có trong bank nhưng không có ở ta
        bankStatements.stream()
            .filter(bank -> !ourMap.containsKey(bank.getRefId()))
            .forEach(bank -> {
                discrepancies.add(ReconciliationDiscrepancy.builder()
                    .type(DiscrepancyType.MISSING_IN_OUR_SYSTEM)
                    .bankRefId(bank.getRefId())
                    .bankAmount(bank.getAmount())
                    .severity(Severity.CRITICAL) // Critical! Money at risk
                    .build());
            });

        // 5. Check: Có ở ta nhưng không có ở bank
        ourMap.entrySet().stream()
            .filter(entry -> !bankMap.containsKey(entry.getKey()))
            .filter(entry -> entry.getValue().getStatus() == TransactionStatus.SUCCESS)
            .forEach(entry -> {
                discrepancies.add(ReconciliationDiscrepancy.builder()
                    .type(DiscrepancyType.MISSING_IN_BANK)
                    .transactionId(entry.getValue().getId())
                    .ourAmount(entry.getValue().getAmount())
                    .severity(Severity.HIGH)
                    .build());
            });

        // 6. Check: Amount mismatch
        ourMap.entrySet().stream()
            .filter(entry -> bankMap.containsKey(entry.getKey()))
            .filter(entry -> entry.getValue().getStatus() == TransactionStatus.SUCCESS)
            .filter(entry -> {
                BigDecimal ourAmt = entry.getValue().getAmount();
                BigDecimal bankAmt = bankMap.get(entry.getKey()).getAmount();
                return ourAmt.compareTo(bankAmt) != 0;
            })
            .forEach(entry -> {
                BankStatement bank = bankMap.get(entry.getKey());
                discrepancies.add(ReconciliationDiscrepancy.builder()
                    .type(DiscrepancyType.AMOUNT_MISMATCH)
                    .transactionId(entry.getValue().getId())
                    .ourAmount(entry.getValue().getAmount())
                    .bankAmount(bank.getAmount())
                    .severity(Severity.CRITICAL) // Amount different = serious!
                    .build());
            });

        // 7. Save discrepancies và alert
        if (!discrepancies.isEmpty()) {
            discrepancyRepo.saveAll(discrepancies);
            
            long criticalCount = discrepancies.stream()
                .filter(d -> d.getSeverity() == Severity.CRITICAL).count();
            
            if (criticalCount > 0) {
                alertService.criticalAlert(
                    "RECONCILIATION_DISCREPANCY",
                    String.format("%d critical discrepancies found for %s", 
                        criticalCount, date)
                );
            }
        }

        // 8. Save reconciliation report
        ReconciliationReport report = ReconciliationReport.builder()
            .date(date)
            .bankRecordCount(bankStatements.size())
            .ourRecordCount(ourTransactions.size())
            .discrepancyCount(discrepancies.size())
            .status(discrepancies.isEmpty() ? "CLEAN" : "HAS_DISCREPANCIES")
            .build();
        reportRepo.save(report);

        log.info("Reconciliation for {} completed. {} discrepancies found",
            date, discrepancies.size());
    }
}
```

---

## PHẦN 5: Idempotency — Trái Tim của Hệ Thống Payment

### 5.1 Tại Sao Idempotency Critical?

```
SCENARIO:
1. Client POST /payments với amount=100,000
2. Network timeout sau 29s (server đang xử lý)
3. Client nghĩ request fail → Retry: POST /payments (cùng amount)
4. Server đã xử lý xong lần đầu VÀ đang xử lý lần hai
→ User bị charge 200,000 thay vì 100,000!

FINTECH NIGHTMARE: Double charge → User complaint → Refund → Trust loss

SOLUTION: Idempotency Key
Client tự generate UUID per request
Nếu retry → dùng cùng UUID → Server detect duplicate → Return same result
```

### 5.2 Full Idempotency Implementation

```java
@Service
public class IdempotencyService {

    private final RedisTemplate<String, String> redis;
    private final IdempotencyKeyRepository keyRepo; // DB backup

    private static final String PROCESSING = "PROCESSING";
    private static final Duration LOCK_TTL = Duration.ofMinutes(5);
    private static final Duration RESULT_TTL = Duration.ofDays(1);

    /**
     * Start processing. Return true nếu first time, false nếu duplicate.
     * Caller should check result and return cached response for duplicates.
     */
    public IdempotencyResult checkAndLock(String idempotencyKey) {
        String redisKey = "idempotency:" + idempotencyKey;

        // Check if already processed (or processing)
        String existing = redis.opsForValue().get(redisKey);

        if (PROCESSING.equals(existing)) {
            // Another request is currently processing → Wait a bit and check again
            return IdempotencyResult.processing();
        }

        if (existing != null && !PROCESSING.equals(existing)) {
            // Already completed → Return cached result
            IdempotencyResponse cachedResponse = 
                jsonMapper.readValue(existing, IdempotencyResponse.class);
            return IdempotencyResult.duplicate(cachedResponse);
        }

        // First time: Acquire lock
        Boolean acquired = redis.opsForValue()
            .setIfAbsent(redisKey, PROCESSING, LOCK_TTL);

        if (!Boolean.TRUE.equals(acquired)) {
            // Race condition: Another thread just acquired
            return IdempotencyResult.processing();
        }

        return IdempotencyResult.firstTime();
    }

    /**
     * Save processing result for future duplicate requests
     */
    public void saveResult(String idempotencyKey, PaymentResponse response) {
        String redisKey = "idempotency:" + idempotencyKey;
        String serialized = jsonMapper.writeValueAsString(response);

        // Update Redis with result (extend TTL)
        redis.opsForValue().set(redisKey, serialized, RESULT_TTL);

        // Also save to DB for durability (Redis có thể restart)
        keyRepo.save(IdempotencyKey.builder()
            .key(idempotencyKey)
            .responsePayload(serialized)
            .expiresAt(LocalDateTime.now().plus(RESULT_TTL))
            .build());
    }

    /**
     * Release lock on failure (allow retry)
     */
    public void releaseLock(String idempotencyKey) {
        redis.delete("idempotency:" + idempotencyKey);
    }
}

// Controller usage:
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestHeader(value = "Idempotency-Key", required = true) 
            String idempotencyKey,
            @RequestBody @Valid PaymentRequest request) {

        // Validate idempotency key format (UUID v4)
        validateIdempotencyKey(idempotencyKey);

        // Check idempotency
        IdempotencyResult result = idempotencyService.checkAndLock(idempotencyKey);

        if (result.isDuplicate()) {
            log.info("Duplicate request for key: {}", idempotencyKey);
            return ResponseEntity.ok(result.getCachedResponse());
        }

        if (result.isProcessing()) {
            // Still processing → Tell client to retry after a bit
            return ResponseEntity.status(202) // Accepted
                .header("Retry-After", "2")
                .build();
        }

        // First time processing
        try {
            PaymentResponse response = paymentService.processPayment(request);
            idempotencyService.saveResult(idempotencyKey, response);
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            // On failure, release lock so client can retry
            idempotencyService.releaseLock(idempotencyKey);
            throw e;
        }
    }
}
```

---

## PHẦN 6: ACID trong Payment Context

### 6.1 ACID Properties và Payment

```
ATOMICITY: Giao dịch thực hiện tất cả hoặc không gì cả
  Ví dụ: Chuyển tiền = Debit A + Credit B
  Nếu Credit B fail → Debit A phải rollback
  → @Transactional trong Spring đảm bảo điều này

CONSISTENCY: DB luôn ở trạng thái hợp lệ
  Ví dụ: Tổng số dư của tất cả accounts = constant
  → Business invariants phải được giữ sau mỗi transaction

ISOLATION: Các transaction không ảnh hưởng lẫn nhau
  Ví dụ: User A và User B cùng transfer cùng lúc
  → Mỗi transaction thấy consistent snapshot của data

DURABILITY: Committed transaction sẽ persist dù có crash
  Ví dụ: Transaction SUCCESS → Restart server → Vẫn SUCCESS
  → WAL (Write-Ahead Log) đảm bảo điều này
```

### 6.2 Isolation Levels trong Payment

```sql
-- Ví dụ: 2 transactions cùng đọc và cập nhật balance

-- READ COMMITTED (default trong PostgreSQL):
-- Transaction A: Read balance = 1,000,000
-- Transaction B: Update balance = 500,000, COMMIT
-- Transaction A: Read balance lại = 500,000 (khác lần đầu!)
-- → NON-REPEATABLE READ → Vấn đề trong inventory check!

-- REPEATABLE READ:
-- Transaction A: Read balance = 1,000,000
-- Transaction B: Update balance = 500,000, COMMIT
-- Transaction A: Read balance lại = 1,000,000 (cùng snapshot!)
-- → Giải quyết Non-repeatable Read

-- SERIALIZABLE (Highest):
-- Chậm nhất nhưng hoàn toàn isolated
-- Payment transactions thường cần ít nhất REPEATABLE READ

-- Optimistic Locking (Alternative):
@Transactional(isolation = Isolation.READ_COMMITTED)
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    Account from = accountRepo.findByIdWithLock(fromId);
    // SELECT ... FOR UPDATE → Pessimistic lock (chỉ 1 transaction có thể access)
    
    if (from.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException();
    }
    
    from.setBalance(from.getBalance().subtract(amount));
    accountRepo.save(from); // version check → OptimisticLockingFailure nếu concurrent
    
    Account to = accountRepo.findById(toId).orElseThrow();
    to.setBalance(to.getBalance().add(amount));
    accountRepo.save(to);
}
```

### 6.3 Deadlock Prevention

```java
// DEADLOCK SCENARIO:
// Thread A: Lock account 1 → Try lock account 2
// Thread B: Lock account 2 → Try lock account 1
// → Deadlock!

// SOLUTION: Luôn lock theo ORDER CỐ ĐỊNH:
@Transactional
public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    // QUAN TRỌNG: Lock theo thứ tự ID tăng dần
    Long first = Math.min(fromAccountId, toAccountId);
    Long second = Math.max(fromAccountId, toAccountId);
    
    Account firstAccount = accountRepo.findByIdWithLock(first);   // SELECT FOR UPDATE
    Account secondAccount = accountRepo.findByIdWithLock(second); // SELECT FOR UPDATE
    
    Account from = fromAccountId.equals(first) ? firstAccount : secondAccount;
    Account to = fromAccountId.equals(first) ? secondAccount : firstAccount;
    
    // Now transfer safely
    from.debit(amount);
    to.credit(amount);
    
    accountRepo.save(from);
    accountRepo.save(to);
}

// JPA Repository:
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Account a WHERE a.id = :id")
Optional<Account> findByIdWithLock(@Param("id") Long id);
```

---

## 🎯 TỔNG KẾT — Checklist cho Senior Interview

```
Khi được hỏi về "xử lý giao dịch", đảm bảo cover:

1. ✅ State Machine: Các trạng thái và transition rules
2. ✅ Idempotency: UUID key, Redis SET NX, cached response
3. ✅ Optimistic Locking: @Version để prevent concurrent update
4. ✅ Outbox Pattern: Atomic DB write + Kafka publish
5. ✅ Retry Logic: Exponential backoff, max retries, classify errors
6. ✅ Hung Detection: Scheduled job, inquiry API, auto-reverse
7. ✅ Reconciliation: Daily job, 3 types of discrepancies
8. ✅ Saga Pattern: Compensating transactions cho distributed TX
9. ✅ ACID: Understand isolation levels in payment context
10. ✅ Deadlock Prevention: Lock ordering

Số liệu cần biết:
- Bank API timeout: 30s typical
- Hung detection threshold: 5-10 phút
- Max retries: 3 lần
- Reconciliation: Hàng ngày lúc ít traffic (2-4 AM)
- Idempotency key TTL: 24 giờ (đủ để client retry trong ngày)
```

> 💡 **Senior Fintech Insight**: Trong payment system, mọi thứ có thể fail.  
> "Happy path" là exceptional case; mọi code phải viết với giả định "what if this fails?".  
> Câu hỏi hay nhất để tự review code: *"Nếu server crash ngay dòng này, điều gì xảy ra?"*
