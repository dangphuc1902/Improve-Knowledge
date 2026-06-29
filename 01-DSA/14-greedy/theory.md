# 14 - Greedy

## 📖 Tổng quan

**Greedy** là chiến lược luôn chọn **lựa chọn tốt nhất hiện tại** mà không quan tâm đến tương lai. Không đảm bảo optimal cho mọi bài, nhưng khi áp dụng đúng → nhanh và hiệu quả.

> **Ý tưởng cốt lõi:** Greedy hoạt động khi **"local optimal → global optimal"**. Cần chứng minh tính đúng đắn (exchange argument hoặc greedy stays ahead).

## 🧠 Kiến thức cốt lõi

### Khi nào Greedy đúng?

1. **Greedy choice property**: Chọn locally optimal không ảnh hưởng đến subproblems còn lại
2. **Optimal substructure**: Optimal solution chứa optimal solutions của subproblems
3. **Không có regret**: Quyết định greedy không bao giờ cần "undo"

### Greedy vs DP

| Tiêu chí | Greedy | DP |
|----------|--------|-----|
| Scope | Local optimal | Global optimal |
| Time | Nhanh hơn | Chậm hơn |
| Space | O(1) thường | O(n) thường |
| Khi dùng | Có thể prove greedy | Overlapping subproblems |

## 🔍 Khi nào sử dụng?

- **Scheduling**: Luôn chọn task kết thúc sớm nhất
- **Coin Change** (canonical coins): Luôn chọn coin lớn nhất
- **Jump Game**: Luôn jump xa nhất có thể
- **Huffman Encoding**: Luôn merge 2 node frequency nhỏ nhất
- Cụm từ: *"minimum/maximum"*, *"can we reach"*, *"activity selection"*

## 📝 Các Pattern phổ biến

### Pattern 1: Jump Game — Track Maximum Reach
- **Nó là gì?**: Tại mỗi vị trí, cập nhật `maxReach`. Nếu `i > maxReach` → không thể tiến.
- **Giải quyết bài toán nào?**: Jump Game I & II (LC 55, 45).
- **Ưu điểm**: O(n) — 1 lần duyệt.
- **Sự thay thế**: BFS/DP O(n²) — chậm hơn.

```java
// Jump Game I: Có thể đến cuối không?
public boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false; // trapped
        maxReach = Math.max(maxReach, i + nums[i]);
    }
    return true;
}

// Jump Game II: Tối thiểu bao nhiêu jump?
public int jump(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currentEnd) { // phải jump để đi tiếp
            jumps++;
            currentEnd = farthest;
        }
    }
    return jumps;
}
```

### Pattern 2: Gas Station — Circular Path
- **Nó là gì?**: Nếu total gas >= total cost → có solution. Nếu hiện tại tank < 0 → start lại từ vị trí kế.
- **Giải quyết bài toán nào?**: Gas Station (LC 134).

```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int totalTank = 0, currentTank = 0, startStation = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        totalTank += diff;
        currentTank += diff;
        if (currentTank < 0) { // không thể đi tiếp từ startStation
            startStation = i + 1;
            currentTank = 0;
        }
    }
    return totalTank >= 0 ? startStation : -1;
}
```

### Pattern 3: Partition Labels — Extend Boundary
- **Nó là gì?**: Tìm last occurrence của mỗi ký tự. Duyệt và extend boundary cho đến khi đến boundary.
- **Giải quyết bài toán nào?**: Partition Labels (LC 763).

```java
// last[c] = last index of character c
int[] last = new int[26];
for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;

List<Integer> sizes = new ArrayList<>();
int start = 0, end = 0;
for (int i = 0; i < s.length(); i++) {
    end = Math.max(end, last[s.charAt(i) - 'a']); // extend boundary
    if (i == end) { // reached boundary → new partition
        sizes.add(end - start + 1);
        start = i + 1;
    }
}
```

### Pattern 4: Activity Selection (Sort by End Time)
- **Nó là gì?**: Sort by end time. Luôn chọn activity kết thúc sớm nhất mà không conflict.
- **Giải quyết bài toán nào?**: Meeting Rooms, Non-overlapping Intervals, Interval Scheduling Maximization.

