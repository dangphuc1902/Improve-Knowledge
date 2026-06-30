# 07 - Trees: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 9 bài toán thuộc chủ đề **Trees** từ LeetCode Master Tracker.

---

## TreeNode Definition (Java)
Tất cả các bài toán sử dụng cấu trúc `TreeNode` tiêu chuẩn sau:
```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
```

---

## 34. Invert Binary Tree (LeetCode #226) - Easy

### 💡 Ý tưởng cốt lõi
Đảo ngược cây nhị phân (ảnh phản chiếu).
Ý tưởng tối ưu là dùng **DFS đệ quy**:
* Với mỗi node hiện tại, đổi chỗ node con bên trái (`left`) và node con bên phải (`right`).
* Gọi đệ quy đảo ngược cho cây con bên trái.
* Gọi đệ quy đảo ngược cho cây con bên phải.
* Trường hợp cơ sở (base case): Nếu node rỗng (`null`), trả về `null`.

### 📊 Hướng tiếp cận tối ưu

#### DFS đệ quy (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ với $n$ là số node trong cây.
  * **Space Complexity**: $O(h)$ với $h$ là chiều cao cây (do call stack). Trường hợp xấu nhất $O(n)$ (cây xiên), tốt nhất $O(\log n)$ (cây cân bằng).

### 🔄 Dry Run với ví dụ
* **Input**: Cây `[4, 2, 7]` (4 là gốc, con là 2 và 7)
* **Xử lý**:
  - Gốc `4` không null:
    * Hoán đổi con: `left` thành `7`, `right` thành `2`.
    * Đệ quy cho `7`: đảo ngược các con của `7`.
    * Đệ quy cho `2`: đảo ngược các con của `2`.
* **Kết quả**: Cây `[4, 7, 2]`

### 💻 Java Clean Code
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    
    // Đảo chỗ hai con trái và phải
    TreeNode temp = root.left;
    root.left = root.right;
    root.right = temp;
    
    // Gọi đệ quy cho hai cây con
    invertTree(root.left);
    invertTree(root.right);
    
    return root;
}
```

---

## 35. Maximum Depth of Binary Tree (LeetCode #104) - Easy

### 💡 Ý tưởng cốt lõi
Tìm chiều cao lớn nhất (số lượng node lớn nhất từ gốc tới lá) của cây nhị phân.
Ta dùng đệ quy **DFS**. Chiều cao của cây tại một node bằng:
$$\text{Depth} = 1 + \max(\text{Depth}(left), \text{Depth}(right))$$
Nếu node rỗng, chiều cao bằng 0.

### 📊 Hướng tiếp cận tối ưu

#### DFS đệ quy (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mọi node.
  * **Space Complexity**: $O(h)$ chiều cao của cây.

### 💻 Java Clean Code
```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

---

## 36. Same Tree (LeetCode #100) - Easy

### 💡 Ý tưởng cốt lõi
Kiểm tra hai cây nhị phân $p$ và $q$ có giống hệt nhau không.
Hai cây giống nhau khi và chỉ khi:
1. Cả hai đều rỗng (`null`).
2. Giá trị tại node hiện tại giống nhau (`p.val == q.val`).
3. Cây con bên trái của cả hai giống nhau.
4. Cây con bên phải của cả hai giống nhau.

### 📊 Hướng tiếp cận tối ưu

#### DFS đệ quy (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ với $n$ là số node nhỏ hơn giữa hai cây.
  * **Space Complexity**: $O(h)$ chiều cao của cây.

### 💻 Java Clean Code
```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    if (p.val != q.val) return false;
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```

---

## 37. Subtree of Another Tree (LeetCode #572) - Easy

### 💡 Ý tưởng cốt lõi
Kiểm tra xem cây `subRoot` có phải là một cây con của cây `root` hay không.
Cây `subRoot` là cây con của `root` nếu:
1. `subRoot` giống hệt cây `root` hiện tại (sử dụng hàm `isSameTree` của bài trước).
2. Hoặc `subRoot` là cây con của cây con bên trái `root.left`.
3. Hoặc `subRoot` là cây con của cây con bên phải `root.right`.

### 📊 Hướng tiếp cận tối ưu

#### Double DFS (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ với $m, n$ là số node của hai cây. Trong trường hợp xấu nhất, ta gọi `isSameTree` trên mọi node của `root`.
  * **Space Complexity**: $O(h_{root})$ do ngăn xếp đệ quy.

### 💻 Java Clean Code
```java
public boolean isSubtree(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    if (isSame(root, subRoot)) return true;
    return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
}

private boolean isSame(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    if (p.val != q.val) return false;
    return isSame(p.left, q.left) && isSame(p.right, q.right);
}
```

---

## 38. Level Order Traversal (LeetCode #102) - Medium

