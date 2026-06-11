# 🧪 Spring Boot Testing

> **Phase:** 2 | **Quan trọng cho:** ALL Tier 1 companies — Testing culture là tiêu chí đánh giá

---

## 1. Testing Pyramid

```
          /\
         /  \    E2E Tests (ít nhất)
        /    \   Selenium, Playwright
       /──────\
      /        \  Integration Tests
     /          \ @SpringBootTest, TestContainers
    /────────────\
   /              \ Unit Tests (nhiều nhất)
  /                \ JUnit 5, Mockito
 /──────────────────\
```

---

## 2. Unit Tests — JUnit 5 + Mockito

```java
@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    
    @Mock
    private TransactionService transactionService;
    
    @InjectMocks
    private WalletService walletService;

    @Test
    @DisplayName("Should deduct balance when sufficient funds")
    void deductBalance_sufficientFunds_success() {
        // Given (Arrange)
        Wallet wallet = Wallet.builder()
            .id(1L).userId(100L).balance(BigDecimal.valueOf(500)).build();
        when(walletRepository.findByUserId(100L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // When (Act)
        walletService.deduct(100L, BigDecimal.valueOf(200));

        // Then (Assert)
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(300));
        verify(walletRepository).save(wallet);
        verify(transactionService).recordTransaction(any());
    }

    @Test
    @DisplayName("Should throw when insufficient funds")
    void deductBalance_insufficientFunds_throwsException() {
        Wallet wallet = Wallet.builder()
            .id(1L).userId(100L).balance(BigDecimal.valueOf(50)).build();
        when(walletRepository.findByUserId(100L)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> walletService.deduct(100L, BigDecimal.valueOf(200)))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessageContaining("Insufficient");
        
        verify(walletRepository, never()).save(any());
    }
}
```

---

## 3. Integration Tests — @SpringBootTest

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_validRequest_returns201() throws Exception {
        CreateUserRequest request = new CreateUserRequest("phuc@test.com", "password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("phuc@test.com"))
            .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found"));
    }
}
```

---

## 4. Database Tests — @DataJpaTest

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_existingUser_returnsWallet() {
        Wallet wallet = Wallet.builder()
            .userId(100L).balance(BigDecimal.valueOf(500)).build();
        entityManager.persistAndFlush(wallet);

        Optional<Wallet> found = walletRepository.findByUserId(100L);

        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }
}
```

---

## 5. TestContainers — Real DB Testing

```java
@SpringBootTest
@Testcontainers
class TransactionServiceIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private TransactionService transactionService;

    @Test
    void processTransaction_fullFlow_success() {
        // Tests with real MySQL + Redis containers
        TransactionResult result = transactionService.process(
            new TransactionRequest(1L, 2L, BigDecimal.valueOf(100)));
        
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }
}
```

---

## 6. Testing Best Practices

| Practice | Lý do |
|:---------|:------|
| Test name = behavior description | `should_returnEmpty_when_userNotFound` |
| AAA pattern (Arrange-Act-Assert) | Code dễ đọc, dễ maintain |
| 1 assert per test (lý tưởng) | Dễ debug khi fail |
| Don't test private methods | Test behavior, not implementation |
| Use `@Nested` for grouping | Organize related tests |
| Mock external deps, not internal | Avoid brittle tests |
| Test edge cases | Null, empty, boundary values |

---

## Câu Hỏi Phỏng Vấn

### Q1: Unit test vs Integration test — khi nào dùng?
**A:** Unit test: test 1 class/method isolated, mock dependencies, fast (ms). Integration test: test nhiều components together, real DB/cache, slow (seconds). Tỷ lệ: 70% unit, 20% integration, 10% E2E.

### Q2: @MockBean vs @Mock — khác nhau thế nào?
**A:** `@Mock` (Mockito) — tạo mock object thuần, không liên quan Spring context. `@MockBean` (Spring) — thay thế bean trong Spring ApplicationContext bằng mock. @MockBean chậm hơn (reload context), dùng cho integration tests.

### Q3: TestContainers là gì, tại sao dùng?
**A:** Library chạy real Docker containers (MySQL, Redis, Kafka) trong test. Tại sao: in-memory DB (H2) behavior khác production DB. TestContainers đảm bảo test chạy trên exact same DB engine, phát hiện bugs sớm hơn.
