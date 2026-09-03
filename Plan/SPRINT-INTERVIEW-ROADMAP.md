# 🚀 SPRINT INTERVIEW ROADMAP — 2 Công Ty Mục Tiêu
> **Profile**: 2–3 năm kinh nghiệm | Java Backend Engineer
> **Mục tiêu**: Ôn luyện cấp tốc đúng trọng tâm JD
> **Updated**: 2026-09-03

---

## 🎯 PHÂN TÍCH 2 JD — ĐIỂM TRỌNG TÂM

### 📋 JD #1 — Công Ty 1 (Senior-leaning, Enterprise)
| Nhóm | Yêu Cầu | Mức Độ |
|---|---|---|
| **Backend** | Java/J2EE hoặc C# | 🔴 BẮT BUỘC |
| **Framework** | Spring MVC, Spring Boot | 🔴 BẮT BUỘC |
| **Frontend** | HTML/CSS, JS/jQuery/Bootstrap | 🟡 CẦN BIẾT |
| **Database** | Oracle | 🔴 BẮT BUỘC |
| **OOP** | Nắm vững kiến thức OOP | 🔴 BẮT BUỘC |
| **Quy trình** | SDLC, Agile/Scrum | 🟡 CẦN BIẾT |
| **Ưu tiên+** | Redis, Docker, Microservices, Angular | 🟢 ĐIỂM CỘNG |
| **Ưu tiên+** | Python, Big Data platforms | 🟢 ĐIỂM CỘNG |
| **Ưu tiên+** | AI tools, System Admin (IIS, Linux) | 🟢 ĐIỂM CỘNG |

### 📋 JD #2 — Công Ty 2 (Junior-Mid, Logistics domain)
| Nhóm | Yêu Cầu | Mức Độ |
|---|---|---|
| **Backend** | Java, JSP, Visual Basic | 🔴 BẮT BUỘC |
| **Database** | Oracle hoặc SQL Server | 🔴 BẮT BUỘC |
| **English** | Giao tiếp cơ bản | 🟡 CẦN BIẾT |
| **Mindset** | Tự học, chịu áp lực, logic | 🔴 BẮT BUỘC |
| **AI Tools** | Sử dụng AI tools hiệu quả | 🟡 CẦN BIẾT |
| **Khác** | Có thể đi công tác nước ngoài | 🟢 THÊM |

### 🎯 GIAO ĐIỂM — TRỌNG TÂM CHUNG CỦA CẢ 2 JD
```
1. Java Core + OOP  ← CỰC KỲ QUAN TRỌNG
2. Spring Boot + Spring MVC
3. Oracle Database (SQL, PL/SQL cơ bản)
4. Quy trình phát triển phần mềm (SDLC/Agile)
5. Tư duy xử lý vấn đề + Communication
```

---

## ⚡ PHÂN BỔ THỜI GIAN THEO MỨC ĐỘ ƯU TIÊN

```
🔴 MUST (60% thời gian) → Bắt buộc pass được
🟡 SHOULD (30% thời gian) → Tăng điểm đáng kể
🟢 NICE-TO-HAVE (10% thời gian) → Đề cập để ghi điểm
```

---

## ⚡ SPRINT 3 NGÀY (Emergency)

**Ngày 1 — Java Core + OOP**
- [ ] OOP 4 tính chất: Encapsulation, Inheritance, Polymorphism, Abstraction
- [ ] Interface vs Abstract Class — khi nào dùng cái nào
- [ ] Collections: ArrayList, HashMap, LinkedHashMap — cơ chế bên trong
- [ ] Java 8: Stream, Lambda, Optional — viết ví dụ thực tế
- [ ] Exception Handling: checked vs unchecked, best practices
- [ ] **Luyện nói**: Giải thích OOP bằng ví dụ thực tế trong project của bạn

**Ngày 2 — Spring Boot + Database**
- [ ] Spring Boot: Auto-configuration, DI/IoC, Component Scan
- [ ] @Transactional: Propagation (REQUIRED vs REQUIRES_NEW), Isolation
- [ ] Spring MVC Request lifecycle (DispatcherServlet → Controller → View)
- [ ] Oracle SQL: JOIN types, GROUP BY, HAVING, Subquery vs JOIN
- [ ] Oracle: INDEX — khi nào tạo, composite index, explain plan
- [ ] **Luyện nói**: Walk through một REST API endpoint bạn đã làm

**Ngày 3 — System Design + Behavioral**
- [ ] Thiết kế hệ thống đơn giản: REST API CRUD với Spring Boot + Oracle
- [ ] SDLC / Agile: Sprint, User Story, Scrum roles
- [ ] Câu hỏi HR: "Tell me about yourself", "Tại sao muốn chuyển?", "Điểm yếu?"
- [ ] Chuẩn bị 3 STAR stories từ project thực tế
- [ ] Ôn Redis concept (30 phút: cache-aside, eviction, TTL)
- [ ] Ôn Docker concept (30 phút: container vs VM, Dockerfile, docker-compose)

