# 11 - Graphs: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 7 bài toán thuộc chủ đề **Graphs (Đồ thị)** từ LeetCode Master Tracker.

---

## 55. Number of Islands (LeetCode #200) - Medium

### 💡 Ý tưởng cốt lõi
Đếm số lượng hòn đảo trong bản đồ lưới 2D `'1'` (đất) và `'0'` (nước). Một hòn đảo được bao quanh bởi nước và được hình thành bằng cách nối các ô đất kề nhau theo chiều dọc hoặc chiều ngang.
Chúng ta duyệt qua từng ô trên bản đồ. Khi gặp một ô đất `'1'`:
* Tăng số lượng hòn đảo lên 1.
* Thực hiện thuật toán **DFS (hoặc BFS) flood fill** từ ô đó để tìm kiếm tất cả các ô đất liên thông với nó.
* Ở mỗi bước đi qua ô đất, ta chuyển đổi giá trị của nó thành `'0'` (hoặc đánh dấu đã thăm) để tránh việc đếm lặp lại.

### 📊 Hướng tiếp cận tối ưu

#### DFS / BFS Grid Flood Fill (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ với $m \times n$ là kích thước lưới, do mỗi ô được duyệt qua tối đa một vài lần.
  * **Space Complexity**: $O(m \cdot n)$ trong trường hợp xấu nhất call stack đệ quy sâu bằng kích thước lưới (khi toàn bộ lưới là một đảo lớn).

### 💻 Java Clean Code
```java
public int numIslands(char[][] grid) {
    if (grid == null || grid.length == 0) return 0;
    int m = grid.length;
    int n = grid[0].length;
    int count = 0;
    
    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (grid[r][c] == '1') {
                count++;
                dfs(grid, r, c); // Lan tỏa xóa toàn bộ hòn đảo
            }
        }
    }
    return count;
}

private void dfs(char[][] grid, int r, int c) {
    // Kiểm tra biên lưới và xem có phải đất liền không
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] == '0') {
        return;
    }
    
    grid[r][c] = '0'; // Đánh dấu ô đã thăm bằng cách biến thành nước
    
    // Đi tiếp 4 hướng kề cạnh
    dfs(grid, r + 1, c);
    dfs(grid, r - 1, c);
    dfs(grid, r, c + 1);
    dfs(grid, r, c - 1);
}
```

---

## 56. Clone Graph (LeetCode #133) - Medium

### 💡 Ý tưởng cốt lõi
Tạo bản sao sâu (deep copy) của một đồ thị liên thông vô hướng.
Mỗi node chứa danh sách các node hàng xóm. Để tạo bản sao sâu mà không bị lặp vô hạn do chu trình trong đồ thị, ta cần lưu giữ liên kết giữa node cũ và node mới.
Giải pháp tối ưu là sử dụng một **HashMap <Node cũ, Node mới sao sao>** kết hợp với duyệt đồ thị bằng **BFS hoặc DFS**:
1. Khi đi qua một node, nếu node đó chưa được clone, ta tạo bản sao của nó và lưu vào map.
2. Với mỗi node hàng xóm của node hiện tại, ta clone node hàng xóm đó (nếu chưa có trong map) và nối vào danh sách hàng xóm của node clone hiện tại.

### 📊 Hướng tiếp cận tối ưu

#### BFS / DFS với Node Map (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(V + E)$ với $V$ là số đỉnh và $E$ là số cạnh của đồ thị.
  * **Space Complexity**: $O(V)$ để lưu trữ ánh xạ các node đã clone trong HashMap và hàng đợi BFS.

