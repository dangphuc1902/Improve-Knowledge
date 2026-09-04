# 🛠️ Common Java Design Patterns for System Design

> **Tài liệu tổng hợp & đi sâu 5 Design Patterns phổ biến nhất trong Thiết kế Hệ thống (System Design)**  
> *Các mẫu thiết kế: Singleton, Factory, Builder, Observer, Strategy.*

---

## 📌 Tổng Quan Nhanh (Summary Matrix)

| Pattern | Nhóm (GoF) | Mục tiêu cốt lõi | Từ khóa System Design |
| :--- | :--- | :--- | :--- |
| **Singleton** | Creational | Đảm bảo 1 instance duy nhất trong ứng dụng | Shared Resource, DB Connection Pool, Config, Cache |
| **Factory** | Creational | Che giấu logic khởi tạo đối tượng đằng sau Interface | Dynamic Object Creation, Multi-provider, Plugin Architecture |
| **Builder** | Creational | Khởi tạo đối tượng phức tạp theo dạng Fluent API | Immutable DTO, Complex Specs, Dynamic HTTP Request |
| **Observer** | Behavioral | Mối quan hệ 1-N, tự động thông báo khi có sự kiện | Event-Driven Architecture, Pub/Sub, Notification Engine |
| **Strategy** | Behavioral | Đóng gói & hoán đổi thuật toán linh hoạt tại runtime | Dynamic Business Rules, Multi-Payment, Shipping Engine |

---

## 1. Singleton Pattern (Creational - Khởi Tạo)

### 1.1. Bản chất & Vấn đề giải quyết
* **Bản chất**: Đảm bảo một class chỉ có **duy nhất 1 instance** tồn tại trong toàn bộ JVM/Application Context và cung cấp một điểm truy cập toàn cục (Global Access Point).
* **Vấn đề giải quyết**:
  * Tránh khởi tạo nhiều lần các đối tượng "nặng" (tốn CPU/RAM/Network).
  * Kiểm soát tập trung truy cập vào tài nguyên dùng chung (Shared Resources).

### 1.2. Cách triển khai chuẩn trong Java & Spring Boot

#### a) Trong Java thuần (Bill Pugh / Enum Singleton)
Cách khuyên dùng nhất trong Java thuần là **Enum Singleton** (chống lại Reflection & Serialization attack):

```java
public enum DatabaseConnectionPool {
    INSTANCE;

    private HikariDataSource dataSource;

    DatabaseConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("root");
        config.setPassword("secret");
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

#### b) Trong Spring Boot (Spring Managed Beans)
Spring IoC Container mặc định quản lý tất cả Bean `@Component`, `@Service`, `@Repository` ở **Singleton Scope**. Bạn **không cần** tự viết `getInstance()` thủ công:

```java
@Service
public class SystemConfigService {
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Tải cấu hình từ DB/File vào RAM khi ứng dụng khởi động
        configCache.put("MAX_RETRY", "3");
    }

    public String getConfig(String key) {
        return configCache.get(key);
    }
}
```

### 1.3. Ứng dụng thực tế trong System Design
* **Database Connection Pools**: HikariCP, DBCP, Druid.
* **Application Configuration Manager**: Đọc các file `application.yml` hoặc Centralized Config (Spring Cloud Config).
* **In-Memory Cache Manager**: Local cache duy nhất như Caffeine hoặc Guava Cache.

### ⚠️ Cạm bẫy cần tránh (Pitfalls)
* **Lưu trạng thái Request (Stateful Singleton)**: Không bao giờ lưu thông tin riêng biệt của user/request vào field của Singleton Bean. Trong môi trường đa luồng (Multi-threading), các thread sẽ ghi đè lẫn nhau (Data Corruption).

---

## 2. Factory Method Pattern (Creational - Khởi Tạo)

### 2.1. Bản chất & Vấn đề giải quyết
* **Bản chất**: Tách rời quá trình khởi tạo đối tượng khỏi logic sử dụng. Client chỉ giao tiếp qua Interface chung mà không cần biết class cụ thể nào đang được tạo.
* **Vấn đề giải quyết**:
  * Loại bỏ các chuỗi `if-else` / `switch-case` khởi tạo cứng nhắc.
  * Tuân thủ nguyên lý **Open/Closed Principle (OCP)**: Thêm loại sản phẩm mới mà không cần sửa code hiện có.

### 2.2. Cách triển khai chuẩn trong Spring Boot (Dynamic Map Wiring)

Spring IoC tự động tiêm tất cả các Beans implement cùng 1 Interface vào một `Map<String, Service>`:

```java
// 1. Interface chung
public interface StorageProvider {
    void upload(String fileName, byte[] content);
}

