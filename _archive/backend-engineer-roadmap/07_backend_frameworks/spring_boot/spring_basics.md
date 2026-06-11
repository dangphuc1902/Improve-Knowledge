# Spring Boot Fundamentals
# Nguyên tắc cơ bản của Spring Boot

## Concept Explanation
## Giải thích khái niệm
Spring Boot is an extension of the Spring framework, aimed at simplifying the setup and development of Spring applications. It takes an "opinionated" view of the Spring platform, providing pre-configured defaults so you can "just run" your application without complex XML or boilerplate configurations.
Spring Boot là một phần mở rộng của khung Spring, nhằm mục đích đơn giản hóa việc thiết lập và phát triển các ứng dụng Spring. Nó có một cái nhìn "có chính kiến" về nền tảng Spring, cung cấp các mặc định được định cấu hình sẵn để bạn có thể "chỉ cần chạy" ứng dụng của mình mà không cần các cấu hình XML hoặc bản soạn sẵn phức tạp.

### Key Concepts
### Các khái niệm chính
1. **Inversion of Control (IoC) Container & Dependency Injection (DI)**: The core of Spring. Instead of objects creating their dependencies (`new UserService()`), the Spring Framework creates them and injects them where needed (`@Autowired`).
1. **Vùng chứa đảo ngược điều khiển (IoC) và Tiêm phụ thuộc (DI)**: Cốt lõi của Spring. Thay vì các đối tượng tạo ra các phụ thuộc của chúng (`new UserService()`), Khung Spring tạo ra chúng và tiêm chúng vào những nơi cần thiết (`@Autowired`).
2. **Auto-Configuration**: Spring Boot analyzes your classpath. If it sees `spring-webmvc` on the classpath, it automatically configures a Tomcat server and DispatcherServlet.
2. **Tự động cấu hình**: Spring Boot phân tích đường dẫn lớp của bạn. Nếu nó thấy `spring-webmvc` trên đường dẫn lớp, nó sẽ tự động định cấu hình một máy chủ Tomcat và DispatcherServlet.
3. **Beans**: The objects that form the backbone of your application and that are managed by the Spring IoC container.
3. **Beans**: Các đối tượng tạo thành xương sống của ứng dụng của bạn và được quản lý bởi vùng chứa IoC của Spring.
4. **Annotations**: Used vastly to configure behavior concisely.
4. **Chú thích**: Được sử dụng rộng rãi để định cấu hình hành vi một cách ngắn gọn.

### Important Annotations
### Các chú thích quan trọng
- `@SpringBootApplication`: A convenience annotation that adds `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- `@SpringBootApplication`: Một chú thích tiện lợi bổ sung `@Configuration`, `@EnableAutoConfiguration` và `@ComponentScan`.
- `@Component`: Marks a Java class as a bean so the component-scanning mechanism can add it to the application context.
- `@Component`: Đánh dấu một lớp Java là một bean để cơ chế quét thành phần có thể thêm nó vào ngữ cảnh ứng dụng.
- `@Service`: A specialization of `@Component` used for business logic.
- `@Service`: Một chuyên môn hóa của `@Component` được sử dụng cho logic nghiệp vụ.
- `@Repository`: A specialization of `@Component` used for data access objects (DAOs).
- `@Repository`: Một chuyên môn hóa của `@Component` được sử dụng cho các đối tượng truy cập dữ liệu (DAO).
- `@RestController`: A convenience annotation that combines `@Controller` and `@ResponseBody`.
- `@RestController`: Một chú thích tiện lợi kết hợp `@Controller` và `@ResponseBody`.

## Practical Example
## Ví dụ thực tế

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 1. The main application class
// 1. Lớp ứng dụng chính
@SpringBootApplication 
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// 2. A Service class containing business logic
// 2. Một lớp Dịch vụ chứa logic nghiệp vụ
@Service
class GreetingService {
    public String generateGreeting() {
        return "Hello from Spring Boot Service!";
    }
}

// 3. A REST Controller that relies on the Service
// 3. Một Bộ điều khiển REST dựa vào Dịch vụ
@RestController
class GreetingController {

    private final GreetingService greetingService;

    // Dependency Injection via Constructor (Preferred over @Autowired on fields)
    // Tiêm phụ thuộc thông qua Hàm tạo (Ưu tiên hơn @Autowired trên các trường)
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return greetingService.generateGreeting();
    }
}
```

## Exercises
## Bài tập
1. What is the difference between `@Controller` and `@RestController` in Spring?
1. Sự khác biệt giữa `@Controller` và `@RestController` trong Spring là gì?
2. Explain the Bean lifecycle in Spring. What do `@PostConstruct` and `@PreDestroy` do?
2. Giải thích vòng đời của Bean trong Spring. `@PostConstruct` và `@PreDestroy` làm gì?
3. Setup a basic Spring Boot project using [Spring Initializr](https://start.spring.io/). Add the `Spring Web` dependency, and write a simple API that returns a JSON list of users.
3. Thiết lập một dự án Spring Boot cơ bản bằng cách sử dụng [Spring Initializr](https://start.spring.io/). Thêm phụ thuộc `Spring Web` và viết một API đơn giản trả về một danh sách JSON người dùng.

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Understand Bean Scopes. What is the default scope of a Spring Bean? (Answer: Singleton). Name others (Prototype, Request, Session).
- Hiểu các phạm vi Bean. Phạm vi mặc định của một Bean Spring là gì? (Trả lời: Singleton). Kể tên những người khác (Prototype, Request, Session).
- Be able to explain the concept of Dependency Injection conceptually—why is it good for Unit Testing?
- Có thể giải thích khái niệm Tiêm phụ thuộc một cách khái niệm—tại sao nó tốt cho Kiểm tra đơn vị?
- Explain how Spring Boot auto-configuration works implicitly.
- Giải thích cách tự động cấu hình Spring Boot hoạt động một cách ngầm định.
