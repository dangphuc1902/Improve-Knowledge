# Database & JPA Concurrency — CV Deep Dive
> Optimistic/Pessimistic Locking, PostgreSQL EXPLAIN ANALYZE, Indexing.
> Những gì CV đã ghi: "optimistic/pessimistic locking", "PostgreSQL indexing", "~40% query time reduction".

---

## 1. JPA Concurrency Control — Locking

### 1.1 Vấn Đề Race Condition (Wallet Scenario)

```
T=0:  Thread A reads wallet balance: 1,000,000 VND
T=0:  Thread B reads wallet balance: 1,000,000 VND
T=1:  Thread A deducts 500,000 → saves 500,000 VND
T=1:  Thread B deducts 300,000 → saves 700,000 VND (overwrites A's update!)

Kết quả: Balance = 700,000 VND (sai! Nên là 200,000 VND)
```

---

### 1.2 Optimistic Locking — @Version

**Cơ chế**: Mỗi entity có `version` field. Khi update:
1. JPA include `WHERE id = ? AND version = ?` trong UPDATE query
2. Nếu `version` đã thay đổi (ai đó update trước) → `rows updated = 0`
3. JPA throw `OptimisticLockException`

```java
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;

    @Version  // JPA tự động quản lý version
    private Long version;
}

// Generated SQL khi save:
// UPDATE wallets SET balance = ?, version = 2 WHERE id = ? AND version = 1
// Nếu version đã là 2 rồi → 0 rows updated → OptimisticLockException
```

**Retry khi OptimisticLockException:**
```java
@Service
public class WalletService {

    @Retryable(
        value = OptimisticLockException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void debit(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepo.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException(walletId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepo.save(wallet); // OptimisticLockException nếu version conflict
    }
}
```

**Khi nào dùng Optimistic Locking:**
- Read-heavy workloads (nhiều đọc, ít ghi)
- Contention thấp (ít transaction cùng update 1 record)
- Acceptable để retry khi conflict
- Không muốn DB-level lock → better scalability

---

### 1.3 Pessimistic Locking — SELECT FOR UPDATE

**Cơ chế**: Acquire DB-level row lock ngay khi SELECT. Các transaction khác muốn lock cùng row phải chờ.

```java
// Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)  // SELECT ... FOR UPDATE
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)   // SELECT ... FOR SHARE
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForRead(@Param("id") Long id);
}

// Service
@Transactional
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    // Lock cả hai wallets (luôn lock theo thứ tự ID để tránh deadlock)
    Long firstId = Math.min(fromId, toId);
    Long secondId = Math.max(fromId, toId);

    Wallet first = walletRepo.findByIdForUpdate(firstId).orElseThrow();
    Wallet second = walletRepo.findByIdForUpdate(secondId).orElseThrow();

    Wallet from = first.getId().equals(fromId) ? first : second;
    Wallet to = first.getId().equals(toId) ? first : second;

    if (from.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException();
    }

    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));

    walletRepo.save(from);
    walletRepo.save(to);
}
// Khi transaction commit/rollback → locks released automatically
```

**Generated SQL:**
```sql
-- PostgreSQL
SELECT id, balance, version FROM wallets WHERE id = ? FOR UPDATE

-- MySQL
SELECT id, balance, version FROM wallets WHERE id = ? FOR UPDATE
```

**Lock modes:**
| Mode | SQL | Blocking |
|---|---|---|
| `PESSIMISTIC_WRITE` | `FOR UPDATE` | Block cả đọc và ghi |
| `PESSIMISTIC_READ` | `FOR SHARE` | Block ghi, cho phép đọc |
| `PESSIMISTIC_FORCE_INCREMENT` | `FOR UPDATE` + increment version | Block + update version |

**Lock timeout (tránh hung indefinitely):**
```java
Map<String, Object> hints = new HashMap<>();
hints.put("jakarta.persistence.lock.timeout", 3000); // 3 seconds
walletRepo.findById(walletId, LockModeType.PESSIMISTIC_WRITE, hints);
```

