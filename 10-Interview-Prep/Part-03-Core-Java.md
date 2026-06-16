# PART 3 - CORE JAVA

> **Topics**: JVM · Memory Model · GC · String · Collections · Multithreading · Java 8

---

## JVM, JRE, JDK

### Q1. What is the difference between JDK, JRE, and JVM?

**Ideal Answer:**
```
JDK (Java Development Kit)
  └─ JRE (Java Runtime Environment)
       └─ JVM (Java Virtual Machine)

JDK = JRE + Development Tools (compiler javac, debugger, javadoc, etc.)
JRE = JVM + Class Libraries (java.lang, java.util, etc.)
JVM = Execution Engine (interprets/compiles bytecode to native machine code)
```

- **JDK**: What developers install to write and compile Java code
- **JRE**: What end-users install to *run* Java applications
- **JVM**: Abstract computing machine — loads, verifies, and executes `.class` files; provides platform independence

**Follow-up Questions:**
- What are the components inside the JVM? (ClassLoader, Execution Engine, Runtime Data Areas)
- Is JVM platform-independent? (The JVM itself is NOT — but Java bytecode IS)
- What does "Write Once, Run Anywhere" mean?

**Common Mistakes:**
- Saying JDK = compiler only (it's the full toolkit)
- Confusing JRE and JVM
- Not knowing that JVM performs Just-In-Time (JIT) compilation

---

### Q2. Explain JVM Architecture

**Ideal Answer:**

```
JVM Architecture
├── Class Loader Subsystem
│   ├── Bootstrap ClassLoader (loads java.lang, java.util)
│   ├── Extension ClassLoader (loads ext directory)
│   └── Application ClassLoader (loads classpath)
│
├── Runtime Data Areas
│   ├── Method Area (class metadata, static variables)
│   ├── Heap (objects, instance variables)
│   ├── Stack (method frames, local variables, per thread)
│   ├── PC Register (program counter, per thread)
│   └── Native Method Stack
│
└── Execution Engine
    ├── Interpreter (slow, line-by-line)
    ├── JIT Compiler (compiles hot code to native)
    └── Garbage Collector
```

**Follow-up Questions:**
- What is the difference between Heap and Stack?
- What is Metaspace? (replaced PermGen in Java 8)
- How does ClassLoader delegation model work?

---

## Memory Model

### Q3. Explain Java Heap and Stack

**Ideal Answer:**

| Feature | Heap | Stack |
|---|---|---|
| Stores | Objects, arrays, instance variables | Method frames, local variables, references |
| Thread sharing | Shared among all threads | Private per thread |
| Lifetime | Until GC collects | Until method returns |
| Error | OutOfMemoryError | StackOverflowError |
| Size | Larger, configurable (-Xmx) | Smaller, fixed |

```java
public void example() {
    int x = 10;               // Stack (primitive local variable)
    String name = "Java";     // Stack (reference), Heap (String object)
    Person p = new Person();  // Stack (reference p), Heap (Person object)
}
```

**Follow-up Questions:**
- What is StackOverflowError? (infinite recursion)
- What is OutOfMemoryError? (heap full)
- What is Metaspace? (class metadata storage, off-heap in Java 8+)

---

### Q4. How does Garbage Collection work in Java?

**Ideal Answer:**

**GC Algorithm Overview:**
```
Heap Structure:
├── Young Generation
│   ├── Eden Space (new objects created here)
│   ├── Survivor S0
│   └── Survivor S1
└── Old Generation (long-lived objects)

GC Process:
1. Minor GC: Runs on Young Gen (Eden → Survivor → Old)
2. Major/Full GC: Runs on Old Gen + Young Gen
```

**Common GC Algorithms:**
- **Serial GC**: Single-threaded, small apps
- **Parallel GC**: Multi-threaded, throughput-focused
- **G1 GC** (default Java 9+): Predictable pause times, region-based
- **ZGC / Shenandoah**: Low latency, sub-millisecond pauses (Java 15+)

**GC Roots**: Objects held by GC roots (static fields, active threads, local variables) are NOT collected.

**Follow-up Questions:**
- What is Stop-the-World (STW)?
- How do you tune GC? (-Xms, -Xmx, -XX:+UseG1GC)
- What is a memory leak in Java? (objects still referenced but no longer needed)

**Common Mistakes:**
- Thinking `System.gc()` guarantees GC runs
- Not understanding generational hypothesis
- Confusing GC with memory deallocation in C/C++

---

## String

### Q5. Explain String Pool in Java

**Ideal Answer:**
```java
String a = "hello";          // Goes to String Pool (Heap)
String b = "hello";          // Returns same reference from Pool
String c = new String("hello"); // Creates new object in Heap (NOT pool)

System.out.println(a == b);  // true (same pool reference)
System.out.println(a == c);  // false (different references)
System.out.println(a.equals(c)); // true (same content)

// intern() moves a string to pool
String d = c.intern();
System.out.println(a == d);  // true
```

- String Pool is in Heap (since Java 7; was in PermGen before)
- String is **immutable** — thread-safe, can be safely shared
- Pool allows memory savings through string interning

**Follow-up Questions:**
- Why is String immutable?
- What is the benefit of immutability? (Thread safety, caching, security)
- When should you use `intern()`?

---

### Q6. String vs StringBuilder vs StringBuffer

**Ideal Answer:**

| Feature | String | StringBuilder | StringBuffer |
|---|---|---|---|
| Mutability | Immutable | Mutable | Mutable |
| Thread Safety | Yes (immutable) | No | Yes (synchronized) |
| Performance | Slow for concatenation | Fast | Slower than SB |
| Use Case | Fixed strings | Single-thread string building | Multi-thread string building |

```java
// Bad — creates many intermediate String objects
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i; // Creates 1000 String objects!
}

// Good — single StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

**Common Mistakes:**
- Using String concatenation in loops (performance killer)
- Using StringBuffer when single-threaded (unnecessary synchronization overhead)

---

## Exception Handling

### Q7. Explain Exception Hierarchy in Java

**Ideal Answer:**
```
Throwable
├── Error (JVM errors, don't catch these)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
│
└── Exception
    ├── Checked Exceptions (must be caught or declared)
    │   ├── IOException
    │   ├── SQLException
    │   └── ClassNotFoundException
    │
    └── RuntimeException (Unchecked — don't need to declare)
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        ├── ClassCastException
        └── IllegalArgumentException
```

**Follow-up Questions:**
- What's the difference between checked and unchecked exceptions?
- When should you create a custom exception?
- What is the difference between `throw` and `throws`?
- What happens in finally block if return is in try?

```java
// Custom exception best practice
public class PaymentException extends RuntimeException {
    private final String errorCode;
    
    public PaymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() { return errorCode; }
}
```

---

## Collections

### Q8. List vs Set vs Map — When to Use Each?

**Ideal Answer:**

| Collection | Allows Duplicates | Ordered | Null | Use When |
|---|---|---|---|---|
| ArrayList | Yes | Insertion order | Yes | Random access, iteration |
| LinkedList | Yes | Insertion order | Yes | Frequent insert/delete |
| HashSet | No | No | One null | Unique elements, fast lookup |
| LinkedHashSet | No | Insertion order | One null | Unique + ordered |
| TreeSet | No | Sorted | No | Sorted unique elements |
| HashMap | Keys: No | No | One null key | Key-value, fast lookup |
| LinkedHashMap | Keys: No | Insertion order | Yes | Key-value + ordered |
| TreeMap | Keys: No | Sorted | No | Sorted key-value |

**Follow-up Questions:**
- What is the time complexity of HashMap operations?
- What is the difference between HashMap and Hashtable?
- When would you use LinkedList over ArrayList?

---

### Q9. How Does HashMap Work Internally?

**Ideal Answer:**
```
HashMap Internal Structure:
- Array of LinkedList buckets (Node<K,V>[])
- Default capacity: 16, Load factor: 0.75
- When 75% full → resize (double capacity, rehash)

Operations:
1. put(key, value):
   - hash = key.hashCode()
   - index = hash % capacity (using bitwise: hash & (n-1))
   - If no collision → store in bucket[index]
   - If collision → add to LinkedList at that bucket
   - Java 8+: if bucket size ≥ 8 → convert to Red-Black Tree (O(n) → O(log n))

2. get(key):
   - Compute index same way
   - Traverse LinkedList/Tree to find by equals()
```

```java
// Why override both hashCode AND equals?
// Contract: if a.equals(b) → a.hashCode() == b.hashCode()
public class Employee {
    private String id;
    
    @Override
    public int hashCode() {
        return Objects.hash(id);  // Must!
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        return Objects.equals(id, ((Employee) o).id);
    }
}
```

**Follow-up Questions:**
- What is hash collision?
- What is the difference between HashMap and ConcurrentHashMap?
- Why is load factor 0.75 the default?

---

### Q10. ConcurrentHashMap vs HashMap vs Hashtable

**Ideal Answer:**

| Feature | HashMap | Hashtable | ConcurrentHashMap |
|---|---|---|---|
| Thread Safety | No | Yes (full lock) | Yes (segment/bucket locking) |
| Performance | Fast | Slow | Fast (concurrent) |
| Null keys/values | Yes | No | No |
| Introduced | Java 1.2 | Java 1.0 | Java 5 |

**ConcurrentHashMap** uses:
- Java 7: Segment locking (16 segments by default)
- Java 8+: Bucket-level CAS (Compare-And-Swap) + synchronized on first element

**Follow-up Questions:**
- Is `computeIfAbsent()` atomic in ConcurrentHashMap? (Yes)
- When would you prefer CopyOnWriteArrayList?

---

## Multithreading

### Q11. What is a Race Condition and How Do You Prevent It?

**Ideal Answer:**
A **race condition** occurs when multiple threads access shared mutable state concurrently, and the result depends on the timing of their execution.

```java
// Race condition example
class Counter {
    private int count = 0;
    
    public void increment() {
        count++; // NOT atomic! Read-Modify-Write
    }
}

// Fix 1: synchronized
public synchronized void increment() { count++; }

// Fix 2: AtomicInteger (better performance)
private AtomicInteger count = new AtomicInteger(0);
public void increment() { count.incrementAndGet(); }

// Fix 3: Lock
private final ReentrantLock lock = new ReentrantLock();
public void increment() {
    lock.lock();
    try { count++; }
    finally { lock.unlock(); }
}
```

**Follow-up Questions:**
- What is a deadlock? How do you prevent it?
- What is volatile? When is it sufficient?
- What is the happens-before relationship?

---

### Q12. Explain ExecutorService and Thread Pools

**Ideal Answer:**
```java
// Types of thread pools
ExecutorService fixed = Executors.newFixedThreadPool(5);
ExecutorService cached = Executors.newCachedThreadPool();
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

// Best practice: Use ThreadPoolExecutor directly for control
ExecutorService executor = new ThreadPoolExecutor(
    2,                              // corePoolSize
    10,                             // maximumPoolSize
    60L, TimeUnit.SECONDS,          // keepAliveTime
    new ArrayBlockingQueue<>(100),  // workQueue
    new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
);

// Submit task
Future<String> future = executor.submit(() -> {
    return "Result";
});

// Always shutdown!
executor.shutdown();
executor.awaitTermination(10, TimeUnit.SECONDS);
```

**Rejection Policies:**
- `AbortPolicy`: Throws RejectedExecutionException (default)
- `CallerRunsPolicy`: Caller thread runs the task
- `DiscardPolicy`: Silently discard
- `DiscardOldestPolicy`: Discard oldest queued task

---

### Q13. CompletableFuture — Async Programming in Java 8+

**Ideal Answer:**
```java
// Basic async
CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
    return fetchUserFromDB(); // runs in ForkJoinPool
});

// Chain operations
CompletableFuture<String> result = CompletableFuture
    .supplyAsync(() -> fetchUser(userId))
    .thenApply(user -> enrichWithProfile(user))   // transform result
    .thenCompose(user -> fetchOrders(user.getId())) // flatMap
    .thenAccept(orders -> System.out.println(orders)) // consume
    .exceptionally(ex -> { log(ex); return null; }); // error handling

// Combine multiple futures
CompletableFuture<Void> all = CompletableFuture.allOf(cf1, cf2, cf3);
CompletableFuture<Object> any = CompletableFuture.anyOf(cf1, cf2, cf3);
```

**Follow-up Questions:**
- What is the difference between `thenApply` and `thenCompose`?
- How do you handle exceptions in CompletableFuture?
- What thread pool does CompletableFuture use by default?

---

## Java 8 Features

### Q14. Explain Lambda Expressions

**Ideal Answer:**
```java
// Lambda = anonymous function
// Syntax: (parameters) -> expression OR { body }

// Before Java 8
Comparator<String> old = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
};