### 💡 Ý tưởng cốt lõi
Duyệt cây nhị phân theo từng cấp (level-by-level) từ trên xuống dưới, trái sang phải.
Chúng ta sử dụng thuật toán **BFS (Breadth-First Search - Tìm kiếm theo chiều rộng)** kết hợp với một **Queue (Hàng đợi)**.
Mỗi vòng lặp đại diện cho việc xử lý một cấp độ (level) của cây. Ta lấy kích thước hiện tại của hàng đợi `size = queue.size()`, sau đó lặp đúng `size` lần để lấy ra các node thuộc cùng cấp này, lưu giá trị của chúng vào một danh sách cấp, và đẩy con trái/phải (nếu có) của chúng vào hàng đợi cho cấp tiếp theo.

### 📊 Hướng tiếp cận tối ưu

#### BFS Queue (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt qua mỗi node đúng 1 lần.
  * **Space Complexity**: $O(n)$ để lưu trữ node ở cấp lớn nhất trong hàng đợi (tối đa $n/2$ node ở hàng lá cuối cùng).

### 🔄 Dry Run với ví dụ
* **Input**: Cây `[3, 9, 20, null, null, 15, 7]`
* **Khởi tạo**: `queue = [3]`, `res = []`
* **Xử lý**:
  - **Cấp 1**: `size = 1`.
    * Pop `3`. Thêm con: `queue = [9, 20]`. Danh sách cấp 1: `[3]`.
  - **Cấp 2**: `size = 2`.
    * Pop `9`. Không có con.
    * Pop `20`. Thêm con: `queue = [15, 7]`. Danh sách cấp 2: `[9, 20]`.
  - **Cấp 3**: `size = 2`.
    * Pop `15`. Không có con.
    * Pop `7`. Không có con. Danh sách cấp 3: `[15, 7]`.
* **Kết quả**: `[[3], [9, 20], [15, 7]]`

