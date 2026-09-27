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
    // REQUIRES_NEW: Luôn tạo TX riêng, KHÔNG phụ thuộc vào TX bên ngoài
    // → Dù outer TX rollback, audit log vẫn được commit
    // Use case: ghi log kiểm toán (audit) — luôn cần lưu lại dù nghiệp vụ thất bại
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(String action) {
        auditRepo.save(new AuditLog(action));
        // Khi method này kết thúc → TX riêng commit ngay lập tức
        // Không bị ảnh hưởng bởi TX của WalletService
    }
}

@Service
public class WalletService {
    // Inject bean khác → gọi qua proxy → @Transactional có hiệu lực
    @Autowired AuditService auditService;

    // REQUIRED (default): tạo TX mới nếu chưa có, tham gia nếu đã có
    @Transactional
    public void transfer(Long from, Long to, BigDecimal amount) {
        debit(from, amount);             // Trừ tiền Ví A (nằm trong TX của transfer)
        auditService.logAudit("DEBIT");  // ← REQUIRES_NEW: Spring SUSPEND TX hiện tại
                                         //   → Tạo TX mới cho logAudit → commit → Restore TX cũ
        credit(to, amount);              // Cộng tiền Ví B
        if (amount.compareTo(limit) > 0) {
            throw new RuntimeException("Over limit");
            // TX của transfer() ROLLBACK → debit + credit đều bị hoàn tác
            // NHƯNG AuditLog đã commit trong TX riêng → KHÔNG bị rollback
            // → Đây là lý do dùng REQUIRES_NEW cho audit: luôn giữ lại bằng chứng
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
    @Transactional  // Proxy mở TX khi Controller gọi placeOrder()
    public void placeOrder(Order order) {
        // ...
        this.sendNotification(order);
        // ❌ 'this' = instance thật của OrderService, KHÔNG phải proxy
        // → Lời gọi đi thẳng vào method thật, bỏ qua proxy hoàn toàn
        // → @Transactional trên sendNotification() bị IGNORE
        // → REQUIRES_NEW không tạo TX mới → vẫn dùng TX của placeOrder()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        // Kỳ vọng: Chạy trong TX mới, độc lập với placeOrder
        // Thực tế: Chạy trong CÙNG TX với placeOrder vì bị gọi qua 'this'
        // → Nếu sendNotification fail → placeOrder cũng rollback (vì cùng TX)
    }
}
```

**Fix 1 — Inject self (đơn giản nhất):**
```java
@Service
public class OrderService {
    @Autowired
    private OrderService self;
    // ✅ Spring inject PROXY của OrderService vào biến 'self'
    // Khi gọi self.method() → đi qua proxy → AOP/@Transactional hoạt động
    // Lưu ý: Không bị circular dependency vì Spring resolve lazy

    @Transactional
    public void placeOrder(Order order) {
        self.sendNotification(order);
        // ✅ self = proxy → gọi qua proxy → @Transactional REQUIRES_NEW có hiệu lực
        // → Spring suspend TX hiện tại, tạo TX mới cho sendNotification()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) { ... }
}
```

**Fix 2 — Tách ra bean riêng (Clean nhất, khuyến nghị):**
```java
@Service
public class NotificationService {
    // ✅ Tách logic notification ra bean riêng
    // → OrderService inject NotificationService → gọi qua proxy → AOP hoạt động
    // → REQUIRES_NEW tạo TX mới đúng như kỳ vọng
    // → Clean nhất vì tuân thủ Single Responsibility Principle (SRP)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) { ... }
}
```

**Fix 3 — AopContext (cần enable):**
```java
// Cần khai báo @EnableAspectJAutoProxy(exposeProxy = true) trên @Configuration class
// AopContext.currentProxy() trả về proxy của bean hiện tại
// → Cast về OrderService → gọi method qua proxy → @Transactional hoạt động
// ⚠️ Nhược điểm: code phụ thuộc vào Spring AOP API, không clean, khó test
// → Chỉ dùng khi không refactor được (legacy code)
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
// REPEATABLE_READ: Trong cùng 1 TX, đọc cùng 1 row nhiều lần luôn ra cùng kết quả
// → DB giữ snapshot tại thời điểm bắt đầu TX, không bị ảnh hưởng bởi TX khác UPDATE
// Use case: getBalance cần đọc chính xác, không bị "nhảy" giữa các lần đọc
@Transactional(isolation = Isolation.REPEATABLE_READ)
public BigDecimal getBalance(Long walletId) {
    return walletRepo.findById(walletId).get().getBalance();
    // Nếu dùng READ_COMMITTED → TX khác update balance giữa chừng → đọc lại ra số khác
    // Với REPEATABLE_READ → luôn đọc cùng giá trị cho đến khi TX này kết thúc
}
```

---

### 1.5 Interview Q&A

**Q: `@Transactional` trên private method có hoạt động không?**
A: Không. Spring proxy chỉ intercept (chặn) được public method. Private method bị bỏ qua hoàn toàn.

**Q: RuntimeException vs CheckedException với rollback?**
A: Mặc định chỉ rollback với `RuntimeException` (unchecked). Checked exception không rollback.
```java
// Mặc định: Spring chỉ rollback khi gặp RuntimeException (unchecked)
// Checked exception (IOException, SQLException...) KHÔNG rollback → data có thể inconsistent!

@Transactional(rollbackFor = Exception.class)
// → Override mặc định: rollback cho TẤT CẢ exception (cả checked lẫn unchecked)
// → Best practice: luôn dùng cái này nếu không muốn bất ngờ

@Transactional(noRollbackFor = BusinessException.class)
// → Ngoại lệ: BusinessException dù là RuntimeException nhưng KHÔNG rollback
// Use case: lỗi nghiệp vụ (VD: "Số dư không đủ") — vẫn muốn commit các thay đổi khác
```

---

## 2. Spring AOP — Proxy Mechanism

### 2.1 Advice Types

```java
// @Aspect: Đánh dấu class này là một Aspect — nơi định nghĩa cross-cutting concerns các mối quan tâm xuyên suốt
//          (logic cắt ngang nhiều class: logging, security, caching...)
// @Component: Đăng ký làm Spring Bean để Spring quản lý và kích hoạt
@Aspect
@Component
public class LoggingAspect {

    // @Pointcut: Định nghĩa "điểm cắt" — NƠI mà advice sẽ được áp dụng
    // execution(* com.example.service.*.*(..)) nghĩa là:
    //   * (return type)  : bất kỳ kiểu trả về nào
    //   com.example.service.* (class) : tất cả class trong package service
    //   .* (method)      : tất cả method
    //   (..) (params)    : bất kỳ tham số nào
    // → Tóm lại: MỌI method trong MỌI class thuộc package service
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() {}
    // Method body rỗng — chỉ dùng làm "tên" cho pointcut, để reuse ở nhiều advice

    // @Before: Chạy TRƯỚC khi method thật được gọi
    // JoinPoint jp: chứa metadata về method đang được intercept
    //   - jp.getSignature().getName(): tên method (VD: "placeOrder")
    //   - jp.getArgs(): mảng các tham số truyền vào method
    @Before("serviceLayer()")
    public void logBefore(JoinPoint jp) {
        log.info("Calling: {}", jp.getSignature().getName());
        // Output: "Calling: placeOrder" — log trước khi method chạy
    }

    // @AfterReturning: Chạy SAU khi method thật return THÀNH CÔNG (không throw exception)
    // returning = "result": bind giá trị trả về của method vào param 'result'
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturn(JoinPoint jp, Object result) {
        log.info("Returned: {}", result);
        // Output: "Returned: WalletDTO{id=1, balance=1000}" — log kết quả trả về
        // ⚠️ KHÔNG chạy nếu method throw exception
    }

    // @AfterThrowing: Chạy SAU khi method throw exception
    // throwing = "ex": bind exception được throw vào param 'ex'
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint jp, Exception ex) {
        log.error("Exception in {}: {}", jp.getSignature().getName(), ex.getMessage());
        // Output: "Exception in transfer: Over limit" — log lỗi
        // ⚠️ KHÔNG ngăn exception — exception vẫn propagate lên caller
    }

    // @Around: MẠNH NHẤT — bao quanh method thật, kiểm soát TRƯỚC + SAU + có thể thay đổi kết quả
    // ProceedingJoinPoint: mở rộng JoinPoint, thêm method proceed() để gọi method thật
    @Around("serviceLayer()")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();  // Ghi nhận thời điểm bắt đầu
        Object result = pjp.proceed();             // GỌI METHOD THẬT — bắt buộc gọi, nếu không method sẽ không chạy
        long elapsed = System.currentTimeMillis() - start;  // Tính thời gian chạy
        log.info("{} took {}ms", pjp.getSignature().getName(), elapsed);
        // Output: "transfer took 125ms" — đo performance
        return result;
        // PHẢI return result — nếu không, caller nhận null thay vì kết quả thật
        // Có thể modify result trước khi return (dùng cho caching, transformation...)
    }
}
```

### 2.2 Pointcut Expressions Thực Tế

```java
// === POINTCUT EXPRESSION SYNTAX ===
// Cú pháp: execution(modifiers? return-type declaring-type? method-name(params) throws?)

// 1. Tất cả method trong package service (phổ biến nhất)
execution(* com.example.service.*.*(..))
// Phân tích: *          = bất kỳ return type
//           service.*   = bất kỳ class nào trong package service (1 cấp)
//           .*          = bất kỳ method name
//           (..)        = bất kỳ số lượng và kiểu param
// ⚠️ service.* chỉ match 1 cấp. Muốn match sub-package → dùng service..*

// 2. Method có annotation @Loggable — match bất kỳ method nào được đánh dấu @Loggable
@annotation(com.example.annotation.Loggable)
// Use case: tự tạo custom annotation để đánh dấu method cần log
// VD: @Loggable trên transfer() → advice tự kích hoạt

// 3. Bean name cụ thể — match tất cả method của bean có tên "walletService"
bean(walletService)
// Spring-specific (không phải AspectJ standard)
// Hữu ích khi muốn apply advice cho 1 bean cụ thể, không cần pointcut phức tạp

// 4. Method có argument đầu tiên là Long
execution(* *.*(Long, ..))
// Long = param đầu tiên phải là Long
// ..   = sau đó có bao nhiêu param cũng được
// VD: match getWallet(Long id) nhưng KHÔNG match getWallet(String name)
```

### 2.3 Use Case Thực Tế Từ CV

**Audit Logging (FPM Project pattern):**
```java
// @Around + @annotation(Auditable): Bao quanh BẤT KỲ method nào có annotation @Auditable
// Pattern: Controller/Service đánh @Auditable → Aspect tự động ghi audit log
// → Không cần viết try-catch trong từng method → DRY (Don't Repeat Yourself)
@Around("@annotation(Auditable)")
public Object auditTransaction(ProceedingJoinPoint pjp) throws Throwable {
    String method = pjp.getSignature().getName();  // Tên method (VD: "transfer")
    Object[] args = pjp.getArgs();                  // Mảng tham số (VD: [fromId, toId, amount])
    
    try {
        Object result = pjp.proceed();              // Gọi method thật
        // Nếu thành công → ghi audit log với status SUCCESS
        auditService.log(method, args, "SUCCESS", null);
        return result;                              // Trả kết quả về caller
    } catch (Exception ex) {
        // Nếu thất bại → ghi audit log với status FAILED + error message
        auditService.log(method, args, "FAILED", ex.getMessage());
        throw ex;  // QUAN TRỌNG: ném lại exception để caller vẫn biết lỗi
                   // Nếu KHÔNG throw → caller tưởng thành công → bug nghiêm trọng
    }
    // → Dù thành công hay thất bại, audit log đều được ghi
    // → FPM Pattern: mọi thao tác tài chính đều phải có bằng chứng
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
// @Component: Đăng ký class này như một Spring Bean, Spring tự quản lý lifecycle
// Kế thừa OncePerRequestFilter: đảm bảo filter chỉ chạy ĐÚNG 1 LẦN cho mỗi HTTP request
// (tránh trường hợp filter bị gọi lại khi request forward/redirect nội bộ)
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JwtTokenProvider: service tự viết, chịu trách nhiệm tạo/validate/parse JWT token
    @Autowired private JwtTokenProvider jwtTokenProvider;
    // RedisTokenBlacklist: service kiểm tra token đã bị thu hồi (revoke) chưa,
    // lưu trong Redis để tra cứu nhanh O(1) và tự hết hạn theo TTL
    @Autowired private RedisTokenBlacklist tokenBlacklist;

    // doFilterInternal(): method bắt buộc override từ OncePerRequestFilter
    // Được gọi tự động cho MỌI request đi qua filter chain
    // Tham số:
    //   - request:     chứa toàn bộ thông tin HTTP request (headers, params, body...)
    //   - response:    dùng để trả response về client (set status, write body...)
    //   - filterChain: chuỗi các filter tiếp theo, gọi doFilter() để chuyển request sang filter kế
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        // Bước 0: Trích xuất JWT token từ header "Authorization: Bearer <token>"
        String token = extractBearerToken(request);

        // Chỉ xử lý khi: token tồn tại VÀ token hợp lệ (chưa hết hạn, signature đúng...)
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // Bước 1: Kiểm tra token có trong blacklist (Redis) không
            // Use case: user logout → token bị đưa vào blacklist
            // → dù token chưa hết hạn vẫn bị từ chối
            if (tokenBlacklist.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                return; // DỪNG ngay, không cho request đi tiếp vào controller
            }

            // Bước 2: Parse JWT payload (claims) để lấy thông tin user
            // userId: định danh user (thường là ID hoặc username, lưu trong claim "sub")
            String userId = jwtTokenProvider.getUserId(token);
            // authorities: danh sách quyền/role của user (VD: ROLE_ADMIN, ROLE_USER)
            // Spring Security dùng để kiểm tra @PreAuthorize, hasRole()...
            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

            // Bước 3: Tạo Authentication object và đặt vào SecurityContext
            // UsernamePasswordAuthenticationToken(principal, credentials, authorities):
            //   - principal:   userId — ai đang đăng nhập
            //   - credentials: null — không cần password vì đã xác thực qua JWT
            //   - authorities: danh sách quyền — dùng cho authorization sau này
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // Gắn thêm thông tin request (IP, session ID...) vào authentication
            // Hữu ích cho audit log, tracking
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // ĐẶT authentication vào SecurityContext
            // → Từ đây trở đi, toàn bộ code downstream (controller, service...)
            //   có thể lấy user hiện tại qua SecurityContextHolder.getContext().getAuthentication()
            // → @PreAuthorize, @Secured, hasRole() đều dựa vào context này
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // Nếu token null hoặc không hợp lệ → KHÔNG set SecurityContext
        // → SecurityContext rỗng → Spring Security coi là anonymous/unauthenticated
        // → Các endpoint yêu cầu auth sẽ trả 401/403

        // LUÔN gọi filterChain.doFilter() để chuyển request sang filter tiếp theo
        // Nếu KHÔNG gọi → request bị "nuốt", client không nhận được response
        filterChain.doFilter(request, response);
    }

