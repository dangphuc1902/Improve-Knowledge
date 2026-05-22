# 🎤 Kỹ Năng Mềm & Mẹo Phỏng Vấn

---

## 1. Chuẩn Bị Trước Phỏng Vấn

### Nghiên Cứu Công Ty
- [ ] Tìm hiểu sản phẩm/dịch vụ chính
- [ ] Stack công nghệ họ đang dùng (LinkedIn, GitHub, JD)
- [ ] Văn hóa công ty, quy mô team
- [ ] Tin tức gần đây về công ty

### Chuẩn Bị Câu Trả Lời STAR

**STAR Method** cho câu hỏi behavioral:
- **S**ituation: Bối cảnh như thế nào?
- **T**ask: Nhiệm vụ/thách thức của bạn?
- **A**ction: Bạn đã làm gì cụ thể?
- **R**esult: Kết quả đạt được (số liệu nếu có)?

### Câu Hỏi Behavioral Thường Gặp

| Câu hỏi | STAR Answer Template |
|---------|---------------------|
| "Kể về lần bạn debug một bug khó" | S: Production bug → T: Fix trong 4h → A: Log analysis, bisect → R: 99.9% uptime |
| "Khi nào bạn không đồng ý với teammate?" | S: Code review → T: Thuyết phục về pattern → A: Demo + benchmark → R: Team adopt |
| "Lần bạn fail và học được gì?" | S: Deploy thiếu test → T: Xử lý incident → A: Rollback + postmortem → R: CI/CD process mới |
| "Bạn quản lý deadline như thế nào?" | S: 3 task song song → T: Deadline chặt → A: Prioritize, communicate → R: Deliver đúng hạn |

---

## 2. Trong Buổi Phỏng Vấn

### Kỹ Thuật Trả Lời

**THINK OUT LOUD:**
```
Interviewer: "Thiết kế hệ thống cache cho API của bạn"

Tôi: "Để tôi hiểu rõ yêu cầu trước...
      - Scale như thế nào? 1K hay 1M request/s?
      - Data thay đổi bao thường xuyên?
      - Consistency requirement ra sao?
      
      Với assumption X, Y, Z... tôi sẽ tiếp cận như sau:
      [Vẽ diagram, giải thích từng component]"
```

**KHI KHÔNG BIẾT:**
❌ "Tôi không biết cái này"  
✅ "Tôi chưa dùng cái này trong production, nhưng tôi hiểu nguyên lý là... Tôi sẽ tìm hiểu thêm về..."

**KHI BỊ STUCK trong coding:**
1. Tóm lại đề bài (confirm hiểu đúng)
2. Nêu brute force solution trước
3. Phân tích complexity
4. Tối ưu dần dần
5. Nói ra suy nghĩ từng bước

---

### Ngôn Ngữ Tích Cực

| Thay vì... | Hãy nói... |
|-----------|-----------|
| "Tôi không giỏi..." | "Đây là area tôi đang phát triển..." |
| "Tôi chưa làm bao giờ" | "Chưa thực hành nhưng tôi đã nghiên cứu và hiểu nguyên lý..." |
| "Tôi không nhớ rõ" | "Nếu tôi nhớ không nhầm... cho tôi suy nghĩ thêm một chút" |
| "Không biết có đúng không" | "Theo tôi hiểu..." |

---

## 3. Câu Hỏi Ngược Cho Interviewer

**Về Công Nghệ:**
- "Stack công nghệ của team hiện tại như thế nào? Có kế hoạch migrate gì không?"
- "Team handle technical debt như thế nào?"
- "Quy trình deployment của team ra sao? CI/CD pipeline?"

**Về Team & Văn Hóa:**
- "Team size hiện tại? Cấu trúc team như thế nào?"
- "Quy trình code review như thế nào? Ai review?"
- "Senior developer mentor junior không?"

