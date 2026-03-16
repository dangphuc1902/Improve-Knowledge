# Memory Management
# Quản lý bộ nhớ

## Concept Explanation
## Giải thích khái niệm
Memory management is the process of controlling and coordinating computer memory, assigning blocks to various running programs to optimize overall system performance.
Quản lý bộ nhớ là quá trình kiểm soát và điều phối bộ nhớ máy tính, gán các khối cho các chương trình đang chạy khác nhau để tối ưu hóa hiệu suất hệ thống tổng thể.

### Stack vs Heap Allocation
### Phân bổ ngăn xếp và đống
1. **Stack Memory**: 
1. **Bộ nhớ ngăn xếp**:
   - Used for static memory allocation and the execution of threads.
   - Được sử dụng để phân bổ bộ nhớ tĩnh và thực thi các luồng.
   - Contains primitive values that are specific to a method and references to objects that are in the heap.
   - Chứa các giá trị nguyên thủy dành riêng cho một phương thức và các tham chiếu đến các đối tượng nằm trong đống.
   - Fast access, automatically managed (LIFO order as methods are called/return).
   - Truy cập nhanh, được quản lý tự động (thứ tự LIFO khi các phương thức được gọi/trả về).
   - Space is limited. Causes `StackOverflowError` if exceeded (e.g., deep recursion).
   - Không gian bị hạn chế. Gây ra `StackOverflowError` nếu vượt quá (ví dụ: đệ quy sâu).

2. **Heap Memory**:
2. **Bộ nhớ đống**:
   - Used for dynamic memory allocation of objects at runtime.
   - Được sử dụng để phân bổ bộ nhớ động cho các đối tượng trong thời gian chạy.
   - Objects are created here.
   - Các đối tượng được tạo ở đây.
   - Slower access compared to stack.
   - Truy cập chậm hơn so với ngăn xếp.
   - Garbage collected (in languages like Java/C#) or manually managed (C/C++).
   - Được thu gom rác (trong các ngôn ngữ như Java/C#) hoặc được quản lý thủ công (C/C++).
   - Causes `OutOfMemoryError` if space is full.
   - Gây ra `OutOfMemoryError` nếu không gian đầy.

### Garbage Collection (GC)
### Thu gom rác (GC)
In managed languages like Java or Node.js, the Garbage Collector automatically frees up memory space by destroying unreachable objects.
Trong các ngôn ngữ được quản lý như Java hoặc Node.js, Bộ thu gom rác sẽ tự động giải phóng không gian bộ nhớ bằng cách hủy các đối tượng không thể truy cập được.

- **Mark and Sweep**: A common GC algorithm.
- **Đánh dấu và quét**: Một thuật toán GC phổ biến.
  1. **Mark**: The GC traverses the object graph starting from GC Roots (local variables, active threads, static fields). It marks all reachable objects as "alive".
  1. **Đánh dấu**: GC duyệt qua biểu đồ đối tượng bắt đầu từ Gốc GC (biến cục bộ, luồng hoạt động, trường tĩnh). Nó đánh dấu tất cả các đối tượng có thể truy cập là "còn sống".
  2. **Sweep**: The GC scans the heap memory. Any memory that is not marked as alive is reclaimed.
  2. **Quét**: GC quét bộ nhớ đống. Bất kỳ bộ nhớ nào không được đánh dấu là còn sống đều được thu hồi.

## Practical Example
## Ví dụ thực tế

**C++: Manual Memory Management (The Danger Zone)**
**C++: Quản lý bộ nhớ thủ công (Vùng nguy hiểm)**
```cpp
#include <iostream>

void memoryLeakExample() {
    // dynamically allocating an array on the heap
    // phân bổ động một mảng trên đống
    int* ptr = new int[100]; 
    
    // doing something with ptr...
    // làm gì đó với ptr...
    ptr[0] = 10;
    
    // IF WE FORGET: delete[] ptr;
    // NẾU CHÚNG TA QUÊN: delete[] ptr;
    // We create a memory leak! The memory remains allocated but unreachable.
    // Chúng ta tạo ra một rò rỉ bộ nhớ! Bộ nhớ vẫn được phân bổ nhưng không thể truy cập được.
}

void smartPointersExample() {
    // Modern C++ uses smart pointers to automatically clean up!
    // C++ hiện đại sử dụng con trỏ thông minh để tự động dọn dẹp!
    std::unique_ptr<int[]> smartPtr(new int[100]);
    smartPtr[0] = 50;
    // Memory is freed automatically when smartPtr goes out of scope here.
    // Bộ nhớ được giải phóng tự động khi smartPtr ra khỏi phạm vi ở đây.
}
```

**Java: Out of Memory Example**
**Java: Ví dụ về hết bộ nhớ**
```java
import java.util.ArrayList;
import java.util.List;

public class MemoryLeak {
    // A static list holding references forever preventing GC
    // Một danh sách tĩnh giữ các tham chiếu mãi mãi ngăn chặn GC
    static List<Object> cache = new ArrayList<>();

    public static void main(String[] args) {
        try {
            while (true) {
                // Continuously adding to heap until OutOfMemoryError
                // Liên tục thêm vào đống cho đến khi hết bộ nhớ
                cache.add(new long[100000]); 
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Heap spaces exhausted!");
        }
    }
}
```

## Exercises
## Bài tập
1. In Java, what is an "Island of Isolation"? Provide a code snippet. Can the Garbage Collector collect objects in an island of isolation?
1. Trong Java, "Đảo cô lập" là gì? Cung cấp một đoạn mã. Bộ thu gom rác có thể thu thập các đối tượng trong một hòn đảo cô lập không?
2. Read about Node.js memory limits. How do you increase the maximum heap size in a Node.js runtime process?
2. Đọc về giới hạn bộ nhớ của Node.js. Làm cách nào để bạn tăng kích thước đống tối đa trong một quy trình thời gian chạy Node.js?
3. What is a "memory leak" in garbage-collected languages (like Java/Node.js) if the memory is managed? Provide a conceptual example.
3. "Rò rỉ bộ nhớ" trong các ngôn ngữ được thu gom rác (như Java/Node.js) là gì nếu bộ nhớ được quản lý? Cung cấp một ví dụ khái niệm.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Differentiate between Pass-by-Value and Pass-by-Reference. (Note: Java is strictly Pass-by-Value).
- Phân biệt giữa Truyền theo giá trị và Truyền theo tham chiếu. (Lưu ý: Java hoàn toàn là Truyền theo giá trị).
- Explain Stack vs Heap clearly.
- Giải thích rõ ràng về Ngăn xếp và Đống.
- Discuss how Garbage Collection can pause application execution ("Stop-The-World" events).
- Thảo luận về cách Thu gom rác có thể tạm dừng việc thực thi ứng dụng (sự kiện "Dừng thế giới").
