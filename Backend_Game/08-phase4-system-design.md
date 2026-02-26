# 🏗️ PHASE 4 — System Design Mastery

> **Timeline**: Month 6  
> **Mục tiêu**: Design 6 systems chuẩn interview, có thể discuss trade-offs  
> **Tài liệu**: *System Design Interview* — Alex Xu Vol 1 & 2

---

## CẤU TRÚC MỖI BÀI DESIGN

Mỗi system design phải cover:
1. **Requirements** (Functional + Non-functional)
2. **High-Level Design** (Architecture diagram)
3. **Data Model** (Schema + access patterns)
4. **Scaling** (Horizontal, caching, sharding)
5. **Failure Handling** (What if X dies?)
6. **Trade-offs** (Consistency vs Availability, Cost vs Performance)

---

## 🎯 DESIGN 1: Chat System

### Requirements
- **Functional**: 1-on-1 chat, group chat (max 500), online status, read receipts
- **Non-functional**: Latency < 200ms, 99.9% uptime, message ordering

### High-Level Design
```
Client ──WebSocket──→ Chat Gateway ──→ Message Service ──→ Cassandra
                          │                                    │
                          ├── Presence Service ──→ Redis        │
                          └── Push Service ──→ FCM/APNs    Kafka (async)
                                                              │
                                                        Notification
                                                         Service
```

### Data Model
```sql
-- Messages (Cassandra - partition by chat_id, cluster by timestamp)
CREATE TABLE messages (
    chat_id UUID,
    message_id TIMEUUID,
    sender_id INT,
    content TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (chat_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);
```

### Scaling: Partition by chat_id, each chat group on one node
### Failure: Message queue buffering if service down, at-least-once delivery
### Trade-off: Eventual consistency for read receipts OK, strong for message ordering

---

## 🎯 DESIGN 2: Real-Time Multiplayer Game Server

### Requirements
- **Functional**: 4 players/room, real-time state sync, reconnection, anti-cheat
- **Non-functional**: Tick rate 20Hz (50ms), latency < 100ms, 10K concurrent rooms

### High-Level Design
```
  Clients (WebSocket)
      │
  ┌───▼────┐     ┌──────────┐
  │Gateway │────→│Matchmaker│──→ assign room to GameServer
  │(Nginx) │     └──────────┘
  └───┬────┘
      │
  ┌───▼────────────┐
  │  Game Server    │ ← Server-authoritative
  │  (Room 1..N)   │ ← Fixed tick loop (20Hz)
  │  State Machine │ ← Input validation
  └───┬────────────┘
      │
  ┌───▼─────┐    ┌──────────┐
  │  Redis  │    │ Postgres │
  │(session)│    │(persist) │
  └─────────┘    └──────────┘
```

### State Sync Strategy
- Server sends **delta updates** (chỉ gửi thay đổi, không full state)
- Client-side prediction + server reconciliation
- Snapshot interpolation cho other players

### Scaling: 1 GameServer = 100 rooms, stateless gateway, matchmaker assigns
### Failure: Player reconnect → restore from Redis snapshot, room timeout 30s
### Trade-off: Server-authoritative = higher latency but cheat-proof

---

## 🎯 DESIGN 3: Leaderboard Service

### Requirements
- **Functional**: Global top 100, per-region, weekly reset, real-time update
- **Non-functional**: 100K score updates/sec, top-100 query < 10ms

### Design
```
Score Update → Kafka → Consumer → Redis ZADD → (async) Postgres archival

Query Top 100:  Redis ZREVRANGE leaderboard 0 99 WITHSCORES  ← O(log(N) + 100)
Query Rank:     Redis ZREVRANK leaderboard player:123        ← O(log(N))
```

### Scaling: Shard by region, merge for global (approximate OK)
### Trade-off: Redis for speed (eventual consistent), Postgres for durability

---

## 🎯 DESIGN 4: Notification System

### Requirements
- Push notifications (FCM/APNs), in-app notifications, email
- Priority levels, rate limiting, user preferences

### Design
```
Event Source → Notification Service → Priority Queue
                                         │
                    ┌────────────────────┬┴───────────────┐
                    ▼                    ▼                 ▼
               Push Worker         Email Worker      In-App Worker
               (FCM/APNs)         (SendGrid)        (WebSocket)
```

### Trade-off: At-least-once delivery (duplicate notification better than missed)

---

## 🎯 DESIGN 5: Rate Limiter

### Algorithms

| Algorithm | Pros | Cons |
|-----------|------|------|
| **Token Bucket** | Smooth, allows burst | Memory per user |
| **Sliding Window Log** | Exact | High memory |
| **Sliding Window Counter** | Low memory | Approximate |
| **Fixed Window** | Simple | Boundary spike |

### Redis Implementation (Sliding Window Counter)
```
Key: rate_limit:{user_id}:{window}
INCR key
EXPIRE key window_size
IF count > limit → REJECT (429)
```

---

## 🎯 DESIGN 6: URL Shortener

### Design
```
POST /shorten  → Generate short_id (Base62 encode counter/hash)
                 Store: short_id → original_url (Cassandra/DynamoDB)
                 Cache: Redis (hot URLs)
                 
GET /{short_id} → Redis cache hit? → 301 redirect
                  Cache miss? → DB lookup → cache → redirect
```

### Scaling: Read-heavy → cache layer, hash-based sharding
### Trade-off: Counter (sequential, predictable) vs hash (random, collision possible)

---

## 🎤 MOCK INTERVIEW SCENARIOS

### Scenario 1: "Design a chat system for a game with 10M DAU"
**Follow-up questions:**
1. "Message ordering bị sai khi user gửi nhanh. Giải quyết thế nào?"
2. "Group chat 500 người, mỗi message → 500 writes. Optimize?"
3. "User offline 3 ngày, reconnect → cần nhận bao nhiêu messages?"
4. "Encrypted chat (E2E). Ảnh hưởng design thế nào?"

### Scenario 2: "Design multiplayer game matchmaking"
**Follow-up questions:**
1. "Skill gap giữa matched players quá lớn → toxic. Fix?"
2. "Queue time > 5 phút cho high-rank. Trade-off?"
3. "Cross-region matching vs same-region. Latency vs wait time?"
4. "Bot fill khi không đủ players. Khi nào trigger?"

### Điểm yếu thường gặp khi trả lời:
- ❌ Nhảy vào detail quá sớm, không clarify requirements
- ❌ Không estimate scale (QPS, storage, bandwidth)
- ❌ Chỉ nói happy path, không mention failure handling
- ❌ Không discuss trade-offs
- ❌ Design over-engineered cho simple problem

---

## ✅ CHECKLIST

- [ ] Design chat system (end-to-end)
- [ ] Design multiplayer game server
- [ ] Design leaderboard service  
- [ ] Design notification system
- [ ] Design rate limiter
- [ ] Design URL shortener
- [ ] Practice mock interview (tự record, nghe lại)
- [ ] Discuss trade-offs cho mỗi design
- [ ] Estimate numbers (QPS, storage, bandwidth)

## 🎯 MILESTONE: Complete 6 designs, pass mock interview (self-recorded, 45 min each)
