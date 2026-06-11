# Concurrency
# Đồng thời

## Concept Explanation
## Giải thích khái niệm
Concurrency means that an application is making progress on more than one task at the same time (concurrently). If the computer only has one CPU, the application may not be progressing on more than one task at exactly the same time, but more than one task is being processed inside the application.
Đồng thời có nghĩa là một ứng dụng đang tiến hành nhiều hơn một tác vụ cùng một lúc (đồng thời). Nếu máy tính chỉ có một CPU, ứng dụng có thể không tiến hành nhiều hơn một tác vụ cùng một lúc, nhưng nhiều hơn một tác vụ đang được xử lý bên trong ứng dụng.

It does not completely finish one task before it begins the next. 
Nó không hoàn thành hoàn toàn một tác vụ trước khi bắt đầu tác vụ tiếp theo.

### Parallelism vs Concurrency
### Song song và đồng thời
- **Concurrency**: Dealing with multiple things at once (task interleaving). Can happen on a single-core CPU via context switching.
- **Đồng thời**: Xử lý nhiều việc cùng một lúc (xen kẽ tác vụ). Có thể xảy ra trên CPU một lõi thông qua chuyển đổi ngữ cảnh.
- **Parallelism**: Doing multiple things at once (simultaneous execution). Requires a multi-core CPU.
- **Song song**: Thực hiện nhiều việc cùng một lúc (thực thi đồng thời). Yêu cầu CPU đa lõi.

### Common Concurrency Issues
### Các vấn đề đồng thời phổ biến
1. **Race Conditions**: When two or more threads can access shared data and they try to change it at the same time. The final result depends on the timing of thread execution.
1. **Tình trạng tranh đua**: Khi hai hoặc nhiều luồng có thể truy cập dữ liệu được chia sẻ và chúng cố gắng thay đổi nó cùng một lúc. Kết quả cuối cùng phụ thuộc vào thời gian thực hiện của luồng.
2. **Deadlocks**: When two or more threads are blocked forever, waiting for each other to release locks.
2. **Bế tắc**: Khi hai hoặc nhiều luồng bị chặn mãi mãi, chờ nhau giải phóng khóa.
3. **Resource Starvation**: When a thread is perpetually denied access to resources it needs to make progress.
3. **Đói tài nguyên**: Khi một luồng liên tục bị từ chối quyền truy cập vào các tài nguyên cần thiết để tiến hành.

## Practical Example
## Ví dụ thực tế
Let's see a race condition in Java, and how to fix it using `synchronized`.
Hãy xem một tình trạng tranh đua trong Java và cách khắc phục nó bằng cách sử dụng `synchronized`.

**RaceConditionExample.java**
```java
public class RaceConditionExample {
    private int counter = 0;

    // Without synchronized, multiple threads will override each other's updates
    // Nếu không có synchronized, nhiều luồng sẽ ghi đè lên các bản cập nhật của nhau
    public synchronized void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        RaceConditionExample example = new RaceConditionExample();
        
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                example.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // Due to synchronized, this will safely print 20000.
        // If you remove synchronized from `increment()`, you will likely get < 20000.
        // Do có synchronized, điều này sẽ in ra 20000 một cách an toàn.
        // Nếu bạn xóa synchronized khỏi `increment()`, bạn có thể sẽ nhận được < 20000.
        System.out.println("Final Counter: " + example.getCounter());
    }
}
```

## System Design Diagram
## Sơ đồ thiết kế hệ thống
```mermaid
graph TD
    subgraph Multi-Core Processor (Parallelism)
        Core1 --> TaskA[Task A Executing]
        Core2 --> TaskB[Task B Executing]
    end

    subgraph Single-Core Processor (Concurrency via Time Slicing)
        Core3 --> TaskC_1[Task C Executing]
        Core3 -. Context Switch .-> TaskD_1[Task D Executing]
        Core3 -. Context Switch .-> TaskC_2[Task C Executing]
    end
```

## Exercises
## Bài tập
1. Write a Java program that demonstrates a simple **Deadlock** between two threads acquiring two different locks in a different order.
1. Viết một chương trình Java minh họa một **Bế tắc** đơn giản giữa hai luồng lấy hai khóa khác nhau theo một thứ tự khác nhau.
2. In C++, implement a thread-safe Queue using `std::mutex` and `std::condition_variable`. 
2. Trong C++, triển khai một Hàng đợi an toàn cho luồng bằng cách sử dụng `std::mutex` và `std::condition_variable`.
3. Research the `java.util.concurrent` package. What is the benefit of `ConcurrentHashMap` over `Hashtable`?
3. Nghiên cứu gói `java.util.concurrent`. Lợi ích của `ConcurrentHashMap` so với `Hashtable` là gì?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Be ready to explain the difference between a mutex, a semaphore, and a monitor.
- Hãy sẵn sàng giải thích sự khác biệt giữa một mutex, một semaphore và một màn hình.
- How do you prevent deadlocks? (Lock ordering, dead-lock detection, lock timeouts).
- Làm thế nào để bạn ngăn chặn bế tắc? (Sắp xếp khóa, phát hiện bế tắc, hết thời gian chờ khóa).
- Concept: What are thread pools and why do we use them instead of creating a new thread for every request?
- Khái niệm: Nhóm luồng là gì và tại sao chúng ta sử dụng chúng thay vì tạo một luồng mới cho mỗi yêu cầu?
