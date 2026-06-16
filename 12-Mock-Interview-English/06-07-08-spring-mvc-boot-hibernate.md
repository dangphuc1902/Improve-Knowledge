# PART 6 — Spring MVC
# PART 7 — Spring Boot
# PART 8 — Hibernate & JPA

---

# ═══════════════════════════════════════
# PART 6: SPRING MVC
# ═══════════════════════════════════════

## 🔷 DispatcherServlet & Request Lifecycle

### Q1: Explain the Spring MVC Request Lifecycle

```
HTTP Request
    ↓
1. DispatcherServlet (Front Controller)
    ↓
2. HandlerMapping → finds @Controller method
    ↓
3. HandlerAdapter → invokes the method
    ↓
4. Controller method executes
    - @RequestBody deserialized (Jackson)
    - Business logic via @Service
    - Repository via @Repository
    ↓
5. Returns: ResponseEntity / @ResponseBody / ModelAndView
    ↓
6. ViewResolver (if ModelAndView)
    ↓
7. HttpMessageConverter (JSON serialization)
    ↓
HTTP Response
```

**DispatcherServlet is a single Servlet that:**
- Receives all requests
- Delegates to the appropriate handler
- Coordinates the full request/response flow

---

### Q2: @Controller vs @RestController

```java
// @Controller — returns View name (for server-side rendering)
@Controller
public class HomeController {
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("user", "Phuc");
        return "home";  // View template name (Thymeleaf, JSP)
    }
}

// @RestController = @Controller + @ResponseBody
// Returns JSON/XML directly
@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
}
```

---

### Q3: Exception Handling in Spring MVC

```java
// Global exception handler (best practice)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

---

### Q4: Bean Validation

```java
// DTO with validation
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @Email(message = "Invalid email format")
    @NotNull
    private String email;

    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150)
    private int age;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;
}

// Controller
@PostMapping("/users")
public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest req) {
    // @Valid triggers Bean Validation
    // If fails → MethodArgumentNotValidException → handled by @ControllerAdvice
    return ResponseEntity.created(...).body(userService.create(req));
}
```

---

# ═══════════════════════════════════════
# PART 7: SPRING BOOT
# ═══════════════════════════════════════

## 🔷 Auto Configuration

### Q5: How does Spring Boot Auto Configuration work?

**The magic is `@EnableAutoConfiguration`:**

```
1. Reads META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   (or spring.factories in older versions)

2. For each AutoConfiguration class, checks @Conditional annotations:
   - @ConditionalOnClass — only if class is on classpath
   - @ConditionalOnMissingBean — only if bean not already defined
   - @ConditionalOnProperty — only if property set

3. Registers beans automatically
```

**Example — DataSource auto-config:**
```java
@AutoConfiguration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {
    // Creates DataSource bean if DataSource class is on classpath
    // and no custom DataSource bean defined
}
```

**To debug auto-config:**
```bash
java -jar app.jar --debug
# Or
management.endpoints.web.exposure.include=conditions
```

---

### Q6: application.properties vs application.yml

```yaml
# application.yml — YAML format, hierarchical
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

server:
  port: 8080
```

```properties
# application.properties — flat key-value
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
server.port=8080
```

**Best practice:** Use `.yml` for complex hierarchical config, `.properties` for simple settings.

---

### Q7: Spring Profiles

```yaml
# application.yml (common)
spring:
  application:
    name: fpm-user-service

---
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fpm_dev
  config:
    activate:
      on-profile: dev

---
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/fpm_prod
  config:
    activate:
      on-profile: prod
```

```java
// Activate profile via:
// 1. Environment variable: SPRING_PROFILES_ACTIVE=prod
// 2. JVM arg: -Dspring.profiles.active=prod
// 3. application.properties: spring.profiles.active=dev

@Profile("dev")
@Component
class MockPaymentService implements PaymentService { ... }

@Profile("prod")
@Component
class StripePaymentService implements PaymentService { ... }
```

---

### Q8: Spring Actuator

```yaml
# Enable all endpoints
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

**Key endpoints:**
| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health status |
| `/actuator/info` | App info (version, build) |
| `/actuator/metrics` | JVM, HTTP, custom metrics |
| `/actuator/env` | Environment properties |
| `/actuator/beans` | All Spring beans |
| `/actuator/mappings` | All @RequestMapping |
| `/actuator/loggers` | Runtime log level change |
| `/actuator/threaddump` | Thread dump |
| `/actuator/heapdump` | Heap dump |

**Custom Health Indicator:**
```java
@Component
public class RedisHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            redisTemplate.opsForValue().get("health-check");
            builder.up().withDetail("redis", "available");
        } catch (Exception e) {
            builder.down().withDetail("redis", "unavailable").withException(e);
        }
    }
}
```

---

### Q9: Spring Boot Exception Handling (Scenario)

**Scenario:** "Our API is returning 500 for all errors. How do you fix this?"

