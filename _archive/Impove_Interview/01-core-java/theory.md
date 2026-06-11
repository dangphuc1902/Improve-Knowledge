# 01 - Core Java Advanced

## 📖 Tổng quan

**Core Java** là nền tảng bắt buộc trong mọi buổi phỏng vấn Java. Với background C++/Java thuần, bạn cần tập trung vào các đặc trưng **Java-specific** mà C++ không có: GC, Collections framework, Concurrency utilities, và Java 8+ functional programming.

---

## 🧠 Kiến thức cốt lõi

### OOP trong Java

| Khái niệm | Mô tả | Ví dụ Java |
|-----------|-------|------------|
| Encapsulation | Ẩn dữ liệu qua `private` + getter/setter | `private int balance;` |
| Inheritance | `extends`, `super`, constructor chaining | `class SavingsAccount extends Account` |
| Polymorphism | Override (`@Override`) vs Overload | Runtime vs Compile-time |
| Abstraction | `abstract class` + `interface` | Template Pattern |

```
interface vs abstract class:
- interface: multiple inheritance, all public, no state (Java 8: default/static methods)
- abstract: single inheritance, có state (fields), có constructor
→ Rule: "IS-A" + shared state → abstract; "CAN-DO" → interface
```

### Java Memory Model

```
┌──────────────────────────────────────────┐
│                  JVM Memory              │
│  ┌───────────┐  ┌──────────────────────┐ │
│  │  Stack    │  │        Heap          │ │
│  │ (per thread)│ │  ┌────────────────┐ │ │
│  │ - local   │  │  │  Young Gen     │ │ │
│  │   vars    │  │  │  (Eden+S0+S1)  │ │ │
│  │ - method  │  │  ├────────────────┤ │ │
│  │   frames  │  │  │  Old Gen       │ │ │
│  └───────────┘  │  └────────────────┘ │ │
│                 └──────────────────────┘ │
│  ┌───────────────────────────────────┐   │
│  │  Metaspace (class metadata, static)│  │
│  └───────────────────────────────────┘   │
└──────────────────────────────────────────┘
```

### String Pool

```java
String s1 = "hello";          // String Pool (Heap)
String s2 = "hello";          // Same reference từ Pool
String s3 = new String("hello"); // New object in Heap

s1 == s2   // true  (same pool reference)
s1 == s3   // false (different object)
s1.equals(s3) // true (same content)
s3.intern() == s1 // true (intern() đưa về pool)
```

### Collections Framework

```
Collection
├── List (ordered, duplicates allowed)
│   ├── ArrayList   — O(1) get, O(n) insert/delete
│   ├── LinkedList  — O(n) get, O(1) insert/delete at head/tail
│   └── Vector      — thread-safe ArrayList (legacy, dùng CopyOnWriteArrayList)
├── Set (no duplicates)
│   ├── HashSet        — O(1), no order
│   ├── LinkedHashSet  — O(1), insertion order
│   └── TreeSet        — O(log n), sorted order
└── Queue
    ├── PriorityQueue  — min-heap mặc định
    ├── ArrayDeque     — double-ended queue
    └── LinkedList     — cũng là Queue

Map (key-value)
├── HashMap           — O(1), no order, null key OK
├── LinkedHashMap     — O(1), insertion/access order
├── TreeMap           — O(log n), sorted by key
├── Hashtable         — thread-safe (legacy, không dùng)
└── ConcurrentHashMap — thread-safe, segment locking
```

### HashMap Internal Working

```java
// Java 8+: Array of Node (LinkedList → Red-Black Tree khi > 8 nodes)
// hash(key) → index = hash & (capacity - 1)
// Load factor mặc định = 0.75, capacity = 16 → resize khi 12 entries

// Collision: chaining (linked list → tree nếu > 8)
// equals() + hashCode() PHẢI override đồng thời!

@Override
public int hashCode() {
    return Objects.hash(id, name); // Dùng Objects.hash() để tránh null NPE
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee)) return false;
    Employee e = (Employee) o;
    return id == e.id && Objects.equals(name, e.name);
}
```

---

## 🔍 Concurrency & Multithreading

### Thread Creation

```java
// Cách 1: Extends Thread (không recommended — class bị khóa inheritance)
class MyThread extends Thread {
    public void run() { System.out.println("Running"); }
}

// Cách 2: Implements Runnable (recommended cho task không có return)
Runnable task = () -> System.out.println("Running");
new Thread(task).start();

// Cách 3: Callable + Future (có return value + checked exception)
Callable<Integer> callable = () -> 42;
ExecutorService executor = Executors.newFixedThreadPool(4);
Future<Integer> future = executor.submit(callable);
int result = future.get(); // blocking call
```

### ExecutorService

