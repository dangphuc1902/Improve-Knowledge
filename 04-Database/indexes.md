# 🗂️ Database Indexes — Chỉ Mục Cơ Sở Dữ Liệu

> **Phase:** 1 | **Time Block:** 05:00-06:00 Sáng  
> **Quan trọng cho:** ALL Tier 1 & Tier 2. Hiểu cấu trúc dữ liệu của Index và cách DB Engine vận hành nó là chìa khóa để giải quyết 90% bài toán nghẽn hiệu năng (performance bottleneck) ở database layer.

---

## 1. Bản Chất của Index và Cấu Trúc B-Tree

### 1.1. Khái niệm cơ bản
Nếu không có index, khi tìm kiếm một bản ghi, DB Engine phải quét qua toàn bộ dữ liệu từ dòng đầu tiên đến dòng cuối cùng trên đĩa cứng (gọi là **Seq Scan - Quét tuần tự**). Kỹ thuật này có độ phức tạp thời gian là $\mathcal{O}(N)$.
Index là một cấu trúc dữ liệu phụ trợ giúp DB Engine tìm kiếm nhanh bản ghi giống như trang mục lục ở cuối cuốn sách, đưa độ phức tạp về $\mathcal{O}(\log N)$.

---

### 1.2. Cấu trúc cây B-Tree (Balanced Tree)
Hầu hết các database mặc định sử dụng cây **B-Tree** (hoặc biến thể **B+ Tree**) làm cấu trúc dữ liệu cho Index.

```
                  ┌──────────────┐
                  │  Root Node   │ (Nút gốc - Luôn nằm trên RAM)
                  └──────┬───────┘
                         │
            ┌────────────┴────────────┐
     ┌──────▼───────┐          ┌──────▼───────┐
     │ Branch Node  │          │ Branch Node  │ (Nút nhánh - Phân cấp tìm kiếm)
     └──────┬───────┘          └──────┬───────┘
            │                         │
     ┌──────┴──────┐           ┌──────┴──────┐
┌────▼────┐   ┌────▼────┐ ┌────▼────┐   ┌────▼────┐
│Leaf Node│ ◄─►Leaf Node│ │Leaf Node│ ◄─►Leaf Node│ (Nút lá - Chứa Key + Con trỏ TID)
└─────────┘   └─────────┘ └─────────┘   └─────────┘
  (Liên kết đôi - Doubly Linked List giữa các nút lá)
```

#### 🔍 Minh họa thực tế tìm kiếm Trái/Phải:
Giả sử ta tìm kiếm bản ghi có `id = 35` trên cây B-Tree:
*   **Root Node**: Chứa mốc giá trị `50`. DB so sánh `35 < 50` -> Quyết định đi về nhánh **bên trái** (trước).
*   **Branch Node**: Chứa hai mốc `20` và `40`. DB thấy `20 < 35 < 40` -> Quyết định đi vào nhánh **ở giữa** hai mốc này.
*   **Leaf Node (Nút lá)**: Đến được trang chứa các khóa `[30, 32, 35, 38]`. Các khóa này được sắp xếp tăng dần từ **trái sang phải**. DB duyệt từ trái qua phải để tìm khóa `35` (Tìm thấy!), lấy ra **TID** và đọc dòng dữ liệu gốc từ ổ đĩa.

#### Các thành phần của B-Tree Index:
1.  **Root Node (Nút gốc)**: Điểm xuất phát của mọi cuộc tìm kiếm. DB thường cache nút này trực tiếp trên RAM.
2.  **Branch Nodes (Nút nhánh/Nút trung gian)**: Chứa các khoảng giá trị và con trỏ dẫn đường để DB Engine quyết định rẽ nhánh trái hay phải.
3.  **Leaf Nodes (Nút lá)**:
    *   Chứa **Key** (giá trị của cột được đánh index) và **TID (Tuple ID)** (địa chỉ vật lý của dòng dữ liệu trên đĩa cứng).
    *   **Điểm mấu chốt**: Các nút lá liên kết với nhau bằng cấu trúc **Doubly Linked List (Danh sách liên kết đôi)**. Điều này giúp các truy vấn phạm vi (`>`, `<`, `BETWEEN`) cực kỳ nhanh. DB chỉ cần duyệt cây tìm phần tử đầu tiên, sau đó đi ngang qua các nút lá kế tiếp mà không cần duyệt cây lại từ đầu.

