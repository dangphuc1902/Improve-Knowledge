# 🔄 Database Transactions & ACID — Giao Dịch & Tính Nhất Quán

> **Phase:** 1 | **Time Block:** 05:00-06:00 Sáng  
> **Quan trọng cho:** ALL Tier 1 & Tier 2. Đây là nền tảng cốt lõi của tính đúng đắn dữ liệu (Data Integrity). Sai sót trong quản lý transaction dẫn đến mất tiền, sai lệch số dư, hoặc treo cứng toàn bộ hệ thống.

---

## 1. Phân Tích Chuyên Sâu Thuộc Tính ACID

Một transaction (giao dịch) là một tập hợp các câu lệnh SQL được thực thi như một đơn vị công việc logic duy nhất. Transaction phải tuân thủ 4 thuộc tính ACID:

### 1.1. Atomicity (Tính nguyên tử - "Tất cả hoặc không có gì")
*   **Ý nghĩa**: Toàn bộ các câu lệnh trong transaction phải thành công, hoặc không có câu lệnh nào được ghi nhận. Nếu xảy ra lỗi ở bất kỳ bước nào, hệ thống phải khôi phục (ROLLBACK) dữ liệu về trạng thái trước khi giao dịch bắt đầu.
*   **Cơ chế hoạt động bên dưới**: DB Engine sử dụng **WAL (Write-Ahead Logging)** hoặc **Undo Log**. Trước khi thực sự thay đổi dữ liệu trên đĩa cứng, DB ghi chép hành động đó vào file Log tuần tự. Nếu hệ thống sập giữa chừng, khi khởi động lại, DB sẽ đọc WAL để hoàn tác (Undo/Rollback) các giao dịch chưa commit.

---

### 1.2. Consistency (Tính nhất quán)
*   **Ý nghĩa**: Giao dịch phải chuyển cơ sở dữ liệu từ một trạng thái hợp lệ này sang một trạng thái hợp lệ khác. Dữ liệu phải luôn tuân thủ mọi ràng buộc (Constraints, Foreign Keys, Unique Index).
*   **Phân biệt**:
    *   *Nhất quán của Database*: DB tự đảm bảo (không cho phép chèn khóa ngoại không tồn tại, không cho phép trùng khóa chính).
    *   *Nhất quán của Nghiệp vụ*: Lập trình viên phải tự đảm bảo (ví dụ: tổng số tiền trong ngân hàng trước và sau khi chuyển khoản của tất cả tài khoản phải không đổi).

---

### 1.3. Isolation (Tính cô lập)
*   **Ý nghĩa**: Các giao dịch chạy đồng thời phải không được can thiệp lẫn nhau. Kết quả của một giao dịch đang chạy không được hiển thị cho các giao dịch khác trước khi nó được commit.
*   **Cơ chế**: Được kiểm soát thông qua các **Isolation Levels** (Mức độ cô lập) và cơ chế **Locking** (Khóa) hoặc **MVCC** (Đa phiên bản).

---

### 1.4. Durability (Tính bền vững)
*   **Ý nghĩa**: Một khi giao dịch đã được committed, dữ liệu sẽ được lưu trữ vĩnh viễn trên đĩa cứng, ngay cả khi máy chủ bị mất điện hay sập OS ngay sau đó.
*   **Cơ chế hoạt động**: Khi gọi lệnh `COMMIT`, DB Engine thực hiện thao tác **fsync** để ép buộc (force write) dữ liệu từ buffer cache của hệ điều hành xuống đĩa cứng vật lý trước khi trả về thông báo thành công cho client.

---

## 2. Concurrency Phenomena (Các Hiện Tượng Đồng Thời)

Khi các giao dịch chạy song song ở mức cô lập thấp, các hiện tượng sai lệch dữ liệu sau sẽ xảy ra:

1.  **Dirty Read (Đọc bẩn)**:
    *   *Hiện tượng:* Giao dịch A đọc dữ liệu được chỉnh sửa bởi giao dịch B, nhưng giao dịch B **chưa commit**. Sau đó giao dịch B rollback. Giao dịch A đã xử lý trên dữ liệu "rác" không bao giờ thực sự tồn tại.
2.  **Non-Repeatable Read (Đọc không lặp lại)**:
    *   *Hiện tượng:* Giao dịch A đọc một dòng dữ liệu. Giao dịch B nhảy vào **UPDATE** dòng đó và COMMIT. Giao dịch A đọc lại dòng đó một lần nữa trong cùng một transaction và thấy dữ liệu đã bị thay đổi.
3.  **Phantom Read (Đọc bóng ma)**:
    *   *Hiện tượng:* Giao dịch A thực hiện câu truy vấn tìm các dòng thỏa mãn điều kiện (ví dụ `WHERE salary > 5000`). Giao dịch B nhảy vào **INSERT** một dòng mới thỏa mãn điều kiện đó và COMMIT. Giao dịch A chạy lại câu truy vấn cũ và thấy xuất hiện thêm dòng dữ liệu "bóng ma" mới.
