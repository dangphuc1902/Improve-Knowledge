Trong phỏng vấn Java, khi nói về transaction (giao dịch), nhà tuyển dụng thường muốn kiểm tra kiến thức của bạn về quản lý tính toàn vẹn dữ liệu trong các ứng dụng Java, đặc biệt khi làm việc với CSDL hoặc hệ thống phân tán.
Dưới đây là những điểm quan trọng bạn nên nắm để trả lời tốt:

1. Khái niệm Transaction

Transaction là một đơn vị công việc logic gồm nhiều thao tác (SQL hoặc xử lý dữ liệu) được thực hiện như một khối duy nhất.
Đặc điểm ACID:

Atomicity – Tất cả hoặc không có gì được thực hiện.
Consistency – Dữ liệu luôn ở trạng thái hợp lệ trước và sau giao dịch.
Isolation – Các giao dịch không ảnh hưởng lẫn nhau.
Durability – Kết quả giao dịch được lưu vĩnh viễn sau khi commit.




2. Transaction trong Java
Có 2 cách phổ biến:


JDBC Transaction – Quản lý thủ công qua Connection:
Javaimport java.sql.*;

public class JdbcTransactionExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "root";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO accounts(id, balance) VALUES (?, ?)");
                 PreparedStatement stmt2 = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?")) {

                // Thao tác 1
                stmt1.setInt(1, 3);
                stmt1.setDouble(2, 500);
                stmt1.executeUpdate();

                // Thao tác 2
                stmt2.setDouble(1, 100);
                stmt2.setInt(2, 1);
                stmt2.executeUpdate();

                conn.commit(); // Xác nhận transaction
                System.out.println("Transaction thành công!");
            } catch (SQLException e) {
                conn.rollback(); // Hoàn tác nếu lỗi
                System.err.println("Transaction thất bại, rollback!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



Spring Transaction Management – Dùng annotation:
Javaimport org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    @Transactional
    public void transferMoney(Long fromId, Long toId, double amount) {
        // Trừ tiền tài khoản nguồn
        accountRepository.decreaseBalance(fromId, amount);
        // Cộng tiền tài khoản đích
        accountRepository.increaseBalance(toId, amount);
        // Nếu có exception, Spring sẽ rollback tự động
    }
}




3. Isolation Levels (Mức cô lập)

READ_UNCOMMITTED – Có thể đọc dữ liệu chưa commit (dirty read).
READ_COMMITTED – Chỉ đọc dữ liệu đã commit.
REPEATABLE_READ – Đảm bảo dữ liệu đọc không thay đổi trong cùng transaction.
SERIALIZABLE – Cô lập cao nhất, tránh mọi xung đột nhưng hiệu năng thấp.


4. Câu hỏi phỏng vấn thường gặp

Transaction là gì? Giải thích ACID.
Sự khác nhau giữa commit và rollback.
Các mức isolation và vấn đề tương ứng (dirty read, non-repeatable read, phantom read).
Cách Spring quản lý transaction.
Khi nào nên dùng transaction phân tán (distributed transaction, XA).


Nếu bạn muốn, mình có thể tổng hợp bộ câu hỏi phỏng vấn Java về transaction kèm đáp án mẫu để bạn luyện tập.
Bạn có muốn mình làm luôn không?