```java
// Thread Pool types:
ExecutorService fixed  = Executors.newFixedThreadPool(4);      // Fixed threads
ExecutorService cached = Executors.newCachedThreadPool();      // Dynamic, unbounded
ExecutorService single = Executors.newSingleThreadExecutor();  // 1 thread, sequential
ScheduledExecutorService sched = Executors.newScheduledThreadPool(2);

// LUÔN shutdown sau khi dùng:
executor.shutdown();
executor.awaitTermination(5, TimeUnit.SECONDS);
```

### CompletableFuture (Java 8+)

```java
// Async pipeline — quan trọng với backend hiện đại
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchUserFromDB(userId))     // async task
    .thenApply(user -> user.getName())              // transform result
    .thenApplyAsync(name -> callExternalService(name)) // async transform
    .exceptionally(ex -> "default-value");          // error handling

// Combine 2 futures:
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "World");
CompletableFuture<String> combined = f1.thenCombine(f2, (a, b) -> a + " " + b);
```

### Synchronization

```java
// synchronized — intrinsic lock (mutex)
public synchronized void deposit(int amount) { balance += amount; } // method lock
synchronized (this) { balance += amount; }                          // block lock

// volatile — visibility guarantee (no caching in CPU register)
private volatile boolean running = true; // đọc từ main memory, không cache

// ReentrantLock — explicit lock (tryLock, lockInterruptibly, fairness)
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try { balance += amount; }
finally { lock.unlock(); } // LUÔN unlock trong finally

// ReentrantReadWriteLock — multiple readers OR one writer
ReadWriteLock rwLock = new ReentrantReadWriteLock();
rwLock.readLock().lock();   // nhiều thread đọc cùng lúc OK
rwLock.writeLock().lock();  // chỉ 1 thread ghi
```

### Deadlock

```java
// Deadlock: Thread A giữ lock1, chờ lock2; Thread B giữ lock2, chờ lock1
// Tránh: luôn acquire locks theo CÙNG THỨ TỰ

// ❌ Deadlock prone:
Thread A: lock(account1) → lock(account2)
Thread B: lock(account2) → lock(account1)

// ✅ Fix: consistent ordering
void transfer(Account from, Account to) {
    Account first  = from.getId() < to.getId() ? from : to;
    Account second = from.getId() < to.getId() ? to   : from;
    synchronized(first) { synchronized(second) { /*...*/ } }
}
```

---

## 📝 Java 8+ Features

### Stream API

```java
List<Employee> employees = getEmployees();

// filter + map + collect
List<String> seniorNames = employees.stream()
    .filter(e -> e.getYears() > 5)
    .sorted(Comparator.comparing(Employee::getSalary).reversed())
    .map(Employee::getName)
    .collect(Collectors.toList());

// groupingBy — dùng nhiều trong banking reports
Map<Department, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// reduce
double totalSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .sum(); // hoặc .reduce(0, Double::sum)
```

### Optional

```java
// Tránh NullPointerException
Optional<User> user = userRepository.findById(id);

// ❌ Sai cách dùng:
if (user.isPresent()) { return user.get().getName(); } // không tốt hơn null check

// ✅ Đúng cách:
return user
    .map(User::getName)
    .orElse("Anonymous");          // default value
    .orElseGet(() -> computeName()) // lazy default
    .orElseThrow(() -> new UserNotFoundException(id)); // throw exception
```

---

## ⏱️ So Sánh Collections

| | ArrayList | LinkedList | HashMap | TreeMap |
|--|-----------|------------|---------|---------|
| Access | O(1) | O(n) | O(1) avg | O(log n) |
| Insert end | O(1) amort | O(1) | O(1) avg | O(log n) |
| Insert mid | O(n) | O(1)* | - | - |
| Thread-safe? | ❌ | ❌ | ❌ | ❌ |
| Ordered? | Index | Index | ❌ | By key |

*với reference đến node

---

## 💡 Tips Phỏng Vấn

1. **HashMap**: Luôn nhắc đến `hashCode()` + `equals()` contract, resize, treeification (Java 8)
2. **String immutability**: Tại sao String immutable? → thread-safe, string pool, hashCode caching
3. **Deadlock 4 conditions**: Mutual exclusion, Hold & wait, No preemption, Circular wait
4. **`volatile` vs `synchronized`**: volatile chỉ visibility, synchronized cả visibility + atomicity
5. **Bank-specific**: Luôn dùng `BigDecimal` cho tiền, KHÔNG dùng `double`/`float`

```java
// ✅ Banking: dùng BigDecimal
BigDecimal amount = new BigDecimal("1234567.89");
BigDecimal fee = amount.multiply(new BigDecimal("0.001"));
BigDecimal total = amount.add(fee).setScale(2, RoundingMode.HALF_UP);
```
