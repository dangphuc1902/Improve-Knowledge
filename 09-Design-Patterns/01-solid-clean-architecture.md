# 🏛️ SOLID Principles & Clean Architecture

> **Phase:** 1-2 | **Quan trọng cho:** ALL — đánh giá tư duy thiết kế

---

## PART 1: SOLID Principles

### S — Single Responsibility Principle
> Mỗi class chỉ có **một lý do để thay đổi**.

```java
// ❌ BAD: UserService làm quá nhiều việc
class UserService {
    public void registerUser(User user) { /* DB logic */ }
    public void sendWelcomeEmail(User user) { /* Email logic */ }
    public String generateReport(List<User> users) { /* Report logic */ }
    public boolean validateUser(User user) { /* Validation logic */ }
}

// ✅ GOOD: Tách responsibilities
class UserService {
    private final UserRepository repository;
    private final UserValidator validator;
    
    public User register(CreateUserRequest request) {
        validator.validate(request);
        return repository.save(User.from(request));
    }
}

class EmailService {
    public void sendWelcome(User user) { /* Email logic */ }
}

class UserReportGenerator {
    public String generate(List<User> users) { /* Report logic */ }
}
```

### O — Open/Closed Principle
> Mở cho extension, đóng cho modification.

```java
// ❌ BAD: Thêm payment method = sửa code hiện tại
class PaymentProcessor {
    public void process(Payment payment) {
        if (payment.getType().equals("CREDIT_CARD")) { /* ... */ }
        else if (payment.getType().equals("MOMO")) { /* ... */ }
        else if (payment.getType().equals("VNPAY")) { /* ... */ }  // Phải sửa class!
    }
}

// ✅ GOOD: Thêm payment method = thêm class mới
interface PaymentStrategy {
    boolean supports(String type);
    PaymentResult process(Payment payment);
}

@Component class CreditCardPayment implements PaymentStrategy { /* ... */ }
@Component class MoMoPayment implements PaymentStrategy { /* ... */ }
@Component class VNPayPayment implements PaymentStrategy { /* ... */ }  // Chỉ thêm, không sửa!

@Service
class PaymentProcessor {
    private final List<PaymentStrategy> strategies;
    
    public PaymentResult process(Payment payment) {
        return strategies.stream()
            .filter(s -> s.supports(payment.getType()))
            .findFirst()
            .orElseThrow(() -> new UnsupportedPaymentException(payment.getType()))
            .process(payment);
    }
}
```

### L — Liskov Substitution Principle
> Subclass phải thay thế được superclass mà không làm hỏng program.

```java
// ❌ BAD: Square "is-a" Rectangle nhưng behavior khác
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) { this.width = this.height = w; }  // Breaks expectation!
    @Override
    public void setHeight(int h) { this.width = this.height = h; }
}

// ✅ GOOD: Use composition or separate hierarchy
interface Shape {
    double area();
}
record Rectangle(double width, double height) implements Shape {
    public double area() { return width * height; }
}
record Square(double side) implements Shape {
    public double area() { return side * side; }
}
```

### I — Interface Segregation Principle
> Client không nên bị buộc implement methods nó không dùng.

```java
// ❌ BAD: Fat interface
interface UserRepository {
    User findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(Long id);
    List<User> searchByName(String name);
    UserReport generateReport();
    void sendNotification(Long userId);
}

// ✅ GOOD: Segregated interfaces
interface ReadableRepository<T> {
    Optional<T> findById(Long id);
    List<T> findAll();
}

interface WritableRepository<T> {
    T save(T entity);
    void delete(Long id);
}

interface UserSearchRepository {
    List<User> searchByName(String name);
}

interface UserRepository extends ReadableRepository<User>, WritableRepository<User> {}
```

### D — Dependency Inversion Principle
> High-level modules không phụ thuộc low-level modules. Cả 2 phụ thuộc abstractions.

```java
// ❌ BAD: Service phụ thuộc trực tiếp implementation
class OrderService {
    private MySQLOrderRepository repository = new MySQLOrderRepository(); // Tight coupling!
    private SmtpEmailSender emailSender = new SmtpEmailSender();
}

// ✅ GOOD: Phụ thuộc abstraction (interface)
@Service
class OrderService {
    private final OrderRepository repository;     // Interface
    private final NotificationSender sender;       // Interface
    
    public OrderService(OrderRepository repo, NotificationSender sender) {
        this.repository = repo;     // Spring inject implementation
        this.sender = sender;
    }
}
```

