# Message Queues and Event Brokers
# Hàng đợi tin nhắn và nhà môi giới sự kiện

## Concept Explanation
## Giải thích khái niệm
Message queues and event brokers are technologies used for asynchronous communication between microservices, allowing them to communicate reliably without being tightly coupled.
Hàng đợi tin nhắn và nhà môi giới sự kiện là những công nghệ được sử dụng để giao tiếp không đồng bộ giữa các vi dịch vụ, cho phép chúng giao tiếp một cách đáng tin cậy mà không bị ràng buộc chặt chẽ.

### Message Queues vs Pub/Sub
### Hàng đợi tin nhắn và Pub/Sub
- **Point-to-Point (Queue)**: A producer sends a message to a queue. ONE consumer pulls the message and processes it. Once processed, it is removed. Useful for job distribution (e.g., sending emails, resizing images).
- **Điểm-đến-Điểm (Hàng đợi)**: Một nhà sản xuất gửi một tin nhắn đến một hàng đợi. MỘT người tiêu dùng kéo tin nhắn và xử lý nó. Sau khi được xử lý, nó sẽ bị xóa. Hữu ích cho việc phân phối công việc (ví dụ: gửi email, thay đổi kích thước hình ảnh).
  - *Example technology*: RabbitMQ, Amazon SQS.
  - *Công nghệ ví dụ*: RabbitMQ, Amazon SQS.
- **Publish/Subscribe (Topic)**: A producer publishes a message to a Topic. MULTIPLE subscribers listening to that topic each receive a copy of the message. Useful for event broadcasting (e.g., "UserPurchasedItem" event triggering an Inventory service and a Billing service).
- **Xuất bản/Đăng ký (Chủ đề)**: Một nhà sản xuất xuất bản một tin nhắn cho một Chủ đề. NHIỀU người đăng ký lắng nghe chủ đề đó đều nhận được một bản sao của tin nhắn. Hữu ích cho việc phát sóng sự kiện (ví dụ: sự kiện "UserPurchasedItem" kích hoạt một dịch vụ Hàng tồn kho và một dịch vụ Thanh toán).
  - *Example technology*: Apache Kafka, Amazon SNS.
  - *Công nghệ ví dụ*: Apache Kafka, Amazon SNS.

### Why use them?
### Tại sao sử dụng chúng?
1. **Decoupling**: Service A doesn't need to know about Service B. It just publishes an event.
1. **Tách rời**: Dịch vụ A không cần biết về Dịch vụ B. Nó chỉ xuất bản một sự kiện.
2. **Fault Tolerance**: If the consumer service goes down, messages queue up safely until it comes back online.
2. **Khả năng chịu lỗi**: Nếu dịch vụ của người tiêu dùng ngừng hoạt động, các tin nhắn sẽ xếp hàng một cách an toàn cho đến khi nó hoạt động trở lại.
3. **Spike Handling (Load Leveling)**: If 10,000 users upload images simultaneously, the Web layer simply drops 10k messages into the queue. The backend processors pull them at their own pace without crashing.
3. **Xử lý đột biến (San bằng tải)**: Nếu 10.000 người dùng tải lên hình ảnh đồng thời, lớp Web chỉ cần thả 10 nghìn tin nhắn vào hàng đợi. Các bộ xử lý backend kéo chúng theo tốc độ của riêng chúng mà không bị treo.

## System Design Diagram
## Sơ đồ thiết kế hệ thống

```mermaid
graph TD
    Web[Web App API] -->|Publishes "UploadEvent"| Q[(Message Queue <br> e.g. RabbitMQ)]
    
    Q -->|Pulls message| Work1[Worker Server 1]
    Q -->|Pulls message| Work2[Worker Server 2]
    
    Work1 -->|Saves Result| DB[(Database)]
    Work2 -->|Saves Result| DB
    
    style Web fill:#bbf
    style Q fill:#f96
    style Work1 fill:#bfb
    style Work2 fill:#bfb
```

## Practical Example (RabbitMQ concept)
## Ví dụ thực tế (khái niệm RabbitMQ)
Producer logic typically looks like:
Logic của nhà sản xuất thường trông như sau:
```java
// Spring Boot RabbitMQ Producer
// Nhà sản xuất RabbitMQ Spring Boot
rabbitTemplate.convertAndSend("exchange_name", "routing_key", new UploadEvent(userId, imageFile));
```

Consumer logic typically looks like:
Logic của người tiêu dùng thường trông như sau:
```java
// Spring Boot RabbitMQ Consumer
// Người tiêu dùng RabbitMQ Spring Boot
@RabbitListener(queues = "image_processing_queue")
public void processImage(UploadEvent event) {
    // This runs asynchronously in the background worker
    // Điều này chạy không đồng bộ trong trình làm việc nền
    imageProcessingService.resize(event);
}
```

## Exercises
## Bài tập
1. What is an "Exchange" in the context of RabbitMQ? How does it differ from a Queue?
1. "Exchange" trong bối cảnh của RabbitMQ là gì? Nó khác với Hàng đợi như thế nào?
2. Research Apache Kafka. Why is Kafka fundamentally different from RabbitMQ? (Hint: Kafka is an append-only log, it doesn't delete messages right after consumption).
2. Nghiên cứu Apache Kafka. Tại sao Kafka về cơ bản khác với RabbitMQ? (Gợi ý: Kafka là một nhật ký chỉ nối thêm, nó không xóa tin nhắn ngay sau khi tiêu thụ).
3. What is a "Dead Letter Queue" (DLQ) and what is its purpose in error handling?
3. "Hàng đợi thư chết" (DLQ) là gì và mục đích của nó trong việc xử lý lỗi là gì?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Understand idempotency. If a message queue accidentally delivers the same message twice (At-Least-Once delivery), how does your consumer ensure it doesn't process the payment twice?
- Hiểu tính bất biến. Nếu một hàng đợi tin nhắn vô tình gửi cùng một tin nhắn hai lần (phân phối ít nhất một lần), làm thế nào để người tiêu dùng của bạn đảm bảo nó không xử lý thanh toán hai lần?
- Differentiate between "At-Most-Once", "At-Least-Once", and "Exactly-Once" delivery semantics.
- Phân biệt giữa các ngữ nghĩa phân phối "Tối đa một lần", "Ít nhất một lần" và "Chính xác một lần".
