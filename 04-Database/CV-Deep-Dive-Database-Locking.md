# Database & JPA Concurrency — CV Deep Dive
> Optimistic/Pessimistic Locking, PostgreSQL EXPLAIN ANALYZE, Indexing.
> Những gì CV đã ghi: "optimistic/pessimistic locking", "PostgreSQL indexing", "~40% query time reduction".

---

## 1. JPA Concurrency Control — Locking

### 1.1 Vấn đề Race Condition (Wallet Scenario)

#### 🔑 Khái niệm trước khi đọc

**Concurrency (Xử lý đồng thời)** = nhiều request/thread cùng chạy một lúc, cùng truy cập/sửa đổi dữ liệu.

**Race Condition (Xung đột đồng thời)** = lỗi xảy ra khi kết quả phụ thuộc vào thứ tự chạy của các thread. Không kiểm soát được, khó tái hiện, rất nguy hiểm trong hệ thống tài chính.

**Locking (Khóa)** = cơ chế đảm bảo chỉ 1 thread/transaction được sửa data tại 1 thời điểm, tránh race condition.

```
T=0:  Thread A reads wallet balance: 1,000,000 VND
T=0:  Thread B reads wallet balance: 1,000,000 VND   ← đọc cùng lúc
T=1:  Thread A deducts 500,000 → saves 500,000 VND
T=1:  Thread B deducts 300,000 → saves 700,000 VND  ← ghi đè lên kết quả của A!

Kết quả: Balance = 700,000 VND (sai! Nên là 200,000 VND)
Lý do: Cả 2 thread đọc balance = 1tr, tính toán riêng, lưu đè lại nhau.
```

---

### 1.2 Optimistic Locking — @Version

#### 🔑 Optimistic vs Pessimistic — triết lý khác nhau

**Optimistic Locking** (Khóa lạc quan) = Giả định **ít conflict** xảy ra. Không khóa data lúc đọc. Chỉ kiểm tra khi commit — nếu ai đó cũng đã sửa → throw exception và retry.

**Pessimistic Locking** (Khóa bi quan) = Giả định **nhiều conflict** xảy ra. Khóa data ngay khi đọc. Người khác muốn đọc/ghi phải chờ.

```
Optimistic:  Đọc → Sửa → Commit (kiểm tra conflict) → OK hoặc Exception + Retry
Pessimistic: Khóa → Đọc → Sửa → Commit → Giải phóng khóa
```

**Cơ chế Optimistic**: Mỗi entity có `version` field. Khi update:
1. JPA include `WHERE id = ? AND version = ?` trong UPDATE query
2. Nếu `version` đã thay đổi (ai đó update trước) → `rows updated = 0`
3. JPA throw `OptimisticLockException`

```java
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       // Primary key, auto-increment bởi DB

    private BigDecimal balance;  // Số dư ví — field bị race condition

    @Version  // Optimistic Locking: JPA tự quản lý field này
    private Long version;
    // JPA tự động:
    //   1. Khi SELECT → đọc version hiện tại (VD: version = 1)
    //   2. Khi UPDATE → tăng version + check version cũ
    //      → SQL: UPDATE wallets SET balance = ?, version = 2 WHERE id = ? AND version = 1
    //   3. Nếu version đã bị thay đổi (thread khác update trước)
    //      → WHERE version = 1 match 0 rows → JPA throw OptimisticLockException
    // ⚠️ KHÔNG cần tự set version — JPA handle hoàn toàn
}
```

