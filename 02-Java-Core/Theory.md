# 📖 Java Nâng Cao - Collections, Generics & Concurrency

> **Tuần 2 | Mức độ: Quan trọng cho mọi Java Developer**

---

## 1. Java Collections Framework

### 1.1. Collections Framework là gì?
Java Collections Framework (JCF) là một **kiến trúc thống nhất** để lưu trữ và thao tác với các nhóm đối tượng (group of objects). Nó bao gồm các Interfaces, Implementations (Classes) và Algorithms (như sắp xếp, tìm kiếm).

#### 1. Tại sao cần Collections Framework?
*   **Tái sử dụng cấu trúc dữ liệu**: Không cần tự xây dựng LinkedList, Stack, Queue từ đầu.
*   **Hiệu năng tối ưu (Performance)**: Các implementations được tối ưu hóa kỹ lưỡng bởi Oracle.
*   **Tính linh hoạt (Interoperability)**: Các collection có thể hoán đổi cho nhau thông qua interface chung (`List`, `Set`, `Map`).

#### 2. Nếu không sử dụng Collections Framework thì thay thế bằng gì?
Trước JCF, lập trình viên phải dùng **mảng thuần (raw arrays)** hoặc tự xây dựng cấu trúc dữ liệu thủ công. Mảng có nhược điểm cố định về kích thước và không có sẵn các phương thức tiện ích như `sort()`, `contains()`, `remove()`.

#### 3. Cấu trúc tổng quan

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

### 2.1. List là gì?
`List` là một **ordered collection (danh sách có thứ tự)**, cho phép lưu trữ các phần tử trùng lặp (duplicate). Hai implementation phổ biến nhất là `ArrayList` và `LinkedList`.

#### 1. Tại sao cần phân biệt ArrayList và LinkedList?
Mỗi loại có đặc điểm hiệu năng khác nhau. Việc chọn đúng loại giúp tối ưu bộ nhớ và tốc độ xử lý:

| Đặc điểm | ArrayList | LinkedList |
| :--- | :--- | :--- |
| Cấu trúc bên trong | Mảng động (Dynamic Array) | Danh sách liên kết đôi (Doubly Linked List) |
| Truy cập ngẫu nhiên `get(i)` | **O(1)** - Rất nhanh | O(n) - Phải duyệt từ đầu |
| Thêm/Xóa ở đầu/giữa | O(n) - Phải dịch chuyển phần tử | **O(1)** - Chỉ cập nhật con trỏ |
| Bộ nhớ sử dụng | Ít hơn (chỉ lưu data) | Nhiều hơn (lưu data + 2 con trỏ prev/next) |
| **Khi nào dùng** | **95% trường hợp** - đọc nhiều, sửa ít | Hàng đợi sự kiện, thêm/xóa liên tục ở đầu/cuối |

#### 2. Nếu không dùng ArrayList/LinkedList thì thay thế bằng gì?
Có thể dùng **mảng thuần (raw array)**, nhưng phải tự quản lý kích thước và không có các method tiện ích. Hoặc dùng `Vector` (thread-safe ArrayList legacy), nhưng hiệu năng thấp hơn do synchronized toàn bộ method.

### 2.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: ArrayList - Hứng kết quả từ Repository
ArrayList là lựa chọn mặc định khi JPA/Hibernate trả về danh sách Entity:

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        // Kết quả trả về từ JPA/Hibernate ngầm định sử dụng ArrayList để lưu dữ liệu
        List<OrderResponse> orders = orderService.getAllOrders(); 
        return ResponseEntity.ok(orders);
    }
}
```

#### Ví dụ 2: LinkedList - Hàng đợi sự kiện có kích thước cố định
LinkedList phù hợp khi cần thêm/xóa phần tử ở đầu/cuối liên tục với tần suất cao:

```java
@Service
public class MetricService {
    // LinkedList hỗ trợ add/remove phần tử đầu/cuối với độ phức tạp O(1)
    private final List<AnalyticEvent> eventQueue = new LinkedList<>();

