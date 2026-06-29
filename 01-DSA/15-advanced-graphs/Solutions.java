import java.util.*;

/**
 * 15 - Advanced Graphs Solutions
 * Các bài: Dijkstra (Network Delay), Bellman-Ford (Cheapest Flights K Stops),
 *          Kruskal's MST (Min Cost Connect Points), Floyd-Warshall
 */
public class Solutions {

    // ============================================================
    // LC 743 - Network Delay Time (Dijkstra)
    // Approach 1: Dijkstra + Min-Heap — O((V+E) log V) ⭐
    // Approach 2: Bellman-Ford — O(V*E)
    // ============================================================
    static class NetworkDelayTime {
        // ⭐ Dijkstra — Single Source Shortest Path
        public int networkDelayTime(int[][] times, int n, int k) {
            // Build adjacency list
            Map<Integer, List<int[]>> graph = new HashMap<>();
            for (int[] time : times) {
                graph.computeIfAbsent(time[0], x -> new ArrayList<>())
                     .add(new int[]{time[1], time[2]}); // [to, weight]
            }

            int[] dist = new int[n + 1];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[k] = 0;

            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]); // [dist, node]
            pq.offer(new int[]{0, k});

            while (!pq.isEmpty()) {
                int[] curr = pq.poll();
                int d = curr[0], node = curr[1];
                if (d > dist[node]) continue; // stale entry

                for (int[] neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                    int next = neighbor[0], weight = neighbor[1];
                    if (dist[node] + weight < dist[next]) {
                        dist[next] = dist[node] + weight;
                        pq.offer(new int[]{dist[next], next});
                    }
                }
            }

