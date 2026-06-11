# 🏗️ System Design Notes — 10 Core Problems

> **Framework:** RESHADED  
> R(equirements) → E(stimation) → S(ystem interface) → H(igh-level design) → A(rchitecture deep dive) → D(ata model) → E(xtended requirements) → D(esign trade-offs)

---

## 1️⃣ URL Shortener (TinyURL)

### Requirements
**Functional:**
- Shorten a long URL → short URL
- Redirect short URL → original URL
- Custom alias (optional)
- Link expiration

**Non-Functional:**
- High availability (99.99%)
- Low latency redirect (<100ms)
- Short URLs should be non-guessable

### Estimation
- 100M URLs created/day → ~1,200 writes/sec
- Read:Write ratio = 100:1 → 120K reads/sec
- 5 years storage: 100M × 365 × 5 = 182B records
- Each record ~500 bytes → 182B × 500B = ~91 TB

### High-Level Design
```
Client → Load Balancer → API Servers → Cache (Redis) → Database (MySQL/Cassandra)
                                    ↓
                            Key Generation Service (KGS)
```

### Key Components
- **ID Generation:** Base62 encoding (a-z, A-Z, 0-9), 7 chars = 62^7 = 3.5 trillion combinations
- **Database:** NoSQL (Cassandra) for high write throughput, or MySQL with sharding
- **Cache:** Redis LRU cache for hot URLs (80/20 rule)
- **Read path:** Check cache → miss → check DB → update cache → redirect (301 vs 302)

### Trade-offs
| Decision | Option A | Option B | Choice |
|:---------|:---------|:---------|:-------|
| Redirect code | 301 (Permanent) | 302 (Temporary) | 302 — better analytics |
| ID generation | Hash (MD5) | Pre-generated IDs | Pre-generated — no collision |
| DB | SQL (MySQL) | NoSQL (Cassandra) | Depends on scale |

---

## 2️⃣ Rate Limiter

### Requirements
- Limit requests per user/IP per time window
- Return 429 Too Many Requests
- Distributed (multiple servers)
- Low latency, minimal memory

### Algorithms
| Algorithm | Pros | Cons | Use Case |
|:----------|:-----|:-----|:---------|
| **Token Bucket** | Smooth, allows burst | Memory per user | API rate limiting |
| **Sliding Window Log** | Accurate | High memory | Audit-critical |
| **Sliding Window Counter** | Low memory, smooth | Approximate | General purpose |
| **Fixed Window Counter** | Simple | Boundary burst | Simple APIs |
| **Leaky Bucket** | Smooth output | Can't burst | Queue processing |

### Architecture
```
Client → API Gateway → Rate Limiter Middleware → Backend
                              ↓
                         Redis (counters)
                              ↓
                         Rules Config (YAML/DB)
```

### Redis Implementation (Sliding Window Counter)
```java
// Key: rate_limit:{userId}:{minute_window}
// INCR + EXPIRE pattern
String key = "rate_limit:" + userId + ":" + (currentTime / 60);
long count = redis.incr(key);
if (count == 1) redis.expire(key, 60);
if (count > limit) return 429;
```

### Relate với kinh nghiệm bản thân
> **FPM Project:** Implemented Token Bucket rate limiting (5 req/5min login, 100 req/s standard) — có thể nói trực tiếp trong phỏng vấn!

---

## 3️⃣ Chat System (WhatsApp/Messenger)

### Requirements
- 1-on-1 messaging
- Group chat (up to 500 members)
- Online/offline status
- Media sharing
- Message delivery status (sent/delivered/read)
- Push notifications

### High-Level Design
```
Client ←→ WebSocket Server ←→ Message Queue (Kafka) ←→ Chat Service
                                                            ↓
                                                      Message DB (Cassandra)
                                                            ↓
                                                      Push Notification Service
```

### Key Decisions
- **Protocol:** WebSocket for real-time bidirectional communication
- **Message storage:** Cassandra — write-heavy, time-series data, partition by (chat_id, message_id)
- **Message ordering:** Snowflake ID (timestamp + machine + sequence)
- **Delivery:** At-least-once + client-side dedup

### Relate với kinh nghiệm
> **Gihot:** WebSocket experience from game server, real-time state sync, Redis pub/sub for cross-server messaging

---

## 4️⃣ Notification System

### Requirements
- Push notifications (iOS/Android), SMS, Email
- Millions of notifications/day
- User preferences (opt-in/opt-out)
- Rate limiting per user
- Template system

### Architecture
```
Event Producers → Kafka → Notification Service → Priority Queue
                                                      ↓
                                              ┌───────┼───────┐
                                              ↓       ↓       ↓
                                           Push    Email    SMS
                                           (FCM)  (SES)   (Twilio)
```

### Key Components
- **Kafka topics:** notification.push, notification.email, notification.sms
- **Priority queues:** Urgent (OTP) > High (payment) > Normal (marketing)
- **Deduplication:** Redis with TTL to prevent duplicate sends
- **Retry with backoff:** Exponential backoff for failed deliveries

---

## 5️⃣ News Feed System (Facebook/Twitter)

### Requirements
- Users post content
- Feed shows posts from followed users, ranked by relevance
- Near real-time updates
- Millions of users

### Two Approaches
| Approach | Fan-out on Write (Push) | Fan-out on Read (Pull) |
|:---------|:----------------------|:----------------------|
| **How** | Pre-compute feed at write time | Build feed at read time |
| **Pros** | Fast reads | No wasted computation |
| **Cons** | Slow writes, celebrity problem | Slow reads |
| **Best for** | Normal users | Celebrities (10M+ followers) |

