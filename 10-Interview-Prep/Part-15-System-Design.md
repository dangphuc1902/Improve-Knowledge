# PART 15 - SYSTEM DESIGN

> **Level**: Java Backend Engineer, 2-5 Years Experience
> **Format**: Requirements → Database → API → Architecture → Scaling

---

## DESIGN 1: URL Shortener (like bit.ly)

### Requirements
**Functional:**
- Given a long URL, generate a short URL (e.g., `sho.rt/abc123`)
- Redirect from short URL to original long URL
- Custom alias support (optional)
- URL expiration (optional)
- Analytics: click count, country, device

**Non-Functional:**
- 100M URLs shortened per day (write: ~1200/sec)
- 10:1 read/write ratio → 12,000 redirects/sec
- 99.99% availability
- Redirect latency < 10ms (P99)
- Short URL length: 7 characters (Base62: 62^7 = 3.5 trillion)

### Database Design
```sql
-- URLs table
CREATE TABLE urls (
    id          BIGINT PRIMARY KEY,  -- Auto-increment
    short_code  VARCHAR(10) UNIQUE NOT NULL,  -- 'abc1234'
    long_url    TEXT NOT NULL,
    user_id     BIGINT,              -- Optional: if user auth exists
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP,
    click_count BIGINT DEFAULT 0
);
CREATE INDEX idx_urls_short_code ON urls(short_code);  -- Most critical!
CREATE INDEX idx_urls_user_id ON urls(user_id);

-- Analytics table (high write volume - consider separate service)
CREATE TABLE url_analytics (
    id          BIGINT PRIMARY KEY,
    url_id      BIGINT REFERENCES urls(id),
    clicked_at  TIMESTAMP NOT NULL,
    ip_address  INET,
    country     VARCHAR(2),
    device_type VARCHAR(20),
    referer     TEXT
);
-- Use TimescaleDB or ClickHouse for analytics at scale
```

### API Design
```
POST /api/v1/urls
Request:  { "longUrl": "https://...", "alias": "my-link", "expiresAt": "2025-12-31" }
Response: { "shortUrl": "https://sho.rt/abc1234", "shortCode": "abc1234" }

GET /{shortCode}
Response: HTTP 301 Redirect to longUrl  (or 302 for analytics)

GET /api/v1/urls/{shortCode}/analytics
Response: { "clicks": 1000, "countries": {...}, "devices": {...} }

DELETE /api/v1/urls/{shortCode}
Response: 204 No Content
```

### Architecture
```
Internet
    ↓
[CloudFront CDN] ← Cache redirects at edge (301 = cached, 302 = not cached)
    ↓
[Load Balancer]
    ↓
[API Gateway]
    ↓              ↓
[URL Service]  [Analytics Service]
    ↓                    ↓
[Redis Cache]      [Kafka → ClickHouse]
    ↓
[PostgreSQL]
```

### Short Code Generation
```java
// Option 1: Base62 encoding of auto-increment ID
public String encode(long id) {
    String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder();
    while (id > 0) {
        sb.append(chars.charAt((int)(id % 62)));
        id /= 62;
    }
    return sb.reverse().toString();
}
// Downside: sequential codes reveal order

// Option 2: Random UUID first 7 chars + collision check
// Option 3: Distributed ID generator (Snowflake)
```

### Scaling Strategy
1. **Read scaling**: Redis cache (90% cache hit rate expected), CDN for 301 redirects
2. **Write scaling**: DB partitioning by short_code hash
3. **Analytics**: Async via Kafka → batch processing → ClickHouse
4. **ID generation**: Snowflake or single ID generation service

---

## DESIGN 2: E-Commerce Backend

### Requirements
**Functional:**
- Product catalog (browse, search, filter)
- Shopping cart (add/remove/update)
- Order placement and management
- Payment processing
- Inventory management
- User accounts

**Non-Functional:**
- 50K concurrent users during peak (Black Friday)
- Product search: < 200ms
- Order placement: < 2 seconds
- 99.9% uptime for payment service
- Payment: exactly-once processing

### Microservices Architecture
```
[API Gateway - Spring Cloud Gateway]
        │
        ├── [User Service]
        │       └── PostgreSQL (users, addresses)
        │
        ├── [Product Service]
        │       ├── PostgreSQL (products, categories)
        │       └── Elasticsearch (search index)
        │
        ├── [Cart Service]
        │       └── Redis (session/cart data, TTL)
        │
        ├── [Order Service]
        │       ├── PostgreSQL (orders, order_items)
        │       └── Kafka Producer (order events)
        │
        ├── [Inventory Service]
        │       ├── PostgreSQL (stock levels)
        │       └── Kafka Consumer (order events)
        │
        └── [Payment Service]
                ├── PostgreSQL (transactions)
                └── Stripe/PayPal integration
```

