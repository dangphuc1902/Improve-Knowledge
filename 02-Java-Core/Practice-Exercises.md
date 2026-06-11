# 💻 Java Nâng Cao - Bài Tập Thực Hành

---

## Bài Tập 1: Collections - Phân Tích Tần Suất Từ

```java
import java.util.*;
import java.util.stream.*;

public class WordFrequency {
    /**
     * Đếm tần suất xuất hiện của từng từ trong đoạn văn.
     * @return Map<String, Long> - từ → số lần xuất hiện, sắp xếp theo tần suất giảm dần
     */
    public static Map<String, Long> analyze(String text) {
        // TODO: 
        // 1. Split text thành words (lowercase, bỏ punctuation)
        // 2. Đếm tần suất mỗi từ dùng Stream + Collectors.groupingBy
        // 3. Sort by count desc
        // 4. Return as LinkedHashMap để giữ thứ tự
        return null;
    }

    /**
     * Tìm top N từ xuất hiện nhiều nhất (bỏ stop words)
     */
    public static List<Map.Entry<String, Long>> topNWords(String text, int n, Set<String> stopWords) {
        // TODO: Dùng analyze() rồi filter stop words
        return null;
    }

    public static void main(String[] args) {
        String text = "Java is a popular programming language. Java is used for backend development. " +
                "Java developers use Spring framework. Spring Boot makes Java development faster.";

        Set<String> stopWords = new HashSet<>(Arrays.asList("is", "a", "for", "and", "the", "in", "of"));

        Map<String, Long> freq = analyze(text);
        System.out.println("Word frequencies:");
        freq.forEach((word, count) -> System.out.printf("  %-15s: %d%n", word, count));

        System.out.println("\nTop 5 words (excluding stop words):");
        topNWords(text, 5, stopWords).forEach(e ->
                System.out.printf("  %-15s: %d%n", e.getKey(), e.getValue()));
    }
}
```

---

## Bài Tập 2: Generic Stack

```java
public class GenericStack<T> {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public GenericStack(int capacity) {
        elements = new Object[capacity];
    }

    public GenericStack() {
        this(DEFAULT_CAPACITY);
    }

    // TODO: push - thêm element, resize nếu đầy
    public void push(T element) { }

    // TODO: pop - lấy element trên cùng, throw EmptyStackException nếu rỗng
    public T pop() { return null; }

    // TODO: peek - xem element trên cùng mà không xóa
    public T peek() { return null; }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    // TODO: toList() - convert stack to List (bottom to top order)
    public List<T> toList() { return null; }

    // Test
    public static void main(String[] args) {
        GenericStack<Integer> stack = new GenericStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println("Peek: " + stack.peek());  // 3
        System.out.println("Pop: " + stack.pop());    // 3
        System.out.println("Size: " + stack.size());  // 2
        System.out.println("List: " + stack.toList()); // [1, 2]

        // Test with Strings
        GenericStack<String> stringStack = new GenericStack<>();
        stringStack.push("Hello");
        stringStack.push("World");
        System.out.println(stringStack.pop());  // World
    }
}
```

---

## Bài Tập 3: Producer-Consumer với BlockingQueue

```java
import java.util.concurrent.*;

public class ProducerConsumer {
    private static final int CAPACITY = 5;
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(CAPACITY);

    static class Producer implements Runnable {
        private final String name;
        private final int itemsToProduce;

        Producer(String name, int items) {
            this.name = name;
            this.itemsToProduce = items;
        }

        @Override
        public void run() {
            // TODO: Produce itemsToProduce items
            // Mỗi item: random number từ 1-100
            // Dùng queue.put() (blocks khi queue full)
            // In ra: "Producer [name] produced: [item], Queue size: [size]"
            // Sleep 100-500ms ngẫu nhiên giữa các lần produce
        }
    }

    static class Consumer implements Runnable {
        private final String name;

        Consumer(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            // TODO: Consume items đến khi nhận "poison pill" (giá trị -1)
            // Dùng queue.take() (blocks khi queue empty)
            // In ra: "Consumer [name] consumed: [item], Queue size: [size]"
            // Sleep 200-700ms ngẫu nhiên giữa các lần consume
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        // 2 producers, 3 consumers
        executor.submit(new Producer("P1", 10));
        executor.submit(new Producer("P2", 10));
        executor.submit(new Consumer("C1"));
        executor.submit(new Consumer("C2"));
        executor.submit(new Consumer("C3"));

        // TODO: Đợi producers xong, rồi send poison pills cho consumers
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("Done!");
    }
}
```