**Khi nào dùng Pessimistic Locking:**
- Write-heavy workloads, high contention
- Financial transactions (balance updates) — cost of conflict > cost of lock
- Không muốn retry logic phức tạp
- Cần đảm bảo data integrity tuyệt đối

---

### 1.4 Deadlock Prevention

```
Thread A: Lock wallet 1 → wait for wallet 2
Thread B: Lock wallet 2 → wait for wallet 1
→ DEADLOCK
```

**Fix: Luôn acquire locks theo thứ tự nhất định (canonical ordering):**
```java
// Always lock by ascending wallet ID
Long lockFirst = Math.min(fromWalletId, toWalletId);
Long lockSecond = Math.max(fromWalletId, toWalletId);

Wallet w1 = walletRepo.findByIdForUpdate(lockFirst).orElseThrow();
Wallet w2 = walletRepo.findByIdForUpdate(lockSecond).orElseThrow();
```

---

### 1.5 N+1 Problem — JPA/Hibernate

**Vấn đề:**
```java
List<User> users = userRepo.findAll();  // 1 query
for (User user : users) {
    List<Order> orders = user.getOrders(); // N queries (1 per user)
    // → N+1 total queries
}
```

**Fix 1 — JOIN FETCH trong JPQL:**
```java
@Query("SELECT DISTINCT u FROM User u JOIN FETCH u.orders WHERE u.status = 'ACTIVE'")
List<User> findActiveUsersWithOrders();
// → 1 query với JOIN, load hết data một lần
```

**Fix 2 — EntityGraph:**
```java
@EntityGraph(attributePaths = {"orders", "orders.items"})
List<User> findByStatus(String status);
```

**Fix 3 — Batch fetching:**
```java
@Entity
public class User {
    @OneToMany
    @BatchSize(size = 20) // Fetch 20 users' orders per batch
    private List<Order> orders;
}
```

**Detect N+1 — Hibernate statistics:**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
        format_sql: true
logging:
  level:
    org.hibernate.stat: DEBUG
    org.hibernate.SQL: DEBUG
```

---

## 2. PostgreSQL — EXPLAIN ANALYZE

### 2.1 Cú Pháp Cơ Bản

```sql
EXPLAIN ANALYZE
SELECT t.id, t.amount, w.balance
FROM transactions t
JOIN wallets w ON t.wallet_id = w.id
WHERE t.user_id = 123
  AND t.created_at > '2026-01-01'
ORDER BY t.created_at DESC
LIMIT 50;
```

### 2.2 Đọc Execution Plan

```
Sort  (cost=15.32..15.45 rows=50 width=48) (actual time=0.234..0.241 rows=50 loops=1)
  Sort Key: t.created_at DESC
  Sort Method: top-N heapsort  Memory: 32kB
  ->  Hash Join  (cost=4.18..14.89 rows=51 width=48) (actual time=0.089..0.198 rows=87 loops=1)
        Hash Cond: (t.wallet_id = w.id)
        ->  Index Scan using idx_transactions_user_created on transactions t
              (cost=0.43..10.02 rows=51 width=36) (actual time=0.031..0.121 rows=87 loops=1)
              Index Cond: ((user_id = 123) AND (created_at > '2026-01-01'::date))
        ->  Hash  (cost=2.30..2.30 rows=130 width=16) (actual time=0.042..0.042 rows=130 loops=1)
              ->  Seq Scan on wallets w  (cost=0.00..2.30 rows=130 width=16) (...) 
Planning Time: 0.312 ms
Execution Time: 0.287 ms
```

**Giải thích từng field:**
```
cost=0.43..10.02
      │     │
      │     └── Total cost (sau khi fetch tất cả rows)
      └──────── Startup cost (trước khi return row đầu tiên)
                (đơn vị arbitrary, relative to sequential page read)

rows=51         Estimated rows (PostgreSQL estimate)
actual rows=87  Actual rows (sau ANALYZE)
                → Nếu difference lớn → statistics cũ, cần ANALYZE

loops=1         Node được execute bao nhiêu lần
                (trong nested loop join → loops = rows của outer loop)

