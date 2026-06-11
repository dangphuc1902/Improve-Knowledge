# 🔒 Spring Security Deep Dive

> **Phase:** 1-2 | **Time Block:** 22:00-22:30 weekday + T7 11:00-12:00  
> **Quan trọng cho:** VNPay, NAB, MoMo, TymeX — Tất cả Tier 1

---

## 1. Kiến Trúc Spring Security

### Filter Chain Architecture
```
HTTP Request
    ↓
┌─────────────────────────────────────────────────────────┐
│                  FilterChain (Servlet)                   │
│                                                         │
│  ┌─────────────────────────────────────────────┐        │
│  │     DelegatingFilterProxy                    │        │
│  │  ┌───────────────────────────────────────┐  │        │
│  │  │   FilterChainProxy                    │  │        │
│  │  │                                       │  │        │
│  │  │  SecurityFilterChain #1 (/api/**)     │  │        │
│  │  │   ├── CorsFilter                      │  │        │
│  │  │   ├── CsrfFilter                      │  │        │
│  │  │   ├── UsernamePasswordAuthFilter      │  │        │
│  │  │   ├── BearerTokenAuthFilter (JWT)     │  │        │
│  │  │   ├── AuthorizationFilter             │  │        │
│  │  │   └── ExceptionTranslationFilter      │  │        │
│  │  │                                       │  │        │
│  │  │  SecurityFilterChain #2 (/public/**)  │  │        │
│  │  │   └── (minimal filters)               │  │        │
│  │  └───────────────────────────────────────┘  │        │
│  └─────────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────┘
    ↓
DispatcherServlet → Controller
```

### Core Components
| Component | Vai trò |
|:----------|:--------|
| `SecurityFilterChain` | Chuỗi filter xử lý security cho URL patterns |
| `AuthenticationManager` | Quản lý xác thực — delegate cho AuthenticationProvider |
| `AuthenticationProvider` | Thực hiện xác thực (DB, LDAP, OAuth) |
| `UserDetailsService` | Load user từ DB |
| `SecurityContext` | Lưu trữ Authentication object (ThreadLocal) |
| `GrantedAuthority` | Đại diện quyền/role của user |

---

## 2. JWT Authentication Flow

### Flow hoàn chỉnh
```
┌─────────┐     POST /auth/login      ┌──────────────┐
│  Client  │ ──────────────────────── → │ AuthController│
│          │     {email, password}      │              │
└─────────┘                             └──────┬───────┘
                                               │
                                    AuthenticationManager
                                               │
                                    UserDetailsService.loadByUsername()
                                               │
                                    PasswordEncoder.matches()
                                               │
                                    JwtTokenProvider.generateToken()
                                               │
                                    ◄── Response: {accessToken, refreshToken}
                                    
┌─────────┐    GET /api/resource       ┌──────────────┐
│  Client  │ ──────────────────────── → │ JwtAuthFilter │
│          │  Authorization: Bearer xxx │              │
└─────────┘                             └──────┬───────┘
                                               │
                                    JwtTokenProvider.validateToken()
                                               │
                                    UserDetailsService.loadByUsername()
                                               │
                                    SecurityContextHolder.setAuthentication()
                                               │
                                    ──→ Controller (authorized)
```

### Implementation Code

