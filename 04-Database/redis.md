# ⚡ Redis (Remote Dictionary Server) — Bộ Đệm Hiệu Năng Cao

> **Phase:** 1-2 | **Time Block:** T7 13:30-15:30  
> **Quan trọng cho:** MoMo, ZaloPay, Tiki, NAB — Các hệ thống chịu tải lớn (High Throughput). Redis là công cụ tối thượng để tối ưu hóa tốc độ phản hồi từ giây xuống mili-giây.

---

## 1. Kiến Trúc Đơn Luồng (Single-threaded) và Tốc Độ của Redis

Một câu hỏi phỏng vấn kinh điển là: *"Tại sao Redis chạy đơn luồng (single-thread) cho việc thực thi lệnh mà vẫn có thể đạt tốc độ hơn 100,000 requests/giây?"*

### 1.1. Lý do Redis chạy đơn luồng vẫn nhanh:
1.  **Chạy hoàn toàn trên RAM**: Tốc độ truy xuất dữ liệu trên RAM nhanh hơn ổ cứng (SSD/HDD) từ 10,000 đến 100,000 lần. CPU không bị nghẽn bởi tốc độ đọc ghi đĩa (Disk I/O).
2.  **Không tốn chi phí tranh chấp luồng (Locking & Context Switching)**: Trong hệ thống đa luồng (multi-threaded), hệ điều hành mất rất nhiều tài nguyên CPU để chuyển đổi ngữ cảnh giữa các luồng (context switch) và đồng bộ hóa khóa (mutex, semaphore) để tránh tranh chấp dữ liệu. Đơn luồng giúp Redis chạy tuần tự cực kỳ mượt mà và an toàn (không bao giờ bị Race Condition bên trong).
3.  **Cơ chế I/O Multiplexing (Thông báo sự kiện)**: Redis sử dụng mô hình lập trình bất đồng bộ dựa trên cơ chế phản hồi sự kiện mạng của hệ điều hành (như `epoll` trên Linux hoặc `kqueue` trên macOS). Thay vì tạo mỗi luồng cho một kết nối khách hàng và ngồi đợi dữ liệu truyền tới, Redis sử dụng duy nhất một luồng để lắng nghe sự kiện từ hệ điều hành. Khi có kết nối thực sự gửi dữ liệu đến, Redis mới nhảy vào xử lý và trả kết quả ngay lập tức.

*Lưu ý:* Từ Redis 6.0+, luồng mạng I/O đọc/ghi dữ liệu từ socket được tách ra chạy đa luồng để tận dụng tối đa băng thông card mạng, nhưng lõi thực thi câu lệnh (Command Execution Core) vẫn chạy **đơn luồng**.

---

## 2. Các Cấu Trúc Dữ Liệu "Sát Thủ" của Redis

Redis không chỉ là kho chứa Key-Value dạng chuỗi thông thường. Sức mạnh của nó nằm ở các cấu trúc dữ liệu tối ưu hóa sẵn cho các nghiệp vụ:

| Cấu trúc dữ liệu | Bản chất bên dưới | Ứng dụng thực tế | Ví dụ lệnh |
|:---|:---|:---|:---|
| **String** | Chuỗi nhị phân an toàn (tối đa 512MB) | Cache trang HTML, lưu session token, counter lượt xem. | `SET`, `GET`, `INCR` |
| **Hash** | Bảng băm (Map bên trong Map) | Lưu trữ đối tượng (User profile, chi tiết sản phẩm) để tiết kiệm RAM. | `HSET`, `HGET` |
| **List** | Danh sách liên kết đôi (Double Linked List) | Xây dựng hàng đợi tin nhắn đơn giản (Message Queue), danh sách hoạt động mới nhất. | `LPUSH`, `RPOP` |
| **Set** | Tập hợp không trùng lặp (Hash Table) | Lưu danh sách IP bị chặn, tìm kiếm bạn chung (giao/hợp tập hợp). | `SADD`, `SINTER` |
| **Sorted Set (ZSet)**| Cấu trúc **Skip List + Hash Table** | **Leaderboard** (Bảng xếp hạng game, sản phẩm bán chạy nhất) dựa vào điểm số (Score). | `ZADD`, `ZRANGE` |
| **Geospatial (Geo)** | Mã hóa tọa độ bằng Geohash | Tìm kiếm người dùng ở gần đây, tìm tài xế Grab xung quanh. | `GEOADD`, `GEODIST` |

---

## 3. Ba Thảm Họa Cache và Giải Pháp Thực Chiến (Crucial for Interviews)

Trong môi trường phân tán thực tế, việc thiết kế hệ thống Cache không kỹ sẽ dẫn đến 3 thảm họa phá hủy Database chính bên dưới:

### 3.1. Cache Penetration (Xâm nhập bộ đệm)
*   **Hiện tượng**: Người dùng (hoặc hacker tấn công) liên tục yêu cầu các Key **hoàn toàn không tồn tại** trong cả Cache lẫn Database chính (ví dụ: truy vấn tìm thông tin user có `id = -9999`). 
    *   Hậu quả: Lượt đọc bị Cache Miss ➡️ Truy vấn đi thẳng xuống Database ➡️ Database phải scan đĩa tìm dữ liệu không có ➡️ Database bị tràn tải và sập.
