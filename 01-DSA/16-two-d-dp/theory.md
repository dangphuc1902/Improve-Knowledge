# 16 - 2D Dynamic Programming

## 📖 Tổng quan

**2D DP** là khi state phụ thuộc vào **hai chiều** — thường là `dp[i][j]` với i và j đại diện cho hai biến độc lập (index trong 2 chuỗi, row/col trong lưới, trọng lượng và item).

> **Ý tưởng cốt lõi:** Xác định `dp[i][j]` = gì?, base case, và transition function. Vẽ bảng dp nhỏ (3x3) để tìm pattern trước khi code.

## 🧠 Kiến thức cốt lõi

### Template 2D DP

```java
// dp[i][j]: state với 2 tham số
int[][] dp = new int[m + 1][n + 1];

// Base cases
// Thường dp[0][j] = 0 và dp[i][0] = 0

// Fill bottom-up
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        // Transition từ dp[i-1][j], dp[i][j-1], dp[i-1][j-1]
    }
}
```

### Space Optimization

```java
// 2D → 1D (khi dp[i] chỉ phụ thuộc dp[i-1])
int[] dp = new int[n + 1];
// hoặc dùng 2 arrays: prev[] và curr[]
```

## 🔍 Khi nào sử dụng?

- Bài toán liên quan **2 chuỗi** (LCS, Edit Distance, LPS)
- **Knapsack 2D** (item × capacity)
- **Grid traversal** (Unique Paths, Minimum Path Sum)
- **Regex Matching**, **Wildcard Matching**
- Cụm từ: *"two strings"*, *"grid"*, *"matrix"*, *"match"*, *"transform"*

## 📝 Các Pattern phổ biến

### Pattern 1: Grid DP (Unique Paths)
- **Nó là gì?**: `dp[i][j]` = số cách/cost đến ô (i,j). Transition: từ trái hoặc từ trên.
- **Giải quyết bài toán nào?**: Unique Paths (LC 62), Minimum Path Sum (LC 64), Triangle.
- **Space optimization**: Chỉ cần 1D array O(n).

```java
// Unique Paths: dp[i][j] = dp[i-1][j] + dp[i][j-1]
int[] dp = new int[cols];
Arrays.fill(dp, 1);
for (int i = 1; i < rows; i++) {
    for (int j = 1; j < cols; j++) {
        dp[j] += dp[j - 1]; // dp[j] = "from above", dp[j-1] = "from left"
    }
}
return dp[cols - 1];
```

### Pattern 2: LCS — Longest Common Subsequence
- **Nó là gì?**: `dp[i][j]` = LCS của text1[0..i-1] và text2[0..j-1].
- **Transition**: Nếu `text1[i-1] == text2[j-1]` → `dp[i][j] = dp[i-1][j-1] + 1`. Else → `max(dp[i-1][j], dp[i][j-1])`.
- **Giải quyết bài toán nào?**: LCS (LC 1143), Edit Distance (build upon LCS), Delete Operation for Two Strings.

```java
int[][] dp = new int[m + 1][n + 1];
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
            dp[i][j] = dp[i - 1][j - 1] + 1; // match!
        } else {
            dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]); // skip one
        }
    }
}
return dp[m][n];
```

### Pattern 3: Edit Distance
- **Nó là gì?**: `dp[i][j]` = min operations để chuyển word1[0..i-1] thành word2[0..j-1].
- **Operations**: Insert, Delete, Replace.
- **Transition**: Match → 0 ops. Mismatch → `1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])`.
- **Giải quyết bài toán nào?**: Edit Distance (LC 72).

```java
int[][] dp = new int[m + 1][n + 1];
// Base cases: dp[i][0] = i, dp[0][j] = j (delete/insert all chars)
for (int i = 0; i <= m; i++) dp[i][0] = i;
for (int j = 0; j <= n; j++) dp[0][j] = j;

for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
            dp[i][j] = dp[i - 1][j - 1]; // same char, 0 ops
        } else {
            dp[i][j] = 1 + Math.min(
                dp[i - 1][j - 1], // replace
                Math.min(dp[i - 1][j], dp[i][j - 1]) // delete, insert
            );
        }
    }
}
```

### Pattern 4: 0/1 Knapsack 2D
- **Nó là gì?**: `dp[i][w]` = max value với i items đầu tiên và capacity w.
- **Giải quyết bài toán nào?**: 0-1 Knapsack, Partition Equal Subset Sum (LC 416), Target Sum (LC 494).