### Database Design (Key Tables)
```sql
-- Products
CREATE TABLE products (
    id              BIGINT PRIMARY KEY,
    sku             VARCHAR(50) UNIQUE NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    price           DECIMAL(12,2) NOT NULL,
    category_id     BIGINT,
    active          BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Orders
CREATE TABLE orders (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL,  -- PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    total_amount    DECIMAL(12,2) NOT NULL,
    shipping_addr   JSONB,
    created_at      TIMESTAMP DEFAULT NOW()
);

CREATE TABLE order_items (
    id              BIGINT PRIMARY KEY,
    order_id        BIGINT REFERENCES orders(id),
    product_id      BIGINT NOT NULL,
    quantity        INT NOT NULL,
    unit_price      DECIMAL(12,2) NOT NULL,  -- Snapshot at time of order!
    product_name    VARCHAR(255) NOT NULL     -- Snapshot: don't rely on product reference
);

-- Inventory (separate service/DB)
CREATE TABLE inventory (
    product_id      BIGINT PRIMARY KEY,
    available_stock INT NOT NULL DEFAULT 0,
    reserved_stock  INT NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP DEFAULT NOW(),
    version         BIGINT DEFAULT 0  -- Optimistic locking
);
```

### Order Placement — Saga Pattern
```
User places order:
1. Order Service: Create order (PENDING)
2. Inventory Service: Reserve stock
   → If fails: Cancel order (compensate)
3. Payment Service: Charge customer
   → If fails: Release stock reservation, Cancel order
4. Order Service: Confirm order (CONFIRMED)
5. Notification Service: Send confirmation email

Each step: Kafka event + local transaction (choreography saga)
```

### Inventory — Preventing Oversell
```java
@Transactional
public boolean reserveStock(Long productId, int quantity) {
    // Optimistic locking with version check
    int updated = inventoryRepository.reserveStock(productId, quantity);
    if (updated == 0) {
        throw new InsufficientStockException(productId);
    }
    return true;
}

// Repository
@Modifying
@Query("""
    UPDATE Inventory i SET 
        i.reservedStock = i.reservedStock + :quantity,
        i.version = i.version + 1
    WHERE i.productId = :productId
    AND i.availableStock - i.reservedStock >= :quantity
    AND i.version = :version
    """)
int reserveStock(@Param("productId") Long productId,
                 @Param("quantity") int quantity,
                 @Param("version") long version);
```

---

## DESIGN 3: Hotel Booking System

### Requirements
**Functional:**
- Search hotels by location, dates, guests
- View room availability and pricing
- Book a room (prevent double booking)
- Manage reservations (cancel, modify)
- Payment and refund

**Non-Functional:**
- Peak: 10,000 searches/sec, 100 bookings/sec
- No double booking — critical correctness requirement
- Search results: < 500ms
- Booking: < 3 seconds

### Database Design
```sql
CREATE TABLE hotels (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    city        VARCHAR(100),
    country     VARCHAR(2),
    lat         DECIMAL(9,6),
    lng         DECIMAL(9,6),
    star_rating INT
);

CREATE TABLE rooms (
    id          BIGINT PRIMARY KEY,
    hotel_id    BIGINT REFERENCES hotels(id),
    room_number VARCHAR(10),
    type        VARCHAR(50),  -- 'SINGLE', 'DOUBLE', 'SUITE'
    capacity    INT,
    base_price  DECIMAL(10,2)
);

-- Room availability (calendar model)
CREATE TABLE room_availability (
    id              BIGINT PRIMARY KEY,
    room_id         BIGINT REFERENCES rooms(id),
    date            DATE NOT NULL,
    status          VARCHAR(20),  -- 'AVAILABLE', 'BOOKED', 'BLOCKED'
    price           DECIMAL(10,2),  -- Dynamic pricing
    UNIQUE (room_id, date)
);

CREATE TABLE reservations (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    room_id         BIGINT REFERENCES rooms(id),
    check_in        DATE NOT NULL,
    check_out       DATE NOT NULL,
    status          VARCHAR(20),  -- 'PENDING', 'CONFIRMED', 'CANCELLED'
    total_price     DECIMAL(12,2),
    created_at      TIMESTAMP DEFAULT NOW()
);
```

