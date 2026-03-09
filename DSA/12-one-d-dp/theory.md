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

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Climbing Stairs - step by step
```
Input: n = 5

Tư duy: Bước lên bậc i có 2 cách:
  1. Từ bậc i-1 bước 1 bậc
  2. Từ bậc i-2 bước 2 bậc
  
Vậy: cách(i) = cách(i-1) + cách(i-2) ← Fibonacci!

Base cases:
  cách(1) = 1: {1}
  cách(2) = 2: {1+1, 2}

DP array:
  dp[0] = (base)
  dp[1] = 1
  dp[2] = 2 (= dp[1] + dp[0] = 1 + 1 = 2)
  dp[3] = 3 (= dp[2] + dp[1] = 2 + 1 = 3)
  dp[4] = 5 (= dp[3] + dp[2] = 3 + 2 = 5)
  dp[5] = 8 (= dp[4] + dp[3] = 5 + 3 = 8)

Space optimize:
  Chỉ need dp[i-1] và dp[i-2]
  
  prev2 = 1  (dp[1])
  prev1 = 2  (dp[2])
  
  i=3: curr = 2 + 1 = 3, prev2=2, prev1=3
  i=4: curr = 3 + 2 = 5, prev2=3, prev1=5
  i=5: curr = 5 + 3 = 8, prev2=5, prev1=8
  
  return 8 ✓

✅ Output: 8 ways
```

**Insight:** Pure fibonacci, nhưng với context "staircase" → DP

---

### Ví dụ 2: House Robber - step by step
```
Input: nums = [5, 3, 4, 11, 2]

Tư duy: Tại nhà i, 2 lựa chọn:
  1. Không cướp: rob[i] = rob[i-1]
  2. Cướp: rob[i] = nums[i] + rob[i-2] (skip nhà i-1)
  
DP[i] = max(rob[i-1], num[i-2] + nums[i])

Base cases:
  rob[0] = nums[0] = 5
  rob[1] = max(nums[0], nums[1]) = max(5, 3) = 5

DP array:
  dp[0] = 5 (cướp nhà 0)
  dp[1] = 5 (cướp nhà 0, skip nhà 1)
  dp[2] = max(5, 4+5) = max(5, 9) = 9 (cướp 0 + 2)
  dp[3] = max(9, 11+5) = max(9, 16) = 16 (cướp 0 + 3)
  dp[4] = max(16, 2+9) = max(16, 11) = 16 (cướp 0 + 3)

Space optimize:
  prev2 = 0 (before dp[0])
  prev1 = 5 (dp[0])
  
  i=1: curr = max(5, 0+3) = 5, prev2=5, prev1=5
  i=2: curr = max(5, 5+4) = 9, prev2=5, prev1=9
  i=3: curr = max(9, 5+11) = 16, prev2=9, prev1=16
  i=4: curr = max(16, 9+2) = 16, prev2=16, prev1=16
  
  return 16 ✓

✅ Output: 16 (cướp nhà 0, 2, 3)
```

**Insight:** Take-or-skip pattern, constraint = không consecutive

---

