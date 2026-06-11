# ❓ Spring Framework - Câu Hỏi Phỏng Vấn

---

## Spring Core

### Q1: IoC và DI là gì? Tại sao quan trọng?

**IoC (Inversion of Control):** Nguyên lý đảo ngược quyền kiểm soát - thay vì class tự tạo dependencies, framework (Spring Container) tạo và quản lý. Class chỉ khai báo những gì nó cần.

**DI (Dependency Injection):** Cơ chế cụ thể của IoC - inject dependencies vào class từ bên ngoài.

**Lợi ích:**
- Loose coupling → dễ thay đổi implementation
- Testable → dễ mock dependencies
- Reusable → component độc lập

---

### Q2: Các cách inject dependency trong Spring?

**1. Constructor Injection (Recommended):**
```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    
    @Autowired  // Optional với 1 constructor
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```
**Ưu điểm:** Immutable, required deps rõ ràng, dễ test

**2. Setter Injection:**
```java
@Autowired
public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```
**Khi dùng:** Optional dependencies

**3. Field Injection (Không khuyến nghị):**
```java
@Autowired
private PaymentService paymentService;
```
**Nhược điểm:** Khó test, ẩn dependencies

---

### Q3: Bean Scope trong Spring là gì?

| Scope | Mô tả |
|-------|-------|
| **singleton** | 1 instance/container (default) |
| **prototype** | Mới mỗi lần request |
| **request** | 1 instance/HTTP request |
| **session** | 1 instance/HTTP session |
| **application** | 1 instance/ServletContext |

**Lưu ý:** Inject prototype bean vào singleton → sẽ dùng cùng 1 instance (vấn đề!). Fix: dùng `@Lookup` hoặc `ApplicationContext.getBean()`

---

### Q4: @Component, @Service, @Repository, @Controller khác nhau?

Tất cả đều là specialization của `@Component` (Spring sẽ auto-detect và tạo bean).

| Annotation | Layer | Thêm gì đặc biệt |
|-----------|-------|-----------------|
| `@Component` | Generic | Không |
| `@Service` | Business | Không (semantic) |
| `@Repository` | Data | Exception translation (SQL → Spring exceptions) |
| `@Controller` | Web | Xử lý HTTP requests |
| `@RestController` | Web | `@Controller` + `@ResponseBody` |

---

### Q5: @Autowired và @Qualifier dùng như thế nào?

```java
public interface NotificationService {
    void send(String message);
}

@Service("emailService")
public class EmailService implements NotificationService { ... }

@Service("smsService")
public class SmsService implements NotificationService { ... }

@Service
public class UserService {
    @Autowired
    @Qualifier("emailService")  // Chỉ định bean cụ thể khi có nhiều implementation
    private NotificationService notificationService;
}
```

---

### Q6: ApplicationContext vs BeanFactory?

- **BeanFactory**: Interface cơ bản, lazy initialization (bean tạo khi request)
- **ApplicationContext**: Extends BeanFactory, thêm: i18n, Event publishing, eager singleton init, AOP support
- **Thực tế:** Luôn dùng `ApplicationContext`

---

## Spring MVC

### Q7: DispatcherServlet làm gì?

**DispatcherServlet** = Front Controller trong Spring MVC. Nhận mọi request, phân phối đến Handler phù hợp:

```
Request → DispatcherServlet → HandlerMapping → Controller → ModelAndView 
       → ViewResolver → View → Response
```

---

### Q8: @Controller vs @RestController?

- **@Controller**: Trả về View name (String) để render HTML
- **@RestController**: Trả về data (JSON/XML), tự động serialize với `@ResponseBody`

```java
@Controller
public class PageController {
    @GetMapping("/home")
    public String home(Model model) {
        return "home";  // View name
    }
}

@RestController
public class ApiController {
    @GetMapping("/api/users")
    public List<User> getUsers() {
        return userService.findAll();  // JSON response
    }
}
```

---

### Q9: @RequestParam vs @PathVariable vs @RequestBody?

```java
// @PathVariable - URL path segment
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) { ... }
// URL: /users/123

// @RequestParam - query parameter
@GetMapping("/users")
public Page<User> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(required = false) String name) { ... }
// URL: /users?page=1&name=Alice

// @RequestBody - request body (JSON → Object)
@PostMapping("/users")
public User createUser(@RequestBody @Valid CreateUserRequest request) { ... }
// Body: {"name":"Alice","email":"alice@example.com"}
```

---

### Q10: Validation trong Spring MVC như thế nào?

```java
// DTO với validation annotations
public class CreateUserRequest {
    @NotBlank private String name;
    @Email private String email;
    @Min(18) @Max(120) private int age;
}

// Controller
@PostMapping("/users")
public ResponseEntity<User> createUser(
    @Valid @RequestBody CreateUserRequest request,
    BindingResult result) {
    if (result.hasErrors()) {
        return ResponseEntity.badRequest().build();
    }
    // ...
}

// Hoặc dùng @RestControllerAdvice để handle globally
```

---

## Spring Boot

### Q11: Spring Boot auto-configuration hoạt động thế nào?

1. `@EnableAutoConfiguration` (có trong `@SpringBootApplication`) kích hoạt auto-config
2. Spring Boot scan `spring.factories` / `AutoConfiguration.imports` trong các starter JAR
3. Mỗi `@Configuration` class có `@Conditional` → chỉ apply nếu điều kiện đúng
4. VD: `DataSourceAutoConfiguration` chỉ chạy khi có `DataSource` class trong classpath và chưa có DataSource bean nào

---

### Q12: @SpringBootApplication làm gì?

```java
@SpringBootApplication
// = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

---

### Q13: Spring Boot profiles là gì?

Cho phép có nhiều configuration cho môi trường khác nhau:

```properties
# application.properties (chung)
app.name=MyApp

# application-dev.properties
spring.datasource.url=jdbc:h2:mem:devdb

# application-prod.properties
spring.datasource.url=jdbc:mysql://prod-server/mydb
```

```bash
# Activate profile
java -jar myapp.jar --spring.profiles.active=prod
```

---

### Q14: @Transactional hoạt động như thế nào?

```java
@Service
public class OrderService {
    
    @Transactional  // Spring tạo proxy, wrap method trong transaction
    public Order placeOrder(OrderRequest request) {
        // Nếu có exception unchecked → rollback tự động
        Order order = orderRepository.save(new Order(request));
        paymentService.charge(order);  // Nếu fail → toàn bộ rollback
        inventoryService.reserve(order);
        return order;
    }
    
    @Transactional(readOnly = true)  // Tối ưu cho read-only
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}
```

**Propagation types:** REQUIRED (default), REQUIRES_NEW, SUPPORTS, MANDATORY, NOT_SUPPORTED, NEVER, NESTED

---

### Q15: Sự khác biệt giữa Spring MVC và Spring Boot?

- **Spring MVC**: Framework xử lý HTTP requests theo MVC pattern. Cần config nhiều (XML hoặc Java config).
- **Spring Boot**: Build on top of Spring, cung cấp auto-configuration, embedded server (Tomcat), starter dependencies → start nhanh hơn nhiều.

Spring Boot KHÔNG thay thế Spring MVC - nó sử dụng Spring MVC bên dưới nhưng giảm boilerplate.

---

*📌 Xem tiếp: [Practice-Exercises.md](Practice-Exercises.md)*