```java
// 1. Security Config (Spring Boot 3.x)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // for @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())  // Disable cho REST API
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(UNAUTHORIZED))
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            )
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

```java
// 2. JWT Auth Filter
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws Exception {
        String token = extractToken(request);

        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getUsernameFromToken(token);
            
            // Check Redis blacklist (logout)
            if (!tokenBlacklistService.isBlacklisted(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource()
                    .buildDetails(request));
                    
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

```java
// 3. JWT Token Provider
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration:900000}")  // 15 min
    private long accessExpiration;

    @Value("${jwt.refresh-expiration:604800000}")  // 7 days
    private long refreshExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## 3. RBAC vs ABAC

### Role-Based Access Control (RBAC)
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public List<User> getAllUsers() { ... }

// SpEL expression
@PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
public User getUser(Long userId) { ... }
```

### Attribute-Based Access Control (ABAC)
```java
// Row-level ownership check (Defense-in-depth)
@PreAuthorize("@securityService.isOwner(#walletId, authentication.principal.id)")
public Wallet getWallet(Long walletId) { ... }

@Service
public class SecurityService {
    public boolean isOwner(Long walletId, Long userId) {
        return walletRepository.findById(walletId)
            .map(w -> w.getUserId().equals(userId))
            .orElse(false);
    }
}
```

> **Relate FPM Project:** Dual-layer JWT security — Gateway validates token + Redis blacklist; downstream services re-validate via shared `fpm-security` library with row-level ownership checks (defense-in-depth).

---

## 4. OAuth 2.0 Flow

```
┌────────┐                              ┌──────────────┐
│  User   │──(1) Login with Google──────→│  Client App  │
└────────┘                              └──────┬───────┘
                                               │(2) Redirect
                                        ┌──────▼───────┐
                                        │ Google Auth   │
                                        │ Server        │
                                        └──────┬───────┘
                                               │(3) Auth Code
                                        ┌──────▼───────┐
                                        │  Client App   │
                                        │(4) Exchange   │
                                        │ code → token  │
                                        └──────┬───────┘
                                               │(5) Access Token
                                        ┌──────▼───────┐
                                        │ Google API    │
                                        │ (user info)   │
                                        └──────────────┘
```

### Grant Types
| Type | Use Case | Security |
|:-----|:---------|:---------|
| Authorization Code | Web apps (server-side) | ✅ Most secure |
| Authorization Code + PKCE | SPAs, mobile apps | ✅ Recommended |
| Client Credentials | Service-to-service | ✅ No user involved |
| ~~Implicit~~ | ~~SPAs (deprecated)~~ | ❌ Deprecated |
| ~~Password~~ | ~~Legacy apps~~ | ❌ Deprecated |

---

## 5. Common Security Vulnerabilities & Prevention

| Attack | Prevention |
|:-------|:-----------|
| **SQL Injection** | Parameterized queries (JPA/PreparedStatement) |
| **XSS** | Input sanitization, CSP headers, encode output |
| **CSRF** | CSRF tokens (for session-based auth), SameSite cookies |
| **JWT Token Theft** | Short expiration, HttpOnly cookies, refresh rotation |
| **Mass Assignment** | DTOs, @JsonIgnore, whitelist fields |
| **Broken Auth** | Rate limiting, account lockout, 2FA |
| **IDOR** | Row-level ownership checks (ABAC) |

---

## Câu Hỏi Phỏng Vấn

### Q1: Spring Security Filter Chain hoạt động như thế nào?
**A:** Spring Security sử dụng `DelegatingFilterProxy` → `FilterChainProxy` → multiple `SecurityFilterChain`. Mỗi request đi qua chuỗi filters theo thứ tự: CORS → CSRF → Authentication → Authorization → Exception Handling. Mỗi filter quyết định pass tiếp hay reject request.

### Q2: JWT vs Session-based auth — khi nào dùng cái nào?
**A:** JWT cho stateless microservices (horizontal scaling dễ, không cần shared session store). Session cho monolith hoặc khi cần revoke ngay lập tức (JWT cần blacklist). JWT trade-off: token size lớn hơn, không revoke được nếu không có blacklist.

### Q3: Làm sao invalidate JWT khi user logout?
**A:** JWT là stateless nên không thể "xóa" token. Solutions: Redis blacklist (check mỗi request), short-lived access token + refresh token rotation, hoặc database token versioning.

### Q4: RBAC vs ABAC — sự khác biệt và khi nào dùng?
**A:** RBAC: quyền dựa trên role (ADMIN, USER) — đơn giản, phù hợp hầu hết apps. ABAC: quyền dựa trên attributes (owner, department, time) — linh hoạt hơn, phù hợp multi-tenant, complex authorization. Thực tế hay combine cả 2.

### Q5: Giải thích defense-in-depth trong microservices security?
**A:** Layer 1: API Gateway validates JWT + rate limiting. Layer 2: Service-level re-validates token (shared security library). Layer 3: Row-level ownership check (ABAC). Nếu 1 layer bị bypass, layers khác vẫn bảo vệ. → **Đây chính là approach trong FPM project.**
