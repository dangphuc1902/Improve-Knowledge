# 07 - Trees

## 📖 Tổng quan

**Tree** là cấu trúc dữ liệu phân cấp, gồm nodes kết nối bởi edges. **Binary Tree** là tree mỗi node có tối đa 2 con (left, right).

```java
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
```

**Binary Search Tree (BST)**: left < root < right cho mọi node.

## 🧠 Kiến thức cốt lõi

### Các loại duyệt (Traversal)
| Loại | Thứ tự | Use case |
|------|--------|----------|
| Inorder | Left → Root → Right | BST sorted order |
| Preorder | Root → Left → Right | Copy tree, serialize |
| Postorder | Left → Right → Root | Delete tree, tính height |
| Level Order | Tầng theo tầng (BFS) | In theo level, tìm min depth |

### Properties
| Property | Giải thích |
|----------|-----------|
| Height | Số edges từ root đến leaf xa nhất |
| Depth | Số edges từ node đến root |
| Balanced | Height(left) - Height(right) ≤ 1 cho mọi node |
| Complete | Mọi level đầy, trừ level cuối (đầy từ trái) |

## 🔍 Khi nào sử dụng?

- Bài cho sẵn `TreeNode` structure
- **DFS** (Depth-First): dùng recursion hoặc stack
- **BFS** (Breadth-First): dùng queue, xử lý theo level
- Bài liên quan **BST**: tận dụng tính chất sorted
- **Path problems**: tìm đường, tổng đường...

## 📝 Các Pattern phổ biến

### Pattern 1: DFS Recursive (Top-Down/Bottom-Up)
- **Nó là gì?**: Sử dụng đệ quy để duyệt qua các node của cây theo chiều sâu. Có thể xử lý dữ liệu khi đi xuống (Top-down) hoặc khi quay ngược lên (Bottom-up).
- **Giải quyết bài toán nào?**: 
    - Đảo ngược cây (`Invert Binary Tree`).
    - Tính chiều cao của cây (`Maximum Depth of Binary Tree`).
    - Kiểm tra cây đối xứng (`Symmetric Tree`).
- **Ưu điểm**:
    - Code cực kỳ ngắn gọn, phản ánh đúng cấu trúc phân cấp của cây.
    - Tự động quản lý trạng thái thông qua Call Stack.
- **Nhược điểm**:
    - Nguy cơ `StackOverflowError` nếu cây quá sâu (ví dụ cây bị lệch - skewed tree).
- **Sự thay thế**:
    - **Iterative DFS**: Sử dụng một `Stack` tường minh (O(n) space).

```java
// Đệ quy cơ bản - xử lý từng node
public int dfs(TreeNode node) {
    if (node == null) return 0; // Base case

    int left = dfs(node.left);
    int right = dfs(node.right);

    return process(node.val, left, right); // Combine
}
```

### Pattern 2: BFS Level Order Traversal
- **Nó là gì?**: Duyệt qua từng tầng (level) của cây bằng cách sử dụng một `Queue`.
- **Giải quyết bài toán nào?**: 
    - Duyệt cây theo tầng (`Binary Tree Level Order Traversal`).
    - Tìm node nằm ở phía bên phải nhất (`Binary Tree Right Side View`).
    - Tìm khoảng cách ngắn nhất từ root đến leaf.
- **Ưu điểm**:
    - Tìm thấy kết quả ở các tầng gần root nhanh hơn DFS (đặc biệt hữu ích cho tìm đường đi ngắn nhất).
- **Nhược điểm**:
    - Tốn bộ nhớ để lưu trữ các node ở tầng rộng nhất (O(w) với w là width).
- **Sự thay thế**:
    - **DFS + Tracking Depth**: Duyệt DFS và truyền thêm biến `depth` để đưa node vào đúng danh sách của tầng đó.

```java
Queue<TreeNode> queue = new LinkedList<>();
queue.offer(root);
while (!queue.isEmpty()) {
    int size = queue.size(); // Số node trong level hiện tại
    for (int i = 0; i < size; i++) {
        TreeNode node = queue.poll();
        // Xử lý node
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
}
```

### Pattern 3: BST Property (Sorted Order)
- **Nó là gì?**: Tận dụng tính chất của Cây tìm kiếm nhị phân (Binary Search Tree): Node trái < Node gốc < Node phải.
- **Giải quyết bài toán nào?**: 
    - Kiểm tra cây có phải là BST không (`Validate BST`).
    - Tìm kiếm, thêm, xóa node trong O(log n).
    - Tìm phần tử nhỏ thứ k (`Kth Smallest Element in a BST`).
- **Ưu điểm**:
    - Tối ưu hóa thời gian tìm kiếm từ O(n) xuống O(log n).
    - Duyệt `Inorder` của BST luôn cho ra một dãy số đã sắp xếp tăng dần.
- **Nhược điểm**:
    - Nếu cây không cân bằng (skewed), hiệu năng sẽ giảm về O(n).
- **Sự thay thế**:
    - Duyệt cây như một Binary Tree thông thường (không tận dụng tính chất BST).

```java
// Validate BST - dùng range [min, max]
public boolean isValidBST(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return isValidBST(node.left, min, node.val)
        && isValidBST(node.right, node.val, max);
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Invert Binary Tree - step by step
```
Input Tree:
       4
      / \
     2   7
    / \ / \
   1  3 6  9

Bước 1: Visit node 4
  - Swap 4.left ↔ 4.right
  - Tree: 4 (2 ← → 7)
  - Call invertTree(2)

Bước 2: Visit node 2
  - Swap 2.left ↔ 2.right (1 ← → 3)
  - Tree: 2 (1 ← → 3)
  - Call invertTree(1) → return (leaf)
  - Call invertTree(3) → return (leaf)