#### Độ cao của cây (Tree Height):
Cây B-Tree có cơ chế tự cân bằng (Balanced). Độ cao của cây thường rất thấp (chỉ khoảng 3 đến 4 tầng đối với bảng có hàng chục triệu dòng). Điều này đảm bảo DB chỉ cần tối đa 3-4 lần đọc đĩa (Disk I/O) là tìm thấy bản ghi.

---

## 2. So Sánh B-Tree Index vs Hash Index

| Đặc tính | B-Tree Index (Mặc định) | Hash Index |
|:---|:---|:---|
| **Cấu trúc dữ liệu** | Cây tự cân bằng (Balanced Tree) | Bảng băm (Hash Table) |
| **Độ phức tạp** | $\mathcal{O}(\log N)$ cho mọi thao tác | $\mathcal{O}(1)$ cho trường hợp tốt nhất |
| **Truy vấn bằng (`=`)** | ✅ Hỗ trợ cực tốt | ✅ Nhanh hơn B-Tree một chút |
| **Truy vấn khoảng (`>`, `<`, `BETWEEN`)** | ✅ Hỗ trợ tốt nhờ danh sách liên kết ở lá | ❌ Không hỗ trợ (phải Seq Scan) |
| **Sắp xếp (`ORDER BY`)** | ✅ Hỗ trợ tốt vì dữ liệu đã sort sẵn | ❌ Không hỗ trợ |
| **Tìm kiếm tiền tố (`LIKE 'abc%'`)** | ✅ Hỗ trợ nếu pattern ở dạng prefix | ❌ Không hỗ trợ |

---

## 3. Các Loại Quét Dữ Liệu của Index (Scan Types)

Khi đọc kết quả `EXPLAIN ANALYZE`, bạn cần hiểu rõ sự khác biệt của các hình thức scan dưới đây:

### 3.1. Seq Scan (Sequential Scan)
*   **Cơ chế**: Quét tuần tự toàn bộ bảng vật lý (Heap) để tìm các dòng thỏa mãn.
*   **Đánh giá**: Chậm nhất. Nên tránh trên các bảng lớn.

### 3.2. Index Scan
*   **Cơ chế**: Duyệt cây Index để tìm các khóa khớp, lấy ra Tuple ID (TID), sau đó nhảy xuống bảng vật lý (Heap) đọc trang dữ liệu (Page) để lấy các cột còn lại.
*   **Đánh giá**: Tối ưu khi câu truy vấn chỉ lấy ra một lượng nhỏ dòng dữ liệu (dưới 5-10% tổng số dòng của bảng).

### 3.3. Index Only Scan (Covering Index)
*   **Cơ chế**: DB chỉ đọc dữ liệu trên các trang của Index và trả về ngay cho ứng dụng, hoàn toàn không cần truy cập xuống bảng vật lý (Heap).
*   **Điều kiện**: Tất cả các cột xuất hiện trong câu lệnh `SELECT`, `WHERE`, `ORDER BY` đều phải nằm trong Index.
*   **Đánh giá**: Nhanh nhất vì giảm thiểu tối đa Disk I/O.
*   *Mẹo cấu hình:* Sử dụng từ khóa `INCLUDE` khi tạo index để biến nó thành covering index mà không cần đưa cột phụ vào cây phân loại:
    ```sql
    CREATE INDEX idx_user_email_covering ON users(email) INCLUDE (name, status);
    ```

### 3.4. Bitmap Index Scan & Bitmap Heap Scan
*   **Cơ chế**:
    1.  `Bitmap Index Scan`: Duyệt index và dựng một bản đồ bit (Bitmap) trong RAM. Mỗi bit đại diện cho một trang dữ liệu chứa dòng khớp điều kiện.
    2.  `Bitmap Heap Scan`: Dựa vào bitmap, DB quét các trang dữ liệu trên đĩa theo thứ tự tuần tự để lấy dữ liệu dòng.
