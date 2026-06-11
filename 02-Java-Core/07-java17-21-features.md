# ☕ Java 17/21 Modern Features

> **Phase:** 1 | **Time Block:** 22:00-22:30  
> **Quan trọng cho:** NAB, Money Forward, TymeX — yêu cầu modern Java

---

## 1. Java 17 Features (LTS)

### Sealed Classes
```java
// Restrict which classes can extend/implement
public sealed class Shape permits Circle, Rectangle, Triangle {
    public abstract double area();
}

public final class Circle extends Shape {
    private final double radius;
    public Circle(double radius) { this.radius = radius; }
    public double area() { return Math.PI * radius * radius; }
}

public final class Rectangle extends Shape {
    private final double width, height;
    public Rectangle(double w, double h) { this.width = w; this.height = h; }
    public double area() { return width * height; }
}

public non-sealed class Triangle extends Shape {
    // non-sealed: anyone can extend Triangle
    private final double base, height;
    public Triangle(double b, double h) { this.base = b; this.height = h; }
    public double area() { return 0.5 * base * height; }
}
```

### Records (Java 16+)
```java
// Immutable data carrier — replaces boilerplate POJO
public record UserDTO(Long id, String name, String email) {
    // Compact constructor for validation
    public UserDTO {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        email = email.toLowerCase();
    }

    // Can add custom methods
    public String displayName() {
        return name + " <" + email + ">";
    }
}

// Usage
var user = new UserDTO(1L, "Phuc", "phuc@gihot.com");
user.name();  // "Phuc" — no get prefix
user.email(); // "phuc@gihot.com"

// Automatically provides: constructor, getters, equals(), hashCode(), toString()
```

### Pattern Matching for instanceof
```java
// Old way
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// Java 16+ pattern matching
if (obj instanceof String s) {
    System.out.println(s.length());  // s already cast
}

// With logical operators
if (obj instanceof String s && s.length() > 5) {
    process(s);
}
```

### Switch Expressions (Java 14+)
```java
// Old switch
String result;
switch (day) {
    case MONDAY: case FRIDAY: result = "Work hard"; break;
    case SATURDAY: case SUNDAY: result = "Rest"; break;
    default: result = "Normal"; break;
}

// New switch expression
String result = switch (day) {
    case MONDAY, FRIDAY -> "Work hard";
    case SATURDAY, SUNDAY -> "Rest";
    default -> "Normal";
};

// Pattern matching in switch (Java 21)
String describe(Object obj) {
    return switch (obj) {
        case Integer i when i > 0 -> "Positive int: " + i;
        case Integer i            -> "Non-positive int: " + i;
        case String s             -> "String of length " + s.length();
        case null                 -> "null";
        default                   -> "Unknown: " + obj;
    };
}
```

### Text Blocks (Java 15+)
```java
String json = """
    {
        "name": "Phuc",
        "role": "Backend Developer",
        "skills": ["Java", "C++", "Spring Boot"]
    }
    """;

String sql = """
    SELECT u.id, u.name, w.balance
    FROM users u
    JOIN wallets w ON u.id = w.user_id
    WHERE u.status = 'ACTIVE'
    ORDER BY w.balance DESC
    """;
```

---

## 2. Java 21 Features (LTS)

### Virtual Threads (Project Loom) ⭐
```java
// Traditional platform threads — expensive (1MB stack each)
ExecutorService executor = Executors.newFixedThreadPool(200);  // Limited pool

// Virtual Threads — lightweight (few KB each), millions possible
ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

// Create individual virtual thread
Thread.startVirtualThread(() -> {
    // This runs on a virtual thread
    var result = httpClient.send(request, BodyHandlers.ofString());
    process(result);
});

// Spring Boot 3.2+ — enable virtual threads
// application.yml
// spring.threads.virtual.enabled: true

// Why it matters for backend:
// - Traditional: 200 threads = 200 concurrent DB connections
// - Virtual: 10,000+ concurrent requests with few platform threads
// - I/O-bound work (DB calls, HTTP calls) benefits most
// - CPU-bound work: no benefit (still limited by cores)
```

