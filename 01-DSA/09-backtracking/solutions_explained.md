# 09 - Backtracking: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 5 bài toán thuộc chủ đề **Backtracking (Quay lui)** từ LeetCode Master Tracker.

---

## 50. Subsets (LeetCode #78) - Medium

### 💡 Ý tưởng cốt lõi
Sinh ra tất cả các tập con (power set) có thể có của một mảng số nguyên `nums` gồm các số độc nhất.
Chúng ta sử dụng thuật toán **Quay lui (Backtracking)** mô phỏng cây quyết định nhị phân. Tại mỗi phần tử `nums[i]`, ta có hai lựa chọn:
1. **Lựa chọn 1**: Đưa `nums[i]` vào tập con hiện tại. Sau đó đệ quy sang phần tử tiếp theo `i + 1`. Sau khi đệ quy xong, ta thực hiện xóa `nums[i]` ra khỏi tập con (backtrack) để khôi phục trạng thái cũ.
2. **Lựa chọn 2**: Không đưa `nums[i]` vào tập con hiện tại, đệ quy trực tiếp sang phần tử tiếp theo `i + 1`.
Khi chỉ số `i == nums.length`, ta đã duyệt qua hết tất cả các phần tử. Ta thêm bản sao của tập con hiện tại vào danh sách kết quả.

### 📊 Hướng tiếp cận tối ưu

#### Decision Tree Backtracking (Optimal) ⭐
* **Mô tả**: Sinh tập con dựa trên hai nhánh quyết định (chọn / không chọn) ở mỗi bước.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \cdot 2^n)$ vì có $2^n$ tập con và mỗi tập con mất tối đa $O(n)$ thời gian để copy vào kết quả.
  * **Space Complexity**: $O(n)$ lưu trữ danh sách tạm thời và call stack đệ quy.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [1, 2]`
* **Xử lý**:
  - Bắt đầu với `i = 0`, `curr = []`
  - Nhánh 1: Thêm `nums[0] = 1`. `curr = [1]`.
    * Đi tiếp `i = 1`. Nhánh 1.1: Thêm `nums[1] = 2`. `curr = [1, 2]`.
      - Đi tiếp `i = 2` (đạt cuối) → Thêm `[1, 2]` vào kết quả.
      - Quay lui (backtrack): xóa `2` khỏi `curr`. `curr = [1]`.
    * Nhánh 1.2: Không thêm `2`. `curr = [1]`.
      - Đi tiếp `i = 2` (đạt cuối) → Thêm `[1]` vào kết quả.
  - Nhánh 2: Không thêm `1`. `curr = []`...
* **Kết quả**: `[[], [1], [2], [1, 2]]` (hoặc thứ tự tương đương)

### 💻 Java Clean Code
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(0, nums, new ArrayList<>(), res);
    return res;
}

private void backtrack(int start, int[] nums, List<Integer> curr, List<List<Integer>> res) {
    res.add(new ArrayList<>(curr)); // Thêm tập con hiện tại vào kết quả
    
    for (int i = start; i < nums.length; i++) {
        curr.add(nums[i]); // Chọn phần tử nums[i]
        backtrack(i + 1, nums, curr, res); // Đi tiếp
        curr.remove(curr.size() - 1); // Bỏ chọn (backtrack)
    }
}
```

---

## 51. Combination Sum (LeetCode #39) - Medium

### 💡 Ý tưởng cốt lõi
Tìm tất cả các tổ hợp số duy nhất từ mảng `candidates` có tổng bằng `target`. Một số có thể được chọn lặp lại nhiều lần.
Chúng ta dùng đệ quy Backtracking duy trì chỉ số bắt đầu `start`, danh sách tổ hợp hiện tại `curr`, và tổng còn lại `remainTarget`.
* Trường hợp cơ sở:
  * Nếu `remainTarget == 0`, tìm thấy tổ hợp hợp lệ → Thêm vào kết quả.
  * Nếu `remainTarget < 0`, vượt quá mục tiêu → Dừng đệ quy (cắt tỉa nhánh).
* Với mỗi bước, duyệt từ `start` để tránh việc tạo ra các tổ hợp hoán vị trùng lặp. Vì một số có thể tái sử dụng, bước đệ quy tiếp theo sẽ truyền `i` thay vì `i + 1`.

