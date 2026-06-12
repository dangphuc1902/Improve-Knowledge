# gRPC (gRPC Remote Procedure Call)
# gRPC (Lệnh gọi thủ tục từ xa gRPC)

## Concept Explanation
## Giải thích khái niệm
gRPC is a modern, open-source high-performance Remote Procedure Call (RPC) framework developed by Google. It can run in any environment and allows an application to directly call a method on a server application on a different machine as if it were a local object, making it easier to create distributed applications and services.
gRPC là một khung Lệnh gọi thủ tục từ xa (RPC) hiệu suất cao, mã nguồn mở, hiện đại được phát triển bởi Google. Nó có thể chạy trong bất kỳ môi trường nào và cho phép một ứng dụng gọi trực tiếp một phương thức trên một ứng dụng máy chủ trên một máy khác như thể nó là một đối tượng cục bộ, giúp tạo các ứng dụng và dịch vụ phân tán dễ dàng hơn.

### Core Differences from REST
### Sự khác biệt cốt lõi so với REST
- **Protocol**: Uses HTTP/2 exclusively (enabling multiplexing, server push, header compression).
- **Giao thức**: Chỉ sử dụng HTTP/2 (cho phép ghép kênh, đẩy máy chủ, nén tiêu đề).
- **Payload Format**: Uses Protocol Buffers (Protobuf) instead of JSON. Protobuf is strongly typed, binary, and significantly smaller/faster than JSON.
- **Định dạng tải trọng**: Sử dụng Bộ đệm giao thức (Protobuf) thay vì JSON. Protobuf được nhập mạnh, nhị phân và nhỏ hơn/nhanh hơn đáng kể so với JSON.
- **Contract-First**: You must define a `.proto` file describing the service interface and payload structures. Code generation tools then create the client and server scaffolding in your target language.
- **Hợp đồng trước**: Bạn phải xác định một tệp `.proto` mô tả giao diện dịch vụ và cấu trúc tải trọng. Các công cụ tạo mã sau đó tạo giàn giáo máy khách và máy chủ bằng ngôn ngữ mục tiêu của bạn.

### Communication Types
### Các loại giao tiếp
1. **Unary RPC**: Standard Request -> Response.
1. **RPC một ngôi**: Yêu cầu -> Phản hồi tiêu chuẩn.
2. **Server Streaming RPC**: Client sends one request, server responds with a stream of messages.
2. **RPC truyền phát từ máy chủ**: Máy khách gửi một yêu cầu, máy chủ trả lời bằng một luồng tin nhắn.
3. **Client Streaming RPC**: Client sends a stream of messages, server responds with one message.
3. **RPC truyền phát từ máy khách**: Máy khách gửi một luồng tin nhắn, máy chủ trả lời bằng một tin nhắn.
4. **Bidirectional Streaming RPC**: Both client and server send a stream of messages simultaneously.
4. **RPC truyền phát hai chiều**: Cả máy khách và máy chủ đều gửi một luồng tin nhắn đồng thời.

## Practical Example
## Ví dụ thực tế
**1. Define the Protocol Buffer (`greet.proto`)**
**1. Xác định bộ đệm giao thức (`greet.proto`)**
```protobuf
syntax = "proto3";

package greet;

// The greeting service definition.
// Định nghĩa dịch vụ chào hỏi.
service Greeter {
  // Sends a greeting (Unary RPC)
  // Gửi lời chào (RPC một ngôi)
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
// Thông báo yêu cầu chứa tên của người dùng.
message HelloRequest {
  string name = 1; // 1 is the tag number identifying the field in binary
}

// The response message containing the greetings.
// Thông báo phản hồi chứa lời chào.
message HelloReply {
  string message = 1;
}
```

**2. Java Server Implementation (Conceptual)**
**2. Triển khai máy chủ Java (khái niệm)**
```java
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Khởi tạo gRPC Server lắng nghe trên cổng 50051
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreeterImpl())
                .build();

        System.out.println("gRPC server starting on port 50051...");
        server.start();
        
        System.out.println("gRPC server running.");
        server.awaitTermination();
    }

    // Triển khai dịch vụ Greeter được sinh ra từ file .proto
    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            // Đọc dữ liệu name từ client request
            String name = request.getName();
            
            // Xây dựng phản hồi HelloReply
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello " + name)
                    .build();

            // Trả kết quả về cho client qua stream và đánh dấu hoàn thành
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
```

## Exercises
## Bài tập
1. Compile a `.proto` file into Java using `protoc` (the Protocol Buffers compiler).
1. Biên dịch một tệp `.proto` thành Java bằng `protoc` (trình biên dịch Bộ đệm giao thức).
2. What happens if you add a new field to a Protobuf message but forget to update the client? Does it break? Why or why not? (Hint: Forward/Backward compatibility).
2. Điều gì xảy ra nếu bạn thêm một trường mới vào một thông báo Protobuf nhưng quên cập nhật máy khách? Nó có bị hỏng không? Tại sao hoặc tại sao không? (Gợi ý: Khả năng tương thích tiến/lùi).
3. Why is HTTP/2 essential for gRPC Streaming?
3. Tại sao HTTP/2 lại cần thiết cho gRPC Streaming?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- Be ready to compare REST vs gRPC vs GraphQL.
- Hãy sẵn sàng so sánh REST, gRPC và GraphQL.
- When to use gRPC: Internal Microservices communication (East-West traffic) where latency and payload size matter.
- Khi nào nên sử dụng gRPC: Giao tiếp vi dịch vụ nội bộ (lưu lượng Đông-Tây) trong đó độ trễ và kích thước tải trọng là quan trọng.
- When NOT to use gRPC: Public browser-facing APIs, as browsers have limited HTTP/2 and Protobuf support without proxies like `grpc-web`.
- Khi nào KHÔNG nên sử dụng gRPC: Các API hướng trình duyệt công khai, vì các trình duyệt có hỗ trợ HTTP/2 và Protobuf hạn chế mà không có các proxy như `grpc-web`.