*   **Đánh giá**: Tối ưu khi cần lấy lượng dữ liệu ở mức trung bình (10-30% bảng). Việc tạo bitmap giúp chuyển đổi việc đọc đĩa ngẫu nhiên (random read) thành đọc đĩa tuần tự (sequential read), tăng hiệu năng ổ cứng đáng kể.

---

## 4. Quy Tắc Tiền Tố Bên Trái (Leftmost Prefix Rule)

Khi tạo một **Composite Index** (chỉ mục tổng hợp nhiều cột) ví dụ: `CREATE INDEX idx_demo ON users (a, b, c);`, DB Engine sẽ sắp xếp cây index theo thứ tự ưu tiên từ **trái sang phải** (đứng trước sắp xếp trước, đứng sau sắp xếp sau):
1. Sắp xếp toàn bộ dữ liệu theo cột `a` (**trái ngoài cùng / đứng trước**).
2. Nếu có nhiều dòng trùng giá trị `a`, DB mới sắp xếp nhóm đó theo cột `b` (**ở giữa**).
3. Nếu tiếp tục trùng cả `a` và `b`, DB mới sắp xếp nhóm đó theo cột `c` (**phải ngoài cùng / đứng sau**).

---

### 📖 Ví Dụ Thực Tế: Cuốn Danh Bạ Điện Thoại
Để hiểu rõ nhất về quy tắc "trước - sau, trái - phải", hãy tưởng tượng Composite Index `(a, b, c)` giống như một **Cuốn danh bạ điện thoại** được sắp xếp theo thứ tự: **(Họ, Tên, Tên_Đệm)**.

*   **Cột `a` (Trái ngoài cùng / Đứng trước)** đại diện cho **Họ**.
*   **Cột `b` (Ở giữa)** đại diện cho **Tên**.
*   **Cột `c` (Phải ngoài cùng / Đứng sau)** đại diện cho **Tên Đệm**.

Dưới đây là cách DB Engine hành xử khi bạn tìm kiếm:

#### Trường hợp 1: Tìm theo `WHERE Họ = 'Nguyễn'` (`WHERE a = 'Nguyễn'`)
*   **Thao tác**: Bạn lật ngay đến phần chữ **N**, tìm phần **Họ Nguyễn**. Vì các họ Nguyễn nằm liền kề nhau, bạn tìm thấy ngay lập tức.
*   **Đánh giá**: **SỬ DỤNG ĐƯỢC INDEX** (tối ưu).

#### Trường hợp 2: Tìm theo `WHERE Họ = 'Nguyễn' AND Tên = 'Anh'` (`WHERE a = 'Nguyễn' AND b = 'Anh'`)
*   **Thao tác**: Bạn tìm đến phần **Họ Nguyễn**, sau đó trong nhóm họ Nguyễn, bạn tìm tiếp đến phần những người có **Tên bắt đầu bằng chữ A (Anh)**.
*   **Đánh giá**: **SỬ DỤNG ĐƯỢC INDEX** (tối ưu nhất, dùng cả 2 cột).

#### Trường hợp 3: Tìm theo `WHERE Tên = 'Anh'` (`WHERE b = 'Anh'`)
*   **Thao tác**: Bạn muốn tìm những người tên "Anh". Nhưng vì cuốn danh bạ sắp xếp theo Họ trước, những người tên "Anh" có thể mang họ Nguyễn, họ Trần, họ Lê... nằm rải rác khắp cuốn sách. Bạn bắt buộc phải **đọc từng dòng từ trang đầu đến trang cuối** cuốn danh bạ.
*   **Đánh giá**: **KHÔNG DÙNG ĐƯỢC INDEX** (Seq Scan - Quét toàn bộ bảng) do vi phạm quy tắc tiền tố bên trái (thiếu cột Họ đứng trước).

