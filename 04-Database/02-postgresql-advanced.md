# 🐘 PostgreSQL Advanced — Phân Tích Chuyên Sâu Cho Phỏng Vấn

> **Phase:** 1-2 | **Time Block:** T7 13:30-15:30  
> **Quan trọng cho:** Tiki, Teko, TymeX, NAB, MoMo — Các công ty dùng PostgreSQL làm primary DB, xử lý dữ liệu lớn và tối ưu hóa truy vấn phức tạp.

---

## 1. Window Functions (Hàm Cửa Sổ)

### 1.1. Bản chất và So sánh với `GROUP BY`
*   **Window Function** là hàm thực hiện tính toán trên một tập hợp các hàng có liên quan đến dòng hiện tại (gọi là một **Window - Cửa sổ**), nhưng **KHÔNG gộp các hàng lại thành một dòng duy nhất** giống như `GROUP BY`.
*   Mỗi hàng trong tập kết quả vẫn giữ nguyên thông tin chi tiết và danh tính của nó, đồng thời có thêm cột giá trị tính toán của cửa sổ.

```
Tập dữ liệu gốc:
[User A, $10]
[User A, $20]

Với GROUP BY (gộp dòng):
-> [User A, $30] (Mất đi thông tin chi tiết của từng giao dịch $10 và $20)

Với WINDOW FUNCTION (giữ dòng + tính toán):
-> [User A, $10, Sum=$30]
-> [User A, $20, Sum=$30] (Vẫn biết giao dịch đầu tiên là $10, giao dịch hai là $20)
```

### 1.2. Cú pháp cốt lõi
```sql
FUNCTION() OVER (
    PARTITION BY column_name      -- Chia dữ liệu thành các nhóm độc lập (cửa sổ)
    ORDER BY column_name          -- Sắp xếp thứ tự các dòng trong từng nhóm để tính toán
    ROWS/RANGE frame_clause       -- Xác định phạm vi các dòng trượt xung quanh dòng hiện tại
)
```

### 1.3. Các hàm Window phổ biến và Ví dụ thực tế

#### A. Phân biệt `ROW_NUMBER()`, `RANK()`, và `DENSE_RANK()`
*   `ROW_NUMBER()`: Đánh số thứ tự tăng dần từ 1, không quan tâm trùng giá trị (luôn tăng).
*   `RANK()`: Đánh số thứ tự xếp hạng. Nếu trùng giá trị, các dòng sẽ có cùng hạng, nhưng hạng tiếp theo sẽ bị **nhảy cóc** (ví dụ: 1, 2, 2, 4).
*   `DENSE_RANK()`: Đánh số thứ tự xếp hạng. Nếu trùng giá trị, các dòng sẽ có cùng hạng, và hạng tiếp theo sẽ **không bị nhảy cóc** (ví dụ: 1, 2, 2, 3).

```sql
-- Ví dụ: Xếp hạng điểm số người chơi
SELECT 
    name, score,
    ROW_NUMBER() OVER (ORDER BY score DESC) AS row_num,
    RANK() OVER (ORDER BY score DESC) AS rank_val,
    DENSE_RANK() OVER (ORDER BY score DESC) AS dense_rank_val
FROM players;
```
*Kết quả giả lập:*
| name | score | row_num | rank_val | dense_rank_val |
|---|---|---|---|---|
| Alice | 100 | 1 | 1 | 1 |
| Bob | 90 | 2 | 2 | 2 |
| Charlie| 90 | 3 | 2 | 2 |
| David | 80 | 4 | 4 (nhảy cóc)| 3 (không nhảy)|

*Ứng dụng thực tế:* Xếp hạng học sinh, bảng xếp hạng game (Leaderboard), tìm top 3 sản phẩm bán chạy nhất trong từng danh mục hàng tuần.

#### B. `LAG()` và `LEAD()` (So sánh dòng trước/sau)
*   `LAG(column, offset)`: Lấy giá trị của cột đó ở dòng **phía trước** dòng hiện tại `offset` dòng.
*   `LEAD(column, offset)`: Lấy giá trị của cột đó ở dòng **phía sau** dòng hiện tại `offset` dòng.