### When Virtual Threads DON'T Help
```java
// ❌ CPU-bound — no benefit
virtualExecutor.submit(() -> {
    // Pure computation — still bound by CPU cores
    fibonacci(1000000);
});

// ❌ synchronized blocks — can pin virtual thread
synchronized (lock) {
    // Blocks carrier thread! Use ReentrantLock instead
    dbCall();
}

// ✅ Fix: Use ReentrantLock
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    dbCall();
} finally {
    lock.unlock();
}
```

### Sequenced Collections
```java
// Java 21 — consistent first/last element access
SequencedCollection<String> list = new ArrayList<>(List.of("A", "B", "C"));
list.getFirst();    // "A"
list.getLast();     // "C"
list.addFirst("Z"); // ["Z", "A", "B", "C"]
list.reversed();    // reversed view: ["C", "B", "A", "Z"]

SequencedMap<String, Integer> map = new LinkedHashMap<>();
map.put("one", 1);
map.put("two", 2);
map.firstEntry();  // "one" = 1
map.lastEntry();   // "two" = 2
```

### Record Patterns (Java 21)
```java
record Point(int x, int y) {}
record Line(Point start, Point end) {}

// Nested pattern matching
void printLine(Object obj) {
    if (obj instanceof Line(Point(var x1, var y1), Point(var x2, var y2))) {
        System.out.printf("Line from (%d,%d) to (%d,%d)%n", x1, y1, x2, y2);
    }
}

// In switch
String describe(Shape shape) {
    return switch (shape) {
        case Circle(var r) when r > 10 -> "Big circle";
        case Circle(var r) -> "Small circle with radius " + r;
        case Rectangle(var w, var h) -> "Rectangle " + w + "x" + h;
        default -> "Other shape";
    };
}
```

---

## 3. So sánh Java Versions

| Feature | Java 8 | Java 11 | Java 17 | Java 21 |
|:--------|:-------|:--------|:--------|:--------|
| Lambda/Stream | ✅ | ✅ | ✅ | ✅ |
| var (local) | ❌ | ✅ | ✅ | ✅ |
| Text blocks | ❌ | ❌ | ✅ | ✅ |
| Records | ❌ | ❌ | ✅ | ✅ |
| Sealed classes | ❌ | ❌ | ✅ | ✅ |
| Pattern matching | ❌ | ❌ | Preview | ✅ |
| Virtual threads | ❌ | ❌ | ❌ | ✅ |
| Switch expressions | ❌ | ❌ | ✅ | ✅ |
| Sequenced Collections | ❌ | ❌ | ❌ | ✅ |

---

## Câu Hỏi Phỏng Vấn

### Q1: Virtual Threads vs Platform Threads?
**A:** Platform threads map 1:1 với OS threads (~1MB stack mỗi thread, giới hạn ~thousands). Virtual threads là lightweight (~few KB), managed bởi JVM, có thể tạo millions. Khi virtual thread gặp I/O blocking (DB call, HTTP), JVM unmount nó khỏi carrier thread → carrier thread serve virtual thread khác. Benefit: massive concurrency cho I/O-bound workloads. Không benefit cho CPU-bound.

### Q2: Records khác gì với Lombok @Data?
**A:** Records là immutable by design (final fields), built-in compiler support (equals, hashCode, toString), transparent semantics. Lombok is annotation processing, mutable, thêm dependency. Records tốt cho DTOs, Value Objects. Nhưng Records không extend class khác (chỉ implement interfaces), không có setter.

### Q3: Sealed classes giải quyết vấn đề gì?
**A:** Restrict class hierarchy — compiler biết tất cả subclasses → exhaustive pattern matching trong switch (không cần default case). Domain modeling tốt hơn: `PaymentStatus sealed permits Pending, Completed, Failed, Refunded`.

### Q4: Khi nào nên migrate từ Java 8 lên 17/21?
**A:** Migrate khi: cần performance (GC improvements, virtual threads), modern features giảm boilerplate, security updates. Challenges: library compatibility, Jakarta EE namespace (javax → jakarta), removed modules (JAXB, Java EE). Approach: 8 → 11 → 17 → 21 theo từng bước.
