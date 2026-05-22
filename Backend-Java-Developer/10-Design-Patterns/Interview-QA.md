# ❓ Design Patterns - Câu Hỏi Phỏng Vấn

---

### Q1: Singleton Pattern là gì? Khi nào dùng?

**Singleton**: Đảm bảo chỉ có 1 instance của class trong suốt vòng đời ứng dụng.

**Khi dùng:** Logger, Config Manager, Database connection pool, Thread pool.

**Thread-safe implementation:**
```java
// Double-checked locking (Java 5+)
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Enum (BEST - automatic thread-safe, serialization-safe)
public enum Singleton {
    INSTANCE;
    public void doSomething() { ... }
}
```

---

### Q2: Factory Method vs Abstract Factory?

**Factory Method:**
- 1 method tạo 1 loại product
- Subclass quyết định class nào được tạo
- VD: `NotificationFactory.createNotification("email")`

**Abstract Factory:**
- Tạo family of related objects
- VD: `UIFactory.createButton()`, `UIFactory.createCheckbox()` → Windows/Mac family

---

### Q3: Builder Pattern khi nào dùng?

Khi object có nhiều optional parameters:
```java
// Thay thế telescoping constructor
Person person = new Person.Builder("Alice", "Nguyen")
    .age(25)
    .email("alice@example.com")
    .phone("0901234567")
    .build();
```

---

### Q4: Observer Pattern là gì?

One-to-many dependency: khi subject thay đổi, tất cả observers được notify.

**Ví dụ thực tế:** Event handling, pub/sub messaging, MVC (Model → View), Spring `ApplicationEventPublisher`.

---

### Q5: Strategy Pattern là gì?

Định nghĩa family of algorithms, cho phép swap tại runtime.

**Ví dụ:** Payment (CreditCard/PayPal/Crypto), Sort algorithms, Compression methods.

---

### Q6: Decorator Pattern vs Inheritance?

**Inheritance**: Thêm behavior tại compile time, rigid  
**Decorator**: Thêm behavior tại runtime, flexible, không thay đổi class gốc

VD: Java I/O streams dùng Decorator:
```java
BufferedReader reader = new BufferedReader(
    new InputStreamReader(
        new FileInputStream("file.txt")
    )
);
```

---

### Q7: DAO Pattern là gì?

**DAO (Data Access Object)**: Tách data access logic khỏi business logic.

```
Controller → Service → DAO Interface → DAO Implementation → Database
```

Lợi ích: Thay đổi DB (MySQL → PostgreSQL) chỉ cần thay DAO implementation.

---

### Q8: Template Method Pattern?

Định nghĩa skeleton của algorithm trong base class, để subclass fill in specifics.

**VD:** `JdbcTemplate` (Spring) - xử lý open/close connection, bạn chỉ cần cung cấp SQL và row mapper.

---

### Q9: Adapter Pattern?

Cho phép incompatible interfaces làm việc cùng nhau. Convert interface của class thành interface mà client mong đợi.

**Ví dụ:** Wrapper cho legacy API, third-party library integration.

---

### Q10: SOLID Principles?

- **S** - Single Responsibility: Class chỉ có 1 lý do để thay đổi
- **O** - Open/Closed: Open for extension, closed for modification
- **L** - Liskov Substitution: Subclass phải thay thế được parent
- **I** - Interface Segregation: Nhiều interface nhỏ tốt hơn 1 interface lớn
- **D** - Dependency Inversion: Depend on abstractions, not concretions

---

*📌 Xem tiếp: [Practice-Exercises.md](Practice-Exercises.md)*
