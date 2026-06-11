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
// Đảo = nhóm '1' kết nối liền nhau (4 hướng: up, down, left, right).
//
// Ví dụ:
//   Input:
//   1 1 0 0 0
//   1 1 0 1 0
//   0 0 1 0 1
//   
//   Output: 4
//   Đảo 1: (0,0), (0,1), (1,0), (1,1)
//   Đảo 2: (1,3)
//   Đảo 3: (2,2)
//   Đảo 4: (2,4)
//
// 💡 Insight: DFS Flood Fill
//   Gặp '1' → đếm +1 → DFS "tô" tất cả '1' kết nối
//   Bằng cách mark thành '0'
//
// ✅ Approach: DFS Flood Fill
// 1. Duyệt từng ô grid
// 2. Gặp '1' → islandCount++, rồi DFS
// 3. DFS: mark current '1' → '0', đệ quy 4 hướng
//
// ⏱️ Time: O(m × n)
//   - Mỗi ô visit tối đa 1 lần
//   - m = rows, n = cols
//
// 📦 Space: O(m × n)
//   - Call stack trong worst case (spiral/snake island)
//   - Best case: O(min(m,n)) (linear island)
//
// 📊 Trace code (xem theory.md chi tiết)
//
// 🔄 Comparison: DFS vs BFS
//   |Approach|Time |Space|Use Case|
//   |DFS Flood|O(mn)|O(mn)|⭐ Recursive, simple |
//   |BFS     |O(mn)|O(mn)| Iterative |
//   |Union-F |O(mn)|O(mn)| Overkill |
//
// 🚨 Edge cases:
//   - grid = empty [] → return 0
//   - grid = all '0' → 0
//   - grid = all '1' → 1
// ⚠️ CRITICAL: Phải mark visited (thành '0')
//   Không thì infinite loop! (visit same cell forever)
//
// 💡 Variation: Surrounded Regions (LeetCode #130)
//   Dùng DFS từ border '0' để mark không bị surround
class NumberOfIslands {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int count = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Duyệt từng ô
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Gặp '1' chưa visit
                if (grid[r][c] == '1') {
                    count++;  // Đảo mới
                    dfs(grid, r, c);  // Tô toàn bộ đảo này
                }
            }
        }
        
        return count;
    }
    
    // DFS: tô tất cả '1' kết nối thành '0'
    private void dfs(char[][] grid, int r, int c) {
        // Boundary check + water check
        // Nếu out of bounds hoặc là '0' → return
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
            || grid[r][c] == '0') {
            return;
        }
        
        // Mark visited: '1' → '0'
        // (thay vì dùng separate visited set)
        grid[r][c] = '0';
        
        // Đệ quy 4 hướng
        dfs(grid, r + 1, c);  // Down
        dfs(grid, r - 1, c);  // Up
        dfs(grid, r, c + 1);  // Right
        dfs(grid, r, c - 1);  // Left
    }
}


// -----------------------------------------------
// Bài 2: Clone Graph (LeetCode #133) - Medium
// -----------------------------------------------
// Deep copy (clone) một undirected graph.
// Mỗi node có val (int) và list neighbors (List<Node>).
//
// Ví dụ:
//   Input: 1 — 2
//          |   |
//          4 — 3
//   
//   Output: Deep copy của graph (separate objects, same structure)
//
// 💡 Insight: HashMap mapping {original → cloned}
//   Để track đã clone node nào
//   Tránh infinite recursion trên cycle
//
// ✅ Approach: BFS + HashMap
// 1. Init: cloneMap[root] = new Node(root.val)
// 2. Queue BFS từ root
// 3. Cho mỗi node:
//    - Với mỗi neighbor:
//      - Nếu chưa cloned → clone node mới
//      - Kết nối cloned node
//
// ⏱️ Time: O(V + E)
//   V = # nodes, E = # edges
//   Mỗi node/edge visit 1 lần
//
// 📦 Space: O(V)
//   HashMap + Queue
//
// 📊 Trace code (xem theory.md chi tiết)
//
// 🔄 Comparison: BFS vs DFS
//   |Approach|Time |Space|Notes|
//   |BFS ⭐   |O(VE)|O(V) |Iterative, queue |
//   |DFS     |O(VE)|O(V) |Recursive, stack |
//   |Both same complexity, BFS slightly clearer|
//
// 🚨 Edge cases:
//   - node = null → return null
//   - node.neighbors = [] (isolated) → clone just node
//   - Graph has self-loop: 1.neighbors = [1]
//     Still works, HashMap handles it
// ⚠️ MISTAKE: Directly assign clones[u].neighbors = original[u].neighbors
//   → Tương đương deep copy fails! (still pointing to original)
//
// 💡 Alternative: DFS approach
//   ```java
//   public Node cloneGraph(Node node) {
//       Map<Node, Node> map = new HashMap<>();
//       return dfs(node, map);
//   }
//   private Node dfs(Node node, Map<Node, Node> map) {
//       if (node == null) return null;
//       if (map.containsKey(node)) return map.get(node);
//       
//       Node clone = new Node(node.val);
//       map.put(node, clone);
//       for (Node neighbor : node.neighbors) {
//           clone.neighbors.add(dfs(neighbor, map));
//       }
//       return clone;
//   }
//   ```
class CloneGraph {
    // Definition cho Node
    static class Node {
        public int val;
        public List<Node> neighbors;
        public Node(int val) {
            this.val = val;
            this.neighbors = new ArrayList<>();
        }
    }
    
