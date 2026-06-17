# PART 5 — Spring Framework

---

## 🔷 IoC & Dependency Injection

### BEGINNER: What is IoC and DI?

**IoC (Inversion of Control):**
> Instead of your code creating its own dependencies, you *invert* the control — the framework creates and manages them for you.

**DI (Dependency Injection):**
> The mechanism IoC uses. The container *injects* dependencies into your class.

```java
// WITHOUT IoC (you control object creation)
class OrderService {
    private PaymentService paymentService = new StripePaymentService(); // hardcoded!
}

// WITH IoC/DI (Spring controls it)
@Service
class OrderService {
    private final PaymentService paymentService;

    @Autowired  // Spring injects the PaymentService bean
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

**Three types of DI:**
1. **Constructor Injection** ← Recommended (immutable, testable)
2. **Setter Injection** (optional dependencies)
3. **Field Injection** (`@Autowired` on field — avoid, hard to test)

---

### INTERMEDIATE: ApplicationContext vs BeanFactory

| | BeanFactory | ApplicationContext |
|---|------------|-------------------|
| Bean initialization | Lazy (on first request) | Eager (on startup) |
| Event publishing | No | Yes |
| AOP | Limited | Full support |
| MessageSource | No | Yes (i18n) |
| Use case | Memory-constrained | Enterprise apps (always use this) |

---

### SENIOR: How does Spring resolve ambiguous beans?

```java
@Component
class StripePaymentService implements PaymentService { ... }

@Component
class PayPalPaymentService implements PaymentService { ... }

// Problem: Spring doesn't know which to inject
@Autowired
PaymentService paymentService; // ERROR: NoUniqueBeanDefinitionException

// Solutions:
// 1. @Primary — mark one as default
@Primary
@Component
class StripePaymentService implements PaymentService { ... }

// 2. @Qualifier — specify by name
@Autowired
@Qualifier("stripePaymentService")
PaymentService paymentService;

// 3. @Profile — activate per environment
@Profile("production")
@Component
class StripePaymentService implements PaymentService { ... }
```

---

## 🔷 Bean Lifecycle

### BEGINNER: What are the phases of a Spring Bean lifecycle?

```
Container starts
    ↓
1. Instantiate (new BeanClass())
    ↓
2. Populate properties (@Autowired dependencies)
    ↓
3. BeanNameAware.setBeanName()
    ↓
4. BeanFactoryAware.setBeanFactory()
    ↓
5. ApplicationContextAware.setApplicationContext()
    ↓
6. @PostConstruct / InitializingBean.afterPropertiesSet()
    ↓
7. ← BEAN IS READY TO USE →
    ↓
8. @PreDestroy / DisposableBean.destroy()
    ↓
Container shuts down
```

```java
@Component
class CacheManager {
    @PostConstruct
    public void init() {
        System.out.println("Cache warmed up!");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Cache cleared!");
    }
}
```

---

## 🔷 Bean Scopes

### INTERMEDIATE: Explain all Spring Bean Scopes

| Scope | Description | Use Case |
|-------|-------------|----------|
| `singleton` | One instance per ApplicationContext (default) | Stateless services |
| `prototype` | New instance every time requested | Stateful beans |
| `request` | One per HTTP request (web only) | Request-scoped data |
| `session` | One per HTTP session (web only) | User session data |
| `application` | One per ServletContext | Global app config |

**Common Mistake:** Injecting prototype bean into singleton bean.
```java
// PROBLEM: prototype bean becomes effectively singleton
@Component // singleton
class MyService {
    @Autowired
    PrototypeBean prototypeBean; // always same instance!
}

// FIX: Use ApplicationContext.getBean() or @Lookup
@Component
class MyService {
    @Autowired
    ApplicationContext ctx;

    public void doWork() {
        PrototypeBean freshBean = ctx.getBean(PrototypeBean.class); // new each time
    }
}
```

---

## 🔷 AOP (Aspect-Oriented Programming)

### INTERMEDIATE: What is AOP and when do you use it?

**AOP separates cross-cutting concerns from business logic.**

Cross-cutting concerns: Logging, Security, Transactions, Caching, Metrics

```java
@Aspect
@Component
public class LoggingAspect {

