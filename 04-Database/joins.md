# 🔗 SQL Joins — Các Phép Nối Dữ Liệu Trong RDBMS

> **Phase:** 1 | **Time Block:** 05:00-06:00 Sáng  
> **Quan trọng cho:** Mọi Backend Engineer. Phép JOIN là cốt lõi của cơ sở dữ liệu quan hệ, nhưng hiểu cơ chế chạy vật lý bên dưới của DB Engine mới là điểm phân biệt giữa Junior và Senior.

---

## 1. Phân Loại JOIN (Logical Joins)

Về mặt logic, JOIN dùng để kết hợp các dòng từ hai hay nhiều bảng dựa trên một cột liên quan giữa chúng.

```
Bảng A (Trái)                  Bảng B (Phải)
┌─────┬───────┐                ┌─────┬────────┐
│ id  │ name  │                │ id  │ amount │
├─────┼───────┤                ├─────┼────────┤
│ 1   │ Alice │                │ 1   │ $100   │
│ 2   │ Bob   │                │ 3   │ $300   │
└─────┴───────┘                └─────┴────────┘
```

1.  **INNER JOIN**: Chỉ trả về các bản ghi có giá trị khớp ở cả hai bảng.
    *   *Kết quả:* `(1, Alice, $100)`. Dòng `2` (Bob) và `3` ($300) bị loại vì không khớp.
2.  **LEFT (OUTER) JOIN**: Trả về tất cả các bản ghi từ bảng bên trái, và các bản ghi khớp từ bảng bên phải. Nếu không khớp ở bảng phải, các cột của bảng phải sẽ nhận giá trị `NULL`.
    *   *Kết quả:* `(1, Alice, $100)`, `(2, Bob, NULL)`.
3.  **RIGHT (OUTER) JOIN**: Ngược lại với LEFT JOIN, trả về tất cả bản ghi bảng bên phải và các dòng khớp ở bảng trái.
    *   *Kết quả:* `(1, Alice, $100)`, `(3, NULL, $300)`.
    *   *Lưu ý:* Trong thực tế dự án, lập trình viên thường quy về dùng LEFT JOIN và hoán đổi vị trí bảng để dễ đọc code hơn là dùng RIGHT JOIN.
4.  **FULL (OUTER) JOIN**: Trả về tất cả bản ghi khi có kết quả khớp ở bảng trái HOẶC bảng phải. Nếu không khớp, điền `NULL` vào bên thiếu.
    *   *Kết quả:* `(1, Alice, $100)`, `(2, Bob, NULL)`, `(3, NULL, $300)`.
5.  **CROSS JOIN (Tích Descartes)**: Kết hợp từng dòng của bảng trái với mọi dòng của bảng phải. Nếu bảng A có $M$ dòng, bảng B có $N$ dòng, kết quả sẽ có $M \times N$ dòng.
    *   *Cú pháp:* `SELECT * FROM A CROSS JOIN B;` (Không có từ khóa `ON`).
6.  **SELF-JOIN**: Bản chất không phải là một loại join mới, mà là việc nối một bảng với chính nó. Thường dùng cho dữ liệu phân cấp đơn giản (ví dụ bảng `employees` có cột `manager_id` trỏ về `employee_id` của chính bảng đó).

---

## 2. Cơ Chế Thực Thi Vật Lý (Physical Join Operators)

Khi nhận câu lệnh JOIN, Optimizer của Database (PostgreSQL/MySQL) sẽ phân tích kích thước bảng, index và tài nguyên RAM khả dụng để chọn một trong ba chiến thuật thực thi vật lý dưới đây. Đây là phần **cực kỳ quan trọng** khi phỏng vấn Tier 1.

### 2.1. Nested Loop Join (Vòng lặp lồng nhau)

#### Cơ chế hoạt động:
DB Engine chạy hai vòng lặp lồng nhau giống như lập trình cơ bản:
```java
// Giả lập logic của Nested Loop Join bằng Java
for (Row rowOuter : tableOuter) {
    for (Row rowInner : tableInner) {
        if (rowOuter.getJoinKey().equals(rowInner.getJoinKey())) {
            output(rowOuter, rowInner);
        }
    }
}
```

