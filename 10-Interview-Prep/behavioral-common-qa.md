# 🎤 Behavioral Interview — Top 30 Questions & Answers

> **Phase:** 3-4 | **Time Block:** T7 + interview prep  
> **Format:** STAR — Situation → Task → Action → Result

---

## Nhóm 1: Technical Challenges

### Q1: "Tell me about a time you solved a difficult technical problem"
→ **Dùng Story #1 (Race Condition Fix)** — xem [star-stories.md](star-stories.md)

### Q2: "Describe a time you optimized system performance"
→ **Dùng Story #2 (Redis Caching 30% DB Reduction)** — xem [star-stories.md](star-stories.md)

### Q3: "Tell me about a significant architectural decision you made"
→ **Dùng Story #3 (Dual-Broker Architecture)** — xem [star-stories.md](star-stories.md)

### Q4: "How do you approach debugging production issues?"
> **Answer Framework:**
> 1. **Triage:** Assess severity, check monitoring dashboards (Grafana/CloudWatch)
> 2. **Reproduce:** Check logs with correlation ID, identify affected scope
> 3. **Isolate:** Binary search — is it DB? Network? Application? External service?
> 4. **Fix:** Hotfix first (rollback/config change), then proper fix
> 5. **Prevent:** Add monitoring alerts, write tests, update runbook
>
> **Example:** → Story #6 (Hahalolo Production Incident)

### Q5: "What's the most complex system you've built?"
> **FPM Project:** 10-service cloud-native microservices system with Java 21, Spring Cloud, Kafka, RabbitMQ, gRPC. Highlight:
> - 7 domain areas: Identity, Wallet, Transaction, Reporting, Notification, OCR, AI
> - Dual-broker event-driven: Kafka (8 topics) + RabbitMQ (domain events)
> - 9 shared internal libraries for cross-service standardization
> - Resilience4j Circuit Breaker + Rate Limiting

### Q6: "How do you handle technical debt?"
> **Answer:** 
> - Identify & document tech debt in backlog (not ignore)
> - Classify: critical (blocking features) vs manageable (workaround exists)
> - Allocate 15-20% sprint capacity for tech debt
> - Example: Tại Gihot, refactored game config management system (từ hardcoded → dynamic config via Redis) during sprint buffer time. Giảm deployment time cho config changes từ 30 phút → real-time.

---

## Nhóm 2: Collaboration & Communication

### Q7: "How do you handle disagreements with team members?"
> **Answer:**
> 1. Listen first — hiểu perspective của họ
> 2. Focus on data, not opinions — "Let's benchmark both approaches"
> 3. Propose compromise or experiment
> 4. Escalate to tech lead only if deadlock
>
> **Example:** Disagreement về Redis vs Memcached cho session cache tại Gihot. Đề xuất benchmark cả 2 với production-like load. Redis won (richer data structures, pub/sub). Team đồng thuận vì data-driven decision.

### Q8: "Describe a time you worked across teams"
→ **Dùng Story #4 (Cross-Team Agile Sprint)** — xem [star-stories.md](star-stories.md)

### Q9: "How do you mentor junior developers?"
> **Answer:**
> - Pair programming sessions (30 min/tuần)
> - Code review with detailed explanations (không chỉ approve/reject)
> - Assign gradually harder tasks with guidance
> - Encourage questions — "No dumb questions" culture
>
> **Example:** Tại Gihot, hướng dẫn 1 junior dev học C++ cùng structured approach. Sau 3 tháng, dev đó có thể independently fix bugs trong game server.

### Q10: "How do you communicate technical decisions to non-technical stakeholders?"
> **Answer:**
> - Use analogies, diagrams, avoid jargon
> - Focus on business impact, not technical details
> - Example: Giải thích Kafka cho PM bằng analogy "hệ thống bưu điện": Producers gửi thư, Kafka là bưu cục trung gian, Consumers nhận thư. PM hiểu tại sao cần async processing.

---

## Nhóm 3: Growth & Learning

### Q11: "Tell me about a time you learned something new quickly"
→ **Dùng Story #5 (Learning C++17 in 3 months)** — xem [star-stories.md](star-stories.md)

### Q12: "How do you stay up-to-date with technology?"
> **Answer:**
> - Daily: Tech newsletters (ByteByteGo, TLDR), Twitter/X tech community
> - Weekly: 1-2 tech blog posts (Baeldung, Martin Fowler), podcast
> - Monthly: Try new technology in personal project (FPM)
> - Quarterly: Read 1 technical book (currently: DDIA)
> - Ongoing: LeetCode practice, System Design study