```java
// Step 1: Define custom exception hierarchy
public class AppException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public AppException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resource, Object id) {
        super("NOT_FOUND", resource + " with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}

// Step 2: Global handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        return ResponseEntity.status(ex.getStatus())
            .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }
}

// Step 3: Use it
public User getUser(Long id) {
    return userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", id));
}
```

---

# ═══════════════════════════════════════
# PART 8: HIBERNATE & JPA
# ═══════════════════════════════════════

## 🔷 Entity Lifecycle & Persistence Context

### Q10: Explain JPA Entity States

```
Transient → Managed → Detached → Removed
```

```java
// Transient: not associated with any persistence context
User user = new User("phuc"); // no id, not tracked

// Managed: inside persistence context, changes auto-synced (dirty checking)
entityManager.persist(user); // now managed
user.setName("phuc2"); // this will be saved on flush/commit — no explicit save()!

// Detached: was managed, context closed or explicitly detached
entityManager.detach(user); // or session closed
user.setName("phuc3"); // this change is NOT tracked

// Removed: scheduled for deletion
entityManager.remove(user);
```

---

### Q11: What is Dirty Checking?

> Hibernate tracks the *original state* of managed entities when loaded. On transaction commit/flush, it compares current state vs. original. If different → automatically generates UPDATE SQL.

```java
@Transactional
public void updateUserName(Long id, String newName) {
    User user = userRepo.findById(id).orElseThrow();
    user.setName(newName);
    // NO userRepo.save(user) needed!
    // Hibernate dirty checking will generate UPDATE at commit
}
```

---

### Q12: Lazy Loading vs Eager Loading

```java
@Entity
class Order {
    @Id Long id;

    // LAZY — loads items only when accessed
    @OneToMany(fetch = FetchType.LAZY)
    List<OrderItem> items;

    // EAGER — loads items with order always
    @ManyToOne(fetch = FetchType.EAGER)
    User user;
}
```

**Default fetch types:**
| Relationship | Default |
|-------------|---------|
| @OneToMany | LAZY |
| @ManyToMany | LAZY |
| @ManyToOne | EAGER |
| @OneToOne | EAGER |

**Best practice:** Keep everything LAZY, use JOIN FETCH when needed.

---

### Q13: N+1 Problem (Most Common Interview Question!)

**Problem:**
```java
// Load 100 orders → 1 query
List<Order> orders = orderRepo.findAll();

// Then for each order, access lazy collection → 100 MORE queries!
orders.forEach(order -> order.getItems().size()); // N+1 = 101 queries!
```

**Solutions:**

```java
// Solution 1: JOIN FETCH in JPQL
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.userId = :userId")
List<Order> findOrdersWithItems(@Param("userId") Long userId);

// Solution 2: @EntityGraph
@EntityGraph(attributePaths = {"items", "user"})
List<Order> findAll();

// Solution 3: Batch Size (reduce to N/batch + 1 queries)
@OneToMany
@BatchSize(size = 50)
List<OrderItem> items;

// Solution 4: Native Query with JOIN
@Query(value = "SELECT o.*, i.* FROM orders o JOIN order_items i ON o.id = i.order_id",
       nativeQuery = true)
```

**Detect N+1:**
```yaml
# application.yml — log SQL
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        generate_statistics: true
```

---

### Q14: JPQL vs Native Query vs Criteria API

```java
// JPQL — object-oriented, entity names not table names
@Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
Optional<User> findByEmail(@Param("email") String email);

// Native Query — actual SQL
@Query(value = "SELECT * FROM users WHERE email = :email AND active = 1",
       nativeQuery = true)
Optional<User> findByEmailNative(@Param("email") String email);

// Criteria API — type-safe, programmatic (for dynamic queries)
public List<User> searchUsers(String name, String email, Boolean active) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    Root<User> root = query.from(User.class);

    List<Predicate> predicates = new ArrayList<>();
    if (name != null) predicates.add(cb.like(root.get("name"), "%" + name + "%"));
    if (email != null) predicates.add(cb.equal(root.get("email"), email));
    if (active != null) predicates.add(cb.equal(root.get("active"), active));

    query.where(predicates.toArray(new Predicate[0]));
    return em.createQuery(query).getResultList();
}
```

---

## 📋 Hibernate Quick Reference

| Concept | Key Point |
|---------|-----------|
| Dirty Checking | Auto-detect changes in managed entities |
| N+1 Problem | 1 + N queries instead of 1 JOIN query |
| Fix N+1 | JOIN FETCH, @EntityGraph, @BatchSize |
| Lazy Loading | Load on access (default for @OneToMany) |
| Eager Loading | Load immediately (default for @ManyToOne) |
| Persistence Context | 1st-level cache, tracks managed entities |
| Session | Per-request, wraps persistence context |
| @Transactional | Required for dirty checking to flush |
