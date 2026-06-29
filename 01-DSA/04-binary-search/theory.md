# 04 - Binary Search

## 📖 Tổng quan

**Binary Search** là thuật toán tìm kiếm trong không gian **đã được sắp xếp** bằng cách chia đôi không gian ở mỗi bước, đạt **O(log n)** thay vì O(n).

> **Ý tưởng cốt lõi:** Mỗi lần so sánh, loại bỏ *một nửa* không gian tìm kiếm. Sau log₂(n) bước → tìm ra đáp án.

## 🧠 Kiến thức cốt lõi

### Template chuẩn

```java
int lo = 0, hi = nums.length - 1;
while (lo <= hi) {
    int mid = lo + (hi - lo) / 2; // tránh overflow so với (lo+hi)/2
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) lo = mid + 1;
    else hi = mid - 1;
}
return -1; // not found
```

> **Lưu ý overflow**: `mid = lo + (hi - lo) / 2` an toàn hơn `(lo + hi) / 2` khi lo và hi là số lớn.

### Ba dạng Binary Search

| Dạng | Điều kiện kết thúc | Ứng dụng |
|------|-------------------|----------|
| **Exact Match** | `lo <= hi` | Tìm phần tử target |
| **Left Bound** | Tìm first occurrence | Tìm biên trái |
| **Right Bound** | Tìm last occurrence | Tìm biên phải |

### Complexity

| | Time | Space |
|--|------|-------|
| Binary Search | O(log n) | O(1) |
| Linear Search | O(n) | O(1) |

## 🔍 Khi nào sử dụng?

- Mảng/collection **đã được sắp xếp**
- Tìm phần tử thỏa điều kiện trong **không gian đơn điệu** (monotonic)
- **"Minimize the maximum"** hoặc **"Maximize the minimum"** — Binary Search on Answer
- Cụm từ trong đề: *"sorted"*, *"rotated"*, *"find minimum/maximum"*, *"search"*
- **Nhanh**: Từ O(n) xuống O(log n) → rất quan trọng cho mảng lớn

## 📝 Các Pattern phổ biến

### Pattern 1: Classic Binary Search
- **Nó là gì?**: Tìm giá trị exact trong mảng sorted.
- **Giải quyết bài toán nào?**: Binary Search (LC 704), Search Insert Position.
- **Ưu điểm**: O(log n) — cực nhanh.
- **Nhược điểm**: Chỉ dùng được khi mảng sorted.
- **Sự thay thế**: Linear Search O(n) — đơn giản nhưng chậm hơn.

```java
// lo <= hi: kiểm tra cả khi lo == hi (1 phần tử)
int lo = 0, hi = nums.length - 1;
while (lo <= hi) {
    int mid = lo + (hi - lo) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) lo = mid + 1;
    else hi = mid - 1;
}
return -1;
```

### Pattern 2: Binary Search on Rotated Array
- **Nó là gì?**: Mảng sorted bị rotate → 1 nửa luôn sorted. Xác định nửa nào sorted, kiểm tra target thuộc đó không.
- **Giải quyết bài toán nào?**: Search in Rotated Sorted Array (LC 33), Find Minimum in Rotated Sorted Array (LC 153).
- **Ưu điểm**: O(log n) — không cần tìm pivot trước.
- **Nhược điểm**: Logic phức tạp hơn, dễ sai off-by-one.
- **Sự thay thế**: Tìm pivot trước rồi BS → 2 bước, nhưng vẫn O(log n).

```java
// Xác định nửa nào sorted:
if (nums[lo] <= nums[mid]) {
    // Nửa trái sorted [lo..mid]
    if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
    else lo = mid + 1;
} else {
    // Nửa phải sorted [mid..hi]
    if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
    else hi = mid - 1;
}
```

### Pattern 3: Binary Search on Answer (Search Space)
- **Nó là gì?**: Không BS trên mảng, BS trên **không gian đáp án** — tìm giá trị nhỏ nhất/lớn nhất thỏa điều kiện.
- **Giải quyết bài toán nào?**: Koko Eating Bananas, Minimum Number of Days to Make m Bouquets, Capacity To Ship Packages.
- **Ưu điểm**: Giải được các bài toán O(n * k) xuống O(n log k).
- **Nhược điểm**: Cần xác định được hàm kiểm tra `isValid(mid)` chạy đơn điệu.
- **Ví dụ pattern**: `lo = min possible answer, hi = max possible answer`.

```java
// Koko Eating Bananas: tìm tốc độ ăn nhỏ nhất
// isValid(k) = có thể ăn hết trong h giờ không?
int lo = 1, hi = max(piles);
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;
    if (canFinish(piles, mid, h)) hi = mid; // có thể ăn → thử nhỏ hơn
    else lo = mid + 1;                       // không thể → tăng tốc độ
}
return lo;
```

### Pattern 4: Find Left/Right Bound
- **Nó là gì?**: Tìm vị trí đầu tiên / cuối cùng của target trong mảng có duplicate.
- **Giải quyết bài toán nào?**: Find First and Last Position of Element (LC 34).
- **Ưu điểm**: O(log n), chính xác.
- **Trick**: Khi tìm left bound → khi tìm thấy `hi = mid - 1`. Khi tìm right bound → `lo = mid + 1`.

