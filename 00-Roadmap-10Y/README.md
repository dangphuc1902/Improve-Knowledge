# 🧭 Lộ trình 10 năm (11/2026 → 2036): Plan A + Plan B

> **Chủ sở hữu:** Đặng Trọng Phúc · **Lập:** 23/10/2026 · **Xem lại:** mỗi Chủ nhật (tuần), ngày cuối tháng (tháng), tuần cuối quý (quý), tuần cuối tháng 12 (năm)
> **Định vị dài hạn:** *Distributed Systems Engineer xây được AI systems.* Giữ lợi thế C++/realtime/gRPC/Redis; dùng AI làm đòn bẩy, không chuyển hẳn sang ML research.

---

## 1. Cách đọc thư mục này

Plan càng xa thì càng thô. Kế hoạch theo ngày cho năm 2032 sẽ sai ngay khi tới nơi; vì vậy độ chi tiết giảm dần theo thời gian:

| Khoảng thời gian | Độ chi tiết | File |
|---|---|---|
| 20/10 → 14/12/2026 | **Từng ngày, từng block giờ** (đã có) | [`Plan/01-detailed-sessions.md`](../Plan/01-detailed-sessions.md) |
| 15/12/2026 → 31/12/2026 | **Từng ngày** | [`2026.md`](./2026.md) |
| 01/01 → 07/02/2027 (nhảy việc) | **Từng ngày** | [`2027.md`](./2027.md) §1 |
| 08/02 → 30/06/2027 (bàn giao + 90 ngày đầu ở công ty mới) | **Từng tuần** | [`2027.md`](./2027.md) §2–3 |
| 07 → 12/2027 | **Từng tháng**, mục tiêu tuần | [`2027.md`](./2027.md) §4 |
| 2028 → 2029 | **Từng tháng** + OKR quý | [`2028.md`](./2028.md), [`2029.md`](./2029.md) |
| 2030 → 2036 | **Từng quý** + mục tiêu tháng chính | [`2030.md`](./2030.md) … [`2036.md`](./2036.md) |
| Mọi ngày | **Mẫu lịch ngày** theo từng giai đoạn sống | [`daily-templates.md`](./daily-templates.md) |
| Mọi tuần/tháng/quý/năm | **Nghi thức review + bảng KPI** | [`review-and-kpi.md`](./review-and-kpi.md) |

**Quy tắc làm mịn dần:** mỗi tháng 12, viết lại năm sau từ mức "tháng" xuống "tuần"; ngày cuối mỗi tháng, viết tháng sau xuống "ngày" bằng mẫu trong `daily-templates.md`.

---

## 2. Bức tranh 10 năm

```
            2026-27            2028-29              2030-31            2032-33              2034-36
PLAN A  │ Junior → MIDDLE │ MIDDLE → SENIOR │ TECH LEAD        │ STAFF / EM        │ PRINCIPAL / ARCHITECT
        │ nhảy việc T3/27 │ AI-native dev   │ chọn 1 nhánh sâu │ ảnh hưởng tổ chức │ / FRACTIONAL CTO
────────┼─────────────────┼─────────────────┼──────────────────┼───────────────────┼──────────────────────
PLAN B  │ B1: DÒNG TIỀN   │ B2: SẢN PHẨM HÓA│ B3: SCALE        │ B3→B4             │ B4: TÀI SẢN
        │ ĐẦU TIÊN        │ micro-SaaS, MRR │ hệ thống hóa     │ Plan B ≥ lương A  │ danh mục SaaS / agency
────────┴─────────────────┴─────────────────┴──────────────────┴───────────────────┴──────────────────────
CỔNG    │ Offer Middle    │ Title Senior    │ Lead 3–5 người   │ B ≥ 1.5× lương    │ chọn: IC / CTO /
        │ trước 07/02/27  │ cuối 2028       │ + 1 nguồn USD    │ 12 tháng?         │ toàn thời gian Plan B
```

### Plan A — Sự nghiệp chính

| Giai đoạn | Thời gian | Mục tiêu chính | Kết quả bắt buộc (KPI cuối giai đoạn) |
|---|---|---|---|
| A0 | 10/2026 – 03/2027 | **Nhảy việc lên Middle** tại công ty lớn | Offer Middle Java Backend; phỏng vấn được bằng tiếng Anh |
| A1 | 03/2027 – 12/2028 | **Senior, dev AI-native** | Title Senior; 3 design doc có số liệu; 1 dự án AI chạy production; CKA hoặc AWS SAA |
| A2 | 2029 – 2030 | **Chọn nhánh sâu, Tech Lead** | Lead 3–5 người; ≥ 1 nguồn thu USD (remote/contract) |
| A3 | 2031 – 2033 | **Staff/Principal hoặc Engineering Manager** | Quyết định kiến trúc cấp tổ chức; uy tín ngoài công ty (talk, blog, YouTube) |
| A4 | 2034 – 2036 | **Principal / Architect / Fractional CTO** | Thu nhập chính không phụ thuộc 1 công ty |

### Plan B — Cỗ máy thu nhập ngoài giờ

