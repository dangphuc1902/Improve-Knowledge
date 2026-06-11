# 💻 Design Patterns - Bài Tập Thực Hành

---

## Bài Tập 1: Implement Creational Patterns

### Singleton - Logger
```java
// Implement thread-safe Logger sử dụng Enum Singleton
public enum AppLogger {
    INSTANCE;
    
    private final List<LogEntry> logs = new ArrayList<>();
    
    // TODO: log(Level level, String message)
    // TODO: getLogs(Level level) - filter by level
    // TODO: clearLogs()
    // TODO: exportLogs(String filePath)
}

enum Level { INFO, WARN, ERROR, DEBUG }
record LogEntry(Level level, String message, LocalDateTime timestamp) {}
```

### Factory - Notification System
```java
// Implement factory để tạo notifications theo channel
// VD: NotificationFactory.create("email") → EmailNotification
// VD: NotificationFactory.create("sms") → SmsNotification
// VD: NotificationFactory.create("slack") → SlackNotification

// Sau đó: implement Abstract Factory cho 2 themes:
// LightTheme: LightButton, LightTextField
// DarkTheme: DarkButton, DarkTextField
```

---

## Bài Tập 2: Implement Structural Patterns

### Decorator - Text Processor
```java
// Implement chain of text decorators:
// PlainText → BoldDecorator → ItalicDecorator → UpperCaseDecorator → TrimDecorator

// Test:
TextFormatter formatter = new TrimDecorator(
    new UpperCaseDecorator(
        new BoldDecorator(new PlainText())
    )
);
System.out.println(formatter.format("  hello world  "));
// Expected: <b>HELLO WORLD</b>
```

### Proxy - Caching
```java
// Implement Caching Proxy cho DatabaseUserRepository
// - Cache kết quả findById() trong Map
// - Khi findById() được gọi: check cache trước
// - Nếu cache miss: call real repo, store in cache
// - Khi save()/delete() được gọi: invalidate cache

public interface UserRepository {
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(Long id);
}

public class DatabaseUserRepository implements UserRepository { ... }
public class CachingUserRepository implements UserRepository {
    private final UserRepository delegate;
    private final Map<Long, User> cache = new ConcurrentHashMap<>();
    // TODO: implement
}
```

---

## Bài Tập 3: Implement Behavioral Patterns

### Observer - Event System
```java
// Implement EventBus cho Order Processing:

// Events:
// OrderPlaced, PaymentCompleted, OrderShipped, OrderDelivered

// Listeners:
// EmailService: listen to OrderPlaced, OrderShipped
// SMSService: listen to OrderShipped, OrderDelivered  
// InventoryService: listen to OrderPlaced
// AnalyticsService: listen to all events

EventBus bus = new EventBus();
// TODO: subscribe services to events
// TODO: publish OrderPlaced(orderId, userId, amount)
// Expected: email sent, inventory reserved, analytics tracked
```

### Strategy - Sorting
```java
// Implement SortContext với multiple strategies:
// BubbleSort, QuickSort, MergeSort

// Test với 3 datasets:
// Small (< 10 items): any sort
// Medium (< 1000 items): QuickSort
// Large (>= 1000 items): MergeSort

public class SmartSorter<T extends Comparable<T>> {
    // Automatically pick strategy based on data size
    public List<T> sort(List<T> data) { ... }
}
```

---

## Bài Tập 4: Mini Project - E-Commerce System với Patterns

**Yêu cầu:** Xây dựng hệ thống e-commerce nhỏ áp dụng các patterns:

```
Singleton: AppConfig (database URL, app settings)
Factory: PaymentFactory (tạo CreditCard/PayPal/Crypto payment)
Builder: OrderBuilder (build Order object)
Observer: OrderEventBus (notify services khi order state thay đổi)
Strategy: DiscountStrategy (Percentage, FixedAmount, BuyXGetY)
Decorator: PriceCalculator (add tax, shipping, coupon)
Facade: CheckoutFacade (đơn giản hóa checkout process)
DAO: UserDao, ProductDao, OrderDao
```

**Flow:**
```
1. User thêm items vào cart
2. Checkout → CheckoutFacade.checkout(cart, payment)
3. CheckoutFacade:
   a. Calculate price với decorators (tax, discount)
   b. Create Order với OrderBuilder
   c. Process payment via PaymentFactory
   d. Publish OrderPlaced event
4. Event listeners: Email, Inventory, Analytics
```

---

*📌 Xem tiếp: [13-Build-Tools](../13-Build-Tools/Theory.md)*
