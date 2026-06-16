# PART 5-7 - SPRING FRAMEWORK, SPRING MVC & SPRING BOOT

> **Topics**: IoC · DI · Bean Lifecycle · AOP · Transactions · Security · MVC · Boot

---

## PART 5 — SPRING FRAMEWORK CORE

### Q1. What is IoC (Inversion of Control)?

**Beginner:**
IoC is a design principle where the **control of object creation is transferred from the developer to the framework**.

Instead of your code creating dependencies (`new SomeService()`), Spring creates and injects them for you.

```java
// Without IoC (Traditional)
public class OrderController {
    private OrderService orderService = new OrderService(); // YOU control creation
    private EmailService emailService = new EmailService(); // Tight coupling!
}

// With IoC (Spring)
@RestController
public class OrderController {
    private final OrderService orderService;    // Spring creates and injects
    private final EmailService emailService;

    public OrderController(OrderService orderService, EmailService emailService) {
        this.orderService = orderService;      // Constructor injection (recommended)
        this.emailService = emailService;
    }
}
```

**Intermediate:**
- IoC Container = Spring ApplicationContext
- The container reads configuration (annotations or XML), creates beans, wires them together
- This enables loose coupling, testability, and modularity

**Senior:**
- ApplicationContext vs BeanFactory: AC is more feature-rich (AOP, events, i18n)
- Bean creation can be customized via BeanPostProcessor, BeanFactoryPostProcessor
- In a large app, you can have multiple ApplicationContexts (parent-child hierarchy)

---

### Q2. Types of Dependency Injection

```java
// 1. Constructor Injection (RECOMMENDED)
@Service
public class UserService {
    private final UserRepository repo;
    private final EmailService emailService;

    public UserService(UserRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }
    // Benefits: immutable, testable, fails fast, no circular dependency tricks
}

// 2. Setter Injection (for optional dependencies)
@Service
public class NotificationService {
    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}

// 3. Field Injection (NOT recommended for production)
@Service
public class ProductService {
    @Autowired  // Hidden dependency, hard to test
    private ProductRepository repo;
}
```

**Why Constructor Injection is best:**
- Dependencies are explicit and required
- Class can be instantiated without Spring (easy unit testing)
- Immutable (final fields)
- Spring detects circular dependencies at startup

---

### Q3. Spring Bean Lifecycle

```
Bean Lifecycle:
1. Instantiation (constructor called)
2. Property Population (DI - @Autowired fields set)
3. BeanNameAware.setBeanName()
4. BeanFactoryAware.setBeanFactory()
5. ApplicationContextAware.setApplicationContext()
6. BeanPostProcessor.postProcessBeforeInitialization()
7. @PostConstruct / InitializingBean.afterPropertiesSet()
8. Custom init-method
9. BeanPostProcessor.postProcessAfterInitialization()
10. Bean is ready to use
11. (On shutdown) @PreDestroy / DisposableBean.destroy()
12. Custom destroy-method
```

```java
@Component
public class DatabaseInitializer {
    @PostConstruct
    public void init() {
        System.out.println("Bean created, running DB setup...");
        // Run DB migrations, load initial data, etc.
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Application shutting down, releasing resources...");
        // Close connections, flush caches, etc.
    }
}
```

---

### Q4. Bean Scopes

| Scope | Description | Use Case |
|---|---|---|
| `singleton` | One instance per ApplicationContext (default) | Stateless services |
| `prototype` | New instance per injection/request | Stateful objects |
| `request` | One per HTTP request | Web: request-specific data |
| `session` | One per HTTP session | Web: user session data |
| `application` | One per ServletContext | Web: app-wide shared state |

```java
@Component
@Scope("prototype")
public class ReportGenerator {
    private List<String> lines = new ArrayList<>(); // Stateful - needs prototype

    public void addLine(String line) { lines.add(line); }
    public String generate() { return String.join("\n", lines); }
}

// Problem: Injecting prototype into singleton
@Service
public class ReportService {
    @Autowired
    private ReportGenerator generator; // WRONG! Only one instance created
    
    // Fix: Use ApplicationContext.getBean() or @Lookup or ObjectFactory
    @Autowired
    private ObjectFactory<ReportGenerator> generatorFactory;
    
    public String createReport() {
        ReportGenerator gen = generatorFactory.getObject(); // New instance each time
        gen.addLine("Report data...");
        return gen.generate();
    }
}
```

