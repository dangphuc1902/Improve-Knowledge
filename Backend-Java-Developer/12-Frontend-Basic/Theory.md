# 📖 Frontend Cơ Bản - HTML5, CSS3, Bootstrap & Responsive Design

> **Tuần 8 | Frontend kiến thức cơ bản cho Backend Developer**

---

## LÝ THUYẾT

### 1. HTML5 Cơ Bản

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Mô tả trang web">
    <title>Tên Trang</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <!-- Semantic HTML5 elements -->
    <header>
        <nav>
            <ul>
                <li><a href="/">Trang chủ</a></li>
                <li><a href="/about">Giới thiệu</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <article>
            <h1>Tiêu đề chính (chỉ 1 h1/page)</h1>
            <section>
                <h2>Phần 1</h2>
                <p>Nội dung...</p>
            </section>
        </article>

        <aside>
            <!-- Sidebar content -->
        </aside>
    </main>

    <footer>
        <p>© 2025 My Company</p>
    </footer>

    <!-- Form example -->
    <form action="/submit" method="POST">
        <label for="name">Họ tên:</label>
        <input type="text" id="name" name="name" required minlength="2">

        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>

        <label for="age">Tuổi:</label>
        <input type="number" id="age" name="age" min="18" max="100">

        <label for="message">Tin nhắn:</label>
        <textarea id="message" name="message" rows="4"></textarea>

        <select name="department">
            <option value="">-- Chọn phòng ban --</option>
            <option value="it">IT</option>
            <option value="hr">HR</option>
        </select>

        <button type="submit">Gửi</button>
    </form>

    <!-- HTML5 semantic elements -->
    <figure>
        <img src="image.jpg" alt="Mô tả ảnh">
        <figcaption>Caption ảnh</figcaption>
    </figure>

    <details>
        <summary>FAQ: Câu hỏi thường gặp</summary>
        <p>Câu trả lời...</p>
    </details>

    <!-- Data attributes -->
    <div data-user-id="123" data-role="admin">User card</div>

    <script src="app.js"></script>
</body>
</html>
```

---

### 2. CSS3 Cơ Bản

```css
/* CSS Selectors */
p { }                    /* Element */
.class-name { }          /* Class */
#id-name { }             /* ID */
div > p { }              /* Direct child */
div p { }                /* Descendant */
a:hover { }              /* Pseudo-class */
p::first-line { }        /* Pseudo-element */
input[type="email"] { }  /* Attribute */

/* Box Model */
.box {
    width: 300px;
    height: 200px;
    padding: 20px;           /* Inside border */
    border: 2px solid #333;
    margin: 10px auto;       /* Outside border, auto = center */
    box-sizing: border-box;  /* Width includes padding + border */
}

/* Flexbox */
.container {
    display: flex;
    flex-direction: row;      /* row | column */
    justify-content: center;  /* main axis alignment */
    align-items: center;      /* cross axis alignment */
    flex-wrap: wrap;
    gap: 16px;
}

.flex-item {
    flex: 1;          /* grow: 1, shrink: 1, basis: 0 */
    flex-basis: 200px;
}

/* Grid */
.grid-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);  /* 3 equal columns */
    grid-template-columns: 200px 1fr 2fr;   /* Fixed + flexible */
    grid-gap: 20px;
    grid-template-rows: auto;
}

.grid-item:first-child {
    grid-column: 1 / 3;  /* Span 2 columns */
    grid-row: 1 / 2;
}

/* CSS Variables */
:root {
    --primary-color: #3498db;
    --secondary-color: #2ecc71;
    --font-size-base: 16px;
    --spacing-md: 16px;
}

.button {
    background-color: var(--primary-color);
    font-size: var(--font-size-base);
    padding: var(--spacing-md);
}

/* Transitions & Animations */
.button {
    transition: all 0.3s ease;
}

.button:hover {
    background-color: #2980b9;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(20px); }
    to   { opacity: 1; transform: translateY(0); }
}

.card {
    animation: fadeIn 0.5s ease forwards;
}
```

---

### 3. Responsive Design & Media Queries

```css
/* Mobile-first approach (recommended) */

/* Base styles for mobile */
.container {
    width: 100%;
    padding: 0 16px;
}

.card {
    width: 100%;
    margin-bottom: 16px;
}

/* Tablet (>= 768px) */
@media (min-width: 768px) {
    .container {
        max-width: 768px;
        margin: 0 auto;
    }
    
    .card {
        width: calc(50% - 8px);  /* 2 columns */
    }
}

/* Desktop (>= 1024px) */
@media (min-width: 1024px) {
    .container {
        max-width: 1200px;
    }
    
    .card {
        width: calc(33.33% - 11px);  /* 3 columns */
    }
}

