# 12 - 1D Dynamic Programming

## 📖 Tổng quan

**Dynamic Programming (DP)** là kỹ thuật giải quyết bài toán bằng cách **chia thành subproblems nhỏ hơn** và **lưu kết quả** để tránh tính lại. 1D-DP là khi trạng thái chỉ phụ thuộc vào 1 chiều.

> **Ý tưởng cốt lõi:** "Overlapping subproblems + Optimal substructure". Nếu có thể tính `f(n)` từ `f(n-1)`, `f(n-2)`,... → đây là DP.

## 🧠 Kiến thức cốt lõi

### Ba cách tiếp cận DP

| Cách | Mô tả | Space | Khi dùng |
|------|-------|-------|----------|
| **Top-Down (Memoization)** | Đệ quy + cache | O(n) | Dễ nghĩ, code trực quan |
| **Bottom-Up (Tabulation)** | Vòng lặp từ base case | O(n) | Hiệu quả hơn, không stack |
| **Space Optimized** | Chỉ giữ vài state trước | O(1) | Khi chỉ cần dp[i-1], dp[i-2] |

### Template DP

```
1. Define state: dp[i] = ?
2. Base case: dp[0], dp[1] = ?
3. Transition: dp[i] = f(dp[i-1], dp[i-2], ...)
4. Answer: dp[n] or max/min of dp
```

## 🔍 Khi nào sử dụng?

- Bài toán có **"optimal"** (max/min/count)
- Có thể chia thành **overlapping subproblems**
- Cụm từ: *"minimum cost"*, *"maximum profit"*, *"number of ways"*, *"can we reach"*
- Test brute force O(2^n) → nghĩ DP O(n)

## 📝 Các Pattern phổ biến

### Pattern 1: Linear DP — Fibonacci Style
- **Nó là gì?**: `dp[i] = f(dp[i-1], dp[i-2])`. State chỉ phụ thuộc vài state trước.
- **Giải quyết bài toán nào?**: Climbing Stairs, Fibonacci, House Robber, Min Cost Climbing Stairs.
- **Space Optimization**: Chỉ giữ 2 biến thay vì mảng → O(1) space.

```java
// Climbing Stairs: dp[i] = dp[i-1] + dp[i-2]
// Space O(1): chỉ giữ 2 biến
int prev2 = 1, prev1 = 1;
for (int i = 2; i <= n; i++) {
    int curr = prev1 + prev2;
    prev2 = prev1;
    prev1 = curr;
}
return prev1;
```

### Pattern 2: Knapsack — Unbounded và 0/1
- **Nó là gì?**: Chọn items để tối ưu giá trị trong giới hạn capacity.
- **Giải quyết bài toán nào?**: Coin Change (Unbounded), House Robber (0/1), Target Sum.
- **Unbounded**: Có thể dùng item nhiều lần → `dp[i] = min(dp[i], dp[i-coin] + 1)`.
- **0/1**: Mỗi item chỉ dùng 1 lần.

```java
// Coin Change — Unbounded Knapsack
// dp[i] = min coins cần để đổi amount = i
int[] dp = new int[amount + 1];
Arrays.fill(dp, amount + 1); // infinity
dp[0] = 0;
for (int i = 1; i <= amount; i++) {
    for (int coin : coins) {
        if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
    }
}
return dp[amount] > amount ? -1 : dp[amount];
```

### Pattern 3: House Robber — Cannot pick adjacent
- **Nó là gì?**: Không thể chọn 2 phần tử liên tiếp. `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`.
- **Giải quyết bài toán nào?**: House Robber I & II, Delete and Earn.

```java
// dp[i] = max money from first i houses
// dp[i] = max(skip house i = dp[i-1], rob house i = dp[i-2] + nums[i])
int rob = 0, skip = 0; // skip = không rob house i, rob = rob house i
for (int num : nums) {
    int newRob = skip + num;
    int newSkip = Math.max(skip, rob);
    rob = newRob;
    skip = newSkip;
}
return Math.max(rob, skip);
```

### Pattern 4: Palindrome DP
- **Nó là gì?**: `dp[i][j]` = true nếu s[i..j] là palindrome. Transition: `s[i]==s[j] && dp[i+1][j-1]`.
- **Giải quyết bài toán nào?**: Longest Palindromic Substring, Palindromic Substrings.
- **Sự thay thế**: Expand Around Center O(n²) — không cần O(n²) space.

### Pattern 5: Word Break — DP với Set
- **Nó là gì?**: `dp[i]` = có thể partition s[0..i-1] thành words từ dictionary không?
- **Transition**: `dp[i] = dp[j] && dict.contains(s[j..i-1])` với j từ 0 đến i.

```java
Set<String> dict = new HashSet<>(wordDict);
boolean[] dp = new boolean[s.length() + 1];
dp[0] = true;
for (int i = 1; i <= s.length(); i++) {
    for (int j = 0; j < i; j++) {
        if (dp[j] && dict.contains(s.substring(j, i))) {
            dp[i] = true;
            break;
        }
    }
}
return dp[s.length()];
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Coin Change — Bottom-Up Dry Run

```
coins = [1, 5, 6, 9], amount = 11

