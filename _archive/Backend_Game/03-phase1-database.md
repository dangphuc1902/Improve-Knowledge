# 🗄️ PHASE 1.3 — Database Internals

> **Timeline**: Month 2, tuần 1-3  
> **Mục tiêu**: Hiểu database từ storage engine level, optimize cho game data  
> **Tài liệu**: *Designing Data-Intensive Applications* Ch 2-3, 7

---

## 📖 LÝ THUYẾT CÔ ĐỌNG

### 1. ACID

| Property | Ý nghĩa | Game Example |
|----------|---------|--------------|
| **Atomicity** | All-or-nothing | Chuyển gold: trừ A VÀ cộng B, không có giữa chừng |
| **Consistency** | Data hợp lệ sau transaction | Gold không thể âm |
| **Isolation** | Transactions không thấy nhau giữa chừng | 2 người cùng mua item, stock = 1 |
| **Durability** | Commit rồi = không mất | Server crash → gold vẫn còn |

### 2. Isolation Levels

```
Level tăng dần (strict hơn, chậm hơn):

READ UNCOMMITTED  → Dirty read ✅ (thấy data chưa commit)
READ COMMITTED    → Dirty read ❌, Non-repeatable ✅
REPEATABLE READ   → Non-repeatable ❌, Phantom ✅ (MySQL default)
SERIALIZABLE      → Full isolation, chậm nhất
```

**Game server nên dùng gì?**
- Economy (gold, item): `SERIALIZABLE` hoặc `REPEATABLE READ` + explicit locks
- Analytics (leaderboard): `READ COMMITTED` đủ
- Logging: `READ UNCOMMITTED` OK

### 3. MVCC (Multi-Version Concurrency Control)

```
Transaction T1 reads player gold = 100
Transaction T2 updates gold = 150, commits
Transaction T1 reads again → vẫn thấy 100 (snapshot isolation!)

Internal: DB giữ multiple versions
  Version 1: gold = 100 (visible to T1)
  Version 2: gold = 150 (visible to new transactions)
```

### 4. Index — B+ Tree

```
              ┌─────────────┐
              │  [50 | 100]  │         ← Internal nodes (keys only)
              └──┬──┬──┬────┘
           ┌─────┘  │  └─────┐
     ┌─────▼──┐ ┌───▼───┐ ┌──▼─────┐
     │ [10,20,│ │[50,60, │ │[100,150│  ← Leaf nodes (keys + data)
     │  30,40]│ │ 70,80] │ │  200]  │
     └────┬───┘ └───┬───┘ └───┬────┘
          └────→ ───→    ────→        ← Leaves linked for range scan
```

**B+ Tree vs Hash Index:**
| | B+ Tree | Hash |
|--|---------|------|
| Exact lookup | O(log n) | O(1) |
| Range query | ✅ Efficient | ❌ Full scan |
| Sorted output | ✅ | ❌ |
| Game use | Leaderboard (range), time queries | Player lookup by ID |

### 5. Write Amplification

Mỗi write logical → multiple physical writes:
- WAL (Write-Ahead Log)
- Actual data page
- Index updates (mỗi index = thêm 1 write)

**Rule of thumb**: Mỗi index thêm ~30-50% write overhead. Game server với nhiều writes (position updates) → hạn chế index.

### 6. Replication & Sharding

**Replication** (availability):
```
  Primary ──write──→ Replica 1 (async)
     │                Replica 2 (async)
     └── reads ←────── Replicas
```

**Sharding** (scalability):
```
  Shard key: player_id % 4
  Shard 0: player 0,4,8...  → Server A
  Shard 1: player 1,5,9...  → Server B
  Shard 2: player 2,6,10... → Server C
  Shard 3: player 3,7,11... → Server D
```

> [!IMPORTANT]
> **Shard key choice critical**: Nếu shard by `server_region` → hot shard khi 1 region đông. Shard by `player_id` → đều hơn nhưng cross-shard query khó (guild data).

---

## 🔨 PROJECTS

### Project 1: Docker Setup + Transaction Race Test
```bash
# docker-compose.yml
docker compose up -d  # MySQL 8 + Postgres 15

# Transaction race test: 2 transactions cùng mua item (stock=1)
# Expected: chỉ 1 thành công
```

### Project 2: Index Benchmark
```sql
-- Table: 1M player records
-- Test 1: SELECT WHERE gold > 1000 (no index)
-- Test 2: SELECT WHERE gold > 1000 (with index on gold)
-- Measure: query time, explain plan
```

### Project 3: Deadlock Scenario
```sql
-- T1: UPDATE players SET gold=gold-10 WHERE id=1; UPDATE players SET gold=gold+10 WHERE id=2;
-- T2: UPDATE players SET gold=gold-10 WHERE id=2; UPDATE players SET gold=gold+10 WHERE id=1;
-- Result: DEADLOCK → analyze, fix with consistent ordering
```

---

## ✅ CHECKLIST

- [ ] Giải thích ACID với game examples
- [ ] So sánh 4 isolation levels, biết game cần level nào
- [ ] Hiểu MVCC mechanism
- [ ] Giải thích B+ Tree operations
- [ ] Setup MySQL + Postgres Docker
- [ ] Reproduce và fix deadlock
- [ ] Benchmark index impact
- [ ] Hiểu replication vs sharding trade-offs

## 🎯 MILESTONE: Reproduce deadlock + fix, benchmark index 10x improvement, design sharding cho CafePho player data

## 📚 TÀI LIỆU: DDIA Ch 2-3, 5, 7 | Use The Index, Luke (web)
