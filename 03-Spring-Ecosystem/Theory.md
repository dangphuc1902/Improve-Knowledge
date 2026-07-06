# 📖 Hibernate & JPA - Lý Thuyết, Interview & Bài Tập

> **Tuần 5 | ORM - Object Relational Mapping**

---

## 1. Hibernate Architecture

### 1.1. Hibernate & JPA là gì?
**JPA (Jakarta Persistence API)** là một **Specification (đặc tả/interface)** định nghĩa cách ánh xạ Java Objects sang bảng trong cơ sở dữ liệu quan hệ. **Hibernate** là **JPA Provider** phổ biến nhất - tức là implementation cụ thể của đặc tả JPA.

```
Java Application
    ↓
JPA API (jakarta.persistence.*)
    ↓
Hibernate (JPA Provider)
    ↓
JDBC
    ↓
Database
```

**JPA** = Specification (interface)
**Hibernate** = Implementation (concrete)
Hibernate có thể dùng standalone hoặc qua JPA API.

#### 1. Tại sao cần Hibernate/JPA?
*   **Loại bỏ boilerplate code JDBC**: Không cần viết thủ công `PreparedStatement`, `ResultSet`, mapping từng cột sang field Java.
*   **Object-Oriented approach**: Làm việc với Java Objects và quan hệ giữa chúng thay vì SQL thuần và các bảng.
*   **Database Independence**: JPQL (Java Persistence Query Language) chạy được trên mọi loại database mà không cần thay đổi code.

#### 2. Nếu không dùng Hibernate/JPA thì thay thế bằng gì?

| Tiêu chí | Hibernate/JPA | JDBC thuần |
| :--- | :--- | :--- |
| Viết SQL | Tự động sinh (hoặc JPQL) | Bắt buộc viết thủ công |
| Mapping Object | Tự động qua Annotation | Manual từng field |
| Cache | L1 (Session) + L2 (tùy chọn) | Không có |
| Lazy Loading | Hỗ trợ sẵn | Manual JOIN |
| Quản lý quan hệ | Tự động (Cascade, orphanRemoval) | Manual JOINs |
| Độc lập DB | ✅ (JPQL) | ❌ (SQL phụ thuộc DB) |

---

## 2. Entity Mapping

### 2.1. Entity Mapping là gì?
**Entity Mapping** là quá trình **ánh xạ (mapping)** một Java Class sang một bảng (table) trong Database thông qua các Annotation của JPA. Một Entity class đại diện cho một hàng (row) trong bảng.

#### 1. Tại sao cần Entity Mapping?
*   **Đồng bộ cấu trúc dữ liệu**: Định nghĩa cột, kiểu dữ liệu, ràng buộc (nullable, length) ngay trong Java code, giúp code và DB luôn nhất quán.
*   **Tự động DDL**: Hibernate có thể tự sinh `CREATE TABLE`, `ALTER TABLE` từ Entity (qua `spring.jpa.hibernate.ddl-auto`).
*   **Tránh lỗi tại runtime**: Các lỗi về cột/kiểu dữ liệu được phát hiện sớm tại startup thay vì khi chạy query.

#### 2. Các Annotation Entity Mapping quan trọng

| Annotation | Mục đích | Ví dụ |
| :--- | :--- | :--- |
| `@Entity` | Đánh dấu class là JPA Entity | `@Entity` |
| `@Table` | Cấu hình tên bảng, index | `@Table(name = "products")` |
| `@Id` | Đánh dấu Primary Key | `@Id` |
| `@GeneratedValue` | Chiến lược sinh ID | `@GeneratedValue(strategy = IDENTITY)` |
| `@Column` | Cấu hình chi tiết cột | `@Column(nullable = false, length = 200)` |
| `@Enumerated` | Lưu Enum dưới dạng String/Ordinal | `@Enumerated(EnumType.STRING)` |
| `@Lob` | Lưu dữ liệu lớn (Text, Blob) | `@Lob` |
| `@CreationTimestamp` | Tự động set khi tạo | `@CreationTimestamp` |
| `@UpdateTimestamp` | Tự động set khi cập nhật | `@UpdateTimestamp` |