    // Helper method: trích xuất JWT token từ header Authorization
    // Format chuẩn: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx.yyy"
    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        // Kiểm tra header không rỗng VÀ bắt đầu bằng "Bearer "
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // Cắt bỏ 7 ký tự "Bearer " → lấy phần token thuần
        }
        return null; // Không có token → trả null → filter sẽ bỏ qua, coi như anonymous
    }
}
```

### 3.3 Token Blacklisting với Redis (FPM Pattern)

```java
// Service quản lý danh sách token bị thu hồi (blacklist)
// Tại sao cần: JWT là stateless — server không lưu session
// → Khi user logout, token vẫn valid cho đến hết hạn
// → Giải pháp: lưu token đã bị revoke vào Redis → check mỗi request
@Service
public class RedisTokenBlacklist {

    // StringRedisTemplate: Spring Data Redis template cho key/value dạng String
    // Dùng để đọc/ghi data vào Redis
    @Autowired private StringRedisTemplate redisTemplate;

    // Prefix cho key trong Redis, tránh xung đột với key khác
    // Key format: "blacklist:token:abc-123-def" (abc-123-def = JTI)
    private static final String PREFIX = "blacklist:token:";

    // Được gọi khi user LOGOUT
    // remainingSeconds = thời gian còn lại trước khi token hết hạn
    // → TTL = remainingSeconds: key tự xóa sau khi token hết hạn
    // → Tại sao không lưu vĩnh viễn? Vì token hết hạn rồi thì blacklist vô nghĩa
    //   → Tiết kiệm bộ nhớ Redis
    public void blacklist(String token, long remainingSeconds) {
        String jti = extractJti(token); // JTI = JWT ID — mã định danh duy nhất của token
        redisTemplate.opsForValue()
            .set(PREFIX + jti, "revoked", Duration.ofSeconds(remainingSeconds));
        // Lưu vào Redis: key="blacklist:token:<jti>", value="revoked", TTL=remainingSeconds
        // Sau remainingSeconds giây → Redis tự xóa key → không cần cleanup thủ công
    }

