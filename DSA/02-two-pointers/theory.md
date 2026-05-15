# 02 - Two Pointers

## 📖 Tổng quan

**Two Pointers** là kỹ thuật dùng 2 con trỏ (index) di chuyển trên mảng/chuỗi để giải quyết bài toán mà brute force cần O(n²), giảm xuống O(n).

Có 2 biến thể chính:
- **Đối đầu** (opposite direction): left ở đầu, right ở cuối, tiến vào giữa
- **Cùng chiều** (same direction): slow/fast pointer, cùng chạy từ đầu

## 🔍 Khi nào sử dụng?

- Mảng **đã sorted** → nghĩ đến Two Pointers ngay
- Bài yêu cầu tìm **cặp/bộ ba** thỏa điều kiện (sum, product...)
- Kiểm tra **palindrome**
- Bài liên quan đến **container/area** (maximize khoảng cách × giá trị)
- Cần so sánh phần tử từ **2 đầu** mảng/chuỗi

## 📝 Các Pattern phổ biến

### Pattern 1: Opposite Direction (Two Pointers on Sorted Array)
- **Nó là gì?**: Sử dụng hai con trỏ khởi tạo ở hai đầu của mảng (thường là mảng đã sắp xếp) và di chuyển chúng về phía nhau dựa trên một điều kiện so sánh.
- **Giải quyết bài toán nào?**: 
    - Tìm cặp số có tổng bằng `target` (`Two Sum II`).
    - Tìm cặp số có diện tích chứa nước lớn nhất (`Container With Most Water`).
    - Bài toán tìm 3 số có tổng bằng 0 (`3Sum`).
- **Ưu điểm**:
    - Tiết kiệm bộ nhớ (Space Complexity O(1)).
    - Tốc độ xử lý nhanh O(n).
- **Nhược điểm**:
    - Hầu hết yêu cầu mảng phải được **sắp xếp trước** (nếu chưa sort, chi phí sort là O(n log n)).
- **Sự thay thế**:
    - **HashMap**: Có thể tìm cặp số trong mảng chưa sort (O(n) time, O(n) space).
    - **Brute Force**: Kiểm tra mọi cặp (O(n²)).

```java
int left = 0, right = nums.length - 1;
while (left < right) {
    int sum = nums[left] + nums[right];
    if (sum == target) return true;
    else if (sum < target) left++;
    else right--;
}
```

### Pattern 2: Palindrome Check (Symmetry)
- **Nó là gì?**: Kiểm tra tính đối xứng của một chuỗi hoặc mảng bằng cách so sánh các phần tử từ hai đầu tiến vào giữa.
- **Giải quyết bài toán nào?**: 
    - Kiểm tra chuỗi đối xứng (`Valid Palindrome`).
    - Tìm chuỗi con đối xứng dài nhất (phần mở rộng).
- **Ưu điểm**:
    - Dừng ngay lập tức khi phát hiện không khớp (Early Exit).
    - Không tốn thêm bộ nhớ.
- **Nhược điểm**:
    - Cần xử lý các ký tự đặc biệt hoặc khoảng trắng (nếu đề bài yêu cầu).
- **Sự thay thế**:
    - **String Reverse**: Đảo ngược chuỗi rồi so sánh với chuỗi gốc (O(n) space).

```java
int left = 0, right = s.length() - 1;
while (left < right) {
    if (s.charAt(left) != s.charAt(right)) return false;
    left++;
    right--;
}
return true;
```

### Pattern 3: Skip Duplicates (Multi-pointer coordination)
- **Nó là gì?**: Kỹ thuật bỏ qua các phần tử trùng lặp khi di chuyển con trỏ để tránh việc tính toán lại hoặc đưa ra kết quả trùng lặp.
- **Giải quyết bài toán nào?**: 
    - Các bài toán tổ hợp như `3Sum`, `4Sum` nơi kết quả yêu cầu các bộ số duy nhất.
