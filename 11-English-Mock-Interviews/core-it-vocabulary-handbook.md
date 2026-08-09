# 📘 Core IT Vocabulary & Communication Handbook

> **Dành cho:** Phuc | Senior Java Backend Developer  
> **Mục tiêu:** Làm chủ từ vựng chuyên ngành, phát âm chuẩn tự nhiên, nắm vững cấu trúc ngữ pháp và ứng dụng trực tiếp vào giao tiếp hàng ngày (Slack, Daily Standup, Code Review) cùng các vòng Phỏng vấn tiếng Anh (HR, Technical, System Design).  
> **Phương pháp luyện tập:** Đọc to thành tiếng. Ghi âm bản thân và so sánh với audio chuẩn. Không học từ đơn lẻ — học theo **cụm từ & ngữ cảnh sử dụng**.

---

## 🧭 Hướng Dẫn Luyện Phát Âm & Phản Xạ Nói

Để cải thiện phát âm gốc rễ và tự tin phản xạ, hãy áp dụng quy tắc **3S**:
1. **Stress (Nhấn âm)**: Luôn nhấn đúng trọng âm của từ (in đậm trong phần hướng dẫn). Nhấn sai trọng âm khiến người bản xứ rất khó nghe ra từ.
2. **Sounds (Âm cuối & Âm khó)**: Chú ý các phụ âm cuối (`s`, `t`, `d`, `g`) và các âm tiếng Việt không có như `/θ/` trong *thread*, `/æ/` trong *cache*.
3. **Shadowing**: Đọc to các câu mẫu ngay lập tức, khớp với tốc độ và nhịp điệu (intonation) gợi ý.

---

## 🟢 PHẦN 1: TỪ VỰNG CHI TIẾT THEO 5 DOMAIN CỐT LÕI

---

### ☕ Domain 1: Java Core & Object-Oriented Programming (OOP)

#### 1. **Concurrency** (n) / **Concurrent** (adj) / **Concurrently** (adv)
* **Phát âm**: 
  * IPA: `/kənˈkʌr.ən.si/` | Phiên âm tiếng Việt dễ đọc: `kən-KUR-rən-si`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`KUR`).
* **Nghĩa tiếng Việt**: Tính đồng thời (xử lý nhiều tác vụ cùng một lúc).
* **Ngữ pháp & Cấu trúc**:
  * `manage/handle concurrency`: quản lý/xử lý tính đồng thời.
  * `concurrency issue/bug`: lỗi đồng thời.
  * `run/execute concurrently`: chạy đồng thời.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack / Standup*: *"I am currently resolving a **concurrency** issue where two threads are updating the same database record simultaneously."*
  * 🎙️ *Phỏng vấn*: *"In Java, we manage **concurrency** using the `java.util.concurrent` package, specifically employing concurrent collections and lock mechanisms to prevent race conditions."*

#### 2. **Inheritance** (n) / **Inherit** (v) / **Inherited** (adj)
* **Phát âm**:
  * IPA: `/ɪnˈher.ɪ.təns/` | Phiên âm tiếng Việt dễ đọc: `dɪ-HER-ɪ-təns`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`HER`), âm cuối có âm `/s/` nhẹ.
* **Nghĩa tiếng Việt**: Tính kế thừa.
* **Ngữ pháp & Cấu trúc**:
  * `inherit from a class`: kế thừa từ một class.
  * `single/multiple inheritance`: đơn/đa kế thừa.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"This subclass **inherits** from `BaseService`, so we don't need to duplicate the logging logic here."*
  * 🎙️ *Phỏng vấn*: *"Java supports single **inheritance** for classes, meaning a subclass can only **inherit** attributes and behaviors from one superclass."*

#### 3. **Polymorphism** (n) / **Polymorphic** (adj)
* **Phát âm**:
  * IPA: `/ˌpɒl.iˈmɔː.fɪ.zəm/` | Phiên âm tiếng Việt dễ đọc: `pah-li-MOR-fiz-əm`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 3 (`MOR`).
* **Nghĩa tiếng Việt**: Tính đa hình.
* **Ngữ pháp & Cấu trúc**:
  * `runtime/compile-time polymorphism`: đa hình lúc runtime/compile-time.
  * `achieve polymorphism through interface/overriding`: đạt được tính đa hình thông qua interface/overriding.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"We should leverage **polymorphism** here by using the interface `PaymentProcessor` instead of checking concrete classes with `instanceof`."*
  * 🎙️ *Phỏng vấn*: *"**Polymorphism** allows an interface to be represented in multiple forms. For example, runtime **polymorphism** is achieved in Java through method overriding."*

#### 4. **Encapsulation** (n) / **Encapsulate** (v) / **Encapsulated** (adj)
* **Phát âm**:
  * IPA: `/ɪnˌkæp.sjəˈleɪ.ʃən/` | Phiên âm tiếng Việt dễ đọc: `ɪn-kap-sə-LAY-ʃən`
  * *Lưu ý*: Nhấn trọng âm chính ở âm tiết thứ 4 (`LAY`).
* **Nghĩa tiếng Việt**: Tính đóng gói.
* **Ngữ pháp & Cấu trúc**:
  * `encapsulate something within a class`: đóng gói cái gì trong class.
  * `achieve encapsulation by using private fields`: đạt được đóng gói bằng cách sử dụng các trường private.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"To maintain **encapsulation**, we should make this field private and only expose it through a getter method."*
  * 🎙️ *Phỏng vấn*: *"**Encapsulation** hides the internal state of an object and restricts direct access, which protects the object's integrity."*

#### 5. **Garbage Collection** (n) / **Garbage Collector** (GC) (n)
* **Phát âm**:
  * IPA: `/ˈɡɑː.bɪdʒ kəˌlek.ʃən/` | Phiên âm tiếng Việt dễ đọc: `GAR-bɪdʒ kə-LEK-ʃən`
  * *Lưu ý*: Trọng âm từ Garbage ở âm thứ 1 (`GAR`), từ Collection ở âm thứ 2 (`LEK`).
* **Nghĩa tiếng Việt**: Cơ chế dọn rác tự động.
* **Ngữ pháp & Cấu trúc**:
  * `trigger/invoke garbage collection`: kích hoạt dọn rác.
  * `tune the garbage collector`: tinh chỉnh bộ dọn rác.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack / Incident*: *"The server CPU spiked because JVM was frequently triggering 'stop-the-world' **Garbage Collection**."*
  * 🎙️ *Phỏng vấn*: *"Java's **Garbage Collector** automatically reclaims memory by deleting unreachable objects. In my last project, we tuned G1GC parameters to minimize latency spikes."*

