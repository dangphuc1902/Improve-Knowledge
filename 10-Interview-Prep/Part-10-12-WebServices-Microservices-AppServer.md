# PART 10-12 - WEB SERVICES · MICROSERVICES · APPLICATION SERVERS

> **Topics**: REST · SOAP · Microservices · Spring Cloud · Kafka · Tomcat · JBoss · WebSphere · WebLogic

---

## PART 10 — WEB SERVICES

### Q1. REST Principles (Richardson Maturity Model)

**6 Constraints of REST:**
1. **Client-Server** — Separation of concerns
2. **Stateless** — No session on server; each request is self-contained
3. **Cacheable** — Responses can be cached
4. **Uniform Interface** — Standard URLs, HTTP methods, status codes
5. **Layered System** — Client doesn't know if it's talking to server or proxy
6. **Code on Demand** (optional) — Server can send executable code

**HTTP Methods (Idempotency matters!):**
| Method | Operation | Idempotent | Safe |
|---|---|---|---|
| GET | Read | ✅ | ✅ |
| POST | Create | ❌ | ❌ |
| PUT | Replace | ✅ | ❌ |
| PATCH | Partial update | ❌ | ❌ |
| DELETE | Delete | ✅ | ❌ |

**HTTP Status Codes:**
```
2xx - Success
  200 OK            - Request succeeded
  201 Created       - Resource created (include Location header)
  204 No Content    - Success, no body (used for DELETE)

3xx - Redirection
  301 Moved Permanently
  304 Not Modified  - Cached response still valid

4xx - Client Error
  400 Bad Request   - Invalid request body/params
  401 Unauthorized  - Not authenticated
  403 Forbidden     - Authenticated but not authorized
  404 Not Found     - Resource doesn't exist
  409 Conflict      - Resource state conflict (duplicate)
  422 Unprocessable - Validation errors
  429 Too Many Requests - Rate limit exceeded

5xx - Server Error
  500 Internal Server Error - Unexpected server error
  502 Bad Gateway           - Upstream service error
  503 Service Unavailable   - Server overloaded/down
  504 Gateway Timeout       - Upstream timeout
```

---

### Q2. REST Authentication & Authorization

```java
// JWT Flow:
// 1. Client POST /auth/login with credentials
// 2. Server validates → issues JWT (header.payload.signature)
// 3. Client includes token: Authorization: Bearer <token>
// 4. Server validates token on each request (stateless!)

// JWT Structure:
// Header: {"alg": "HS256", "typ": "JWT"}
// Payload: {"sub": "userId", "roles": ["USER"], "exp": 1234567890}
// Signature: HMACSHA256(base64Header + "." + base64Payload, secret)

@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;
    
    @Value("${app.jwt.expiration-ms:86400000}") // 24 hours
    private long expirationMs;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}

// OAuth2 + JWT with Spring Security
// 1. Client → Auth Server (Google/Keycloak) → Access Token
// 2. Client → Your API with Access Token
// 3. Your API validates token with Auth Server or locally via JWK
```

---

### Q3. SOAP Web Services — WSDL and Message Structure

**SOAP Message Structure:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header>
        <!-- Optional: Authentication, WS-Security, Routing -->
        <wsse:Security>
            <wsse:UsernameToken>
                <wsse:Username>user</wsse:Username>
                <wsse:Password>pass</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soap:Header>
    <soap:Body>
        <!-- Actual message payload -->
        <tns:GetOrderRequest xmlns:tns="http://example.com/orders">
            <tns:OrderId>12345</tns:OrderId>
        </tns:GetOrderRequest>
    </soap:Body>
    <!-- Optional Fault for errors:
    <soap:Fault>
        <faultcode>soap:Server</faultcode>
        <faultstring>Internal Error</faultstring>
    </soap:Fault>
    -->
</soap:Envelope>
```

**Spring Boot SOAP Consumer (JAX-WS):**
```java
// Generate client from WSDL using wsimport or Spring plugin
// Maven: jaxws-maven-plugin

@Service
public class LegacySOAPClient {
    
    private final OrderServicePortType port;