- **Ưu điểm**:
    - Giúp kết quả chính xác theo yêu cầu đề bài mà không cần dùng `Set` để lọc lại (tiết kiệm bộ nhớ).
- **Nhược điểm**:
    - Dễ gây lỗi logic "off-by-one" hoặc quên không kiểm tra biên `left < right`.
- **Sự thay thế**:
    - Sử dụng `HashSet<List<Integer>>` để lưu kết quả (Tốn thêm O(n) space và tốn thời gian hash).

```java
// Sau khi tìm được kết quả, skip duplicate
while (left < right && nums[left] == nums[left + 1]) left++;
while (left < right && nums[right] == nums[right - 1]) right--;
left++;
right--;
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Valid Palindrome - step by step
```
Input: "A man, a plan, a canal: Panama"

Khởi tạo: left=0, right=31

Bước 1: left=0, s[0]='A' (valid)
        right=31, s[31]='a' (valid)
        toLowerCase('A') == toLowerCase('a')? YES ✓
        left++, right--

Bước 2: left=1, s[1]=' ' (SKIP, không alphanumeric)
        left++
        
        left=2, s[2]='m' (valid)
        right=30, s[30]='n' (valid)
        toLowerCase('m') != toLowerCase('a')? WAIT...
        
        Tiếp tục... (mô phỏng quá dài)

✅ Output: true (chuỗi đúng thực là palindrome nếu chỉ xét chữ số)
```

**Insight:** Skip non-alphanumeric, compare case-insensitive

---

### Ví dụ 2: 3Sum - step by step
```
Input: nums = [-1, 0, 1, 2, -1, -4], target = 0

Bước 1: Sort mảng
nums = [-4, -1, -1, 0, 1, 2]

Bước 2: Duyệt từ i=0 đến n-3

i=0: nums[0]=-4
  left=1, right=5
  sum = -4 + (-1) + 2 = -3 (< 0) → left++
  
  left=2, right=5
  sum = -4 + (-1) + 2 = -3 (< 0) → left++
  
  left=3, right=5
  sum = -4 + 0 + 2 = -2 (< 0) → left++
  
  left=4, right=5
  sum = -4 + 1 + 2 = -1 (< 0) → left++
  
  left=5, left >= right → exit while

i=1: nums[1]=-1
  left=2, right=5
  sum = -1 + (-1) + 2 = 0 ✓
  result.add([-1, -1, 2])
  
  skip duplicate: nums[2]==-1 → left++
                   nums[5]==2, nums[4]=1 → right--
  
  left=3, right=4
  sum = -1 + 0 + 1 = 0 ✓
  result.add([-1, 0, 1])
  
  left++, right--
  left >= right → exit while

i=2: nums[2]=-1
  i>0 && nums[2]==nums[1]? YES → SKIP (duplicate)

i=3: nums[3]=0
  nums[3] > 0? NO
  left=4, right=5
  sum = 0 + 1 + 2 = 3 (> 0) → right--
  left >= right → exit while

i=4: i < n-2? NO → exit for

✅ Output: [[-1,-1,2], [-1,0,1]]
```

**Insight:** Sort → fixed element → 2 pointers tìm complement

---

### Ví dụ 3: Container With Most Water - step by step
```
Input: height = [1,8,6,2,5,4,8,3,7]
       indices: 0 1 2 3 4 5 6 7 8

Visual:
       8|
       6|   #
       4|   #     #
       2|   #   # #   # #
       0| # # # # # # # # #
         0 1 2 3 4 5 6 7 8

Bước 1: left=0, right=8
  width = 8-0 = 8
  h = min(height[0], height[8]) = min(1, 7) = 1
  area = 1 × 8 = 8
  height[0]=1 < height[8]=7 → left++

Bước 2: left=1, right=8
  width = 8-1 = 7
  h = min(height[1], height[8]) = min(8, 7) = 7
  area = 7 × 7 = 49 ✓ (tốt hơn!)
  height[1]=8 >= height[8]=7 → right--