#### 6. **Dependency Injection** (n) / **Inject** (v)
* **Phát âm**:
  * IPA: `/dɪˈpen.dən.si ɪnˈdʒek.ʃən/` | Phiên âm tiếng Việt dễ đọc: `dɪ-PEN-dən-si ɪn-DJEK-ʃən`
  * *Lưu ý*: Nhấn âm 2 của Dependency (`PEN`) và âm 2 của Injection (`DJEK`).
* **Nghĩa tiếng Việt**: Tiêm phụ thuộc (truyền phụ thuộc vào class thay vì khởi tạo trực tiếp).
* **Ngữ pháp & Cấu trúc**:
  * `inject A into B`: tiêm A vào B.
  * `constructor/setter injection`: tiêm qua constructor/setter.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"We should use constructor **injection** instead of field injection with `@Autowired` for better testability."*
  * 🎙️ *Phỏng vấn*: *"By using **Dependency Injection**, we decouple classes, making our system much easier to unit test with mock objects."*

#### 7. **Serialization** (n) / **Serialize** (v) / **Serialized** (adj)
* **Phát âm**:
  * IPA: `/ˌsɪə.ri.ə.laɪˈzeɪ.ʃən/` | Phiên âm tiếng Việt dễ đọc: `sɪər-i-ə-lɪ-ZAY-ʃən`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 5 (`ZAY`).
* **Nghĩa tiếng Việt**: Tuần tự hóa (chuyển object thành byte stream/JSON để lưu trữ/truyền tải).
* **Ngữ pháp & Cấu trúc**:
  * `serialize an object to JSON/XML`: tuần tự hóa một đối tượng thành JSON/XML.
  * `serialization overhead`: chi phí hiệu năng của việc tuần tự hóa.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"We are experiencing **serialization** issues with our Kafka payloads due to mismatched DTO schemas."*
  * 🎙️ *Phỏng vấn*: *"We chose Jackson for JSON **serialization** because of its high throughput and extensive customization support in Spring Boot."*

#### 8. **Reflection** (n) / **Reflect** (v)
* **Phát âm**:
  * IPA: `/rɪˈflek.ʃən/` | Phiên âm tiếng Việt dễ đọc: `rɪ-FLEK-ʃən`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`FLEK`).
* **Nghĩa tiếng Việt**: Phản chiếu (đọc siêu dữ liệu và thao tác động lúc runtime).
* **Ngữ pháp & Cấu trúc**:
  * `use reflection to inspect classes`: dùng reflection để kiểm tra cấu trúc class.
  * `reflection overhead/performance cost`: chi phí hiệu năng của reflection.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"Using **reflection** here might degrade performance; let's check if we can achieve this with a standard design pattern."*
  * 🎙️ *Phỏng vấn*: *"Spring Framework heavily relies on **reflection** to instantiate beans and inspect custom annotations at startup."*

#### 9. **Boilerplate** (n/adj)
* **Phát âm**:
  * IPA: `/ˈbɔɪ.lə.pleɪt/` | Phiên âm tiếng Việt dễ đọc: `BOY-lər-playt`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`BOY`).
* **Nghĩa tiếng Việt**: Code mẫu, code lặp đi lặp lại tẻ nhạt (như getter, setter, constructor).
* **Ngữ pháp & Cấu trúc**:
  * `boilerplate code`: code boilerplate.
  * `reduce boilerplate with Lombok/Java Records`: giảm code boilerplate bằng Lombok/Java Records.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"We can use Lombok's `@Data` annotation to eliminate the **boilerplate** getter and setter methods."*
  * 🎙️ *Phỏng vấn*: *"By introducing Java Records in our new microservices, we eliminated a lot of **boilerplate** code, making the codebase cleaner."*

---

### 💾 Domain 2: Databases, Caching & Performance

#### 10. **Indexing** (n) / **Index** (n/v)
* **Phát âm**:
  * IPA: `/ˈɪn.deks.ɪŋ/` | Phiên âm tiếng Việt dễ đọc: `IN-deks-ɪŋ`
  * *Lưu ý*: Số nhiều của index là *indexes* hoặc *indices* (`ɪn-dɪ-seez`). Nhấn âm 1 (`IN`).
* **Nghĩa tiếng Việt**: Đánh chỉ mục.
* **Ngữ pháp & Cấu trúc**:
  * `create/add an index on a column/table`: tạo chỉ mục trên cột/bảng.
  * `index scan vs table scan`: quét chỉ mục vs quét toàn bộ bảng.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"Yesterday, I optimized the query by adding a composite **index** on the `user_id` and `created_at` columns."*
  * 🎙️ *Phỏng vấn*: *"While **indexing** speeds up read operations, we must be careful because it introduces write overhead on every insert or update."*

#### 11. **Transaction** (n) / **Transactional** (adj)
* **Phát âm**:
  * IPA: `/trænˈzæk.ʃən/` | Phiên âm tiếng Việt dễ đọc: `tran-ZAK-ʃən`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`ZAK`).
* **Nghĩa tiếng Việt**: Giao dịch cơ sở dữ liệu.
* **Ngữ pháp & Cấu trúc**:
  * `execute/commit/rollback a transaction`: thực thi/commit/rollback một giao dịch.
  * `transaction isolation level`: cấp độ cô lập của giao dịch.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack / Incident*: *"The payment **transaction** failed due to a database deadlock, triggering a complete rollback."*
  * 🎙️ *Phỏng vấn*: *"To ensure data integrity, the entire booking flow is wrapped inside a single database **transaction** using `@Transactional` in Spring."*

#### 12. **Locking** (n) / **Lock** (n/v)
* **Phát âm**:
  * IPA: `/ˈlɒk.ɪŋ/` | Phiên âm tiếng Việt dễ đọc: `LOK-ɪŋ`
  * *Lưu ý*: Phát âm rõ âm `/k/` ở giữa.
* **Nghĩa tiếng Việt**: Cơ chế khóa dữ liệu (bi quan/lạc quan).
* **Ngữ pháp & Cấu trúc**:
  * `optimistic/pessimistic locking`: khóa lạc quan/bi quan.
  * `acquire/release a lock on a row`: lấy/giải phóng khóa trên một hàng dữ liệu.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"We should use optimistic **locking** with a version column to handle concurrent updates in this high-read scenario."*
  * 🎙️ *Phỏng vấn*: *"Pessimistic **locking** blocks write access to a row until the lock is released, which is suitable for critical financial updates."*

