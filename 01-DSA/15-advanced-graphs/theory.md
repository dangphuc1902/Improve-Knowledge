# 15 - Advanced Graphs

## 📖 Tổng quan

**Advanced Graphs** bao gồm các thuật toán xử lý **weighted graphs**, tìm **shortest path** (Dijkstra, Bellman-Ford), **Minimum Spanning Tree** (Prim, Kruskal), và các thuật toán nâng cao khác.

> **Ý tưởng cốt lõi:** Dijkstra = BFS + Heap (greedy chọn node gần nhất). Kruskal = Union-Find + Sort edges (greedy chọn edge nhỏ nhất).

## 🧠 Kiến thức cốt lõi

### Tổng quan thuật toán

| Thuật toán | Bài toán | Time | Space |
|-----------|---------|------|-------|
| **Dijkstra** | Shortest path (non-negative) | O((V+E) log V) | O(V+E) |
| **Bellman-Ford** | Shortest path (with negative) | O(V*E) | O(V) |
| **Floyd-Warshall** | All-pairs shortest path | O(V³) | O(V²) |
| **Prim's** | Minimum Spanning Tree | O(E log V) | O(V+E) |
| **Kruskal's** | Minimum Spanning Tree | O(E log E) | O(V+E) |

### Khi nào dùng thuật toán nào?

| Điều kiện | Thuật toán |
|-----------|-----------|
| Unweighted graph → shortest path | BFS |
| Weighted, non-negative → single source | Dijkstra |
| Weighted, negative edges → single source | Bellman-Ford |
| All pairs shortest path | Floyd-Warshall |
| Minimum Spanning Tree | Prim's / Kruskal's |

## 🔍 Khi nào sử dụng?

- Bài toán **shortest path** trong weighted graph
- **Network flow**, min-cost routing
- **Clustering** (MST)
- Bài toán có từ: *"cheapest"*, *"shortest"*, *"minimum cost"*, *"network"*, *"flight"*

## 📝 Các Pattern phổ biến

### Pattern 1: Dijkstra's Algorithm
- **Nó là gì?**: BFS với Min-Heap (Priority Queue). Luôn xử lý node có dist nhỏ nhất trước.
- **Điều kiện**: Edge weights phải **non-negative**.
- **Giải quyết bài toán nào?**: Network Delay Time (LC 743), Cheapest Flights Within K Stops (LC 787, modified), Path With Minimum Effort.
- **Ưu điểm**: O((V+E) log V) — hiệu quả với sparse graph.
- **Nhược điểm**: Không work với negative weights.

```java
// Dijkstra — Single Source Shortest Path
Map<Integer, List<int[]>> graph = new HashMap<>(); // graph[u] = [(v, weight), ...]
int[] dist = new int[n];
Arrays.fill(dist, Integer.MAX_VALUE);
dist[src] = 0;

PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]); // [dist, node]
pq.offer(new int[]{0, src});

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
```

### Pattern 2: Bellman-Ford
- **Nó là gì?**: Relax tất cả edges V-1 lần. Lần V detect cycle âm.
- **Điều kiện**: Xử lý được **negative edges**, detect **negative cycle**.
- **Giải quyết bài toán nào?**: Cheapest Flights (with K stops), Negative Cycle Detection.

```java
// Bellman-Ford — giải phiên bản "K stops"
int[] prices = new int[n];
Arrays.fill(prices, Integer.MAX_VALUE);
prices[src] = 0;

for (int i = 0; i < k + 1; i++) { // k+1 iterations for k stops
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
```

### Pattern 3: Kruskal's MST (Union-Find)
- **Nó là gì?**: Sort edges by weight. Greedily add edge nếu không tạo cycle (Union-Find check).
- **Giải quyết bài toán nào?**: Min Cost to Connect All Points, Network Delay, Connecting Cities.

```java
// Kruskal's — sort edges, use Union-Find
Arrays.sort(edges, (a, b) -> a[2] - b[2]); // sort by weight
UnionFind uf = new UnionFind(n);
int mstCost = 0, edgesUsed = 0;

for (int[] edge : edges) {
    if (uf.union(edge[0], edge[1])) { // không tạo cycle
        mstCost += edge[2];
        edgesUsed++;
        if (edgesUsed == n - 1) break; // MST hoàn chỉnh
    }
}
return edgesUsed == n - 1 ? mstCost : -1;
```