```java
// Find Left Bound
private int findLeft(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) { result = mid; hi = mid - 1; } // tiếp tục tìm bên trái
        else if (nums[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return result;
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Search in Rotated Sorted Array — Dry Run

```
Input: nums = [4,5,6,7,0,1,2], target = 0

lo=0, hi=6, mid=3, nums[3]=7
  nums[lo]=4 <= nums[mid]=7 → LEFT half sorted [4,5,6,7]
  target=0 ∉ [4,7] → lo = mid+1 = 4

lo=4, hi=6, mid=5, nums[5]=1
  nums[lo]=0 > nums[mid]=1? No, 0 <= 1 → LEFT half sorted [0,1]
  target=0 ∈ [0,1) (nums[lo]=0 <= 0 < nums[mid]=1) → hi = mid-1 = 4

lo=4, hi=4, mid=4, nums[4]=0
  nums[lo]=0 <= nums[mid]=0 → LEFT half sorted
  target=0 == nums[mid]=0 → lo=4, hi: check → target IN range → hi=3? 
  Actually: nums[lo]<=target<nums[mid]? 0<=0<0? NO → lo=mid+1=5... 

[Re-trace với điều kiện đúng]
lo=4, hi=4, mid=4, nums[4]=0
  Check: nums[lo]=0 <= nums[mid]=0 → left sorted
  target=0: nums[lo]=0<=0 AND 0<nums[mid]=0? → 0<0 FALSE → lo=5
  
lo=5>hi=4 → EXIT, return -1 ???

Sai! Cần kiểm tra nums[mid]==target TRƯỚC:
  if nums[mid] == target → return mid
  if left sorted: ...

lo=4, hi=4, mid=4, nums[4]=0 == target=0 → return 4 ✅

✅ Output: 4
```

### Ví dụ 2: Find Minimum in Rotated Array

```
Input: nums = [3,4,5,1,2]

lo=0, hi=4, mid=2, nums[2]=5
  nums[mid]=5 > nums[hi]=2 → minimum ở RIGHT half
  lo = mid+1 = 3

lo=3, hi=4, mid=3, nums[3]=1
  nums[mid]=1 < nums[hi]=2 → minimum ở LEFT half (bao gồm mid)
  hi = mid = 3

lo=3, hi=3, mid=3, nums[3]=1
  nums[mid]=1 < nums[hi]=1 (false) → lo=mid+1=4?
  
  Điều kiện đúng: while (lo < hi), kết thúc khi lo==hi
  lo=3 == hi=3 → EXIT

✅ Output: nums[lo] = nums[3] = 1
```

## 🔄 So sánh các Approach

### Binary Search: Classic vs Recursive

| Approach | Time | Space | Ưu điểm |
|----------|------|-------|---------|
| **Iterative ⭐** | O(log n) | O(1) | Không stack overflow |
| Recursive | O(log n) | O(log n) | Code ngắn hơn |

### Rotated Array: One-pass BS vs Find Pivot + BS

| Approach | Time | Space | Ưu điểm |
|----------|------|-------|---------|
| **One-pass BS ⭐** | O(log n) | O(1) | 1 lần duyệt |
| Find Pivot + BS | O(log n) | O(1) | Dễ code hơn, 2 lần BS |

## 🚨 Edge Cases cần chú ý

```java
// Binary Search:
// 1. nums = [] → -1 (mảng rỗng)
// 2. nums = [5] → 0 nếu target=5, -1 nếu không
// 3. target ở đầu hoặc cuối mảng
// 4. All duplicates: [2,2,2,2], target=2 → trả về index nào?

// Rotated Array:
// 1. [1] → 1 (không bị rotate)
// 2. [1,2,3,4,5] → không bị rotate → BS thường
// 3. Duplicate handling: [2,2,2,1,2] → cần thêm lo++ để skip

// Find Min:
// 1. [1,2,3] → 1 (không rotate, min ở đầu)
// 2. [2,1] → 1
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Classic Binary Search | O(log n) | O(1) |
| Search in Rotated Array | O(log n) | O(1) |
| Find Min in Rotated Array | O(log n) | O(1) |
| Koko Eating Bananas | O(n log m) | O(1) |
| Find Left/Right Bound | O(log n) | O(1) |

## 💡 Tips phỏng vấn

1. **`mid = lo + (hi - lo) / 2`**: Luôn dùng để tránh integer overflow.
2. **`lo < hi` vs `lo <= hi`**: Dùng `lo < hi` khi BS on Answer (tìm bound), `lo <= hi` khi tìm exact match.
3. **Rotated Array trick**: Không cần tìm pivot — chỉ cần xác định nửa nào sorted.
4. **BS on Answer**: Khi thấy "minimum maximum" hay "maximum minimum" → nghĩ đến BS on Answer.
5. **Tránh infinite loop**: Khi `lo < hi` và update `hi = mid` (không `mid-1`) → vẫn ổn vì `mid < hi`.
