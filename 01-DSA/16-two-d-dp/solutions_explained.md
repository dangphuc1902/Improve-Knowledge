# 16 - 2D Dynamic Programming: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 5 bài toán thuộc chủ đề **2D Dynamic Programming (Quy hoạch động 2 chiều)** từ LeetCode Master Tracker.

---

## 71. Unique Paths (LeetCode #62) - Medium

### 💡 Ý tưởng cốt lõi
Một robot xuất phát từ góc trên bên trái `(0, 0)` của một lưới ma trận kích thước $m \times n$. Robot chỉ có thể di chuyển xuống dưới hoặc sang bên phải tại mỗi bước. Tìm tổng số đường đi duy nhất để robot đi tới góc dưới bên phải `(m-1, n-1)`.
Định nghĩa trạng thái: `dp[i][j]` là số lượng đường đi duy nhất tới ô `(i, j)`.
* Để đi tới ô `(i, j)`, robot chỉ có thể đi từ ô phía trên `(i-1, j)` đi xuống, hoặc từ ô bên trái `(i, j-1)` đi sang.
* Công thức quy hoạch động:
  $$dp[i][j] = dp[i-1][j] + dp[i][j-1]$$
* Hàng đầu tiên (`i = 0`) và cột đầu tiên (`j = 0`) chỉ có đúng 1 đường đi duy nhất (toàn đi thẳng hoặc toàn đi xuống).
* Ta có thể tối ưu không gian lưu trữ từ ma trận 2D thành mảng 1D độ dài $n$ để lưu trữ trạng thái của hàng trước đó.

### 📊 Hướng tiếp cận tối ưu

#### Space Optimized 1D Array DP (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ do duyệt qua tất cả các ô trong lưới.
  * **Space Complexity**: $O(n)$ dùng mảng 1D lưu trạng thái hàng kề trước.

### 💻 Java Clean Code
```java
public int uniquePaths(int m, int n) {
    int[] dp = new int[n];
    Arrays.fill(dp, 1); // Hàng đầu tiên toàn bộ giá trị là 1
    
    for (int r = 1; r < m; r++) {
        for (int c = 1; c < n; c++) {
            // dp[c] mới = dp[c] cũ (ô phía trên) + dp[c-1] (ô bên trái)
            dp[c] = dp[c] + dp[c - 1];
        }
    }
    
    return dp[n - 1];
}
```

---

## 72. Longest Common Subsequence (LeetCode #1143) - Medium

### 💡 Ý tưởng cốt lõi
Tìm độ dài của chuỗi con chung dài nhất (LCS) giữa hai chuỗi `text1` và `text2`. Một chuỗi con không yêu cầu các ký tự phải nằm kề nhau.
Định nghĩa trạng thái: `dp[i][j]` là độ dài LCS của hai tiền tố `text1.substring(0, i)` và `text2.substring(0, j)`.
Khi so sánh ký tự `text1.charAt(i-1)` và `text2.charAt(j-1)`:
* **Nếu hai ký tự khớp nhau**: Ký tự này thuộc LCS. Độ dài tăng thêm 1 từ trạng thái trước đó khi chưa xét 2 ký tự này:
  $$dp[i][j] = dp[i-1][j-1] + 1$$
* **Nếu hai ký tự khác nhau**: Ta lấy giá trị lớn hơn giữa hai lựa chọn: bỏ qua ký tự hiện tại của `text1` hoặc bỏ qua của `text2`:
  $$dp[i][j] = \max(dp[i-1][j], dp[i][j-1])$$

### 📊 Hướng tiếp cận tối ưu

#### 2D DP Table (Optimal) ⭐
* **Mô tả**: Tạo ma trận quy hoạch động kích thước $(m+1) \times (n+1)$ để lưu trữ.
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ với $m, n$ là độ dài của hai chuỗi.
  * **Space Complexity**: $O(m \cdot n)$ bộ nhớ ma trận. Có thể tối ưu về $O(\min(m, n))$ bằng mảng 1D.

### 💻 Java Clean Code
```java
public int longestCommonSubsequence(String text1, String text2) {
    int m = text1.length();
    int n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    
    return dp[m][n];
}
```

---

## 73. Target Sum (LeetCode #494) - Medium

### 💡 Ý tưởng cốt lõi
Gán dấu `+` hoặc `-` trước mỗi số trong mảng `nums` để tổng thu được bằng `target`. Hãy tìm số cách gán dấu thỏa mãn.
Bài toán này thực chất có thể quy đổi về bài toán **Tổng tập con (Subset Sum)**:
* Gọi $P$ là tập các số được gán dấu `+`, và $N$ là tập các số được gán dấu `-`.
* Ta có phương trình: $\sum(P) - \sum(N) = target$.
* Cộng cả hai vế với tổng các phần tử trong mảng $\sum(P) + \sum(N) = sum(nums)$:
  $$2 \cdot \sum(P) = target + sum(nums) \implies \sum(P) = \frac{target + sum(nums)}{2}$$
* Do đó, bài toán trở thành: *Tìm số cách chọn một tập con các phần tử từ `nums` sao cho tổng của chúng bằng một giá trị mới $S = (target + sum) / 2$*.
* Đây chính là bài toán **Knapsack 0/1 (Cái túi)** tìm số cách đạt được tổng $S$.
* Lưu ý: Nếu $target + sum$ là số lẻ, hoặc $target + sum < 0$, thì không có cách nào thỏa mãn (trả về 0).

### 📊 Hướng tiếp cận tối ưu

