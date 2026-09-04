# Spring Ecosystem — CV Deep Dive
> Senior-level technical reference. Viết dựa trên kiến thức thực tế trong CV.
> Không có lý thuyết thừa. Mỗi section = concept + mechanism + code + interview Q&A.

---

## 1. @Transactional — Internals & Traps

### 1.1 Cơ chế hoạt động

Spring `@Transactional` hoạt động qua **AOP proxy**. Khi bạn gọi method từ bên ngoài bean, lời gọi đi qua proxy → proxy mở transaction → gọi method thật → proxy commit/rollback.

```
Caller → [Spring Proxy] → openTransaction() → [Real Bean Method] → commit/rollback
```

Spring tạo proxy theo 2 cách:
- **JDK Dynamic Proxy**: khi bean implement interface
- **CGLIB Proxy**: khi bean không implement interface (subclass bytecode generation)

---

### 1.2 Propagation — Khái Niệm & Bảng Đầy Đủ

#### 🔑 Transaction là gì?

**Transaction** = một tập hợp các thao tác DB được thực thi như một khối duy nhất. Hoặc **tất cả thành công** (COMMIT), hoặc **tất cả bị hoàn tác** (ROLLBACK).

```
Ví dụ: Chuyển tiền từ Ví A sang Ví B

Bước 1: Trừ 100k từ Ví A
Bước 2: Cộng 100k vào Ví B

→ Nếu Bước 2 fail → Bước 1 phải được rollback (hoàn tác)
→ Không được để Ví A bị trừ tiền mà Ví B không nhận được
```

Trong Java/Spring, transaction tương ứng với **1 connection đến DB**. Trong transaction, mọi câu lệnh SQL đều thuộc cùng 1 "phiên làm việc" với DB. Đến khi `commit()` → DB ghi dữ liệu thật. Đến khi `rollback()` → DB hoàn tác hết.

---

#### 🔑 Propagation là gì?

**Propagation** (lan truyền) = **quy tắc xử lý transaction khi method này gọi method khác**.

Câu hỏi cốt lõi: *"Khi Service A (đang có transaction) gọi Service B, thì Service B dùng transaction nào?"*

- Dùng chung transaction của A? (REQUIRED)
- Tạo transaction mới riêng? (REQUIRES_NEW)
- Không cần transaction? (NOT_SUPPORTED)

```
Service A (@Transactional)
    │
    └──→ Service B (@Transactional ???)
              Propagation quyết định điều này
```

---

#### 🔑 Suspend (Tạm dừng) Transaction là gì?

Khi **Suspend** xảy ra, Spring **tạm thời đặt transaction hiện tại sang một bên**, tạo và dùng transaction mới. Khi transaction mới kết thúc (commit hoặc rollback), Spring **khôi phục lại** transaction cũ và tiếp tục.

```
Transaction A đang chạy...
    → Gặp REQUIRES_NEW → Suspend A
    → Transaction B chạy → commit/rollback độc lập
    → Restore Transaction A → tiếp tục
```

---

#### Bảng Propagation Đầy Đủ

| Propagation | Có transaction hiện tại | Không có transaction |
|---|---|---|
| `REQUIRED` (default) | Tham gia vào transaction hiện tại | Tạo transaction mới |
| `REQUIRES_NEW` | Suspend (tạm dừng) transaction hiện tại, tạo transaction mới độc lập | Tạo transaction mới |
| `SUPPORTS` | Tham gia vào transaction hiện tại | Chạy không có transaction |
| `NOT_SUPPORTED` | Suspend (tạm dừng) transaction hiện tại, chạy không có transaction | Chạy không có transaction |
| `MANDATORY` | Tham gia vào transaction hiện tại | Throw `IllegalTransactionStateException` (bắt buộc phải có TX từ trước) |
| `NEVER` | Throw `IllegalTransactionStateException` (cấm có TX) | Chạy không có transaction |
| `NESTED` | Tạo savepoint bên trong transaction hiện tại (có thể rollback về savepoint mà không ảnh hưởng outer TX) | Tạo transaction mới |

