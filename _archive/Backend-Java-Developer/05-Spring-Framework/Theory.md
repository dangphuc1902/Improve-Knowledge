# 📖 Spring Framework - Lý Thuyết Chi Tiết

> **Tuần 4 | Spring Core, Spring MVC, Spring Boot**

---

## 1. Spring Framework Overview

### 1.1 Khái Niệm Spring Framework

**Spring Framework** là một framework Java mã nguồn mở phổ biến nhất dùng để xây dựng enterprise applications. Nó cung cấp một infrastructure hoàn chỉnh giúp developers tập trung vào business logic thay vì phải lo liệu về technical details.

**Tại sao cần Spring?**
- **Tháo gỡ coupling**: Quản lý dependencies tự động (IoC Container)
- **Boilerplate reduction**: Giảm lượng code lặp lại
- **Cross-cutting concerns**: Xử lý transactions, logging, security một cách thống nhất
- **Testability**: Dễ viết unit tests nhờ dependency injection
- **Ecosystem support**: Tích hợp với nhiều công nghệ khác (Database, Cache, Message Queue)

```
Spring Ecosystem - Cách Tổ Chức
├── Spring Core 🔧
│   ├── IoC Container - Quản lý bean lifecycle
│   ├── DI (Dependency Injection) - Inject dependencies
│   └── AOP (Aspect-Oriented Programming) - Cross-cutting concerns
│
├── Spring MVC 🌐
│   ├── DispatcherServlet - Front controller
│   ├── Controller - Xử lý request
│   ├── View - Render response (JSP, Thymeleaf, etc.)
│   └── Model - Data transfer object
│
├── Spring Boot ⚡
│   ├── Auto-configuration - Tự động setup dựa trên classpath
│   ├── Starter Dependencies - Pre-configured dependencies
│   ├── Embedded Server - Tomcat/Jetty/Undertow built-in
│   └── Application Properties - Externalized configuration
│
├── Spring Data 📊
│   ├── JPA Repository - ORM abstraction
│   ├── Spring Data MongoDB, Redis, Elasticsearch
│   └── Custom Query Methods
│
├── Spring Security 🔐
│   ├── Authentication - Login/logout
│   ├── Authorization - Role-based access control (RBAC)
│   ├── CSRF Protection - Cross-site request forgery
│   └── OAuth2/JWT - Token-based authentication
│
├── Spring Cloud ☁️
│   ├── Service Discovery (Eureka, Consul)
│   ├── Load Balancing (Ribbon, Spring Cloud LoadBalancer)
│   ├── Circuit Breaker (Hystrix, Resilience4j)
│   └── Distributed Tracing (Sleuth, Zipkin)
│
└── Spring Batch 🔄
    ├── Job Management - Orchestration của batch jobs
    ├── Step - Unit công việc trong một job
    ├── Retry/Skip Logic - Xử lý lỗi
    └── Parallel Processing - Xử lý dữ liệu lớn
```

---

## 2. IoC (Inversion of Control) & DI (Dependency Injection)

### 2.1 Lý Thuyết IoC

**Inversion of Control (IoC)** là một principle/design pattern cơ bản trong Spring Framework.

**Khái Niệm:**
- **Truyền thống (Tight Coupling)**: Class tự tạo và quản lý dependencies của nó
- **IoC (Loose Coupling)**: Thay vào đó, một container (IoC Container) sẽ tạo và manage các objects

**Ví dụ Minh Họa:**

```
┌─────────────────────────────────┐
│   Truyền Thống (Tight Coupling) │
└─────────────────────────────────┘

OrderService
    ├─ tự tạo EmailService
    ├─ tự tạo PaymentService
    ├─ tự tạo NotificationService
    └─ Khi một service thay đổi → cần update code

┌──────────────────────────────────┐
│   IoC (Loose Coupling)           │
└──────────────────────────────────┘

IoC Container (Spring)
    ├─ Tạo EmailService instance
    ├─ Tạo PaymentService instance
    ├─ Tạo NotificationService instance
    └─ Inject vào OrderService
    
OrderService
    └─ Nhận các dependencies từ container (không tự tạo)
```

### 2.2 Ví Dụ: Tight Coupling vs Loose Coupling

#### ❌ Trước Spring (Tight Coupling - BAD)
```java
public class OrderService {
    // ❌ PROBLEM: OrderService phải biết cách tạo các dependencies
    // ❌ Nếu EmailService constructor thay đổi → OrderService phải thay đổi
    // ❌ Khó test vì phải tạo real instances thay vì mock
    
    private EmailService emailService = new EmailService();
    private PaymentService paymentService = new PaymentService();
    private NotificationService notificationService = new NotificationService();

    public void placeOrder(Order order) {
        // Quá trình:
        // 1. Validate order
        // 2. Process payment
        // 3. Send confirmation email
        // 4. Send notifications
        
        paymentService.process(order);
        emailService.sendConfirmation(order);
        notificationService.notifyUser(order);
    }
}

// Khi muốn test OrderService, bắt buộc phải tạo real instances:
public void testPlaceOrder() {
    // ❌ Tạo real instances - phải có database connection, email server, etc.
    OrderService orderService = new OrderService();
    orderService.placeOrder(new Order());
    // Không thể mock dependencies để test riêng logic của OrderService
}
```

#### ✅ Sau Spring (Loose Coupling - GOOD)
```java
// Step 1: Định nghĩa interfaces
public interface EmailService {
    void sendConfirmation(Order order);
}

public interface PaymentService {
    void process(Order order);
}

public interface NotificationService {
    void notifyUser(Order order);
}

// Step 2: Implement các interfaces
@Component
public class GmailEmailService implements EmailService {
    @Override
    public void sendConfirmation(Order order) {
        // Send email via Gmail API
    }
}

@Component
public class PayPalPaymentService implements PaymentService {
    @Override
    public void process(Order order) {
        // Process payment via PayPal
    }
}

@Component
public class PushNotificationService implements NotificationService {
    @Override
    public void notifyUser(Order order) {
        // Send push notification
    }
}

// Step 3: OrderService inject dependencies
@Service
public class OrderService {
    // ✅ GOOD: Dependencies inject từ IoC Container
    // ✅ OrderService không cần biết cách tạo chúng
    // ✅ Dễ dàng swap implementations (thay Gmail bằng SMTP, etc.)
    private final EmailService emailService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    // Constructor Injection (RECOMMENDED)
    @Autowired
    public OrderService(EmailService emailService, 
                       PaymentService paymentService,
                       NotificationService notificationService) {
        this.emailService = emailService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    public void placeOrder(Order order) {
        paymentService.process(order);
        emailService.sendConfirmation(order);
        notificationService.notifyUser(order);
    }
}

// ✅ Khi test, dễ dàng mock dependencies:
@Test
public void testPlaceOrder() {
    // Mock các dependencies
    EmailService mockEmailService = mock(EmailService.class);
    PaymentService mockPaymentService = mock(PaymentService.class);
    NotificationService mockNotificationService = mock(NotificationService.class);
    
    // Inject mocks
    OrderService orderService = new OrderService(
        mockEmailService, 
        mockPaymentService,
        mockNotificationService
    );
    
    // Test logic
    Order order = new Order();
    orderService.placeOrder(order);
    
    // Verify
    verify(mockPaymentService).process(order);
    verify(mockEmailService).sendConfirmation(order);
}
```

