# 📊 PHASE 3.1 — Observability

> **Timeline**: Month 5, tuần 1-2  
> **Mục tiêu**: Monitor, debug, và alert production game server  
> **Mindset**: "If you can't measure it, you can't improve it"

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### Three Pillars of Observability

```
  ┌──────────┐    ┌──────────┐    ┌──────────┐
  │  LOGS    │    │ METRICS  │    │ TRACES   │
  │          │    │          │    │          │
  │ What     │    │ How many │    │ Where    │
  │ happened │    │ How fast │    │ it went  │
  └──────────┘    └──────────┘    └──────────┘
  
  Tool: ELK/Loki    Prometheus     Jaeger/Zipkin
  View: Grafana      Grafana        Jaeger UI
```

### 1. Structured Logging

```cpp
// ❌ BAD: Unstructured
log("Player 123 bought item sword for 100 gold");

// ✅ GOOD: Structured (JSON)
log({
    "level": "info",
    "event": "item_purchase",
    "player_id": 123,
    "item": "sword",
    "price": 100,
    "currency": "gold",
    "timestamp": "2025-01-15T10:30:00Z",
    "trace_id": "abc-123-def",
    "latency_ms": 15
});
// → Searchable, filterable, aggregatable
```

**Log levels cho game server:**
- `ERROR`: Player mất data, transaction fail, crash
- `WARN`: Latency spike >100ms, queue near capacity  
- `INFO`: Player join/leave, purchase, level up
- `DEBUG`: State transitions, cache hit/miss (production: OFF)

### 2. Metrics (Prometheus)

**4 Metric Types:**

| Type | Description | Game Example |
|------|-------------|-------------|
| **Counter** | Only goes up | Total requests, errors, purchases |
| **Gauge** | Can go up/down | Online players, room count, queue depth |
| **Histogram** | Distribution | Request latency, response size |
| **Summary** | Like histogram, client-side percentiles | Latency percentiles |

**Key metrics for game server:**
```
# Business metrics
game_online_players{server="room-1"}                    gauge
game_rooms_active{type="cafe"}                          gauge
game_transactions_total{type="purchase",status="ok"}    counter
game_gold_economy_balance                               gauge

# Technical metrics
http_request_duration_seconds{method="POST",path="/buy"} histogram
grpc_server_handled_total{method="JoinRoom",code="OK"}   counter
process_resident_memory_bytes                            gauge
go_goroutines (hoặc thread count)                        gauge
```

### 3. Grafana Dashboard Design

**Game Server Dashboard Template:**
```
Row 1: Business Overview
  [Online Players] [Active Rooms] [Revenue/hour] [Error Rate]

Row 2: Performance  
  [Request Latency P50/P95/P99] [QPS] [Error Rate %]

Row 3: Infrastructure
  [CPU %] [Memory MB] [Network I/O] [Disk I/O]

Row 4: Game-Specific
  [Matchmaking Wait Time] [Room Fill Rate] [Economy Flow]
```

### 4. Tracing (Jaeger concept)

```
Request: Player buys item
  ├── [API Gateway]          2ms
  │   ├── [Auth Service]     5ms
  │   ├── [Economy Service]  15ms
  │   │   ├── [DB: Check Gold]   3ms
  │   │   ├── [DB: Deduct Gold]  4ms
  │   │   └── [DB: Add Item]     3ms
  │   └── [Event Publish]    2ms
  └── Total: 31ms

→ If slow: trace shows WHICH service/DB call is the bottleneck
```

### 5. Alert Strategy

| Severity | Condition | Action | Example |
|----------|-----------|--------|---------|
| **P0 Critical** | Service down | Page on-call | All rooms disconnected |
| **P1 High** | Error rate > 5% | Notify Slack | Economy transactions failing |
| **P2 Medium** | Latency P99 > 500ms | Ticket | Matchmaking slow |
| **P3 Low** | Disk > 80% | Next sprint | Log storage filling |

**Alert rules:**
- Alert on **symptoms**, not causes (high latency, not "CPU high")
- Include **runbook link** in alert
- **Test alerts** regularly

---

## 🔨 PROJECT: Instrument CafePho

1. **Add structured logging** to all services
2. **Add Prometheus metrics**: latency per room, transaction count, error rate
3. **Build Grafana dashboard** with business + technical panels
4. **Detect anomaly**: unusual gold economy flow (inflation/deflation)
5. **Alert**: set up alert for error rate > 1%

### Docker Compose Stack:
```yaml
services:
  prometheus:
    image: prom/prometheus:latest
    ports: ["9090:9090"]
  grafana:
    image: grafana/grafana:latest
    ports: ["3000:3000"]
  # jaeger (optional)
  jaeger:
    image: jaegertracing/all-in-one:latest
    ports: ["16686:16686"]
```

---

## ✅ CHECKLIST

- [ ] Implement structured logging (JSON format)
- [ ] Add Prometheus metrics (4 types)
- [ ] Build Grafana dashboard
- [ ] Understand distributed tracing concept
- [ ] Design alert strategy (P0-P3)
- [ ] Detect economy anomaly via metrics
- [ ] Log latency per room operation

## 🎯 MILESTONE: Dashboard shows real-time metrics, alerts fire correctly