---

### Q5. Spring AOP (Aspect-Oriented Programming)

**Beginner**: AOP separates cross-cutting concerns (logging, security, transactions) from business logic.

**Intermediate:**
```java
// Key concepts
@Aspect
@Component
public class LoggingAspect {

    // Pointcut: WHERE to apply (method pattern)
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    // Before Advice: runs BEFORE method
    @Before("serviceLayer()")
    public void logBefore(JoinPoint jp) {
        log.info("Calling: {}.{}({})", 
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            Arrays.toString(jp.getArgs()));
    }

    // After Returning: runs after successful execution
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfter(JoinPoint jp, Object result) {
        log.info("Returned: {}", result);
    }

    // Around: wraps method execution (most powerful)
    @Around("serviceLayer()")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed(); // Actual method call
        long elapsed = System.currentTimeMillis() - start;
        log.info("{} took {}ms", pjp.getSignature(), elapsed);
        return result;
    }
}
```

**Senior — How AOP works internally:**
- Spring creates a **proxy** of the target bean (JDK Dynamic Proxy or CGLIB)
- When you call a proxied method, it goes through the aspect chain first
- `@Transactional`, `@Cacheable`, `@Async` all work via AOP proxies
- **Self-invocation problem**: Calling a @Transactional method from within the same class bypasses the proxy!

---

### Q6. Transaction Management

```java
@Service
@Transactional // Class-level: applies to all public methods
public class OrderService {

    // REQUIRED (default) - join existing or create new
    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder(Order order) {
        orderRepository.save(order);
        inventoryService.deductStock(order); // Joins this transaction
        paymentService.charge(order);        // Joins this transaction
    }

    // REQUIRES_NEW - always creates new transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog log) {
        // Runs in a SEPARATE transaction - committed even if outer fails
        auditRepository.save(log);
    }

    // Read-only optimization
    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    // Rollback rules
    @Transactional(rollbackFor = PaymentException.class,
                   noRollbackFor = InsufficientInventoryWarning.class)
    public void processPayment(Payment payment) {
        // PaymentException → rollback
        // InsufficientInventoryWarning → NO rollback (just a warning)
    }
}
```

**Propagation Types:**
| Type | Behavior |
|---|---|
| REQUIRED | Join existing, or create new |
| REQUIRES_NEW | Always create new (suspend existing) |
| NESTED | Create savepoint within existing |
| SUPPORTS | Join existing, or run non-transactional |
| NOT_SUPPORTED | Run non-transactional (suspend existing) |
| NEVER | Must run non-transactional |
| MANDATORY | Must have existing transaction |

---

### Q7. Spring Security — Core Concepts

**Beginner:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())  // Disable for REST APIs
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

**Intermediate — JWT Filter:**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

**Senior — Security Filter Chain order, Method Security:**
```java
@EnableMethodSecurity
@Service
public class ProjectService {
    
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Project getProject(Long projectId, Long userId) { ... }
    
    @PostAuthorize("returnObject.ownerId == authentication.principal.id")
    public Project findById(Long id) { ... }
}
```

---

## PART 6 — SPRING MVC

### Q8. DispatcherServlet and Request Lifecycle

```
HTTP Request → Tomcat → DispatcherServlet
    ↓
HandlerMapping (finds which Controller handles this URL)
    ↓
HandlerAdapter (calls the Controller method)
    ↓
Controller method runs, returns ModelAndView or ResponseBody
    ↓
ViewResolver (for MVC) or HttpMessageConverter (for REST)
    ↓
HTTP Response
```

```java
// Complete REST Controller example
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable @Positive Long id) {
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Q9. Global Exception Handling in Spring MVC

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
            .body(new ValidationErrorResponse(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(500, "Internal Server Error"));
    }
}
```

---

### Q10. Validation in Spring MVC

```java
// DTO with validation annotations
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$",
             message = "Password must be 8+ chars with uppercase and digit")
    private String password;

    @Min(value = 18, message = "Must be at least 18 years old")
    @Max(value = 120)
    private Integer age;

    @NotNull
    @Valid  // Cascade validation to nested object
    private AddressRequest address;
}

// Custom validator
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email already registered";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && !userRepository.existsByEmail(email);
    }
}
```

---

## PART 7 — SPRING BOOT

### Q11. How Does Spring Boot Auto-Configuration Work?

**Beginner**: Spring Boot reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and conditionally creates beans based on what's on the classpath.