### 2.3 IoC Container - Cách Hoạt Động

```
┌─────────────────────────────────────────────────────┐
│           Spring IoC Container                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│  1️⃣  Bean Definitions Collection                    │
│      ├─ XML config files                            │
│      ├─ @Configuration classes                      │
│      └─ @Component/@Service annotations             │
│                                                     │
│  2️⃣  Bean Instantiation                             │
│      ├─ Reflection API để tạo instances            │
│      ├─ Constructor-based instantiation            │
│      └─ Static factory method                       │
│                                                     │
│  3️⃣  Dependency Resolution                          │
│      ├─ Analyze dependencies                        │
│      ├─ Find matching beans                         │
│      └─ Handle circular dependencies                │
│                                                     │
│  4️⃣  Lifecycle Management                           │
│      ├─ @PostConstruct initialization              │
│      ├─ @PreDestroy cleanup                        │
│      └─ Singleton/Prototype scope handling          │
│                                                     │
│  5️⃣  Bean Registry (In-Memory Storage)              │
│      └─ Map<beanName, beanInstance>                │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 2.4 Dependency Injection Methods

#### 1️⃣ Constructor Injection (RECOMMENDED)
```java
// ✅ BEST PRACTICE
// Ưu điểm:
// - Immutable: fields là final, không thay đổi sau initialization
// - Testability: dễ tạo instances trong tests
// - Explicit dependencies: rõ ràng class cần gì
// - Circular dependency detection: Spring phát hiện vòng lặp lúc startup

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final LoggingService loggingService;

    @Autowired  // Optional từ Spring 4.3+, nếu chỉ có 1 constructor
    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       LoggingService loggingService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.loggingService = loggingService;
    }

    public User createUser(CreateUserRequest request) {
        // Sử dụng dependencies
        User newUser = new User(request.getName(), request.getEmail());
        User savedUser = userRepository.save(newUser);
        emailService.sendWelcomeEmail(savedUser);
        loggingService.log("User created: " + savedUser.getId());
        return savedUser;
    }
}
```

#### 2️⃣ Setter Injection
```java
// ⚠️  KHÔNG RECOMMEND (nhưng vẫn sử dụng trong một số trường hợp)
// Nhược điểm:
// - Có thể null nếu quên inject
// - Mutable: dependencies có thể thay đổi runtime
// - Không rõ ràng dependencies bắt buộc
// Dùng khi: Optional dependencies

@Service
public class ReportService {
    private UserRepository userRepository;
    private Optional<AnalyticsService> analyticsService; // Optional dependency

    // Main constructor with required dependencies
    @Autowired
    public ReportService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Setter for optional dependency
    @Autowired(required = false)
    public void setAnalyticsService(AnalyticsService analyticsService) {
        this.analyticsService = Optional.ofNullable(analyticsService);
    }

    public void generateReport() {
        List<User> users = userRepository.findAll();
        analyticsService.ifPresent(analytics -> analytics.track("report_generated"));
    }
}
```

#### 3️⃣ Field Injection
```java
// ❌ KHÔNG RECOMMEND (dễ gây mất an toàn)
// Nhược điểm:
// - Khó tạo instances trong tests (không có constructor)
// - Không rõ ràng dependencies
// - Reflection-based injection chậm hơn
// - Có thể null nếu quên @Autowired

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;  // ❌ Không rõ ràng

    @Autowired
    private EmailService emailService;

    public void placeOrder(Order order) {
        orderRepository.save(order);
        emailService.sendConfirmation(order);
    }
}

// Test sẽ khó khăn:
@Test
public void testPlaceOrder() {
    OrderService orderService = new OrderService();
    // ❌ Không thể inject mock dependencies dễ dàng
    // ❌ Phải dùng ReflectionTestUtils để set fields
}
```

---

## 3. Spring Annotations & Components

### 3.1 Component Annotations (Stereotypes)

```java
// 🔧 @Component - Generic bean, cho bất kỳ class nào
// Dùng khi: Class không rõ tầng nào (utility, helper)
@Component
public class FileUploadUtil {
    public void upload(File file) { /* ... */ }
}

// 🏢 @Service - Business Logic Layer
// Dùng khi: Chứa business logic, calculation, workflow
// Spring thêm @Transactional nếu configure
@Service
public class UserService {
    public void registerUser(UserRegistrationForm form) {
        // Validate, create, save, send emails, etc.
    }
}

// 💾 @Repository - Data Access Layer
// Dùng khi: Tương tác với database (DAO, Repository pattern)
// Ưu điểm: Exception translation - convert SQL exceptions → DataAccessException
@Repository
public class UserRepository {
    public User findById(Long id) {
        try {
            // Database query
        } catch (SQLException e) {
            // Spring tự convert sang DataAccessException
            throw new DataAccessException(e);
        }
    }
}

// 🌐 @Controller - Web MVC Layer
// Dùng khi: Xử lý HTTP requests, trả về Model + View
@Controller
@RequestMapping("/users")
public class UserController {
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user/detail";  // View name
    }
}

// 🔌 @RestController - REST API Layer
// Dùng khi: REST endpoints (JSON responses, không phải HTML view)
// = @Controller + @ResponseBody (tất cả methods trả JSON)
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new UserDto(user));
    }
}

// 🎯 @Configuration - Java-based configuration
// Dùng khi: Định nghĩa beans bằng Java thay vì XML
// Mỗi @Bean method trả về một bean
@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(/* config */);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

### 3.2 Chi Tiết @Bean & @Configuration

