# -- 04_databases/sql/sql_examples.sql
# -- tập lệnh này chứa các ví dụ SQL tiêu chuẩn bao gồm các phép nối, chỉ mục và giao dịch.

-- 1. Setup sample tables
-- 1. Cài đặt các bảng mẫu
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING'
);

-- 2. Insert Data
-- 2. Chèn dữ liệu
INSERT INTO users (username) VALUES ('alice'), ('bob'), ('charlie');
INSERT INTO orders (user_id, amount) VALUES (1, 50.00), (1, 120.00), (2, 75.50);

-- 3. JOIN Examples
-- 3. Ví dụ về phép nối
-- Inner Join
-- Phép nối trong
SELECT u.username, o.amount 
FROM users u 
INNER JOIN orders o ON u.id = o.user_id;

-- Left Join (Charlie will be included with NULL orders)
-- Phép nối trái (Charlie sẽ được bao gồm với các đơn hàng NULL)
SELECT u.username, o.amount 
FROM users u 
LEFT JOIN orders o ON u.id = o.user_id;

-- 4. Indexing Example
-- 4. Ví dụ về lập chỉ mục
-- Create an index to speed up lookups by status
-- Tạo một chỉ mục để tăng tốc độ tra cứu theo trạng thái
CREATE INDEX idx_order_status ON orders(status);

-- 5. Transaction Example
-- 5. Ví dụ về giao dịch
BEGIN;

UPDATE orders SET status = 'COMPLETED' WHERE id = 1;
-- Simulate complex business logic
-- Mô phỏng logic nghiệp vụ phức tạp
UPDATE users SET username = 'alice_updated' WHERE id = 1;

COMMIT;