---

## 📅 SPRINT 1 TUẦN (Recommended)

### NGÀY 1 — OOP & Java Core Deep Dive

| Chủ đề | Việc cần làm | File tham khảo |
|---|---|---|
| OOP 4 trụ cột | Thuộc định nghĩa + ví dụ code + ví dụ thực tế | `Part-04-OOP-Design-Patterns.md` |
| SOLID Principles | S, O, L, I, D — giải thích + vi phạm trông như thế nào | `Part-04-OOP-Design-Patterns.md` |
| Collections Framework | HashMap internals, ArrayList vs LinkedList, TreeMap | `Part-03-Core-Java.md` |
| Java 8 Features | Stream API, Lambda, Optional, Method Reference | `Part-03-Core-Java.md` |
| Design Patterns | Singleton, Factory, Builder, Strategy — code ví dụ | `Part-04-OOP-Design-Patterns.md` |

**Câu hỏi chắc chắn bị hỏi:**
```
"Phân biệt Interface và Abstract Class trong Java 8?"
"HashMap hoạt động như thế nào khi có collision?"
"Kể tên Design Pattern bạn đã dùng trong project?"
```

---

### NGÀY 2 — Spring Boot + Spring MVC

| Chủ đề | Việc cần làm | File tham khảo |
|---|---|---|
| IoC & DI | @Autowired, @Bean, @Configuration, 3 loại injection | `Part-05-07-Spring-Framework-MVC-Boot.md` |
| Spring MVC | DispatcherServlet flow, @Controller vs @RestController | `Part-05-07-Spring-Framework-MVC-Boot.md` |
| Spring Boot | Auto-configuration, Starter dependencies, application.yml | `Part-05-07-Spring-Framework-MVC-Boot.md` |
| @Transactional | Propagation REQUIRED/REQUIRES_NEW, Isolation levels | `Part-05-07-Spring-Framework-MVC-Boot.md` |
| AOP | @Before, @After, @Around — use case logging/auditing | `Part-05-07-Spring-Framework-MVC-Boot.md` |
| Spring Security | JWT, Basic Auth flow (ở mức khái niệm) | `Part-05-07-Spring-Framework-MVC-Boot.md` |

**Câu hỏi chắc chắn bị hỏi:**
```
"Self-invocation trong @Transactional là gì? Cách fix?"
"Spring Boot vs Spring MVC khác nhau thế nào?"
"Kể về một project bạn dùng Spring Boot?"
```

---

### NGÀY 3 — Oracle Database

| Chủ đề | Việc cần làm | File tham khảo |
|---|---|---|
| SQL Advanced | INNER/LEFT/RIGHT JOIN, EXISTS vs IN, Subquery | `Part-09-Database.md` |
| Oracle Specific | ROWNUM, ROWID, Sequences, Dual table | `Part-09-Database.md` |
| Indexing | B-Tree Index, Composite Index, khi nào index bị skip | `Part-09-Database.md` |
| Performance | Explain Plan, Full Table Scan vs Index Scan | `Part-09-Database.md` |
| Transactions | ACID, Commit/Rollback, Savepoint | `Part-09-Database.md` |
| PL/SQL cơ bản | Stored Procedure, Function, Cursor (biết khái niệm) | `Part-09-Database.md` |
| Window Functions | ROW_NUMBER(), RANK(), PARTITION BY | `Part-09-Database.md` |

**Câu hỏi chắc chắn bị hỏi:**
```
"Sự khác biệt giữa TRUNCATE và DELETE?"
"Khi nào dùng Index? Khi nào Index không hiệu quả?"
"Explain Plan cho query này trông như thế nào?"
```

---

### NGÀY 4 — Hibernate/JPA + REST API

| Chủ đề | Việc cần làm | File tham khảo |
|---|---|---|
| Entity Lifecycle | Transient → Persistent → Detached → Removed | `Part-08-Hibernate-JPA.md` |
| Lazy vs Eager | LazyInitializationException — cách xử lý | `Part-08-Hibernate-JPA.md` |
| N+1 Problem | Định nghĩa, cách detect, fix với JOIN FETCH | `Part-08-Hibernate-JPA.md` |
| REST API Design | HTTP methods, Status codes, RESTful naming | `Part-10-12-WebServices-Microservices-AppServer.md` |
| SOAP vs REST | Khi nào dùng SOAP (legacy enterprise) | `Part-10-12-WebServices-Microservices-AppServer.md` |

---