---

## Bài Tập 4: Stream API - Phân Tích Dữ Liệu

```java
import java.util.*;
import java.util.stream.*;

public class StreamAnalytics {
    record Employee(String name, String department, double salary, int yearsOfExperience) {}

    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "Engineering", 95000, 5),
            new Employee("Bob", "Marketing", 72000, 3),
            new Employee("Charlie", "Engineering", 110000, 8),
            new Employee("Diana", "HR", 65000, 2),
            new Employee("Eve", "Engineering", 88000, 4),
            new Employee("Frank", "Marketing", 78000, 6),
            new Employee("Grace", "HR", 70000, 7),
            new Employee("Henry", "Engineering", 120000, 10)
        );

        // TODO 1: Lương trung bình theo phòng ban
        // Expected: {Engineering=103250.0, Marketing=75000.0, HR=67500.0}
        Map<String, Double> avgSalaryByDept = null;

        // TODO 2: Nhân viên lương cao nhất ở mỗi phòng ban
        Map<String, Optional<Employee>> topEarnerByDept = null;

        // TODO 3: Tổng lương toàn công ty
        double totalPayroll = 0;

        // TODO 4: Nhân viên Engineering có kinh nghiệm > 5 năm, sort by salary desc
        List<Employee> seniorEngineers = null;

        // TODO 5: Group nhân viên theo salary range:
        // "Junior" (< 75000), "Mid" (75000-100000), "Senior" (> 100000)
        Map<String, List<Employee>> salaryGroups = null;

        // TODO 6: Tên nhân viên theo bảng chữ cái, phân cách bằng ", "
        String employeeNames = null;

        // Print results
        System.out.println("Avg salary by dept: " + avgSalaryByDept);
        System.out.println("Total payroll: " + totalPayroll);
        System.out.println("Senior engineers: ");
        if (seniorEngineers != null) seniorEngineers.forEach(e ->
                System.out.printf("  %s - $%.0f%n", e.name(), e.salary()));
        System.out.println("Employee names: " + employeeNames);
    }
}
```

---

## Bài Tập 5: Thread-safe Singleton với Double-Checked Locking

```java
public class ConfigManager {
    // TODO: Implement thread-safe Singleton với Double-Checked Locking
    private static volatile ConfigManager instance;
    private final Map<String, String> properties;

    private ConfigManager() {
        properties = new HashMap<>();
        loadDefaultConfig();
    }

    private void loadDefaultConfig() {
        properties.put("db.url", "jdbc:mysql://localhost:3306/mydb");
        properties.put("db.username", "root");
        properties.put("db.pool.size", "10");
        properties.put("app.timeout", "30000");
    }

    // TODO: Implement getInstance() với double-checked locking
    public static ConfigManager getInstance() {
        // Your code here
        return null;
    }

    public String get(String key) {
        return properties.getOrDefault(key, "");
    }

    public void set(String key, String value) {
        // TODO: Make this thread-safe
        properties.put(key, value);
    }

    // Test: Multiple threads access same instance
    public static void main(String[] args) throws InterruptedException {
        Runnable test = () -> {
            ConfigManager config = ConfigManager.getInstance();
            System.out.println(Thread.currentThread().getName() +
                    " - DB URL: " + config.get("db.url") +
                    " - same instance: " + (config == ConfigManager.getInstance()));
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(test, "Thread-" + i);
            threads.add(t);
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
    }
}
```

---

## 📊 Kết Quả Mong Đợi

**Bài 1 - Word Frequency:**
```
Word frequencies:
  java            : 4
  spring          : 2
  development     : 2
  ...

Top 5 words: java(4), spring(2), development(2), popular(1), programming(1)
```

**Bài 4 - Stream Analytics:**
```
Avg salary by dept: {Engineering=103250.0, Marketing=75000.0, HR=67500.0}
Total payroll: 698000.0
Senior engineers:
  Henry - $120000
  Charlie - $110000
```

---

*📌 Tiếp theo: [03-Servlet-JSP-MVC/Theory.md](../03-Servlet-JSP-MVC/Theory.md)*
