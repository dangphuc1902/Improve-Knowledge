# 📖 Database Oracle & MSSQL - SQL Nâng Cao, Interview & Bài Tập

> **Tuần 7 | Advanced SQL & Enterprise Databases**

---

## LÝ THUYẾT

### 1. SQL Cơ Bản - Ôn Nhanh

```sql
-- Sample schema
CREATE TABLE departments (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(100),
    budget DECIMAL(15,2)
);

CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(150),
    salary DECIMAL(10,2),
    department_id INT REFERENCES departments(id),
    manager_id INT REFERENCES employees(id),  -- self-reference
    hire_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);
```

---

### 2. JOINs

```sql
-- INNER JOIN - chỉ records có match cả 2 bảng
SELECT e.name, d.name AS dept_name
FROM employees e
INNER JOIN departments d ON e.department_id = d.id;

-- LEFT JOIN - tất cả từ bảng trái + match từ phải (NULL nếu không có)
SELECT e.name, d.name AS dept_name
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id;
-- Sẽ hiện employees không có department (dept_name = NULL)

-- RIGHT JOIN - ngược lại LEFT JOIN
SELECT e.name, d.name AS dept_name
FROM employees e
RIGHT JOIN departments d ON e.department_id = d.id;
-- Sẽ hiện departments không có employee

-- FULL OUTER JOIN - tất cả records từ cả 2 bảng
SELECT e.name, d.name AS dept_name
FROM employees e
FULL OUTER JOIN departments d ON e.department_id = d.id;

-- SELF JOIN - join bảng với chính nó (manager hierarchy)
SELECT e.name AS employee, m.name AS manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;

-- CROSS JOIN - tích Cartesian (mọi kết hợp)
SELECT p.name, c.name AS color
FROM products p
CROSS JOIN colors c;
```

---

### 3. Subquery & CTEs

```sql
-- Subquery trong WHERE
SELECT name, salary
FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);

-- Subquery trong FROM (derived table)
SELECT dept_name, avg_salary
FROM (
    SELECT d.name AS dept_name, AVG(e.salary) AS avg_salary
    FROM employees e JOIN departments d ON e.department_id = d.id
    GROUP BY d.name
) dept_stats
WHERE avg_salary > 50000;

-- CTE (Common Table Expression) - dễ đọc hơn
WITH dept_avg AS (
    SELECT d.id, d.name, AVG(e.salary) AS avg_salary
    FROM employees e JOIN departments d ON e.department_id = d.id
    GROUP BY d.id, d.name
),
high_paying_depts AS (
    SELECT id, name FROM dept_avg WHERE avg_salary > 60000
)
SELECT e.name, e.salary, h.name AS dept_name
FROM employees e
JOIN high_paying_depts h ON e.department_id = h.id
ORDER BY e.salary DESC;

-- Recursive CTE - organization hierarchy
WITH RECURSIVE emp_hierarchy AS (
    -- Base case: CEO (no manager)
    SELECT id, name, manager_id, 0 AS level
    FROM employees WHERE manager_id IS NULL
    
    UNION ALL
    
    -- Recursive case: employees với manager
    SELECT e.id, e.name, e.manager_id, h.level + 1
    FROM employees e
    INNER JOIN emp_hierarchy h ON e.manager_id = h.id
)
SELECT CONCAT(REPEAT('  ', level), name) AS name, level
FROM emp_hierarchy
ORDER BY level, name;
```

---

### 4. Window Functions

```sql
-- ROW_NUMBER, RANK, DENSE_RANK
SELECT 
    name,
    salary,
    department_id,
    ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY salary DESC) AS row_num,
    RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS rank,
    DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS dense_rank
FROM employees;

-- Lấy top 3 lương cao nhất mỗi department
WITH ranked AS (
    SELECT name, salary, department_id,
           ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY salary DESC) AS rn
    FROM employees
)
SELECT name, salary, department_id
FROM ranked WHERE rn <= 3;

-- LAG/LEAD - so sánh với row trước/sau
SELECT 
    hire_date,
    COUNT(*) AS new_hires,
    LAG(COUNT(*)) OVER (ORDER BY hire_date) AS prev_month_hires,
    COUNT(*) - LAG(COUNT(*)) OVER (ORDER BY hire_date) AS change
FROM employees
GROUP BY hire_date
ORDER BY hire_date;

-- SUM, AVG running total
SELECT 
    name,
    salary,
    SUM(salary) OVER (PARTITION BY department_id ORDER BY hire_date) AS running_total,
    AVG(salary) OVER (PARTITION BY department_id) AS dept_avg
FROM employees;
```

---

### 5. Indexes & Query Optimization

```sql
-- Tạo index
CREATE INDEX idx_emp_email ON employees(email);
CREATE UNIQUE INDEX idx_emp_unique_email ON employees(email);
CREATE INDEX idx_emp_dept_status ON employees(department_id, status);  -- Composite

-- Khi nào nên đánh index?
-- ✅ Columns dùng trong WHERE, JOIN ON, ORDER BY
-- ✅ Foreign keys
-- ✅ Columns trong unique constraints
-- ❌ Columns ít cardinality (boolean, status với 2-3 giá trị)
-- ❌ Bảng nhỏ (< 1000 rows)
-- ❌ Columns thường xuyên UPDATE

-- EXPLAIN / Execution Plan
EXPLAIN SELECT * FROM employees WHERE email = 'alice@example.com';
-- Kiểm tra: "Using index" → tốt, "Full table scan" → cần index

-- Query optimization tips
-- ❌ Avoid functions on indexed columns in WHERE
WHERE YEAR(hire_date) = 2024  -- không dùng index
-- ✅ Use range instead
WHERE hire_date >= '2024-01-01' AND hire_date < '2025-01-01'

-- ❌ Avoid SELECT *
SELECT * FROM employees  -- Load tất cả columns
-- ✅ Select only needed
SELECT id, name, email FROM employees

-- ❌ LIKE với leading wildcard
WHERE name LIKE '%Smith'  -- Full scan
-- ✅ LIKE với trailing wildcard (có thể dùng index)
WHERE name LIKE 'Smith%'
```