#### 3. Nếu không dùng Entity Mapping thì thay thế bằng gì?
Phải tự viết SQL `CREATE TABLE` riêng và tự map thủ công từng cột sang field Java qua JDBC `ResultSet`. Code sẽ bị trùng lặp (định nghĩa cấu trúc ở cả SQL lẫn Java), dễ sai sót khi thêm/sửa cột.

### 2.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Entity đầy đủ với các Annotation mapping phổ biến

```java
@Entity
@Table(name = "products", 
       indexes = @Index(name = "idx_product_name", columnList = "name"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto-increment
    private Long id;

    @Column(name = "product_name", nullable = false, length = 200)
    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)  // Lưu "ACTIVE" thay vì số thứ tự 0, 1, 2
    private ProductStatus status;

    @Lob  // Lưu text dài không giới hạn
    private String description;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp  // Tự động set khi INSERT, không cho phép UPDATE
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp  // Tự động cập nhật mỗi khi entity thay đổi
    private LocalDateTime updatedAt;

    // Standard constructors, getters, setters...
}

public enum ProductStatus {
    ACTIVE, INACTIVE, DISCONTINUED
}
```

---

## 3. Relationships (Quan hệ)

### 3.1. JPA Relationships là gì?
**Relationships** là cách Hibernate biểu diễn các **mối quan hệ giữa các bảng** (1-1, 1-N, N-N) trong Java thông qua các Annotation. Thay vì viết JOIN trong SQL, ta làm việc với object references trong Java.

#### 1. Tại sao cần Relationships Mapping?
*   **Điều hướng object tự nhiên**: Truy cập dữ liệu liên quan qua `order.getItems()` thay vì viết JOIN SQL thủ công.
*   **Cascade tự động**: Khi lưu/xóa một đối tượng cha, Hibernate tự động thực hiện cùng thao tác trên các đối tượng con.
*   **Tính toàn vẹn dữ liệu (Data Integrity)**: `orphanRemoval = true` đảm bảo không có dữ liệu mồ côi (orphan records) còn sót lại trong DB.

#### 2. Phân loại Relationships

| Annotation | Ý nghĩa | Ví dụ thực tế | FetchType mặc định |
| :--- | :--- | :--- | :--- |
| `@OneToMany` | 1 Order có nhiều OrderItem | `Order → List<OrderItem>` | **LAZY** |
| `@ManyToOne` | Nhiều OrderItem thuộc 1 Order | `OrderItem → Order` | **EAGER** |
| `@ManyToMany` | Nhiều Student học nhiều Course | `Student ↔ Course` | **LAZY** |
| `@OneToOne` | 1 User có 1 UserProfile | `User → UserProfile` | **EAGER** |

#### 3. Nếu không dùng Relationships Mapping thì thay thế bằng gì?
Phải tự viết các câu SQL JOIN khi cần truy vấn dữ liệu liên quan, và tự duy trì foreign key khi insert/update/delete. Rủi ro cao về inconsistency dữ liệu.

### 3.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Mapping quan hệ đầy đủ trong hệ thống Order

```java
// @OneToMany - @ManyToOne (Order và OrderItems)
@Entity
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // Helper methods để maintain bidirectional consistency
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}

@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private BigDecimal unitPrice;
}

// @ManyToMany (Student và Course) - cần bảng trung gian
@Entity
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany
    @JoinTable(name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();
}

// @OneToOne
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}
```

---

## 4. FetchType: LAZY vs EAGER

### 4.1. FetchType là gì?
**FetchType** xác định **thời điểm** Hibernate tải dữ liệu liên quan (associated entities) từ Database: tải ngay lập tức khi load entity cha (`EAGER`) hay chỉ tải khi thực sự truy cập (`LAZY`).

#### 1. Tại sao cần quan tâm đến FetchType?
*   **Hiệu năng**: Dùng `EAGER` sai chỗ gây tải dư thừa hàng chục bảng khi chỉ cần một bảng.
*   **Tránh N+1 Problem**: Dùng `LAZY` không đúng cách dẫn đến N+1 queries (1 query lấy danh sách + N queries riêng lẻ cho từng item).
*   **Kiểm soát SQL sinh ra**: Hiểu FetchType giúp ta biết Hibernate đang thực thi bao nhiêu câu SQL và tối ưu hóa chúng.

#### 2. So sánh LAZY vs EAGER

