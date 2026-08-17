# 🎯 KAI ASIA — 7-Day Interview Preparation Guide
### Vị trí: Java Backend Developer (Banking/Fintech) · Onsite Hàm Nghi, Q1, HCM
### Ứng viên: Đặng Trọng Phúc · Ngày bắt đầu ôn: _____/____/2026

> **Thời lượng học:** Tối đa 2–3 tiếng/ngày (vì vẫn đi làm)
> **Phương châm:** *"Không học rộng, học đúng trọng tâm JD yêu cầu."*

---

## I. SO SÁNH JD vs CV

### (a) 🟢 Điểm Khớp Mạnh — Nên Khai Thác Khi Trả Lời

| # | JD yêu cầu | CV có gì | Cách khai thác |
|---|-----------|---------|---------------|
| 1 | Java, Spring Boot, Microservices | 2.5+ năm Java/Spring Boot, Spring Cloud (Gateway, Eureka, Config Server) | **Lead với FPM**: 10-service microservices với đầy đủ Spring Cloud stack |
| 2 | Kafka, RabbitMQ (Message Queue) | Kafka (8 topics FPM), RabbitMQ (domain events, DLQ, retry with backoff) | **Kể chuyện Dual-Broker Architecture** — phân tích trade-off Kafka vs RabbitMQ |
| 3 | RESTful API, gRPC | REST APIs (cả 3 dự án), gRPC (FPM inter-service) | Nêu lý do chọn gRPC cho internal service (low-latency, schema-first) |
| 4 | High concurrency | Game server C++/Java: 5,000–10,000 CCU, race condition fix | **Vũ khí bí mật**: Kinh nghiệm xử lý lock/concurrency ở game → tương đồng banking |
| 5 | Redis caching | Redis multi-layer caching (session, config, leaderboard, rate limiting) | Giảm 30% DB load — con số cụ thể rất ghi điểm |
| 6 | Circuit Breaker, Resilience | Resilience4j Circuit Breaker, Rate Limiting (Token Bucket) | Kể cách áp dụng Circuit Breaker khi gọi external APIs |
| 7 | Docker, CI/CD | Docker, GitHub Actions CI/CD | Nhắc DevOps mindset: containerize toàn bộ microservices |
| 8 | MySQL/PostgreSQL | MySQL (Gihot), PostgreSQL (FPM, Hahalolo) | Query optimization, indexing, EXPLAIN ANALYZE kinh nghiệm thực tế |

### (b) 🔴 Khoảng Trống (Gaps) — Kỹ Năng JD Yêu Cầu Nhưng CV Thiếu

| # | Gap | Mức độ nghiêm trọng | Chiến lược bù đắp |
|---|-----|---------------------|-------------------|
| 1 | **Core Banking / Payment Gateway integration** (ISO 8583, NAPAS, Reconciliation) | 🔴 Cao | Ngày 4: Học về luồng thanh toán, giao thức ISO 8583, cơ chế đối soát |
| 2 | **Oracle Database** (JD yêu cầu Oracle/MySQL/PostgreSQL) | 🟡 Trung bình | Ngày 5: Học khác biệt Oracle vs Postgres, PL/SQL cơ bản, Partitioning |
| 3 | **Banking Security & Compliance** (Data Encryption, Digital Signatures, Data Masking) | 🔴 Cao | Ngày 5: Học java.security API, HMAC/RSA signing, Logback masking |
| 4 | **Distributed Transactions** (Saga Pattern trong ngữ cảnh giao dịch tiền) | 🟡 Trung bình | Ngày 4: Saga Orchestrator vs Choreography, Idempotency |
| 5 | **Kinh nghiệm thực tế tại môi trường tài chính/ngân hàng** | 🟡 Trung bình | Luôn **kết nối** kinh nghiệm Game Server concurrency → Banking concurrency |

### (c) 🔍 Con Số/Thành Tích Trong CV Có Khả Năng Bị Hỏi Xoáy Sâu

