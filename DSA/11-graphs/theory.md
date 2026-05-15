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

### Pattern 1: DFS/BFS on Adjacency List
- **Nó là gì?**: Duyệt qua các đỉnh của đồ thị bằng cách sử dụng đệ quy (DFS) hoặc hàng đợi (BFS) dựa trên danh sách kề (Adjacency List).
- **Giải quyết bài toán nào?**: 
    - Kiểm tra tính kết nối giữa hai đỉnh.
    - Tìm các thành phần liên thông (`Connected Components`).
    - Sao chép đồ thị (`Clone Graph`).
- **Ưu điểm**:
    - O(V + E) - cực kỳ hiệu quả cho đồ thị thưa (sparse graphs).
- **Nhược điểm**:
    - Cần một `Set` hoặc mảng `boolean` để theo dõi các đỉnh đã thăm (`visited`), nếu không sẽ bị lặp vô tận trong đồ thị có chu trình.
- **Sự thay thế**:
    - **Adjacency Matrix**: Dùng mảng 2 chiều (O(V²) space, O(V²) time).

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

### Pattern 2: DFS/BFS on Grid (Matrix Traversal)
- **Nó là gì?**: Coi mỗi ô trong ma trận là một đỉnh và các ô xung quanh (trên, dưới, trái, phải) là các cạnh.
- **Giải quyết bài toán nào?**: 
    - Đếm số lượng đảo (`Number of Islands`).
    - Tìm đường đi trong mê cung.
    - Loang màu (`Flood Fill`).
- **Ưu điểm**:
    - Không cần xây dựng danh sách kề tường minh, tiết kiệm thời gian và bộ nhớ.
- **Nhược điểm**:
    - Dễ bị lỗi chỉ số mảng (`IndexOutOfBounds`).
- **Sự thay thế**:
    - **Union-Find**: Nhóm các ô thuộc cùng một đảo.

```java
void dfs(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
        || grid[r][c] == '0') return;
    grid[r][c] = '0'; // Mark visited trực tiếp trên grid để tiết kiệm bộ nhớ
    dfs(grid, r + 1, c); dfs(grid, r - 1, c);
    dfs(grid, r, c + 1); dfs(grid, r, c - 1);
}
```

### Pattern 3: Topological Sort (Kahn's Algorithm)
- **Nó là gì?**: Sắp xếp các đỉnh của đồ thị có hướng không chu trình (DAG) sao cho với mọi cạnh (u, v), u luôn đứng trước v. Thuật toán Kahn sử dụng khái niệm `in-degree` (bậc vào).
- **Giải quyết bài toán nào?**: 
    - Lập lịch học các môn có điều kiện tiên quyết (`Course Schedule`).
    - Xác định thứ tự build của các thư viện phụ thuộc nhau.
- **Ưu điểm**:
    - Có thể đồng thời phát hiện chu trình trong đồ thị (nếu số đỉnh đã xử lý < tổng số đỉnh).
- **Nhược điểm**:
    - Chỉ hoạt động trên đồ thị có hướng và không có chu trình.
- **Sự thay thế**:
    - **DFS-based Topological Sort**: Sử dụng 3 màu (White, Gray, Black) để đánh dấu trạng thái đỉnh.

```java
// Dùng cho DAG: course schedule, build order
int[] inDegree = new int[n];
Queue<Integer> queue = new LinkedList<>();
// Thêm nodes có inDegree = 0 vào queue
// Poll, giảm inDegree của neighbors, thêm nếu inDegree = 0
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Number of Islands - step by step
```
Input Grid:
  1 1 0 0 0
  1 1 0 1 0
  0 0 1 0 1

Step 1: Duyệt (r=0,c=0)
  grid[0][0]='1' → count=1
  DFS từ (0,0):
    - Mark (0,0) = '0'
    - DFS(1,0): '1' → Mark (1,0)='0'
      - DFS(2,0): '0' → return
      - DFS(0,0): '0' (already marked) → return
      - DFS(1,1): '1' → Mark (1,1)='0'
        - DFS(2,1): '0' → return
        - DFS(0,1): '1' → Mark (0,1)='0'
          - DFS(1,1): '0' (marked) → return
          - DFS(-1,1): out of bounds → return
          - DFS(0,2): '0' → return
          - DFS(0,0): '0' (marked) → return
        - DFS(1,2): '0' → return
        - DFS(1,0): '0' (marked) → return
    - DFS(0,-1): out of bounds → return
    - DFS(0,1): '0' (marked) → return
  
  Sau DFS, grid:
  0 0 0 0 0
  0 0 0 1 0
  0 0 1 0 1

Step 2: Duyệt (0,1) → (0,4): tất cả '0'

Step 3: Duyệt (1,3)
  grid[1][3]='1' → count=2
  DFS từ (1,3):
    - Mark (1,3)='0'
    - DFS(2,3): '0' → return
    - (... other directions)

Step 4: Duyệt (2,2)
  grid[2][2]='1' → count=3
  DFS từ (2,2): (isolated)

Step 5: Duyệt (2,4)
  grid[2][4]='1' → count=4
  DFS từ (2,4): (isolated)

✅ Output: 4
```

**Insight:** DFS flood fill = explore tất cả cell kết nối

---

### Ví dụ 2: Clone Graph - step by step
```
Input Graph:
  1 — 2
  |   |
  4 — 3

