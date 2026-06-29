# 11 - Graphs

## 📖 Tổng quan

**Graph** là cấu trúc dữ liệu gồm **vertices (đỉnh)** và **edges (cạnh)**. Cạnh có thể có hướng (Directed) hoặc vô hướng (Undirected), có trọng số (Weighted) hoặc không.

> **Ý tưởng cốt lõi:** BFS tìm đường đi ngắn nhất (unweighted). DFS khám phá sâu, phát hiện chu trình, topological sort. Chọn đúng thuật toán dựa vào loại bài toán.

## 🧠 Kiến thức cốt lõi

### Biểu diễn Graph trong Java

```java
// Adjacency List — phổ biến nhất
Map<Integer, List<Integer>> graph = new HashMap<>();
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);

// Adjacency Matrix (cho dense graph)
int[][] matrix = new int[n][n];
matrix[u][v] = 1; // edge u → v

// Edge List
int[][] edges = {{0,1}, {1,2}, {2,3}};
```

### Complexity

| Thuật toán | Time | Space |
|-----------|------|-------|
| BFS | O(V + E) | O(V) |
| DFS | O(V + E) | O(V) |
| Topological Sort | O(V + E) | O(V) |
| Dijkstra (PQ) | O((V+E) log V) | O(V) |
| Union-Find | O(α(n)) ≈ O(1) | O(n) |

## 🔍 Khi nào sử dụng?

- **BFS**: Shortest path (unweighted), level-order traversal, flood fill
- **DFS**: Cycle detection, topological sort, connected components, path existence
- **Topological Sort**: Dependency ordering (course schedule)
- **Union-Find**: Connected components, cycle detection in undirected graph
- **Dijkstra**: Shortest path (weighted, non-negative edges)
- Cụm từ: *"shortest path"*, *"connected"*, *"cycle"*, *"course schedule"*, *"islands"*

## 📝 Các Pattern phổ biến

### Pattern 1: BFS — Level-by-Level Traversal
- **Nó là gì?**: Dùng Queue, duyệt theo từng "tầng" (level). Đảm bảo tìm đường ngắn nhất trong unweighted graph.
- **Giải quyết bài toán nào?**: Shortest Path, Word Ladder, Rotten Oranges, Binary Tree Level Order.
- **Ưu điểm**: Đảm bảo shortest path trong unweighted graph.
- **Nhược điểm**: O(V) space — có thể lớn hơn DFS.

```java
// BFS Template
Queue<Integer> queue = new LinkedList<>();
Set<Integer> visited = new HashSet<>();
queue.offer(start);
visited.add(start);
int level = 0;

while (!queue.isEmpty()) {
    int size = queue.size();
    for (int i = 0; i < size; i++) { // process one level
        int node = queue.poll();
        // process node
        for (int neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
    level++;
}
```

### Pattern 2: DFS — Deep Exploration
- **Nó là gì?**: Đi sâu trước, backtrack khi không còn đường đi. Dùng recursion hoặc explicit stack.
- **Giải quyết bài toán nào?**: Number of Islands (LC 200), Clone Graph, Path Sum, Cycle Detection.
- **Ưu điểm**: O(max depth) space — tốt hơn BFS nếu graph sâu nhưng hẹp.
- **Sự thay thế**: BFS — nếu cần level information.

```java
// DFS Recursive Template
void dfs(int node, Set<Integer> visited, Map<Integer, List<Integer>> graph) {
    visited.add(node);
    for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
        if (!visited.contains(neighbor)) {
            dfs(neighbor, visited, graph);
        }
    }
}

// DFS Iterative (dùng Stack thay vì recursion)
Deque<Integer> stack = new ArrayDeque<>();
stack.push(start);
while (!stack.isEmpty()) {
    int node = stack.pop();
    if (visited.contains(node)) continue;
    visited.add(node);
    for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
        stack.push(neighbor);
    }
}
```

### Pattern 3: Topological Sort
- **Nó là gì?**: Sắp xếp nodes sao cho mọi edge đều đi từ trái sang phải. Chỉ áp dụng cho DAG (Directed Acyclic Graph).
- **Giải quyết bài toán nào?**: Course Schedule (LC 207/210), Build Order, Task Dependencies.
- **Hai cách**: Kahn's Algorithm (BFS, in-degree) hoặc DFS-based.