*   **Giải pháp**:
    1.  **Cache giá trị Null**: Nếu DB trả về kết quả rỗng (Null), ta vẫn ghi đè giá trị Null đó vào Redis với thời gian hết hạn (TTL) rất ngắn (ví dụ 1-5 phút).
    2.  **Sử dụng Bloom Filter**: Đây là cấu trúc dữ liệu xác suất cực kỳ tiết kiệm bộ nhớ, cho phép kiểm tra nhanh một Key chắc chắn không tồn tại hoặc có khả năng tồn tại. Nếu Bloom Filter báo Key không có, ta chặn ngay lập tức và trả về lỗi mà không cần hỏi Redis hay DB.

---

### 3.2. Cache Breakdown / Cache Stampede (Sụp đổ bộ đệm - Cháy Key nóng)
*   **Hiện tượng**: Một Key cực kỳ "nóng" (Hot Key - ví dụ: thông tin khuyến mãi trang chủ Shopee ngày 11/11) đột ngột hết hạn (expired) ngay tại một mili-giây. Đồng thời, có 10,000 request ập đến cùng lúc.
    *   Hậu quả: Do Key vừa hết hạn, tất cả 10,000 request đều bị Cache Miss ➡️ Đồng loạt chạy truy vấn xuống Database chính ➡️ Database chịu tải đột biến cùng một thời điểm và sập ngay lập tức.
*   **Giải pháp**:
    1.  **Sử dụng Khóa phân tán (Distributed Lock - Mutex)**: Chỉ cho phép luồng đầu tiên bị Cache Miss được lấy Lock (ví dụ bằng lệnh `SETNX` trong Redis hoặc Redisson trong Java) để xuống DB lấy dữ liệu và cập nhật lại Cache. Các luồng khác phải xếp hàng đợi hoặc thử lại.
        ```java
        // Logic giả lập giải pháp Mutex
        public String getData(String key) {
            String value = redis.get(key);
            if (value != null) return value;
            
            // Lấy lock phân tán
            if (redis.setNx("lock:" + key, "1", 10 * 1000)) { // lock 10 giây
                try {
                    value = database.get(key);
                    redis.set(key, value, 3600); // cập nhật lại cache
                } finally {
                    redis.del("lock:" + key); // nhả lock
                }
            } else {
                // Đợi 100ms và thử lại
                Thread.sleep(100);
                return getData(key);
            }
            return value;
        }
        ```
    2.  **Thời gian hết hạn logic (Logical Expiration)**: Lưu thời gian hết hạn bên trong payload của giá trị. Khi luồng đọc thấy đã quá thời gian logic nhưng Key vẫn chưa bị xóa vật lý, nó trả về dữ liệu cũ ngay lập tức cho người dùng, đồng thời đẩy một job chạy ngầm (asynchronous background thread) đi cập nhật dữ liệu mới từ DB vào Redis.

---

### 3.3. Cache Avalanche (Tuyết lở bộ đệm)
*   **Hiện tượng**: Một lượng cực kỳ lớn các Key trong Cache bị hết hạn cùng một thời điểm, hoặc bản thân cụm Server Redis bị crash/mất kết nối mạng.
    *   Hậu quả: Toàn bộ lượng truy vấn của ứng dụng đổ ập xuống Database như một trận tuyết lở ➡️ Sập toàn bộ hệ thống.
*   **Giải pháp**:
    1.  **Cộng ngẫu nhiên thời gian hết hạn (Randomized TTL)**: Khi thiết lập cache, ta cộng thêm một khoảng thời gian ngẫu nhiên (ví dụ 30-60 giây jitter) vào TTL của từng Key để thời gian hết hạn của chúng rải đều ra, tránh trùng lặp.
    2.  **Xây dựng cụm High Availability**: Sử dụng Redis Sentinel hoặc Redis Cluster để đảm bảo nếu 1 node chết, các node dự phòng sẽ tự động nhảy lên thay thế (failover) trong vài giây.
    3.  **Circuit Breaker (Cầu chì chống quá tải)**: Sử dụng các thư viện như Resilience4j ở phía ứng dụng để giới hạn lượng request tối đa được phép xuống DB khi phát hiện hệ thống đang quá tải.

---

## 4. Cơ Chế Lưu Trữ Xuống Đĩa (Persistence: RDB vs AOF)

Mặc dù chạy trên RAM, Redis vẫn cần lưu dữ liệu xuống đĩa cứng để phục vụ việc khôi phục dữ liệu khi server bị khởi động lại.

