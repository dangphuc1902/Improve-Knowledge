# 🎯 ROADMAP PHỎNG VẤN — Java Backend Engineer JD (Tối Ưu Thời Gian)

> **Target JD**: Java Backend Engineer — 4+ years (Spring Boot, Microservices, REST, Docker/K8s, JWT/OAuth2)
> **CV Match**: ~82% — Điểm mạnh: Spring ecosystem, Kafka, Redis, Microservices. Gap: ~1 năm kinh nghiệm, Oracle, K8s, OAuth2
> **Updated**: 2026-09-05
> **Nguyên tắc**: Ôn đúng thứ CV đã ghi trước → Fill gap sau → Mock cuối

---

## 🧠 CHIẾN LƯỢC TỔNG QUAN

```
CV của bạn = 82% match JD
→ Không cần học nhiều thứ mới
→ Cần: GIẢI THÍCH ĐƯỢC những gì đã ghi + Fill 3 gap nhỏ

3 RỦI RO PHỎNG VẤN CẦN XỬ LÝ:
1. "Bạn viết Kafka, giải thích consumer group rebalancing?"
2. "Bạn viết Resilience4j, 3 states của Circuit Breaker?"
3. "Bạn thiếu Oracle, bạn adapt được không?"
```

---

## 📅 SPRINT 5 NGÀY — INTERVIEW-READY (Khuyến Nghị)

> Dùng khi có ≥ 5 ngày trước phỏng vấn. Tập trung hoàn toàn, không học lan man.

---

### 🔴 NGÀY 1 — Spring Core (Bắt buộc giải thích được)
**Thời gian**: 3-4 tiếng | **File**: [`CV-Deep-Dive-Spring.md`](../03-Spring-Ecosystem/CV-Deep-Dive-Spring.md)

| # | Topic | Mức độ | Output cần đạt |
|---|---|---|---|
| 1 | `@Transactional` Propagation REQUIRED vs REQUIRES_NEW | 🔴 MUST | Giải thích được bằng lời + ví dụ wallet |
| 2 | Self-invocation trap — tại sao fail + 3 cách fix | 🔴 MUST | Nói được không cần nhìn tài liệu |
| 3 | Isolation levels — 4 level + dirty/phantom read | 🔴 MUST | Biết khi nào dùng cái nào |
| 4 | Spring AOP — Proxy mechanism, @Around thực tế | 🟡 SHOULD | Giải thích proxy JDK vs CGLIB |
| 5 | JWT Filter Chain flow (extract → validate → blacklist → context) | 🔴 MUST | Walk through từng bước |
| 6 | Spring Cloud Gateway — Route + Filter + Rate Limit | 🟡 SHOULD | Giải thích role của Gateway trong kiến trúc |

**Câu hỏi tự test cuối ngày:**
```
❓ @Transactional private method có hoạt động không? Tại sao?
❓ Self-invocation: Tôi có class A, method A1 gọi A2 (cùng class, @Transactional REQUIRES_NEW). Điều gì xảy ra?
❓ Token blacklisting hoạt động như thế nào trong FPM project của bạn?
```

---

### 🔴 NGÀY 2 — Kafka & Redis (CV ghi nhiều → PHẢI giải thích được)
**Thời gian**: 3-4 tiếng | **File**: [`CV-Deep-Dive-Kafka-Redis.md`](../06-Distributed-Systems/CV-Deep-Dive-Kafka-Redis.md)

| # | Topic | Mức độ | Output cần đạt |
|---|---|---|---|
| 1 | Kafka Producer acks: 0, 1, all — trade-off | 🔴 MUST | Biết chọn gì cho financial transaction |
| 2 | Consumer Group rebalancing — trigger, protocol, stop-the-world | 🔴 MUST | Giải thích được flow |
| 3 | Offset Management — at-least-once vs exactly-once | 🔴 MUST | Tại sao FPM dùng at-least-once + idempotency |
| 4 | Kafka vs RabbitMQ — khi nào dùng cái nào (FPM context) | 🟡 SHOULD | Giải thích lý do chọn trong dự án |
| 5 | Redis Cache-aside — flow + code pattern | 🔴 MUST | Giải thích ~30% DB reduction từ CV |
| 6 | Distributed Lock — SETNX + Lua script + Redisson | 🔴 MUST | Tại sao cần Lua script? |
| 7 | Token Bucket Rate Limiting — thuật toán | 🟡 SHOULD | Giải thích concept, không cần nhớ Lua |