| Giai đoạn | Thời gian | Mục tiêu chính | KPI cuối giai đoạn |
|---|---|---|---|
| B0 | 10/2026 – 03/2027 | **Giữ nhịp tối thiểu** (ưu tiên nhảy việc) | Gumroad pack live; ≤ 3h/tuần |
| B1 | 04/2027 – 12/2027 | **Dòng tiền đầu tiên** | 1 case study có số liệu; 3–5 khách retainer *hoặc* doanh thu B = 20–30% lương; email list ≥ 300 |
| B2 | 2028 – 2029 | **Sản phẩm hóa** | micro-SaaS có MRR; MRR ≥ 50% doanh thu B; B ≈ 50% lương |
| B3 | 2030 – 2032 | **Scale, giảm phụ thuộc bản thân** | Plan B ≥ lương Plan A trong 6 tháng liên tục |
| B4 | 2033 – 2036 | **Tài sản** | Danh mục sản phẩm; có thể bán bớt hoặc tái đầu tư |

> **Điều chỉnh so với bản kế hoạch 10 năm gốc:** Q4/2026 – Q1/2027 dồn sức cho nhảy việc (A0), nên Plan B lùi lại 1 quý (B0 = giữ nhịp). Trong bản gốc, Q4/2027 là "phỏng vấn lên Senior"; vì đã nhảy việc lên Middle vào 03/2027, mục tiêu đó chuyển thành **review hiệu suất tốt tại công ty mới**, Senior vào cuối 2028.

---

## 3. Cổng quyết định (Decision gates)

Không đổi hướng theo cảm xúc. Chỉ đổi khi dữ liệu thỏa điều kiện:

| Cổng | Khi nào xét | Điều kiện | Nếu ĐẠT | Nếu KHÔNG ĐẠT |
|---|---|---|---|---|
| G1 — Offer Middle | 07/02/2027 | Có offer Middle, lương ≥ mức hiện tại + 30% | Báo nghỉ sau Tết, bắt đầu 03/2027 | Tiếp tục phỏng vấn đến 30/04/2027; ở lại không phải thất bại |
| G2 — Plan B có thật | 31/12/2027 | ≥ 1 khách trả tiền lặp lại *hoặc* ≥ 50 đơn Gumroad | Tăng Plan B lên 15h/tuần trong 2028 | Đổi kênh/ngách 1 lần; nếu 06/2028 vẫn 0 → B chuyển sang nội dung + freelance |
| G3 — Senior | 31/12/2028 | Title Senior hoặc offer Senior | Bắt đầu chọn nhánh sâu (A2) | Nhảy việc có chủ đích trong H1/2029 |
| G4 — Nguồn USD | 31/12/2030 | ≥ 1 nguồn thu USD đều đặn ≥ 6 tháng | Cân nhắc remote toàn thời gian | Ưu tiên tiếng Anh nói + network quốc tế 2031 |
| **G5 — Nghỉ việc chính?** | Bất kỳ lúc nào từ 2031 | **Cả 3:** B ≥ 1.5× lương trong 12 tháng liên tục; ≥ 70% doanh thu định kỳ; quỹ dự phòng ≥ 12 tháng chi tiêu | Có thể chuyển toàn thời gian Plan B | Giữ việc chính, B tiếp tục bán thời gian |
| G6 — Hướng 2034+ | 12/2033 | Tự đánh giá: thích IC kỹ thuật sâu hay quản lý/kinh doanh hơn? | Chọn A4 phù hợp | — |

---

## 4. Ngân sách thời gian theo giai đoạn

| Giai đoạn | Plan A (học/phát triển ngoài giờ) | Plan B | Nghỉ hoàn toàn |
|---|---|---|---|
| 10/2026 – 02/2027 (nhảy việc) | 25–35h/tuần | ≤ 3h/tuần | Chiều Chủ nhật |
| 03 – 06/2027 (90 ngày đầu việc mới) | 5h/tuần (onboarding là ưu tiên) | 5–8h/tuần | Cả Chủ nhật |
| 07/2027 – 2029 | 5h/tuần | 12–14h/tuần | Cả Chủ nhật |
| 2030 – 2031 | ~5h/tuần + networking | 11–15h/tuần (thuê người để giảm dần) | Cả Chủ nhật + 2 tuần nghỉ/năm |
| 2032+ | ~5h/tuần | ≤ 12h/tuần (B chạy bằng hệ thống + người) | Cả Chủ nhật + 2 tuần nghỉ/năm |

**Giới hạn cứng:** tổng Plan B > 25h/tuần trong 4 tuần liên tiếp → giảm ngay. Kiệt sức làm hỏng cả A lẫn B.

---

## 5. Kỹ năng theo thời gian

| Năm | Plan A — học | Plan B — học |
|---|---|---|
| 2026–27 | Tiếng Anh phỏng vấn; Java/Spring/DB sâu; system design cơ bản | Viết landing page, outreach email tiếng Anh |
| 2027–28 | Kubernetes + observability (CKA/AWS SAA); LLM engineering (RAG, tool calling, evals) | Cold outreach, discovery call; Stripe; copywriting |
| 2028–29 | System design nâng cao (consistency, sharding, Kafka exactly-once); mentoring | TypeScript + Next.js đủ cho SaaS; SEO; YouTube analytics |
| 2030–31 | Kiến trúc nhánh đã chọn (AI Platform / game realtime / fintech); quản lý dự án kỹ thuật | Product management; thuê và quản lý freelancer |
| 2032+ | Chiến lược kỹ thuật, ngân sách, tuyển dụng | Tài chính doanh nghiệp, M&A sản phẩm nhỏ, pháp lý |
| **Luôn luôn** | Đọc hợp đồng lao động (non-compete, IP, làm thêm); không dùng giờ/máy/tài khoản công ty cho Plan B | Thuế thu nhập từ nước ngoài; TCPA/HIPAA khi chạm dữ liệu khách Mỹ |
