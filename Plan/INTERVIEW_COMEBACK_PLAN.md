# 🎯 INTERVIEW COMEBACK PLAN — Đặng Trọng Phúc
## Java Backend Engineer — Kế Hoạch Quay Lại Ôn Luyện

> **Ngày bắt đầu lại:** 27/09/2026
> **Tình trạng:** Lâu không ôn → cần warm-up + consolidate
> **Profile:** Java Backend ~3 năm | Spring Boot, Kafka, Redis, Microservices, gRPC

---

## 📊 AUDIT WORKSPACE HIỆN TẠI

### ✅ Tài Liệu Đã Có (Giữ Nguyên — Chất Lượng Cao)

| File | Nội dung | Chất lượng |
|:-----|:---------|:-----------|
| `CV-Deep-Dive-Spring.md` | @Transactional, AOP, JWT, Spring Cloud | ⭐⭐⭐⭐⭐ |
| `CV-Deep-Dive-Kafka-Redis.md` | Kafka internals, Redis patterns, Distributed Lock | ⭐⭐⭐⭐⭐ |
| `CV-Deep-Dive-Database-Locking.md` | Locking, N+1, EXPLAIN, Indexing | ⭐⭐⭐⭐⭐ |
| `CV-Deep-Dive-Microservices-Patterns.md` | Circuit Breaker, Saga, ACL | ⭐⭐⭐⭐⭐ |
| `CV-Gaps-OAuth2-K8s-SOLID-gRPC.md` | Gaps filling | ⭐⭐⭐⭐ |
| `JD-JAVA-BACKEND-ROADMAP.md` | Sprint 5 ngày, checklist | ⭐⭐⭐⭐⭐ |
| `SPRINT-INTERVIEW-ROADMAP.md` | Sprint 1 tuần cho JD cụ thể | ⭐⭐⭐⭐ |
| `QUICK-INTERVIEW-STUDY-GUIDE.md` | Phương pháp học | ⭐⭐⭐⭐⭐ |
| `weekly-tracker.md` | Progress tracking | ⭐⭐⭐⭐ |

### ⚠️ Vấn Đề Phát Hiện

- **Kế hoạch cũ đã hết hạn** (target 04/10/2026 — gần tới rồi)
- **Chưa track progress** — weekly-tracker.md hầu hết vẫn còn ⬜
- **Lâu không ôn** → kiến thức có nhưng chưa kích hoạt → cần warm-up trước khi sprint

> [!IMPORTANT]
> Workspace của bạn có chất lượng rất cao. Không cần tạo thêm tài liệu mới.
> Vấn đề là: **chưa practice** và **kế hoạch cần reset lại mốc thời gian**.

---

## 🧠 ĐÁNH GIÁ CV — STRENGTHS & GAPS

### 💪 Điểm Mạnh (Từ tài liệu đã có)

```
✅ Spring Boot + Microservices — hệ thống FPM 10 services
✅ Kafka — 8 topics, consumer group, at-least-once + idempotency
✅ Redis — Cache-aside, Distributed Lock (SETNX+Lua), Token Bucket
✅ gRPC — protobuf, service-to-service, ACL pattern
✅ Resilience4j — Circuit Breaker 3 states + fallback
✅ Database Locking — Optimistic/Pessimistic + wallet scenario
✅ Số liệu ấn tượng: ~30% DB reduction, sub-50ms latency, 10k users
✅ Hahalolo — ~40% query improvement (JPA fetch strategy + indexing)
```

### ❌ Gaps Cần Xử Lý

```
⚠️  Oracle SQL (nếu JD yêu cầu) → Cần script adapt sẵn
⚠️  OAuth2 Authorization Code Flow (biết concept, nói được flow)
⚠️  Kubernetes objects: Pod, Deployment, Service, Ingress
⚠️  Java 21 Virtual Threads (nếu JD senior/advanced)
⚠️  DSA — LeetCode tracker cho thấy hầu hết còn ⬜ (chưa practice đủ)
```

---

## ⚡ TỰ CHẤM ĐIỂM — LÀM NGAY HÔM NAY

