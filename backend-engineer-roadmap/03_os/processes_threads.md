# Processes and Threads
# Tiến trình và Luồng

## Concept Explanation
## Giải thích khái niệm

### What is a Process?
### Tiến trình là gì?
A process is an executing instance of an application. When you double click on a `.exe` or run a command, you start a process.
Một tiến trình là một phiên bản đang thực thi của một ứng dụng. Khi bạn nhấp đúp vào một tệp `.exe` hoặc chạy một lệnh, bạn sẽ bắt đầu một tiến trình.
- Each process has its own distinct, isolated memory space holding the executable code, heap, and stack.
- Mỗi tiến trình có không gian bộ nhớ riêng biệt, bị cô lập, chứa mã thực thi, đống và ngăn xếp.
- Processes are independent. If one crashes, it usually doesn't affect others.
- Các tiến trình là độc lập. Nếu một tiến trình bị lỗi, nó thường không ảnh hưởng đến các tiến trình khác.
- Inter-Process Communication (IPC) is required for processes to share data (Pipes, Sockets, Shared Memory).
- Giao tiếp giữa các tiến trình (IPC) là cần thiết để các tiến trình chia sẻ dữ liệu (Đường ống, Ổ cắm, Bộ nhớ dùng chung).

### What is a Thread?
### Luồng là gì?
A thread is a path of execution within a process.
Một luồng là một đường dẫn thực thi trong một tiến trình.
- A process can contain multiple threads.
- Một tiến trình có thể chứa nhiều luồng.
- All threads within a single process share the same memory space (heap) and application resources.
- Tất cả các luồng trong một tiến trình duy nhất chia sẻ cùng một không gian bộ nhớ (đống) và tài nguyên ứng dụng.
- However, each thread has its own Stack (to keep track of local variables and function calls) and Program Counter.
- Tuy nhiên, mỗi luồng có Ngăn xếp riêng (để theo dõi các biến cục bộ và các lệnh gọi hàm) và Bộ đếm chương trình.
- Thread context switching is significantly faster and cheaper than process context switching.
- Chuyển đổi ngữ cảnh luồng nhanh hơn và rẻ hơn đáng kể so với chuyển đổi ngữ cảnh tiến trình.

## System Design Diagram
## Sơ đồ thiết kế hệ thống
```mermaid
graph TD
    subgraph Process
        Code[Code / Text Section]
        Data[Data Section / Global Variables]
        Heap[Heap / Dynamic Memory]
        
        subgraph Thread 1
            Stack1[Stack]
            Registers1[Registers / PC]
        end
        
        subgraph Thread 2
            Stack2[Stack]
            Registers2[Registers / PC]
        end
    end
    
    Code --> Thread 1
    Code --> Thread 2
    Heap --> Thread 1
    Heap --> Thread 2
```

## Practical Example: C++ Processes vs Threads
## Ví dụ thực tế: Tiến trình và luồng C++

```cpp
#include <iostream>
#include <thread>
#include <unistd.h>

void threadTask() {
    std::cout << "  [Thread] Running in thread ID: " << std::this_thread::get_id() << std::endl;
}

int main() {
    std::cout << "[Process] Main process PID: " << getpid() << std::endl;

    // 1. Creating a Thread
    // 1. Tạo một luồng
    std::thread t1(threadTask);
    t1.join(); // Wait for thread to finish

    // 2. Creating a Process (fork is POSIX specific, typical in Linux/Mac for C++)
    // 2. Tạo một tiến trình (fork là đặc trưng của POSIX, điển hình trong Linux/Mac cho C++)
    pid_t pid = fork();
    
    if (pid == 0) {
        // Child Process
        // Tiến trình con
        std::cout << "[Child Process] PID: " << getpid() << " Parent PID: " << getppid() << std::endl;
    } else if (pid > 0) {
        // Parent Process
        // Tiến trình cha
        std::cout << "[Parent Process] continuing. Child PID spawned: " << pid << std::endl;
    } else {
        std::cerr << "Fork failed" << std::endl;
    }

    return 0;
}
```

## Exercises
## Bài tập
1. In Java, what is the difference between implementing the `Runnable` interface and extending the `Thread` class?
1. Trong Java, sự khác biệt giữa việc triển khai giao diện `Runnable` và mở rộng lớp `Thread` là gì?
2. What happens to a child process if the parent process terminates before the child does? (Hint: Orphan processes vs Zombie processes).
2. Điều gì xảy ra với một tiến trình con nếu tiến trình cha kết thúc trước khi tiến trình con kết thúc? (Gợi ý: Tiến trình mồ côi và tiến trình Zombie).
3. Write a tiny Node.js app utilizing the `child_process` core module to execute a shell command (`ls` or `dir`) in a separate process.
3. Viết một ứng dụng Node.js nhỏ sử dụng mô-đun lõi `child_process` để thực thi một lệnh shell (`ls` hoặc `dir`) trong một tiến trình riêng biệt.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Contrast context-switching overhead between threads and processes.
- So sánh chi phí chuyển đổi ngữ cảnh giữa các luồng và các tiến trình.
- Why is memory sharing easy between threads but hard between processes? (Because threads share the heap).
- Tại sao chia sẻ bộ nhớ dễ dàng giữa các luồng nhưng lại khó giữa các tiến trình? (Vì các luồng chia sẻ đống).
- Understand why Chrome browsers run each tab as a separate process (fault tolerance at the cost of higher memory).
- Hiểu tại sao các trình duyệt Chrome chạy mỗi tab như một tiến trình riêng biệt (khả năng chịu lỗi với chi phí bộ nhớ cao hơn).
