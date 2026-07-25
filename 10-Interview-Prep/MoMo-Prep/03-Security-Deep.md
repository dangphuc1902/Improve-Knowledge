# 🔐 Security Deep Dive — RSA, Chữ Ký Số, Hashing, JWT

> **Mức độ**: Senior Engineer perspective  
> **Context**: Bảo vệ request trong hệ thống thanh toán MoMo

---

## PHẦN 1: Mã Hóa RSA — Nền tảng của Public Key Cryptography

### 1.1 Asymmetric vs Symmetric Encryption

```
SYMMETRIC (AES, DES):
  Dùng CÙNG 1 key để mã hóa VÀ giải mã
  
  Encrypt(key, plaintext) → ciphertext
  Decrypt(key, ciphertext) → plaintext
  
  ✅ Nhanh (10-100x so với asymmetric)
  ❌ Vấn đề key distribution: Làm sao share secret key an toàn?
  Use case: Mã hóa data at rest, TLS session data

ASYMMETRIC (RSA, ECDSA, Ed25519):
  Dùng 2 key liên quan toán học: Public Key + Private Key
  
  Encrypt(publicKey, plaintext) → ciphertext
  Decrypt(privateKey, ciphertext) → plaintext  ← Chỉ private key mới giải được!
  
  ✅ Key distribution dễ: Public key có thể share thoải mái
  ❌ Chậm hơn symmetric
  Use case: Key exchange, digital signatures, certificates

THỰC TẾ: TLS dùng HYBRID
  1. Asymmetric để exchange secret key (RSA/ECDH)
  2. Sau đó dùng symmetric (AES) cho data — Best of both worlds!
```

### 1.2 RSA — Math Cơ Bản (Không cần nhớ công thức, cần hiểu nguyên lý)

```
Nguyên lý RSA dựa trên:
Factorization problem: Dễ nhân 2 số nguyên tố lớn p×q = n
                       Nhưng RẤT KHÓ tìm p,q từ n!

2048-bit RSA key: n = p × q
n có ~617 chữ số thập phân
Không có máy tính nào có thể factor n trong thời gian hợp lý

Điều bạn cần nhớ:
- Private key: Chỉ bạn giữ, KHÔNG BAO GIỜ share
- Public key: Ai cũng có thể có, an toàn để distribute
- Toán học đảm bảo: Biết public key KHÔNG suy ra được private key
```

### 1.3 2 Use Cases của RSA

**Use Case 1: Encryption (Mã hóa)**
```
NGƯỜI GỬI (Merchant) muốn gửi secret data cho MoMo:

1. Lấy MoMo's PUBLIC key (public, ai cũng biết)
2. Encrypt(momoPublicKey, sensitiveData) → ciphertext
3. Gửi ciphertext qua mạng

MoMo server:
4. Decrypt(momoPrivateKey, ciphertext) → sensitiveData ✅

→ Chỉ MoMo (private key holder) đọc được
→ Attacker intercept: Chỉ thấy gibberish (ciphertext)

Trong thực tế: Dùng RSA để encrypt AES key,
sau đó dùng AES để encrypt actual data (Hybrid encryption)
```

**Use Case 2: Digital Signature (Chữ ký số)**
```
MERCHANT muốn CHỨNG MINH request từ họ (không bị giả mạo):

1. Merchant có: privateKey (bí mật) + publicKey (đã đăng ký với MoMo)
2. Tạo message M
3. Sign(merchantPrivateKey, hash(M)) → Signature S
4. Gửi: {M, S}

MoMo verify:
5. Verify(merchantPublicKey, hash(M), S) → TRUE/FALSE ✅

→ Chứng minh 3 điều:
   ✅ Authenticity: Chỉ merchant có private key mới tạo được S
   ✅ Integrity: Nếu M bị sửa → hash(M) khác → Verify fail
   ✅ Non-repudiation: Merchant không thể phủ nhận đã ký
```

### 1.4 Java RSA Implementation

