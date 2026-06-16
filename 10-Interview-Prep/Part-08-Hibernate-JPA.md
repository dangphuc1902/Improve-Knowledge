# PART 8 - HIBERNATE & JPA

> **Topics**: Entity Lifecycle · Persistence Context · Lazy/Eager Loading · N+1 Problem · JPQL

---

## Entity Lifecycle

### Q1. Explain JPA Entity Lifecycle States

```
New/Transient → [persist()] → Managed → [flush/commit] → DB
                              Managed → [detach()/close()] → Detached
                              Managed → [remove()] → Removed → [commit] → Deleted from DB
                              Detached → [merge()] → Managed
```

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "ORDER_SEQ", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)  // Always lazy for @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version  // Optimistic locking
    private Long version;
}

// Usage
@Transactional
public void demonstrateLifecycle() {
    // 1. TRANSIENT - not associated with any persistence context
    Order order = new Order();
    order.setStatus("NEW");

    // 2. MANAGED - associated with persistence context
    entityManager.persist(order);
    // Now order.getId() is set, tracked for dirty checking

    // 3. Changes to managed entities are automatically synced (dirty checking)
    order.setStatus("CONFIRMED"); // No explicit save needed!

    // 4. DETACHED - disconnected from persistence context
    entityManager.detach(order);
    order.setStatus("SHIPPED"); // This change will NOT be persisted!

    // 5. Merge detached entity back
    Order managed = entityManager.merge(order); // Returns new managed instance

    // 6. REMOVED - scheduled for deletion
    entityManager.remove(managed);
    // Deleted from DB on flush/commit
}
```

---

## Persistence Context & Dirty Checking

### Q2. What is the Persistence Context?

**Answer:**
The Persistence Context is a **first-level cache** and **change tracker** that lives for the duration of a transaction (with `TRANSACTION` scope) or EntityManager (with `EXTENDED` scope).

```java
@Service
@Transactional
public class OrderService {

    @PersistenceContext
    private EntityManager em;

    public void updateOrder(Long id) {
        // Fetch creates a managed entity in persistence context
        Order order = em.find(Order.class, id);
        
        // Modify managed entity
        order.setStatus("PROCESSING");
        
        // NO explicit save needed!
        // At transaction commit, Hibernate compares current state
        // to snapshot taken at load time → detects change → issues UPDATE
        
    } // Transaction commits → dirty check → UPDATE SQL executed

    public void demonstrateFirstLevelCache() {
        // Both calls return the SAME Java object (from cache)
        Order o1 = em.find(Order.class, 1L); // DB hit
        Order o2 = em.find(Order.class, 1L); // Cache hit — no DB query!
        
        System.out.println(o1 == o2); // true
    }
}
```

**Key Points:**
- First-level cache = Within same transaction, same entity is only loaded once
- Dirty checking = Hibernate automatically detects and persists changes to managed entities
- Flushing = Syncing persistence context to DB (before query, at commit, or manually)

---

## Lazy vs Eager Loading

### Q3. Lazy Loading vs Eager Loading

| | Lazy | Eager |
|---|---|---|
| When loaded | On first access | Immediately with parent |
| Default for @OneToMany | LAZY | — |
| Default for @ManyToOne | EAGER | — |
| Best practice | Prefer LAZY | Use only when always needed |
| Risk | LazyInitializationException | Cartesian product / memory |

```java
@Entity
public class Customer {
    @Id private Long id;

    // LAZY - List of orders loaded only when accessed
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders;

    // EAGER - address always loaded with customer
    @ManyToOne(fetch = FetchType.EAGER)  // Don't do this in general
    private Address address;
}

// LazyInitializationException - classic mistake
@Service
public class CustomerService {

    public Customer getCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        return customer;
        // ↑ Transaction ends here, Session closes
    }
}

@RestController
public class CustomerController {
    public ResponseEntity<?> getCustomer(Long id) {
        Customer customer = customerService.getCustomer(id);
        // BOOM! LazyInitializationException - accessing orders outside transaction
        return ResponseEntity.ok(customer.getOrders());
    }
}