dp = [0, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞, ∞]
      0  1  2  3  4  5  6  7  8  9  10 11

i=1: coin=1: dp[1]=min(∞, dp[0]+1)=1
     coin=5,6,9: 5>1, skip
     dp[1]=1

i=5: coin=1: dp[5]=min(∞,dp[4]+1)=min(∞,4+1)=5
     coin=5: dp[5]=min(5,dp[0]+1)=min(5,1)=1
     dp[5]=1

i=6: coin=1: dp[6]=min(∞,dp[5]+1)=min(∞,1+1)=2
     coin=5: dp[6]=min(2,dp[1]+1)=min(2,1+1)=2
     coin=6: dp[6]=min(2,dp[0]+1)=min(2,1)=1
     dp[6]=1

i=11: coin=1: dp[10]+1=3
      coin=5: dp[6]+1=1+1=2
      coin=6: dp[5]+1=1+1=2
      coin=9: dp[2]+1=2+1=3
      dp[11]=2

✅ Output: 2 (coins: 5+6=11 hoặc 5+6=11)
```

### Ví dụ 2: House Robber — Space Optimized

```
nums = [2, 7, 9, 3, 1]

Initial: skip=0, rob=0

num=2: newRob=0+2=2, newSkip=max(0,0)=0 → rob=2, skip=0
num=7: newRob=0+7=7, newSkip=max(0,2)=2 → rob=7, skip=2
num=9: newRob=2+9=11, newSkip=max(2,7)=7 → rob=11, skip=7
num=3: newRob=7+3=10, newSkip=max(7,11)=11 → rob=10, skip=11
num=1: newRob=11+1=12, newSkip=max(11,10)=11 → rob=12, skip=11

✅ Output: max(12, 11) = 12 (rob houses 2,9,1 = 2+9+1=12... wait)
Verify: [2,7,9,3,1] — skip 7, rob 2+9+1=12 ✓
```

### Ví dụ 3: Fibonacci — Ba cách

```
n = 5, fib(5) = 5

Top-Down (Memo):
  fib(5) → fib(4) + fib(3)
  fib(4) → fib(3) + fib(2) → cached after first call
  memo = {0:0, 1:1, 2:1, 3:2, 4:3, 5:5}

Bottom-Up:
  dp=[0,1,1,2,3,5]
  dp[i] = dp[i-1] + dp[i-2]

Space O(1):
  prev2=0, prev1=1
  i=2: curr=1, prev2=1, prev1=1
  i=3: curr=2, prev2=1, prev1=2
  i=4: curr=3, prev2=2, prev1=3
  i=5: curr=5 ✅
```

## 🔄 So sánh các Approach

### Fibonacci: Recursive vs Memoization vs Tabulation vs Space-Opt

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Naive Recursive | O(2^n) | O(n) | **TLE** |
| **Memoization ⭐** | O(n) | O(n) | Top-down, intuitive |
| **Tabulation ⭐** | O(n) | O(n) | Bottom-up, no recursion |
| **Space Optimized ⭐** | O(n) | O(1) | Best overall |

### Coin Change: BFS vs DP

| Approach | Time | Space |
|----------|------|-------|
| **DP ⭐** | O(amount * coins) | O(amount) |
| BFS | O(amount * coins) | O(amount) |

## 🚨 Edge Cases cần chú ý

```java
// Coin Change:
// 1. amount = 0 → 0 (không cần đồng xu nào)
// 2. Không thể đổi → -1 (dp[amount] > amount)
// 3. coins = [2], amount = 3 → -1 (lẻ)

// House Robber:
// 1. nums.length = 1 → nums[0]
// 2. nums.length = 2 → max(nums[0], nums[1])

// Climbing Stairs:
// 1. n = 0 → 0 hoặc 1 (tùy definition)
// 2. n = 1 → 1
// 3. n = 2 → 2
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Fibonacci / Climbing Stairs | O(n) | O(1) |
| Coin Change | O(n * m) | O(n) |
| House Robber | O(n) | O(1) |
| Word Break | O(n² * L) | O(n) |
| Longest Palindromic Substring | O(n²) | O(n²) hoặc O(1) |

## 💡 Tips phỏng vấn

1. **Xác định state**: `dp[i]` là gì? Phát biểu rõ ràng trước khi code.
2. **Base case**: Luôn khởi tạo `dp[0]` và `dp[1]` trước.
3. **Transition direction**: Bottom-up (i=0→n) hay top-down (n→0)?
4. **Space optimization**: Khi transition chỉ dùng dp[i-1] và dp[i-2] → dùng 2 biến.
5. **Infinity init**: Với min DP, khởi tạo với `amount+1` hoặc `Integer.MAX_VALUE/2` để tránh overflow.