---

## PART 2: Clean Architecture

### Hexagonal Architecture (Ports & Adapters)

```
                    ┌──────────────────────────────┐
                    │        DRIVING ADAPTERS       │
                    │  (Primary / Input)            │
                    │                               │
                    │  REST Controller              │
                    │  gRPC Handler                 │
                    │  Kafka Consumer               │
                    │  CLI Command                  │
                    └──────────┬───────────────────┘
                               │
                      ┌────────▼────────┐
                      │    INPUT PORTS   │ ← Interfaces
                      │  (Use Cases)     │
                      └────────┬────────┘
                               │
                    ┌──────────▼───────────────────┐
                    │      DOMAIN / CORE            │
                    │                               │
                    │  Entities (Business Objects)  │
                    │  Value Objects                │
                    │  Domain Services              │
                    │  Domain Events                │
                    │                               │
                    │  ❌ No framework dependency   │
                    │  ❌ No DB dependency           │
                    │  ❌ No external dependency     │
                    └──────────┬───────────────────┘
                               │
                      ┌────────▼────────┐
                      │   OUTPUT PORTS   │ ← Interfaces
                      │  (Repositories)  │
                      └────────┬────────┘
                               │
                    ┌──────────▼───────────────────┐
                    │       DRIVEN ADAPTERS         │
                    │  (Secondary / Output)         │
                    │                               │
                    │  JPA Repository Impl          │
                    │  Redis Cache Impl             │
                    │  Kafka Producer Impl          │
                    │  External API Client          │
                    └──────────────────────────────┘
```

### Package Structure
```
com.example.wallet/
├── domain/                    ← Core (NO framework deps)
│   ├── model/
│   │   ├── Wallet.java        ← Entity
│   │   ├── Money.java         ← Value Object
│   │   └── WalletId.java      ← Value Object
│   ├── port/
│   │   ├── in/
│   │   │   └── DeductBalanceUseCase.java   ← Input Port
│   │   └── out/
│   │       ├── LoadWalletPort.java         ← Output Port
│   │       └── SaveWalletPort.java         ← Output Port
│   └── service/
│       └── WalletService.java  ← Domain Service (implements Use Case)
│
├── adapter/                   ← Infrastructure (framework deps OK)
│   ├── in/
│   │   ├── rest/
│   │   │   └── WalletController.java      ← REST Adapter
│   │   └── grpc/
│   │       └── WalletGrpcHandler.java     ← gRPC Adapter
│   └── out/
│       ├── persistence/
│       │   ├── WalletJpaEntity.java       ← JPA Entity (separate from domain!)
│       │   ├── WalletJpaRepository.java   ← Spring Data JPA
│       │   └── WalletPersistenceAdapter.java  ← Implements Output Port
│       └── cache/
│           └── WalletCacheAdapter.java    ← Redis implementation
│
└── config/
    └── WalletBeanConfig.java  ← Wire everything together
```

### Key Rule: Dependency Direction
```
Adapters → Ports → Domain
     ↑                ↑
     │                │
Framework         Pure Java
Spring, JPA      No @Annotation
Kafka, Redis     No import spring.*
```

---

## Câu Hỏi Phỏng Vấn

### Q1: SOLID principles — cho ví dụ thực tế vi phạm từng principle?
**A:** (Dùng ví dụ code ở trên cho từng principle)

### Q2: Hexagonal architecture vs traditional layered architecture?
**A:** Layered (Controller → Service → Repository): simple nhưng domain phụ thuộc infrastructure. Hexagonal: domain ở center, không phụ thuộc gì → dễ test (mock ports), dễ swap infrastructure (change DB, change messaging). Trade-off: more code (adapters, mappers), nhưng maintainable long-term.

### Q3: Khi nào nên dùng Clean Architecture?
**A:** Complex business logic, long-lived projects, team lớn. Không cần cho: CRUD apps, prototypes, small services. Rule of thumb: nếu domain logic > infrastructure logic → Clean Architecture.

### Q4: DDD Aggregate là gì?
**A:** Cluster of domain objects (entities + value objects) treated as unit. 1 Aggregate Root controls access. Example: Order (root) → OrderItems → Shipping. Rules: reference other aggregates by ID only, 1 transaction = 1 aggregate, eventual consistency between aggregates.
