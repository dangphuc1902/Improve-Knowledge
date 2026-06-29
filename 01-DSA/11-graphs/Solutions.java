import java.util.*;

/**
 * 11 - Graphs Solutions
 * Các bài: Number of Islands, Clone Graph, Course Schedule, BFS Shortest Path,
 *          Pacific Atlantic Water Flow, Walls and Gates
 */
public class Solutions {

    // ============================================================
    // LC 200 - Number of Islands
    // Approach 1: DFS mark visited — O(R*C), O(R*C) ⭐
    // Approach 2: BFS — O(R*C), O(R*C)
    // Approach 3: Union-Find — O(R*C * α(n))
    // ============================================================
    static class NumberOfIslands {
        // ⭐ Approach 1: DFS — modify grid in-place
        public int numIslands(char[][] grid) {
            int islands = 0;
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[0].length; c++) {
                    if (grid[r][c] == '1') {
                        dfs(grid, r, c);
                        islands++;
                    }
                }
            }
            return islands;
        }

        private void dfs(char[][] grid, int r, int c) {
            if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
            grid[r][c] = '0'; // mark visited
            dfs(grid, r + 1, c);
            dfs(grid, r - 1, c);
            dfs(grid, r, c + 1);
            dfs(grid, r, c - 1);
        }

        // Approach 2: BFS
        public int numIslandsBFS(char[][] grid) {
            int islands = 0;
            int[] dr = {1, -1, 0, 0};
            int[] dc = {0, 0, 1, -1};

            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[0].length; c++) {
                    if (grid[r][c] == '1') {
                        islands++;
                        Queue<int[]> queue = new LinkedList<>();
                        queue.offer(new int[]{r, c});
                        grid[r][c] = '0';

                        while (!queue.isEmpty()) {
                            int[] cell = queue.poll();
                            for (int d = 0; d < 4; d++) {
                                int nr = cell[0] + dr[d], nc = cell[1] + dc[d];
                                if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length
                                        && grid[nr][nc] == '1') {
                                    grid[nr][nc] = '0';
                                    queue.offer(new int[]{nr, nc});
                                }
                            }
                        }
                    }
                }
            }
            return islands;
        }
    }

    // ============================================================
    // LC 133 - Clone Graph
    // Approach 1: DFS + HashMap — O(V+E), O(V) ⭐
    // Approach 2: BFS + HashMap — O(V+E), O(V)
    // ============================================================
    static class CloneGraph {
        static class Node {
            int val;
            List<Node> neighbors;
            Node(int val) { this.val = val; neighbors = new ArrayList<>(); }
        }

        // ⭐ DFS + HashMap<original, clone>
        private Map<Node, Node> visited = new HashMap<>();

        public Node cloneGraph(Node node) {
            if (node == null) return null;
            if (visited.containsKey(node)) return visited.get(node);

            Node clone = new Node(node.val);
            visited.put(node, clone);

            for (Node neighbor : node.neighbors) {
                clone.neighbors.add(cloneGraph(neighbor));
            }
            return clone;
        }

        // Approach 2: BFS
        public Node cloneGraphBFS(Node node) {
            if (node == null) return null;
            Map<Node, Node> map = new HashMap<>();
            Queue<Node> queue = new LinkedList<>();

            map.put(node, new Node(node.val));
            queue.offer(node);

            while (!queue.isEmpty()) {
                Node curr = queue.poll();
                for (Node neighbor : curr.neighbors) {
                    if (!map.containsKey(neighbor)) {
                        map.put(neighbor, new Node(neighbor.val));
                        queue.offer(neighbor);
                    }
                    map.get(curr).neighbors.add(map.get(neighbor));
                }
            }
            return map.get(node);
        }
    }

    // ============================================================
    // LC 207 - Course Schedule (Cycle Detection in Directed Graph)
    // Approach 1: Kahn's Algorithm (BFS Topological Sort) — O(V+E) ⭐
    // Approach 2: DFS with 3-color marking — O(V+E)
    // ============================================================
    static class CourseSchedule {
        // ⭐ Approach 1: Kahn's BFS Topological Sort
        public boolean canFinish(int numCourses, int[][] prerequisites) {
            int[] inDegree = new int[numCourses];
            Map<Integer, List<Integer>> graph = new HashMap<>();

            for (int[] edge : prerequisites) {
                graph.computeIfAbsent(edge[1], k -> new ArrayList<>()).add(edge[0]);
                inDegree[edge[0]]++;
            }

            Queue<Integer> queue = new LinkedList<>();
            for (int i = 0; i < numCourses; i++) {
                if (inDegree[i] == 0) queue.offer(i);
            }

            int processed = 0;
            while (!queue.isEmpty()) {
                int node = queue.poll();
                processed++;
                for (int next : graph.getOrDefault(node, new ArrayList<>())) {
                    if (--inDegree[next] == 0) queue.offer(next);
                }
            }
            return processed == numCourses;
        }

        // Approach 2: DFS 3-color (WHITE=0, GRAY=1 in progress, BLACK=2 done)
        public boolean canFinishDFS(int numCourses, int[][] prerequisites) {
            Map<Integer, List<Integer>> graph = new HashMap<>();
            for (int[] edge : prerequisites) {
                graph.computeIfAbsent(edge[1], k -> new ArrayList<>()).add(edge[0]);
            }

            int[] color = new int[numCourses]; // 0=white, 1=gray, 2=black

            for (int i = 0; i < numCourses; i++) {
                if (color[i] == 0 && hasCycle(graph, color, i)) return false;
            }
            return true;
        }

        private boolean hasCycle(Map<Integer, List<Integer>> graph, int[] color, int node) {
            color[node] = 1; // mark as in-progress (GRAY)
            for (int next : graph.getOrDefault(node, new ArrayList<>())) {
                if (color[next] == 1) return true; // back edge → cycle!
                if (color[next] == 0 && hasCycle(graph, color, next)) return true;
            }
            color[node] = 2; // mark as done (BLACK)
            return false;
        }
    }

    // ============================================================
    // LC 210 - Course Schedule II (Return actual order)
    // Approach: Kahn's BFS Topological Sort — O(V+E) ⭐
    // ============================================================
    static class CourseScheduleII {
        public int[] findOrder(int numCourses, int[][] prerequisites) {
            int[] inDegree = new int[numCourses];
            Map<Integer, List<Integer>> graph = new HashMap<>();

            for (int[] edge : prerequisites) {
                graph.computeIfAbsent(edge[1], k -> new ArrayList<>()).add(edge[0]);
                inDegree[edge[0]]++;
            }

            Queue<Integer> queue = new LinkedList<>();
            for (int i = 0; i < numCourses; i++) {
                if (inDegree[i] == 0) queue.offer(i);
            }

            int[] order = new int[numCourses];
            int idx = 0;
            while (!queue.isEmpty()) {
                int node = queue.poll();
                order[idx++] = node;
                for (int next : graph.getOrDefault(node, new ArrayList<>())) {
                    if (--inDegree[next] == 0) queue.offer(next);
                }
            }
            return idx == numCourses ? order : new int[]{};
        }
    }

    // ============================================================
    // Graph Utilities: Union-Find
    // ============================================================
    static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        // Find with path compression — O(α(n)) ≈ O(1)
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        // Union by rank — O(α(n))
        boolean union(int x, int y) {
            int px = find(x), py = find(y);
            if (px == py) return false; // already in same component (cycle in undirected)
            if (rank[px] < rank[py]) parent[px] = py;
            else if (rank[px] > rank[py]) parent[py] = px;
            else { parent[py] = px; rank[px]++; }
            return true;
        }

        boolean connected(int x, int y) {
            return find(x) == find(y);
        }
    }

    // ============================================================
    // LC 684 - Redundant Connection (Union-Find)
    // ============================================================
    static class RedundantConnection {
        public int[] findRedundantConnection(int[][] edges) {
            int n = edges.length;
            UnionFind uf = new UnionFind(n + 1);

            for (int[] edge : edges) {
                if (!uf.union(edge[0], edge[1])) {
                    return edge; // này là cạnh dư thừa (tạo cycle)
                }
            }
            return new int[]{};
        }
    }
}