#### 13. **Replication** (n) / **Replicate** (v) / **Replica** (n)
* **Phát âm**:
  * IPA: `/ˌrep.lɪˈkeɪ.ʃən/` | Phiên âm tiếng Việt dễ đọc: `rep-lɪ-KAY-ʃən`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 3 (`KAY`). Từ *replica* phát âm là `REP-lɪ-kə`.
* **Nghĩa tiếng Việt**: Nhân bản dữ liệu.
* **Ngữ pháp & Cấu trúc**:
  * `master-slave / primary-secondary replication`: nhân bản chính-phụ.
  * `replication lag`: độ trễ đồng bộ dữ liệu nhân bản.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"We are seeing some temporary data inconsistency due to **replication** lag between the primary DB and the read replicas."*
  * 🎙️ *Phỏng vấn*: *"We configured database **replication** to offload read queries to multiple read **replicas**, keeping the write database highly available."*

#### 14. **Sharding** (n) / **Shard** (n/v)
* **Phát âm**:
  * IPA: `/ˈʃɑː.dɪŋ/` | Phiên âm tiếng Việt dễ đọc: `SHAR-dɪŋ`
  * *Lưu ý*: Âm `/ʃ/` phát âm cong môi dài (như "s" trong tiếng Việt).
* **Nghĩa tiếng Việt**: Phân mảnh dữ liệu theo chiều ngang (chia nhỏ bảng ra nhiều DB physical).
* **Ngữ pháp & Cấu trúc**:
  * `shard a database/table by key`: phân mảnh database/bảng theo key.
  * `shard key design`: thiết kế khóa phân mảnh.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"We are planning to **shard** the orders database by user ID because the table size is approaching 100 million rows."*
  * 🎙️ *Phỏng vấn*: *"**Sharding** is horizontal partitioning. Choosing the right **shard** key is critical to avoid uneven data distribution and hot spots."*

#### 15. **Caching** (n) / **Cache** (n/v)
* **Phát âm**:
  * IPA: `/ˈkæʃ.ɪŋ/` | Phiên âm tiếng Việt dễ đọc: `KASH-ɪŋ`
  * *Lưu ý*: **Từ cực kỳ hay phát âm sai!** Từ *cache* đọc giống từ **cash** (`KASH`). KHÔNG đọc là "cat-che" hay "cay-chê".
* **Nghĩa tiếng Việt**: Lưu trữ đệm.
* **Ngữ pháp & Cấu trúc**:
  * `implement caching`: triển khai lưu trữ đệm.
  * `cache hit vs cache miss`: trúng cache vs hụt cache.
  * `local vs distributed cache`: cache cục bộ vs cache phân tán.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I implemented Redis **caching** for the product catalog, resulting in a 70% decrease in API latency."*
  * 🎙️ *Phỏng vấn*: *"To handle spikes in read traffic, we introduced a **caching** layer with a time-to-live of 10 minutes."*

#### 16. **Latency** (n)
* **Phát âm**:
  * IPA: `/ˈleɪ.tən.si/` | Phiên âm tiếng Việt dễ đọc: `LAY-tən-si`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`LAY`).
* **Nghĩa tiếng Việt**: Độ trễ (thời gian xử lý/phản hồi).
* **Ngữ pháp & Cấu trúc**:
  * `low/high latency`: độ trễ thấp/cao.
  * `reduce/minimize latency`: giảm thiểu độ trễ.
  * `end-to-end latency`: độ trễ từ đầu đến cuối luồng.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"Our end-to-end **latency** dropped by 50ms after we optimized the inter-service gRPC calls."*
  * 🎙️ *Phỏng vấn*: *"For payment systems, keeping API **latency** low under 100 milliseconds is a critical requirement."*

#### 17. **Throughput** (n)
* **Phát âm**:
  * IPA: `/ˈθruː.pʊt/` | Phiên âm tiếng Việt dễ đọc: `THROO-pʊt`
  * *Lưu ý*: Âm `/θ/` phát âm bằng cách đặt đầu lưỡi giữa hai hàm răng và thổi hơi ra. Nhấn âm 1 (`THROO`).
* **Nghĩa tiếng Việt**: Thông lượng (số lượng yêu cầu được xử lý trên giây - QPS/TPS).
* **Ngữ pháp & Cấu trúc**:
  * `high/low throughput`: thông lượng cao/thấp.
  * `system throughput`: thông lượng hệ thống.
  * `improve/increase throughput`: tăng thông lượng.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"During the load test, the payment gateway handled a maximum **throughput** of 2,000 requests per second."*
  * 🎙️ *Phỏng vấn*: *"To scale consumer **throughput**, we increased the number of partitions in Kafka and scaled out the consumer instances."*

#### 18. **Bottleneck** (n) / **Bottleneck** (v)
* **Phát âm**:
  * IPA: `/ˈbɒt.əl.nek/` | Phiên âm tiếng Việt dễ đọc: `BOT-əl-nek`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`BOT`).
* **Nghĩa tiếng Việt**: Điểm nghẽn (nơi giới hạn hiệu năng của toàn hệ thống).
* **Ngữ pháp & Cấu trúc**:
  * `performance bottleneck`: điểm nghẽn hiệu năng.
  * `identify/find a bottleneck`: tìm ra điểm nghẽn.
  * `eliminate/remove a bottleneck`: loại bỏ điểm nghẽn.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"We ran a profiling tool on staging and identified that the database CPU usage was the main **bottleneck**."*
  * 🎙️ *Phỏng vấn*: *"We resolved a major **bottleneck** in our report generation service by processing the files asynchronously using Spring Batch."*

#### 19. **Invalidation** (n) / **Invalidate** (v)
* **Phát âm**:
  * IPA: `/ɪnˌvæl.ɪˈdeɪ.ʃən/` | Phiên âm tiếng Việt dễ đọc: `ɪn-val-ɪ-DAY-ʃən`
  * *Lưu ý*: Trọng âm chính nhấn ở âm tiết thứ 4 (`DAY`). Động từ *invalidate* nhấn âm 2 (`val`).
* **Nghĩa tiếng Việt**: Thu hồi/làm mất hiệu lực (thường dùng cho cache hoặc token/session).
* **Ngữ pháp & Cấu trúc**:
  * `cache invalidation`: làm mới/xóa cache lỗi thời.
  * `invalidate a token/session`: thu hồi token/phiên làm việc.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"We need to trigger a manual cache **invalidation** because some static configurations were updated directly in the DB."*
  * 🎙️ *Phỏng vấn*: *"We implement write-through caching, which simplifies cache **invalidation** because data is updated in the DB and cache simultaneously."*

---

### 🌐 Domain 3: Microservices & Distributed Systems

