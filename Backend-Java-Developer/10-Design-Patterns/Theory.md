# 📖 Design Patterns - Lý Thuyết & Ví Dụ

> **Tuần 8 | GoF Design Patterns - 23 mẫu kinh điển**

---

## Tổng Quan Design Patterns

**Design Pattern** là giải pháp tái sử dụng cho vấn đề thiết kế phổ biến trong phần mềm.

```
GoF Patterns (Gang of Four)
├── Creational (5) - Tạo object
│     ├── Singleton
│     ├── Factory Method
│     ├── Abstract Factory
│     ├── Builder
│     └── Prototype
├── Structural (7) - Cấu trúc class/object
│     ├── Adapter
│     ├── Bridge
│     ├── Composite
│     ├── Decorator
│     ├── Facade
│     ├── Flyweight
│     └── Proxy
└── Behavioral (11) - Tương tác giữa objects
      ├── Observer
      ├── Strategy
      ├── Command
      ├── Iterator
      ├── Template Method
      ├── Chain of Responsibility
      ├── State
      ├── Visitor
      ├── Mediator
      ├── Memento
      └── Interpreter
```

---

## Creational Patterns

### 1. Singleton

**Mục đích:** Đảm bảo chỉ có 1 instance của class trong toàn bộ ứng dụng.

**Khi dùng:** Database connection pool, Logger, Config Manager, Thread Pool.

```java
// Thread-safe Singleton với Double-Checked Locking
public class Logger {
    private static volatile Logger instance;
    private final List<String> logs = new ArrayList<>();

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {  // double-check
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        synchronized (logs) {
            logs.add("[" + LocalDateTime.now() + "] " + message);
        }
    }
}

// Enum Singleton (BEST - thread-safe, serialization-safe)
public enum DatabasePool {
    INSTANCE;

    private final Connection connection;

    DatabasePool() {
        this.connection = createConnection();
    }

    public Connection getConnection() { return connection; }
}
```

---

### 2. Factory Method

**Mục đích:** Định nghĩa interface tạo object, nhưng để subclass quyết định class nào sẽ được tạo.

**Khi dùng:** Khi không biết trước class cụ thể nào cần tạo, khi muốn subclass kiểm soát việc tạo object.

```java
// Product interface
public interface Notification {
    void send(String message, String recipient);
}

// Concrete Products
public class EmailNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("Email to " + recipient + ": " + message);
    }
}

public class SMSNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("SMS to " + recipient + ": " + message);
    }
}

public class PushNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("Push to " + recipient + ": " + message);
    }
}

// Factory
public class NotificationFactory {
    public static Notification createNotification(String channel) {
        return switch (channel.toLowerCase()) {
            case "email" -> new EmailNotification();
            case "sms" -> new SMSNotification();
            case "push" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown channel: " + channel);
        };
    }
}

// Usage
Notification notification = NotificationFactory.createNotification("email");
notification.send("Welcome!", "user@example.com");
```

---

### 3. Builder

**Mục đích:** Xây dựng object phức tạp từng bước, tách quá trình construction khỏi representation.

**Khi dùng:** Object có nhiều optional parameters, construction cần nhiều bước.

```java
public class QueryBuilder {
    private String table;
    private List<String> columns = new ArrayList<>();
    private String whereClause;
    private String orderBy;
    private int limit = -1;
    private int offset = 0;

    private QueryBuilder() {}

    public static QueryBuilder from(String table) {
        QueryBuilder qb = new QueryBuilder();
        qb.table = table;
        return qb;
    }

    public QueryBuilder select(String... cols) {
        columns.addAll(Arrays.asList(cols));
        return this;  // Method chaining
    }

    public QueryBuilder where(String condition) {
        this.whereClause = condition;
        return this;
    }

    public QueryBuilder orderBy(String column) {
        this.orderBy = column;
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String build() {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(columns.isEmpty() ? "*" : String.join(", ", columns));
        sql.append(" FROM ").append(table);
        if (whereClause != null) sql.append(" WHERE ").append(whereClause);
        if (orderBy != null) sql.append(" ORDER BY ").append(orderBy);
        if (limit > 0) sql.append(" LIMIT ").append(limit);
        if (offset > 0) sql.append(" OFFSET ").append(offset);
        return sql.toString();
    }
}

// Usage
String query = QueryBuilder.from("users")
    .select("id", "name", "email")
    .where("age > 18")
    .orderBy("name")
    .limit(10)
    .offset(20)
    .build();
// SELECT id, name, email FROM users WHERE age > 18 ORDER BY name LIMIT 10 OFFSET 20
```

