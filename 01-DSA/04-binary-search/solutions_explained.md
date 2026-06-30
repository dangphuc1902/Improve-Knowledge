# 04 - Binary Search: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 6 bài toán thuộc chủ đề **Binary Search** từ LeetCode Master Tracker.

---

## 22. Binary Search (LeetCode #704) - Easy

### 💡 Ý tưởng cốt lõi
Tìm vị trí của `target` trong mảng đã sắp xếp tăng dần `nums`.
Chúng ta sử dụng thuật toán Tìm kiếm nhị phân chuẩn bằng cách duy trì hai con trỏ `lo = 0` và `hi = n - 1`. Ở mỗi bước, ta tính toán phần tử giữa `mid = lo + (hi - lo) / 2`.
* Nếu `nums[mid] == target`, trả về `mid`.
* Nếu `nums[mid] < target`, không gian tìm kiếm được thu hẹp về nửa bên phải bằng cách gán `lo = mid + 1`.
* Nếu `nums[mid] > target`, không gian tìm kiếm được thu hẹp về nửa bên trái bằng cách gán `hi = mid - 1`.

### 📊 Hướng tiếp cận tối ưu

#### Iterative Binary Search (Optimal) ⭐
* **Mô tả**: Sử dụng vòng lặp `while (lo <= hi)` để chia đôi khoảng tìm kiếm.
* **Độ phức tạp**:
  * **Time Complexity**: $O(\log n)$ vì không gian tìm kiếm giảm đi một nửa sau mỗi bước.
  * **Space Complexity**: $O(1)$ chỉ dùng vài biến chỉ số.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [-1, 0, 3, 5, 9, 12]`, `target = 9`
* **Khởi tạo**: `lo = 0`, `hi = 5`
* **Vòng lặp**:
  1. `mid = 0 + (5 - 0) / 2 = 2`. `nums[2] = 3 < 9` → `lo = 3`.
  2. `mid = 3 + (5 - 3) / 2 = 4`. `nums[4] = 9 == 9` → Trả về `4`.
* **Kết quả**: `4`

### 💻 Java Clean Code
```java
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2; // Tránh integer overflow
        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return -1;
}
```

---

## 23. Search a 2D Matrix (LeetCode #74) - Medium

### 💡 Ý tưởng cốt lõi
Tìm một giá trị `target` trong ma trận kích thước $m \times n$ có các hàng được sắp xếp tăng dần và phần tử đầu tiên của mỗi hàng lớn hơn phần tử cuối cùng của hàng trước đó.
Đặc điểm này giúp ta có thể coi toàn bộ ma trận như một mảng 1D được sắp xếp tăng dần có kích thước $m \times n$. Ta có thể áp dụng thuật toán Tìm kiếm nhị phân thông thường trên mảng 1D ảo này.
Để chuyển đổi một chỉ số `mid` trong mảng 1D ảo về tọa độ `(row, col)` trong ma trận 2D:
$$\text{row} = mid / n, \quad \text{col} = mid \% n$$
với $n$ là số cột của ma trận.

### 📊 Hướng tiếp cận tối ưu

#### Flatten to 1D Binary Search (Optimal) ⭐
* **Mô tả**: Tìm kiếm nhị phân trên không gian chỉ số từ `0` đến `m * n - 1`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(\log(m \cdot n))$ do chia đôi không gian tìm kiếm $m \times n$ phần tử.
  * **Space Complexity**: $O(1)$ không dùng thêm cấu trúc dữ liệu nào.

### 🔄 Dry Run với ví dụ
* **Input**: `matrix = [[1, 3, 5, 7], [10, 11, 16, 20], [23, 30, 34, 60]]`, `target = 3`
* **Kích thước**: `m = 3`, `n = 4`. Mảng 1D ảo có độ dài `12` (chỉ số 0 đến 11).
* **Khởi tạo**: `lo = 0`, `hi = 11`.
* **Vòng lặp**:
  1. `mid = 5`. `row = 5 / 4 = 1`, `col = 5 % 4 = 1`. `matrix[1][1] = 11`.
     * Do `11 > 3` → `hi = mid - 1 = 4`.
  2. `mid = 2`. `row = 2 / 4 = 0`, `col = 2 % 4 = 2`. `matrix[0][2] = 5`.
     * Do `5 > 3` → `hi = mid - 1 = 1`.
  3. `mid = 0 + (1 - 0) / 2 = 0`. `row = 0`, `col = 0`. `matrix[0][0] = 1`.
     * Do `1 < 3` → `lo = mid + 1 = 1`.
  4. `mid = 1 + (1 - 1) / 2 = 1`. `row = 0`, `col = 1`. `matrix[0][1] = 3 == 3` → Trả về `true`.
* **Kết quả**: `true`

### 💻 Java Clean Code
```java
public boolean searchMatrix(int[][] matrix, int target) {
    if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
        return false;
    }
    int m = matrix.length;
    int n = matrix[0].length;
    int lo = 0, hi = m * n - 1;
    
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int row = mid / n;
        int col = mid % n;
        int val = matrix[row][col];
        
        if (val == target) {
            return true;
        } else if (val < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return false;
}
```

---

## 24. Koko Eating Bananas (LeetCode #875) - Medium

### 💡 Ý tưởng cốt lõi
Koko cần ăn hết tất cả chuối trong các đống `piles` trong vòng `h` giờ. Hãy tìm tốc độ ăn tối thiểu `k` (quả/giờ) để ăn hết chuối trong thời gian quy định.
Tốc độ ăn `k` tối thiểu là `1` quả/giờ, và tối đa là giá trị lớn nhất của một đống chuối trong `piles` (để ăn xong 1 đống trong 1 giờ).
Do hàm thời gian ăn chuối theo tốc độ `k` là hàm đơn điệu giảm (tốc độ càng nhanh thì số giờ cần dùng càng ít), ta có thể **tìm kiếm nhị phân trên không gian đáp án** `k` từ `1` đến `max(piles)`.
Với mỗi tốc độ `mid`, ta tính tổng số giờ cần thiết. Nếu có thể ăn hết trong `h` giờ, ta lưu kết quả và thử tìm tốc độ nhỏ hơn (`hi = mid`). Ngược lại, ta phải tăng tốc độ ăn lên (`lo = mid + 1`).

### 📊 Hướng tiếp cận tối ưu

#### Binary Search on Answer (Optimal) ⭐
* **Mô tả**: Định nghĩa hàm `canEatAll(piles, k, h)` để kiểm tra tính khả thi của tốc độ `k`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log(\max(\text{piles})))$ với $n$ là số đống chuối. Không gian tìm kiếm là $\max(\text{piles})$, mỗi lần kiểm tra mất $O(n)$ thời gian.
  * **Space Complexity**: $O(1)$.

### 🔄 Dry Run với ví dụ
* **Input**: `piles = [3, 6, 7, 11]`, `h = 8`
* **Không gian tìm kiếm**: `lo = 1`, `hi = 11`.
* **Vòng lặp**:
  - `mid = 6`. Tính giờ: `ceil(3/6) + ceil(6/6) + ceil(7/6) + ceil(11/6) = 1 + 1 + 2 + 2 = 6` giờ.
    * Do `6 <= 8` → Có thể ăn hết. Thử tốc độ nhỏ hơn: `hi = 6`.
  - `mid = 3`. Tính giờ: `ceil(3/3) + ceil(6/3) + ceil(7/3) + ceil(11/3) = 1 + 2 + 3 + 4 = 10` giờ.
    * Do `10 > 8` → Không kịp ăn hết. Phải tăng tốc độ: `lo = 4`.
  - ... tiếp tục tìm kiếm.
* **Kết quả**: `4`

### 💻 Java Clean Code
```java
public int minEatingSpeed(int[] piles, int h) {
    int lo = 1;
    int hi = 0;
    for (int pile : piles) {
        hi = Math.max(hi, pile);
    }
    
    int result = hi;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (canEatAll(piles, mid, h)) {
            result = mid;
            hi = mid - 1; // Thử tốc độ nhỏ hơn
        } else {
            lo = mid + 1; // Cần tốc độ lớn hơn
        }
    }
    return result;
}