// 2. Concrete Implementations
@Service("s3")
public class S3StorageProvider implements StorageProvider {
    @Override
    public void upload(String fileName, byte[] content) {
        System.out.println("Uploading to AWS S3: " + fileName);
    }
}

@Service("gcs")
public class GcsStorageProvider implements StorageProvider {
    @Override
    public void upload(String fileName, byte[] content) {
        System.out.println("Uploading to Google Cloud Storage: " + fileName);
    }
}

// 3. Dynamic Factory
@Component
public class StorageFactory {
    @Autowired
    private Map<String, StorageProvider> storageMap; // Key: "s3", "gcs"

    public StorageProvider getProvider(String type) {
        StorageProvider provider = storageMap.get(type.toLowerCase());
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported storage type: " + type);
        }
        return provider;
    }
}
```

### 2.3. Ứng dụng thực tế trong System Design
* **Multi-Cloud File Storage**: Tự động switch giữa S3, Google Cloud Storage, Azure Blob Storage.
* **Notification Dispatcher**: Khởi tạo đúng kênh thông báo (Email, SMS, Push Notification, Zalo OA).
* **Document Exporters**: Tạo Exporter tương ứng cho các định dạng PDF, Excel, CSV.

---

## 3. Builder Pattern (Creational - Khởi Tạo)

### 3.1. Bản chất & Vấn đề giải quyết
* **Bản chất**: Cho phép xây dựng một đối tượng phức tạp từng bước một theo kiểu Fluent API (`.setA().setB().build()`).
* **Vấn đề giải quyết**:
  * Khắc phục hiện tượng **Telescoping Constructor** (constructor nạp chồng quá nhiều tham số gây nhầm lẫn).
  * Đảm bảo tính **Immutable (Bất biến)** cho đối tượng sau khi khởi tạo xong.

### 3.2. Cách triển khai chuẩn trong Java (Lombok `@Builder`)

```java
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class OrderRequestDTO {
    private final String orderId;
    private final String userId;
    private final List<String> itemIds;
    private final String couponCode; // Optional
    private final String note;       // Optional
}

// Sử dụng Fluent API rõ ràng, sạch sẽ:
OrderRequestDTO dto = OrderRequestDTO.builder()
        .orderId("ORD-8899")
        .userId("USR-1001")
        .itemIds(List.of("ITEM-1", "ITEM-2"))
        .couponCode("SUMMER2026") // Có thể bỏ qua nếu null
        .build();
```

### 3.3. Ứng dụng thực tế trong System Design
* **Complex API DTOs**: Tạo các object truyền dữ liệu giữa các microservices với nhiều field tùy chọn.
* **Query Builders**: Xây dựng câu truy vấn phức tạp (ElasticSearch Query Builder, JPA Specification Builder).
* **HTTP Client Request Builder**: RestTemplate / WebClient / OkHttpClient dựng Request Headers, Params, Body.

---

## 4. Observer Pattern (Behavioral - Hành Vi)

### 4.1. Bản chất & Vấn đề giải quyết
* **Bản chất**: Định nghĩa mối quan hệ **1-N (One-to-Many)** giữa các đối tượng. Khi một Subject (Publisher) thay đổi trạng thái, tất cả Observers (Subscribers) lắng nghe sẽ nhận thông báo tự động.
* **Vấn đề giải quyết**:
  * Tách rời (Decouple) luồng nghiệp vụ chính với các tác vụ ăn theo (Side-effects).
  * Là nền tảng chính của **Event-Driven Architecture (EDA)**.

### 4.2. Cách triển khai chuẩn trong Spring Boot (Spring ApplicationEvent)

```java
// 1. Domain Event
public record OrderPlacedEvent(String orderId, String userEmail, double totalAmount) {}

// 2. Publisher (Subject)
@Service
public class OrderService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void checkout(String orderId, String email, double amount) {
        // Luồng chính: Lưu đơn hàng
        System.out.println("Order saved to DB: " + orderId);

        // Bắn event thông báo - Không cần biết bên dưới ai đang xử lý
        eventPublisher.publishEvent(new OrderPlacedEvent(orderId, email, amount));
    }
}

// 3. Observers (Subscribers)
@Component
public class EmailNotificationListener {
    @EventListener
    public void sendEmail(OrderPlacedEvent event) {
        System.out.println("Sending confirmation email to " + event.userEmail());
    }
}