| # | Con số trong CV | Câu hỏi xoáy có thể gặp |
|---|----------------|--------------------------|
| 1 | **"Giảm 30% tải DB"** bằng Redis caching | *"30% đo bằng gì? Metric nào? Trước/sau bao nhiêu? Tool gì đo?"* |
| 2 | **"5,000–10,000 CCU"** game server | *"Benchmark thế nào? Bottleneck ở đâu? Horizontal scale ra sao?"* |
| 3 | **"10 microservices"** trong FPM | *"10 service là gì? Tại sao tách vậy? Có over-engineering không? Boundary ra sao?"* |
| 4 | **"Giảm 20% response time"** | *"Từ bao nhiêu xuống bao nhiêu ms? Percentile nào (p50, p95, p99)?"* |
| 5 | **"Giảm 40% query time"** (Hahalolo N+1 fix) | *"N+1 là gì? EntityGraph hoạt động ra sao? Tại sao không dùng native query?"* |
| 6 | **"Kafka xử lý 10K+ events/sec"** | *"Bao nhiêu partition? Consumer group config thế nào? Ordering guarantee?"* |
| 7 | **"Restored in 45 minutes"** (production incident) | *"Quy trình incident response? Ai tham gia? Postmortem viết gì?"* |

---

## II. LỘ TRÌNH ÔN TẬP 7 NGÀY

```
Ngày 1 ──── Ngày 2 ──── Ngày 3 ──── Ngày 4 ──── Ngày 5 ──── Ngày 6 ──── Ngày 7
TỔNG QUAN   JAVA CORE   SPRING &    BANKING     LẤP GAP     MOCK        TỔNG ÔN
Self-Intro  & OOP       MICROSVCS   DOMAIN      Security    INTERVIEW   & RELAX
Career      Collections Architecture Integration  Oracle DB   Full Sim    Q&A ngược
Story       Concurrency  Kafka/MQ    Saga/Idemp  Logging     Feedback    Mindset
```

---

## NGÀY 1: TỔNG QUAN — Self-Introduction & Career Story
**⏰ Thời lượng: 2.5 tiếng**

### Chủ đề chính
1. **Giới thiệu bản thân (Self-Introduction)** — 2-3 phút, nêu bật điểm khác biệt
2. **Career Story** — Hành trình Hahalolo → Gihot → Tại sao Banking?
3. **Why this company?** — Nghiên cứu về KAI ASIA và dự án banking
4. **Chuẩn bị STAR Stories** — Đọc lại và tập nói 6 stories đã viết

### 📝 5 Câu hỏi phỏng vấn mẫu

**Q1 (Dễ):** *"Giới thiệu về bản thân em đi."*
> **Gợi ý:** Mở bằng tên + số năm kinh nghiệm + tech stack → highlight 1 thành tích nổi bật nhất (5000–10000 CCU game server) → kết bằng lý do muốn chuyển sang banking. **Không quá 2.5 phút.**

**Q2 (Trung bình):** *"Tại sao em muốn chuyển từ Game sang Banking?"*
> **Gợi ý:** *"Em muốn áp dụng kinh nghiệm concurrency và distributed system ở scale lớn hơn, nơi mỗi giao dịch đều có ý nghĩa quan trọng với người dùng. Ngân hàng là môi trường đòi hỏi độ chính xác tuyệt đối — điều đó phù hợp với mindset mà em đã rèn luyện khi xử lý real-time state trong game server."*

**Q3 (Trung bình):** *"Em biết gì về dự án mà em sẽ tham gia?"*
> **Gợi ý:** Nghiên cứu trước về KAI ASIA + ngân hàng đối tác. Nêu rằng bạn hiểu đây là dự án tích hợp Core Banking/Payment Gateway, và bạn hào hứng với bài toán high-availability, transaction integrity.

**Q4 (Khó):** *"Điểm yếu lớn nhất của em là gì?"*
> **Gợi ý:** *"Em chưa có kinh nghiệm trực tiếp với Core Banking integration và Oracle DB. Tuy nhiên em đã chủ động tìm hiểu về ISO 8583, Saga Pattern, và đang thực hành Oracle trên Docker. Với nền tảng xử lý concurrency cao và kiến trúc microservices, em tin mình sẽ ramp-up nhanh."*

**Q5 (Đào sâu):** *"Nếu em có 30 ngày đầu tiên ở đây, em sẽ làm gì?"*
> **Gợi ý:** Tuần 1: Hiểu domain, đọc tài liệu kiến trúc, setup local env → Tuần 2: Pair programming với senior, hiểu codebase → Tuần 3: Pick up task nhỏ, viết tests → Tuần 4: Deliver task độc lập, viết documentation.

### 💡 Mẹo ghi nhớ: Công thức Self-Introduction "PITCH"

```
P — Position (Vị trí hiện tại + kinh nghiệm)
I — Impact (Thành tích nổi bật nhất, có con số)
T — Technology (Tech stack chính)
C — Connection (Liên kết kinh nghiệm cũ → vị trí mới)
H — Hope (Kỳ vọng ở vị trí mới)
```