```sql
-- Ví dụ: Tính chênh lệch doanh thu ngày hôm nay so với hôm trước
SELECT 
    date, revenue,
    LAG(revenue, 1) OVER (ORDER BY date) AS prev_day_revenue,
    revenue - LAG(revenue, 1) OVER (ORDER BY date) AS daily_change
FROM daily_stats;
```
*Ứng dụng thực tế:* Phân tích tăng trưởng doanh thu ngày-qua-ngày (DoD), tuần-qua-tuần (WoW), phát hiện biến động bất thường (anomalies) trong hệ thống giám sát.

#### C. Running Total (Tổng lũy kế trượt)
```sql
-- Ví dụ: Tính số dư lũy kế của tài khoản ngân hàng sau mỗi giao dịch
SELECT 
    transaction_id, user_id, amount, created_at,
    SUM(amount) OVER (
        PARTITION BY user_id 
        ORDER BY created_at 
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS balance
FROM transactions;
```
*Giải thích `ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW`:* Cửa sổ tính tổng sẽ bắt đầu từ dòng đầu tiên của nhóm (`UNBOUNDED PRECEDING`) kéo dài đến dòng hiện tại (`CURRENT ROW`). Khi di chuyển xuống dòng tiếp theo, cửa sổ tự động nới rộng thêm.

#### D. Moving Average (Trung bình động)
```sql
-- Ví dụ: Tính trung bình trượt doanh thu 7 ngày gần nhất để làm mượt biểu đồ
SELECT 
    date, revenue,
    AVG(revenue) OVER (
        ORDER BY date 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS avg_7day
FROM daily_stats;
```
*Giải thích `ROWS BETWEEN 6 PRECEDING AND CURRENT ROW`:* Lấy dữ liệu của 6 dòng phía trước cộng dòng hiện tại (tổng cộng 7 dòng) để chia trung bình.

### 1.4. Ưu và Nhược điểm của Window Functions
*   **Ưu điểm**:
    *   **Hiệu năng vượt trội**: DB Engine chỉ cần duyệt qua dữ liệu đã được phân hoạch/sắp xếp một lần để tính toán thay vì phải thực hiện các phép Self-Join (tự nối bảng với chính nó) vốn cực kỳ ngốn I/O và RAM.
    *   **SQL sạch**: Code ngắn gọn, dễ đọc và dễ bảo trì.
*   **Nhược điểm**:
    *   **Vấn đề WHERE**: Không thể lọc trực tiếp kết quả Window Function trong mệnh đề `WHERE` (vì nó được chạy sau cùng trong vòng đời thực thi SQL). Phải bọc trong một CTE hoặc Subquery.
    *   **Bộ nhớ**: Nếu `PARTITION BY` hoặc `ORDER BY` trên các trường không được đánh chỉ mục và bảng có hàng chục triệu bản ghi, database sẽ phải thực hiện sắp xếp ngoài đĩa (disk sort), làm chậm hệ thống.

---

## 2. Common Table Expressions (CTE) & Đệ Quy (`WITH RECURSIVE`)

### 2.1. CTE thông thường (Common Table Expression)
CTE là một tập kết quả tạm thời được đặt tên, khai báo bằng từ khóa `WITH`.

