# Redis (Remote Dictionary Server)

## Concept Explanation
## Giải thích khái niệm
Redis is an open-source, in-memory data structure store, used as a database, cache, message broker, and streaming engine.
Redis là một kho lưu trữ cấu trúc dữ liệu trong bộ nhớ, mã nguồn mở, được sử dụng làm cơ sở dữ liệu, bộ đệm, nhà môi giới tin nhắn và công cụ truyền phát.
Because it holds all its data in RAM, operations are incredibly fast (microseconds), making it a dominant choice for application caching.
Vì nó chứa tất cả dữ liệu trong RAM, các hoạt động cực kỳ nhanh (micro giây), khiến nó trở thành lựa chọn hàng đầu cho việc lưu trữ bộ đệm ứng dụng.

### Key Characteristics
### Các đặc điểm chính
- **Key-Value Store**: Primarily stores data as keys mapping to values.
- **Kho lưu trữ khóa-giá trị**: Chủ yếu lưu trữ dữ liệu dưới dạng các khóa ánh xạ tới các giá trị.
- **Data Structures**: Supports strings, hashes, lists, sets, sorted sets, bitmaps, JSON, and more.
- **Cấu trúc dữ liệu**: Hỗ trợ chuỗi, băm, danh sách, tập hợp, tập hợp được sắp xếp, bitmap, JSON, v.v.
- **In-Memory**: Lightning fast, but requires persistence configurations (RDB snapshots or AOF append-only files) to save data to disk in case of a crash.
- **Trong bộ nhớ**: Nhanh như chớp, nhưng yêu cầu cấu hình lưu trữ liên tục (ảnh chụp nhanh RDB hoặc tệp chỉ nối thêm AOF) để lưu dữ liệu vào đĩa trong trường hợp xảy ra sự cố.
- **Single-Threaded Architecture**: For command execution, Redis acts sequentially. Because it's purely in-memory, the single thread is highly optimized and prevents race conditions internally.
- **Kiến trúc đơn luồng**: Để thực thi lệnh, Redis hoạt động tuần tự. Vì nó hoàn toàn trong bộ nhớ, luồng đơn được tối ưu hóa cao và ngăn chặn các tình trạng tranh đua bên trong.

### Common Use Cases
### Các trường hợp sử dụng phổ biến
1. **Caching**: Storing results of slow database queries or API calls.
1. **Lưu trữ bộ đệm**: Lưu trữ kết quả của các truy vấn cơ sở dữ liệu chậm hoặc các lệnh gọi API.
2. **Session Storage**: Managing user session tokens across distributed servers.
2. **Lưu trữ phiên**: Quản lý mã thông báo phiên người dùng trên các máy chủ phân tán.
3. **Rate Limiting**: Throttling API requests based on IPs.
3. **Giới hạn tốc độ**: Điều chỉnh các yêu cầu API dựa trên IP.
4. **Pub/Sub Broker**: Lightweight messaging system.
4. **Nhà môi giới Pub/Sub**: Hệ thống nhắn tin nhẹ.

## Practical Example: Node.js Redis Client Cache
## Ví dụ thực tế: Bộ đệm máy khách Node.js Redis

```javascript
const redis = require('redis');
const express = require('express');

const app = express();
// Create Redis Client (assuming Redis is running on default localhost:6379)
// Tạo máy khách Redis (giả sử Redis đang chạy trên localhost:6379 mặc định)
const client = redis.createClient();

client.on('error', (err) => console.log('Redis Client Error', err));

async function start() {
    await client.connect();

    app.get('/api/data/:id', async (req, res) => {
        const id = req.params.id;
        
        // 1. Check Cache
        // 1. Kiểm tra bộ đệm
        const cachedValue = await client.get(`user_data:${id}`);
        if (cachedValue) {
            console.log("Cache HIT");
            return res.json(JSON.parse(cachedValue));
        }

        // 2. Cache MISS. Simulate slow DB call
        // 2. Bỏ lỡ bộ đệm. Mô phỏng lệnh gọi DB chậm
        console.log("Cache MISS");
        const dbData = { id: id, name: "Fetched from Slow DB", timestamp: Date.now() };
        
        // 3. Set data in Cache for next time. Expiry=60 seconds.
        // 3. Đặt dữ liệu trong Bộ đệm cho lần tiếp theo. Hết hạn = 60 giây.
        await client.setEx(`user_data:${id}`, 60, JSON.stringify(dbData));
        
        return res.json(dbData);
    });

    app.listen(3000, () => console.log('Server running on 3000'));
}

start();
```

## Exercises
## Bài tập
1. Install Redis locally (via Docker is easiest: `docker run -p 6379:6379 -d redis`) and use the `redis-cli` tool.
1. Cài đặt Redis cục bộ (qua Docker là dễ nhất: `docker run -p 6379:6379 -d redis`) và sử dụng công cụ `redis-cli`.
2. Command practice: Write a string (`SET key value`), read it (`GET key`), set an expiry (`EXPIRE key 10`), and wait 10 seconds to see it disappear.
2. Thực hành lệnh: Viết một chuỗi (`SET key value`), đọc nó (`GET key`), đặt thời gian hết hạn (`EXPIRE key 10`) và đợi 10 giây để xem nó biến mất.
3. What is a Redis "Sorted Set" (ZSET) and how is it useful for building game Leaderboards?
3. "Tập hợp được sắp xếp" (ZSET) của Redis là gì và nó hữu ích như thế nào để xây dựng Bảng xếp hạng trò chơi?

## Interview Preparation Notes
## Ghi chú chuẩn bị phỏng vấn
- What happens when Redis memory is full? (Answer involves Eviction Policies like LRU - Least Recently Used).
- Điều gì xảy ra khi bộ nhớ Redis đầy? (Câu trả lời liên quan đến các Chính sách loại bỏ như LRU - được sử dụng gần đây nhất).
- Compare Redis vs Memcached. (Redis supports complex data structures and disk persistence, Memcached is pure string storage).
- So sánh Redis và Memcached. (Redis hỗ trợ các cấu trúc dữ liệu phức tạp và lưu trữ liên tục trên đĩa, Memcached là kho lưu trữ chuỗi thuần túy).
- Explain cache penetration, cache breakdown, and cache avalanche.
- Giải thích sự xâm nhập bộ đệm, sự cố bộ đệm và sự sụt lún bộ đệm.
