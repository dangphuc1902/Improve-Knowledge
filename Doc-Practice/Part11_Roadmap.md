# PHẦN 11: LỘ TRÌNH PHÁT TRIỂN (ROADMAP TO SENIOR)

Trở thành một Senior không chỉ là số năm kinh nghiệm, mà là khả năng làm chủ sự phức tạp và đưa ra quyết định đúng đắn. Dưới đây là lộ trình "thực chiến".

---

## Giai đoạn 1: Master the Language (Tháng 1-6)
- **Mục tiêu**: Code sạch, hiểu sâu công cụ.
- **Kiến thức**: Java Core, JVM internals, Multi-threading cơ bản, Unit Test (JUnit/Mockito).
- **Hành động**: Đọc cuốn "Effective Java" và thực hành Object Pooling.

## Giai đoạn 2: Infrastructure & Communication (Tháng 7-12)
- **Mục tiêu**: Xây dựng nền tảng kết nối.
- **Kiến thức**: Network sockets, Netty, Protobuf, gRPC, Redis cơ bản.
- **Hành động**: Tự viết một chương trình Chat đơn giản dùng Netty và Protobuf.

## Giai đoạn 3: Distributed Systems (Năm 2)
- **Mục tiêu**: Giải quyết bài toán quy mô lớn.
- **Kiến thức**: Kafka, Microservices patterns, Database Indexing/Sharding, K8s & Docker.
- **Hành động**: Tham gia thiết kế hệ thống Matchmaking hoặc nạp tiền quy mô triệu CCU.

## Giai đoạn 4: System Architect (Năm 3+)
- **Mục tiêu**: Tư duy hệ thống và tối ưu hóa.
- **Kiến thức**: System Design patterns, GC Tuning, Cloud Computing, High Availability.
- **Hành động**: Đọc cuốn "Designing Data-Intensive Applications".

---

## Lời khuyên vàng từ Senior:
1.  **Học lý thuyết song song thực hành**: Đừng chỉ đọc, hãy gõ code.
2.  **Hiểu tại sao (The Why)**: Luôn đặt câu hỏi: "Tại sao dùng Kafka mà không phải RabbitMQ?".
3.  **Tập trung vào Fundamentals**: Công nghệ thay đổi liên tục, nhưng kiến thức về Network, OS, Data Structure thì trường tồn.
4.  **Học cách Debug**: Một Senior giỏi là người có thể tìm ra bug OOM hay Race Condition chỉ qua vài dòng log.
