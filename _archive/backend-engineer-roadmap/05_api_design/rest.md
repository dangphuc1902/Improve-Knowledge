# REST API (Representational State Transfer)
# API REST (Chuyển trạng thái đại diện)

## Concept Explanation
## Giải thích khái niệm
REST is an architectural style for providing standards between computer systems on the web. Systems conforming to REST constraints are called RESTful. 
REST là một kiểu kiến trúc để cung cấp các tiêu chuẩn giữa các hệ thống máy tính trên web. Các hệ thống tuân thủ các ràng buộc của REST được gọi là RESTful.

REST APIs rely on stateless, client-server, cacheable communications protocols — virtually always HTTP.
Các API REST dựa trên các giao thức truyền thông không trạng thái, máy khách-máy chủ, có thể lưu vào bộ nhớ đệm — hầu như luôn là HTTP.

### Guiding Principles of REST
### Các nguyên tắc chỉ đạo của REST
1. **Client-Server Architecture**: Separation of concerns. The client handles UI/UX; the server handles data storage and backend logic.
1. **Kiến trúc máy khách-máy chủ**: Tách biệt các mối quan tâm. Máy khách xử lý giao diện người dùng/trải nghiệm người dùng; máy chủ xử lý lưu trữ dữ liệu và logic backend.
2. **Statelessness**: Every request from client to server must contain all the information needed to understand the request. The server cannot save client state across requests.
2. **Không trạng thái**: Mọi yêu cầu từ máy khách đến máy chủ phải chứa tất cả thông tin cần thiết để hiểu yêu cầu. Máy chủ không thể lưu trạng thái máy khách qua các yêu cầu.
3. **Cacheability**: Responses must define themselves as cacheable or not to improve network efficiency.
3. **Khả năng lưu vào bộ nhớ đệm**: Các phản hồi phải tự xác định là có thể lưu vào bộ nhớ đệm hay không để cải thiện hiệu quả mạng.
4. **Uniform Interface (Resource-Based)**: URIs map to specific resources (nouns, not verbs). 
4. **Giao diện đồng nhất (Dựa trên tài nguyên)**: URI ánh xạ tới các tài nguyên cụ thể (danh từ, không phải động từ).
   - `GET /users` (Good)
   - `GET /users` (Tốt)
   - `GET /getUsers` (Bad - uses verb)
   - `GET /getUsers` (Xấu - sử dụng động từ)

### HTTP Methods Mapping to CRUD
### Các phương thức HTTP ánh xạ tới CRUD
- **Create**: `POST /users` (Creates a new user)
- **Tạo**: `POST /users` (Tạo người dùng mới)
- **Read**: `GET /users` (Gets all) or `GET /users/1` (Gets specific)
- **Đọc**: `GET /users` (Lấy tất cả) hoặc `GET /users/1` (Lấy cụ thể)
- **Update**: `PUT /users/1` (Replaces whole user) or `PATCH /users/1` (Partial patch)
- **Cập nhật**: `PUT /users/1` (Thay thế toàn bộ người dùng) hoặc `PATCH /users/1` (Bản vá một phần)
- **Delete**: `DELETE /users/1`
- **Xóa**: `DELETE /users/1`

## Practical Example: Spring Boot REST Controller
## Ví dụ thực tế: Bộ điều khiển REST Spring Boot

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    // Simulating a database
    // Mô phỏng một cơ sở dữ liệu
    private Map<Long, String> products = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<String>> getAll() {
        return ResponseEntity.ok(products.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable Long id) {
        if (!products.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(products.get(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody String product, @RequestParam Long id) {
        products.put(id, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
}
```

## Exercises
## Bài tập
1. Critique this URI design and correct it into RESTful standards: `POST /api/updateCustomerData?id=5`
1. Phê bình thiết kế URI này và sửa nó thành các tiêu chuẩn RESTful: `POST /api/updateCustomerData?id=5`
2. How would you handle Pagination and Filtering in a REST API request for a list of products? Design the URI.
2. Làm cách nào để bạn xử lý Phân trang và Lọc trong một yêu cầu API REST cho một danh sách các sản phẩm? Thiết kế URI.
3. Build a small Express.js REST API using the principles outlined above. Handle 404 and 500 error cases gracefully with JSON responses.
3. Xây dựng một API REST Express.js nhỏ bằng cách sử dụng các nguyên tắc được nêu ở trên. Xử lý các trường hợp lỗi 404 và 500 một cách khéo léo với các phản hồi JSON.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Understand the exact difference between `PUT` and `PATCH`. `PUT` should be idempotent.
- Hiểu sự khác biệt chính xác giữa `PUT` và `PATCH`. `PUT` phải là bất biến.
- Name the constraints of REST.
- Nêu tên các ràng buộc của REST.
- Explain Richardson Maturity Model (Level 0: Swamp of POX, Level 1: Resources, Level 2: HTTP Verbs, Level 3: HATEOAS).
- Giải thích Mô hình trưởng thành Richardson (Cấp 0: Đầm lầy POX, Cấp 1: Tài nguyên, Cấp 2: Động từ HTTP, Cấp 3: HATEOAS).