```java
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import javax.crypto.*;

public class RsaExample {

    // ===== KEY GENERATION (chỉ làm 1 lần, lưu an toàn) =====
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // 2048-bit minimum, 4096 cho paranoid
        return generator.generateKeyPair();
    }

    // ===== ENCRYPTION =====
    public static byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        // OAEP padding = modern, secure. Đừng dùng PKCS1Padding (vulnerable)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

    // ===== DIGITAL SIGNATURE =====
    public static byte[] sign(PrivateKey privateKey, String data) throws Exception {
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(privateKey);
        signer.update(data.getBytes("UTF-8"));
        return signer.sign();
    }

    public static boolean verify(PublicKey publicKey, String data,
                                  byte[] signature) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(data.getBytes("UTF-8"));
        return verifier.verify(signature);
    }

    // ===== SERIALIZE KEY TO STRING (để lưu vào config/DB) =====
    public static String publicKeyToString(PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey stringToPublicKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // ===== DEMO =====
    public static void main(String[] args) throws Exception {
        // 1. Generate keys
        KeyPair pair = generateKeyPair();
        System.out.println("Public Key: " + publicKeyToString(pair.getPublic()));

        // 2. Sign payment request
        String paymentRequest = "amount=100000&orderId=ORD123&timestamp=1706000000";
        byte[] signature = sign(pair.getPrivate(), paymentRequest);
        String signatureB64 = Base64.getEncoder().encodeToString(signature);
        System.out.println("Signature: " + signatureB64);

        // 3. Verify
        boolean valid = verify(pair.getPublic(), paymentRequest, signature);
        System.out.println("Valid: " + valid); // true

        // 4. Tamper with request
        String tamperedRequest = "amount=1&orderId=ORD123&timestamp=1706000000";
        boolean tamperedValid = verify(pair.getPublic(), tamperedRequest, signature);
        System.out.println("Tampered valid: " + tamperedValid); // false ✅
    }
}
```

---

## PHẦN 2: Chữ Ký Số (Digital Signature) — Payment Context

### 2.1 Flow Ký và Verify Request

```
MERCHANT SIDE (Signing):
━━━━━━━━━━━━━━━━━━━━━━
1. Tạo request parameters:
   amount=100000
   orderId=ORD-ABC-123
   merchantId=MERCHANT001
   timestamp=1706000000   ← Unix timestamp (chống replay attack)
   nonce=random-uuid      ← Random value (thêm chống replay)

2. Canonicalize (chuẩn hóa):
   Sort keys alphabetically:
   "amount=100000&merchantId=MERCHANT001&nonce=xyz&orderId=ORD-ABC-123&timestamp=1706000000"

3. Sign:
   signature = RSA_SHA256_Sign(merchantPrivateKey, canonicalized_string)
   
4. Gửi request:
   {
     "amount": 100000,
     "orderId": "ORD-ABC-123",
     "merchantId": "MERCHANT001",
     "timestamp": 1706000000,
     "nonce": "xyz",
     "signature": "base64_encoded_signature"
   }

MOMO SERVER SIDE (Verifying):
━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Kiểm tra timestamp: |now - timestamp| < 5 phút → Chống replay attack
2. Kiểm tra nonce: Nonce chưa dùng → Chống replay (lưu nonce đã dùng trong Redis)
3. Reconstruct canonical string từ request parameters
4. Verify signature với merchant's public key
5. Nếu valid → process; nếu không → reject 400
```

### 2.2 Tại sao cần cả Timestamp VÀ Nonce?

```
Chỉ timestamp:
- Attacker capture request lúc 12:00:00
- Replay lúc 12:03:00 → Vẫn trong 5 phút window → PASS!
- Attacker replay 100 lần trong 5 phút → 100 giao dịch!

Chỉ nonce:
- Nonce random UUID, không hết hạn
- Redis phải lưu MỌI nonce mãi mãi → Memory explosion

Cả hai:
- Timestamp: Giới hạn window 5 phút
- Nonce: Trong 5 phút, mỗi nonce chỉ dùng 1 lần
- Redis chỉ cần lưu nonces trong 5 phút → TTL = 5 phút

→ Perfect! Không thể replay, không tốn memory
```

