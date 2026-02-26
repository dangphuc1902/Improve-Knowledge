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
```java
int maxSum = nums[0], currentSum = 0;
for (int num : nums) {
    currentSum = Math.max(num, currentSum + num);
    maxSum = Math.max(maxSum, currentSum);
}
```

### Pattern 2: Jump Game (Can Reach End?)
```java
int farthest = 0;
for (int i = 0; i < nums.length; i++) {
    if (i > farthest) return false; // Không thể đến ô này
    farthest = Math.max(farthest, i + nums[i]);
}
return true;
```

### Pattern 3: Gas Station (Circular)
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
