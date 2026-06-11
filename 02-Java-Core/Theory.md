# 📖 Java Nâng Cao - Collections, Generics & Concurrency

> **Tuần 2 | Mức độ: Quan trọng cho mọi Java Developer**

---

## 1. Java Collections Framework

```
java.util.Collection (interface)
    ├── List (ordered, allow duplicates)
    │     ├── ArrayList     - dynamic array, fast random access O(1)
    │     ├── LinkedList    - doubly linked list, fast insert/delete O(1)
    │     └── Vector        - synchronized ArrayList (legacy)
    ├── Set (no duplicates)
    │     ├── HashSet       - O(1) operations, no order
    │     ├── LinkedHashSet - maintains insertion order
    │     └── TreeSet       - sorted order (implements SortedSet)
    └── Queue
          ├── LinkedList    - also implements Deque
          ├── PriorityQueue - heap-based priority queue
          └── ArrayDeque    - efficient double-ended queue

java.util.Map (key-value pairs)
    ├── HashMap       - O(1) average, no order, allows null key
    ├── LinkedHashMap - maintains insertion/access order
    ├── TreeMap       - sorted by key (implements SortedMap)
    └── Hashtable     - synchronized HashMap (legacy)
```

---

## 2. List - ArrayList vs LinkedList

### ArrayList
```java
List<String> list = new ArrayList<>();
list.add("A");           // O(1) amortized
list.add(0, "B");        // O(n) - shift elements
list.get(0);             // O(1) - random access
list.remove(0);          // O(n) - shift elements
list.contains("A");      // O(n) - linear search

// Initialization
List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
List<String> fixed = List.of("A", "B", "C");  // Immutable (Java 9+)
```

### LinkedList
```java
LinkedList<String> linked = new LinkedList<>();
linked.addFirst("A");    // O(1)
linked.addLast("B");     // O(1)
linked.removeFirst();    // O(1)
linked.get(5);           // O(n) - no random access
```

**Khi nào dùng:**
- `ArrayList`: Truy cập ngẫu nhiên nhiều, ít insert/delete ở giữa
- `LinkedList`: Insert/delete ở đầu/giữa nhiều, dùng làm Queue/Stack

---

## 3. Set - Không Cho Duplicate

```java
// HashSet - O(1) average, no order
Set<String> hashSet = new HashSet<>();
hashSet.add("Banana");
hashSet.add("Apple");
hashSet.add("Apple");  // Ignored - duplicate
System.out.println(hashSet.size());  // 2

// LinkedHashSet - insertion order
Set<String> linked = new LinkedHashSet<>(Arrays.asList("C", "A", "B"));
// Iterates: C, A, B

// TreeSet - sorted order
Set<Integer> sorted = new TreeSet<>(Arrays.asList(5, 2, 8, 1, 9));
// Iterates: 1, 2, 5, 8, 9

// Custom sorting with TreeSet
Set<String> byLength = new TreeSet<>(Comparator.comparingInt(String::length));
```

---

## 4. Map - Key-Value Pairs

```java
// HashMap - most common
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);
scores.put("Charlie", 92);

// Access
scores.get("Alice");           // 95
scores.getOrDefault("Dave", 0); // 0 (not found)
scores.containsKey("Bob");     // true
scores.size();                 // 3

// Iteration
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    System.out.println(entry.getKey() + " = " + entry.getValue());
}
scores.forEach((k, v) -> System.out.println(k + " = " + v));

// Useful operations (Java 8+)
scores.putIfAbsent("Alice", 100);  // Không ghi đè nếu đã có
scores.computeIfAbsent("Dave", k -> 0);  // Compute nếu key chưa tồn tại
scores.merge("Alice", 5, Integer::sum);  // Alice = 95 + 5 = 100

// Group by (hay dùng)
Map<String, List<String>> grouped = names.stream()
    .collect(Collectors.groupingBy(name -> name.substring(0, 1)));
```

---

## 5. Queue & Deque

```java
// Queue - FIFO
Queue<String> queue = new LinkedList<>();
queue.offer("First");   // add to tail (returns false if full)
queue.offer("Second");
queue.poll();            // remove from head → "First"
queue.peek();            // look at head without removing → "Second"

// PriorityQueue - min-heap by default
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.add(5); pq.add(1); pq.add(3);
pq.poll();  // 1 (smallest first)

// Max-heap
PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Comparator.reverseOrder());

// Deque - double-ended queue (Stack + Queue)
Deque<String> deque = new ArrayDeque<>();
deque.push("A");   // stack push (adds to front)
deque.pop();       // stack pop (removes from front)
deque.offer("B");  // queue offer (adds to back)
deque.poll();      // queue poll (removes from front)
```

---

## 6. Java Generics

### Generic Class
```java
public class Pair<T, U> {
    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
}

Pair<String, Integer> pair = new Pair<>("Alice", 30);
```

### Generic Method
```java
public static <T extends Comparable<T>> T findMax(List<T> list) {
    if (list.isEmpty()) throw new IllegalArgumentException("Empty list");
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) max = item;
    }
    return max;
}

findMax(List.of(3, 1, 4, 1, 5, 9, 2, 6));  // 9
findMax(List.of("Banana", "Apple", "Cherry")); // "Cherry"
```

