# 🌍 PHASE 2.1 — Distributed System Core

> **Timeline**: Month 3-4, tuần 1-4  
> **Mục tiêu**: Design và build distributed game services  
> **Tài liệu**: *DDIA* Ch 5, 8, 9 | *System Design Interview* Vol 1

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### 1. CAP Theorem

```
        Consistency
           /\
          /  \
         /    \
        / CP   \  CA (không tồn tại trong distributed)
       /________\
  Partition     Availability
  Tolerance
  
  Game server choices:
  ├── Economy (Gold, Items) → CP: chấp nhận unavailable hơn mất data
  ├── Chat → AP: chấp nhận delay hơn không chat được  
  └── Leaderboard → AP: eventual consistency OK
```

### 2. Consistency Models

| Model | Guarantee | Game Use Case |
|-------|-----------|---------------|
| **Strong** | Read luôn thấy latest write | Gold transactions |
| **Eventual** | Eventually converge | Leaderboard |
| **Causal** | Causally related ops ordered | Chat (reply order) |
| **Session** | Read-your-write | Player profile update |

### 3. Leader Election & Consensus (Raft simplified)

```
  ┌─────────┐     ┌─────────┐     ┌─────────┐
  │ LEADER  │────→│FOLLOWER │     │FOLLOWER │
  │ Node A  │────→│ Node B  │     │ Node C  │
  └─────────┘     └────┬────┘     └────┬────┘
                       │               │
          Heartbeat timeout? → Start election
          Get majority votes? → Become new leader
```

**Raft Key Points:**
- Leader nhận tất cả writes, replicate to followers
- Majority (N/2 + 1) phải ACK trước commit
- Nếu leader die → election timeout → new leader
- Game server: Dùng etcd/consul (Raft-based) cho service discovery, KHÔNG tự implement

### 4. Idempotency

```
Client: "Mua item X" → Server crash sau khi trừ gold, trước khi thêm item
Client retry: "Mua item X" → Nếu không idempotent → trừ gold LẦN 2!

Fix: Idempotency key
  Request: { action: "buy", item: "X", idempotency_key: "uuid-123" }
  Server: Check Redis "processed:uuid-123"
          → exists? return cached result
          → not exists? process, then SET "processed:uuid-123" result EX 3600
```

### 5. Circuit Breaker & Retry

```
States:
  CLOSED ──(failures > threshold)──→ OPEN ──(timeout)──→ HALF-OPEN
    ↑                                                        │
    └──────────── (success in half-open) ────────────────────┘
                  (failure in half-open) ──→ OPEN

Config:
  failure_threshold: 5 failures trong 60s
  open_timeout: 30s
  half_open_max_calls: 3
```

**Retry strategy:**
- Exponential backoff: 100ms → 200ms → 400ms → 800ms (+ jitter)
- Max retries: 3-5
- KHÔNG retry non-idempotent operations

---

## 🔨 PROJECT: Multi-Service Game Platform

### Architecture:
```
  ┌──────────┐     ┌──────────┐     ┌──────────┐
  │Matchmaker│     │  Room    │     │ Economy  │
  │ Service  │────→│ Service  │     │ Service  │
  └────┬─────┘     └────┬─────┘     └────┬─────┘
       │                │                │
       └────────────────┼────────────────┘
                        │
                  ┌─────▼─────┐
                  │   Redis   │
                  │ (shared)  │
                  └───────────┘
```

**Build Requirements:**
1. **Matchmaking**: Queue players, match by criteria, multi-instance safe
2. **Room Service**: Create/join/leave rooms, state sync, max capacity
3. **Economy**: Gold/item transactions, idempotent, audit log

**Each service MUST have:**
- Health check endpoint
- Idempotent key support
- Circuit breaker for cross-service calls
- Structured logging
- Run 2+ instances behind load balancer

**Scaling Exercise:**
- Run 2 instances of each service
- Use Redis for shared state
- Identify bottlenecks under load
- Document: "What breaks at 1K, 10K, 100K users?"

---

## ✅ CHECKLIST

- [ ] Giải thích CAP theorem với game examples
- [ ] Phân biệt consistency models
- [ ] Hiểu Raft concept (không cần implement)
- [ ] Implement idempotency key pattern
- [ ] Implement circuit breaker
- [ ] Build 3-service system chạy multi-instance
- [ ] Identify scaling bottlenecks
- [ ] Design retry strategy with backoff

## 🎯 MILESTONE: Multi-service chạy ổn 2 instances, handle 1K concurrent, idempotent transactions pass

## 📚 TÀI LIỆU: DDIA Ch 5, 8, 9 | Martin Kleppmann lectures (YouTube)