```java
// 🎛️ Configuration Class - Giống như XML config nhưng dùng Java
@Configuration
public class ApplicationConfig {
    
    // 1️⃣ Simple Bean Definition
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        ds.setUsername("root");
        ds.setPassword("secret");
        ds.setMaximumPoolSize(20);
        return ds;
    }
    
    // 2️⃣ Bean with Dependencies (method parameter = dependency)
    // Spring tự inject dataSource vào method này
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    // 3️⃣ Multiple Implementations with @Qualifier
    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource() {
        return new HikariDataSource(/* mysql config */);
    }
    
    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        return new HikariDataSource(/* postgres config */);
    }
    
    @Bean
    public UserRepository userRepository(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new JdbcUserRepository(dataSource);
    }
    
    // 4️⃣ Profile-Specific Beans
    @Bean
    @Profile("dev")  // Chỉ active trong "dev" profile
    public DataSource h2DataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("classpath:schema.sql")
                .addScript("classpath:data-dev.sql")
                .build();
    }
    
    @Bean
    @Profile("prod")  // Chỉ active trong "prod" profile
    public DataSource productionDataSource() {
        return new HikariDataSource(/* production config */);
    }
    
    // 5️⃣ Conditional Beans
    @Bean
    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")  // Nếu Redis client trong classpath
    public CacheManager redisCacheManager() {
        return new RedisCacheManager(jedisConnectionFactory());
    }
    
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)  // Nếu không có CacheManager bean
    public CacheManager simpleCacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    // 6️⃣ Bean Lifecycle Management
    @Bean(initMethod = "connect", destroyMethod = "disconnect")
    public DatabaseConnection databaseConnection() {
        return new DatabaseConnection();
    }
    
    // Tương đương:
    @Bean
    public DatabaseConnection databaseConnection2() {
        DatabaseConnection conn = new DatabaseConnection();
        conn.connect();  // Gọi ngay lập tức
        return conn;
    }
}

// Usage trong tests
@SpringBootTest
public class ConfigTest {
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;
    
    @Test
    public void testDataSourceInjection() {
        assertNotNull(dataSource);
    }
}
```

---

## 4. Bean Scope - Lifecycle & Visibility

### 4.1 Hiểu Rõ Bean Scope

**Scope** định nghĩa khoảng đời (lifetime) và visibility của bean trong container.

```
┌─────────────────────────────────────────────────────────────┐
│                    Bean Scope Types                         │
├──────────────┬──────────────┬──────────────┬────────────────┤
│   Singleton  │  Prototype   │   Request    │    Session     │
├──────────────┼──────────────┼──────────────┼────────────────┤
│ 1 instance   │ New instance │ 1 per HTTP   │ 1 per user     │
│ per container│ per request  │ request      │ session        │
│              │              │              │                │
│ Lifetime:    │ Lifetime:    │ Lifetime:    │ Lifetime:      │
│ Application  │ Until GC     │ Request      │ Session        │
│ startup to   │              │ processing   │ lifetime       │
│ shutdown     │              │              │                │
│              │              │              │                │
│ Use for:     │ Use for:     │ Use for:     │ Use for:       │
│ Services     │ Stateful     │ Request      │ User shopping  │
│ Repositories │ beans        │ context data │ cart           │
│ Utilities    │ DTOs         │              │ User info      │
└──────────────┴──────────────┴──────────────┴────────────────┘
```

### 4.2 Chi Tiết Từng Scope

#### 🔄 Singleton (Default & Recommended)
```java
// 1️⃣ SINGLETON SCOPE - DEFAULT
// - Spring tạo EXACTLY 1 instance duy nhất khi application start
// - Tất cả requests đều dùng same instance
// - Tiết kiệm memory, tốc độ nhanh
// - ⚠️ MUST BE STATELESS (không lưu request-specific data)

@Service
@Scope("singleton")  // Hoặc @Scope(SCOPE_SINGLETON)
public class UserService {
    private final UserRepository userRepository;  // ✅ Stateless dependency
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // ✅ GOOD: Method không lưu state
    public User findUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    // ❌ BAD: Lưu request-specific data ở instance level
    // private User currentUser;  // ❌ Nhiều threads sẽ overwrite nhau
}

// Singleton diagram:
Container Start
    ↓
Create UserService instance (1 lần)
    ↓
Application Running
    ├─ Request 1 → Use same instance
    ├─ Request 2 → Use same instance
    ├─ Request 3 → Use same instance
    └─ Request N → Use same instance
```

#### 🔄 Prototype (Tạo mới mỗi lần)
```java
// 2️⃣ PROTOTYPE SCOPE
// - Spring tạo NEW instance mỗi khi được inject hoặc request
// - Khác singleton: mỗi @Autowired, mỗi getBean() → new instance
// - Dùng khi: Bean có stateful data

@Component
@Scope("prototype")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();  // ✅ State per user
    private BigDecimal total = BigDecimal.ZERO;
    
    public void addItem(Item item) {
        items.add(item);
        total = total.add(item.getPrice());
    }
    
    public BigDecimal getTotal() {
        return total;
    }
}

@Service
public class OrderService {
    @Autowired
    private ShoppingCart shoppingCart1;  // ← New instance
    
    @Autowired
    private ShoppingCart shoppingCart2;  // ← Different instance
    
    public void test() {
        System.out.println(shoppingCart1 == shoppingCart2);  // false
    }
}

// Prototype in Web Application
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ShoppingCart shoppingCart;  // New instance per request
    
    @PostMapping("/add")
    public ResponseEntity<Void> addItem(@RequestBody Item item) {
        shoppingCart.addItem(item);
        return ResponseEntity.ok().build();
    }
    // Mỗi request → new ShoppingCart instance
}
```

#### 🌐 Request Scope (Web-specific)
```java
// 3️⃣ REQUEST SCOPE
// - 1 instance per HTTP request
// - Scope kết thúc khi response gửi về client
// - Dùng: Request-scoped data

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {
    private String requestId;
    private String userId;
    private Map<String, String> requestData = new HashMap<>();
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setUserData(String userId, String email) {
        this.userId = userId;
        requestData.put("email", email);
    }
}

@Service
public class ReportService {
    @Autowired
    private RequestContext requestContext;  // Injected request-scoped bean
    
    public void generateReport() {
        String requestId = requestContext.getRequestId();
        String userId = requestContext.getUserId();
        // ...
    }
}

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private RequestContext requestContext;
    
    @GetMapping
    public ResponseEntity<Report> getReport(HttpServletRequest httpRequest) {
        // Set request context data
        requestContext.setRequestId(UUID.randomUUID().toString());
        requestContext.setUserData("user123", "user@example.com");
        
        Report report = reportService.generateReport();
        return ResponseEntity.ok(report);
        // ← Request ends, RequestContext instance destroyed
    }
}
```