    public synchronized void logEvent(AnalyticEvent event) {
        eventQueue.add(event); // Thêm vào cuối O(1)
        if (eventQueue.size() > 100) {
            eventQueue.remove(0); // Xóa phần tử đầu tiên O(1) để giữ dung lượng 100
        }
    }
}
```

---

## 3. Set - Không Cho Duplicate

### 3.1. Set là gì?
`Set` là một collection **không cho phép chứa phần tử trùng lặp**. Tính duy nhất được xác định bởi contract giữa `equals()` và `hashCode()`. Các implementation phổ biến gồm `HashSet`, `LinkedHashSet` và `TreeSet`.

#### 1. Tại sao cần Set?
*   **Đảm bảo tính duy nhất (Uniqueness)**: Tự động loại bỏ duplicate mà không cần kiểm tra thủ công.
*   **Kiểm tra tồn tại nhanh**: `HashSet.contains()` hoạt động với độ phức tạp **O(1)** thay vì O(n) của `List.contains()`.
*   **Giao/Hợp/Hiệu tập hợp**: Set hỗ trợ các phép toán tập hợp chuẩn qua `addAll()`, `retainAll()`, `removeAll()`.

#### 2. Phân biệt các loại Set

| Implementation | Thứ tự phần tử | Hiệu năng | Khi nào dùng |
| :--- | :--- | :--- | :--- |
| `HashSet` | Không có thứ tự | O(1) cho add/remove/contains | **Mặc định** - Khi không cần thứ tự |
| `LinkedHashSet` | Theo thứ tự thêm vào | O(1) | Cần duy trì thứ tự chèn |
| `TreeSet` | Sắp xếp tự nhiên hoặc theo Comparator | O(log n) | Cần thứ tự sắp xếp |

#### 3. Nếu không dùng Set thì thay thế bằng gì?
Có thể dùng `List` và tự kiểm tra trùng lặp trước khi thêm (`if (!list.contains(element))`), nhưng `contains()` của List là O(n), dẫn đến hiệu năng kém hơn nhiều khi tập dữ liệu lớn.

### 3.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: HashSet - Lưu trữ Roles/Authorities trong Spring Security
Sử dụng `HashSet` để đảm bảo không có Role trùng lặp và kiểm tra quyền hạn nhanh với O(1):

```java
public class CustomUserDetails implements UserDetails {
    private User user; // JPA Entity

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Dùng HashSet để đảm bảo không bị trùng lặp Role
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> 
            authorities.add(new SimpleGrantedAuthority(role.getName()))
        );
        return authorities;
    }
}
```

#### Ví dụ 2: TreeSet - Sắp xếp sản phẩm tự động theo giá
`TreeSet` tự động sắp xếp phần tử theo `Comparator` được chỉ định khi khởi tạo:

```java
// Sắp xếp các Product theo giá tăng dần tự động khi add vào Set
Set<Product> sortedProducts = new TreeSet<>(Comparator.comparing(Product::getPrice));
sortedProducts.addAll(productRepository.findAll());
```

---

## 4. Map - Key-Value Pairs

### 4.1. Map là gì?
`Map` là một cấu trúc dữ liệu lưu trữ dữ liệu dưới dạng **cặp Key-Value**, trong đó mỗi key là duy nhất. `Map` **không** implement interface `Collection` nhưng vẫn là một phần của Java Collections Framework.

#### 1. Tại sao cần Map?
*   **Tra cứu cực nhanh (O(1))**: Tìm kiếm theo key thay vì duyệt tuần tự như List.
*   **Mô hình hóa dữ liệu có liên kết**: Phù hợp để biểu diễn các mối quan hệ 1-1 (ID → Object, Token → Session...).
*   **Gom nhóm dữ liệu (Grouping)**: Kết hợp với Stream API để nhóm dữ liệu theo tiêu chí.

#### 2. Phân biệt các loại Map

| Implementation | Thread-safe | Thứ tự key | Cho phép null key | Khi nào dùng |
| :--- | :--- | :--- | :--- | :--- |
| `HashMap` | ❌ Không | Không có | ✅ Có | **Mặc định** - Single-thread |
| `LinkedHashMap` | ❌ Không | Thứ tự chèn | ✅ Có | Cần duy trì thứ tự chèn (LRU Cache) |
| `TreeMap` | ❌ Không | Sắp xếp theo key | ❌ Không | Cần key được sắp xếp |
| `ConcurrentHashMap` | ✅ Có | Không có | ❌ Không | **Multi-thread** - In-memory Cache |
| `Hashtable` | ✅ Có (legacy) | Không có | ❌ Không | Tránh dùng - Legacy, thay bằng `ConcurrentHashMap` |

#### 3. Nếu không dùng Map thì thay thế bằng gì?
Có thể dùng hai `List` song song (một list key, một list value) và tìm kiếm theo index tương ứng, nhưng tra cứu sẽ là O(n) thay vì O(1). Đây là cách làm không hiệu quả và khó bảo trì.

### 4.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: ConcurrentHashMap - In-memory Session Cache
Dùng `ConcurrentHashMap` (Thread-safe) làm bộ nhớ đệm tạm thời thay vì Redis khi app quy mô nhỏ:

```java
@Service
public class SessionManager {
    // ConcurrentHashMap đảm bảo an toàn đa luồng (thread-safe) khi nhiều Request gọi tới cùng lúc
    private final Map<String, UserSession> sessionCache = new ConcurrentHashMap<>();