    // Kiểm tra token có bị blacklist không — gọi trong JwtAuthenticationFilter
    // Độ phức tạp: O(1) — Redis lookup cực nhanh (sub-millisecond)
    public boolean isBlacklisted(String token) {
        String jti = extractJti(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
        // hasKey() trả về Boolean (có thể null) → dùng Boolean.TRUE.equals() để tránh NPE
        // true = token đã bị revoke → từ chối request
    }

    // Trích xuất JTI (JWT ID) từ token
    // JTI là claim tiêu chuẩn trong JWT spec (RFC 7519)
    // Dùng JTI thay vì toàn bộ token string → tiết kiệm bộ nhớ Redis
    private String extractJti(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)    // Verify signature bằng secret key
            .build()
            .parseClaimsJws(token)       // Parse token → Claims object
            .getBody()                   // Lấy payload (claims)
            .getId();                    // Lấy claim "jti" — JWT ID
    }
}
```

### 3.4 RBAC Configuration

```java
// @Configuration: Đánh dấu class này là nguồn config cho Spring IoC container
// @EnableWebSecurity: Kích hoạt Spring Security — bắt buộc để config security
// @EnableMethodSecurity: Kích hoạt @PreAuthorize, @PostAuthorize, @Secured trên method
//   → Nếu không có annotation này, @PreAuthorize sẽ bị IGNORE
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // SecurityFilterChain: cấu hình chuỗi filter xử lý security cho mọi HTTP request
    // Spring Security 6.x dùng Lambda DSL (thay thế cho .and() chain cũ)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // CSRF (Cross-Site Request Forgery): TẮT vì dùng JWT (stateless)
            // CSRF chỉ cần khi dùng session + cookie (stateful)
            // REST API + JWT → không cần CSRF protection
            .csrf(csrf -> csrf.disable())

            // Session Management: STATELESS — Spring KHÔNG tạo/dùng HttpSession
            // Mỗi request phải tự chứng minh danh tính qua JWT trong header
            // → Phù hợp cho REST API, microservices
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))

            // Authorization Rules: quy tắc phân quyền theo URL pattern
            // Thứ tự QUAN TRỌNG — rule đầu tiên match sẽ được áp dụng
            .authorizeHttpRequests(auth -> auth
                // /api/auth/** (login, register): cho phép TẤT CẢ, không cần token
                .requestMatchers("/api/auth/**").permitAll()
                // /api/admin/**: chỉ user có ROLE_ADMIN mới truy cập được
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // GET /api/wallets/**: USER hoặc ADMIN đều xem được
                // Lưu ý: chỉ GET, POST/PUT/DELETE cần rule riêng hoặc rơi vào anyRequest()
                .requestMatchers(HttpMethod.GET, "/api/wallets/**").hasAnyRole("USER", "ADMIN")
                // Tất cả request còn lại: phải authenticated (có token hợp lệ)
                .anyRequest().authenticated()
            )

            // CHÈN JwtAuthenticationFilter VÀO TRƯỚC UsernamePasswordAuthenticationFilter
            // → JWT filter chạy trước → set SecurityContext
            // → Các filter sau đó đọc SecurityContext để authorize
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}