#### 💾 Session Scope (Web-specific)
```java
// 4️⃣ SESSION SCOPE
// - 1 instance per HTTP session (per user)
// - Scope kết thúc khi session expire hoặc user logout
// - Dùng: User-specific data trong session

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    private String userId;
    private String userName;
    private List<String> roles = new ArrayList<>();
    private LocalDateTime loginTime;
    
    public void login(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.loginTime = LocalDateTime.now();
    }
    
    public boolean isAuthenticated() {
        return userId != null;
    }
    
    public void logout() {
        userId = null;
        userName = null;
        roles.clear();
    }
}

@Service
public class UserPreferenceService {
    @Autowired
    private UserSession userSession;
    
    public void savePreference(String key, String value) {
        if (!userSession.isAuthenticated()) {
            throw new UnauthorizedException("User not logged in");
        }
        // Lưu preference cho user session này
        preferenceRepository.save(userSession.getUserId(), key, value);
    }
}

// User flow:
User A login      → UserSession instance A created
User B login      → UserSession instance B created
User A request 1  → Use instance A
User A request 2  → Use instance A (same session)
User B request    → Use instance B
User A logout     → Instance A destroyed
```

#### 🌍 Application Scope
```java
// 5️⃣ APPLICATION SCOPE (rare, mostly deprecated)
// - 1 instance per ServletContext (application-wide)
// - Lifetime: application start to shutdown
// - ⚠️ Cẩn thận thread-safety!

@Component
@Scope(value = "application")
public class ApplicationSettings {
    private String appVersion;
    private int maxConnections;
    private Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());
    
    public void addActiveUser(String userId) {
        activeUsers.add(userId);
    }
    
    public int getActiveUserCount() {
        return activeUsers.size();
    }
}
```

### 4.3 Scope Comparison Table

```
┌────────────┬──────────────┬─────────────────┬──────────────┬────────────────┐
│   Scope    │   Lifetime   │  # Instances    │  Thread-safe │  Use Cases     │
├────────────┼──────────────┼─────────────────┼──────────────┼────────────────┤
│ singleton  │ App start    │ 1 per container │ ✅ Must be   │ Services,      │
│            │ to shutdown  │                 │              │ Repositories   │
├────────────┼──────────────┼─────────────────┼──────────────┼────────────────┤
│ prototype  │ Until GC     │ New per request │ ⚠️ Each      │ Shopping cart, │
│            │              │                 │ instance     │ DTOs, Forms    │
├────────────┼──────────────┼─────────────────┼──────────────┼────────────────┤
│ request    │ Per HTTP req │ 1 per request   │ ✅ Per req   │ Request-bound  │
│            │              │                 │              │ data, logging  │
├────────────┼──────────────┼─────────────────┼──────────────┼────────────────┤
│ session    │ Per user ses │ 1 per session   │ ✅ Per user  │ User prefs,    │
│            │              │                 │              │ login info     │
├────────────┼──────────────┼─────────────────┼──────────────┼────────────────┤
│application │ App start    │ 1 per app       │ ⚠️ Global    │ App settings   │
│            │ to shutdown  │                 │ state        │ (rarely used)  │
└────────────┴──────────────┴─────────────────┴──────────────┴────────────────┘
```

---

## 5. Spring MVC Architecture - Chi Tiết Luồng Xử Lý

### 5.1 Kiến Trúc MVC

**MVC (Model-View-Controller)** pattern tách ứng dụng thành 3 phần:

```
┌─────────────────────────────────────────────────────────────────┐
│                     HTTP Request                                │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  1️⃣  DispatcherServlet (Front Controller)                       │
│     - Entry point cho tất cả HTTP requests                      │
│     - Một DispatcherServlet duy nhất cho mỗi webapp             │
│     - Quản lý request lifecycle                                 │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  2️⃣  HandlerMapping (URL → Controller mapping)                  │
│     - Tìm Controller phù hợp dựa trên URL pattern              │
│     - Ví dụ: /users/123 → UserController.getUser(123)          │
│     - RequestMappingHandlerMapping (dùng @RequestMapping)       │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  3️⃣  Handler Adapter (Invoke Controller)                        │
│     - Gọi method trong Controller                               │
│     - Xử lý parameter binding (@PathVariable, @RequestParam)   │
│     - Return ModelAndView hoặc object                           │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  4️⃣  Controller (Business Logic Layer)                          │
│     - @RequestMapping/@GetMapping/@PostMapping                  │
│     - Xử lý request, gọi service layer                          │
│     - Trả về Model + View name (hoặc ResponseEntity)            │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  5️⃣  ViewResolver (View name → View instance)                   │
│     - Resolve view name thành View object                       │
│     - InternalResourceViewResolver: "user/detail" → /WEB-INF/   │
│       views/user/detail.jsp                                     │
│     - ThymeleafViewResolver: "user/detail" → template file      │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  6️⃣  View (Rendering HTML)                                      │
│     - JSP, Thymeleaf, FreeMarker, etc.                          │
│     - Nhận Model data, render HTML                              │
│     - Return HTML content                                       │
└──────────────────────────┬──────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│  7️⃣  HTTP Response (HTML page)                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 Chi Tiết Flow Xử Lý Request

```java
// ============= STEP 1: DispatcherServlet =============
// web.xml hoặc WebApplicationInitializer
@Configuration
public class ServletConfig implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // DispatcherServlet mapping
        ServletRegistration.Dynamic dispatcher = 
            servletContext.addServlet("dispatcher", new DispatcherServlet(
                createApplicationContext()));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");  // Handle all requests
    }
}

// ============= STEP 2: HandlerMapping =============
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public HandlerMapping requestMappingHandlerMapping() {
        // Spring tự động scan @RequestMapping/@GetMapping/@PostMapping
        // Tạo mapping: /api/users/{id} → UserController.getUser()
        return new RequestMappingHandlerMapping();
    }
}