#### Trường hợp 4: Tìm theo `WHERE Họ = 'Nguyễn' AND Tên_Đệm = 'Văn'` (`WHERE a = 'Nguyễn' AND c = 'Văn'`)
*   **Thao tác**: Bạn lật đến phần **Họ Nguyễn** (dùng được index cột đầu). Nhưng trong nhóm họ Nguyễn, danh bạ lại xếp theo Tên rồi mới đến Tên Đệm. Vì bạn không biết **Tên** (cột `b`) là gì, bạn không thể nhảy nhanh đến phần tên đệm "Văn" (cột `c`) được. Bạn buộc phải quét thủ công tất cả những người họ Nguyễn để xem ai có tên đệm là "Văn".
*   **Đánh giá**: **CHỈ DÙNG ĐƯỢC INDEX MỘT PHẦN** (chỉ dùng cột `a` để lọc nhanh dòng có Họ Nguyễn, cột `c` bị bỏ qua không dùng index được và phải lọc thủ công bằng CPU).

---

### 📊 Bảng đối chiếu khả năng sử dụng Composite Index `(a, b, c)`:

| Câu lệnh WHERE | Index được sử dụng? | Giải thích chi tiết |
|:---|:---|:---|
| `WHERE a = 1` | ✅ **Có (Cột a)** | Khớp tiền tố bên trái ngoài cùng. |
| `WHERE a = 1 AND b = 2` | ✅ **Có (Cột a, b)** | Khớp liên tục từ trái qua phải. |
| `WHERE a = 1 AND b = 2 AND c = 3` | ✅ **Có (Cột a, b, c)** | Khớp toàn bộ index theo đúng thứ tự. |
| `WHERE a = 1 AND c = 3` | ⚠️ **Một phần (Chỉ cột a)** | Dùng cột `a` để tìm kiếm. Cột `c` không dùng được index vì thiếu cột `b` làm cầu nối ở giữa. |
| `WHERE b = 2` | ❌ **Không** | Vi phạm quy tắc tiền tố bên trái (thiếu cột `a` đứng trước). |
| `WHERE b = 2 AND c = 3` | ❌ **Không** | Vi phạm quy tắc tiền tố bên trái (thiếu cột `a` đứng trước). |

### 💡 Quy tắc thiết kế Composite Index:
1.  **Độ lọc cao đứng trước (High Cardinality)**: Cột nào chứa nhiều giá trị phân biệt hơn (ví dụ `user_id` so với `gender`) nên được ưu tiên đứng bên trái (đứng trước).
2.  **Phép bằng đứng trước, phép khoảng đứng sau**: Các cột dùng phép so sánh bằng (`=`) xếp bên trái, các cột so sánh khoảng (`>`, `<`, `BETWEEN`, `LIKE`) xếp bên phải ngoài cùng.
    *   *Tại sao?* Phép so sánh khoảng làm dừng việc duyệt index ở các cột phía sau. Ví dụ với index `(age, name)`: nếu truy vấn `WHERE age > 18 AND name = 'Anh'`, DB tìm các nút có `age > 18`, nhưng trong nhóm này, các `name` không còn được xếp nhóm liên tục nữa. Do đó cột `name` không dùng được index. Nếu đổi index thành `(name, age)`, cả 2 cột đều dùng được index.

---

## 5. Tác Tác Động Phụ của Index (Index Overhead)

Index không phải là "viên đạn bạc". Việc lạm dụng index sẽ gây ra các tác dụng phụ nghiêm trọng:

1.  **Giảm hiệu năng ghi (Write Amplification)**: Mỗi khi bạn `INSERT`, `UPDATE`, hoặc `DELETE` dữ liệu trên bảng, DB Engine bắt buộc phải cập nhật lại toàn bộ cây chỉ mục liên quan. Bảng càng nhiều index, tốc độ ghi càng chậm.
2.  **Tốn không gian đĩa**: Các file index chiếm dung lượng đĩa đáng kể, đôi khi lớn hơn cả dung lượng bảng dữ liệu gốc.
3.  **Phân mảnh và Bloat**: Các hoạt động ghi đè liên tục làm các trang của cây chỉ mục bị chia tách (Page Splitting), tạo ra nhiều khoảng trống dư thừa (Bloat) trong cây Index, làm giảm tốc độ tìm kiếm. Cần chạy lệnh `REINDEX` định kỳ trên production để tái cấu trúc lại chỉ mục.

---

## 6. Ghi Chú Phóng Vấn (Interview Q&A)

