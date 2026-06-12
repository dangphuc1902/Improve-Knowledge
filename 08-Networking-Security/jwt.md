# JWT (JSON Web Tokens)
# JWT (Mã thông báo web JSON)

## Concept Explanation
## Giải thích khái niệm
JSON Web Tokens (JWT) provide a stateless method of authentication. The server does not keep a record of the session. Instead, all necessary information is encoded directly into a signed token.
Mã thông báo web JSON (JWT) cung cấp một phương thức xác thực không trạng thái. Máy chủ không lưu giữ hồ sơ của phiên. Thay vào đó, tất cả thông tin cần thiết được mã hóa trực tiếp vào một mã thông báo đã ký.

### JWT Structure
### Cấu trúc JWT
A JWT consists of three parts separated by dots (`.`):
Một JWT bao gồm ba phần được phân tách bằng dấu chấm (`.`):
1. **Header**: Describes the token type and signing algorithm (e.g., HMAC SHA256).
1. **Tiêu đề**: Mô tả loại mã thông báo và thuật toán ký (ví dụ: HMAC SHA256).
2. **Payload**: Contains the claims (e.g., `userId`, `role`, `expiration date`).
2. **Tải trọng**: Chứa các xác nhận quyền sở hữu (ví dụ: `userId`, `role`, `ngày hết hạn`).
3. **Signature**: Created by signing the encoded Header + Payload using a Secret Key known only to the server.
3. **Chữ ký**: Được tạo bằng cách ký Tiêu đề + Tải trọng được mã hóa bằng Khóa bí mật chỉ máy chủ mới biết.

Format: `xxxxxxx.yyyyyyy.zzzzzzz`
Định dạng: `xxxxxxx.yyyyyyy.zzzzzzz`

### How it Works
### Cách hoạt động
1. **Login**: Client sends username and password.
1. **Đăng nhập**: Máy khách gửi tên người dùng và mật khẩu.
2. **Token Generation**: Server validates credentials and generates a JWT, signing it with a Secret Key. No database record of the session is created.
2. **Tạo mã thông báo**: Máy chủ xác thực thông tin đăng nhập và tạo JWT, ký bằng Khóa bí mật. Không có bản ghi cơ sở dữ liệu nào của phiên được tạo.
3. **Token Transmission**: Server sends the JWT to the client.
3. **Truyền mã thông báo**: Máy chủ gửi JWT cho máy khách.
4. **Subsequent Requests**: Client stores the JWT (e.g., in LocalStorage or an HttpOnly cookie) and sends it in the `Authorization: Bearer <token>` header on subsequent requests.
4. **Các yêu cầu tiếp theo**: Máy khách lưu trữ JWT (ví dụ: trong LocalStorage hoặc cookie chỉ có HTTP) và gửi nó trong tiêu đề `Authorization: Bearer <token>` trong các yêu cầu tiếp theo.
5. **Validation**: The server recalculates the signature using its Secret Key. If it matches, the token is valid, and the server trusts the claims inside it.
5. **Xác thực**: Máy chủ tính toán lại chữ ký bằng Khóa bí mật của nó. Nếu nó khớp, mã thông báo là hợp lệ và máy chủ tin tưởng vào các xác nhận quyền sở hữu bên trong nó.

## Practical Example (Spring Boot & io.jsonwebtoken JJWT)
## Ví dụ thực tế (Spring Boot & io.jsonwebtoken JJWT)

```java
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@SpringBootApplication
public class JwtAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtAuthApplication.class, args);
    }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

class JwtResponse {
    private String token;
    public JwtResponse(String token) { this.token = token; }
    public String getToken() { return token; }
}

@RestController
@RequestMapping("/api")
public class JwtAuthController {

    // Khóa ký JWT bí mật (Độ dài tối thiểu 256 bits cho HS256)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 3600000; // 1 giờ = 3,600,000 ms

    // Login endpoint generating JWT
    // Điểm cuối đăng nhập tạo JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if ("admin".equals(request.getUsername()) && "password123".equals(request.getPassword())) {
            // Tạo mã thông báo JWT có thời hạn 1 giờ
            String token = Jwts.builder()
                    .setSubject("admin")
                    .claim("userId", 1L)
                    .claim("role", "admin")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SECRET_KEY)
                    .compact();
            return ResponseEntity.ok(new JwtResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    // Protected Route
    // Tuyến đường được bảo vệ
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or Invalid Token");
        }

        String token = authHeader.substring(7); // Bỏ phần "Bearer "
        try {
            // Xác thực và giải mã JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = claims.get("userId", Long.class);
            return ResponseEntity.ok("Welcome Admin ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token altered or expired");
        }
    }
}
```

## Exercises
## Bài tập
1. Go to [jwt.io](https://jwt.io/) and paste any JWT to decode its payload. Notice that the payload is just Base64 encoded, **not encrypted**.
1. Truy cập [jwt.io](https://jwt.io/) và dán bất kỳ JWT nào để giải mã tải trọng của nó. Lưu ý rằng tải trọng chỉ được mã hóa Base64, **không được mã hóa**.
2. Why should you NEVER put sensitive information (like passwords or full credit card numbers) inside a JWT payload?
2. Tại sao bạn KHÔNG BAO GIỜ nên đặt thông tin nhạy cảm (như mật khẩu hoặc số thẻ tín dụng đầy đủ) vào bên trong tải trọng JWT?
3. How do you implement "Logout" in a stateless JWT architecture, since the server doesn't have a session ID to delete? (Hint: token blocklists or short expirations + refresh tokens).
3. Làm cách nào để bạn triển khai "Đăng xuất" trong kiến trúc JWT không trạng thái, vì máy chủ không có ID phiên để xóa? (Gợi ý: danh sách chặn mã thông báo hoặc hết hạn ngắn + mã thông báo làm mới).

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Contrast JWT vs Session Auth regarding server memory and scalability. JWTs scale easily across microservices because any service with the secret key can validate the token independently.
- So sánh xác thực JWT và xác thực phiên về bộ nhớ máy chủ và khả năng mở rộng. JWT dễ dàng mở rộng quy mô trên các vi dịch vụ vì bất kỳ dịch vụ nào có khóa bí mật đều có thể xác thực mã thông báo một cách độc lập.
- Understand the challenge of invalidating a JWT before it expires.
- Hiểu thách thức của việc vô hiệu hóa JWT trước khi nó hết hạn.
- Know the difference between an Access Token and a Refresh Token.
- Biết sự khác biệt giữa Mã thông báo truy cập và Mã thông báo làm mới.