// ============= STEP 3: Controller Handler =============
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // GET /api/users/123
    // DispatcherServlet gọi method này
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        // 1. Nhận parameter: id = 123
        // 2. Gọi service layer
        User user = userService.findById(id);
        
        // 3. Kiểm tra user tồn tại
        if (user == null) {
            return ResponseEntity.notFound().build();  // 404
        }
        
        // 4. Convert entity → DTO
        UserDto userDto = new UserDto(user);
        
        // 5. Return ResponseEntity (JSON)
        return ResponseEntity.ok(userDto);  // 200 + JSON body
    }
}

// ============= STEP 4: MVC Controller (Server-side rendering) =============
@Controller  // ← Trả về HTML page, không JSON
@RequestMapping("/users")
public class UserMvcController {
    
    @Autowired
    private UserService userService;
    
    // GET /users/list
    @GetMapping("/list")
    public String listUsers(Model model, 
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {
        // 1. Fetch data từ database
        Page<User> users = userService.findAll(PageRequest.of(page - 1, size));
        
        // 2. Add to Model (data để render trong view)
        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", page);
        
        // 3. Return view name
        // ViewResolver sẽ resolve: "users/list" → /WEB-INF/views/users/list.jsp
        return "users/list";
    }
    
    // GET /users/{id}
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, 
                         Model model) {
        // 1. Fetch single user
        User user = userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // 2. Add to Model
        model.addAttribute("user", user);
        model.addAttribute("createdDate", 
            user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 3. Return view name
        // ViewResolver: "user/detail" → /WEB-INF/views/user/detail.jsp
        return "user/detail";
    }
}

// ============= STEP 5: View (JSP/Thymeleaf) =============
/*
File: /WEB-INF/views/user/detail.jsp

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<html>
<head>
    <title>${user.name} - User Profile</title>
</head>
<body>
    <h1>User Profile</h1>
    
    <div class="user-info">
        <p><strong>Name:</strong> ${user.name}</p>
        <p><strong>Email:</strong> ${user.email}</p>
        <p><strong>Created:</strong> ${createdDate}</p>
    </div>
    
    <c:if test="${user.role == 'ADMIN'}">
        <p class="admin-badge">Administrator</p>
    </c:if>
</body>
</html>
*/

// ============= STEP 6: ViewResolver Configuration =============
@Configuration
public class ViewResolverConfig implements WebMvcConfigurer {
    
    @Bean
    public ViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        
        // View name: "user/detail"
        // Resolved to: /WEB-INF/views/user/detail.jsp
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        
        return resolver;
    }
    
    // Thymeleaf view resolver
    @Bean
    public ViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
}
```

### 5.3 Controller Methods - Chi Tiết

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // ========== PARAMETER BINDING METHODS ==========
    
    // 1️⃣ @PathVariable - Extract từ URL path
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable Long id) {  // /api/v1/products/123 → id = 123
        Product product = productService.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(ProductDto.fromEntity(product));
    }
    
    // 2️⃣ @RequestParam - Query string parameters
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @RequestParam(required = false) String name,          // ?name=iPhone
            @RequestParam(defaultValue = "0") int minPrice,       // ?minPrice=100
            @RequestParam(defaultValue = "999999") int maxPrice,  // ?maxPrice=1000
            @RequestParam(defaultValue = "0") int page) {         // ?page=1
        
        List<Product> products = productService.search(
            name, minPrice, maxPrice, PageRequest.of(page, 10));
        return ResponseEntity.ok(
            products.stream().map(ProductDto::fromEntity).collect(Collectors.toList())
        );
    }
    
    // 3️⃣ @RequestBody - Parse JSON request body
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request) {  // JSON body
        
        // @Valid validates theo @NotNull, @Size, etc. annotations
        Product product = productService.create(
            request.getName(),
            request.getPrice(),
            request.getStock()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductDto.fromEntity(product));
    }
    
    // 4️⃣ @ModelAttribute - Bind form data (application/x-www-form-urlencoded)
    @PostMapping("/form")
    public String createProductForm(
            @Valid @ModelAttribute ProductFormDto form,
            BindingResult bindingResult) {  // Validation errors
        
        if (bindingResult.hasErrors()) {
            // Return form page với errors
            return "product/form";
        }
        
        productService.create(form);
        return "redirect:/products";
    }
    
    // 5️⃣ @RequestHeader - Extract HTTP headers
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @RequestHeader(required = false) String "User-Agent" userAgent) {
        
        byte[] fileBytes = productService.downloadAsFile(id);
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"product.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(fileBytes);
    }
    
    // 6️⃣ HttpServletRequest - Direct access to request object
    @GetMapping("/client-info")
    public ResponseEntity<Map<String, String>> getClientInfo(HttpServletRequest request) {
        Map<String, String> info = new HashMap<>();
        info.put("remoteAddr", request.getRemoteAddr());
        info.put("userAgent", request.getHeader("User-Agent"));
        info.put("method", request.getMethod());
        info.put("contentType", request.getContentType());
        return ResponseEntity.ok(info);
    }
    
    // ========== RESPONSE HANDLING METHODS ==========
    
    // 7️⃣ ResponseEntity - Full control over response
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        Product updated = productService.update(id, request);
        return ResponseEntity
            .ok()
            .header("X-Custom-Header", "Updated")
            .body(ProductDto.fromEntity(updated));
    }
    
    // 8️⃣ @ResponseStatus - Set HTTP status code
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // 204
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        // No body returned
    }
    
    // 9️⃣ Returning Page/Paginated Data
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Product> products = productService.findAll(pageable);
        
        return ResponseEntity.ok(
            products.map(ProductDto::fromEntity)
        );
    }
    
    // 🔟 Custom Object Return (Spring chuyển thành JSON)
    @GetMapping("/stats")
    public ProductStats getStats() {
        // Spring tự convert ProductStats object thành JSON
        return new ProductStats(
            productService.getTotalCount(),
            productService.getAveragePrice(),
            productService.getMostPopular()
        );
    }
}

// DTO Classes
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    // Getters & Setters
}

public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    
    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.price = product.getPrice();
        dto.stock = product.getStock();
        dto.createdAt = product.getCreatedAt();
        return dto;
    }
}
```

---

## 6. Spring Boot - Auto-Configuration & Convention

### 6.1 Spring Boot Overview

**Spring Boot** là một project trong Spring Ecosystem giúp tạo production-ready standalone applications nhanh chóng.

**Khác biệt giữa Spring Framework và Spring Boot:**