> **Script mẫu:**
> *"Hi, my name is Phuc. I have about 2.5 years of experience as a Backend Developer, currently working at Gihot Studio building real-time game servers in C++ and Java handling up to 10,000 concurrent users. My core stack is Java 21, Spring Boot, and I've built a 10-service microservices system using Kafka and RabbitMQ for a personal fintech project called FPM. I'm now eager to apply my concurrency and distributed systems expertise to the banking domain where precision and reliability are paramount."*

---

## NGÀY 2: JAVA CORE & OOP — Nền Tảng Vững Chắc
**⏰ Thời lượng: 2.5 tiếng**

### Chủ đề chính
1. **JVM Architecture** — ClassLoader, Memory Model, GC (G1, ZGC)
2. **OOP & SOLID** — Polymorphism, Inheritance, Encapsulation, Abstraction
3. **Collections Framework** — HashMap internals, ConcurrentHashMap, TreeMap
4. **Concurrency** — synchronized, volatile, ReentrantLock, AtomicInteger, ThreadPool
5. **Java 17–21 features** — Records, Sealed Classes, Virtual Threads, Pattern Matching

### 📝 5 Câu hỏi phỏng vấn mẫu

**Q1 (Dễ):** *"HashMap hoạt động nội bộ như thế nào?"*
> **Gợi ý:** Hash function → bucket index → Node (key, value, hash, next) → nếu collision thì dùng linked list, khi linked list > 8 nodes thì treeify thành Red-Black Tree (Java 8+). Load factor 0.75, resize double capacity.

**Q2 (Trung bình):** *"Sự khác nhau giữa `synchronized` và `ReentrantLock`?"*
> **Gợi ý:** ReentrantLock linh hoạt hơn: tryLock() với timeout, fairness policy, multiple Conditions. synchronized đơn giản, tự unlock khi ra khỏi block. Trong banking thường dùng ReentrantLock vì cần tryLock timeout để tránh deadlock.

**Q3 (Trung bình):** *"Giải thích nguyên lý SOLID với ví dụ thực tế từ dự án?"*
> **Gợi ý:** Lấy ví dụ từ FPM: Single Responsibility — tách WalletService (xử lý nghiệp vụ ví) khỏi WalletNotificationService (gửi thông báo). Open/Closed — Payment Strategy pattern cho nhiều loại thanh toán (QR, card, transfer) mà không sửa code cũ.

**Q4 (Khó):** *"Virtual Threads (Java 21) khác gì Platform Threads? Khi nào nên dùng?"*
> **Gợi ý:** Virtual Threads nhẹ hơn (~1KB vs ~1MB), do JVM quản lý scheduling (không phải OS). Phù hợp cho IO-bound tasks (gọi API, query DB). Trong banking: mỗi giao dịch là 1 virtual thread → triệu threads đồng thời mà không hết bộ nhớ. **Không nên dùng** cho CPU-bound tasks.

**Q5 (Đào sâu):** *"Trong CV em nói xử lý 10,000 CCU. Em dùng mechanism nào để đảm bảo thread-safety? Tại sao không dùng global lock?"*
> **Gợi ý:** Global lock = bottleneck khủng khiếp. Em dùng **fine-grained locking per game room** (mỗi room 1 mutex) + **AtomicInteger** cho các counter số (HP, mana, kill count). Tổng latency chỉ tăng 2-3ms. Tương tự trong banking: lock per account ID, không lock toàn bộ bảng.

### 💡 Mẹo ghi nhớ: HashMap Collision Chain

```
Bucket 5: [ Alice:100 ] → [ Bob:200 ] → [ Charlie:300 ]
                                            ↓ (> 8 nodes)
Bucket 5: RedBlackTree { Alice, Bob, Charlie, ... }
```
> Nhớ: **"8 Nodes = Treeify, 6 Nodes = Untreeify"**

---

## NGÀY 3: SPRING BOOT & MICROSERVICES ARCHITECTURE
**⏰ Thời lượng: 3 tiếng**

### Chủ đề chính
1. **Spring Boot Internals** — Auto-configuration, Starter POMs, Actuator
2. **Spring Cloud** — Gateway, Eureka, Config Server, OpenFeign
3. **Kafka Deep Dive** — Partitions, Consumer Groups, Ordering, Exactly-once semantics
4. **RabbitMQ** — Exchange types, DLQ, Retry with backoff
5. **API Design** — REST maturity levels, gRPC vs REST trade-offs

