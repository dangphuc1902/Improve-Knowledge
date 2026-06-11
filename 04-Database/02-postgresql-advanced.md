# 🐘 PostgreSQL Advanced

> **Phase:** 1-2 | **Time Block:** T7 13:30-15:30  
> **Quan trọng cho:** Tiki, Teko, TymeX, NAB — dùng PostgreSQL làm primary DB

---

## 1. Window Functions

```sql
-- ROW_NUMBER: đánh số thứ tự trong group
SELECT 
    user_id, amount, created_at,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
FROM transactions;
-- Lấy transaction mới nhất mỗi user: WHERE rn = 1

-- RANK vs DENSE_RANK
SELECT 
    name, score,
    RANK() OVER (ORDER BY score DESC) AS rank,          -- 1,2,2,4 (skip)
    DENSE_RANK() OVER (ORDER BY score DESC) AS dense_rank -- 1,2,2,3 (no skip)
FROM players;

-- LAG / LEAD: so sánh với row trước/sau
SELECT 
    date, revenue,
    LAG(revenue, 1) OVER (ORDER BY date) AS prev_day_revenue,
    revenue - LAG(revenue, 1) OVER (ORDER BY date) AS daily_change,
    LEAD(revenue, 1) OVER (ORDER BY date) AS next_day_revenue
FROM daily_stats;

-- Running total
SELECT 
    date, amount,
    SUM(amount) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) 
        AS running_total
FROM transactions;

-- Moving average (7 days)
SELECT 
    date, revenue,
    AVG(revenue) OVER (ORDER BY date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) 
        AS avg_7day
FROM daily_stats;
```

---

## 2. Common Table Expressions (CTE)

```sql
-- Recursive CTE: hierarchical data (org chart, categories)
WITH RECURSIVE category_tree AS (
    -- Base case
    SELECT id, name, parent_id, 0 AS depth
    FROM categories
    WHERE parent_id IS NULL
    
    UNION ALL
    
    -- Recursive step
    SELECT c.id, c.name, c.parent_id, ct.depth + 1
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT * FROM category_tree ORDER BY depth, name;

-- CTE for readability
WITH 
    active_users AS (
        SELECT id, name FROM users WHERE status = 'ACTIVE'
    ),
    user_orders AS (
        SELECT user_id, COUNT(*) AS order_count, SUM(total) AS total_spent
        FROM orders
        GROUP BY user_id
    )
SELECT u.name, COALESCE(o.order_count, 0) AS orders, COALESCE(o.total_spent, 0) AS spent
FROM active_users u
LEFT JOIN user_orders o ON u.id = o.user_id;
```

---

## 3. JSONB (Binary JSON)

```sql
-- Create table with JSONB
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50),
    payload JSONB NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Insert
INSERT INTO events (event_type, payload) VALUES 
('match_end', '{"player_id": 123, "score": 450, "duration": 120, "items": ["sword", "shield"]}');

-- Query JSONB
SELECT payload->>'player_id' AS player_id,           -- text
       (payload->>'score')::int AS score,              -- cast to int
       payload->'items' AS items_array,                -- jsonb array
       payload->'items'->>0 AS first_item              -- first array element
FROM events 
WHERE payload->>'player_id' = '123';

-- JSONB containment (@>)
SELECT * FROM events WHERE payload @> '{"score": 450}';

-- JSONB existence (?)
SELECT * FROM events WHERE payload ? 'duration';

-- GIN index for JSONB
CREATE INDEX idx_events_payload ON events USING GIN (payload);
CREATE INDEX idx_events_player ON events USING GIN ((payload->'player_id'));
```

---

## 4. Table Partitioning

```sql
-- Range partitioning (by date — most common)
CREATE TABLE transactions (
    id BIGSERIAL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(15,2),
    created_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Create partitions
CREATE TABLE transactions_2026_01 PARTITION OF transactions
    FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE transactions_2026_02 PARTITION OF transactions
    FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');

-- List partitioning (by region/status)
CREATE TABLE orders (
    id BIGSERIAL,
    region VARCHAR(10),
    total DECIMAL(15,2)
) PARTITION BY LIST (region);

CREATE TABLE orders_hcm PARTITION OF orders FOR VALUES IN ('HCM');
CREATE TABLE orders_hn PARTITION OF orders FOR VALUES IN ('HN');

-- Hash partitioning (even distribution)
CREATE TABLE sessions (
    id UUID,
    user_id BIGINT
) PARTITION BY HASH (user_id);

CREATE TABLE sessions_0 PARTITION OF sessions FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE sessions_1 PARTITION OF sessions FOR VALUES WITH (MODULUS 4, REMAINDER 1);
```

