# PART 15 — System Design

---

## 🏗️ Design Framework (Use for ALL system design questions)

```
1. Clarify Requirements (5 min)
   - Functional: what does the system DO?
   - Non-functional: scale, latency, availability

2. Back-of-envelope Estimation
   - DAU, requests/sec, storage

3. High-level Architecture
   - Draw boxes: Client → API Gateway → Services → DB

4. API Design
   - REST endpoints

5. Database Design
   - Schema, indexes

6. Scale & Deep Dive
   - Bottlenecks, caching, sharding, replication
```

---

## 🔷 Design 1: URL Shortener (e.g., bit.ly)

### Requirements
**Functional:**
- Shorten long URL → short URL (e.g., `bit.ly/abc123`)
- Redirect short URL → original URL
- Custom alias (optional)
- URL expiration (optional)

**Non-functional:**
- 100M URLs created/day
- 10:1 read:write ratio → 1B redirects/day
- Low latency (<100ms redirect)
- High availability (99.99%)

### Estimation
```
Write: 100M/day = ~1,200 URLs/sec
Read: 1B/day = ~12,000 redirects/sec
Storage (5 years): 100M * 365 * 5 * ~500 bytes = ~90TB
```

### Short URL Generation
```java
// Option 1: Base62 encoding of auto-increment ID
// 62 chars: [a-z][A-Z][0-9]
// 7 chars = 62^7 = 3.5 trillion URLs

long id = db.nextId(); // from Snowflake or DB sequence
String shortCode = base62Encode(id); // e.g., "abc1234"

// Option 2: MD5 hash → take first 7 chars
// Risk: collision → must check uniqueness

// Option 3: Nanoid / UUID + truncate
```

### Database Schema
```sql
CREATE TABLE urls (
    id          BIGINT PRIMARY KEY,
    short_code  VARCHAR(10) NOT NULL UNIQUE,
    long_url    TEXT NOT NULL,
    user_id     BIGINT,
    created_at  TIMESTAMP DEFAULT NOW(),
    expires_at  TIMESTAMP,
    click_count BIGINT DEFAULT 0
);
CREATE INDEX idx_short_code ON urls(short_code);
```

### API Design
```
POST /api/shorten
Body: { "longUrl": "https://...", "customAlias": "mylink", "expiresIn": 86400 }
Response: { "shortUrl": "https://bit.ly/abc123" }

GET /{shortCode}
Response: 301 Redirect to longUrl

GET /api/stats/{shortCode}
Response: { clickCount, createdAt, expiresAt }
```

### Architecture
```
Client
  ↓
CDN (cache popular short codes)
  ↓
API Gateway + Rate Limiter
  ↓
URL Service (stateless, multiple instances)
  ↓
Redis Cache (short_code → long_url, TTL 24h)   ← first check here
  ↓ cache miss
MySQL (read replicas for scale)

Redirect flow:
GET /abc123
  1. Check Redis → cache hit → 301 redirect (< 5ms)
  2. Cache miss → MySQL lookup → cache it → 301 redirect (< 100ms)
```

### Scaling
- Redis cluster for cache
- MySQL read replicas
- Sharding by short_code hash
- Snowflake ID generator (distributed, monotonic, no single point)

---

## 🔷 Design 2: Hotel Booking System

### Requirements
**Functional:**
- Search available rooms (date range, city, guests)
- View hotel/room details
- Book a room
- Cancel booking
- View booking history

**Non-functional:**
- 5M hotels, 50M rooms
- 1M concurrent users during peak (holiday season)
- Double-booking must NEVER happen (critical)
- Search: <500ms, Booking: <2s

### Database Schema
```sql
CREATE TABLE hotels (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255),
    city        VARCHAR(100),
    lat         DECIMAL(10,8),
    lng         DECIMAL(11,8)
);

CREATE TABLE rooms (
    id          BIGINT PRIMARY KEY,
    hotel_id    BIGINT REFERENCES hotels(id),
    type        VARCHAR(50),    -- SINGLE, DOUBLE, SUITE
    max_guests  INT,
    price_per_night DECIMAL(10,2)
);

-- Availability table (most critical)
CREATE TABLE room_availability (
    room_id     BIGINT REFERENCES rooms(id),
    date        DATE,
    status      ENUM('AVAILABLE', 'BOOKED', 'BLOCKED'),
    booking_id  BIGINT,
    PRIMARY KEY (room_id, date)
);

CREATE TABLE bookings (
    id          BIGINT PRIMARY KEY,
    user_id     BIGINT,
    room_id     BIGINT,
    check_in    DATE,
    check_out   DATE,
    status      ENUM('PENDING', 'CONFIRMED', 'CANCELLED'),
    total_price DECIMAL(10,2),
    created_at  TIMESTAMP
);
```