// Solutions:
// 1. Open Session in View (ANTI-PATTERN - don't use)
// 2. Fetch join in repository
// 3. DTO with explicit projection
// 4. @EntityGraph
```

---

## N+1 Problem

### Q4. What is the N+1 Problem and How to Fix It?

**The Problem:**
```java
// Query 1: Get all customers (1 query)
List<Customer> customers = customerRepository.findAll();

// Query N: For each customer, Hibernate loads orders separately
for (Customer customer : customers) {
    System.out.println(customer.getOrders().size()); // N queries!
}
// Total: 1 + N queries (1 + 100 for 100 customers = 101 queries)
```

**Solution 1: JOIN FETCH in JPQL**
```java
@Query("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.orders WHERE c.active = true")
List<Customer> findAllWithOrders();
// Single query: SELECT c.*, o.* FROM customer c LEFT JOIN orders o ON o.customer_id = c.id
```

**Solution 2: @EntityGraph**
```java
@EntityGraph(attributePaths = {"orders", "orders.items"})
@Query("SELECT c FROM Customer c WHERE c.active = true")
List<Customer> findAllWithOrdersAndItems();
```

**Solution 3: DTO Projection with JOIN**
```java
@Query("""
    SELECT new com.example.dto.CustomerOrderSummary(
        c.id, c.name, COUNT(o))
    FROM Customer c 
    LEFT JOIN c.orders o 
    WHERE c.active = true
    GROUP BY c.id, c.name
    """)
List<CustomerOrderSummary> findCustomerSummaries();
```

**Solution 4: Batch Fetching (Hibernate)**
```java
@OneToMany(mappedBy = "customer")
@BatchSize(size = 30)  // Instead of N queries, does CEIL(N/30) queries
private List<Order> orders;
```

**Detection: Enable SQL Logging + p6spy**
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE  # Shows parameter values
```

---

## Fetch Join & EntityGraph

### Q5. Fetch Join vs EntityGraph

```java
// Fetch Join - explicit in JPQL
@Query("""
    SELECT o FROM Order o 
    JOIN FETCH o.customer c 
    JOIN FETCH o.items i
    JOIN FETCH i.product p
    WHERE o.status = :status
    """)
List<Order> findOrdersWithDetails(@Param("status") String status);

// EntityGraph - declarative (can be reused across methods)
@Entity
@NamedEntityGraph(
    name = "Order.withDetails",
    attributeNodes = {
        @NamedAttributeNode("customer"),
        @NamedAttributeNode(value = "items", subgraph = "items.product")
    },
    subgraphs = @NamedSubgraph(name = "items.product",
        attributeNodes = @NamedAttributeNode("product"))
)
public class Order { ... }

// Repository
@EntityGraph("Order.withDetails")
List<Order> findByStatus(String status);

// Or ad-hoc EntityGraph
@EntityGraph(attributePaths = {"customer", "items.product"})
Optional<Order> findById(Long id);
```

**Fetch Join Caveat — Pagination + Join Fetch:**
```java
// DANGEROUS: Hibernate fetches ALL records to memory, then paginates in Java
@Query("SELECT c FROM Customer c JOIN FETCH c.orders")
Page<Customer> findAll(Pageable pageable); // HibernateJpaDialect warning!

// FIX: Separate count query + @EntityGraph or two-query approach
@Query(value = "SELECT c FROM Customer c",
       countQuery = "SELECT COUNT(c) FROM Customer c")
Page<Customer> findAllCustomers(Pageable pageable);

// Then apply EntityGraph or fetch orders separately for result page
```

---

## JPQL & Native Queries

### Q6. JPQL vs Native Query vs Criteria API