/* Large screen */
@media (min-width: 1440px) {
    .card {
        width: calc(25% - 12px);  /* 4 columns */
    }
}

/* Print media */
@media print {
    nav, footer, .ads { display: none; }
    body { font-size: 12pt; }
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
    body {
        background: #1a1a1a;
        color: #ffffff;
    }
}
```

---

### 4. Bootstrap 5

```html
<!-- Bootstrap CDN -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

<!-- Grid System (12 columns) -->
<div class="container">
    <div class="row">
        <div class="col-12 col-md-6 col-lg-4">
            <!-- Full width mobile, half tablet, 1/3 desktop -->
        </div>
        <div class="col-12 col-md-6 col-lg-8">
            <!-- Full width mobile, half tablet, 2/3 desktop -->
        </div>
    </div>
</div>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">My App</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" 
                data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="/about">About</a></li>
            </ul>
        </div>
    </div>
</nav>

<!-- Cards -->
<div class="card shadow-sm">
    <img src="image.jpg" class="card-img-top" alt="...">
    <div class="card-body">
        <h5 class="card-title">Card Title</h5>
        <p class="card-text">Card content...</p>
        <a href="#" class="btn btn-primary">Action</a>
    </div>
</div>

<!-- Form -->
<form>
    <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" class="form-control" id="email" placeholder="name@example.com">
        <div class="form-text">Chúng tôi không chia sẻ email của bạn.</div>
    </div>
    <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input type="password" class="form-control" id="password">
    </div>
    <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
</form>

<!-- Table -->
<div class="table-responsive">
    <table class="table table-striped table-hover">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Email</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>1</td>
                <td>Nguyễn A</td>
                <td>a@example.com</td>
                <td>
                    <button class="btn btn-sm btn-warning">Edit</button>
                    <button class="btn btn-sm btn-danger">Delete</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<!-- Alert -->
<div class="alert alert-success alert-dismissible fade show" role="alert">
    <strong>Thành công!</strong> Đã lưu thay đổi.
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>

<!-- Modal -->
<div class="modal fade" id="confirmModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Xác nhận</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">Bạn có chắc chắn muốn xóa?</div>
            <div class="modal-footer">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button class="btn btn-danger">Xóa</button>
            </div>
        </div>
    </div>
</div>
```

---

## INTERVIEW Q&A

### Q1: Semantic HTML là gì? Tại sao quan trọng?

**Semantic HTML** = dùng tags có ý nghĩa mô tả nội dung (`<header>`, `<nav>`, `<main>`, `<article>`, `<section>`, `<footer>`).

**Lợi ích:**
- SEO tốt hơn (Google hiểu structure)
- Accessibility (screen readers)
- Code dễ đọc
- Maintain dễ hơn

---

### Q2: Flexbox vs Grid - khi nào dùng cái nào?

- **Flexbox**: 1-dimensional layout (row hoặc column). Tốt cho: navbar, button groups, card alignment.
- **CSS Grid**: 2-dimensional layout (rows AND columns). Tốt cho: page layout, complex grid systems.

---

### Q3: Media Query là gì?

Media query cho phép apply CSS dựa trên device characteristics (screen size, orientation, color scheme):
```css
@media (min-width: 768px) { /* Tablet và lớn hơn */ }
@media (max-width: 767px) { /* Mobile only */ }
@media print { /* Khi in */ }
```

---

### Q4: Bootstrap Grid System hoạt động thế nào?

Bootstrap dùng 12-column grid system:
- `.container` → fixed max-width container
- `.row` → flex container
- `.col-*` → columns (12 = full width, 6 = half, 4 = 1/3)
- Breakpoints: xs(<576), sm(≥576), md(≥768), lg(≥992), xl(≥1200), xxl(≥1400)

---

## BÀI TẬP

### Tạo Trang Web Responsive cho Backend Dev

Xây dựng portfolio page đơn giản:

**Yêu cầu:**
1. Navbar responsive (hamburger menu trên mobile)
2. Hero section với giới thiệu bản thân
3. Skills section (Bootstrap cards - 3 columns desktop, 2 tablet, 1 mobile)
4. Projects section với table
5. Contact form với validation
6. Footer

```html
<!-- starter template -->
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Java Backend Developer Portfolio</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="style.css" rel="stylesheet">
</head>
<body>
    <!-- TODO: Implement sections above -->
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

---

*📌 Tiếp theo: Ôn tập tổng hợp → [Soft-Skills/Interview-Tips.md](../Soft-Skills/Interview-Tips.md)*
