# 🎯 MoMo Interview Prep — Master Index

> **Target**: Java Backend Developer @ MoMo  
> **Level**: 2-4 years experience | Fintech/Payment domain  
> **Cập nhật**: 2026-07-24

---

## 📁 Tài Liệu Ôn Tập (Deep Dive Series)

| File | Chủ đề | Trọng tâm | Priority |
|---|---|---|---|
| [01-Microservices-Deep.md](./01-Microservices-Deep.md) | HTTP, gRPC, RabbitMQ, Kafka, Load Balancer, API Gateway | Kafka internals, Outbox Pattern | ⭐⭐⭐⭐⭐ |
| [02-Redis-Caching-Deep.md](./02-Redis-Caching-Deep.md) | Redis data structures, Cache patterns, Cache problems | Stampede, Penetration, Distributed Lock | ⭐⭐⭐⭐⭐ |
| [03-Security-Deep.md](./03-Security-Deep.md) | RSA, Digital Signature, HMAC, JWT, RBAC/ABAC | Request signing flow, JWT rotation | ⭐⭐⭐⭐⭐ |
| [04-Transaction-Handling-Deep.md](./04-Transaction-Handling-Deep.md) | Thành công, Thất bại, Treo, Reconciliation | Idempotency, Saga, Hung TX | ⭐⭐⭐⭐⭐ |

---

## 🔥 Top 10 Câu Hỏi Hay Hỏi Nhất @ MoMo

### Technical:
1. **Kafka vs RabbitMQ** — Khi nào dùng gì, tại sao MoMo chọn Kafka?
2. **Idempotency** — Implement như thế nào? Redis SET NX + DB constraint
3. **Hung Transaction** — Detect và xử lý như thế nào? Inquiry → Auto-reverse
4. **Cache Stampede** — Nguyên nhân, giải pháp (Mutex lock, Early expiration)
5. **JWT Security** — RS256 vs HS256, Refresh token rotation, Blacklist

### System Design:
6. **Design Payment System** — State machine, idempotency, outbox, reconciliation
7. **Distributed Transaction** — Saga pattern, compensating transactions
8. **High Availability** — Redis Sentinel/Cluster, Circuit Breaker, Graceful shutdown
9. **Request Signing** — RSA + canonical string + timestamp + nonce anti-replay
10. **Rate Limiting** — Token bucket với Redis Lua script

---

## 📊 Quick Reference — Bảng So Sánh Nhanh

### Message Queue:
| | RabbitMQ | Kafka |
|---|---|---|
| Model | Push | Pull |
| Retention | Delete after ACK | TTL-based (days) |
| Throughput | ~50K/s | ~1M+/s |
| Replay | No | Yes |
| Use case | Task queue, delay | Event streaming, audit |

### Cache Patterns:
| Pattern | Consistency | Performance | Use case |
|---|---|---|---|
| Cache-aside | Eventual | ✅ Fast reads | Read-heavy |
| Write-through | Strong | ❌ Slow writes | Balance cache |
| Write-behind | Eventual | ✅ Fast writes | Analytics, NON-financial |

### JWT Algorithms:
| | HS256 | RS256 |
|---|---|---|
| Keys | Shared secret | Private/Public key pair |
| Who verifies | Only those with secret | Anyone with public key |
| Use case | Internal, microservices | Auth server to multiple services |
| Non-repudiation | No | Yes |

### Hashing:
| Function | Speed | Use case |
|---|---|---|
| SHA-256 | Fast | Integrity check, signing |
| HMAC-SHA256 | Fast | Message authentication (shared key) |
| bcrypt | Slow (!) | Password hashing |
| Argon2 | Very slow | Password hashing (modern) |

---

## 🗣️ STAR Stories Cần Chuẩn Bị

### Story 1: Performance Problem
```
S: Hệ thống đang response time > Xs cho [endpoint]
T: Được giao optimize để đạt < Ys
A: Analyze query plan → Add index + Redis cache → [chi tiết technical]
R: Response time giảm từ Xs xuống Ys (X% improvement)
```

### Story 2: Production Bug / Incident
```
S: Production có bug [mô tả] ảnh hưởng [X users]
T: Cần resolve trong [thời gian]
A: RCA → Hotfix deploy → [giải pháp]
R: Resolved in [X hours], root cause fixed, prevent recurrence with [monitoring/test]
```

### Story 3: Technical Challenge / Learning
```
S: Project cần implement [tính năng mới/unfamiliar tech]
T: Tôi là người responsible
A: Research → POC → Implement → Code review → Deploy
R: Feature shipped on time, tôi learn được [skills]
```

---

## 📅 Study Plan (2 tuần còn lại)

### Tuần 1: Hiểu sâu + Practice
| Ngày | Sáng | Tối |
|---|---|---|
| T2 | Đọc 01-Microservices (HTTP + gRPC) | Code gRPC demo nhỏ |
| T3 | Đọc 01-Microservices (Kafka + RabbitMQ) | Implement Outbox Pattern |
| T4 | Đọc 02-Redis (Basic + Patterns) | Code Cache-aside + Lock |
| T5 | Đọc 02-Redis (Problems + Advanced) | Implement Rate Limiter |
| T6 | Đọc 03-Security (RSA + Digital Sig) | Code request signing |
| T7 | Đọc 03-Security (JWT full flow) | Code JWT filter |
| CN | Đọc 04-Transaction toàn bộ | Mock interview |

### Tuần 2: Review + Mock
| Ngày | Focus |
|---|---|
| T2 | Review điểm yếu từ mock interview |
| T3 | System Design: Design Payment System (white board) |
| T4 | Behavioral: STAR stories x5 |
| T5 | Full mock: 60 phút kỹ thuật |
| T6 | Review + Rest |
| **INTERVIEW DAY** | ☀️ |

---

## 💡 Interview Day Tips

### Khi bị hỏi câu khó:
```
"Let me think through this..."  (30 giây suy nghĩ là bình thường)
"That's an interesting edge case. In our system, we handle it by..."
"I haven't used X directly, but the concept is similar to Y which I've worked with..."
```

### Khi nói về technical:
```
Luôn mention: Trade-offs!
"We chose X because..., although the trade-off is..."
"The benefit is..., but the downside is..."

Luôn connect về fintech context:
"In a payment system like MoMo, this is especially important because..."
```

### Khi hỏi ngược lại interviewer:
- "Tech stack hiện tại của Payment team là gì?"
- "Team xử lý distributed transaction như thế nào?"
- "Incident response và on-call process ở MoMo ra sao?"
- "Cơ hội growth và mentorship trong team?"

---

*🚀 Chúc phỏng vấn thành công! Remember: You're not just answering questions,  
you're demonstrating how you THINK about problems.*