#### 20. **Decoupling** (n) / **Decouple** (v) / **Decoupled** (adj)
* **Phát âm**:
  * IPA: `/ˌdiːˈkʌp.lɪŋ/` | Phiên âm tiếng Việt dễ đọc: `dee-KUP-lɪŋ`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`KUP`).
* **Nghĩa tiếng Việt**: Gỡ bỏ sự phụ thuộc chặt chẽ (làm cho các thành phần độc lập nhau).
* **Ngữ pháp & Cấu trúc**:
  * `decouple A from B`: tách rời A khỏi B.
  * `loosely decoupled architecture`: kiến trúc liên kết lỏng lẻo độc lập.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"Using Kafka events here **decouples** the Order service from the Inventory service, making them failure-isolated."*
  * 🎙️ *Phỏng vấn*: *"To build a resilient system, we **decoupled** the notification logic from the main purchase flow using an asynchronous event queue."*

#### 21. **Scalability** (n) / **Scale** (n/v) / **Scalable** (adj)
* **Phát âm**:
  * IPA: `/ˌskeɪ.ləˈbɪl.ə.ti/` | Phiên âm tiếng Việt dễ đọc: `skay-lə-BIL-ɪ-ti`
  * *Lưu ý*: Nhấn trọng âm chính ở âm tiết thứ 4 (`BIL`). Tính từ *scalable* nhấn âm 1 (`SKAY`).
* **Nghĩa tiếng Việt**: Khả năng mở rộng.
* **Ngữ pháp & Cấu trúc**:
  * `horizontal/vertical scalability`: khả năng mở rộng theo chiều ngang/dọc.
  * `highly scalable system`: hệ thống có khả năng mở rộng cao.
  * `scale out / scale up`: mở rộng ngang (thêm node) / mở rộng dọc (nâng cấp phần cứng).
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"Our auth service is designed for horizontal **scalability**, allowing Kubernetes to spin up new pods dynamically."*
  * 🎙️ *Phỏng vấn*: *"To ensure system **scalability**, we design all microservices to be stateless, allowing us to **scale** out easily during flash sales."*

#### 22. **Resilience** (n) / **Resilient** (adj)
* **Phát âm**:
  * IPA: `/rɪˈzɪl.jəns/` | Phiên âm tiếng Việt dễ đọc: `rɪ-ZIL-yəns`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`ZIL`). Âm `/s/` ở giữa đọc thành `/z/`.
* **Nghĩa tiếng Việt**: Khả năng phục hồi/chống chịu lỗi (hệ thống tự phục hồi khi có sự cố).
* **Ngữ pháp & Cấu trúc**:
  * `improve/increase system resilience`: nâng cao khả năng tự phục hồi hệ thống.
  * `build resilient services`: xây dựng các dịch vụ chống chịu lỗi tốt.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"We are implementing retry policies and fallback methods to improve the **resilience** of the payment client."*
  * 🎙️ *Phỏng vấn*: *"Designing for failure is key. We build **resilient** microservices by configuring timeouts, retries, and dead-letter queues."*

#### 23. **Circuit Breaker** (n)
* **Phát âm**:
  * IPA: `/ˈsɜː.kɪt ˌbreɪ.kər/` | Phiên âm tiếng Việt dễ đọc: `SUR-kɪt BRAY-kər`
  * *Lưu ý*: Từ *circuit* âm `/c/` thứ 2 bị câm (`SUR-kɪt`), đừng đọc là "sơ-cuýt". Nhấn âm 1 của mỗi từ.
* **Nghĩa tiếng Việt**: Cơ chế ngắt mạch tự động (chống sập hệ thống dây chuyền khi service con lỗi).
* **Ngữ pháp & Cấu trúc**:
  * `implement/configure a circuit breaker`: triển khai/cấu hình circuit breaker.
  * `circuit breaker trips / opens / closes`: mạch ngắt mở ra (ngắt kết nối) / đóng lại (kết nối lại).
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"The **circuit breaker** tripped this morning because the third-party SMS gateway was completely down."*
  * 🎙️ *Phỏng vấn*: *"We use Resilience4j to configure **circuit breakers** on external API calls, preventing downstream downtime from freezing our main application threads."*

#### 24. **Load Balancing** (n) / **Load Balancer** (n)
* **Phát âm**:
  * IPA: `/ˈləʊd ˌbæl.əns.ɪŋ/` | Phiên âm tiếng Việt dễ đọc: `LOHD BAL-əns-ɪŋ`
  * *Lưu ý*: Nhấn trọng âm ở từ *Load* (`LOHD`) và âm 1 của từ *balancing* (`BAL`).
* **Nghĩa tiếng Việt**: Cân bằng tải.
* **Ngữ pháp & Cấu trúc**:
  * `load balancing algorithm`: thuật toán cân bằng tải.
  * `distribute traffic via a load balancer`: phân phối lưu lượng truy cập qua bộ cân bằng tải.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"The **load balancer** is misconfigured, sending all traffic to instance A while instance B is idle."*
  * 🎙️ *Phỏng vấn*: *"We use AWS Application Load Balancer to perform **load balancing**, distributing incoming HTTPS traffic across our ECS tasks dynamically."*

#### 25. **Asynchronous** (adj) / **Asynchronously** (adv)
* **Phát âm**:
  * IPA: `/eɪˈsɪŋ.krə.nəs/` | Phiên âm tiếng Việt dễ đọc: `ay-SING-krə-nəs`
  * *Lưu ý*: Chữ đầu đọc là `ay` (bất). Nhấn trọng âm ở âm tiết thứ 2 (`SING`).
* **Nghĩa tiếng Việt**: Bất đồng bộ.
* **Ngữ pháp & Cấu trúc**:
  * `asynchronous communication/processing`: giao tiếp/xử lý bất đồng bộ.
  * `send/process messages asynchronously`: gửi/xử lý tin nhắn bất đồng bộ.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I am moving the invoice generation logic to **asynchronous** processing to avoid blocking the user checkout API."*
  * 🎙️ *Phỏng vấn*: *"For inter-service communication, we prefer **asynchronous** messaging using Apache Kafka over synchronous HTTP calls to reduce coupling."*

#### 26. **Payload** (n)
* **Phát âm**:
  * IPA: `/ˈpeɪ.ləʊd/` | Phiên âm tiếng Việt dễ đọc: `PAY-lohd`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`PAY`).