```sql
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

#### Materialization Barrier (Rào cản vật lý hóa)
*   **Trước PostgreSQL 12**: Mặc định, Postgres sẽ thực thi hoàn toàn các CTE độc lập và ghi kết quả tạm thời vào RAM/Disk trước khi chạy câu truy vấn chính. Nếu CTE trả về 1 triệu dòng nhưng câu truy vấn chính chỉ lọc lấy 1 dòng, Postgres vẫn phải tính toán cả 1 triệu dòng ➡️ Rất chậm.
*   **Từ PostgreSQL 12+**: Optimizer đã cải tiến. Nó sẽ tự động "inline" (gộp code CTE vào truy vấn chính) để tối ưu chỉ scan các dòng cần thiết, trừ khi bạn chỉ định rõ từ khóa `MATERIALIZED` (ví dụ: `WITH temp_table AS MATERIALIZED (...)`) khi muốn cố tình cache kết quả trung gian để tránh tính toán lại.

---

### 2.2. CTE Đệ Quy (`WITH RECURSIVE`)

#### Cú pháp và cấu trúc hoạt động
Truy vấn đệ quy được sử dụng khi dữ liệu có quan hệ dạng cha-con nhiều cấp mà chúng ta không biết trước độ sâu.

```sql
WITH RECURSIVE category_tree AS (
    -- 1. Anchor Member (Bản ghi gốc - Base Case)
    SELECT id, name, parent_id, 0 AS depth
    FROM categories
    WHERE parent_id IS NULL
    
    UNION ALL
    
    -- 2. Recursive Member (Bước đệ quy - Recursive Step)
    SELECT c.id, c.name, c.parent_id, ct.depth + 1
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT * FROM category_tree ORDER BY depth, name;
```

#### Giải thích chi tiết dòng code:
*   `WITH RECURSIVE category_tree AS (...)`: Khởi tạo một bảng tạm đệ quy tên là `category_tree`.
*   **Anchor Member**: Lấy ra các node cha cao nhất (gốc cây), nơi không có cha (`parent_id IS NULL`). Độ sâu ban đầu gán là `0`.
*   `UNION ALL`: Gộp kết quả của các tầng đệ quy lại với nhau.
*   **Recursive Member**: JOIN bảng vật lý `categories c` với chính bảng tạm `category_tree ct` đang được tích lũy. Điều kiện JOIN `c.parent_id = ct.id` có nghĩa là: *"Tìm tất cả các danh mục trong bảng categories có parent_id bằng id của danh mục ở tầng trước đó"*. Đồng thời tăng độ sâu lên 1 (`ct.depth + 1`).

#### Minh họa chạy tay (Dry-run) từng bước của DB Engine:
Giả sử bảng `categories` có các dòng dữ liệu sau:
*   (1, 'Điện tử', NULL)
*   (2, 'Thời trang', NULL)
*   (3, 'Điện thoại', 1)  -- Con của Điện tử
*   (4, 'Máy tính', 1)    -- Con của Điện tử
*   (5, 'iPhone', 3)      -- Con của Điện thoại
*   (6, 'MacBook', 4)     -- Con của Máy tính

DB Engine sẽ xử lý đệ quy qua các vòng lặp như sau:

```
[Vòng 1: Chạy Anchor Member]
Tìm các dòng parent_id IS NULL:
-> Tích lũy được:
   (1, 'Điện tử', NULL, depth=0)
   (2, 'Thời trang', NULL, depth=0)

[Vòng 2: Chạy Recursive Member lần 1]
Tìm các danh mục có parent_id nằm trong tập ID của Vòng 1 {1, 2}:
-> Khớp các dòng có parent_id = 1 (Điện thoại, Máy tính).
-> Tích lũy thêm:
   (3, 'Điện thoại', 1, depth=1)
   (4, 'Máy tính', 1, depth=1)

[Vòng 3: Chạy Recursive Member lần 2]
Tìm các danh mục có parent_id nằm trong tập ID mới của Vòng 2 {3, 4}:
-> Khớp các dòng có parent_id = 3 (iPhone) và parent_id = 4 (MacBook).
-> Tích lũy thêm:
   (5, 'iPhone', 3, depth=2)
   (6, 'MacBook', 4, depth=2)

[Vòng 4: Chạy Recursive Member lần 3]
Tìm các danh mục có parent_id nằm trong tập ID mới của Vòng 3 {5, 6}:
-> Không có danh mục nào có parent_id = 5 hoặc 6.
-> Vòng lặp trả về kết quả RỖNG.

[Kết thúc & Trả kết quả]
DB Engine dừng đệ quy. Thực hiện UNION ALL kết quả của Vòng 1 + Vòng 2 + Vòng 3.
```

---

## 3. JSONB (Binary JSON) trong PostgreSQL

PostgreSQL hỗ trợ 2 kiểu dữ liệu JSON: `JSON` (dạng text thuần) và `JSONB` (dạng nhị phân đã phân tích cú pháp).

### 3.1. So sánh JSON vs JSONB
| Đặc tính | JSON (Plain Text) | JSONB (Binary JSON) |
|:---|:---|:---|
| **Định dạng lưu trữ** | Lưu chuỗi text JSON gốc, giữ nguyên khoảng trắng và thứ tự key | Phân tích cú pháp thành cấu trúc nhị phân, loại bỏ khoảng trắng dư thừa |
| **Tốc độ chèn (Insert)** | Nhanh hơn (không cần phân tích cú pháp) | Chậm hơn một chút (tốn CPU phân tích) |
| **Tốc độ truy vấn (Query)** | Chậm (phải phân tích cú pháp mỗi lần đọc) | **Cực kỳ nhanh** (đọc trực tiếp nhị phân) |
| **Đánh chỉ mục (Indexing)** | Không hỗ trợ chỉ mục trực tiếp trên key | **Hỗ trợ GIN Index** (tối ưu hóa tìm kiếm) |

### 3.2. Cú pháp và Ví dụ thực chiến

```sql
-- Tạo bảng lưu trữ Event log dạng semi-structured
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(50),
    payload JSONB NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Chèn dữ liệu JSONB
INSERT INTO audit_logs (action, payload) VALUES 
('purchase', '{"user_id": 99, "items": [{"id": 10, "price": 150}, {"id": 11, "price": 50}], "metadata": {"ip": "1.1.1.1"}}');

-- 1. Truy xuất dữ liệu dùng toán tử -> (trả về JSONB) và ->> (trả về TEXT)
SELECT 
    payload->>'user_id' AS user_id_str,               -- text: "99"
    (payload->>'user_id')::int AS user_id_int,         -- ép kiểu sang int
    payload->'items'->0->>'price' AS first_item_price  -- trỏ sâu vào array: "150"
FROM audit_logs;

-- 2. Toán tử chứa đựng (@>) - Kiểm tra xem JSONB có chứa cấu trúc con này không
SELECT * FROM audit_logs 
WHERE payload @> '{"metadata": {"ip": "1.1.1.1"}}';

-- 3. Toán tử tồn tại (?) - Kiểm tra key có tồn tại trong JSONB không
SELECT * FROM audit_logs 
WHERE payload ? 'items';
```

### 3.3. Tối ưu hiệu năng JSONB với GIN Index
GIN (Generalized Inverted Index) cực kỳ phù hợp cho JSONB vì nó đánh chỉ mục cho từng key và value con bên trong đối tượng JSON.

```sql
-- Tạo index GIN cho toàn bộ payload
CREATE INDEX idx_audit_payload ON audit_logs USING GIN (payload);

-- Tạo index GIN tối ưu cho một đường dẫn cụ thể (ví dụ chỉ mục các phần tử trong array items)
CREATE INDEX idx_audit_payload_jsonb_path_ops ON audit_logs USING GIN (payload jsonb_path_ops);
```

*Khi nào dùng JSONB trong thực tế?*
*   **Audit logs**: Ghi nhận lịch sử thay đổi của các cấu trúc thực thể biến động.
*   **Semi-structured catalog**: Thông số kỹ thuật của sản phẩm thương mại điện tử (ví dụ: Laptop có RAM, CPU; nhưng Quần áo có Size, Màu sắc - không thể thiết kế cột cố định cho tất cả).

---

## 4. Table Partitioning (Phân Vùng Bảng)

Table Partitioning là chia một bảng lớn thành các bảng nhỏ hơn về mặt vật lý (gọi là các partition), nhưng về mặt logic, ứng dụng vẫn nhìn nhận nó là một bảng duy nhất.

### 4.1. Các loại Partitioning trong Postgres
1.  **Range Partitioning (Theo khoảng)**: Phổ biến nhất, chia theo thời gian hoặc ID tăng dần (ví dụ: partition theo từng tháng).
2.  **List Partitioning (Theo danh sách)**: Chia theo một danh sách giá trị cố định (ví dụ: partition theo quốc gia, status).
3.  **Hash Partitioning (Theo mã băm)**: Chia đều dữ liệu vào một số lượng bảng cố định bằng hàm băm (ví dụ: chia làm 4 partition để phân bổ tải đồng đều).

```sql
-- Ví dụ: Range Partitioning bảng transactions theo tháng
CREATE TABLE transactions (
    id BIGSERIAL,
    amount DECIMAL(15,2),
    created_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id, created_at) -- Partition key bắt buộc phải là một phần của khóa chính
) PARTITION BY RANGE (created_at);

