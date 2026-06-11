# 🎤 STAR Stories — Behavioral Interview Prep

> **STAR Format:** Situation → Task → Action → Result  
> Chuẩn bị 6 stories cover các câu hỏi phổ biến nhất.  
> Mỗi story cần version Tiếng Việt + Tiếng Anh.

---

## 📌 Common Behavioral Questions Mapped to Stories

| Question Category | Story # | Phổ biến tại |
|:------------------|:--------|:-------------|
| "Tell me about a difficult bug" | #1 | MoMo, Tiki, NAB |
| "How did you improve performance?" | #2 | VNPay, ZaloPay |
| "Describe a system design decision" | #3 | NAB, MoMo |
| "How do you handle team conflicts?" | #4 | All companies |
| "Tell me about learning something new quickly" | #5 | Tiki, One Mount |
| "Describe a production incident" | #6 | VNPay, NAB |
| "What's your biggest failure?" | #6 (variant) | All companies |
| "Why are you leaving your current job?" | Special | All companies |

---

## Story #1: Race Condition Fix in C++ Game Server 🐛

### Situation
Tại Gihot Studio, đang phát triển game MOBA/MMO "Đấu Trường Huyền Thoại" bằng C++17. Trong giai đoạn QA testing, team phát hiện 1 bug nghiêm trọng: **player state bị corrupt ngẫu nhiên** — đôi khi HP/mana hiển thị sai, hoặc 2 players cùng nhận reward cho 1 kill.

### Task
Tôi được assign điều tra và fix bug này. Bug chỉ xảy ra dưới load cao (>1000 concurrent sessions), không reproduce được trong local testing.

### Action
1. **Systematic debugging:** Thêm detailed logging với thread ID + timestamp vào toàn bộ state update flow
2. **Root cause analysis:** Phát hiện race condition — 2 threads cùng modify player state object mà không có proper synchronization
3. **Solution design:** Implement fine-grained mutex locks per room (thay vì global lock) + sử dụng atomic operations cho numeric counters (HP, mana, kill count)
4. **Verification:** Viết stress test simulate 2000+ concurrent sessions, chạy liên tục 24 giờ

### Result
- **Eliminated 100% race condition bugs** — zero state corruption sau fix
- **Performance maintained:** Fine-grained locking chỉ tăng latency 2-3ms (negligible)
- **Knowledge sharing:** Viết internal doc về thread-safety patterns cho team

### 🇬🇧 English Version (Practice Speaking)
> "At Gihot Studio, while developing a MOBA game backend in C++17, we discovered a critical bug where player states were randomly getting corrupted under high load. I was assigned to investigate. Through systematic logging with thread IDs, I identified a race condition where two threads were modifying the same player state simultaneously without proper synchronization. I implemented fine-grained mutex locks per game room and used atomic operations for numeric counters. After stress testing with 2,000+ concurrent sessions for 24 hours, we confirmed zero state corruption. The fix had negligible performance impact — only 2-3ms additional latency."

---

## Story #2: Redis Caching — 30% DB Load Reduction 🚀

### Situation
Game server tại Gihot đang gặp high latency spikes trong peak hours (8-10 PM). MySQL database CPU consistently >80%. API response time trung bình 200ms, peak 500ms+.

### Task
Optimize backend performance để đảm bảo smooth gameplay experience cho 5,000+ concurrent users.

### Action
1. **Profiling:** Dùng MySQL slow query log + application profiling → phát hiện 70% queries là read operations lặp lại (player profile, game config, leaderboard)
2. **Cache strategy:** Implement multi-layer Redis caching:
   - **Player session:** Redis hash, TTL 30 min
   - **Game config:** Redis string, TTL 1 hour (write-through on admin update)
   - **Leaderboard:** Redis sorted set, real-time update
   - **Rate limiting:** Redis INCR + EXPIRE pattern
3. **Cache invalidation:** Event-driven invalidation qua RabbitMQ khi data thay đổi
4. **Monitoring:** Set up Redis metrics dashboard

### Result
- **DB load giảm ~30%** (CPU từ 80% → 55%)
- **API response time giảm ~20%** (200ms → 160ms average)
- **Leaderboard queries:** từ 50ms (MySQL) → <5ms (Redis sorted set)
- **Zero cache-DB inconsistency** nhờ event-driven invalidation

### 🇬🇧 English Version
> "At Gihot, our MySQL database was hitting 80% CPU during peak hours, causing latency spikes for 5,000 concurrent users. I profiled the system and found that 70% of queries were repetitive reads. I implemented a multi-layer Redis caching strategy: hash for sessions, strings for config with write-through, sorted sets for leaderboards, and INCR/EXPIRE for rate limiting. I used RabbitMQ event-driven invalidation to ensure consistency. This reduced DB load by 30% and API response time by 20%."

