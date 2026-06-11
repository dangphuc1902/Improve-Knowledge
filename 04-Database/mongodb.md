# 🍃 MongoDB — Cơ Sở Dữ Liệu Hướng Tài Liệu (Document Database)

> **Phase:** 1-2 | **Time Block:** T7 13:30-15:30  
> **Quan trọng cho:** Teko, Tiki, MoMo, Gihot — Các dự án cần xử lý dữ liệu phi cấu trúc, cấu trúc thay đổi liên tục (schema-less) và yêu cầu khả năng mở rộng (scale-out) dễ dàng.

---

## 1. Kiến Trúc Hướng Tài Liệu (Document-oriented Architecture)

MongoDB là cơ sở dữ liệu NoSQL phổ biến nhất thuộc nhóm **Document Store**.

### 1.1. BSON (Binary JSON) là gì?
Mặc dù bạn nhìn thấy dữ liệu hiển thị dưới dạng JSON, nhưng MongoDB lưu trữ dữ liệu trên đĩa cứng dưới dạng **BSON (Binary JSON)**.
*   **Ưu điểm của BSON so với JSON**:
    *   **Hiệu năng cao**: BSON được thiết kế để phân tích cú pháp (parsing) và duyệt dữ liệu cực nhanh. Nó lưu trữ độ dài của phần tử và độ lệch (offset) nên DB engine có thể nhảy trực tiếp đến một trường cụ thể mà không cần đọc toàn bộ tài liệu.
    *   **Nhiều kiểu dữ liệu hơn**: JSON thông thường chỉ hỗ trợ String, Number, Boolean, Null, Array, Object. BSON bổ sung các kiểu dữ liệu quan trọng như `ObjectId`, `Date`, `BinData` (lưu file/ảnh nhị phân), `32-bit/64-bit Integer` (tránh sai số dấu phẩy động của float).

---

### 1.2. Chiến thuật thiết kế Schema: Embedding vs Referencing
Trong thế giới quan hệ (SQL), chuẩn hóa dữ liệu (normalization) là bắt buộc. Trong MongoDB, bạn có 2 cách thiết kế mối quan hệ giữa các thực thể:

```
Embedding (Nhúng - Denormalized)          Referencing (Tham chiếu - Normalized)
┌──────────────────────────────────────┐  ┌────────────────┐    ┌────────────────┐
│ User Document                        │  │ User Document  │    │ Address Doc    │
│ {                                    │  │ {              │    │ {              │
│   _id: 1,                            │  │   _id: 1,      │──┐ │   _id: 99,     │
│   name: "Alice",                     │  │   name: "Alice"│  │ │   user_id: 1,  │
│   addresses: [                       │  │ }              │  │ │   city: "HCM"  │
│     { city: "HCM", zip: 70000 }      │  └────────────────┘  │ │ }              │
│   ]                                  │                      └─►────────────────┘
│ }                                    │
└──────────────────────────────────────┘
```

#### A. Embedding (Nhúng dữ liệu con trực tiếp vào tài liệu cha)
*   **Cách dùng**: Lưu các đối tượng liên quan vào trong một mảng (Array) của tài liệu chính.
*   **Khi nào chọn**:
    *   Quan hệ 1-1 hoặc 1-N nhưng phía N có số lượng giới hạn và cố định (ví dụ: một người dùng có tối đa 3-5 địa chỉ).
    *   Dữ liệu con luôn đi kèm với dữ liệu cha (đọc cha là luôn luôn đọc con).
    *   Dữ liệu con không cần truy vấn độc lập.
*   **Ưu điểm**: Đọc cực nhanh nhờ tính chất **Locality of Reference** (toàn bộ dữ liệu nằm trong 1 trang đĩa đơn lẻ, không cần thực hiện phép JOIN).
*   **Hạn chế**: Giới hạn kích thước của một tài liệu MongoDB là **16MB**. Nếu mảng nhúng tăng trưởng vô hạn (ví dụ: nhúng toàn bộ bình luận vào một bài viết có hàng triệu comment), tài liệu sẽ bị tràn và lỗi.

#### B. Referencing (Tham chiếu qua ID - giống Khóa ngoại SQL)
*   **Cách dùng**: Lưu trữ tài liệu con ở một Collection riêng và lưu `_id` làm liên kết. Sử dụng toán tử `$lookup` để JOIN khi cần.
*   **Khi nào chọn**:
    *   Quan hệ 1-N với N tăng trưởng vô hạn (ví dụ: một cửa hàng có hàng triệu đơn hàng).
    *   Quan hệ N-N (Nhiều - Nhiều).
    *   Dữ liệu con cần được truy vấn độc lập và thường xuyên cập nhật riêng lẻ.
