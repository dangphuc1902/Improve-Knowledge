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
- **Nó là gì?**: Thuật toán tìm kiếm phần tử bằng cách chia đôi phạm vi tìm kiếm sau mỗi lần so sánh giá trị ở giữa (`mid`) với giá trị cần tìm (`target`).
- **Giải quyết bài toán nào?**: 
    - Tìm vị trí của một số trong mảng đã sắp xếp (`Binary Search`).
    - Tìm vị trí chèn phần tử (`Search Insert Position`).
- **Ưu điểm**:
    - Hiệu suất cực cao O(log n), thích hợp với mảng hàng triệu phần tử.
- **Nhược điểm**:
    - Chỉ hoạt động trên mảng đã **sắp xếp**.
    - Việc truy cập ngẫu nhiên (random access) trên mảng liên tiếp là bắt buộc (không hiệu quả trên LinkedList).
- **Sự thay thế**:
    - **Linear Search**: Duyệt tuần tự (O(n)), dùng khi mảng chưa sort và không muốn tốn chi phí sort.

```java
int left = 0, right = nums.length - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
```

### Pattern 2: Search in Rotated Sorted Array
- **Nó là gì?**: Một biến thể của Binary Search dùng cho mảng đã từng được sắp xếp nhưng bị "xoay" (rotated) tại một điểm nào đó. Key insight là ít nhất một nửa của mảng (trái hoặc phải) luôn được sắp xếp.
- **Giải quyết bài toán nào?**: 
    - Tìm kiếm trong mảng xoay (`Search in Rotated Sorted Array`).
    - Tìm điểm nhỏ nhất trong mảng xoay (`Find Minimum in Rotated Sorted Array`).
- **Ưu điểm**:
    - Vẫn giữ được độ phức tạp O(log n).
- **Nhược điểm**:
    - Logic phức tạp hơn vì phải xác định nửa nào là "Sorted" trước khi quyết định thu hẹp phạm vi.
- **Sự thay thế**:
    - **Linear Search**: O(n).

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
- **Nó là gì?**: Thay vì tìm kiếm trên mảng input, ta thực hiện Binary Search trên **không gian giá trị của đáp án** (từ giá trị nhỏ nhất có thể đến giá trị lớn nhất có thể). Với mỗi `mid`, ta dùng một hàm `check(mid)` để xem giá trị đó có thỏa mãn yêu cầu không.
- **Giải quyết bài toán nào?**: 
    - Tìm vận tốc ăn chuối nhỏ nhất (`Koko Eating Bananas`).
    - Tìm trọng tải tàu tối thiểu (`Capacity To Ship Packages Within D Days`).
- **Ưu điểm**:
    - Giải quyết được các bài toán tối ưu hóa (Min-Max) phức tạp mà Brute Force không thể làm nổi.
- **Nhược điểm**:
    - Phải xác định được dải giá trị của đáp án (Lower bound & Upper bound).
    - Cần viết thêm hàm `isValid/check` với độ phức tạp tốt (thường là O(n)).
- **Sự thay thế**:
    - **Brute Force**: Thử từng giá trị đáp án từ nhỏ đến lớn (O(max_ans * n)).

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