* **Nghĩa tiếng Việt**: Phần dữ liệu hữu ích/nội dung thực tế của gói tin (không tính header/metadata).
* **Ngữ pháp & Cấu trúc**:
  * `request/response payload`: nội dung yêu cầu/phản hồi.
  * `payload size`: kích thước dữ liệu.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"The request **payload** is too large; we should remove these redundant fields to optimize network utilization."*
  * 🎙️ *Phỏng vấn*: *"We compress our message broker **payloads** using Avro serialization to minimize network bandwidth consumption."*

#### 27. **Routing** (n) / **Route** (n/v)
* **Phát âm**:
  * IPA: `/ˈruː.tɪŋ/` hoặc `/ˈraʊ.tɪŋ/` | Phiên âm tiếng Việt dễ đọc: `ROO-tɪŋ` hoặc `ROW-tɪŋ`
  * *Lưu ý*: Cả hai cách phát âm đều đúng, người Mỹ hay đọc là `ROW-tɪŋ`, người Anh hay đọc là `ROO-tɪŋ`.
* **Nghĩa tiếng Việt**: Định tuyến.
* **Ngữ pháp & Cấu trúc**:
  * `routing rules/table`: luật/bảng định tuyến.
  * `route requests to a microservice`: định tuyến yêu cầu đến một microservice.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I configured the API Gateway **routing** rules to forward path `/orders/**` directly to the Order microservice."*
  * 🎙️ *Phỏng vấn*: *"Spring Cloud Gateway handles dynamic **routing** and filters incoming requests for authentication and rate-limiting."*

#### 28. **Fault Tolerance** (n) / **Fault-tolerant** (adj)
* **Phát âm**:
  * IPA: `/ˈdɪ-HER-ɪ-təns/` là sai chính tả của `inheritance`, đúng phải là `/ɪnˈher.ɪ.təns/`.
  * IPA: `/ˈfɒlt ˌtɒl.ər.əns/` | Phiên âm tiếng Việt dễ đọc: `FAWLT TOL-ər-əns`
  * *Lưu ý*: Trọng âm nhấn ở từ *Fault* (`FAWLT`) và âm 1 của từ *tolerance* (`TOL`).
* **Nghĩa tiếng Việt**: Khả năng chịu lỗi (hệ thống vẫn chạy bình thường kể cả khi một vài phần cứng/phần mềm bị hỏng).
* **Ngữ pháp & Cấu trúc**:
  * `design a fault-tolerant system`: thiết kế hệ thống chịu lỗi.
  * `fault tolerance mechanism`: cơ chế chịu lỗi (như clustering, failover).
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"We verified our **fault tolerance** yesterday by killing one database node; the backup database took over immediately with zero downtime."*
  * 🎙️ *Phỏng vấn*: *"To achieve high **fault tolerance**, we deploy our application instances across multiple availability zones in AWS."*

---

### ☁️ Domain 4: DevOps, CI/CD & Cloud

#### 29. **Pipeline** (n)
* **Phát âm**:
  * IPA: `/ˈpaɪp.laɪn/` | Phiên âm tiếng Việt dễ đọc: `PIPE-layn`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`PIPE`).
* **Nghĩa tiếng Việt**: Đường ống (chuỗi các bước tự động hóa trong CI/CD).
* **Ngữ pháp & Cấu trúc**:
  * `CI/CD pipeline`: đường ống tích hợp và triển khai tự động.
  * `pipeline execution/run`: lượt thực thi đường ống.
  * `fail/pass the pipeline`: trượt/đỗ quy trình build.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"The CI/CD **pipeline** is failing at the SonarQube stage because the test coverage fell below the 80% threshold."*
  * 🎙️ *Phỏng vấn*: *"I built a deployment **pipeline** using Jenkins that automatically builds, tests, and deploys Java artifacts to Docker registry."*

#### 30. **Deployment** (n) / **Deploy** (v)
* **Phát âm**:
  * IPA: `/dɪˈplɔɪ.mənt/` | Phiên âm tiếng Việt dễ đọc: `dɪ-PLOY-mənt`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`PLOY`).
* **Nghĩa tiếng Việt**: Triển khai (đưa code lên môi trường chạy thực tế).
* **Ngữ pháp & Cấu trúc**:
  * `deploy code/application to production/staging`: triển khai code lên production/staging.
  * `automated deployment`: triển khai tự động.
  * `deployment strategy (blue-green, canary)`: chiến lược triển khai (blue-green, canary).
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"We are planning a production **deployment** tonight at 11 PM. Please freeze all code changes by 6 PM."*
  * 🎙️ *Phỏng vấn*: *"In my previous company, we moved from bi-weekly manual **deployments** to automated rolling updates twice a day."*

#### 31. **Containerization** (n) / **Containerize** (v) / **Container** (n)
* **Phát âm**:
  * IPA: `/kənˈteɪ.nə.raɪ.zɪŋ/` | Phiên âm tiếng Việt dễ đọc: `kən-TAY-nər-eye-ZAY-ʃən`
  * *Lưu ý*: Nhấn trọng âm chính ở âm tiết thứ 5 (`ZAY`).
* **Nghĩa tiếng Việt**: Container hóa (đóng gói ứng dụng kèm môi trường của nó).
* **Ngữ pháp & Cấu trúc**:
  * `containerize a Spring Boot application`: container hóa ứng dụng Spring Boot.
  * `run/stop a Docker container`: chạy/dừng container Docker.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I have **containerized** the backend service using a multi-stage Dockerfile to optimize the final image size."*
  * 🎙️ *Phỏng vấn*: *"**Containerization** ensures that our application runs identically in local development, staging, and production environments."*

#### 32. **Orchestration** (n) / **Orchestrate** (v)
* **Phát âm**:
  * IPA: `/ˌɔː.kɪˈstreɪ.ʃən/` | Phiên âm tiếng Việt dễ đọc: `or-kɪs-TRAY-ʃən`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 3 (`TRAY`).
* **Nghĩa tiếng Việt**: Điều phối (quản lý tự động vòng đời container hoặc luồng dữ liệu).
* **Ngữ pháp & Cấu trúc**:
  * `container orchestration`: điều phối container (ví dụ bằng Kubernetes).
  * `orchestrate microservices workflow`: điều phối quy trình hoạt động microservices.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"We are migrating our application **orchestration** from Docker Swarm to Kubernetes for better auto-scaling."*
  * 🎙️ *Phỏng vấn*: *"Kubernetes provides container **orchestration**, managing resource allocation, horizontal scaling, and service healing."*

#### 33. **Provisioning** (n) / **Provision** (v)
* **Phát âm**:
  * IPA: `/prəˈvɪʒ.ən.ɪŋ/` | Phiên âm tiếng Việt dễ đọc: `prə-VIZH-ən-ɪŋ`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 2 (`VIZH`).
