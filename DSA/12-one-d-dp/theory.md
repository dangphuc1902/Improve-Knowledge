# 12 - 1-D Dynamic Programming

## 📖 Tổng quan

**Dynamic Programming (DP)** giải bài toán bằng cách chia thành **subproblems nhỏ hơn**, lưu kết quả để tránh tính lại (memoization / tabulation).

DP 1 chiều: state chỉ phụ thuộc vào **1 biến** (thường là index i).

```
dp[i] = f(dp[i-1], dp[i-2], ...)
```

## 🧠 Hai cách tiếp cận

| | Top-Down (Memoization) | Bottom-Up (Tabulation) |
|-|----------------------|----------------------|
| Hướng | Đệ quy + cache | Vòng lặp từ base case |
| Viết | Tự nhiên, dễ nghĩ | Cần xác định thứ tự |
| Space tối ưu | Khó | Dễ (vd: chỉ giữ 2 biến) |
| LeetCode | Thường ổn | Thường nhanh hơn |

## 🔍 Khi nào sử dụng?

- Bài có **overlapping subproblems** (cùng input tính nhiều lần)
- Bài có **optimal substructure** (tối ưu toàn cục = tối ưu cục bộ)
- Từ khóa: "minimum/maximum", "how many ways", "is it possible"
- Cảm giác như *backtracking nhưng có lặp tính toán*

## 📝 Các Pattern phổ biến

### Pattern 1: Fibonacci-like
```java
// dp[i] phụ thuộc dp[i-1] và dp[i-2]
int prev2 = base0, prev1 = base1;
for (int i = 2; i <= n; i++) {
    int curr = prev1 + prev2;
    prev2 = prev1;
    prev1 = curr;
}
return prev1;
```

### Pattern 2: Take or Skip
```java
// Tại mỗi vị trí: lấy hoặc không lấy
dp[i] = Math.max(
    dp[i - 1],             // Không lấy i
    dp[i - 2] + nums[i]    // Lấy i (skip i-1)
);
```

### Pattern 3: LIS (Longest Increasing Subsequence)
```java
// dp[i] = LIS kết thúc tại i
for (int i = 1; i < n; i++) {
    for (int j = 0; j < i; j++) {
        if (nums[j] < nums[i]) {
            dp[i] = Math.max(dp[i], dp[j] + 1);
        }
    }
}
```

## ⏱️ Complexity thường gặp

| Bài | Time | Space | Tối ưu Space |
|-----|------|-------|-------------|
| Climbing Stairs | O(n) | O(n) | O(1) |
| House Robber | O(n) | O(n) | O(1) |
| LIS | O(n²) | O(n) | O(n log n) binary search |

## 💡 Tips phỏng vấn

1. **5 bước DP**: Define state → Recurrence → Base case → Order → Optimize space
2. **State**: Hỏi "dp[i] đại diện cho gì?" — phải định nghĩa RÕ RÀNG
3. **Space optimize**: Nếu dp[i] chỉ phụ thuộc dp[i-1], dp[i-2] → dùng 2 biến
4. **Top-Down first**: Nếu khó nghĩ bottom-up, viết recursion + memo trước