            int maxDist = 0;
            for (int i = 1; i <= n; i++) {
                if (dist[i] == Integer.MAX_VALUE) return -1;
                maxDist = Math.max(maxDist, dist[i]);
            }
            return maxDist;
        }
    }

    // ============================================================
    // LC 787 - Cheapest Flights Within K Stops (Bellman-Ford modified)
    // Approach 1: Bellman-Ford with K iterations — O(k * E) ⭐
    // Approach 2: Dijkstra modified (state = [cost, node, stops]) — O(k*E log V)
    // ============================================================
    static class CheapestFlightsKStops {
        // ⭐ Bellman-Ford with K+1 iterations
        // Key: dùng temp array để không "chain" updates trong cùng 1 iteration
        public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
            int[] prices = new int[n];
            Arrays.fill(prices, Integer.MAX_VALUE);
            prices[src] = 0;

            for (int i = 0; i < k + 1; i++) { // k stops = k+1 edges
                int[] temp = Arrays.copyOf(prices, n);
                for (int[] flight : flights) {
                    int u = flight[0], v = flight[1], w = flight[2];
                    if (prices[u] != Integer.MAX_VALUE && prices[u] + w < temp[v]) {
                        temp[v] = prices[u] + w;
                    }
                }
                prices = temp;
            }
            return prices[dst] == Integer.MAX_VALUE ? -1 : prices[dst];
        }

        // Approach 2: Dijkstra với state (cost, node, stops)
        public int findCheapestPriceDijkstra(int n, int[][] flights, int src, int dst, int k) {
            Map<Integer, List<int[]>> graph = new HashMap<>();
            for (int[] f : flights) {
                graph.computeIfAbsent(f[0], x -> new ArrayList<>()).add(new int[]{f[1], f[2]});
            }

            // PQ: [cost, node, stops]
            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
            pq.offer(new int[]{0, src, 0});
            int[] minStops = new int[n];
            Arrays.fill(minStops, Integer.MAX_VALUE);

            while (!pq.isEmpty()) {
                int[] curr = pq.poll();
                int cost = curr[0], node = curr[1], stops = curr[2];

                if (node == dst) return cost;
                if (stops > k || stops >= minStops[node]) continue;
                minStops[node] = stops;

                for (int[] next : graph.getOrDefault(node, new ArrayList<>())) {
                    pq.offer(new int[]{cost + next[1], next[0], stops + 1});
                }
            }
            return -1;
        }
    }

    // ============================================================
    // LC 1584 - Min Cost to Connect All Points (Kruskal's MST)
    // Approach 1: Kruskal's + Union-Find — O(n² log n) ⭐
    // Approach 2: Prim's — O(n²)
    // ============================================================
    static class MinCostConnectPoints {
        // ⭐ Kruskal's MST
        public int minCostConnectPoints(int[][] points) {
            int n = points.length;
            // Build all edges
            List<int[]> edges = new ArrayList<>(); // [weight, u, v]
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int dist = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
                    edges.add(new int[]{dist, i, j});
                }
            }
            edges.sort((a, b) -> a[0] - b[0]); // sort by weight

            UnionFind uf = new UnionFind(n);
            int totalCost = 0, edgesUsed = 0;

            for (int[] edge : edges) {
                if (uf.union(edge[1], edge[2])) {
                    totalCost += edge[0];
                    edgesUsed++;
                    if (edgesUsed == n - 1) break;
                }
            }
            return totalCost;
        }

        // Prim's Algorithm — O(n²), no Union-Find needed
        public int minCostConnectPointsPrim(int[][] points) {
            int n = points.length;
            int[] minDist = new int[n];
            Arrays.fill(minDist, Integer.MAX_VALUE);
            boolean[] inMST = new boolean[n];
            minDist[0] = 0;
            int totalCost = 0;

            for (int i = 0; i < n; i++) {
                // Find unvisited node with min dist
                int u = -1;
                for (int j = 0; j < n; j++) {
                    if (!inMST[j] && (u == -1 || minDist[j] < minDist[u])) u = j;
                }
                inMST[u] = true;
                totalCost += minDist[u];

                // Update distances
                for (int v = 0; v < n; v++) {
                    if (!inMST[v]) {
                        int dist = Math.abs(points[u][0] - points[v][0]) + Math.abs(points[u][1] - points[v][1]);
                        minDist[v] = Math.min(minDist[v], dist);
                    }
                }
            }
            return totalCost;
        }

        static class UnionFind {
            int[] parent, rank;
            UnionFind(int n) {
                parent = new int[n]; rank = new int[n];
                for (int i = 0; i < n; i++) parent[i] = i;
            }
            int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }
            boolean union(int x, int y) {
                int px = find(x), py = find(y);
                if (px == py) return false;
                if (rank[px] < rank[py]) parent[px] = py;
                else if (rank[px] > rank[py]) parent[py] = px;
                else { parent[py] = px; rank[px]++; }
                return true;
            }
        }
    }

    // ============================================================
    // LC 1334 - Find the City With the Smallest Number of Neighbors (Floyd-Warshall)
    // Approach: Floyd-Warshall — O(n³) time, O(n²) space ⭐
    // ============================================================
    static class FindCitySmallestNeighbors {
        public int findTheCity(int n, int[][] edges, int distanceThreshold) {
            int INF = Integer.MAX_VALUE / 2;
            int[][] dist = new int[n][n];
            for (int[] row : dist) Arrays.fill(row, INF);
            for (int i = 0; i < n; i++) dist[i][i] = 0;
            for (int[] edge : edges) {
                dist[edge[0]][edge[1]] = edge[2];
                dist[edge[1]][edge[0]] = edge[2];
            }

            // Floyd-Warshall
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }

            int result = -1, minCount = n + 1;
            for (int i = 0; i < n; i++) {
                int count = 0;
                for (int j = 0; j < n; j++) {
                    if (i != j && dist[i][j] <= distanceThreshold) count++;
                }
                if (count <= minCount) { // prefer larger city index
                    minCount = count;
                    result = i;
                }
            }
            return result;
        }
    }
}
