# 🎯 INTERVIEW COMEBACK PLAN — Đặng Trọng Phúc
## Java Backend Engineer — Kế Hoạch Quay Lại Ôn Luyện

> **Ngày bắt đầu lại:** 20/10/2026 (Tối nay 20:00)
> **Ngày target phỏng vấn:** 16/11/2026 (4 tuần)
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

- **Kế hoạch cũ đã hết hạn** — reset mốc bắt đầu lại từ tối nay 20/10/2026
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

## 📅 KẾ HOẠCH 4 TUẦN — BẮT ĐẦU 20/10/2026

### Nguyên Tắc "Active Recall First" — Áp Dụng Ngay

```
ĐỪNG đọc lại từ đầu → Mở phần Q&A cuối mỗi file → Tự trả lời to
Blank chỗ nào → Mới mở lại đọc đúng section đó → Đóng → Thử lại
30 phút nói to > 3 tiếng đọc thầm
```

---

### 🔴 TUẦN 1 — WARM-UP & RECALL (20/10 - 26/10)

> **Mục tiêu**: Kích hoạt lại kiến thức đã có. Không học mới.
> **Lịch Thứ 7 24/10**: Ngày chẵn → NGHỈ | **Chủ Nhật 25/10**: Nghỉ

<details>
<summary><b>Week 1 (20/10 - 26/10): Active Recall — Spring, Kafka/Redis, DB Locking, Microservices</b></summary>

