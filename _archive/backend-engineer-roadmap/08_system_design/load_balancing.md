# Load Balancing
# Cân bằng tải

## Concept Explanation
## Giải thích khái niệm
As a website's traffic grows, a single server will eventually be overwhelmed. The solution is to add more servers (Horizontal Scaling). A Load Balancer (LB) is a device or software that distributes incoming network traffic across a group of backend servers.
Khi lưu lượng truy cập của một trang web tăng lên, một máy chủ duy nhất cuối cùng sẽ bị quá tải. Giải pháp là thêm nhiều máy chủ hơn (Mở rộng theo chiều ngang). Bộ cân bằng tải (LB) là một thiết bị hoặc phần mềm phân phối lưu lượng truy cập mạng đến trên một nhóm các máy chủ backend.

### Benefits
### Lợi ích
1. **Increased Capacity**: Distributes load to handle millions of requests.
1. **Tăng dung lượng**: Phân phối tải để xử lý hàng triệu yêu cầu.
2. **High Availability**: If one server crashes, the load balancer redirects traffic to the remaining healthy servers.
2. **Tính sẵn sàng cao**: Nếu một máy chủ bị lỗi, bộ cân bằng tải sẽ chuyển hướng lưu lượng truy cập đến các máy chủ khỏe mạnh còn lại.
3. **Flexibility**: Servers can be added or removed seamlessly without users noticing.
3. **Tính linh hoạt**: Các máy chủ có thể được thêm hoặc xóa một cách liền mạch mà người dùng không nhận thấy.

### Common Load Balancing Algorithms
### Các thuật toán cân bằng tải phổ biến
1. **Round Robin**: Requests are distributed across the group of servers sequentially.
1. **Round Robin**: Các yêu cầu được phân phối trên nhóm máy chủ một cách tuần tự.
2. **Least Connections**: Sends requests to the server with the fewest current active connections. Useful if queries take varying amounts of time.
2. **Kết nối ít nhất**: Gửi yêu cầu đến máy chủ có ít kết nối hoạt động hiện tại nhất. Hữu ích nếu các truy vấn mất lượng thời gian khác nhau.
3. **IP Hash**: The IP address of the client is hashed to determine which server receives the request. This guarantees a specific user always hits the same server (useful for session persistence, though external caching is better).
3. **IP Hash**: Địa chỉ IP của máy khách được băm để xác định máy chủ nào nhận yêu cầu. Điều này đảm bảo một người dùng cụ thể luôn truy cập cùng một máy chủ (hữu ích cho việc duy trì phiên, mặc dù bộ nhớ đệm bên ngoài tốt hơn).

### Layer 4 vs Layer 7 Load Balancing
### Cân bằng tải lớp 4 và lớp 7
- **Layer 4 (Transport, e.g. TCP/UDP)**: Balances at the network layer without inspecting the data content. Extremely fast.
- **Lớp 4 (Giao vận, ví dụ: TCP/UDP)**: Cân bằng ở lớp mạng mà không kiểm tra nội dung dữ liệu. Cực nhanh.
- **Layer 7 (Application, e.g. HTTP/HTTPS)**: Inspects the content of the HTTP request (URLs, headers, cookies). It can route `/api` traffic to one set of servers and `/images` to another set. Nginx and HAProxy are popular L7 balancers.
- **Lớp 7 (Ứng dụng, ví dụ: HTTP/HTTPS)**: Kiểm tra nội dung của yêu cầu HTTP (URL, tiêu đề, cookie). Nó có thể định tuyến lưu lượng `/api` đến một nhóm máy chủ và `/images` đến một nhóm khác. Nginx và HAProxy là các bộ cân bằng tải L7 phổ biến.

## System Design Diagram
## Sơ đồ thiết kế hệ thống

```mermaid
graph TD
    Client1[Mobile Client] --> LB[Load Balancer / Nginx]
    Client2[Web Client] --> LB
    
    LB -- "Round Robin" --> Web1[Web Server 1]
    LB -- "Round Robin" --> Web2[Web Server 2]
    LB -- "Round Robin" --> Web3[Web Server 3 / Crashed]
    
    style Web3 fill:#f99,stroke:#333
    
    note right of LB: LB continuously Health Checks servers.<br/>Web3 will be removed from rotation.
```

## Exercises
## Bài tập
1. What is a "Health Check" in the context of Load Balancing?
1. "Kiểm tra sức khỏe" trong bối cảnh Cân bằng tải là gì?
2. Install Nginx locally or via Docker. Configure a basic Nginx `nginx.conf` to round-robin traffic between two local backend Node.js apps running on port 3001 and 3002.
2. Cài đặt Nginx cục bộ hoặc qua Docker. Định cấu hình Nginx `nginx.conf` cơ bản để lưu lượng truy cập xoay vòng giữa hai ứng dụng Node.js backend cục bộ chạy trên cổng 3001 và 3002.
3. Read about AWS ALBs (Application Load Balancers) and NLBs (Network Load Balancers). Which one operates at Layer 7?
3. Đọc về AWS ALB (Bộ cân bằng tải ứng dụng) và NLB (Bộ cân bằng tải mạng). Cái nào hoạt động ở Lớp 7?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Understand Single Point of Failure (SPOF). If you have one load balancer, it is a SPOF. How do you mitigate this? (Active-Passive load balancer pairs using keepalived).
- Hiểu về Điểm lỗi đơn (SPOF). Nếu bạn có một bộ cân bằng tải, đó là một SPOF. Làm thế nào để bạn giảm thiểu điều này? (Các cặp bộ cân bằng tải Chủ động-Bị động sử dụng keepalived).
- Explain sticky sessions and why they are generally considered an anti-pattern in modern microservices architectures.
- Giải thích các phiên dính và tại sao chúng thường được coi là một phản mẫu trong các kiến trúc vi dịch vụ hiện đại.
