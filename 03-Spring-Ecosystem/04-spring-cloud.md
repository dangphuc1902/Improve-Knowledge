# ☁️ Spring Cloud — Microservices Infrastructure

> **Phase:** 2 | **Time Block:** T7 11:00-12:00  
> **Quan trọng cho:** NAB, MoMo, TymeX, VNPay

---

## 1. Spring Cloud Ecosystem Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT / MOBILE APP                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                  ┌────────▼────────┐
                  │  API Gateway    │  ← Spring Cloud Gateway
                  │  (Routing,      │  ← Rate Limiting
                  │   Auth, LB)     │  ← Circuit Breaker
                  └────────┬────────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
     ┌────────▼───┐ ┌──────▼────┐ ┌────▼───────┐
     │ User       │ │ Order     │ │ Payment    │
     │ Service    │ │ Service   │ │ Service    │
     └────────┬───┘ └──────┬────┘ └────┬───────┘
              │            │            │
              └────────────┼────────────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
     ┌────────▼───┐ ┌──────▼────┐ ┌────▼───────┐
     │ Eureka     │ │ Config    │ │ Zipkin     │
     │ (Discovery)│ │ Server    │ │ (Tracing)  │
     └────────────┘ └───────────┘ └────────────┘
```

---

## 2. Service Discovery — Eureka

### Server
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication { }
```
```yaml
# eureka-server application.yml
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Client
```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication { }
```
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
```

### Alternatives
| Discovery | Pros | Cons |
|:----------|:-----|:-----|
| Eureka | Easy Spring integration | Netflix OSS, no strong consistency |
| Consul | Multi-DC, KV store, health check | More complex setup |
| K8s DNS | Native K8s, zero config | K8s only |

---

## 3. API Gateway — Spring Cloud Gateway

```java
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Request-Source", "gateway")
                    .circuitBreaker(c -> c
                        .setName("userCB")
                        .setFallbackUri("forward:/fallback/users"))
                    .requestRateLimiter(rl -> rl
                        .setRateLimiter(redisRateLimiter())))
                .uri("lb://USER-SERVICE"))  // lb:// = load balanced via Eureka
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://ORDER-SERVICE"))
            .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 200);  // 100 req/s, burst 200
    }
}
```

### Gateway vs Load Balancer
| Feature | API Gateway | Load Balancer (Nginx) |
|:--------|:------------|:----------------------|
| Routing | Path-based, header-based | Basic URL routing |
| Auth | JWT validation, OAuth2 | Basic auth only |
| Rate limiting | Per-user/per-API | Per-IP |
| Circuit breaker | ✅ Built-in | ❌ Need external |
| Protocol transform | REST → gRPC | ❌ |
| Monitoring | Request-level metrics | Connection-level |

---

## 4. Config Server — Centralized Configuration

```yaml
# Config Server
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/company/config-repo
          default-label: main
          search-paths: '{application}'
```

```yaml
# Client (user-service bootstrap.yml)
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        max-attempts: 5
```

### Config Refresh
```java
// Dynamic config refresh without restart
@RefreshScope
@RestController
public class FeatureFlagController {
    @Value("${feature.new-ui.enabled:false}")
    private boolean newUiEnabled;
}

// Trigger refresh: POST /actuator/refresh
// Or use Spring Cloud Bus + RabbitMQ for broadcast refresh
```

---

## 5. Inter-Service Communication

### OpenFeign (Declarative REST Client)
```java
@FeignClient(name = "order-service", fallbackFactory = OrderClientFallback.class)
public interface OrderClient {
    
    @GetMapping("/orders/user/{userId}")
    List<OrderDTO> getOrdersByUser(@PathVariable Long userId);
    
    @PostMapping("/orders")
    OrderDTO createOrder(@RequestBody CreateOrderRequest request);
}

@Component
public class OrderClientFallback implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {
            @Override
            public List<OrderDTO> getOrdersByUser(Long userId) {
                return Collections.emptyList();  // Graceful degradation
            }
            // ...
        };
    }
}
```

### gRPC Inter-Service (FPM approach)
```protobuf
// wallet-service.proto
service WalletService {
    rpc GetBalance(GetBalanceRequest) returns (GetBalanceResponse);
    rpc DeductBalance(DeductRequest) returns (DeductResponse);
}
```
```java
// gRPC Client in Order Service
@GrpcClient("wallet-service")
private WalletServiceGrpc.WalletServiceBlockingStub walletStub;

public void processOrder(Order order) {
    DeductResponse response = walletStub.deductBalance(
        DeductRequest.newBuilder()
            .setUserId(order.getUserId())
            .setAmount(order.getTotal())
            .build());
}
```

> **Relate FPM Project:** 6 Protobuf contracts, Anti-Corruption Layer pattern, sub-50ms internal RPC latency via HTTP/2 + binary Protobuf.

---

## 6. Distributed Tracing — Micrometer + Zipkin

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% sampling (dev), 0.1 for prod
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### Trace propagation
```
User Request → Gateway (traceId: abc123)
    → User Service (traceId: abc123, spanId: span1)
        → Order Service (traceId: abc123, spanId: span2)
            → Payment Service (traceId: abc123, spanId: span3)
```

---

## Câu Hỏi Phỏng Vấn

### Q1: Service Discovery hoạt động thế nào?
**A:** Mỗi service khi start sẽ register với Eureka Server (heartbeat mỗi 30s). Khi Service A cần gọi Service B, nó hỏi Eureka lấy danh sách instances của B, rồi client-side load balance (Round Robin) chọn 1 instance.

### Q2: API Gateway vs Reverse Proxy (Nginx)?
**A:** Gateway làm nhiều hơn: intelligent routing, JWT validation, rate limiting per-user, circuit breaker, protocol transformation. Nginx chủ yếu load balancing và static content. Trong microservices, thường dùng cả 2: Nginx phía trước (TLS termination, DDoS) → Gateway phía sau (business logic routing).

### Q3: Khi Config Server chết thì service có chạy được không?
**A:** Có, nếu service đã start và cache config. Nhưng service mới start sẽ fail. Solutions: `fail-fast: false` + default values, hoặc dùng config sidecar pattern, hoặc embed config backup.

### Q4: gRPC vs REST cho inter-service — khi nào dùng cái nào?
**A:** gRPC: latency-critical (binary Protobuf, HTTP/2, multiplexing), strong contract (schema evolution), streaming support. REST: external APIs, browser-compatible, simpler debugging. Thường dùng gRPC nội bộ + REST cho external.
