# 🎯 Định vị: Junior Backend Game → Middle Backend Developer (công ty lớn)

> **Bối cảnh:** đang là Backend Game (C++/Java), biết build web nhưng chưa có dự án web thực chiến trong công ty.
> **Mục tiêu:** offer Middle Backend tại công ty lớn (VNG, MoMo, Zalo, fintech, outsourcing quốc tế) — phỏng vấn tiếng Anh — đầu năm 2027.
> **File này trả lời:** (1) "Middle" khác "Junior" ở đâu, (2) kể kinh nghiệm game thế nào để thành điểm mạnh, (3) lấp khoảng trống "chưa làm web thực chiến", (4) kiểm tra CV để không bị bắt bẻ.

---

## 1. Interviewer chấm "Middle" bằng gì?

Cùng một câu hỏi, khác biệt nằm ở **độ sâu** câu trả lời:

| Câu hỏi | Câu trả lời Junior | Câu trả lời Middle |
|---|---|---|
| "What is `@Transactional`?" | Định nghĩa: rollback khi có lỗi | + propagation, mặc định rollback với `RuntimeException` và `Error` (checked exception vẫn commit, trừ khi khai báo `rollbackFor`), **self-invocation không hoạt động vì proxy**, ví dụ bug thật |
| "How does HashMap work?" | Key-value, hash | + bucket, collision → linked list → tree (Java 8, ngưỡng 8), resize, vì sao không thread-safe, khi nào dùng `ConcurrentHashMap` |
| "How do you use Redis?" | Làm cache | + cache-aside vs write-through, TTL, **cache stampede / penetration**, invalidation, khi nào KHÔNG cache |
| "Design a leaderboard" | Bảng MySQL + `ORDER BY` | + Redis Sorted Set, O(log n), xử lý top-N vs rank của 1 user, sync về DB, sharding khi > 1 instance |
| "Tell me about a bug" | "Có bug, tôi sửa" | Cách **khoanh vùng** (log, metric, reproduce), root cause, fix, **cách ngăn lặp lại** (test, alert) |

**Công thức Middle:** *Đúng định nghĩa* + *cơ chế bên trong* + *trade-off* + *một lần dùng thật* + *khi nào sai/không dùng*.

---

## 2. Biến kinh nghiệm game thành lợi thế

Đừng nói "tôi chỉ làm game". Hãy **dịch** sang ngôn ngữ backend chung:

| Bạn đã làm (game) | Interviewer web/fintech nghe thấy | Câu nói được (EN) |
|---|---|---|
| Room/player state nhiều thread cùng sửa | Concurrency, race condition, locking | "I handled race conditions when many players update the same room at the same time." |
| Matchmaking engine | Queue processing, fairness, latency SLA | "I built a matchmaking queue that groups players by rank within a time limit." |
| Leaderboard trên Redis | Sorted Set, ranking at scale, cache | "I used Redis sorted sets for leaderboards, so rank lookup is O(log n)." |
| Phát thưởng/mission phải đúng | Idempotency, consistency, giống giao dịch tiền | "Rewards are like money: they must never be given twice, so the reward API is idempotent." |
| gRPC + Protobuf giữa các server | Service-to-service communication, schema evolution | "Our services talk through gRPC. I learned how to change Protobuf schemas without breaking old clients." |
| Stress test nhiều user ảo | Load testing, bottleneck analysis | "I ran load tests, found the bottleneck with profiling, and fixed it." |
| Debug lỗi sync khó | Systematic debugging, root cause analysis | "I debug with logs and metrics first, then reproduce the bug, then fix the root cause." |
| GMTool bằng Spring Boot | **Web backend thật trong công ty** (REST, auth, DB) | "I built Spring Boot services for our internal admin tool, used by the operation team every day." |

> 💡 **GMTool chính là kinh nghiệm web trong công ty của bạn.** Nó có người dùng thật (team vận hành), có REST API, auth, database. Nói về nó như một hệ thống thật — vì nó là thật.

---

## 3. Lấp khoảng trống "chưa làm web thực chiến" (trước 01/2027)

Công ty lớn sẽ hỏi những thứ hệ thống web production có mà game server ít gặp. Làm **FPM** (dự án cá nhân) chứa đủ các điểm sau để có câu chuyện thật:

| Chủ đề hay bị hỏi | Việc cần làm trong FPM | Câu chuyện để kể |
|---|---|---|
| Pagination, filter, validation | API danh sách giao dịch có phân trang cursor-based | Vì sao offset chậm với bảng lớn |
| Transaction + locking | Chuyển tiền giữa 2 ví, chạy 100 request song song | Optimistic vs pessimistic lock, test chứng minh không âm tiền |
| Idempotency | Header `Idempotency-Key` cho API tạo giao dịch | Client retry không tạo 2 giao dịch |
| Kafka event | Event `TransactionCreated` → service thông báo | At-least-once + consumer idempotent |
| Cache | Cache số dư/thống kê bằng Redis + invalidation | Lỗi cache stale và cách xử lý |
| Testing | Unit test (JUnit, Mockito) + 1 integration test với Testcontainers | "How do you test?" — câu Middle luôn bị hỏi |
| Observability | Actuator + Prometheus + 1 dashboard Grafana | "How do you know your service is healthy?" |
| Deploy | Docker Compose chạy toàn bộ; README có sơ đồ kiến trúc | Đưa link GitHub vào CV |

