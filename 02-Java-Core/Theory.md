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

### ArrayList (Thường dùng 95% trường hợp)
*   **Ví dụ thực tế Spring Boot:** ArrayList dùng để hứng kết quả DTOs trả về từ Database thông qua Repository/Service để trả về cho Client.
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

### LinkedList (Ít dùng hơn)
*   **Ví dụ thực tế Spring Boot:** LinkedList thường dùng khi bạn cần xử lý một hàng đợi sự kiện tạm thời (Event Queue) có kích thước cố định trong Service (cần thêm/xóa rất nhanh ở đầu hoặc cuối).
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

### HashSet
*   **Ví dụ thực tế Spring Boot:** Sử dụng Set để lưu trữ các vai trò/quyền hạn (Roles/Authorities) của người dùng trong Spring Security. Tránh trùng lặp quyền và kiểm tra quyền nhanh chóng với độ phức tạp $O(1)$.
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

### TreeSet
*   **Ví dụ thực tế Spring Boot:** TreeSet tự động sắp xếp các phần tử. Dùng khi cần hiển thị danh sách sản phẩm hoặc danh mục được phân loại tự động theo giá trị/tên.
```java
// Sắp xếp các Product theo giá tăng dần tự động khi add vào Set
Set<Product> sortedProducts = new TreeSet<>(Comparator.comparing(Product::getPrice));
sortedProducts.addAll(productRepository.findAll());
```

---

## 4. Map - Key-Value Pairs

### HashMap & ConcurrentHashMap
*   **Ví dụ thực tế Spring Boot:** 
    1.  Dùng `ConcurrentHashMap` (Thread-safe) làm bộ nhớ đệm (In-memory Cache) tạm thời cho các API Session hoặc Dynamic Configuration thay vì dùng Redis khi app nhỏ.
    2.  Dùng `groupingBy` để gom nhóm các Order theo CustomerId khi viết API báo cáo xuất dữ liệu.
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

// Gom nhóm Order trong Service
public Map<String, List<Order>> getOrdersGroupedByStatus() {
    List<Order> orders = orderRepository.findAll();
    // Trả về Map chứa danh sách các đơn hàng gom theo trạng thái (PENDING, COMPLETED...)
    return orders.stream().collect(Collectors.groupingBy(Order::getStatus));
}
```

---

## 5. Queue & Deque

### PriorityQueue (Hàng đợi ưu tiên)
*   **Ví dụ thực tế Spring Boot:** Dùng để thiết kế một Scheduler xử lý các Transaction hoặc Ticket/Task cần ưu tiên theo mức độ "VIP" của khách hàng.
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

### BlockingQueue (LinkedBlockingQueue)
*   **Ví dụ thực tế Spring Boot:** Dùng để hiện thực hóa mô hình **Producer-Consumer** xử lý Logs hoặc gửi Email/Notification ngầm (Background Tasks) bên trong JVM.
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

### 7.1. Phân loại Exception trong Spring Boot
*   **Unchecked Exception (RuntimeException):** Ví dụ: `NullPointerException`, `IllegalArgumentException`, `InsufficientFundsException`.
    *   *Đặc điểm:* Không cần khai báo `throws` trong phương thức. **Mặc định, Spring `@Transactional` sẽ tự động ROLLBACK giao dịch khi gặp RuntimeException**.
*   **Checked Exception (Exception):** Ví dụ: `IOException`, `SQLException`.
    *   *Đặc điểm:* Bắt buộc phải xử lý bằng `try-catch` hoặc khai báo `throws`. **Mặc định, Spring `@Transactional` KHÔNG rollback khi gặp Checked Exception** (trừ khi khai báo `@Transactional(rollbackFor = Exception.class)`).

### 7.2. Global Exception Handling thực tế trong Spring Boot
Sử dụng bộ đôi `@RestControllerAdvice` và `@ExceptionHandler` để kiểm soát lỗi tập trung, tránh trả về StackTrace thô lỗ cho client:

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

### 8.1. Đọc File từ Resources (Classpath) trong Spring Boot
Không sử dụng `FileReader` truyền thống vì khi đóng gói thành file `.jar`, đường dẫn vật lý sẽ bị thay đổi. Ta sử dụng Spring `ResourceLoader` hoặc `ClassPathResource` để đọc file trong thư mục `src/main/resources`:

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

### 8.2. Upload File thông qua Controller & NIO.2
Spring MVC bọc file tải lên bằng interface `MultipartFile`. Ta sử dụng thư viện NIO.2 (`java.nio.file.Files`) để lưu ghi file lên ổ đĩa của server:

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

### 9.1. Tạo Thread & Bất đồng bộ trong Spring Boot (`@Async`)
Tránh tạo Thread thủ công (`new Thread()`) vì sẽ bỏ qua khả năng quản lý tài nguyên của Spring IoC. Thay vào đó, ta định nghĩa một `ThreadPoolTaskExecutor` (Task Thread Pool) và sử dụng chú thích `@Async`.

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

### 9.2. Triển khai API Gateway Song Song với `CompletableFuture`
Trong kiến trúc Microservices, API Gateway thường phải gọi đồng thời nhiều dịch vụ độc lập khác nhau (Ví dụ: gọi dịch vụ User và ví Wallet) rồi gộp kết quả trả về, giúp giảm độ trễ tổng thể (Latency).

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

### 10.1. Stream API (Chuyển đổi Entity sang DTO)
Ví dụ kinh điển nhất của Stream API trong Spring Boot là chuyển đổi danh sách các thực thể cơ sở dữ liệu (Entities) thành DTOs trước khi gửi trả lại cho Controller.

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

### 10.2. Optional (Xử lý dữ liệu Null-safe từ JPA)
Spring Data JPA trả về dữ liệu tìm kiếm bằng `Optional`. Chúng ta dùng nó để xử lý khéo léo trường hợp không tìm thấy dữ liệu:

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