**Ví dụ thực tế — REQUIRES_NEW:**
```java
@Service
public class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(String action) {
        // Luôn commit độc lập, dù outer transaction có rollback
        auditRepo.save(new AuditLog(action));
    }
}

@Service
public class WalletService {
    @Autowired AuditService auditService;

    @Transactional
    public void transfer(Long from, Long to, BigDecimal amount) {
        debit(from, amount);
        auditService.logAudit("DEBIT"); // Commit ngay lập tức
        credit(to, amount);
        if (amount.compareTo(limit) > 0) {
            throw new RuntimeException("Over limit"); // WalletService rollback
            // nhưng AuditLog đã commit rồi → không bị rollback
        }
    }
}
```

---

### 1.3 Self-Invocation Trap ⚠️ (Câu hỏi phỏng vấn kinh điển)

#### 🔑 Khái niệm trước khi đọc

**Proxy** = Spring tạo ra một lớp "bọc" bên ngoài bean của bạn. Khi bạn gọi method từ bên ngoài (ví dụ Controller gọi Service), lời gọi đi qua proxy này → proxy kích hoạt `@Transactional`, `@Cacheable`, `@AOP`...

```
[Controller] → gọi method → [PROXY] → kích hoạt @Transactional → [Bean thật]
```

**Self-Invocation** (tự gọi) = method trong class gọi method khác trong cùng class thông qua `this`. Lời gọi đi thẳng vào bean thật, **bỏ qua proxy** → `@Transactional` không có tác dụng.

```
[Method A] → this.methodB() → [Bean thật, bỏ qua Proxy] → @Transactional IGNORED
```

**Vấn đề cụ thể**: Khi method trong cùng class gọi nhau, lời gọi đi qua `this` — không qua proxy → `@Transactional` bị bỏ qua.

```java
@Service
public class OrderService {
    @Transactional
    public void placeOrder(Order order) {
        // ...
        this.sendNotification(order); // ❌ Gọi qua 'this', không qua proxy
                                      // @Transactional KHÔNG có effect
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        // Tưởng là TX mới, nhưng thực ra chạy trong TX của placeOrder
    }
}
```

**Fix 1 — Inject self (đơn giản nhất):**
```java
@Service
public class OrderService {
    @Autowired
    private OrderService self; // Spring inject proxy của chính nó

    @Transactional
    public void placeOrder(Order order) {
        self.sendNotification(order); // ✅ Đi qua proxy
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) { ... }
}
```

**Fix 2 — Tách ra bean riêng (Clean nhất, khuyến nghị):**
```java
@Service
public class NotificationService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) { ... }
}
```

**Fix 3 — AopContext (cần enable):**
```java
// Cần @EnableAspectJAutoProxy(exposeProxy = true)
((OrderService) AopContext.currentProxy()).sendNotification(order);
```

---

### 1.4 Isolation Levels

#### 🔑 Isolation là gì?

**Isolation** (cô lập) = **mức độ các transaction ảnh hưởng lẫn nhau khi chạy đồng thời**.

Khi có nhiều transaction chạy cùng lúc (concurrent), chúng có thể đọc/ghi cùng một data → sinh ra 3 vấn đề:

| Vấn đề | Giải thích đơn giản | Ví dụ |
|---|---|---|
| **Dirty Read** (đọc bẩn) | Đọc được data của TX khác chưa commit — data đó có thể bị rollback | TX A đọc số dư 1tr (TX B đang trừ nhưng chưa commit). TX B rollback → số dư thật vẫn là 1tr nhưng TX A đã dùng số sai |
| **Non-Repeatable Read** (đọc không lặp lại được) | Đọc cùng 1 row 2 lần trong cùng TX → kết quả khác nhau vì TX khác đã UPDATE row đó | TX A đọc lương = 5tr. TX B update lương thành 7tr và commit. TX A đọc lại → 7tr. Khác! |
| **Phantom Read** (đọc bóng ma) | Đọc cùng 1 điều kiện 2 lần → số lượng row khác nhau vì TX khác INSERT/DELETE | TX A đếm users có role=ADMIN → 5 người. TX B thêm 1 admin và commit. TX A đếm lại → 6 người. Khác! |