### 💻 Java Clean Code
```java
// Node definition
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() { val = 0; neighbors = new ArrayList<Node>(); }
    public Node(int _val) { val = _val; neighbors = new ArrayList<Node>(); }
    public Node(int _val, ArrayList<Node> _neighbors) { val = _val; neighbors = _neighbors; }
}

class CloneGraph {
    public Node cloneGraph(Node node) {
        if (node == null) return null;
        
        Map<Node, Node> visited = new HashMap<>();
        Queue<Node> queue = new ArrayDeque<>();
        
        // Clone node đầu tiên
        visited.put(node, new Node(node.val));
        queue.offer(node);
        
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            
            for (Node neighbor : curr.neighbors) {
                if (!visited.containsKey(neighbor)) {
                    // Clone node hàng xóm nếu chưa từng đi qua
                    visited.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }
                // Nối node hàng xóm đã clone vào node hiện tại đã clone
                visited.get(curr).neighbors.add(visited.get(neighbor));
            }
        }
        
        return visited.get(node);
    }
}
```

---

## 57. Course Schedule (LeetCode #207) - Medium

### 💡 Ý tưởng cốt lõi
Xác định xem có thể hoàn thành tất cả các khóa học hay không, biết rằng mỗi khóa học có thể yêu cầu học xong một khóa học khác trước (phụ thuộc).
Bài toán thực chất là **phát hiện chu trình trong đồ thị có hướng**. Nếu đồ thị phụ thuộc có chu trình, ta không thể hoàn thành việc đăng ký học.
Chúng ta sử dụng thuật toán **Topological Sort (Sắp xếp topo)**, cụ thể là **Kahn's Algorithm (BFS bằng bán bậc vào - In-degree)**:
1. Tính bán bậc vào (số lượng cạnh đi vào) cho mỗi đỉnh.
2. Đưa tất cả các đỉnh có bán bậc vào bằng 0 (khóa học không yêu cầu điều kiện) vào hàng đợi.
3. Khi pop một đỉnh ra, ta duyệt qua các đỉnh kề của nó, giảm bán bậc vào của chúng đi 1.
4. Nếu đỉnh kề nào có bán bậc vào giảm về 0, đưa nó vào hàng đợi.
5. Cuối cùng, nếu số lượng khóa học lấy ra được khỏi hàng đợi bằng tổng số khóa học, đồ thị không có chu trình (trả về `true`). Ngược lại là `false`.

### 📊 Hướng tiếp cận tối ưu

#### Kahn's Algorithm (Optimal) ⭐
* **Mô tả**: BFS dựa trên bậc vào của các node.
* **Độ phức tạp**:
  * **Time Complexity**: $O(V + E)$ với $V$ là số môn học và $E$ là số mối quan hệ phụ thuộc.
  * **Space Complexity**: $O(V + E)$ để lưu danh sách kề và mảng bậc vào.

### 💻 Java Clean Code
```java
public boolean canFinish(int numCourses, int[][] prerequisites) {
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
    
    int[] inDegree = new int[numCourses];
    for (int[] pre : prerequisites) {
        int course = pre[0];
        int preReq = pre[1];
        adj.get(preReq).add(course);
        inDegree[course]++;
    }
    
    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) {
        if (inDegree[i] == 0) {
            queue.offer(i);
        }
    }
    
    int count = 0;
    while (!queue.isEmpty()) {
        int curr = queue.poll();
        count++;
        
        for (int neighbor : adj.get(curr)) {
            inDegree[neighbor]--;
            if (inDegree[neighbor] == 0) {
                queue.offer(neighbor);
            }
        }
    }
    
    return count == numCourses;
}
```

---

## 58. Course Schedule II (LeetCode #210) - Medium

### 💡 Ý tưởng cốt lõi
Tương tự như bài Course Schedule I, nhưng thay vì chỉ kiểm tra xem có học được không, đề bài yêu cầu **trả về thứ tự môn học** cần hoàn thành. Nếu không thể học hết các môn, trả về mảng rỗng.
Chúng ta áp dụng hoàn toàn thuật toán **Kahn's Algorithm** tương tự như bài trước. Khi lấy một môn học ra khỏi hàng đợi `queue`, ta lưu môn học đó vào mảng kết quả.

### 📊 Hướng tiếp cận tối ưu

#### Kahn's Algorithm (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(V + E)$.
  * **Space Complexity**: $O(V + E)$.

