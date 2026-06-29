# 02 - Two Pointers

## 📖 Tổng quan

**Two Pointers** là kỹ thuật sử dụng **hai con trỏ** (biến index) di chuyển qua một cấu trúc dữ liệu (thường là mảng hoặc chuỗi) để giải quyết bài toán với **O(n) time** thay vì O(n²).

> **Ý tưởng cốt lõi:** Thay vì dùng 2 vòng lặp lồng nhau kiểm tra mọi cặp, dùng 2 con trỏ thông minh thu hẹp không gian tìm kiếm.

## 🧠 Kiến thức cốt lõi

### Hai biến thể chính

| Biến thể | Mô tả | Áp dụng khi |
|----------|-------|-------------|
| **Opposite Ends** | `left=0`, `right=n-1`, co dần vào giữa | Mảng đã sort, tìm cặp tổng |
| **Same Direction** | `slow` & `fast` pointer cùng chiều | Linked list cycle, remove duplicates |

### Complexity

| Approach | Time | Space |
|----------|------|-------|
| Two Pointers | O(n) hoặc O(n log n) nếu cần sort trước | O(1) |
| Brute Force 2 loops | O(n²) | O(1) |

## 🔍 Khi nào sử dụng?

- Mảng/chuỗi **đã sắp xếp** hoặc có thể sắp xếp
- Tìm **cặp/bộ số** thỏa điều kiện (tổng, hiệu, tích)
- **Remove duplicates** hoặc **partition** mảng in-place
- Bài toán **palindrome** (kiểm tra hai đầu)
- Khi thấy pattern: *"tìm hai phần tử sao cho..."*

## 📝 Các Pattern phổ biến

### Pattern 1: Opposite Ends — Tìm cặp thỏa tổng
- **Nó là gì?**: `left` bắt đầu từ 0, `right` từ cuối. Nếu `sum < target` → `left++`, nếu `sum > target` → `right--`.
- **Giải quyết bài toán nào?**: Two Sum (sorted array), Container With Most Water, trapping rain water.
- **Ưu điểm**: O(n) time, O(1) space — tốt hơn HashMap về space.
- **Nhược điểm**: Yêu cầu mảng đã sort (thêm O(n log n) nếu chưa sort).
- **Sự thay thế**: HashMap O(n) time + O(n) space (khi mảng chưa sort).

```java
int left = 0, right = nums.length - 1;
while (left < right) {
    int sum = nums[left] + nums[right];
    if (sum == target) return new int[]{left, right};
    else if (sum < target) left++;
    else right--;
}
```

### Pattern 2: Fix One + Two Pointers — k-Sum
- **Nó là gì?**: Fix một phần tử (vòng for), dùng two pointer cho phần còn lại. Tổng quát hóa cho 3Sum, 4Sum.
- **Giải quyết bài toán nào?**: 3Sum, 3Sum Closest, 4Sum.
- **Ưu điểm**: O(n²) cho 3Sum — tốt hơn O(n³) brute force.
- **Nhược điểm**: Cần xử lý duplicate cẩn thận.
- **Sự thay thế**: HashSet để skip duplicate (code phức tạp hơn).

```java
Arrays.sort(nums);
for (int i = 0; i < nums.length - 2; i++) {
    if (i > 0 && nums[i] == nums[i-1]) continue; // skip dup
    int left = i + 1, right = nums.length - 1;
    while (left < right) {
        int sum = nums[i] + nums[left] + nums[right];
        if (sum == 0) {
            result.add(Arrays.asList(nums[i], nums[left], nums[right]));
            while (left < right && nums[left] == nums[left+1]) left++;
            while (left < right && nums[right] == nums[right-1]) right--;
            left++; right--;
        } else if (sum < 0) left++;
        else right--;
    }
}
```

### Pattern 3: Fast & Slow Pointer — Linked List
- **Nó là gì?**: `slow` đi 1 bước, `fast` đi 2 bước. Khi `fast` đến cuối, `slow` ở giữa.
- **Giải quyết bài toán nào?**: Detect cycle, find middle of list, detect cycle start.
- **Ưu điểm**: O(n) time, O(1) space — không cần HashSet.
- **Nhược điểm**: Khó hình dung hơn, cần cẩn thận null check.
- **Sự thay thế**: HashSet lưu các node đã thăm (O(n) space).

```java
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) return true; // cycle detected
}
return false;
```

### Pattern 4: Container Problem — Maximize Area
- **Nó là gì?**: Tính diện tích giữa hai đường thẳng, luôn move pointer có height nhỏ hơn vì đó là điểm hạn chế.
- **Giải quyết bài toán nào?**: Container With Most Water, Trapping Rain Water.
- **Ưu điểm**: O(n) — thay vì O(n²) brute force.
- **Nhược điểm**: Logic "tại sao move pointer nhỏ hơn" cần giải thích rõ.

