# 15 - Advanced Graphs

## 📖 Tổng quan

**Advanced Graphs** bao gồm các thuật toán đồ thị nâng cao: tìm đường ngắn nhất với trọng số, cây khung nhỏ nhất (MST), và các bài toán phức tạp hơn.

## 🧠 Kiến thức cốt lõi

### Dijkstra's Algorithm (Shortest Path - weighted, no negative)
- Dùng **Min Heap** (PriorityQueue)
- Greedy: luôn xử lý node có distance nhỏ nhất
- Time: O((V + E) log V)

### Prim's Algorithm (Minimum Spanning Tree)
- Dùng **Min Heap** chọn edge nhỏ nhất kết nối cây hiện tại
- Tương tự Dijkstra nhưng so sánh edge weight, không phải tổng distance
- Time: O((V + E) log V)

### Kruskal's Algorithm (MST)
- Sort tất cả edges theo weight
- Dùng **Union-Find** thêm edge nếu không tạo cycle
- Time: O(E log E)

### Union-Find (Disjoint Set Union)
```java
class UnionFind {
    int[] parent, rank;
    UnionFind(int n) {
        parent = new int[n]; rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // Path compression
        return parent[x];
    }
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) parent[px] = py;
        else if (rank[px] > rank[py]) parent[py] = px;
        else { parent[py] = px; rank[px]++; }
        return true;
    }
}
```

## 🔍 Khi nào sử dụng?

- **Shortest path** trong weighted graph → Dijkstra
- **MST** (minimum cost to connect all nodes) → Prim/Kruskal
- Bài cần **Union-Find** (connected components, cycle detection)
- **Network delay**, **cheapest flights** → Dijkstra variant

## ⏱️ Complexity thường gặp

| Algorithm | Time | Space |
|-----------|------|-------|
| Dijkstra (heap) | O((V+E) log V) | O(V) |
| Prim (heap) | O((V+E) log V) | O(V) |
| Kruskal | O(E log E) | O(V) |
| Union-Find | O(α(n)) per op | O(V) |

## 💡 Tips phỏng vấn

1. **Dijkstra**: KHÔNG dùng được với negative weights → dùng Bellman-Ford
2. **Prim vs Kruskal**: Dense graph → Prim, Sparse graph → Kruskal
3. **Union-Find**: Path compression + union by rank → nearly O(1) per operation
4. **Relaxation**: Dijkstra "relax" edges — cập nhật nếu tìm được đường ngắn hơn
