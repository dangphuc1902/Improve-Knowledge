# 📬 PHASE 2.2 — Cache & Message Queue

> **Timeline**: Month 4, tuần 1-3  
> **Mục tiêu**: Master caching strategies và async event-driven architecture  
> **Tài liệu**: Redis documentation, Kafka: The Definitive Guide

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### 1. Redis Internals

**Data Structures & Game Use Cases:**

| Structure | Redis Type | Game Use Case |
|-----------|-----------|---------------|
| Player session | `STRING` + TTL | Session cache, JWT token |
| Online players | `SET` | Who's online, room members |
| Leaderboard | `SORTED SET` | Top players by score |
| Player state | `HASH` | Per-player data (gold, level, position) |
| Recent chat | `LIST` (capped) | Last 100 messages per room |
| Rate limiting | `STRING` + `INCR` + TTL | Anti-spam, API rate limit |
| Pub/Sub | `PUBLISH/SUBSCRIBE` | Real-time notifications |
| Distributed lock | `SET NX EX` (Redlock) | Prevent double-buy |

**Single-threaded model**: Redis xử lý commands sequentially → NO race conditions giữa commands. Nhưng Lua scripts block toàn bộ server → keep scripts short.

### 2. Cache Invalidation Strategies

| Strategy | How | Consistency | Performance | Game Use |
|----------|-----|-------------|-------------|----------|
| **Write-through** | Write cache + DB cùng lúc | Strong | Slower write | Gold balance |
| **Write-behind** | Write cache, async DB | Eventual | Fast write | Position updates |
| **Cache-aside** | App reads cache first, miss → read DB → set cache | Eventual | Balanced | Player profile |
| **TTL-based** | Cache tự expire | Eventual | Simple | Leaderboard |

**Cache-aside pattern:**
```cpp
Player* getPlayer(int id) {
    // 1. Check cache
    auto cached = redis.get("player:" + id);
    if (cached) return deserialize(cached);
    
    // 2. Cache miss → DB
    auto player = db.query("SELECT * FROM players WHERE id=?", id);
    
    // 3. Set cache với TTL
    redis.setex("player:" + id, 300, serialize(player));  // 5min TTL
    return player;
}

void updatePlayer(int id, PlayerData data) {
    db.update("UPDATE players SET ... WHERE id=?", id, data);
    redis.del("player:" + id);  // Invalidate cache
    // KHÔNG set cache ở đây → tránh race condition
}
```

> [!CAUTION]
> **Race condition trong cache**: Thread A miss cache → đọc DB (old data). Thread B update DB → delete cache. Thread A set cache (OLD data). Fix: dùng version number hoặc CAS.

### 3. Kafka vs RabbitMQ

| Feature | Kafka | RabbitMQ |
|---------|-------|----------|
| Model | Distributed log | Message broker |
| Ordering | Per-partition | Per-queue |
| Replay | ✅ Replay messages | ❌ Once consumed, gone |
| Throughput | 100K+ msg/sec | 10K msg/sec |
| Game use | Event sourcing, analytics | Task queue, RPC |

### 4. At-Least-Once vs Exactly-Once

| Delivery | Mechanism | Risk | Game Impact |
|----------|-----------|------|-------------|
| At-most-once | Fire and forget | Message lost | Player misses reward |
| At-least-once | ACK + retry | Duplicate processing | Player gets reward 2x |
| Exactly-once | Idempotent consumer | Complex | Correct behavior |

**Idempotent consumer pattern:**
```cpp
void handleGoldReward(const Event& event) {
    string dedup_key = "processed:" + event.id;
    
    // SETNX = SET if Not eXists
    bool is_new = redis.setnx(dedup_key, "1");
    if (!is_new) {
        log("Duplicate event {}, skipping", event.id);
        return;
    }
    redis.expire(dedup_key, 86400);  // 24h dedup window
    
    // Process (only happens once per event)
    db.exec("UPDATE players SET gold = gold + ? WHERE id = ?",
            event.amount, event.player_id);
}
```

---

## 🔨 PROJECT: Async Event-Driven Architecture

### Build:
1. **Event Bus** (Redis Streams or simple pub/sub)
2. **Event Producer** → publish: player_joined, item_purchased, gold_changed
3. **Multiple Consumers**: 
   - Leaderboard updater
   - Analytics aggregator
   - Notification sender
4. **Idempotent processing** → handle duplicate events
5. **Dead letter queue** → failed events go here for manual review

### Test Scenarios:
- Send 10K events, verify all processed exactly once
- Kill consumer mid-processing, restart, verify no duplicates
- Simulate network partition, verify recovery

---

## ✅ CHECKLIST

- [ ] Sử dụng Redis data structures cho game scenarios
- [ ] Implement cache-aside pattern
- [ ] Handle cache invalidation race conditions
- [ ] So sánh Kafka vs RabbitMQ
- [ ] Implement idempotent consumer
- [ ] Build event-driven architecture
- [ ] Handle message duplication
- [ ] Design dead letter queue

## 🎯 MILESTONE: Event system xử lý 10K events, 0 duplicates, survive consumer restart