    public LegacySOAPClient(@Value("${soap.service.url}") String serviceUrl) {
        try {
            URL wsdlUrl = new URL(serviceUrl + "?wsdl");
            OrderService service = new OrderService(wsdlUrl);
            this.port = service.getOrderServicePort();
            
            // Set timeout
            BindingProvider bp = (BindingProvider) port;
            bp.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "5000");
            bp.getRequestContext().put("javax.xml.ws.client.receiveTimeout", "30000");
        } catch (Exception e) {
            throw new ServiceInitializationException("Failed to init SOAP client", e);
        }
    }

    public OrderResponse getOrder(String orderId) {
        GetOrderRequest request = new GetOrderRequest();
        request.setOrderId(orderId);
        return port.getOrder(request);
    }
}
```

---

### Q4. REST vs SOAP Comparison

| Feature | REST | SOAP |
|---|---|---|
| Protocol | HTTP | HTTP, SMTP, TCP, etc. |
| Format | JSON, XML, any | XML only |
| Contract | OpenAPI/Swagger (optional) | WSDL (mandatory) |
| Security | HTTPS, JWT, OAuth2 | WS-Security, SSL |
| Performance | Faster, lightweight | Heavier, more overhead |
| Stateless | Yes | Depends |
| Error handling | HTTP status codes | SOAP Fault |
| Transactions | No built-in | WS-AtomicTransaction |
| Best for | Public APIs, mobile, microservices | Enterprise, banking, B2B integrations |

---

## PART 11 — MICROSERVICES

### Q5. Microservices Architecture — Core Concepts

**Definition**: An architectural style where an application is built as a suite of small, independently deployable services, each running in its own process and communicating via APIs.

**Advantages:**
- Independent deployment and scaling
- Technology heterogeneity (each service can use different stack)
- Fault isolation (one service failure doesn't kill all)
- Smaller codebase per team → faster development
- Better align with business domains (Domain-Driven Design)

**Disadvantages:**
- Distributed system complexity
- Network latency and failures
- Data consistency challenges (no ACID across services)
- Operational overhead (monitoring, tracing, orchestration)
- Service discovery and routing complexity

---

### Q6. Spring Cloud Ecosystem

```yaml
# Complete microservices architecture:

# 1. Config Server - centralized configuration
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          
# 2. Eureka - Service Discovery
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true