> [!TIP]
> Điền bảng này TRƯỚC khi bắt đầu ôn. Tự trả lời to, không nhìn tài liệu.
> Điểm < 3/5 → ưu tiên ôn trước trong Tuần 1.

| Chủ đề | Tự chấm (1-5) | Cần đạt | Priority |
|:-------|:-------------|:--------|:---------|
| @Transactional + self-invocation trap | __/5 | 5/5 | 🔴 MUST |
| JWT filter chain flow (step by step) | __/5 | 5/5 | 🔴 MUST |
| Kafka: consumer group rebalancing | __/5 | 5/5 | 🔴 MUST |
| Redis: cache-aside + distributed lock | __/5 | 5/5 | 🔴 MUST |
| Circuit Breaker 3 states | __/5 | 5/5 | 🔴 MUST |
| Optimistic vs Pessimistic Locking | __/5 | 5/5 | 🔴 MUST |
| N+1 Problem — define + fix | __/5 | 4/5 | 🔴 MUST |
| OAuth2 vs JWT relationship | __/5 | 4/5 | 🟡 SHOULD |
| SOLID — S, O, D với ví dụ Java | __/5 | 4/5 | 🟡 SHOULD |
| K8s: Pod, Deployment, Service, Ingress | __/5 | 3/5 | 🟡 SHOULD |
| DSA: giải Medium < 25 phút | __/5 | 3/5 | 🟡 SHOULD |
| Self introduction 90 giây (có số liệu) | __/5 | 5/5 | 🔴 MUST |
| 3 STAR stories nói trôi chảy | __/5 | 5/5 | 🔴 MUST |
| English: giải thích technical concepts | __/5 | 3/5 | 🟡 SHOULD |

---

## 📅 KẾ HOẠCH 4 TUẦN — BẮT ĐẦU 28/09/2026

### Nguyên Tắc "Active Recall First" — Áp Dụng Ngay

```
ĐỪNG đọc lại từ đầu → Mở phần Q&A cuối mỗi file → Tự trả lời to
Blank chỗ nào → Mới mở lại đọc đúng section đó → Đóng → Thử lại
30 phút nói to > 3 tiếng đọc thầm
```

---

### 🔴 TUẦN 1 — WARM-UP & RECALL (28/09 - 04/10)

> **Mục tiêu**: Kích hoạt lại kiến thức đã có. Không học mới.

**Ngày 1 — CN 28/09:**
- [ ] Active Recall `CV-Deep-Dive-Spring.md` (chỉ Q&A cuối file):
  - `@Transactional` self-invocation trap → giải thích to 60s
  - JWT filter chain → giải thích từng bước
  - Spring AOP proxy JDK vs CGLIB
- [ ] DSA Warm-up: Two Sum, Valid Parentheses, Contains Duplicate (Easy x3)
- [ ] Điền bảng Self-Assessment ở trên

**Ngày 2 — T2 29/09:**
- [ ] Active Recall `CV-Deep-Dive-Kafka-Redis.md`:
  - Kafka: acks=all, consumer group rebalancing, offset management
  - Redis: Cache-aside flow, SETNX + Lua script atomicity
- [ ] DSA: Best Time Buy/Sell Stock, Maximum Subarray (x2)
- [ ] Nói to: "Tại sao FPM dùng at-least-once + idempotency chứ không phải exactly-once?"

**Ngày 3 — T3 30/09:**
- [ ] Active Recall `CV-Deep-Dive-Database-Locking.md`:
  - Optimistic vs Pessimistic — khi nào dùng cái nào + wallet scenario
  - N+1 problem — define, detect, fix với JOIN FETCH
- [ ] DSA: Reverse Linked List, Linked List Cycle (x2)
- [ ] Nói to: STAR #4 "Query optimization ~40% improvement tại Hahalolo"

**Ngày 4 — T4 01/10:**
- [ ] Active Recall `CV-Deep-Dive-Microservices-Patterns.md`:
  - Circuit Breaker 3 states — vẽ state diagram ra giấy
  - Saga Orchestration vs Choreography — khi nào chọn gì