    // Pointcut: apply to all methods in service package
    @Around("execution(* com.example.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();  // call the actual method

        long duration = System.currentTimeMillis() - start;
        log.info("{} executed in {}ms", joinPoint.getSignature(), duration);

        return result;
    }
}
```

**AOP Concepts:**
| Term | Meaning |
|------|---------|
| Aspect | Class containing cross-cutting logic |
| Advice | What to do (@Before, @After, @Around, @AfterReturning, @AfterThrowing) |
| Pointcut | WHERE to apply (expression matching methods) |
| Join Point | Specific execution point (method call) |
| Weaving | Process of applying aspects |

**How Spring AOP works:**
> Spring uses **JDK Dynamic Proxy** (for interface-based) or **CGLIB Proxy** (for class-based) to wrap beans and intercept method calls.

### SENIOR: What is the self-invocation problem in Spring AOP?

```java
@Service
public class OrderService {

    @Transactional
    public void createOrder() {
        // ...
        sendConfirmation();  // PROBLEM: calls internal method directly, bypasses proxy!
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendConfirmation() {
        // This @Transactional is IGNORED when called via createOrder()
    }
}

// Fix: inject self or use ApplicationContext.getBean()
```

---

## 🔷 Transaction Management

### INTERMEDIATE: Explain @Transactional and Propagation

**Transaction Propagation:**
| Type | Behavior |
|------|---------|
| `REQUIRED` (default) | Join existing or create new |
| `REQUIRES_NEW` | Always create new, suspend existing |
| `SUPPORTS` | Join if exists, run without if not |
| `NOT_SUPPORTED` | Suspend existing, run without transaction |
| `MANDATORY` | Must join existing; throw if none |
| `NEVER` | Must NOT have transaction; throw if one exists |
| `NESTED` | Nested within existing (savepoint) |

**Isolation Levels:**
| Level | Dirty Read | Non-repeatable Read | Phantom Read |
|-------|-----------|---------------------|-------------|
| READ_UNCOMMITTED | ✅ | ✅ | ✅ |
| READ_COMMITTED | ❌ | ✅ | ✅ |
| REPEATABLE_READ | ❌ | ❌ | ✅ |
| SERIALIZABLE | ❌ | ❌ | ❌ |

```java
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.READ_COMMITTED,
    readOnly = false,
    rollbackFor = Exception.class,
    timeout = 30
)
public void processPayment(PaymentRequest req) { ... }
```

### SENIOR: Why does @Transactional not work on private methods?

> Spring AOP creates a proxy around the bean. When you call a private method, the proxy is bypassed — the call goes directly to the actual object. Since `@Transactional` is applied via AOP proxy, private methods can't be intercepted.

---

## 🔷 Spring Security

### BEGINNER: How does Spring Security work?

```
Request → FilterChain → UsernamePasswordAuthenticationFilter
                              ↓
                    AuthenticationManager
                              ↓
                    UserDetailsService.loadUserByUsername()
                              ↓
                    BCryptPasswordEncoder.matches()
                              ↓
                    SecurityContext (stores Authentication)
                              ↓
                    JWT Token generated and returned
```

### INTERMEDIATE: JWT with Spring Security

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws... {
        String token = extractBearerToken(req);
        if (token != null && jwtService.isValid(token)) {
            UserDetails user = userDetailsService.loadUserByUsername(
                jwtService.extractUsername(token)
            );
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
```

### SENIOR: How did you implement security in your FPM project?

> "In FPM, I implemented defense-in-depth with dual-layer JWT validation. The API Gateway validates the token against Redis blacklist. Each downstream microservice re-validates using a shared `fpm-security` library. I also added row-level ownership checks to prevent horizontal privilege escalation — Mass Assignment attack protection. Rate limiting was implemented as Token Bucket (5 req/5min for login, 100 req/s standard)."