**Retry khi OptimisticLockException:**
```java
@Service
public class WalletService {

    // @Retryable (Spring Retry): tự động retry khi gặp exception chỉ định
    //   value = OptimisticLockException.class: chỉ retry khi gặp lỗi optimistic lock
    //   maxAttempts = 3: tối đa 3 lần thử (1 lần đầu + 2 lần retry)
    //   backoff: thời gian chờ giữa các lần retry
    //     delay = 100ms (lần 1), multiplier = 2 → 200ms (lần 2), 400ms (lần 3)
    //     → Exponential backoff: tránh tất cả thread retry cùng lúc → conflict tiếp
    // ⚠️ Cần @EnableRetry trên @Configuration class để kích hoạt
    @Retryable(
        value = OptimisticLockException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional  // Mỗi lần retry = 1 transaction MỚI (transaction cũ đã rollback)
    public void debit(Long walletId, BigDecimal amount) {
        // Đọc wallet từ DB → lấy version hiện tại
        Wallet wallet = walletRepo.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException(walletId));

        // Business rule: kiểm tra số dư đủ không
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();  // Không retry — lỗi business
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));  // Trừ tiền
        walletRepo.save(wallet);
        // JPA generate: UPDATE wallets SET balance=?, version=N+1 WHERE id=? AND version=N
        // Nếu version conflict → OptimisticLockException → @Retryable retry
        // Retry sẽ đọc lại wallet với version MỚI NHẤT → tính toán lại → save lại
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
// === REPOSITORY: khai báo query có lock ===
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // PESSIMISTIC_WRITE → SQL: SELECT ... FOR UPDATE
    // → DB lock row → thread khác muốn lock CÙNG row phải CHỜ
    // → Dùng khi CẦN UPDATE row sau khi đọc (read-then-write pattern)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);

    // PESSIMISTIC_READ → SQL: SELECT ... FOR SHARE
    // → Cho phép NHIỀU thread đọc cùng lúc (shared lock)
    // → BLOCK thread muốn WRITE (exclusive lock)
    // → Dùng khi chỉ cần ĐỌC chính xác, không update
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForRead(@Param("id") Long id);
}

// === SERVICE: chuyển tiền giữa 2 ví ===
@Transactional  // Bắt buộc: lock chỉ tồn tại trong transaction, commit/rollback → lock tự giải phóng
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    // === CANONICAL ORDERING: luôn lock theo thứ tự ID tăng dần ===
    // Tại sao? Tránh DEADLOCK:
    //   Thread A: lock wallet 1 → chờ wallet 2
    //   Thread B: lock wallet 2 → chờ wallet 1 → DEADLOCK!
    // Fix: cả 2 thread đều lock wallet 1 trước → thread B chờ → không deadlock
    Long firstId = Math.min(fromId, toId);
    Long secondId = Math.max(fromId, toId);

    // Lock row theo thứ tự — thread khác muốn lock cùng rows phải CHỜ
    Wallet first = walletRepo.findByIdForUpdate(firstId).orElseThrow();
    Wallet second = walletRepo.findByIdForUpdate(secondId).orElseThrow();

    // Map lại: first/second theo ID, from/to theo business logic
    Wallet from = first.getId().equals(fromId) ? first : second;
    Wallet to = first.getId().equals(toId) ? first : second;

    // Business rule: kiểm tra số dư
    if (from.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException();
        // Exception → transaction ROLLBACK → locks GIẢI PHÓNG → thread khác tiếp tục
    }

    from.setBalance(from.getBalance().subtract(amount));  // Trừ tiền ví nguồn
    to.setBalance(to.getBalance().add(amount));            // Cộng tiền ví đích

    walletRepo.save(from);
    walletRepo.save(to);
    // Transaction COMMIT → locks GIẢI PHÓNG → thread đang chờ được tiếp tục
}
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
// Lock timeout: tránh thread chờ lock VÔ THỜI HẠN
// Mặc định: thread chờ cho đến khi lock được giải phóng (có thể rất lâu)
// → Set timeout → sau 3s chờ mà không lấy được lock → throw LockTimeoutException
Map<String, Object> hints = new HashMap<>();
hints.put("jakarta.persistence.lock.timeout", 3000); // 3000ms = 3 giây
// ⚠️ Hỗ trợ tùy thuộc DB: PostgreSQL có, MySQL InnoDB có (innodb_lock_wait_timeout)
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
// === CANONICAL ORDERING: chống deadlock ===
// Quy tắc: LUÔN lock theo thứ tự ID tăng dần, bất kể fromId hay toId
// → Tất cả thread đều lock cùng thứ tự → không bao giờ deadlock
Long lockFirst = Math.min(fromWalletId, toWalletId);   // ID nhỏ hơn → lock trước
Long lockSecond = Math.max(fromWalletId, toWalletId);  // ID lớn hơn → lock sau

// VD: transfer(5, 3) → lock wallet 3 trước, wallet 5 sau
//     transfer(3, 5) → lock wallet 3 trước, wallet 5 sau
// → Cùng thứ tự → thread B chờ thread A giải phóng wallet 3 → không deadlock
Wallet w1 = walletRepo.findByIdForUpdate(lockFirst).orElseThrow();
Wallet w2 = walletRepo.findByIdForUpdate(lockSecond).orElseThrow();
```

---

### 1.5 N+1 Problem — JPA/Hibernate