Representation:
  1.neighbors = [2, 4]
  2.neighbors = [1, 3]
  3.neighbors = [2, 4]
  4.neighbors = [1, 3]

BFS:
  queue = [1], cloneMap = {1: new Node(1)}
  
  Iteration 1:
    current = 1, neighbors = [2, 4]
    - Check 2: not in cloneMap
      cloneMap[2] = new Node(2)
      queue = [2, 4]
      cloneMap[1].neighbors.add(cloneMap[2])
    - Check 4: not in cloneMap
      cloneMap[4] = new Node(4)
      cloneMap[1].neighbors.add(cloneMap[4])
  
  Iteration 2:
    current = 2, neighbors = [1, 3]
    - Check 1: in cloneMap
      cloneMap[2].neighbors.add(cloneMap[1])
    - Check 3: not in cloneMap
      cloneMap[3] = new Node(3)
      queue = [4, 3]
      cloneMap[2].neighbors.add(cloneMap[3])
  
  Iteration 3:
    current = 4, neighbors = [1, 3]
    - Check 1: in cloneMap
      cloneMap[4].neighbors.add(cloneMap[1])
    - Check 3: in cloneMap
      cloneMap[4].neighbors.add(cloneMap[3])
  
  Iteration 4:
    current = 3, neighbors = [2, 4]
    - Both in cloneMap
      cloneMap[3].neighbors = [cloneMap[2], cloneMap[4]]
  
  queue empty → return cloneMap[1]

✅ Output: Deep copy graph
```

**Insight:** HashMap track visited + cloned nodes

---

### Ví dụ 3: Course Schedule (Topological Sort) - step by step
```
Input:
  numCourses = 4
  prerequisites = [[1,0], [2,1], [3,2]]
  
  Meaning: 0 → 1 → 2 → 3 (DAG, linear)

Build Graph:
  graph[0] = [1]  (0 → 1)
  graph[1] = [2]  (1 → 2)
  graph[2] = [3]  (2 → 3)
  graph[3] = []
  
  inDegree = [0, 1, 1, 1]
  (0 không depend on anything,
   1 depends on 0,
   2 depends on 1,
   3 depends on 2)

Kahn's Algorithm:
  Step 1: Find all nodes with inDegree = 0
    queue = [0]
  
  Step 2: Process queue
    poll 0 → processed = 1
      neighbors = [1]
      inDegree[1]-- = 0
      queue = [1]
  
    poll 1 → processed = 2
      neighbors = [2]
      inDegree[2]-- = 0
      queue = [2]
  
    poll 2 → processed = 3
      neighbors = [3]
      inDegree[3]-- = 0
      queue = [3]
  
    poll 3 → processed = 4
      neighbors = []
      queue = []
  
  processed == numCourses? 4 == 4? YES
  return true ✓

Nếu có cycle:
  Input: [[1,0], [0,1]]
  graph[0] = [1]
  graph[1] = [0]
  inDegree = [1, 1]
  
  queue = [] (không có node with inDegree=0)
  processed = 0
  
  processed == 2? NO → return false (cycle detected) ✓
```

**Insight:** Topological sort chứng minh DAG không có cycle

---

## 🔄 So sánh các Approach

### Number of Islands: DFS vs BFS vs Union-Find
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| DFS Flood Fill ⭐ | O(m*n) | O(m*n) | Simple, intuitive |
| BFS | O(m*n) | O(m*n) | Can set depth limit |
| Union-Find | O(m*n) | O(m*n) | Overkill for this |

### Clone Graph: DFS vs BFS
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| DFS | O(V+E) | O(V) | Recursive |
| BFS ⭐ | O(V+E) | O(V) | Iterative, clearer |

### Course Schedule: Kahn's vs DFS cycle detect
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Kahn's BFS ⭐ | O(V+E) | O(V) | Explicit topo sort |
| DFS + color | O(V+E) | O(V) | Color: white/gray/black |

---

## 🚨 Edge Cases & Common Mistakes

```java
// Number of Islands:
// 1. grid = empty → 0
// 2. grid = all '0' → 0
// 3. grid = all '1' → 1 (single island)
// ⚠️ MISTAKE: Quên mark visited
//   → Infinite loop! (visit same cell forever)

// Clone Graph:
// 1. node = null → return null
// 2. node.neighbors = [] (isolated) → clone just that
// 3. Graph has self-loop: 1.neighbors = [1]
//   Still works, HashMap handles it
// ⚠️ MISTAKE: Directly assign cloned.neighbors = original.neighbors
//   → Still shares reference!

// Course Schedule:
// 1. prerequisites = [] → true (no dependency)
// 2. numCourses = 1 → true
// 3. prerequisites = [[0,1], [1,0]] (cycle)
//   → inDegree = [1,1], queue empty, processed=0 < 2 → false
// ⚠️ MISTAKE: DFS without visited set
//   → Infinite recursion on cycle!
```

## 💡 Built-in pattern recognition

1. **Grid problem?** → DFS 4/8 directions
2. **Connected components?** → Union-Find hoặc DFS/BFS
3. **Shortest path (unweighted)?** → BFS
4. **DAG + scheduling?** → Topological sort (Kahn's hoặc DFS)
5. **Cycle detection directed?** → DFS 3-color hoặc Kahn's
6. **Tree problem?** → DFS/BFS, không cần visited (DAG)

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
