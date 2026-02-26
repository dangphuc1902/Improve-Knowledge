# 11 - Graphs

## 📖 Tổng quan

**Graph** gồm **nodes** (đỉnh) và **edges** (cạnh) kết nối chúng. Là cấu trúc dữ liệu tổng quát nhất — tree là special case, linked list cũng vậy.

### Phân loại
| Loại | Đặc điểm |
|------|----------|
| Directed / Undirected | Cạnh có hướng / không hướng |
| Weighted / Unweighted | Cạnh có trọng số / không |
| Cyclic / Acyclic | Có chu trình / không |
| DAG | Directed Acyclic Graph (phổ biến trong scheduling) |

### Biểu diễn Graph
```java
// 1. Adjacency List (phổ biến nhất)
Map<Integer, List<Integer>> graph = new HashMap<>();

// 2. Adjacency Matrix
int[][] matrix = new int[n][n]; // matrix[i][j] = 1 nếu có edge

// 3. Edge List
int[][] edges; // edges[i] = {from, to, weight}
```

## 🔍 Khi nào sử dụng?

- Bài có **grid** (matrix 2D) → coi mỗi ô là node
- Bài có **connections/relationships** giữa các phần tử
- **Shortest path**, **connected components**
- **Course schedule** (prerequisites = DAG)
- **Social network**, **network flow**

## 📝 Các Pattern phổ biến

### Pattern 1: DFS trên Graph
```java
Set<Integer> visited = new HashSet<>();
void dfs(int node, Map<Integer, List<Integer>> graph) {
    if (visited.contains(node)) return;
    visited.add(node);
    for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
        dfs(neighbor, graph);
    }
}
```

### Pattern 2: BFS trên Graph
```java
Queue<Integer> queue = new LinkedList<>();
Set<Integer> visited = new HashSet<>();
queue.offer(start);
visited.add(start);
while (!queue.isEmpty()) {
    int node = queue.poll();
    for (int neighbor : graph.get(node)) {
        if (!visited.contains(neighbor)) {
            visited.add(neighbor);
            queue.offer(neighbor);
        }
    }
}
```

### Pattern 3: DFS trên Grid (Number of Islands)
```java
void dfs(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
        || grid[r][c] == '0') return;
    grid[r][c] = '0'; // Mark visited
    dfs(grid, r + 1, c); dfs(grid, r - 1, c);
    dfs(grid, r, c + 1); dfs(grid, r, c - 1);
}
```

### Pattern 4: Topological Sort (BFS - Kahn's)
```java
// Dùng cho DAG: course schedule, build order
int[] inDegree = new int[n];
Queue<Integer> queue = new LinkedList<>();
// Thêm nodes có inDegree = 0 vào queue
// Poll, giảm inDegree của neighbors, thêm nếu inDegree = 0
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| DFS/BFS | O(V + E) | O(V) |
| DFS trên Grid | O(m × n) | O(m × n) worst |
| Topological Sort | O(V + E) | O(V) |

## 💡 Tips phỏng vấn

1. **Grid = Graph**: Grid m×n = graph với m×n nodes, each connects to 4 neighbors
2. **Visited set**: BẮT BUỘC có — không có = infinite loop
3. **DFS vs BFS**: DFS cho connected components, BFS cho shortest path (unweighted)
4. **Build graph first**: Đọc input → build adjacency list → rồi mới BFS/DFS