### 2.3 Implementation trong Spring

```java
@Service
public class SignatureService {

    private final RedisTemplate<String, String> redis;
    private final MerchantKeyRepository merchantKeyRepo;

    // VERIFY incoming request từ merchant
    public void verifyMerchantRequest(PaymentRequest request) {
        // 1. Timestamp check
        long now = System.currentTimeMillis() / 1000;
        long diff = Math.abs(now - request.getTimestamp());
        if (diff > 300) { // 5 minutes
            throw new SignatureException("Request timestamp expired");
        }

        // 2. Nonce check (replay attack prevention)
        String nonceKey = "nonce:" + request.getMerchantId() + ":" + request.getNonce();
        Boolean nonceIsNew = redis.opsForValue()
            .setIfAbsent(nonceKey, "1", Duration.ofMinutes(6));
        if (Boolean.FALSE.equals(nonceIsNew)) {
            throw new SignatureException("Duplicate request detected");
        }

        // 3. Reconstruct canonical string
        String canonical = buildCanonicalString(request);

        // 4. Get merchant public key
        String publicKeyStr = merchantKeyRepo.getPublicKey(request.getMerchantId());
        PublicKey publicKey = KeyUtils.stringToPublicKey(publicKeyStr);

        // 5. Verify signature
        boolean valid = KeyUtils.verify(publicKey, canonical, 
            Base64.getDecoder().decode(request.getSignature()));
        
        if (!valid) {
            throw new SignatureException("Invalid signature");
        }
    }

    private String buildCanonicalString(PaymentRequest request) {
        // Sort parameters alphabetically
        TreeMap<String, String> params = new TreeMap<>();
        params.put("amount", request.getAmount().toString());
        params.put("merchantId", request.getMerchantId());
        params.put("nonce", request.getNonce());
        params.put("orderId", request.getOrderId());
        params.put("timestamp", request.getTimestamp().toString());

        return params.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
    }
}
```

---

## PHẦN 3: Hashing & Checksum

### 3.1 Hash Functions — Bảng so sánh

```
┌──────────────┬──────────┬───────────┬─────────────────────────────────────┐
│ Function     │ Output   │ Speed     │ Use Case                            │
├──────────────┼──────────┼───────────┼─────────────────────────────────────┤
│ MD5          │ 128 bit  │ Rất nhanh │ File checksum (KHÔNG dùng security) │
│ SHA-1        │ 160 bit  │ Nhanh     │ Deprecated, bị break                │
│ SHA-256      │ 256 bit  │ Nhanh     │ Digital signature, integrity check  │
│ SHA-3        │ Variable │ Nhanh     │ Modern alternative SHA-256          │
│ HMAC-SHA256  │ 256 bit  │ Nhanh     │ Message authentication (shared key) │
│ bcrypt       │ 60 char  │ Chậm (!)  │ Password hashing                   │
│ Argon2       │ Variable │ Rất chậm  │ Password hashing (modern, PHC win) │
│ scrypt       │ Variable │ Chậm      │ Password hashing (memory-hard)      │
└──────────────┴──────────┴───────────┴─────────────────────────────────────┘
```

### 3.2 Tại sao cần bcrypt cho Password? (Không thể dùng SHA-256)

```
Nếu dùng SHA-256 cho password:
  hash = SHA256("password123") → "ef92b778..." (luôn giống nhau!)

Vấn đề 1: Rainbow Table Attack
  Attacker precompute: SHA256("password") = "5e88489..."
                       SHA256("123456")   = "8d969eef..."
                       SHA256("password123") = "ef92b778..."
  Lấy DB → lookup hash → biết password ngay!

Vấn đề 2: GPU Brute Force
  RTX 4090 có thể compute 20 TỶ SHA256 hashes/giây
  → Dictionary attack 1 triệu passwords trong 0.05ms!

bcrypt giải quyết:
1. Salt: Random bytes thêm vào password trước khi hash
   SHA256("password123" + "random_salt_xyz") → khác mỗi lần
   → Rainbow table useless (phải compute per-user)

2. Cost factor: Số rounds (default 10 = 2^10 = 1024 iterations)
   → Intentionally SLOW: ~100ms per hash
   → GPU: 20B SHA256/s → chỉ ~20 bcrypt/s với cost=10
   → Dictionary attack 1M passwords = 14 giờ!

3. Adaptive: Tăng cost khi hardware mạnh hơn

// Spring Security:
PasswordEncoder encoder = new BCryptPasswordEncoder(12); // cost=12 ~250ms
String hash = encoder.encode("password123");
// Hash format: $2a$12$SaltSaltSaltSaltSal.HashHashHashHashHashHashHashHashHashH

boolean match = encoder.matches("password123", hash); // true
boolean wrong = encoder.matches("wrongpassword", hash); // false
```

