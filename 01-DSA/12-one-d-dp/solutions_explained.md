# 12 - 1D Dynamic Programming: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 9 bài toán thuộc chủ đề **1D Dynamic Programming (Quy hoạch động 1 chiều)** từ LeetCode Master Tracker.

---

## 62. Climbing Stairs (LeetCode #70) - Easy

### 💡 Ý tưởng cốt lõi
Cần $n$ bước để lên đỉnh cầu thang. Mỗi lần bạn có thể bước 1 hoặc 2 bước. Có bao nhiêu cách khác nhau để lên đỉnh?
Để lên đến bậc thứ $i$, ta chỉ có thể đi từ bậc $i-1$ (bước lên 1 bước) hoặc từ bậc $i-2$ (bước lên 2 bước). Do đó, số cách lên bậc $i$ bằng tổng số cách lên bậc $i-1$ và bậc $i-2$:
$$dp[i] = dp[i-1] + dp[i-2]$$
Đây chính là dãy số Fibonacci. Ta chỉ cần lưu trữ hai trạng thái trước đó để tối ưu bộ nhớ về $O(1)$.

### 📊 Hướng tiếp cận tối ưu

#### Space Optimized DP (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do lặp tuyến tính đến $n$.
  * **Space Complexity**: $O(1)$ chỉ sử dụng hai biến ghi nhớ.

