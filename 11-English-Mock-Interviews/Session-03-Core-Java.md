# SESSION 03 — Core Java Technical English

> **SRS Level**: Technical | **Review**: Every 3 days
> **Goal**: Explain JVM, GC, Collections, Multithreading confidently in English

---

## 🃏 CARD 1 — Explaining JVM

**Q: "Can you explain what the JVM is and how it works?"**

✅ Model answer:
> "Sure. The JVM — Java Virtual Machine — is essentially the runtime environment that executes Java bytecode. It's what makes Java platform-independent: you write your code once, compile it to bytecode, and the JVM on any operating system can run it.

> Internally, the JVM has three main components. First, the Class Loader, which loads your compiled .class files. Second, the Runtime Data Areas — this is your memory model, which includes the Heap where objects live, and the Stack, which holds method call frames.

> And third, the Execution Engine — which includes the interpreter for running bytecode, and the JIT compiler, which identifies 'hot' code paths and compiles them to native machine code for better performance.

> One thing I find particularly interesting is how the JIT works — it profiles your running application and optimizes the most frequently executed code paths dynamically."

**🔑 Key phrases:**
- *"essentially"* = về cơ bản
- *"internally"* = bên trong
- *"I find particularly interesting"* = tôi thấy đặc biệt thú vị
- *"dynamically"* = động, linh hoạt
- *"hot code paths"* = đường dẫn code được thực thi nhiều

**📝 YOUR SCORE: [ ]✅ [ ]⚠️ [ ]❌**

---

## 🃏 CARD 2 — Explaining Garbage Collection

**Q: "How does Java's Garbage Collector work?"**

✅ Model answer:
> "Garbage collection in Java is automatic memory management — the GC identifies and reclaims memory occupied by objects that are no longer reachable from any part of the application.

> The heap is divided into generations. Most objects are short-lived, so new objects go into the Young Generation — specifically the Eden space. When Eden fills up, a Minor GC runs: surviving objects move to Survivor spaces, and eventually to the Old Generation if they've lived long enough.

> Full GC happens when the Old Generation fills up, and that's typically where you see the dreaded Stop-the-World pauses.

> In modern Java, G1 GC is the default. It divides the heap into equal-sized regions and prioritizes collecting regions with the most garbage — that's where the 'Garbage First' name comes from. For ultra-low latency, there are ZGC and Shenandoah that achieve sub-millisecond pauses."

**🔑 Key phrases:**
- *"identifies and reclaims"* = xác định và thu hồi
- *"no longer reachable"* = không còn có thể truy cập
- *"the dreaded Stop-the-World pauses"* = khoảng dừng đáng sợ Stop-the-World
- *"ultra-low latency"* = độ trễ cực thấp
- *"that's where the name comes from"* = đó là nguồn gốc của tên gọi

**📝 YOUR SCORE: [ ]✅ [ ]⚠️ [ ]❌**

---

## 🃏 CARD 3 — Explaining HashMap Internals

**Q: "How does HashMap work internally?"**

✅ Model answer:
> "HashMap uses an array of buckets under the hood. When you put a key-value pair, it computes a hash code from the key using `hashCode()`, then maps that to a bucket index using a bitwise AND operation.

> If two keys hash to the same bucket — that's called a collision — the entries are stored in a linked list at that bucket. Starting from Java 8, when a bucket's linked list grows beyond 8 entries, it automatically converts to a red-black tree, which reduces lookup time from O(n) to O(log n).

> Two important things about HashMap: First, it's not thread-safe. If multiple threads modify it concurrently, you can get data corruption. For concurrent access, use ConcurrentHashMap instead.

> Second, whenever you use a custom object as a key, you must override both `hashCode()` AND `equals()` together. They're contractually linked: if two objects are equal, they must have the same hash code."

**🔑 Key phrases:**
- *"under the hood"* = bên trong/phía sau
- *"bitwise AND operation"* = phép AND bit
- *"contractually linked"* = gắn kết theo hợp đồng/theo quy tắc
- *"grows beyond"* = vượt quá
- *"data corruption"* = hỏng dữ liệu

**📝 YOUR SCORE: [ ]✅ [ ]⚠️ [ ]❌**