    public void saveSession(String token, UserSession session) {
        sessionCache.put(token, session); // O(1)
    }

    public UserSession getSession(String token) {
        return sessionCache.get(token); // O(1)
    }
}
```

#### Ví dụ 2: HashMap + Stream groupingBy - Gom nhóm Order theo trạng thái
Kết hợp `Stream API` với `Collectors.groupingBy()` để tạo `Map` phân loại dữ liệu:

```java
// Gom nhóm Order trong Service
public Map<String, List<Order>> getOrdersGroupedByStatus() {
    List<Order> orders = orderRepository.findAll();
    // Trả về Map chứa danh sách các đơn hàng gom theo trạng thái (PENDING, COMPLETED...)
    return orders.stream().collect(Collectors.groupingBy(Order::getStatus));
}
```

---

## 5. Queue & Deque

### 5.1. Queue & Deque là gì?
`Queue` là một cấu trúc dữ liệu theo nguyên tắc **FIFO (First-In, First-Out)** - phần tử nào vào trước sẽ được lấy ra trước. `Deque` (Double-Ended Queue) mở rộng `Queue`, cho phép thêm/xóa ở **cả hai đầu**.

#### 1. Tại sao cần Queue & Deque?
*   **Mô hình hóa hàng đợi xử lý (Processing Queue)**: Đảm bảo thứ tự xử lý công bằng (ai đến trước được phục vụ trước).
*   **Kiểm soát luồng xử lý (Flow Control)**: `BlockingQueue` giúp điều phối tốc độ giữa Producer và Consumer, tránh quá tải.
*   **Hàng đợi ưu tiên (Priority Queue)**: `PriorityQueue` cho phép xử lý tác vụ quan trọng hơn trước.

#### 2. Phân biệt các loại Queue

| Implementation | Đặc điểm | Khi nào dùng |
| :--- | :--- | :--- |
| `LinkedList` | FIFO cơ bản, cũng implement `Deque` | Hàng đợi đơn giản |
| `ArrayDeque` | Nhanh hơn `LinkedList`, hiệu quả bộ nhớ | Stack hoặc Queue hiệu năng cao |
| `PriorityQueue` | Heap-based, tự sắp xếp theo ưu tiên | Xử lý tác vụ theo độ ưu tiên (VIP, Scheduler) |
| `LinkedBlockingQueue` | Thread-safe, block khi rỗng/đầy | **Producer-Consumer pattern** trong đa luồng |

#### 3. Nếu không dùng Queue thì thay thế bằng gì?
Có thể mô phỏng FIFO bằng `ArrayList` với `add()` ở cuối và `remove(0)` ở đầu, nhưng `remove(0)` trên `ArrayList` là **O(n)** do phải dịch chuyển phần tử. `LinkedBlockingQueue` cung cấp blocking semantics mà không thể tự xây dựng một cách an toàn với `List`.

### 5.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: PriorityQueue - Hàng đợi xử lý giao dịch theo độ ưu tiên VIP
`PriorityQueue` tự động sắp xếp nội bộ theo `Comparator`, đảm bảo tác vụ ưu tiên cao luôn được xử lý trước:

```java
public class TransactionScheduler {
    // PriorityQueue sẽ tự động sắp xếp các Transaction theo độ ưu tiên của khách hàng (VIP > NORMAL)
    private final PriorityQueue<TransactionRequest> vipQueue = new PriorityQueue<>(
        Comparator.comparing(TransactionRequest::getCustomerTier).reversed()
    );

    public void addRequest(TransactionRequest request) {
        vipQueue.offer(request);
    }

