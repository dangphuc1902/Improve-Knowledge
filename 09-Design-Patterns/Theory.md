# 📖 Design Patterns - Lý Thuyết & Ví Dụ

> **Tuần 8 | GoF Design Patterns - 23 mẫu kinh điển**

---

## Tổng Quan Design Patterns

### Design Patterns là gì?
**Design Pattern** là **giải pháp tái sử dụng (reusable solution)** cho các vấn đề thiết kế phần mềm phổ biến và lặp đi lặp lại. Chúng không phải là code cụ thể mà là **khuôn mẫu (template)** mô tả cách giải quyết một vấn đề trong nhiều tình huống khác nhau.

#### 1. Tại sao cần Design Patterns?
*   **Ngôn ngữ chung giữa developers**: Nói "dùng Strategy Pattern" ngay lập tức truyền đạt ý tưởng thiết kế mà không cần giải thích dài dòng.
*   **Tránh anti-patterns**: Các pattern là kinh nghiệm đúc kết để tránh các lỗi thiết kế thường gặp (God Object, Spaghetti Code...).
*   **Dễ bảo trì và mở rộng**: Code theo pattern tuân thủ SOLID principles, dễ thêm tính năng mới mà không phá vỡ cái cũ.

#### 2. Phân loại 23 GoF Patterns

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

### 1.1. Singleton là gì?
**Singleton Pattern** đảm bảo **chỉ có duy nhất 1 instance** của class được tạo ra và tồn tại trong toàn bộ ứng dụng, đồng thời cung cấp một điểm truy cập toàn cục (global access point) duy nhất tới instance đó.

#### 1. Tại sao cần Singleton?
*   **Chia sẻ trạng thái toàn cục (Shared State)**: Các component dùng chung một tài nguyên (logger, config, connection pool) mà không tạo ra bản sao dư thừa.
*   **Tiết kiệm tài nguyên**: Chỉ tạo instance một lần, tránh overhead khởi tạo lặp lại.
*   **Điều phối truy cập tập trung**: Kiểm soát đồng thời (concurrency) qua một điểm duy nhất (ví dụ: cache manager).

#### 2. Nếu không dùng Singleton thì thay thế bằng gì?
Phải truyền instance qua tham số (Dependency Injection) ở từng nơi cần dùng, hoặc dùng biến `static` thủ công. Cách dùng biến static thủ công không an toàn đa luồng và khó test.

### 1.2. Ví dụ cụ thể trong Spring Boot

Trong Spring Boot, **KHÔNG CẦN** viết các hàm Double-Checked Locking hay Enum Singleton thủ công. Mặc định, mọi class được đánh dấu bằng `@Component`, `@Service`, `@Repository` đều có **Singleton Scope**. Spring IoC Container sẽ khởi tạo duy nhất 1 thực thể và tiêm (inject) vào mọi nơi yêu cầu:

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

> [!IMPORTANT]
> Vì Singleton Bean được chia sẻ giữa nhiều thread, **tuyệt đối không lưu trạng thái request-specific** (như thông tin người dùng hiện tại) trong field của Bean. Dữ liệu per-request phải dùng `ThreadLocal` hoặc truyền qua parameter.

---

### 2. Factory Method (Factory Pattern)

### 2.1. Factory Method là gì?
**Factory Method Pattern** định nghĩa một **interface để tạo đối tượng**, nhưng để các subclass hoặc implementations quyết định **class cụ thể nào** sẽ được khởi tạo. Nó che giấu logic tạo object khỏi client.

#### 1. Tại sao cần Factory Method?
*   **Tuân thủ Open/Closed Principle (OCP)**: Thêm loại sản phẩm mới (ví dụ: `ZaloNotification`) mà **không cần sửa** Factory class - chỉ cần thêm implementation mới.
*   **Che giấu logic khởi tạo phức tạp**: Client chỉ biết interface, không biết class cụ thể nào đang được dùng.
*   **Dễ thay đổi implementation**: Thay đổi loại object được tạo mà không ảnh hưởng đến code sử dụng.

#### 2. Nếu không dùng Factory thì thay thế bằng gì?
Phải dùng `if-else` hoặc `switch-case` để chọn class cụ thể. Mỗi khi thêm loại mới phải sửa đoạn switch này - **vi phạm OCP** và dễ gây lỗi.

### 2.2. Ví dụ cụ thể trong Spring Boot

Thay vì viết switch-case thủ công, ta tận dụng Spring IoC để tự động quản lý Map các implementations:

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
    // Key: Tên Spring Bean ("email", "sms", "push") | Value: Instance tương ứng
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

