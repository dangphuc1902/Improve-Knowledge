# PART 4 — OOP & Design Patterns

---

## 🔷 OOP Pillars

### Q1: Encapsulation

**Definition:** Bundling data (fields) and behavior (methods) together, and hiding internal state from outside.

**Real-world example:** A bank account — you can't directly modify `balance`. You must use `deposit()` / `withdraw()` which validate the operation.

```java
public class BankAccount {
    private double balance;  // hidden

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount");
        balance += amount;
    }

    public double getBalance() { return balance; }
}
```

**Benefits:**
- Control over data validation
- Reduce coupling
- Easier to change internals without affecting callers

---

### Q2: Inheritance

**Definition:** A class (child) inherits fields and methods from another class (parent).

```java
abstract class Animal {
    String name;
    abstract void makeSound();

    void breathe() { System.out.println("Breathing..."); }
}

class Dog extends Animal {
    @Override
    void makeSound() { System.out.println("Woof!"); }
}
```

**Follow-up:** "Why Java doesn't support multiple inheritance with classes?"
> To avoid the **Diamond Problem**. Java supports multiple inheritance through **interfaces** (default methods with explicit resolution).

---

### Q3: Polymorphism

**Definition:** Same interface, different behavior. Two types:
- **Compile-time (method overloading)**: Same method name, different parameters
- **Runtime (method overriding)**: Subclass provides specific implementation

```java
// Runtime polymorphism
Animal a = new Dog();  // reference is Animal, object is Dog
a.makeSound();         // calls Dog's makeSound() → "Woof!"

// This is how Spring's DI works — you code to interface
PaymentService service = new StripePaymentService();
```

---

### Q4: Abstraction

**Definition:** Hide *how* something works, expose only *what* it does.

```java
// Interface — pure abstraction
interface PaymentGateway {
    void charge(String userId, double amount);
    void refund(String transactionId);
}

// Implementation hides complexity
class StripePaymentGateway implements PaymentGateway {
    public void charge(String userId, double amount) {
        // Stripe API calls, error handling, retry logic...
    }
}
```

**Abstract Class vs Interface:**
| | Abstract Class | Interface |
|---|---------------|-----------|
| State | Can have fields | No state (default in Java 9+) |
| Constructor | Yes | No |
| Inheritance | Single | Multiple |
| Use when | Shared base behavior | Contract/capability |

---

## 🔷 Design Patterns

### Q5: Singleton Pattern

**Intent:** Ensure only one instance of a class exists.

**Thread-safe implementation (Double-Checked Locking):**
```java
public class DatabasePool {
    private static volatile DatabasePool instance;

    private DatabasePool() {}  // private constructor

    public static DatabasePool getInstance() {
        if (instance == null) {
            synchronized (DatabasePool.class) {
                if (instance == null) {
                    instance = new DatabasePool();
                }
            }
        }
        return instance;
    }
}
```

**Enum Singleton (Best practice):**
```java
public enum AppConfig {
    INSTANCE;

    private final String dbUrl = "jdbc:mysql://localhost/app";

    public String getDbUrl() { return dbUrl; }
}
```

**Real-world:** Spring beans are singletons by default. `ApplicationContext`, `EntityManagerFactory`, DB connection pools.

**Follow-up:** "How to break Singleton?"
> Reflection, Serialization, Cloning. Fix with enum-based singleton or override `readResolve()`.

---

### Q6: Factory Method Pattern

**Intent:** Define an interface for creating objects, but let subclasses decide which class to instantiate.

```java
// Abstract creator
abstract class NotificationFactory {
    abstract Notification create(String type);

    void sendNotification(String message) {
        Notification n = create("email");
        n.send(message);
    }
}

// Concrete creator
class EmailNotificationFactory extends NotificationFactory {
    @Override
    Notification create(String type) {
        return new EmailNotification();
    }
}
```

**Real-world:** `Calendar.getInstance()`, `NumberFormat.getInstance()`, Spring's `BeanFactory`.

---

### Q7: Abstract Factory Pattern

**Intent:** Factory of Factories — create families of related objects without specifying concrete classes.

```java
// Family: UI components per OS
interface UIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

class WindowsUIFactory implements UIFactory {
    public Button createButton() { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class MacUIFactory implements UIFactory {
    public Button createButton() { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}
```

**Difference from Factory Method:** Factory Method creates ONE product. Abstract Factory creates a FAMILY of related products.

---

### Q8: Builder Pattern

**Intent:** Construct complex objects step by step. Useful when a class has many optional fields.