### 3.3 HMAC — Message Authentication Code

```
HMAC = Hash-based Message Authentication Code
Dùng SHARED SECRET để tạo và verify message integrity

Khác với Digital Signature:
- HMAC: Symmetric (cả 2 bên biết secret)
- Digital Sig: Asymmetric (private key ký, public key verify)

HMAC dùng khi:
- Bạn TRUST bên kia (internal API, webhook)
- Không cần non-repudiation
- Cần nhanh hơn RSA

HMAC-SHA256 trong Java:
```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtil {

    public static String computeHmac(String secretKey, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
            secretKey.getBytes("UTF-8"), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    // Constant-time comparison (chống timing attack!)
    public static boolean verifyHmac(String expected, String actual) {
        return MessageDigest.isEqual(
            expected.getBytes(),
            actual.getBytes()
        );
        // KHÔNG dùng expected.equals(actual)!
        // String.equals() dừng khi gặp ký tự khác nhau đầu tiên
        // → Attacker đo response time → biết prefix của HMAC đúng!
    }
}
```

```
Webhook Security với HMAC (ví dụ: Bank gọi callback MoMo):

Bank → POST /callback
{
  "transactionId": "TXN123",
  "status": "SUCCESS",
  "amount": 100000
}
Header: X-Signature: hmac_sha256(sharedSecret, body)

MoMo server:
1. Lấy raw body (TRƯỚC KHI parse JSON!)
2. Compute HMAC với shared secret
3. So sánh với header X-Signature
4. Nếu match → process; nếu không → reject 401

Quan trọng: Parse JSON SAU KHI verify HMAC
Vì attacker có thể gửi: {"amount": 1} với signature của {"amount": 100000}
```

---

## PHẦN 4: JWT — Authentication & Authorization

### 4.1 JWT Structure Deep Dive

```
JWT = Header.Payload.Signature
       (base64).(base64).(signature)

HEADER (Algorithm + Type):
{
  "alg": "RS256",  // RSA SHA-256 (asymmetric, production-grade)
  "typ": "JWT"
}
→ Avoid "none" algorithm (security vulnerability!)
→ HS256 dùng cho internal (shared secret)
→ RS256 dùng cho public auth (private key sign, public key verify)

PAYLOAD (Claims - KHÔNG MÃ HÓA, chỉ base64):
{
  "sub": "user-123",              // Subject (user ID)
  "iss": "https://auth.momo.vn", // Issuer
  "aud": "payment-service",       // Audience
  "iat": 1706000000,              // Issued At
  "exp": 1706003600,              // Expires At (1 hour later)
  "jti": "unique-jwt-id",         // JWT ID (để blacklist khi logout)
  "roles": ["USER", "PREMIUM"],   // Custom claims
  "email": "phuc@example.com"
}

⚠️ QUAN TRỌNG: Payload chỉ là Base64, KHÔNG MÃ HÓA!
→ Ai cũng decode được → KHÔNG bao giờ để sensitive data (password, card number) trong JWT!

SIGNATURE:
RS256: RSA_SHA256_Sign(privateKey, base64(header) + "." + base64(payload))
HS256: HMAC_SHA256(secret, base64(header) + "." + base64(payload))
```

### 4.2 JWT Authentication Flow

```java
// AuthService.java
@Service
public class AuthService {

    private final RSAPrivateKey privateKey;   // Ký token
    private final UserRepository userRepo;
    private final RefreshTokenRepository rtRepo;
    private final RedisTemplate<String, String> redis;

    public AuthResponse login(String username, String password) {
        // 1. Verify credentials
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // 2. Generate Access Token (short-lived: 15 min)
        String accessToken = Jwts.builder()
            .subject(user.getId().toString())
            .issuer("https://auth.momo.vn")
            .audience().add("momo-services").and()
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plusSeconds(900))) // 15 min
            .id(UUID.randomUUID().toString()) // jti
            .claim("roles", user.getRoles())
            .claim("email", user.getEmail())
            .signWith(privateKey) // RS256
            .compact();

        // 3. Generate Refresh Token (long-lived: 7 days, opaque)
        String refreshToken = UUID.randomUUID().toString();
        rtRepo.save(RefreshToken.builder()
            .token(hashToken(refreshToken)) // Store hashed!
            .userId(user.getId())
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(900)
            .build();
    }

    // REFRESH TOKEN ROTATION
    public AuthResponse refresh(String refreshToken) {
        String hashedToken = hashToken(refreshToken);

        RefreshToken rt = rtRepo.findByToken(hashedToken)
            .orElseThrow(() -> {
                // Token không tìm thấy → Có thể bị stolen và đã rotate
                // → Revoke ALL tokens của user (family invalidation)
                log.warn("Refresh token reuse detected!");
                return new UnauthorizedException("Invalid refresh token");
            });

        if (rt.isExpired() || rt.isRevoked()) {
            throw new UnauthorizedException("Refresh token expired/revoked");
        }

        // Rotate: Invalidate old token
        rt.setRevoked(true);
        rtRepo.save(rt);

        // Issue new token pair
        User user = userRepo.findById(rt.getUserId()).orElseThrow();
        return login(user); // Tạo mới cả access + refresh token
    }

    public void logout(String jti, long expiresAt) {
        // Blacklist JWT ID trong Redis until token expires
        long ttl = expiresAt - System.currentTimeMillis() / 1000;
        if (ttl > 0) {
            redis.opsForValue().set(
                "blacklist:jti:" + jti,
                "1",
                Duration.ofSeconds(ttl)
            );
        }
    }
}
```

### 4.3 JWT Validation Filter

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final RSAPublicKey publicKey; // Verify token
    private final RedisTemplate<String, String> redis;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws Exception {
        String token = extractToken(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 1. Parse và verify signature
            Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer("https://auth.momo.vn")
                .build()
                .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();

            // 2. Check blacklist (logout case)
            String jti = claims.getId();
            if (redis.hasKey("blacklist:jti:" + jti)) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token revoked");
                return;
            }

            // 3. Set Authentication context
            List<String> roles = claims.get("roles", List.class);
            List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(toList());

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), // Principal = userId
                    null,
                    authorities
                );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token expired");
            return;
        } catch (JwtException e) {
            response.sendError(401, "Invalid token");
            return;
        }

        chain.doFilter(request, response);
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

### 4.4 Authorization — RBAC vs ABAC

**RBAC (Role-Based Access Control):**
```java
// Simple: User có roles, roles có permissions
// User → [ROLE_USER, ROLE_PREMIUM] → Allowed endpoints

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/transactions")
                .hasAnyRole("USER", "PREMIUM", "BUSINESS")
            .requestMatchers("/api/v1/analytics/**").hasRole("ANALYTICS")
            .anyRequest().authenticated()
        );
        return http.build();
    }
}