```java
// 0/1 Knapsack
int[][] dp = new int[n + 1][capacity + 1];
for (int i = 1; i <= n; i++) {
    for (int w = 0; w <= capacity; w++) {
        dp[i][w] = dp[i - 1][w]; // không lấy item i
        if (weights[i - 1] <= w) {
            dp[i][w] = Math.max(dp[i][w], values[i - 1] + dp[i - 1][w - weights[i - 1]]);
        }
    }
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: LCS — Dry Run

```
text1 = "abcde", text2 = "ace"

dp table (i=text1 index, j=text2 index):
        ""  "a" "c" "e"
  ""     0   0   0   0
  "a"    0   1   1   1
  "b"    0   1   1   1
  "c"    0   1   2   2
  "d"    0   1   2   2
  "e"    0   1   2   3

- dp[1][1]: text1[0]='a' == text2[0]='a' → dp[0][0]+1 = 1
- dp[3][2]: text1[2]='c' == text2[1]='c' → dp[2][1]+1 = 2
- dp[5][3]: text1[4]='e' == text2[2]='e' → dp[4][2]+1 = 3

✅ Output: 3 (LCS = "ace")
```

### Ví dụ 2: Edit Distance — Dry Run

```
word1 = "horse", word2 = "ros"

dp table:
        ""  "r" "o" "s"
  ""     0   1   2   3
  "h"    1   1   2   3
  "o"    2   2   1   2
  "r"    3   2   2   2
  "s"    4   3   3   2
  "e"    5   4   4   3

- dp[2][2]: word1[1]='o'==word2[1]='o' → dp[1][1]=1+0=1
- dp[5][3]: word1[4]='e' != word2[2]='s' → 1+min(dp[4][3]=2, dp[5][2]=4, dp[4][2]=3) = 1+2=3

✅ Output: 3
  horse → rorse (replace h with r)
  rorse → rose (remove r)
  rose → ros (remove e)
```

### Ví dụ 3: Unique Paths — 1D Space Optimization

```
m=3, n=3 grid
Start top-left, end bottom-right

dp (1D, size=3):
Initial: [1, 1, 1] (first row = all 1s)

Row 2 (i=1):
  j=0: dp[0]=1 (unchanged)
  j=1: dp[1] += dp[0] = 1+1 = 2
  j=2: dp[2] += dp[1] = 1+2 = 3
  dp = [1, 2, 3]

Row 3 (i=2):
  j=0: dp[0]=1
  j=1: dp[1] += dp[0] = 2+1 = 3
  j=2: dp[2] += dp[1] = 3+3 = 6
  dp = [1, 3, 6]

✅ Output: 6
```

## 🔄 So sánh các Approach

### LCS: 2D DP vs Recursive

| Approach | Time | Space |
|----------|------|-------|
| **2D DP ⭐** | O(m*n) | O(m*n) |
| Space Optimized | O(m*n) | O(n) |
| Naive Recursive | O(2^(m+n)) | O(m+n) |

### Unique Paths: 2D DP vs Math (Combinatorics)

| Approach | Time | Space |
|----------|------|-------|
| **1D DP ⭐** | O(m*n) | O(n) |
| Math C(m+n-2, m-1) | O(m+n) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// LCS:
// 1. Một chuỗi rỗng → LCS = 0
// 2. Cả 2 chuỗi giống nhau → LCS = length
// 3. Không có chung ký tự → LCS = 0

// Edit Distance:
// 1. word1 = "" → len(word2) (chỉ insert)
// 2. word2 = "" → len(word1) (chỉ delete)
// 3. Cùng word → 0

// Unique Paths:
// 1. m=1 or n=1 → chỉ 1 đường đi
// 2. Obstacles → dp[i][j]=0 nếu grid[i][j]=1

// 0/1 Knapsack:
// 1. capacity=0 → 0
// 2. weight > capacity → không thể lấy
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Unique Paths | O(m*n) | O(n) |
| Minimum Path Sum | O(m*n) | O(n) |
| LCS | O(m*n) | O(m*n) hoặc O(n) |
| Edit Distance | O(m*n) | O(m*n) hoặc O(n) |
| 0/1 Knapsack | O(n*W) | O(n*W) hoặc O(W) |

## 💡 Tips phỏng vấn

1. **Vẽ bảng**: Luôn vẽ bảng dp nhỏ 3-4 ô để xác nhận transition trước khi code.
2. **Base case**: `dp[0][j]` và `dp[i][0]` thường là 0 hoặc i/j (delete all chars).
3. **Transition arrow**: Mũi tên từ đâu đến `dp[i][j]`? Từ trái, trên, hay chéo?
4. **Space optimization**: 2D → 1D khi `dp[i][j]` chỉ dùng row trước.
5. **Reconstruct path**: Nếu cần reconstruct (không chỉ giá trị) → cần lưu thêm parent array.