**Vấn đề:**
```java
// === N+1 PROBLEM: vấn đề phổ biến nhất với JPA/Hibernate ===
List<User> users = userRepo.findAll();  // 1 query: SELECT * FROM users
for (User user : users) {
    List<Order> orders = user.getOrders();
    // Hibernate LAZY LOAD: mỗi lần gọi getOrders() → 1 query SQL
    // → SELECT * FROM orders WHERE user_id = ? (cho TỪNG user)
    // Nếu có 100 users → 1 + 100 = 101 queries!
    // → N+1 total queries (1 cho users + N cho orders)
    // → Performance rất tệ, đặc biệt với bảng lớn
}
```

**Fix 1 — JOIN FETCH trong JPQL:**
```java
// JOIN FETCH: Hibernate load User + Orders trong 1 query duy nhất
// DISTINCT: loại bỏ duplicate users (1 user có nhiều orders → nhiều rows → JPA trả duplicate)
// → Thay vì N+1 queries → CHỈ 1 query:
//    SELECT DISTINCT u.*, o.* FROM users u JOIN orders o ON u.id = o.user_id WHERE u.status = 'ACTIVE'
@Query("SELECT DISTINCT u FROM User u JOIN FETCH u.orders WHERE u.status = 'ACTIVE'")
List<User> findActiveUsersWithOrders();
// ⚠️ JOIN FETCH với pagination (Pageable) → Hibernate fetch ALL rồi paginate trong memory → nguy hiểm!
```

**Fix 2 — EntityGraph:**
```java
// @EntityGraph: cách declarative để chỉ định eager loading
// attributePaths: danh sách associations cần load cùng lúc
//   "orders" → load User.orders
//   "orders.items" → load nested Order.items
// → Hibernate generate 1 query với LEFT JOIN → không N+1
// Ưu điểm so với JOIN FETCH: không cần viết JPQL, dùng được với derived query methods
@EntityGraph(attributePaths = {"orders", "orders.items"})
List<User> findByStatus(String status);
```

**Fix 3 — Batch fetching:**
```java
@Entity
public class User {
    @OneToMany
    @BatchSize(size = 20)
    // BatchSize: thay vì 1 query per user → gom 20 users rồi query 1 lần
    // Không N+1: SELECT * FROM orders WHERE user_id IN (1,2,3,...20)
    // 100 users → 5 batch queries thay vì 100 queries
    // Ưu điểm: không cần thay đổi query, chỉ thêm annotation
    // Nhược điểm: vẫn nhiều hơn 1 query (so với JOIN FETCH)
    private List<Order> orders;
}
```

**Detect N+1 — Hibernate statistics:**
```yaml
# Cấu hình để PHÁT HIỆN N+1 problem trong development
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true   # Hibernate ghi thống kê: số query, fetch count, cache hit...
        format_sql: true            # Format SQL đẹp trong log (dễ đọc)
        # → Log output: "Session Metrics { 101 queries executed }" → thấy ngay N+1!
logging:
  level:
    org.hibernate.stat: DEBUG   # Log statistics (tổng số queries, thời gian...)
    org.hibernate.SQL: DEBUG    # Log từng câu SQL được generate
    # ⚠️ CHỈ bật ở DEV/TEST — KHÔNG bật ở PRODUCTION (ảnh hưởng performance)
```

---

## 2. PostgreSQL — EXPLAIN ANALYZE

### 2.1 Cú Pháp Cơ Bản