private boolean canEatAll(int[] piles, int speed, int h) {
    long hours = 0;
    for (int pile : piles) {
        // Tránh lỗi chia số nguyên bằng cách làm tròn lên
        hours += (pile + speed - 1) / speed;
    }
    return hours <= h;
}
```

---

## 25. Find Minimum in Rotated Sorted Array (LeetCode #153) - Medium

### 💡 Ý tưởng cốt lõi
Tìm phần tử nhỏ nhất trong một mảng đã được sắp xếp tăng dần nhưng bị xoay (rotated) một số lần không xác định.
Khi một mảng được sắp xếp bị xoay, nó sẽ được chia thành hai nửa: một nửa luôn tăng dần ổn định và một nửa chứa điểm gãy (pivot - phần tử nhỏ nhất).
Ta dùng Binary Search. Ở mỗi bước, so sánh `nums[mid]` với `nums[hi]`:
* Nếu `nums[mid] > nums[hi]`, có nghĩa là phần tử nhỏ nhất nằm ở nửa bên phải (từ `mid + 1` đến `hi`). Ta gán `lo = mid + 1`.
* Nếu `nums[mid] <= nums[hi]`, có nghĩa là nửa bên phải từ `mid` đến `hi` đã được sắp xếp tăng dần, do đó giá trị nhỏ nhất chỉ có thể nằm từ `lo` đến `mid`. Ta gán `hi = mid`.
Khi kết thúc vòng lặp `lo == hi`, ta thu được phần tử nhỏ nhất.

### 📊 Hướng tiếp cận tối ưu

#### Modified Binary Search (Optimal) ⭐
* **Mô tả**: Thu hẹp dần khoảng tìm kiếm bằng cách so sánh phần tử giữa với phần tử biên phải.
* **Độ phức tạp**:
  * **Time Complexity**: $O(\log n)$.
  * **Space Complexity**: $O(1)$.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [3, 4, 5, 1, 2]`