### Hybrid Approach
- Normal users → Fan-out on write (push to followers' feeds)
- Celebrities → Fan-out on read (pull at read time)
- Feed stored in Redis sorted set (score = timestamp)

---

## 6️⃣ E-Commerce Order/Payment System

### Requirements
- Product catalog, search
- Shopping cart
- Order placement
- Payment processing
- Inventory management (prevent overselling)

### Key Challenge: Distributed Transaction
```
Order Service → [Saga Orchestrator] → Payment Service
                                    → Inventory Service  
                                    → Notification Service
```

### Saga Pattern (Choreography vs Orchestration)
- **Choreography:** Each service publishes events, others react
- **Orchestration:** Central orchestrator controls the flow

### Inventory: Prevent Overselling
```sql
-- Optimistic locking
UPDATE inventory SET quantity = quantity - 1, version = version + 1
WHERE product_id = ? AND version = ? AND quantity > 0;
```

### Relate với kinh nghiệm
> **FPM Project:** 10-service microservices with Kafka for transaction streaming + RabbitMQ for domain events. Saga pattern experience.

---

## 7️⃣ Real-Time Gaming Backend ⭐ (Kinh nghiệm trực tiếp)

### Requirements
- Real-time multiplayer (card game / MOBA)
- 5,000+ concurrent sessions
- Sub-50ms latency
- Matchmaking
- State synchronization
- Anti-cheat (authoritative server)

### Architecture
```
Mobile Client ←→ Gateway (WebSocket/gRPC) ←→ Game Server (C++/Java)
                                                    ↓
                                            ┌───────┼───────┐
                                            ↓       ↓       ↓
                                         Redis   Kafka   MySQL
                                        (state)  (events) (persist)
```

### Key Components
- **Authoritative Server:** Server owns game state, clients send inputs only
- **Matchmaking:** ELO-based or skill-based, Redis sorted sets
- **State Sync:** Delta compression, only send changes
- **Concurrency:** Thread-safe state with mutex locks, thread pools

### Relate với kinh nghiệm
> **Đây là điểm mạnh lớn nhất!** Trực tiếp thiết kế và implement tại Gihot. Nhấn mạnh:
> - C++17 thread-safe game logic
> - gRPC + Protobuf sub-50ms RTT
> - Redis session management + leaderboard
> - Load tested 10,000+ concurrent users

---

## 8️⃣ Distributed Cache System

### Requirements
- Key-value store with TTL
- High throughput (100K+ ops/sec)
- Consistent hashing for distribution
- Replication for availability

### Consistent Hashing
```
Ring: 0 ─── Node A ─── Node B ─── Node C ─── 2^32
Key hash → clockwise → first node = owner
Virtual nodes: Each physical node → 100+ virtual nodes for balance
```

### Cache Patterns
| Pattern | Write | Read | Use Case |
|:--------|:------|:-----|:---------|
| Cache-Aside | App writes DB only | App checks cache → miss → DB → update cache | General purpose |
| Write-Through | App writes cache → cache writes DB | Cache serves reads | Strong consistency |
| Write-Behind | App writes cache → async batch write DB | Cache serves reads | High write throughput |
| Read-Through | Same as cache-aside but cache manages DB | Cache serves reads | Simplify app code |

### Relate với kinh nghiệm
> **Gihot + FPM:** Redis multi-layer caching (TTL 5min), reduced DB load 30%, rate-limiting, session management, pub/sub messaging

---

## 9️⃣ Event-Driven Microservices Architecture

### Requirements
- Decoupled services
- Async communication
- Event sourcing / CQRS
- Resilience (retry, circuit breaker)

### Kafka vs RabbitMQ Decision Matrix
| Criteria | Kafka | RabbitMQ |
|:---------|:------|:---------|
| **Throughput** | Very high (millions/sec) | High (tens of thousands/sec) |
| **Ordering** | Per partition | Per queue |
| **Replay** | ✅ Persistent log | ❌ Consumed = gone |
| **Use case** | Event streaming, analytics | Task queues, domain events |
| **Protocol** | Custom TCP | AMQP |

### Relate với kinh nghiệm
> **FPM Project:** Dual-broker architecture — Kafka (8 topics) for high-throughput transaction streaming + RabbitMQ for domain event routing. Anti-Corruption Layer pattern. Resilience4j Circuit Breaker.

---

## 🔟 Search System (Elasticsearch)

### Requirements
- Full-text search across millions of documents
- Faceted search (filters)
- Autocomplete/suggestions
- Relevance ranking

### Architecture
```
Write Path: App → Kafka → Elasticsearch Indexer → ES Cluster
Read Path:  Client → API → Elasticsearch Query → Results + Highlights
```

### Key Concepts
- **Inverted Index:** word → [doc1, doc2, doc3]
- **Sharding:** Distribute index across nodes
- **Replicas:** Each shard has replica for HA
- **Analyzers:** Tokenizer + token filters (lowercase, stemming, stop words)

---

## 📝 System Design Interview Checklist

Trước mỗi câu hỏi System Design, đi qua checklist này:

- [ ] **Clarify requirements** — Hỏi ít nhất 3 câu clarifying questions
- [ ] **Define scope** — MVP features vs nice-to-have
- [ ] **Estimate scale** — QPS, storage, bandwidth
- [ ] **Define API** — RESTful endpoints hoặc gRPC services
- [ ] **Draw high-level** — Components và data flow
- [ ] **Choose database** — SQL vs NoSQL, justify WHY
- [ ] **Design data model** — Tables/collections, indexes
- [ ] **Address bottlenecks** — Caching, sharding, replication
- [ ] **Discuss trade-offs** — CAP, consistency vs availability
- [ ] **Mention monitoring** — Metrics, logging, alerting