// Method-level security:
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) { ... }

@PreAuthorize("hasRole('USER') and #userId == authentication.principal")
public List<Transaction> getTransactions(Long userId) { ... }
// → User chỉ xem được transaction của CHÍNH HỌ
```

**ABAC (Attribute-Based Access Control):**
```java
// Phức tạp hơn: Quyền dựa trên attributes của user, resource, environment

// Ví dụ: User chỉ approve transaction nếu:
// - Role = APPROVER
// - AND transaction.department == user.department
// AND transaction.amount <= user.approvalLimit
// AND business hours (9am-5pm)

@Service
public class TransactionAuthorizationService {

    public boolean canApprove(User user, Transaction transaction) {
        // Role check
        if (!user.hasRole("APPROVER")) return false;

        // Department match
        if (!user.getDepartment().equals(transaction.getDepartment())) return false;

        // Approval limit
        if (transaction.getAmount().compareTo(user.getApprovalLimit()) > 0) return false;

        // Business hours (VN timezone)
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        if (now.isBefore(LocalTime.of(9, 0)) || now.isAfter(LocalTime.of(17, 0))) {
            return false;
        }

        return true;
    }
}

@PreAuthorize("@transactionAuthorizationService.canApprove(authentication.principal, #transaction)")
public void approveTransaction(Transaction transaction) { ... }
```

---

## PHẦN 5: Security Best Practices — Tổng hợp

### 5.1 Request Signing Full Flow (MoMo-style)

```java
// Payment Request từ Merchant:
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestHeader("X-Merchant-Id") String merchantId,
            @RequestHeader("X-Timestamp") long timestamp,
            @RequestHeader("X-Nonce") String nonce,
            @RequestHeader("X-Signature") String signature,
            @RequestBody String rawBody) { // Raw body để verify HMAC

        // 1. Verify request authenticity
        signatureService.verify(merchantId, timestamp, nonce, rawBody, signature);

        // 2. Parse body AFTER verification
        PaymentRequest request = objectMapper.readValue(rawBody, PaymentRequest.class);

        // 3. Additional validation
        // 4. Process payment
        ...
    }
}
```

### 5.2 Common Security Mistakes

```
❌ MISTAKE 1: String comparison cho secret (timing attack)
if (providedSignature.equals(expectedSignature)) // VULNERABLE
→ Fix: MessageDigest.isEqual(providedSignature.getBytes(), 
                             expectedSignature.getBytes())