```
┌──────────────────────────┬──────────────────────────┐
│   Spring Framework       │   Spring Boot            │
├──────────────────────────┼──────────────────────────┤
│ - Cần XML config nhiều   │ - Auto-configuration     │
│ - Cần setup server       │ - Embedded server        │
│ - WAR deployment         │ - JAR deployment         │
│ - Manual dependency mgmt │ - Starter dependencies   │
│ - Low-level config       │ - Convention-based setup │
└──────────────────────────┴──────────────────────────┘

Spring Boot = Spring Framework + Starters + Auto-Configuration + Embedded Server
```

### 6.2 Auto-Configuration Mechanism

```java
// Spring Boot Auto-Configuration Flow:

// 1️⃣ Main Application Class
@SpringBootApplication  // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// 2️⃣ @EnableAutoConfiguration (implicit via @SpringBootApplication)
// Scans JAR files cho META-INF/spring.factories
// File spring.factories chứa list auto-configuration classes

// META-INF/spring.factories (trong spring-boot-autoconfigure.jar):
/*
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
  ...
*/

// 3️⃣ Auto-Configuration Classes (Conditional)
@Configuration
@ConditionalOnClass(DataSource.class)  // Chỉ nếu DataSource class trong classpath
@ConditionalOnProperty(                // Chỉ nếu property được set
    prefix = "spring.datasource", 
    name = "url", 
    matchIfMissing = false
)
public class DataSourceAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean  // Chỉ nếu chưa có DataSource bean
    public DataSource dataSource() {
        // Auto-configure DataSource dựa trên application.properties
        return new HikariDataSource(createHikariConfig());
    }
    
    private HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("spring.datasource.url"));
        config.setUsername(env.getProperty("spring.datasource.username"));
        config.setPassword(env.getProperty("spring.datasource.password"));
        return config;
    }
}

// 4️⃣ ApplicationContext Load Order
ApplicationContext Initialization
    ↓
@ComponentScan (scan @Component/@Service/@Controller)
    ↓
@EnableAutoConfiguration
    ↓
Auto-Configuration Conditions Evaluate
    ├─ @ConditionalOnClass - classpath check
    ├─ @ConditionalOnProperty - property check
    ├─ @ConditionalOnMissingBean - existing beans check
    └─ @ConditionalOnWebApplication - web app check
    ↓
Beans Instantiation & Injection
    ↓
Application Ready
```

### 6.3 Starter Dependencies

**Starter** là một tập hợp dependencies được pre-configured. Thay vì khai báo 10+ dependencies riêng lẻ, chỉ cần 1 starter.

```xml
<!-- ❌ WITHOUT STARTER - Phải khai báo tất cả dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>6.0.0</version>
</dependency>
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-core</artifactId>
    <version>10.0.0</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.14.0</version>
</dependency>
<!-- ... and more -->

<!-- ✅ WITH STARTER - 1 dependency only -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- Automatically includes:
         - spring-webmvc
         - spring-boot-starter-tomcat
         - spring-boot-starter-json
         - spring-boot-starter-validation
         - etc.
    -->
</dependency>
```

**Common Starters:**

```xml
<!-- Web applications (REST APIs, MVC) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Database with JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Caching -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Message Queue (RabbitMQ, Kafka) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
</dependency>
```

### 6.4 Application Properties Configuration

```properties
# ============================================
# DATABASE CONFIGURATION
# ============================================

# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Connection Pool (HikariCP - default)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000

# ============================================
# JPA / HIBERNATE CONFIGURATION
# ============================================

# DDL Auto: validate | update | create | create-drop
# - validate: chỉ validate schema, không thay đổi
# - update: update schema nếu entity thay đổi
# - create: tạo mới schema mỗi lần start (DROP nếu tồn tại)
# - create-drop: tạo on startup, DROP on shutdown
spring.jpa.hibernate.ddl-auto=update

# Show SQL queries
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# SQL dialect (depends on database)
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Batch settings for better performance
spring.jpa.properties.hibernate.jdbc.batch_size=10
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# ============================================
# SERVER CONFIGURATION
# ============================================

# Server Port
server.port=8080

# Context Path (base URL)
server.servlet.context-path=/api

# Server Compression
server.compression.enabled=true
server.compression.min-response-size=1024

# ============================================
# LOGGING CONFIGURATION
# ============================================

# Root logger level
logging.level.root=INFO

# Specific package logging
logging.level.com.myapp=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Log file
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=10
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# ============================================
# APPLICATION CONFIGURATION
# ============================================

# Application name & version
spring.application.name=my-rest-api
app.version=1.0.0
app.title=My REST API Application

# Custom properties (accessible via @Value)
app.jwt.secret=my-secret-key-must-be-at-least-32-characters-long
app.jwt.expiration=86400000
app.upload.dir=/tmp/uploads
app.mail.from=noreply@myapp.com

# ============================================
# ACTUATOR CONFIGURATION
# ============================================

# Enable actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always

# ============================================
# CACHE CONFIGURATION
# ============================================

spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=600s

# ============================================
# PROFILES
# ============================================

# Active profile (dev, staging, prod)
# Set via: java -Dspring.profiles.active=prod -jar app.jar
spring.profiles.active=dev
```

### 6.5 Profile-Specific Properties

```
Project Structure:
src/main/resources/
  ├── application.properties           (shared config)
  ├── application-dev.properties       (dev profile)
  ├── application-staging.properties   (staging profile)
  └── application-prod.properties      (prod profile)
```

```properties
# application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb_dev
spring.datasource.username=root
spring.datasource.password=root123
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.root=DEBUG
app.mail.enabled=false  # Don't send real emails in dev

# application-prod.properties
spring.datasource.url=jdbc:mysql://prod-db-server:3306/mydb_prod
spring.datasource.username=${DB_USERNAME}  # Environment variables
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate  # Validate only
logging.level.root=WARN
app.mail.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
```

---

## 7. REST API với Spring Boot - Chi Tiết Design & Implementation

### 7.1 REST Principles

**REST (Representational State Transfer)** là architectural style cho designing networked applications.

```
REST Constraints:
1. Client-Server Architecture   - Separation of concerns
2. Statelessness               - Server không lưu client context
3. Uniform Interface           - Consistent API design
4. Resource-Based URLs         - /api/users/123 (resource), not /api/getUser?id=123
5. HTTP Methods               - GET (retrieve), POST (create), PUT (update), DELETE (remove)
6. JSON/XML Representation    - Standard data format
7. Cache Control              - Leverage HTTP caching
8. Code on Demand (Optional)  - Server có thể return executable code
```

### 7.2 REST Endpoint Design