---

### 6. Stored Procedures

```sql
-- MySQL Stored Procedure
DELIMITER $$
CREATE PROCEDURE GetEmployeesByDept(
    IN dept_id INT,
    IN min_salary DECIMAL(10,2)
)
BEGIN
    SELECT e.id, e.name, e.salary, d.name AS dept_name
    FROM employees e
    JOIN departments d ON e.department_id = d.id
    WHERE e.department_id = dept_id
      AND e.salary >= min_salary
    ORDER BY e.salary DESC;
END$$
DELIMITER ;

-- Gọi procedure
CALL GetEmployeesByDept(1, 50000);

-- Oracle PL/SQL Stored Procedure
CREATE OR REPLACE PROCEDURE transfer_budget(
    p_from_dept NUMBER,
    p_to_dept   NUMBER,
    p_amount    NUMBER
)
AS
    v_from_budget NUMBER;
BEGIN
    SELECT budget INTO v_from_budget
    FROM departments WHERE id = p_from_dept
    FOR UPDATE;  -- Lock row
    
    IF v_from_budget < p_amount THEN
        RAISE_APPLICATION_ERROR(-20001, 'Insufficient budget');
    END IF;
    
    UPDATE departments SET budget = budget - p_amount WHERE id = p_from_dept;
    UPDATE departments SET budget = budget + p_amount WHERE id = p_to_dept;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
```

---

### 7. Oracle vs MSSQL Syntax Differences

| Tính năng | Oracle | SQL Server |
|-----------|--------|-----------|
| Auto-increment | `SEQUENCE` + `TRIGGER` hoặc `GENERATED ALWAYS AS IDENTITY` | `IDENTITY(1,1)` |
| Top N rows | `ROWNUM`, `FETCH FIRST N ROWS` | `TOP N`, `FETCH NEXT N ROWS ONLY` |
| String concat | `\|\|` hoặc `CONCAT()` | `+` hoặc `CONCAT()` |
| Date functions | `SYSDATE`, `TO_DATE()` | `GETDATE()`, `CAST()` |
| Null coalesce | `NVL()`, `COALESCE()` | `ISNULL()`, `COALESCE()` |
| If/else | `DECODE()`, `CASE WHEN` | `IIF()`, `CASE WHEN` |
| Dual table | `FROM DUAL` (required) | Không cần |

```sql
-- Oracle: Top 10 highest salaries
SELECT * FROM employees
ORDER BY salary DESC
FETCH FIRST 10 ROWS ONLY;  -- Oracle 12c+

-- SQL Server: Top 10 highest salaries
SELECT TOP 10 * FROM employees
ORDER BY salary DESC;

-- Oracle: Next sequence value
CREATE SEQUENCE emp_seq START WITH 1 INCREMENT BY 1;
SELECT emp_seq.NEXTVAL FROM DUAL;

-- SQL Server: Identity
CREATE TABLE employees (id INT IDENTITY(1,1) PRIMARY KEY, ...);
```

---

## INTERVIEW Q&A

### Q1: INDEX có loại nào? Khi nào cần đánh index?

**Loại index:**
- **B-Tree Index**: Mặc định, tốt cho range queries, equality
- **Hash Index**: Chỉ equality, rất nhanh
- **Bitmap Index** (Oracle): Columns với ít distinct values
- **Clustered Index** (MSSQL): Sắp xếp physical data theo index (mỗi bảng 1 cái)
- **Non-clustered**: Tách biệt với data

**Khi đánh index:** Columns trong WHERE, JOIN, ORDER BY có nhiều distinct values và query thường xuyên.

---

### Q2: Giải thích ACID trong SQL?

- **A**tomicity: Toàn bộ transaction hoặc không
- **C**onsistency: DB luôn ở trạng thái hợp lệ
- **I**solation: Transactions không ảnh hưởng nhau
- **D**urability: Committed data tồn tại vĩnh cửu

---

### Q3: Isolation Levels?

| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|-------|-----------|--------------------| -------------|
| READ UNCOMMITTED | ✅ Có thể | ✅ | ✅ |
| READ COMMITTED | ❌ Không | ✅ | ✅ |
| REPEATABLE READ | ❌ | ❌ | ✅ |
| SERIALIZABLE | ❌ | ❌ | ❌ |

---

### Q4: View là gì? Materialized View?

- **View**: Virtual table = stored SELECT query. Không lưu data, query khi access.
- **Materialized View** (Oracle): Lưu kết quả query vật lý, refresh periodically. Nhanh hơn view thường.

---

## BÀI TẬP SQL

```sql
-- Schema: employees, departments, projects, employee_projects

-- 1. Liệt kê nhân viên cùng department với 'Alice'
-- 2. Departments có avg salary cao hơn avg salary toàn công ty
-- 3. Top 3 nhân viên lương cao nhất ở mỗi department
-- 4. Nhân viên không tham gia project nào
-- 5. Department có số nhân viên nhiều nhất
-- 6. Running total salary theo hire_date
-- 7. Employee-Manager hierarchy (recursive)
-- 8. Tháng nào có nhiều người được tuyển nhất (last 2 years)
```

---

*📌 Tiếp theo: [10-Design-Patterns](../10-Design-Patterns/Theory.md)*
