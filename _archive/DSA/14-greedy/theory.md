# 14 - Greedy

## 📖 Tổng quan

**Greedy** là chiến lược chọn **tối ưu cục bộ** tại mỗi bước, hy vọng đạt **tối ưu toàn cục**. Không quay lại (khác backtracking), không lưu state (khác DP).

> Greedy hoạt động khi: **locally optimal choice → globally optimal solution**.

## 🔍 Khi nào sử dụng?

- Bài cho phép chọn **tham lam** mà vẫn đúng
- **Activity selection** / interval scheduling
- Bài có tính chất **exchange argument** (đổi 2 lựa chọn không tốt hơn)
- Từ khóa: "minimum number", "maximum profit", "can you reach"
- Thường cần **chứng minh** greedy đúng (hoặc recognize pattern)

## 📝 Các Pattern phổ biến

### Pattern 1: Max Subarray (Kadane's Algorithm)
- **Nó là gì?**: Duyệt qua mảng và tại mỗi bước, quyết định xem nên bắt đầu một subarray mới từ phần tử hiện tại hay cộng dồn nó vào subarray hiện tại.
- **Giải quyết bài toán nào?**: 
    - Tìm tổng lớn nhất của một subarray liên tiếp (`Maximum Subarray`).
- **Ưu điểm**:
    - Tốc độ xử lý cực nhanh O(n).
    - Tiết kiệm bộ nhớ (O(1) space).
- **Nhược điểm**:
    - Chỉ áp dụng được cho subarray liên tiếp.
- **Sự thay thế**:
    - **Divide and Conquer**: O(n log n).

```java
int maxSum = nums[0], currentSum = 0;
for (int num : nums) {
    currentSum = Math.max(num, currentSum + num);
    maxSum = Math.max(maxSum, currentSum);
}
```

### Pattern 2: Jump Game (Farthest Reachable)
- **Nó là gì?**: Tại mỗi vị trí, cập nhật điểm xa nhất mà bạn có thể nhảy tới. Nếu vị trí hiện tại vượt quá điểm xa nhất có thể tới, nghĩa là không thể đi tiếp.
- **Giải quyết bài toán nào?**: 
    - Kiểm tra có thể nhảy tới cuối mảng không (`Jump Game`).
    - Tìm số bước nhảy ít nhất để tới đích (`Jump Game II`).
- **Ưu điểm**:
    - Giải quyết bài toán trong một lần duyệt O(n).
- **Nhược điểm**:
    - Đòi hỏi sự tin tưởng vào chiến lược "luôn chọn bước nhảy xa nhất" (Greedy choice).
- **Sự thay thế**:
    - **Dynamic Programming**: O(n²) - rất chậm khi mảng lớn.
    - **BFS**: O(n) nhưng tốn thêm O(n) space.

```java
int farthest = 0;
for (int i = 0; i < nums.length; i++) {
    if (i > farthest) return false; // Không thể đến ô này
    farthest = Math.max(farthest, i + nums[i]);
}
return true;
```

### Pattern 3: Greedy Choice on Streams (Gas Station)
- **Nó là gì?**: Nếu không thể đi từ A tới B, thì bất kỳ trạm nào giữa A và B cũng không thể là điểm bắt đầu hợp lệ. Do đó, ta có thể bỏ qua toàn bộ đoạn đó và thử trạm tiếp theo.
- **Giải quyết bài toán nào?**: 
    - Bài toán trạm xăng (`Gas Station`).
- **Ưu điểm**:
    - Giảm số lượng điểm bắt đầu cần thử từ O(n) xuống thực tế chỉ là 1 lần duyệt.
- **Nhược điểm**:
    - Cần một chứng minh toán học (Total gas >= Total cost) để đảm bảo kết quả cuối cùng.
- **Sự thay thế**:
    - **Brute Force**: Thử mọi trạm bắt đầu (O(n²)).

```java
int totalSurplus = 0, currentSurplus = 0, start = 0;
for (int i = 0; i < n; i++) {
    totalSurplus += gas[i] - cost[i];
    currentSurplus += gas[i] - cost[i];
    if (currentSurplus < 0) {
        start = i + 1;
        currentSurplus = 0;
    }
}
return totalSurplus >= 0 ? start : -1;
```

## ⏱️ Complexity thường gặp

| Bài | Time | Space |
|-----|------|-------|
| Max Subarray | O(n) | O(1) |
| Jump Game | O(n) | O(1) |
| Gas Station | O(n) | O(1) |

## 💡 Tips phỏng vấn

1. **Prove it**: Nếu được hỏi, giải thích TẠI SAO greedy đúng (exchange argument)
2. **vs DP**: Nếu greedy không đúng → thử DP. Nếu DP quá chậm → thử greedy
3. **Sort first**: Nhiều bài greedy cần sort trước (intervals, scheduling)
4. **Local → Global**: Luôn tự hỏi "chọn tối ưu bước này có đảm bảo toàn cục?"