### 3.1. Builder Pattern là gì?
**Builder Pattern** cho phép **xây dựng một đối tượng phức tạp từng bước một** bằng cách tách rời quá trình xây dựng khỏi biểu diễn cuối cùng. Nó giải quyết vấn đề **Telescoping Constructor** (constructor có quá nhiều tham số).

#### 1. Tại sao cần Builder Pattern?
*   **Tránh Telescoping Constructor**: Thay vì `new User(id, name, email, null, null, null)` không rõ ràng, dùng `.id(1).name("John").email("john@ex.com").build()`.
*   **Optional parameters rõ ràng**: Không cần nạp chồng nhiều constructor cho mọi tổ hợp tham số tùy chọn.
*   **Immutable objects**: Builder thường tạo ra các đối tượng bất biến (final fields) an toàn đa luồng.

#### 2. Nếu không dùng Builder thì thay thế bằng gì?
Có thể dùng **Constructor với nhiều tham số** (khó đọc, dễ nhầm thứ tự tham số) hoặc **Setter methods** (object có thể ở trạng thái không hợp lệ trong quá trình xây dựng).

### 3.2. Ví dụ cụ thể trong Spring Boot

Trong Spring Boot, ta dùng **Lombok `@Builder`** thay vì tự viết Builder thủ công (boilerplate code rất nhiều):

```java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // Lombok tự động sinh code Builder Pattern trong quá trình biên dịch
public class UserResponseDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String phoneNumber; // Thuộc tính tùy chọn (optional)
    private final String address;     // Thuộc tính tùy chọn (optional)
}

// Sử dụng trong Service để tạo DTO
@Service
public class UserService {
    public UserResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Tạo DTO bằng Builder - rõ ràng, chỉ set những field cần thiết
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // phoneNumber và address có thể bỏ qua nếu null
                // mà không cần nạp chồng Constructor
                .build();
    }
}
```

---

## Structural Patterns

### 4. Adapter

### 4.1. Adapter Pattern là gì?
**Adapter Pattern** **chuyển đổi interface** của một class thành interface khác mà client mong đợi. Nó cho phép các class với interface **không tương thích** làm việc cùng nhau - như một đầu chuyển đổi ổ cắm điện.

#### 1. Tại sao cần Adapter Pattern?
*   **Tích hợp legacy code**: Sử dụng lại code cũ mà không sửa đổi nó (tuân thủ OCP).
*   **Tích hợp third-party library**: Wrap API của thư viện ngoài vào interface của hệ thống mình.
*   **Giảm coupling**: Client chỉ phụ thuộc vào Target interface, không biết về Adaptee cụ thể.

#### 2. Nếu không dùng Adapter thì thay thế bằng gì?
Phải sửa trực tiếp class legacy/third-party (vi phạm OCP và có thể không có source code), hoặc viết lại toàn bộ - tốn thời gian và rủi ro cao.

### 4.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Tích hợp XML Legacy Processor vào hệ thống xử lý JSON

```java
// Target interface (hệ thống hiện tại muốn dùng)
public interface JsonDataProcessor {
    Map<String, Object> process(String jsonData);
}

// Adaptee (legacy XML processor - không thể sửa đổi)
public class XmlDataProcessor {
    public Document parseXml(String xmlData) { /* parse XML */ return null; }
    public Map<String, Object> convertToMap(Document doc) { return null; }
}

// Adapter - bọc XmlDataProcessor và implement JsonDataProcessor
@Component
public class XmlToJsonAdapter implements JsonDataProcessor {
    private final XmlDataProcessor xmlProcessor;

    public XmlToJsonAdapter(XmlDataProcessor xmlProcessor) {
        this.xmlProcessor = xmlProcessor;
    }

    @Override
    public Map<String, Object> process(String jsonData) {
        // Bên trong: chuyển JSON sang XML → dùng XmlProcessor → trả về Map
        String xmlData = convertJsonToXml(jsonData);
        Document doc = xmlProcessor.parseXml(xmlData);
        return xmlProcessor.convertToMap(doc);
    }

    private String convertJsonToXml(String json) { /* ... */ return null; }
}

// Client chỉ biết JsonDataProcessor, không biết XML processor bên dưới
@Service
public class DataImportService {
    @Autowired
    private JsonDataProcessor dataProcessor; // Inject XmlToJsonAdapter

    public void importData(String jsonInput) {
        Map<String, Object> data = dataProcessor.process(jsonInput);
        // xử lý data...
    }
}
```

---

### 5. Decorator

