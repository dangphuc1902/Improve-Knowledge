# SQL Joins
# Các phép nối SQL

## Concept Explanation
## Giải thích khái niệm
A JOIN clause is used to combine rows from two or more tables, based on a related column between them. This is essential in relational databases for assembling normalized data back into a readable format.
Một mệnh đề JOIN được sử dụng để kết hợp các hàng từ hai hoặc nhiều bảng, dựa trên một cột liên quan giữa chúng. Điều này là cần thiết trong các cơ sở dữ liệu quan hệ để lắp ráp dữ liệu đã được chuẩn hóa trở lại thành một định dạng có thể đọc được.

### Types of SQL Joins
### Các loại phép nối SQL
1. **INNER JOIN**: Returns records that have matching values in both tables.
1. **INNER JOIN**: Trả về các bản ghi có các giá trị khớp trong cả hai bảng.
2. **LEFT (OUTER) JOIN**: Returns all records from the left table, and the matched records from the right table. The result is NULL from the right side if there is no match.
2. **LEFT (OUTER) JOIN**: Trả về tất cả các bản ghi từ bảng bên trái và các bản ghi khớp từ bảng bên phải. Kết quả là NULL từ phía bên phải nếu không có kết quả khớp.
3. **RIGHT (OUTER) JOIN**: Returns all records from the right table, and the matched records from the left table.
3. **RIGHT (OUTER) JOIN**: Trả về tất cả các bản ghi từ bảng bên phải và các bản ghi khớp từ bảng bên trái.
4. **FULL (OUTER) JOIN**: Returns all records when there is a match in either left or right table.
4. **FULL (OUTER) JOIN**: Trả về tất cả các bản ghi khi có một kết quả khớp trong bảng bên trái hoặc bên phải.

```mermaid
venn
    title SQL Joins Overview
    %% Note: Mermaid doesn't support complex venn natively well yet, but conceptually:
    %% Inner Join is intersection. Left Join is Left Circle + Intersection.
```

## Practical Example
## Ví dụ thực tế
Consider two tables: `Users` and `Orders`.
Hãy xem xét hai bảng: `Users` và `Orders`.

**Users Table**
**Bảng người dùng**
| user_id | name |
|---|---|
| 1 | Alice |
| 2 | Bob |
| 3 | Charlie|

**Orders Table**
**Bảng đơn hàng**
| order_id | user_id | amount |
|---|---|---|
| 101 | 1 | $50 |
| 102 | 1 | $20 |
| 103 | 2 | $100|

```sql
-- INNER JOIN: Get users who have made orders and order details
-- INNER JOIN: Lấy người dùng đã đặt hàng và chi tiết đơn hàng
SELECT Users.name, Orders.order_id, Orders.amount
FROM Users
INNER JOIN Orders ON Users.user_id = Orders.user_id;
-- Output will NOT include Charlie, as he has no orders.
-- Đầu ra sẽ KHÔNG bao gồm Charlie, vì anh ấy không có đơn đặt hàng nào.

-- LEFT JOIN: Get all users, and their orders if they have any
-- LEFT JOIN: Lấy tất cả người dùng và đơn đặt hàng của họ nếu có
SELECT Users.name, Orders.order_id
FROM Users
LEFT JOIN Orders ON Users.user_id = Orders.user_id;
-- Output WILL include Charlie, with a NULL order_id.
-- Đầu ra SẼ bao gồm Charlie, với một order_id NULL.
```

## Exercises
## Bài tập
1. Setup a local PostgreSQL or MySQL instance. Create the `Users` and `Orders` tables from above. Insert the data.
1. Cài đặt một phiên bản PostgreSQL hoặc MySQL cục bộ. Tạo các bảng `Users` và `Orders` từ trên. Chèn dữ liệu.
2. Write a query to find all users who have **NOT** placed any orders (Hint: `LEFT JOIN` combined with `WHERE Orders.id IS NULL`).
2. Viết một truy vấn để tìm tất cả người dùng **KHÔNG** đặt bất kỳ đơn hàng nào (Gợi ý: `LEFT JOIN` kết hợp với `WHERE Orders.id IS NULL`).
3. Explain the difference between `JOIN` and `UNION`.
3. Giải thích sự khác biệt giữa `JOIN` và `UNION`.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Memorize the logical differences between INNER, LEFT, RIGHT, and FULL joins.
- Ghi nhớ sự khác biệt logic giữa các phép nối INNER, LEFT, RIGHT và FULL.
- Be able to analyze performance bottlenecks due to missing indexes on JOIN columns.
- Có thể phân tích các tắc nghẽn hiệu suất do thiếu các chỉ mục trên các cột JOIN.
- What is a cross join? (Cartesian product of both tables).
- Phép nối chéo là gì? (Tích Descartes của cả hai bảng).