-- Tạo các partition vật lý
CREATE TABLE transactions_2026_01 PARTITION OF transactions
    FOR VALUES FROM ('2026-01-01 00:00:00+00') TO ('2026-02-01 00:00:00+00');
    
CREATE TABLE transactions_2026_02 PARTITION OF transactions
    FOR VALUES FROM ('2026-02-01 00:00:00+00') TO ('2026-03-01 00:00:00+00');
```

### 4.2. Cơ chế Partition Pruning (Cắt tỉa phân vùng)
*   Khi bạn viết câu truy vấn có kèm theo filter của partition key (ví dụ: `WHERE created_at BETWEEN '2026-01-10' AND '2026-01-15'`), Postgres Optimizer sẽ kích hoạt cơ chế **Partition Pruning**.
*   Nó sẽ loại bỏ lập tức các partition khác khỏi kế hoạch thực thi và **chỉ scan duy nhất** bảng `transactions_2026_01`. Điều này giúp giảm thiểu lượng I/O đọc đĩa đáng kể, tăng tốc truy vấn từ vài chục giây xuống mili-giây trên các bảng hàng tỷ dòng.

---

## 5. Tối Ưu Hóa Truy Vấn & Đọc EXPLAIN ANALYZE

Để tối ưu hóa hiệu năng, chúng ta cần hiểu cách Database Engine lập kế hoạch thực thi và truy xuất dữ liệu từ đĩa cứng lên RAM.

### 5.1. Cơ chế hoạt động của Query Planner
Khi một câu lệnh SQL được gửi lên, Postgres sẽ chạy qua các bước:
1.  **Parser**: Kiểm tra cú pháp SQL.
2.  **Analyzer/Rewriter**: Xử lý view, rule và biến đổi câu truy vấn.
3.  **Optimizer (Planner)**: Đây là bước quan trọng nhất. Optimizer sẽ tính toán **Cost** (chi phí về CPU, RAM, Disk I/O) cho hàng loạt cách thực thi khác nhau (quét tuần tự, quét index, join kiểu gì) và chọn ra kế hoạch có Cost thấp nhất.
4.  **Executor**: Thực thi kế hoạch và trả về kết quả.

### 5.2. Các thuật ngữ cốt lõi trong EXPLAIN ANALYZE

#### A. Các kiểu quét dữ liệu (Data Scan Types)
*   **Seq Scan (Sequential Scan - Quét tuần tự)**: DB quét toàn bộ bảng từ đầu đến cuối để lọc dữ liệu. Cực kỳ chậm đối với bảng lớn. Đây là dấu hiệu của việc thiếu index hoặc index không được sử dụng.
*   **Index Scan (Quét chỉ mục)**: DB duyệt cây B-Tree của Index để tìm các con trỏ trỏ đến dòng dữ liệu vật lý (TID), sau đó truy cập xuống bảng vật lý để lấy đầy đủ các cột. Tối ưu khi lấy một lượng nhỏ dữ liệu.
*   **Index Only Scan (Chỉ quét chỉ mục)**: DB chỉ cần đọc dữ liệu trên cây Index mà không cần nhảy xuống bảng vật lý (Heap). Xảy ra khi tất cả các cột được yêu cầu trong câu lệnh SELECT đều nằm trong cấu trúc của Index (Covering Index). Đây là loại quét nhanh nhất.
*   **Bitmap Index Scan / Bitmap Heap Scan**: DB quét Index và tạo ra một bản đồ bit (bitmap) các trang dữ liệu (pages) chứa dòng thỏa mãn điều kiện. Sau đó, nó thực hiện quét các trang này (Bitmap Heap Scan) một cách tuần tự để tránh việc đọc đĩa ngẫu nhiên (random disk read) nhiều lần. Tối ưu khi cần lấy số lượng dòng ở mức trung bình.

#### B. Các cơ chế JOIN vật lý (Join Types)
Khi thực hiện phép nối hai bảng (Outer table và Inner table), DB Engine có 3 chiến thuật:
1.  **Nested Loop Join (Vòng lặp lồng nhau)**: 
    *   *Cách chạy:* Với mỗi dòng ở bảng ngoài, DB sẽ chạy xuống bảng trong để tìm dòng khớp.
    *   *Khi nào chọn:* Tối ưu nhất khi bảng ngoài rất nhỏ và cột JOIN của bảng trong có đánh Index.
2.  **Hash Join (Nối bằng bảng băm)**:
    *   *Cách chạy:* DB quét bảng nhỏ hơn và xây dựng một bảng băm (Hash Table) trong bộ nhớ RAM (`work_mem`). Sau đó, nó quét bảng lớn hơn, băm giá trị cột JOIN và đối chiếu nhanh vào bảng băm để tìm dòng khớp.
    *   *Khi nào chọn:* Tối ưu cho bảng lớn, không có index trên cột JOIN, nhưng tốn RAM để lưu bảng băm.
3.  **Merge Join (Nối trộn)**:
    *   *Cách chạy:* DB sắp xếp cả hai bảng theo cột JOIN (nếu chưa được sắp xếp sẵn bởi Index), sau đó chạy song song hai con trỏ trên hai bảng để ghép các dòng khớp với nhau.
    *   *Khi nào chọn:* Rất nhanh đối với bảng cực lớn nếu hai bảng đã được sắp xếp sẵn (bằng chỉ mục).

### 5.3. Cách đọc kết quả EXPLAIN ANALYZE
Sử dụng câu lệnh: `EXPLAIN (ANALYZE, BUFFERS, COSTS OFF) SELECT ...`
*   `ANALYZE`: Thực thi câu lệnh thực tế để lấy thời gian chạy thật (thay vì chỉ ước lượng).
*   `BUFFERS`: Hiển thị số lượng trang dữ liệu được đọc từ bộ đệm (shared hit) hoặc từ đĩa (read).

```
->  Hash Join  (actual time=12.450..45.120 rows=1250 loops=1)
      Hash Cond: (orders.user_id = users.id)
      ->  Seq Scan on orders  (actual time=0.012..18.520 rows=50000 loops=1)  <-- Cảnh báo: Seq Scan!
      ->  Hash  (actual time=1.200..1.200 rows=1000 loops=1)
            Buckets: 1024  Batches: 1  Memory Usage: 45kB
            ->  Index Scan using idx_users_status on users  (actual time=0.020..0.850 rows=1000 loops=1)