- [ ] DSA: Number of Islands, Clone Graph (x2)
- [ ] Nói to: "FPM có 10 services — khi Wallet Service down, Transaction Engine handle thế nào?"

**Ngày 5 — T5 02/10:**
- [ ] Active Recall `CV-Gaps-OAuth2-K8s-SOLID-gRPC.md`:
  - OAuth2 vs JWT — relationship rõ ràng
  - Authorization Code Flow — 8 bước chính
- [ ] DSA: Binary Search, Search Rotated Array (x2)
- [ ] Nói to: Self introduction 90 giây — tiếng Việt + tiếng Anh

**Ngày 6-7 — T6-T7 03-04/10:**
- [ ] Full Mock Interview (tự nói to 45 phút):
  - Vòng 1 (30p): 6 câu kỹ thuật từ `JD-JAVA-BACKEND-ROADMAP.md`
  - Vòng 2 (15p): 3 STAR stories
- [ ] List ra 3 điểm yếu nhất → ưu tiên Tuần 2

---

### 🟡 TUẦN 2 — CONSOLIDATE & DEEPEN (05/10 - 11/10)

> **Mục tiêu**: Ôn sâu điểm yếu + Fill gaps + DSA tăng tốc

| Ngày | Sáng (2h) | Tối (1.5h) |
|:-----|:----------|:-----------|
| T2 | Ôn kỹ 3 điểm yếu từ Mock Tuần 1 | DSA: 2-3 bài Medium |
| T3 | `02-Java-Core/Theory.md` — Concurrency, Generics, Stream API | DSA: 2-3 bài Medium |
| T4 | `CV-Gaps-OAuth2-K8s-SOLID-gRPC.md` — K8s deep + SOLID | DSA: Trees practice |
| T5 | `03-Spring/02-spring-security-deep.md` — Security deep | DSA: DP basics |
| T6 | `04-Database/02-postgresql-advanced.md` — CTEs, Window Fn | Mock coding 1h |
| T7 | System Design: URL Shortener + Rate Limiter | STAR stories practice |
| CN | Full Mock Interview lần 2 (nhờ người hỏi nếu được) | Review + Nghỉ |

---

### 🟢 TUẦN 3 — SYSTEM DESIGN + APPLY (12/10 - 18/10)

> **Mục tiêu**: 5 System Design problems + Bắt đầu nộp CV

| Ngày | System Design Topic | DSA Maintenance |
|:-----|:--------------------|:----------------|
| T2 | Payment System (Idempotency Key, Reconciliation) | 1-2 bài/tối |
| T3 | Chat System (WebSocket, Kafka, Wide-column DB) | 1-2 bài/tối |
| T4 | Distributed Cache (Consistent Hashing) | 1-2 bài/tối |
| T5 | News Feed (Fanout-on-write vs Fanout-on-read) | 1-2 bài/tối |
| T6 | Search / Autocomplete (Trie, Elasticsearch concept) | 1-2 bài/tối |
| T7 | Nộp CV: 3-5 công ty (NAB, MoMo, VNPay, Money Forward) | Review SD |
| CN | Full Mock Interview #3 + STAR stories polish | Nghỉ ngơi |

---

### 🏁 TUẦN 4 — INTERVIEW SPRINT (19/10 - 25/10)

> **Mục tiêu**: Company-specific prep + Active interviews

- Research tech stack: NAB, MoMo, VNPay, Money Forward
- Customize câu trả lời theo stack từng công ty
- DSA: 1-2 Easy/Medium mỗi tối (giữ phản xạ)
- Theo dõi feedback + chuẩn bị từng vòng

---

## 📚 THỨ TỰ ĐỌC FILE — THEO ƯU TIÊN

