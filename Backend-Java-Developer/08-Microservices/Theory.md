# 📖 Microservices - Lý Thuyết, Interview & Bài Tập

> **Tuần 6 | Kiến trúc Microservices**

---

## LÝ THUYẾT

### 1. Monolith vs Microservices

```
Monolith:
┌─────────────────────────────────────┐
│           Single Application        │
│  UI + Business Logic + Data Layer   │
│          Deployed as 1 unit         │
└─────────────────────────────────────┘

Microservices:
┌──────────┐  ┌──────────┐  ┌──────────┐
│  User    │  │  Order   │  │ Payment  │
│ Service  │  │ Service  │  │ Service  │
│  DB own  │  │  DB own  │  │  DB own  │
└──────────┘  └──────────┘  └──────────┘
    ↑               ↑              ↑
         API Gateway / Load Balancer
```

---

### 2. Microservices Principles (Martin Fowler)

1. **Single Responsibility**: Mỗi service làm 1 việc
2. **Decentralized**: Mỗi service có DB riêng
3. **Failure Isolation**: Service A lỗi không làm sập Service B
4. **Independent Deployment**: Deploy từng service riêng lẻ
5. **Designed for Failure**: Circuit Breaker, Retry
6. **Organized around Business Capabilities**

---

### 3. Microservices Architecture Components

```
Client (Browser/Mobile)
    ↓
API Gateway (Spring Cloud Gateway / Kong)
  - Routing
  - Authentication
  - Rate Limiting
  - Load Balancing
    ↓
Service Discovery (Eureka / Consul)
    ↓
Individual Services
  - User Service
  - Product Service  
  - Order Service
  - Payment Service
    ↓ (async)
Message Broker (Kafka / RabbitMQ)
    ↓
Distributed Tracing (Zipkin / Jaeger)
Centralized Logging (ELK Stack)
Centralized Config (Spring Cloud Config)
```

---

### 4. Service Communication

#### Synchronous (REST/gRPC):
```java
// RestTemplate (legacy)
@Service
public class OrderService {
    private final RestTemplate restTemplate;

    public ProductDto getProduct(Long productId) {
        return restTemplate.getForObject(
            "http://product-service/api/products/{id}",
            ProductDto.class, productId);
    }
}

// WebClient (reactive, preferred in Spring 5+)
@Service
public class OrderService {
    private final WebClient webClient;

    public Mono<ProductDto> getProduct(Long productId) {
        return webClient
            .get()
            .uri("/api/products/{id}", productId)
            .retrieve()
            .bodyToMono(ProductDto.class);
    }
}

// OpenFeign (declarative REST client)
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductDto getProduct(@PathVariable Long id);
}
```

#### Asynchronous (Message Broker):
```java
// Kafka Producer
@Service
public class OrderService {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void placeOrder(Order order) {
        orderRepository.save(order);
        // Async event - không cần chờ payment service
        kafkaTemplate.send("order-placed", new OrderPlacedEvent(order.getId()));
    }
}

// Kafka Consumer (Payment Service)
@Component
public class OrderEventConsumer {
    @KafkaListener(topics = "order-placed", groupId = "payment-group")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        paymentService.processPayment(event.getOrderId());
    }
}
```

---

### 5. Spring Cloud (Microservices với Spring)

```xml
<!-- Eureka Server (Service Registry) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<!-- Eureka Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- API Gateway -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<!-- Config Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApp { }

// Service Client
@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApp { }

// application.yml (product-service)
spring:
  application:
    name: product-service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

### 6. Circuit Breaker Pattern (Resilience4j)

```java
// Ngăn cascade failures
@Service
public class OrderService {

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    @TimeLimiter(name = "productService")
    public ProductDto getProduct(Long id) {
        return productClient.getProduct(id);
    }

    // Fallback khi circuit open
    public ProductDto getProductFallback(Long id, Throwable ex) {
        return ProductDto.builder()
            .id(id)
            .name("Product Unavailable")
            .price(BigDecimal.ZERO)
            .build();
    }
}