    public void processNextTransaction() {
        TransactionRequest next = vipQueue.poll(); // Luôn lấy ra transaction của khách hàng VIP nhất
        if (next != null) {
            execute(next);
        }
    }
}
```

#### Ví dụ 2: BlockingQueue - Mô hình Producer-Consumer xử lý Log ngầm
`LinkedBlockingQueue` phối hợp giữa luồng đẩy log (Producer) và luồng gửi log (Consumer) mà không cần `synchronized` thủ công:

```java
@Component
public class AsyncLogProcessor {
    // Hàng đợi chặn có kích thước tối đa 1000
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>(1000);

    public void pushLog(String log) {
        logQueue.offer(log); // Non-blocking
    }

    @Async // Chạy trên một background thread riêng biệt của Spring
    public void startProcessing() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            // Hầm này sẽ "block/treo" luồng hiện tại nếu queue rỗng cho đến khi có log mới
            String log = logQueue.take(); 
            sendToLogServer(log);
        }
    }
}
```

---

## 6. Java Generics & Wildcards

### 6.1. Java Generics là gì?
Generics cho phép **tham số hóa kiểu dữ liệu (parameterized types)**. Thay vì chỉ định cứng một kiểu dữ liệu cụ thể (như `String`, `Integer`, `Employee`), ta sử dụng các ký hiệu đại diện (placeholders như `T` - Type, `E` - Element, `K` - Key, `V` - Value). Kiểu dữ liệu thực tế sẽ được truyền vào khi ta khởi tạo class hoặc gọi method.

#### 1. Tại sao cần sử dụng Generics?
*   **Type Safety (An toàn kiểu dữ liệu) ở Compile-time**: Giúp phát hiện sai sót về kiểu dữ liệu ngay khi viết code thay vì đợi đến lúc chạy chương trình (Runtime).
*   **Loại bỏ việc ép kiểu thủ công (Eliminate Casts)**: Compiler tự động kiểm tra và chuyển đổi kiểu dữ liệu phù hợp khi lấy ra khỏi Collection.
*   **Tái sử dụng mã nguồn (Code Reusability)**: Một Class, Interface hoặc Method generic có thể xử lý nhiều kiểu dữ liệu khác nhau.

#### 2. Nếu không sử dụng Generics thì thay thế bằng gì?
Trước Java 5 (khi chưa có Generics), Java sử dụng lớp cha cao nhất là `Object` làm đại diện.
*   **Cách thay thế bằng Object (Raw Types)**:
    ```java
    // Không dùng Generics (dễ lỗi Runtime)
    List list = new ArrayList();
    list.add("Hello");
    list.add(100); // Không bị compiler ngăn cản dù chứa 2 kiểu khác nhau

    String s1 = (String) list.get(0); // Bắt buộc phải ép kiểu thủ công
    String s2 = (String) list.get(1); // Ném ra ClassCastException tại Runtime!
    ```

---

### 6.2. Wildcards trong Generics (`?`)
Ký tự `?` đại diện cho một **kiểu dữ liệu chưa xác định (unknown type)**. Nó được sử dụng làm kiểu dữ liệu của tham số phương thức, trường, hoặc biến cục bộ.

#### 1. Tại sao cần Wildcards?
Trong Java, Generics có tính chất **Invariant (Bất biến)**. Tức là dù `Integer` là subclass của `Number`, thì `List<Integer>` **KHÔNG PHẢI** là subclass của `List<Number>`. 
Do đó, nếu một phương thức nhận `List<Number>`, bạn không thể truyền `List<Integer>` vào. Để giải quyết sự thiếu linh hoạt này, Java đưa ra Wildcards.

#### 2. Phân loại Wildcards & Ứng dụng thực tế
Có 3 dạng Wildcard chính:

| Dạng Wildcard | Cú pháp | Ý nghĩa | Khi nào dùng (Quy tắc PECS) |
| :--- | :--- | :--- | :--- |
| **Unbounded** | `?` | Bất kỳ kiểu nào | Chỉ thao tác bằng các phương thức của `Object` hoặc ko phụ thuộc kiểu (như `size()`). |
| **Upper Bounded** | `? extends T` | Kiểu `T` hoặc bất kỳ lớp con (subclass) nào của `T` | **READ Only** (Producer) - Đọc dữ liệu ra dưới dạng `T`. Không thể ghi dữ liệu vào list. |
| **Lower Bounded** | `? super T` | Kiểu `T` hoặc bất kỳ lớp cha (superclass) nào của `T` | **WRITE Only** (Consumer) - Ghi dữ liệu kiểu `T` vào list. |

*   **Quy tắc PECS (Producer Extends, Consumer Super)**:
    *   *Producer* (cung cấp dữ liệu để đọc): Dùng `? extends T`.
    *   *Consumer* (tiêu thụ dữ liệu để ghi vào): Dùng `? super T`.

#### 3. Nếu không có Wildcards thì thay thế bằng gì?
Có thể thay thế bằng **Generic Methods** sử dụng tham số kiểu xác định (ví dụ `<T extends Number>`).
*   *Ví dụ thay thế*:
    ```java
    // Dùng Wildcard
    public double sum(List<? extends Number> list) { ... }

    // Thay thế bằng Generic Method (không dùng wildcard)
    public <T extends Number> double sumGeneric(List<T> list) { ... }
    ```
    Tuy nhiên, Generic Method đôi khi làm cú pháp phức tạp hơn và khó biểu đạt các mối quan hệ kiểu dữ liệu phức tạp hoặc đa cấp.

---

### 6.3. Ví dụ cụ thể trong Spring Boot

Trong thực tế phát triển dự án Spring Boot, Generics & Wildcards được áp dụng rộng rãi để viết code sạch và tái sử dụng tốt. Dưới đây là 3 ví dụ kinh điển:

#### Ví dụ 1: Chuẩn hóa dữ liệu trả về API với `ApiResponse<T>` (Generics)
Đây là cách phổ biến nhất để chuẩn hóa cấu trúc JSON trả về cho frontend:

```java
// 1. Định nghĩa Generic Class đại diện cho cấu trúc Response
public class ApiResponse<T> {
    private int code;
    private String message;
    private T result; // T có thể là bất cứ DTO nào (UserDTO, ProductDTO, List...)