@Component
public class InventoryListener {
    @EventListener
    @Async // Chạy bất đồng bộ ở luồng riêng để không block API checkout
    public void updateInventory(OrderPlacedEvent event) {
        System.out.println("Deducting inventory for order " + event.orderId());
    }
}
```

### 4.3. Ứng dụng thực tế trong System Design
* **E-commerce Checkout Pipeline**: Sau khi Checkout thành công -> Bắn event để: Trừ tồn kho, Tích điểm thưởng, Gửi mail xác nhận, Log Audit.
* **Mở rộng ra Distributed Systems**: Sử dụng Message Brokers như **Apache Kafka**, **RabbitMQ**, **AWS SNS/SQS** hay **Redis Pub/Sub**.

---

## 5. Strategy Pattern (Behavioral - Hành Vi)

### 5.1. Bản chất & Vấn đề giải quyết
* **Bản chất**: Đóng gói các thuật toán / quy tắc nghiệp vụ biến đổi vào các class riêng biệt có chung 1 Interface, cho phép hoán đổi thuật toán linh hoạt tại runtime.
* **Vấn đề giải quyết**:
  * Thay thế các khối `if-else` / `switch` phức tạp về logic nghiệp vụ.
  * Giúp test từng thuật toán độc lập dễ dàng.

### 5.2. Cách triển khai chuẩn trong Spring Boot

```java
// 1. Strategy Interface
public interface PaymentStrategy {
    PaymentResult pay(double amount, String accountDetails);
}

// 2. Concrete Strategies
@Service("VNPAY")
public class VnPayStrategy implements PaymentStrategy {
    @Override
    public PaymentResult pay(double amount, String accountDetails) {
        return new PaymentResult(true, "VNPAY-TXN-12345");
    }
}

@Service("MOMO")
public class MomoStrategy implements PaymentStrategy {
    @Override
    public PaymentResult pay(double amount, String accountDetails) {
        return new PaymentResult(true, "MOMO-TXN-67890");
    }
}

// 3. Context Service sử dụng Strategy
@Service
public class PaymentService {
    @Autowired
    private Map<String, PaymentStrategy> paymentStrategies;

    public PaymentResult process(String provider, double amount, String account) {
        PaymentStrategy strategy = paymentStrategies.get(provider.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment provider: " + provider);
        }
        return strategy.pay(amount, account);
    }
}
```

### 5.3. Ứng dụng thực tế trong System Design
* **Payment Gateways Engine**: Xử lý cổng thanh toán đa dạng (VNPAY, Momo, Stripe, PayPal).
* **Shipping Fee Calculation**: Tính phí giao hàng theo thuật toán riêng của từng đơn vị vận chuyển (GHTK, GHN, ViettelPost).
* **Dynamic Promotion Rules**: Áp dụng các quy tắc giảm giá (Discounts: % off, Fixed amount off, Buy 1 Get 1).

---

## 💡 Case Study Tích Hợp: Kết hợp Factory + Strategy trong Microservices

Trong thực tế System Design, **Factory Pattern** và **Strategy Pattern** rất hay được kết hợp với nhau:
1. **Factory Pattern**: Chịu trách nhiệm **tìm và khởi tạo** đúng đối tượng Strategy (Object Creation).
2. **Strategy Pattern**: Chịu trách nhiệm **thực thi nghiệp vụ** cụ thể (Business Execution).

```
Client Request (Provider = "MOMO")
         │
         ▼
┌──────────────────┐      1. Get Strategy      ┌──────────────────────┐
│  PaymentFactory  │ ────────────────────────► │ Map<String, Strategy>│
└──────────────────┘                           └──────────────────────┘
         │                                                │
         │ 2. Returns MomoStrategy                        │
         ▼                                                ▼
┌──────────────────┐                           ┌──────────────────────┐
│ PaymentService   │ ──3. Execute pay() ─────► │    MomoStrategy      │
└──────────────────┘                           └──────────────────────┘
```

---

## 🔗 Tài liệu tham khảo liên quan trong Repository
* 📖 [`Theory.md`](file:///d:/myself/Improve-Knowledge/09-Design-Patterns/Theory.md) - Chi tiết lý thuyết 23 mẫu GoF Patterns.
* 📝 [`01-solid-clean-architecture.md`](file:///d:/myself/Improve-Knowledge/09-Design-Patterns/01-solid-clean-architecture.md) - Nguyên lý SOLID và Clean Architecture.
* 🏋️ [`Practice-Exercises.md`](file:///d:/myself/Improve-Knowledge/09-Design-Patterns/Practice-Exercises.md) - Bài tập thực hành Design Patterns.
