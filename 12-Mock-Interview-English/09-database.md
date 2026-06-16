# PART 9 — Database (Oracle, SQL Server, SQL)

---

## 🔷 ORACLE

### Q1: What is an Index and how does it work?

**Ideal Answer:**
> An index is a data structure (typically a B-Tree) that allows the database to find rows without scanning the entire table.

```sql
-- B-Tree Index (default) — good for equality and range queries
CREATE INDEX idx_user_email ON users(email);

-- Unique Index — enforces uniqueness
CREATE UNIQUE INDEX idx_user_email_unique ON users(email);

-- Composite Index — order matters!
CREATE INDEX idx_order_user_date ON orders(user_id, created_at);
-- Good for: WHERE user_id = ? AND created_at > ?
-- Also good for: WHERE user_id = ?  (leftmost prefix)
-- NOT useful: WHERE created_at > ?  (not leftmost)

-- Function-Based Index (Oracle)
CREATE INDEX idx_user_upper_email ON users(UPPER(email));
-- Supports: WHERE UPPER(email) = 'PHUC@EXAMPLE.COM'
```

**When indexes HURT performance:**
- High-frequency INSERT/UPDATE/DELETE (index maintenance overhead)
- Low-cardinality columns (gender: M/F → full scan often faster)
- Small tables (full scan is faster)

---

### Q2: Oracle Execution Plan

```sql
-- View execution plan
EXPLAIN PLAN FOR
SELECT * FROM orders WHERE user_id = 123;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Or in SQL*Plus:
SET AUTOTRACE ON;
```

**Key operations to understand:**
| Operation | Meaning |
|-----------|---------|
| TABLE ACCESS FULL | Full table scan — bad for large tables |
| INDEX RANGE SCAN | Using index with range condition |
| INDEX UNIQUE SCAN | Using unique index — best |
| NESTED LOOPS | Join: for each row in outer, scan inner |
| HASH JOIN | Build hash table — good for large tables |
| SORT MERGE JOIN | Sort both sides, merge |

---

### Q3: Oracle Sequence

```sql
-- Create sequence
CREATE SEQUENCE user_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Use in INSERT
INSERT INTO users (id, name) VALUES (user_seq.NEXTVAL, 'Phuc');

-- Current value (after first NEXTVAL in session)
SELECT user_seq.CURRVAL FROM DUAL;
```

---

### Q4: Stored Procedure vs Function (Oracle)

```sql
-- Procedure — no return value, side effects OK
CREATE OR REPLACE PROCEDURE update_user_status(
    p_user_id IN NUMBER,
    p_status IN VARCHAR2
)
AS
BEGIN
    UPDATE users SET status = p_status WHERE id = p_user_id;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;

-- Function — must return a value, no side effects (ideally)
CREATE OR REPLACE FUNCTION get_user_balance(p_user_id IN NUMBER)
RETURN NUMBER
AS
    v_balance NUMBER;
BEGIN
    SELECT balance INTO v_balance FROM wallets WHERE user_id = p_user_id;
    RETURN v_balance;
END;
```

---

### Q5: Locking in Oracle

```sql
-- Row-level lock
SELECT * FROM orders WHERE id = 1 FOR UPDATE;

-- Table lock
LOCK TABLE orders IN EXCLUSIVE MODE;

-- Skip locked rows (useful for queue processing)
SELECT * FROM job_queue WHERE status = 'PENDING'
    FOR UPDATE SKIP LOCKED;
```

**Deadlock:**
> Two transactions wait for each other's locks. Oracle detects this and kills one transaction with ORA-00060.

---

## 🔷 SQL SERVER

### Q6: Clustered vs Non-Clustered Index

| | Clustered Index | Non-Clustered Index |
|---|----------------|---------------------|
| Physical order | Table rows physically sorted by key | Separate structure with pointer to row |
| Count per table | 1 (one physical sort possible) | Up to 999 |
| Storage | Data IS the index | Separate B-Tree |
| Speed (on key) | Fastest for range scans | Slightly slower (lookup needed) |
| Default | Primary Key (SQL Server) | Explicit creation |

```sql
-- Clustered (one per table)
CREATE CLUSTERED INDEX CIX_Order_Date ON orders(created_at);

-- Non-Clustered
CREATE NONCLUSTERED INDEX NIX_Order_UserID ON orders(user_id)
    INCLUDE (status, total_amount);  -- covering index
```

