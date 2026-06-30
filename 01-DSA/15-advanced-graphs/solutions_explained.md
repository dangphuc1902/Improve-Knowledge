# 15 - Advanced Graphs: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 3 bài toán thuộc chủ đề **Advanced Graphs (Đồ thị nâng cao)** từ LeetCode Master Tracker.

---

## 85. Cheapest Flights Within K Stops (LeetCode #787) - Medium

### 💡 Ý tưởng cốt lõi
Tìm chi phí rẻ nhất để bay từ trạm xuất phát `src` đến trạm đích `dst` với tối đa `k` trạm dừng trung gian.
Đây là bài toán tìm đường đi ngắn nhất có giới hạn số cạnh.
Giải pháp tối ưu là sử dụng thuật toán **Bellman-Ford** chạy đúng `k + 1` lần lặp (mỗi lần lặp đại diện cho việc đi thêm tối đa 1 chặng bay):
1. Khởi tạo mảng khoảng cách `prices` có kích thước `n`, gán giá trị mặc định là vô cùng lớn (`Integer.MAX_VALUE`). Gán `prices[src] = 0`.
2. Trong mỗi lần lặp từ `0` đến `k`:
   * Tạo bản sao của mảng khoảng cách hiện tại `tempPrices = Arrays.copyOf(prices, n)`.
   * Duyệt qua tất cả các chuyến bay `[u, v, cost]`:
     * Nếu trạm xuất phát `u` đã đi tới được (`prices[u] != INF`):
       * Cập nhật chi phí rẻ nhất đến `v` ở bảng phụ:
         $$\text{tempPrices}[v] = \min(\text{tempPrices}[v], \text{prices}[u] + cost)$$
   * Cập nhật `prices = tempPrices`.
3. Trả về `prices[dst]` nếu tìm thấy đường đi hợp lệ, ngược lại trả về `-1`.

### 📊 Hướng tiếp cận tối ưu

#### Bellman-Ford với K+1 lần lặp (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(K \cdot E)$ với $E$ là số lượng chuyến bay (`flights.length`).
  * **Space Complexity**: $O(n)$ để lưu trữ khoảng cách chi phí.

### 💻 Java Clean Code
```java
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    int[] prices = new int[n];
    Arrays.fill(prices, Integer.MAX_VALUE);
    prices[src] = 0;
    
    // Chạy K+1 lần lặp (tối đa K trạm dừng nghĩa là tối đa K+1 chặng bay)
    for (int i = 0; i <= k; i++) {
        int[] tempPrices = Arrays.copyOf(prices, n);
        
        for (int[] flight : flights) {
            int u = flight[0];
            int v = flight[1];
            int cost = flight[2];
            
            if (prices[u] == Integer.MAX_VALUE) continue;
            
            if (prices[u] + cost < tempPrices[v]) {
                tempPrices[v] = prices[u] + cost;
            }
        }
        prices = tempPrices;
    }
    
    return prices[dst] == Integer.MAX_VALUE ? -1 : prices[dst];
}
```

---

## 86. Network Delay Time (LeetCode #743) - Medium

### 💡 Ý tưởng cốt lõi
Một tín hiệu được gửi từ một node nguồn `k` trong một mạng lưới gồm `n` node có trọng số cạnh dương (thời gian truyền tín hiệu). Tìm thời gian tối thiểu để tất cả các node đều nhận được tín hiệu. Nếu không thể truyền tới mọi node, trả về `-1`.
Đây là bài toán tìm đường đi ngắn nhất xuất phát từ một nguồn duy nhất đến tất cả các đỉnh khác trên đồ thị có trọng số dương.
Thuật toán tối ưu nhất là **Dijkstra**:
1. Xây dựng danh sách kề đồ thị.
2. Khởi tạo mảng khoảng cách `dist` lưu thời gian tối thiểu nhận tín hiệu của mỗi node, gán vô cực, riêng `dist[k] = 0`.
3. Sử dụng một **Min Heap (Priority Queue)** để lưu trữ các cặp `[node, time]` sắp xếp theo thời gian tăng dần.
4. Đẩy `[k, 0]` vào Heap.
5. Khi Heap không rỗng:
   * Lấy ra node có thời gian nhỏ nhất `[currNode, currTime]`.
   * Nếu `currTime > dist[currNode]`, bỏ qua (đã có đường đi tối ưu hơn).
   * Duyệt qua các node hàng xóm `neighbor`, cập nhật khoảng cách:
     * Nếu `currTime + weight < dist[neighbor]`:
       * Gán `dist[neighbor] = currTime + weight`.
       * Đẩy `[neighbor, dist[neighbor]]` vào Heap.
6. Kết quả là giá trị lớn nhất trong mảng `dist` (thời gian để node nhận tín hiệu trễ nhất nhận được). Nếu có node nào vẫn bằng vô cực, trả về `-1`.

### 📊 Hướng tiếp cận tối ưu