| Tiêu chí | LAZY (Lười tải) | EAGER (Tải ngay) |
| :--- | :--- | :--- |
| Thời điểm tải | Khi truy cập thuộc tính lần đầu | Ngay khi load entity cha |
| Mặc định của | `@OneToMany`, `@ManyToMany` | `@ManyToOne`, `@OneToOne` |
| Hiệu năng | Tốt hơn khi không cần dữ liệu | Tốt khi luôn cần dữ liệu |
| Rủi ro | N+1 Problem | Tải quá nhiều dữ liệu |
| **Khuyến nghị** | **Ưu tiên dùng LAZY** | Chỉ dùng khi thực sự cần |

#### 3. Nếu không xử lý N+1 Problem thì hậu quả là gì?
Hệ thống load 100 Order → thực thi 1 + 100 = **101 queries** thay vì 1 query với JOIN. Ở scale lớn (1000 order), đây là **1001 queries** - thảm họa hiệu năng.

### 4.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: N+1 Problem và các cách giải quyết

```java
// ❌ LAZY gây ra N+1 Problem:
List<Order> orders = orderRepo.findAll();  // 1 query
for (Order o : orders) {
    o.getItems().size();  // N queries! (1 per order) - tổng N+1 queries
}

// ✅ Fix 1: JOIN FETCH trong JPQL - tải tất cả trong 1 query
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Optional<Order> findByIdWithItems(@Param("id") Long id);

// ✅ Fix 2: @EntityGraph - khai báo cấu trúc cần tải
@EntityGraph(attributePaths = {"items", "customer"})
List<Order> findAllWithDetails();

// ✅ Fix 3: Batch fetching - giảm N+1 thành N/batchSize queries
@BatchSize(size = 20)
@OneToMany private List<OrderItem> items;
```

> [!IMPORTANT]
> Luôn **ưu tiên LAZY** cho tất cả quan hệ và chỉ dùng `JOIN FETCH` hoặc `@EntityGraph` trong Repository method khi thực sự cần load dữ liệu liên quan. Tránh đặt `FetchType.EAGER` vì nó ảnh hưởng đến **mọi** câu query của entity đó.

---

## 5. Spring Data JPA - Repository

### 5.1. Spring Data JPA Repository là gì?
**Spring Data JPA Repository** là một lớp trừu tượng cung cấp sẵn các thao tác CRUD, Paging, Sorting thông qua **interface kế thừa**. Lập trình viên chỉ cần khai báo method name theo quy ước, Spring sẽ tự động sinh SQL tương ứng.

#### 1. Tại sao cần Spring Data JPA Repository?
*   **Zero boilerplate CRUD**: Kế thừa `JpaRepository<T, ID>` là có ngay `save()`, `findById()`, `findAll()`, `delete()`, `count()`...
*   **Derived Query**: Đặt tên method theo quy ước (`findByStatusAndName`) → Spring tự sinh SQL, không cần viết JPQL/SQL.
*   **Paging & Sorting tích hợp sẵn**: `findAll(Pageable pageable)` trả về `Page<T>` với metadata (tổng trang, tổng bản ghi...).

#### 2. Các cấp độ Repository Interface

| Interface | Cung cấp | Khi nào dùng |
| :--- | :--- | :--- |
| `Repository<T, ID>` | Marker interface (không có method) | Base cho custom |
| `CrudRepository<T, ID>` | Basic CRUD + `count()`, `existsById()` | Khi chỉ cần CRUD đơn giản |
| `PagingAndSortingRepository<T, ID>` | CRUD + Paging + Sorting | Khi cần phân trang |
| `JpaRepository<T, ID>` | Tất cả trên + `flush()`, `saveAll()`, `findAllById()` | **Thường dùng nhất** |

#### 3. Nếu không dùng Spring Data JPA thì thay thế bằng gì?
Phải tự viết class implement `EntityManager` với `em.persist()`, `em.find()`, `em.createQuery()`. Code nhiều hơn gấp 3-5 lần và dễ quên xử lý transaction, exception.

### 5.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Repository đầy đủ với Derived Query, JPQL, Native SQL và Paging

