# 05 - Sliding Window

## 📖 Tổng quan

**Sliding Window** là kỹ thuật dùng một "cửa sổ" di chuyển qua mảng/chuỗi, duy trì một trạng thái trong cửa sổ đó. Tránh tính lại từ đầu mỗi lần bằng cách **thêm phải, bỏ trái**.

> **Ý tưởng cốt lõi:** Thay vì O(n²) — với mỗi điểm đầu duyệt lại từ đầu — ta maintain trạng thái và chỉ update khi window mở rộng/thu hẹp. → O(n).

## 🧠 Kiến thức cốt lõi

### Hai loại Sliding Window

| Loại | Kích thước | Khi nào dùng |
|------|-----------|-------------|
| **Fixed Size** | `k` cố định | "subarray of size k", max sum window |
| **Variable Size** | Thay đổi | "longest subarray/substring", min window |

### Template

```java
// Variable Sliding Window
int left = 0, result = 0;
for (int right = 0; right < n; right++) {
    // 1. Expand: thêm nums[right] vào window
    // 2. Shrink: thu hẹp khi window không hợp lệ
    while (windowInvalid()) {
        // Remove nums[left] khỏi window
        left++;
    }
    // 3. Update result
    result = Math.max(result, right - left + 1);
}
```

## 🔍 Khi nào sử dụng?

- **Subarray/substring** liên tiếp thỏa điều kiện
- Tìm **longest/shortest** subarray
- Tìm **maximum/minimum** trong window
- Bài toán có cụm từ: *"consecutive"*, *"subarray"*, *"substring"*, *"window"*
- Khi brute force là O(n²) — sliding window thường giảm xuống O(n)

## 📝 Các Pattern phổ biến

### Pattern 1: Fixed Window Size
- **Nó là gì?**: Window kích thước k di chuyển từ trái sang phải.
- **Giải quyết bài toán nào?**: Maximum Sum Subarray of Size K, Find All Anagrams in String.
- **Ưu điểm**: O(n) — thêm 1 phần tử, bỏ 1 phần tử.
- **Nhược điểm**: Chỉ dùng được khi biết trước kích thước window.

```java
// Max sum subarray size k
int windowSum = 0, maxSum = 0;
for (int i = 0; i < nums.length; i++) {
    windowSum += nums[i];
    if (i >= k) windowSum -= nums[i - k]; // slide window
    if (i >= k - 1) maxSum = Math.max(maxSum, windowSum);
}
```

### Pattern 2: Variable Window — Longest with Constraint
- **Nó là gì?**: Expand right, shrink left khi vi phạm constraint. Tìm window dài nhất.
- **Giải quyết bài toán nào?**: Longest Substring Without Repeating, Longest Substring with At Most K Distinct, Fruit Into Baskets.
- **Ưu điểm**: O(n) — left và right đều chỉ đi từ trái sang phải.
- **Nhược điểm**: Cần xác định "invalid condition" chính xác.

```java
// Longest substring without repeating
Map<Character, Integer> lastSeen = new HashMap<>();
int left = 0, maxLen = 0;
for (int right = 0; right < s.length(); right++) {
    char c = s.charAt(right);
    if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
        left = lastSeen.get(c) + 1; // jump left past duplicate
    }
    lastSeen.put(c, right);
    maxLen = Math.max(maxLen, right - left + 1);
}
```

### Pattern 3: Variable Window — Minimum with Constraint
- **Nó là gì?**: Tìm window nhỏ nhất thỏa điều kiện. Expand cho đến khi valid, sau đó shrink.
- **Giải quyết bài toán nào?**: Minimum Window Substring (LC 76), Minimum Size Subarray Sum.
- **Ưu điểm**: O(n) amortized.
- **Nhược điểm**: Logic điều kiện valid/invalid phức tạp hơn.

```java
// Minimum Window Substring
Map<Character, Integer> need = new HashMap<>();
for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
int left = 0, formed = 0, required = need.size();
int[] ans = {-1, 0, 0}; // [length, left, right]
Map<Character, Integer> window = new HashMap<>();

for (int right = 0; right < s.length(); right++) {
    char c = s.charAt(right);
    window.merge(c, 1, Integer::sum);
    if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) formed++;
    
    while (formed == required) {
        if (ans[0] == -1 || right - left + 1 < ans[0]) ans = new int[]{right - left + 1, left, right};
        char lc = s.charAt(left++);
        window.merge(lc, -1, Integer::sum);
        if (need.containsKey(lc) && window.get(lc) < need.get(lc)) formed--;
    }
}
```

### Pattern 4: Sliding Window Maximum (Monotonic Deque)
- **Nó là gì?**: Tìm max trong mỗi window size k. Dùng Deque đơn điệu (Monotonic Deque).
- **Giải quyết bài toán nào?**: Sliding Window Maximum (LC 239).
- **Ưu điểm**: O(n) — mỗi phần tử push/pop đúng 1 lần.
- **Nhược điểm**: Khó implement đúng, cần track index thay vì value.