### 💻 Java Clean Code
```java
public int[] findOrder(int numCourses, int[][] prerequisites) {
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
    
    int[] inDegree = new int[numCourses];
    for (int[] pre : prerequisites) {
        int course = pre[0];
        int preReq = pre[1];
        adj.get(preReq).add(course);
        inDegree[course]++;
    }
    
    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) {
        if (inDegree[i] == 0) {
            queue.offer(i);
        }
    }
    
    int[] order = new int[numCourses];
    int idx = 0;
    
    while (!queue.isEmpty()) {
        int curr = queue.poll();
        order[idx++] = curr;
        
        for (int neighbor : adj.get(curr)) {
            inDegree[neighbor]--;
            if (inDegree[neighbor] == 0) {
                queue.offer(neighbor);
            }
        }
    }
    
    return (idx == numCourses) ? order : new int[]{};
}
```

---

## 59. Pacific Atlantic Water Flow (LeetCode #417) - Medium

### 💡 Ý tưởng cốt lõi
Tìm các ô trên bản đồ ma trận độ cao mà nước mưa từ đó có thể chảy ra cả hai đại dương: Thái Bình Dương (phía trên và bên trái) và Đại Tây Dương (phía dưới và bên phải).
Nước chảy từ ô cao hơn (hoặc bằng) sang ô thấp hơn.
Thay vì chạy DFS từ mỗi ô trên lưới để kiểm tra (rất chậm), ta thực hiện **chạy DFS ngược dòng từ các ô biên**:
1. Chạy DFS từ biên Thái Bình Dương (hàng 0 và cột 0) ngược dòng vào trong lưới (chỉ đi lên ô có độ cao lớn hơn hoặc bằng ô hiện tại). Đánh dấu các ô tới được vào ma trận `pacific`.
2. Chạy DFS tương tự từ biên Đại Tây Dương (hàng cuối và cột cuối), đánh dấu vào ma trận `atlantic`.
3. Giao của hai ma trận đánh dấu này chính là các ô nước có thể chảy tới cả hai đại dương.

### 📊 Hướng tiếp cận tối ưu

#### Multi-Source Reverse DFS (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n)$ do mỗi ô được thăm tối đa trong 2 lượt chạy DFS từ hai biên.
  * **Space Complexity**: $O(m \cdot n)$ cho hai ma trận đánh dấu boolean.

### 💻 Java Clean Code
```java
public List<List<Integer>> pacificAtlantic(int[][] heights) {
    List<List<Integer>> res = new ArrayList<>();
    if (heights == null || heights.length == 0) return res;
    
    int m = heights.length;
    int n = heights[0].length;
    
    boolean[][] pacific = new boolean[m][n];
    boolean[][] atlantic = new boolean[m][n];
    
    // DFS từ biên trên/dưới và trái/phải
    for (int c = 0; c < n; c++) {
        dfs(heights, 0, c, Integer.MIN_VALUE, pacific);     // Biên trên (Pacific)
        dfs(heights, m - 1, c, Integer.MIN_VALUE, atlantic); // Biên dưới (Atlantic)
    }
    for (int r = 0; r < m; r++) {
        dfs(heights, r, 0, Integer.MIN_VALUE, pacific);     // Biên trái (Pacific)
        dfs(heights, r, n - 1, Integer.MIN_VALUE, atlantic); // Biên phải (Atlantic)
    }
    
    // Gom các ô thỏa mãn cả hai
    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (pacific[r][c] && atlantic[r][c]) {
                res.add(Arrays.asList(r, c));
            }
        }
    }
    
    return res;
}

private void dfs(int[][] heights, int r, int c, int prevVal, boolean[][] ocean) {
    if (r < 0 || r >= heights.length || c < 0 || c >= heights[0].length 
        || ocean[r][c] || heights[r][c] < prevVal) {
        return;
    }
    
    ocean[r][c] = true;
    int val = heights[r][c];
    
    dfs(heights, r + 1, c, val, ocean);
    dfs(heights, r - 1, c, val, ocean);
    dfs(heights, r, c + 1, val, ocean);
    dfs(heights, r, c - 1, val, ocean);
}
```