### 💻 Java Clean Code
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            currentLevel.add(node.val);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        res.add(currentLevel);
    }
    
    return res;
}
```

---

## 39. Validate Binary Search Tree (LeetCode #98) - Medium

### 💡 Ý tưởng cốt lõi
Kiểm tra xem một cây nhị phân có phải là Cây tìm kiếm nhị phân (BST) hợp lệ hay không.
Một BST hợp lệ khi:
* Cây con bên trái chỉ chứa các node có giá trị **nhỏ hơn** node cha.
* Cây con bên phải chỉ chứa các node có giá trị **lớn hơn** node cha.
* Cả hai cây con trái và phải cũng phải là BST.
Chúng ta dùng đệ quy DFS truyền kèm **khoảng giới hạn giá trị** `[min, max]` cho mỗi node. Ban đầu, gốc có giới hạn `[-INF, INF]`.
* Khi đi sang con trái, giới hạn trên cập nhật: `max = node.val`.
* Khi đi sang con phải, giới hạn dưới cập nhật: `min = node.val`.

### 📊 Hướng tiếp cận tối ưu

#### DFS Range Validation (Optimal) ⭐
* **Mô tả**: Sử dụng kiểu dữ liệu `Long` hoặc đối tượng `Integer` để tránh lỗi tràn số khi so sánh với `Integer.MIN_VALUE` hoặc `Integer.MAX_VALUE`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ kiểm tra mỗi node 1 lần.
  * **Space Complexity**: $O(h)$ call stack.

### 💻 Java Clean Code
```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer low, Integer high) {
    if (node == null) return true;
    
    // Nếu vượt ngoài khoảng giới hạn cho phép
    if ((low != null && node.val <= low) || (high != null && node.val >= high)) {
        return false;
    }
    
    // Đệ quy kiểm tra con trái (cập nhật giới hạn trên) và con phải (cập nhật giới hạn dưới)
    return validate(node.left, low, node.val) && validate(node.right, node.val, high);
}
```

---

## 40. Kth Smallest Element in a BST (LeetCode #230) - Medium

### 💡 Ý tưởng cốt lõi
Tìm phần tử nhỏ thứ $k$ trong Cây tìm kiếm nhị phân (BST).
Đặc điểm quan trọng của BST là **Phép duyệt trung thứ tự (Inorder Traversal - Trái → Gốc → Phải)** sẽ đi qua các node theo thứ tự **tăng dần**.
Ta thực hiện duyệt trung thứ tự (có thể dùng đệ quy hoặc lặp bằng Stack). Khi đi qua mỗi node, ta giảm $k$ đi 1. Khi $k == 0$, ta tìm được phần tử đích.

### 📊 Hướng tiếp cận tối ưu

#### Iterative Inorder Traversal (Optimal) ⭐
* **Mô tả**: Duyệt trung thứ tự bằng Stack giúp ta dừng thuật toán ngay khi tìm thấy phần tử thứ $k$, tối ưu hơn việc duyệt hết cây.
* **Độ phức tạp**:
  * **Time Complexity**: $O(h + k)$ với $h$ là chiều cao cây. Trong trường hợp tốt nhất, ta chỉ cần duyệt $k$ phần tử.
  * **Space Complexity**: $O(h)$ để lưu trữ các node trên stack.

### 💻 Java Clean Code
```java
public int kthSmallest(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    
    while (curr != null || !stack.isEmpty()) {
        // Đi hết sang bên trái
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        
        curr = stack.pop();
        k--;
        if (k == 0) {
            return curr.val;
        }
        
        // Đi sang bên phải
        curr = curr.right;
    }
    
    return -1;
}
```

---

## 41. Lowest Common Ancestor of a Binary Tree (LeetCode #236) - Medium

### 💡 Ý tưởng cốt lõi
Tìm Tổ tiên chung thấp nhất (LCA) của hai node $p$ và $q$ trong cây nhị phân.
Ta dùng đệ quy DFS:
* Nếu node hiện tại là `null`, hoặc là $p$, hoặc là $q$, ta trả về chính nó.
* Gọi đệ quy tìm LCA ở cây con bên trái (`left`) và cây con bên phải (`right`).
* Nếu cả hai kết quả trả về từ `left` và `right` đều khác `null`, nghĩa là $p$ và $q$ phân bố ở hai phía của node hiện tại. Vậy node hiện tại chính là LCA.
* Nếu một bên là `null`, ta trả về kết quả của bên còn lại (phía không null).

### 📊 Hướng tiếp cận tối ưu

#### DFS Post-order (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt qua các node.
  * **Space Complexity**: $O(h)$ call stack.

### 💻 Java Clean Code
```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) {
        return root;
    }
    
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    
    // Nếu p và q nằm ở hai nhánh khác nhau của root
    if (left != null && right != null) {
        return root;
    }
    
    // Nếu chỉ có một nhánh chứa p hoặc q
    return (left != null) ? left : right;
}
```

---

## 42. Binary Tree Maximum Path Sum (LeetCode #124) - Hard

### 💡 Ý tưởng cốt lõi
Tìm tổng đường đi lớn nhất giữa hai node bất kỳ trong một cây nhị phân. Đường đi không bắt buộc phải đi qua gốc của cây.
Tại mỗi node, ta tính toán **Đường đi lớn nhất có thể đóng góp lên cha** (chỉ chọn nhánh trái hoặc nhánh phải, nhánh nào lớn hơn):
$$\text{Max gain} = \text{val} + \max(0, \max(\text{gain}(left), \text{gain}(right)))$$
Ta chỉ lấy các giá trị gain dương (`max(0, ...)`), nếu âm thì bỏ qua (không đi vào nhánh đó).
Đồng thời, ta liên tục cập nhật tổng đường đi lớn nhất đi qua chính node hiện tại làm đỉnh nối (uốn cong qua node):
$$\text{Current path sum} = \text{val} + \max(0, \text{gain}(left)) + \max(0, \text{gain}(right))$$
Dùng một biến toàn cục `maxSum` để cập nhật giá trị cực đại này.

### 📊 Hướng tiếp cận tối ưu

#### DFS Post-order (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt qua mỗi node đúng 1 lần.
  * **Space Complexity**: $O(h)$ call stack.

### 🔄 Dry Run với ví dụ
* **Input**: Cây `[-10, 9, 20, null, null, 15, 7]`
* **Xử lý**:
  - Tại node lá `15`: gain = 15, maxSum cập nhật = 15.
  - Tại node lá `7`: gain = 7, maxSum cập nhật = 15.
  - Tại node `20`:
    * gain left = 15, gain right = 7.
    * gain của `20` lên cha = `20 + max(15, 7) = 35`.
    * Tổng đường uốn cong qua `20` = `20 + 15 + 7 = 42`. `maxSum` cập nhật = 42.
  - Tại node `9`: gain = 9.
  - Tại gốc `-10`:
    * gain left = 9, gain right = 35.
    * Tổng uốn cong = `-10 + 9 + 35 = 34 < 42`.
* **Kết quả**: `42` (đường đi `15 -> 20 -> 7`).

### 💻 Java Clean Code
```java
class Solution {
    private int maxSum = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        calculateGain(root);
        return maxSum;
    }

    private int calculateGain(TreeNode node) {
        if (node == null) return 0;
        
        // Chỉ lấy gain nếu nó dương, ngược lại coi như bằng 0 (bỏ nhánh đó)
        int leftGain = Math.max(0, calculateGain(node.left));
        int rightGain = Math.max(0, calculateGain(node.right));
        
        // Tính tổng đường đi uốn cong lấy node làm đỉnh
        int currentPathSum = node.val + leftGain + rightGain;
        maxSum = Math.max(maxSum, currentPathSum);
        
        // Trả về nhánh lớn hơn cho cha chọn
        return node.val + Math.max(leftGain, rightGain);
    }
}
```