# application.yml - Circuit Breaker config
resilience4j:
  circuitbreaker:
    instances:
      productService:
        failure-rate-threshold: 50        # Open sau 50% failures
        wait-duration-in-open-state: 30s  # Wait 30s trước khi half-open
        sliding-window-size: 10           # Track 10 recent calls
```

---

### 7. Saga Pattern - Distributed Transactions

```
Vấn đề: Làm sao đảm bảo consistency khi transaction span nhiều services?

Choreography Saga:
  Order Service → publish OrderCreated
  Payment Service → listen, process payment → publish PaymentCompleted
  Inventory Service → listen, reserve items → publish ItemsReserved
  Order Service → listen, confirm order

Orchestration Saga:
  Order Orchestrator → Step 1: Payment Service
                     → Step 2: Inventory Service  
                     → Step 3: Shipping Service
  Nếu Step 2 fail → Compensate Step 1 (refund payment)
```

---

## INTERVIEW Q&A

### Q1: Microservices vs Monolith - khi nào nên dùng?

**Monolith (bắt đầu với):**
- Startup/MVP - team nhỏ
- Domain chưa rõ ràng
- Đơn giản hơn để develop và debug

**Microservices (khi cần):**
- Scale independent services
- Team lớn (Conway's Law)
- Different tech stacks cho services
- High availability requirements
- Clear domain boundaries

---

### Q2: Challenges của Microservices?

1. **Distributed system complexity** - network latency, partial failures
2. **Data consistency** - không có ACID transactions cross-service
3. **Service discovery** - tìm endpoints
4. **Distributed tracing** - debug cross-service issues
5. **Operational overhead** - nhiều services cần deploy, monitor
6. **Testing complexity** - integration tests khó hơn

---

### Q3: API Gateway là gì? Lợi ích?

**API Gateway** = Single entry point cho tất cả clients.

**Lợi ích:**
- Cross-cutting concerns: Authentication, rate limiting, logging
- Request routing
- Load balancing
- Protocol translation
- Response aggregation
- SSL termination

---

### Q4: Service Discovery hoạt động thế nào?

1. Service start → đăng ký với Eureka (IP, port, health endpoint)
2. Client muốn gọi service → hỏi Eureka: "Đâu là product-service?"
3. Eureka trả về list instances
4. Client chọn 1 instance (load balancing)
5. Gọi trực tiếp

---

### Q5: Circuit Breaker là gì? Tại sao cần?

**Circuit Breaker** ngăn cascade failures trong distributed system.

**3 states:**
- **Closed**: Normal operation, requests pass through
- **Open**: Service failing, requests fail-fast (no actual call)
- **Half-Open**: Test if service recovered (allow few requests)

**Tại sao cần:** Khi service B chậm, service A không nên chờ mãi → thread exhaustion → A cũng sập.

---

## BÀI TẬP

### Thiết Kế Hệ Thống E-commerce Microservices

```
Vẽ diagram kiến trúc:

Services:
- User Service       (port 8081) - Quản lý users
- Product Service    (port 8082) - Quản lý sản phẩm  
- Order Service      (port 8083) - Quản lý đơn hàng
- Payment Service    (port 8084) - Xử lý thanh toán
- Notification Svc   (port 8085) - Email/SMS

Infrastructure:
- Eureka Server      (port 8761) - Service registry
- API Gateway        (port 8080) - Entry point
- Config Server      (port 8888) - Centralized config

Communication:
- REST: User/Product → Order Service
- Kafka: Order placed → Payment, Notification
```

**Câu hỏi thiết kế:**
1. Order service cần lấy product info - nên dùng REST hay Kafka?
2. Khi order failed, làm sao rollback payment?
3. Làm sao handle Product Service down khi Order Service đang tạo order?

---

*📌 Tiếp theo: [09-Database-Oracle-MSSQL](../09-Database-Oracle-MSSQL/Theory.md)*
