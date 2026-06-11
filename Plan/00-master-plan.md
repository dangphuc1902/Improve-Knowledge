# 🎯 Master Plan — Backend Developer Interview Prep (Product Companies HCM)

> **Bắt đầu:** 16/06/2026 | **Kết thúc target:** 07/09/2026 (12 tuần)  
> **Cam kết:** 3-4h/ngày (T2-T6) + 6-8h (T7-CN)

---

## 📊 Daily Time Blocks (Thứ 2 - Thứ 6)

```
┌───────────────────────────────────────────────────────────────────────┐
│ 19:00 - 19:30  │ 🇬🇧 English             │ 10-Interview-Prep/       │
│ 19:30 - 21:00  │ 📚 DSA Theory+Practice  │ 01-DSA/                  │
│ 21:00 - 22:00  │ 💻 LeetCode Coding      │ 01-DSA/ + Plan/leetcode  │
│ 22:00 - 22:30  │ 📝 Java/Spring/Review   │ 02-Java-Core/ → 03-Spring│
└───────────────────────────────────────────────────────────────────────┘
```

## 📊 Weekend Schedule (Thứ 7 + Chủ Nhật)

```
Thứ 7:
┌───────────────────────────────────────────────────────────────────────┐
│ 08:00 - 09:00  │ 🇬🇧 English (video)     │ 10-Interview-Prep/       │
│ 09:00 - 11:00  │ 💻 LeetCode Marathon    │ 01-DSA/                  │
│ 11:00 - 12:00  │ 📚 Java / Spring Deep   │ 02-Java-Core/ 03-Spring/ │
│ 13:30 - 15:30  │ 📚 Deep Topics          │ 04~08 folders (by phase) │
│ 15:30 - 17:00  │ 📝 CV / Portfolio       │ Plan/                    │
│ 17:00 - 17:30  │ 📝 Weekly Review        │ Plan/weekly-tracker.md   │
└───────────────────────────────────────────────────────────────────────┘

Chủ Nhật:
┌───────────────────────────────────────────────────────────────────────┐
│ 09:00 - 11:00  │ 💻 LeetCode Review      │ 01-DSA/                  │
│ 14:00 - 16:00  │ 📖 Reading (DDIA)       │ External books           │
│ 16:00 - 17:00  │ 🇬🇧 English Writing     │ 10-Interview-Prep/       │
│ 17:00+         │ 🧘 Rest & Recharge      │                          │
└───────────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ 4 Phases Overview

| Phase | Tuần | Focus | Deep Topics (T7 13:30-15:30) | Exit Criteria |
|:------|:-----|:------|:-----------------------------|:--------------|
| **1. Foundation** | 1-3 | DSA basics (40 bài), Java Core, SQL | `04-Database/` SQL & PostgreSQL | 2 Medium/45 phút |
| **2. Deep Dive** | 4-6 | DSA adv (90 bài), Cloud, STAR | `06-Distributed/` `07-Cloud/` | Medium < 25 phút |
| **3. System Design** | 7-9 | 10 SD problems, Mock, apply | `05-System-Design/` | Design trong 45 phút |
| **4. Sprint** | 10-12 | Apply 15 cty, interview | `10-Interview-Prep/` review all | ≥ 1 offer |

---

## 📂 Workspace Structure (NEW)

```
Improve-Knowledge/
│
├── 📋 Plan/                             ← COMMAND CENTER
│   ├── 00-master-plan.md                ← ★ File này
│   ├── weekly-tracker.md                ← Track tiến độ tuần
│   ├── leetcode-tracker.md              ← Track LeetCode (95 bài + links)
│   └── company-research.md              ← 11 công ty target
│
├── 📚 01-DSA/                           ← ⏰ 19:30-22:00 (Phase 1-2)
│   ├── 00-overview.md                   │  NeetCode Roadmap
│   └── 01~18 topic folders              │  theory.md + Solutions.java
│
├── 📚 02-Java-Core/                     ← ⏰ 22:00-22:30 (Phase 1)
│   ├── Theory.md                        │  OOP, Collections (from Backend-Java-Developer)
│   ├── Interview-QA.md                  │  Java interview questions
│   ├── Practice-Exercises.md            │  Coding exercises
│   └── 07-java17-21-features.md         │  🆕 Records, Virtual Threads, etc.
│
├── 📚 03-Spring-Ecosystem/              ← ⏰ 22:00-22:30 + T7 11:00 (Phase 1-2)
│   ├── Theory.md                        │  Spring Boot internals (84KB)
│   ├── Interview-QA.md                  │  Spring interview questions
│   ├── 02-spring-security-deep.md       │  🆕 JWT, OAuth2, RBAC/ABAC
│   ├── 04-spring-cloud.md              │  🆕 Gateway, Eureka, Config, Feign
│   └── 05-spring-testing.md            │  🆕 JUnit5, Mockito, TestContainers
│
├── 📚 04-Database/                      ← ⏰ T7 13:30-15:30 (Phase 1)
│   ├── indexes.md                       │  B-tree, Hash, Covering index
│   ├── joins.md                         │  JOIN types, performance
│   ├── transactions.md                  │  ACID, Isolation levels, MVCC
│   ├── 02-postgresql-advanced.md        │  🆕 Window func, CTE, JSONB, Partition
│   ├── redis.md + redis-production.md   │  Redis data structures + production
│   └── mongodb.md                       │  MongoDB basics
│
├── 📚 05-System-Design/                 ← ⏰ T7 13:30-15:30 (Phase 3)
│   ├── 00-problems-overview.md          │  10 problems (URL, Rate Limiter, Chat...)
│   └── system-design-methodology.md     │  RESHADED framework
│
├── 📚 06-Distributed-Systems/           ← ⏰ T7 13:30-15:30 (Phase 2)
│   ├── cap-consistency-models.md        │  CAP, consistency models
│   ├── cache-and-mq.md                  │  Redis cache + Kafka + RabbitMQ
│   ├── kafka-deep-dive-game.md          │  Kafka internals (game context)
│   ├── 02-distributed-transactions-resilience.md │ 🆕 Saga, Outbox, Circuit Breaker
│   ├── distributed_systems.md           │  Distributed fundamentals
│   ├── horizontal_scaling.md            │  Scaling strategies
│   └── sharding.md                      │  Database sharding
│
├── 📚 07-Cloud-DevOps/                  ← ⏰ T7 13:30-15:30 (Phase 2)
│   ├── 01-aws-core-services.md          │  🆕 EC2, S3, RDS, Lambda, SQS/SNS
│   ├── ci_cd.md                         │  CI/CD pipelines
│   └── docker/                          │  Docker fundamentals
│
├── 📚 08-Networking-Security/           ← ⏰ Review material (Phase 1)
│   ├── http.md, dns.md, tcp_ip.md       │  Internet fundamentals
│   ├── rest.md, grpc.md, graphql.md     │  API Design
│   ├── jwt.md, oauth.md, session.md     │  Authentication
│   └── api_examples/, auth_examples/    │  Code examples
│
├── 📚 09-Design-Patterns/              ← ⏰ Review material (Phase 1-2)
│   ├── 01-solid-clean-architecture.md   │  🆕 SOLID + Hexagonal Architecture
│   ├── Theory.md                        │  GoF Design Patterns
│   ├── Interview-QA.md                  │  Pattern interview Q&A
│   └── Practice-Exercises.md            │  Pattern exercises
│
├── 🎤 10-Interview-Prep/               ← ⏰ Daily English + Phase 3-4
│   ├── english-practice.md              │  Self-intro scripts, phrases, vocab
│   ├── star-stories.md                  │  6 STAR stories (VN + EN)
│   ├── behavioral-common-qa.md          │  🆕 Top 30 behavioral questions
│   └── mock-interview-guide.md          │  Mock interview format
│
├── 📄 DangTrongPhuc_Backend Developer CV.pdf
│
└── 📁 _archive/                         ← Old materials (reference only)
    ├── Backend-Java-Developer/
    ├── Backend_Game/
    ├── Doc-Practice/
    ├── Impove_Interview/
    ├── DSA/
    └── backend-engineer-roadmap/
