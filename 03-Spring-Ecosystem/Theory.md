# 📖 Hibernate & JPA - Lý Thuyết, Interview & Bài Tập

> **Tuần 5 | ORM - Object Relational Mapping**

---

## LÝ THUYẾT

### 1. Hibernate Architecture

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

---

### 2. Entity Mapping

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

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Lob
    private String description;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Standard constructors, getters, setters...
}

public enum ProductStatus {
    ACTIVE, INACTIVE, DISCONTINUED
}
```

---

### 3. Relationships (Quan hệ)

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

    // Helper methods để maintain bidirectional
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

// @ManyToMany (Student và Course)
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

### 4. FetchType: LAZY vs EAGER

```java
// LAZY (mặc định với @OneToMany, @ManyToMany) - load khi access
@OneToMany(fetch = FetchType.LAZY)
private List<OrderItem> items;  // SELECT khi gọi getItems()

// EAGER (mặc định với @ManyToOne, @OneToOne) - load ngay
@ManyToOne(fetch = FetchType.EAGER)
private Customer customer;  // JOIN khi load Order

// N+1 Problem với LAZY:
List<Order> orders = orderRepo.findAll();  // 1 query
for (Order o : orders) {
    o.getItems().size();  // N queries! (1 per order)
}

// Fix: JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Optional<Order> findByIdWithItems(@Param("id") Long id);

// Hoặc EntityGraph
@EntityGraph(attributePaths = {"items", "customer"})
List<Order> findAllWithDetails();
```

---

### 5. Spring Data JPA - Repository

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

    // Native SQL
    @Query(value = "SELECT * FROM products WHERE stock_quantity > ?1", nativeQuery = true)
    List<Product> findInStock(int minStock);

    // Paging
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}

// Service
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepo;

    @Transactional(readOnly = true)
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

### 6. Cascade Types

| Cascade | Mô tả |
|---------|-------|
| `ALL` | Tất cả operations |
| `PERSIST` | Save parent → save children |
| `MERGE` | Update parent → update children |
| `REMOVE` | Delete parent → delete children |
| `REFRESH` | Refresh parent → refresh children |
| `DETACH` | Detach parent → detach children |

**`orphanRemoval = true`**: Khi remove item khỏi collection → tự động delete khỏi DB.

---

## INTERVIEW Q&A

### Q1: JPA vs Hibernate khác nhau thế nào?

**JPA (Jakarta Persistence API)**: Specification (interface/annotation) - không có implementation.  
**Hibernate**: JPA provider phổ biến nhất - implementation đầy đủ của JPA.  
Có thể swap Hibernate sang EclipseLink, OpenJPA nếu dùng JPA API thuần.

---

### Q2: N+1 Problem là gì? Cách fix?

**N+1 Problem**: Load N entities, mỗi entity trigger thêm 1 query để load association → tổng N+1 queries.

```java
// N+1 Problem
List<Author> authors = authorRepo.findAll();  // 1 query
for (Author a : authors) {
    System.out.println(a.getBooks().size());   // N queries
}

// Fix 1: JOIN FETCH in JPQL
@Query("SELECT a FROM Author a JOIN FETCH a.books")
List<Author> findAllWithBooks();

// Fix 2: @EntityGraph
@EntityGraph(attributePaths = "books")
List<Author> findAll();

// Fix 3: Batch fetching
@BatchSize(size = 20)
@OneToMany private List<Book> books;
```

---

### Q3: Hibernate Entity có các state nào?

```
Transient  → new object, không có ID, không được Hibernate quản lý
Persistent → được Hibernate quản lý (trong session/EntityManager), changes auto-detected
Detached   → session đã đóng, object có ID nhưng không được track
Removed    → đánh dấu xóa, sẽ delete khi commit
```

---

### Q4: @GeneratedValue strategies?

| Strategy | Mô tả |
|---------|-------|
| `IDENTITY` | DB auto-increment (MySQL) |
| `SEQUENCE` | DB sequence (Oracle, PostgreSQL) |
| `TABLE` | Bảng riêng để generate ID |
| `AUTO` | Hibernate tự chọn phù hợp nhất |
| `UUID` | UUID (Java 17+ / custom) |

---

### Q5: Ưu điểm Hibernate so với JDBC thuần?

| Hibernate | JDBC |
|-----------|------|
| Tự viết SQL | Phải viết SQL thủ công |
| Object-oriented | Row-based |
| Cache support | Không có |
| Lazy loading | Manual |
| Relationship management | Manual JOINs |
| HQL độc lập DB | SQL phụ thuộc DB |

---

## BÀI TẬP

### Bài 1: E-commerce Domain

Tạo entities cho hệ thống bán hàng:

```
Customer (1) ←→ (N) Order
Order (1) ←→ (N) OrderItem
OrderItem (N) ←→ (1) Product
Product (N) ←→ (N) Category
```

Implement repositories và service methods:
- `placeOrder(customerId, List<CartItem>)` với transaction
- `getOrderHistory(customerId, pageable)` 
- `getProductsByCategory(categoryId)` - tránh N+1
- `updateInventory(productId, quantity)`

### Bài 2: Hibernate Criteria API

Viết dynamic search query:
```java
public Page<Product> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
    // criteria: name (optional), minPrice, maxPrice, status, categoryId
    // Dùng JPA Criteria API hoặc Querydsl
}
```

---

*📌 Tiếp theo: [07-WebServices-REST-SOAP](../07-WebServices-REST-SOAP/Theory.md)*
