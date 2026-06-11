# 18 - Math & Geometry

## 📖 Tổng quan

**Math & Geometry** bao gồm các bài toán sử dụng kiến thức toán học và thao tác trên ma trận/hình học.

## 🧠 Kiến thức cốt lõi

### Ma trận (Matrix)
```
Rotate 90° clockwise: Transpose → Reverse mỗi row
Rotate 90° counter-clockwise: Reverse mỗi row → Transpose
Transpose: matrix[i][j] ↔ matrix[j][i]
```

### Spiral traversal
```
Dùng 4 boundary: top, bottom, left, right
Duyệt: → ↓ ← ↑, thu hẹp boundary mỗi vòng
```

### Power (x^n)
```
Fast power (binary exponentiation):
x^n = (x^(n/2))² nếu n chẵn
x^n = x × (x^(n/2))² nếu n lẻ
O(log n) thay vì O(n)
```

### Số học thường gặp
| Concept | Formula/Trick |
|---------|---------------|
| GCD | `gcd(a, b) = gcd(b, a % b)` |
| LCM | `lcm(a, b) = a * b / gcd(a, b)` |
| Modular | `(a + b) % m = ((a % m) + (b % m)) % m` |
| Check prime | Chỉ cần kiểm tra đến √n |

## 🔍 Khi nào sử dụng?

- **Rotate/Spiral** ma trận
- **Fast power** (x^n trong O(log n))
- Bài liên quan **counting, combinatorics**
- **GCD, LCM, modular arithmetic**
- **Coordinate geometry** trên grid

## 📝 Các Pattern phổ biến

### Pattern 1: Rotate Matrix In-place (Transpose & Reverse)
- **Nó là gì?**: Để xoay một ma trận 90 độ theo chiều kim đồng hồ mà không dùng thêm ma trận phụ, ta thực hiện hai bước: chuyển vị ma trận (Transpose - đổi hàng thành cột) và sau đó đảo ngược từng hàng.
- **Giải quyết bài toán nào?**: 
    - Xoay ảnh 90 độ (`Rotate Image`).
- **Ưu điểm**:
    - O(1) space, thực hiện trực tiếp trên ma trận gốc.
    - Logic chia nhỏ bài toán giúp code dễ hiểu và ít lỗi.
- **Nhược điểm**:
    - Phải là ma trận vuông (n x n). Nếu ma trận n x m, cần cách tiếp cận khác hoặc chấp nhận O(n*m) space.
- **Sự thay thế**:
    - **Layer-by-layer rotation**: Xoay từng lớp từ ngoài vào trong (code phức tạp hơn).

```java
// Transpose + Reverse
for (int i = 0; i < n; i++)
    for (int j = i; j < n; j++)
        swap(matrix[i][j], matrix[j][i]); // Transpose
for (int[] row : matrix)
    reverse(row); // Reverse mỗi row
```

### Pattern 2: Spiral Matrix (Boundary Shrinking)
- **Nó là gì?**: Sử dụng 4 biến để quản lý các biên của ma trận (`top`, `bottom`, `left`, `right`). Sau mỗi lần duyệt một cạnh, ta thu hẹp biên đó lại cho đến khi các biên gặp nhau.
- **Giải quyết bài toán nào?**: 
    - Duyệt ma trận theo hình xoắn ốc (`Spiral Matrix`).
    - Tạo ma trận xoắn ốc từ số 1 đến n² (`Spiral Matrix II`).
- **Ưu điểm**:
    - Xử lý được ma trận hình chữ nhật bất kỳ.
- **Nhược điểm**:
    - Cần kiểm tra kỹ điều kiện `top <= bottom` và `left <= right` sau mỗi lần cập nhật biên để tránh duyệt trùng.
- **Sự thay thế**:
    - **Simulation**: Sử dụng mảng `visited` và các hướng `(dr, dc)` (Tốn O(m*n) space).

```java
int top = 0, bottom = m-1, left = 0, right = n-1;
while (top <= bottom && left <= right) {
    for (int c = left; c <= right; c++) add(matrix[top][c]); top++;
    for (int r = top; r <= bottom; r++) add(matrix[r][right]); right--;
    if (top <= bottom) { for (int c = right; c >= left; c--) add(matrix[bottom][c]); bottom--; }
    if (left <= right) { for (int r = bottom; r >= top; r--) add(matrix[r][left]); left++; }
}
```

### Pattern 3: Binary Exponentiation (Fast Power)
- **Nó là gì?**: Tính `x^n` bằng cách chia đôi số mũ sau mỗi bước. Nếu `n` chẵn: `x^n = (x*x)^(n/2)`. Nếu `n` lẻ: `x^n = x * (x*x)^((n-1)/2)`.
- **Giải quyết bài toán nào?**: 
    - Tính lũy thừa (`Pow(x, n)`).
    - Tính số Fibonacci lớn bằng ma trận.
- **Ưu điểm**:
    - Giảm số phép nhân từ `n` xuống còn `log n`.
- **Nhược điểm**:
    - Cần xử lý trường hợp số mũ âm và tràn số (Overflow).
- **Sự thay thế**:
    - **Loop**: Nhân `x` n lần (O(n) time).

```java
double myPow(double x, int n) {
    long N = n;
    if (N < 0) { x = 1 / x; N = -N; }
    double result = 1;
    while (N > 0) {
        if ((N & 1) == 1) result *= x;
        x *= x; N >>= 1;
    }
    return result;
}
```

## ⏱️ Complexity thường gặp

| Bài | Time | Space |
|-----|------|-------|
| Rotate Image | O(n²) | O(1) in-place |
| Spiral Matrix | O(m×n) | O(1) ngoài output |
| Pow(x,n) | O(log n) | O(1) |

## 💡 Tips phỏng vấn

1. **Rotate**: Transpose + Reverse là trick cần nhớ — nhanh và in-place
2. **Spiral**: Dùng 4 biên (top/bottom/left/right), cẩn thận check boundary
3. **Overflow**: Pow(x,n) cẩn thận n = Integer.MIN_VALUE → dùng long
4. **Math.**: Java có `Math.pow()` nhưng interviewer muốn bạn implement