```java
Arrays.sort(activities, (a, b) -> a[1] - b[1]); // sort by end time
int count = 1, lastEnd = activities[0][1];
for (int i = 1; i < activities.length; i++) {
    if (activities[i][0] >= lastEnd) {
        count++;
        lastEnd = activities[i][1];
    }
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Jump Game I — Dry Run

```
nums = [3, 2, 1, 0, 4]

i=0: maxReach = max(0, 0+3) = 3. i=0 <= maxReach=3 ✓
i=1: maxReach = max(3, 1+2) = 3. i=1 <= maxReach=3 ✓
i=2: maxReach = max(3, 2+1) = 3. i=2 <= maxReach=3 ✓
i=3: maxReach = max(3, 3+0) = 3. i=3 <= maxReach=3 ✓
i=4: i=4 > maxReach=3 → return false ❌

✅ Output: false (bị mắc kẹt tại index 3)
```

```
nums = [2, 3, 1, 1, 4]

i=0: maxReach = max(0, 0+2) = 2. ✓
i=1: maxReach = max(2, 1+3) = 4. ✓
i=2: i=2 <= maxReach=4. maxReach = max(4, 2+1) = 4. ✓
i=3: maxReach = max(4, 3+1) = 4. ✓
i=4: maxReach = max(4, 4+4) = 8. ✓

✅ Output: true
```

### Ví dụ 2: Jump Game II — Minimum Jumps

```
nums = [2, 3, 1, 1, 4]

jumps=0, currentEnd=0, farthest=0

i=0: farthest=max(0,0+2)=2. i==currentEnd(0) → jump! jumps=1, currentEnd=2
i=1: farthest=max(2,1+3)=4. i!=currentEnd(2)
i=2: farthest=max(4,2+1)=4. i==currentEnd(2) → jump! jumps=2, currentEnd=4
i=3: farthest=max(4,3+1)=4. i!=currentEnd(4) (i<n-1=4, stop at n-2)

✅ Output: 2 jumps
```

## 🔄 So sánh các Approach

### Jump Game: Greedy vs BFS vs DP

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| **Greedy ⭐** | O(n) | O(1) | Best |
| BFS | O(n) | O(n) | Level = jump count |
| DP | O(n²) | O(n) | dp[i] = min jumps to reach i |

### Gas Station: Greedy vs Brute Force

| Approach | Time | Space |
|----------|------|-------|
| **Greedy ⭐** | O(n) | O(1) |
| Brute Force (try each) | O(n²) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// Jump Game:
// 1. nums = [0] → true (already at end)
// 2. nums = [0, 1] → false (stuck at 0)
// 3. nums = [1, 0, 0] → false (can reach index 1 but stuck)

// Gas Station:
// 1. Single station: gas=[5], cost=[4] → 0
// 2. All tanks empty: gas=[0], cost=[0] → 0
// 3. If totalTank < 0 → impossible, return -1

// Jump Game II:
// 1. Already at end (n=1) → 0 jumps
// 2. Max jump size guarantees reachable (can always reach end)
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Jump Game I | O(n) | O(1) |
| Jump Game II | O(n) | O(1) |
| Gas Station | O(n) | O(1) |
| Partition Labels | O(n) | O(26) = O(1) |
| Activity Selection | O(n log n) | O(1) |

## 💡 Tips phỏng vấn

1. **Prove Greedy**: Luôn nói tại sao greedy choice này là optimal — "exchange argument".
2. **Greedy vs DP**: Hỏi interviewer xem có cần liệt kê all solutions không (→ backtracking), hay chỉ optimal value (→ greedy/DP).
3. **Jump Game II**: Xử lý "đến cuối" là `i < n-1` trong vòng lặp — không cần jump từ node cuối.
4. **Sort là chìa khóa**: Hầu hết greedy bắt đầu với sort theo end time hoặc value/weight ratio.
5. **Gas Station observation**: Nếu `totalTank >= 0` thì luôn có solution, và nó luôn là vị trí sau segment tổng âm cuối cùng.