```java
// JPQL - Entity-based, portable
@Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
Optional<User> findActiveByEmail(@Param("email") String email);

// JPQL with constructor expression (DTO)
@Query("""
    SELECT new com.example.dto.OrderDTO(
        o.id, o.status, o.totalAmount, c.name
    )
    FROM Order o JOIN o.customer c
    WHERE o.createdAt >= :fromDate
    """)
List<OrderDTO> findOrderDTOs(@Param("fromDate") LocalDateTime fromDate);

// Native SQL - for DB-specific features (Oracle hints, window functions)
@Query(
    value = """
        SELECT o.*, c.name as customer_name,
               ROW_NUMBER() OVER (PARTITION BY c.id ORDER BY o.created_at DESC) as rn
        FROM orders o
        JOIN customers c ON c.id = o.customer_id
        WHERE o.status = :status
        """,
    nativeQuery = true
)
List<Object[]> findOrdersWithRowNumber(@Param("status") String status);

// Criteria API - dynamic queries (avoid SQL injection, type-safe)
public List<User> searchUsers(UserSearchCriteria criteria) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> cq = cb.createQuery(User.class);
    Root<User> root = cq.from(User.class);
    
    List<Predicate> predicates = new ArrayList<>();
    
    if (StringUtils.hasText(criteria.getName())) {
        predicates.add(cb.like(cb.lower(root.get("name")),
            "%" + criteria.getName().toLowerCase() + "%"));
    }
    if (criteria.getMinAge() != null) {
        predicates.add(cb.ge(root.get("age"), criteria.getMinAge()));
    }
    if (criteria.getDepartment() != null) {
        predicates.add(cb.equal(root.get("department"), criteria.getDepartment()));
    }
    
    cq.where(cb.and(predicates.toArray(new Predicate[0])));
    cq.orderBy(cb.desc(root.get("createdAt")));
    
    return em.createQuery(cq)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
}
```

---

## Transactions in Hibernate

### Q7. Optimistic vs Pessimistic Locking

```java
// Optimistic Locking - for low-contention scenarios
@Entity
public class Account {
    @Id private Long id;
    private BigDecimal balance;
    
    @Version  // Hibernate adds version column
    private Long version;
    // On update: WHERE id = ? AND version = ?
    // If version changed → OptimisticLockException
}

// Pessimistic Locking - for high-contention scenarios
@Query("SELECT a FROM Account a WHERE a.id = :id")
@Lock(LockModeType.PESSIMISTIC_WRITE)  // SELECT FOR UPDATE
Optional<Account> findByIdWithLock(@Param("id") Long id);

@Transactional
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    // Lock in consistent order to prevent deadlock
    Long firstId = Math.min(fromId, toId);
    Long secondId = Math.max(fromId, toId);
    
    Account first = accountRepo.findByIdWithLock(firstId).orElseThrow();
    Account second = accountRepo.findByIdWithLock(secondId).orElseThrow();
    
    Account from = firstId.equals(fromId) ? first : second;
    Account to = firstId.equals(toId) ? first : second;
    
    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));
    // No explicit save - dirty checking handles it
}
```

---

### Q8. Common Hibernate Performance Tips

```java
// 1. Disable OSIV (Open Session In View)
spring:
  jpa:
    open-in-view: false  # Add to application.yml!

// 2. Use Projections instead of full entities when you don't need all fields
public interface UserSummary {
    Long getId();
    String getUsername();
    String getEmail();
}
List<UserSummary> findByActiveTrue(); // SELECT id, username, email FROM users

// 3. Use pagination - never load all records
Page<User> users = userRepository.findAll(PageRequest.of(0, 20, Sort.by("createdAt")));

// 4. Second-level cache for frequently-read, rarely-changed data
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Country { ... }

// 5. Batch inserts
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true

// Then:
@Transactional
public void bulkInsert(List<Order> orders) {
    for (int i = 0; i < orders.size(); i++) {
        entityManager.persist(orders.get(i));
        if (i % 50 == 0) {
            entityManager.flush();   // Execute batch
            entityManager.clear();   // Clear persistence context (avoid OOM)
        }
    }
}
```

---

*Next: [Part 9 - Database](./Part-09-Database.md)*
