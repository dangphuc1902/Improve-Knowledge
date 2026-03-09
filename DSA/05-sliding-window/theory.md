# 05 - Sliding Window

## 📖 Tổng quan

**Sliding Window** là kỹ thuật duy trì một "cửa sổ" (subarray/substring liên tiếp) trượt trên mảng/chuỗi. Thay vì tính toán lại từ đầu, chỉ cần cập nhật khi mở rộng/thu hẹp cửa sổ.

Biến O(n²) brute force → **O(n)**.

## 🔍 Khi nào sử dụng?

- Bài yêu cầu tìm **subarray/substring** liên tiếp thỏa điều kiện
- **Maximum/Minimum** subarray với điều kiện ràng buộc
- Từ khóa: "contiguous", "substring", "subarray", "window"
- Có thể mở rộng/thu hẹp window bằng cách thêm/bớt phần tử

## 📝 Các Pattern phổ biến

### Pattern 1: Fixed Size Window
```java
// Window kích thước cố định k
int windowSum = 0;
for (int i = 0; i < nums.length; i++) {
    windowSum += nums[i];
    if (i >= k) windowSum -= nums[i - k]; // Bỏ phần tử cũ nhất
    if (i >= k - 1) maxSum = Math.max(maxSum, windowSum);
}
```

### Pattern 2: Variable Size Window (Expand/Shrink)
```java
// Tìm window nhỏ nhất/lớn nhất thỏa điều kiện
int left = 0;
for (int right = 0; right < n; right++) {
    // Mở rộng: thêm nums[right] vào window

    while (/* window không hợp lệ */) {
        // Thu hẹp: bỏ nums[left] khỏi window
        left++;
    }

    // Cập nhật kết quả
    result = Math.max(result, right - left + 1);
}
```

### Pattern 3: HashMap + Sliding Window
```java
// Dùng HashMap đếm frequency trong window
Map<Character, Integer> window = new HashMap<>();
int left = 0;
for (int right = 0; right < s.length(); right++) {
    char c = s.charAt(right);
    window.put(c, window.getOrDefault(c, 0) + 1);

    while (/* điều kiện vi phạm */) {
        char leftChar = s.charAt(left);
        window.put(leftChar, window.get(leftChar) - 1);
        left++;
    }
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Best Time to Buy and Sell Stock - step by step
```
Input: prices = [7, 1, 5, 3, 6, 4]
       indices: 0  1  2  3  4  5

Bước 1: i=0, price=7
  minPrice = min(MAX, 7) = 7
  profit = 7 - 7 = 0
  maxProfit = max(0, 0) = 0

Bước 2: i=1, price=1
  minPrice = min(7, 1) = 1
  profit = 1 - 1 = 0
  maxProfit = max(0, 0) = 0

Bước 3: i=2, price=5
  minPrice = min(1, 5) = 1
  profit = 5 - 1 = 4 ✓
  maxProfit = max(0, 4) = 4

Bước 4: i=3, price=3
  minPrice = min(1, 3) = 1
  profit = 3 - 1 = 2
  maxProfit = max(4, 2) = 4

Bước 5: i=4, price=6
  minPrice = min(1, 6) = 1
  profit = 6 - 1 = 5 ✓✓
  maxProfit = max(4, 5) = 5

Bước 6: i=5, price=4
  minPrice = min(1, 4) = 1
  profit = 4 - 1 = 3
  maxProfit = max(5, 3) = 5

✅ Output: 5 (mua tại 1, bán tại 6)
```

**Insight:** Không phải sliding window thật, nhưng tracking min = one-pass optimization

---

### Ví dụ 2: Longest Substring Without Repeating Characters - step by step
```
Input: s = "abcabcbb"
       indices: 0 1 2 3 4 5 6 7

Bước 1: right=0, c='a'
  window = {a: 1}
  left = 0, maxLen = 0 - 0 + 1 = 1
  window: [a]

Bước 2: right=1, c='b'
  window = {a: 1, b: 1}
  left = 0, maxLen = 1 - 0 + 1 = 2
  window: [a, b]

Bước 3: right=2, c='c'
  window = {a: 1, b: 1, c: 1}
  left = 0, maxLen = 2 - 0 + 1 = 3
  window: [a, b, c]

Bước 4: right=3, c='a' ← DUPLICATE!
  while (window.contains('a')):
    remove s[0]='a' → window = {b: 1, c: 1}
    left = 1
  window.add('a') → window = {b: 1, c: 1, a: 1}
  maxLen = max(3, 3 - 1 + 1) = 3
  window: [b, c, a]

Bước 5: right=4, c='b' ← DUPLICATE!
  while (window.contains('b')):
    remove s[1]='b' → window = {c: 1, a: 1}
    left = 2
  window.add('b') → window = {c: 1, a: 1, b: 1}
  maxLen = max(3, 4 - 2 + 1) = 3
  window: [c, a, b]

Bước 6: right=5, c='c' ← DUPLICATE!
  while (window.contains('c')):
    remove s[2]='c' → window = {a: 1, b: 1}
    left = 3
  window.add('c') → window = {a: 1, b: 1, c: 1}
  maxLen = 3
  window: [a, b, c]