### Q13: "What's a mistake you made and what did you learn?"
> **Answer:**
> Tại Hahalolo, deploy feature mới vào Friday evening without full regression testing. Service gặp N+1 query issue dưới production load → latency spike. 
> **Lesson:** 
> 1. Never deploy Friday evening (or have rollback plan ready)
> 2. Always load test with production-like data
> 3. Added pre-deployment checklist including query performance review

### Q14: "Where do you see yourself in 3-5 years?"
> **Answer:**
> "In 3 years, I want to grow into a Senior/Staff Engineer role where I can influence system architecture and mentor team members. I'm particularly interested in deepening my expertise in distributed systems and cloud-native architecture. In 5 years, I'd like to be leading the technical direction for a product domain, bridging the gap between business needs and technical execution."

---

## Nhóm 4: Work Style & Values

### Q15: "Why are you leaving your current job?"
→ Xem script trong [star-stories.md](star-stories.md) — Special Question section

### Q16: "Why do you want to join [Company]?"
> **Template:**
> "Three reasons: First, [PRODUCT/MISSION] — I'm excited about [specific product/feature]. Second, [TECH STACK] — your use of [Kafka/K8s/Go] aligns with where I want to grow. Third, [ENGINEERING CULTURE] — I've read your tech blog about [specific post] and I admire [specific practice]."
>
> **Customize for each company!**

### Q17: "What's your ideal work environment?"
> "I thrive in environments that balance autonomy with collaboration. I prefer Agile teams where engineers have ownership over their services, with regular code reviews and knowledge sharing. I value a culture that encourages experimentation — where it's safe to propose new approaches and learn from failures."

### Q18: "How do you handle pressure/tight deadlines?"
> **Answer:**
> 1. Prioritize ruthlessly — what's MVP vs nice-to-have?
> 2. Communicate early if timeline is at risk
> 3. Break work into smaller deliverables
> 4. Focus time — reduce meetings, context switching
>
> **Example:** Sprint deadline cho battle replay feature tại Gihot. Đề xuất chia thành 3 milestones: basic replay (week 1), UI polish (week 2), sharing (week 3). Deliver milestone 1 đúng hạn, stakeholders happy.

### Q19: "How do you prioritize tasks?"
> **Answer:**
> - Urgent + Important → Do now
> - Important + Not Urgent → Schedule (most valuable work!)
> - Urgent + Not Important → Delegate
> - Not Urgent + Not Important → Eliminate
> - Use story points + business value matrix in sprint planning
> - Daily: 2-3 most important tasks first (eat the frog)

### Q20: "Do you prefer working alone or in a team?"
> "Both, depending on the task. For deep technical work like debugging or designing architecture, I need focused alone time. For design reviews, brainstorming, and knowledge sharing, collaboration is essential. At Gihot, I typically do individual deep work in the morning and collaborate with the team in the afternoon for reviews and discussions."

---

## Nhóm 5: Company-Specific Questions

### Q21: "How would you design a payment system?" (VNPay, MoMo)
> → Refer to System Design #6 (E-commerce Payment) + FPM experience

### Q22: "How do you ensure data consistency in distributed systems?" (NAB)
> → Saga pattern, Outbox pattern, Idempotency, Eventual Consistency

### Q23: "Experience with high-traffic systems?" (Tiki, MoMo)
> → Gihot: 10,000+ concurrent users load test, Redis caching, connection pooling

### Q24: "How do you approach code reviews?" (ALL)
> **Answer:**
> - Focus on: correctness, performance, readability, security
> - Ask questions instead of commanding: "What happens if X?" vs "Change this"
> - Praise good code, not just criticize bad code
> - Small PRs (<400 lines) for effective reviews
> - Example: Tại Gihot, phát hiện race condition trong code review → prevented production bug

---

## 📝 Practice Checklist

- [ ] Practice answering Q1-Q6 (Technical) — ghi âm, nghe lại
- [ ] Practice Q7-Q10 (Collaboration) — practice with partner
- [ ] Practice Q11-Q14 (Growth) — viết script trước
- [ ] Practice Q15-Q20 (Work Style) — customize cho từng company
- [ ] Prepare Q21-Q24 (Company-Specific) — research company trước

### Tips
- ⏱️ **2-3 phút** mỗi câu trả lời (không quá dài)
- 📊 **Luôn có số liệu**: "giảm 30%", "5000 users", "45 phút"
- 🎯 **"I" not "We"**: nhấn mạnh contribution cá nhân
- 😊 **Enthusiasm**: tỏ ra hứng thú khi kể technical challenges
- ❌ **KHÔNG nói xấu** công ty/đồng nghiệp cũ
- ✅ **Chuẩn bị 3 câu hỏi HAY** để hỏi interviewer cuối buổi
