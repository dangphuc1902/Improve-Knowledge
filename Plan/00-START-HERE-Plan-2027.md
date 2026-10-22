# 🧭 START HERE — Kế hoạch nhảy việc Middle Backend đầu 2027

> **Cập nhật:** 22/10/2026 · **Mục tiêu:** offer Middle Backend (Java) tại công ty lớn, phỏng vấn tiếng Anh, trước hoặc ngay sau Tết Đinh Mùi (06/02/2027).
> **Đây là file kế hoạch chính.** Các file khác trong `Plan/` là chi tiết hoặc tham khảo (xem cuối file).

---

## 1. Ba điều thay đổi so với các kế hoạch trước

1. **Mốc thật là đầu 2027, không phải 16/11 hay 14/12.** Ngày 14/12 = kết thúc sprint kiến thức (giữ nguyên `00-master-plan.md`). Sau đó là giai đoạn **nộp đơn + phỏng vấn thật** đến Tết.
2. **Tiếng Anh nói là nút thắt số 1**, không phải kiến thức. Ngày nào cũng có ít nhất 45 phút **nói to + ghi âm** — theo [`00-English-Survival-Kit.md`](../11-English-Mock-Interviews/00-English-Survival-Kit.md). Không thay bằng đọc tài liệu.
3. **Có "ngày tối thiểu".** Lịch 4:30 sáng + ~36–40h/tuần trong `00-master-plan.md` rất tham vọng khi vẫn đi làm cả thứ Bảy lẻ. Khi mệt/OT, chạy **Ngày tối thiểu (90 phút)** thay vì bỏ hẳn. Chuỗi không đứt quan trọng hơn ngày hoàn hảo.

---

## 2. Lộ trình 6 giai đoạn (26/10/2026 → 07/02/2027)

| # | Thời gian | Tập trung | Đầu ra bắt buộc (exit criteria) |
|---|---|---|---|
| 1 | 26/10 → 08/11 | **Nền móng + tiếng Anh sinh tồn** | Nói bản giới thiệu 90s không nhìn; thuộc 3 khung trả lời; 15 bài LeetCode Easy |
| 2 | 09/11 → 29/11 | **Java Core + Spring + Database sâu** | Trả lời được mục Java/Spring/DB trong checklist Middle bằng tiếng Anh; 25 bài Medium |
| 3 | 30/11 → 14/12 | **Distributed + System Design + FPM** | 3 bài design nói trong 20 phút; FPM có test + idempotency + README kiến trúc |
| 4 | 15/12 → 31/12 | **Phỏng vấn khởi động + sửa CV** | CV tiếng Anh đã rà 4 câu/bullet; nộp 3–5 công ty "khởi động"; ≥ 2 buổi phỏng vấn thật |
| 5 | 01/01 → 24/01 | **Đợt nộp chính** | Nộp 8–12 công ty mục tiêu; 1 mock tiếng Anh/tuần; ghi lại mọi câu hỏi bị hỏi |
| 6 | 25/01 → 07/02 | **Vòng cuối + offer** | So sánh offer; thương lượng lương; chọn ngày bắt đầu (cân nhắc thưởng Tết) |