    public ApiResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
    // Getter & Setter...
}

// 2. Sử dụng trong Controller
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/users/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable String id) {
        UserResponse user = new UserResponse("John Doe", "john@example.com");
        // Tự động ép kiểu result thành UserResponse
        return new ApiResponse<>(200, "Success", user);
    }

    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = List.of(new UserResponse("John", "john@eg.com"));
        // Tự động ép kiểu result thành List<UserResponse>
        return new ApiResponse<>(200, "Success", list);
    }
}
```

#### Ví dụ 2: Viết Generic CRUD Service (`BaseService<T, ID>`)
Tái sử dụng lại logic nghiệp vụ cơ bản cho nhiều Entity khác nhau (giống như Spring Data JPA `JpaRepository<T, ID>` hoạt động):

```java
// 1. Base Service Interface dùng Generics
public interface BaseService<T, ID> {
    T findById(ID id);
    T save(T entity);
    void deleteById(ID id);
}

// 2. Implement cụ thể cho một Entity
@Service
public class UserServiceImpl implements BaseService<User, Long> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
```

#### Ví dụ 3: Xử lý Polymorphism trong DTO với Wildcard (`? extends T`)
Giả sử ta có hệ thống Import dữ liệu từ Excel/CSV. Có nhiều loại DTO import kế thừa từ `BaseImportDTO` (như `UserImportDTO`, `ProductImportDTO`). Ta cần viết một Service chung để Validate và lưu dữ liệu.

```java
// Base class cho các DTO import
public abstract class BaseImportDTO {
    public abstract boolean isValid();
}

// Các class con kế thừa
public class UserImportDTO extends BaseImportDTO {
    private String email;
    @Override
    public boolean isValid() { return email != null && email.contains("@"); }
}