### Preventing Double Booking
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public Reservation createBooking(BookingRequest request) {
    // 1. Lock availability rows for date range
    List<RoomAvailability> availability = availabilityRepo
        .findByRoomIdAndDateRangeWithLock(
            request.getRoomId(),
            request.getCheckIn(),
            request.getCheckOut().minusDays(1)
        );
    
    // 2. Check all dates are available
    boolean allAvailable = availability.stream()
        .allMatch(a -> "AVAILABLE".equals(a.getStatus()));
    
    if (!allAvailable) {
        throw new RoomNotAvailableException();
    }
    
    // 3. Mark as BOOKED
    availability.forEach(a -> a.setStatus("BOOKED"));
    availabilityRepo.saveAll(availability);
    
    // 4. Create reservation
    return reservationRepo.save(buildReservation(request));
}

// Repository - pessimistic lock
@Query("""
    SELECT a FROM RoomAvailability a 
    WHERE a.roomId = :roomId 
    AND a.date BETWEEN :checkIn AND :checkOut
    """)
@Lock(LockModeType.PESSIMISTIC_WRITE)
List<RoomAvailability> findByRoomIdAndDateRangeWithLock(...);
```

### Search — Elasticsearch Integration
```java
// Index hotel data in Elasticsearch for fast geo-search
@Document(indexName = "hotels")
public class HotelDocument {
    @Id private String id;
    private String name;
    private String city;
    @GeoPointField private GeoPoint location;
    private double minPrice;
    private int starRating;
    private List<String> amenities;
}

// Search query
SearchRequest request = new SearchRequest("hotels");
request.source(new SearchSourceBuilder()
    .query(QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery("city", city))
        .filter(QueryBuilders.rangeQuery("minPrice").lte(maxPrice))
        .filter(QueryBuilders.geoDistanceQuery("location")
            .point(lat, lng)
            .distance("10km"))
    )
    .sort(SortBuilders.geoDistanceSortBuilder("location", lat, lng).order(ASC))
    .size(20)
);
```

---

## DESIGN 4: Notification System

### Requirements
**Functional:**
- Send notifications via Email, SMS, Push, WhatsApp
- Template-based messages with personalization
- Scheduled notifications
- Bulk sending (marketing campaigns: 10M users)
- Delivery tracking (sent, delivered, failed, opened)

**Non-Functional:**
- Throughput: 1M notifications/hour for campaigns
- Latency: Transactional (OTP, order) < 5 seconds
- At-least-once delivery guarantee
- No duplicate sends (idempotency)

### Architecture
```
[Notification API] → [Kafka: notification-requests]
                            ↓
                    [Notification Dispatcher]
                    ├── Email Worker → SendGrid/SES
                    ├── SMS Worker → Twilio
                    ├── Push Worker → FCM/APNs
                    └── WhatsApp Worker → Twilio
                            ↓
                    [Kafka: notification-results]
                            ↓
                    [Delivery Tracker Service]
                            ↓
                    [PostgreSQL: notification_logs]
```

### Database Design
```sql
CREATE TABLE notification_templates (
    id          BIGINT PRIMARY KEY,
    code        VARCHAR(50) UNIQUE,  -- 'ORDER_CONFIRMED', 'OTP_SMS'
    channel     VARCHAR(20),
    subject     VARCHAR(255),
    body        TEXT,  -- "Dear {{name}}, your order {{orderId}} is confirmed"
    active      BOOLEAN DEFAULT TRUE
);

CREATE TABLE notification_logs (
    id              BIGINT PRIMARY KEY,
    idempotency_key VARCHAR(100) UNIQUE,  -- Prevent duplicates
    recipient       VARCHAR(255),
    channel         VARCHAR(20),
    template_code   VARCHAR(50),
    status          VARCHAR(20),  -- QUEUED, SENT, DELIVERED, FAILED
    provider_id     VARCHAR(100), -- External tracking ID from SendGrid/Twilio
    sent_at         TIMESTAMP,
    delivered_at    TIMESTAMP,
    error_message   TEXT,
    retry_count     INT DEFAULT 0
);
```

### Idempotency — Prevent Duplicate Sends
```java
@Service
public class NotificationService {
    
    public void sendNotification(NotificationRequest request) {
        String idempotencyKey = generateKey(request);
        
        // Check if already sent
        if (notificationLogRepo.existsByIdempotencyKey(idempotencyKey)) {
            log.info("Notification already sent: {}", idempotencyKey);
            return;  // Skip duplicate
        }
        
        // Atomic insert to claim this notification
        try {
            NotificationLog log = NotificationLog.builder()
                .idempotencyKey(idempotencyKey)
                .status("QUEUED")
                .build();
            notificationLogRepo.save(log);  // Will fail if duplicate (unique constraint)
        } catch (DataIntegrityViolationException e) {
            return;  // Race condition - another instance claimed it
        }
        
        // Send via Kafka
        kafkaTemplate.send("notification-requests", idempotencyKey, request);
    }
    