Chi tiết từng tuần của giai đoạn 1–3 → [`00-master-plan.md`](./00-master-plan.md) (tuần 2–8).
Checklist chủ đề Middle → [`Positioning-Junior-Game-to-Middle-Backend.md`](../10-Interview-Prep/Positioning-Junior-Game-to-Middle-Backend.md#5-danh-sách-chủ-đề-tối-thiểu-cho-middle-đánh-dấu-khi-nói-được-bằng-tiếng-anh-2-phút).

---

## 3. Lịch ngày

### Ngày đầy đủ (~3–4.5h) — theo `00-master-plan.md`
- Sáng: 45–60 phút **English drill** (Survival Kit, Phần 7) → phần còn lại: kiến thức sâu.
- Tối: DSA 60–90 phút → 20–30 phút **giải thích lời giải bằng tiếng Anh, ghi âm**.

### Ngày tối thiểu (90 phút) — khi OT, mệt, đi làm thứ Bảy
| Phút | Việc |
|---|---|
| 30 | English drill rút gọn: 10' shadowing + 20' talk-out-loud 1 câu hỏi (ghi âm) |
| 45 | 1 bài LeetCode **hoặc** 1 chủ đề kỹ thuật (active recall, không đọc lại) |
| 15 | Cập nhật `weekly-tracker.md`: làm gì, bí ở đâu |

### Chủ nhật (cố định)
- 60 phút **mock interview tiếng Anh** (người thật hoặc AI voice — prompt ở Survival Kit, Phần 8)
- 30 phút review tuần + lên kế hoạch tuần sau
- Còn lại: **nghỉ**. Kiệt sức tháng 12 = trượt phỏng vấn tháng 1.

---

## 4. Thứ tự học trong repo

| Ưu tiên | Dùng file | Ghi chú |
|---|---|---|
| 1 | `11-English-Mock-Interviews/00-English-Survival-Kit.md` | Học trước tiên, ôn mỗi ngày |
| 2 | `10-Interview-Prep/Positioning-Junior-Game-to-Middle-Backend.md` | Định vị + checklist + rà CV |
| 3 | `12-Mock-Interview-English/` (01 → 17) | **Bộ câu hỏi chính** để luyện nói (tiếng Anh) |
| 4 | `02` → `09` (Java, Spring, DB, System Design, Distributed, Cloud, Network, Patterns) | Đọc sâu khi trả lời ở bước 3 còn nông |
| 5 | `01-DSA/` + `leetcode-tracker.md` | Theo pattern, không theo số lượng |
| 6 | `10-Interview-Prep/MoMo-Prep/`, `EPAM-*`, `study_guide_vpbank.md` | Chỉ khi có lịch phỏng vấn công ty đó |
| — | `10-Interview-Prep/Part-xx` | Trùng nội dung với `12-Mock-Interview-English/` — dùng 1 trong 2, không học cả hai |
| — | `_archive/` | Không học. Chỉ tra cứu |

---

## 5. KPI theo dõi mỗi Chủ nhật (ghi vào `weekly-tracker.md`)

| Chỉ số | Mục tiêu cuối GĐ 3 (14/12) | Mục tiêu cuối GĐ 5 (24/01) |
|---|---|---|
| Câu hỏi trả lời được bằng TA, không nhìn (từ bộ 12-xx) | ≥ 60 | ≥ 100 |
| Im lặng dài nhất trong bản ghi | < 8 giây | < 5 giây |
| LeetCode (Easy/Medium) | 40 | 70 |
| Bài system design nói trọn 20 phút | 3 | 5 |
| Mock interview tiếng Anh đã làm | 7 | 12 |
| Phỏng vấn thật đã tham gia | 0 | ≥ 6 |

---

## 6. Các kế hoạch khác trong `Plan/`

| File | Vai trò hiện tại |
|---|---|
| `00-master-plan.md` | Chi tiết tuần cho giai đoạn 1–3 (đến 14/12) |
| `01-detailed-sessions.md` | Chi tiết từng buổi học |
| `english-6month-plan.md` | Lộ trình tiếng Anh dài hạn (đến 02/2027) — dùng song song với Survival Kit |
| `INTERVIEW_COMEBACK_PLAN.md` | Tham khảo: audit workspace + điểm mạnh/gap CV. Mốc 16/11 → giờ là "buổi mock đầu tiên" |
| `SPRINT-INTERVIEW-ROADMAP.md`, `JD-JAVA-BACKEND-ROADMAP.md` | Sprint cấp tốc khi có lịch phỏng vấn cho 1 JD cụ thể |
| `company-research.md` | Danh sách công ty mục tiêu — cập nhật trước giai đoạn 4 |
| `weekly-tracker.md`, `leetcode-tracker.md` | Theo dõi tiến độ |