@Service
public class ImportService {
    // Sử dụng Upper Bounded Wildcard (? extends BaseImportDTO) để đọc dữ liệu linh hoạt
    // Phương thức này có thể nhận vào List<UserImportDTO> hoặc List<ProductImportDTO>
    public void processImportData(List<? extends BaseImportDTO> importList) {
        for (BaseImportDTO item : importList) {
            if (item.isValid()) {
                // Đọc thông tin và xử lý... (chỉ READ, không thêm phần tử vào importList)
                System.out.println("Processing valid item");
            }
        }
    }
}
```
> [!IMPORTANT]
> Trong ví dụ trên, nếu bạn khai báo `processImportData(List<BaseImportDTO> importList)`, bạn sẽ **không thể** truyền một biến kiểu `List<UserImportDTO>` vào phương thức này do tính bất biến (Invariant) của Generics. Việc sử dụng `? extends BaseImportDTO` là bắt buộc để hỗ trợ tính đa hình (Polymorphism).

---

## 7. Exception Handling

### 7.1. Exception trong Java là gì?
`Exception` là một sự kiện bất thường xảy ra trong quá trình thực thi chương trình, làm gián đoạn luồng xử lý bình thường. Java sử dụng cơ chế **try-catch-finally** và hệ thống phân cấp kế thừa để quản lý Exception.

#### 1. Tại sao cần Exception Handling?
*   **Tách biệt logic nghiệp vụ và xử lý lỗi**: Code chính không bị "nhiễm" bởi các đoạn xử lý lỗi.
*   **Thông báo lỗi rõ ràng cho client**: Thay vì trả về StackTrace thô, ta trả về JSON lỗi có cấu trúc với HTTP status code phù hợp.
*   **Tập trung hóa xử lý lỗi (Centralized Error Handling)**: Một nơi duy nhất (`@RestControllerAdvice`) xử lý mọi exception trong toàn bộ ứng dụng.

#### 2. Phân loại Exception trong Spring Boot

| Loại | Ví dụ | Cần khai báo `throws`? | Spring `@Transactional` mặc định |
| :--- | :--- | :--- | :--- |
| **Checked Exception** (`Exception`) | `IOException`, `SQLException` | ✅ Bắt buộc | ❌ **KHÔNG rollback** |
| **Unchecked Exception** (`RuntimeException`) | `NullPointerException`, `IllegalArgumentException` | ❌ Không cần | ✅ **Tự động ROLLBACK** |

> [!IMPORTANT]
> Nếu muốn Spring `@Transactional` rollback khi gặp Checked Exception, phải khai báo rõ: `@Transactional(rollbackFor = Exception.class)`.

#### 3. Nếu không dùng `@RestControllerAdvice` thì thay thế bằng gì?
Phải `try-catch` trong từng Controller method và tự xây dựng response lỗi, dẫn đến code bị lặp lại (boilerplate) và khó bảo trì khi cần thay đổi format lỗi toàn hệ thống.

### 7.2. Ví dụ cụ thể trong Spring Boot

#### Global Exception Handling với `@RestControllerAdvice`
Bộ đôi `@RestControllerAdvice` và `@ExceptionHandler` giúp kiểm soát lỗi tập trung, tránh trả về StackTrace thô lỗ cho client:

```java
// 1. Custom Business Exception (Unchecked)
public class InsufficientFundsException extends RuntimeException {
    private final double amountShort;

    public InsufficientFundsException(double amountShort) {
        super("Giao dịch thất bại. Tài khoản thiếu: " + amountShort);
        this.amountShort = amountShort;
    }
    public double getAmountShort() { return amountShort; }
}

