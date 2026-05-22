# 📖 Spring Framework - Lý Thuyết

> **Tuần 4 | Spring Core, Spring MVC, Spring Boot**

---

## 1. Spring Framework Overview

Spring là framework Java phổ biến nhất cho enterprise applications. Cung cấp infrastructure support, giúp developer tập trung vào business logic.

```
Spring Ecosystem
├── Spring Core    - IoC Container, DI
├── Spring MVC     - Web MVC framework
├── Spring Boot    - Convention over configuration
├── Spring Data    - Data access abstraction
├── Spring Security - Authentication & Authorization  
├── Spring Cloud   - Microservices support
└── Spring Batch   - Batch processing
```

---

## 2. IoC (Inversion of Control) & DI (Dependency Injection)

### Khái Niệm
**IoC**: Thay vì class tự tạo dependencies, IoC Container tạo và inject vào.  
**DI**: Cơ chế thực hiện IoC - inject dependencies từ bên ngoài vào class.

### Trước khi dùng Spring (Tight Coupling):
```java
public class OrderService {
    // OrderService tự tạo dependency → TIGHT COUPLING
    private EmailService emailService = new EmailService();
    private PaymentService paymentService = new PaymentService();

    public void placeOrder(Order order) {
        paymentService.process(order);
        emailService.sendConfirmation(order);
    }
}
```

### Sau khi dùng Spring DI (Loose Coupling):
```java
@Service
public class OrderService {
    private final EmailService emailService;
    private final PaymentService paymentService;

    // Constructor injection (RECOMMENDED)
    @Autowired
    public OrderService(EmailService emailService, PaymentService paymentService) {
        this.emailService = emailService;
        this.paymentService = paymentService;
    }

    public void placeOrder(Order order) {
        paymentService.process(order);
        emailService.sendConfirmation(order);
    }
}
```

---

## 3. Spring Annotations Cơ Bản

### Component Annotations
```java
@Component    // Generic bean
@Service      // Business logic layer
@Repository   // Data access layer (+ exception translation)
@Controller   // Spring MVC controller
@RestController // @Controller + @ResponseBody
```

### Dependency Injection
```java
// Constructor injection (PREFERRED - immutable, testable)
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired  // Optional khi chỉ có 1 constructor (Spring 4.3+)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// Field injection (convenient but harder to test)
@Autowired
private UserRepository userRepository;

// Setter injection
@Autowired
public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

### Configuration
```java
@Configuration  // Class chứa Bean definitions
public class AppConfig {

    @Bean  // Method tạo bean được quản lý bởi Spring
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        return ds;
    }

    @Bean
    @Profile("dev")  // Chỉ active trong "dev" profile
    public DataSource h2DataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }
}
```

---

## 4. Bean Scope

| Scope | Mô tả | Khi dùng |
|-------|--------|----------|
| `singleton` | **Default** - 1 instance/container | Stateless beans (Services, Repos) |
| `prototype` | Tạo mới mỗi khi inject/request | Stateful beans |
| `request` | 1 instance/HTTP request | Web request data |
| `session` | 1 instance/HTTP session | User session data |
| `application` | 1 instance/ServletContext | Application-wide config |

```java
@Bean
@Scope("prototype")
public ShoppingCart shoppingCart() {
    return new ShoppingCart();
}
```

---

## 5. Spring MVC Architecture

```
HTTP Request
    ↓
DispatcherServlet (Front Controller)
    ↓
HandlerMapping (tìm Controller phù hợp)
    ↓
Controller (xử lý request, trả Model)
    ↓
ViewResolver (resolve view name → View)
    ↓
View (render HTML)
    ↓
HTTP Response
```

```java
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET /products
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";  // View name → WEB-INF/views/products/list.jsp
    }

    // GET /products/{id}
    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        model.addAttribute("product", product);
        return "products/detail";
    }

    // POST /products
    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute ProductForm form,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "products/form";
        }
        productService.save(form.toProduct());
        return "redirect:/products";
    }
}
```

---

## 6. Spring Boot

### Auto-Configuration
Spring Boot tự động configure dựa trên classpath và properties.
```
@EnableAutoConfiguration
    → Scans @ConditionalOnClass, @ConditionalOnProperty
    → Auto-configures DataSource, JPA, MVC, Security, etc.
```

### Starter Dependencies
```xml
<!-- spring-boot-starter-web = spring-webmvc + tomcat + jackson + ... -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### application.properties
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.com.myapp=DEBUG
logging.level.org.springframework=INFO
```

---

## 7. REST API với Spring Boot

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    // GET /api/v1/products
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(products.map(ProductDto::fromEntity).getContent());
    }

    // GET /api/v1/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(ProductDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/v1/products
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.create(request);
        return ProductDto.fromEntity(product);
    }

    // PUT /api/v1/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request)
                .map(ProductDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/v1/products/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        return new ErrorResponse("VALIDATION_ERROR", String.join(", ", errors));
    }
}
```

---

## 8. Spring Validation

```java
public class CreateProductRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    @Email
    private String supplierEmail;
}
```

---

*📌 Tiếp theo: [Interview-QA.md](Interview-QA.md) | [Practice-Exercises.md](Practice-Exercises.md)*
