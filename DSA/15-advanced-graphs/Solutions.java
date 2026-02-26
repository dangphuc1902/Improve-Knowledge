import java.util.*;

/**
 * =============================================
 *  15 - ADVANCED GRAPHS
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Network Delay Time (LeetCode #743) - Medium
// -----------------------------------------------
// Có n nodes và weighted edges. Gửi signal từ node k.
// Tìm thời gian để TẤT CẢ nodes nhận được signal. Trả -1 nếu không thể.
// (= shortest path từ k đến node xa nhất)
//
// Approach: Dijkstra's Algorithm.
// BFS với PriorityQueue, luôn xử lý node có distance nhỏ nhất.
//
// Time: O((V + E) log V)
// Space: O(V + E)
class NetworkDelayTime {
    public int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list: node → [(neighbor, weight)]
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] edge : times) {
            graph.computeIfAbsent(edge[0], x -> new ArrayList<>())
                 .add(new int[]{edge[1], edge[2]});
        }

        // Dijkstra: Min Heap (distance, node)
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        minHeap.offer(new int[]{0, k}); // Start từ node k, distance = 0

        Map<Integer, Integer> dist = new HashMap<>(); // Shortest distance đến mỗi node

        while (!minHeap.isEmpty()) {
            int[] curr = minHeap.poll();
            int d = curr[0], node = curr[1];

            // Đã tìm được shortest path đến node này rồi → skip
            if (dist.containsKey(node)) continue;
            dist.put(node, d);

            // Relax neighbors
            if (graph.containsKey(node)) {
                for (int[] neighbor : graph.get(node)) {
                    int nextNode = neighbor[0], weight = neighbor[1];
                    if (!dist.containsKey(nextNode)) {
                        minHeap.offer(new int[]{d + weight, nextNode});
                    }
                }
            }
        }

        // Nếu chưa đến được tất cả nodes → return -1
        if (dist.size() != n) return -1;

        // Kết quả = max distance (node xa nhất)
        int maxDist = 0;
        for (int d : dist.values()) {
            maxDist = Math.max(maxDist, d);
        }
        return maxDist;
    }
}

// -----------------------------------------------
// Bài 2: Min Cost to Connect All Points (LeetCode #1584) - Medium
// -----------------------------------------------
// Cho n points, cost kết nối = Manhattan distance.
// Tìm min cost để connect TẤT CẢ points (= MST).
//
// Approach: Prim's Algorithm.
// BFS với Min Heap, mỗi bước thêm point gần nhất vào MST.
//
// Time: O(n² log n) - n² edges (complete graph)
// Space: O(n²)
class MinCostConnectPoints {
    public int minCostConnectAllPoints(int[][] points) {
        int n = points.length;
        boolean[] visited = new boolean[n];
        // Min Heap: (cost, pointIndex)
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        minHeap.offer(new int[]{0, 0}); // Bắt đầu từ point 0

        int totalCost = 0;
        int connected = 0;

        while (connected < n) {
            int[] curr = minHeap.poll();
            int cost = curr[0], point = curr[1];

            if (visited[point]) continue;
            visited[point] = true;
            totalCost += cost;
            connected++;

            // Thêm edges đến các unvisited points
            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
                    int dist = Math.abs(points[point][0] - points[i][0])
                             + Math.abs(points[point][1] - points[i][1]);
                    minHeap.offer(new int[]{dist, i});
                }
            }
        }

        return totalCost;
    }
}

// -----------------------------------------------
// Bài 3: Redundant Connection (LeetCode #684) - Medium
// -----------------------------------------------
// Cho tree + 1 edge thừa tạo thành cycle. Tìm edge thừa đó.
// Nếu nhiều đáp án, trả edge xuất hiện cuối cùng trong input.
//
// Approach: Union-Find.
// Duyệt từng edge: nếu 2 nodes đã cùng component → đây là edge thừa.
//
// Time: O(n × α(n)) ≈ O(n) - α là inverse Ackermann (gần O(1))
// Space: O(n)
class RedundantConnection {
    private int[] parent;
    private int[] rank;

    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        parent = new int[n + 1];
        rank = new int[n + 1];

        // Initialize: mỗi node là root của chính nó
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }

        for (int[] edge : edges) {
            // Nếu union thất bại → 2 nodes đã connected → edge thừa
            if (!union(edge[0], edge[1])) {
                return edge;
            }
        }

        return new int[]{};
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    private boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false; // Đã cùng component

        // Union by rank
        if (rank[px] < rank[py]) {
            parent[px] = py;
        } else if (rank[px] > rank[py]) {
            parent[py] = px;
        } else {
            parent[py] = px;
            rank[px]++;
        }
        return true;
    }
}
