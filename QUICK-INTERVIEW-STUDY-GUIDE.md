# 🚀 HƯỚNG DẪN ÔN LUYỆN & REVIEW PROJECT HÀNG NGÀY (DAILY INTERVIEW STUDY GUIDE)

> **Mục tiêu**: Ghi nhớ 100% kiến thức trọng tâm JD/CV, phản xạ trả lời tự tin, súc tích và hiểu sâu sắc kiến thức + hệ thống FPM Project mà không bị học vẹt.

---

## 🧠 PHẦN 1: PHƯƠNG PHÁP HỌC KỸ THUẬT CHO PHỎNG VẤN

### 1. Active Recall — Không Đọc Lại, Tự Hỏi Rồi Trả Lời

**Sai lầm phổ biến**: Đọc lại tài liệu nhiều lần → cảm giác thuộc nhưng không recall được.

**Cách đúng**: Đọc 1 section → đóng file → tự giải thích to ra như đang phỏng vấn. Không nhớ được → mới mở lại.

**Thực hành với mỗi section**:
1. Đọc "1.2 Propagation" (5 phút)
2. Đóng file
3. Nói to: *"Propagation là... REQUIRED hoạt động như... REQUIRES_NEW khác ở chỗ..."*
4. Điểm nào blank → mở lại đọc **đúng chỗ đó**
5. Đóng lại → thử lại

---

### 2. Feynman Technique — Giải Thích Như Dạy Người Khác

Nếu bạn không giải thích được bằng lời đơn giản → bạn chưa thực sự hiểu.

**Với mỗi concept, thử giải thích trong 60 giây**:
- ❓ *"Optimistic Locking là gì và tại sao dùng nó?"*
- ❓ *"Consumer Group Rebalancing xảy ra khi nào và có hại gì?"*
- ❓ *"Circuit Breaker khác Retry ở điểm nào?"*
- ❓ *"@Transactional Propagation.REQUIRES_NEW khác gì REQUIRED? Khi nào dùng?"*
- ❓ *"Kafka `acks=all` giải quyết vấn đề gì? Trade-off là gì?"*

> Nếu giải thích vấp váp → chưa hiểu đủ sâu → mở lại section tương ứng đọc lại ngay.

---

### 3. Spaced Repetition — Lịch Ôn Lại Theo Chu Kỳ

Não người quên theo **đường cong Ebbinghaus**. Cần ôn lại đúng thời điểm.

| Lần học | Ôn lại lần 1 | Ôn lại lần 2 | Ôn lại lần 3 |
| :--- | :--- | :--- | :--- |
| **Ngày 1** | Ngày 2 | Ngày 4 | Ngày 7 |
| **Ngày 2** | Ngày 3 | Ngày 5 | Ngày 8 |
| **Ngày 3** | Ngày 4 | Ngày 6 | Ngày 9 |

**Cách ôn lại nhanh** (không cần đọc lại toàn bộ file):
- Dùng phần **Interview Q&A** cuối mỗi file — tự hỏi rồi trả lời
- Nếu trả lời được → **skip**
- Nếu không trả lời được → **đọc lại section đó**

---

## 📋 PHẦN 2: QUY TRÌNH HỌC MỖI NGÀY (THỰC HÀNH CỤ THỂ)

### Block 1 (30 phút) — ÔN CŨ

→ Lấy file của **ngày hôm qua**
→ Đọc **CHỈ** phần "Interview Q&A" cuối file
→ Tự trả lời to từng câu, **không nhìn đáp án**
→ Câu nào không trả lời được → đánh dấu → đọc lại section đó

### Block 2 (90 phút) — HỌC MỚI

→ Đọc section mới (1-2 sections)
→ Sau mỗi section: **đóng file → giải thích to** (Active Recall)
→ Kết nối với FPM project (xem **Phần 3** bên dưới)

### Block 3 (30 phút) — MOCK

