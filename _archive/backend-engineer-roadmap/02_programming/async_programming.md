# Asynchronous Programming
# Lập trình bất đồng bộ

## Concept Explanation
## Giải thích khái niệm
Asynchronous programming allows execution of tasks in the background without blocking the main execution thread. When the background task is complete, it notifies the main program, usually through a callback function, a promise, or an event.
Lập trình bất đồng bộ cho phép thực hiện các tác vụ trong nền mà không chặn luồng thực thi chính. Khi tác vụ nền hoàn tất, nó sẽ thông báo cho chương trình chính, thường thông qua một hàm gọi lại, một lời hứa hoặc một sự kiện.

This is fundamentally important in backend development (especially Node.js) because it allows a single thread to handle thousands of concurrent I/O operations (like database queries, API calls, file reads) without freezing up while waiting for responses.
Điều này về cơ bản là quan trọng trong phát triển backend (đặc biệt là Node.js) vì nó cho phép một luồng duy nhất xử lý hàng nghìn hoạt động I/O đồng thời (như truy vấn cơ sở dữ liệu, lệnh gọi API, đọc tệp) mà không bị treo trong khi chờ phản hồi.

### Synchronous vs Asynchronous
### Đồng bộ và bất đồng bộ
- **Synchronous**: Operations execute one after another in order. If `Operation B` depends on `Operation A`, `B` must wait for `A` to finish. The thread is "blocked."
- **Đồng bộ**: Các hoạt động thực hiện lần lượt theo thứ tự. Nếu `Hoạt động B` phụ thuộc vào `Hoạt động A`, `B` phải đợi `A` kết thúc. Luồng bị "chặn".
- **Asynchronous**: `Operation A` initiates an I/O request and then execution moves on to `Operation B`. When the I/O for `A` completes, a callback/handler processes the result.
- **Bất đồng bộ**: `Hoạt động A` khởi tạo một yêu cầu I/O và sau đó quá trình thực thi chuyển sang `Hoạt động B`. Khi I/O cho `A` hoàn tất, một lệnh gọi lại/trình xử lý sẽ xử lý kết quả.

### Node.js Event Loop
### Vòng lặp sự kiện Node.js
Node.js relies entirely on the Event Loop for async behavior.
Node.js hoàn toàn dựa vào Vòng lặp sự kiện cho hành vi bất đồng bộ.
1. Code executes in the Call Stack.
1. Mã thực thi trong Ngăn xếp cuộc gọi.
2. Async operations (like `setTimeout`, file system reads) are offloaded to Web APIs (or C++ threads in libuv).
2. Các hoạt động bất đồng bộ (như `setTimeout`, đọc hệ thống tệp) được giảm tải cho các API Web (hoặc các luồng C++ trong libuv).
3. Callbacks from finished async tasks get pushed to the Task Queue (or Microtask Queue for Promises).
3. Các lệnh gọi lại từ các tác vụ bất đồng bộ đã hoàn thành sẽ được đẩy vào Hàng đợi tác vụ (hoặc Hàng đợi tác vụ vi mô cho Lời hứa).
4. The Event Loop continuously checks if the Call Stack is empty; if so, it pushes tasks from the Queue to the Stack.
4. Vòng lặp sự kiện liên tục kiểm tra xem Ngăn xếp cuộc gọi có trống không; nếu có, nó sẽ đẩy các tác vụ từ Hàng đợi vào Ngăn xếp.

## Practical Example
## Ví dụ thực tế

**Node.js: Promises and Async/Await**
**Node.js: Lời hứa và Bất đồng bộ/Chờ đợi**
```javascript
const util = require('util');
const sleep = util.promisify(setTimeout);

// Approach 1: Promises
// Cách tiếp cận 1: Lời hứa
function fetchDataPromise() {
    console.log("1. Starting data fetch (Promise)...");
    
    // Simulate DB query
    // Mô phỏng truy vấn DB
    sleep(2000).then(() => {
        console.log("3. Data fetched successfully (Promise)!");
    });
    
    console.log("2. I am not blocked! Continuing execution.");
}

// Approach 2: Async/Await (Cleaner syntax)
// Cách tiếp cận 2: Bất đồng bộ/Chờ đợi (Cú pháp gọn gàng hơn)
async function fetchDataAsync() {
    console.log("A. Starting data fetch (Async/Await)...");
    
    // await "pauses" the execution of THIS async function
    // but yields control back to the Node event loop, not blocking the main thread.
    // await "tạm dừng" việc thực thi hàm async NÀY
    // nhưng trả lại quyền điều khiển cho vòng lặp sự kiện Node, không chặn luồng chính.
    await sleep(2000); 
    
    console.log("B. Data fetched successfully (Async/Await)!");
}

fetchDataPromise();
// After 2 seconds, fetchDataAsync will be called (for demonstration)
// Sau 2 giây, fetchDataAsync sẽ được gọi (để trình diễn)
setTimeout(fetchDataAsync, 3000); 
```

**Java: CompletableFuture**
**Java: CompletableFuture**
```java
import java.util.concurrent.CompletableFuture;

public class AsyncExample {
    public static void main(String[] args) {
        System.out.println("Main Thread starts.");

        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // Simulate slow I/O
            } catch (InterruptedException e) {}
            return "Data from DB";
        }).thenAccept(result -> {
            System.out.println("Async Process Complete: " + result);
        });

        System.out.println("Main Thread finishes, not waiting for CompletableFuture.");
        
        // Block main thread to allow completable future to finish before program exits
        // Chặn luồng chính để cho phép tương lai hoàn thành trước khi chương trình thoát
        try { Thread.sleep(3000); } catch (Exception e){}
    }
}
```

## Exercises
## Bài tập
1. Write a Node.js script using `fs.readFile` (callback version) and compare it against `fs.promises.readFile` (async/await variant). 
1. Viết một tập lệnh Node.js sử dụng `fs.readFile` (phiên bản gọi lại) và so sánh nó với `fs.promises.readFile` (biến thể bất đồng bộ/chờ đợi).
2. What is "Callback Hell" and how do Promises solve it?
2. "Địa ngục gọi lại" là gì và Lời hứa giải quyết nó như thế nào?
3. In Java, what is the difference between `Callable` and `Runnable`?
3. Trong Java, sự khác biệt giữa `Callable` và `Runnable` là gì?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Explain the Event Loop in detail (Macro tasks vs Micro tasks).
- Giải thích chi tiết về Vòng lặp sự kiện (Tác vụ vĩ mô và Tác vụ vi mô).
- How do you execute multiple Promises concurrently? (`Promise.all()` in JS, `CompletableFuture.allOf()` in Java).
- Làm thế nào để bạn thực hiện nhiều Lời hứa đồng thời? (`Promise.all()` trong JS, `CompletableFuture.allOf()` trong Java).
- What happens if an error occurs inside a Promise but there is no `.catch()` block? (Unhandled Promise Rejection).
- Điều gì xảy ra nếu có lỗi xảy ra bên trong Lời hứa nhưng không có khối `.catch()`? (Từ chối lời hứa không được xử lý).