**Covering Index:** Include all columns needed by the query to avoid Key Lookup.

---

## 🔷 GENERAL SQL

### Q7: Joins

```sql
-- INNER JOIN: only matching rows
SELECT u.name, o.id
FROM users u
INNER JOIN orders o ON u.id = o.user_id;

-- LEFT JOIN: all from left, matching from right (NULL if no match)
SELECT u.name, o.id
FROM users u
LEFT JOIN orders o ON u.id = o.user_id;
-- Finds users with NO orders: WHERE o.id IS NULL

-- RIGHT JOIN: opposite of LEFT JOIN
-- FULL OUTER JOIN: all rows from both sides

-- SELF JOIN: join table with itself (hierarchy)
SELECT e.name, m.name AS manager
FROM employees e
JOIN employees m ON e.manager_id = m.id;

-- CROSS JOIN: Cartesian product
SELECT * FROM products CROSS JOIN colors;
```

---

### Q8: GROUP BY, HAVING, WHERE

```sql
-- WHERE filters BEFORE grouping
-- HAVING filters AFTER grouping

SELECT user_id,
       COUNT(*) AS order_count,
       SUM(total_amount) AS total_spent
FROM orders
WHERE created_at >= '2024-01-01'      -- filter rows first
GROUP BY user_id
HAVING COUNT(*) > 5                    -- filter groups
ORDER BY total_spent DESC;
```

---

### Q9: Window Functions (Most asked!)

```sql
-- ROW_NUMBER: unique row number in partition
SELECT
    id, user_id, amount,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
FROM transactions;

-- Find most recent transaction per user:
SELECT * FROM (
    SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
    FROM transactions
) t WHERE rn = 1;

-- RANK: same value gets same rank, gap after ties
-- DENSE_RANK: same value gets same rank, no gap

-- LEAD/LAG: access next/previous row
SELECT
    id, amount,
    LAG(amount, 1) OVER (PARTITION BY user_id ORDER BY created_at) AS prev_amount,
    LEAD(amount, 1) OVER (PARTITION BY user_id ORDER BY created_at) AS next_amount
FROM transactions;

-- Running total
SELECT
    id, amount,
    SUM(amount) OVER (PARTITION BY user_id ORDER BY created_at ROWS UNBOUNDED PRECEDING) AS running_total
FROM transactions;
```

---

### Q10: ACID Properties

| Property | Meaning | Example |
|----------|---------|---------|
| **A**tomicity | All or nothing | Transfer: debit + credit both succeed or both fail |
| **C**onsistency | Data always valid | Balance can't go negative |
| **I**solation | Transactions don't interfere | Two concurrent transfers don't see each other's partial state |
| **D**urability | Committed data persists | After commit, data survives crash |

---

### Q11: Isolation Levels & Problems

```sql
-- Set isolation level (SQL Server)
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Oracle: ALTER SESSION SET ISOLATION_LEVEL = SERIALIZABLE;
```

| Problem | Description |
|---------|-------------|
| Dirty Read | Read uncommitted data from another transaction |
| Non-Repeatable Read | Same row read twice gives different values |
| Phantom Read | Same query returns different rows (INSERT by other tx) |

---

### Q12: SQL Coding Questions

**Q: Find the second highest salary:**
```sql
-- Option 1: DENSE_RANK
SELECT salary FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) WHERE rnk = 2;

-- Option 2: Subquery
SELECT MAX(salary) FROM employees
WHERE salary < (SELECT MAX(salary) FROM employees);
```

**Q: Find users who placed orders every month in 2024:**
```sql
SELECT user_id
FROM orders
WHERE YEAR(created_at) = 2024
GROUP BY user_id
HAVING COUNT(DISTINCT MONTH(created_at)) = 12;
```

**Q: Delete duplicate rows, keep one:**
```sql
DELETE FROM users
WHERE id NOT IN (
    SELECT MIN(id)
    FROM users
    GROUP BY email
);
```

**Q: Running total of transactions per user:**
```sql
SELECT
    user_id,
    created_at,
    amount,
    SUM(amount) OVER (
        PARTITION BY user_id
        ORDER BY created_at
        ROWS UNBOUNDED PRECEDING
    ) AS running_balance
FROM transactions;
```