**Quy tắc:** làm ít mà đo được > làm nhiều mà không giải thích được. Mỗi mục phải trả lời được câu *"Why did you choose this?"*.

---

## 4. Kiểm tra CV: mỗi dòng phải "bảo vệ được"

Công ty lớn hỏi xoáy **từng con số và từng công nghệ** trên CV. Trước khi nộp, với mỗi bullet, tự trả lời 4 câu (bằng tiếng Anh, nói to):

1. **What exactly did *you* do?** (không phải team)
2. **How did you measure it?** (con số ~30%, ~40%, 10,000 users đo bằng công cụ gì, so với baseline nào?)
3. **What was the hardest part?**
4. **What would you do differently now?**

Bảng tự rà (điền vào):

| Dòng trên CV | Trả lời được 4 câu? | Hành động |
|---|---|---|
| Gihot — matchmaking, room state, thread-safe | ⬜ | |
| Gihot — Redis giảm DB load ~30% | ⬜ | Nhớ: đo bằng gì, trước/sau bao nhiêu |
| Gihot — stress test 10,000+ virtual users | ⬜ | Công cụ gì? Bottleneck cụ thể là gì? |
| Gihot — Spring Boot GM Tool | ⬜ | |
| Hahalolo — REST API, Spring Security + JWT | ⬜ | |
| Hahalolo — giảm query time ~40% | ⬜ | Query nào? Fix bằng index hay fetch strategy? |
| FPM — số service, Kafka, gRPC | ⬜ | Chỉ ghi những gì đã chạy được |

> ⚠️ **Quan trọng:** Nếu một dòng không trả lời được, **sửa CV cho đúng mức thật** (ví dụ ghi rõ "internship", "contributed to", "personal project") thay vì cố học thuộc câu trả lời. Ở công ty lớn, một chi tiết phóng đại bị phát hiện ở vòng kỹ thuật thường làm hỏng cả buổi phỏng vấn — và có thể bị phát hiện ở bước kiểm tra tham chiếu.
>
> Kiểm tra thêm sự nhất quán giữa các file: CV ghi **C++17**, một số bài mẫu ghi **C++11** — thống nhất 1 cách theo đúng thực tế.

---

## 5. Danh sách chủ đề tối thiểu cho Middle (đánh dấu khi nói được bằng tiếng Anh 2 phút)

**Java Core** → `02-Java-Core/`, `12-Mock-Interview-English/03-core-java.md`
- [ ] HashMap internals, `equals`/`hashCode` contract
- [ ] `ConcurrentHashMap`, `synchronized` vs `ReentrantLock`, `volatile`
- [ ] Thread pool (`ExecutorService`) — cấu hình core/max/queue và vì sao
- [ ] JVM memory (heap/stack/metaspace), GC cơ bản (G1), cách đọc OOM
- [ ] Java 8+: Stream, Optional, `CompletableFuture`; Java 21 virtual threads (biết khái niệm)

**Spring** → `03-Spring-Ecosystem/`
- [ ] IoC/DI, bean scope, bean lifecycle
- [ ] `@Transactional`: propagation, isolation, rollback rules, self-invocation
- [ ] Spring Security filter chain + JWT flow
- [ ] JPA: N+1 và cách sửa (fetch join, `@EntityGraph`), lazy loading exception

**Database** → `04-Database/`
- [ ] Index B-tree, composite index (leftmost prefix), `EXPLAIN`
- [ ] Isolation levels + dirty/non-repeatable/phantom read
- [ ] Optimistic vs pessimistic locking (ví dụ chuyển tiền)
- [ ] Khi nào dùng NoSQL/Redis thay vì SQL

**Distributed / System design** → `05-System-Design/`, `06-Distributed-Systems/`
- [ ] Cache-aside, cache stampede, TTL, invalidation
- [ ] Kafka: partition, consumer group, offset, at-least-once + idempotent consumer
- [ ] Rate limiter (token bucket), idempotency key
- [ ] 3 bài design: URL shortener, leaderboard, notification system (nói được trong 20 phút)

**Coding** → `01-DSA/`
- [ ] 60–80 bài LeetCode Easy/Medium theo pattern (two pointers, sliding window, hash map, BFS/DFS, heap)
- [ ] Vừa code vừa **nói tiếng Anh** suy nghĩ của mình (Survival Kit, Phần 2)

**Behavioral** → `10-Interview-Prep/star-stories.md`
- [ ] 4 câu chuyện STAR (Survival Kit, Phần 9)

---

## 6. Chiến lược nộp đơn

1. **Phỏng vấn "khởi động" trước** (giữa 12/2026): nộp 2–3 công ty bạn ít muốn vào nhất để quen áp lực phỏng vấn tiếng Anh. Mỗi buổi thật = 10 buổi tự luyện.
2. **Đợt chính** (đầu 01/2027): nộp nhóm mục tiêu. Nhiều công ty mở headcount mới đầu năm.
3. **Thời điểm nghỉ việc:** cân nhắc thưởng tháng 13/Tết ở công ty hiện tại — có thể thương lượng ngày bắt đầu sau Tết. Kiểm tra thời gian báo trước trong hợp đồng.
4. **Mỗi lần trượt:** ghi lại câu hỏi bị hỏi và câu trả lời yếu vào `Plan/weekly-tracker.md` → luyện lại trong tuần.
