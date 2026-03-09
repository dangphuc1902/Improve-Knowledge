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

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Unique Paths - step by step
```
Input: m = 3, n = 4 (3 rows, 4 cols)

Grid visual:
  (0,0) → (0,1) → (0,2) → (0,3)
    ↓       ↓       ↓       ↓
  (1,0) → (1,1) → (1,2) → (1,3)
    ↓       ↓       ↓       ↓
  (2,0) → (2,1) → (2,2) → (2,3) [destination]

DP recurrence:
  dp[i][j] = # paths đến (i,j) = dp[i-1][j] + dp[i][j-1]
  (từ trên + từ trái)

Tính toán:
  
Row 0: [1, 1, 1, 1]  (chỉ 1 cách: đi phải)
  dp[0][0]=1, dp[0][1]=1, dp[0][2]=1, dp[0][3]=1

Row 1: [1, ?, ?, ?]
  dp[1][0]=1 (chỉ 1 cách: đi xuống)
  dp[1][1] = dp[0][1] + dp[1][0] = 1+1 = 2
  dp[1][2] = dp[0][2] + dp[1][1] = 1+2 = 3
  dp[1][3] = dp[0][3] + dp[1][2] = 1+3 = 4
  Row 1: [1, 2, 3, 4]

Row 2: [1, ?, ?, ?]
  dp[2][0]=1 (chỉ 1 cách: đi xuống)
  dp[2][1] = dp[1][1] + dp[2][0] = 2+1 = 3
  dp[2][2] = dp[1][2] + dp[2][1] = 3+3 = 6
  dp[2][3] = dp[1][3] + dp[2][2] = 4+6 = 10
  Row 2: [1, 3, 6, 10]

DP table:
  [1, 1,  1,  1]
  [1, 2,  3,  4]
  [1, 3,  6, 10]

✅ Output: dp[2][3] = 10 paths
```

**Insight:** Grid DP = mỗi ô = tổng từ trên + trái

---

### Ví dụ 2: Longest Common Subsequence (LCS) - step by step
```
Input: text1 = "abcde", text2 = "ace"

DP table (m+1) × (n+1):
  cols: "", a, c, e
  rows:
    "" [0, 0, 0, 0]
    a  [0, ?, ?, ?]
    b  [0, ?, ?, ?]
    c  [0, ?, ?, ?]
    d  [0, ?, ?, ?]
    e  [0, ?, ?, ?]

Base case: row 0, col 0 = 0 (empty string)

Fill tabel:
  
  i=1 (a):
    j=1 (a): text1[0]='a' == text2[0]='a'? YES
      dp[1][1] = dp[0][0] + 1 = 0 + 1 = 1
    j=2 (c): text1[0]='a' == text2[1]='c'? NO
      dp[1][2] = max(dp[0][2], dp[1][1]) = max(0, 1) = 1
    j=3 (e): text1[0]='a' == text2[2]='e'? NO
      dp[1][3] = max(dp[0][3], dp[1][2]) = max(0, 1) = 1
    Row 1: [0, 1, 1, 1]
  
  i=2 (b):
    j=1 (a): 'b' == 'a'? NO → dp[2][1] = max(1, 0) = 1
    j=2 (c): 'b' == 'c'? NO → dp[2][2] = max(1, 1) = 1
    j=3 (e): 'b' == 'e'? NO → dp[2][3] = max(1, 1) = 1
    Row 2: [0, 1, 1, 1]
  
  i=3 (c):
    j=1 (a): 'c' == 'a'? NO → dp[3][1] = max(1, 0) = 1
    j=2 (c): 'c' == 'c'? YES
      dp[3][2] = dp[2][1] + 1 = 1 + 1 = 2
    j=3 (e): 'c' == 'e'? NO
      dp[3][3] = max(dp[2][3], dp[3][2]) = max(1, 2) = 2
    Row 3: [0, 1, 2, 2]
  
  i=4 (d):
    j=1 (a): 'd' == 'a'? NO → dp[4][1] = max(1, 0) = 1
    j=2 (c): 'd' == 'c'? NO → dp[4][2] = max(2, 1) = 2
    j=3 (e): 'd' == 'e'? NO → dp[4][3] = max(2, 2) = 2
    Row 4: [0, 1, 2, 2]
  
  i=5 (e):
    j=1 (a): 'e' == 'a'? NO → dp[5][1] = max(1, 0) = 1
    j=2 (c): 'e' == 'c'? NO → dp[5][2] = max(2, 1) = 2
    j=3 (e): 'e' == 'e'? YES
      dp[5][3] = dp[4][2] + 1 = 2 + 1 = 3
    Row 5: [0, 1, 2, 3]

Final table:
  [0, 0, 0, 0]
  [0, 1, 1, 1]
  [0, 1, 1, 1]
  [0, 1, 2, 2]
  [0, 1, 2, 2]
  [0, 1, 2, 3]

✅ Output: dp[5][3] = 3 (LCS = "ace")
```