actual time=0.031..0.121
             │      │
             │      └── Time đến khi fetch row cuối (ms)
             └────────── Time đến khi fetch row đầu (ms)
```

### 2.3 Scan Types

| Scan Type | Khi nào xuất hiện | Tốt hay Xấu? |
|---|---|---|
| **Seq Scan** | Full table scan, không có index hoặc planner thấy index không hiệu quả | Xấu nếu bảng lớn |
| **Index Scan** | Query selective, index tồn tại | ✅ Tốt |
| **Index Only Scan** | Tất cả columns cần thiết đều trong index | ✅ Tốt nhất |
| **Bitmap Index Scan** | Nhiều rows match, moderate selectivity | Tương đối tốt |
| **Bitmap Heap Scan** | Kết hợp nhiều bitmap indexes | Tương đối tốt |

### 2.4 Join Types

| Join | Khi nào PostgreSQL chọn |
|---|---|
| **Hash Join** | Khi không có index, hoặc bảng lớn join bảng lớn |
| **Nested Loop Join** | Khi inner table nhỏ hoặc có index tốt |
| **Merge Join** | Khi cả hai tables đã được sort (thường trên indexed columns) |

---

## 3. PostgreSQL Indexing — Deep Dive

### 3.1 Index Types

**B-Tree (default)** — Dùng cho:
```sql
WHERE user_id = 123          -- Equality
WHERE amount > 1000          -- Range
WHERE name LIKE 'Nguyen%'    -- Prefix (không dùng LIKE '%Nguyen')
ORDER BY created_at DESC     -- Sort
```

**Hash Index** — Chỉ dùng cho equality (`=`):
```sql
CREATE INDEX idx_sessions_token ON sessions USING HASH (session_token);
-- Nhanh hơn B-Tree cho equality, không support range
```

**GIN (Generalized Inverted Index)** — Dùng cho:
```sql
-- JSONB
CREATE INDEX idx_metadata ON transactions USING GIN (metadata jsonb_path_ops);
WHERE metadata @> '{"type": "TRANSFER"}'

-- Full-text search
CREATE INDEX idx_description ON products USING GIN (to_tsvector('english', description));
WHERE to_tsvector('english', description) @@ to_tsquery('wallet & transfer')

-- Arrays
CREATE INDEX idx_tags ON posts USING GIN (tags);
WHERE 'java' = ANY(tags)
```

### 3.2 Composite Index — Column Order

```sql
-- Index trên (user_id, created_at)
CREATE INDEX idx_tx_user_created ON transactions (user_id, created_at DESC);

-- Queries sử dụng được index này:
WHERE user_id = 123                           -- ✅ Dùng được (prefix)
WHERE user_id = 123 AND created_at > '...'   -- ✅ Dùng được (full)
ORDER BY user_id, created_at DESC             -- ✅ Dùng được

-- Queries KHÔNG sử dụng được:
WHERE created_at > '...'                      -- ❌ Không dùng được (không phải prefix)
```

**Quy tắc**: Composite index chỉ hữu ích khi query include **prefix columns** (equality columns trước, range columns sau).

### 3.3 Partial Index

```sql
-- Chỉ index active transactions (bỏ completed/cancelled)
CREATE INDEX idx_active_transactions
ON transactions (wallet_id, created_at)
WHERE status = 'PENDING';

-- Dùng khi:
-- 1. Phần lớn query chỉ care về subset data (e.g., active records)
-- 2. Muốn index nhỏ hơn, maintain nhanh hơn
```

### 3.4 Khi nào Index không hiệu quả

```sql
-- 1. Selectivity thấp (index trên column có ít distinct values)
-- Bad: index trên gender (M/F) khi bảng có 50% M, 50% F
-- Planner sẽ chọn Seq Scan vì quá nhiều rows match

-- 2. Function applied trên indexed column
WHERE UPPER(email) = 'TEST@EXAMPLE.COM'  -- ❌ index bị skip
-- Fix:
CREATE INDEX idx_email_upper ON users (UPPER(email));