---

## 60. Graph Valid Tree (LeetCode #261) - Medium

### 💡 Ý tưởng cốt lõi
Kiểm tra xem một đồ thị vô hướng gồm `n` đỉnh và danh sách các cạnh `edges` có tạo thành một cây hợp lệ (valid tree) hay không.
Một đồ thị là một cây khi và chỉ khi:
1. Đồ thị **không có chu trình**.
2. Đồ thị **liên thông hoàn toàn** (tất cả các đỉnh đều được kết nối với nhau).
Về mặt toán học, một đồ thị liên thông vô hướng không chu trình có đúng $n - 1$ cạnh.
Ta kiểm tra điều kiện cạnh trước: `edges.length == n - 1`. Sau đó áp dụng DFS/BFS xuất phát từ một đỉnh bất kỳ để kiểm tra xem có đi qua được toàn bộ $n$ đỉnh không.

### 📊 Hướng tiếp cận tối ưu

#### Edge Count + DFS / Union Find (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(V + E)$ với $V = n$ và $E = edges.length$.
  * **Space Complexity**: $O(V + E)$ lưu danh sách kề đồ thị.

### 💻 Java Clean Code
```java
public boolean validTree(int n, int[][] edges) {
    // Điều kiện cần của cây: n đỉnh có n - 1 cạnh
    if (edges.length != n - 1) return false;
    
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    for (int[] edge : edges) {
        adj.get(edge[0]).add(edge[1]);
        adj.get(edge[1]).add(edge[0]);
    }
    
    Set<Integer> visited = new HashSet<>();
    dfs(0, adj, visited);
    
    // Đồ thị liên thông nếu đi qua được tất cả các đỉnh
    return visited.size() == n;
}

private void dfs(int node, List<List<Integer>> adj, Set<Integer> visited) {
    if (visited.contains(node)) return;
    visited.add(node);
    for (int neighbor : adj.get(node)) {
        dfs(neighbor, adj, visited);
    }
}
```

---

## 61. Number of Connected Components in an Undirected Graph (LeetCode #323) - Medium

### 💡 Ý tưởng cốt lõi
Tìm số lượng thành phần liên thông trong một đồ thị vô hướng.
Ý tưởng tối ưu nhất cho bài toán này là sử dụng cấu trúc dữ liệu **Union-Find (Disjoint Set Union - DSU)**:
1. Khởi tạo số lượng thành phần liên thông bằng $n$. Mỗi đỉnh ban đầu là một tập hợp độc lập (đỉnh tự làm cha của chính nó).
2. Duyệt qua từng cạnh `(u, v)`:
   * Tìm tập hợp đại diện (find parent) của `u` và `v`.
   * Nếu chúng có cha khác nhau, ta gộp hai tập hợp lại làm một (union).
   * Mỗi phép gộp thành công làm giảm số lượng thành phần liên thông đi 1.
3. Trả về kết quả cuối cùng.

### 📊 Hướng tiếp cận tối ưu

#### Union-Find với Path Compression (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(V + E \cdot \alpha(V))$ với $\alpha$ là hàm Inverse Ackermann cực kỳ nhỏ (gần như $O(1)$).
  * **Space Complexity**: $O(V)$ lưu trữ mảng cha `parent` trong DSU.

### 💻 Java Clean Code
```java
public int countComponents(int n, int[][] edges) {
    int[] parent = new int[n];
    for (int i = 0; i < n; i++) parent[i] = i;
    
    int components = n;
    for (int[] edge : edges) {
        int root1 = find(edge[0], parent);
        int root2 = find(edge[1], parent);
        if (root1 != root2) {
            parent[root1] = root2; // Gộp hai nhóm
            components--;
        }
    }
    return components;
}

private int find(int val, int[] parent) {
    while (val != parent[val]) {
        parent[val] = parent[parent[val]]; // Path compression
        val = parent[val];
    }
    return val;
}
```