// 2. Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi nghiệp vụ cụ thể
    @ExceptionHandler(InsufficientFundsException.class)
    
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Bắt lỗi xác thực dữ liệu đầu vào (Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldError().getDefaultMessage();
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Bắt mọi lỗi hệ thống chưa được định nghĩa khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Lỗi hệ thống nghiêm trọng, vui lòng liên hệ admin!",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

---

## 8. Java I/O & File Systems

### 8.1. Java I/O là gì?
Java cung cấp hai thế hệ API I/O:
*   **Java I/O (java.io)**: API truyền thống, stream-based, blocking.
*   **Java NIO.2 (java.nio.file)**: API hiện đại từ Java 7+, hỗ trợ non-blocking I/O, cung cấp `Path`, `Files`, `Paths` với hiệu năng và tính năng vượt trội.

#### 1. Tại sao cần NIO.2 thay vì I/O truyền thống?
*   **Xử lý đường dẫn trừu tượng (Path API)**: `Path` thay thế `File`, hoạt động nhất quán trên mọi hệ điều hành (Windows/Linux).
*   **Sao chép/Di chuyển file an toàn**: `Files.copy()`, `Files.move()` hỗ trợ `CopyOption` như `REPLACE_EXISTING`.
*   **Tích hợp với Spring Resources**: Spring Boot cung cấp `ResourceLoader` và `ClassPathResource` để đọc file trong classpath (bên trong file `.jar`) một cách an toàn.

#### 2. Nếu không dùng NIO.2 & Spring ResourceLoader thì thay thế bằng gì?
Khi đóng gói ứng dụng thành file `.jar`, các file trong `src/main/resources` không còn nằm trên hệ thống file vật lý nữa mà nằm bên trong archive. `FileReader` với đường dẫn vật lý sẽ **thất bại** trong môi trường production. Ta phải dùng `ClassPathResource` hoặc `ResourceLoader` của Spring.

### 8.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: Đọc File từ Resources (Classpath)
Sử dụng `ResourceLoader` để đọc file trong `src/main/resources`, hoạt động cả khi chạy local lẫn khi build thành `.jar`:

```java
@Service
public class TemplateService {
    @Autowired
    private ResourceLoader resourceLoader;

    public String getEmailTemplate() throws IOException {
        // Tự động tìm kiếm tài nguyên trong file JAR đóng gói
        Resource resource = resourceLoader.getResource("classpath:templates/welcome.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
```

#### Ví dụ 2: Upload File thông qua Controller & NIO.2
Spring MVC bọc file tải lên bằng interface `MultipartFile`. Ta sử dụng NIO.2 (`java.nio.file.Files`) để lưu file lên ổ đĩa server:

```java
@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo thư mục uploads nếu chưa tồn tại
            Files.createDirectories(this.fileStorageLocation);
            
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            
            // Sao chép luồng dữ liệu đầu vào và lưu đè lên file cũ nếu trùng tên
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return ResponseEntity.ok("Tải file lên thành công: " + fileName);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể lưu trữ file!");
        }
    }
}
```

---

## 9. Multithreading & Asynchronous in Spring Boot

### 9.1. Multithreading & Async là gì?
**Multithreading** cho phép thực thi nhiều tác vụ đồng thời trên các luồng (Thread) riêng biệt trong JVM. **Asynchronous (Bất đồng bộ)** là cơ chế kích hoạt một tác vụ và **không chờ** nó hoàn thành, giúp luồng gọi tiếp tục xử lý công việc khác.

#### 1. Tại sao cần Multithreading & Async trong Spring Boot?
*   **Tăng throughput (Thông lượng)**: Cho phép xử lý nhiều request đồng thời thay vì tuần tự.
*   **Tránh blocking luồng chính**: Các tác vụ tốn thời gian (gửi email, gọi API bên ngoài) chạy trên luồng nền, không làm chậm response trả về cho client.
*   **Giảm latency trong Microservices**: Gọi song song nhiều service độc lập thay vì gọi tuần tự.

#### 2. Nếu không dùng `@Async` & `ThreadPoolTaskExecutor` thì thay thế bằng gì?
Tạo `new Thread()` thủ công **bỏ qua Spring IoC Container**, dẫn đến:
*   Không có quản lý vòng đời (lifecycle management).
*   Không có giới hạn số luồng tối đa, dễ gây **OutOfMemoryError** khi có nhiều request.
*   Không thể inject `@Autowired` bean vào Thread thủ công.

Thay vào đó, ta định nghĩa `ThreadPoolTaskExecutor` và dùng `@Async` để Spring quản lý toàn bộ vòng đời luồng.

### 9.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: `@Async` với ThreadPoolTaskExecutor - Gửi Notification ngầm
Tác vụ gửi thông báo chạy ngầm trên một luồng riêng, không block HTTP response trả về cho client:

```java
// 1. Cấu hình Thread Pool
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);       // Số luồng tối thiểu duy trì
        executor.setMaxPoolSize(15);       // Số luồng tối đa khi quá tải
        executor.setQueueCapacity(500);    // Hàng đợi chứa task trước khi tạo luồng mới
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}

// 2. Sử dụng trong Service
@Service
public class NotificationService {

    @Async("taskExecutor") // Chạy bất đồng bộ trên một luồng thuộc taskExecutor
    public void sendNotification(String userId, String message) {
        System.out.println("Đang gửi thông báo cho: " + userId + " trên Thread: " + Thread.currentThread().getName());
        // Giả lập thời gian gửi
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
    }
}
```

#### Ví dụ 2: `CompletableFuture` - Gọi song song nhiều Microservice
Trong kiến trúc Microservices, API Gateway gọi đồng thời nhiều dịch vụ độc lập rồi gộp kết quả, giúp giảm độ trễ tổng thể (Latency):

```java
@Service
public class UserAggregatorService {

    @Autowired
    private RestTemplate restTemplate;

    public CompletableFuture<UserInfo> getUserInfoAsync(Long userId) {
        return CompletableFuture.supplyAsync(() -> 
            restTemplate.getForObject("http://user-service/api/users/" + userId, UserInfo.class)
        );
    }

    public CompletableFuture<WalletBalance> getWalletBalanceAsync(Long userId) {
        return CompletableFuture.supplyAsync(() -> 
            restTemplate.getForObject("http://wallet-service/api/wallets/" + userId, WalletBalance.class)
        );
    }

    // Gọi đồng thời cả 2 API cùng lúc
    public UserProfileAggregate getAggregateProfile(Long userId) {
        CompletableFuture<UserInfo> userInfoFuture = getUserInfoAsync(userId);
        CompletableFuture<WalletBalance> walletFuture = getWalletBalanceAsync(userId);

        // Chờ cả 2 tác vụ hoàn thành song song
        CompletableFuture.allOf(userInfoFuture, walletFuture).join();

        // Gộp kết quả
        return new UserProfileAggregate(userInfoFuture.join(), walletFuture.join());
    }
}
```

---

## 10. Java 8+ Features trong Spring Boot

### 10.1. Java 8+ Features là gì?
Java 8 (2014) là bản cập nhật lớn nhất trong lịch sử Java, mang đến **lập trình hàm (Functional Programming)** thông qua Lambda Expressions, Stream API, và Optional. Đây là nền tảng của phong cách code hiện đại trong Spring Boot.

#### 1. Tại sao cần Java 8+ Features?
*   **Code ngắn gọn và biểu cảm hơn**: Lambda và Stream API thay thế các vòng lặp `for` dài dòng bằng pipeline xử lý dữ liệu trực quan.
*   **Tránh `NullPointerException` (NPE)**: `Optional` buộc lập trình viên xử lý trường hợp dữ liệu rỗng một cách tường minh, thay vì để NPE xảy ra ngầm.
*   **Tích hợp sẵn trong Spring Boot**: JPA repositories trả về `Optional`, `@Async` trả về `CompletableFuture` - bắt buộc phải biết các tính năng này để làm việc hiệu quả với Spring.

#### 2. Các tính năng Java 8+ quan trọng nhất

| Tính năng | Mục đích | Ví dụ điển hình trong Spring Boot |
| :--- | :--- | :--- |
| **Lambda Expression** | Biểu diễn hàm vô danh (anonymous function) | `list.forEach(item -> process(item))` |
| **Stream API** | Xử lý collection theo kiểu pipeline | Filter Entity → Map sang DTO → Collect |
| **Optional\<T\>** | Bọc giá trị có thể null, tránh NPE | Kết quả `findById()` của JPA Repository |
| **Method Reference** | Tham chiếu method ngắn gọn | `Product::isActive`, `ProductDTO::new` |
| **CompletableFuture** | Xử lý bất đồng bộ có thể kết hợp | Gọi song song nhiều Microservice |

#### 3. Nếu không dùng Java 8+ Features thì thay thế bằng gì?
Phải dùng **vòng lặp `for-each` truyền thống** với biến tạm trung gian, code nhiều dòng hơn và dễ xảy ra lỗi (đặc biệt là quên kiểm tra `null`). Ví dụ thay thế `Optional` là kiểm tra `if (result != null)` thủ công - rất dễ bỏ sót.

### 10.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ 1: Stream API - Chuyển đổi Entity sang DTO
Ví dụ kinh điển nhất của Stream API trong Spring Boot là chuyển đổi danh sách Entity thành DTOs qua pipeline:

```java
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getActiveProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(Product::isActive) // 1. Lọc sản phẩm đang kích hoạt
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                )) // 2. Ánh xạ Entity -> DTO
                .collect(Collectors.toList()); // 3. Thu gom về danh sách
    }
}
```

#### Ví dụ 2: Optional - Xử lý dữ liệu Null-safe từ JPA
Spring Data JPA trả về `Optional` từ `findById()`. Dùng `Optional` để xử lý trường hợp không tìm thấy dữ liệu một cách thanh lịch:

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse getUserById(Long id) {
        // userRepository.findById(id) trả về Optional<User>
        return userRepository.findById(id)
                .map(user -> new UserResponse(user.getName(), user.getEmail())) // Convert sang Response nếu có dữ liệu
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id)); // Ném ngoại lệ nghiệp vụ nếu rỗng
    }
}
```

---

*📌 Tiếp theo: [Interview-QA.md](Interview-QA.md) | [Practice-Exercises.md](Practice-Exercises.md)*