* **Nghĩa tiếng Việt**: Cung cấp/khởi tạo tài nguyên hệ thống (như tạo VM, DB trên Cloud).
* **Ngữ pháp & Cấu trúc**:
  * `provision server instances/resources`: cung cấp tài nguyên máy chủ.
  * `automated resource provisioning`: tự động khởi tạo tài nguyên.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"I have **provisioned** a new Redis instance in our AWS staging environment for the performance tests."*
  * 🎙️ *Phỏng vấn*: *"We use Terraform to automate the **provisioning** of our cloud infrastructure, guaranteeing consistency and speed."*

#### 34. **Rollback** (n/v)
* **Phát âm**:
  * IPA: `/ˈrəʊl.bæk/` | Phiên âm tiếng Việt dễ đọc: `ROHL-bak`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`ROHL`).
* **Nghĩa tiếng Việt**: Quay lui (khôi phục lại phiên bản stable trước đó khi bản mới bị lỗi).
* **Ngữ pháp & Cấu trúc**:
  * `trigger/perform a rollback`: thực hiện quay lui.
  * `rollback a deployment/database migration`: quay lui bản deploy/migration DB.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack / Incident*: *"Due to a new bug in release v2.1, we had to trigger an immediate **rollback** to v2.0."*
  * 🎙️ *Phỏng vấn*: *"Our Kubernetes setup supports rolling **rollbacks**; if a new deployment fails health checks, it reverts to the old state."*

#### 35. **Telemetry** (n)
* **Phát âm**:
  * IPA: `/təˈlem.ə.tri/` | Phiên âm tiếng Việt dễ đọc: `tə-LEM-ə-tri`
  * *Lưu ý*: **Rất hay phát âm sai!** Nhấn trọng âm ở âm tiết thứ 2 (`LEM`). KHÔNG đọc là "tele-met-ry".
* **Nghĩa tiếng Việt**: Đo lường từ xa (thu thập logs, metrics, traces từ hệ thống về).
* **Ngữ pháp & Cấu trúc**:
  * `telemetry data (logs, metrics, traces)`: dữ liệu đo lường từ xa.
  * `collect telemetry data`: thu thập dữ liệu đo lường.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I configured OpenTelemetry in our microservices to export tracing metrics to Prometheus and Grafana."*
  * 🎙️ *Phỏng vấn*: *"**Telemetry** is crucial for distributed systems. Without standard traces and logs, debugging inter-service issues is extremely difficult."*

---

### 🏃 Domain 5: Scrum Agile & Daily Operations

#### 36. **Standup** (n)
* **Phát âm**:
  * IPA: `/ˈstænd.ʌp/` | Phiên âm tiếng Việt dễ đọc: `STAND-up`
  * *Lưu ý*: Nhấn trọng âm chính ở âm tiết thứ 1 (`STAND`).
* **Nghĩa tiếng Việt**: Buổi họp ngắn hàng ngày của team Scrum (15 phút).
* **Ngữ pháp & Cấu trúc**:
  * `daily standup`: họp hàng ngày.
  * `give standup updates`: cập nhật báo cáo standup.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"Hi team, let's join the daily **standup** link in 5 minutes."*
  * 🎙️ *Phỏng vấn*: *"In our daily **standup**, we keep our updates concise: what we did yesterday, what we'll do today, and our blockers."*

#### 37. **Sprint** (n)
* **Phát âm**:
  * IPA: `/sprɪnt/` | Phiên âm tiếng Việt dễ đọc: `sprint`
  * *Lưu ý*: Bật rõ phụ âm cuối `/t/`.
* **Nghĩa tiếng Việt**: Sprint (chu kỳ phát triển ngắn, thường kéo dài 2 tuần).
* **Ngữ pháp & Cấu trúc**:
  * `sprint planning / retrospective`: lập kế hoạch sprint / họp cải tiến sprint.
  * `sprint backlog`: danh sách task cam kết cho sprint.
  * `during/in this sprint`: trong sprint này.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I committed to finishing the security ticket in this **sprint**, and it's already in code review."*
  * 🎙️ *Phỏng vấn*: *"Our team works in two-week **sprints**. We use retrospectives at the end of each **sprint** to optimize our delivery process."*

#### 38. **Backlog** (n)
* **Phát âm**:
  * IPA: `/ˈbæk.lɒɡ/` | Phiên âm tiếng Việt dễ đọc: `BAK-log`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`BAK`), phát âm rõ âm `/g/` cuối.
* **Nghĩa tiếng Việt**: Danh sách các tính năng/lỗi cần xử lý trong tương lai.
* **Ngữ pháp & Cấu trúc**:
  * `product/sprint backlog`: danh sách việc của sản phẩm / của sprint.
  * `backlog grooming/refinement`: cuộc họp làm mịn/làm rõ backlog.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"Let's add this minor UI improvement to the product **backlog** and prioritize it for the next sprint."*
  * 🎙️ *Phỏng vấn*: *"During **backlog** refinement sessions, I work closely with the Product Owner to evaluate technical feasibility and effort."*

#### 39. **Estimation** (n) / **Estimate** (v)
* **Phát âm**:
  * IPA: `/ˌes.tɪˈmeɪ.ʃən/` | Phiên âm tiếng Việt dễ đọc: `es-tɪ-MAY-ʃən`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 3 (`MAY`). Động từ *estimate* phát âm là `ES-tɪ-mayt` (nhấn âm 1).
* **Nghĩa tiếng Việt**: Sự ước lượng (thời gian, công sức làm task).
* **Ngữ pháp & Cấu trúc**:
  * `provide/give an estimation for a task`: đưa ra ước lượng cho một task.
  * `story point estimation`: ước lượng theo điểm độ khó.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I will need to investigate the legacy code logic before providing a final **estimation** for this integration ticket."*
  * 🎙️ *Phỏng vấn*: *"For task **estimation**, our team uses Story Points based on the Fibonacci sequence to reflect complexity and risk."*

#### 40. **Blocker** (n) / **Block** (v) / **Blocked** (adj)
* **Phát âm**:
  * IPA: `/ˈblɒk.ər/` | Phiên âm tiếng Việt dễ đọc: `BLOK-ər`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`BLOK`). Tính từ *blocked* bật âm `/t/` cuối (`BLOKT`).
* **Nghĩa tiếng Việt**: Điểm nghẽn/vấn đề gây cản trở công việc không thể tiếp tục.
* **Ngữ pháp & Cấu trúc**:
  * `have a blocker`: gặp blocker cản trở.
  * `blocked by something/someone`: bị nghẽn bởi cái gì/ai đó.
  * `unblock someone`: gỡ nghẽn giúp ai đó.
