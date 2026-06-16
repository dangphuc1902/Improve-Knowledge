# SESSION 10 — Full Mock Interview Script

> **SRS Level**: Capstone | **Review**: Once per week
> **Format**: Full 60-minute interview simulation
> **Instructions**: Answer OUT LOUD. Record yourself. Review against model answers.

---

## 🎬 INTERVIEW START

**Interviewer**: "Good morning! Thanks for joining us today. I hope the connection is good on your end. Please make yourself comfortable. Can you start by introducing yourself?"

**[ANSWER HERE — 90 seconds]**

---
### ✅ Model Introduction
> "Good morning, thanks for having me! Yes, connection is great on my side.
>
> So, I'm Phuc — a Java Backend Engineer with about three years of experience, primarily in the fintech and telecom domains. My core stack is Spring Boot, Hibernate, and Oracle, and I've recently been working with microservices — Spring Cloud, Kafka, Docker.
>
> What drives me as an engineer is building systems that are reliable and maintainable at scale. I've had the chance to lead backend development on a few significant projects, including a digital loan processing system that handled thousands of transactions per day.
>
> I'm really excited about this opportunity at [Company] — particularly because of [specific reason]. I'm looking forward to our conversation."

---

## SECTION 1: HR QUESTIONS (15 min)

**Q1**: "Why are you looking to leave your current company?"
**[ANSWER — 60 seconds]**

---

**Q2**: "Tell me about a challenging technical problem you solved."
**[ANSWER — 2 minutes, use STAR]**

---

**Q3**: "Describe a time you disagreed with a colleague. How did you handle it?"
**[ANSWER — 2 minutes]**

---

## SECTION 2: CORE JAVA (15 min)

**Q4**: "What's the difference between HashMap and ConcurrentHashMap? When would you use each?"
**[ANSWER — 2 minutes]**

---

**Q5**: "Explain how Garbage Collection works. What's G1 GC?"
**[ANSWER — 2 minutes]**

---

**Q6**: "What is a race condition? Give me a real example and how you'd fix it."
**[ANSWER — 2 minutes]**

---

## SECTION 3: SPRING & HIBERNATE (15 min)

**Q7**: "Explain Spring's IoC container and the types of Dependency Injection."
**[ANSWER — 2 minutes]**

**Q8**: "What is the N+1 problem in Hibernate? How do you detect and fix it?"
**[ANSWER — 2 minutes]**

**Q9**: "How does `@Transactional` work? What happens with self-invocation?"
**[ANSWER — 2 minutes]**

---
### ✅ Model Answer for Q9 — @Transactional Self-Invocation
> "Great question — this is a common gotcha that catches a lot of developers.
>
> `@Transactional` works via Spring AOP. When you annotate a method with `@Transactional`, Spring creates a proxy around your bean. When external code calls your method, it goes through the proxy, which handles transaction management — begin, commit, rollback.
>
> The problem with self-invocation is: if `methodA()` calls `methodB()` within the same class, and `methodB()` is `@Transactional`, the call bypasses the proxy entirely because `methodA()` is calling `this.methodB()` — not `proxy.methodB()`. So the transaction on `methodB()` is never started.
>
> The fix options are: First, extract `methodB()` to a separate Spring bean so it's called through the proxy. Second, inject the bean into itself using `@Autowired` — though that's a bit unusual. Or third, use `AspectJ` mode for AOP instead of proxy-based.
>
> In practice, I refactor the logic into a separate service class — that's the cleanest solution."

---

## SECTION 4: SYSTEM DESIGN (10 min)

**Q10**: "Let's do a quick system design. I want you to design the backend for a URL shortener. Walk me through your approach."

**[ANSWER — 5 minutes, think aloud]**

---
### ✅ Model Thinking Structure
> "Sure, let me start by clarifying requirements.

> **Functional**: Users submit a long URL, get a short URL back. Short URL redirects to long URL. Maybe analytics — click count, location.

> **Non-functional**: I'd assume high read traffic (10:1 read/write ratio). Low redirect latency is critical — sub-10ms ideally. High availability.

> **Scale**: If we're talking 100M URLs and 1B redirects per day, that's about 12K redirects/second.

> **Core design**: I'd use a simple service with a REST API. `POST /urls` to create, `GET /{code}` to redirect.

> For the short code, I'd encode a unique ID in Base62 — that gives 7 characters for 62^7 possible URLs.

> **Storage**: A PostgreSQL table with short_code indexed. Most critical path is read — so I'd put a Redis cache in front. 90%+ redirects should be cache hits.

> **Redirect type**: I'd use 302 (temporary redirect) rather than 301 (permanent) because 301 gets cached by browsers and we'd lose analytics visibility.

> **Scaling**: CDN for global distribution, Redis cluster for cache, read replicas for DB.

> Should I go deeper on any part — the ID generation, the caching strategy, or the analytics?"

---

## SECTION 5: ENGLISH COMMUNICATION (5 min)

**Q11**: "A client is calling you, very upset. There's a bug in production causing transaction failures. How do you handle the call?"

**[ANSWER — 90 seconds]**

---
### ✅ Model Answer
> "First, I'd stay calm and let the client finish venting — they need to feel heard. I'd say something like: 'I completely understand your frustration, and I want you to know we're treating this with the highest priority.'

> Then I'd clarify impact: 'Can you tell me roughly how many users are affected and since when?' I'd give an immediate ETA for an update: 'We'll have a status update in 30 minutes.'

> I'd immediately loop in the team: start investigating logs, check recent deployments.

> Throughout the incident, I'd send a brief update every 30 minutes — even if just to say 'Investigation ongoing, expected resolution by X.' No communication is worse than bad news.

> After resolution, I'd send a concise post-mortem: what happened, what we fixed, what we're doing to prevent recurrence. And I'd follow up personally with the client."

---

## SECTION 6: CLOSING (5 min)

**Q12**: "We're almost at the end. Do you have any questions for us?"

**[Ask 2-3 prepared questions]**

---

**Q13**: "Is there anything you'd like to add before we wrap up?"

---
### ✅ Strong Closing
> "Yes, actually. I want to say that I've genuinely enjoyed this conversation — not just as an interview, but as a technical discussion. The questions you asked touched on things I care deeply about.

> I want to reiterate that I'm very motivated about this role. The combination of the technical challenge and the team's culture as you've described it is exactly what I'm looking for in my next step.

> I'm confident I can contribute quickly, and I'm excited about the opportunity to grow here. Thank you for your time today."

---

## 📊 Self-Evaluation Sheet

After completing the mock, rate yourself 1-5:

| Area | Score (1-5) | Notes |
|---|---|---|
| Opening confidence | | |
| Answer structure (STAR) | | |
| Technical accuracy | | |
| English fluency | | |
| Grammar errors | | |
| Use of filler phrases | | |
| Thinking out loud | | |
| Questions asked | | |
| Closing impression | | |
| **Overall** | | |

**Target score for real interview readiness**: Average 4+

---

## 🔄 SRS Review Triggers

- Score 5/5 on a card → Next review in 14 days
- Score 4/5 → Next review in 7 days
- Score 3/5 → Next review in 3 days
- Score 1-2/5 → Review tomorrow

---

*Congratulations on completing all 10 sessions! Return to [README](./README.md) to track your progress.*