### 5.1. Decorator Pattern là gì?
**Decorator Pattern** cho phép **thêm hành vi (behavior) mới vào object một cách động** mà không cần thay đổi class của nó và không ảnh hưởng đến các objects khác. Decorator "bọc" (wrap) object gốc và bổ sung thêm chức năng.

#### 1. Tại sao cần Decorator Pattern?
*   **Mở rộng không cần kế thừa**: Thêm behavior mà không tạo ra vô số subclass (`BoldText`, `ItalicBoldText`, `ItalicBoldUpperText`...).
*   **Kết hợp linh hoạt**: Stack nhiều decorator lại với nhau để tạo ra nhiều tổ hợp chức năng.
*   **Tuân thủ Single Responsibility Principle**: Mỗi decorator chỉ chịu trách nhiệm cho một behavior.

#### 2. Nếu không dùng Decorator thì thay thế bằng gì?
Phải dùng **Inheritance (Kế thừa)**: tạo subclass cho mỗi tổ hợp chức năng. Với N features độc lập, sẽ cần đến 2^N subclass - **class explosion** (bùng nổ class).

### 5.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Text Formatter với Decorator - có thể stack nhiều lớp

```java
// Component interface
public interface TextFormatter {
    String format(String text);
}

// Concrete Component - implement cơ bản
public class PlainText implements TextFormatter {
    @Override
    public String format(String text) { return text; }
}

// Decorator base - bọc một TextFormatter khác
public abstract class TextDecorator implements TextFormatter {
    protected final TextFormatter wrapped;
    public TextDecorator(TextFormatter wrapped) { this.wrapped = wrapped; }
}

// Concrete Decorators - mỗi cái thêm 1 behavior
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

// Sử dụng - stack decorators linh hoạt
TextFormatter formatter = new BoldDecorator(
    new ItalicDecorator(
        new PlainText()
    )
);
System.out.println(formatter.format("Hello"));  // <b><i>Hello</i></b>
```

> [!NOTE]
> Trong Spring Boot, **AOP (Aspect-Oriented Programming)** chính là Decorator Pattern được framework implement sẵn. Khi bạn dùng `@Transactional`, `@Cacheable`, `@Async` - Spring "bọc" bean của bạn bằng một proxy (decorator) để thêm behavior tương ứng.

---

### 6. Facade

### 6.1. Facade Pattern là gì?
**Facade Pattern** cung cấp một **interface đơn giản, thống nhất** cho một **subsystem phức tạp**. Nó không thêm chức năng mới mà chỉ đơn giản hóa API để client dễ sử dụng hơn, giảm sự phụ thuộc vào các thành phần nội bộ.

#### 1. Tại sao cần Facade Pattern?
*   **Giảm complexity cho client**: Thay vì gọi 5-6 service riêng lẻ theo đúng thứ tự, client chỉ gọi 1 method duy nhất.
*   **Giảm coupling**: Client không phụ thuộc vào các class nội bộ của subsystem - dễ thay đổi implementation bên trong.
*   **Phân tầng rõ ràng**: Service layer trong Spring Boot chính là Facade pattern - che giấu độ phức tạp khỏi Controller.

#### 2. Nếu không dùng Facade thì thay thế bằng gì?
Controller phải trực tiếp gọi và phối hợp nhiều repository/service - **vi phạm Single Responsibility Principle** và làm Controller trở thành "God Class".

### 6.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: UserRegistrationFacade - đơn giản hóa quy trình đăng ký

```java
// Các subsystems phức tạp bên dưới
class UserValidator { public boolean validate(User user) { return true; } }
class EmailSender { public void sendWelcome(String email) { } }
class DatabaseRepository { public void save(User user) { } }
class AuditLogger { public void log(String action) { } }
class CacheManager { public void invalidate(String key) { } }

// Facade - ẩn đi sự phức tạp của các subsystem
@Service
public class UserRegistrationFacade {
    private final UserValidator validator;
    private final EmailSender emailSender;
    private final DatabaseRepository repository;
    private final AuditLogger logger;
    private final CacheManager cache;

    // Constructor injection...

    // Client chỉ cần gọi 1 method thay vì phối hợp 5 service riêng lẻ
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

// Controller sử dụng Facade - rất gọn gàng
@RestController
public class UserController {
    @Autowired
    private UserRegistrationFacade registrationFacade;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResult> register(@RequestBody User user) {
        return ResponseEntity.ok(registrationFacade.register(user));
    }
}
```

---

## Behavioral Patterns

### 7. Observer

### 7.1. Observer Pattern là gì?
**Observer Pattern** định nghĩa mối quan hệ **one-to-many** giữa các objects: khi một object (Subject/Publisher) **thay đổi trạng thái**, tất cả các đối tượng phụ thuộc (Observer/Subscriber) được **tự động thông báo và cập nhật**.

