# GraphQL

## Concept Explanation
## Giải thích khái niệm
GraphQL is a query language for APIs and a runtime for fulfilling those queries with your existing data.
GraphQL là một ngôn ngữ truy vấn cho các API và một thời gian chạy để thực hiện các truy vấn đó với dữ liệu hiện có của bạn.
Developed by Facebook, it allows clients to request exactly the data they need, nothing more and nothing less.
Được phát triển bởi Facebook, nó cho phép khách hàng yêu cầu chính xác dữ liệu họ cần, không hơn không kém.

### Problems GraphQL Solves
### Các vấn đề mà GraphQL giải quyết
1. **Over-fetching**: Getting more data than you need. In REST, `GET /users/1` might return 20 fields even if you only need the `name`.
1. **Tìm nạp quá mức**: Nhận được nhiều dữ liệu hơn bạn cần. Trong REST, `GET /users/1` có thể trả về 20 trường ngay cả khi bạn chỉ cần `tên`.
2. **Under-fetching (N+1 Problem)**: Not getting enough data in a single request. In REST, you might fetch a user `GET /users/1`, then have to make 5 more API calls to get their posts `GET /users/1/posts`.
2. **Tìm nạp dưới mức (Vấn đề N+1)**: Không nhận đủ dữ liệu trong một yêu cầu duy nhất. Trong REST, bạn có thể tìm nạp một người dùng `GET /users/1`, sau đó phải thực hiện thêm 5 lệnh gọi API để lấy các bài đăng của họ `GET /users/1/posts`.

### Core Concepts
### Các khái niệm cốt lõi
- **Schema**: Strongly typed definition of your API's capabilities.
- **Lược đồ**: Định nghĩa được nhập mạnh về các khả năng của API của bạn.
- **Query**: The way clients request data (equivalent to REST `GET`).
- **Truy vấn**: Cách khách hàng yêu cầu dữ liệu (tương đương với `GET` của REST).
- **Mutation**: The way clients modify data (equivalent to REST `POST/PUT/DELETE`).
- **Đột biến**: Cách khách hàng sửa đổi dữ liệu (tương đương với `POST/PUT/DELETE` của REST).
- **Resolver**: The backend function that actually fetches the data for a specific field in the schema.
- **Trình phân giải**: Hàm backend thực sự tìm nạp dữ liệu cho một trường cụ thể trong lược đồ.

## Practical Example
## Ví dụ thực tế
**GraphQL Query example (Client side)**
**Ví dụ truy vấn GraphQL (phía máy khách)**
```graphql
query {
  user(id: "1") {
    name
    email
    posts {
      title
    }
  }
}
```

**JSON Response (Exact shape as query)**
**Phản hồi JSON (Hình dạng chính xác như truy vấn)**
```json
{
  "data": {
    "user": {
      "name": "Alice",
      "email": "alice@example.com",
      "posts": [
        { "title": "My first GraphQL tutorial" },
        { "title": "Understanding Resolvers" }
      ]
    }
  }
}
```

**Spring Boot GraphQL Server Setup (Conceptual)**
**Thiết lập Máy chủ Spring Boot GraphQL (Khái niệm)**

**1. Schema Definition (`src/main/resources/graphql/schema.graphqls`)**
**1. Định nghĩa lược đồ (`src/main/resources/graphql/schema.graphqls`)**
```graphql
type User {
  id: ID!
  name: String!
  email: String!
}

type Query {
  user(id: ID!): User
}
```

**2. Java Implementation & Controllers**
**2. Triển khai Java & Bộ điều khiển (Controller)**
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@SpringBootApplication
public class GraphqlApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }
}

class User {
    private String id;
    private String name;
    private String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

@Controller
public class UserGraphqlController {

    // @QueryMapping ánh xạ trực tiếp phương thức này với type Query 'user(id: ID!)' trong Schema
    @QueryMapping
    public User user(@Argument String id) {
        // Mô phỏng lấy dữ liệu từ Database
        return new User(id, "Alice", "alice@example.com");
    }
}
```

## Exercises
## Bài tập
1. Define a GraphQL Schema that includes a Mutation to `createPost(title: String!, content: String!)`.
1. Xác định một Lược đồ GraphQL bao gồm một Đột biến thành `createPost(title: String!, content: String!)`.
2. Look up the "Dataloader" pattern in GraphQL. How does it solve the N+1 problem efficiently?
2. Tra cứu mẫu "Dataloader" trong GraphQL. Nó giải quyết vấn đề N+1 một cách hiệu quả như thế nào?
3. Contrast versioning in REST API (e.g., `v1/`, `v2/`) vs versioning in a GraphQL API.
3. So sánh việc tạo phiên bản trong API REST (ví dụ: `v1/`, `v2/`) với việc tạo phiên bản trong API GraphQL.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Pros of GraphQL: Phenomenal developer experience for frontend, strongly typed, efficient network usage.
- Ưu điểm của GraphQL: Trải nghiệm tuyệt vời cho nhà phát triển frontend, được nhập mạnh, sử dụng mạng hiệu quả.
- Cons of GraphQL: Caching at the network level is hard (since everything is a `POST` to `/graphql`), complex backend query optimization, potential for malicious deep queries (requires query depth limiting).
- Nhược điểm của GraphQL: Việc lưu trữ bộ đệm ở cấp độ mạng rất khó (vì mọi thứ đều là `POST` tới `/graphql`), tối ưu hóa truy vấn backend phức tạp, tiềm ẩn các truy vấn sâu độc hại (yêu cầu giới hạn độ sâu truy vấn).
