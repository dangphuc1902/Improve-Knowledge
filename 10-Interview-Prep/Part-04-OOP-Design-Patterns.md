# PART 4 - OOP & DESIGN PATTERNS

> **Topics**: 4 OOP Pillars · Singleton · Factory · Builder · Strategy · Observer · Adapter · Decorator · Proxy

---

## OOP PILLARS

### Q1. Explain the Four Pillars of OOP with Real Examples

---

### 1. Encapsulation

**Definition**: Bundling data (fields) and behavior (methods) together; restricting direct access to internal state.

```java
// BAD - No encapsulation
public class BankAccount {
    public double balance; // Anyone can set balance = -1000000
}

// GOOD - Encapsulated
public class BankAccount {
    private double balance;
    private String accountNumber;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) throw new InsufficientFundsException();
        this.balance -= amount;
    }

    public double getBalance() { return balance; } // Read-only access
}
```

**Real-world**: ATM machine — you interact via buttons (public methods), not directly with the cash mechanism (private fields).

**Follow-up**: What is the difference between encapsulation and abstraction?

---

### 2. Inheritance

**Definition**: A class (child) acquires properties and behaviors of another class (parent).

```java
// Base class
public abstract class Vehicle {
    protected String brand;
    protected int speed;

    public abstract void move(); // Abstract method

    public void stop() { System.out.println(brand + " stopped"); }
}

// Child class
public class Car extends Vehicle {
    private int doors;

    @Override
    public void move() {
        System.out.println(brand + " car drives at " + speed + " km/h");
    }
}

public class Motorcycle extends Vehicle {
    @Override
    public void move() {
        System.out.println(brand + " motorcycle rides at " + speed + " km/h");
    }
}
```

**Issues with Inheritance**:
- Tight coupling between parent and child
- Fragile base class problem
- Prefer **composition over inheritance**

**Follow-up**: What is the difference between `extends` and `implements`?

---

### 3. Polymorphism

**Definition**: Same interface, different implementations. One object can take many forms.

**Two types:**
- **Compile-time (static)**: Method overloading
- **Runtime (dynamic)**: Method overriding

```java
// Runtime Polymorphism
public interface PaymentProcessor {
    void process(double amount);
}

public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void process(double amount) {
        System.out.println("Processing credit card payment: " + amount);
    }
}

public class PayPalProcessor implements PaymentProcessor {
    @Override
    public void process(double amount) {
        System.out.println("Processing PayPal payment: " + amount);
    }
}

// Usage - polymorphic reference
PaymentProcessor processor = getProcessor(paymentType); // returns either type
processor.process(100.0); // Correct implementation called at runtime

// Compile-time Polymorphism (Overloading)
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}
```

---

### 4. Abstraction

**Definition**: Hiding implementation details; exposing only what's necessary.

```java
// Abstract Class - partial abstraction
public abstract class DatabaseConnector {
    // Common implementation
    public void openConnection() { /* JDBC boilerplate */ }
    public void closeConnection() { /* Close resources */ }

    // Abstract - each DB implements differently
    public abstract String buildConnectionString();
    public abstract void executeQuery(String sql);
}

// Interface - full abstraction (contract)
public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void delete(ID id);
}
```

**Abstract Class vs Interface:**

| Feature | Abstract Class | Interface |
|---|---|---|
| Methods | Abstract + concrete | Abstract (default in Java 8+) |
| Fields | Any | Only public static final |
| Inheritance | Single | Multiple |
| Constructor | Yes | No |
| Use when | "Is-a" with common code | "Can-do" contract |

---

## DESIGN PATTERNS

### Q2. Singleton Pattern

**Definition**: Ensures a class has only ONE instance and provides a global access point.

**Real-world**: Database connection pool, Configuration manager, Logger

**Thread-Safe Implementation:**
```java
// Double-Checked Locking (Java 5+)
public class DatabasePool {
    private static volatile DatabasePool instance; // volatile is critical!
    private List<Connection> connections;

    private DatabasePool() {
        connections = initializePool(10);
    }

    public static DatabasePool getInstance() {
        if (instance == null) {                    // First check (no lock)
            synchronized (DatabasePool.class) {
                if (instance == null) {            // Second check (with lock)
                    instance = new DatabasePool();
                }
            }
        }
        return instance;
    }
}

// Enum Singleton (Best approach - Bill Pugh)
public enum AppConfig {
    INSTANCE;

    private final Properties properties = loadProperties();

    public String get(String key) {
        return properties.getProperty(key);
    }
}

// Usage
AppConfig.INSTANCE.get("db.url");
```