---

#### Bảng Isolation Levels

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read | Performance |
|---|---|---|---|---|
| `READ_UNCOMMITTED` | ✅ Có thể xảy ra | ✅ Có thể | ✅ Có thể | Cao nhất |
| `READ_COMMITTED` *(default của hầu hết DB)* | ❌ Ngăn được | ✅ Có thể | ✅ Có thể | Cao |
| `REPEATABLE_READ` | ❌ Ngăn được | ❌ Ngăn được | ✅ Có thể | Trung bình |
| `SERIALIZABLE` | ❌ Ngăn được | ❌ Ngăn được | ❌ Ngăn được | Thấp nhất |

> **Nguyên tắc**: Isolation càng cao → data càng chính xác nhưng performance càng thấp (DB phải lock nhiều hơn).

**Định nghĩa ngắn gọn (để nhớ khi phỏng vấn):**
- **Dirty Read**: Đọc data chưa commit của TX khác
- **Non-Repeatable Read**: Đọc 2 lần cùng row → kết quả khác (do UPDATE)
- **Phantom Read**: Đọc 2 lần cùng điều kiện → số row khác (do INSERT/DELETE)

```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public BigDecimal getBalance(Long walletId) {
    // Đảm bảo cùng walletId luôn trả về cùng balance trong transaction này
    return walletRepo.findById(walletId).get().getBalance();
}
```

---

### 1.5 Interview Q&A

**Q: `@Transactional` trên private method có hoạt động không?**
A: Không. Spring proxy chỉ intercept được public method. Private method bị bỏ qua hoàn toàn.

**Q: RuntimeException vs CheckedException với rollback?**
A: Mặc định chỉ rollback với `RuntimeException` (unchecked). Checked exception không rollback.
```java
@Transactional(rollbackFor = Exception.class) // Rollback cả checked exception
@Transactional(noRollbackFor = BusinessException.class) // Không rollback với exception này
```

---

## 2. Spring AOP — Proxy Mechanism

### 2.1 Advice Types

```java
@Aspect
@Component
public class LoggingAspect {

    // Pointcut expression: tất cả method trong service package
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}

    @Before("serviceLayer()")
    public void logBefore(JoinPoint jp) {
        log.info("Calling: {}", jp.getSignature().getName());
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturn(JoinPoint jp, Object result) {
        log.info("Returned: {}", result);
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint jp, Exception ex) {
        log.error("Exception in {}: {}", jp.getSignature().getName(), ex.getMessage());
    }

    @Around("serviceLayer()")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed(); // gọi method thật
        long elapsed = System.currentTimeMillis() - start;
        log.info("{} took {}ms", pjp.getSignature().getName(), elapsed);
        return result;
    }
}
```

### 2.2 Pointcut Expressions Thực Tế

```java
// Tất cả method trong package service
execution(* com.example.service.*.*(..))

// Method có annotation @Loggable
@annotation(com.example.annotation.Loggable)

// Bean name cụ thể
bean(walletService)

// Method có argument là Long
execution(* *.*(Long, ..))
```

### 2.3 Use Case Thực Tế Từ CV

**Audit Logging (FPM Project pattern):**
```java
@Around("@annotation(Auditable)")
public Object auditTransaction(ProceedingJoinPoint pjp) throws Throwable {
    String method = pjp.getSignature().getName();
    Object[] args = pjp.getArgs();
    
    try {
        Object result = pjp.proceed();
        auditService.log(method, args, "SUCCESS", null);
        return result;
    } catch (Exception ex) {
        auditService.log(method, args, "FAILED", ex.getMessage());
        throw ex;
    }
}
```

---

## 3. Spring Security + JWT — Filter Chain & Token Blacklisting

### 3.1 Security Filter Chain

Request đi qua chain các filter theo thứ tự:
```
HTTP Request
    → SecurityContextPersistenceFilter  (restore SecurityContext)
    → UsernamePasswordAuthenticationFilter
    → [Custom] JwtAuthenticationFilter  (validate JWT, set context)
    → ExceptionTranslationFilter        (handle 401/403)
    → FilterSecurityInterceptor         (authorization check)
    → Controller
```