// Method-level security: kiểm tra quyền ngay tại method (fine-grained)
// hasRole('ADMIN'): user có role ADMIN → cho phép
// #userId == authentication.principal: user chỉ xem được ví của CHÍNH MÌNH
//   → #userId = tham số method, authentication.principal = userId từ JWT
// Kết hợp bằng 'or': ADMIN xem được tất cả, USER chỉ xem ví của mình
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
public WalletDTO getWallet(Long userId) { ... }
```

---

## 4. Spring Cloud — Gateway & Eureka

### 4.1 Spring Cloud Gateway

Gateway hoạt động theo pipeline: **Route → Predicate → Filter → Downstream Service**

```yaml
# application.yml — Cấu hình Spring Cloud Gateway
# Gateway = cổng vào duy nhất cho tất cả microservices (API Gateway pattern)
spring:
  cloud:
    gateway:
      routes:
        - id: wallet-service           # ID duy nhất cho route — dùng để debug/logging
          uri: lb://WALLET-SERVICE     # lb:// = Load Balanced → Gateway tự query Eureka
                                       # để lấy danh sách instances của WALLET-SERVICE
                                       # và phân tải (round-robin) giữa các instance
          predicates:
            - Path=/api/wallets/**     # Predicate: route này CHỈ match request có path /api/wallets/**
                                       # VD: GET /api/wallets/123 → forward đến WALLET-SERVICE
          filters:
            - StripPrefix=1            # Xóa 1 segment đầu tiên của path trước khi forward
                                       # /api/wallets/123 → /wallets/123 (gửi đến downstream)
                                       # Vì downstream service không cần prefix /api
            - name: CircuitBreaker     # Circuit Breaker (Resilience4j): bảo vệ khi downstream chết
              args:
                name: walletCircuitBreaker  # Tên circuit breaker — dùng để config riêng
                fallbackUri: forward:/fallback/wallet
                # Khi circuit OPEN (downstream fail quá nhiều) → chuyển hướng đến /fallback/wallet
                # → Trả response mặc định thay vì lỗi 500 → UX tốt hơn
            - name: RequestRateLimiter  # Rate Limiter: giới hạn số request/giây (dùng Redis)
              args:
                redis-rate-limiter.replenishRate: 100   # 100 request/giây được cấp phép
                redis-rate-limiter.burstCapacity: 200   # Cho phép burst tối đa 200 request
                # Quá 200 → trả 429 Too Many Requests
                # Dùng Redis để đếm request — hoạt động đúng khi có nhiều Gateway instances
```

**Custom Gateway Filter (JWT validation tại Gateway):**
```java
// GlobalFilter: filter áp dụng cho TẤT CẢ routes trong Gateway (không cần gắn từng route)
// Ordered: cho phép set thứ tự ưu tiên khi có nhiều filter
// Khác với OncePerRequestFilter (Servlet): Gateway dùng WebFlux (reactive) → dùng Mono<Void>
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // ServerWebExchange: chứa cả request + response (tương đương HttpServletRequest + Response)
    // GatewayFilterChain: chuỗi filter tiếp theo — gọi chain.filter() để chuyển tiếp
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Bước 1: Kiểm tra endpoint có public không (VD: /api/auth/login)
        // → Nếu public → bỏ qua auth, cho request đi tiếp
        if (isPublicEndpoint(request.getPath())) {
            return chain.filter(exchange);
        }

        // Bước 2: Extract và validate JWT token
        String token = extractToken(request);
        if (!jwtValidator.isValid(token)) {
            // Token không hợp lệ → trả 401 Unauthorized ngay tại Gateway
            // → Request KHÔNG được forward đến downstream service
            // → Bảo vệ downstream khỏi request xấu
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();  // Kết thúc response ngay
        }

        // Bước 3: Propagate thông tin user đến downstream service qua custom headers
        // Tại sao: Downstream service không cần parse JWT lại
        // → Chỉ cần đọc header X-User-Id, X-User-Role → biết ai đang gọi
        // → Centralize JWT logic tại Gateway, downstream chỉ care authorization
        ServerHttpRequest mutatedRequest = request.mutate()
            .header("X-User-Id", jwtValidator.getUserId(token))    // Truyền userId
            .header("X-User-Role", jwtValidator.getRole(token))    // Truyền role
            .build();
        // request.mutate(): tạo bản copy của request với headers mới
        // (request trong WebFlux là immutable → phải mutate để thay đổi)

        // Forward request đã gắn headers đến downstream service
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() { return -1; }
    // Order = -1: chạy TRƯỚC tất cả filter khác (order mặc định = 0)
    // → Auth filter phải chạy đầu tiên: từ chối request sớm nhất có thể
    // → Giảm tải cho downstream: request xấu bị chặn ngay tại Gateway
}
```

### 4.2 Eureka — Client-Side Service Discovery

```
Service A → [Eureka Client] → query Eureka Server → lấy danh sách instances của Service B
         → [Ribbon/Spring Cloud LoadBalancer] → chọn instance → gọi Service B
```

```java
// Từ Spring Cloud 2020+: KHÔNG cần @EnableEurekaClient nữa
// Chỉ cần có dependency spring-cloud-starter-netflix-eureka-client → tự kích hoạt

// @FeignClient: Khai báo một HTTP client DECLARATIVE (chỉ khai báo interface, Spring tự tạo implementation)
// name = "WALLET-SERVICE": tên service đăng ký trên Eureka
// → Feign tự query Eureka → lấy danh sách instances → gọi HTTP + load balancing
// → KHÔNG cần hardcode URL (http://localhost:8081) → microservice có thể scale tự do
@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {
    // Khai báo giống Controller nhưng ở phía CLIENT
    // Spring tự generate implementation: gửi GET request đến WALLET-SERVICE/api/wallets/{id}
    // Response tự động deserialize thành WalletDTO (Jackson)
    @GetMapping("/api/wallets/{id}")
    WalletDTO getWallet(@PathVariable Long id);
    // Sử dụng: @Autowired WalletClient walletClient;
    //          WalletDTO wallet = walletClient.getWallet(123L);
    // → Feign gửi: GET http://<wallet-service-instance>/api/wallets/123
}
```

```yaml
# Eureka client config — cấu hình service đăng ký với Eureka Server
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
      # URL của Eureka Server — nơi service đăng ký và query danh sách services khác
      # Có thể config nhiều URL (HA): http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
  instance:
    prefer-ip-address: true
    # true: đăng ký bằng IP (VD: 192.168.1.10) thay vì hostname
    # → Quan trọng trong Docker/K8s: hostname container thường không resolve được từ bên ngoài
    lease-renewal-interval-in-seconds: 10
    # Heartbeat: mỗi 10 giây gửi 1 "tôi còn sống" đến Eureka Server
    # → Eureka biết service đang hoạt động
    lease-expiration-duration-in-seconds: 30
    # Nếu Eureka Server KHÔNG nhận được heartbeat trong 30 giây
    # → Coi service đã chết → loại khỏi registry
    # → Các service khác sẽ không gọi đến instance này nữa
    # ⚠️ Nên giữ tỉ lệ: expiration ≥ 3x renewal (30 ≥ 3×10) để tránh false positive
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