---

## Structural Patterns

### 4. Adapter

**Mục đích:** Chuyển đổi interface của class thành interface khác mà client mong đợi.

**Khi dùng:** Tích hợp legacy code, third-party libraries với incompatible interfaces.

```java
// Target interface (cái app muốn dùng)
public interface JsonDataProcessor {
    Map<String, Object> process(String jsonData);
}

// Adaptee (legacy XML processor)
public class XmlDataProcessor {
    public Document parseXml(String xmlData) { /* parse XML */ return null; }
    public Map<String, Object> convertToMap(Document doc) { return null; }
}

// Adapter
public class XmlToJsonAdapter implements JsonDataProcessor {
    private final XmlDataProcessor xmlProcessor;

    public XmlToJsonAdapter(XmlDataProcessor xmlProcessor) {
        this.xmlProcessor = xmlProcessor;
    }

    @Override
    public Map<String, Object> process(String data) {
        // Internally, convert to XML then process
        String xmlData = convertJsonToXml(data);
        Document doc = xmlProcessor.parseXml(xmlData);
        return xmlProcessor.convertToMap(doc);
    }

    private String convertJsonToXml(String json) { /* ... */ return null; }
}
```

---

### 5. Decorator

**Mục đích:** Thêm behavior cho object động, không ảnh hưởng objects khác.

**Khi dùng:** Thêm features mà không muốn subclass (open/closed principle).

```java
public interface TextFormatter {
    String format(String text);
}

public class PlainText implements TextFormatter {
    @Override
    public String format(String text) { return text; }
}

// Decorator base
public abstract class TextDecorator implements TextFormatter {
    protected final TextFormatter wrapped;
    public TextDecorator(TextFormatter wrapped) { this.wrapped = wrapped; }
}

public class BoldDecorator extends TextDecorator {
    public BoldDecorator(TextFormatter wrapped) { super(wrapped); }
    @Override
    public String format(String text) { return "<b>" + wrapped.format(text) + "</b>"; }
}

public class ItalicDecorator extends TextDecorator {
    public ItalicDecorator(TextFormatter wrapped) { super(wrapped); }
    @Override
    public String format(String text) { return "<i>" + wrapped.format(text) + "</i>"; }
}

public class UpperCaseDecorator extends TextDecorator {
    public UpperCaseDecorator(TextFormatter wrapped) { super(wrapped); }
    @Override
    public String format(String text) { return wrapped.format(text).toUpperCase(); }
}

// Usage - stack decorators
TextFormatter formatter = new BoldDecorator(
    new ItalicDecorator(
        new PlainText()
    )
);
System.out.println(formatter.format("Hello"));  // <b><i>Hello</i></b>
```

---

### 6. Facade

**Mục đích:** Cung cấp interface đơn giản cho subsystem phức tạp.

**Khi dùng:** Đơn giản hóa API cho client, giảm coupling với subsystem.

```java
// Complex subsystems
class UserValidator { public boolean validate(User user) { return true; } }
class EmailSender { public void sendWelcome(String email) { } }
class DatabaseRepository { public void save(User user) { } }
class AuditLogger { public void log(String action) { } }
class CacheManager { public void invalidate(String key) { } }

// Facade - hides complexity
public class UserRegistrationFacade {
    private final UserValidator validator;
    private final EmailSender emailSender;
    private final DatabaseRepository repository;
    private final AuditLogger logger;
    private final CacheManager cache;

    public UserRegistrationFacade(/* inject deps */) { }

    // Simple one-call API
    public RegistrationResult register(User user) {
        if (!validator.validate(user)) {
            return RegistrationResult.failure("Invalid user data");
        }
        repository.save(user);
        emailSender.sendWelcome(user.getEmail());
        logger.log("User registered: " + user.getId());
        cache.invalidate("users");
        return RegistrationResult.success(user);
    }
}

// Client chỉ cần gọi 1 method
facade.register(newUser);
```