* **Mẫu câu giao tiếp**:
  * 💬 *Standup*: *"I have a **blocker** on the database setup. I'm waiting for the DevOps team to grant me administrative access."*
  * 🎙️ *Phỏng vấn*: *"Whenever I am **blocked**, I immediately escalate the issue on Slack and collaborate with teammates to resolve it quickly."*

#### 41. **Refactoring** (n) / **Refactor** (v)
* **Phát âm**:
  * IPA: `/ˌriːˈfæk.tər.ɪŋ/` | Phiên âm tiếng Việt dễ đọc: `ree-FAK-tər-ɪŋ`
  * *Lưu ý*: Nhấn trọng âm chính ở âm tiết thứ 2 (`FAK`).
* **Nghĩa tiếng Việt**: Tái cấu trúc mã nguồn (cải thiện thiết kế bên trong mà không làm đổi hành vi bên ngoài).
* **Ngữ pháp & Cấu trúc**:
  * `code refactoring`: tái cấu trúc code.
  * `refactor a class/method/logic`: tái cấu trúc một class/method/logic.
* **Mẫu câu giao tiếp**:
  * 💬 *Code Review*: *"This helper method is growing too complex; we should **refactor** it by breaking it down into smaller functions."*
  * 🎙️ *Phỏng vấn*: *"I spent two days **refactoring** the notification subsystem, which improved readability and reduced future maintenance costs."*

#### 42. **Code Review** (n)
* **Phát âm**:
  * IPA: `/kəʊd rɪˈvjuː/` | Phiên âm tiếng Việt dễ đọc: `KOHD rɪ-VYOO`
  * *Lưu ý*: Nhấn trọng âm chính ở từ *Review* (`VYOO`).
* **Nghĩa tiếng Việt**: Duyệt mã nguồn.
* **Ngữ pháp & Cấu trúc**:
  * `submit code for code review`: gửi code để review.
  * `request a code review`: yêu cầu duyệt code.
  * `address code review comments`: sửa code theo nhận xét review.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"I've submitted my PR for the authentication logic. Can anyone help me with a **code review**?"*
  * 🎙️ *Phỏng vấn*: *"In my team, every feature must pass **code review** by at least two senior engineers before merging into the main branch."*

#### 43. **Hotfix** (n/v)
* **Phát âm**:
  * IPA: `/ˈhɒt.fɪks/` | Phiên âm tiếng Việt dễ đọc: `HOT-fɪks`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`HOT`). Bật rõ âm `/s/` cuối.
* **Nghĩa tiếng Việt**: Bản sửa lỗi khẩn cấp (sửa trực tiếp lên production).
* **Ngữ pháp & Cấu trúc**:
  * `deploy/apply a hotfix`: triển khai bản sửa lỗi khẩn cấp.
  * `create a hotfix branch`: tạo nhánh hotfix.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"We identified a critical null pointer exception in production. I'm preparing a **hotfix** deployment now."*
  * 🎙️ *Phỏng vấn*: *"When a production issue occurs, we immediately create a **hotfix** from the main branch, run regression tests, and deploy."*

#### 44. **Incident** (n)
* **Phát âm**:
  * IPA: `/ˈɪn.sɪ.dənt/` | Phiên âm tiếng Việt dễ đọc: `IN-sɪ-dənt`
  * *Lưu ý*: Nhấn trọng âm ở âm tiết thứ 1 (`IN`). Bật rõ phụ âm cuối `/t/`.
* **Nghĩa tiếng Việt**: Sự cố hệ thống (ngừng hoạt động hoặc lỗi nghiêm trọng ảnh hưởng người dùng).
* **Ngữ pháp & Cấu trúc**:
  * `report/log an incident`: báo cáo sự cố.
  * `resolve a system incident`: khắc phục sự cố hệ thống.
  * `incident post-mortem / root cause analysis (RCA)`: phân tích nguyên nhân gốc rễ sau sự cố.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"The DBA team is currently investigating a database **incident** that caused a 10-minute outage."*
  * 🎙️ *Phỏng vấn*: *"During critical **incidents**, our first priority is to restore service, followed by writing a detailed post-mortem to prevent recurrence."*

#### 45. **Pull Request** (PR) (n)
* **Phát âm**:
  * IPA: `/pʊl rɪˈkwest/` | Phiên âm tiếng Việt dễ đọc: `PUL rɪ-KWEST`
  * *Lưu ý*: Bật hơi rõ phụ âm cuối `/st/` của từ request. Nhấn trọng âm ở từ *request* (`KWEST`).
* **Nghĩa tiếng Việt**: Yêu cầu sáp nhập code (trong Git).
* **Ngữ pháp & Cấu trúc**:
  * `open/create a pull request`: mở/tạo PR.
  * `merge a pull request`: sáp nhập PR.
  * `approve a pull request`: phê duyệt PR.
* **Mẫu câu giao tiếp**:
  * 💬 *Slack*: *"I have resolved all the comment changes in my **pull request**, it is ready for another review."*
  * 🎙️ *Phỏng vấn*: *"We run automated integration tests on every **pull request** using GitHub Actions before it can be merged."*

---

## 🟢 PHẦN 2: NGỮ PHÁP ỨNG DỤNG (GRAMMAR IN CONTEXT)

Để giao tiếp tiếng Anh IT tự tin, bạn không cần ngữ pháp cao siêu, chỉ cần nắm vững và phối hợp chính xác **3 chủ điểm ngữ pháp** dưới đây.

### 1. Phối hợp Thì khi Báo Cáo Standup (Standup Tense Integration)
* **Quy tắc vàng 3 thì**:
  * **Hôm qua (Đã hoàn thành)**: Dùng **Past Simple** (Quá khứ đơn) để báo cáo hành động đã xong và có mốc thời gian rõ ràng.
  * **Hôm nay (Kế hoạch)**: Dùng **Be going to** (Kế hoạch định trước) hoặc **Present Continuous** (Việc đang tiến hành) để báo cáo.
  * **Vấn đề / Blockers**: Dùng **Present Simple** (Trạng thái hiện tại) hoặc **Present Perfect** (Việc vừa xảy ra ảnh hưởng hiện tại).

* *Ví dụ template*:
> *"Yesterday, I **optimized** the query execution path and **reduced** response time by 40%. Today, I **am going to implement** the integration test suite. I **have a blocker** because I **haven't received** the API specifications from the third-party provider yet."*