---

## 🃏 CARD 4 — Explaining Multithreading / Race Condition

**Q: "What is a race condition and how do you prevent it?"**

✅ Model answer:
> "A race condition occurs when two or more threads access shared mutable state concurrently, and the final result depends on the timing of their execution — which is unpredictable.

> Classic example: a counter being incremented by two threads. Even though `count++` looks atomic, it's actually three operations: read the value, increment it, write it back. If two threads interleave these operations, you can lose increments.

> There are a few ways to prevent this. The simplest is using `synchronized`, which ensures only one thread can execute a block at a time. But it can create bottlenecks.

> A better approach for simple counters is `AtomicInteger`, which uses CPU-level Compare-And-Swap instructions — much faster than locking.

> For more complex scenarios, you'd use `ReentrantLock`, which gives you more control — like try-lock with timeout, or separate read/write locks with `ReadWriteLock`.

> And of course, the best solution is to avoid shared mutable state entirely — using immutable objects or thread-local data where possible."

**🔑 Key phrases:**
- *"shared mutable state"* = trạng thái có thể thay đổi được chia sẻ
- *"interleave these operations"* = xen kẽ các thao tác này
- *"create bottlenecks"* = tạo nút cổ chai
- *"CPU-level"* = cấp độ CPU
- *"avoid shared mutable state"* = tránh trạng thái chia sẻ có thể thay đổi

**📝 YOUR SCORE: [ ]✅ [ ]⚠️ [ ]❌**

---

## 🃏 CARD 5 — Explaining Java 8 Stream API

**Q: "Can you explain Java 8 Streams and when you'd use them?"**

✅ Model answer:
> "Java 8 Streams provide a functional, declarative way to process collections of data — very similar in concept to SQL queries, but for in-memory data.

> The key insight is that streams are lazy: you can chain multiple intermediate operations like `filter`, `map`, and `sorted`, and nothing actually executes until you hit a terminal operation like `collect` or `count`. This lazy evaluation means you're not doing unnecessary work.

> For example, instead of writing a loop to filter active employees, sort them by salary, and collect their names — you write one fluent pipeline that reads almost like English.

> I use streams regularly in production code — they significantly reduce boilerplate and make the intent of the code much clearer.

> One thing I'm careful about is not overusing parallel streams. They can help with CPU-intensive tasks on large datasets, but for typical CRUD operations or I/O-bound work, parallel streams add coordination overhead that can actually hurt performance."

**🔑 Key phrases:**
- *"declarative way"* = cách khai báo
- *"lazy evaluation"* = đánh giá lười biếng
- *"fluent pipeline"* = chuỗi phương thức trôi chảy
- *"significantly reduce boilerplate"* = giảm đáng kể code lặp
- *"coordination overhead"* = chi phí phối hợp

**📝 YOUR SCORE: [ ]✅ [ ]⚠️ [ ]❌**

---

## 📖 Technical Vocabulary Bank

| Technical Term | How to Say It Naturally |
|---|---|
| HashMap collision | "when two keys hash to the same bucket" |
| Garbage Collection | "automatic memory management" |
| Null Pointer Exception | "a NullPointerException — typically from calling a method on a null reference" |
| Stack Overflow Error | "usually from infinite recursion — the call stack exceeds its limit" |
| Thread-safe | "safe for concurrent access by multiple threads" |
| Immutable | "once created, its state cannot be changed" |
| Generic types | "parameterized types that provide compile-time type safety" |
| Lazy loading | "load data only when it's actually needed" |
| Eager loading | "load all related data upfront" |

---

## 🎤 Speaking Challenges

**Challenge 1**: Explain HashMap internals in exactly 60 seconds. Time yourself.

**Challenge 2**: Explain Java Streams to a non-technical person using an analogy.
> *Hint: "Imagine you have a pipeline — data flows in from one end, gets filtered, transformed, and exits the other end."*

**Challenge 3**: What do you say if asked about something you don't know well?
> ✅ *"That's not something I've worked with directly, but based on my understanding of [related concept], I'd expect it to work like... Is that roughly correct?"*

---

*Next: [Session 04 - Spring](./Session-04-Spring.md)*
