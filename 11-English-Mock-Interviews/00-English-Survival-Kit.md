# 🛟 English Survival Kit — Phỏng vấn Backend khi tiếng Anh còn yếu

> **Dành cho:** người giao tiếp hằng ngày được, nhưng khi phỏng vấn thì thiếu từ, bị "đứng hình", mất tự tin.
> **Mục tiêu đến 01/2027:** không cần nói hay — cần nói **rõ, ngắn, đúng ý, không im lặng quá 5 giây**.
> **Thời lượng:** 45 phút/ngày (xem Phần 7). Học file này TRƯỚC các file Session-xx.

---

## 0. Tư duy đúng (đọc 1 lần, nhớ mãi)

1. **Interviewer chấm kỹ thuật + khả năng làm việc cùng team, không chấm IELTS.** Câu đơn giản, đúng ngữ pháp cơ bản, đúng thuật ngữ = đủ.
2. **Câu ngắn thắng câu dài.** Mỗi câu 8–15 từ. Câu dài → dễ sai ngữ pháp → hoảng → quên ý.
3. **Thuật ngữ kỹ thuật là "từ vựng miễn phí".** `cache`, `index`, `transaction`, `thread`, `latency` — bạn đã biết. Chỉ cần thêm "keo dán" (Phần 2) để nối chúng lại.
4. **Không học thuộc nguyên văn bài mẫu dài.** Học thuộc **khung** + **từ khóa**, rồi nói bằng lời của mình. Interviewer nhận ra ngay câu học vẹt.
5. **Im lặng là kẻ thù, không phải lỗi sai.** Khi bí, dùng câu mua thời gian (Phần 3) thay vì im lặng.

---

## 1. Ba khung trả lời — dùng cho 90% câu hỏi

### Khung A — Câu hỏi kỹ thuật "What is X / How does X work?" → **D-E-E-T**

| Bước | Nói gì | Câu mở đầu mẫu |
|---|---|---|
| **D**efine | Định nghĩa 1 câu | "X is a ... that ..." |
| **E**xplain | Cách hoạt động, 2–3 câu | "It works like this. First, ... Then, ..." |
| **E**xample | Ví dụ từ dự án thật của bạn | "In my project, I used it to ..." |
| **T**rade-off | Ưu/nhược hoặc khi nào không dùng | "The downside is ... so I would not use it when ..." |

**Ví dụ — "What is a database index?"**

> "An index is a data structure that helps the database find rows faster.
> It works like the index at the end of a book. Most databases use a B-tree, so a lookup is O(log n) instead of a full table scan.
> In my game server, the match history table was slow. I added a composite index on `player_id` and `created_at`, and the query became much faster.
> The trade-off is that writes become slower, and the index uses extra storage. So I don't add indexes on every column."

👉 4 câu ngắn, mỗi câu < 20 từ, có ví dụ thật, có trade-off → đây chính là câu trả lời **Middle-level**. Junior thường dừng ở bước D.

### Khung B — Câu hỏi hành vi "Tell me about a time..." → **STAR rút gọn**

| Bước | Thời lượng | Câu mở đầu mẫu |
|---|---|---|
| **S**ituation | 1 câu | "In my current company, we had a problem with ..." |
| **T**ask | 1 câu | "My job was to ..." |
| **A**ction | 3–4 câu (phần quan trọng nhất) | "First, I ... Then, I ... Finally, I ..." |
| **R**esult | 1–2 câu, có số nếu được | "As a result, ... / After that, ... dropped by about 30%." |
| (+ **L**esson) | 1 câu | "What I learned is ..." |

> Luôn dùng **"I"** cho phần Action, không phải "we". Interviewer muốn biết **bạn** làm gì.

### Khung C — System design / câu hỏi mở → **Clarify → Simple → Scale**

1. **Clarify:** "Before I start, can I ask a few questions? How many users do we expect? Is it read-heavy or write-heavy?"
2. **Simple first:** "Let me start with a simple version. The client calls an API server, and the server stores data in MySQL."
3. **Scale:** "Now, if traffic grows 10 times, the database becomes the bottleneck. So I would add Redis as a cache and..."
4. **Trade-off:** "This adds complexity, because now we need to keep the cache and the database consistent."

