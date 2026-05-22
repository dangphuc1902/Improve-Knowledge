# 📖 Servlet/JSP & MVC - Lý Thuyết, Interview & Bài Tập

> **Tuần 3 | Java Web Application cơ bản**

---

## LÝ THUYẾT

### 1. Servlet Lifecycle (Chu kỳ sống)

```
1. Loading     → ClassLoader load Servlet class
2. Instantiation → new Servlet()
3. init()      → Gọi 1 lần khi khởi tạo
4. service()   → Gọi mỗi request (doGet/doPost/...)
5. destroy()   → Gọi 1 lần khi shutdown
```

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // Khởi tạo resources (DB connection, config...)
        System.out.println("Servlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String name = req.getParameter("name");

        try (PrintWriter out = resp.getWriter()) {
            out.println("<h1>Hello, " + name + "!</h1>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Handle form submission
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Process, set attribute, forward/redirect
        req.setAttribute("user", username);
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }

    @Override
    public void destroy() {
        // Cleanup resources
        System.out.println("Servlet destroyed");
    }
}
```

---

### 2. Request vs Response

```java
// HttpServletRequest
req.getParameter("name");          // Query param / form field
req.getParameterValues("items[]"); // Multiple values
req.getAttribute("user");          // Request-scoped attribute
req.getSession();                  // Get/create session
req.getRequestURI();               // /myapp/hello
req.getContextPath();              // /myapp
req.getHeader("Authorization");   // HTTP header
req.getInputStream();              // Request body

// HttpServletResponse
resp.setStatus(200);
resp.setContentType("application/json");
resp.setHeader("X-Custom", "value");
resp.sendRedirect("/login");       // Client-side redirect (302)
resp.getWriter().println("text");
```

---

### 3. Session Management

```java
// Tạo/lấy session
HttpSession session = req.getSession();         // Tạo nếu chưa có
HttpSession session = req.getSession(false);    // Trả null nếu chưa có

// Lưu dữ liệu
session.setAttribute("userId", 123);
session.setAttribute("cart", cartObject);

// Đọc dữ liệu
Integer userId = (Integer) session.getAttribute("userId");

// Xóa/logout
session.removeAttribute("userId");
session.invalidate();  // Xóa toàn bộ session

// Cấu hình timeout (giây)
session.setMaxInactiveInterval(30 * 60);  // 30 phút
```

---

### 4. Kiến Trúc MVC với Servlet/JSP

```
Model    → Java classes (POJOs, Service, DAO)
View     → JSP files
Controller → Servlet

Request Flow:
Browser → Servlet (Controller)
              ↓
         Service/DAO (Model)
              ↓
         req.setAttribute(data)
              ↓
         forward to JSP (View)
              ↓
         JSP renders HTML → Browser
```

```java
// Controller Servlet
@WebServlet("/products")
public class ProductController extends HttpServlet {

    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ... {
        String action = req.getParameter("action");

        if ("detail".equals(action)) {
            Long id = Long.parseLong(req.getParameter("id"));
            Product product = productService.findById(id);
            req.setAttribute("product", product);
            req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
        } else {
            List<Product> products = productService.findAll();
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/views/product-list.jsp").forward(req, resp);
        }
    }
}
```

```jsp
<%-- /WEB-INF/views/product-list.jsp --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<body>
  <h1>Products</h1>
  <c:forEach items="${products}" var="p">
    <div>
      <a href="/products?action=detail&id=${p.id}">${p.name}</a>
      - $${p.price}
    </div>
  </c:forEach>
</body>
</html>
```

---

### 5. Filter & Listener

```java
// Filter - intercept requests
@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpSession session = httpReq.getSession(false);

        String uri = httpReq.getRequestURI();
        boolean isPublic = uri.contains("/login") || uri.contains("/css/");

        if (isPublic || (session != null && session.getAttribute("userId") != null)) {
            chain.doFilter(req, resp);  // Continue
        } else {
            ((HttpServletResponse) resp).sendRedirect("/login");
        }
    }
}
```

---

## INTERVIEW Q&A

### Q1: Servlet hoạt động thế nào?

Servlet là Java class chạy trên server, xử lý HTTP requests và trả responses. Quy trình:
1. Client gửi request → Servlet Container (Tomcat) nhận
2. Container tìm Servlet mapping (URL pattern)
3. Gọi `service()` → `doGet()` hoặc `doPost()`
4. Servlet tạo response → trả client

**Servlet Container quản lý lifecycle:** load, init, service (multithreaded), destroy.

---

### Q2: Servlet là single-instance hay multi-instance?

**Single instance** nhưng **multi-threaded** - Container dùng 1 Servlet instance cho nhiều concurrent requests. Do đó:
- ❌ KHÔNG dùng instance variables để lưu request-specific data
- ✅ Dùng local variables trong method
- ✅ Lưu data trong request/session attributes

---

### Q3: JSP được biên dịch ra gì?

JSP được biên dịch thành **Servlet** bởi JSP container:
```
login.jsp → login_jsp.java (Servlet class) → login_jsp.class
```

Quá trình này xảy ra lần đầu request (hoặc khi JSP thay đổi). Sau đó Servlet được tái sử dụng.

---

### Q4: forward() vs sendRedirect() khác nhau thế nào?

| | forward() | sendRedirect() |
|--|-----------|----------------|
| Thực hiện | Server-side | Client-side |
| URL thay đổi | Không | Có |
| Request scope | Giữ lại | Mất |
| HTTP status | - | 302 Found |
| Khi dùng | MVC dispatch | Sau POST (PRG pattern) |

**PRG Pattern (Post-Redirect-Get):** Sau POST xử lý xong → sendRedirect → tránh duplicate submit khi refresh.

---

### Q5: Tại sao đặt JSP trong WEB-INF?

`WEB-INF/` không accessible trực tiếp từ browser (bảo mật). JSP trong đây chỉ được access qua Servlet forward → không bị bypass Controller.

---

## BÀI TẬP

### Bài 1: Login Form với Session

**Yêu cầu:** Tạo mini web app:
- `/login` (GET): Hiển thị form đăng nhập
- `/login` (POST): Xử lý đăng nhập, tạo session
- `/dashboard` (GET): Hiển thị trang chính (yêu cầu đăng nhập)
- `/logout` (GET): Xóa session, redirect login
- Filter: Bảo vệ `/dashboard`

```
File structure:
src/
  LoginServlet.java    - Handle /login
  DashboardServlet.java - Handle /dashboard
  LogoutServlet.java   - Handle /logout
  AuthFilter.java      - Protect secure pages
webapp/
  WEB-INF/views/
    login.jsp
    dashboard.jsp
  css/style.css
```

### Bài 2: CRUD Product với MVC

Xây dựng Product Management system:
- List all products (GET /products)
- View product detail (GET /products?id=1)
- Add product form (GET /products/new)
- Save product (POST /products)
- Delete product (POST /products/delete?id=1)

Dùng in-memory List làm data store (chưa cần DB).

---

*📌 Tiếp theo: [04-JDBC-Database](../04-JDBC-Database/Theory.md)*
