# 02 - Two Pointers: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 5 bài toán thuộc chủ đề **Two Pointers** từ LeetCode Master Tracker.

---

## 9. Valid Palindrome (LeetCode #125) - Easy

### 💡 Ý tưởng cốt lõi
Kiểm tra xem một chuỗi có phải là Palindrome (chuỗi đối xứng) hay không, sau khi đã chuyển tất cả ký tự viết hoa thành viết thường và loại bỏ tất cả các ký tự không phải là chữ cái hoặc số.
Giải pháp tối ưu là sử dụng **hai con trỏ** di chuyển từ hai đầu của chuỗi hướng vào giữa. Ở mỗi bước, bỏ qua các ký tự không phải là chữ cái/số, so sánh hai ký tự hiện tại (sau khi chuyển thành chữ thường). Nếu tại bất kỳ điểm nào chúng khác nhau, chuỗi không phải là Palindrome.

### 📊 Hướng tiếp cận tối ưu

#### Two Pointers from Ends (Optimal) ⭐
* **Mô tả**:
  1. Đặt `left = 0` và `right = s.length() - 1`.
  2. Vòng lặp `while (left < right)`:
     * Dịch con trỏ `left` sang phải nếu `s.charAt(left)` không phải chữ cái/số.
     * Dịch con trỏ `right` sang trái nếu `s.charAt(right)` không phải chữ cái/số.
     * Khi cả hai con trỏ đều trỏ tới ký tự hợp lệ, so sánh chúng (không phân biệt hoa thường). Nếu khác nhau, trả về `false`.
     * Tăng `left` và giảm `right`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt qua chuỗi tối đa 1 lần.
  * **Space Complexity**: $O(1)$ hoạt động trực tiếp trên chuỗi gốc, không tạo chuỗi mới.

### 🔄 Dry Run với ví dụ
* **Input**: `s = "A man, a plan, a canal: Panama"`
* **Khởi tạo**: `left = 0`, `right = 29`
* **Vòng lặp**:
  - `s[0] = 'A'` (hợp lệ), `s[29] = 'a'` (hợp lệ). So sánh `'a'` vs `'a'` → Giống nhau. `left = 1`, `right = 28`.
  - ... (bỏ qua khoảng trắng, dấu phẩy, dấu hai chấm).
  - Trận so sánh cuối cùng diễn ra tại vị trí giữa chuỗi.
* **Kết quả**: `true`