# 3. API Gateway (Spring Cloud Gateway)
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE  # lb = load balanced via Eureka
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: userServiceCB
                fallbackUri: forward:/fallback/users
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/orders/**
```

---

### Q7. Feign Client — Service-to-Service Communication

```java
// Client definition
@FeignClient(
    name = "user-service",  // Matches eureka service name
    fallback = UserClientFallback.class  // Circuit breaker fallback
)
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
    
    @PostMapping("/api/users")
    UserResponse createUser(@RequestBody CreateUserRequest request);
    
    @GetMapping("/api/users")
    List<UserResponse> getUsersByIds(@RequestParam("ids") List<Long> ids);
}

// Fallback for when user-service is down
@Component
public class UserClientFallback implements UserServiceClient {
    
    @Override
    public UserResponse getUserById(Long id) {
        return UserResponse.builder()
            .id(id)
            .name("Unknown User")
            .build();
    }
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        throw new ServiceUnavailableException("User service is currently unavailable");
    }
    
    @Override
    public List<UserResponse> getUsersByIds(List<Long> ids) {
        return Collections.emptyList();
    }
}

// Usage in another service
@Service
public class OrderService {
    private final UserServiceClient userClient;
    private final OrderRepository orderRepository;
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Calls user-service via Feign (with load balancing and circuit breaker)
        UserResponse user = userClient.getUserById(request.getUserId());
        
        Order order = Order.builder()
            .userId(user.getId())
            .items(request.getItems())
            .status("PENDING")
            .build();
            
        return mapper.toResponse(orderRepository.save(order));
    }
}
```

---

### Q8. Circuit Breaker — Resilience4j

```java
// States: CLOSED → OPEN → HALF_OPEN → CLOSED/OPEN
// CLOSED: Normal operation, calls pass through
// OPEN: Too many failures, calls short-circuit (fast fail)
// HALF_OPEN: Test with limited calls, decide to recover or stay OPEN

// application.yml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        sliding-window-size: 10          # Last 10 calls
        failure-rate-threshold: 50       # Open if >50% fail
        wait-duration-in-open-state: 10s # Stay open for 10s
        permitted-number-of-calls-in-half-open-state: 3
        minimum-number-of-calls: 5       # Min calls before evaluating
        
  retry:
    instances:
      userService:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2  # 1s, 2s, 4s
        retry-exceptions:
          - java.io.IOException
          - feign.RetryableException
          
  ratelimiter:
    instances:
      userService:
        limit-for-period: 100     # 100 calls
        limit-refresh-period: 1s  # per second
        timeout-duration: 0       # Don't wait if limit exceeded

// Java code
@Service
public class PaymentService {
    
    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "paymentFallback")
    @Retry(name = "paymentGateway")
    @RateLimiter(name = "paymentGateway")
    public PaymentResult processPayment(PaymentRequest request) {
        return externalPaymentGateway.charge(request);
    }
    
    public PaymentResult paymentFallback(PaymentRequest request, Exception ex) {
        log.error("Payment gateway unavailable: {}", ex.getMessage());
        // Queue for retry later, or use backup gateway
        pendingPaymentQueue.add(request);
        return PaymentResult.queued(request.getId());
    }
}
```

---

### Q9. Kafka — Event-Driven Communication

**Concepts:**
- **Topic**: Category/feed of messages
- **Partition**: Topic split into partitions for parallelism
- **Consumer Group**: Group of consumers reading from same topic (each partition → one consumer)
- **Offset**: Position of consumer in partition
- **Producer**: Publishes messages to topics
- **Broker**: Kafka server storing messages

```java
// Producer
@Service
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderPlaced(Order order) {
        OrderEvent event = OrderEvent.builder()
            .orderId(order.getId())
            .customerId(order.getCustomerId())
            .totalAmount(order.getTotalAmount())
            .status("PLACED")
            .timestamp(Instant.now())
            .build();
            
        kafkaTemplate.send("order-events", order.getId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish order event: {}", ex.getMessage());
                } else {
                    log.info("Order event published to partition {} offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}

// Consumer
@Service
public class InventoryEventConsumer {
    
    @KafkaListener(
        topics = "order-events",
        groupId = "inventory-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderPlaced(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack) {
        try {
            log.info("Processing order event {} from partition {} offset {}",
                event.getOrderId(), partition, offset);
                
            inventoryService.deductStock(event.getOrderId(), event.getItems());
            
            ack.acknowledge(); // Manual commit only on success
        } catch (Exception e) {
            log.error("Failed to process order event", e);
            // Don't acknowledge — will be reprocessed
            // Or send to Dead Letter Topic
        }
    }
}

// application.yml
spring:
  kafka:
    bootstrap-servers: kafka:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all              # Wait for all replicas to acknowledge
      retries: 3
      properties:
        enable.idempotence: true  # Exactly-once semantics
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      auto-offset-reset: earliest
      enable-auto-commit: false  # Manual commit for reliability
      group-id: inventory-service
```

---

### Q10. Distributed Tracing — Zipkin/Jaeger

**Why needed?**: In microservices, a single request spans multiple services. Tracing shows the complete path and identifies bottlenecks.

```yaml
# application.yml - Micrometer Tracing (Spring Boot 3.x)
management:
  tracing:
    sampling:
      probability: 1.0  # Sample 100% (reduce in production)
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans

spring:
  sleuth:  # Spring Boot 2.x
    zipkin:
      base-url: http://zipkin:9411
    sampler:
      probability: 0.1  # Sample 10% in production
```

**ELK Stack for Logging:**
```yaml
# Logback config for JSON logging → Logstash → Elasticsearch → Kibana
# logback-spring.xml
<appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>logstash:5000</destination>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <includeMdcKeyName>traceId</includeMdcKeyName>
        <includeMdcKeyName>spanId</includeMdcKeyName>
    </encoder>
</appender>
```

---

## PART 12 — APPLICATION SERVERS

### Q11. Tomcat vs JBoss vs WebSphere vs WebLogic

| Feature | Tomcat | JBoss/WildFly | IBM WebSphere | BEA WebLogic |
|---|---|---|---|---|
| Type | Servlet Container | Full Java EE Server | Full Java EE Server | Full Java EE Server |
| Vendor | Apache | Red Hat | IBM | Oracle |
| License | Open Source | Open Source (+ Enterprise) | Commercial | Commercial |
| EJB Support | No | Yes | Yes | Yes |
| JMS Support | No | Yes | Yes | Yes |
| Typical Use | Spring Boot apps | Enterprise Java EE | Banking/IBM shops | Oracle/Enterprise |
| Deployment | WAR | WAR/EAR | WAR/EAR | WAR/EAR |
| Memory | Low | Medium | High | High |

---

### Q12. WAR vs EAR Deployment

```
WAR (Web Application Archive):
├── WEB-INF/
│   ├── web.xml          (Deployment descriptor)
│   ├── classes/         (Compiled .class files)
│   └── lib/             (JAR dependencies)
└── index.html, etc.

EAR (Enterprise Application Archive):
├── application.xml      (Deployment descriptor)
├── my-web.war           (Web module)
├── my-ejb.jar           (EJB module)
└── META-INF/

# Spring Boot - Embedded Tomcat (executable JAR)
# No external container needed - "java -jar app.jar"
# Can also be deployed as WAR to external Tomcat:
```

```java
// Converting Spring Boot to WAR for external Tomcat
@SpringBootApplication
public class MyApplication extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyApplication.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

// pom.xml
<packaging>war</packaging>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>  <!-- Provided by external Tomcat -->
</dependency>
```

---

### Q13. ClassLoader in Application Servers

```
Bootstrap ClassLoader (JVM built-in)
    └── Extension ClassLoader
            └── Application ClassLoader
                    └── WAR ClassLoader (each webapp gets its own!)
                            ← This provides isolation between webapps
```

**ClassLoader issues:**
- `ClassNotFoundException` vs `NoClassDefFoundError`
- Class loading order in WAS/WebLogic (parent-first vs parent-last)
- `log4j`, `slf4j` conflicts between app server and application

---

*Next: [Part 13-14 - Git & Build Tools](./Part-13-14-Git-BuildTools.md)*