### Preventing Double Booking
```sql
-- Pessimistic Locking approach
START TRANSACTION;
SELECT * FROM room_availability
WHERE room_id = 123
  AND date BETWEEN '2025-12-24' AND '2025-12-26'
  AND status = 'AVAILABLE'
FOR UPDATE;  -- lock these rows

-- Check all dates available
-- If yes: update status to BOOKED
UPDATE room_availability SET status = 'BOOKED', booking_id = 456
WHERE room_id = 123 AND date BETWEEN '2025-12-24' AND '2025-12-26';

INSERT INTO bookings (...) VALUES (...);
COMMIT;

-- Or: Optimistic Locking with version field
-- Or: Distributed lock via Redis (Redlock)
```

### Architecture
```
Client
  ↓
API Gateway (rate limit: 100 req/s)
  ↓
Search Service → Elasticsearch (for geo-search, full-text)
  ↓
Booking Service → MySQL (with pessimistic locking)
  ↓
Notification Service ← RabbitMQ (async email/SMS)
  ↓
Payment Service (Stripe/PayPal integration)
```

---

## 🔷 Design 3: Notification System

### Requirements
**Functional:**
- Send notifications via Email, SMS, Push (mobile)
- User preferences (opt-out channels)
- Scheduled notifications
- Notification templates

**Non-functional:**
- 100M notifications/day
- At-least-once delivery
- <30s delivery SLA for critical notifications

### Architecture
```
Trigger Sources: API calls, scheduled jobs, system events
  ↓
Notification Service (API)
  ↓
Message Queue (Kafka)
  Topics: notification.email, notification.sms, notification.push
  ↓
Workers (per channel, auto-scale)
  ↓
3rd Party: SendGrid (email), Twilio (SMS), Firebase (push)
  ↓
Delivery Status DB (track sent/failed/retried)
```

```java
// Kafka consumer for email
@KafkaListener(topics = "notification.email", groupId = "email-worker")
public void sendEmail(NotificationEvent event) {
    try {
        emailProvider.send(event.getTo(), event.getSubject(), event.getBody());
        updateStatus(event.getId(), Status.SENT);
    } catch (Exception e) {
        if (event.getRetryCount() < 3) {
            // retry with exponential backoff
            kafkaTemplate.send("notification.email.retry", event.withRetryCount(event.getRetryCount() + 1));
        } else {
            updateStatus(event.getId(), Status.FAILED);
            // move to DLQ for manual inspection
        }
    }
}
```

---

## 🔷 Design 4: Payment System

### Requirements
**Functional:**
- Process payments (credit card, e-wallet)
- View transaction history
- Refunds
- Webhooks for payment status updates

**Non-functional:**
- Exactly-once processing (never double charge)
- 99.999% availability
- PCI-DSS compliance
- Audit log for every state change

### Idempotency (Critical!)
```java
// Client sends idempotency key
// POST /api/payments
// Idempotency-Key: client-generated-uuid-123

@PostMapping("/payments")
public PaymentResponse createPayment(
    @RequestHeader("Idempotency-Key") String idempotencyKey,
    @RequestBody PaymentRequest req
) {
    // Check if we've seen this key
    Optional<Payment> existing = paymentRepo.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
        return PaymentResponse.from(existing.get()); // return same response
    }

    // Process new payment
    Payment payment = paymentService.process(req, idempotencyKey);
    return PaymentResponse.from(payment);
}
```

### State Machine
```
INITIATED → PENDING → COMPLETED
                  ↘ FAILED → REFUNDED
```

### Architecture
```
Client
  ↓
API Gateway (TLS, rate limiting, DDoS protection)
  ↓
Payment Service
  - Idempotency check (Redis key expiry 24h)
  - Risk assessment
  - Call payment gateway (Stripe/VNPay)
  ↓
Payment Gateway (Stripe/VNPay)
  ↓ Webhook
Payment Service receives confirmation
  ↓
Kafka event: payment.completed
  ↓
Order Service, Notification Service, Analytics Service
```

---

## 📋 System Design Quick Wins

| Scenario | Solution |
|----------|---------|
| High read traffic | Redis cache + CDN |
| Prevent double booking | Pessimistic DB lock or Redlock |
| Prevent double payment | Idempotency key + Redis |
| Async processing | Kafka/RabbitMQ |
| Geo-search | Elasticsearch or PostGIS |
| Distributed unique ID | Snowflake ID |
| Rate limiting | Token Bucket in Redis |
| Circuit breaking | Resilience4j |