#### Thứ 3 (20/10) — 🚀 BẮT ĐẦU TỐI NAY (20:00 - 22:00)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nghe và nhại theo 1 đoạn technical explanation trên YouTube (System Design Insider / NeetCode).
  * *Từ vựng (20m):* [B1-B2-Interview-Vocabulary.md](file:///d:/WorkSpace/Document/Improve-Knowledge/B1-B2-Interview-Vocabulary.md) — Ôn lại 10 từ đầu.
  * *Ngữ pháp (20m):* Viết 3 câu giải thích @Transactional bằng tiếng Anh.
* **Sáng Deep Topic (05:30 - 07:00):** [CV-Deep-Dive-Spring.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/CV-Deep-Dive-Spring.md) — Chỉ phần Q&A cuối file.
  * *Active Recall:* @Transactional self-invocation trap → giải thích to 60s không nhìn tài liệu.
  * *Active Recall:* JWT filter chain flow → giải thích từng bước.
  * *Active Recall:* Spring AOP proxy JDK vs CGLIB — khi nào dùng cái nào.
* **Tối DSA (20:00 - 21:30):** LeetCode Easy Warm-up: Two Sum, Contains Duplicate, Valid Anagram.
* **Tối Speaking (21:30 - 22:00):** Nói to: "Explain @Transactional self-invocation trap và 3 cách fix." Record nếu được.

#### Thứ 4 (21/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại theo cách giải thích Kafka trên YouTube.
  * *Từ vựng (20m):* [B1-B2-Interview-Vocabulary.md](file:///d:/WorkSpace/Document/Improve-Knowledge/B1-B2-Interview-Vocabulary.md) — Ôn 10 từ tiếp theo.
  * *Ngữ pháp (20m):* Viết câu mô tả Kafka flow bằng tiếng Anh (Passive Voice).
* **Sáng Deep Topic (05:30 - 06:30):** [CV-Deep-Dive-Kafka-Redis.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/CV-Deep-Dive-Kafka-Redis.md) — Q&A cuối file.
  * *Active Recall:* Kafka: acks=all, consumer group rebalancing, offset management.
  * *Active Recall:* Redis: Cache-aside flow, SETNX + Lua script atomicity — tại sao cần Lua?
* **Sáng Java (06:30 - 07:00):** [CV-Deep-Dive-Spring.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/CV-Deep-Dive-Spring.md) — Spring Cloud Gateway section.
* **Tối DSA (20:00 - 21:30):** LeetCode Easy: Best Time Buy/Sell Stock, Maximum Subarray.
* **Tối Speaking (21:30 - 22:00):** Nói to: "Tại sao FPM dùng at-least-once + idempotency chứ không phải exactly-once?"

#### Thứ 5 (22/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại cách giải thích DB locking.
  * *Từ vựng (20m):* Ôn các từ liên quan DB: `deadlock`, `race condition`, `isolation level`.
  * *Ngữ pháp (20m):* Viết câu dùng Conditional Type 2 (If we hadn't used pessimistic locking...).
* **Sáng Deep Topic (05:30 - 06:30):** [CV-Deep-Dive-Database-Locking.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/CV-Deep-Dive-Database-Locking.md) — Q&A cuối file.
  * *Active Recall:* Optimistic vs Pessimistic — khi nào dùng cái nào + wallet scenario.
  * *Active Recall:* N+1 problem — define, detect, fix với JOIN FETCH.
* **Sáng Java (06:30 - 07:00):** [CV-Deep-Dive-Database-Locking.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/CV-Deep-Dive-Database-Locking.md) — EXPLAIN ANALYZE section.
* **Tối DSA (20:00 - 21:30):** LeetCode Easy/Medium: Reverse Linked List, Linked List Cycle.
* **Tối Speaking (21:30 - 22:00):** Nói to STAR #4: "Query optimization ~40% improvement tại Hahalolo."

#### Thứ 6 (23/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại cách giải thích Circuit Breaker pattern.
  * *Từ vựng (20m):* Ôn từ microservices: `circuit breaker`, `fallback`, `saga`, `orchestration`.
  * *Ngữ pháp (20m):* Viết câu dùng Present Perfect để kể về project FPM.
* **Sáng Deep Topic (05:30 - 06:30):** [CV-Deep-Dive-Microservices-Patterns.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/CV-Deep-Dive-Microservices-Patterns.md) — Q&A cuối file.
  * *Active Recall:* Circuit Breaker 3 states — vẽ state diagram ra giấy rồi giải thích.
  * *Active Recall:* Saga Orchestration vs Choreography — khi nào chọn gì, FPM chọn gì?
* **Sáng Java (06:30 - 07:00):** [CV-Deep-Dive-Microservices-Patterns.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/CV-Deep-Dive-Microservices-Patterns.md) — Anti-Corruption Layer section.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: Number of Islands, Clone Graph.
* **Tối Speaking (21:30 - 22:00):** Nói to: "FPM có 10 services — khi Wallet Service down, Transaction Engine handle thế nào?"

#### Thứ 7 (24/10) — Ngày chẵn — NGHỈ
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại giải thích OAuth2 flow bằng tiếng Anh.
  * *Từ vựng (20m):* Ôn: `authorization`, `authentication`, `access token`, `refresh token`.
  * *Ngữ pháp (20m):* Viết Self Introduction 90 giây bằng tiếng Anh, có số liệu CV.
* **Sáng Deep Topic (05:30 - 06:30):** [CV-Gaps-OAuth2-K8s-SOLID-gRPC.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md) — OAuth2 section.
  * *Active Recall:* OAuth2 vs JWT — relationship rõ ràng.
  * *Active Recall:* Authorization Code Flow — 8 bước chính, tại sao back-channel an toàn hơn?
* **Sáng Java (06:30 - 07:00):** [CV-Gaps-OAuth2-K8s-SOLID-gRPC.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md) — SOLID section.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: Binary Search, Search Rotated Array.
* **Tối Speaking (21:30 - 22:00):** Nói to Self Introduction 90 giây — tiếng Anh, có số liệu: 30%, sub-50ms, 10k users.

#### Chủ Nhật (25/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện walk-through architecture presentation.
  * *Từ vựng (20m):* Ôn từ system design: `scalability`, `availability`, `consistency`.
  * *Ngữ pháp (20m):* Viết câu mô tả FPM architecture bằng tiếng Anh (Present Simple).
* **Sáng Deep Topic (05:30 - 07:00):** Ôn tổng hợp: Xem lại các điểm blank từ Active Recall 4 ngày trước.
  * *Review:* Mở lại các section còn blank và đọc lại đúng chỗ đó.
* **Tối DSA (20:00 - 21:30):** LeetCode: Climbing Stairs, House Robber (DP Easy).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Walk me through your FPM system architecture." (3 phút)

#### Thứ 2 (26/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Mock interview opening luyện tập.
  * *Từ vựng (20m):* Ôn lại toàn bộ từ tuần này.
  * *Ngữ pháp (20m):* Review lại câu đã viết trong tuần.
* **Sáng Deep Topic (05:30 - 06:30):** Full Mock: 6 câu kỹ thuật từ [JD-JAVA-BACKEND-ROADMAP.md](file:///d:/WorkSpace/Document/Improve-Knowledge/Plan/JD-JAVA-BACKEND-ROADMAP.md) — nói to.
* **Sáng Java (06:30 - 07:00):** List ra 3 điểm yếu nhất → ghi vào tracker để ưu tiên Tuần 2.
* **Tối DSA (20:00 - 22:00):** LeetCode Timed: 3 bài Medium/90 phút. Viết STAR stories #1 + #2 sau khi giải xong.

</details>

---

### 🟡 TUẦN 2 — CONSOLIDATE & DEEPEN (27/10 - 02/11)

> **Mục tiêu**: Ôn sâu điểm yếu + Fill gaps + DSA tăng tốc
> **Lịch Thứ 7 31/10**: Ngày lẻ → ĐI LÀM | **Chủ Nhật 01/11**: Nghỉ

<details>
<summary><b>Week 2 (27/10 - 02/11): Consolidate Weak Points, K8s, Java Core Deep, PostgreSQL Advanced</b></summary>

#### Thứ 3 (27/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện nói về điểm yếu từ Mock Tuần 1.
  * *Từ vựng (20m):* Ôn từ Java concurrency: `thread-safe`, `deadlock`, `volatile`, `synchronized`.
  * *Ngữ pháp (20m):* Viết câu dùng Past Simple kể về bug production đã fix.
* **Sáng Deep Topic (05:30 - 07:00):** Ôn kỹ 3 điểm yếu nhất từ Mock Tuần 1 (tự xác định từ tracker).
* **Tối DSA (20:00 - 21:30):** LeetCode Review: Giải lại các bài bị stuck tuần trước, tối ưu code.
* **Tối Speaking (21:30 - 22:00):** Giải thích STAR #3 "Kafka vs RabbitMQ decision" bằng tiếng Anh.

#### Thứ 4 (28/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại giải thích Java concurrency.
  * *Từ vựng (20m):* Ôn từ: `race condition`, `atomic`, `immutable`, `lock`.
  * *Ngữ pháp (20m):* Viết câu mô tả thread lifecycle bằng Present Simple.
* **Sáng Deep Topic (05:30 - 06:30):** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) — Java Concurrency: synchronized, ReentrantLock, volatile.
  * *Ví dụ:* Viết code demo race condition và fix bằng synchronized block.
* **Sáng Java (06:30 - 07:00):** [Theory.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/Theory.md) — Generics và Stream API.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: 2-3 bài (Trees: Invert Binary Tree, Max Depth, Level Order).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Explain thread-safety issues and how you solved them in production."

#### Thứ 5 (29/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại giải thích K8s concept.
  * *Từ vựng (20m):* Ôn từ K8s: `pod`, `deployment`, `service`, `ingress`, `namespace`.
  * *Ngữ pháp (20m):* Viết câu Conditional Type 1 về K8s scaling scenario.
* **Sáng Deep Topic (05:30 - 06:30):** [CV-Gaps-OAuth2-K8s-SOLID-gRPC.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md) — K8s section.
  * *Active Recall:* 4 K8s objects: Pod, Deployment, Service, Ingress — giải thích bằng lời.
  * *Active Recall:* Docker → K8s mental model: docker run → Pod, docker-compose → Deployment.
* **Sáng Java (06:30 - 07:00):** [CV-Gaps-OAuth2-K8s-SOLID-gRPC.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md) — SOLID với ví dụ Java thực tế.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: Trees practice (Validate BST, Path Sum).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Describe K8s vs Docker Compose — why do we need K8s?"

#### Thứ 6 (30/10)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại giải thích Spring Security filter chain.
  * *Từ vựng (20m):* Ôn: `CORS`, `CSRF`, `XSS`, `JWT expiry`, `refresh token rotation`.
  * *Ngữ pháp (20m):* Viết câu giải thích security flow bằng Passive Voice.
* **Sáng Deep Topic (05:30 - 06:30):** [02-spring-security-deep.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/02-spring-security-deep.md) — JWT + OAuth2 deep.
  * *Active Recall:* JWT blacklisting với Redis — flow chi tiết.
  * *Active Recall:* Token refresh rotation — tại sao cần?
* **Sáng Java (06:30 - 07:00):** [02-spring-security-deep.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/02-spring-security-deep.md) — RBAC vs ABAC section.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: DP basics (Climbing Stairs, Coin Change, House Robber).
* **Tối Speaking (21:30 - 22:00):** Nói to STAR #2: "High concurrency 10k users stress test — tìm bottleneck."

#### Thứ 7 (31/10) — Ngày lẻ — ĐI LÀM
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Nhại giải thích PostgreSQL advanced features.
  * *Từ vựng (20m):* Ôn: `CTE`, `window function`, `partition`, `JSONB`, `GIN index`.
  * *Ngữ pháp (20m):* Viết câu dùng Relative Clause để giải thích query optimization.
* **Sáng Deep Topic (05:30 - 06:30):** [02-postgresql-advanced.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/02-postgresql-advanced.md) — CTEs, Window Functions, JSONB.
  * *Thực hành:* Viết 1 query dùng ROW_NUMBER() + PARTITION BY.
  * *Thực hành:* Viết 1 Recursive CTE cho hierarchical data.
* **Sáng Java (06:30 - 07:00):** [07-java17-21-features.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/07-java17-21-features.md) — Java 17 records, sealed classes.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: DP advanced (Longest Increasing Subsequence, Word Break).
* **Tối Speaking (21:30 - 22:00):** Nói to STAR #1: "Performance optimization ~30% DB reduction tại Gihot."

#### Chủ Nhật (01/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Mock coding interview explanation practice.
  * *Từ vựng (20m):* Ôn từ System Design: `horizontal scaling`, `load balancing`, `cache stampede`.
  * *Ngữ pháp (20m):* Viết câu dùng Comparatives/Superlatives khi trade-off giải pháp.
* **Sáng Deep Topic (05:30 - 07:00):** System Design: URL Shortener (Base62, hash collision, Cassandra/Redis).
  * *Output:* Vẽ architecture diagram ra giấy. Giải thích to 5 phút.
* **Tối DSA (20:00 - 21:30):** LeetCode Timed: 3 bài Mixed/90 phút (giả lập interview).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Design a URL shortener for 100M URLs." (5 phút)

#### Thứ 2 (02/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Ôn lại Rate Limiter system design explanation.
  * *Từ vựng (20m):* Ôn từ rate limiting: `token bucket`, `sliding window`, `throttle`.
  * *Ngữ pháp (20m):* Viết câu giải thích rate limiter bằng tiếng Anh.
* **Sáng Deep Topic (05:30 - 06:30):** System Design: Rate Limiter (Token Bucket, Redis Cluster, Lua script).
* **Sáng Java (06:30 - 07:00):** [07-java17-21-features.md](file:///d:/WorkSpace/Document/Improve-Knowledge/02-Java-Core/07-java17-21-features.md) — Java 21 Virtual Threads.
* **Tối DSA (20:00 - 22:00):** LeetCode Marathon: 4-6 bài Medium (2 tiếng timed). Viết STAR stories #4 + #5 sau khi giải xong.

</details>

---

### 🟢 TUẦN 3 — SYSTEM DESIGN + APPLY (03/11 - 09/11)

> **Mục tiêu**: 5 System Design problems + Bắt đầu nộp CV
> **Lịch Thứ 7 07/11**: Ngày lẻ → ĐI LÀM | **Chủ Nhật 08/11**: Nghỉ

<details>
<summary><b>Week 3 (03/11 - 09/11): System Design Deep, STAR Stories Polish, Start Applying</b></summary>

#### Thứ 3 (03/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện System Design discussion template bằng tiếng Anh.
  * *Từ vựng (20m):* Ôn từ payment: `idempotency`, `reconciliation`, `eventual consistency`.
  * *Ngữ pháp (20m):* Viết câu trade-off giải pháp payment (Modal Verbs: "We should/could/would...").
* **Sáng Deep Topic (05:30 - 07:00):** System Design: Payment System (Idempotency Key, double-spend prevention, Reconciliation Service).
  * *Output:* Vẽ architecture, giải thích idempotency key pattern.
* **Tối DSA (20:00 - 21:30):** LeetCode Review: Giải lại bài yếu nhất tuần 2.
* **Tối Speaking (21:30 - 22:00):** Nói to: "Design a payment system — how do you ensure idempotency?" (5 phút)

#### Thứ 4 (04/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện WebSocket and real-time system explanation.
  * *Từ vựng (20m):* Ôn: `WebSocket`, `long polling`, `SSE`, `pub-sub`, `fan-out`.
  * *Ngữ pháp (20m):* Viết câu Compare/Contrast WebSocket vs HTTP polling.
* **Sáng Deep Topic (05:30 - 06:30):** System Design: Chat System (WebSocket, Kafka message queues, Wide-column DB).
  * *Output:* Vẽ architecture cho 1-1 chat và group chat.
* **Sáng Java (06:30 - 07:00):** [04-spring-cloud.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/04-spring-cloud.md) — Spring Cloud Gateway + Eureka.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: Graphs (Number of Islands, Pacific Atlantic Water Flow).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Design a chat system that supports 1M concurrent users." (5 phút)

#### Thứ 5 (05/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện giải thích distributed caching.
  * *Từ vựng (20m):* Ôn: `consistent hashing`, `cache stampede`, `hot key`, `eviction`.
  * *Ngữ pháp (20m):* Viết câu dùng Passive Voice để mô tả how cache is populated/evicted.
* **Sáng Deep Topic (05:30 - 06:30):** System Design: Distributed Cache (Consistent Hashing, Cache Stampede prevention, Redis Cluster).
  * *Output:* Giải thích consistent hashing ring bằng lời, vẽ diagram.
* **Sáng Java (06:30 - 07:00):** [CV-Deep-Dive-Kafka-Redis.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/CV-Deep-Dive-Kafka-Redis.md) — Redis Cluster section.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: 2-3 bài (Coin Change, LIS, Unique Paths).
* **Tối Speaking (21:30 - 22:00):** Nói to: "How does consistent hashing work and why do we need it?" (3 phút)

#### Thứ 6 (06/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện news feed system explanation.
  * *Từ vựng (20m):* Ôn: `fan-out`, `timeline`, `push model`, `pull model`, `hybrid`.
  * *Ngữ pháp (20m):* Viết câu Compare push vs pull model dùng Comparatives.
* **Sáng Deep Topic (05:30 - 06:30):** System Design: News Feed System (Fanout-on-write vs Fanout-on-read, Celebrity problem).
  * *Output:* Giải thích trade-off của 2 model, khi nào dùng hybrid.
* **Sáng Java (06:30 - 07:00):** Chuẩn bị Company Research: NAB, MoMo, VNPay tech stack.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: 2-3 bài (Intervals: Merge Intervals, Insert Interval).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Design a Twitter news feed — fanout-on-write vs fanout-on-read?" (5 phút)

#### Thứ 7 (07/11) — Ngày lẻ — ĐI LÀM
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện search autocomplete system explanation.
  * *Từ vựng (20m):* Ôn: `trie`, `prefix`, `autocomplete`, `inverted index`, `Elasticsearch`.
  * *Ngữ pháp (20m):* Viết câu mô tả Trie data structure bằng tiếng Anh.
* **Sáng Deep Topic (05:30 - 06:30):** System Design: Search / Autocomplete (Trie, Elasticsearch, ranking algorithm).
  * *Output:* Vẽ Trie diagram, giải thích prefix search và ranking.
* **Sáng Java (06:30 - 07:00):** Chuẩn bị CV: Update self-introduction, check số liệu khớp với những gì nói được.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: Bit Manipulation + Greedy (2-3 bài).
* **Tối Speaking (21:30 - 22:00):** Full STAR #5: "System design: 10-service FPM platform với strict domain boundaries."

#### Chủ Nhật (08/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện behavioral question answers.
  * *Từ vựng (20m):* Ôn từ behavioral: `collaborate`, `ownership`, `initiative`, `trade-off`.
  * *Ngữ pháp (20m):* Viết câu STAR format bằng tiếng Anh cho 1 story.
* **Sáng Deep Topic (05:30 - 07:00):** Chuẩn bị nộp CV: Polish CV tiếng Anh + Viết cover letter template cho NAB / MoMo.
  * *Action:* Nộp CV vào 2-3 công ty target đầu tiên.
* **Tối DSA (20:00 - 21:30):** LeetCode Timed: 3 bài Mixed/90 phút (giả lập interview round 2).
* **Tối Speaking (21:30 - 22:00):** Nói to: "Tell me about a time you had a disagreement with a teammate." (STAR format)

#### Thứ 2 (09/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện tổng hợp — SD discussion + behavioral.
  * *Từ vựng (20m):* Ôn từ negotiation: `expectation`, `package`, `equity`, `flexible`.
  * *Ngữ pháp (20m):* Viết câu salary negotiation bằng tiếng Anh (Polite requests).
* **Sáng Deep Topic (05:30 - 07:00):** Review toàn bộ 5 System Design đã học — mỗi cái 5 phút tóm tắt architecture.
* **LeetCode Marathon (08:00 - 10:00):** 6-8 bài Mixed difficulty (2 tiếng). Track time per problem.
* **System Design Review (10:00 - 11:30):** Re-draw tất cả 5 SD architectures từ memory ra giấy.
* **STAR Stories (11:30 - 12:00):** Đọc lại [star-stories.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/star-stories.md) — 5 stories nói to không nhìn notes.
* **Java/Spring Deep (13:30 - 15:30):** Ôn lại những gap còn lại từ Self-Assessment ban đầu.
* **CV & Apply (15:30 - 17:00):** Nộp CV thêm 2-3 công ty (VNPay, Money Forward). LinkedIn update.
* **Weekly Review (17:00 - 17:30):** Đánh giá tiến độ tuần 3. Chuẩn bị plan cho tuần 4.

</details>

---

### 🏁 TUẦN 4 — INTERVIEW SPRINT (10/11 - 16/11)

> **Mục tiêu**: Company-specific prep + Active interviews + Salary negotiation ready
> **Lịch Thứ 7 14/11**: Ngày chẵn → NGHỈ | **Chủ Nhật 15/11**: Nghỉ

<details>
<summary><b>Week 4 (10/11 - 16/11): Company Prep, Active Interviews, Mock & Negotiate</b></summary>

#### Thứ 3 (10/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện Company-specific interview opening.
  * *Từ vựng (20m):* Ôn từ fintech: `payment gateway`, `compliance`, `KYC`, `AML`.
  * *Ngữ pháp (20m):* Viết câu "Why do you want to join [Company]?" bằng tiếng Anh.
* **Sáng Deep Topic (05:30 - 07:00):** Research MoMo tech stack: Spring Boot, Kafka, Redis, K8s. Customize câu trả lời.
  * *Chuẩn bị:* 5 câu hỏi ngược cho MoMo interviewer.
* **Tối DSA (20:00 - 21:30):** LeetCode Easy/Medium warm-up: 2 bài giữ phản xạ.
* **Tối Speaking (21:30 - 22:00):** Full mock interview: Self intro + 3 technical + 2 behavioral (cho MoMo).

#### Thứ 4 (11/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện NAB interview prep (English-heavy company).
  * *Từ vựng (20m):* Ôn từ banking: `core banking`, `transaction`, `compliance`, `microservices`.
  * *Ngữ pháp (20m):* Viết 2 câu hỏi bằng tiếng Anh hỏi ngược interviewer NAB.
* **Sáng Deep Topic (05:30 - 06:30):** Research NAB tech stack: Java, Spring Boot, AWS, Kafka. Customize câu trả lời.
* **Sáng Java (06:30 - 07:00):** Ôn nhanh AWS core: EC2, S3, RDS, Lambda, IAM — khái niệm cơ bản (NAB dùng AWS).
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: 2 bài Mixed.
* **Tối Speaking (21:30 - 22:00):** Full mock interview cho NAB (English — toàn bộ bằng tiếng Anh).

#### Thứ 5 (12/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Luyện VNPay interview opening (fintech context).
  * *Từ vựng (20m):* Ôn từ payment processing: `settlement`, `gateway`, `PSP`, `QR code`.
  * *Ngữ pháp (20m):* Viết câu "Describe your experience with high-volume payment systems."
* **Sáng Deep Topic (05:30 - 06:30):** Research VNPay tech stack. Kết nối FPM payment experience với VNPay domain.
* **Sáng Java (06:30 - 07:00):** Ôn lại Payment System design từ Tuần 3 — áp dụng vào VNPay context.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: 2 bài Mixed.
* **Tối Speaking (21:30 - 22:00):** Full mock: "How would you design VNPay's payment processing system?" (10 phút)

#### Thứ 6 (13/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Salary negotiation English practice.
  * *Từ vựng (20m):* Ôn từ negotiation: `competitive compensation`, `market rate`, `benefits package`.
  * *Ngữ pháp (20m):* Viết câu salary negotiation script bằng tiếng Anh (Polite but assertive).
* **Sáng Deep Topic (05:30 - 07:00):** Research Money Forward tech stack (Japanese company — culture fit quan trọng).
  * *Chuẩn bị:* Câu hỏi về team culture, engineering practices, growth opportunities.
* **Tối DSA (20:00 - 21:30):** LeetCode Medium: 2 bài Mixed.
* **Tối Speaking (21:30 - 22:00):** Practice salary negotiation script to bằng tiếng Anh.

#### Thứ 7 (14/11) — Ngày chẵn — NGHỈ
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Ôn lại weak topics trong English từ suốt 4 tuần.
  * *Từ vựng (20m):* Review tất cả từ vựng đã học.
  * *Ngữ pháp (20m):* Viết 5 câu dùng cấu trúc khác nhau đã học trong 4 tuần.
* **Sáng Deep Topic (05:30 - 07:00):** Final Review: Đọc lại TOP 20 câu kỹ thuật từ [JD-JAVA-BACKEND-ROADMAP.md](file:///d:/WorkSpace/Document/Improve-Knowledge/Plan/JD-JAVA-BACKEND-ROADMAP.md).
  * *Active Recall:* Trả lời to từng câu. Câu nào blank → đọc lại section đó ngay.
* **Tối DSA (20:00 - 21:30):** LeetCode Easy: 3 bài giữ phản xạ (không cần Medium).
* **Tối Speaking (21:30 - 22:00):** Full Final Mock Interview — 45 phút tự nói to. Record và review.

#### Chủ Nhật (15/11)
* **Sáng English (04:30 - 05:30):**
  * *Shadowing (20m):* Ôn Self Introduction + Why this company.
  * *Từ vựng (20m):* Review 10 từ quan trọng nhất trong 4 tuần.
  * *Ngữ pháp (20m):* Nhẩm lại STAR story #1 và #5 bằng tiếng Anh.
* **Sáng Deep Topic (05:30 - 07:00):** Đọc lại CV — mọi số liệu phải giải thích được chi tiết:
  * ~30% DB reduction: Redis cache-aside + Kafka async + DB indexing.
  * sub-50ms latency: gRPC/Protobuf + Redis in-memory + HikariCP.
  * 10k users: Load testing + horizontal scaling + Kafka partitioning.
* **Tối**: Nghỉ ngơi hoàn toàn — không học kỹ thuật nặng.

#### Thứ 2 (16/11)
* **Sáng (04:30 - 07:00):** Nhẹ nhàng — chỉ đọc lại Self Introduction + 5 câu hỏi ngược.
* **Tối (20:00 - 21:00):** Nghỉ ngơi hoặc nhẹ nhàng ôn 1-2 câu yếu nhất. KHÔNG học nhiều.

</details>

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

| Ngày | Câu drill |
|:-----|:----------|
| T3 20/10 | "Explain @Transactional self-invocation trap và 3 cách fix" |
| T4 21/10 | "Kafka consumer group rebalancing — khi nào trigger, protocol là gì?" |
| T5 22/10 | "Tại sao FPM chọn Pessimistic Locking cho wallet balance?" |
| T6 23/10 | "Circuit Breaker đi từ CLOSED → OPEN → HALF_OPEN như thế nào?" |
| T7 24/10 | "Tell me about yourself" (90 giây, tiếng Anh, có số liệu CV) |
| CN 25/10 | "Walk me through your FPM system architecture" |
| T2 26/10 | Full mock — tất cả câu trả lời |
| T3 27/10 | "Describe a time you optimized a slow system" (STAR format EN) |
| T4 28/10 | "How do you handle thread safety in Java?" |
| T5 29/10 | "Explain Kubernetes Pod vs Deployment vs Service" |
| T6 30/10 | "How does JWT blacklisting work with Redis?" |
| T7 31/10 | "Design a rate limiter using Redis Token Bucket" |
| CN 01/11 | "Design a URL shortener for 100M URLs" |
| T2 02/11 | Full mock lần 2 — English only |
| T4 04/11 | "Design a payment system with idempotency key" |
| T5 05/11 | "Design a chat system for 1M concurrent users" |
| T6 06/11 | "Design a distributed cache with consistent hashing" |
| T7 07/11 | "Why do you want to join NAB/MoMo?" |
| CN 08/11 | "What's your expected salary?" (EN, assertive) |
| T2 09/11 | Full mock lần 3 — company-specific |

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

*Cập nhật: 20/10/2026 — Comeback Plan sau thời gian break (4 tuần, target: 16/11/2026)*