```sql
-- EXPLAIN: chỉ show plan DỰ KIẾN (không thực sự chạy query)
-- EXPLAIN ANALYZE: CHẠY THẬT query và show plan THỰC TẾ (actual time, actual rows)
-- → Dùng EXPLAIN ANALYZE để so sánh estimated vs actual → phát hiện vấn đề
-- ⚠️ ANALYZE thực sự execute query → cẩn thận với DELETE/UPDATE (dùng trong transaction + ROLLBACK)
EXPLAIN ANALYZE
SELECT t.id, t.amount, w.balance
FROM transactions t
JOIN wallets w ON t.wallet_id = w.id
WHERE t.user_id = 123                 -- Filter: chỉ lấy transactions của user 123
  AND t.created_at > '2026-01-01'     -- Range filter: chỉ lấy từ 2026 trở đi
ORDER BY t.created_at DESC            -- Sort: mới nhất trước
LIMIT 50;                             -- Pagination: chỉ lấy 50 rows
-- → Query này hưởng lợi từ composite index (user_id, created_at DESC)
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
# HikariCP: Connection Pool mặc định của Spring Boot
# Tại sao cần pool? Tạo DB connection rất TỐN (TCP handshake, auth, SSL...)
# → Tạo sẵn N connections, reuse → giảm latency đáng kể
spring:
  datasource:
    hikari:
      maximum-pool-size: 10         # Tối đa 10 connections đồng thời đến DB
                                    # Quá nhiều → DB quá tải, quá ít → thread phải chờ
      minimum-idle: 5               # Giữ sẵn 5 idle connections (không chờ tạo mới)
      connection-timeout: 30000     # Thread chờ tối đa 30s để lấy connection từ pool
                                    # Hết 30s → throw SQLTransientConnectionException
      idle-timeout: 600000          # Connection idle > 10 phút → đóng (giải phóng resource)
                                    # Chỉ áp dụng khi pool > minimum-idle
      max-lifetime: 1800000         # Connection tồn tại tối đa 30 phút → đóng và tạo mới
                                    # Tránh DB/firewall kill connection cũ đột ngột
                                    # ⚠️ Nên nhỏ hơn DB wait_timeout vài phút
      pool-name: WalletHikariPool   # Tên pool — hiện trong log, JMX metrics
```

**Formula tính pool size** (từ PGBouncer team): `pool_size = (core_count * 2) + effective_spindle_count`

Với server 4 cores, SSD: `pool_size = (4 * 2) + 1 = 9` → 10 connections.

---

## 4. SQL Window Functions

```sql
-- === WINDOW FUNCTIONS: tính toán trên "cửa sổ" rows liên quan ===
-- Cú pháp: function() OVER (PARTITION BY ... ORDER BY ...)
--   PARTITION BY: chia rows thành nhóm (giống GROUP BY nhưng KHÔNG gom)
--   ORDER BY: thứ tự rows trong mỗi nhóm

-- ROW_NUMBER(): đánh số thứ tự 1, 2, 3... cho mỗi row trong partition
-- Không có tie: dù 2 rows giống nhau vẫn đánh số khác nhau
SELECT 
    user_id,
    transaction_id,
    amount,
    ROW_NUMBER() OVER (
        PARTITION BY user_id           -- Nhóm theo user
        ORDER BY created_at DESC       -- Mới nhất = số 1
    ) AS rn
FROM transactions;
-- Kết quả: user_id=1 có rn=1,2,3..., user_id=2 có rn=1,2,3... (reset mỗi user)

-- RANK() vs DENSE_RANK():
-- RANK():       amount=100→rank 1, amount=100→rank 1, amount=50→rank 3 (gap!)
-- DENSE_RANK(): amount=100→rank 1, amount=100→rank 1, amount=50→rank 2 (no gap)

-- LAG(column, offset): lấy giá trị từ row TRƯỚC (offset rows)
-- LEAD(column, offset): lấy giá trị từ row SAU
-- Use case: so sánh với giao dịch trước đó → tính biến động
SELECT
    transaction_id,
    amount,
    LAG(amount) OVER (PARTITION BY wallet_id ORDER BY created_at) AS prev_amount,
    -- prev_amount = amount của giao dịch TRƯỚC đó trong cùng wallet
    amount - LAG(amount) OVER (PARTITION BY wallet_id ORDER BY created_at) AS change
    -- change = chênh lệch so với giao dịch trước → phát hiện biến động bất thường
FROM transactions;

-- Running total (tổng tích lũy): SUM cộng dồn theo thứ tự
SELECT
    transaction_id,
    amount,
    SUM(amount) OVER (
        PARTITION BY wallet_id         -- Tính riêng cho từng wallet
        ORDER BY created_at            -- Cộng dồn theo thời gian
    ) AS running_balance
    -- running_balance = tổng tất cả amount từ đầu đến row hiện tại
    -- VD: 100, 200, -50 → running_balance: 100, 300, 250
FROM transactions;

-- Top N per group: lấy 3 giao dịch lớn nhất MỖI user
-- Pattern: đánh ROW_NUMBER → filter rn <= N
SELECT * FROM (
    SELECT
        *,
        ROW_NUMBER() OVER (
            PARTITION BY user_id        -- Nhóm theo user
            ORDER BY amount DESC        -- Lớn nhất trước
        ) AS rn
    FROM transactions
) t
WHERE rn <= 3;  -- Chỉ lấy top 3 mỗi user
-- ⚠️ Đây là subquery + filter, không dùng LIMIT vì LIMIT áp dụng toàn bộ result
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