---

## 2. "Keo dán" — 40 cụm nối ý (học thuộc, dùng mọi nơi)

**Bắt đầu / sắp xếp ý**
- "There are two main reasons. The first one is ... The second one is ..."
- "Let me explain it step by step."
- "In simple words, ..."
- "From my experience, ..."

**Ví dụ**
- "For example, in my game server project, ..."
- "A real case I faced was ..."
- "Let me give you a concrete example."

**So sánh**
- "The main difference is that X ..., while Y ..."
- "X is better when ..., but Y is better when ..."
- "Both of them ..., but ..."

**Nguyên nhân – kết quả**
- "Because of that, ..."
- "This causes ..."
- "That's why we decided to ..."
- "As a result, ..."

**Trade-off (cụm "ăn điểm" Middle)**
- "The trade-off is ..."
- "It depends on the use case. If ..., I would choose ..."
- "The downside is ..."
- "It's faster, but it costs more memory."
- "We chose consistency over performance here, because it's about money/rewards."

**Kết thúc câu trả lời** (để interviewer biết bạn đã nói xong — tránh im lặng khó xử)
- "So that's how I would handle it."
- "That's the main idea."
- "Does that answer your question?"
- "I can go deeper into any part if you want."

---

## 3. Câu cứu nguy — khi bí, không hiểu, hoặc không biết

### Không nghe rõ / không hiểu câu hỏi
- "Sorry, could you repeat the question, please?"
- "Could you say that a bit more slowly?"
- "Just to make sure I understand — you're asking about **[nhắc lại ý]**, right?"  ⭐ *câu mạnh nhất: vừa mua thời gian vừa tránh trả lời sai câu hỏi*
- "Do you mean X or Y?"

### Cần thời gian suy nghĩ
- "That's a good question. Let me think for a moment."
- "Give me a few seconds to organize my thoughts."
- "Let me think out loud." *(rồi nói từng bước — interviewer đánh giá cao)*

### Không biết từ tiếng Anh
- "I'm not sure about the exact word, but it's the thing that ..." *(mô tả bằng từ đơn giản)*
- "Let me draw it / write it down." *(trên bảng hoặc trong editor)*

### Không biết câu trả lời (TRUNG THỰC — không đoán bừa)
- "I haven't used that in production, but my understanding is ..."
- "I'm not 100% sure, but I think it works like this ... I would verify it in the documentation."
- "I don't know the details of that, but I know the related concept, ..."
- "I haven't worked with Kubernetes in production yet. I've only deployed my personal project with Docker Compose. But I'm learning it now."

### Nói sai, muốn sửa
- "Sorry, let me correct that. I meant ..."
- "Let me rephrase that."

---

## 4. Tự giới thiệu — 2 phiên bản (câu ngắn, dễ nói)

> **Quy tắc:** Nói chậm. Ngắt sau mỗi dấu chấm. Thuộc **thứ tự ý**, không thuộc từng chữ.
> **Điều chỉnh** chi tiết cho đúng 100% với CV — interviewer sẽ hỏi sâu từng điểm bạn nhắc.

### Bản 30 giây (vòng HR, câu mở đầu)

> "Hi, my name is Phuc. I'm a backend developer with about three years of experience.
> Right now, I work at a game company. I build real-time game servers with C++ and Java.
> I also build backend services with Spring Boot, Redis, and MySQL.
> I'm looking for a middle backend role, where I can work on larger systems with more users."

### Bản 90 giây (vòng kỹ thuật)

