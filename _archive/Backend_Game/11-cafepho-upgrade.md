# 🚀 CAFEPHO UPGRADE PLAN

> **Mục tiêu**: Nâng cấp CafePho từ monolith → multi-service production-ready platform  
> **Thời gian**: Song song với Phase 2-3 của roadmap

---

## 🏗️ Architecture Diagram

```
                          ┌───────────────┐
                          │   Nginx LB    │
                          │ (port 80/443) │
                          └───────┬───────┘
                                  │
                    ┌─────────────┼─────────────┐
                    ▼             ▼              ▼
             ┌──────────┐ ┌──────────┐  ┌──────────┐
             │  API     │ │  Game    │  │  Admin   │
             │ Gateway  │ │ WebSocket│  │  Panel   │
             │ (REST)   │ │ Server   │  │ (HTTP)   │
             └────┬─────┘ └────┬─────┘  └────┬─────┘
                  │            │              │
        ┌─────────┼────────────┼──────────────┘
        │         │            │
   ┌────▼───┐ ┌──▼─────┐ ┌───▼──────┐ ┌──────────┐
   │  Auth  │ │  Room  │ │ Economy  │ │ Social   │
   │Service │ │Service │ │ Service  │ │ Service  │
   │(JWT)   │ │        │ │          │ │(chat)    │
   └───┬────┘ └───┬────┘ └───┬──────┘ └───┬──────┘
       │          │           │            │
       │     ┌────┴───────────┴────────────┘
       │     │
  ┌────▼─────▼────┐  ┌──────────┐  ┌──────────┐
  │    Redis      │  │ Postgres │  │  Kafka   │
  │  - Session    │  │ - Players│  │ - Events │
  │  - Cache      │  │ - Items  │  │ - Audit  │
  │  - Leaderboard│  │ - Txns   │  │ - Notif  │
  └───────────────┘  └──────────┘  └──────────┘
       │
  ┌────▼──────────────────────────────────────┐
  │           Observability Stack              │
  │  Prometheus → Grafana → Alerts            │
  │  Structured Logs → Loki                   │
  └───────────────────────────────────────────┘
```

---

## 📋 Upgrade Steps

### Step 1: Service Extraction
| Service | Responsibility | Port |
|---------|---------------|------|
| **Auth Service** | Register, login, JWT issue/verify | 8001 |
| **Room Service** | Create/join/leave room, state sync | 8002 |
| **Economy Service** | Gold, items, transactions | 8003 |
| **Social Service** | Chat, friends, notifications | 8004 |

### Step 2: Add Redis
- Session store (JWT blacklist, online status)
- Cache (player profile, room list)
- Leaderboard (SORTED SET)
- Distributed lock (Redlock for economy)
- Pub/Sub (cross-service events)

### Step 3: Add Kafka
- Event bus: `player.joined`, `item.purchased`, `gold.changed`
- Audit log: immutable record of all economy transactions
- Analytics pipeline: player behavior tracking
- Async processing: notification delivery

### Step 4: Auth JWT
```
Login Flow:
  Client → POST /auth/login {username, password}
  Auth Service → validate → issue JWT {player_id, role, exp}
  Client → sends JWT in Authorization header for all requests
  
  Services verify JWT locally (shared secret or public key)
  Token refresh: /auth/refresh with refresh_token
  Logout: blacklist JWT in Redis
```

### Step 5: Docker Compose
```yaml
# docker-compose.yml — Full stack
services:
  auth-service:    { build: ./auth,    ports: ["8001:8001"], depends_on: [redis, postgres] }
  room-service:    { build: ./room,    ports: ["8002:8002"], depends_on: [redis] }
  economy-service: { build: ./economy, ports: ["8003:8003"], depends_on: [redis, postgres, kafka] }
  social-service:  { build: ./social,  ports: ["8004:8004"], depends_on: [redis, kafka] }
  nginx:           { image: nginx, ports: ["80:80"], depends_on: [auth-service, room-service] }
  redis:           { image: redis:7-alpine, ports: ["6379:6379"] }
  postgres:        { image: postgres:15, environment: { POSTGRES_DB: cafepho } }
  kafka:           { image: confluentinc/cp-kafka:7.5.0, depends_on: [zookeeper] }
  zookeeper:       { image: confluentinc/cp-zookeeper:7.5.0 }
  prometheus:      { image: prom/prometheus }
  grafana:         { image: grafana/grafana, ports: ["3000:3000"] }
```

### Step 6: Metrics Dashboard
- Online players (gauge) → Grafana panel
- QPS per service (counter) → rate() graph
- Latency P50/P99 (histogram) → heatmap
- Economy balance (custom) → trend line
- Error rate (counter) → alert > 1%

### Step 7: Load Test
```bash
# k6 load test script
k6 run --vus 1000 --duration 60s load_test.js

# Targets:
#   - 1K concurrent connections maintained
#   - P99 latency < 200ms  
#   - 0 errors under normal load
#   - Identify breaking point (increase VUs until errors)
```

---

## ⚠️ Bottleneck Dự Kiến

| Bottleneck | Cause | Fix |
|-----------|-------|-----|
| Redis single-thread | Tất cả requests đi qua 1 Redis | Redis Cluster hoặc multiple instances |
| Postgres connections | Max connections exhausted | Connection pooling (PgBouncer) |
| Kafka consumer lag | Slow consumer | Add consumer instances, increase partitions |
| Room service memory | In-memory room state | Limit rooms per instance, horizontal scale |
| Auth JWT validation | CPU-bound verification | Cache verified tokens in Redis |

---

## ✅ Completion Checklist

- [ ] 4 services extracted và communicate qua REST/gRPC
- [ ] Redis integrated (session, cache, leaderboard)
- [ ] Kafka integrated (event bus, audit log)
- [ ] JWT auth flow working (login, verify, refresh, logout)
- [ ] Docker Compose: all services + infra running
- [ ] Nginx reverse proxy + load balance
- [ ] Prometheus metrics exposed
- [ ] Grafana dashboard with 5+ panels
- [ ] Load test: 1K concurrent, P99 < 200ms
- [ ] Zero data loss under normal operation
- [ ] Graceful shutdown all services
- [ ] README with setup instructions
