# 16 - 2-D Dynamic Programming

## 📖 Tổng quan

**DP 2 chiều**: state phụ thuộc vào **2 biến** (thường là 2 index, hoặc index + capacity).

```
dp[i][j] = f(dp[i-1][j], dp[i][j-1], dp[i-1][j-1], ...)
```

Phổ biến trong: grid paths, string matching, knapsack...

## 🔍 Khi nào sử dụng?

- Bài trên **grid 2D** (unique paths, min path sum)
- So sánh/match **2 chuỗi** (LCS, edit distance)
- **Knapsack** variant (items, capacity)
- DP 1D nhưng có thêm 1 ràng buộc/biến

## 📝 Các Pattern phổ biến

### Pattern 1: Grid DP
```java
// Unique Paths: dp[i][j] = số đường đến (i,j)
dp[i][j] = dp[i-1][j] + dp[i][j-1];
// Chỉ đi phải hoặc xuống
```

### Pattern 2: Two Strings (LCS)
```java
// dp[i][j] = LCS của s1[0..i-1] và s2[0..j-1]
if (s1.charAt(i-1) == s2.charAt(j-1))
    dp[i][j] = dp[i-1][j-1] + 1;
else
    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
```

### Pattern 3: Knapsack (0/1)
```java
// dp[i][w] = max value dùng items 0..i-1, capacity w
if (weight[i-1] <= w)
    dp[i][w] = Math.max(dp[i-1][w], dp[i-1][w-weight[i-1]] + value[i-1]);
else
    dp[i][w] = dp[i-1][w];
```

## ⏱️ Complexity thường gặp

| Bài | Time | Space | Tối ưu Space |
|-----|------|-------|-------------|
| Unique Paths | O(m×n) | O(m×n) | O(n) rolling array |
| LCS | O(m×n) | O(m×n) | O(n) |
| Coin Change | O(n×amount) | O(n×amount) | O(amount) |

## 💡 Tips phỏng vấn

1. **State**: dp[i][j] đại diện cho gì? Phải định nghĩa CỰC KỲ rõ ràng
2. **Base case**: Row 0, column 0 thường cần initialize riêng
3. **Fill order**: Thường fill từ trái→phải, trên→dưới
4. **Space optimize**: Nếu dp[i] chỉ phụ thuộc dp[i-1] → dùng 1D rolling array
