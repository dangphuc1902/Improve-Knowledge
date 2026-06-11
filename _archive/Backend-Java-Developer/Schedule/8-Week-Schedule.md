# 📅 Lịch Học 8 Tuần - Backend Java Developer

> Kế hoạch chi tiết từng ngày để ôn luyện toàn diện trong 2 tháng.

---

## 🗓️ Tuần 1: Core Java & OOP (Nền tảng)

**Mục tiêu:** Nắm vững cú pháp Java cơ bản và các nguyên lý OOP.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | Java Basics: JVM, JDK, JRE, Data Types, Variables | Đọc lý thuyết + viết code Hello World |
| Thứ 3 | OOP: Class, Object, Encapsulation | Tạo class BankAccount với getter/setter |
| Thứ 4 | OOP: Inheritance, Polymorphism | Viết hierarchy Animal → Dog, Cat |
| Thứ 5 | OOP: Abstraction, Interface, Abstract Class | So sánh interface vs abstract class |
| Thứ 6 | Java Keywords, Access Modifiers, Static | Ôn câu hỏi phỏng vấn |
| Thứ 7 | Ôn tập + Bài tập tổng hợp | Mock code 5 bài OOP |
| CN | Review + Nghỉ ngơi | Đọc lại ghi chú |

**Tài liệu:** Oracle Java Tutorials - OOP, "Head First Java" Chapter 1-8

---

## 🗓️ Tuần 2: Java Nâng Cao (Collections, Generics, Concurrency)

**Mục tiêu:** Sử dụng thành thạo Collections API và lập trình đa luồng cơ bản.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | Collections: List (ArrayList, LinkedList) | Bài tập: xử lý danh sách sinh viên |
| Thứ 3 | Collections: Set (HashSet, TreeSet), Map (HashMap) | Bài tập: đếm tần suất từ |
| Thứ 4 | Exception Handling, Custom Exception | Viết ứng dụng tính điểm có try/catch |
| Thứ 5 | Generics: Generic class, method, wildcard | Viết Stack<T> generic |
| Thứ 6 | I/O Streams, FileReader/Writer, BufferedReader | Đọc/ghi file văn bản |
| Thứ 7 | Multithreading: Thread, Runnable, synchronized | Bài tập: Producer-Consumer pattern |
| CN | Concurrent Collections, Lock, Atomic | Ôn câu hỏi phỏng vấn concurrency |

**Tài liệu:** Oracle Java Tutorial (Collections, Concurrency), Baeldung articles

---

## 🗓️ Tuần 3: Servlet/JSP + JDBC

**Mục tiêu:** Hiểu cơ chế Web Application cơ bản và kết nối CSDL với Java.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | Servlet: lifecycle, doGet, doPost | Tạo HelloServlet đơn giản |
| Thứ 3 | JSP: Scriptlets, EL, JSTL | Tạo trang hiển thị dữ liệu |
| Thứ 4 | MVC pattern với Servlet/JSP | Xây form đăng nhập theo MVC |
| Thứ 5 | JDBC: Connection, Statement, PreparedStatement | Kết nối MySQL, thực thi SELECT |
| Thứ 6 | JDBC: ResultSet, Transaction (commit/rollback) | Viết DAO class cho User |
| Thứ 7 | Tích hợp: Web app CRUD với Servlet + JDBC | Mini project: quản lý sinh viên |
| CN | Ôn lý thuyết + câu hỏi phỏng vấn | Review bài tập |

**Tài liệu:** Baeldung Servlet tutorial, Oracle JDBC tutorial

---

## 🗓️ Tuần 4: Spring Core + Spring MVC + Spring Boot

**Mục tiêu:** Hiểu và sử dụng được Spring Framework trong ứng dụng thực tế.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | Spring IoC/DI: ApplicationContext, Bean | Tạo project Spring, cấu hình bean XML |
| Thứ 3 | Annotation-based DI: @Component, @Autowired, @Bean | Refactor sang annotation |
| Thứ 4 | Bean Scope: Singleton, Prototype, Request, Session | Test từng scope |
| Thứ 5 | Spring MVC: DispatcherServlet, Controller, View | Tạo web MVC đơn giản |
| Thứ 6 | Spring Boot: auto-config, starter, application.properties | Khởi tạo project với Spring Initializr |
| Thứ 7 | Spring Boot REST: @RestController, @RequestMapping | Viết API CRUD đơn giản |
| CN | Ôn câu hỏi phỏng vấn Spring | Thực hành thêm |

**Tài liệu:** spring.io/guides, "Spring in Action" (Craig Walls), Baeldung Spring series

---

## 🗓️ Tuần 5: Hibernate & JPA

**Mục tiêu:** Sử dụng ORM để tương tác với CSDL theo cách hướng đối tượng.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | ORM là gì? Hibernate architecture | Đọc lý thuyết + cài đặt Hibernate |
| Thứ 3 | Entity mapping: @Entity, @Table, @Column, @Id | Tạo Entity User, Product |
| Thứ 4 | SessionFactory, Session, Transaction | Thực hành save/get entity |
| Thứ 5 | HQL (Hibernate Query Language), Criteria API | Viết các query tìm kiếm |
| Thứ 6 | JPA: EntityManager, @OneToMany, @ManyToMany | Mapping quan hệ giữa bảng |
| Thứ 7 | Spring Data JPA: JpaRepository, @Query | Tích hợp vào Spring Boot |
| CN | Ôn lại + câu hỏi phỏng vấn Hibernate | Mini project review |