Bước 3: left=1, right=7
  width = 7-1 = 6
  h = min(height[1], height[7]) = min(8, 3) = 3
  area = 3 × 6 = 18
  height[1]=8 >= height[7]=3 → right--

Bước 4: left=1, right=6
  width = 6-1 = 5
  h = min(height[1], height[6]) = min(8, 8) = 8
  area = 8 × 5 = 40
  height[1]=8 >= height[6]=8 → right--

Bước 5: left=1, right=5
  width = 5-1 = 4
  h = min(height[1], height[5]) = min(8, 4) = 4
  area = 4 × 4 = 16
  height[1]=8 >= height[5]=4 → right--

Bước 6: left=1, right=4
  width = 4-1 = 3
  h = min(height[1], height[4]) = min(8, 5) = 5
  area = 5 × 3 = 15
  height[1]=8 >= height[4]=5 → right--

Bước 7: left=1, right=3
  width = 3-1 = 2
  h = min(height[1], height[3]) = min(8, 2) = 2
  area = 2 × 2 = 4
  height[1]=8 >= height[3]=2 → right--

Bước 8: left=1, right=2
  width = 2-1 = 1
  h = min(height[1], height[2]) = min(8, 6) = 6
  area = 6 × 1 = 6
  height[1]=8 >= height[2]=6 → right--

Bước 9: left >= right? YES → exit

✅ Output: 49 (area tốt nhất)
```

**Insight:** Greedy - luôn move short side vì move it là cách duy nhất tăng area

---

## 🔄 So sánh các Approach

### Valid Palindrome: Two Pointer vs String Reverse
| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Two Pointers ⭐ | O(n) | O(1) | In-place | Phức tạp hơn |
| String Reverse | O(n) | O(n) | Đơn giản | Dùng thêm space |

### 3Sum: Two Pointers (Sorted) vs HashMap vs Brute Force
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Sort + 2 Pointers ⭐ | O(n²) | O(1) | Phù hợp nhất |
| HashMap | O(n²) | O(n) | Cũng tốt, dùng space |
| Brute Force | O(n³) | O(1) | Quá chậm |

### Container: Two Pointers vs Brute Force
| Approach | Time | Space |
|----------|------|-------|
| Two Pointers ⭐ | O(n) | O(1) |
| Brute Force | O(n²) | O(1) |

---

## 🚨 Edge Cases & Gotchas

```java
// Valid Palindrome:
// 1. s = "" → true (rỗng coi là palindrome)
// 2. s = " " → true (chỉ space, không có alphanumeric)
// 3. s = "0P" → false

// 3Sum:
// 1. nums = [] → []
// 2. nums = [0] → []
// 3. nums = [0,0,0] → [[0,0,0]]
// 4. nums = [-2,0,1,1,2] → [[-2,0,2], [-2,1,1]] (skip duplicate!)
// 5. PHẢI skip duplicate sau khi tìm result, KHÔNG phải skip trước check

// Container:
// 1. height = [1] → 0 (chỉ 1 phần tử)
// 2. height = [1,1] → 1 (1 × 1)
// 3. height = [1,200,1] → 2 (min(1,1) × 2, KHÔNG phải 1 × 1)
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Brute force | O(n²) hoặc O(n³) | O(1) |
| Two Pointers | O(n) hoặc O(n²) cho 3Sum | O(1) |
| Sort + Two Pointers | O(n log n) | O(1) |

## 💡 Tips phỏng vấn

1. **Prerequisite**: Two pointers trên unsorted array thường cần sort trước
2. **Duplicate handling**: 3Sum cần skip duplicate cẩn thận để tránh kết quả trùng
3. **Boundary**: Luôn check `left < right` trước khi truy cập
4. **Greedy reasoning**: Giải thích TẠI SAO di chuyển pointer này mà không phải kia