---

## Story #3: Dual-Broker Architecture Decision (FPM Project) 🏗️

### Situation
Khi thiết kế FPM (Financial Portfolio Manager) — 10-service microservices system — cần chọn messaging solution. Có 2 loại communication patterns khác nhau: high-throughput financial transaction streaming VÀ domain event routing (wallet created, balance changed, budget alerts).

### Task
Thiết kế messaging architecture đáp ứng cả 2 patterns mà không compromise performance hay reliability.

### Action
1. **Requirements analysis:**
   - Transaction streaming: high throughput, ordering guarantee, replay capability
   - Domain events: routing flexibility, acknowledgment, dead letter queue
2. **Research & POC:** Test Kafka-only vs RabbitMQ-only vs hybrid approach
3. **Decision: Dual-broker architecture:**
   - **Kafka (8 topics):** Transaction streaming, analytics events — cần ordering + replay
   - **RabbitMQ:** Domain event routing (wallet.created, balance.changed, budget.alerts) — cần flexible routing + DLQ
4. **Implementation:** Anti-Corruption Layer pattern tách messaging logic khỏi business logic

### Result
- **Clear separation of concerns:** Mỗi broker optimize cho use case phù hợp
- **Transaction processing:** Kafka xử lý 10K+ events/sec trong load test
- **Domain events:** RabbitMQ routing linh hoạt, DLQ handle failures gracefully
- **Developer experience:** Shared fpm-messaging library abstract hóa broker details

### 🇬🇧 English Version
> "For my FPM microservices project, I faced a design decision: choosing a messaging solution for two distinct patterns — high-throughput transaction streaming and domain event routing. After researching and doing a proof of concept, I chose a dual-broker architecture: Kafka for transaction streaming requiring ordering and replay, and RabbitMQ for domain events requiring flexible routing and dead letter queues. I implemented an Anti-Corruption Layer to abstract broker details. This achieved clear separation of concerns, with Kafka handling 10K+ events per second in load tests."

---

## Story #4: Cross-Team Collaboration in Agile Sprint 🤝

### Situation
Tại Gihot, sprint planning cho game "Siêu Anh Hùng Đại Chiến". Team 20+ người (Backend, Client, QA, Art, Game Design). Client team implement feature mới (real-time battle replay) nhưng backend API chưa sẵn sàng → blocked.

### Task
Coordinate giữa Backend và Client team để unblock development, đảm bảo deliver đúng sprint deadline.

### Action
1. **Communication:** Tổ chức daily sync 15 phút giữa Backend + Client team (ngoài stand-up chung)
2. **API-first approach:** Viết Protobuf schema + mock server trước → Client team develop song song
3. **Contract testing:** Agree on API contract, viết test cases verify cả 2 sides
4. **Incremental delivery:** Chia feature thành 3 milestones nhỏ, deliver từng phần
5. **Documentation:** Viết clear API documentation cho mỗi endpoint

### Result
- **Unblocked Client team trong 2 ngày** (thay vì đợi full backend 2 tuần)
- **Feature delivered đúng sprint deadline**
- **Reduced integration bugs by 60%** nhờ contract testing
- **Process adopted by other squads** trong company

### 🇬🇧 English Version
> "During a sprint at Gihot, the client team was blocked waiting for my backend API for a battle replay feature. I organized daily 15-minute sync meetings and took an API-first approach — writing Protobuf schemas and mock servers so the client team could develop in parallel. We agreed on API contracts with test cases for both sides and broke the feature into three incremental milestones. This unblocked the client team within 2 days, the feature shipped on time, and integration bugs dropped by 60%. Other squads adopted this approach."

---

## Story #5: Learning C++17 for Production in 3 Months 📚

### Situation
Khi join Gihot Studio (Apr 2024), background chính là Java/Spring Boot (từ Hahalolo). Company yêu cầu phát triển game server bằng C++17 — 1 ngôn ngữ tôi chỉ biết cơ bản từ đại học.

### Task
Nắm vững C++17 đủ để develop production game server trong vòng 3 tháng.

### Action
1. **Structured learning plan:**
   - Tuần 1-2: Modern C++ features (smart pointers, move semantics, constexpr, structured bindings)
   - Tuần 3-4: STL deep dive (containers, algorithms, iterators)
   - Tuần 5-6: Concurrency (std::thread, mutex, condition_variable, atomic)
   - Tuần 7-8: Memory management, RAII, profiling tools
