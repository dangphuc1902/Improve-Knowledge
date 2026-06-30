# 18 - Math & Geometry: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 3 bài toán thuộc chủ đề **Math & Geometry (Toán & Hình học)** từ LeetCode Master Tracker.

---

## 93. Rotate Image (LeetCode #48) - Medium

### 💡 Ý tưởng cốt lõi
Xoay một ma trận hình vuông kích thước $n \times n$ theo chiều kim đồng hồ góc 90 độ in-place (không sử dụng ma trận phụ để tiết kiệm bộ nhớ).
Ý tưởng toán học tối ưu để xoay ma trận 90 độ là kết hợp hai phép biến đổi đơn giản:
1. **Phép chuyển vị (Transpose)**: Hoán đổi các phần tử đối xứng qua đường chéo chính, tức là `matrix[i][j]` hoán đổi với `matrix[j][i]` với mọi $j > i$.
2. **Phép đảo ngược hàng (Reverse)**: Đảo ngược thứ tự các phần tử trên từng hàng của ma trận đã chuyển vị (đảo ngược từ trái sang phải).

### 📊 Hướng tiếp cận tối ưu

#### Transpose + Reverse in-place (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2)$ do đi qua mọi phần tử trong ma trận 2 lần.
  * **Space Complexity**: $O(1)$ biến đổi trực tiếp trên ma trận ban đầu.

### 🔄 Dry Run với ví dụ
* **Input**:
  ```
  [1, 2, 3]
  [4, 5, 6]
  [7, 8, 9]
  ```
* **Bước 1: Chuyển vị (Swap matrix[i][j] với matrix[j][i])**:
  * Swap (0,1) và (1,0): 2 hoán đổi với 4.
  * Swap (0,2) và (2,0): 3 hoán đổi với 7.
  * Swap (1,2) và (2,1): 6 hoán đổi với 8.
  * Kết quả chuyển vị:
    ```
    [1, 4, 7]
    [2, 5, 8]
    [3, 6, 9]
    ```
* **Bước 2: Đảo ngược từng hàng**:
  * Hàng 0: `[1, 4, 7]` → `[7, 4, 1]`
  * Hàng 1: `[2, 5, 8]` → `[8, 5, 2]`
  * Hàng 2: `[3, 6, 9]` → `[9, 6, 3]`
* **Kết quả**:
  ```
  [7, 4, 1]
  [8, 5, 2]
  [9, 6, 3]
  ```

### 💻 Java Clean Code
```java
public void rotate(int[][] matrix) {
    int n = matrix.length;
    
    // Bước 1: Chuyển vị ma trận (Transpose)
    for (int r = 0; r < n; r++) {
        for (int c = r + 1; c < n; c++) {
            int temp = matrix[r][c];
            matrix[r][c] = matrix[c][r];
            matrix[c][r] = temp;
        }
    }
    
    // Bước 2: Đảo ngược từng hàng (Reverse each row)
    for (int r = 0; r < n; r++) {
        int left = 0, right = n - 1;
        while (left < right) {
            int temp = matrix[r][left];
            matrix[r][left] = matrix[r][right];
            matrix[r][right] = temp;
            left++;
            right--;
        }
    }
}
```

---

## 94. Spiral Matrix (LeetCode #54) - Medium

### 💡 Ý tưởng cốt lõi
Trả về tất cả các phần tử của ma trận $m \times n$ theo thứ tự xoắn ốc (từ ngoài vào trong, theo chiều kim đồng hồ).
Ý tưởng tối ưu là duy trì **4 đường biên (boundaries)** giới hạn vùng chưa duyệt:
* `top`: Biên trên (ban đầu là 0).
* `bottom`: Biên dưới (ban đầu là $m - 1$).
* `left`: Biên trái (ban đầu là 0).
* `right`: Biên phải (ban đầu là $n - 1$).
Ta liên tục lặp qua 4 biên theo đúng chiều:
1. Đi từ trái sang phải dọc theo biên `top`. Sau đó tăng `top++` để co biên trên.
2. Đi từ trên xuống dưới dọc theo biên `right`. Sau đó giảm `right--` để co biên phải.
3. Đi từ phải sang trái dọc theo biên `bottom` (kiểm tra điều kiện `top <= bottom` trước để tránh trùng lặp khi ma trận chỉ còn 1 hàng). Sau đó giảm `bottom--` để co biên dưới.
4. Đi từ dưới lên trên dọc theo biên `left` (kiểm tra điều kiện `left <= right` trước). Sau đó tăng `left++` để co biên trái.

### 📊 Hướng tiếp cận tối ưu

#### Boundary Shrinking (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ đi qua mỗi phần tử đúng 1 lần.
  * **Space Complexity**: $O(1)$ nếu không tính danh sách kết quả đầu ra.

