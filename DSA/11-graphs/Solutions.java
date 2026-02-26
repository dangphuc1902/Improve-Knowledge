import java.util.*;

/**
 * =============================================
 *  11 - GRAPHS
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Number of Islands (LeetCode #200) - Medium
// -----------------------------------------------
// Cho grid 2D gồm '1' (đất) và '0' (nước), đếm số đảo.
// Đảo = nhóm '1' kết nối liền nhau (4 hướng).
//
// Approach: DFS flood fill.
// Duyệt grid, gặp '1' → đếm +1, DFS "tô" tất cả '1' liên thông thành '0'.
//
// Time: O(m × n) - mỗi ô visit tối đa 1 lần
// Space: O(m × n) - call stack trong worst case
class NumberOfIslands {
    public int numIslands(char[][] grid) {
        int count = 0;
        int rows = grid.length, cols = grid[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c); // Tô toàn bộ đảo này
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, int r, int c) {
        // Boundary check + water check
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
            || grid[r][c] == '0') {
            return;
        }

        grid[r][c] = '0'; // Mark visited (biến đất thành nước)

        // DFS 4 hướng
        dfs(grid, r + 1, c);
        dfs(grid, r - 1, c);
        dfs(grid, r, c + 1);
        dfs(grid, r, c - 1);
    }
}

// -----------------------------------------------
// Bài 2: Clone Graph (LeetCode #133) - Medium
// -----------------------------------------------
// Clone (deep copy) một undirected graph.
// Mỗi node có val và list neighbors.
//
// Approach: BFS/DFS + HashMap.
// HashMap<Original Node, Cloned Node> để track đã clone chưa.
// Khi visit 1 node: tạo clone, rồi clone tất cả neighbors.
//
// Time: O(V + E) - visit mỗi node và edge 1 lần
// Space: O(V) - HashMap + queue/stack
class CloneGraph {
    // Definition cho Node
    static class Node {
        public int val;
        public List<Node> neighbors;
        public Node(int val) { this.val = val; this.neighbors = new ArrayList<>(); }
    }

    public Node cloneGraph(Node node) {
        if (node == null) return null;

        Map<Node, Node> cloneMap = new HashMap<>();

        // BFS
        Queue<Node> queue = new LinkedList<>();
        queue.offer(node);
        cloneMap.put(node, new Node(node.val)); // Clone root

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node neighbor : current.neighbors) {
                if (!cloneMap.containsKey(neighbor)) {
                    // Clone neighbor chưa tồn tại → tạo mới
                    cloneMap.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }
                // Kết nối clone của current với clone của neighbor
                cloneMap.get(current).neighbors.add(cloneMap.get(neighbor));
            }
        }

        return cloneMap.get(node);
    }
}

// -----------------------------------------------
// Bài 3: Course Schedule (LeetCode #207) - Medium
// -----------------------------------------------
// Có n courses (0 đến n-1) và prerequisites.
// prerequisites[i] = [a, b] nghĩa là phải học b trước a.
// Hỏi: có thể hoàn thành tất cả courses không?
// (= kiểm tra DAG có cycle không)
//
// Approach: Topological Sort (BFS - Kahn's Algorithm).
// 1. Build adjacency list + đếm inDegree
// 2. BFS từ nodes có inDegree = 0
// 3. Nếu process được tất cả nodes → no cycle → true
//
// Time: O(V + E)
// Space: O(V + E)
class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Build adjacency list + inDegree
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] pre : prerequisites) {
            graph.get(pre[1]).add(pre[0]); // pre[1] → pre[0]
            inDegree[pre[0]]++;
        }

        // BFS: bắt đầu từ courses không có prerequisite
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;

            for (int next : graph.get(course)) {
                inDegree[next]--;
                if (inDegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        // Nếu process hết tất cả courses → no cycle
        return processed == numCourses;
    }
}