### 4.1. RDB (Redis Database Backup - Chụp ảnh Snapshot)
*   **Cơ chế**: Định kỳ (ví dụ sau mỗi 15 phút hoặc khi có 10,000 thay đổi), Redis sẽ dùng lệnh `fork()` để tạo ra một tiến trình con (child process). Tiến trình con này sẽ sao chép toàn bộ dữ liệu trên RAM tại thời điểm đó và ghi xuống một file nhị phân đống gọi là `dump.rdb`.
*   **Ưu điểm**: File backup gọn nhẹ, phục hồi dữ liệu cực nhanh khi khởi động lại.
*   **Nhược điểm**: Rất dễ mất mát dữ liệu. Nếu server bị sập điện tại phút thứ 14 kể từ lần snapshot trước, toàn bộ dữ liệu thay đổi trong 14 phút đó sẽ biến mất vĩnh viễn.

### 4.2. AOF (Append Only File - Ghi log lệnh)
*   **Cơ chế**: Mỗi khi có câu lệnh thay đổi dữ liệu (SET, DEL, HSET,...), Redis sẽ ghi câu lệnh đó dưới dạng text vào một file log đuôi `.aof`. Khi khởi động lại, Redis sẽ chạy lại tuần tự toàn bộ các lệnh trong file này để dựng lại dữ liệu.
*   **Ưu điểm**: An toàn tuyệt đối, gần như không mất mát dữ liệu (thường cấu hình sync xuống đĩa 1 giây một lần).
*   **Nhược điểm**: File log phình to rất nhanh, tốc độ khôi phục dữ liệu cực kỳ chậm do phải thực thi lại hàng triệu dòng lệnh.
*   *Cơ chế Rewrite AOF:* Để tránh file phình to vô hạn, Redis có tiến trình chạy ngầm phân tích file AOF và tối ưu lại (ví dụ: 100 câu lệnh `INCR key` tăng dần sẽ được gộp lại thành duy nhất 1 câu lệnh `SET key 100`).

---

## 5. Chính Sách Loại Bỏ Dữ Liệu Khi Đầy Bộ Nhớ (Eviction Policies)

Khi dung lượng RAM của máy chủ đạt giới hạn (`maxmemory`), Redis sẽ kích hoạt chính sách Eviction để dọn dẹp bộ nhớ:

1.  **noeviction (Mặc định)**: Trả về lỗi khi ứng dụng ghi thêm dữ liệu mới, nhưng vẫn cho phép đọc.
2.  **allkeys-lru (Least Recently Used)**: Tìm và xóa các Key **ít được truy cập gần đây nhất** trên toàn bộ tập Key.
3.  **volatile-lru**: Chỉ tìm và xóa các Key **ít được truy cập gần đây nhất** trong số các Key có thiết lập thời gian hết hạn (TTL).
4.  **allkeys-lfu (Least Frequently Used)**: Tìm và xóa các Key có **tần suất truy cập thấp nhất** (ít dùng nhất).
5.  **volatile-ttl**: Tìm và ưu tiên xóa các Key có thời gian sống (TTL) còn lại ngắn nhất.

---

## 6. Ghi Chú Phóng Vấn (Interview Q&A)

### Q1: So sánh Redis vs Memcached. Tại sao các dự án hiện đại ưu tiên dùng Redis?
**Trả lời**:
Cả hai đều là in-memory key-value stores hiệu năng cao, nhưng Redis vượt trội nhờ:
*   **Cấu trúc dữ liệu đa dạng**: Memcached chỉ hỗ trợ kiểu dữ liệu chuỗi (String) thuần túy. Redis hỗ trợ List, Set, Hash, ZSet,... giúp lập trình viên xử lý logic phức tạp (như xếp hạng, định vị) trực tiếp trên Cache mà không cần kéo dữ liệu về ứng dụng xử lý.
*   **Tính bền vững (Persistence)**: Memcached hoàn toàn không có cơ chế lưu xuống đĩa (mất điện là mất sạch dữ liệu). Redis hỗ trợ RDB và AOF.
*   **Tính năng bổ sung**: Redis hỗ trợ Pub/Sub, Transaction cơ bản, Lua Scripting chạy trực tiếp trên server, và khả năng mở rộng tốt hơn qua Redis Cluster.

### Q2: Khóa phân tán Redlock hoạt động thế nào và khi nào cần dùng?
**Trả lời**:
*   **Redlock** là thuật toán phân tán được đề xuất bởi tác giả Redis để đảm bảo tính an toàn tối đa cho Khóa phân tán trên nhiều node Redis độc lập (tránh trường hợp node Master bị chết ngay sau khi cấp lock mà chưa kịp replicate sang node Slave).
*   **Cách chạy**: Ứng dụng sẽ cố gắng lấy khóa trên tất cả $N$ node Redis độc lập (thường là 5 node) song song với một timeout rất ngắn. Khóa chỉ được coi là lấy thành công nếu ứng dụng lấy được khóa ở **đa số node** (tức là $\ge (N/2 + 1)$ node, ví dụ 3/5 node) và tổng thời gian lấy khóa phải nhỏ hơn thời gian sống của khóa.
*   **Khi nào dùng**: Chỉ dùng khi hệ thống yêu cầu tính chính xác tuyệt đối về mặt tài chính hoặc concurrency lock mà một cụm Redis thông thường không đảm bảo được (tuy nhiên thuật toán này có chi phí latency cao hơn).
