# PART 9 - DATABASE (Oracle, SQL Server, General SQL)

> **Topics**: Indexes · Execution Plan · ACID · Isolation Levels · Window Functions · Stored Procedures

---

## ORACLE

### Q1. Indexes in Oracle — Types and Internals

**B-Tree Index (Default):**
```sql
-- Create index
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Composite index (column order matters!)
CREATE INDEX idx_orders_status_date ON orders(status, created_date);
-- Efficient for: WHERE status = 'ACTIVE'
-- Efficient for: WHERE status = 'ACTIVE' AND created_date > SYSDATE - 30
-- NOT efficient for: WHERE created_date > SYSDATE - 30 (leading column missing)
```

**Bitmap Index (for low-cardinality columns):**
```sql
-- Good for columns with few distinct values (status, gender, boolean flags)
CREATE BITMAP INDEX idx_orders_status_bitmap ON orders(status);
-- Excellent for data warehouse queries, BAD for OLTP (lock contention)
```

**Function-Based Index:**
```sql
-- When you always query with UPPER(email)
CREATE INDEX idx_users_email_upper ON users(UPPER(email));

SELECT * FROM users WHERE UPPER(email) = 'USER@EXAMPLE.COM'; -- Uses index!
```

**Partial Index (Oracle: Filtered):**
```sql
-- Index only active records
CREATE INDEX idx_active_orders ON orders(customer_id)
WHERE status = 'ACTIVE';  -- In Oracle use: INCLUDE ... WHERE
```

**When NOT to use an index:**
- Columns with very low cardinality (e.g., boolean Y/N)
- Small tables (full scan is faster)
- Columns rarely used in WHERE/JOIN/ORDER BY
- High-DML tables (index maintenance overhead)

---

### Q2. Reading Oracle Execution Plans

```sql
-- Generate execution plan
EXPLAIN PLAN FOR
SELECT o.*, c.name
FROM orders o
JOIN customers c ON c.id = o.customer_id
WHERE o.status = 'ACTIVE'
AND o.created_date > SYSDATE - 30;

-- View the plan
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY());

-- Key operations to recognize:
-- TABLE ACCESS FULL   → Full table scan (bad for large tables)
-- INDEX RANGE SCAN    → Using index with range condition (good)
-- INDEX UNIQUE SCAN   → Using unique index (optimal)
-- HASH JOIN           → Joins large tables efficiently
-- NESTED LOOPS        → Good for small tables with index
-- SORT (MERGE JOIN)   → Joining on sorted columns
-- FILTER              → Post-join filtering

-- Force index hint (when optimizer makes wrong choice)
SELECT /*+ INDEX(o idx_orders_status_date) */ o.*
FROM orders o
WHERE o.status = 'ACTIVE'
AND o.created_date > SYSDATE - 30;
```

---

### Q3. Oracle Sequences

```sql
-- Create sequence
CREATE SEQUENCE order_seq
    START WITH 1000
    INCREMENT BY 1
    MAXVALUE 9999999999
    NOCACHE          -- Cache = performance vs gap risk
    NOCYCLE;         -- Don't restart after max

-- Or with cache for performance
CREATE SEQUENCE order_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 50;        -- Pre-fetches 50 values into memory

-- Usage
INSERT INTO orders (id, status, created_date)
VALUES (order_seq.NEXTVAL, 'NEW', SYSDATE);

-- Current value (only after calling NEXTVAL in same session)
SELECT order_seq.CURRVAL FROM DUAL;

-- With JPA
@SequenceGenerator(
    name = "order_seq",
    sequenceName = "ORDER_SEQ",
    allocationSize = 50  // Must match Oracle sequence cache!
)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
private Long id;
```

---

### Q4. Oracle Stored Procedures

```sql
-- Basic stored procedure
CREATE OR REPLACE PROCEDURE transfer_funds(
    p_from_account IN NUMBER,
    p_to_account   IN NUMBER,
    p_amount       IN NUMBER,
    p_result       OUT VARCHAR2
) AS
    v_from_balance NUMBER;
    v_to_balance   NUMBER;
    
    insufficient_funds EXCEPTION;
BEGIN
    -- Pessimistic lock on both accounts
    SELECT balance INTO v_from_balance
    FROM accounts
    WHERE account_id = p_from_account
    FOR UPDATE;
    
    IF v_from_balance < p_amount THEN
        RAISE insufficient_funds;
    END IF;
    
    -- Debit
    UPDATE accounts SET balance = balance - p_amount
    WHERE account_id = p_from_account;
    
    -- Credit
    UPDATE accounts SET balance = balance + p_amount
    WHERE account_id = p_to_account;
    
    -- Audit
    INSERT INTO audit_log (from_acc, to_acc, amount, trans_date)
    VALUES (p_from_account, p_to_account, p_amount, SYSDATE);
    
    COMMIT;
    p_result := 'SUCCESS';

EXCEPTION
    WHEN insufficient_funds THEN
        ROLLBACK;
        p_result := 'INSUFFICIENT_FUNDS';
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := 'ERROR: ' || SQLERRM;
END;
/

-- Call in Java with Spring JDBC
SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
    .withProcedureName("TRANSFER_FUNDS");

MapSqlParameterSource params = new MapSqlParameterSource()
    .addValue("P_FROM_ACCOUNT", fromId)
    .addValue("P_TO_ACCOUNT", toId)
    .addValue("P_AMOUNT", amount);

Map<String, Object> result = call.execute(params);
String status = (String) result.get("P_RESULT");
```