> "Hi, my name is Phuc. I'm a backend developer based in Ho Chi Minh City.
>
> **[Hiện tại]** At my current company, Gihot Studio, I work on the backend for mobile multiplayer games. I write server logic in C++ — things like matchmaking, room state, rewards, and leaderboards. The servers talk to each other with gRPC and Protocol Buffers, and we use Redis and MySQL for data. The hard part is concurrency: many players change the same room state at the same time, so I spend a lot of time on thread safety and performance.
>
> **[Java/Web]** I also use Java and Spring Boot. I built services for our internal GM tool, which the operation team uses to manage game configs and players.
>
> **[Dự án cá nhân]** Outside work, I build a personal project called FPM. It's a finance management system with several Spring Boot microservices, Kafka, and Docker. It helps me learn microservice patterns that I don't use every day at work.
>
> **[Mục tiêu]** Now I want to move to a middle backend role at a bigger company, with more traffic and stronger engineering practices."

**Vì sao bản này hợp với bạn:**
- Câu ngắn, từ đơn giản → nói được khi run.
- Biến "dev game" thành điểm mạnh (concurrency, realtime, performance) thay vì điểm yếu.
- Nói rõ phần nào là công việc, phần nào là dự án cá nhân → **không bị bắt bẻ** khi bị hỏi sâu.

---

## 5. Câu hỏi khó về profile của bạn — trả lời mẫu

### "You work in games. Why should we hire you for a web/fintech backend?"

> "Game backends and web backends share the same core problems: concurrency, low latency, caching, and data consistency. In games, these problems are even stricter, because players notice a 100-millisecond delay immediately. So I'm used to thinking about thread safety, race conditions, and performance. What I need to learn is your business domain, and I learn domains quickly."

### "Have you worked on a large web project in a company?"

> "Not as my main job yet. My company work is mostly game servers, plus internal tools with Spring Boot. To close that gap, I built FPM, a microservice project with Spring Boot, Kafka, Redis, and Docker. I'm honest that a personal project is not the same as production traffic. That's exactly why I want this role."

👉 Trung thực + chủ động bù đắp = điểm cộng. **Không bao giờ** phóng đại một dự án cá nhân thành "production system with real users" nếu không phải vậy.

### "What is your weakness?"

> "My spoken English. I can read documentation and write technical messages well, but I'm slower when I speak. I practice every day — I record myself explaining technical topics, and I do mock interviews in English every week. It's getting better."

### "Why do you want to leave your current company?"

> "I've learned a lot about real-time systems there. But the team is small, and I want to work with more senior engineers and bigger systems. I'm looking for growth, not running away from problems."

---

## 6. Phát âm — 40 từ IT hay đọc sai (CHỮ IN HOA = trọng âm)

| Từ | Đọc | Lỗi thường gặp |
|---|---|---|
| cache | /kæʃ/ "kát-sh" | đọc "cát-chê" ❌ |
| queue | /kjuː/ "kiu" | đọc "kiu-ơ" ❌ |
| Redis | RED-is | |
| MySQL | my-ESS-queue-ELL | |
| SQL | ESS-queue-ELL hoặc "SEE-quel" | |
| data | DAY-ta | |
| database | DAY-ta-base | |
| latency | LAY-ten-see | |
| throughput | THROO-put | |
| concurrency | con-KUR-ren-see | |
| asynchronous | ay-SIN-kro-nus | |
| synchronous | SIN-kro-nus | |
| architecture | AR-ki-tek-chur | đọc "ar-chi" ❌ |
| algorithm | AL-go-ri-thm | |
| idempotent | eye-dem-POH-tent | |
| Kubernetes | koo-ber-NET-eez | |
| Kafka | KAHF-ka | |
| microservice | MY-cro-ser-vis | |
| protocol | PRO-to-col | |
| authentication | aw-then-ti-KAY-shun | |
| authorization | aw-thor-i-ZAY-shun | |
| distributed | dis-TRIB-yoo-ted | |
| consistency | con-SIS-ten-see | |
| scalable | SKAY-la-bul | |
| optimize | OP-ti-mize | |
| deploy | de-PLOY | |
| execute | EK-se-kyoot | |
| allocate | AL-lo-kate | |
| thread | /θred/ lưỡi giữa răng | đọc "tred" ❌ |
| method | METH-od, /θ/ | |
| hierarchy | HIGH-er-ar-key | |
| inheritance | in-HER-i-tance | |
| polymorphism | pol-ee-MOR-fiz-um | |
| encapsulation | en-cap-soo-LAY-shun | |
| abstraction | ab-STRAK-shun | |
| interface | IN-ter-face | |
| deadlock | DED-lock | |
| bottleneck | BOT-tle-neck | |
| leaderboard | LEE-der-board | |
| matchmaking | MATCH-may-king | |