```java
// ❌ BAD REST Design (RPC-style)
@RestController
public class OrderController {
    @GetMapping("/getOrder")
    public Order getOrder(@RequestParam Long id) { /* ... */ }
    
    @PostMapping("/createOrder")
    public Order createOrder(@RequestBody OrderRequest req) { /* ... */ }
    
    @PostMapping("/deleteOrder")
    public void deleteOrder(@RequestParam Long id) { /* ... */ }
    
    @PostMapping("/updateOrder")
    public Order updateOrder(@RequestBody Order order) { /* ... */ }
}

// ✅ GOOD REST Design (Resource-oriented)
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    // GET /api/v1/orders/123 - Retrieve single resource
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) { /* ... */ }
    
    // GET /api/v1/orders - List all resources
    @GetMapping
    public ResponseEntity<Page<OrderDto>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) { /* ... */ }
    
    // POST /api/v1/orders - Create new resource
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest req) { /* ... */ }
    
    // PUT /api/v1/orders/123 - Replace entire resource
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest req) { /* ... */ }
    
    // PATCH /api/v1/orders/123 - Partial update
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) { /* ... */ }
    
    // DELETE /api/v1/orders/123 - Delete resource
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) { /* ... */ }
}

// HTTP Response Status Codes:
/*
2xx Success:
  - 200 OK: Request succeeded
  - 201 Created: Resource created successfully
  - 204 No Content: Success but no content to return (DELETE, PATCH)
  - 206 Partial Content: Partial response (pagination)

3xx Redirection:
  - 301 Moved Permanently: Resource moved
  - 307 Temporary Redirect: Temporary redirection

4xx Client Error:
  - 400 Bad Request: Invalid input
  - 401 Unauthorized: Authentication required
  - 403 Forbidden: Authenticated but not authorized
  - 404 Not Found: Resource not found
  - 409 Conflict: Resource conflict (unique constraint)
  - 422 Unprocessable Entity: Validation failed

5xx Server Error:
  - 500 Internal Server Error: Unexpected server error
  - 503 Service Unavailable: Server temporarily unavailable
*/
```

### 7.3 Complete REST Controller Implementation

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductRestController.class);
    
    @Autowired
    private ProductService productService;
    
    // ========== READ OPERATIONS ==========
    
    // 1️⃣ GET all products with pagination and filtering
    /**
     * GET /api/v1/products?page=0&size=20&sortBy=name&category=electronics&minPrice=100
     * 
     * @param page       - Page number (0-indexed)
     * @param size       - Page size (items per page)
     * @param sortBy     - Sort field (default: id)
     * @param direction  - Sort direction (ASC, DESC)
     * @param category   - Filter by category
     * @param minPrice   - Filter minimum price
     * @param maxPrice   - Filter maximum price
     * @return Paginated list of products
     */
    @GetMapping
    @PreAuthorize("permitAll()")  // No authentication required
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<Product> products = productService.search(
                category, minPrice, maxPrice, pageable);
            
            return ResponseEntity.ok(
                products.map(ProductDto::fromEntity)
            );
        } catch (Exception e) {
            logger.error("Error fetching products", e);
            throw new RuntimeException("Failed to fetch products");
        }
    }
    
    // 2️⃣ GET single product by ID
    /**
     * GET /api/v1/products/123
     * 
     * @param id - Product ID
     * @return Single product or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        logger.debug("Fetching product with ID: {}", id);
        
        return productService.findById(id)
            .map(product -> {
                logger.info("Product found: {}", product.getId());
                return ResponseEntity.ok(ProductDto.fromEntity(product));
            })
            .orElseThrow(() -> {
                logger.warn("Product not found with ID: {}", id);
                return new ProductNotFoundException("Product not found with id: " + id);
            });
    }
    
    // 3️⃣ GET products by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = productService.findByCategory(categoryId, pageable).getContent();
        
        return ResponseEntity.ok(
            products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList())
        );
    }
    
    // ========== CREATE OPERATIONS ==========
    
    // 4️⃣ POST - Create new product
    /**
     * POST /api/v1/products
     * Request Body: { "name": "iPhone", "price": 999.99, "stock": 50 }
     * 
     * @param request - Product creation request
     * @return Created product with 201 status
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can create
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        
        logger.info("Creating new product: {}", request.getName());
        
        try {
            Product product = productService.create(
                request.getName(),
                request.getPrice(),
                request.getStock(),
                request.getDescription()
            );
            
            logger.info("Product created successfully with ID: {}", product.getId());
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/api/v1/products/" + product.getId())
                .body(ProductDto.fromEntity(product));
                
        } catch (InvalidProductException e) {
            logger.error("Invalid product data: {}", e.getMessage());
            throw e;
        }
    }
    
    // ========== UPDATE OPERATIONS ==========
    
    // 5️⃣ PUT - Complete update of product
    /**
     * PUT /api/v1/products/123
     * Replaces entire product resource
     * 
     * @param id - Product ID
     * @param request - Complete product update
     * @return Updated product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        logger.info("Updating product with ID: {}", id);
        
        Product updated = productService.update(id, request)
            .orElseThrow(() -> new ProductNotFoundException(
                "Cannot update. Product not found with id: " + id));
        
        logger.info("Product updated successfully");
        
        return ResponseEntity.ok(ProductDto.fromEntity(updated));
    }
    
    // 6️⃣ PATCH - Partial update of product
    /**
     * PATCH /api/v1/products/123
     * Partially updates product (only provided fields)
     * 
     * @param id - Product ID
     * @param updates - Fields to update
     * @return Updated product
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        
        Product updated = productService.partialUpdate(id, updates)
            .orElseThrow(() -> new ProductNotFoundException(
                "Product not found with id: " + id));
        
        return ResponseEntity.ok(ProductDto.fromEntity(updated));
    }
    
    // ========== DELETE OPERATIONS ==========
    
    // 7️⃣ DELETE - Remove product
    /**
     * DELETE /api/v1/products/123
     * 
     * @param id - Product ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        
        boolean deleted = productService.delete(id);
        
        if (!deleted) {
            logger.warn("Product not found for deletion: {}", id);
            throw new ProductNotFoundException(
                "Cannot delete. Product not found with id: " + id);
        }
        
        logger.info("Product deleted successfully");
    }
    
    // ========== BULK OPERATIONS ==========
    
    // 8️⃣ DELETE multiple products
    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMultiple(
            @RequestBody List<Long> ids) {
        
        int deletedCount = productService.deleteMultiple(ids);
        
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", deletedCount);
        response.put("total", ids.size());
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}

// ========== REQUEST/RESPONSE DTOs ==========

public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    @Size(max = 500, message = "Description too long")
    private String description;
    
    // Getters & Setters
}

public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.price = product.getPrice();
        dto.stock = product.getStock();
        dto.description = product.getDescription();
        dto.createdAt = product.getCreatedAt();
        dto.updatedAt = product.getUpdatedAt();
        return dto;
    }
}
```

### 7.4 Global Exception Handler

```java
/**
 * Centralized exception handling for all REST endpoints
 * Returns consistent error response format
 */