### Ví dụ 3: Longest Increasing Subsequence - step by step
```
Input: nums = [10, 9, 2, 5, 3, 7, 101, 18]

Cách 1: DP O(n²)

DP[i] = LIS kết thúc tại index i

  i=0: nums[0]=10, dp[0]=1 (chỉ 10)
  i=1: nums[1]=9
    j=0: 10 < 9? NO
    dp[1]=1
  
  i=2: nums[2]=2
    j=0,1: 10<2? 9<2? NO
    dp[2]=1
  
  i=3: nums[3]=5
    j=0: 10<5? NO
    j=1: 9<5? NO
    j=2: 2<5? YES → dp[3]=max(1, dp[2]+1)=2
    dp[3]=2  [2,5]
  
  i=4: nums[4]=3
    j=0: 10<3? NO
    j=1: 9<3? NO
    j=2: 2<3? YES → dp[4]=max(1, 1+1)=2
    dp[4]=2  [2,3]
  
  i=5: nums[5]=7
    j=0: 10<7? NO
    j=1: 9<7? NO
    j=2: 2<7? YES → dp[5]=max(1, 1+1)=2
    j=3: 5<7? YES → dp[5]=max(2, 2+1)=3
    j=4: 3<7? YES → dp[5]=max(3, 2+1)=3
    dp[5]=3  [2,5,7]
  
  i=6: nums[6]=101
    j=all: ... → 101 > all nums[j]
    j=5: dp[5]=3 → dp[6]=max(prev, 3+1)=4
    dp[6]=4  [2,5,7,101]
  
  i=7: nums[7]=18
    j=5: 7<18 → dp[7]=max(1, 3+1)=4
    j=6: 101<18? NO
    dp[7]=4  [2,5,7,18]

maxLen = 4

✅ Output: 4 (LIS = [2,5,7,101])

---

Cách 2: Binary Search O(n log n) - tối ưu

tails[i] = phần tử nhỏ nhất kết thúc LIS độ dài i+1
(maintain sorted)

  num=10: tails=[], pos=0 → tails=[10]
  num=9: binary_search(9) in [10] → pos=0 → tails=[9]
  num=2: binary_search(2) in [9] → pos=0 → tails=[2]
  num=5: binary_search(5) in [2] → pos=1 → tails=[2,5]
  num=3: binary_search(3) in [2,5] → pos=1 → tails=[2,3]
  num=7: binary_search(7) in [2,3] → pos=2 → tails=[2,3,7]
  num=101: binary_search(101) in [2,3,7] → pos=3 (insert at end) → tails=[2,3,7,101]
  num=18: binary_search(18) in [2,3,7,101] → pos=3 → tails=[2,3,7,18]

return tails.size() = 4

✅ Output: 4 (tốt nhất!)
```

**Insight:** Binary search optimization = maintain smallest tail for each length

---

## 🔄 So sánh các Approach

### Climbing Stairs
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Recursion | O(2^n) | O(n) | Exponential, slow ✗ |
| DP O(n²) | O(n) | O(n) | Standard |
| DP Optimized ⭐ | O(n) | O(1) | Best |

### House Robber
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Backtracking | O(2^n) | O(n) | Slow |
| DP Array | O(n) | O(n) | Standard |
| DP Optimized ⭐ | O(n) | O(1) | Space efficient |

### LIS
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| DP O(n²) | O(n²) | O(n) | Straightforward |
| Binary Search ⭐ | O(n log n) | O(n) | Best |
| Greedy+Binary | O(n log n) | O(n) | Optimal |

---

## 🚨 Edge Cases & Mistakes

```java
// Climbing Stairs:
// 1. n = 0 → 0 (edge case)
// 2. n = 1 → 1
// 3. n = 2 → 2

// House Robber:
// 1. nums = [5] → 5
// 2. nums = [5, 3] → 5
// 3. nums = [1, 100, 1, 1, 1] → 100 + 1 = 101

// LIS:
// 1. nums = [] → 0
// 2. nums = [1] → 1
// 3. nums = [5,4,3,2,1] (decreasing) → 1
// 4. nums = [1,2,3,4,5] (increasing) → 5
// ⚠️ MISTAKE: LIS ≠ LCS (Longest Common Subsequence)
//   LIS = tại 1 array có tính chất increasing
//   LCS = phần chung giữa 2 arrays
```

## 5 bước định nghĩa DP

1. **State**: dp[i] = ? (define clearly)
2. **Recurrence**: dp[i] = f(dp[i-1], dp[i-2], ...)
3. **Base case**: dp[0], dp[1], ... = ?
4. **Order**: Tính từ trái sang phải (i=0 → n)
5. **Optimize space**: Nếu chỉ need 2 state trước → dùng 2 biến

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