*   **Ưu điểm**: Tránh trùng lặp dữ liệu, không lo giới hạn 16MB.
*   **Hạn chế**: Truy vấn chậm hơn vì DB phải thực hiện scan ở nhiều collection khác nhau.

---

## 2. Các Loại Index Trong MongoDB

MongoDB hỗ trợ hệ thống index cực kỳ mạnh mẽ để tối ưu hóa tìm kiếm:

1.  **Single Field Index**: Đánh chỉ mục trên một trường đơn lẻ (tương tự SQL).
2.  **Compound Index**: Chỉ mục tổng hợp nhiều trường. Thứ tự khai báo cực kỳ quan trọng (tuân thủ Leftmost Prefix Rule).
3.  **Multikey Index**: Tự động kích hoạt khi bạn đánh index trên một trường có kiểu dữ liệu là Mảng (Array). MongoDB sẽ tạo ra một entry index riêng biệt cho **từng phần tử** bên trong mảng đó.
    *   *Lưu ý:* Không được phép tạo Compound Index chứa nhiều hơn 1 trường dạng Mảng (tránh bùng nổ tổ hợp Cartesian product của index).
4.  **TTL Index (Time-To-Live)**: Tự động xóa tài liệu sau một khoảng thời gian thiết lập sẵn (rất thích hợp làm session store, lưu log tạm thời).
5.  **Text Index**: Hỗ trợ tìm kiếm từ khóa đầy đủ (Full-text search) trên các trường văn bản.

---

## 3. Tính Sẵn Sàng Cao: Replica Set (Bộ Bản Sao)

Replica Set trong MongoDB là một nhóm các tiến trình `mongod` duy trì cùng một tập dữ liệu, hoạt động theo mô hình **Primary-Secondary**.

```
                ┌──────────────────┐
                │  Primary Node    │ (Nhận Write + Read mặc định)
                └─┬──────────────┬─┘
                  │              │
        Replicate │              │ Replicate
        (Oplog)   │              │ (Oplog)
                  ▼              ▼
        ┌───────────┐          ┌───────────┐
        │ Secondary │ ◄────────► Secondary │ (Chỉ nhận Read)
        └───────────┘ Heartbeat└───────────┘
```

### 3.1. Cơ chế đồng bộ dữ liệu (Oplog)
*   Mọi lệnh ghi (Write) từ ứng dụng bắt buộc phải gửi tới node **Primary**.
*   Node Primary ghi thay đổi vào dữ liệu của nó và đồng thời ghi nhận hành động đó vào một collection đặc biệt gọi là **Oplog (Operations Log)**.
*   Các node **Secondary** liên tục kéo (pull) Oplog từ Primary về và chạy lại các lệnh đó để đồng bộ dữ liệu.

### 3.2. Cơ chế bầu cử tự động (Election & Heartbeat)
*   Các node trong Replica Set gửi tín hiệu **Heartbeat** (mặc định 2 giây/lần) cho nhau để kiểm tra trạng thái sống chết.
*   Nếu node Primary bị chết hoặc mất kết nối quá 10 giây, các node Secondary sẽ kích hoạt một cuộc bầu cử tự động (**Election**).
*   Node Secondary nào có dữ liệu cập nhật mới nhất (so khớp qua Oplog) và nhận được đa số phiếu bầu sẽ được thăng cấp lên làm Primary mới. Quá trình này diễn ra hoàn toàn tự động trong vài giây mà không cần con người can thiệp.

### 3.3. Cấu hình Read Preference (Tùy chọn đọc)
Ứng dụng có thể cấu hình nơi đọc dữ liệu từ driver:
*   `primary` (Mặc định): Chỉ đọc từ Primary (đảm bảo dữ liệu luôn mới nhất - Strong Consistency).
*   `secondary`: Chỉ đọc từ các Secondary để giảm tải cho Primary (chấp nhận độ trễ đồng bộ - Eventual Consistency).
*   `primaryPreferred`: Ưu tiên đọc từ Primary, nếu Primary chết thì đọc từ Secondary.

---

## 4. Khả Năng Mở Rộng: Sharding (Phân Mảnh Dữ Liệu)

Khi tập dữ liệu vượt quá khả năng lưu trữ của một server vật lý, MongoDB sử dụng **Sharding** để chia nhỏ dữ liệu sang nhiều server độc lập (các Shard).

```
                      ┌───────────────┐
                      │  Application  │
                      └───────┬───────┘
                              │
                      ┌───────▼───────┐
                      │ mongos Router │ (Bộ điều phối truy vấn)
                      └─┬───────────┬─┘
                        │           │
            ┌───────────┘           └───────────┐
            ▼                                   ▼
      ┌───────────┐                       ┌───────────┐
      │  Shard A  │                       │  Shard B  │ (Các Replica Set vật lý)
      └───────────┘                       └───────────┘
```