#### Độ phức tạp thuật toán:
*   Nếu không có index trên bảng trong: $\mathcal{O}(M \times N)$ (quét toàn bộ - cực kỳ chậm).
*   Nếu bảng trong có Index trên cột JOIN (Index Nested Loop): $\mathcal{O}(M \times \log N)$ (chỉ cần quét bảng ngoài, mỗi dòng bảng ngoài sẽ tìm kiếm nhị phân cực nhanh trên index bảng trong).

#### Khi nào Optimizer chọn?
*   Khi bảng ngoài (Outer table) có kích thước rất nhỏ (vài chục đến vài trăm dòng).
*   Khi bảng trong (Inner table) có kích thước lớn nhưng có **Index** hỗ trợ trên cột JOIN.

---

### 2.2. Hash Join (Nối bằng bảng băm)

#### Cơ chế hoạt động:
Gồm 2 giai đoạn chính:
1.  **Build Phase (Xây dựng)**: DB quét qua bảng nhỏ hơn, băm các giá trị của cột JOIN bằng hàm băm và lưu vào một bảng băm (Hash Table) trong bộ nhớ RAM (`work_mem`).
2.  **Probe Phase (Dò tìm)**: DB quét bảng lớn hơn, lấy giá trị cột JOIN của từng dòng chạy qua hàm băm để tìm đối chiếu trực tiếp vào bảng băm RAM. Nếu khớp, trả ra kết quả.

```
Build Phase (Bảng nhỏ):
[Key: User_1] -> Hash(User_1) -> Hash Table RAM [Slot 42: User_1]

Probe Phase (Bảng lớn):
[Key: User_1] -> Hash(User_1) -> Check RAM Slot 42 -> Match!
```

#### Độ phức tạp thuật toán:
*   Thời gian: $\mathcal{O}(M + N)$ (chỉ cần quét qua mỗi bảng đúng 1 lần).
*   Không gian: $\mathcal{O}(M)$ (tốn RAM để lưu bảng băm của bảng nhỏ).

#### Khi nào Optimizer chọn?
*   Khi cả hai bảng đều có kích thước lớn.
*   Không có index trên cột JOIN.
*   Bảng băm của bảng nhỏ vừa vặn trong bộ nhớ RAM được cấp phát (`work_mem`). Nếu vượt quá RAM, DB sẽ phải thực hiện "Grace Hash Join" (chia nhỏ ghi xuống đĩa) làm giảm tốc độ rõ rệt.

---

### 2.3. Sort-Merge Join (Nối trộn)

#### Cơ chế hoạt động:
Gồm 2 giai đoạn chính:
1.  **Sort Phase (Sắp xếp)**: Sắp xếp cả hai bảng theo thứ tự tăng dần của cột JOIN (nếu dữ liệu chưa được sắp xếp sẵn bởi Index).
2.  **Merge Phase (Trộn)**: Duyệt song song hai con trỏ trên hai bảng đã sắp xếp để ghép các dòng khớp với nhau. Nếu con trỏ bảng A nhỏ hơn con trỏ bảng B, dịch con trỏ A lên, và ngược lại.

```
Bảng A (đã sort): [1, 2, 4, 5] (Con trỏ A trỏ vào 1)
Bảng B (đã sort): [1, 3, 5]    (Con trỏ B trỏ vào 1)
-> Khớp 1. Dịch cả hai.
A trỏ 2, B trỏ 3. Do 2 < 3 -> dịch A lên 4.
A trỏ 4, B trỏ 3. Do 4 > 3 -> dịch B lên 5.
A trỏ 4, B trỏ 5. Do 4 < 5 -> dịch A lên 5.
-> Khớp 5.
```

#### Độ phức tạp thuật toán:
*   Thời gian: $\mathcal{O}(M \log M + N \log N)$ (chủ yếu là chi phí sắp xếp dữ liệu).
*   Nếu dữ liệu đã được sắp xếp sẵn bằng index: $\mathcal{O}(M + N)$.

#### Khi nào Optimizer chọn?
*   Khi cả hai bảng đều cực kỳ lớn, không thể chứa bảng băm trong RAM.
*   Khi cột JOIN của cả hai bảng đều đã có **Index** hỗ trợ sắp xếp sẵn (tiết kiệm được Sort Phase).
*   Thường dùng cho các phép so sánh khoảng trong JOIN (ví dụ: `A.date BETWEEN B.start_date AND B.end_date`).

