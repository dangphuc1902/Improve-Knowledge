import java.util.*;

/**
 * =============================================
 *  07 - TREES
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// Definition cho TreeNode (dùng chung cho các bài)
class TreeNode {
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

// -----------------------------------------------
// Bài 1: Invert Binary Tree (LeetCode #226) - Easy
// -----------------------------------------------
// Lật ngược binary tree: swap left ↔ right cho mọi node.
//
// Ví dụ:
//   Input:        4
//                / \
//               2   7
//              / \ / \
//             1  3 6  9
//   
//   Output:       4
//                / \
//               7   2
//              / \ / \
//             9  6 3  1
//
// ✅ Approach: DFS Recursive
// Tại mỗi node:
//   1. Swap left ↔ right
//   2. Đệ quy vào left subtree
//   3. Đệ quy vào right subtree
//
// ⏱️ Time: O(n) - visit mọi node tới đó
// 📦 Space: O(h) - call stack depth = height
//   - Balanced tree: h = O(log n)
//   - Skewed tree: h = O(n)
//
// 📊 Trace code:
//   invertTree(4):
//     - Swap 4: left=2, right=7 → 4: left=7, right=2
//     - invertTree(7):
//       - Swap 7: left=6, right=9 → 7: left=9, right=6
//       - invertTree(9) → null checks, return
//       - invertTree(6) → null checks, return
//     - invertTree(2):
//       - Swap 2: left=1, right=3 → 2: left=3, right=1
//       - invertTree(3) → null checks, return
//       - invertTree(1) → null checks, return
//     - return 4
//
// 🚨 Edge cases:
//   - root = null → return null (empty tree)
//   - root = single node → return that node
//   - root = completely skewed → O(n) space (call stack)
//
// 💡 Insight: DFS + swap in-place → simple and clean!
class InvertBinaryTree {
    public TreeNode invertTree(TreeNode root) {
        // Base case: null node không cần xử lý
        if (root == null) return null;
        
        // Swap left và right child
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        
        // Đệ quy vào left subtree (cũ là right)
        invertTree(root.left);
        
        // Đệ quy vào right subtree (cũ là left)
        invertTree(root.right);
        
        return root;
    }
}


// -----------------------------------------------
// Bài 2: Maximum Depth of Binary Tree (LeetCode #104) - Easy
// -----------------------------------------------
// Tìm chiều sâu tối đa (# nodes) từ root đến leaf xa nhất.
//
// Ví dụ:
//         3
//        / \
//       9  20
//         /  \
//        15   7
//   
//   Đường dài nhất: 3 → 20 → 15 (hoặc 3 → 20 → 7)
//   Depth = 3 (số nodes)
//
// 💡 Insight: Recursion
//   depth(node) = 1 + max(depth(left), depth(right))
//   depth(null) = 0
//
// ✅ Approach: DFS Recursive
// 1. Base case: null node → depth = 0
// 2. Recursive case: depth = 1 + max(left_depth, right_depth)
//
// ⏱️ Time: O(n) - visit every node
//   - Dù tree balanced hay skewed cũng phải visit all nodes
//
// 📦 Space: O(h) - call stack
//   - Balanced: h = O(log n)
//   - Skewed: h = O(n) ← có thể exceed stack limit!
//
// 📊 Trace code:
//   maxDepth(3):
//     maxDepth(9):
//       maxDepth(null) → 0
//       maxDepth(null) → 0
//       return 1 + max(0,0) = 1
//     
//     maxDepth(20):
//       maxDepth(15):
//         maxDepth(null) → 0
//         maxDepth(null) → 0
//         return 1 + max(0,0) = 1
//       
//       maxDepth(7):
//         maxDepth(null) → 0
//         maxDepth(null) → 0
//         return 1 + max(0,0) = 1
//       
//       return 1 + max(1,1) = 2
//     
//     return 1 + max(1,2) = 3
//   
//   ✅ Output: 3
//
// 🔄 Comparison DFS vs BFS:
//   |Approach|Time |Space|Pros|
//   |DFS Rec |O(n) |O(h) |⭐ Simple, intuitive|
//   |BFS     |O(n) |O(w) |w=max width |
//   |Itera   |O(n) |O(h) |Avoid recursion |
//
// 🚨 Edge cases:
//   - root = null → 0 (NOT 1!)
//   - root = single node → 1
//   - root = skewed (linked list) → O(n) depth
//     Có thể gây stack overflow → nên dùng iterative BFS
//
// 💡 Alternative: BFS (iterative, safe for deep trees)
//   ```java
//   public int maxDepth(TreeNode root) {
//       if (root == null) return 0;
//       Queue<TreeNode> q = new LinkedList<>();
//       q.offer(root);
//       int depth = 0;
//       while (!q.isEmpty()) {
//           depth++;
//           int size = q.size();
//           for (int i = 0; i < size; i++) {
//               TreeNode node = q.poll();
//               if (node.left != null) q.offer(node.left);
//               if (node.right != null) q.offer(node.right);
//           }
//       }
//       return depth;
//   }
//   ```
class MaxDepthBinaryTree {
    public int maxDepth(TreeNode root) {
        // Base case: null node không có depth
        if (root == null) return 0;
        
        // Tính depth của left subtree
        int leftDepth = maxDepth(root.left);
        
        // Tính depth của right subtree
        int rightDepth = maxDepth(root.right);
        
        // Depth của node hiện tại = 1 + max(left, right)
        // +1 vì đang ở node này
        return 1 + Math.max(leftDepth, rightDepth);
    }
}


// -----------------------------------------------
// Bài 3: Binary Tree Level Order Traversal (LeetCode #102) - Medium
// -----------------------------------------------
// Duyệt tree theo từng level (tầng), return List<List<Integer>>.
//
// Ví dụ:
//           3
//          / \
//         9  20
//           /  \
//          15   7
//   
//   Output: [[3], [9, 20], [15, 7]]
//           Level 0: [3]
//           Level 1: [9, 20]
//           Level 2: [15, 7]
//
// 💡 Insight: BFS (Breadth-First Search)
//   - DFS (stack) → depth-first → in-order, pre-order, post-order
//   - BFS (queue) → level-first → exactly what we need!
//
// ✅ Approach: BFS với Queue
// 1. Queue initial với root
// 2. Mỗi iteration:
//    - Get levelSize = number of nodes in current level
//    - Process tất cả levelSize nodes (remove + process)
//    - Add children vào queue cho level tiếp theo
// 3. Repeat cho đến khi queue empty
//
// ⏱️ Time: O(n) - visit mọi node một lần
//
// 📦 Space: O(w) where w = max width (max nodes in one level)
//   - Balanced tree: w = O(n/2) = O(n)
//   - Wide tree: w could be close to n
//
// 📊 Trace code (xem theory.md chi tiết):
//   queue = [3], result = []
//   
//   Iteration 1:
//     levelSize = 1
//     Process node 3 → currentLevel = [3]
//     Add 9, 20 → queue = [9, 20]
//     result = [[3]]
//   
//   Iteration 2:
//     levelSize = 2
//     Process node 9 → currentLevel = [9], queue = [20]
//     Process node 20 → currentLevel = [9, 20]
//       Add 15, 7 → queue = [15, 7]
//     result = [[3], [9, 20]]
//   
//   Iteration 3:
//     levelSize = 2
//     Process node 15 → currentLevel = [15], queue = [7]
//     Process node 7 → currentLevel = [15, 7], queue = []
//     result = [[3], [9, 20], [15, 7]]
//   
//   Output: [[3], [9, 20], [15, 7]]
//
// ⚠️ KEY TRICK: int levelSize = queue.size()
//   - Phải capture size trước loop!
//   - Vì queue.size() thay đổi khi thêm phần tử
//   - for (int i=0; i<queue.size(); i++) ← WRONG (size tăng trong loop)
//   - for (int i=0; i<levelSize; i++) ← CORRECT
//
// 🔄 Comparison BFS vs DFS:
//   |Approach|Time |Space|Best For|
//   |BFS Queue|O(n)|O(w) |⭐ Level order|
//   |DFS+depth|O(n)|O(h) |Less intuitive|
//
// 🚨 Edge cases:
//   - root = null → return []  (NOT null!)
//   - root = single node → [[val]]
//   - root = wide tree (many nodes level) → large space
//
// 💡 Variant: Zigzag Level Order (LeetCode #103)
//   Alternate direction: left-to-right, right-to-left, ...
//   Dùng Deque thay vì ArrayList để thêm đầu/cuối linh hoạt
class LevelOrderTraversal {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        
        // Edge case: empty tree
        if (root == null) return result;
        
        // BFS setup: queue + initial root
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        // Lặp cho đến khi queue empty
        while (!queue.isEmpty()) {
            // ⭐ KEY: Capture size trước loop!
            // Vì ta sẽ thêm element vào queue trong loop
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            
            // Process tất cả node trong level hiện tại
            for (int i = 0; i < levelSize; i++) {
                // Remove node từ queue
                TreeNode node = queue.poll();
                
                // Process: thêm value vào current level
                currentLevel.add(node.val);
                
                // Chuẩn bị cho level tiếp theo:
                // Thêm children vào queue
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            
            // Sau khi xong level, thêm vào result
            result.add(currentLevel);
        }
        
        return result;
    }
}