```java
// Kahn's Algorithm — BFS + In-degree
int[] inDegree = new int[numCourses];
Map<Integer, List<Integer>> graph = new HashMap<>();
for (int[] edge : prerequisites) {
    graph.computeIfAbsent(edge[1], k -> new ArrayList<>()).add(edge[0]);
    inDegree[edge[0]]++;
}

Queue<Integer> queue = new LinkedList<>();
for (int i = 0; i < numCourses; i++) if (inDegree[i] == 0) queue.offer(i);

int count = 0;
while (!queue.isEmpty()) {
    int node = queue.poll();
    count++;
    for (int next : graph.getOrDefault(node, new ArrayList<>())) {
        if (--inDegree[next] == 0) queue.offer(next);
    }
}
return count == numCourses; // false nếu có cycle
```

### Pattern 4: Union-Find (Disjoint Set Union)
- **Nó là gì?**: Cấu trúc dữ liệu track tập hợp rời nhau. `find(x)` → root của x. `union(x,y)` → merge 2 tập hợp.
- **Giải quyết bài toán nào?**: Number of Connected Components, Graph Valid Tree, Redundant Connection.
- **Ưu điểm**: Gần O(1) với path compression + union by rank.

```java
class UnionFind {
    int[] parent, rank;

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }

    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false; // already connected (cycle!)
        if (rank[px] < rank[py]) parent[px] = py;
        else if (rank[px] > rank[py]) parent[py] = px;
        else { parent[py] = px; rank[px]++; }
        return true;
    }
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Number of Islands — DFS Dry Run

```
grid = [
  ["1","1","0","0","0"],
  ["1","1","0","0","0"],
  ["0","0","1","0","0"],
  ["0","0","0","1","1"]
]

(0,0)='1': DFS → đánh dấu tất cả '1' liên kết
  Mark (0,0), (0,1), (1,0), (1,1) = '0'
  islands=1

Scan tiếp → (2,2)='1': DFS → mark (2,2) = '0'
  islands=2

Scan tiếp → (3,3)='1': DFS → mark (3,3), (3,4) = '0'
  islands=3

✅ Output: 3
```

### Ví dụ 2: Course Schedule — Topological Sort

```
numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]
Graph: 0→1, 0→2, 1→3, 2→3
InDegree: [0, 1, 1, 2]

Queue start (inDegree=0): [0]

Process 0: count=1, update neighbors 1,2
  inDegree[1]=0 → add to queue
  inDegree[2]=0 → add to queue
  Queue: [1, 2]

Process 1: count=2, update 3
  inDegree[3]=1 (was 2)
  Queue: [2]

Process 2: count=3, update 3
  inDegree[3]=0 → add to queue
  Queue: [3]

Process 3: count=4
  Queue: []

count=4 == numCourses=4 → ✅ NO CYCLE, can finish all
```

## 🔄 So sánh các Approach

### Shortest Path: BFS vs Dijkstra vs Bellman-Ford

| Approach | Time | Space | Khi dùng |
|----------|------|-------|----------|
| **BFS ⭐** | O(V+E) | O(V) | Unweighted graph |
| **Dijkstra ⭐** | O((V+E) log V) | O(V) | Weighted, non-negative |
| Bellman-Ford | O(V*E) | O(V) | Negative edges |

### Cycle Detection: DFS vs Union-Find

| Approach | Time | Space | Khi dùng |
|----------|------|-------|----------|
| **DFS ⭐** | O(V+E) | O(V) | Directed graph |
| **Union-Find ⭐** | O(α(n)) | O(n) | Undirected graph |

## 🚨 Edge Cases cần chú ý

```java
// Number of Islands:
// 1. Grid rỗng → 0
// 2. Grid toàn 0 → 0
// 3. Grid toàn 1 → 1

// Course Schedule:
// 1. Không có prerequisites → true (0 courses needed)
// 2. Self-loop: [[0,0]] → false (cycle)
// 3. Disconnected components → vẫn work (mỗi component xử lý độc lập)

// Union-Find:
// 1. n=1 → không cần union
// 2. Same edge repeated → union trả về false (đã connected)
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Number of Islands | O(R*C) | O(R*C) |
| Clone Graph | O(V+E) | O(V) |
| Course Schedule | O(V+E) | O(V+E) |
| BFS Shortest Path | O(V+E) | O(V) |
| Dijkstra | O((V+E) log V) | O(V) |
| Union-Find (amortized) | O(α(n)) | O(n) |

## 💡 Tips phỏng vấn

1. **BFS vs DFS**: BFS → shortest path, DFS → existence/connectivity.
2. **Topological Sort**: Nếu có cycle → không có topological order → return false/empty.
3. **Grid DFS**: 4-directional (up/down/left/right) với boundary check.
4. **Visited set**: Luôn cần visited set để tránh infinite loop trong graph có cycle.
5. **Union-Find template**: Học thuộc `find` với path compression và `union` với rank.