4.  **Write Skew (Lệch ghi)**:
    *   *Hiện tượng:* Xảy ra ở mức cô lập cao. Ví dụ: Nghiệp vụ yêu cầu tổng số dư của tài khoản A + B phải $\ge 0$. Hiện tại A = $100, B = $100.
        *   Giao dịch 1 rút $150 từ A (đọc thấy tổng A+B = $200 $\ge 0$ -> hợp lệ).
        *   Giao dịch 2 đồng thời rút $150 từ B (đọc thấy tổng A+B = $200 $\ge 0$ -> hợp lệ).
        *   Cả hai giao dịch cùng committed. Kết quả: A = -$50, B = -$50, tổng là -$100 (vi phạm quy tắc).

---

## 3. Các Mức Độ Cô Lập (Isolation Levels)

Chuẩn SQL định nghĩa 4 mức độ cô lập để ngăn chặn các hiện tượng đồng thời nêu trên. Mức cô lập càng cao, hệ thống càng an toàn nhưng hiệu năng càng giảm do tranh chấp tài nguyên (Locking).

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read | Viết tắt | Cơ chế mặc định của DB |
|:---|:---:|:---:|:---:|:---|:---|
| **Read Uncommitted** | ❌ Bị | ❌ Bị | ❌ Bị | Ít dùng | Cho phép đọc trực tiếp từ Buffer |
| **Read Committed** | ✅ Tránh | ❌ Bị | ❌ Bị | Mặc định Postgres/SQL Server | Đọc Snapshot tại mỗi câu lệnh SQL đơn lẻ |
| **Repeatable Read** | ✅ Tránh | ✅ Tránh | ❌ Bị *(MySQL tránh được)* | Mặc định MySQL (InnoDB) | Đọc Snapshot tạo ra từ đầu Transaction |
| **Serializable** | ✅ Tránh | ✅ Tránh | ✅ Tránh | Chậm nhất | Khóa hoặc kiểm tra xung đột đụng độ |

*Lưu ý đặc biệt về PostgreSQL:* Trong Postgres, mức cô lập `Repeatable Read` ngăn chặn được cả hiện tượng `Phantom Read` nhờ cơ chế chụp ảnh Snapshot thông minh. Ở mức `Serializable`, Postgres sử dụng thuật toán SSI (Serializable Snapshot Isolation) để phát hiện và ngăn chặn `Write Skew` mà không cần lock bảng vật lý.

---

## 4. Cơ Chế MVCC (Multi-Version Concurrency Control) trong PostgreSQL

Để tránh việc độc giả chặn người ghi (Readers block Writers) và người ghi chặn người độc (Writers block Readers), PostgreSQL sử dụng cơ chế **MVCC**.

### 4.1. Cách Postgres quản lý phiên bản dòng (Tuple Versioning)
Mỗi dòng dữ liệu (tuple) trong bảng vật lý của Postgres đều chứa các cột hệ thống ẩn để kiểm soát visibility (khả năng hiển thị):
*   `xmin`: ID của transaction đã chèn (INSERT) dòng này.
*   `xmax`: ID của transaction đã xóa hoặc cập nhật (DELETE/UPDATE) dòng này (mặc định là 0 nếu dòng chưa bị xóa).

### 4.2. Cơ chế hoạt động khi UPDATE dòng:
1.  Khi một transaction thực hiện `UPDATE` dòng dữ liệu có ID = 1:
    *   Postgres **không ghi đè** lên dòng cũ.
    *   Nó chèn một dòng mới tinh (Tuple mới) có cùng ID = 1 nhưng chứa dữ liệu mới.
    *   Ghi ID transaction hiện tại vào cột `xmax` của dòng cũ (đánh dấu dòng cũ đã chết) và vào cột `xmin` của dòng mới (đánh dấu dòng mới được tạo sinh).
2.  Khi các transaction khác SELECT:
    *   DB Engine đối chiếu ID transaction của transaction đang đọc với giá trị `xmin` và `xmax` của dòng để quyết định dòng nào hiển thị (Visibility Rules).
    *   Nhờ vậy, transaction đọc vẫn đọc bản cũ bình thường mà không bị block bởi transaction đang viết bản mới.

---

## 5. Các Cơ Chế Khóa (Locking Mechanisms)

Khi MVCC là chưa đủ và bạn cần kiểm soát nghiêm ngặt dữ liệu tránh Race Condition, bạn phải dùng Lock.