❌ MISTAKE 2: Log sensitive data
log.info("Payment request: {}", request.toString()); // Logs card number, etc.
→ Fix: Mask sensitive fields trong toString()

❌ MISTAKE 3: JWT alg=none vulnerability
// Attacker có thể forge token với alg=none nếu không explicitly check
→ Fix: Whitelist algorithms: parser().requireAlgorithm(...)

❌ MISTAKE 4: Weak JWT secret
secretKey = "secret" // Guessable!
→ Fix: Minimum 256-bit random key, hoặc RS256 asymmetric

❌ MISTAKE 5: Not checking JWT audience/issuer
// Nếu nhiều service cùng trust 1 auth server
// Token cho service A có thể dùng cho service B!
→ Fix: .requireIssuer(...).requireAudience(...)

❌ MISTAKE 6: Store plaintext refresh token
refreshTokenRepo.save(RefreshToken(token=refreshToken)) // Plaintext!
// → DB breach → Attacker dùng tất cả refresh tokens
→ Fix: Store SHA-256 hash: SHA256(refreshToken)
```

---

## 🎯 TỔNG KẾT — Quick Reference

```
Mã hóa 2 chiều (cần giải mã):
→ Symmetric (AES): Data at rest, fast
→ Asymmetric RSA: Key exchange, OAEP padding

Chứng minh identity & integrity:
→ Digital Signature (RSA SHA-256): Non-repudiation, external
→ HMAC-SHA256: Fast, symmetric, internal API

Mã hóa 1 chiều (không giải mã):
→ bcrypt/Argon2: Password hashing (slow by design)
→ SHA-256: Data integrity, file checksum
→ HMAC: Message authentication với shared secret

Authentication & Authorization:
→ JWT RS256: Stateless, distributed, scalable
→ Refresh Token: Thêm vào Redis blacklist khi logout
→ RBAC: Role-based, simple
→ ABAC: Attribute-based, flexible
```

> 💡 **Senior Security Insight**: Security là layers (defense in depth). Không chỉ JWT, mà còn:  
> HTTPS (TLS) → API Gateway auth → Service-level auth → Database encryption → Audit log.  
> Nếu 1 layer fail, các layer khác vẫn bảo vệ được.