### Pattern 4: Floyd-Warshall (All Pairs)
- **Nó là gì?**: 3 vòng lặp k, i, j. `dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])`.
- **Giải quyết bài toán nào?**: Find City With Smallest Number of Neighbors (LC 1334).

```java
// Floyd-Warshall — All pairs shortest path
int[][] dist = new int[n][n];
// Khởi tạo với input edges, ∞ nếu không có cạnh
for (int k = 0; k < n; k++) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (dist[i][k] != INF && dist[k][j] != INF) {
                dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
            }
        }
    }
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Dijkstra — Network Delay Time

```
n=4, times=[[2,1,1],[2,3,1],[3,4,1]], k=2 (source=2)

Graph: 2→1(1), 2→3(1), 3→4(1)
dist = [∞, ∞, 0, ∞, ∞] (1-indexed, src=2)

PQ: [(0, 2)]

Process (0, 2):
  Neighbors: (1, w=1), (3, w=1)
  dist[1] = 0+1 = 1 → PQ: [(1,1), (1,3)]
  dist[3] = 0+1 = 1

Process (1, 1): node 1, no outgoing edges
  dist[1] = 1 confirmed

Process (1, 3): node 3
  Neighbor: (4, w=1)
  dist[4] = 1+1 = 2 → PQ: [(2,4)]

Process (2, 4): node 4
  dist[4] = 2 confirmed

dist = [∞, 1, 0, 1, 2]
max(dist[1..4]) = max(1, 0, 1, 2) = 2

✅ Output: 2
```

### Ví dụ 2: Bellman-Ford — Cheapest Flights with K Stops

```
n=3, flights=[[0,1,100],[1,2,100],[0,2,500]], src=0, dst=2, k=1

prices = [0, ∞, ∞]

Iteration 1 (k+1=2 iterations, i=0):
  [0,1,100]: prices[0]=0 + 100 = 100 < temp[1]=∞ → temp[1]=100
  [1,2,100]: prices[1]=∞ → skip
  [0,2,500]: prices[0]=0 + 500 = 500 < temp[2]=∞ → temp[2]=500
  prices = [0, 100, 500]

Iteration 2 (i=1):
  [0,1,100]: prices[0]+100=100. temp[1] already=100 → no change
  [1,2,100]: prices[1]=100 + 100 = 200 < temp[2]=500 → temp[2]=200
  [0,2,500]: 500 > 200 → no change
  prices = [0, 100, 200]

✅ Output: 200 (0→1→2, 1 stop)
```

## 🔄 So sánh các Approach

### Shortest Path Algorithms

| Algorithm | Time | Space | Negative Edges | All Pairs |
|-----------|------|-------|----------------|-----------|
| BFS | O(V+E) | O(V) | ❌ (unweighted) | ❌ |
| **Dijkstra ⭐** | O((V+E)logV) | O(V) | ❌ | ❌ |
| Bellman-Ford | O(VE) | O(V) | ✅ | ❌ |
| Floyd-Warshall | O(V³) | O(V²) | ✅ | ✅ |

## 🚨 Edge Cases cần chú ý

```java
// Dijkstra:
// 1. src == dst → 0
// 2. Không có đường đi → ∞ / -1
// 3. Negative edges → Dijkstra sai! Dùng Bellman-Ford

// Bellman-Ford with K stops:
// 1. k = 0 → chỉ direct flight
// 2. Không thể đến dst → -1

// Kruskal's:
// 1. Disconnected graph → không thể tạo MST
// 2. edgesUsed != n-1 → graph không connected
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Dijkstra | O((V+E) log V) | O(V+E) |
| Bellman-Ford | O(V * E) | O(V) |
| Kruskal's MST | O(E log E) | O(V+E) |
| Floyd-Warshall | O(V³) | O(V²) |

## 💡 Tips phỏng vấn

1. **Dijkstra pitfall**: Luôn check `if (d > dist[node]) continue` để skip stale entries.
2. **Bellman-Ford với K stops**: Dùng **temp array** để không dùng kết quả của iteration hiện tại.
3. **Kruskal's = MST**: Cần `edgesUsed == n-1` để xác nhận graph connected.
4. **Negative cycle**: Nếu iteration thứ V vẫn relax → negative cycle detected.
5. **Dijkstra vs BFS**: Dijkstra = BFS + Priority Queue. Khi weights đều bằng 1 → BFS là đủ (và nhanh hơn).