-- 3. Leading % trong LIKE
WHERE name LIKE '%Nguyen'  -- ❌ index bị skip

-- 4. Implicit type cast
WHERE user_id = '123'  -- user_id là integer, '123' là varchar
                       -- ❌ có thể bị skip nếu PostgreSQL cast không match
-- Fix: WHERE user_id = 123 (correct type)

-- 5. OR conditions (thường)
WHERE user_id = 123 OR wallet_id = 456  -- Có thể Bitmap OR, verify với EXPLAIN
```

### 3.5 Connection Pooling — HikariCP

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10         # Số connection tối đa
      minimum-idle: 5               # Số idle connections giữ sẵn
      connection-timeout: 30000     # 30s timeout để lấy connection từ pool
      idle-timeout: 600000          # 10 phút → close idle connection
      max-lifetime: 1800000         # 30 phút → retire và recreate connection
      pool-name: WalletHikariPool
```

**Formula tính pool size** (từ PGBouncer team): `pool_size = (core_count * 2) + effective_spindle_count`

Với server 4 cores, SSD: `pool_size = (4 * 2) + 1 = 9` → 10 connections.

---

## 4. SQL Window Functions

```sql
-- ROW_NUMBER(): Rank từng row trong partition (không có tie)
SELECT 
    user_id,
    transaction_id,
    amount,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
FROM transactions;

-- RANK(): Tie → same rank, next rank có gap
-- DENSE_RANK(): Tie → same rank, next rank không có gap

-- LAG/LEAD: Lấy value từ row trước/sau
SELECT
    transaction_id,
    amount,
    LAG(amount) OVER (PARTITION BY wallet_id ORDER BY created_at) AS prev_amount,
    amount - LAG(amount) OVER (PARTITION BY wallet_id ORDER BY created_at) AS change
FROM transactions;

-- Running total
SELECT
    transaction_id,
    amount,
    SUM(amount) OVER (PARTITION BY wallet_id ORDER BY created_at) AS running_balance
FROM transactions;

-- Lấy top N per group (ví dụ: 3 giao dịch lớn nhất mỗi user)
SELECT * FROM (
    SELECT
        *,
        ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY amount DESC) AS rn
    FROM transactions
) t
WHERE rn <= 3;
```

---

## 5. Interview Q&A

**Q: Tại sao bạn chọn Pessimistic Locking cho wallet trong FPM?**
A: Wallet balance update là write-heavy, high-contention scenario. Với Optimistic Locking, nhiều transactions sẽ conflict và phải retry, gây overhead. Với Pessimistic Locking, cost là lock wait time nhưng guaranteed correctness với zero retries. Trong financial context, correctness > throughput.

**Q: EXPLAIN vs EXPLAIN ANALYZE khác gì?**
A: `EXPLAIN` chỉ show estimated plan (không execute). `EXPLAIN ANALYZE` thực sự execute query và show actual time + actual rows. Dùng `EXPLAIN ANALYZE` để compare estimated vs actual — khi estimate sai nhiều → cần `VACUUM ANALYZE` để update statistics.

**Q: Bạn đã đạt ~40% query time reduction như thế nào ở Hahalolo?**
A: (Câu trả lời mẫu dựa trên common techniques)
*"Tôi profiled với EXPLAIN ANALYZE và phát hiện N+1 problem trong social feed query — mỗi post trigger thêm 1 query để load user info. Fix bằng JOIN FETCH trong JPQL. Ngoài ra thêm composite index trên (user_id, created_at) cho feed query thay vì index riêng lẻ, từ Seq Scan xuống Index Scan. Kết hợp thêm Redis cache-aside cho user profile data — những data này read-heavy, thay đổi ít."*

**Q: Deadlock xảy ra khi nào và cách detect?**
A: Deadlock khi 2+ transactions mỗi cái đang hold lock và chờ lock của cái kia. PostgreSQL tự detect sau `deadlock_timeout` (default 1s) và cancel 1 transaction. Log ra `ERROR: deadlock detected`. Fix: consistent lock ordering hoặc retry với exponential backoff.