---

## 3. Tổng Hợp So Sánh Cơ Chế JOIN

| Cơ chế JOIN | Độ phức tạp thời gian | Độ phức tạp bộ nhớ | Điều kiện tối ưu |
|:---|:---|:---|:---|
| **Nested Loop** | $\mathcal{O}(M \log N)$ | $\mathcal{O}(1)$ | Bảng ngoài nhỏ, bảng trong có index cột JOIN |
| **Hash Join** | $\mathcal{O}(M + N)$ | $\mathcal{O}(M)$ | Hai bảng lớn, không có index, vừa RAM `work_mem` |
| **Merge Join** | $\mathcal{O}(M + N)$ *(nếu đã sort)* | $\mathcal{O}(1)$ | Hai bảng cực lớn, đã sắp xếp sẵn bởi Index |

---

## 4. Ghi Chú Phóng Vấn (Interview Q&A)

### Q1: Bạn có một câu lệnh JOIN hai bảng lớn và chạy rất chậm. Bạn sẽ tối ưu hóa như thế nào?
**Trả lời**:
Tôi sẽ tiếp cận theo các bước sau để cô lập và xử lý vấn đề:
1.  **Chạy EXPLAIN ANALYZE**: Xem kế hoạch thực thi thực tế để tìm điểm nghẽn. Xem DB đang sử dụng cơ chế JOIN nào (`Seq Scan`, `Hash Join` ghi xuống đĩa, hay `Nested Loop` không index).
2.  **Đánh Index cột JOIN**: Đảm bảo các cột dùng trong mệnh đề `ON` được đánh chỉ mục (thường là Khóa ngoại trỏ sang Khóa chính). Điều này giúp chuyển `Nested Loop` không index thành `Index Nested Loop` hoặc giúp `Merge Join` bỏ qua bước Sort.
3.  **Kiểm tra tính tương đồng kiểu dữ liệu (Data Type Mismatch)**: Đảm bảo cột JOIN ở hai bảng có cùng kiểu dữ liệu chính xác (ví dụ cả hai là `BIGINT` hoặc `VARCHAR(50)`). Nếu một bên là `INT` và một bên là `BIGINT`, DB sẽ phải ép kiểu ngầm (Implicit Casting), làm vô hiệu hóa index và ép chạy `Seq Scan`.
4.  **Tăng bộ nhớ `work_mem` (Postgres)**: Nếu EXPLAIN cho thấy `Hash Join` bị tràn bộ nhớ và phải ghi dữ liệu tạm xuống đĩa (Disk spill), tôi sẽ tăng tham số `work_mem` cho session đó để bảng băm được dựng hoàn toàn trong RAM.
5.  **Lọc dữ liệu sớm (Filtering early)**: Đảm bảo các điều kiện lọc `WHERE` được thực hiện trước khi JOIN (Postgres tự động làm điều này qua push-down filter, nhưng viết SQL rõ ràng vẫn tốt hơn).

### Q2: Tại sao chúng ta nên tránh viết các câu lệnh JOIN quá nhiều bảng (ví dụ 10+ bảng) trong hệ thống OLTP?
**Trả lời**:
*   **Chi phí tối ưu hóa (Planning Time)**: Số lượng cách kết hợp JOIN tăng theo hàm giai thừa của số lượng bảng. Khi join 10 bảng, Optimizer phải tính toán hàng triệu phương án sắp đặt thứ tự JOIN để chọn ra phương án rẻ nhất. Quá trình này tiêu tốn rất nhiều CPU và thời gian lập kế hoạch (Planning Time) của DB, đôi khi thời gian planning còn lâu hơn thời gian chạy thật.
*   **Khóa dữ liệu (Locking & Contention)**: Trong hệ thống OLTP ghi nhiều, join nhiều bảng yêu cầu giữ read lock trên nhiều tài nguyên đồng thời, dễ dẫn đến hiện tượng nghẽn luồng hoặc Deadlock.
*   **Giải pháp**: Tách câu truy vấn lớn thành nhiều truy vấn nhỏ, denormalize dữ liệu hợp lý ở những bảng đọc nhiều, hoặc sử dụng cơ chế Caching (Redis) để giảm tải truy vấn phức tạp xuống Database chính.