**Intermediate:**
```java
// How @ConditionalOnMissingBean works
@Configuration
@ConditionalOnClass(DataSource.class)           // Only if DataSource class is present
@ConditionalOnProperty(name = "spring.datasource.url") // Only if property set
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean   // Only creates if YOU haven't defined one
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}

// Override auto-config by defining your own
@Configuration
public class MyDataSourceConfig {
    @Bean
    public DataSource dataSource() {
        // Spring Boot won't create its own because @ConditionalOnMissingBean
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:oracle:thin:@localhost:1521/mydb");
        return ds;
    }
}
```

---

### Q12. Spring Boot Configuration — application.yml Deep Dive

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@${DB_HOST:localhost}:1521/${DB_NAME:mydb}
    username: ${DB_USER:admin}
    password: ${DB_PASS:secret}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: validate  # Production: validate, Dev: update
    show-sql: false
    open-in-view: false   # Disable OSIV anti-pattern

  cache:
    type: redis
    
server:
  port: 8080
  tomcat:
    threads:
      max: 200
      min-spare: 10

logging:
  level:
    com.example: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized

---
# Profile-specific config
spring:
  config:
    activate:
      on-profile: production

logging:
  level:
    com.example: INFO
```

---

### Q13. Spring Profiles

```java
// Activate: spring.profiles.active=dev,local
// Or: java -jar app.jar --spring.profiles.active=production

@Component
@Profile("dev")  // Only loaded in dev profile
public class MockEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("DEV MODE - Mock email to {}: {}", to, subject);
    }
}

@Component
@Profile("!dev")  // Everything except dev
public class RealEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        // Real SMTP implementation
    }
}

// Profile-specific properties files
application.properties           (default)
application-dev.properties       (dev profile)
application-staging.properties   (staging)
application-production.properties (production)
```

---

### Q14. Spring Boot Actuator

```yaml
# Enable actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: "*"   # All endpoints (restrict in production!)
  endpoint:
    health:
      show-details: always
    shutdown:
      enabled: true  # Graceful shutdown via HTTP
```

**Key Endpoints:**
- `/actuator/health` — Application health (DB, disk, Redis, custom checks)
- `/actuator/metrics` — JVM metrics, HTTP metrics, custom metrics
- `/actuator/info` — Application info (version, git commit, etc.)
- `/actuator/env` — All properties (filter sensitive ones!)
- `/actuator/loggers` — Change log levels at runtime without restart
- `/actuator/heapdump` — Download heap dump for analysis
- `/actuator/threaddump` — Active thread dump

```java
// Custom Health Indicator
@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {
    private final PaymentGatewayClient client;

    @Override
    public Health health() {
        try {
            client.ping();
            return Health.up()
                .withDetail("latency", "15ms")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

---

### Q15. Spring Data JPA — Scenario-Based Questions

**Scenario 1: Query Methods**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data generates query from method name
    List<User> findByEmailAndActiveTrue(String email);
    Optional<User> findByUsername(String username);
    List<User> findByAgeBetweenOrderByCreatedAtDesc(int minAge, int maxAge);
    
    // Custom JPQL
    @Query("SELECT u FROM User u WHERE u.department = :dept AND u.salary > :salary")
    List<User> findHighEarnersByDept(@Param("dept") String dept,
                                    @Param("salary") double salary);
    
    // Native SQL
    @Query(value = "SELECT * FROM users WHERE ROWNUM <= :limit", nativeQuery = true)
    List<User> findTopUsers(@Param("limit") int limit);
    
    // Modifying query
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
    int deactivateInactiveUsers(@Param("date") LocalDate date);
    
    // Projection
    @Query("SELECT u.id AS id, u.username AS username FROM User u WHERE u.active = true")
    List<UserSummary> findActiveSummaries();
}

// Projection interface
public interface UserSummary {
    Long getId();
    String getUsername();
}
```

**Scenario 2: Custom Repository with Criteria API**
```java
@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<User> findByFilters(UserFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (filter.getName() != null) {
            predicates.add(cb.like(root.get("name"), "%" + filter.getName() + "%"));
        }
        if (filter.getMinAge() != null) {
            predicates.add(cb.ge(root.get("age"), filter.getMinAge()));
        }
        
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
```

---

*Next: [Part 8 - Hibernate & JPA](./Part-08-Hibernate-JPA.md)*
