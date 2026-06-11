# HTTP (Hypertext Transfer Protocol)
# HTTP (Giao thức truyền siêu văn bản)

## Concept Explanation
## Giải thích khái niệm
HTTP is the foundation of data communication for the World Wide Web. It functions as a request-response protocol in the client-server computing model.
HTTP là nền tảng của giao tiếp dữ liệu cho World Wide Web. Nó hoạt động như một giao thức yêu cầu-phản hồi trong mô hình tính toán máy khách-máy chủ.

A web browser, for example, may be the client, and an application running on a computer hosting a website may be the server.
Ví dụ, một trình duyệt web có thể là máy khách và một ứng dụng chạy trên máy tính lưu trữ một trang web có thể là máy chủ.

### Key Aspects of HTTP
### Các khía cạnh chính của HTTP
- **Stateless**: Each request is independent. The server does not keep session information in the HTTP protocol itself (we use cookies/tokens for that).
- **Không trạng thái**: Mỗi yêu cầu là độc lập. Máy chủ không giữ thông tin phiên trong chính giao thức HTTP (chúng tôi sử dụng cookie/token cho việc đó).
- **Methods (Verbs)**: Define the action to be performed (GET, POST, PUT, DELETE, PATCH).
- **Phương thức (Động từ)**: Xác định hành động sẽ được thực hiện (GET, POST, PUT, DELETE, PATCH).
- **Status Codes**: Indicate the result of the HTTP request.
- **Mã trạng thái**: Cho biết kết quả của yêu cầu HTTP.
  - 1xx: Informational
  - 1xx: Thông tin
  - 2xx: Success (e.g., 200 OK, 201 Created)
  - 2xx: Thành công (ví dụ: 200 OK, 201 Created)
  - 3xx: Redirection (e.g., 301 Moved Permanently)
  - 3xx: Chuyển hướng (ví dụ: 301 Moved Permanently)
  - 4xx: Client Error (e.g., 400 Bad Request, 404 Not Found)
  - 4xx: Lỗi máy khách (ví dụ: 400 Bad Request, 404 Not Found)
  - 5xx: Server Error (e.g., 500 Internal Server Error)
  - 5xx: Lỗi máy chủ (ví dụ: 500 Internal Server Error)
- **Headers**: Key-value pairs sent with requests/responses providing meta-information (e.g., `Content-Type: application/json`).
- **Tiêu đề**: Các cặp khóa-giá trị được gửi cùng với các yêu cầu/phản hồi cung cấp thông tin meta (ví dụ: `Content-Type: application/json`).

### HTTP vs HTTPS
### HTTP và HTTPS
HTTPS is HTTP with encryption. The only difference is that HTTPS uses TLS (SSL) to encrypt normal HTTP requests and responses.
HTTPS là HTTP có mã hóa. Sự khác biệt duy nhất là HTTPS sử dụng TLS (SSL) để mã hóa các yêu cầu và phản hồi HTTP thông thường.

## Practical Example
## Ví dụ thực tế
Here is a raw HTTP request and response flow:
Đây là một luồng yêu cầu và phản hồi HTTP thô:

**Request:**
**Yêu cầu:**
```http
GET /api/users/1 HTTP/1.1
Host: api.example.com
Accept: application/json
```

**Response:**
**Phản hồi:**
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 42

{
  "id": 1,
  "name": "Backend Developer"
}
```

**Node.js Express Example:**
**Ví dụ về Node.js Express:**
```javascript
const express = require('express');
const app = express();

app.get('/api/greeting', (req, res) => {
    // Setting a custom header and returning a 200 JSON response
    res.set('X-Custom-Header', 'Hello World');
    res.status(200).json({ message: 'Welcome to the Backend Roadmap!' });
});

app.listen(3000, () => console.log('Server running on port 3000'));
```

## Exercises
## Bài tập
1. Using `curl` or Postman, make a GET request to `https://jsonplaceholder.typicode.com/posts/1` and inspect the headers.
1. Sử dụng `curl` hoặc Postman, thực hiện yêu cầu GET tới `https://jsonplaceholder.typicode.com/posts/1` và kiểm tra các tiêu đề.
2. What are the semantic differences between PUT and PATCH? Create a small markdown document explaining it.
2. Sự khác biệt về ngữ nghĩa giữa PUT và PATCH là gì? Tạo một tài liệu markdown nhỏ giải thích nó.
3. Write a Spring Boot endpoint that returns a `404 Not Found` with a custom error message if a requested ID is less than 0.
3. Viết một điểm cuối Spring Boot trả về `404 Not Found` với một thông báo lỗi tùy chỉnh nếu ID được yêu cầu nhỏ hơn 0.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Memorize common status codes (200, 201, 204, 400, 401, 403, 404, 500, 502, 503).
- Ghi nhớ các mã trạng thái phổ biến (200, 201, 204, 400, 401, 403, 404, 500, 502, 503).
- Explain idempotency. Which HTTP methods are idempotent? (GET, PUT, DELETE).
- Giải thích tính bất biến. Những phương thức HTTP nào là bất biến? (GET, PUT, DELETE).
- Understand the structure of an HTTP message.
- Hiểu cấu trúc của một thông báo HTTP.
