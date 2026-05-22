# 📖 JDBC & Cơ Sở Dữ Liệu - Lý Thuyết, Interview & Bài Tập

> **Tuần 3 | Java Database Connectivity**

---

## LÝ THUYẾT

### 1. JDBC Architecture

```
Java Application
      ↓
JDBC API (java.sql.*)
      ↓
JDBC Driver Manager
      ↓
JDBC Driver (MySQL Connector, Oracle Driver...)
      ↓
Database (MySQL, Oracle, PostgreSQL...)
```

### 2. Các Bước Kết Nối JDBC

```java
import java.sql.*;

public class JdbcExample {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "secret";

    public static void main(String[] args) {
        // Bước 1: Load Driver (không cần với JDBC 4.0+ / Class.forName auto-load)
        // Class.forName("com.mysql.cj.jdbc.Driver");

        // Bước 2: Tạo Connection
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Bước 3: Tạo Statement
            // Bước 4: Execute Query
            // Bước 5: Process ResultSet
            // Bước 6: Close (handled by try-with-resources)

            System.out.println("Connected: " + conn.getMetaData().getDatabaseProductName());
            performCrud(conn);

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### 3. Statement vs PreparedStatement vs CallableStatement

```java
// Statement - dùng cho dynamic SQL không có parameters
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + userId);
// ⚠️ DỄ BỊ SQL INJECTION!

// PreparedStatement - parameterized queries (PREFERRED)
String sql = "SELECT * FROM users WHERE email = ? AND status = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, email);      // parameter 1 (1-indexed!)
pstmt.setString(2, "ACTIVE");   // parameter 2
ResultSet rs = pstmt.executeQuery();

// Update/Insert/Delete
String insertSql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
PreparedStatement insertStmt = conn.prepareStatement(insertSql,
        Statement.RETURN_GENERATED_KEYS);  // Lấy auto-generated ID
insertStmt.setString(1, "Alice");
insertStmt.setString(2, "alice@example.com");
insertStmt.setInt(3, 25);
int rowsAffected = insertStmt.executeUpdate();

// Lấy generated key
ResultSet keys = insertStmt.getGeneratedKeys();
if (keys.next()) {
    long newId = keys.getLong(1);
}

// CallableStatement - stored procedures
CallableStatement cstmt = conn.prepareCall("{call get_user_by_id(?, ?)}");
cstmt.setLong(1, userId);
cstmt.registerOutParameter(2, Types.VARCHAR);  // OUT parameter
cstmt.execute();
String result = cstmt.getString(2);
```

### 4. ResultSet & Data Retrieval

```java
String sql = "SELECT id, name, email, created_at FROM users ORDER BY name";
PreparedStatement pstmt = conn.prepareStatement(sql);
ResultSet rs = pstmt.executeQuery();

List<User> users = new ArrayList<>();
while (rs.next()) {
    User user = new User();
    user.setId(rs.getLong("id"));          // Dùng column name (safer)
    user.setName(rs.getString("name"));
    user.setEmail(rs.getString("email"));
    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    users.add(user);
}
```

### 5. Transaction Management

```java
Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
conn.setAutoCommit(false);  // Bắt đầu transaction

try {
    // Thao tác 1: Rút tiền tài khoản A
    PreparedStatement debit = conn.prepareStatement(
        "UPDATE accounts SET balance = balance - ? WHERE id = ?");
    debit.setDouble(1, 500.0);
    debit.setLong(2, fromAccountId);
    debit.executeUpdate();

    // Thao tác 2: Nạp tiền tài khoản B
    PreparedStatement credit = conn.prepareStatement(
        "UPDATE accounts SET balance = balance + ? WHERE id = ?");
    credit.setDouble(1, 500.0);
    credit.setLong(2, toAccountId);
    credit.executeUpdate();

    conn.commit();  // Thành công - lưu tất cả thay đổi
    System.out.println("Transfer successful!");

} catch (SQLException e) {
    conn.rollback();  // Lỗi - hoàn tác tất cả
    System.err.println("Transfer failed, rolled back: " + e.getMessage());
} finally {
    conn.setAutoCommit(true);
    conn.close();
}
```

### 6. DAO Pattern với JDBC

```java
public class UserDao {
    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + id, e);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY name";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
    }

    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getAge());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getLong(1));
            }
            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setAge(rs.getInt("age"));
        return user;
    }
}
```

### 7. Connection Pooling (HikariCP)

```java
// application.properties với Spring Boot
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000

// Manual setup
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
config.setUsername("root");
config.setPassword("secret");
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);

DataSource dataSource = new HikariDataSource(config);
```

---

## INTERVIEW Q&A

### Q1: Các bước kết nối JDBC?

1. Load Driver (auto trong JDBC 4.0+)
2. `DriverManager.getConnection(url, user, password)`
3. `conn.prepareStatement(sql)`
4. Set parameters
5. `executeQuery()` hoặc `executeUpdate()`
6. Process `ResultSet`
7. Close resources (dùng try-with-resources)

---

### Q2: PreparedStatement khác Statement như thế nào?

| Tiêu chí | Statement | PreparedStatement |
|---------|----------|-------------------|
| SQL Injection | Dễ bị | Ngăn chặn |
| Performance | Compile mỗi lần | Pre-compiled, cache |
| Parameters | String concat | Typed setters |
| Readability | Khó đọc | Dễ đọc |

**PreparedStatement ngăn SQL Injection** vì parameters được escape tự động:
```java
// SAFE
pstmt.setString(1, "' OR '1'='1");  // Được treat là literal string
// UNSAFE
stmt.execute("SELECT * FROM users WHERE name = '" + userInput + "'");
```

---

### Q3: executeQuery() vs executeUpdate() vs execute()?

- **executeQuery()**: SELECT → trả ResultSet
- **executeUpdate()**: INSERT/UPDATE/DELETE → trả int (rows affected)
- **execute()**: Bất kỳ SQL → trả boolean (true nếu có ResultSet)

---

### Q4: Transaction ACID là gì?

- **A**tomicity: Toàn bộ transaction thành công hoặc toàn bộ rollback
- **C**onsistency: DB chuyển từ trạng thái hợp lệ này sang trạng thái hợp lệ khác
- **I**solation: Transactions song song không ảnh hưởng nhau
- **D**urability: Dữ liệu được committed thì tồn tại mãi (dù crash)

---

### Q5: Connection Pool là gì? Tại sao cần?

Tạo Connection mới rất tốn kém (network, auth, resources). **Connection Pool** duy trì sẵn pool connections, tái sử dụng → giảm latency, tăng throughput.

**HikariCP** là pool phổ biến nhất trong Java (default trong Spring Boot).

---

## BÀI TẬP

### Bài 1: CRUD hoàn chỉnh với JDBC

Tạo `StudentDao` với đầy đủ CRUD:
```java
public interface StudentDao {
    Student save(Student student);  // insert hoặc update
    Optional<Student> findById(Long id);
    List<Student> findAll();
    List<Student> findByGradeAbove(double minGrade);
    boolean delete(Long id);
    int countByClass(String className);
}
```

Schema:
```sql
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    class_name VARCHAR(50),
    grade DOUBLE,
    enrolled_date DATE
);
```

### Bài 2: Transaction - Bank Transfer

Implement hàm `transfer(fromId, toId, amount)` với transaction xử lý đúng:
- Validate balance đủ
- Debit source, credit destination trong cùng transaction
- Rollback nếu bất kỳ bước nào fail
- Log transaction history vào bảng `transactions`

---

*📌 Tiếp theo: [05-Spring-Framework](../05-Spring-Framework/Theory.md)*
