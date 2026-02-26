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

### Pattern 1: Opposite Direction (Sorted Array)
```java
int left = 0, right = nums.length - 1;
while (left < right) {
    int sum = nums[left] + nums[right];
    if (sum == target) return true;
    else if (sum < target) left++;
    else right--;
}
```

### Pattern 2: Palindrome Check
```java
int left = 0, right = s.length() - 1;
while (left < right) {
    if (s.charAt(left) != s.charAt(right)) return false;
    left++;
    right--;
}
return true;
```

### Pattern 3: Skip Duplicates (3Sum)
```java
// Sau khi tìm được kết quả, skip duplicate
while (left < right && nums[left] == nums[left + 1]) left++;
while (left < right && nums[right] == nums[right - 1]) right--;
left++;
right--;
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
