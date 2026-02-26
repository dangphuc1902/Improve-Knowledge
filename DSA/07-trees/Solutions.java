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
// Lật ngược binary tree (swap left và right cho mọi node).
//
// Approach: DFS recursive.
// Tại mỗi node: swap left ↔ right, rồi đệ quy vào 2 con.
//
// Time: O(n) - visit mọi node
// Space: O(h) - call stack, h = height
class InvertBinaryTree {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;

        // Swap left và right
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        // Đệ quy vào 2 con
        invertTree(root.left);
        invertTree(root.right);

        return root;
    }
}

// -----------------------------------------------
// Bài 2: Maximum Depth of Binary Tree (LeetCode #104) - Easy
// -----------------------------------------------
// Tìm chiều sâu tối đa của binary tree.
// Depth = số nodes trên đường dài nhất từ root đến leaf.
//
// Approach: DFS recursive.
// Depth(node) = 1 + max(depth(left), depth(right))
//
// Time: O(n) - visit mọi node
// Space: O(h) - call stack
class MaxDepthBinaryTree {
    public int maxDepth(TreeNode root) {
        // Base case: node null có depth = 0
        if (root == null) return 0;

        // Đệ quy: depth = 1 (node hiện tại) + max depth của 2 con
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);

        return 1 + Math.max(leftDepth, rightDepth);
    }
}

// -----------------------------------------------
// Bài 3: Binary Tree Level Order Traversal (LeetCode #102) - Medium
// -----------------------------------------------
// Duyệt tree theo từng level, trả về List<List<Integer>>.
// Ví dụ:     3
//           / \
//          9  20
//            /  \
//           15   7
// → [[3],[9,20],[15,7]]
//
// Approach: BFS dùng Queue.
// Mỗi vòng lặp xử lý tất cả nodes trong 1 level (dùng size trick).
//
// Time: O(n) - visit mọi node
// Space: O(n) - queue chứa tối đa n/2 nodes (level cuối cùng)
class LevelOrderTraversal {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // Số node trong level hiện tại
            List<Integer> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);

                // Thêm con vào queue cho level tiếp theo
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(currentLevel);
        }

        return result;
    }
}