**Tài liệu:** Hibernate official docs, Baeldung Hibernate/JPA series, "Spring Data JPA" guide

---

## 🗓️ Tuần 6: RESTful Web Services + Microservices

**Mục tiêu:** Thiết kế và xây dựng REST API, hiểu kiến trúc microservices.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | HTTP Protocol, REST principles, HTTP Methods | So sánh GET/POST/PUT/DELETE |
| Thứ 3 | JSON, REST API với Spring Boot | Xây API quản lý sản phẩm |
| Thứ 4 | SOAP: XML, WSDL, SOAP envelope | Đọc/tạo WSDL đơn giản |
| Thứ 5 | REST vs SOAP: khi nào dùng cái nào? | Ôn câu hỏi phỏng vấn Web Services |
| Thứ 6 | Microservices: khái niệm, lợi ích, thách thức | Vẽ kiến trúc hệ thống bán hàng |
| Thứ 7 | Tạo 2 microservice (User Service, Product Service) | Giao tiếp qua REST giữa 2 service |
| CN | Service Discovery, API Gateway (cơ bản) | Đọc về Eureka, Zuul |

**Tài liệu:** Martin Fowler Microservices article, Spring Boot REST guides, Baeldung

---

## 🗓️ Tuần 7: Database Oracle/MSSQL + Application Servers

**Mục tiêu:** Thành thạo SQL nâng cao và biết cách deploy ứng dụng Java.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | SQL: JOIN (INNER, LEFT, RIGHT, FULL), Subquery | Viết 10 câu SQL JOIN phức tạp |
| Thứ 3 | SQL: View, Index, Tối ưu hóa query | Tạo Index, đo performance |
| Thứ 4 | Stored Procedure, Function, Trigger | Viết SP cho báo cáo đơn giản |
| Thứ 5 | Oracle vs MSSQL: đặc điểm, syntax khác biệt | So sánh TOP (MSSQL) vs ROWNUM (Oracle) |
| Thứ 6 | Apache Tomcat: cài đặt, cấu hình, deploy WAR | Deploy Spring MVC app lên Tomcat |
| Thứ 7 | JBoss/WildFly, WebSphere, WebLogic (tổng quan) | Đọc so sánh Application Servers |
| CN | Ôn câu hỏi phỏng vấn SQL + Server | Review + notes |

**Tài liệu:** Oracle official docs, Microsoft SQL Server docs, Tomcat docs

---

## 🗓️ Tuần 8: Design Patterns + Build Tools + Git + Tổng Ôn

**Mục tiêu:** Củng cố toàn bộ kiến thức, chuẩn bị mock interview.

| Ngày | Nội dung | Hoạt động |
|------|----------|-----------|
| Thứ 2 | Creational Patterns: Singleton, Factory, Builder | Code ví dụ từng pattern |
| Thứ 3 | Structural Patterns: Adapter, Decorator, Facade | Code ví dụ từng pattern |
| Thứ 4 | Behavioral Patterns: Observer, Strategy, Command | Code Observer với Java |
| Thứ 5 | Maven: POM, lifecycle, dependency management | Tạo Maven project, quản lý dependencies |
| Thứ 6 | Gradle + Git: branching, merging, rebase | Tạo GitHub repo, push project |
| Thứ 7 | Frontend cơ bản: HTML5, CSS3, Bootstrap responsive | Tạo trang HTML với Bootstrap |
| CN | **MOCK INTERVIEW TỔNG HỢP** | Ôn tất cả câu hỏi phỏng vấn |

**Tài liệu:** "Head First Design Patterns", Maven quickstart, Pro Git book

---

## 📊 Bảng Theo Dõi Tiến Độ

| Tuần | Chủ đề | Bắt đầu | Hoàn thành | Trạng thái |
|------|--------|---------|-----------|-----------|
| 1 | Core Java & OOP | | | ⬜ Chưa bắt đầu |
| 2 | Java Nâng Cao | | | ⬜ Chưa bắt đầu |
| 3 | Servlet/JSP + JDBC | | | ⬜ Chưa bắt đầu |
| 4 | Spring Framework | | | ⬜ Chưa bắt đầu |
| 5 | Hibernate & JPA | | | ⬜ Chưa bắt đầu |
| 6 | REST + Microservices | | | ⬜ Chưa bắt đầu |
| 7 | Database + App Servers | | | ⬜ Chưa bắt đầu |
| 8 | Patterns + Tools + Review | | | ⬜ Chưa bắt đầu |

---

## 💡 Mẹo Học Hiệu Quả

- **Pomodoro:** Học 25 phút → nghỉ 5 phút × 4 lần → nghỉ 15 phút
- **Active Recall:** Đọc xong thì đóng sách, tự ghi lại những gì nhớ được
- **Spaced Repetition:** Ôn lại bài cũ vào đầu mỗi buổi học mới
- **Code Every Day:** Viết ít nhất 30 phút code thực hành mỗi ngày
- **Mock Interview:** Cuối mỗi tuần, tự trả lời 5-10 câu hỏi phỏng vấn to thành lời
