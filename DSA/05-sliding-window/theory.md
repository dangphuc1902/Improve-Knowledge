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