* **Khởi tạo**: `lo = 0`, `hi = 4`.
* **Vòng lặp**:
  1. `mid = 2`. `nums[2] = 5 > nums[4]` (2) → Phần tử nhỏ nhất ở bên phải: `lo = 3`.
  2. `mid = 3 + (4 - 3) / 2 = 3`. `nums[3] = 1 <= nums[4]` (2) → Phần tử nhỏ nhất ở bên trái (kèm mid): `hi = 3`.
  3. Vòng lặp `lo < hi` dừng lại vì `lo = 3 == hi`.
* **Kết quả**: `nums[3] = 1`

### 💻 Java Clean Code
```java
public int findMin(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) {
            lo = mid + 1; // Min nằm bên phải mid
        } else {
            hi = mid; // Min nằm bên trái bao gồm cả mid
        }
    }
    return nums[lo];
}
```

---

## 26. Search in Rotated Sorted Array (LeetCode #33) - Medium

### 💡 Ý tưởng cốt lõi
Tìm vị trí của `target` trong một mảng đã sắp xếp tăng dần bị xoay. Yêu cầu độ phức tạp thời gian $O(\log n)$.
Mặc dù mảng bị xoay, khi ta chọn một vị trí `mid`, **ít nhất một trong hai nửa** (trái `[lo..mid]` hoặc phải `[mid..hi]`) luôn được sắp xếp bình thường.
1. Xác định nửa nào được sắp xếp bằng cách so sánh `nums[lo] <= nums[mid]`.
2. Kiểm tra xem `target` có nằm trong khoảng của nửa được sắp xếp đó không:
   * Nếu có, thu hẹp khoảng tìm kiếm vào nửa đó.
   * Nếu không, tìm kiếm ở nửa còn lại.