### 💻 Java Clean Code
```java
public boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
            left++;
        }
        while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
            right--;
        }
        if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

---

## 10. Two Sum II - Input Array Is Sorted (LeetCode #167) - Medium

### 💡 Ý tưởng cốt lõi
Tìm hai số trong một mảng số nguyên đã sắp xếp theo thứ tự tăng dần sao cho tổng của chúng bằng một giá trị `target` cho trước.
Vì mảng đã được **sắp xếp**, ta có thể sử dụng kỹ thuật **hai con trỏ** đặt ở hai đầu mảng (`left = 0`, `right = n-1`).
* Nếu `nums[left] + nums[right] == target`, ta tìm thấy đáp án.
* Nếu tổng nhỏ hơn `target`, ta cần tăng tổng lên bằng cách dịch con trỏ `left` sang phải (`left++`).
* Nếu tổng lớn hơn `target`, ta cần giảm tổng xuống bằng cách dịch con trỏ `right` sang trái (`right--`).

### 📊 Hướng tiếp cận tối ưu

#### Two Pointers (Optimal) ⭐
* **Mô tả**: Dùng hai con trỏ di chuyển ngược chiều trên mảng đã sắp xếp.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do mỗi phần tử được xét tối đa một lần.
  * **Space Complexity**: $O(1)$ chỉ sử dụng hai biến con trỏ.

### 🔄 Dry Run với ví dụ
* **Input**: `numbers = [2, 7, 11, 15]`, `target = 9`
* **Khởi tạo**: `left = 0` (giá trị 2), `right = 3` (giá trị 15).
* **Vòng lặp**:
  1. `sum = 2 + 15 = 17 > 9` → `right--` (right thành 2).
  2. `sum = 2 + 11 = 13 > 9` → `right--` (right thành 1).
  3. `sum = 2 + 7 = 9 == 9` → Đạt kết quả.
* **Kết quả**: indices `[1, 2]` (1-indexed theo yêu cầu đề bài: return `[left + 1, right + 1]`).

### 💻 Java Clean Code
```java
public int[] twoSum(int[] numbers, int target) {
    int left = 0, right = numbers.length - 1;
    while (left < right) {
        int sum = numbers[left] + numbers[right];
        if (sum == target) {
            return new int[]{left + 1, right + 1}; // 1-indexed
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return new int[]{-1, -1};
}
```

---

## 11. 3Sum (LeetCode #15) - Medium

### 💡 Ý tưởng cốt lõi
Tìm tất cả các bộ ba số duy nhất trong mảng sao cho tổng của chúng bằng 0.
Thuật toán tối ưu là **Sắp xếp mảng** trước, sau đó duyệt qua từng số `nums[i]`. Với mỗi `nums[i]`, bài toán trở thành tìm cặp số `(nums[left], nums[right])` trong phần còn lại của mảng sao cho tổng của chúng bằng `-nums[i]`. Chúng ta áp dụng kỹ thuật Two Pointers cho phần mảng còn lại này.
Để tránh các bộ ba trùng lặp, ta cần bỏ qua các số giống nhau khi dịch chuyển `i`, `left`, và `right`.

### 📊 Hướng tiếp cận tối ưu

#### Sort + Fix One + Two Pointers (Optimal) ⭐
* **Mô tả**:
  1. Sắp xếp mảng tăng dần.
  2. Vòng lặp `i` chạy từ `0` đến `n-3`. Bỏ qua nếu `nums[i] == nums[i-1]`.
  3. Đặt `left = i + 1`, `right = n - 1`. Chạy Two Pointers tìm `nums[left] + nums[right] == -nums[i]`.
  4. Mỗi lần tìm thấy, thêm bộ ba vào kết quả và dịch `left`, `right` qua các phần tử trùng lặp.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2)$ do sắp xếp mất $O(n \log n)$ và vòng lặp ngoài kết hợp Two Pointers bên trong mất $O(n^2)$.
  * **Space Complexity**: $O(1)$ hoặc $O(n)$ tùy thuộc thuật toán sắp xếp (in-place hay không).

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [-1, 0, 1, 2, -1, -4]`
* **Sau sort**: `nums = [-4, -1, -1, 0, 1, 2]`
* **Xử lý**:
  - `i = 0` (`nums[0] = -4`): `target = 4`. Two pointers tìm cặp tổng = 4 trong `[-1, -1, 0, 1, 2]`. Không tìm thấy.
  - `i = 1` (`nums[1] = -1`): `target = 1`. Two pointers tìm trong `[-1, 0, 1, 2]`.
    * `left = 2` (`-1`), `right = 5` (`2`). `sum = -1 + 2 = 1 == 1` → Tìm thấy bộ `[-1, -1, 2]`. Dịch con trỏ qua trùng lặp.
    * `left = 3` (`0`), `right = 4` (`1`). `sum = 0 + 1 = 1 == 1` → Tìm thấy bộ `[-1, 0, 1]`.
  - `i = 2` (`nums[2] = -1`): Trùng với `nums[1]`, bỏ qua.
* **Kết quả**: `[[-1, -1, 2], [-1, 0, 1]]`

### 💻 Java Clean Code
```java
public List<List<Integer>> threeSum(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    Arrays.sort(nums);
    
    for (int i = 0; i < nums.length - 2; i++) {
        // Tránh trùng lặp cho phần tử thứ nhất
        if (i > 0 && nums[i] == nums[i - 1]) {
            continue;
        }
        
        int left = i + 1;
        int right = nums.length - 1;
        
        while (left < right) {
            int sum = nums[i] + nums[left] + nums[right];
            if (sum == 0) {
                res.add(Arrays.asList(nums[i], nums[left], nums[right]));
                left++;
                right--;
                // Tránh trùng lặp cho phần tử thứ hai và thứ ba
                while (left < right && nums[left] == nums[left - 1]) left++;
                while (left < right && nums[right] == nums[right + 1]) right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }
    return res;
}
```

---

## 12. Container With Most Water (LeetCode #11) - Medium

### 💡 Ý tưởng cốt lõi
Tìm hai đường thẳng sao cho chúng cùng với trục hoành tạo thành một bình chứa nước có thể tích chứa lớn nhất.
Diện tích chứa nước được tính bởi:
$$\text{Area} = \min(\text{height}[left], \text{height}[right]) \times (right - left)$$
Ta sử dụng **hai con trỏ** đặt ở hai đầu mảng `left = 0` và `right = n - 1`. Ở mỗi bước, ta tính diện tích hiện tại và cập nhật diện tích cực đại. Sau đó, ta dịch chuyển con trỏ có chiều cao **nhỏ hơn**, bởi vì chiều cao của bình chứa bị giới hạn bởi cột thấp hơn. Di chuyển cột thấp hơn là cơ hội duy nhất để tìm được một cột cao hơn giúp tăng diện tích bất chấp khoảng cách chiều ngang đang bị thu hẹp.

### 📊 Hướng tiếp cận tối ưu

#### Two Pointers (Optimal) ⭐
* **Mô tả**: Di chuyển hai con trỏ từ hai đầu vào giữa, luôn dịch chuyển cột có chiều cao nhỏ hơn.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do chỉ duyệt mảng một lần.
  * **Space Complexity**: $O(1)$ chỉ sử dụng các biến lưu trữ diện tích và con trỏ.

### 🔄 Dry Run với ví dụ
* **Input**: `height = [1, 8, 6, 2, 5, 4, 8, 3, 7]`
* **Xử lý**:
  - `left = 0` (h = 1), `right = 8` (h = 7): `width = 8`, `area = min(1, 7) * 8 = 8`. Dịch `left` (do 1 < 7).
  - `left = 1` (h = 8), `right = 8` (h = 7): `width = 7`, `area = min(8, 7) * 7 = 49`. Dịch `right` (do 7 < 8).
  - `left = 1` (h = 8), `right = 7` (h = 3): `width = 6`, `area = min(8, 3) * 6 = 18`. Dịch `right` (do 3 < 8).
  - Lặp lại cho tới khi `left >= right`.
* **Kết quả**: `49`

### 💻 Java Clean Code
```java
public int maxArea(int[] height) {
    int left = 0, right = height.length - 1;
    int maxArea = 0;
    while (left < right) {
        int width = right - left;
        int currentHeight = Math.min(height[left], height[right]);
        maxArea = Math.max(maxArea, currentHeight * width);
        
        if (height[left] < height[right]) {
            left++;
        } else {
            right--;
        }
    }
    return maxArea;
}
```

---

## 13. Trapping Rain Water (LeetCode #42) - Hard

### 💡 Ý tưởng cốt lõi
Tính tổng lượng nước có thể tích trữ được sau một cơn mưa giữa các cột có chiều cao khác nhau.
Tại mỗi cột `i`, lượng nước được tích lũy phụ thuộc vào **cột cao nhất bên trái** (`maxLeft`) và **cột cao nhất bên phải** (`maxRight`) của nó:
$$\text{Water}[i] = \max(0, \min(\text{maxLeft}, \text{maxRight}) - \text{height}[i])$$
Kỹ thuật tối ưu là dùng **hai con trỏ** di chuyển từ hai phía (`left = 0`, `right = n-1`) và duy trì hai biến `leftMax`, `rightMax`. Chúng ta so sánh `leftMax` và `rightMax`:
* Nếu `leftMax < rightMax`, lượng nước tích lũy tại vị trí `left` bị giới hạn bởi `leftMax`. Ta tính lượng nước tại `left` và dịch `left` sang phải.
* Ngược lại, lượng nước tại `right` bị giới hạn bởi `rightMax`. Ta tính lượng nước tại `right` và dịch `right` sang trái.

### 📊 Các hướng tiếp cận

#### Cách 1: Dynamic Programming (Prefix & Suffix Arrays)
* **Mô tả**: Tạo hai mảng phụ `leftMax` và `rightMax` để lưu trước chiều cao lớn nhất bên trái và bên phải của mỗi phần tử. Duyệt qua mảng để tính lượng nước.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mảng 3 lần.
  * **Space Complexity**: $O(n)$ do dùng hai mảng phụ.

#### Cách 2: Two Pointers (Optimal) ⭐
* **Mô tả**: Thay vì dùng mảng phụ, duy trì trực tiếp `leftMax` và `rightMax` thông qua hai con trỏ di chuyển từ hai đầu mảng vào trong.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$ không tốn thêm bộ nhớ phụ.

### 🔄 Dry Run với ví dụ
* **Input**: `height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]`
* **Khởi tạo**: `left = 0`, `right = 11`, `leftMax = 0`, `rightMax = 0`, `water = 0`.
* **Xử lý**:
  - `height[0] < height[11]` (0 < 1) → cập nhật `leftMax = 0`. `water += leftMax - height[0] = 0`. `left = 1`.
  - `height[1] < height[11]` (1 < 1) → cập nhật `leftMax = 1`. `water += 1 - 1 = 0`. `left = 2`.
  - `height[2] < height[11]` (0 < 1) → cập nhật `leftMax = 1`. `water += 1 - 0 = 1`. `left = 3`.
  - Tiếp tục thực hiện tương tự.
* **Kết quả**: `6`

### 💻 Java Clean Code
```java
public int trap(int[] height) {
    if (height == null || height.length == 0) {
        return 0;
    }
    
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0;
    int trappedWater = 0;
    
    while (left < right) {
        if (height[left] < height[right]) {
            if (height[left] >= leftMax) {
                leftMax = height[left];
            } else {
                trappedWater += leftMax - height[left];
            }
            left++;
        } else {
            if (height[right] >= rightMax) {
                rightMax = height[right];
            } else {
                trappedWater += rightMax - height[right];
            }
            right--;
        }
    }
    return trappedWater;
}
```
