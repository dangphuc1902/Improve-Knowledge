# 📖 RESTful Web Services & SOAP - Lý Thuyết, Interview & Bài Tập

> **Tuần 6 | Web Services**

---

## LÝ THUYẾT

### 1. REST vs SOAP So Sánh

| Tiêu chí | REST | SOAP |
|---------|------|------|
| Protocol | HTTP | HTTP, SMTP, JMS,... |
| Data Format | JSON, XML, Text | XML (chỉ XML) |
| Contract | OpenAPI/Swagger | WSDL (bắt buộc) |
| State | Stateless | Có thể stateful |
| Performance | Nhẹ, nhanh | Nặng hơn |
| Security | HTTPS, OAuth2, JWT | WS-Security |
| Khi dùng | Public APIs, mobile, web | Enterprise, legacy, banking |

---

### 2. HTTP Methods và REST CRUD

| Method | CRUD | Idempotent | Safe | Dùng cho |
|--------|------|-----------|------|---------|
| GET | Read | ✅ | ✅ | Lấy resource |
| POST | Create | ❌ | ❌ | Tạo resource mới |
| PUT | Update (full) | ✅ | ❌ | Thay thế toàn bộ resource |
| PATCH | Update (partial) | ❌ | ❌ | Cập nhật một phần |
| DELETE | Delete | ✅ | ❌ | Xóa resource |

---

### 3. HTTP Status Codes

```
2xx Success
  200 OK              - Thành công (GET, PUT, PATCH)
  201 Created         - Tạo mới thành công (POST)
  204 No Content      - Thành công, không có body (DELETE)

3xx Redirection
  301 Moved Permanently
  302 Found (temporary redirect)
  304 Not Modified    - Cache valid

4xx Client Error
  400 Bad Request     - Input không hợp lệ
  401 Unauthorized    - Cần authentication
  403 Forbidden       - Không có quyền
  404 Not Found       - Resource không tồn tại
  409 Conflict        - Conflict (duplicate key)
  422 Unprocessable Entity - Validation fail

5xx Server Error
  500 Internal Server Error - Lỗi server
  502 Bad Gateway
  503 Service Unavailable
```

---

### 4. REST API Design Best Practices

```
Resource naming (nouns, not verbs):
✅ GET    /api/v1/products
✅ GET    /api/v1/products/{id}
✅ POST   /api/v1/products
✅ PUT    /api/v1/products/{id}
✅ DELETE /api/v1/products/{id}
✅ GET    /api/v1/products/{id}/reviews
✅ POST   /api/v1/products/{id}/reviews

❌ GET /getProducts
❌ POST /createProduct
❌ DELETE /product/delete/123

Filtering, Sorting, Paging:
GET /api/v1/products?status=ACTIVE&minPrice=100&maxPrice=500
GET /api/v1/products?sort=price,asc&sort=name,desc
GET /api/v1/products?page=0&size=20

Response format:
{
  "data": [...],
  "pagination": {
    "page": 0, "size": 20, "total": 150, "totalPages": 8
  }
}

Error format:
{
  "code": "VALIDATION_ERROR",
  "message": "Invalid input",
  "errors": [
    {"field": "email", "message": "must be a valid email"},
    {"field": "age", "message": "must be >= 18"}
  ],
  "timestamp": "2025-01-01T10:00:00Z"
}
```

---

### 5. Spring Boot REST API

```java
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products with filtering and paging")
    public ResponseEntity<PageResponse<ProductDto>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Page<Product> products = productService.search(
                new ProductFilter(name, minPrice), PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(products, ProductDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(p -> ResponseEntity.ok(ProductDto.from(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ProductDto.from(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request)
                .map(p -> ResponseEntity.ok(ProductDto.from(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

---

### 6. JWT Authentication

```java
// JWT Token Structure:
// Header.Payload.Signature (Base64 encoded)
// {alg:HS256, typ:JWT}.{sub:userId, roles:[], exp:timestamp}.signature

// Spring Security + JWT flow:
// 1. POST /api/auth/login {username, password}
// 2. Server verify → return JWT
// 3. Client store JWT (localStorage hoặc httpOnly cookie)
// 4. Client gửi kèm JWT: Authorization: Bearer <token>
// 5. Server verify JWT → extract user info → proceed