### 📝 5 Câu hỏi phỏng vấn mẫu

**Q1 (Dễ):** *"Spring Boot Auto-configuration hoạt động như thế nào?"*
> **Gợi ý:** `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`. Spring Boot đọc `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`, kiểm tra `@Conditional` annotations, và tự động cấu hình các beans dựa trên classpath dependencies.

**Q2 (Trung bình):** *"Trong FPM, tại sao em chọn Kafka cho transaction streaming và RabbitMQ cho domain events? Tại sao không dùng 1 cái cho cả 2?"*
> **Gợi ý chuẩn bị (STAR Story #3):**
> - **Kafka strengths**: Ordering guarantee per partition, replay capability (offset reset), high throughput (10K+ events/sec), log retention cho audit
> - **RabbitMQ strengths**: Flexible routing (topic/fanout exchange), built-in DLQ, acknowledgment per message, low latency cho real-time events
> - **Kết luận**: *"Right tool for the right job"* — không phải lúc nào 1 size fits all

**Q3 (Trung bình):** *"Kafka consumer bị lag, em xử lý thế nào?"*
> **Gợi ý:** Tăng partition count + tăng consumer instances trong consumer group (horizontal scale). Kiểm tra max.poll.records, max.poll.interval.ms. Nếu processing chậm: batch processing, async processing. Nếu vẫn không kịp: thêm Retry Topic + DLQ để tách bản tin lỗi ra khỏi main flow.

**Q4 (Khó):** *"Làm sao đảm bảo ordering trong Kafka khi có retry?"*
> **Gợi ý:** Ordering chỉ guarantee **per partition**. Dùng cùng partition key (ví dụ: `account_id`) để mọi giao dịch của 1 tài khoản đi vào cùng partition. Khi retry: gửi vào cùng partition key trong retry topic. `max.in.flight.requests.per.connection=1` + `enable.idempotence=true` cho producer.

**Q5 (Đào sâu — Phản biện):** *"10 microservices cho 1 dự án cá nhân? Có phải over-engineering không? Monolith có đủ không?"*
> **Gợi ý:** *"Hoàn toàn đúng, cho production với team nhỏ thì monolith modular là đủ. Em xây FPM 10 services với mục đích **học và thực hành** kiến trúc microservices. Mỗi service là 1 bounded context rõ ràng (Wallet, Transaction, Budget, Report, Auth, Notification...). Trong thực tế, em sẽ bắt đầu bằng modular monolith rồi tách dần khi cần scale."*

### 💡 Mẹo ghi nhớ: Kafka vs RabbitMQ — "KO vs RFD"

```
Kafka:  K-Ordering, O-Offset replay
Rabbit: R-Routing flexible, F-Fanout/Topic exchange, D-DLQ built-in
```

---

## NGÀY 4: BANKING DOMAIN — Integration & Transaction Management
**⏰ Thời lượng: 3 tiếng (ngày quan trọng nhất)**

### Chủ đề chính
1. **Core Banking Integration** — SOAP/XML, REST/JSON, ISO 8583 (jPOS)
2. **Payment Gateway** — Luồng thanh toán NAPAS, Auth & Capture, Redirect flow
3. **Saga Pattern** — Orchestrator vs Choreography cho distributed transactions
4. **Idempotency** — Chống trùng giao dịch bằng Redis Lock + DB State
5. **Reconciliation** — Cơ chế đối soát cuối ngày

### 📝 5 Câu hỏi phỏng vấn mẫu

**Q1 (Dễ):** *"Distributed Transaction là gì? Tại sao không dùng `@Transactional` bình thường trong microservices?"*
> **Gợi ý:** `@Transactional` chỉ hoạt động trên single datasource (1 DB). Trong microservices, 1 luồng giao dịch chuyển tiền đi qua Wallet Service → Transaction Service → Notification Service, mỗi service có DB riêng. Không thể rollback cross-database bằng `@Transactional`. Cần dùng **Saga Pattern**.

**Q2 (Trung bình):** *"Orchestrator-based Saga vs Choreography-based Saga — khác gì nhau? Banking nên dùng cái nào?"*
> **Gợi ý:**
> - **Choreography**: Services tự phát/nghe event. Đơn giản nhưng khó trace luồng khi nhiều step.
> - **Orchestrator**: 1 service trung tâm điều phối toàn bộ luồng. Dễ trace, dễ quản lý error/compensation.
> - **Banking nên dùng Orchestrator** vì: luồng giao dịch tiền cần audit trail rõ ràng, compensation logic phức tạp, cần biết chính xác step nào fail.

**Q3 (Trung bình):** *"Khách hàng bấm 'Thanh toán' 3 lần liên tiếp (mạng lag). Làm sao chống trùng?"*
> **Gợi ý 3 tầng bảo vệ:**
> 1. **Frontend**: Disable button sau click đầu tiên
> 2. **Redis Distributed Lock**: `SET lock:txn:{request_id} NX EX 30` — chặn tức thời
> 3. **DB Unique Index**: Bảng `transactions` có unique index trên `request_id` — chặn vĩnh viễn

**Q4 (Khó):** *"Service gọi API NAPAS để trừ tiền, response timeout. Em xử lý thế nào?"*
> **Gợi ý (STAR-ready):**
> - **TUYỆT ĐỐI KHÔNG** tự coi là fail để refund (ngân hàng đã trừ rồi → mất tiền)
> - **TUYỆT ĐỐI KHÔNG** tự coi là success (chưa chắc tiền đã trừ → sai dữ liệu)
> - **Đánh dấu PENDING_VERIFICATION** → Chạy worker query lại trạng thái bằng transaction ID
> - **Cuối ngày**: Reconciliation file đối soát tự động khớp các giao dịch PENDING

**Q5 (Đào sâu — Phản biện):** *"Em chưa từng làm banking thực tế. Sao em tự tin handle được?"*
> **Gợi ý:** *"Đúng là em chưa trực tiếp làm banking production. Nhưng bản chất kỹ thuật của bài toán banking — race condition trên số dư, đảm bảo exactly-once processing, xử lý timeout và retry — hoàn toàn trùng với bài toán em đã giải ở game server: tranh chấp game state giữa hàng nghìn player đồng thời, đảm bảo không mất item/gold, xử lý reconnect khi mất kết nối. Em cần thời gian học domain knowledge (luồng thanh toán, compliance), nhưng nền tảng kỹ thuật em đã sẵn sàng."*

### 💡 Mẹo ghi nhớ: Xử lý Timeout Banking — "PQRC"

```
P — PENDING: Đánh dấu giao dịch chờ xác minh
Q — QUERY:   Truy vấn lại trạng thái từ đối tác
R — RESOLVE: Cập nhật SUCCESS/FAILED dựa trên kết quả
C — CONFIRM: Reconciliation đối soát cuối ngày
```

---

## NGÀY 5: LẤP GAP — Security, Oracle DB & System Resilience
**⏰ Thời lượng: 3 tiếng**

### Chủ đề chính
1. **Banking Security** — Digital Signature (RSA/HMAC), AES-256, TLS 1.3
2. **Secure Logging** — Data Masking (Logback custom filter)
3. **Oracle DB** — Khác biệt vs MySQL/Postgres, Explain Plan, Partitioning, PL/SQL cơ bản
4. **Locking Strategies** — Pessimistic vs Optimistic trong JPA
5. **Circuit Breaker** — Resilience4j states (CLOSED, OPEN, HALF-OPEN)

### 📝 5 Câu hỏi phỏng vấn mẫu

**Q1 (Dễ):** *"Pessimistic Locking vs Optimistic Locking — khi nào dùng cái nào?"*
> **Gợi ý:**
> | | Pessimistic | Optimistic |
> |---|---|---|
> | Cơ chế | `SELECT ... FOR UPDATE` | `@Version` column |
> | Khi nào | Tranh chấp cao, cần chính xác tuyệt đối (trừ tiền) | Tranh chấp thấp, đọc nhiều ghi ít (cập nhật profile) |
> | Nhược | Có thể deadlock, throughput thấp | Phải retry khi conflict |
> | Banking | ✅ Giao dịch tiền | ✅ Cập nhật thông tin KYC |

**Q2 (Trung bình):** *"Làm sao bảo mật thông tin nhạy cảm trong log hệ thống?"*
> **Gợi ý:** Viết custom Logback `PatternLayoutEncoder` hoặc Jackson `@JsonSerialize` để tự động mask:
> - Số thẻ: `4111-XXXX-XXXX-1111`
> - Số điện thoại: `0912-XXX-XX89`
> - Email: `ph***@gmail.com`
> - **Tuyệt đối không log**: PIN, CVV, password, OTP

**Q3 (Trung bình):** *"Oracle DB khác MySQL ở điểm gì quan trọng khi xử lý giao dịch?"*
> **Gợi ý:**
> - Oracle: Multi-Version Read Consistency qua **Undo Tablespace** → Reader KHÔNG BAO GIỜ bị block bởi Writer
> - MySQL InnoDB: Cũng dùng MVCC nhưng ở isolation level SERIALIZABLE thì reader CÓ THỂ bị block
> - Oracle: Explain Plan dùng `EXPLAIN PLAN FOR ...` rồi `SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY)`
> - Oracle: Hỗ trợ Partition Pruning mạnh mẽ cho bảng lịch sử giao dịch cỡ lớn

**Q4 (Khó):** *"Hãy giải thích cách hoạt động của Circuit Breaker. Nếu Core Banking chậm, em xử lý ra sao?"*
> **Gợi ý:**
> ```
> CLOSED ──(failure rate > threshold)──► OPEN ──(wait duration)──► HALF-OPEN
>   ▲                                                                  │
>   └──────────────(success in HALF-OPEN)──────────────────────────────┘
> ```
> - CLOSED: Cho request đi qua bình thường, đếm failures
> - OPEN: Chặn toàn bộ request → trả về fallback/error ngay (fail-fast)
> - HALF-OPEN: Cho vài request thử → nếu OK thì về CLOSED, nếu fail thì về OPEN
> - **Fallback cho banking**: Trả về "Hệ thống đang bảo trì, vui lòng thử lại sau 5 phút"

**Q5 (Đào sâu — Phản biện):** *"Em nói dùng AES-256 mã hóa trong DB. Key management thế nào? Key lộ ra thì sao?"*
> **Gợi ý:** Dùng **HashiCorp Vault** hoặc **AWS KMS** để quản lý key rotation. Key không bao giờ hardcode trong code hay config file. Application lấy key từ Vault qua API (có authentication). Key rotation định kỳ (mỗi 90 ngày). Nếu nghi ngờ key bị lộ → rotate ngay + re-encrypt toàn bộ dữ liệu.

### 💡 Mẹo ghi nhớ: Circuit Breaker States — "COH"

```
C — CLOSED:    "Cửa đóng" → request đi qua bình thường (đếm lỗi)
O — OPEN:      "Cửa mở" → CHẶN request (fail-fast, trả fallback)
H — HALF-OPEN: "Hé cửa" → thử vài request, nếu OK thì đóng lại
```
> ⚠️ **Lưu ý ngược trực giác**: CLOSED = cho đi qua, OPEN = chặn lại

---

## NGÀY 6: MOCK INTERVIEW — Thực Hành Toàn Bộ
**⏰ Thời lượng: 2.5 tiếng**

### Cấu trúc Mock Interview (90 phút)

| Phase | Thời gian | Nội dung |
|-------|-----------|----------|
| 1. Warm-up | 10 phút | Self-intro + Why Banking + Why KAI ASIA |
| 2. Java Core | 15 phút | 3 câu: JVM, Collections, Concurrency |
| 3. Spring & Architecture | 20 phút | 3 câu: Spring Boot, Kafka, Microservices |
| 4. Banking Domain | 20 phút | 3 câu: Transaction, Security, Integration |
| 5. Behavioral (STAR) | 15 phút | 2 câu: Difficult bug + Performance improvement |
| 6. Câu hỏi ngược | 10 phút | Bạn hỏi lại nhà tuyển dụng |

### Cách thực hành
1. **Tự ghi âm**: Mở Voice Recorder, đặt timer cho mỗi câu (2 phút/câu)
2. **Nghe lại**: Kiểm tra có bị "ờ ừm", có nói quá dài không, có con số cụ thể không
3. **Hoặc dùng AI**: ChatGPT/Claude làm interviewer, bật voice mode

### Câu hỏi "sát thủ" để tự test

1. *"Giải thích cho anh luồng hoàn chỉnh từ lúc khách hàng bấm 'Chuyển tiền' đến khi tiền vào tài khoản người nhận."*
2. *"Nếu hệ thống của em có 1 triệu giao dịch/ngày, lịch sử giao dịch lưu ở đâu? Query thế nào cho nhanh?"*
3. *"Em đã fix race condition trong game server. Nếu chuyển sang fix race condition trên số dư tài khoản ngân hàng, cách tiếp cận có gì khác?"*

---

## NGÀY 7: TỔNG ÔN & RELAX
**⏰ Thời lượng: 2 tiếng**

### Checklist ôn lại nhanh (1 tiếng)

- [ ] Tập nói Self-Introduction 3 lần (dưới 2.5 phút)
- [ ] Đọc lại 6 STAR Stories — nhớ con số quan trọng
- [ ] Ôn lại sơ đồ Saga Pattern (vẽ ra giấy 1 lần)
- [ ] Nhắc lại: Timeout banking → PQRC
- [ ] Nhắc lại: Circuit Breaker → COH
- [ ] Chuẩn bị 5 câu hỏi ngược

### Mindset trước phỏng vấn (30 phút đọc)

> [!IMPORTANT]
> **Quy tắc vàng khi trả lời phỏng vấn Banking:**
> 1. **Luôn nói con số**: "giảm 30%", "45 phút restore", "10K events/sec"
> 2. **Thừa nhận gap thành thật**: "Em chưa làm banking nhưng..." → kết nối về thế mạnh
> 3. **Dùng STAR**: Situation → Task → Action → Result (mỗi câu ≤ 2 phút)
> 4. **Khi không biết**: "Em chưa trực tiếp làm phần này, nhưng theo hiểu biết của em thì..." → trình bày hướng tiếp cận
> 5. **Kết thúc câu trả lời**: Luôn quay lại kết nối với vị trí đang ứng tuyển

### Chuẩn bị logistics (30 phút)

- [ ] Kiểm tra địa chỉ Hàm Nghi, Q1 — đường đi, chỗ gửi xe
- [ ] Chuẩn bị outfit: áo sơ mi, quần tây (formal nhưng thoải mái)
- [ ] In CV 2 bản
- [ ] Mang theo sổ tay + bút (để ghi chú khi cần vẽ kiến trúc)
- [ ] Ngủ sớm tối hôm trước (≥ 7 tiếng)

---

## III. CHUẨN BỊ STAR STORIES — Các Con Số Bị Hỏi Xoáy

### STAR #1: Giảm 30% DB Load bằng Redis Caching
> **S:** Game server Gihot peak hours (8-10 PM), MySQL CPU > 80%, API response 200ms, peak 500ms+.
>
> **T:** Optimize cho 5,000+ concurrent users chơi mượt.
>
> **A:** Profiling → 70% queries là read lặp lại. Implement multi-layer Redis: session (Hash, TTL 30m), config (String, write-through), leaderboard (Sorted Set, real-time), rate limiting (INCR+EXPIRE). Cache invalidation qua RabbitMQ events.
>
> **R:** DB CPU 80% → 55% (**giảm 30%**), API response 200ms → 160ms (**giảm 20%**), leaderboard query 50ms → <5ms.

### STAR #2: Fix Race Condition — 0% State Corruption
> **S:** Game MOBA C++17, QA phát hiện player state bị corrupt ngẫu nhiên dưới high load (>1000 sessions).
>
> **T:** Điều tra và fix bug không reproduce được trên local.
>
> **A:** Thêm logging với thread ID + timestamp → phát hiện 2 threads cùng modify player state. Implement fine-grained mutex per room + AtomicInteger cho counters. Stress test 2000+ sessions chạy 24 giờ.
>
> **R:** **0% race condition** sau fix, latency chỉ tăng 2-3ms. Viết internal doc về thread-safety cho team.

### STAR #3: Kafka 10K+ events/sec — Dual-Broker Architecture
> **S:** FPM cần messaging cho 2 patterns khác nhau: transaction streaming (ordering, replay) và domain events (routing, DLQ).
>
> **T:** Chọn messaging solution đáp ứng cả 2 mà không compromise.
>
> **A:** POC Kafka-only vs RabbitMQ-only vs hybrid → chọn dual-broker: Kafka 8 topics cho transactions, RabbitMQ cho domain events. Anti-Corruption Layer tách messaging khỏi business logic.
>
> **R:** Kafka xử lý **10K+ events/sec** load test, RabbitMQ DLQ handle failures gracefully, shared `fpm-messaging` library cho developer experience.

### STAR #4: Production Incident — Restored in 45 Minutes
> **S:** Hahalolo đêm thứ 6, peak traffic, hệ thống chậm 5-10x. Users timeout upload ảnh, load feed.
>
> **T:** Diagnose + fix trong SLA 2 giờ.
>
> **A:** Enable slow query log → phát hiện image gallery API giữ DB connection quá lâu → N+1 query (1 request = 50+ queries). Hotfix: tăng connection pool + query timeout. Long-term: EntityGraph + batch fetching.
>
> **R:** **Restored 45 phút** (dưới SLA), query time **giảm 40%**, added automated monitoring alerts, viết postmortem cho team.

### STAR #5: 10,000 CCU Game Server
> **S:** Gihot phát triển game MOBA mới, target 10,000 concurrent users cho server Southeast Asia.
>
> **T:** Đảm bảo game server xử lý real-time state cho 10K players đồng thời.
>
> **A:** Fine-grained locking per room (không global lock), lock-free data structures cho read-heavy paths (ConcurrentHashMap, AtomicReference), connection pooling (Netty event loop), binary protocol (Protobuf) giảm bandwidth.
>
> **R:** **Stable 10K CCU** trong stress test, latency p99 < 50ms, zero state corruption.

---

## IV. CÂU HỎI NGƯỢC LẠI — Hỏi Nhà Tuyển Dụng

> [!TIP]
> Câu hỏi ngược cho thấy bạn **nghiêm túc** và **có tư duy kỹ thuật**. Chọn 2-3 câu phù hợp nhất.

### 5 Câu hỏi thông minh dựa trên JD

**1. Về kiến trúc hệ thống:**
> *"Hệ thống hiện tại đang ở giai đoạn nào: monolith truyền thống, đang chuyển đổi sang microservices, hay đã là microservices hoàn chỉnh? Team mình sẽ tham gia vào phần nào trong quá trình chuyển đổi đó?"*

**2. Về Core Banking Integration:**
> *"Dự án tích hợp với Core Banking engine nào (T24, FLEXCUBE, hay in-house)? Giao tiếp chủ yếu qua SOAP/XML hay đã có REST wrapper? Em hỏi vì muốn chuẩn bị trước về protocol và tooling."*

**3. Về quy trình phát triển:**
> *"Team áp dụng quy trình deploy như thế nào? Có CI/CD pipeline tự động không? Khi release feature mới cho hệ thống banking, quy trình review và approval đi qua bao nhiêu bước trước khi lên Production?"*

**4. Về con người & văn hóa:**
> *"Team backend hiện có bao nhiêu người? Tỷ lệ senior/junior như thế nào? Anh/chị kỳ vọng gì ở người mới trong 3 tháng đầu tiên về mặt đóng góp kỹ thuật?"*

**5. Về thử thách kỹ thuật:**
> *"Thử thách kỹ thuật lớn nhất mà team đang gặp phải hiện tại là gì? Có phải liên quan đến performance, scalability, hay legacy migration? Em hỏi vì muốn xem mình có thể đóng góp ngay từ những ngày đầu ở chỗ nào."*

---

## PHỤ LỤC: Quick Reference Card (In ra A4 để mang theo ôn)

```
╔══════════════════════════════════════════════════════════╗
║  SELF-INTRO: PITCH (Position-Impact-Technology-         ║
║                      Connection-Hope) ≤ 2.5 phút       ║
╠══════════════════════════════════════════════════════════╣
║  TIMEOUT BANKING: PQRC                                  ║
║  (Pending → Query → Resolve → Confirm/Reconcile)        ║
╠══════════════════════════════════════════════════════════╣
║  CIRCUIT BREAKER: COH                                    ║
║  (Closed=OK → Open=Block → Half-Open=Test)               ║
╠══════════════════════════════════════════════════════════╣
║  IDEMPOTENCY 3 TẦNG:                                    ║
║  Frontend Disable → Redis Lock → DB Unique Index         ║
╠══════════════════════════════════════════════════════════╣
║  SAGA: Orchestrator (Banking ✅) vs Choreography          ║
╠══════════════════════════════════════════════════════════╣
║  KEY NUMBERS:                                            ║
║  30% DB load ↓ | 20% response ↓ | 40% query ↓          ║
║  10K CCU | 10K events/sec | 45min restore                ║
╠══════════════════════════════════════════════════════════╣
║  KHI KHÔNG BIẾT:                                         ║
║  "Em chưa trực tiếp làm phần này, nhưng theo hiểu biết  ║
║   của em thì..." → trình bày hướng tiếp cận               ║
╚══════════════════════════════════════════════════════════╝
```

---

*Tài liệu được tạo bởi Technical Interview Coach · Cập nhật: Tháng 08/2026*
*Dựa trên: CV Đặng Trọng Phúc × JD KAI ASIA Java Developer (Banking, Hàm Nghi Q1)*