**Câu hỏi tự test cuối ngày:**
```
❓ Bạn dùng "8 Kafka topics" trong FPM — tại sao cần nhiều topic vậy? Partition key là gì?
❓ Redis là single-threaded, tại sao vẫn cần Lua script cho atomicity?
❓ Khi nào dùng Kafka, khi nào dùng RabbitMQ? Trong FPM bạn chọn thế nào?
```

---

### 🔴 NGÀY 3 — Microservices Patterns + Database Locking
**Thời gian**: 4 tiếng | **File**: [`CV-Deep-Dive-Microservices-Patterns.md`](../05-System-Design/CV-Deep-Dive-Microservices-Patterns.md) + [`CV-Deep-Dive-Database-Locking.md`](../04-Database/CV-Deep-Dive-Database-Locking.md)

#### Buổi sáng: Microservices (2 tiếng)
| # | Topic | Mức độ | Output |
|---|---|---|---|
| 1 | Circuit Breaker — 3 states + threshold config (FPM: 50%) | 🔴 MUST | Vẽ được state diagram + giải thích |
| 2 | Resilience4j Fallback — viết fallback method | 🔴 MUST | Code từ memory |
| 3 | Saga Orchestration — coordinator flow + compensating transaction | 🟡 SHOULD | Giải thích tại sao chọn Orchestration cho financial |
| 4 | Anti-Corruption Layer — tại sao cần, ví dụ FPM | 🟡 SHOULD | Giải thích bằng ví dụ gRPC adapter |

#### Buổi chiều: Database Locking (2 tiếng)
| # | Topic | Mức độ | Output |
|---|---|---|---|
| 1 | Optimistic vs Pessimistic — cơ chế, `@Version`, SQL | 🔴 MUST | Khi nào dùng cái nào + lý do |
| 2 | Tại sao FPM chọn Pessimistic cho wallet | 🔴 MUST | Chuẩn bị câu trả lời phỏng vấn |
| 3 | Deadlock prevention — canonical ordering | 🟡 SHOULD | Giải thích pattern |
| 4 | N+1 Problem — detect + fix với JOIN FETCH | 🔴 MUST | Code fix từ memory |
| 5 | EXPLAIN ANALYZE — đọc được Seq Scan vs Index Scan | 🟡 SHOULD | Giải thích output |

**Câu hỏi tự test cuối ngày:**
```
❓ FPM có 10 services. Khi service Wallet down, Transaction Engine handle thế nào?
❓ Bạn dùng locking gì để prevent race condition trong wallet balance update? Tại sao?
❓ N+1 là gì? Trong dự án Hahalolo bạn fix như thế nào để đạt ~40% improvement?
```

---

### 🟡 NGÀY 4 — Fill Gaps (OAuth2, K8s, SOLID)
**Thời gian**: 3 tiếng | **File**: [`CV-Gaps-OAuth2-K8s-SOLID-gRPC.md`](../10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md)

| # | Topic | Mức độ | Output |
|---|---|---|---|
| 1 | OAuth2 Authorization Code Flow — các bước, tại sao an toàn | 🟡 SHOULD | Vẽ được flow, giải thích back-channel |
| 2 | OAuth2 Client Credentials — machine-to-machine | 🟡 SHOULD | Giải thích khi nào dùng |
| 3 | OAuth2 vs JWT — relationship | 🔴 MUST | Đây là câu hỏi hay bị hỏi |
| 4 | Kubernetes: Pod, Deployment, Service, Ingress | 🟡 SHOULD | Giải thích được 4 objects |
| 5 | Docker → K8s mental model (docker run → Pod, etc.) | 🟡 SHOULD | Biết tại sao cần K8s |
| 6 | kubectl cơ bản: get, logs, describe, exec | 🟢 NICE | Biết tên commands |
| 7 | SOLID — S, O, D với ví dụ Java thực tế | 🟡 SHOULD | Ví dụ từ project thực tế của bạn |