```

---

## 📅 Phase-Folder Mapping (Chi tiết tuần nào học folder nào)

### Phase 1: Foundation (Tuần 1-3)
| Tuần | Weekday 22:00 | Weekend Deep (T7 13:30) | Weekend Java/Spring (T7 11:00) |
|:-----|:-------------|:------------------------|:-------------------------------|
| 1 | `02-Java-Core/` OOP + Collections | `04-Database/` indexes + joins | `02-Java-Core/` Concurrency |
| 2 | `02-Java-Core/` Streams + Generics | `04-Database/` transactions + PostgreSQL | `03-Spring/` Boot internals |
| 3 | `08-Networking/` HTTP, REST, JWT | `04-Database/` PostgreSQL advanced | `09-Design-Patterns/` SOLID |

### Phase 2: Deep Dive (Tuần 4-6)
| Tuần | Weekday 22:00 | Weekend Deep (T7 13:30) | Weekend Java/Spring (T7 11:00) |
|:-----|:-------------|:------------------------|:-------------------------------|
| 4 | `03-Spring/` Security + Testing | `06-Distributed/` CAP + Kafka deep | `03-Spring/` Spring Cloud |
| 5 | `02-Java-Core/` Java 17/21 | `06-Distributed/` Saga + Resilience | `03-Spring/` Testing |
| 6 | `09-Design-Patterns/` Clean Arch | `07-Cloud-DevOps/` AWS + K8s | Review all + STAR stories |

### Phase 3: System Design (Tuần 7-9)
| Tuần | Weekday 22:00 | Weekend Deep (T7 13:30) | Weekend (T7 11:00) |
|:-----|:-------------|:------------------------|:--------------------|
| 7 | Review weak topics | `05-System-Design/` #1-5 | Mock coding |
| 8 | Review weak topics | `05-System-Design/` #6-10 | Start apply |
| 9 | Company research | Full mock interviews | Mock + apply |

### Phase 4: Interview Sprint (Tuần 10-12)
| Focus | Folders | Action |
|:------|:--------|:-------|
| Daily maintenance | `01-DSA/` 1-2 bài | Giữ form |
| Interview prep | `10-Interview-Prep/` | STAR + English |
| Company-specific | `Plan/company-research.md` | Research trước PV |
| Apply + Interview | ALL review | 10-15 companies |

---

## ✅ Quick Reference — Resources

| Category | Resource | Priority |
|:---------|:---------|:---------|
| DSA | NeetCode 150 (neetcode.io) | 🔴 |
| DSA | LeetCode (leetcode.com) | 🔴 |
| System Design | "DDIA" - Martin Kleppmann | 🔴 |
| System Design | ByteByteGo (Alex Xu) | 🔴 |
| System Design | Gaurav Sen YouTube | 🟡 |
| Java | "Java Concurrency in Practice" | 🟡 |
| Spring | Baeldung.com | 🟡 |
| English | Tech podcasts + Shadowing | 🟡 |
| Job Board | ITviec, VietnamDevs, LinkedIn | 🟡 |

---

## 🏢 Target Companies

### Tier 1 (Lương $2,000-4,000+ USD)
1. MoMo — Fintech, Java/Kotlin/Go
2. VNPay — Payment, Java/Spring
3. Tiki — E-commerce, Go/Java
4. NAB Innovation — Banking, Java/AWS
5. One Mount — VinID ecosystem, Go/Java
6. Money Forward — Fintech (JP), Ruby/Go/Java

### Tier 2 (Lương $1,500-3,000 USD)
7. Teko — E-commerce infra, Go/Java
8. ZaloPay — Digital wallet, Java/Go
9. TymeX — Digital banking, Java/AWS
10. KMS Technology — Product+Consulting, Java
11. Anfin — Investment, Java/Spring