### 4.1. Các thành phần của Cụm Sharded:
1.  **Shard**: Mỗi shard là một Replica Set chứa một phần dữ liệu của database.
2.  **mongos (Query Router)**: Đóng vai trò là bộ định tuyến. Ứng dụng kết nối trực tiếp tới `mongos`. Nó sẽ phân tích câu truy vấn và gửi nó tới chính xác Shard chứa dữ liệu cần tìm, sau đó gộp kết quả trả về cho client.
3.  **Config Servers**: Lưu trữ metadata của toàn bộ cụm (dữ liệu nào nằm ở shard nào). `mongos` sẽ đọc dữ liệu từ Config Servers để biết đường định tuyến.

### 4.2. Tầm quan trọng của Shard Key (Khóa phân mảnh)
*   **Shard Key** là cột dữ liệu được sử dụng để quyết định tài liệu sẽ được lưu vào shard nào.
*   **Lựa chọn Shard Key kém (Ví dụ chọn trường tăng dần như Auto-increment ID hoặc CreatedAt)**:
    *   Hậu quả: Toàn bộ dữ liệu mới ghi vào hệ thống luôn có giá trị Shard Key lớn nhất ➡️ `mongos` sẽ đẩy tất cả dữ liệu mới vào duy nhất 1 Shard cuối cùng ➡️ Shard đó bị nghẽn (Hotspot Shard), trong khi các Shard cũ thì rảnh rỗi.
*   **Shard Key tốt**: Phải có **độ phân tán cao (High Cardinality)** và phân bổ đều lượng ghi (ví dụ băm ID người dùng - Hashed Shard Key).

---

## 5. Ghi Chú Phóng Vấn (Interview Q&A)

### Q1: Khi nào bạn chọn MongoDB thay vì PostgreSQL/MySQL?
**Trả lời**:
Tôi sẽ chọn MongoDB khi:
1.  **Cấu trúc dữ liệu biến động (Dynamic Schema)**: Ví dụ danh mục sản phẩm thương mại điện tử, các thuộc tính của sản phẩm khác nhau hoàn toàn (điện thoại có RAM, dung lượng; thời trang có size, chất liệu). MongoDB cho phép lưu trữ trực tiếp mà không cần chạy các script migration DB phức tạp làm downtime hệ thống.
2.  **Yêu cầu mở rộng theo chiều ngang dễ dàng (Horizontal Scaling)**: MongoDB hỗ trợ Sharding tự động rất tốt ngay từ đầu. Với SQL, việc sharding thủ công ở tầng ứng dụng rất phức tạp và dễ lỗi.
3.  **Dữ liệu dạng cây hoặc tài liệu tự chứa (Self-contained)**: Khi đọc dữ liệu, ta muốn lấy toàn bộ thông tin liên quan trong 1 lần đọc duy nhất mà không muốn thực hiện quá nhiều phép JOIN phức tạp gây chậm hệ thống.

Tôi sẽ chọn SQL (Postgres/MySQL) khi:
1.  Dữ liệu có tính chất quan hệ chặt chẽ, đòi hỏi các phép JOIN phức tạp trên nhiều bảng để làm báo cáo.
2.  Hệ thống yêu cầu tính giao dịch nghiêm ngặt (ACID) đa tài liệu cao (mặc dù MongoDB hiện tại đã hỗ trợ giao dịch đa tài liệu từ bản 4.0, nhưng hiệu năng và tính tối ưu vẫn thua kém RDBMS truyền thống).

### Q2: MongoDB giải quyết bài toán ACID Transactions như thế nào?
**Trả lời**:
*   Mặc định, MongoDB đảm bảo tính nguyên tử (Atomicity) ở mức **đơn tài liệu (Single-document)**. Nghĩa là việc cập nhật nhiều trường trong cùng một Document luôn thành công hoàn toàn hoặc thất bại hoàn toàn. Điều này đáp ứng 80-90% nhu cầu nếu thiết kế Schema dạng nhúng (Embedding) hợp lý.
*   Từ phiên bản **4.0**, MongoDB đã hỗ trợ **Multi-Document Transactions** (Giao dịch đa tài liệu) chạy trên cụm Replica Set, và từ **4.2** hỗ trợ trên cụm Sharded Cluster.
*   *Cách thức hoạt động:* Sử dụng giao thức cam kết hai pha (2-Phase Commit) kết hợp cơ chế kiểm soát đồng thời lạc quan (Optimistic Concurrency Control). Tuy nhiên, giao dịch đa tài liệu trong MongoDB có chi phí latency rất cao, nếu transaction chạy quá 60 giây sẽ bị tự động hủy để tránh treo hệ thống. Khuyến cáo chỉ dùng khi thực sự bắt buộc (ví dụ chuyển khoản ví tiền).