```java
int left = 0, right = height.length - 1;
int maxArea = 0;
while (left < right) {
    int area = Math.min(height[left], height[right]) * (right - left);
    maxArea = Math.max(maxArea, area);
    if (height[left] < height[right]) left++;
    else right--;
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: 3Sum — Dry Run Step by Step

```
Input: nums = [-1, 0, 1, 2, -1, -4]
Sau sort: [-4, -1, -1, 0, 1, 2]

i=0, nums[i]=-4: left=1, right=5
  sum=-4+(-1)+2=-3 < 0 → left++
  sum=-4+(-1)+2=-3 < 0 → left++
  sum=-4+0+2=-2 < 0 → left++
  sum=-4+1+2=-1 < 0 → left++
  left >= right → end

i=1, nums[i]=-1: left=2, right=5
  sum=-1+(-1)+2=0 ✓ → add [-1,-1,2]
    skip dup: left++ (nums[2]==-1, no dup after)
    skip dup: right-- (nums[4]==1, no dup before)
    left=3, right=4
  sum=-1+0+1=0 ✓ → add [-1,0,1]
    left=4, right=3 → end

i=2, nums[i]=-1: SKIP (nums[2]==nums[1])

i=3, nums[i]=0: left=4, right=5
  sum=0+1+2=3 > 0 → right--
  left >= right → end

✅ Output: [[-1,-1,2], [-1,0,1]]
```

### Ví dụ 2: Container With Most Water

```
Input: height = [1,8,6,2,5,4,8,3,7]
         idx:     0 1 2 3 4 5 6 7 8

left=0 (h=1), right=8 (h=7):
  area = min(1,7) * 8 = 8. maxArea=8
  h[left]=1 < h[right]=7 → left++

left=1 (h=8), right=8 (h=7):
  area = min(8,7) * 7 = 49. maxArea=49
  h[left]=8 > h[right]=7 → right--

left=1 (h=8), right=7 (h=3):
  area = min(8,3) * 6 = 18. maxArea=49
  right--

left=1 (h=8), right=6 (h=8):
  area = min(8,8) * 5 = 40. maxArea=49
  right--

... tiếp tục cho đến left >= right

✅ Output: 49 (between index 1 and 8)
```

**Tại sao move pointer nhỏ hơn?**
- Nếu move pointer lớn hơn → chiều cao chỉ có thể giảm hoặc bằng → width giảm → area chắc chắn giảm
- Nếu move pointer nhỏ hơn → chiều cao *có thể* tăng → cơ hội tìm area lớn hơn

## 🔄 So sánh các Approach

### 3Sum: Two Pointers vs HashSet vs Brute Force

| Approach | Time | Space | Xử lý dup | Dễ code |
|----------|------|-------|-----------|---------|
| **Two Pointers ⭐** | O(n²) | O(1) | Sort + skip | Trung bình |
| HashSet | O(n²) | O(n) | HashSet | Phức tạp |
| Brute Force | O(n³) | O(1) | Sorting results | Dễ nhất |

### Container With Most Water: Two Pointers vs Brute Force

| Approach | Time | Space |
|----------|------|-------|
| **Two Pointers ⭐** | O(n) | O(1) |
| Brute Force (2 loops) | O(n²) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// 3Sum Edge Cases:
// 1. nums = [0,0,0] → [[0,0,0]] (tất cả bằng 0)
// 2. nums = [-2,0,0,2,2] → [[-2,0,2]] (duplicate cần skip)
// 3. nums.length < 3 → [] (không đủ phần tử)
// 4. Tất cả dương → [] (không có tổng = 0 nếu toàn dương)

// Container Edge Cases:
// 1. height = [1,1] → 1 (chỉ 2 phần tử)
// 2. height = [0,0,0] → 0 (toàn 0)
// 3. Tất cả cùng chiều cao → width * height
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space | Ghi chú |
|----------|------|-------|---------|
| Two Sum (sorted) | O(n) | O(1) | Two pointer |
| 3Sum | O(n²) | O(1) | Sort + two pointer |
| 4Sum | O(n³) | O(1) | 2 loops + two pointer |
| Container With Most Water | O(n) | O(1) | Two pointer |
| Valid Palindrome | O(n) | O(1) | Two pointer từ 2 đầu |
| Trapping Rain Water | O(n) | O(1) | Two pointer + prefix max |

## 💡 Tips phỏng vấn

1. **Câu hỏi đầu tiên**: "Mảng có được sort không?" — nếu có, ngay lập tức nghĩ Two Pointers
2. **Khi thấy target sum**: Luôn nghĩ đến `complement = target - current`
3. **Duplicate handling trong 3Sum**: Luôn `skip` sau khi tìm được bộ hợp lệ
4. **Giải thích rõ**: Tại sao move pointer nào? → Cần lập luận chặt chẽ
5. **Follow-up thường gặp**: "Nếu muốn trả về indices thay vì values?" → Cần HashMap