**Câu trả lời cho gap Oracle:**
```
Script chuẩn bị sẵn:
"Tôi làm việc chủ yếu với PostgreSQL và MySQL trong production.
Oracle và PostgreSQL đều là RDBMS — SQL standard tương đương nhau,
chỉ khác Oracle-specific syntax như ROWNUM, Sequences, dual table.
Tôi tự tin adapt được nhanh chóng vì nền tảng SQL của tôi vững."
```

---

### 🟢 NGÀY 5 — STAR Stories + Mock Interview
**Thời gian**: 3-4 tiếng

#### 5.1 Chuẩn Bị 5 STAR Stories (2 tiếng)

Lấy thẳng từ số liệu đã có trong CV:

| Story | Tình huống | Lấy từ |
|---|---|---|
| **STAR #1** | Performance optimization: ~30% DB load reduction | Gihot — Redis caching + RabbitMQ async |
| **STAR #2** | High concurrency: 10k users stress test, tìm bottleneck | Gihot — CPU/Memory profiling |
| **STAR #3** | Technical decision: Kafka vs RabbitMQ cho FPM | FPM Project — lý do chọn |
| **STAR #4** | Query optimization: ~40% execution time improvement | Hahalolo — JPA fetch strategy + indexing |
| **STAR #5** | System design: 10-service platform với strict domain boundaries | FPM — kiến trúc ACL + gRPC |

**Template STAR:**
```
S (Situation): "Tại Gihot/Hahalolo/FPM, hệ thống đang gặp vấn đề..."
T (Task):      "Nhiệm vụ của tôi là..."
A (Action):    "Tôi đã [kỹ thuật cụ thể]: implement X, optimize Y, design Z"
R (Result):    "Kết quả: [số liệu cụ thể]% improvement / giảm / tăng"
```

#### 5.2 Mock Interview — Nói To (1 tiếng)

Tự hỏi và trả lời to (hoặc nhờ ai đó hỏi):

**Vòng 1 — Technical (30 phút):**
```
1. "Tell me about yourself" (90 giây — nói về Java/Spring, FPM project, số liệu)
2. "@Transactional self-invocation problem?"
3. "Kafka consumer group rebalancing?"
4. "Circuit Breaker 3 states?"
5. "Tại sao chọn Pessimistic Locking cho wallet?"
6. "Thiết kế một REST API cho wallet service của bạn"
```

**Vòng 2 — Behavioral (15 phút):**
```
7. "Kể về lần bạn optimize performance trong production"
8. "Khi gặp bug production không reproduce được, bạn làm gì?"
9. "Bạn học công nghệ mới như thế nào?" (mention AI tools)
```

**Vòng 3 — Câu hỏi ngược (15 phút):**
```
→ "Tech stack hiện tại của team backend như thế nào?"
→ "Team đang dùng microservices hay monolith?"
→ "Có mentor/code review process không?"
→ "Tiếng Anh được dùng ở mức nào trong công việc hàng ngày?"
```

---

## 🚨 SPRINT 3 NGÀY — EMERGENCY (Nếu chỉ có 3 ngày)

```
Ngày 1: Ngày 1 + Ngày 2 của sprint 5 ngày (rút gọn)
          → @Transactional + JWT + Kafka + Redis
Ngày 2: Ngày 3 của sprint 5 ngày
          → Circuit Breaker + Locking + N+1
Ngày 3: STAR Stories + Mock Interview + OAuth2 concept (30 phút)
```

---

## 🚨 SPRINT 1 NGÀY — ULTRA EMERGENCY (Phỏng vấn ngày mai)