**Âm cuối** — lỗi lớn nhất của người Việt: nói rõ **-s, -ed, -t, -k** ở cuối từ: *request**s***, *use**d***, *cach**ed***, *threa**d***.
Tra phát âm chuẩn: [youglish.com](https://youglish.com) (nghe người bản xứ nói từ đó trong video thật).

---

## 7. Drill 45 phút/ngày (thay cho đọc tài liệu thụ động)

| Phút | Hoạt động | Cách làm |
|---|---|---|
| 0–10 | **Shadowing** | 1 đoạn 1–2 phút từ video tech tiếng Anh (ví dụ: ByteByteGo, Hussein Nasser). Nghe 1 câu → dừng → nói lại y hệt ngữ điệu. |
| 10–25 | **Talk-out-loud 1 chủ đề** | Chọn 1 câu hỏi từ `12-Mock-Interview-English/` → trả lời theo khung D-E-E-T → **ghi âm điện thoại**. |
| 25–35 | **Nghe lại + sửa** | Nghe bản ghi. Ghi 3 lỗi (từ sai, câu ngập ngừng, thiếu ý). Nói lại lần 2, ghi âm lại. |
| 35–45 | **Ôn 10 cụm "keo dán" + 5 từ phát âm** | Từ Phần 2 và Phần 6. Dùng Anki hoặc giấy. |

**Mỗi Chủ nhật:** 1 buổi mock 45–60 phút với người thật (bạn bè, đồng nghiệp, hoặc nền tảng luyện nói), hoặc với AI voice mode: dán prompt ở Phần 8.

**Đo tiến bộ (ghi vào `Plan/weekly-tracker.md`):**
- Số giây im lặng dài nhất trong bản ghi (mục tiêu < 5s)
- Số câu hỏi trả lời trọn khung D-E-E-T không cần nhìn
- Tự chấm 1–5: độ rõ ràng, độ tự tin

---

## 8. Prompt luyện phỏng vấn với AI (voice mode)

```
You are a technical interviewer at a large tech company in Vietnam.
You are interviewing me for a Middle Java Backend Developer role.
My background: ~3 years backend, mostly C++ game servers, plus Java/Spring Boot.
My English is intermediate. Speak clearly and at a normal speed.

Rules:
- Ask ONE question at a time. Wait for my answer.
- Mix: 2 HR questions, 4 Java/Spring questions, 2 database questions,
  1 Redis/Kafka question, 1 small system design question.
- If my answer is shallow, ask ONE follow-up, like a real interviewer.
- After all 10 questions, give feedback:
  1) technical gaps, 2) English mistakes I repeated (with corrected sentences),
  3) one thing I did well, 4) a score from 1 to 10 for a Middle level.
```

---

## 9. Checklist trước ngày phỏng vấn

- [ ] Nói được bản giới thiệu 90 giây mà không nhìn, 3 lần liên tiếp
- [ ] Có 4 câu chuyện STAR sẵn sàng: bug khó nhất, tối ưu hiệu năng, bất đồng trong team, sai lầm + bài học (xem `10-Interview-Prep/star-stories.md`)
- [ ] Thuộc 10 câu cứu nguy ở Phần 3
- [ ] Chuẩn bị 3 câu hỏi ngược cho interviewer:
  - "What does a typical week look like for a middle engineer on this team?"
  - "What is the biggest technical challenge the team is facing now?"
  - "How do you do code review and on-call?"
- [ ] Phỏng vấn online: kiểm tra mic, tai nghe, ánh sáng; để sẵn giấy và bút