**Follow-up Questions:**
- Why is `volatile` needed in double-checked locking?
- How would you break a Singleton? (Reflection, Serialization, Cloning)
- How does Enum Singleton prevent all these? (JVM guarantees, Enum serialization safe)

---

### Q3. Factory & Abstract Factory Pattern

**Factory Method:**
```java
// Interface
public interface Notification {
    void send(String message, String recipient);
}

// Concrete products
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
    public static Notification create(String type) {
        return switch (type.toLowerCase()) {
            case "email" -> new EmailNotification();
            case "sms" -> new SMSNotification();
            case "push" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}

// Usage
Notification n = NotificationFactory.create("email");
n.send("Your order is shipped!", "user@example.com");
```

**Abstract Factory** — Factory of Factories:
```java
// Abstract factory for creating UI components per OS
public interface UIFactory {
    Button createButton();
    TextInput createTextInput();
}

public class WindowsUIFactory implements UIFactory {
    @Override public Button createButton() { return new WindowsButton(); }
    @Override public TextInput createTextInput() { return new WindowsTextInput(); }
}

public class MacUIFactory implements UIFactory {
    @Override public Button createButton() { return new MacButton(); }
    @Override public TextInput createTextInput() { return new MacTextInput(); }
}
```

---

### Q4. Builder Pattern

**Definition**: Constructs complex objects step-by-step. Solves constructor telescoping problem.

```java
// Complex object with many optional fields
public class HttpRequest {
    private final String url;         // required
    private final String method;      // required
    private final Map<String, String> headers;
    private final String body;
    private final int timeout;
    private final boolean followRedirects;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.body = builder.body;
        this.timeout = builder.timeout;
        this.followRedirects = builder.followRedirects;
    }

    public static class Builder {
        private final String url;
        private final String method;
        private Map<String, String> headers = new HashMap<>();
        private String body;
        private int timeout = 30;
        private boolean followRedirects = true;

        public Builder(String url, String method) {
            this.url = url;
            this.method = method;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this; // Fluent interface
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder timeout(int seconds) {
            this.timeout = seconds;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}

// Usage - clean and readable
HttpRequest request = new HttpRequest.Builder("https://api.example.com", "POST")
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer token123")
    .body("{\"name\": \"John\"}")
    .timeout(60)
    .build();
```

**Real-world**: Lombok's `@Builder`, StringBuilder, Hibernate Criteria Builder, Spring's `UriComponentsBuilder`

---

### Q5. Strategy Pattern

**Definition**: Define a family of algorithms, encapsulate each one, make them interchangeable. Lets the algorithm vary independently from clients.

```java
// Strategy interface
public interface SortStrategy {
    void sort(int[] array);
}

// Concrete strategies
public class BubbleSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] array) { /* Bubble sort */ }
}

public class QuickSortStrategy implements SortStrategy {
    @Override
    public void sort(int[] array) { /* Quick sort */ }
}

// Context
public class DataProcessor {
    private SortStrategy strategy;

    public DataProcessor(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy; // Can change at runtime!
    }

    public void process(int[] data) {
        strategy.sort(data); // Delegates to strategy
    }
}

// Real-world: Payment processing
public interface DiscountStrategy {
    double applyDiscount(double price);
}

public class NoDiscount implements DiscountStrategy {
    @Override public double applyDiscount(double price) { return price; }
}

public class PercentageDiscount implements DiscountStrategy {
    private double percent;
    public PercentageDiscount(double percent) { this.percent = percent; }
    @Override public double applyDiscount(double price) { return price * (1 - percent/100); }
}
```

---

### Q6. Observer Pattern

**Definition**: One-to-many dependency — when one object changes state, all dependents are notified automatically.