→ Mở phần "Câu hỏi tự test cuối ngày" trong [JD-JAVA-BACKEND-ROADMAP.md](file:///d:/WorkSpace/Document/Improve-Knowledge/Plan/JD-JAVA-BACKEND-ROADMAP.md)
→ Trả lời to, đủ câu, đủ ví dụ thực tế từ project FPM / Hahalolo

---

## 🔄 PHẦN 3: REVIEW PROJECT FPM THEO TỪNG PHẦN

> Đây là cách kết nối **tài liệu lý thuyết** với **project thực tế** — quan trọng nhất để trả lời phỏng vấn tự tin.

### Bản Đồ: Concept → FPM Code

| Khi học xong | Vào FPM xem lại |
| :--- | :--- |
| `@Transactional` Propagation | Service layer nơi gọi AuditService — xem propagation được set như nào |
| Circuit Breaker 3 states | Resilience4j config trong `application.yml` — xem threshold thực tế |
| Kafka Consumer Group | Kafka listener config — xem `group-id`, `enable-auto-commit` |
| Redis Distributed Lock | Nơi dùng Redisson/Redis lock cho wallet balance |
| Optimistic / Pessimistic Lock | Entity Wallet — có `@Version` không? Hay dùng `@Lock`? |
| gRPC | Proto files + Stub usage trong service-to-service calls |

**Bộ file tài liệu tham chiếu theo thứ tự trọng tâm**:
- 📄 [CV-Deep-Dive-Spring.md](file:///d:/WorkSpace/Document/Improve-Knowledge/03-Spring-Ecosystem/CV-Deep-Dive-Spring.md)
- 📄 [CV-Deep-Dive-Kafka-Redis.md](file:///d:/WorkSpace/Document/Improve-Knowledge/06-Distributed-Systems/CV-Deep-Dive-Kafka-Redis.md)
- 📄 [CV-Deep-Dive-Database-Locking.md](file:///d:/WorkSpace/Document/Improve-Knowledge/04-Database/CV-Deep-Dive-Database-Locking.md)
- 📄 [CV-Deep-Dive-Microservices-Patterns.md](file:///d:/WorkSpace/Document/Improve-Knowledge/05-System-Design/CV-Deep-Dive-Microservices-Patterns.md)
- 📄 [CV-Gaps-OAuth2-K8s-SOLID-gRPC.md](file:///d:/WorkSpace/Document/Improve-Knowledge/10-Interview-Prep/CV-Gaps-OAuth2-K8s-SOLID-gRPC.md)

---

### Câu Hỏi Tự Hỏi Khi Review Code

Khi nhìn vào một đoạn code trong FPM, hỏi:

1. **"Đây là pattern gì?"** (Circuit Breaker? Saga? Cache-aside? Event-driven?)
2. **"Tại sao chọn approach này thay vì alternative?"** (VD: Tại sao Pessimistic Lock thay vì Optimistic? Tại sao Kafka thay vì RabbitMQ cho flow này?)
3. **"Nếu không có cái này, chuyện gì xảy ra?"** (VD: Bỏ Circuit Breaker → cascade failure khi Payment Service chết. Bỏ Redis lock → race condition overbooking.)
4. **"Số liệu trong CV (30% DB reduction, sub-50ms) — explain HOW cụ thể?"**
   - **~30% DB load reduction**: Đến từ đâu?
     - Redis Cache-Aside cho hot data (tránh query lặp lại vào DB)
     - Database indexing tối ưu trên các column hay query
     - Kafka async processing giảm synchronous DB writes
   - **Sub-50ms latency**: Đạt được nhờ gì?
     - gRPC/Protobuf thay vì REST/JSON (serialization nhanh hơn, connection multiplexing)
     - Redis in-memory read thay vì DB disk read
     - Connection pooling (HikariCP) tránh overhead tạo connection mới
   - **10,000+ concurrent users stress-tested**: Chứng minh bằng gì?
     - Load testing tool (JMeter / Gatling / k6)
     - Horizontal scaling với multiple service instances
     - Kafka partitioning phân tải consumer xử lý song song

---

### Quy Trình Review Project Lội Ngược Dòng (Reverse Code Walkthrough)

Thay vì đọc code từ đầu đến cuối một cách thụ động, review theo **3 bước lội ngược dòng**:

```
Feature/Business Flow → Pattern & Architecture → Actual Code/Config
```

#### Ví dụ: Walkthrough Feature "Đặt Hàng & Thanh Toán" (FPM System)

* **Bước 1 (Business Flow)**: Người dùng đặt gói giao dịch trên ứng dụng.
* **Bước 2 (Mapping Pattern & Architecture)**:
  * **Tránh Overbooking / Race Condition** → Pessimistic Locking trên DB hoặc Redis Distributed Lock (Redlock)
  * **Bảo vệ Database khỏi Spike Traffic** → Redis Cache-Aside Pattern
  * **Xử lý Bất đồng bộ & Event-Driven** → Kafka Consumer Group (`acks=all`, retry topic)
  * **Bảo vệ hệ thống khỏi Cascade Failure** → Resilience4j Circuit Breaker khi gọi gRPC/REST sang Payment Service
* **Bước 3 (Tra cứu Code)**: Mở 5 file Markdown trong bộ tài liệu `Improve-Knowledge`, đối chiếu từ khóa và code mẫu tương ứng.

---

## 📋 DAILY QUICK CHECKLIST

- [ ] **Block 1 (30p)**: Ôn Q&A ngày hôm qua — tự trả lời to, không nhìn đáp án.
- [ ] **Block 2 (90p)**: Học section mới → đóng file → giải thích to → mapping vào FPM code.
- [ ] **Block 3 (30p)**: Mock interview — trả lời câu hỏi tự test cuối ngày trong roadmap.
- [ ] **Cuối ngày**: Đánh dấu tích vào checklist lộ trình tại [JD-JAVA-BACKEND-ROADMAP.md](file:///d:/WorkSpace/Document/Improve-Knowledge/Plan/JD-JAVA-BACKEND-ROADMAP.md).