### NGÀY 5 — Microservices + Redis + Docker (Điểm Cộng JD #1)

| Chủ đề | Mục tiêu | Thời gian |
|---|---|---|
| Microservices concept | Monolith vs Microservices, API Gateway, Service Discovery | 45 phút |
| Redis | Cache-aside pattern, TTL, eviction, Session storage | 45 phút |
| Docker | Container vs VM, Dockerfile, docker-compose, image vs container | 45 phút |
| Kafka (bonus) | Producer/Consumer, Topic, Consumer Group — khái niệm | 30 phút |
| Angular (bonus) | Component-based, 2-way binding, SPA — khái niệm | 15 phút |

**Script trả lời khi bị hỏi:**
```
Redis: "Tôi đã dùng Redis làm distributed cache trong Spring Boot.
        Dùng @Cacheable để cache response API, TTL 30 phút,
        giảm load Oracle 60-70% cho các query read-heavy."

Docker: "Tôi dùng Docker Compose để setup môi trường local
         với Spring Boot app + Oracle XE + Redis containers.
         Giúp đồng bộ môi trường giữa các dev trong team."
```

---

### NGÀY 6 — SDLC + Behavioral + HR

| Chủ đề | Cần chuẩn bị |
|---|---|
| SDLC | Waterfall vs Agile, Sprint Planning, Daily Standup, Retrospective |
| Git workflow | Feature branch, Pull Request, Code Review, Merge vs Rebase |
| 3 STAR Stories | Lấy từ project thực: khó khăn, solution, kết quả đo được |
| "Tại sao chuyển?" | Chuẩn bị câu trả lời chuyên nghiệp, không nói xấu cty cũ |
| AI Tools | Nêu cụ thể: "Tôi dùng GitHub Copilot/ChatGPT để..., tiết kiệm X% thời gian" |
| English | Luyện self-introduction bằng tiếng Anh (JD #2 yêu cầu) |

**3 STAR Stories cần chuẩn bị:**
```
STAR #1: Xử lý performance issue trong production
STAR #2: Học/áp dụng công nghệ mới trong deadline gấp
STAR #3: Phối hợp team resolve conflict kỹ thuật
```

---

### NGÀY 7 — Mock Interview + Weak Points
- [ ] Full mock interview 60 phút (tự nói to hoặc nhờ người hỏi)
- [ ] Review các câu trả lời yếu nhất
- [ ] Đọc lại tài liệu về SDLC, Agile process (JD #2 nhấn mạnh)
- [ ] Chuẩn bị 5 câu hỏi ngược lại cho nhà tuyển dụng
- [ ] Kiểm tra: CV facts khớp với những gì mình nói được không?

---

## 🧠 TOP 20 CÂU HỎI KỸ THUẬT CHẮC CHẮN BỊ HỎI

### Java/OOP (Cả 2 JD)
1. "Phân biệt `==` và `.equals()` trong Java?"
2. "HashMap collision xử lý như thế nào? Java 8 thay đổi gì?"
3. "Interface vs Abstract Class — khi nào dùng cái nào?"
4. "Polymorphism là gì? Cho ví dụ thực tế?"
5. "Checked vs Unchecked Exception — best practice handling?"
6. "String Pool và tại sao String immutable?"

### Spring (JD #1 nặng hơn)
7. "`@Autowired` inject field vs constructor — cái nào tốt hơn và tại sao?"
8. "Giải thích `@Transactional(propagation = REQUIRES_NEW)` khi nào dùng?"
9. "Self-invocation problem trong Spring AOP là gì?"
10. "Spring Boot starter là gì? Auto-configuration hoạt động như thế nào?"

### Database / Oracle (Cả 2 JD)
11. "Clustered Index vs Non-Clustered Index?"
12. "Tại sao query chạy chậm? Bạn debug bằng cách nào?"
13. "INNER JOIN vs LEFT JOIN — khi nào dùng cái nào?"
14. "ACID là gì? Isolation levels và dirty read?"
15. "Stored Procedure vs Function trong Oracle?"

### Soft Skills / Process (JD #2 nhấn mạnh)
16. "Kể về project phức tạp nhất bạn từng làm?"
17. "Khi gặp bug không thể fix, bạn làm gì?"
18. "Bạn học công nghệ mới như thế nào?"
19. "Describe your experience working with a team?"
20. "How do you use AI tools in your daily work?"

---

## 💡 GÓC NHÌN VỀ TỪNG CÔNG TY

### Công Ty 1 — Approach
- **Tech-heavy interview**: Expect whiteboard coding, deep Spring internals
- **Nhấn mạnh**: Oracle expertise, microservices architecture
- **Key differentiator**: Mention Redis caching experience, Docker setup
- **Câu hỏi bạn nên hỏi ngược**: "Team hiện tại đang migrate sang Microservices không?"

### Công Ty 2 — Approach
- **Process-heavy interview**: Expect SDLC questions, team collaboration scenarios
- **Nhấn mạnh**: Self-learning ability, English communication, adaptability
- **Key differentiator**: Highlight AI tools usage cụ thể, willingness to travel/adapt
- **Domain**: Logistics → Mention nếu có experience với business flow phức tạp
- **Câu hỏi bạn nên hỏi ngược**: "Tech stack hiện tại của team là gì? Có plan upgrade không?"

---

## 📊 ĐÁNH GIÁ BẢN THÂN

Tự chấm điểm 1-5 để biết cần ưu tiên ôn cái gì:

| Chủ đề | Tự Chấm | Cần Đạt |
|---|---|---|
| Java Core & OOP | __ /5 | 4/5 |
| Spring Boot/MVC | __ /5 | 4/5 |
| Oracle SQL | __ /5 | 3/5 |
| Hibernate/JPA | __ /5 | 3/5 |
| Microservices concept | __ /5 | 3/5 |
| Redis (concept) | __ /5 | 2/5 |
| Docker (concept) | __ /5 | 2/5 |
| English communication | __ /5 | 3/5 |
| SDLC/Agile | __ /5 | 3/5 |
| Behavioral/HR | __ /5 | 4/5 |

---

## 🗣️ SCRIPT MẪU

### Self Introduction (60-90 giây)
```
"Tôi là [Tên], có [X] năm kinh nghiệm phát triển backend với Java và Spring Boot.
Trong [X] năm, tôi đã làm việc chủ yếu với [domain], xây dựng RESTful APIs,
tích hợp Oracle database, và deploy ứng dụng.

Gần đây tôi đã tìm hiểu thêm về Microservices architecture và Docker,
cũng như sử dụng AI tools như GitHub Copilot để tăng productivity.

Tôi đang tìm kiếm môi trường mới để phát triển, đặc biệt quan tâm đến
[domain của công ty] vì [lý do cụ thể]."
```

### Khi không biết câu trả lời
```
"Tôi chưa có kinh nghiệm trực tiếp với [X], nhưng tôi hiểu concept cơ bản là [concept].
Tôi đã từng xử lý vấn đề tương tự với [công nghệ tương đương], và tôi tin mình
có thể học [X] nhanh chóng vì [lý do cụ thể]."
```

---

## 📚 FILE THAM KHẢO TRONG WORKSPACE

| Ưu Tiên | File | Dùng Khi |
|---|---|---|
| 🔴 #1 | `Part-03-Core-Java.md` | Ôn Java Collections, Threading, Java 8 |
| 🔴 #2 | `Part-04-OOP-Design-Patterns.md` | Ôn OOP 4 tính chất, Design Patterns |
| 🔴 #3 | `Part-05-07-Spring-Framework-MVC-Boot.md` | Ôn IoC, Transactional, MVC lifecycle |
| 🔴 #4 | `Part-09-Database.md` | Ôn Oracle SQL, Index, ACID |
| 🟡 #5 | `Part-08-Hibernate-JPA.md` | Ôn N+1, Lazy/Eager, Entity lifecycle |
| 🟡 #6 | `Part-10-12-WebServices-Microservices-AppServer.md` | Ôn REST, Microservices, Kafka |
| 🟡 #7 | `Part-01-HR-Interview.md` | Ôn câu HR, STAR stories |
| 🟢 #8 | `Part-15-System-Design.md` | Nếu còn thời gian |
| 🟢 #9 | `Part-16-Coding-Test.md` | Nếu còn thời gian |

---

## ✅ CHECKLIST TRƯỚC NGÀY PHỎNG VẤN

### 48 giờ trước
- [ ] Đọc lại CV — mọi thứ ghi trong CV phải nói được chi tiết
- [ ] Google "[Tên Công Ty] tech stack" / "[Tên Công Ty] software engineer review"
- [ ] Chuẩn bị 5 câu hỏi hỏi ngược nhà tuyển dụng
- [ ] Full mock interview lần cuối

### Buổi tối trước
- [ ] Chỉ ôn lại KEY CONCEPTS — không học mới
- [ ] Ngủ đủ giấc

### Sáng ngày phỏng vấn
- [ ] Đọc lại 20 câu hỏi kỹ thuật top (30 phút)
- [ ] Nhẩm lại self-introduction
- [ ] Chuẩn bị notebook để note câu hỏi của interviewer

---

*"Với 2-3 năm kinh nghiệm, interviewer không kỳ vọng bạn biết hết.
Họ kỳ vọng bạn hiểu sâu những gì bạn đã làm, và có tư duy giải quyết vấn đề tốt."* 🎯