### 5.1. Khóa Bi quan (Pessimistic Locking)
*   **Ý nghĩa**: Bạn giả định rằng xung đột dữ liệu chắc chắn sẽ xảy ra, vì vậy bạn khóa cứng dòng dữ liệu ngay khi đọc lên để không cho bất kỳ ai chỉnh sửa cho đến khi bạn xong việc.
*   **Thực thi trong SQL**:
    ```sql
    -- Khóa ghi (Pessimistic Write): Ngăn chặn người khác đọc/ghi dòng này
    SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
    
    -- Khóa đọc (Pessimistic Read): Cho phép người khác đọc, ngăn chặn sửa đổi
    SELECT * FROM accounts WHERE id = 1 FOR SHARE;
    ```
*   **Ứng dụng**: Thích hợp cho hệ thống có **tỷ lệ tranh chấp cao** (ví dụ: giỏ hàng thanh toán flash sale, số dư ví tiền tài khoản).

---

### 5.2. Khóa Lạc quan (Optimistic Locking)
*   **Ý nghĩa**: Bạn giả định xung đột ít khi xảy ra, vì vậy bạn không khóa gì cả khi đọc dữ liệu. Chỉ khi bạn ghi dữ liệu xuống (UPDATE), bạn mới kiểm tra xem dữ liệu có bị ai sửa đổi trước đó chưa.
*   **Thực thi**: Thêm một cột phiên bản (version hoặc timestamp) vào bảng.
    ```sql
    -- Bước 1: Đọc dữ liệu lên (lấy được version = 5)
    SELECT balance, version FROM accounts WHERE id = 1;
    
    -- Bước 2: Thực hiện tính toán ở Java App (balance mới = balance - 100)
    -- Bước 3: UPDATE xuống DB kèm điều kiện WHERE version = 5 cũ
    UPDATE accounts 
    SET balance = 900, version = version + 1 
    WHERE id = 1 AND version = 5;
    ```
*   **Xử lý kết quả**:
    *   Nếu số dòng bị ảnh hưởng (rows affected) = 1: Thành công.
    *   Nếu số dòng bị ảnh hưởng = 0: Thất bại (có nghĩa là có ai đó đã nhanh tay hơn sửa dữ liệu và tăng version lên 6 trước bạn). Java App sẽ ném ra ngoại lệ `OptimisticLockingFailureException`, bạn cần bắt ngoại lệ này và thực hiện retry (đọc lại và ghi lại).
*   **Ứng dụng**: Thích hợp cho hệ thống **đọc nhiều ghi ít, tỷ lệ tranh chấp thấp** (ví dụ: cập nhật thông tin user profile, viết bài blog).

---

## 6. Giao Dịch trong Spring Boot (`@Transactional`)

Trong Java Spring Boot, lập trình viên sử dụng chú thích `@Transactional` để quản lý ranh giới giao dịch một cách khai báo (Declarative Transaction Management).

### 6.1. Cơ chế hoạt động của `@Transactional`
*   Spring sử dụng **AOP (Aspect-Oriented Programming)** tạo ra một lớp Proxy bọc ngoài class Service của bạn.

```
Request gọi Service.transferMoney()
    ↓
Proxy intercept cuộc gọi
    ↓
Proxy mở kết nối DB vật lý -> Chạy lệnh SQL: "BEGIN TRANSACTION;"
    ↓
Proxy gọi method transferMoney() thực tế trong Service của bạn
    │
    ├── Nếu chạy bình thường không lỗi:
    │     Proxy chạy lệnh SQL: "COMMIT;"
    │
    └── Nếu xảy ra Exception (mặc định là RuntimeException):
          Proxy chạy lệnh SQL: "ROLLBACK;"
```

### 6.2. Lỗi kinh điển: Self-Invocation (Tự gọi nội bộ)
Một lỗi rất phổ biến khiến `@Transactional` vô hiệu hóa là tự gọi phương thức trong cùng một Class.

```java
@Service
public class OrderService {

    // Không có Transaction
    public void createOrder(Order order) {
        // ... xử lý logic
        
        // Gọi nội bộ phương thức có @Transactional
        saveToDatabase(order); 
    }

    @Transactional
    public void saveToDatabase(Order order) {
        orderRepository.save(order);
        paymentRepository.process(order);
    }
}
```
*   **Tại sao lỗi?** Vì createOrder() được gọi từ bên ngoài, Spring Container sẽ trả về thực thể Proxy của `OrderService`. Nhưng bên trong `createOrder()`, lệnh gọi `saveToDatabase()` là gọi trực tiếp (`this.saveToDatabase()`), đi vòng qua lớp Proxy. Kết quả là **không có transaction nào được tạo ra**, nếu dòng payment lỗi, dữ liệu order vẫn bị commit (lỗi Atomicity).
*   **Cách sửa**: Tách `saveToDatabase()` sang một class Service khác (ví dụ `OrderDbService`) để cuộc gọi đi qua Proxy của class mới.