#### Knapsack DP với mảng 1D (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \cdot S)$ với $n$ là số lượng phần tử và $S$ là tổng tập con đích cần tìm.
  * **Space Complexity**: $O(S)$ lưu mảng DP 1 chiều.

### 💻 Java Clean Code
```java
public int findTargetSumWays(int[] nums, int target) {
    int sum = 0;
    for (int num : nums) sum += num;
    
    // Kiểm tra điều kiện toán học vô lý
    if (Math.abs(target) > sum || (target + sum) % 2 != 0) {
        return 0;
    }
    
    int subsetSum = (target + sum) / 2;
    int[] dp = new int[subsetSum + 1];
    dp[0] = 1; // Có 1 cách tạo tổng bằng 0 là không chọn gì
    
    for (int num : nums) {
        // Duyệt ngược từ subsetSum về num để tránh tái sử dụng phần tử trong cùng 1 lượt
        for (int j = subsetSum; j >= num; j--) {
            dp[j] += dp[j - num];
        }
    }
    
    return dp[subsetSum];
}
```

---

## 74. Edit Distance (LeetCode #72) - Medium

### 💡 Ý tưởng cốt lõi
Tìm số lượng thao tác tối thiểu (chèn, xóa, thay thế ký tự) để biến đổi chuỗi `word1` thành chuỗi `word2`.
Định nghĩa trạng thái: `dp[i][j]` là khoảng cách Edit tối thiểu để biến đổi tiền tố `word1.substring(0, i)` thành `word2.substring(0, j)`.
Khi xét ký tự `word1.charAt(i-1)` và `word2.charAt(j-1)`:
* **Nếu hai ký tự giống nhau**: Không cần thao tác gì thêm:
  $$dp[i][j] = dp[i-1][j-1]$$
* **Nếu hai ký tự khác nhau**: Ta chọn giá trị nhỏ nhất cộng thêm 1 từ ba thao tác:
  * Thao tác 1 (Chèn): `dp[i][j-1] + 1`
  * Thao tác 2 (Xóa): `dp[i-1][j] + 1`
  * Thao tác 3 (Thay thế): `dp[i-1][j-1] + 1`
  * Tổng hợp:
    $$dp[i][j] = 1 + \min(dp[i][j-1], \min(dp[i-1][j], dp[i-1][j-1]))$$
* Trường hợp cơ sở:
  * `dp[i][0] = i` (xóa toàn bộ $i$ ký tự để thành chuỗi rỗng).
  * `dp[0][j] = j` (chèn toàn bộ $j$ ký tự từ chuỗi rỗng).

### 📊 Hướng tiếp cận tối ưu

#### 2D DP Table (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ với $m, n$ là độ dài của hai từ.
  * **Space Complexity**: $O(m \cdot n)$ lưu bảng trạng thái.

### 💻 Java Clean Code
```java
public int minDistance(String word1, String word2) {
    int m = word1.length();
    int n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    // Khởi tạo các trường hợp cơ sở
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1]; // Không tốn thêm thao tác nào
            } else {
                int insertOp = dp[i][j - 1];
                int deleteOp = dp[i - 1][j];
                int replaceOp = dp[i - 1][j - 1];
                dp[i][j] = 1 + Math.min(replaceOp, Math.min(insertOp, deleteOp));
            }
        }
    }
    
    return dp[m][n];
}
```

---

## 75. Partition Equal Subset Sum (LeetCode #416) - Medium

### 💡 Ý tưởng cốt lõi
Xác định xem mảng số nguyên dương `nums` có thể được chia làm hai tập con sao cho tổng các phần tử của hai tập con này bằng nhau hay không.
Gọi tổng tất cả các phần tử trong mảng là `sum`.
* Nếu `sum` là số lẻ, chắc chắn không thể chia đôi bằng nhau (trả về `false`).
* Nếu `sum` chẵn, bài toán trở thành: *Tìm xem có tồn tại một tập con nào trong `nums` có tổng bằng đúng `sum / 2` hay không*.
* Đây chính là bài toán **Knapsack 0/1** dạng quyết định (tìm sự tồn tại của tổng mục tiêu `target = sum / 2`):
  * Định nghĩa trạng thái: `dp[j]` là boolean, cho biết có thể tạo ra tổng `j` từ các phần tử đã duyệt hay không.
  * Ban đầu `dp[0] = true`, các giá trị khác bằng `false`.
  * Duyệt qua từng số `num`. Với mỗi số, duyệt ngược từ `target` về `num` để cập nhật:
    $$dp[j] = dp[j] \lor dp[j - num]$$

### 📊 Hướng tiếp cận tối ưu

#### Knapsack 0/1 1D State Array (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \cdot target)$ với $target = sum / 2$.
  * **Space Complexity**: $O(target)$ sử dụng mảng trạng thái boolean 1D.

### 💻 Java Clean Code
```java
public boolean canPartition(int[] nums) {
    int sum = 0;
    for (int num : nums) sum += num;
    
    if (sum % 2 != 0) return false;
    
    int target = sum / 2;
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;
    
    for (int num : nums) {
        // Phải duyệt ngược để đảm bảo mỗi số trong nums chỉ được chọn tối đa 1 lần
        for (int j = target; j >= num; j--) {
            if (dp[j - num]) {
                dp[j] = true;
            }
        }
        
        // Tối ưu: dừng sớm nếu đã tìm được tổng target
        if (dp[target]) {
            return true;
        }
    }
    
    return dp[target];
}
```