```
🔴 NGAY LẬP TỨC (Active Recall — không đọc lại từ đầu):
  1. CV-Deep-Dive-Spring.md           → Q&A cuối file → tự trả lời
  2. CV-Deep-Dive-Kafka-Redis.md      → Q&A cuối file → tự trả lời
  3. CV-Deep-Dive-Database-Locking.md → Q&A cuối file → tự trả lời
  4. CV-Deep-Dive-Microservices-Patterns.md → Q&A cuối file

🟡 SAU 3-4 NGÀY (nếu blank nhiều):
  5. CV-Gaps-OAuth2-K8s-SOLID-gRPC.md → Full read
  6. 02-Java-Core/Theory.md           → Concurrency + Generics section
  7. 03-Spring/02-spring-security-deep.md → Security deep

🟢 TUẦN 3:
  8. 05-System-Design/ — 5-10 problems
  9. 02-Java-Core/07-java17-21-features.md — Java 21 VThread
```

---

## 🗣️ DAILY SPEAKING DRILL (15 phút/ngày)

Mỗi ngày 1 câu — nói to 3 lần — record nếu có thể:

| Ngày | Câu drill |
|:-----|:----------|
| CN 28/09 | "Explain @Transactional self-invocation trap và 3 cách fix" |
| T2 29/09 | "Kafka consumer group rebalancing — khi nào trigger, protocol là gì?" |
| T3 30/09 | "Tại sao FPM chọn Pessimistic Locking cho wallet balance?" |
| T4 01/10 | "Circuit Breaker đi từ CLOSED → OPEN → HALF_OPEN như thế nào?" |
| T5 02/10 | "Tell me about yourself" (90 giây, tiếng Anh, có số liệu CV) |
| T6 03/10 | "Walk me through your FPM system architecture" |
| T7 04/10 | Full mock — tất cả câu trả lời |

---

## 🚨 EMERGENCY SPRINT — Nếu Phỏng Vấn Trong 3 Ngày

```
NGÀY 1 — Sáng (4h):
  ✅ @Transactional self-invocation trap (CV-Deep-Dive-Spring.md)
  ✅ JWT filter chain flow
  ✅ Kafka: consumer group + offset + at-least-once
  ✅ Redis: cache-aside + distributed lock concept
  ✅ Circuit Breaker: 3 states + threshold (Resilience4j)

NGÀY 1 — Chiều (3h):
  ✅ Optimistic vs Pessimistic Locking + wallet scenario
  ✅ N+1 problem + fix với JOIN FETCH
  ✅ OAuth2 vs JWT relationship + Auth Code Flow steps

NGÀY 2 — Sáng (4h):
  ✅ 3 STAR Stories (mở star-stories.md, đọc rồi nói to)
  ✅ Self introduction 90 giây (có số liệu: 30%, sub-50ms, 10k)
  ✅ DSA: 3 Easy warm-up (Two Sum, Valid Parens, Contains Dup)

NGÀY 2 — Tối:
  ✅ Full mock interview — nói to 45 phút
  ✅ 5 câu hỏi ngược cho interviewer
  ✅ NGỦ ĐỦ GIẤC

NGÀY 3 — Sáng phỏng vấn:
  ✅ Đọc lại TOP 20 câu kỹ thuật (JD-JAVA-BACKEND-ROADMAP.md)
  ✅ Nhẩm lại self-introduction
  ✅ Đọc lại CV — mọi số liệu phải explain được chi tiết
```

---

## 💡 NGUYÊN TẮC VÀNG — QUAY LẠI SAU BREAK

```
1. ĐỪNG đọc lại từ đầu → Dùng Active Recall từ Q&A sections
   Kiến thức nền vẫn còn → Chỉ cần kích hoạt → 3-4 ngày là recall 80%

2. Ưu tiên "nói được" hơn "đọc nhiều"
   30 phút nói to > 3 tiếng đọc thầm (não encode mạnh hơn)

3. Số liệu trong CV là vũ khí chủ lực
   "~30% DB reduction" ← phải giải thích được: Redis cache-aside giảm query,
   Kafka async processing giảm sync DB writes, Indexing tối ưu hot path

4. DSA: Bắt đầu Easy → không nhảy vào Hard ngay
   3 Easy/ngày × 3 ngày → tự tin → Medium dưới 25 phút

5. Mỗi concept phải có "ví dụ từ project của tôi"
   Không nói lý thuyết suông → Link về FPM / Gihot / Hahalolo
```

---

*Cập nhật: 27/09/2026 — Comeback Plan sau thời gian break*