**Về Role:**
- "Trong 3 tháng đầu, tôi sẽ làm việc với gì cụ thể?"
- "Làm sao team đo lường thành công của developer?"
- "Có cơ hội học công nghệ mới không?"

**TRÁNH hỏi sớm:**
- Lương, thưởng (chờ offer stage)
- Số ngày phép
- Remote policy (chờ có offer)

---

## 4. Kỹ Thuật Luyện Tập

### Mock Interview Self-Practice

**Quy trình 30 phút/ngày:**
1. **5 phút:** Random pick 3 câu hỏi từ Interview-QA.md
2. **15 phút:** Trả lời to thành lời (ghi âm nếu có thể)
3. **5 phút:** Nghe lại, đánh giá: rõ ràng chưa? Đủ technical depth chưa?
4. **5 phút:** Đọc answer mẫu, note điểm cần bổ sung

### Whiteboard Coding Practice

Khi làm bài coding:
```
[1] Đọc đề (2 phút) → Confirm hiểu đúng
[2] Clarify edge cases (2 phút)
[3] Thiết kế solution to thành lời (3 phút)
[4] Code (15-20 phút)
[5] Test với examples (3 phút)
[6] Analyze time/space complexity (2 phút)
```

### Fluency Checklist

Mỗi topic, tự kiểm tra:
- [ ] Có thể giải thích trong 1 phút không?
- [ ] Có ví dụ code minh họa không?
- [ ] Biết tradeoffs (ưu/nhược điểm) không?
- [ ] Kết nối được với real-world use case không?

---

## 5. Phong Thái & Thái Độ

### Những Điều Nên Làm ✅
- Đến/vào (nếu online) trước 5-10 phút
- Chuẩn bị môi trường coding (IDE, browser tabs)
- Nói chuyện chuyên nghiệp, tự tin nhưng không kiêu ngạo
- Lắng nghe chủ động (gật đầu, paraphrase)
- Hỏi lại khi không chắc hiểu đúng yêu cầu
- Thank you note sau phỏng vấn

### Những Điều Không Nên Làm ❌
- Nói xấu công ty/sếp/team cũ
- Nói "Tôi biết hết" hoặc "Cái đó dễ mà"
- Im lặng khi suy nghĩ mà không nói gì
- Trả lời chỉ "Có" hoặc "Không" cho câu hỏi kỹ thuật
- Bắt đầu code ngay mà chưa hiểu yêu cầu

---

## 6. Sau Phỏng Vấn

### Tự Đánh Giá
- Câu nào trả lời tốt? Tại sao?
- Câu nào còn yếu? Cần ôn thêm gì?
- Cảm nhận về company culture?
- Có muốn làm ở đây không?

### Follow-up
- Gửi thank you email trong 24h
- Nếu chờ lâu (> 1 tuần), có thể follow up 1 lần
- Nếu bị reject → hỏi feedback (không phải lúc nào cũng được)

---

## 7. Chuẩn Bị CV

### CV Backend Java Developer

**Technical Skills section:**
```
Languages: Java 8/11/17, SQL, HTML/CSS
Frameworks: Spring Boot, Spring MVC, Hibernate/JPA
Databases: MySQL, Oracle, PostgreSQL, Redis
Tools: Maven, Gradle, Git, Docker, Postman
Servers: Apache Tomcat, JBoss/WildFly
Concepts: REST APIs, Microservices, Design Patterns, OOP
```

**Mỗi job experience - dùng format STAR:**
```
✅ "Tối ưu hóa REST API giảm response time từ 2s xuống 200ms 
    bằng cách implement caching với Redis và optimize SQL queries"

❌ "Làm việc với REST API và database"
```

**Projects - nên có:**
- Mini E-commerce: Spring Boot + Hibernate + MySQL + REST API
- Microservices: 2-3 services, REST communication
- GitHub link với README rõ ràng

---

*Chúc bạn phỏng vấn thành công! 🎉*