#### 1. Tại sao cần Observer Pattern?
*   **Loose coupling**: Subject không cần biết Observer cụ thể là gì, chỉ biết chúng implement interface Observer.
*   **Mở rộng dễ dàng**: Thêm Observer mới (ví dụ: thêm kênh thông báo Zalo) mà không sửa Subject.
*   **Nền tảng của Event-Driven Architecture**: Kafka, RabbitMQ, Spring Events đều dựa trên nguyên lý Observer.

#### 2. Nếu không dùng Observer thì thay thế bằng gì?
Subject phải trực tiếp gọi từng Observer (`emailService.send()`, `smsService.send()`, `pushService.send()`...) - **tight coupling**, mỗi khi thêm kênh mới phải sửa Subject.

### 7.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: Custom EventBus - triển khai Observer thủ công

```java
// Observer interface
public interface EventListener<T> {
    void onEvent(T event);
}

// Observable (Subject) - quản lý danh sách listener
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

// Events (domain events)
public record OrderPlacedEvent(String orderId, String userId, double amount) {}

// Sử dụng
EventBus eventBus = new EventBus();

// Subscribe - đăng ký nhiều listener cho cùng 1 event
eventBus.subscribe(OrderPlacedEvent.class, event ->
    System.out.println("Email Service: Gửi xác nhận đơn hàng " + event.orderId()));
eventBus.subscribe(OrderPlacedEvent.class, event ->
    System.out.println("Inventory Service: Trừ tồn kho cho đơn " + event.orderId()));

// Publish - Subject chỉ publish event, không biết ai đang lắng nghe
eventBus.publish(new OrderPlacedEvent("ORD-123", "USR-456", 299.99));
```

#### Ví dụ 2: Spring ApplicationEvent - Observer tích hợp sẵn trong Spring

```java
// 1. Định nghĩa Event
public class OrderPlacedEvent extends ApplicationEvent {
    private final String orderId;
    public OrderPlacedEvent(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }
    public String getOrderId() { return orderId; }
}

// 2. Publisher (Subject)
@Service
public class OrderService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void placeOrder(Order order) {
        orderRepository.save(order);
        // Publish event - không cần biết ai đang lắng nghe
        eventPublisher.publishEvent(new OrderPlacedEvent(this, order.getId().toString()));
    }
}

// 3. Listeners (Observers) - có thể có nhiều listener
@Component
public class EmailNotificationListener {
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        System.out.println("Gửi email xác nhận cho đơn: " + event.getOrderId());
    }
}

@Component
public class InventoryListener {
    @EventListener
    @Async  // Xử lý bất đồng bộ, không block luồng chính
    public void onOrderPlaced(OrderPlacedEvent event) {
        System.out.println("Cập nhật tồn kho cho đơn: " + event.getOrderId());
    }
}
```

---

### 8. Strategy

### 8.1. Strategy Pattern là gì?
**Strategy Pattern** định nghĩa một **family of algorithms (họ các thuật toán)**, encapsulate mỗi thuật toán vào class riêng, và cho phép **hoán đổi chúng linh hoạt tại runtime** mà không thay đổi client code.

#### 1. Tại sao cần Strategy Pattern?
*   **Thay thế if-else/switch khổng lồ**: Mỗi nhánh if-else trở thành một class Strategy riêng biệt.
*   **Tuân thủ OCP**: Thêm thuật toán/chiến lược mới bằng cách thêm class mới, không sửa code cũ.
*   **Dễ test**: Mỗi Strategy có thể được test độc lập.

#### 2. Nếu không dùng Strategy thì thay thế bằng gì?
Phải dùng chuỗi `if-else` hoặc `switch-case` lớn trong một method. Mỗi khi thêm chiến lược mới (ví dụ: phương thức thanh toán mới) phải sửa method đó - **vi phạm OCP**.

### 8.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Payment Strategy - hệ thống thanh toán đa phương thức

