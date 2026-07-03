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

**Mục đích:** Đảm bảo chỉ có duy nhất 1 instance của class được tạo ra và tồn tại trong toàn bộ ứng dụng.

**Cách triển khai thực tế trong Spring Boot:**
Trong Spring Boot, bạn **KHÔNG CẦN** viết các hàm Double-Checked Locking hay Enum Singleton thủ công. 
Mặc định, mọi class được đánh dấu bằng các chú thích `@Component`, `@Service`, `@Repository`, hoặc được định nghĩa bằng `@Bean` đều có **Singleton Scope**. Spring IoC Container (ApplicationContext) sẽ khởi tạo duy nhất 1 thực thể và tiêm (inject) thực thể đó vào mọi nơi yêu cầu.

```java
@Service // Spring tự động đăng ký class này dưới dạng một Singleton Bean
public class AuditLogService {
    private final List<String> logs = new ArrayList<>();

    // Không cần hàm getInstance() và private Constructor thủ công
    public synchronized void log(String action, String user) {
        logs.add(String.format("[%s] User %s performed action: %s", LocalDateTime.now(), user, action));
    }
}

// Khi sử dụng ở các Controller/Service khác nhau:
@RestController
public class OrderController {
    @Autowired
    private AuditLogService auditLogService; // Spring tiêm chính xác instance Singleton duy nhất vào đây
}

@RestController
public class UserController {
    @Autowired
    private AuditLogService auditLogService; // Tiếp tục sử dụng chung instance Singleton đó
}
```

---

### 2. Factory Method (Factory Pattern)

**Mục đích:** Định nghĩa một interface tạo đối tượng, nhưng để các subclass quyết định class cụ thể nào sẽ được tạo.

**Cách triển khai thực tế trong Spring Boot (Dynamic Map Autowired Factory):**
Thay vì viết switch-case thủ công gây vi phạm nguyên tắc **Open/Closed Principle (OCP)** (mỗi khi thêm loại sản phẩm mới lại phải sửa code Factory), ta tận dụng sức mạnh của Spring IoC. Spring cho phép tự động tiêm tất cả các implementations của một interface vào một `Map<String, Service>` mà Key là tên của Spring Bean.

```java
// 1. Định nghĩa Interface Product
public interface NotificationService {
    void send(String recipient, String message);
}

// 2. Định nghĩa các Concrete Product (đặt tên Bean cụ thể)
@Service("email")
public class EmailNotification implements NotificationService {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Gửi Email đến " + recipient + ": " + message);
    }
}

@Service("sms")
public class SmsNotification implements NotificationService {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Gửi SMS đến " + recipient + ": " + message);
    }
}

@Service("push")
public class PushNotification implements NotificationService {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Gửi Push Notification đến " + recipient + ": " + message);
    }
}

// 3. Thiết kế Dynamic Factory tận dụng Spring IoC
@Component
public class NotificationFactory {

    // Spring tự động quét và tiêm toàn bộ Bean thuộc kiểu NotificationService vào Map
    // Key: Tên Spring Bean ("email", "sms", "push")
    // Value: Instance tương ứng
    @Autowired
    private Map<String, NotificationService> notificationMap;

    public NotificationService getService(String channel) {
        NotificationService service = notificationMap.get(channel.toLowerCase());
        if (service == null) {
            throw new IllegalArgumentException("Kênh thông báo không hợp lệ: " + channel);
        }
        return service;
    }
}

// 4. Cách sử dụng trong Controller
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationFactory notificationFactory;

    @PostMapping
    public ResponseEntity<String> notify(@RequestParam String channel, @RequestParam String msg) {
        // Lấy service động dựa trên tham số truyền vào từ client
        NotificationService service = notificationFactory.getService(channel);
        service.send("customer@example.com", msg);
        return ResponseEntity.ok("Gửi thành công!");
    }
}
```

---

### 3. Builder

**Mục đích:** Xây dựng một đối tượng phức tạp từng bước một. Nó giúp tách rời quá trình xây dựng đối tượng khỏi các thuộc tính bên trong nó, tránh việc viết các Constructor có quá nhiều tham số (Telescoping Constructor).

**Cách triển khai thực tế trong Spring Boot (Lombok @Builder):**
Trong các ứng dụng Spring Boot thực tế, chúng ta hầu như không tự viết class Builder thủ công (vốn sinh ra rất nhiều boilerplate code). Chúng ta sử dụng thư viện **Lombok** với chú thích `@Builder` để tạo Builder Pattern một cách thanh lịch cho các DTO (Data Transfer Object) hoặc JPA Entities.

```java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // Lombok sẽ tự động sinh code Builder Pattern trong quá trình biên dịch
public class UserResponseDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String phoneNumber; // Thuộc tính tùy chọn (optional)
    private final String address;     // Thuộc tính tùy chọn (optional)
}

// Sử dụng trong Service / Controller để tạo đối tượng:
@Service
public class UserService {
    public UserResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Tạo DTO bằng Builder
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // phoneNumber và address có thể bỏ qua nếu null mà không cần nạp chồng Constructor
                .build();
    }
}
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