Bước 7: right=6, c='b' ← DUPLICATE!
  while (window.contains('b')):
    remove s[3]='a' → window = {b: 1, c: 1}
    left = 4
  window.add('b') → window = {b: 1, c: 1, b: 2}
  Vẫn có 'b' dup → remove s[4]='b' → window = {c: 1}
  left = 5
  window.add('b') → window = {c: 1, b: 1}
  maxLen = 3
  window: [c, b]

Bước 8: right=7, c='b' ← DUPLICATE!
  while (window.contains('b')):
    remove s[5]='c' → window = {b: 1}
    left = 6
  window.add('b') → window = {b: 2}
  Vẫn duplicate → remove s[6]='b' → window = {}
  left = 7
  window.add('b') → window = {b: 1}
  maxLen = max(3, 7 - 7 + 1) = 3
  window: [b]

✅ Output: 3 (substr "abc")
```

**Insight:** HashSet để track duplicate, shrink left khi gặp duplicate

---

### Ví dụ 3: Minimum Window Substring - step by step
```
Input: s = "ADOBECODEBANC", t = "ABC"

Step 1: Setup
  need = {'A': 1, 'B': 1, 'C': 1}
  required = 3 (3 unique chars)
  formed = 0 (chưa có char nào đủ frequency)

Step 2-3: Expand right window
  right=0: c='A' → window={'A':1} → need has 'A' && window['A']==need['A']? YES → formed=1
  right=1: c='D' → window={'A':1,'D':1}
  right=2: c='O' → window={'A':1,'D':1,'O':1}
  right=3: c='B' → window={'A':1,'D':1,'O':1,'B':1} → formed=2
  right=4: c='E' → window={'A':1,...,'E':1}
  right=5: c='C' → window={'A':1,...,'C':1} → formed=3 ✓

Step 3: formed == required? YES
  Shrink: window = "ADOBEC" [0:6]
  length = 6 - 0 + 1 = 6
  result = [6, 0, 5]
  
  left=0: remove 'A' → formed=2
  left=1

Step 4: Continue expanding
  right=6: c='O' → window={'D':2,...}
  right=7: c='D' → window={'D':3,...}
  right=8: c='E' → window={'E':2,...}
  
  (keep expanding...)

Step 5: Backtrack - vài bước:
  ...
  Eventually: window = "BANC" [9:13]
  length = 13 - 9 + 1 = 4 < 6
  result = [4, 9, 12]

✅ Output: "BANC"

Complexity:
- Outer loop (right): O(n)
- Inner loop (left): O(n) tổng (mỗi char xuất hiện tối đa 2 lần)
- Thao tác HashMap: O(1) average
- Time: O(n + m) where m=len(t)
```

**Insight:** Track `formed` để biết khi nào window hợp lệ, sau đó shrink để tìm min

---

## 🔄 So sánh các Approach

### Best Time to Buy and Sell: One Pass vs Brute Force
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| One Pass (Track Min) ⭐ | O(n) | O(1) | Simple, efficient |
| Brute Force | O(n²) | O(1) | Check mọi cặp |

### Longest Substring: Sliding Window vs Brute Force
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Sliding Window ⭐ | O(n) | O(min(n,26)) | Optimal |
| Brute Force | O(n³) | O(min(n,26)) | Slow |

### Min Window: Sliding Window vs Brute Force
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Sliding Window ⭐ | O(n+m) | O(m) | Best |
| Brute Force | O(n²×m) | O(m) | Very slow |

---

## 🚨 Edge Cases & Common Mistakes

```java
// Best Time to Buy and Sell:
// 1. prices = [7,6,4,3,1] → 0 (prices luôn giảm)
// 2. prices = [1] → 0
// 3. prices = [1,2,3] → 2

// Longest Substring:
// 1. s = "" → 0
// 2. s = "a" → 1
// 3. s = "au" → 2
// 4. s = "dvdf" → 3 (substr "vdf")
// ⚠️ MISTAKE: Dùng Array[26] thay HashSet
//   Cũng được, nhưng HashSet dễ hiểu hơn

// Min Window:
// 1. s = "", t = "a" → ""
// 2. s = "a", t = "b" → ""
// 3. s = "ab", t = "ab" → "ab"
// ⚠️ MISTAKE: Quên check if (need.containsKey(c))
//   Nếu không, formed sẽ tăng cho char không cần
// ⚠️ MISTAKE: Dùng == để so sánh Integer
//   Dùng .intValue() hoặc .equals()
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Brute force (mọi subarray) | O(n²) | O(1) |
| Sliding Window | O(n) | O(1) hoặc O(k) |
| Sliding Window + HashMap | O(n) | O(min(n, alphabet)) |

## 💡 Tips phỏng vấn

1. **Template**: Luôn dùng `left`/`right` pointer, right mở rộng, left thu hẹp
2. **Khi nào shrink**: Xác định rõ điều kiện "window không hợp lệ"
3. **Update result**: Cẩn thận update ở đúng thời điểm (sau expand hay sau shrink)
4. **Edge case**: Chuỗi rỗng, k > n, tất cả ký tự giống nhau