### 💻 Java Clean Code
```java
public int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1; // dp[1]
    int prev1 = 2; // dp[2]
    for (int i = 3; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

---

## 63. House Robber (LeetCode #198) - Medium

### 💡 Ý tưởng cốt lõi
Một tên trộm muốn trộm tiền từ các ngôi nhà nằm trên một con đường thẳng. Không thể trộm hai ngôi nhà kề nhau vì hệ thống báo động sẽ kích hoạt. Hãy tìm số tiền tối đa có thể trộm.
Với mỗi ngôi nhà $i$ có số tiền là `nums[i]`, tên trộm có 2 lựa chọn:
1. **Lựa chọn 1: Trộm nhà $i$**: Khi đó không được trộm nhà $i-1$. Tổng tiền bằng số tiền nhà $i$ cộng với số tiền tối đa trộm được tính đến nhà $i-2$.
2. **Lựa chọn 2: Bỏ qua nhà $i$**: Tổng tiền bằng số tiền tối đa trộm được tính đến nhà $i-1$.
Công thức quy hoạch động:
$$dp[i] = \max(dp[i-1], nums[i] + dp[i-2])$$
Ta có thể tối ưu không gian về $O(1)$ bằng cách duy trì hai giá trị tiền tối đa của hai bước trước đó.

### 📊 Hướng tiếp cận tối ưu

#### Space Optimized DP (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$ chỉ lưu hai biến `prev1` và `prev2`.

### 💻 Java Clean Code
```java
public int rob(int[] nums) {
    if (nums == null || nums.length == 0) return 0;
    int prev2 = 0; // Số tiền lớn nhất khi lùi 2 nhà
    int prev1 = 0; // Số tiền lớn nhất khi lùi 1 nhà
    for (int num : nums) {
        int curr = Math.max(prev1, num + prev2);
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

---

## 64. House Robber II (LeetCode #213) - Medium

### 💡 Ý tưởng cốt lõi
Tương tự như bài House Robber I, nhưng tất cả các ngôi nhà được sắp xếp theo **một vòng tròn**. Có nghĩa là ngôi nhà đầu tiên và ngôi nhà cuối cùng là kề nhau.
Vì nhà đầu tiên và nhà cuối cùng kề nhau, ta không thể trộm cả hai cùng lúc.
Ý tưởng tối ưu là chia bài toán thành hai trường hợp nhỏ:
* **Trường hợp 1**: Trộm từ nhà thứ nhất đến nhà kề cuối (`nums[0]` đến `nums[n-2]`).
* **Trường hợp 2**: Trộm từ nhà thứ hai đến nhà cuối cùng (`nums[1]` đến `nums[n-1]`).
Sau đó ta áp dụng thuật toán House Robber I cho hai trường hợp này và lấy giá trị lớn hơn.

### 📊 Hướng tiếp cận tối ưu

#### Two-pass DP (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$.
  * **Space Complexity**: $O(1)$.

### 💻 Java Clean Code
```java
public int rob(int[] nums) {
    if (nums == null || nums.length == 0) return 0;
    if (nums.length == 1) return nums[0];
    
    return Math.max(robHelper(nums, 0, nums.length - 2), 
                    robHelper(nums, 1, nums.length - 1));
}

private int robHelper(int[] nums, int start, int end) {
    int prev2 = 0, prev1 = 0;
    for (int i = start; i <= end; i++) {
        int curr = Math.max(prev1, nums[i] + prev2);
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

---

## 65. Longest Palindromic Substring (LeetCode #5) - Medium

### 💡 Ý tưởng cốt lõi
Tìm chuỗi con là Palindrome (chuỗi đối xứng) dài nhất trong chuỗi `s`.
Thay vì dùng ma trận quy hoạch động 2D $O(n^2)$ space, ta có thể dùng phương pháp **Expand Around Center (Mở rộng từ tâm)** với $O(1)$ space.
Một chuỗi Palindrome có thể đối xứng qua:
* Một tâm ký tự đơn (độ dài lẻ), ví dụ `"aba"` tâm là `'b'`.
* Một khoảng giữa hai ký tự (độ dài chẵn), ví dụ `"abba"` tâm là khoảng giữa hai chữ `'b'`.
Có tổng cộng $2n - 1$ tâm tiềm năng trong chuỗi. Với mỗi vị trí, ta thực hiện mở rộng về hai phía sang trái và phải cho đến khi không còn đối xứng, từ đó ghi nhận độ dài lớn nhất.

### 📊 Hướng tiếp cận tối ưu

#### Expand Around Center (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2)$ do mở rộng từ mỗi tâm tốn tối đa $O(n)$.
  * **Space Complexity**: $O(1)$ chỉ sử dụng chỉ số cắt chuỗi.

### 💻 Java Clean Code
```java
public String longestPalindrome(String s) {
    if (s == null || s.length() < 1) return "";
    int start = 0, end = 0;
    
    for (int i = 0; i < s.length(); i++) {
        int len1 = expandAroundCenter(s, i, i);     // Tâm lẻ (độ dài 1 ký tự)
        int len2 = expandAroundCenter(s, i, i + 1); // Tâm chẵn (độ dài 2 ký tự)
        int len = Math.max(len1, len2);
        
        if (len > end - start) {
            start = i - (len - 1) / 2;
            end = i + len / 2;
        }
    }
    return s.substring(start, end + 1);
}

private int expandAroundCenter(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--;
        right++;
    }
    return right - left - 1; // Trả về độ dài chuỗi con Palindrome hợp lệ
}
```

---

## 66. Coin Change (LeetCode #322) - Medium

### 💡 Ý tưởng cốt lõi
Tìm số lượng đồng xu ít nhất để có tổng mệnh giá bằng `amount`. Nếu không thể đổi được, trả về `-1`.
Định nghĩa trạng thái: `dp[i]` là số lượng đồng xu ít nhất cần dùng để tạo ra mệnh giá `i`.
Với mỗi mệnh giá `i` từ `1` đến `amount`, số xu ít nhất sẽ bằng:
$$dp[i] = \min_{coin \in coins}(dp[i - coin] + 1)$$
với điều kiện `i - coin >= 0` và `dp[i - coin]` có lời giải hợp lệ (khác vô cùng).

### 📊 Hướng tiếp cận tối ưu

#### Bottom-up DP (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(amount \cdot n)$ với $n$ là số mệnh giá xu.
  * **Space Complexity**: $O(amount)$ cho mảng quy hoạch động `dp`.

### 🔄 Dry Run với ví dụ
* **Input**: `coins = [1, 2, 5]`, `amount = 11`
* **Khởi tạo**: `dp` kích thước 12. `dp[0] = 0`, các phần tử khác gán `amount + 1` (đại diện vô cực).
* **Xử lý**:
  - `i = 1`: `dp[1] = min(dp[1-1]+1) = dp[0]+1 = 1`.
  - `i = 2`: `dp[2] = min(dp[2-1]+1, dp[2-2]+1) = min(dp[1]+1, dp[0]+1) = 1`.
  - ... tính tiếp.
  - `i = 11`: `dp[11] = min(dp[11-1]+1, dp[11-2]+1, dp[11-5]+1) = min(dp[10]+1, dp[9]+1, dp[6]+1) = 3`.
* **Kết quả**: `3` (tổ hợp 5 + 5 + 1).

### 💻 Java Clean Code
```java
public int coinChange(int[] coins, int amount) {
    int max = amount + 1;
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, max); // Gán giá trị mặc định vô cực
    dp[0] = 0;
    
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (i - coin >= 0) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    
    return dp[amount] > amount ? -1 : dp[amount];
}
```

---

## 67. Maximum Product Subarray (LeetCode #152) - Medium

### 💡 Ý tưởng cốt lõi
Tìm tích lớn nhất của một chuỗi con liên tục trong mảng số nguyên `nums`.
Khác với tổng lớn nhất (Kadane's algorithm), khi nhân các số nguyên, một số âm nhân với một số âm sẽ ra một số dương rất lớn.
Do đó, tại mỗi vị trí `i`, ta cần duy trì cả **Tích lớn nhất** (`maxProduct`) và **Tích nhỏ nhất** (`minProduct`) tính đến phần tử kề trước nó.
Khi duyệt đến `nums[i]`:
* Nếu `nums[i]` là số âm, ta hoán đổi `maxProduct` và `minProduct`.
* Cập nhật:
  $$\text{maxProduct} = \max(\text{nums}[i], \text{nums}[i] \times \text{maxProduct})$$
  $$\text{minProduct} = \min(\text{nums}[i], \text{nums}[i] \times \text{minProduct})$$
* Cập nhật kết quả cực đại toàn cục `result`.

### 📊 Hướng tiếp cận tối ưu

#### Dynamic Programming with Min/Max Tracking (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$ chỉ sử dụng vài biến tích lũy.

### 💻 Java Clean Code
```java
public int maxProduct(int[] nums) {
    if (nums == null || nums.length == 0) return 0;
    
    int maxSoFar = nums[0];
    int minSoFar = nums[0];
    int result = maxSoFar;
    
    for (int i = 1; i < nums.length; i++) {
        int curr = nums[i];
        
        // Nếu số hiện tại âm, hoán đổi min/max
        if (curr < 0) {
            int temp = maxSoFar;
            maxSoFar = minSoFar;
            minSoFar = temp;
        }
        
        maxSoFar = Math.max(curr, maxSoFar * curr);
        minSoFar = Math.min(curr, minSoFar * curr);
        
        result = Math.max(result, maxSoFar);
    }
    
    return result;
}
```

---

## 68. Word Break (LeetCode #139) - Medium

### 💡 Ý tưởng cốt lõi
Xác định xem chuỗi `s` có thể được phân tách thành một chuỗi các từ trong từ điển `wordDict` hay không.
Định nghĩa trạng thái: `dp[i]` là boolean, cho biết chuỗi con từ đầu đến index `i` (`s.substring(0, i)`) có thể phân tách hợp lệ không.
Để tính `dp[i]`, ta tìm một điểm cắt `j` ($0 \le j < i$) sao cho:
* Phần đầu `s.substring(0, j)` phân tách hợp lệ (`dp[j] == true`).
* Phần đuôi `s.substring(j, i)` là một từ nằm trong từ điển `wordDict`.
Nếu tìm thấy điểm `j` thỏa mãn, ta gán `dp[i] = true` và dừng kiểm tra cho vị trí `i`.

### 📊 Hướng tiếp cận tối ưu

#### Bottom-up DP with HashSet (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2 \cdot l)$ với $n$ là độ dài chuỗi $s$ và $l$ là thời gian tạo chuỗi con. Có thể tối ưu hơn nếu giới hạn độ dài của các từ trong từ điển.
  * **Space Complexity**: $O(n + m)$ với $m$ là số từ trong từ điển (lưu trong HashSet).

### 💻 Java Clean Code
```java
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict); // Để tra cứu trong O(1)
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true; // Chuỗi rỗng luôn phân tách hợp lệ
    
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && wordSet.contains(s.substring(j, i))) {
                dp[i] = true;
                break; // Tìm thấy 1 cách phân tách hợp lệ là đủ
            }
        }
    }
    
    return dp[s.length()];
}
```

---

## 69. Longest Increasing Subsequence (LeetCode #300) - Medium

### 💡 Ý tưởng cốt lõi
Tìm độ dài của dãy con tăng dài nhất (LIS) trong một mảng số nguyên `nums`.
Có hai cách tiếp cận: Quy hoạch động thông thường $O(n^2)$ và Thuật toán tối ưu kết hợp **Tìm kiếm nhị phân** đạt $O(n \log n)$.
Ý tưởng $O(n \log n)$: Ta duy trì một mảng động `sub` lưu dãy con tăng dần ảo.
* Duyệt qua từng số `num`.
* Sử dụng Binary Search để tìm vị trí thích hợp của `num` trong mảng `sub` (vị trí phần tử đầu tiên $\ge num$).
* Nếu vị trí tìm được bằng độ dài hiện tại của `sub`, ta thêm `num` vào cuối `sub` (dãy con tăng dài ra).
* Ngược lại, ta thay thế phần tử cũ tại vị trí đó bằng `num` (giúp hạ thấp các phần tử để mở rộng cơ hội cho các số sau).
* Lưu ý: Mảng `sub` không chứa chính xác dãy con tăng thực tế, nhưng độ dài của nó luôn bằng độ dài của LIS.

### 📊 Các hướng tiếp cận

#### Cách 1: Quy hoạch động thông thường
* **Mô tả**: `dp[i]` là độ dài LIS kết thúc tại `i`. `dp[i] = 1 + max(dp[j])` với mọi `j < i` và `nums[j] < nums[i]`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2)$.
  * **Space Complexity**: $O(n)$.

#### Cách 2: Binary Search / Patience Sorting (Optimal) ⭐
* **Mô tả**: Sử dụng tìm kiếm nhị phân để chèn phần tử vào vị trí tối ưu trong mảng phụ.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ cực nhanh.
  * **Space Complexity**: $O(n)$ để lưu mảng phụ.

### 💻 Java Clean Code
```java
public int lengthOfLIS(int[] nums) {
    List<Integer> sub = new ArrayList<>();
    
    for (int num : nums) {
        int idx = binarySearch(sub, num);
        if (idx == sub.size()) {
            sub.add(num); // Thêm vào cuối nếu num lớn hơn tất cả
        } else {
            sub.set(idx, num); // Thay thế để hạ giá trị tối ưu cho các số sau
        }
    }
    
    return sub.size();
}