### 📊 Hướng tiếp cận tối ưu

#### Backtracking with Element Reuse (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(2^T)$ trong đó $T$ phụ thuộc vào giá trị của `target` (số lượng nhánh đệ quy tối đa).
  * **Space Complexity**: $O(T / \min)$ cho chiều sâu call stack đệ quy.

### 💻 Java Clean Code
```java
public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(0, candidates, target, new ArrayList<>(), res);
    return res;
}

private void backtrack(int start, int[] candidates, int remain, List<Integer> curr, List<List<Integer>> res) {
    if (remain < 0) {
        return; // Cắt tỉa nhánh khi tổng vượt quá target
    }
    if (remain == 0) {
        res.add(new ArrayList<>(curr)); // Tìm thấy tổ hợp thỏa mãn
        return;
    }
    
    for (int i = start; i < candidates.length; i++) {
        curr.add(candidates[i]);
        // Đi tiếp, truyền i vì cho phép sử dụng lại phần tử hiện tại
        backtrack(i, candidates, remain - candidates[i], curr, res);
        curr.remove(curr.size() - 1); // Quay lui
    }
}
```

---

## 52. Permutations (LeetCode #46) - Medium

### 💡 Ý tưởng cốt lõi
Sinh ra tất cả các hoán vị (permutations) của một mảng số nguyên `nums` gồm các số độc nhất.
Để sinh hoán vị, thứ tự chọn phần tử là quan trọng (ví dụ `[1, 2]` khác `[2, 1]`). Ta cần duyệt qua toàn bộ các phần tử ở mỗi bước của cây đệ quy, nhưng chỉ chọn phần tử chưa từng có trong hoán vị hiện tại.
Ta có thể sử dụng một mảng boolean `used` hoặc một `HashSet` để đánh dấu phần tử đã được chọn, từ đó tránh chọn lại.

### 📊 Hướng tiếp cận tối ưu

#### Backtracking with Used Array (Optimal) ⭐
* **Mô tả**: Dùng mảng `used` kiểm tra trong $O(1)$ phần tử đã được chọn.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \cdot n!)$ vì có $n!$ hoán vị và mỗi hoán vị mất $O(n)$ sao chép vào kết quả.
  * **Space Complexity**: $O(n)$ cho mảng `used` và call stack.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [1, 2]`
* **Xử lý**:
  - Bước 1: Chọn `1`. `curr = [1]`, `used[0] = true`.
    * Bước 2: Chọn `2`. `curr = [1, 2]`, `used[1] = true`.
      - Đạt độ dài 2 → Thêm `[1, 2]` vào kết quả.
      - Quay lui: xóa `2`, `used[1] = false`. `curr = [1]`.
    * Quay lui: xóa `1`, `used[0] = false`. `curr = []`.
  - Bước 3: Chọn `2`. `curr = [2]`, `used[1] = true`...
* **Kết quả**: `[[1, 2], [2, 1]]`

### 💻 Java Clean Code
```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, used, new ArrayList<>(), res);
    return res;
}

private void backtrack(int[] nums, boolean[] used, List<Integer> curr, List<List<Integer>> res) {
    if (curr.size() == nums.length) {
        res.add(new ArrayList<>(curr));
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if (!used[i]) {
            used[i] = true;
            curr.add(nums[i]);
            
            backtrack(nums, used, curr, res); // Đi tiếp
            
            curr.remove(curr.size() - 1); // Quay lui
            used[i] = false;
        }
    }
}
```

---

## 53. Word Search (LeetCode #79) - Medium

### 💡 Ý tưởng cốt lõi
Kiểm tra xem chuỗi `word` có tồn tại trên bảng ký tự 2D `board` hay không.
Chúng ta thực hiện DFS quay lui từ từng ô trên bảng:
1. So khớp ký tự đầu tiên của `word` tại ô `(r, c)`.
2. Ghi nhớ ô hiện tại bằng cách đánh dấu nó là đã thăm (ví dụ đổi ký tự thành `'#'` để tránh đi lặp lại ô đó trong từ hiện tại).
3. Đệ quy đi sang 4 hướng kề cạnh (lên, xuống, trái, phải) để khớp ký tự tiếp theo của `word`.
4. Nếu bất kỳ hướng đi nào khớp thành công toàn bộ từ, trả về `true`.
5. Nếu không thành công, khôi phục lại ký tự gốc tại ô `(r, c)` (backtrack) và trả về `false`.

### 📊 Hướng tiếp cận tối ưu

#### DFS Backtracking on Grid (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n \cdot 3^L)$ với $m \times n$ là kích thước bảng và $L$ là độ dài từ. Tại mỗi bước ta có tối đa 3 hướng đi tiếp (trừ hướng vừa tới).
  * **Space Complexity**: $O(L)$ cho call stack đệ quy sâu bằng độ dài của từ.

### 💻 Java Clean Code
```java
public boolean exist(char[][] board, String word) {
    int m = board.length;
    int n = board[0].length;
    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (dfs(board, r, c, word, 0)) {
                return true;
            }
        }
    }
    return false;
}