```java
// Strategy interface
public interface PaymentStrategy {
    PaymentResult pay(double amount, String currency);
    String getMethodName(); // Để Factory nhận diện
}

// Concrete Strategies
@Service("creditCard")
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public PaymentResult pay(double amount, String currency) {
        // Gọi Credit Card payment gateway
        System.out.println("Thanh toán " + amount + " " + currency + " qua Credit Card");
        return PaymentResult.success("CC-TXN-001");
    }
    @Override
    public String getMethodName() { return "creditCard"; }
}

@Service("paypal")
public class PayPalPayment implements PaymentStrategy {
    @Override
    public PaymentResult pay(double amount, String currency) {
        // Gọi PayPal API
        System.out.println("Thanh toán " + amount + " " + currency + " qua PayPal");
        return PaymentResult.success("PP-TXN-001");
    }
    @Override
    public String getMethodName() { return "paypal"; }
}

@Service("crypto")
public class CryptoPayment implements PaymentStrategy {
    @Override
    public PaymentResult pay(double amount, String currency) {
        // Gọi Blockchain API
        System.out.println("Thanh toán " + amount + " " + currency + " qua Crypto");
        return PaymentResult.success("CRYPTO-TXN-001");
    }
    @Override
    public String getMethodName() { return "crypto"; }
}

// Context - sử dụng Strategy qua Spring Map Autowiring
@Service
public class PaymentService {
    @Autowired
    private Map<String, PaymentStrategy> paymentStrategies; // Spring inject tất cả strategy

    public PaymentResult processPayment(String method, double amount, String currency) {
        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không hỗ trợ: " + method);
        }
        return strategy.pay(amount, currency); // Chọn strategy tại runtime theo request
    }
}
```

---

### 9. DAO Pattern (Java EE)

### 9.1. DAO Pattern là gì?
**DAO (Data Access Object) Pattern** tách **data access logic** (truy vấn DB) khỏi **business logic** (nghiệp vụ). Mọi thao tác với database được đóng gói vào một lớp DAO riêng biệt, business logic không trực tiếp biết đến DB.

#### 1. Tại sao cần DAO Pattern?
*   **Separation of Concerns**: Business logic không bị "nhiễm" bởi code SQL/JPQL.
*   **Dễ thay đổi persistence technology**: Thay MongoDB bằng PostgreSQL chỉ cần viết lại DAO class, không động đến Service.
*   **Dễ test**: Có thể mock DAO để test Service mà không cần DB thật.

#### 2. Trong Spring Boot, DAO Pattern biểu hiện như thế nào?
**Spring Data JPA Repository** chính là hiện thực hóa DAO Pattern một cách tự động. `JpaRepository` là DAO interface, Spring Boot tự động sinh implementation (không cần viết `UserDaoImpl` thủ công).

### 9.2. Ví dụ cụ thể

#### Ví dụ 1: DAO Pattern truyền thống với EntityManager

```java
// DAO Interface
public interface UserDao {
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(Long id);
}

// DAO Implementation
@Repository
public class UserDaoImpl implements UserDao {
    private final EntityManager em;

    public UserDaoImpl(EntityManager em) { this.em = em; }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        }
        return em.merge(user);
    }

    @Override
    public void delete(Long id) {
        User user = em.find(User.class, id);
        if (user != null) em.remove(user);
    }
}
```

#### Ví dụ 2: Spring Data JPA - DAO tự động (cách hiện đại)

```java
// Spring Data tự động sinh implementation - không cần viết UserDaoImpl
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Các method nâng cao nếu cần
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
}
```

---

## Summary Table - Khi Nào Dùng Pattern Nào

| Pattern | Vấn đề giải quyết | Dùng khi... | Spring tương đương |
| :--- | :--- | :--- | :--- |
| **Singleton** | Chỉ cần 1 instance | Shared resource (logger, config) | `@Scope("singleton")` (mặc định) |
| **Factory Method** | Tạo object không biết class cụ thể | Nhiều loại sản phẩm, OCP | `@Bean`, `Map<String, Service>` |
| **Builder** | Object nhiều optional params | DTO phức tạp | Lombok `@Builder` |
| **Adapter** | Interface không tương thích | Tích hợp legacy/third-party | `HandlerAdapter` |
| **Decorator** | Thêm behavior động, tránh subclass explosion | Nhiều tổ hợp tính năng | AOP, `BeanPostProcessor` |
| **Facade** | Đơn giản hóa API phức tạp | Service layer, orchestration | `@Service` class |
| **Observer** | One-to-many event notification | Event-driven, loose coupling | `ApplicationEventPublisher` |
| **Strategy** | Hoán đổi thuật toán tại runtime | Nhiều variants của một hành vi | `@Qualifier` beans, Map inject |
| **DAO** | Tách data access khỏi business logic | Repository layer | `JpaRepository<T, ID>` |
| **Template Method** | Fixed steps, biến thể implementation | Workflow cố định, bước thay đổi | `JdbcTemplate`, `RestTemplate` |

---

*📌 Tiếp theo: [Interview-QA.md](Interview-QA.md) | [Practice-Exercises.md](Practice-Exercises.md)*