### 2. Thể Bị Động khi Viết Docs & Log (Passive Voice in Tech Documentation)
Trong IT, hành động tác động lên hệ thống/dữ liệu quan trọng hơn người thực hiện. Do đó, hãy dùng **Passive Voice** để câu nói trở nên khách quan và chuyên nghiệp.
* *Công thức*: `Object + Be + V3 (V-ed)`
* *Ví dụ ứng dụng*:
  * Cấu hình bảo mật: *"Passwords **are hashed** with BCrypt and **stored** securely."* (Thay vì: *"We hash passwords..."*)
  * Giải thích luồng: *"When a message **is published** to Kafka, it **is consumed** by the order service."*
  * Báo cáo lỗi: *"The user profile picture **was deleted** due to a database sync failure."*

### 3. Câu Điều Kiện cho System Design & Trade-offs (Conditionals)
Khi phỏng vấn thiết kế hệ thống, bạn cần so sánh phương án và giải thích lý do lựa chọn. Sử dụng câu điều kiện loại 1 và loại 2 để phân tích linh hoạt.
* **Câu điều kiện loại 1 (Giả định thực tế)**: `If + Present Simple, S + Will + V`
  * Dùng khi phân tích tình huống thực tế hoặc sự lựa chọn khả thi trong Sprint.
  * *Ví dụ*: *"If we **use** Redis, the system **will load** the homepage within 50 milliseconds."*
* **Câu điều kiện loại 2 (Giả định giả định/Trái thực tế hiện tại)**: `If + Past Simple, S + Would + V`
  * Dùng khi đưa ra so sánh trade-off, phân tích kịch bản chưa xảy ra.
  * *Ví dụ*: *"If we **deployed** this system as a monolith, it **would be** easier to build initially, but scaling individual services **would be** extremely difficult."*

---

## 🟢 PHẦN 3: KỊCH BẢN GIAO TIẾP MẪU THEO TÌNH HUỐNG (REAL-WORLD SCRIPTS)

### 🎬 Tình huống 1: Nhận xét Code Review tinh tế & lịch sự
Tránh viết: *"Your code is slow, fix it."* (Quá thô lỗ, thiếu chuyên nghiệp).
Hãy viết theo mẫu sử dụng câu hỏi khuyết thiếu (Modal verbs) và câu điều kiện:

* **Mẫu 1**: *"Instead of querying the database inside the loop, **could we** fetch all records once using `findAllById`? This **would reduce** the database round-trips from N to 1."*
* **Mẫu 2**: *"I noticed that this connection is not closed in a finally block. **It would be safer if** we used a try-with-resources statement to avoid resource leaks."*
* **Mẫu 3**: *"**Should we** extract this hardcoded timeout value to the application configuration file? It **will make** tuning performance much easier in production."*

### 🚨 Tình huống 2: Báo cáo sự cố khẩn cấp trên Slack (Incident Alert)
Khi hệ thống bị sập, bạn cần thông tin nhanh gọn, cấu trúc rõ ràng: Trạng thái hiện tại ➔ Ảnh hưởng ➔ Hành động khắc phục ➔ Hẹn giờ cập nhật tiếp theo.

* **Mẫu câu**:
> *"@here We **are currently experiencing** a service outage on the Payment service. Users **are seeing** HTTP 500 errors when checkout. The DevOps team **has isolated** the issue to database connection pool exhaustion. We **are restarting** the service instances with increased pool size. Next update in **10 minutes**."*

### 🎙️ Tình huống 3: Trả lời phỏng vấn Technical Q&A theo khung RESHADED
Khi interviewer hỏi: *"Explain how indexing works in databases."* Hãy trả lời theo cấu trúc 4 bước:

1. **Definition (Định nghĩa)**: *"A database index is a data structure, typically a B-Tree, that **improves** data retrieval speed on a table."*
2. **Mechanism (Cơ chế)**: *"The way it works is that instead of performing a full-table scan, the database **searches** the index structure first to find the exact row pointers, then **fetches** the actual data."*
3. **Application (Ứng dụng thực tế)**: *"In my previous project, we had a query that took over 3 seconds to fetch order history. I **added** a composite index on `user_id` and `order_date`, which **reduced** execution time to under 10 milliseconds."*
4. **Trade-offs (Đánh đổi)**: *"However, the trade-off is write overhead. Every time we insert or update a row, the database must also update the index, which **can slow down** high-write systems."*

---

## 📅 BẢNG ÔN LUYỆN NHANH (QUICK REVIEW MATRIX)

*Hãy che cột bên phải, nhìn cột bên trái để tập phản xạ dịch nhanh và nói to thành tiếng.*

| Tình huống / Ý muốn diễn đạt | Câu Tiếng Anh Sẵn Sàng Dùng | Điểm Ngữ Pháp Cần Nhớ |
|:---|:---|:---|
| Báo cáo Standup (Hôm qua làm gì) | *"Yesterday, I **refactored** the authentication flow."* | Past Simple (`refactored`) |
| Báo cáo Standup (Hôm nay sẽ làm) | *"Today, I **am going to implement** the caching layer."* | Be going to |
| Báo cáo Standup (Bị nghẽn) | *"I am **blocked by** the gateway configuration ticket."* | Passive Voice (`blocked by`) |
| Phản đối ý kiến thiết kế lịch sự | *"I see your point, but **what if** we used Kafka instead?"* | Type 2 Conditional (`what if we used`) |
| Giải thích cơ chế bảo mật dữ liệu | *"Sensitive payloads **are encrypted** using AES-256."* | Passive Voice (`are encrypted`) |
| Nói về lỗi trễ hạn do bên thứ 3 | *"The API integration **was delayed** because the vendor was down."* | Past Simple + Passive |
| So sánh REST và gRPC | *"gRPC is **faster and more lightweight than** REST over HTTP/1."* | Comparative (`faster than`) |
| Trả lời khi không nghe rõ câu hỏi | *"Could you please **rephrase** that? I want to be precise."* | Polite request (`Could you`) |
| Xin thêm thời gian suy nghĩ | *"That's a great question. Let me take a moment to **structure my thoughts**."* | Idiomatic phrase |
| Giải thích kinh nghiệm Java | *"I **have worked** with Java and Spring Boot for 4 years."* | Present Perfect (`have worked`) |

---

> [!TIP]
> **Kế hoạch thực chiến hàng ngày**: 
> 1. Mỗi sáng dành **10 phút** đọc to 1 Domain (10 từ).
> 2. Đọc to 3 lần mỗi câu mẫu.
> 3. Tự đặt lại 1 câu khác của riêng mình cho mỗi từ.
> 4. Record bằng điện thoại và nghe lại xem phát âm có rõ phụ âm cuối hay không.