---

### Q5. Oracle Locking

**Row-Level Locking (default in Oracle):**
```sql
-- Pessimistic: Lock rows for update
SELECT * FROM orders WHERE id = 100 FOR UPDATE;
SELECT * FROM orders WHERE customer_id = 5 FOR UPDATE SKIP LOCKED; -- Skip already locked rows

-- Check for lock contention
SELECT s.username, s.sid, s.serial#, l.type, l.lmode, o.object_name
FROM v$lock l
JOIN v$session s ON s.sid = l.sid
JOIN dba_objects o ON o.object_id = l.id1
WHERE s.username IS NOT NULL;
```

---

## SQL SERVER

### Q6. Clustered vs Non-Clustered Index

```sql
-- Clustered Index: Determines physical sort order of table data
-- Only ONE per table (usually primary key)
CREATE CLUSTERED INDEX PK_Orders ON orders(order_id);

-- Non-Clustered Index: Separate structure with pointer to data row
-- Multiple allowed per table
CREATE NONCLUSTERED INDEX IX_Orders_Customer ON orders(customer_id)
INCLUDE (status, total_amount);  -- Covering index: avoids key lookup!

-- Query that benefits from covering index above:
SELECT customer_id, status, total_amount FROM orders WHERE customer_id = 100;
-- Only index scan needed — no key lookup to table!
```

**Index with SQL Server — Query Optimization:**
```sql
-- Check missing indexes (SQL Server suggests them)
SELECT * FROM sys.dm_db_missing_index_details;

-- Check index usage stats
SELECT 
    i.name AS IndexName,
    ius.user_seeks,
    ius.user_scans,
    ius.user_lookups,
    ius.user_updates,
    ius.last_user_seek
FROM sys.indexes i
JOIN sys.dm_db_index_usage_stats ius 
    ON i.object_id = ius.object_id AND i.index_id = ius.index_id
WHERE OBJECT_NAME(i.object_id) = 'orders';

-- Execution plan
SET STATISTICS IO ON;
SET STATISTICS TIME ON;
SELECT * FROM orders WHERE customer_id = 100;
```

---

## GENERAL SQL

### Q7. Advanced JOIN Types

```sql
-- Sample data setup
-- customers: id, name, city
-- orders: id, customer_id, amount, status

-- INNER JOIN: Only matching rows
SELECT c.name, COUNT(o.id) AS order_count
FROM customers c
INNER JOIN orders o ON o.customer_id = c.id
GROUP BY c.id, c.name;

-- LEFT JOIN: All customers, even without orders
SELECT c.name, COALESCE(SUM(o.amount), 0) AS total_spent
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.id
GROUP BY c.id, c.name;

-- CROSS JOIN: Cartesian product (every combination)
SELECT p.name AS product, s.name AS store
FROM products p
CROSS JOIN stores s;
-- Use case: Generate all possible product-store combinations

-- SELF JOIN: Join table with itself
SELECT e1.name AS employee, e2.name AS manager
FROM employees e1
LEFT JOIN employees e2 ON e1.manager_id = e2.id;
```

---

### Q8. Window Functions — Advanced SQL

```sql
-- ROW_NUMBER: Unique sequential number
SELECT 
    customer_id,
    order_id,
    amount,
    ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY amount DESC) AS rn
FROM orders;

-- Get top 1 order per customer
SELECT * FROM (
    SELECT 
        customer_id, order_id, amount,
        ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY amount DESC) AS rn
    FROM orders
) ranked
WHERE rn = 1;

-- RANK vs DENSE_RANK vs ROW_NUMBER
-- RANK: 1,2,2,4 (skips after tie)
-- DENSE_RANK: 1,2,2,3 (no skip)
-- ROW_NUMBER: 1,2,3,4 (always unique)

-- Running Total (Cumulative Sum)
SELECT 
    order_date,
    amount,
    SUM(amount) OVER (ORDER BY order_date ROWS UNBOUNDED PRECEDING) AS running_total
FROM orders;

-- Moving Average (last 7 days)
SELECT 
    sale_date,
    daily_sales,
    AVG(daily_sales) OVER (
        ORDER BY sale_date 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_avg_7day
FROM daily_sales;

-- LAG/LEAD: Access previous/next row
SELECT 
    customer_id,
    order_date,
    amount,
    LAG(amount, 1) OVER (PARTITION BY customer_id ORDER BY order_date) AS prev_order_amount,
    LEAD(amount, 1) OVER (PARTITION BY customer_id ORDER BY order_date) AS next_order_amount,
    amount - LAG(amount, 1) OVER (PARTITION BY customer_id ORDER BY order_date) AS change
FROM orders;

-- NTILE: Divide into N groups (quartiles, deciles)
SELECT 
    customer_id,
    total_spent,
    NTILE(4) OVER (ORDER BY total_spent) AS spending_quartile
FROM customer_totals;
```