    public Node cloneGraph(Node node) {
        // Edge case
        if (node == null) return null;
        
        // HashMap: original node → cloned node
        // Dùng để track đã clone chưa
        Map<Node, Node> cloneMap = new HashMap<>();
        
        // BFS setup
        Queue<Node> queue = new LinkedList<>();
        queue.offer(node);
        
        // Clone root node, thêm vào map
        cloneMap.put(node, new Node(node.val));
        
        // BFS duyệt graph
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            
            // Xử lý tất cả neighbors của current
            for (Node neighbor : current.neighbors) {
                // Nếu neighbor chưa cloned
                if (!cloneMap.containsKey(neighbor)) {
                    // Tạo cloned neighbor
                    cloneMap.put(neighbor, new Node(neighbor.val));
                    
                    // Thêm vào queue để xử lý sau
                    queue.offer(neighbor);
                }
                
                // Kết nối cloned current → cloned neighbor
                cloneMap.get(current).neighbors.add(
                    cloneMap.get(neighbor)
                );
            }
        }
        
        // Trả về cloned root
        return cloneMap.get(node);
    }
}


// -----------------------------------------------
// Bài 3: Course Schedule (LeetCode #207) - Medium
// -----------------------------------------------
// Có n courses (0...n-1) và prerequisites.
// prerequisites[i] = [a, b] = "phải học b trước a"
// Hỏi: có thể hoàn thành tất cả courses không?
//
// ⟺ Kiểm tra DAG (Directed Acyclic Graph) không có cycle
//
// Ví dụ 1:
//   numCourses = 4
//   prerequisites = [[1,0], [2,1], [3,2]]
//   → 0 → 1 → 2 → 3 (DAG, no cycle)
//   Output: true ✓
//
// Ví dụ 2:
//   numCourses = 2
//   prerequisites = [[1,0], [0,1]]
//   → 0 → 1 → 0 (cycle!)
//   Output: false ✗
//
// 💡 Insight: Topological Sort (DAG check)
//   Nếu có cycle → không thể sort topologically
//   Nếu không cycle → có topological order
//
// ✅ Approach: Topological Sort (Kahn's Algorithm - BFS)
// Kahn's = BFS-based topo sort:
// 1. Build adjacency list + inDegree[] (# prerequisites cho mỗi course)
// 2. Queue = tất cả courses với inDegree=0 (không depend on anything)
// 3. BFS:
//    - Poll course → processed++
//    - Giảm inDegree của courses depend on nó
//    - Thêm course vào queue nếu inDegree=0
// 4. Nếu processed == numCourses → no cycle → true
//
// ⏱️ Time: O(V + E)
//   V = # courses, E = # prerequisites
//   Duyệt mỗi course 1 lần, mỗi edge 1 lần
//
// 📦 Space: O(V + E)
//   Adjacency list + inDegree[] + queue
//
// 📊 Trace code (xem theory.md chi tiết)
//
// 🔄 Comparison: Kahn's vs DFS cycle detect
//   |Approach|Time |Space|Notes|
//   |Kahn's BFS ⭐|O(VE)|O(VE)|Explicit topo sort|
//   |DFS 3-color|O(VE)|O(V) |Color: white/gray/black|
//   Both detect cycle, Kahn's is more explicit
//
// 🚨 Edge cases:
//   - prerequisites = [] → true (no dependency)
//   - numCourses = 1 → true
//   - Single cycle: [[0,1], [1,0]]
//     → inDegree=[1,1], queue empty, processed=0 < 2 → false ✓
//   - Self-loop (unlikely): [[0,0]]
//     → inDegree[0]=1, queue empty, processed=0 < 1 → false ✓
//
// 💡 Alternative: DFS cycle detection
//   ```java
//   // Color: 0=white(unvisited), 1=gray(visiting), 2=black(done)
//   int[] state = new int[numCourses];
//   
//   for (int i = 0; i < numCourses; i++) {
//       if (hasCycle(i, state, graph)) return false;
//   }
//   return true;
//   
//   private boolean hasCycle(int node, int[] state, List<List<Integer>> graph) {
//       if (state[node] == 1) return true; // Gray = cycle
//       if (state[node] == 2) return false; // Black = ok
//       
//       state[node] = 1; // Mark gray (visiting)
//       for (int next : graph.get(node)) {
//           if (hasCycle(next, state, graph)) return true;
//       }
//       state[node] = 2; // Mark black (done)
//       return false;
//   }
//   ```
class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Edge case
        if (numCourses <= 0) return false;
        
        // Bước 1: Build adjacency list + inDegree[]
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        // Thêm edge: prerequisites[i] = [a, b] → b → a
        // graph[b].add(a)
        for (int[] pre : prerequisites) {
            int course = pre[0];          // phải học
            int prerequisite = pre[1];    // trước tiên
            
            graph.get(prerequisite).add(course);  // prerequisite → course
            inDegree[course]++;  // course phụ thuộc vào prerequisite
        }
        
        // Bước 2: Queue = tất cả courses với inDegree=0
        // (courses không depend on anything)
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        // Bước 3: Topological Sort (Kahn's Algorithm)
        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;  // Process course
            
            // Giảm inDegree của courses depend on nó
            for (int nextCourse : graph.get(course)) {
                inDegree[nextCourse]--;
                
                // Nếu inDegree=0, có thể học ngay
                if (inDegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }
        
        // Bước 4: Check result
        // Nếu process được tất cả → no cycle → true
        // Nếu process < numCourses → có cycle → false
        return processed == numCourses;
    }
}