private int binarySearch(List<Integer> sub, int target) {
    int lo = 0, hi = sub.size() - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (sub.get(mid) == target) {
            return mid;
        } else if (sub.get(mid) < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return lo; // Trả về vị trí phần tử đầu tiên >= target
}
```

---

## 70. Decode Ways (LeetCode #91) - Medium

### 💡 Ý tưởng cốt lõi
Một thông điệp chứa các chữ cái từ A-Z được mã hóa thành các số sử dụng ánh xạ: `'A' -> "1"`, `'B' -> "2"`,..., `'Z' -> "26"`. Hãy tìm tổng số cách để giải mã chuỗi số này.
Đây là bài toán biến thể của Fibonacci. Định nghĩa trạng thái: `dp[i]` là số cách giải mã chuỗi con độ dài `i` (`s.substring(0, i)`).
Với mỗi vị trí `i`, ta có tối đa hai cách giải mã:
1. **Giải mã 1 ký tự**: Nếu ký tự tại `i-1` khác `'0'` (từ `'1'` đến `'9'`), số cách giải mã được cộng thêm số cách giải mã của chuỗi con trước đó: `dp[i] += dp[i-1]`.
2. **Giải mã 2 ký tự**: Nếu hai ký tự `s.substring(i-2, i)` tạo thành một số hợp lệ từ `10` đến `26`, số cách giải mã được cộng thêm: `dp[i] += dp[i-2]`.

### 📊 Hướng tiếp cận tối ưu

#### Space Optimized DP (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ với $n$ là độ dài chuỗi.
  * **Space Complexity**: $O(1)$ chỉ lưu hai biến `prev1` và `prev2`.

### 💻 Java Clean Code
```java
public int numDecodings(String s) {
    if (s == null || s.length() == 0 || s.charAt(0) == '0') {
        return 0;
    }
    
    int n = s.length();
    int prev2 = 1; // dp[i-2]
    int prev1 = 1; // dp[i-1]
    
    for (int i = 2; i <= n; i++) {
        int curr = 0;
        
        // 1. Kiểm tra giải mã 1 chữ số
        int oneDigit = s.charAt(i - 1) - '0';
        if (oneDigit >= 1 && oneDigit <= 9) {
            curr += prev1;
        }
        
        // 2. Kiểm tra giải mã 2 chữ số
        int twoDigits = Integer.parseInt(s.substring(i - 2, i));
        if (twoDigits >= 10 && twoDigits <= 26) {
            curr += prev2;
        }
        
        prev2 = prev1;
        prev1 = curr;
    }
    
    return prev1;
}
```
