# PART 17 — Mock Interview Session
> 🎭 I will act as a real interviewer. Answer each question. I will evaluate and give feedback.

---

## 📋 Instructions for Use

1. **Read one question at a time**
2. **Say your answer out loud** (don't just read the sample)
3. **Record yourself** on phone
4. **Review**: grammar, vocabulary, fluency, confidence
5. **Repeat** until natural

---

## 🎬 SESSION 1 — HR + English (30 min)

---

### 👔 Round 1: HR Warmup

**Q1 [Interviewer]:**
> "Hello! Nice to meet you. Can you please introduce yourself in English?"

*(Answer out loud — aim for 90 seconds)*

**Evaluation Checklist:**
- [ ] Mentioned name + years of experience
- [ ] Covered both Hahalolo and Gihot Studio
- [ ] Mentioned FPM project
- [ ] Ended with forward-looking statement
- [ ] Under 2 minutes
- [ ] No long pauses or filler words (um, uh)

---

**Q2 [Interviewer]:**
> "I see you've worked in game development with C++. Why are you applying for a Java backend role now?"

*(Think: be honest but positive — connect your Java experience)*

**Sample Answer Framework:**
> "While my most recent role involved C++ for game servers, Java has been a constant in my career — [Hahalolo], and my FPM personal project. I chose C++ at Gihot because of the real-time performance requirements. But my passion and primary expertise is Java backend, particularly in Spring Boot and microservices. I want to go deeper in that direction, especially in enterprise or product domains."

---

**Q3 [Interviewer]:**
> "What's your biggest technical achievement in the last year?"

*(Use STAR format — pick your best project)*

---

**Q4 [Interviewer]:**
> "Where do you see yourself in 3 years?"

**Sample Answer:**
> "In 3 years, I'd like to be at a senior Java backend engineer level, leading technical decisions on complex distributed systems. I want to deepen my expertise in system design, performance engineering, and ideally mentor junior developers. I'm particularly interested in growing within a company that works on meaningful, large-scale products."

---

## 🎬 SESSION 2 — Core Java (45 min)

**Q5 [Interviewer]:**
> "Let's get technical. Can you explain how HashMap works internally in Java?"

*(Speak without notes — test your understanding)*

**Evaluation:**
- [ ] Mentioned array of buckets
- [ ] Mentioned hash function
- [ ] Mentioned collision handling (LinkedList/TreeMap)
- [ ] Mentioned load factor and resize
- [ ] Mentioned Java 8 treeify at threshold 8

---

**Q6 [Interviewer]:**
> "What's the difference between `synchronized` and `volatile`? When would you use each?"

---

**Q7 [Interviewer]:**
> "What are Java 8 Stream operations? Give me a real example from your work."

**Your real example:**
> "In my FPM notification service, I use streams to filter and map incoming events. For example, when processing a batch of transactions, I'd filter out already-processed ones, map them to notification DTOs, and collect them into groups by user ID using `Collectors.groupingBy()`."

---

**Q8 [Interviewer]:**
> "Write a method that returns the top 3 most frequent words in a string."

```java
public List<String> top3Words(String text) {
    return Arrays.stream(text.toLowerCase().split("\\s+"))
        .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(3)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
}
```

---

## 🎬 SESSION 3 — Spring & Architecture (45 min)

**Q9 [Interviewer]:**
> "Walk me through what happens when a Spring Boot application starts up."

**Sample Answer:**
> "When a Spring Boot app starts, first the `main` method runs `SpringApplication.run()`. This creates the `ApplicationContext`. Then auto-configuration kicks in — Spring Boot scans `spring.factories` or the autoconfigure imports file and conditionally creates beans based on what's on the classpath and what's configured. `@ComponentScan` finds all `@Component`, `@Service`, `@Repository`, `@Controller` beans. Dependency injection resolves all `@Autowired` relationships. Then `@PostConstruct` methods run. Finally, the embedded Tomcat starts, and the app is ready to serve requests."

---

**Q10 [Interviewer]:**
> "You mentioned your FPM project. Tell me about the most challenging technical decision you made in that project."

**Your answer framework:**
> "The most challenging decision was the dual-broker architecture — using both Kafka and RabbitMQ. Many people use just one. I chose to use Kafka for transaction streaming because of its durability, replay capability, and throughput. But for domain event routing — things like wallet.created or budget.alerts — I chose RabbitMQ because of its flexible routing with exchanges and dead-letter queue support. Justifying and implementing this hybrid approach taught me a lot about matching the tool to the use case."

---

**Q11 [Interviewer]:**
> "You have a performance issue in production. API response time increased from 100ms to 3000ms. Walk me through your debugging process."

**Sample Answer:**
> "I'd follow a systematic approach. First, I'd check the monitoring and logs — look at APM tools or Actuator metrics for HTTP response time distribution. Second, I'd identify which endpoints are slow. Third, I'd look at distributed traces to find which service or layer is the bottleneck. Fourth, if it's DB, I'd check slow query logs, execution plans. If it's a specific service, I'd look at thread pool exhaustion, GC pauses, or external dependency timeouts. Based on what I found, I'd address the root cause — add an index, increase thread pool size, add caching, or optimize a query."

---

**Q12 [Interviewer]:**
> "How would you design an API that handles 10,000 requests per second?"

**Your answer:**
> "I'd use several strategies: horizontal scaling with load balancer (stateless services), Redis caching for frequently-read data, connection pooling (HikariCP tuning), async processing for non-critical work (message queue), database read replicas for read-heavy endpoints, and rate limiting at the gateway level. In my game server work, I handled 10,000 concurrent users using this kind of layered approach."

---

## 🎬 SESSION 4 — System Design (30 min)

**Q13 [Interviewer]:**
> "Design a simple notification system that sends email, SMS, and push notifications."

*(Speak through the design — draw on paper if helpful)*

**Evaluation checklist:**
- [ ] Identified async processing (not synchronous)
- [ ] Used message queue (Kafka/RabbitMQ)
- [ ] Mentioned separate workers per channel
- [ ] Mentioned retry/DLQ for failed deliveries
- [ ] Mentioned user preference management

---

## 🎬 SESSION 5 — Live Coding (30 min)

**Q14 [Interviewer]:**
> "Given a string, find the length of the longest substring without repeating characters."

*(Code it out loud, explain your thinking)*

**Rubric:**
- [ ] Explained approach before coding
- [ ] Mentioned sliding window pattern
- [ ] Correct solution
- [ ] Explained time/space complexity
- [ ] Handled edge cases (empty string, all same chars)

---

**Q15 [Interviewer]:**
> "Do you have any questions for me?"

**Your questions to ask:**
- "What does the tech stack currently look like for this team?"
- "What are the biggest technical challenges the team is facing right now?"
- "How does the team approach code review and technical decisions?"
- "What does the onboarding process look like for a new backend engineer?"
- "What are the growth opportunities for engineers in this company?"

---

## 📊 Self-Evaluation Scorecard

After each mock session, rate yourself:

| Category | Score (1-5) | Notes |
|----------|------------|-------|
| English fluency | /5 | |
| Technical accuracy | /5 | |
| STAR format usage | /5 | |
| Confidence | /5 | |
| Pace (not too fast/slow) | /5 | |
| Eye contact / tone | /5 | |
| Used specific numbers | /5 | |
| Asked good questions | /5 | |

**Target: 4+/5 on all categories before real interview**

---

## 🔑 Last-Minute Cheat Sheet

### Your Key Numbers
```
3+ years backend experience
5,000+ concurrent game sessions (Gihot)
10 services in FPM project
Sub-50ms gRPC latency
40% SQL query time reduction (Hahalolo)
30% DB load reduction with Redis caching
25% latency reduction under 10k user load test
Java 21 + Spring Boot 3.5 (FPM)
```

### Your Unique Stories
```
Race condition fix → race_condition_fix (10k stress test, mutex + Redis idempotency)
Production SQL incident → postmortem, staging policy adopted by team
FPM architecture → dual broker, 9 shared libraries, defense-in-depth security
```

### Opening Line
> "I'm Phuc, a Backend Developer with 3+ years building distributed systems in Java and C++."

### Closing Line
> "I'm confident I can contribute from day one, and I'm excited about the opportunity to apply my distributed systems experience at a larger scale here."