**Insight:** Match = take diagonal + 1, No match = take max from top/left

---

### Ví dụ 3: Coin Change - step by step
```
Input: coins = [1, 5, 11], amount = 15

DP[a] = min coins để tạo amount a

Init: dp = [0, INF, INF, ..., INF] (length 16)
      dp[0] = 0 (0 coins cho amount 0)

Fill:
  a=1:
    coin=1: dp[1] = min(INF, dp[1-1]+1) = min(INF, dp[0]+1) = 1
  
  a=2:
    coin=1: dp[2] = min(INF, dp[2-1]+1) = min(INF, dp[1]+1) = 2
    coin=5: 5 > 2, skip
  
  a=3:
    coin=1: dp[3] = min(INF, dp[2]+1) = 3
  
  a=4:
    coin=1: dp[4] = min(INF, dp[3]+1) = 4
  
  a=5:
    coin=1: dp[5] = min(INF, dp[4]+1) = 5
    coin=5: dp[5] = min(5, dp[5-5]+1) = min(5, dp[0]+1) = 1 ← Better!
  
  a=6:
    coin=1: dp[6] = min(INF, dp[5]+1) = 2
    coin=5: dp[6] = min(2, dp[1]+1) = min(2, 2) = 2
  
  a=10:
    coin=5: dp[10] = min(INF, dp[5]+1) = 2
    (5+5)
  
  a=15:
    coin=1: dp[15] = ... = keep updating
    coin=5: dp[15] = min(prev, dp[10]+1) = ... = 3
    coin=11: dp[15] = min(prev, dp[4]+1) = ...
    
  Final: dp[15] = 3 (5+5+5)

DP array: [0, 1, 2, 3, 4, 1, 2, 3, 4, 5, 2, 1, 2, 3, 4, 3]

✅ Output: 3
```

**Insight:** Unbounded knapsack = dùng coin unlimited lần

---

## 🔄 So sánh các Approach

### Unique Paths
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Math (combinations) | O(n) | O(1) | Tính C(m+n-2, m-1) |
| DP 2D | O(m*n) | O(m*n) | Standard |
| DP 1D rolling ⭐ | O(m*n) | O(n) | Space optimized |

### LCS
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| DP 2D | O(m*n) | O(m*n) | Standard |
| DP 1D rolling | O(m*n) | O(n) | Space opt |
| Recursion | O(2^(m+n)) | O(m+n) | Slow |

### Coin Change
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Backtracking | O(amount^coin.length) | O(amount) | Exponential |
| DP ⭐ | O(amount * coins.length) | O(amount) | Linear |
| BFS | O(amount * coins.length) | O(amount) | Alternative |

---

## 🚨 Edge Cases

```java
// Unique Paths:
// - m=1, n=1 → 1
// - m=1, n=5 → 1 (chỉ đi phải)

// LCS:
// - text1="", text2="" → 0
// - text1="abc", text2="abc" → 3 (toàn bộ match)
// - text1="abc", text2="xyz" → 0 (không match)

// Coin Change:
// - coins=[1], amount=5 → 5
// - coins=[2], amount=3 → -1 (không thể)
// - coins=[10], amount=1 → -1
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