@PostMapping("/api/auth/login")
public AuthResponse login(@RequestBody LoginRequest request) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    String token = jwtService.generateToken(auth);
    return new AuthResponse(token, "Bearer");
}
```

---

### 7. SOAP Cơ Bản

```xml
<!-- WSDL (Web Service Definition Language) -->
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" ...>
  <wsdl:types>
    <xs:schema>
      <xs:element name="GetUserRequest">
        <xs:complexType>
          <xs:sequence>
            <xs:element name="userId" type="xs:long"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
    </xs:schema>
  </wsdl:types>

  <wsdl:message name="GetUserRequestMessage">
    <wsdl:part name="parameters" element="tns:GetUserRequest"/>
  </wsdl:message>

  <wsdl:portType name="UserService">
    <wsdl:operation name="GetUser">
      <wsdl:input message="tns:GetUserRequestMessage"/>
      <wsdl:output message="tns:GetUserResponseMessage"/>
    </wsdl:operation>
  </wsdl:portType>
</wsdl:definitions>

<!-- SOAP Envelope -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Header>
    <Security>...</Security>
  </soap:Header>
  <soap:Body>
    <GetUserRequest>
      <userId>123</userId>
    </GetUserRequest>
  </soap:Body>
</soap:Envelope>
```

---

## INTERVIEW Q&A

### Q1: REST là gì? Các nguyên lý cơ bản?

**REST (Representational State Transfer)** là architectural style, không phải protocol.

**6 nguyên lý:**
1. **Client-Server**: Tách biệt client và server
2. **Stateless**: Mỗi request độc lập, không lưu state server
3. **Cacheable**: Response có thể cache
4. **Uniform Interface**: Resource naming nhất quán
5. **Layered System**: Client không biết đang nói chuyện với server hay proxy
6. **Code on Demand (optional)**: Server có thể gửi code về client

---

### Q2: Idempotent là gì?

Thực hiện cùng operation nhiều lần có cùng kết quả như làm 1 lần.

- **Idempotent**: GET, PUT, DELETE
- **Non-idempotent**: POST (tạo mới mỗi lần)

**Tại sao quan trọng?** Khi network lỗi, client có thể retry safely với idempotent methods.

---

### Q3: REST vs SOAP - khi nào dùng cái nào?

**REST:**
- Public APIs (mobile, web, third-party)
- Performance quan trọng (JSON nhẹ hơn XML)
- Đơn giản, ít overhead
- Modern microservices

**SOAP:**
- Enterprise systems, banking, healthcare
- Cần WS-Security, WS-ReliableMessaging
- Strict contract (WSDL)
- Stateful operations
- Legacy system integration

---

### Q4: HTTP vs HTTPS?

- **HTTP**: Plain text, không mã hóa, dễ bị intercept
- **HTTPS**: HTTP + TLS/SSL encryption, xác thực server via certificate

---

### Q5: REST API versioning strategies?

```
1. URL versioning:    /api/v1/products (phổ biến nhất)
2. Header versioning: Accept: application/vnd.api+json;version=1
3. Query param:       /api/products?version=1
```

---

## BÀI TẬP

### Bài 1: REST API hoàn chỉnh

Xây dựng REST API cho Blog system:
```
POST   /api/v1/posts          - Tạo bài viết
GET    /api/v1/posts          - Danh sách bài viết (paging, filter by tag)
GET    /api/v1/posts/{id}     - Chi tiết bài viết
PUT    /api/v1/posts/{id}     - Cập nhật bài viết
DELETE /api/v1/posts/{id}     - Xóa bài viết

POST   /api/v1/posts/{id}/comments  - Thêm comment
GET    /api/v1/posts/{id}/comments  - Danh sách comments
DELETE /api/v1/comments/{id}        - Xóa comment

POST   /api/v1/auth/register  - Đăng ký
POST   /api/v1/auth/login     - Đăng nhập → JWT
```

Yêu cầu: Spring Boot + Spring Security + JWT + Spring Data JPA + validation + error handling

### Bài 2: Postman Testing

Sau khi xây API, tạo Postman collection test:
- Happy path CRUD
- Authentication flow
- Validation errors (400)
- Not found (404)
- Unauthorized (401)

---

*📌 Tiếp theo: [08-Microservices](../08-Microservices/Theory.md)*