```java
// Observer interface
public interface EventListener {
    void onEvent(String eventType, Object data);
}

// Subject (Observable)
public class OrderService {
    private final List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void placeOrder(Order order) {
        // Business logic
        processOrder(order);
        
        // Notify all listeners
        notifyListeners("ORDER_PLACED", order);
    }

    private void notifyListeners(String event, Object data) {
        listeners.forEach(l -> l.onEvent(event, data));
    }
}

// Concrete observers
public class EmailNotificationListener implements EventListener {
    @Override
    public void onEvent(String eventType, Object data) {
        if ("ORDER_PLACED".equals(eventType)) {
            Order order = (Order) data;
            sendEmail(order.getCustomerEmail(), "Order confirmed: " + order.getId());
        }
    }
}

public class InventoryListener implements EventListener {
    @Override
    public void onEvent(String eventType, Object data) {
        if ("ORDER_PLACED".equals(eventType)) {
            Order order = (Order) data;
            deductInventory(order.getItems());
        }
    }
}

// Spring equivalent: @EventListener, ApplicationEventPublisher
```

**Real-world**: Spring Events, Kafka consumers, GUI event handlers, WebSocket notifications

---

### Q7. Adapter Pattern

**Definition**: Converts the interface of a class into another interface clients expect. Allows incompatible interfaces to work together.

```java
// Existing (Adaptee) - old XML payment API
public class LegacyPaymentSystem {
    public void makePayment(String xmlData) {
        System.out.println("Processing XML payment: " + xmlData);
    }
}

// Target interface - what our app expects
public interface ModernPaymentGateway {
    void processPayment(PaymentRequest request);
}

// Adapter
public class LegacyPaymentAdapter implements ModernPaymentGateway {
    private final LegacyPaymentSystem legacy;

    public LegacyPaymentAdapter(LegacyPaymentSystem legacy) {
        this.legacy = legacy;
    }

    @Override
    public void processPayment(PaymentRequest request) {
        // Convert JSON/object to XML format
        String xml = convertToXml(request);
        legacy.makePayment(xml); // Delegate to legacy
    }

    private String convertToXml(PaymentRequest request) {
        return "<payment><amount>" + request.getAmount() + "</amount></payment>";
    }
}
```

**Real-world**: Java's `Arrays.asList()`, `Collections.list()`, JPA adapters for different databases

---

### Q8. Decorator Pattern

**Definition**: Adds behavior to objects dynamically without modifying their class. Wraps an object to extend functionality.

```java
// Component interface
public interface Coffee {
    double getCost();
    String getDescription();
}

// Base component
public class SimpleCoffee implements Coffee {
    @Override public double getCost() { return 1.0; }
    @Override public String getDescription() { return "Coffee"; }
}

// Base decorator
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    public CoffeeDecorator(Coffee coffee) { this.coffee = coffee; }
}

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    @Override public double getCost() { return coffee.getCost() + 0.5; }
    @Override public String getDescription() { return coffee.getDescription() + ", Milk"; }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) { super(coffee); }
    @Override public double getCost() { return coffee.getCost() + 0.25; }
    @Override public String getDescription() { return coffee.getDescription() + ", Sugar"; }
}

// Usage
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new MilkDecorator(coffee); // Add milk again

System.out.println(coffee.getDescription()); // Coffee, Milk, Sugar, Milk
System.out.println(coffee.getCost()); // 2.25
```

**Real-world**: Java I/O Streams (`BufferedInputStream(new FileInputStream())`), Spring Security filter chain, HTTP request interceptors

---

### Q9. Proxy Pattern

**Definition**: Provides a surrogate that controls access to another object. Can add: lazy init, access control, logging, caching.

```java
// Subject interface
public interface UserService {
    User findById(Long id);
    void createUser(User user);
}

// Real implementation
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    
    @Override
    public User findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
    
    @Override
    public void createUser(User user) {
        repository.save(user);
    }
}

// Caching Proxy
public class CachingUserServiceProxy implements UserService {
    private final UserService realService;
    private final Map<Long, User> cache = new ConcurrentHashMap<>();

    public CachingUserServiceProxy(UserService realService) {
        this.realService = realService;
    }

    @Override
    public User findById(Long id) {
        return cache.computeIfAbsent(id, key -> realService.findById(key));
    }

    @Override
    public void createUser(User user) {
        realService.createUser(user);
        cache.put(user.getId(), user); // Update cache
    }
}

// In Spring: This is what @Transactional, @Cacheable do under the hood!
// Spring creates a proxy of your service bean
```

**Real-world**: Spring AOP (all `@Transactional`, `@Cacheable`, `@Async` beans are proxies), JDK Dynamic Proxy, CGLIB Proxy

---

*Next: [Part 5 - Spring Framework](./Part-05-Spring-Framework.md)*