### 3.2 JWT Authentication Filter Implementation

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private RedisTokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 1. Check blacklist (Redis)
            if (tokenBlacklist.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                return;
            }

            // 2. Extract claims
            String userId = jwtTokenProvider.getUserId(token);
            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

            // 3. Set SecurityContext
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

### 3.3 Token Blacklisting với Redis (FPM Pattern)

```java
@Service
public class RedisTokenBlacklist {

    @Autowired private StringRedisTemplate redisTemplate;

    private static final String PREFIX = "blacklist:token:";

    // Khi logout → blacklist token với TTL = remaining token lifetime
    public void blacklist(String token, long remainingSeconds) {
        String jti = extractJti(token); // JWT ID claim
        redisTemplate.opsForValue()
            .set(PREFIX + jti, "revoked", Duration.ofSeconds(remainingSeconds));
    }

    public boolean isBlacklisted(String token) {
        String jti = extractJti(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }

    private String extractJti(String token) {
        // Parse JWT and extract 'jti' claim
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getId();
    }
}
```

### 3.4 RBAC Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/wallets/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}

// Method-level security
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
public WalletDTO getWallet(Long userId) { ... }
```

---

## 4. Spring Cloud — Gateway & Eureka

### 4.1 Spring Cloud Gateway

Gateway hoạt động theo pipeline: **Route → Predicate → Filter → Downstream Service**

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: wallet-service
          uri: lb://WALLET-SERVICE  # lb:// → load-balanced via Eureka
          predicates:
            - Path=/api/wallets/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: walletCircuitBreaker
                fallbackUri: forward:/fallback/wallet
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
```

**Custom Gateway Filter (JWT validation tại Gateway):**
```java
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicEndpoint(request.getPath())) {
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (!jwtValidator.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Propagate user info to downstream
        ServerHttpRequest mutatedRequest = request.mutate()
            .header("X-User-Id", jwtValidator.getUserId(token))
            .header("X-User-Role", jwtValidator.getRole(token))
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() { return -1; } // Chạy trước tất cả filter khác
}
```

### 4.2 Eureka — Client-Side Service Discovery

```
Service A → [Eureka Client] → query Eureka Server → lấy danh sách instances của Service B
         → [Ribbon/Spring Cloud LoadBalancer] → chọn instance → gọi Service B
```

```java
// @EnableEurekaClient đã tự kích hoạt khi có spring-cloud-starter-netflix-eureka-client
// Chỉ cần:
@FeignClient(name = "WALLET-SERVICE")  // name = service ID đăng ký với Eureka
public interface WalletClient {
    @GetMapping("/api/wallets/{id}")
    WalletDTO getWallet(@PathVariable Long id);
}
```

```yaml
# Eureka client config
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10  # Heartbeat interval
    lease-expiration-duration-in-seconds: 30
```

**Client-side vs Server-side Load Balancing:**
- **Client-side** (Eureka + Ribbon): Client tự query registry, tự chọn instance. Không có single point of failure ở LB.
- **Server-side** (AWS ALB, Nginx): Client chỉ biết 1 endpoint, server LB phân phối. Đơn giản hơn cho client.

---

## 5. Interview Q&A — Spring Cloud

**Q: Tại sao nên validate JWT tại API Gateway thay vì từng service?**
A: Centralize authentication logic. Downstream services nhận request đã được authenticated, chỉ cần care về authorization. Giảm duplicate code và single point để update JWT logic.

**Q: Eureka vs Consul vs Kubernetes Service Discovery?**
A:
- Eureka: Client-side, AP (Available + Partition tolerant), dễ setup trong Spring
- Consul: CP (Consistent), có health check mạnh hơn, có Key-Value store
- K8s: Built-in DNS-based, kube-proxy handles routing. Dùng K8s → không cần Eureka

**Q: Spring Cloud Gateway vs Zuul?**
A: Gateway dùng Project Reactor (non-blocking, reactive). Zuul 1.x blocking (servlet-based), Zuul 2 reactive nhưng ít adoption hơn Gateway.