```java
public class UserRequest {
    private final String username;  // required
    private final String email;     // required
    private final String phone;     // optional
    private final String address;   // optional

    private UserRequest(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    public static class Builder {
        private String username;
        private String email;
        private String phone;
        private String address;

        public Builder username(String u) { this.username = u; return this; }
        public Builder email(String e) { this.email = e; return this; }
        public Builder phone(String p) { this.phone = p; return this; }
        public Builder address(String a) { this.address = a; return this; }

        public UserRequest build() { return new UserRequest(this); }
    }
}

// Usage
UserRequest req = new UserRequest.Builder()
    .username("phuc")
    .email("phuc@example.com")
    .phone("0909123456")
    .build();
```

**Real-world:** `StringBuilder`, Lombok's `@Builder`, `HttpRequest.newBuilder()`.

---

### Q9: Strategy Pattern

**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

```java
// Strategy interface
interface SortStrategy {
    void sort(int[] data);
}

class QuickSort implements SortStrategy {
    public void sort(int[] data) { /* quick sort */ }
}

class MergeSort implements SortStrategy {
    public void sort(int[] data) { /* merge sort */ }
}

// Context
class DataProcessor {
    private SortStrategy strategy;

    public DataProcessor(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void process(int[] data) {
        strategy.sort(data);
    }
}

// Usage
DataProcessor p = new DataProcessor(new QuickSort());
p.process(data);
```

**Real-world:** Java's `Comparator`, Spring Security's `AuthenticationStrategy`, payment processing (StripeStrategy, PayPalStrategy).

---

### Q10: Observer Pattern

**Intent:** When one object changes state, all dependents are notified automatically. (Event system, pub/sub)

```java
interface Observer {
    void update(String event);
}

class EventBus {
    private List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer o) { observers.add(o); }
    public void unsubscribe(Observer o) { observers.remove(o); }

    public void publish(String event) {
        observers.forEach(o -> o.update(event));
    }
}
```

**Real-world:** Kafka consumers, Spring `ApplicationEvent`, GUI listeners.

---

### Q11: Adapter Pattern

**Intent:** Convert the interface of a class into another interface clients expect. Allows incompatible interfaces to work together.

```java
// Target interface (what our system expects)
interface JsonParser {
    Map<String, Object> parse(String input);
}

// Adaptee (3rd party library we can't change)
class XmlLibrary {
    public Document parseXml(String xml) { /* ... */ return new Document(); }
}

// Adapter
class XmlToJsonAdapter implements JsonParser {
    private XmlLibrary xmlLib = new XmlLibrary();

    @Override
    public Map<String, Object> parse(String input) {
        Document doc = xmlLib.parseXml(input);
        return convertDocumentToMap(doc); // transform
    }
}
```

**Real-world:** Spring's `HandlerAdapter`, JDBC drivers, legacy system integration.

---

### Q12: Proxy Pattern

**Intent:** Provide a surrogate or placeholder for another object to control access.

**Three types:**
1. **Virtual Proxy**: Lazy initialization (expensive objects)
2. **Protection Proxy**: Access control
3. **Remote Proxy**: Represents object in another address space (RMI, gRPC stub)

```java
interface UserService {
    User getUser(int id);
}

// Caching proxy
class CachingUserServiceProxy implements UserService {
    private UserService real;
    private Map<Integer, User> cache = new HashMap<>();

    public CachingUserServiceProxy(UserService real) { this.real = real; }

    @Override
    public User getUser(int id) {
        return cache.computeIfAbsent(id, real::getUser);
    }
}
```

**Real-world:** Spring AOP is based on Proxy (JDK Dynamic Proxy or CGLIB). Spring Data JPA repository implementations are proxies.

---

### Q13: Decorator Pattern

**Intent:** Attach additional responsibilities to an object dynamically, without modifying the original class.

```java
interface Coffee {
    double getCost();
    String getDescription();
}

class SimpleCoffee implements Coffee {
    public double getCost() { return 1.0; }
    public String getDescription() { return "Coffee"; }
}

class MilkDecorator implements Coffee {
    private Coffee coffee;
    public MilkDecorator(Coffee c) { this.coffee = c; }
    public double getCost() { return coffee.getCost() + 0.5; }
    public String getDescription() { return coffee.getDescription() + ", Milk"; }
}

// Usage
Coffee c = new MilkDecorator(new SimpleCoffee());
// → "Coffee, Milk", cost = 1.5
```

**Real-world:** Java I/O streams (`BufferedReader(new FileReader(...))`), Spring's `HttpServletRequestWrapper`.

---

## 📋 Design Pattern Quick Reference

| Pattern | Category | One-line Summary |
|---------|----------|-----------------|
| Singleton | Creational | One instance globally |
| Factory Method | Creational | Let subclass decide which object to create |
| Abstract Factory | Creational | Create families of related objects |
| Builder | Creational | Construct complex objects step-by-step |
| Strategy | Behavioral | Swap algorithm at runtime |
| Observer | Behavioral | Notify all dependents on state change |
| Adapter | Structural | Convert incompatible interfaces |
| Decorator | Structural | Add behavior without modifying class |
| Proxy | Structural | Control access to another object |