```
*Phân tích mẫu:*
1.  Đọc từ dưới lên trên, từ trong ra ngoài (theo các khoảng thụt dòng).
2.  DB thực hiện quét Index (`Index Scan using idx_users_status`) trên bảng `users` để lọc lấy 1000 dòng có status thỏa mãn. Tốn 0.85ms.
3.  1000 dòng này được nạp vào bảng băm (`Hash`) trong RAM, tiêu tốn 45kB.
4.  DB quét tuần tự bảng `orders` (`Seq Scan on orders`) lấy 50,000 dòng (tốn 18.52ms) vì không có index hỗ trợ.
5.  Thực hiện `Hash Join` kết hợp hai tập dữ liệu và trả ra 1250 dòng khớp. Tổng thời gian chạy thực tế là 45.12ms.
6.  *Hướng tối ưu hóa:* Cần tạo index trên `orders(user_id)` để loại bỏ `Seq Scan on orders`.

---

## 6. PostgreSQL vs MySQL: Kiến Trúc Phía Dưới

Khi đi phỏng vấn các công ty lớn, bạn thường bị hỏi so sánh chi tiết cơ chế lưu trữ của Postgres và MySQL (sử dụng Storage Engine InnoDB).

### 6.1. Cơ chế MVCC (Multi-Version Concurrency Control)
Cả hai đều dùng MVCC để xử lý transaction đồng thời, nhưng cách hiện thực hóa hoàn toàn khác nhau:
*   **MySQL (InnoDB)**: Sử dụng **Undo Log**. Khi một dòng dữ liệu bị cập nhật, InnoDB sẽ ghi đè trực tiếp lên dòng dữ liệu cũ trong trang bảng, đồng thời ghi lại phiên bản dữ liệu cũ vào Undo Log. Nếu transaction khác cần đọc phiên bản cũ, nó sẽ tìm trong Undo Log để dựng lại dữ liệu.
    *   *Ưu điểm:* File dữ liệu của bảng gọn gàng, không bị phình to (bloat).
*   **PostgreSQL**: Sử dụng **Write-Once (Tuple Versioning)**. Khi bạn UPDATE một dòng, Postgres không ghi đè mà sẽ chèn một dòng hoàn toàn mới (được gọi là một Tuple mới) với ID transaction ghi nhận tại cột ẩn `xmin` (ID tạo dòng) và đánh dấu ID transaction xóa ở cột ẩn `xmax` của dòng cũ.
    *   *Nhược điểm:* Gây ra hiện tượng **Bloat** (bảng bị phình to do chứa nhiều dòng dữ liệu rác đã cũ).
    *   *Giải pháp:* Postgres cần tiến trình chạy ngầm **VACUUM** (hoặc Autovacuum) định kỳ để dọn dẹp các dòng dữ liệu chết này và giải phóng không gian đĩa.

### 6.2. Cơ chế lưu trữ Index (Clustered vs Non-Clustered)
*   **MySQL (InnoDB)**: Index chính (Primary Key) là **Clustered Index**. Dữ liệu vật lý của dòng được sắp xếp và lưu trực tiếp tại các lá (leaf nodes) của cây chỉ mục khóa chính. Các chỉ mục phụ (Secondary Indexes) không chứa dữ liệu mà chứa giá trị của Khóa chính.
    *   *Hệ quả:* Query theo khóa phụ phải qua 2 lần duyệt cây (Duyệt cây khóa phụ -> lấy Khóa chính -> Duyệt cây khóa chính để lấy dữ liệu dòng).
*   **PostgreSQL**: Tất cả index (bao gồm cả Khóa chính) đều là **Non-Clustered Index**. Index chỉ chứa con trỏ vật lý (Tuple ID - TID gồm số hiệu trang và vị trí dòng) trỏ xuống bảng dữ liệu vật lý (gọi là Heap).
    *   *Hệ quả:* Truy vấn theo bất kỳ index nào đều chỉ cần duyệt 1 lần cây chỉ mục để lấy TID, sau đó truy cập trực tiếp xuống Heap. Tuy nhiên, nếu cập nhật dữ liệu dòng, tất cả các index trỏ đến dòng đó đều phải cập nhật lại con trỏ (trừ khi kích hoạt cơ chế tối ưu HOT - Heap Only Tuple).

---

## 7. Câu Hỏi Phỏng Vấn Kinh Điển (Senior/Tier 1)

### Q1: Autovacuum trong PostgreSQL hoạt động thế nào? Tại sao nó lại làm chậm hệ thống và cách cấu hình tối ưu?
**Trả lời**:
*   **Nguyên lý**: Do cơ chế MVCC của Postgres, các lệnh UPDATE và DELETE tạo ra các dòng dữ liệu cũ (dead tuples). `Autovacuum` là tiến trình chạy ngầm thực hiện quét các bảng để thu hồi không gian đĩa trống từ các dead tuples này và cập nhật statistic của bảng để Optimizer lập kế hoạch chính xác.
*   **Gây chậm**: Autovacuum đọc và ghi đĩa rất nhiều. Nếu cấu hình mặc định quá dịu dàng (thời gian nghỉ giữa các lần quét lớn, hạn mức đĩa được phép tác động thấp), nó sẽ không dọn dẹp kịp khi hệ thống bị write-heavy (ghi nhiều). Dẫn đến bảng phình quá to, và khi Autovacuum buộc phải chạy gắt hơn, nó sẽ tranh chấp I/O với ứng dụng gây nghẽn.
*   **Tối ưu**:
    *   Tăng tốc độ đọc ghi của Autovacuum bằng cách giảm `autovacuum_vacuum_delay` (mặc định 20ms xuống 2-5ms) để nó chạy liên tục dọn dẹp nhanh hơn.
    *   Tăng `autovacuum_max_workers` (mặc định 3 luồng) nếu hệ thống có nhiều bảng lớn cần dọn dẹp song song.
    *   Điều chỉnh ngưỡng kích hoạt dọn dẹp linh hoạt theo tỷ lệ phần trăm dòng thay đổi (`autovacuum_vacuum_scale_factor` mặc định là 0.2 tức 20% dữ liệu bảng thay đổi mới dọn dẹp ➡️ bảng 10 triệu dòng cần 2 triệu dòng thay đổi mới chạy vacuum là quá muộn. Nên hạ xuống 0.05 hoặc 0.1).

### Q2: Điều gì xảy ra khi bạn tạo một Composite Index trên ba cột `(a, b, c)`? Những câu truy vấn nào sẽ sử dụng được index này?
**Trả lời**:
PostgreSQL sử dụng quy tắc tiền tố bên trái (**Leftmost Prefix Rule**) cho chỉ mục tổng hợp (Composite Index). Index được xây dựng bằng cách sắp xếp dữ liệu theo thứ tự ưu tiên từ trái qua phải: sắp xếp theo `a` trước, nếu trùng `a` thì sắp xếp theo `b`, trùng `b` mới sắp theo `c`.

*   **Các câu truy vấn sử dụng được index**:
    *   `WHERE a = ?` (Sử dụng tối đa index)
    *   `WHERE a = ? AND b = ?` (Sử dụng tối đa index)
    *   `WHERE a = ? AND b = ? AND c = ?` (Sử dụng tối đa index)
    *   `WHERE a = ? AND c = ?` (Chỉ sử dụng phần index trên cột `a` để lọc nhanh, sau đó phải dùng CPU để duyệt thủ công cột `c`).
*   **Các câu truy vấn KHÔNG sử dụng được index**:
    *   `WHERE b = ?` (Vi phạm quy tắc tiền tố, thiếu cột `a`)
    *   `WHERE b = ? AND c = ?` (Vi phạm quy tắc tiền tố)
*   **Quy tắc thiết kế Composite Index**: Cột có độ lọc cao (High Cardinality - nhiều giá trị khác biệt như ID, Email) hoặc cột dùng phép so sánh bằng (`=`) nên được xếp bên trái. Cột dùng phép so sánh khoảng (`>`, `<`, `BETWEEN`) nên được xếp bên phải ngoài cùng.

### Q3: Phân biệt `UNION` và `UNION ALL`. Khi nào dùng loại nào để tối ưu hiệu năng?
**Trả lời**:
*   `UNION`: Gộp kết quả của hai hoặc nhiều truy vấn, đồng thời thực hiện loại bỏ các dòng trùng lặp (de-duplication).
    *   *Cơ chế:* DB Engine sau khi gộp sẽ phải thực hiện một thao tác sắp xếp (Sort) hoặc băm (Hash) trên toàn bộ tập dữ liệu để tìm và xóa dòng trùng. Thao tác này cực kỳ tốn CPU và RAM.
*   `UNION ALL`: Chỉ đơn thuần là nối kết quả của các truy vấn lại với nhau và trả về toàn bộ dữ liệu, chấp nhận có dòng trùng lặp.
    *   *Cơ chế:* DB Engine không cần kiểm tra trùng lặp, dữ liệu được trả về trực tiếp ngay lập tức dưới dạng stream.
*   **Tối ưu hiệu năng**: Luôn ưu tiên dùng `UNION ALL` nếu bạn chắc chắn rằng dữ liệu giữa hai truy vấn không bao giờ trùng lặp (ví dụ: truy vấn từ hai bảng khác nhau hoàn toàn) hoặc khi nghiệp vụ chấp nhận dữ liệu trùng lặp. Chỉ dùng `UNION` khi bắt buộc phải loại bỏ trùng lặp.