```java
// Kế thừa JpaRepository để có sẵn CRUD + Paging
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Derived query methods - Spring tự generate SQL từ method name
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByNameContainingIgnoreCase(String name);
    Optional<Product> findByNameAndStatus(String name, ProductStatus status);
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    long countByStatus(ProductStatus status);
    boolean existsByName(String name);

    // Custom JPQL query
    @Query("SELECT p FROM Product p WHERE p.price < :maxPrice AND p.status = 'ACTIVE' ORDER BY p.price")
    List<Product> findActiveProductsUnder(@Param("maxPrice") BigDecimal maxPrice);

    // Native SQL - dùng khi cần tính năng SQL đặc thù của DB
    @Query(value = "SELECT * FROM products WHERE stock_quantity > ?1", nativeQuery = true)
    List<Product> findInStock(int minStock);

    // Paging - trả về Page<T> chứa metadata (totalPages, totalElements...)
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}

// Service sử dụng Repository
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepo;

    @Transactional(readOnly = true)  // Tối ưu: không cần dirty checking khi chỉ đọc
    public Page<Product> getActiveProducts(int page, int size) {
        return productRepo.findByStatus(ProductStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by("name")));
    }

    public Product createProduct(CreateProductRequest req) {
        Product product = new Product();
        product.setName(req.getName());
        product.setPrice(req.getPrice());
        product.setStatus(ProductStatus.ACTIVE);
        return productRepo.save(product);
    }
}
```

---

## 6. Cascade Types

### 6.1. Cascade là gì?
**Cascade** xác định các thao tác nào trên Entity cha sẽ **tự động lan truyền (cascade)** xuống các Entity con liên quan. Ví dụ: khi xóa một Order, Cascade REMOVE sẽ tự động xóa tất cả OrderItem con.

#### 1. Tại sao cần Cascade?
*   **Giảm code thủ công**: Không cần gọi `itemRepo.save(item)` riêng lẻ khi đã save Order.
*   **Tính nhất quán dữ liệu**: Đảm bảo dữ liệu con luôn đồng bộ với cha (không còn orphan records).
*   **Đơn giản hóa service layer**: Service chỉ cần thao tác trên entity cha, Hibernate lo phần còn lại.

#### 2. Phân loại Cascade Types

| Cascade Type | Khi nào kích hoạt | Khi nào dùng |
| :--- | :--- | :--- |
| `PERSIST` | Khi `save()` entity cha | Con phụ thuộc hoàn toàn vào cha |
| `MERGE` | Khi `merge()/save()` entity đã detached | Cần cập nhật cả cây object |
| `REMOVE` | Khi `delete()` entity cha | Con không tồn tại độc lập (OrderItem) |
| `REFRESH` | Khi `refresh()` entity cha | Reload dữ liệu từ DB |
| `DETACH` | Khi `detach()` entity cha | Đưa cả cây ra khỏi session |
| `ALL` | Tất cả operations trên | Con phụ thuộc hoàn toàn vào cha |

#### 3. `orphanRemoval` vs `CascadeType.REMOVE` - Sự khác biệt là gì?

| | `CascadeType.REMOVE` | `orphanRemoval = true` |
| :--- | :--- | :--- |
| Kích hoạt khi | Xóa entity cha | **Remove khỏi collection** của cha |
| Ví dụ | `orderRepo.delete(order)` | `order.getItems().remove(item)` |

> [!IMPORTANT]
> Dùng `orphanRemoval = true` khi con **không thể tồn tại độc lập** mà không có cha (ví dụ: `OrderItem` không thể tồn tại mà không có `Order`). Đây là pattern rất phổ biến với quan hệ `@OneToMany`.

### 6.2. Ví dụ cụ thể trong Spring Boot

#### Ví dụ: Cascade trong quan hệ Order - OrderItem

```java
@Entity
public class Order {

    // CascadeType.ALL: persist/merge/remove/refresh đều lan truyền xuống items
    // orphanRemoval = true: xóa item khỏi list → tự động DELETE khỏi DB
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);       // orphanRemoval tự động DELETE item này khỏi DB
        item.setOrder(null);
    }
}

@Service
@Transactional
public class OrderService {

    public Order createOrder(Long customerId, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomer(customerRepo.findById(customerId).orElseThrow());

        cartItems.forEach(cartItem -> {
            OrderItem item = new OrderItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            order.addItem(item); // Nhờ CascadeType.PERSIST, không cần gọi itemRepo.save(item)
        });

        return orderRepo.save(order); // Chỉ cần save Order, items tự được persist
    }
}
```

---

*📌 Tiếp theo: [Interview-QA.md](Interview-QA.md)*