```java
// Monotonic Decreasing Deque (front = max)
Deque<Integer> deque = new ArrayDeque<>(); // lưu index
int[] result = new int[nums.length - k + 1];
for (int i = 0; i < nums.length; i++) {
    // Remove out-of-window elements
    while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) deque.pollFirst();
    // Remove smaller elements (maintain decreasing)
    while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast();
    deque.offerLast(i);
    if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Longest Substring Without Repeating — Dry Run

```
Input: s = "abcabcbb"
lastSeen = {}, left=0, maxLen=0

i=0, c='a': lastSeen={}, add. lastSeen={a:0}. len=1. maxLen=1
i=1, c='b': lastSeen={}, add. lastSeen={a:0,b:1}. len=2. maxLen=2
i=2, c='c': add. lastSeen={a:0,b:1,c:2}. len=3. maxLen=3
i=3, c='a': lastSeen[a]=0 >= left=0 → left=0+1=1
             update lastSeen[a]=3. len=3-1+1=3. maxLen=3
i=4, c='b': lastSeen[b]=1 >= left=1 → left=1+1=2
             update lastSeen[b]=4. len=4-2+1=3. maxLen=3
i=5, c='c': lastSeen[c]=2 >= left=2 → left=2+1=3
             update lastSeen[c]=5. len=5-3+1=3. maxLen=3
i=6, c='b': lastSeen[b]=4 >= left=3 → left=4+1=5
             update lastSeen[b]=6. len=6-5+1=2. maxLen=3
i=7, c='b': lastSeen[b]=6 >= left=5 → left=6+1=7
             update lastSeen[b]=7. len=7-7+1=1. maxLen=3

✅ Output: 3 (substring "abc")
```

### Ví dụ 2: Minimum Window Substring — Concept

```
s = "ADOBECODEBANC", t = "ABC"
need = {A:1, B:1, C:1}, required=3

Expand right cho đến khi formed == 3:
  right=0: A → window={A:1}, formed=1
  right=1: D → window={A:1,D:1}, formed=1
  right=2: O → formed=1
  right=3: B → window={B:1,...}, formed=2
  right=4: E → formed=2
  right=5: C → window={C:1,...}, formed=3 ✓
  
Shrink left:
  ans = "ADOBEC" (len=6)
  left=0, remove A → formed=2, stop shrinking
  
Continue expand...
  right=9: A → window={A:1,...}, formed=3 ✓
  Shrink: ans = "DOBECODEBA"? No, shrink từ left=1
  left=1: D → formed=3 (D not in need)
  left=2: O → formed=3
  ...đến khi remove A → formed=2

✅ Final Output: "BANC" (len=4)
```

## 🔄 So sánh các Approach

### Longest Substring: HashMap vs Array[128]

| Approach | Time | Space | Ưu điểm |
|----------|------|-------|---------|
| **HashMap ⭐** | O(n) | O(min(m,n)) | Linh hoạt, mọi ký tự |
| Array[128] | O(n) | O(1) | Nhanh hơn với ASCII |
| HashSet | O(n) | O(n) | Đơn giản nhưng cần update left từng bước |

### Sliding Window Max: Deque vs Heap vs Brute Force

| Approach | Time | Space |
|----------|------|-------|
| **Monotonic Deque ⭐** | O(n) | O(k) |
| Max Heap (PQ) | O(n log k) | O(k) |
| Brute Force | O(n*k) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// Longest Without Repeat:
// 1. s = "" → 0
// 2. s = "aaa" → 1 (duplicate liên tiếp)
// 3. s = " " → 1 (space là ký tự hợp lệ)
// 4. s = "abba" → 2 (TRAP: khi gặp 'a' lần 2, left nhảy qua 'b' đầu tiên)

// Min Window Substring:
// 1. t dài hơn s → ""
// 2. t = "" → "" hoặc tùy definition
// 3. Duplicate trong t: t="AA" cần 2 'A' trong s

// Fixed Window:
// 1. k > nums.length → không hợp lệ
// 2. nums = [] → 0
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Fixed Size Window | O(n) | O(1) hoặc O(k) |
| Longest Substring (various) | O(n) | O(charset size) |
| Minimum Window Substring | O(n + m) | O(charset size) |
| Sliding Window Maximum | O(n) | O(k) |

## 💡 Tips phỏng vấn

1. **Fixed hay Variable?** Xác định ngay từ đầu: kích thước window cố định hay không?
2. **"Invalid condition"**: Định nghĩa rõ khi nào window không hợp lệ để biết khi nào shrink.
3. **Duplicate handling**: Dùng `HashMap<Char, LastIndex>` thay vì `HashSet` để jump `left` nhanh hơn.
4. **Min Window**: Trick `formed` counter — chỉ tăng khi đủ số lượng yêu cầu, không phải khi tồn tại.
5. **Follow-up**: "At most k distinct" → sliding window + hashmap đếm distinct count.
