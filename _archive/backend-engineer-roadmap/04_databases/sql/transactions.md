# Database Transactions and ACID
# Giao dịch cơ sở dữ liệu và ACID

## Concept Explanation
## Giải thích khái niệm
A transaction is a single unit of logic or work containing a sequence of operations. If it succeeds, everything is committed. If any part fails, the entire transaction is rolled back, leaving the database unchanged.
Một giao dịch là một đơn vị logic hoặc công việc duy nhất chứa một chuỗi các hoạt động. Nếu nó thành công, mọi thứ sẽ được cam kết. Nếu bất kỳ phần nào không thành công, toàn bộ giao dịch sẽ được khôi phục, để lại cơ sở dữ liệu không thay đổi.

### ACID Properties
### Thuộc tính ACID
To ensure data integrity, relational database transactions must adhere to ACID properties:
Để đảm bảo tính toàn vẹn của dữ liệu, các giao dịch cơ sở dữ liệu quan hệ phải tuân thủ các thuộc tính ACID:
1. **Atomicity**: "All or nothing." If a transaction has 5 statements, either all 5 succeed, or none do.
1. **Tính nguyên tử**: "Tất cả hoặc không có gì." Nếu một giao dịch có 5 câu lệnh, thì cả 5 đều thành công, hoặc không có câu lệnh nào thành công.
2. **Consistency**: A transaction must transform the database from one valid state to another valid state, upholding constraints and foreign keys.
2. **Tính nhất quán**: Một giao dịch phải chuyển đổi cơ sở dữ liệu từ một trạng thái hợp lệ này sang một trạng thái hợp lệ khác, duy trì các ràng buộc và khóa ngoại.
3. **Isolation**: Concurrent transactions must not interfere with each other. (See Isolation Levels).
3. **Tính cô lập**: Các giao dịch đồng thời không được can thiệp lẫn nhau. (Xem Mức độ cô lập).
4. **Durability**: Once a transaction is committed, it remains committed even in the event of a system crash or power failure (stored defensively to disk).
4. **Tính bền vững**: Một khi một giao dịch đã được cam kết, nó vẫn được cam kết ngay cả trong trường hợp hệ thống bị sập hoặc mất điện (được lưu trữ dự phòng vào đĩa).

### Isolation Levels (From weakest-fastest to strongest-slowest)
### Mức độ cô lập (Từ yếu nhất-nhanh nhất đến mạnh nhất-chậm nhất)
1. **Read Uncommitted**: Can read uncommitted data of other transactions (Dirty Reads).
1. **Đọc chưa cam kết**: Có thể đọc dữ liệu chưa được cam kết của các giao dịch khác (Đọc bẩn).
2. **Read Committed**: Can only read committed data. (Solves Dirty Reads. Default in Postgres).
2. **Đọc đã cam kết**: Chỉ có thể đọc dữ liệu đã được cam kết. (Giải quyết Đọc bẩn. Mặc định trong Postgres).
3. **Repeatable Read**: If you read a row twice in one transaction, it guarantees the data won't change between reads. (Solves Non-repeatable reads).
3. **Đọc có thể lặp lại**: Nếu bạn đọc một hàng hai lần trong một giao dịch, nó đảm bảo dữ liệu sẽ không thay đổi giữa các lần đọc. (Giải quyết các lần đọc không thể lặp lại).
4. **Serializable**: Transactions behave as if executed sequentially, one after another. Prevents all concurrency phenomena (Solves Phantom reads).
4. **Có thể tuần tự hóa**: Các giao dịch hoạt động như thể được thực hiện tuần tự, cái này sau cái khác. Ngăn chặn tất cả các hiện tượng đồng thời (Giải quyết các lần đọc bóng ma).

## Practical Example
## Ví dụ thực tế
Classic bank transfer scenario: Transfer $100 from Account A to Account B.
Kịch bản chuyển khoản ngân hàng cổ điển: Chuyển 100 đô la từ Tài khoản A sang Tài khoản B.

```sql
BEGIN; -- Start transaction

-- Step 1: Deduct 100 from Alice
UPDATE accounts SET balance = balance - 100 WHERE id = 1;

-- Step 2: Add 100 to Bob
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

-- If both succeed:
COMMIT;

-- If power fails, or an error occurs in step 2:
-- ROLLBACK;
```

**Spring Boot Transaction Example:**
**Ví dụ về giao dịch Spring Boot:**
```java
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BankService {
    
    // Spring automatically begins, commits, or rolls back on Exception
    // Spring tự động bắt đầu, cam kết hoặc khôi phục khi có ngoại lệ
    @Transactional
    public void transferMoney(Long fromId, Long toId, Double amount) {
        accountRepository.deduct(fromId, amount);
        
        // If an exception occurs here (e.g. division by zero, network failure)
        // Spring will Catch it and execute a SQL ROLLBACK. 
        // Money won't be lost.
        // Nếu một ngoại lệ xảy ra ở đây (ví dụ: chia cho không, lỗi mạng)
        // Spring sẽ bắt nó và thực thi một SQL ROLLBACK.
        // Tiền sẽ không bị mất.
        accountRepository.credit(toId, amount);
    }
}
```

## Exercises
## Bài tập
1. What is a "Dirty Read"? Provide a concrete example involving an e-commerce checkout.
1. "Đọc bẩn" là gì? Cung cấp một ví dụ cụ thể liên quan đến thanh toán thương mại điện tử.
2. Research the `isolation` parameter of the `@Transactional` annotation in Spring Boot. How do you set it to `SERIALIZABLE`?
2. Nghiên cứu tham số `isolation` của chú thích `@Transactional` trong Spring Boot. Làm cách nào để bạn đặt nó thành `SERIALIZABLE`?
3. Using `psql` or `mysql` CLI, open two separate terminal windows. Begin a transaction in window 1, update a row but do not commit. Try to query that row in window 2. What happens? Note the default isolation level behavior.
3. Sử dụng `psql` hoặc `mysql` CLI, mở hai cửa sổ dòng lệnh riêng biệt. Bắt đầu một giao dịch trong cửa sổ 1, cập nhật một hàng nhưng không cam kết. Cố gắng truy vấn hàng đó trong cửa sổ 2. Điều gì xảy ra? Lưu ý hành vi mức độ cô lập mặc định.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Fully memorize the ACID acronym and understand what each letter specifically prevents.
- Ghi nhớ đầy đủ từ viết tắt ACID và hiểu từng chữ cái cụ thể ngăn chặn điều gì.
- Be able to list the Isolation Levels and the phenomena they prevent (Dirty read, Non-repeatable read, Phantom read).
- Có thể liệt kê các Mức độ cô lập và các hiện tượng mà chúng ngăn chặn (Đọc bẩn, Đọc không thể lặp lại, Đọc bóng ma).
- Know what a "Deadlock" is in the context of database transactions.
- Biết "Bế tắc" là gì trong bối cảnh các giao dịch cơ sở dữ liệu.