@RestControllerAdvice  // = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // ========== VALIDATION EXCEPTIONS ==========
    
    // Handle @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> errors = new HashMap<>();
        
        fieldErrors.forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("Request validation failed")
            .details(errors)
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
    
    // Handle JSON parsing errors
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJsonParseException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        logger.error("Invalid JSON format", ex);
        
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("INVALID_JSON")
            .message("Invalid JSON format in request body")
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
    
    // ========== BUSINESS EXCEPTIONS ==========
    
    // Handle custom ResourceNotFound exception
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request) {
        
        logger.warn("Product not found: {}", ex.getMessage());
        
        return ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
    
    // Handle business logic violations
    @ExceptionHandler(InvalidProductException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleInvalidProduct(
            InvalidProductException ex,
            HttpServletRequest request) {
        
        return ErrorResponse.builder()
            .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .error("INVALID_PRODUCT")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
    
    // ========== SECURITY EXCEPTIONS ==========
    
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        return ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("FORBIDDEN")
            .message("You don't have permission to access this resource")
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
    
    // ========== GENERIC EXCEPTIONS ==========
    
    // Catch-all for unexpected exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        return ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_ERROR")
            .message("An unexpected error occurred. Please try again later.")
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
}

// ========== ERROR RESPONSE MODEL ==========

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;
    private LocalDateTime timestamp;
    private String path;
    
    // Example response:
    // {
    //   "status": 400,
    //   "error": "VALIDATION_ERROR",
    //   "message": "Request validation failed",
    //   "details": {
    //     "name": "Name must be 2-100 characters",
    //     "price": "Price must be positive"
    //   },
    //   "timestamp": "2024-01-10T14:30:00",
    //   "path": "/api/v1/products"
    // }
}

// ========== CUSTOM EXCEPTIONS ==========

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}

public class InvalidProductException extends RuntimeException {
    public InvalidProductException(String message) {
        super(message);
    }
}
```

---

## 8. Spring Validation - Hibernate Validator

### 8.1 Built-in Validation Annotations

```java
public class CreateProductRequest {
    
    // ✅ String Validators
    @NotNull(message = "Product name cannot be null")
    @NotBlank(message = "Product name is required")  // null, "", whitespace check
    @NotEmpty(message = "Product name cannot be empty")  // null, "" check
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    @Length(min = 2, max = 100, message = "Name must be 2-100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Name can only contain letters, numbers, and spaces")
    private String name;
    
    // ✅ Numeric Validators
    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    @Positive(message = "Price must be positive")  // > 0
    @PositiveOrZero(message = "Price must be >= 0")  // >= 0
    @Negative(message = "Value must be negative")  // < 0
    @NegativeOrZero(message = "Value must be <= 0")  // <= 0
    @Digits(integer = 5, fraction = 2, message = "Price must have max 5 integer and 2 decimal digits")
    @Min(value = 1, message = "Minimum stock is 1")
    @Max(value = 99999, message = "Maximum stock is 99999")
    private BigDecimal price;
    
    // ✅ Collection Validators
    @NotNull
    @NotEmpty(message = "At least one category is required")
    @Size(min = 1, max = 5, message = "Product can have 1-5 categories")
    private List<String> categories;
    
    // ✅ Temporal Validators
    @NotNull(message = "Release date is required")
    @FutureOrPresent(message = "Release date cannot be in the past")
    @Future(message = "Release date must be in the future")
    @Past(message = "Published date must be in the past")
    @PastOrPresent
    private LocalDate releaseDate;
    
    // ✅ Email & URL Validators
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @URL(message = "Invalid URL format")
    private String documentationUrl;
    
    // ✅ Custom Validators
    @ValidProductCode(message = "Invalid product code format")
    private String productCode;
    
    private String description;
}

// ========== CUSTOM VALIDATORS ==========

// 1️⃣ Define Custom Annotation
@Documented
@Constraint(validatedBy = ProductCodeValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProductCode {
    String message() default "Invalid product code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2️⃣ Implement Validator
public class ProductCodeValidator implements ConstraintValidator<ValidProductCode, String> {
    
    @Override
    public void initialize(ValidProductCode annotation) {
        // Initialize validator
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // @NotNull handles null check
        }
        
        // Product code format: PROD-XXXXXX (6 digits)
        return value.matches("^PROD-\\d{6}$");
    }
}

// ========== VALIDATION IN CONTROLLER ==========

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request) {  // @Valid triggers validation
        // If validation fails, MethodArgumentNotValidException is thrown
        // GlobalExceptionHandler catches and returns 400 Bad Request
        
        Product product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductDto.fromEntity(product));
    }
    
    // ========== ACCESSING VALIDATION ERRORS ==========
    
    @PostMapping("/with-errors")
    public ResponseEntity<?> createProductWithErrors(
            @Valid @RequestBody CreateProductRequest request,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            
            // Collect all field errors
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            
            // Collect global errors (cross-field validation)
            bindingResult.getGlobalErrors().forEach(error ->
                errors.put(error.getObjectName(), error.getDefaultMessage())
            );
            
            return ResponseEntity.badRequest().body(errors);
        }
        
        // Validation passed, create product
        Product product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductDto.fromEntity(product));
    }
}

// ========== PROGRAMMATIC VALIDATION ==========

@Service
public class ProductService {
    
    @Autowired
    private Validator validator;
    
    public void validateProduct(CreateProductRequest request) {
        Set<ConstraintViolation<CreateProductRequest>> violations = 
            validator.validate(request);
        
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            violations.forEach(violation ->
                sb.append(violation.getPropertyPath())
                  .append(": ")
                  .append(violation.getMessage())
                  .append("; ")
            );
            throw new InvalidProductException(sb.toString());
        }
    }
}
```

---

*📌 Tiếp theo: Advanced Topics - AOP, Transactions, Caching, Security*