    private String generateKey(NotificationRequest req) {
        return DigestUtils.md5DigestAsHex(
            (req.getUserId() + req.getTemplateCode() + req.getContextId()).getBytes()
        );
    }
}
```

---

## DESIGN 5: Payment System

### Requirements
**Functional:**
- Process payments (credit card, e-wallet, bank transfer)
- Refunds
- Transaction history
- Multi-currency support
- Fraud detection

**Non-Functional:**
- Exactly-once processing (critical!)
- 99.999% uptime (5 nines)
- Audit trail for all transactions (regulatory)
- PCI DSS compliance (card data security)
- Latency: < 3 seconds

### Architecture
```
[Client] → [API Gateway (TLS)]
                    ↓
        [Payment Service (Auth + Routing)]
            ↓               ↓
    [Fraud Detection]  [Payment Processor]
                            ↓
                    ├── Stripe (Cards)
                    ├── VNPay (Local)
                    └── Bank Transfer API
                            ↓
                    [Transaction DB]
                    [Audit Log (Immutable)]
```

### Database Design
```sql
-- Ledger-style (double-entry bookkeeping)
CREATE TABLE accounts (
    id          BIGINT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    currency    CHAR(3) NOT NULL,
    balance     DECIMAL(15,4) NOT NULL DEFAULT 0,
    version     BIGINT DEFAULT 0,  -- Optimistic locking
    CONSTRAINT positive_balance CHECK (balance >= 0)
);

CREATE TABLE transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(100) UNIQUE NOT NULL,  -- Client-generated
    from_account_id BIGINT,
    to_account_id   BIGINT,
    amount          DECIMAL(15,4) NOT NULL,
    currency        CHAR(3) NOT NULL,
    type            VARCHAR(20),  -- PAYMENT, REFUND, TOPUP, WITHDRAWAL
    status          VARCHAR(20),  -- PENDING, PROCESSING, COMPLETED, FAILED
    provider_txn_id VARCHAR(100), -- Stripe charge ID
    created_at      TIMESTAMP DEFAULT NOW(),
    completed_at    TIMESTAMP
);

-- Immutable audit log (append-only, no updates!)
CREATE TABLE audit_log (
    id              BIGINT PRIMARY KEY,
    transaction_id  UUID,
    event_type      VARCHAR(50),
    event_data      JSONB,
    created_at      TIMESTAMP DEFAULT NOW()
);
-- Partition by created_at for performance
-- Consider separate DB instance or AWS DynamoDB for audit
```

### Exactly-Once Payment Processing
```java
@Service
public class PaymentService {

    @Transactional
    public TransactionResult processPayment(PaymentRequest request) {
        // 1. Idempotency check - use client's key
        Optional<Transaction> existing = txnRepo
            .findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return buildResult(existing.get()); // Return same result
        }
        
        // 2. Create pending transaction
        Transaction txn = Transaction.builder()
            .idempotencyKey(request.getIdempotencyKey())
            .status("PENDING")
            .amount(request.getAmount())
            .build();
        txn = txnRepo.save(txn); // Claim this payment

        // 3. Validate: Check account balance with optimistic lock
        Account account = accountRepo.findByIdWithLock(request.getAccountId());
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            txn.setStatus("FAILED");
            txnRepo.save(txn);
            throw new InsufficientFundsException();
        }

        // 4. Deduct balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepo.save(account);

        // 5. Call external payment provider
        try {
            String providerTxnId = stripeClient.charge(request);
            txn.setStatus("COMPLETED");
            txn.setProviderTxnId(providerTxnId);
        } catch (StripeException e) {
            // Rollback: restore balance (within same transaction)
            account.setBalance(account.getBalance().add(request.getAmount()));
            accountRepo.save(account);
            txn.setStatus("FAILED");
            txn.setErrorMessage(e.getMessage());
            throw new PaymentProcessingException(e);
        } finally {
            txnRepo.save(txn);
        }

        return buildResult(txn);
    }
}
```

### Scaling Strategy (Payment System)
1. **Active-Active**: Multiple regions, consistent hashing
2. **CQRS**: Separate read (replicas, cache) from write path
3. **Event Sourcing**: Audit log IS the source of truth
4. **Circuit Breaker**: Isolate from payment provider failures
5. **Rate Limiting**: Per-user and global limits

---

*Next: [Part 16 - Coding Test](./Part-16-Coding-Test.md)*