### Khi nào partition?
| Table size | Recommendation |
|:-----------|:---------------|
| < 10GB | Không cần partition |
| 10-100GB | Consider partitioning nếu query chậm |
| > 100GB | **Nên partition** |
| Time-series data | **Luôn partition** by time |

---

## 5. Query Optimization — EXPLAIN ANALYZE

```sql
-- EXPLAIN ANALYZE: xem execution plan thực tế
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT u.name, COUNT(o.id) as order_count
FROM users u
JOIN orders o ON u.id = o.user_id
WHERE u.status = 'ACTIVE'
GROUP BY u.name
ORDER BY order_count DESC
LIMIT 10;

-- Reading output:
-- Seq Scan → full table scan (BAD for large tables)
-- Index Scan → using index (GOOD)
-- Bitmap Index Scan → index for filtering, then heap fetch
-- Hash Join → join using hash table (good for large joins)
-- Nested Loop → join with nested iteration (good for small tables)
-- Sort → explicit sorting (check if index can avoid)
```

### Index Types
| Type | Use Case | Example |
|:-----|:---------|:--------|
| B-tree (default) | Equality, range, ordering | `CREATE INDEX idx ON t(col)` |
| Hash | Equality only | `CREATE INDEX idx ON t USING HASH (col)` |
| GIN | Full-text, JSONB, arrays | `CREATE INDEX idx ON t USING GIN (payload)` |
| GiST | Geometric, range types | PostGIS spatial data |
| BRIN | Large sequential data | Time-series, append-only |

### Composite Index Rules
```sql
-- Index on (a, b, c) supports:
-- WHERE a = ?              ✅
-- WHERE a = ? AND b = ?    ✅
-- WHERE a = ? AND b = ? AND c = ?  ✅
-- WHERE b = ?              ❌ (leftmost prefix rule)
-- WHERE a = ? AND c = ?    ⚠️ (uses a, skip c)

-- Covering index (includes columns for index-only scan)
CREATE INDEX idx_covering ON orders(user_id, status) INCLUDE (total, created_at);
```

---

## 6. PostgreSQL vs MySQL

| Feature | PostgreSQL | MySQL |
|:--------|:-----------|:------|
| JSONB | ✅ Native, indexed | JSON (less performant) |
| CTE/Window Functions | ✅ Full support | ✅ Since 8.0 |
| Partitioning | ✅ Declarative | ✅ But less flexible |
| Full-text search | ✅ Built-in | ✅ But limited |
| MVCC | ✅ True MVCC | ⚠️ Undo log based |
| Replication | Logical + Streaming | Binary log |
| Extensions | ✅ Rich (PostGIS, pg_stat) | Limited |
| Concurrency | Better under heavy writes | Better for simple reads |

---

## Câu Hỏi Phỏng Vấn

### Q1: EXPLAIN ANALYZE output — bạn tìm gì?
**A:** Seq Scan trên table lớn (thiếu index), high cost estimates, sort operations (có thể tránh bằng index), nested loops trên large datasets (nên dùng hash join), actual time vs estimated (sai lệch = outdated statistics → ANALYZE table).

### Q2: Khi nào dùng JSONB vs normalized tables?
**A:** JSONB: schema-less data, varied attributes, audit logs, event payloads. Normalized: structured data, relationships, complex joins, aggregations. Trade-off: JSONB flexible nhưng harder to enforce constraints, slower for complex queries across JSON fields.

### Q3: Table partitioning benefits?
**A:** Query performance (partition pruning — chỉ scan partition cần), maintenance (DROP partition thay vì DELETE millions rows), parallel scan. Drawbacks: unique constraints phải include partition key, cross-partition queries slower, quản lý partitions.

### Q4: Composite index ordering quan trọng thế nào?
**A:** Leftmost prefix rule: index (a, b, c) chỉ dùng được nếu query filter bắt đầu từ a. Equality columns trước, range columns sau. High cardinality columns trước. ORDER BY columns phải match index order.
