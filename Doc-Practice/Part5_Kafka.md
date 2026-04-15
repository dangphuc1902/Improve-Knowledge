# PHẦN 5: KAFKA (HỆ THỐNG EVENT-DRIVEN CHO GAME)

Trong game online, có hàng tỷ sự kiện (events) xảy ra mỗi phút: Player giết quái, nhặt đồ, thăng cấp, nạp tiền... Nếu mỗi lần như vậy ta đều gọi API hoặc DB trực tiếp, hệ thống sẽ sập nhanh chóng. **Kafka** sinh ra để giải quyết vấn đề này.

---

## 1. Kafka là gì?
Kafka là một **Distributed Streaming Platform**. Bạn hãy tưởng tượng nó như một bưu điện khổng lồ. 
- **Producer**: Kẻ gửi thư (Game Server).
- **Topic**: Hòm thư (ví dụ: `player_levelup_events`).
- **Consumer**: Người nhận thư để xử lý sau (Analytics Service, Quest Service, Reward Service).

---

## 2. Tại sao Game Backend cần Kafka?

### Decoupling (Tách biệt)
Game server chỉ cần đẩy event "Player Level Up" lên Kafka. Nó không cần biết ai sẽ dùng event đó. 
- Team Content dùng để tặng quà.
- Team Analytics dùng để làm báo cáo.
- Team Marketing dùng để gửi notification.
Nếu 1 trong 3 service kia chết, Game Server vẫn chạy bình thường.

### Buffer & Throttling
Khi game có Big Update, lượng user tăng đột biến. Kafka đóng vai trò là "đê chắn sóng". Nó hứng toàn bộ traffic và để các service Consumer xử lý từ từ theo khả năng của chúng.

---

## 3. Case Study: Hệ thống Phần thưởng (Quest & Reward)

**Flow truyền thống (Dễ sập):**
1. Player giết Boss.
2. Game Server gọi API `RewardService.addItem()`.
3. `RewardService` chậm hoặc timeout -> Game Server bị block -> Player bị lag.

**Flow dùng Kafka (Xịn):**
1. Player giết Boss.
2. Game Server đẩy message `{playerId: 123, bossId: 99}` vào topic `BOSS_KILLED`.
3. Game Server tiếp tục chạy logic khác ngay lập tức (Non-blocking).
4. `RewardService` (Consumer) thấy message, tự động cộng đồ vào DB sau vài mili giây.

---

## 4. Sai lầm phổ biến khi dùng Kafka trong Game

- **Gửi dữ liệu quá lớn**: Đừng nhét cả ảnh hay file log khổng lồ vào Kafka message. Chỉ gửi ID và metadata cần thiết.
- **Không xử lý Idempotency**: Nếu Kafka gửi trùng message, Reward service có thể cộng quà 2 lần. Phải dùng `transactionId` để check.
- **Quá nhiều Partition**: Tăng partition giúp tăng throughput nhưng làm tăng load cho Zookeeper/Controller.

---

## 5. Ví dụ Code Java (Spring Kafka)

```java
// Producer: Đẩy sự kiện từ Game Server
@Service
public class GameEventProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBattleResult(BattleResult result) {
        kafkaTemplate.send("BATTLE_RESULTS", result.getMatchId(), result);
    }
}

// Consumer: Xử lý phần thưởng
@Service
public class RewardConsumer {
    @KafkaListener(topics = "BATTLE_RESULTS", groupId = "reward_group")
    public void onMessage(BattleResult result) {
        // Logic cộng Rank Point, cộng Vàng ở đây
        long winnerId = result.getWinnerId();
        playerService.addGold(winnerId, 100);
    }
}
```

---

## CÂU HỎI PHỎNG VẤN

### Mid
- **Q**: Phân biệt Kafka và RabbitMQ trong thực tế?
- **A**: 
    - **Kafka**: throughput cao hơn, lưu trữ dữ liệu lịch sử (Replay), phù hợp cho log/analytics và event-streaming lớn.
    - **RabbitMQ**: latency thấp hơn, routing phức tạp, phù hợp cho các task cần tin cậy cao và xử lý ngay lập tức (như chat, thông báo).

- **Q**: Làm sao để đảm bảo thứ tự message trong Kafka? (Ví dụ: Sự kiện A phải xử lý trước sự kiện B)
- **A**: Dùng chung một **Message Key** (ví dụ: `playerId`). Kafka đảm bảo các message cùng Key sẽ rơi vào cùng 1 Partition, và trong 1 Partition thì thứ tự là tuyệt đối.

### Senior
- **Q**: Hệ thống bị lag nghiêm trọng do "Consumer Group Lag". Bạn sẽ xử lý thế nào?
- **A**: 
    1. Check xem code Consumer có xử lý quá chậm (gọi DB lâu) không? Optimize code.
    2. Tăng số lượng **Partition** và tăng số lượng **Consumer instance** để xử lý song song.
    3. Kiểm tra xem có hiện tượng "Data Skew" (1 partition chứa quá nhiều data) không? (Do chọn Key không đều).

---

## BÀI TẬP THỰC HÀNH
**Đề bài:** Thiết kế một hệ thống "Log chiến đấu" dùng Kafka. Mỗi hành động tấn công của player được ghi lại. Yêu cầu:
- Consumer A: Lưu log vào MongoDB để player xem lại lịch sử.
- Consumer B: Tính toán sát thương tổng để làm bảng xếp hạng realtime (Top Damage).
Hãy mô tả cấu trúc Topic và Key bạn sẽ dùng.
