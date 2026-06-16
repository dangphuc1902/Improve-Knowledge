# PART 3 — Core Java Interview Questions

---

## 🔷 JVM / JRE / JDK

### Q1: What is the difference between JDK, JRE, and JVM?

**Ideal Answer:**
> - **JVM (Java Virtual Machine)**: Executes bytecode. Platform-specific. Handles memory management and GC.
> - **JRE (Java Runtime Environment)**: JVM + standard libraries. Needed to *run* Java programs.
> - **JDK (Java Development Kit)**: JRE + compiler (`javac`) + tools (debugger, jshell). Needed to *develop* Java programs.

```
JDK ⊃ JRE ⊃ JVM
```

**Follow-up:** "Is JVM platform-independent?"
> No — the JVM is platform-specific. But Java *bytecode* is platform-independent. "Write once, run anywhere" refers to the bytecode.

**Common Mistakes:**
- Saying JVM is platform-independent (it's not — the JVM itself is native)

---

### Q2: Explain the JVM Memory Model

**Ideal Answer:**
```
┌──────────────────────────────────────────────┐
│                   JVM Memory                 │
├──────────────┬───────────────────────────────┤
│  Heap        │  Young Gen + Old Gen (Tenured) │
├──────────────┼───────────────────────────────┤
│  Stack       │  Per-thread: frames, locals    │
├──────────────┼───────────────────────────────┤
│  Metaspace   │  Class metadata (replaces PermGen)│
├──────────────┼───────────────────────────────┤
│  Code Cache  │  JIT compiled code             │
├──────────────┼───────────────────────────────┤
│  PC Register │  Current thread instruction    │
└──────────────┴───────────────────────────────┘
```

- **Heap**: Objects live here. Shared across threads. GC manages it.
- **Stack**: Each thread has its own. Stores method call frames, local variables, references.
- **Metaspace**: Replaced PermGen in Java 8. Stores class definitions. Grows dynamically (native memory).

**Common Mistakes:**
- Confusing PermGen (pre-Java 8) and Metaspace (Java 8+)

---

## 🔷 Garbage Collection

### Q3: How does Garbage Collection work in Java?

**Ideal Answer:**
> GC automatically reclaims memory from objects that are no longer reachable from any GC root (static fields, local variables, active threads).

**Generational GC:**
```
Young Generation:
  - Eden Space → objects created here
  - Survivor 0 / Survivor 1

When Eden fills → Minor GC (fast)
  - Live objects copied to Survivor
  - After N surviving GCs → promoted to Old Gen

Old Generation:
  - Long-lived objects
  - Major GC / Full GC when full (slow, stop-the-world)
```

**Common Collectors:**
| Collector | Use Case |
|-----------|----------|
| Serial GC | Single-threaded, small apps |
| Parallel GC | Throughput-focused |
| G1 GC | Default since Java 9, balanced |
| ZGC | Ultra-low latency (Java 11+) |
| Shenandoah | Low pause times |

**Follow-up:** "What is a Stop-The-World pause?"
> All application threads are paused while GC runs. Minor GC pauses are short (ms). Full GC pauses can be long (seconds).

---

## 🔷 String Pool

### Q4: What is String Pool and how does it work?

**Ideal Answer:**
> String Pool (Interned String Pool) is a special area in the **Heap** (Metaspace in older JVMs) where string literals are stored and reused.

```java
String a = "hello";      // stored in pool
String b = "hello";      // reuses same pool reference
String c = new String("hello"); // new object on heap, NOT in pool

System.out.println(a == b);  // true (same reference)
System.out.println(a == c);  // false (different object)
System.out.println(a.equals(c)); // true (same value)

// Force into pool:
String d = c.intern();
System.out.println(a == d);  // true
```

---

### Q5: String vs StringBuilder vs StringBuffer

| | String | StringBuilder | StringBuffer |
|---|--------|--------------|-------------|
| Mutability | Immutable | Mutable | Mutable |
| Thread-safe | Yes (immutable) | No | Yes (synchronized) |
| Performance | Slow for concat | Fast | Slower than StringBuilder |
| Use when | Value doesn't change | Single-thread string building | Multi-thread string building |

**Why is String immutable?**
> Security (class names, network connections, credentials), caching (hashCode cached), thread safety without synchronization, String pool optimization.

---

## 🔷 Exception Handling

### Q6: Checked vs Unchecked Exceptions

**Ideal Answer:**
```
Throwable
├── Error (JVM errors, don't catch)
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception
    ├── Checked (must handle or declare)
    │   ├── IOException
    │   └── SQLException
    └── Unchecked / RuntimeException
        ├── NullPointerException
        ├── IllegalArgumentException
        └── IndexOutOfBoundsException
```

- **Checked**: Compiler forces you to handle. For *recoverable* conditions (file not found, network error).
- **Unchecked**: Programming errors. Fail fast. Don't catch unless you know why.

**Follow-up:** "What happens in finally block if there's a return in try?"
> `finally` always runs. If `finally` also has a `return`, it overrides the `try`'s return value.

---

## 🔷 Collections

### Q7: List vs Set vs Map

| | List | Set | Map |
|---|------|-----|-----|
| Ordered | Yes (insertion order) | Depends | Keys: unordered (HashMap) |
| Duplicates | Allowed | Not allowed | Keys: unique, Values: allowed |
| Null | Allowed | One null (HashSet) | One null key (HashMap) |
| Common impl | ArrayList, LinkedList | HashSet, TreeSet | HashMap, TreeMap |

---

### Q8: HashMap Internals (Most Important!)

**Ideal Answer:**

```java
// Structure: Array of Nodes (buckets)
// Each bucket is a LinkedList (or TreeMap when size > 8)

// put(key, value):
// 1. Compute hash: hash = key.hashCode() ^ (h >>> 16)
// 2. Index: index = hash & (capacity - 1)
// 3. If bucket empty → insert
// 4. If collision → check equals()
//    - If same key → update
//    - If different key → chain (LinkedList)
// 5. If chain length > 8 → treeify to Red-Black Tree
// 6. If size > capacity * loadFactor (0.75) → resize (2x)
```

**Key numbers:**
- Default capacity: **16**
- Load factor: **0.75**
- Treeify threshold: **8**
- Untreeify threshold: **6**

**Follow-up:** "Why capacity is always power of 2?"
> `index = hash & (capacity - 1)` is a fast bitwise operation equivalent to `hash % capacity`, but only works correctly when capacity is a power of 2.

**Common Mistakes:**
- Thinking HashMap is thread-safe (it's NOT)
- Not overriding both `hashCode()` AND `equals()` for custom keys

---

### Q9: ConcurrentHashMap vs HashMap vs Hashtable

| | HashMap | Hashtable | ConcurrentHashMap |
|---|---------|-----------|------------------|
| Thread-safe | No | Yes (full lock) | Yes (segment/bucket lock) |
| Null keys | 1 null key allowed | Not allowed | Not allowed |
| Performance | Best (single thread) | Worst | Best (multi-thread) |
| Java version | Java 2 | Java 1 | Java 5 |

**ConcurrentHashMap in Java 8+:**
> Uses CAS (Compare-And-Swap) operations and synchronized blocks on individual buckets, not the whole map. Much higher concurrency.

---

## 🔷 Multithreading

### Q10: Thread vs Runnable vs Callable

```java
// Thread - extend class (can't extend other class)
class MyThread extends Thread {
    public void run() { ... }
}

// Runnable - implement interface (preferred), no return value
class MyTask implements Runnable {
    public void run() { ... }
}

// Callable - returns value, can throw checked exception
class MyCallable implements Callable<Integer> {
    public Integer call() throws Exception { return 42; }
}
```

---

### Q11: synchronized vs volatile vs AtomicInteger

| | synchronized | volatile | AtomicInteger |
|---|-------------|----------|---------------|
| Atomicity | Yes (block/method) | No | Yes (single ops) |
| Visibility | Yes | Yes | Yes |
| Compound ops | Yes | No | Yes (getAndIncrement) |
| Performance | Slowest | Fast | Fast |

**volatile guarantees:**
- Visibility: reads always from main memory
- Does NOT guarantee atomicity: `count++` is NOT atomic even with volatile

---

### Q12: ExecutorService

```java
// Types of thread pools
ExecutorService pool = Executors.newFixedThreadPool(4);     // Fixed size
ExecutorService cached = Executors.newCachedThreadPool();   // Grows as needed
ExecutorService single = Executors.newSingleThreadExecutor(); // 1 thread
ScheduledExecutorService sched = Executors.newScheduledThreadPool(2);

// Submit task
Future<Integer> future = pool.submit(() -> 42);
Integer result = future.get(); // blocks until done

// Shutdown
pool.shutdown();        // wait for tasks to finish
pool.shutdownNow();    // interrupt all running tasks
```

---

### Q13: CompletableFuture (Java 8+)

```java
// Basic chain
CompletableFuture.supplyAsync(() -> fetchUser(id))
    .thenApply(user -> enrichWithOrders(user))
    .thenAccept(result -> saveToCache(result))
    .exceptionally(ex -> { log(ex); return null; });

// Combine two futures
CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> getUser());
CompletableFuture<Order> orderFuture = CompletableFuture.supplyAsync(() -> getOrder());

CompletableFuture.allOf(userFuture, orderFuture).thenRun(() -> {
    User user = userFuture.join();
    Order order = orderFuture.join();
});
```

**vs Future:**
- `Future.get()` blocks. `CompletableFuture` is non-blocking and composable.

---

## 🔷 Java 8 Features

### Q14: Lambda, Stream API, Optional

**Lambda:**
```java
// Before Java 8
Comparator<String> comp = new Comparator<String>() {
    public int compare(String a, String b) { return a.compareTo(b); }
};

// Java 8 lambda
Comparator<String> comp = (a, b) -> a.compareTo(b);
```

**Stream API:**
```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Filter → Map → Collect
List<Integer> result = nums.stream()
    .filter(n -> n % 2 == 0)        // [2,4,6,8,10]
    .map(n -> n * n)                // [4,16,36,64,100]
    .collect(Collectors.toList());

// Intermediate ops (lazy): filter, map, flatMap, sorted, distinct, limit, skip
// Terminal ops (eager):    collect, forEach, reduce, count, min, max, findFirst

// Parallel stream (use with caution)
nums.parallelStream().filter(n -> n > 5).count();
```

**Optional:**
```java
Optional<User> user = userRepo.findById(id);

// Bad pattern (defeats the purpose):
if (user.isPresent()) { return user.get(); }

// Good pattern:
return user.orElseThrow(() -> new UserNotFoundException(id));
return user.map(User::getName).orElse("Unknown");
user.ifPresent(u -> sendEmail(u));
```

**Follow-up:** "What is method reference?"
```java
// Four types:
ClassName::staticMethod        // String::valueOf
instance::instanceMethod       // System.out::println
ClassName::instanceMethod      // String::toUpperCase
ClassName::new                 // ArrayList::new
```

---

## 📋 Core Java Quick Reference

| Topic | Key Point |
|-------|-----------|
| JVM Memory | Heap (objects), Stack (frames), Metaspace (classes) |
| GC | Young → Old gen. Minor GC fast. Major GC slow. |
| String Pool | Interned literals share references. `==` vs `equals()`. |
| HashMap | hash → index → bucket (LinkedList → TreeMap at 8) |
| ConcurrentHashMap | Bucket-level locking, CAS ops |
| volatile | Visibility only, NOT atomicity |
| synchronized | Mutual exclusion, visibility, atomicity |
| CompletableFuture | Non-blocking async chaining |
| Stream | Lazy intermediate, eager terminal |
| Optional | Avoid null returns from APIs |