private boolean dfs(char[][] board, int r, int c, String word, int index) {
    // Tìm thấy toàn bộ từ
    if (index == word.length()) {
        return true;
    }
    
    // Kiểm tra biên và khớp ký tự
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != word.charAt(index)) {
        return false;
    }
    
    char temp = board[r][c];
    board[r][c] = '#'; // Đánh dấu ô đã đi qua
    
    // Đi tiếp 4 hướng kề cạnh
    boolean found = dfs(board, r + 1, c, word, index + 1)
                 || dfs(board, r - 1, c, word, index + 1)
                 || dfs(board, r, c + 1, word, index + 1)
                 || dfs(board, r, c - 1, word, index + 1);
                 
    board[r][c] = temp; // Khôi phục lại trạng thái (backtrack)
    
    return found;
}
```

---

## 54. N-Queens (LeetCode #51) - Hard

### 💡 Ý tưởng cốt lõi
Đặt $n$ quân Hậu trên bàn cờ $n \times n$ sao cho không có hai quân Hậu nào tấn công nhau.
Quân Hậu tấn công nhau khi chúng nằm trên cùng một hàng, cùng một cột, hoặc cùng một đường chéo.
Chúng ta dùng đệ quy Backtracking đặt quân Hậu theo từng hàng (row) từ `0` đến `n - 1`. Ở hàng `r`, ta thử đặt quân Hậu vào cột `c` từ `0` đến `n - 1`.
Để kiểm tra xem vị trí đặt quân Hậu có bị tấn công không trong thời gian $O(1)$, ta duy trì 3 Set:
1. `cols`: Lưu các cột đã có quân Hậu.
2. `diag1` (đường chéo xuôi): Các ô trên cùng đường chéo này có cùng hiệu chỉ số `r - c`.
3. `diag2` (đường chéo ngược): Các ô trên cùng đường chéo này có cùng tổng chỉ số `r + c`.

### 📊 Hướng tiếp cận tối ưu

#### Backtracking with Diagonal Sets (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n!)$ vì hàng đầu tiên có $n$ cách chọn, hàng thứ hai tối đa $n-2$ cách,...
  * **Space Complexity**: $O(n^2)$ để lưu trữ trạng thái bàn cờ và call stack đệ quy.

### 💻 Java Clean Code
```java
public class NQueens {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> res = new ArrayList<>();
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], '.');
        }
        
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // r - c
        Set<Integer> diag2 = new HashSet<>(); // r + c
        
        backtrack(0, n, board, cols, diag1, diag2, res);
        return res;
    }

    private void backtrack(int r, int n, char[][] board, 
                           Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2, 
                           List<List<String>> res) {
        if (r == n) {
            res.add(construct(board));
            return;
        }
        
        for (int c = 0; c < n; c++) {
            if (cols.contains(c) || diag1.contains(r - c) || diag2.contains(r + c)) {
                continue; // Vị trí bị tấn công, bỏ qua
            }
            
            // Đặt quân Hậu
            board[r][c] = 'Q';
            cols.add(c);
            diag1.add(r - c);
            diag2.add(r + c);
            
            backtrack(r + 1, n, board, cols, diag1, diag2, res); // Đệ quy hàng tiếp theo
            
            // Quay lui (backtrack)
            board[r][c] = '.';
            cols.remove(c);
            diag1.remove(r - c);
            diag2.remove(r + c);
        }
    }

    private List<String> construct(char[][] board) {
        List<String> path = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            path.add(new String(board[i]));
        }
        return path;
    }
}
```
