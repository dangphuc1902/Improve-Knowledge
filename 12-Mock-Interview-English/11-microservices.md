# PART 11 — Microservices
> Heavily tailored to your FPM project (10 services, Spring Cloud, Kafka, RabbitMQ)

---

## 🔷 Microservices Architecture

### Q1: What are Microservices and why use them?

**Ideal Answer:**
> Microservices is an architectural approach where an application is decomposed into small, independently deployable services, each owning its own data and running in its own process, communicating via APIs or messaging.

**Benefits:**
- Independent deployment and scaling
- Technology diversity (use best tool per service)
- Fault isolation (one service fails, others continue)
- Small, focused teams (Conway's Law)

**Drawbacks:**
- Distributed system complexity
- Network latency between services
- Data consistency challenges
- Operational overhead (monitoring, tracing, deployment)

**Real-world from your FPM project:**
> "In my FPM project I decomposed a financial management system into 10 services: user-auth, wallet, transaction, reporting, notification, OCR, AI-categorization, API Gateway, Eureka discovery, and Config Server. Each service owns its own MySQL schema. They communicate via gRPC (6 Protobuf contracts) and dual-broker async messaging."

---

## 🔷 Service Discovery — Eureka

### Q2: What is Service Discovery and how does Eureka work?

**Problem:** In microservices, service instances have dynamic IPs (containers, auto-scaling). How does Service A know where Service B is?

**Solution: Service Registry (Eureka)**

```
Services register themselves → Eureka Server
                                      ↓
Clients query → Eureka Server returns available instances
                                      ↓
Client-side load balancing (Ribbon/Spring Cloud LoadBalancer)
```

```yaml
# Eureka Server
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false

# Any microservice (Eureka Client)
spring:
  application:
    name: wallet-service

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
```

```java
// Client: call another service by name (not IP)
@FeignClient(name = "wallet-service")
interface WalletServiceClient {
    @GetMapping("/api/wallets/{userId}")
    WalletResponse getWallet(@PathVariable Long userId);
}
```

---

## 🔷 API Gateway — Spring Cloud Gateway

### Q3: What is an API Gateway and what does it do?

**Functions:**
- Single entry point for all clients
- Routing (route to correct microservice)
- Authentication (validate JWT once, not per service)
- Rate limiting
- Load balancing
- SSL termination
- Request/Response transformation
- Circuit breaking

```yaml
# Spring Cloud Gateway routes
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service          # lb = load balanced via Eureka
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20

        - id: wallet-service
          uri: lb://wallet-service
          predicates:
            - Path=/api/wallets/**
```

---

## 🔷 Feign Client

### Q4: What is OpenFeign and how does it work?

```java
// Declarative HTTP client — no boilerplate RestTemplate code
@FeignClient(
    name = "transaction-service",
    fallback = TransactionServiceFallback.class
)
public interface TransactionServiceClient {

    @GetMapping("/api/transactions/user/{userId}")
    List<TransactionDto> getUserTransactions(@PathVariable Long userId);

    @PostMapping("/api/transactions")
    TransactionDto createTransaction(@RequestBody CreateTransactionRequest req);
}

// Fallback (Circuit Breaker)
@Component
public class TransactionServiceFallback implements TransactionServiceClient {
    @Override
    public List<TransactionDto> getUserTransactions(Long userId) {
        return Collections.emptyList(); // graceful degradation
    }
}
```

---

## 🔷 Circuit Breaker — Resilience4j

### Q5: What is Circuit Breaker and how does Resilience4j work?

**Problem:** If Service A calls Service B and B is slow/down, A's threads pile up → cascade failure.

**Circuit Breaker States:**
```
CLOSED → failures below threshold → normal operation
    ↓ failure rate > threshold
OPEN → all calls fail fast (no network call)
    ↓ after wait duration
HALF_OPEN → allow test calls
    ↓ if successful
CLOSED
    ↓ if failed
OPEN again
```

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    instances:
      wallet-service:
        failure-rate-threshold: 50          # open if 50% calls fail
        wait-duration-in-open-state: 30s    # stay open for 30s
        sliding-window-size: 10             # based on last 10 calls
        permitted-number-of-calls-in-half-open-state: 3
```

```java
@CircuitBreaker(name = "wallet-service", fallbackMethod = "getWalletFallback")
@Retry(name = "wallet-service")
@TimeLimiter(name = "wallet-service")
public WalletDto getWallet(Long userId) {
    return walletClient.getWallet(userId);
}

public WalletDto getWalletFallback(Long userId, Exception ex) {
    log.warn("Circuit breaker triggered for wallet-service: {}", ex.getMessage());
    return new WalletDto(userId, BigDecimal.ZERO); // cached/default value
}
```

**Your FPM answer:**
> "In FPM I configured Resilience4j Circuit Breaker with 50% failure threshold and 30-second open window. Combined with Token Bucket rate limiting and RabbitMQ retry with exponential backoff for resilient async pipelines."

---

## 🔷 Config Server

### Q6: What is Spring Cloud Config Server?

```
Config Server reads from Git repo (or file system)
↓
All services fetch config on startup and refresh
↓
Change config in Git → refresh endpoints → no restart needed
```

```yaml
# Config Server
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          default-label: main
          search-paths: '{application}'

# Each service in bootstrap.yml
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: wallet-service
      profile: prod
```

---

## 🔷 Distributed Tracing — Zipkin / Jaeger

### Q7: What is Distributed Tracing?

**Problem:** Request spans multiple services. How do you trace the full journey?

```
Request → API Gateway → User Service → Wallet Service → DB
                                    ↘ Notification Service

Without tracing: which service caused the 3s latency?
```

**Solution:** Trace ID + Span ID

```
Trace ID: abc-123 (same across all services for one request)
  └── Span: API Gateway (50ms)
       └── Span: User Service (200ms)
            └── Span: Wallet Service (2800ms)  ← bottleneck!
```

```yaml
# Spring Boot 3 (Micrometer Tracing + Zipkin)
management:
  tracing:
    sampling:
      probability: 1.0   # trace 100% of requests (use 0.1 in production)

spring:
  zipkin:
    base-url: http://zipkin:9411
```

---

## 🔷 Message Brokers — Kafka & RabbitMQ

### Q8: When to use Kafka vs RabbitMQ?

| | Kafka | RabbitMQ |
|---|-------|----------|
| Model | Pub/Sub log-based | Queue + Routing |
| Message retention | Long-term (days/weeks) | Until consumed |
| Throughput | Millions/sec | Thousands/sec |
| Consumer groups | Multiple consumers, independent offsets | One queue, one consumer group |
| Ordering | Per-partition ordering | Per-queue ordering |
| Use case | Event streaming, audit log, analytics | Task queue, workflow, RPC |

**Your FPM answer:**
> "In FPM, I made a deliberate architectural choice: Kafka for high-throughput transaction streaming (8 topics) where durability and replay are critical, and RabbitMQ for domain event routing (wallet.created, balance.changed, budget.alerts) where routing flexibility and dead-letter handling matter."

---

### Q9: Kafka Concepts

```java
// Producer
@Service
public class TransactionEventProducer {
    @Autowired KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void publish(TransactionEvent event) {
        kafkaTemplate.send("transaction.created", event.getUserId().toString(), event);
    }
}

// Consumer
@KafkaListener(
    topics = "transaction.created",
    groupId = "reporting-service",
    containerFactory = "kafkaListenerContainerFactory"
)
public void handleTransaction(TransactionEvent event, Acknowledgment ack) {
    try {
        reportingService.process(event);
        ack.acknowledge();  // manual commit
    } catch (Exception e) {
        // handle retry / DLQ
    }
}
```

**Key concepts:**
| Term | Meaning |
|------|---------|
| Topic | Named stream of records |
| Partition | Sub-division of topic (enables parallelism) |
| Offset | Position of message in partition |
| Consumer Group | Group of consumers sharing a topic |
| Replication | Copy partitions across brokers for HA |

---

### Q10: Common Microservices Interview Scenarios

**"How do you handle distributed transactions?"**
> "In FPM I avoid distributed transactions. Instead, I use the Saga pattern with choreography via Kafka/RabbitMQ events. Each service listens for domain events and updates its own data, publishing compensating events if it needs to rollback. For example, when a transaction is created, wallet service listens, deducts balance, and publishes wallet.balance.changed. If validation fails, it publishes a compensating transaction.failed event."

**"How do you handle service-to-service authentication?"**
> "In FPM, the API Gateway validates the JWT from the client. It then forwards a verified internal token (or propagates claims via headers) to downstream services. Each downstream service uses a shared fpm-security library to re-validate — defense-in-depth approach."

**"What happens when Config Server is down at startup?"**
> "Spring Cloud Config Server can be configured with a fallback to local properties. I also run Config Server with at least 2 replicas behind a load balancer. For critical services, I use `spring.cloud.config.fail-fast=true` so they fail loudly rather than start with stale config."
