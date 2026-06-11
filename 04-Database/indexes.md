# Database Indexes
# Chỉ mục cơ sở dữ liệu

## Concept Explanation
## Giải thích khái niệm
An index is a data structure (usually a B-Tree or Hash) that improves the speed of data retrieval operations on a database table at the cost of additional writes and storage space.
Một chỉ mục là một cấu trúc dữ liệu (thường là B-Tree hoặc Hash) giúp cải thiện tốc độ của các hoạt động truy xuất dữ liệu trên một bảng cơ sở dữ liệu với chi phí ghi thêm và không gian lưu trữ.

Think of an index like an index at the back of a textbook. Instead of reading every page to find a topic (a Full Table Scan), you look at the index to find the exact page number.
Hãy nghĩ về một chỉ mục giống như một chỉ mục ở cuối sách giáo khoa. Thay vì đọc mọi trang để tìm một chủ đề (Quét toàn bộ bảng), bạn hãy xem chỉ mục để tìm số trang chính xác.

### Types of Indexes
### Các loại chỉ mục
1. **Primary Index**: Automatically created when a Primary Key is defined.
1. **Chỉ mục chính**: Tự động được tạo khi một Khóa chính được xác định.
2. **Unique Index**: Ensures all values in the indexed column are unique.
2. **Chỉ mục duy nhất**: Đảm bảo tất cả các giá trị trong cột được lập chỉ mục là duy nhất.
3. **Composite/Compound Index**: An index covering multiple columns.
3. **Chỉ mục tổng hợp/hợp chất**: Một chỉ mục bao gồm nhiều cột.
4. **Clustered Index**: The actual table data is physically sorted on the disk based on the clustered index. A table can only have ONE clustered index.
4. **Chỉ mục cụm**: Dữ liệu bảng thực tế được sắp xếp vật lý trên đĩa dựa trên chỉ mục cụm. Một bảng chỉ có thể có MỘT chỉ mục cụm.
5. **Non-Clustered Index**: A separate structure from the data rows. Contains a pointer/reference mapping to the actual data location. A table can have multiple non-clustered indexes.
5. **Chỉ mục không cụm**: Một cấu trúc riêng biệt với các hàng dữ liệu. Chứa một con trỏ/ánh xạ tham chiếu đến vị trí dữ liệu thực tế. Một bảng có thể có nhiều chỉ mục không cụm.

## Practical Example
## Ví dụ thực tế

```sql
-- Suppose we have millions of rows in Employees table
-- Giả sử chúng ta có hàng triệu hàng trong bảng Nhân viên
CREATE TABLE Employees (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    department VARCHAR(50),
    salary INT
);

-- Querying by department without an index performs a Full Table Scan (slow)
-- Truy vấn theo phòng ban mà không có chỉ mục sẽ thực hiện Quét toàn bộ bảng (chậm)
SELECT * FROM Employees WHERE department = 'Engineering';

-- Let's create an index on department to speed this up
-- Hãy tạo một chỉ mục trên phòng ban để tăng tốc độ này
CREATE INDEX idx_department ON Employees(department);

-- Now the same query will utilize the B-Tree index (fast)
-- Bây giờ cùng một truy vấn sẽ sử dụng chỉ mục B-Tree (nhanh)
EXPLAIN SELECT * FROM Employees WHERE department = 'Engineering';
-- (Using EXPLAIN shows query execution plan, proving index usage)
-- (Sử dụng EXPLAIN cho thấy kế hoạch thực thi truy vấn, chứng minh việc sử dụng chỉ mục)

-- Composite Index Example (Order matters!)
-- Ví dụ về chỉ mục tổng hợp (Thứ tự quan trọng!)
CREATE INDEX idx_dept_salary ON Employees(department, salary);
```

## Exercises
## Bài tập
1. What is the downside of creating an index on every single column in a table? Explain the impact on `INSERT`, `UPDATE`, and `DELETE` queries.
1. Nhược điểm của việc tạo chỉ mục trên mỗi cột trong một bảng là gì? Giải thích tác động đến các truy vấn `INSERT`, `UPDATE` và `DELETE`.
2. In a composite index created on `(department, salary)`, can the database efficiently utilize the index if you write a query: `SELECT * FROM Employees WHERE salary > 50000;`? Why or why not?
2. Trong một chỉ mục tổng hợp được tạo trên `(phòng ban, lương)`, cơ sở dữ liệu có thể sử dụng hiệu quả chỉ mục nếu bạn viết một truy vấn: `SELECT * FROM Employees WHERE salary > 50000;` không? Tại sao hoặc tại sao không?
3. What is the difference between a Hash Index and a B-Tree Index? When would you use Hash over B-Tree?
3. Sự khác biệt giữa Chỉ mục băm và Chỉ mục B-Tree là gì? Khi nào bạn sẽ sử dụng Băm thay vì B-Tree?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Understand **B-Trees**. Why are B-Trees generally preferred over Hash maps for standard indexes? (B-Trees support range queries `<`, `>`, `BETWEEN`, while Hashes only support exact matches `=`).
- Hiểu về **B-Trees**. Tại sao B-Trees thường được ưu tiên hơn các bản đồ Băm cho các chỉ mục tiêu chuẩn? (B-Trees hỗ trợ các truy vấn phạm vi `<`, `>`, `BETWEEN`, trong khi Băm chỉ hỗ trợ các kết quả khớp chính xác `=`).
- Be able to explain why an index might *not* be used by the SQL query planner (e.g., table is too small, using wildcard `LIKE '%value'`, ignoring left-side of composite index).
- Có thể giải thích tại sao một chỉ mục có thể *không* được sử dụng bởi trình lập kế hoạch truy vấn SQL (ví dụ: bảng quá nhỏ, sử dụng ký tự đại diện `LIKE '%value'`, bỏ qua phía bên trái của chỉ mục tổng hợp).