### 💻 Java Clean Code
```java
public List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> res = new ArrayList<>();
    if (matrix == null || matrix.length == 0) return res;
    
    int m = matrix.length;
    int n = matrix[0].length;
    
    int top = 0, bottom = m - 1;
    int left = 0, right = n - 1;
    
    while (top <= bottom && left <= right) {
        // 1. Đi từ trái sang phải dọc theo biên top
        for (int c = left; c <= right; c++) {
            res.add(matrix[top][c]);
        }
        top++; // Co biên trên
        
        // 2. Đi từ trên xuống dưới dọc theo biên right
        for (int r = top; r <= bottom; r++) {
            res.add(matrix[r][right]);
        }
        right--; // Co biên phải
        
        // 3. Đi từ phải sang trái dọc theo biên bottom
        if (top <= bottom) {
            for (int c = right; c >= left; c--) {
                res.add(matrix[bottom][c]);
            }
            bottom--; // Co biên dưới
        }
        
        // 4. Đi từ dưới lên trên dọc theo biên left
        if (left <= right) {
            for (int r = bottom; r >= top; r--) {
                res.add(matrix[r][left]);
            }
            left++; // Co biên trái
        }
    }
    
    return res;
}
```

---

## 95. Set Matrix Zeroes (LeetCode #73) - Medium

### 💡 Ý tưởng cốt lõi
Cho một ma trận $m \times n$, nếu một phần tử bằng 0, hãy đặt toàn bộ hàng và cột của nó thành 0. Yêu cầu thực hiện in-place với bộ nhớ phụ $O(1)$.
Cách thông thường là tạo 2 mảng phụ lưu thông tin hàng/cột cần xóa thành 0 (tốn $O(m+n)$ space).
Để tối ưu về $O(1)$ space, ta **sử dụng chính hàng đầu tiên và cột đầu tiên của ma trận** làm mảng phụ ghi nhớ:
1. Xác định trước xem hàng đầu tiên và cột đầu tiên có chứa số 0 ban đầu không (lưu vào 2 biến boolean `firstRowZero` và `firstColZero`).
2. Quét qua phần còn lại của ma trận (từ hàng 1, cột 1). Nếu `matrix[r][c] == 0`, ta ghi nhớ bằng cách đặt marker: `matrix[r][0] = 0` và `matrix[0][c] = 0`.
3. Quét lại ma trận (từ hàng 1, cột 1). Nếu marker hàng `matrix[r][0] == 0` hoặc marker cột `matrix[0][c] == 0`, ta gán `matrix[r][c] = 0`.
4. Cuối cùng, cập nhật hàng đầu tiên và cột đầu tiên thành 0 nếu 2 biến boolean ghi nhận ban đầu là `true`.

### 📊 Hướng tiếp cận tối ưu

#### Matrix Edge as Markers (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ duyệt qua ma trận 2 lượt.
  * **Space Complexity**: $O(1)$ tái sử dụng vùng biên để lưu marker.

### 💻 Java Clean Code
```java
public void setZeroes(int[][] matrix) {
    int m = matrix.length;
    int n = matrix[0].length;
    
    boolean firstRowZero = false;
    boolean firstColZero = false;
    
    // Bước 1: Kiểm tra xem hàng đầu tiên có chứa số 0 ban đầu không
    for (int c = 0; c < n; c++) {
        if (matrix[0][c] == 0) {
            firstRowZero = true;
            break;
        }
    }
    
    // Bước 2: Kiểm tra xem cột đầu tiên có chứa số 0 ban đầu không
    for (int r = 0; r < m; r++) {
        if (matrix[r][0] == 0) {
            firstColZero = true;
            break;
        }
    }
    
    // Bước 3: Sử dụng hàng và cột đầu tiên để đánh dấu
    for (int r = 1; r < m; r++) {
        for (int c = 1; c < n; c++) {
            if (matrix[r][c] == 0) {
                matrix[r][0] = 0; // Đánh dấu hàng r cần gán 0
                matrix[0][c] = 0; // Đánh dấu cột c cần gán 0
            }
        }
    }
    
    // Bước 4: Gán các ô về 0 dựa trên đánh dấu ở biên
    for (int r = 1; r < m; r++) {
        for (int c = 1; c < n; c++) {
            if (matrix[r][0] == 0 || matrix[0][c] == 0) {
                matrix[r][c] = 0;
            }
        }
    }
    
    // Bước 5: Gán hàng đầu tiên và cột đầu tiên về 0 nếu cần
    if (firstRowZero) {
        for (int c = 0; c < n; c++) {
            matrix[0][c] = 0;
        }
    }
    if (firstColZero) {
        for (int r = 0; r < m; r++) {
            matrix[r][0] = 0;
        }
    }
}
```
