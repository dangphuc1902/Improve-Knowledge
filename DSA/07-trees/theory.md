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

### Pattern 1: DFS Recursive
```java
// Đệ quy cơ bản - xử lý từng node
public int dfs(TreeNode node) {
    if (node == null) return 0; // Base case

    int left = dfs(node.left);
    int right = dfs(node.right);

    return process(node.val, left, right); // Combine
}
```

### Pattern 2: BFS Level Order
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

### Pattern 3: BST Property
```java
// Validate BST - dùng range [min, max]
public boolean isValidBST(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return isValidBST(node.left, min, node.val)
        && isValidBST(node.right, node.val, max);
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
