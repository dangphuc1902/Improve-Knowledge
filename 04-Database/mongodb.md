# MongoDB

## Concept Explanation
## Giải thích khái niệm
MongoDB is a source-available cross-platform document-oriented database program. It is classified as a NoSQL database program; it uses JSON-like documents with optional schemas.
MongoDB là một chương trình cơ sở dữ liệu hướng tài liệu đa nền tảng có nguồn mở. Nó được phân loại là một chương trình cơ sở dữ liệu NoSQL; nó sử dụng các tài liệu giống JSON với các lược đồ tùy chọn.

### Key Characteristics
### Các đặc điểm chính
- **Document Store**: Rather than rows and columns, data is stored as BSON (Binary JSON) documents.
- **Kho tài liệu**: Thay vì các hàng và cột, dữ liệu được lưu trữ dưới dạng tài liệu BSON (JSON nhị phân).
- **Flexible Schema**: Documents in the same collection do not need to have the exact same fields or structure. This allows rapid iteration without complex migration scripts.
- **Lược đồ linh hoạt**: Các tài liệu trong cùng một bộ sưu tập không cần phải có các trường hoặc cấu trúc giống hệt nhau. Điều này cho phép lặp lại nhanh chóng mà không cần các tập lệnh di chuyển phức tạp.
- **Scalability**: Designed out-of-the-box for horizontal scaling via Sharding.
- **Khả năng mở rộng**: Được thiết kế sẵn cho khả năng mở rộng theo chiều ngang thông qua Sharding.
- **Replication**: High availability via Replica Sets (Primary-Secondary model).
- **Sao chép**: Tính sẵn sàng cao thông qua các Bộ bản sao (mô hình Chính-Phụ).

### SQL to MongoDB Mapping
### Ánh xạ từ SQL sang MongoDB
- Database -> Database
- Cơ sở dữ liệu -> Cơ sở dữ liệu
- Table -> Collection
- Bảng -> Bộ sưu tập
- Row -> Document
- Hàng -> Tài liệu
- Column -> Field
- Cột -> Trường
- JOIN -> `$lookup` (though de-normalization/embedding is preferred in Mongo)
- JOIN -> `$lookup` (mặc dù việc khử chuẩn hóa/nhúng được ưu tiên trong Mongo)

## System Design Diagram: Denormalization
## Sơ đồ thiết kế hệ thống: Khử chuẩn hóa
In SQL, you normalize data. In MongoDB, you often "Embed" related data to optimize for fast reads.
Trong SQL, bạn chuẩn hóa dữ liệu. Trong MongoDB, bạn thường "Nhúng" dữ liệu liên quan để tối ưu hóa cho các lần đọc nhanh.

```mermaid
graph LR
    subgraph SQL Approach (Normalized)
        User[User Table] -->|Foreign Key| Address[Address Table]
    end

    subgraph MongoDB Approach (Embedded)
        Doc["{ <br/> name: 'Alice', <br/> age: 30, <br/> addresses: [ { city: 'NY', zip: '10001' } ] <br/> }"]
    end
```

## Practical Example: Spring Boot Data MongoDB
## Ví dụ thực tế: Spring Boot Data MongoDB

**Pom Dependency:**
**Phụ thuộc Pom:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

**Java Entity & Repository:**
**Thực thể và kho lưu trữ Java:**
```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

@Document(collection = "users")
public class User {
    @Id
    private String id; // Mongo auto-generates ObjectIds
    private String name;
    private int age;
    
    // Getters and Setters omitted for brevity
    // Getters và Setters được bỏ qua cho ngắn gọn
}

// Spring automatically generates the implementation!
// Spring tự động tạo triển khai!
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByNameAndAgeGreaterThan(String name, int age);
}
```

## Exercises
## Bài tập
1. Setup MongoDB locally via Docker: `docker run -d -p 27017:27017 mongo`.
1. Cài đặt MongoDB cục bộ qua Docker: `docker run -d -p 27017:27017 mongo`.
2. Connect to the MongoDB instance using a GUI client like MongoDB Compass, create a database, and insert 5 documents manually.
2. Kết nối với phiên bản MongoDB bằng máy khách GUI như MongoDB Compass, tạo cơ sở dữ liệu và chèn 5 tài liệu theo cách thủ công.
3. Read about the MongoDB Aggregation Pipeline. How would you write an aggregate query to find the average `age` of users grouped by `city`?
3. Đọc về Đường ống tổng hợp MongoDB. Làm cách nào để bạn viết một truy vấn tổng hợp để tìm `tuổi` trung bình của người dùng được nhóm theo `thành phố`?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- When should you choose a NoSQL Document store over an RDBMS SQL store? (Flexible schemas, rapid prototyping, horizontal scaling, read-heavy workloads).
- Khi nào bạn nên chọn kho tài liệu NoSQL thay vì kho RDBMS SQL? (Lược đồ linh hoạt, tạo mẫu nhanh, mở rộng theo chiều ngang, khối lượng công việc đọc nhiều).
- Why might MongoDB *not* be the best choice? (Complex multi-document ACID transactions, heavily relational data requiring deep joins).
- Tại sao MongoDB *không* phải là lựa chọn tốt nhất? (Các giao dịch ACID đa tài liệu phức tạp, dữ liệu quan hệ nhiều đòi hỏi các phép nối sâu).
- Understand how MongoDB handles consistency in a replica set (eventual consistency vs read preferences).
- Hiểu cách MongoDB xử lý tính nhất quán trong một bộ bản sao (tính nhất quán cuối cùng và tùy chọn đọc).