---

### Q9. GROUP BY, HAVING, Subqueries

```sql
-- Complex GROUP BY with ROLLUP
SELECT 
    department,
    job_title,
    COUNT(*) AS headcount,
    AVG(salary) AS avg_salary
FROM employees
GROUP BY ROLLUP (department, job_title);
-- Shows subtotals per department and grand total

-- HAVING vs WHERE:
-- WHERE: filters rows BEFORE grouping
-- HAVING: filters groups AFTER grouping
SELECT 
    customer_id,
    COUNT(*) AS order_count,
    SUM(amount) AS total_amount
FROM orders
WHERE status = 'COMPLETED'          -- Filter rows BEFORE aggregation
GROUP BY customer_id
HAVING COUNT(*) > 5                 -- Filter groups AFTER aggregation
AND SUM(amount) > 1000;

-- Correlated Subquery
SELECT c.name, c.city
FROM customers c
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.customer_id = c.id
    AND o.amount > 5000
    AND o.status = 'COMPLETED'
);

-- Subquery in FROM (Derived Table)
SELECT dept, avg_salary
FROM (
    SELECT department AS dept, AVG(salary) AS avg_salary
    FROM employees
    GROUP BY department
) dept_avg
WHERE avg_salary > 50000;
```

---

### Q10. ACID Properties and Isolation Levels

**ACID:**
| Property | Description | Example |
|---|---|---|
| **A**tomicity | All or nothing | Bank transfer: debit + credit must both succeed |
| **C**onsistency | Data remains valid | Account balance can't go negative (if enforced) |
| **I**solation | Concurrent transactions don't interfere | Two transfers don't see each other's partial state |
| **D**urability | Committed data survives failures | System crash after commit — data still there |

**Isolation Levels and Problems:**

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| READ UNCOMMITTED | ✅ Possible | ✅ Possible | ✅ Possible |
| READ COMMITTED | ❌ Prevented | ✅ Possible | ✅ Possible |
| REPEATABLE READ | ❌ Prevented | ❌ Prevented | ✅ Possible |
| SERIALIZABLE | ❌ Prevented | ❌ Prevented | ❌ Prevented |

```sql
-- Problems:
-- Dirty Read: Read uncommitted data that might be rolled back
-- Non-Repeatable Read: Same row reads different values in same transaction
-- Phantom Read: Range query returns different rows on second execution

-- Oracle default: READ COMMITTED
-- MySQL (InnoDB) default: REPEATABLE READ
-- Spring default: Follows database default

-- Set in Spring:
@Transactional(isolation = Isolation.READ_COMMITTED)
public void doSomething() { ... }
```

---

### SQL Coding Questions

**Q: Find customers who placed more than 3 orders in the last 30 days**
```sql
SELECT c.id, c.name, COUNT(o.id) AS recent_orders
FROM customers c
JOIN orders o ON o.customer_id = c.id
WHERE o.created_date >= CURRENT_DATE - INTERVAL '30' DAY
  AND o.status != 'CANCELLED'
GROUP BY c.id, c.name
HAVING COUNT(o.id) > 3
ORDER BY recent_orders DESC;
```

**Q: Find the second highest salary in each department**
```sql
SELECT department, salary
FROM (
    SELECT 
        department,
        salary,
        DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS rank_num
    FROM employees
) ranked
WHERE rank_num = 2;
```

**Q: Find duplicate emails in users table**
```sql
SELECT email, COUNT(*) AS count
FROM users
GROUP BY email
HAVING COUNT(*) > 1
ORDER BY count DESC;

-- Delete duplicates, keeping lowest id
DELETE FROM users
WHERE id NOT IN (
    SELECT MIN(id)
    FROM users
    GROUP BY email
);
```

**Q: Hierarchical query (Oracle) — Employee reporting chain**
```sql
SELECT 
    LEVEL,
    LPAD(' ', (LEVEL-1)*2) || name AS org_chart,
    manager_id
FROM employees
START WITH manager_id IS NULL    -- Root: CEO
CONNECT BY PRIOR id = manager_id -- Parent → Child relationship
ORDER SIBLINGS BY name;
```

---

*Next: [Part 10 - Web Services](./Part-10-12-WebServices-Microservices-AppServer.md)*