### Wildcards
```java
// Upper bounded wildcard: ? extends T - READ only
public double sumList(List<? extends Number> numbers) {
    return numbers.stream().mapToDouble(Number::doubleValue).sum();
}
// Có thể nhận: List<Integer>, List<Double>, List<Float>

// Lower bounded wildcard: ? super T - WRITE
public void addNumbers(List<? super Integer> list) {
    list.add(1); list.add(2); list.add(3);
}
// Có thể nhận: List<Integer>, List<Number>, List<Object>
```

---

## 7. Exception Handling

```java
// Hierarchy
Throwable
├── Error (JVM errors - don't catch)
│     ├── OutOfMemoryError
│     └── StackOverflowError
└── Exception
      ├── RuntimeException (Unchecked)
      │     ├── NullPointerException
      │     ├── ArrayIndexOutOfBoundsException
      │     ├── ClassCastException
      │     └── ArithmeticException
      └── Checked Exceptions
            ├── IOException
            ├── SQLException
            └── FileNotFoundException
```

```java
// Try-with-resources (Java 7+) - tự động đóng resource
try (Connection conn = DriverManager.getConnection(url);
     PreparedStatement ps = conn.prepareStatement(sql)) {
    ResultSet rs = ps.executeQuery();
    // use rs
} catch (SQLException e) {
    logger.error("DB error", e);
} finally {
    // Luôn chạy dù có exception hay không
}

// Custom Exception
public class InsufficientFundsException extends RuntimeException {
    private double amount;

    public InsufficientFundsException(double amount) {
        super("Insufficient funds. Short by: " + amount);
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}

// Multi-catch (Java 7+)
try {
    riskyOperation();
} catch (IOException | SQLException e) {
    handle(e);
}
```

---

## 8. Java I/O

```java
// Reading a file
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt", StandardCharsets.UTF_8))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

// Writing a file
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt", true))) {  // true = append
    writer.write("Hello World");
    writer.newLine();
}

// NIO.2 (Java 7+) - cleaner API
Path path = Paths.get("data.txt");
List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
Files.write(path, lines, StandardCharsets.UTF_8);

// Stream-based (Java 8+)
try (Stream<String> stream = Files.lines(path)) {
    stream.filter(line -> !line.isEmpty())
          .forEach(System.out::println);
}
```

---

## 9. Multithreading (Đa Luồng)

### Tạo Thread
```java
// Cách 1: Extend Thread
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + getName());
    }
}
new MyThread().start();

// Cách 2: Implement Runnable (preferred)
Runnable task = () -> System.out.println("Lambda thread: " + Thread.currentThread().getName());
new Thread(task).start();

// Cách 3: ExecutorService (best practice)
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> { /* task 1 */ });
executor.submit(() -> { /* task 2 */ });
executor.shutdown();
```

### Synchronization
```java
public class Counter {
    private int count = 0;

    // synchronized method - chỉ 1 thread dùng tại 1 thời điểm
    public synchronized void increment() {
        count++;
    }

    // synchronized block - fine-grained control
    public void increment2() {
        synchronized (this) {
            count++;
        }
    }
}

// AtomicInteger - thread-safe without synchronization
private AtomicInteger atomicCount = new AtomicInteger(0);
atomicCount.incrementAndGet();  // Thread-safe increment
atomicCount.compareAndSet(expected, newValue);  // CAS operation
```

### Concurrent Collections
```java
// Thread-safe alternatives
Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(100);

// BlockingQueue - used for Producer-Consumer pattern
blockingQueue.put("item");   // blocks if full
blockingQueue.take();        // blocks if empty
```

### Common Threading Issues
```java
// Deadlock example (BAD - DON'T DO THIS)
Object lock1 = new Object();
Object lock2 = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lock1) {
        synchronized (lock2) { /* work */ }  // Thread 1 needs lock2
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lock2) {
        synchronized (lock1) { /* work */ }  // Thread 2 needs lock1 → DEADLOCK
    }
});

// FIX: Always acquire locks in same order
```

---

## 10. Java 8+ Features

### Stream API
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Filter, map, collect
List<Integer> evenSquares = numbers.stream()
    .filter(n -> n % 2 == 0)          // 2, 4, 6, 8, 10
    .map(n -> n * n)                    // 4, 16, 36, 64, 100
    .collect(Collectors.toList());

// Reduce
int sum = numbers.stream().reduce(0, Integer::sum);

// Group by
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));

// Statistics
IntSummaryStatistics stats = numbers.stream()
    .mapToInt(Integer::intValue)
    .summaryStatistics();
System.out.println("Max: " + stats.getMax() + ", Avg: " + stats.getAverage());
```

### Optional
```java
Optional<String> opt = Optional.ofNullable(getValue());

// Anti-pattern: don't use get() without check
String value = opt.get();  // throws NoSuchElementException if empty

// Good patterns
String result = opt.orElse("default");
String result2 = opt.orElseGet(() -> computeDefault());
opt.orElseThrow(() -> new IllegalStateException("No value"));
opt.ifPresent(v -> System.out.println(v));
Optional<Integer> length = opt.map(String::length);
```

---

*📌 Tiếp theo: [Interview-QA.md](Interview-QA.md) | [Practice-Exercises.md](Practice-Exercises.md)*