---

## Behavioral Patterns

### 7. Observer

**Mục đích:** Định nghĩa one-to-many dependency. Khi 1 object thay đổi state, tất cả dependents được notify tự động.

**Khi dùng:** Event handling, pub/sub systems, MVC (Model notifies Views).

```java
// Observer interface
public interface EventListener<T> {
    void onEvent(T event);
}

// Observable (Subject)
public class EventBus {
    private final Map<Class<?>, List<EventListener>> listeners = new HashMap<>();

    public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<EventListener> eventListeners = listeners.getOrDefault(event.getClass(), List.of());
        eventListeners.forEach(listener -> listener.onEvent(event));
    }
}

// Events
public record OrderPlacedEvent(String orderId, String userId, double amount) {}
public record PaymentCompletedEvent(String paymentId, String orderId) {}

// Usage
EventBus eventBus = new EventBus();

// Subscribe
eventBus.subscribe(OrderPlacedEvent.class, event -> {
    System.out.println("Email Service: Send confirmation for order " + event.orderId());
});
eventBus.subscribe(OrderPlacedEvent.class, event -> {
    System.out.println("Inventory Service: Reserve items for order " + event.orderId());
});

// Publish
eventBus.publish(new OrderPlacedEvent("ORD-123", "USR-456", 299.99));
```

---

### 8. Strategy

**Mục đích:** Định nghĩa family of algorithms, encapsulate mỗi cái, và cho phép interchange.

**Khi dùng:** Khi cần nhiều variants của algorithm, thay thế điều kiện if/else lớn.

```java
// Strategy interface
public interface SortStrategy<T> {
    List<T> sort(List<T> items, Comparator<T> comparator);
}

// Concrete strategies
public class BubbleSortStrategy<T> implements SortStrategy<T> {
    @Override
    public List<T> sort(List<T> items, Comparator<T> comparator) {
        List<T> result = new ArrayList<>(items);
        // Bubble sort implementation
        return result;
    }
}

public class QuickSortStrategy<T> implements SortStrategy<T> {
    @Override
    public List<T> sort(List<T> items, Comparator<T> comparator) {
        List<T> result = new ArrayList<>(items);
        // Quick sort implementation
        return result;
    }
}

// Context
public class DataSorter<T> {
    private SortStrategy<T> strategy;

    public DataSorter(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }

    // Change strategy at runtime
    public void setStrategy(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public List<T> sort(List<T> data, Comparator<T> comparator) {
        return strategy.sort(data, comparator);
    }
}

// Practical: Payment Strategy
public interface PaymentStrategy {
    PaymentResult pay(double amount);
}

// VD: CreditCard, PayPal, Crypto đều implement PaymentStrategy
```

---

### 9. DAO Pattern (Java EE)

**Mục đích:** Tách data access logic khỏi business logic.

```java
public interface UserDao {
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(Long id);
}

public class UserDaoImpl implements UserDao {
    private final EntityManager em;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }
    // ... implement other methods
}
```

---

## Summary Table - Khi Nào Dùng Pattern Nào

| Pattern | Dùng khi... | Spring tương đương |
|---------|------------|-------------------|
| Singleton | Shared resource (logger, config) | `@Scope("singleton")` |
| Factory | Tạo object mà không biết class cụ thể | `@Bean`, `FactoryBean` |
| Builder | Object có nhiều optional params | Lombok `@Builder` |
| Adapter | Tích hợp incompatible interfaces | `HandlerAdapter` |
| Decorator | Thêm behavior động | `BeanPostProcessor`, AOP |
| Facade | Đơn giản hóa API phức tạp | Service layer |
| Observer | Event-driven communication | `ApplicationEventPublisher` |
| Strategy | Swap algorithms tại runtime | `@Qualifier` beans |
| Template Method | Fixed steps, biến thể implementation | `JdbcTemplate` |

---

*📌 Tiếp theo: [Interview-QA.md](Interview-QA.md) | [Practice-Exercises.md](Practice-Exercises.md)*
