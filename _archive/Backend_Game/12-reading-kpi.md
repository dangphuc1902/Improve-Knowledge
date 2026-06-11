# 📚 TÀI LIỆU & KPI ĐÁNH GIÁ LEVEL

---

## I. TÀI LIỆU PHẢI ĐỌC — Theo Phase

### Phase 1: Foundation (Month 1-2)

| # | Tài liệu | Chương/Phần | Mục tiêu |
|---|----------|-------------|----------|
| 1 | **C++ Concurrency in Action** (Anthony Williams) | Ch 1-7 | Memory model, atomics, thread pool |
| 2 | **Designing Data-Intensive Applications** (Kleppmann) | Ch 2-3 | Data models, storage engines |
| 3 | **Designing Data-Intensive Applications** | Ch 7 | Transactions |
| 4 | **High Performance Browser Networking** (Grigorik) | Ch 1-4 | TCP/UDP/TLS fundamentals |
| 5 | [Beej's Guide to Network Programming](https://beej.us/guide/bgnet/) | All | Socket programming |
| 6 | [Preshing on Programming](https://preshing.com/archives/) | Memory ordering series | Visual memory model |

### Phase 2: Distributed (Month 3-4)

| # | Tài liệu | Chương/Phần | Mục tiêu |
|---|----------|-------------|----------|
| 7 | **DDIA** | Ch 5 | Replication |
| 8 | **DDIA** | Ch 8-9 | Distributed system challenges, consistency |
| 9 | **System Design Interview Vol 1** (Alex Xu) | Ch 1-5 | Framework + basic designs |
| 10 | [Redis documentation](https://redis.io/docs/) | Data types, persistence | Practical Redis |
| 11 | [Martin Kleppmann lectures](https://youtube.com/playlist?list=PLeKd45zvjcDFUEv_ohr_HdUFe97RItdiB) | All 8 lectures | Distributed systems visual |

### Phase 3: Production (Month 5)

| # | Tài liệu | Chương/Phần | Mục tiêu |
|---|----------|-------------|----------|
| 12 | **DDIA** | Ch 4 | Encoding & evolution |
| 13 | [Prometheus docs](https://prometheus.io/docs/) | Getting started | Metrics |
| 14 | [Docker docs](https://docs.docker.com/get-started/) | Multi-stage, compose | Containerization |
| 15 | [The Twelve-Factor App](https://12factor.net/) | All 12 factors | Production mindset |

### Phase 4: System Design (Month 6)

| # | Tài liệu | Chương/Phần | Mục tiêu |
|---|----------|-------------|----------|
| 16 | **System Design Interview Vol 1** | Ch 6-13 | Specific system designs |
| 17 | **System Design Interview Vol 2** (Alex Xu) | Selected chapters | Advanced designs |
| 18 | [ByteByteGo newsletter](https://blog.bytebytego.com/) | Weekly | Stay current |

### Thứ tự đọc tối ưu

```
Week 1-3:   C++ Concurrency in Action (Ch 1-7) + Preshing blog
Week 4-5:   High Performance Browser Networking (Ch 1-4) + Beej's Guide
Week 6-8:   DDIA (Ch 2-3, 7) — database fundamentals
Week 9-12:  DDIA (Ch 5, 8-9) — distributed systems
Week 13-15: System Design Interview Vol 1 (Ch 1-8)
Week 16-17: Redis docs + Prometheus docs + Docker docs
Week 18-20: System Design Interview Vol 1 (Ch 9-13) + Vol 2
Week 21-24: Practice + Review + Mock interviews
```

---

## II. KPI ĐÁNH GIÁ LEVEL

### 🏁 PRE-ASSESSMENT TEST (Làm TRƯỚC khi bắt đầu)

Trả lời mỗi câu trong 3 phút (viết hoặc nói). Ghi điểm 0-2:
- 0 = Không biết
- 1 = Biết sơ
- 2 = Giải thích rõ ràng

| # | Câu hỏi | Score |
|---|---------|-------|
| 1 | `std::memory_order_acquire` khác gì `relaxed`? | /2 |
| 2 | Deadlock xảy ra khi nào? Cách phòng tránh? | /2 |
| 3 | TCP vs UDP — khi nào dùng gì cho game? | /2 |
| 4 | ACID là gì? Isolation level nào phổ biến? | /2 |
| 5 | CAP theorem giải thích bằng ví dụ? | /2 |
| 6 | Redis dùng cho gì trong game server? | /2 |
| 7 | Docker container khác VM ở đâu? | /2 |
| 8 | Prometheus thu thập metrics như thế nào? | /2 |
| 9 | Design chat system — high level approach? | /2 |
| 10 | Code review — bạn check những gì? | /2 |
| | **Total** | /20 |

**Đánh giá:**
- 0-6: Junior rõ → package này rất cần
- 7-12: Junior mạnh → có base, cần structured learning
- 13-16: Gần Middle → focus vào gaps
- 17-20: Có thể đã Middle → focus system design + production

---

### 📊 POST-PHASE TESTS

#### Test sau Phase 1 (Month 2)

| # | Task | Pass Criteria |
|---|------|--------------|
| 1 | Implement thread pool, benchmark > 1M tasks/sec | Code + benchmark results |
| 2 | Explain 6 memory orders (recorded) | 5-min explanation, no notes |
| 3 | Build TCP server handle 5K connections | Running code + benchmark QPS |
| 4 | Reproduce deadlock + fix | SQL scripts + explanation |
| 5 | Design: "How would you build game event queue?" | 10-min whiteboard |
| | **Pass: 4/5** | |

#### Test sau Phase 2 (Month 4)

| # | Task | Pass Criteria |
|---|------|--------------|
| 1 | Multi-service (3 services) running 2 instances each | Docker compose up, working |
| 2 | Idempotent transaction (no duplicate gold) | Test script passes |
| 3 | Explain CAP theorem + game trade-offs | 5-min explanation |
| 4 | Redis: leaderboard + cache-aside working | Demo |
| 5 | Event system: 10K events, 0 duplicates | Test results |
| | **Pass: 4/5** | |

#### Test sau Phase 3 (Month 5)

| # | Task | Pass Criteria |
|---|------|--------------|
| 1 | CafePho running in Docker Compose (5+ containers) | `docker compose up` works |
| 2 | Grafana dashboard with 5+ panels | Screenshot |
| 3 | Alert fires when error rate > 1% | Demo |
| 4 | CI/CD pipeline builds and deploys | GitHub Actions log |
| 5 | Load test 1K concurrent, P99 < 200ms | k6 results |
| | **Pass: 4/5** | |

#### Test sau Phase 4 (Month 6)

| # | Task | Pass Criteria |
|---|------|--------------|
| 1 | Design chat system in 35 minutes | Self-recorded video |
| 2 | Design game matchmaking with follow-ups | Self-recorded video |
| 3 | Design leaderboard service | Written design doc |
| 4 | Mock interview (ask someone or self-record) | Pass 2/3 designs |
| 5 | Re-take pre-assessment → score ≥ 16/20 | Score sheet |
| | **Pass: 4/5** | |

---

### ⭐ CHUẨN XÁC ĐỊNH ĐẠT MIDDLE

Bạn **đạt Middle** khi:

| Criteria | Requirement |
|----------|-------------|
| **Pre-assessment re-test** | ≥ 16/20 |
| **Phase tests** | Pass all 4 phases (4/5 mỗi phase) |
| **Checklist** | ≥ 15 items 🟢 trong [09-checklist.md](file:///d:/WorkSpace/Improve%20Knowledge/09-checklist.md) |
| **Projects** | ≥ 5 completed projects với benchmarks |
| **System design** | Design 4/6 systems end-to-end |
| **Code review** | Successfully review 5+ PRs (real or practice) |
| **Production debug** | Solve 3+ simulated production issues |

> [!IMPORTANT]
> Nếu bạn đạt tất cả criteria trên, bạn **confidently** ở level Middle Software Engineer. Có thể apply và pass interview cho Middle positions.