### 📊 Hướng tiếp cận tối ưu

#### Split and Search Binary Search (Optimal) ⭐
* **Mô tả**: Dùng Binary Search kiểm tra tính chất được sắp xếp của các nửa mảng trước khi quyết định dịch chuyển con trỏ.
* **Độ phức tạp**:
  * **Time Complexity**: $O(\log n)$.
  * **Space Complexity**: $O(1)$.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [4, 5, 6, 7, 0, 1, 2]`, `target = 0`
* **Khởi tạo**: `lo = 0`, `hi = 6`
* **Vòng lặp**:
  1. `mid = 3`. `nums[3] = 7`.
     * So sánh `nums[0] <= nums[3]` (4 <= 7) → Nửa bên trái `[4, 5, 6, 7]` được sắp xếp.
     * Kiểm tra `target = 0` có thuộc khoảng `[nums[0], nums[3])` tức `[4, 7)` không? **Không**.
     * Tìm kiếm ở nửa bên phải: `lo = mid + 1 = 4`.
  2. `lo = 4`, `hi = 6`, `mid = 5`. `nums[5] = 1`.
     * So sánh `nums[4] <= nums[5]` (0 <= 1) → Nửa bên trái `[0, 1]` được sắp xếp.
     * Kiểm tra `target = 0` có thuộc khoảng `[nums[4], nums[5])` tức `[0, 1)` không? **Có** (0 <= 0 < 1).
     * Tìm kiếm ở nửa bên trái này: `hi = mid - 1 = 4`.
  3. `lo = 4`, `hi = 4`, `mid = 4`. `nums[4] = 0 == target` → Trả về `4`.
* **Kết quả**: `4`

### 💻 Java Clean Code
```java
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) {
            return mid;
        }
        
        // Kiểm tra xem nửa bên trái có được sắp xếp không
        if (nums[lo] <= nums[mid]) {
            // Target nằm trong nửa bên trái đã sắp xếp
            if (nums[lo] <= target && target < nums[mid]) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        } 
        // Ngược lại, nửa bên phải phải được sắp xếp
        else {
            // Target nằm trong nửa bên phải đã sắp xếp
            if (nums[mid] < target && target <= nums[hi]) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
    }
    return -1;
}
```

---

## 27. Median of Two Sorted Arrays (LeetCode #4) - Hard

### 💡 Ý tưởng cốt lõi
Tìm trung vị (median) của hai mảng đã sắp xếp `nums1` và `nums2` với tổng kích thước $m + n$ trong thời gian $O(\log(m+n))$.
Ý tưởng tối ưu là **tìm phân hoạch (partition)** của hai mảng sao cho:
1. Tổng số phần tử ở nửa bên trái của cả hai mảng bằng nửa bên phải (hoặc nhiều hơn 1 nếu tổng số phần tử lẻ).
2. Mọi phần tử ở nửa bên trái đều nhỏ hơn hoặc bằng mọi phần tử ở nửa bên phải.
Ta thực hiện Tìm kiếm nhị phân để tìm điểm cắt `partitionX` của mảng ngắn hơn. Điểm cắt `partitionY` của mảng dài hơn được tính toán trực tiếp:
$$\text{partitionY} = \frac{x + y + 1}{2} - \text{partitionX}$$
Tại mỗi bước nhị phân:
* Đọc `maxLeftX`, `minRightX` từ `nums1`, và `maxLeftY`, `minRightY` từ `nums2`.
* Nếu `maxLeftX <= minRightY` và `maxLeftY <= minRightX`, ta đã tìm thấy phân hoạch chính xác!
* Nếu `maxLeftX > minRightY`, ta đã cắt quá nhiều về bên phải của `nums1`, cần dịch chuyển sang trái (`hi = partitionX - 1`).
* Ngược lại, cần dịch chuyển sang phải (`lo = partitionX + 1`).

### 📊 Hướng tiếp cận tối ưu

#### Binary Search on Shorter Array (Optimal) ⭐
* **Mô tả**: Thực hiện tìm kiếm nhị phân trên mảng có kích thước nhỏ hơn để giảm thiểu thời gian tìm kiếm.
* **Độ phức tạp**:
  * **Time Complexity**: $O(\log(\min(m, n)))$ với $m, n$ là độ dài của hai mảng.
  * **Space Complexity**: $O(1)$.

### 🔄 Dry Run với ví dụ
* **Input**: `nums1 = [1, 3]`, `nums2 = [2]`
* **Hoán đổi để nums1 là mảng ngắn hơn**: `nums1 = [2]` (độ dài 1), `nums2 = [1, 3]` (độ dài 2).
* **Khởi tạo**: `x = 1`, `y = 2`, `lo = 0`, `hi = 1`.
* **Vòng lặp**:
  - `partitionX = 0`. `partitionY = (1 + 2 + 1)/2 - 0 = 2`.
  - `maxLeftX = -INF` (do partitionX = 0), `minRightX = nums1[0] = 2`.
  - `maxLeftY = nums2[1] = 3`, `minRightY = INF` (do partitionY = 2).
  - Kiểm tra điều kiện: `maxLeftX <= minRightY` (-INF <= INF - Đúng) nhưng `maxLeftY <= minRightX` (3 <= 2 - Sai).
  - Do `maxLeftY > minRightX` (3 > 2), ta cần dịch `lo = partitionX + 1 = 1`.
  - Ở bước tiếp theo: `partitionX = 1`, `partitionY = 1`.
  - `maxLeftX = 2`, `minRightX = INF`.
  - `maxLeftY = 1`, `minRightY = 3`.
  - Kiểm tra điều kiện: `2 <= 3` (Đúng) và `1 <= INF` (Đúng) → Đã tìm thấy điểm cắt chính xác.
  - Tổng số phần tử lẻ (1+2=3) → Trung vị là `max(maxLeftX, maxLeftY) = max(2, 1) = 2`.
* **Kết quả**: `2.0`

### 💻 Java Clean Code
```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    // Đảm bảo nums1 là mảng có độ dài nhỏ hơn
    if (nums1.length > nums2.length) {
        return findMedianSortedArrays(nums2, nums1);
    }
    
    int x = nums1.length;
    int y = nums2.length;
    int lo = 0;
    int hi = x;
    
    while (lo <= hi) {
        int partitionX = lo + (hi - lo) / 2;
        int partitionY = (x + y + 1) / 2 - partitionX;
        
        int maxLeftX = (partitionX == 0) ? Integer.MIN_VALUE : nums1[partitionX - 1];
        int minRightX = (partitionX == x) ? Integer.MAX_VALUE : nums1[partitionX];
        
        int maxLeftY = (partitionY == 0) ? Integer.MIN_VALUE : nums2[partitionY - 1];
        int minRightY = (partitionY == y) ? Integer.MAX_VALUE : nums2[partitionY];
        
        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
            // Tổng số phần tử chẵn
            if ((x + y) % 2 == 0) {
                return ((double) Math.max(maxLeftX, maxLeftY) + Math.min(minRightX, minRightY)) / 2;
            } 
            // Tổng số phần tử lẻ
            else {
                return Math.max(maxLeftX, maxLeftY);
            }
        } else if (maxLeftX > minRightY) {
            hi = partitionX - 1; // Dịch sang trái
        } else {
            lo = partitionX + 1; // Dịch sang phải
        }
    }
    
    throw new IllegalArgumentException("Input arrays are not sorted.");
}
```