2. **Learn by doing:** Start với small features, review bởi senior C++ devs
3. **Reading:** "Effective Modern C++" (Scott Meyers), CppReference
4. **Practice:** LeetCode bằng C++ 30 phút/ngày

### Result
- **3 tháng:** Independently develop production game logic (Siêu Anh Hùng Đại Chiến)
- **6 tháng:** Lead C++17 backend development cho MOBA project mới
- **Dual expertise:** C++ + Java → unique competitive advantage
- **Mentoring:** Hướng dẫn 1 junior dev học C++ cùng approach

### 🇬🇧 English Version
> "When I joined Gihot, I needed to transition from Java to C++17 for game server development. I created a structured 8-week learning plan covering modern C++ features, STL, concurrency, and memory management. I practiced with small production tasks reviewed by senior devs and solved LeetCode problems in C++ daily. Within 3 months, I was independently developing production game logic, and by 6 months, I was leading the C++17 backend for a new MOBA project. This dual C++/Java expertise became a unique competitive advantage."

---

## Story #6: Production Incident Debugging at Hahalolo 🔥

### Situation
Tại Hahalolo (social travel platform), 1 đêm thứ 6 (peak traffic), hệ thống bất ngờ response chậm 5-10x. Users báo lỗi timeout khi upload ảnh và load feed. Monitoring shows MySQL connections maxed out.

### Task
Diagnose và fix production incident trong thời gian nhanh nhất có thể (SLA: restore trong 2 giờ).

### Action
1. **Immediate:** Enable MySQL slow query log, check connection pool status
2. **Discovery:** Phát hiện 1 API endpoint (image processing) giữ DB connection quá lâu → connection pool exhaustion
3. **Root cause:** JPA N+1 query problem trong image gallery feature — 1 request trigger 50+ queries
4. **Hotfix:**
   - Short-term: Tăng connection pool size + set query timeout
   - Long-term: Refactor query với `@EntityGraph` + batch fetching
5. **Prevention:** Add connection pool monitoring alerts, query performance test trong CI

### Result
- **Service restored trong 45 phút** (dưới SLA 2 giờ)
- **Query execution time giảm 40%** sau refactor
- **Added automated alerts** — phát hiện sớm trước khi users bị ảnh hưởng
- **Postmortem document** → team học được lesson về N+1 prevention

### 🇬🇧 English Version
> "At Hahalolo, during a Friday peak, our social platform experienced 5-10x slower response times. I investigated and found the MySQL connection pool was exhausted. The root cause was an N+1 query problem in an image gallery API — each request triggered 50+ queries, holding connections too long. I applied a hotfix by increasing the pool size and setting query timeouts, then refactored the queries using EntityGraph and batch fetching. Service was restored in 45 minutes. The long-term fix reduced query time by 40%, and I set up automated monitoring alerts to catch similar issues early."

---

## 🎯 Special Question: "Why Are You Leaving?"

### Vietnamese Version
> "Tôi rất trân trọng thời gian ở Gihot — nơi tôi phát triển mạnh kỹ năng C++, real-time systems, và game server architecture. Tuy nhiên, sau 2 năm, tôi muốn tìm kiếm thử thách mới ở môi trường product company, nơi tôi có thể áp dụng kinh nghiệm high-concurrency và distributed systems vào scale lớn hơn — serving millions of users. Tôi cũng muốn phát triển thêm về cloud-native architecture và contribute vào products có impact trực tiếp đến người dùng cuối."

### English Version
> "I truly value my time at Gihot where I developed strong skills in C++, real-time systems, and game server architecture. However, after two years, I'm seeking new challenges at a product company where I can apply my high-concurrency and distributed systems experience at a larger scale — serving millions of users. I'm also eager to grow in cloud-native architecture and contribute to products that have a direct impact on end users."

---

## 📝 Practice Schedule

| Tuần | Practice | Method |
|:-----|:---------|:-------|
| Tuần 6 | Write all 6 stories | Viết ra giấy/file |
| Tuần 7 | Practice speaking VN | Ghi âm, nghe lại, sửa |
| Tuần 8 | Practice speaking EN | Ghi âm, so sánh với script |
| Tuần 9 | Mock with partner | Nhờ bạn/đồng nghiệp hỏi random |
| Tuần 10+ | Real interviews | Apply và học từ feedback |

### Tips
- ⏱️ Mỗi story nên **2-3 phút** khi nói
- 📊 **Luôn có con số:** "giảm 30%", "45 phút", "5000+ users"
- 🎯 **Focus on YOUR contribution** — dùng "I" không phải "We"
- 😊 **Show enthusiasm** khi kể về technical challenges
- ❌ **KHÔNG nói xấu** công ty cũ khi trả lời "Why leaving?"
