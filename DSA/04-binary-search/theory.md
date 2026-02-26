# 04 - Binary Search

## 📖 Tổng quan

**Binary Search** là thuật toán tìm kiếm trên mảng/không gian **đã sorted**, chia đôi phạm vi tìm kiếm mỗi bước.

Từ O(n) brute force → **O(log n)** — cực kỳ hiệu quả cho dữ liệu lớn.

## 🧠 Kiến thức cốt lõi

### Template cơ bản
```java
int left = 0, right = nums.length - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;  // Tránh overflow
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
return -1;
```

> **⚠️ Lưu ý**: Dùng `left + (right - left) / 2` thay vì `(left + right) / 2` để tránh integer overflow.

### Biến thể điều kiện vòng lặp

| Variant | Loop | Return | Use case |
|---------|------|--------|----------|
| Exact match | `left <= right` | `mid` khi found | Tìm phần tử cụ thể |
| Left bound | `left < right` | `left` | Tìm vị trí chèn / first occurrence |
| Shrink range | `left < right` | `left` hoặc `right` | Min/Max thỏa điều kiện |

## 🔍 Khi nào sử dụng?

- Mảng **sorted** hoặc có tính chất **monotonic**
- Bài yêu cầu tìm trong O(log n)
- Tìm **min/max** thỏa điều kiện trên không gian tìm kiếm
- Mảng **rotated sorted**
- **Search space** có thể chia đôi (binary search on answer)

## 📝 Các Pattern phổ biến

### Pattern 1: Standard Binary Search
```java
int left = 0, right = nums.length - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
```

### Pattern 2: Rotated Sorted Array
```java
// Xác định nửa nào sorted, rồi quyết định tìm ở nửa nào
if (nums[left] <= nums[mid]) {
    // Nửa trái sorted
    if (nums[left] <= target && target < nums[mid]) right = mid - 1;
    else left = mid + 1;
} else {
    // Nửa phải sorted
    if (nums[mid] < target && target <= nums[right]) left = mid + 1;
    else right = mid - 1;
}
```

### Pattern 3: Binary Search on Answer
```java
// Tìm giá trị nhỏ nhất thỏa điều kiện
int left = minAnswer, right = maxAnswer;
while (left < right) {
    int mid = left + (right - left) / 2;
    if (isValid(mid)) right = mid;     // mid có thể là đáp án
    else left = mid + 1;               // mid quá nhỏ
}
return left;
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Linear search | O(n) | O(1) |
| Binary Search | O(log n) | O(1) |
| Binary Search + check | O(log n × check cost) | O(1) |

## 💡 Tips phỏng vấn

1. **Overflow**: Luôn dùng `left + (right - left) / 2`
2. **Boundary**: Cẩn thận `<=` vs `<`, `mid + 1` vs `mid` — vẽ ví dụ nhỏ để verify
3. **Rotated array**: Key insight là luôn có ít nhất 1 nửa sorted
4. **Binary search on answer**: Khi bài hỏi "tìm min/max thỏa...", nghĩ đến ngay