#### Dijkstra với Min Heap (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(E \log V)$ với $V = n$ là số đỉnh và $E$ là số cạnh (`times.length`).
  * **Space Complexity**: $O(V + E)$ lưu danh sách kề đồ thị và mảng khoảng cách.

### 💻 Java Clean Code
```java
public int networkDelayTime(int[][] times, int n, int k) {
    // 1. Xây dựng danh sách kề
    List<List<int[]>> adj = new ArrayList<>();
    for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
    for (int[] time : times) {
        adj.get(time[0]).add(new int[]{time[1], time[2]}); // [target, weight]
    }
    
    // 2. Mảng khoảng cách thời gian
    int[] dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;
    
    // 3. Min Heap lưu [node, time_to_reach]
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
    pq.offer(new int[]{k, 0});
    
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int currNode = curr[0];
        int currTime = curr[1];
        
        if (currTime > dist[currNode]) continue;
        
        for (int[] edge : adj.get(currNode)) {
            int neighbor = edge[0];
            int weight = edge[1];
            
            if (currTime + weight < dist[neighbor]) {
                dist[neighbor] = currTime + weight;
                pq.offer(new int[]{neighbor, dist[neighbor]});
            }
        }
    }
    
    int maxDelay = 0;
    for (int i = 1; i <= n; i++) {
        if (dist[i] == Integer.MAX_VALUE) return -1; // Không kết nối được tới node i
        maxDelay = Math.max(maxDelay, dist[i]);
    }
    
    return maxDelay;
}
```

---

## 87. Alien Dictionary (LeetCode #269) - Hard

### 💡 Ý tưởng cốt lõi
Cho một danh sách các từ `words` được sắp xếp theo thứ tự từ điển của một ngôn ngữ ngoài hành tinh mới. Hãy tìm thứ tự đúng của các chữ cái trong ngôn ngữ đó. Nếu không có thứ tự hợp lệ (phát hiện mâu thuẫn), trả về chuỗi rỗng `""`.
Bài toán thực chất là **Xây dựng đồ thị có hướng và tìm Sắp xếp Topo**:
1. Đưa tất cả các ký tự độc nhất trong danh sách từ vào danh sách đỉnh của đồ thị.
2. So sánh từng cặp từ kề nhau `words[i]` và `words[i+1]` để tìm chữ cái đầu tiên khác nhau:
   * Chữ cái đầu tiên khác nhau `c1` của `words[i]` và `c2` của `words[i+1]` tạo ra một cạnh có hướng từ `c1 -> c2` (chữ `c1` đứng trước `c2` trong bảng chữ cái).
   * Lưu ý trường hợp biên đặc biệt: Nếu `words[i]` dài hơn `words[i+1]` nhưng toàn bộ `words[i+1]` là tiền tố của `words[i]` (ví dụ `"abc"` đứng trước `"ab"`), điều này là sai quy tắc từ điển → Trả về `""` ngay lập tức.
3. Thực hiện **Kahn's Algorithm (Topological Sort)** để tìm thứ tự chữ cái. Nếu kết quả không chứa đủ số lượng chữ cái độc nhất ban đầu, đồ thị có chu trình mâu thuẫn → Trả về `""`.

### 📊 Hướng tiếp cận tối ưu

#### Topological Sort / Kahn's Algorithm (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(C)$ với $C$ là tổng độ dài của tất cả các từ trong danh sách (để so sánh và tạo đồ thị).
  * **Space Complexity**: $O(1)$ vì bảng chữ cái có kích thước giới hạn tối đa là 26 đỉnh và $26 \times 26$ cạnh.

### 💻 Java Clean Code
```java
public class AlienDictionary {
    public String alienOrder(String[] words) {
        Map<Character, Set<Character>> adj = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        
        // Khởi tạo đồ thị cho tất cả ký tự độc nhất
        for (String w : words) {
            for (char c : w.toCharArray()) {
                adj.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }
        
        // So sánh các từ kề nhau để xây dựng cạnh đồ thị
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];
            
            // Biên kiểm tra tiền tố sai quy tắc (ví dụ "abc" đứng trước "ab")
            if (w1.length() > w2.length() && w1.startsWith(w2)) {
                return "";
            }
            
            int minLen = Math.min(w1.length(), w2.length());
            for (int j = 0; j < minLen; j++) {
                char c1 = w1.charAt(j);
                char c2 = w2.charAt(j);
                if (c1 != c2) {
                    if (adj.get(c1).add(c2)) {
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    break; // Chỉ so sánh ký tự khác biệt đầu tiên
                }
            }
        }
        
        // Sắp xếp Topo bằng Kahn's Algorithm
        Queue<Character> queue = new ArrayDeque<>();
        for (char c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                queue.offer(c);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            char curr = queue.poll();
            sb.append(curr);
            
            for (char neighbor : adj.get(curr)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        // Nếu chuỗi kết quả không đủ số lượng ký tự độc nhất -> có chu trình mâu thuẫn
        return sb.length() == inDegree.size() ? sb.toString() : "";
    }
}
```