```
Sáng (4 tiếng):
  ✅ @Transactional self-invocation trap
  ✅ JWT filter chain flow
  ✅ Kafka: consumer group + offset + at-least-once
  ✅ Redis: cache-aside + distributed lock concept
  ✅ Circuit Breaker: 3 states + threshold

Chiều (3 tiếng):
  ✅ Optimistic vs Pessimistic Locking — wallet scenario
  ✅ N+1 problem + fix
  ✅ OAuth2 vs JWT (chỉ cần biết khác nhau thế nào)
  ✅ 3 STAR Stories (STAR #1, #4, #5 từ bảng trên)

Tối (1 tiếng):
  ✅ Mock interview — nói to 30 phút
  ✅ 5 câu hỏi ngược cho interviewer
  ✅ NGỦ ĐỦ GIẤC
```

---

## 📊 CHECKLIST TỰ ĐÁNH GIÁ TRƯỚC PHỎNG VẤN

Đánh dấu ✅ khi có thể trả lời không cần nhìn tài liệu:

### Spring
- [ ] @Transactional Propagation REQUIRED vs REQUIRES_NEW — ví dụ thực tế
- [ ] Self-invocation trap — giải thích và 3 cách fix
- [ ] JWT filter chain — từng bước validate
- [ ] Token blacklisting với Redis — flow

### Kafka & Redis
- [ ] Consumer group rebalancing — khi nào trigger, protocol
- [ ] At-least-once vs exactly-once — chọn gì cho financial
- [ ] Cache-aside pattern — flow + eviction
- [ ] Distributed lock — tại sao cần Lua script

### Microservices
- [ ] Circuit Breaker 3 states — CLOSED/OPEN/HALF_OPEN
- [ ] Saga Orchestration vs Choreography — khi nào dùng gì
- [ ] ACL — tại sao cần trong FPM

### Database
- [ ] Optimistic vs Pessimistic — code + khi nào dùng
- [ ] Deadlock prevention — canonical ordering
- [ ] N+1 — define, detect, fix

### Gaps
- [ ] OAuth2 vs JWT — relationship rõ ràng
- [ ] Authorization Code Flow — các bước chính
- [ ] SOLID — 3 principles với ví dụ Java

### Behavioral
- [ ] Self introduction — 90 giây, mention số liệu
- [ ] 5 STAR stories — nói được không nhìn notes
- [ ] 5 câu hỏi ngược — đã chuẩn bị

---

## 📚 INDEX TÀI LIỆU KỸ THUẬT (Đọc theo thứ tự này)

| Ngày | File | Topics |
|---|---|---|
| Ngày 1 | [`03-Spring-Ecosystem/CV-Deep-Dive-Spring.md`](../03-Spring-Ecosystem/CV-Deep-Dive-Spring.md) | @Transactional, AOP, Security+JWT, Spring Cloud |
| Ngày 2 | [`06-Distributed-Systems/CV-Deep-Dive-Kafka-Redis.md`](../06-Distributed-Systems/CV-Deep-Dive-Kafka-Redis.md) | Kafka internals, Redis patterns |
| Ngày 3 | [`05-System-Design/CV-Deep-Dive-Microservices-Patterns.md`](../05-System-Design/CV-Deep-Dive-Microservices-Patterns.md) | Circuit Breaker, Saga, ACL, Idempotency |
| Ngày 3 | [`04-Database/CV-Deep-Dive-Database-Locking.md`](../04-Database/CV-Deep-Dive-Database-Locking.md) | Locking, EXPLAIN, N+1, Indexing |
| Ngày 4 | [`10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md`](../10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md) | OAuth2, K8s, SOLID, gRPC |

---

## 💡 NGUYÊN TẮC VÀNG

```
1. CV của bạn mạnh → Interviewer HỎI SÂU những gì đã ghi
   → Ôn kỹ 5 file tài liệu là đủ

2. Thiếu 1 năm kinh nghiệm → Bù bằng chiều sâu kỹ thuật FPM project
   → Luôn dẫn câu chuyện về FPM (10 services, gRPC, Kafka, Circuit Breaker)

3. Không biết Oracle → Admit thẳng + show adaptability
   → Script đã chuẩn bị sẵn ở Ngày 4

4. Nói to khi ôn → Nhớ lâu hơn 3x so với đọc im
   → Mỗi topic xong → giải thích to như đang phỏng vấn thật
```

---

*Updated: 2026-09-05 — Tối ưu cho JD: Java Backend Engineer (Spring Boot, Microservices, REST)*