// Java 8 Lambda
Comparator<String> lambda = (a, b) -> a.compareTo(b);

// Functional Interface - must have exactly ONE abstract method
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;

// Built-in Functional Interfaces
Function<String, Integer> strLen = String::length; // method reference
Predicate<String> isEmpty = String::isEmpty;
Consumer<String> printer = System.out::println;
Supplier<String> greeting = () -> "Hello";
```

---

### Q15. Stream API — Deep Dive

**Ideal Answer:**
```java
List<Employee> employees = getEmployees();

// Filter, transform, collect
List<String> seniorNames = employees.stream()
    .filter(e -> e.getYears() > 5)          // intermediate (lazy)
    .sorted(Comparator.comparing(Employee::getSalary))
    .map(Employee::getName)                  // intermediate (lazy)
    .collect(Collectors.toList());           // terminal (eager, triggers pipeline)

// Reduction
double avgSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .average()
    .orElse(0.0);

// Grouping
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// Parallel stream
long count = employees.parallelStream()
    .filter(e -> e.getSalary() > 50000)
    .count();

// flatMap (one-to-many)
List<String> skills = employees.stream()
    .flatMap(e -> e.getSkills().stream())
    .distinct()
    .collect(Collectors.toList());
```

**Intermediate Operations** (lazy): `filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`, `skip`
**Terminal Operations** (eager): `collect`, `count`, `reduce`, `forEach`, `findFirst`, `anyMatch`

---

### Q16. Optional — Avoiding NullPointerException

**Ideal Answer:**
```java
// Problem Optional solves
String city = user.getAddress().getCity(); // NPE if address is null

// With Optional
Optional<User> optUser = userRepository.findById(id);

// Bad usage - same as null check
if (optUser.isPresent()) {
    User user = optUser.get();
}

// Good usage - functional style
String city = optUser
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown City");

// orElseGet - lazy evaluation (prefer for expensive defaults)
String city2 = optUser
    .map(User::getAddress)
    .map(Address::getCity)
    .orElseGet(() -> fetchDefaultCity());

// orElseThrow
User user = optUser.orElseThrow(() -> new UserNotFoundException(id));
```

**Follow-up Questions:**
- Should Optional be used as method parameter? (No — anti-pattern)
- Should Optional be used in entity fields? (Generally no — not serializable)
- Difference between `orElse` and `orElseGet`? (`orElse` always evaluates, `orElseGet` is lazy)

---

*Next: [Part 4 - OOP & Design Patterns](./Part-04-OOP-Design-Patterns.md)*