Bước 3: Visit node 7
  - Swap 7.left ↔ 7.right (6 ← → 9)
  - Tree: 7 (6 ← → 9)
  - Call invertTree(6) → return (leaf)
  - Call invertTree(9) → return (leaf)

Output Tree:
       4
      / \
     7   2
    / \ / \
   9  6 3  1

✅ Hoàn toàn lật ngược!
```

**Insight:** DFS recursive đơn giản — swap parent, đệ quy vào con

---

### Ví dụ 2: Maximum Depth - step by step
```
Input Tree (chiều sâu 3):
       3
      / \
     9  20
       /  \
      15   7

Bước 1: maxDepth(3)
  maxDepth(left=9):
    maxDepth(left=null) → 0
    maxDepth(right=null) → 0
    return 1 + max(0,0) = 1
  
  maxDepth(right=20):
    maxDepth(left=15):
      maxDepth(left=null) → 0
      maxDepth(right=null) → 0
      return 1 + max(0,0) = 1
    
    maxDepth(right=7):
      maxDepth(left=null) → 0
      maxDepth(right=null) → 0
      return 1 + max(0,0) = 1
    
    return 1 + max(1,1) = 2
  
  return 1 + max(1,2) = 3

✅ Output: 3
```

**Insight:** Tư duy: "Depth = 1 + max(left_depth, right_depth)"

---

### Ví dụ 3: Level Order Traversal - step by step
```
Input Tree:
       3
      / \
     9  20
       /  \
      15   7

Init: queue = [3], result = []

Iteration 1 (level 0):
  levelSize = 1
  node = 3 → currentLevel = [3]
  add 9, 20 to queue → queue = [9, 20]
  result = [[3]]

Iteration 2 (level 1):
  levelSize = 2
  node = 9 → currentLevel = [9]
    9.left = null, 9.right = null → queue = [20]
  node = 20 → currentLevel = [9, 20]
    20.left = 15, 20.right = 7 → queue = [15, 7]
  result = [[3], [9, 20]]

Iteration 3 (level 2):
  levelSize = 2
  node = 15 → currentLevel = [15]
    15.left = null, 15.right = null → queue = [7]
  node = 7 → currentLevel = [15, 7]
    7.left = null, 7.right = null → queue = []
  result = [[3], [9, 20], [15, 7]]

queue empty → exit

✅ Output: [[3], [9, 20], [15, 7]]
```

**Insight:** 
- BFS (Queue) xử lý level-by-level
- Size trick: `for (int i=0; i<size; i++)` để xử lý từng level riêng

---

## 🔄 So sánh các Approach

### Invert Tree: DFS Recursive vs BFS
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| DFS recursive ⭐ | O(n) | O(h) | Simple, intuitive |
| BFS | O(n) | O(n) | More code |
| Iterative DFS | O(n) | O(h) | Stack based |

### Max Depth: DFS vs BFS
| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| DFS recursive ⭐ | O(n) | O(h) | Simple, natural |
| BFS | O(n) | O(w) | w = max width |
| DP (memoization) | O(n) | O(h) + O(n) cache | Reuse subproblems |

### Level Order: BFS vs DFS
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| BFS Queue ⭐ | O(n) | O(w) | Natural for level order |
| DFS + depth | O(n) | O(h) | More complex |

---

## 🚨 Edge Cases & Common Mistakes

```java
// Invert Tree:
// 1. Tree = null → null
// 2. Tree = single node → return that node
// 3. Tree = completely skewed (linked list) → O(n) space

// Max Depth:
// 1. Tree = null → 0 (NOT 1)
// 2. Single node → 1
// 3. Completely skewed (linked list) → O(n) depth (time limit?)

// Level Order:
// 1. Tree = null → [] (NOT null!)
// 2. Single node → [[val]]
// 3. Wide tree → may need large queue space
// ⚠️ MISTAKE: Quên dùng size trick
//   for (int i=0; i<queue.size(); i++) ← WRONG (size thay đổi)
//   int size = queue.size(); for (int i=0; i<size; i++) ← RIGHT
```

## 💡 Best Practices

1. **Define TreeNode hết**
   ```java
   class TreeNode {
       int val;
       TreeNode left, right;
       TreeNode(int val) { this.val = val; }
   }
   ```

2. **DFS template**
   ```java
   public ResultType dfs(TreeNode node) {
       if (node == null) return baseCase; // Base case luôn đầu tiên!
       
       ResultType left = dfs(node.left);
       ResultType right = dfs(node.right);
       
       return combine(node.val, left, right);
   }
   ```

3. **BFS template**
   ```java
   public ResultType bfs(TreeNode root) {
       if (root == null) return baseCase;
       
       Queue<TreeNode> q = new LinkedList<>();
       q.offer(root);
       
       while (!q.isEmpty()) {
           int size = q.size(); // ⭐ Key trick!
           // Process level
       }
   }
   ```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| DFS (recursive) | O(n) | O(h) - h=height, call stack |
| BFS (level order) | O(n) | O(w) - w=max width |
| BST search | O(h) | O(1) iterative / O(h) recursive |

> Balanced tree: h = O(log n). Skewed tree: h = O(n).

## 💡 Tips phỏng vấn

1. **Base case**: Luôn xử lý `node == null` đầu tiên
2. **DFS vs BFS**: DFS cho path/depth problems, BFS cho level problems
3. **BST**: Inorder traversal cho sorted order — rất hữu ích
4. **Recursion**: Nghĩ "giả sử left/right subtree đã giải xong, root cần làm gì?"