### Q1: Tại sao tôi đã đánh index cho cột `status` nhưng khi SELECT, DB vẫn chọn Seq Scan?
**Trả lời**:
Có 3 nguyên nhân phổ biến sau:
1.  **Độ chọn lọc kém (Low Selectivity)**: Cột `status` chỉ có 2 giá trị (`ACTIVE` và `INACTIVE`). Nếu dữ liệu của bạn có tới 90% dòng là `ACTIVE`, và bạn truy vấn `WHERE status = 'ACTIVE'`, Optimizer sẽ tính toán chi phí (Cost) và thấy rằng việc đọc toàn bộ bảng (Seq Scan) sẽ nhanh hơn việc đi đọc cây Index để lấy 90% TID rồi lại nhảy xuống đĩa đọc bảng vật lý. DB thường chỉ chọn Index Scan khi lượng dữ liệu trả về chiếm dưới 5-15% tổng số dòng.
2.  **Thống kê cũ (Outdated Statistics)**: Database duy trì các bảng thống kê tần suất dữ liệu để Optimizer tính Cost. Nếu bảng vừa nạp lượng dữ liệu lớn mà chưa chạy lệnh `ANALYZE`, DB sẽ tính toán sai chi phí và chọn Seq Scan.
3.  **Bảng quá nhỏ**: Nếu bảng chỉ có vài trăm dòng, DB Engine sẽ đọc trực tiếp toàn bộ bảng vào RAM luôn cho nhanh thay vì tốn công đi duyệt cây index.

### Q2: Làm sao để đánh index hỗ trợ tìm kiếm dạng text bằng ký tự đại diện dạng `LIKE '%abc%'`?
**Trả lời**:
Để hiểu điều này, hãy tưởng tượng cuốn **Từ điển giấy** được sắp xếp theo bảng chữ cái từ trái qua phải:

1.  **Tìm kiếm tiền tố: `LIKE 'abc%'` (Ký tự `%` nằm bên PHẢI / đứng SAU)**
    *   **Ví dụ**: Tìm các từ bắt đầu bằng "abc" (như: abacus, abbey, ability).
    *   **Cách bạn tìm**: Bạn chỉ cần mở từ điển đến mục bắt đầu bằng chữ "a", tiếp theo tìm chữ "b", rồi chữ "c". Tất cả các từ này đều nằm liền kề nhau.
    *   **DB Engine**: **SỬ DỤNG ĐƯỢC B-Tree Index**.
2.  **Tìm kiếm hậu tố/trung tố: `LIKE '%abc'` hoặc `LIKE '%abc%'` (Ký tự `%` nằm bên TRÁI / đứng TRƯỚC)**
    *   **Ví dụ**: Tìm các từ kết thúc bằng "abc" (như: "abc") hoặc chứa cụm "abc" (như: "database").
    *   **Cách bạn tìm**: Các từ này nằm rải rác khắp từ điển (ví dụ: "cat", "bat" nằm ở phần C và B; "database" nằm ở phần D). Không có cách nào khác ngoài việc bạn phải lật từng trang từ đầu đến cuối cuốn từ điển.
    *   **DB Engine**: **KHÔNG DÙNG ĐƯỢC B-Tree Index** (phải chạy Seq Scan).

**Giải pháp công nghệ khi cần tìm `LIKE '%abc%'`:**
*   **Trong PostgreSQL**:
    1.  Sử dụng extension **pg_trgm** (Trigram). Nó sẽ cắt nhỏ chuỗi text thành các cụm 3 ký tự (ví dụ "database" thành "dat", "ata", "tab",...).
    2.  Tạo chỉ mục **GIN Index** kết hợp với lớp toán tử `gin_trgm_ops`:
        ```sql
        CREATE EXTENSION IF NOT EXISTS pg_trgm;
        CREATE INDEX idx_products_name_trgm ON products USING GIN (name gin_trgm_ops);
        ```
    3.  Sau khi đánh index GIN Trigram, câu truy vấn `LIKE '%abc%'` sẽ sử dụng index cực kỳ nhanh.
*   **Trong MySQL**:
    *   Sử dụng **Full-Text Search (FTS) Index** với hàm `MATCH() ... AGAINST()`.
