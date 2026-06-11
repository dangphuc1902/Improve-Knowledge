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

## 📝 Các Pattern phổ biến

### Pattern 1: Dijkstra's Algorithm (Shortest Path)
- **Nó là gì?**: Sử dụng chiến lược tham lam kết hợp với hàng đợi ưu tiên (Priority Queue) để tìm đường đi ngắn nhất từ một đỉnh nguồn đến tất cả các đỉnh khác trong đồ thị có trọng số không âm.
- **Giải quyết bài toán nào?**: 
    - Tìm đường đi nhanh nhất giữa hai điểm.
    - Tính thời gian trễ của mạng lưới (`Network Delay Time`).
- **Ưu điểm**:
    - Hiệu quả cao O((V + E) log V).
    - Đảm bảo tìm được đường đi ngắn nhất nếu trọng số không âm.
- **Nhược điểm**:
    - Không hoạt động chính xác nếu đồ thị có cạnh trọng số âm.
- **Sự thay thế**:
    - **Bellman-Ford**: Dùng khi đồ thị có trọng số âm (O(V*E)).
    - **BFS**: Dùng khi đồ thị không có trọng số (hoặc trọng số bằng nhau).

### Pattern 2: Kruskal's & Prim's (Minimum Spanning Tree)
- **Nó là gì?**: 
    - **Kruskal**: Sắp xếp các cạnh và dùng Union-Find để nối các đỉnh mà không tạo chu trình.
    - **Prim**: Bắt đầu từ 1 đỉnh và liên tục mở rộng sang đỉnh gần nhất bằng Min-Heap.
- **Giải quyết bài toán nào?**: 
    - Kết nối tất cả các điểm với tổng chi phí thấp nhất (`Min Cost to Connect All Points`).
- **Ưu điểm**:
    - Tối ưu hóa chi phí kết nối toàn bộ hệ thống.
- **Nhược điểm**:
    - MST có thể không duy nhất nếu có nhiều cạnh cùng trọng số.
- **Sự thay thế**:
    - Duyệt đồ thị thông thường nếu không cần tối ưu chi phí cạnh.

### Pattern 3: Union-Find (Disjoint Set Union)
- **Nó là gì?**: Một cấu trúc dữ liệu quản lý một tập hợp các phần tử được chia thành các nhóm không giao nhau. Hỗ trợ hai thao tác chính: `find` (tìm nhóm của phần tử) và `union` (gộp hai nhóm).
- **Giải quyết bài toán nào?**: 
    - Phát hiện chu trình trong đồ thị vô hướng.
    - Tìm số lượng thành phần liên thông.
    - Bài toán về các mối quan hệ (ví dụ: nhóm bạn bè).
- **Ưu điểm**:
    - Tốc độ gần như hằng số O(α(n)) cho mỗi thao tác nhờ nén đường (Path Compression) và gộp theo hạng (Union by Rank).
- **Nhược điểm**:
    - Khó áp dụng cho đồ thị có hướng hoặc các bài toán cần thay đổi cấu trúc liên tục (xóa cạnh).
- **Sự thay thế**:
    - **DFS/BFS**: Tìm thành phần liên thông trong O(V+E).

```java
// Template Union-Find xem ở phần kiến thức cốt lõi
```
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
