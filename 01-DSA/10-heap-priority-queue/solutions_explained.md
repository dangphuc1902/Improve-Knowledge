# 10 - Heap / Priority Queue: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 5 bài toán thuộc chủ đề **Heap / Priority Queue** từ LeetCode Master Tracker.

---

## 45. Kth Largest Element in a Stream (LeetCode #703) - Easy

### 💡 Ý tưởng cốt lõi
Thiết kế một lớp để tìm phần tử lớn thứ $k$ trong một luồng dữ liệu (data stream).
Ý tưởng tối ưu là duy trì một **Min Heap (Priority Queue)** có kích thước tối đa là $k$.
* Khi ta thêm một số mới vào heap:
  * Nếu số lượng phần tử trong heap vượt quá $k$, ta pop (xóa) phần tử nhỏ nhất ra.
  * Vì đây là Min Heap, phần tử ở đỉnh heap luôn là phần tử nhỏ nhất trong số $k$ phần tử lớn nhất đã đi qua.
  * Do đó, phần tử ở đỉnh heap chính là phần tử lớn thứ $k$.

### 📊 Hướng tiếp cận tối ưu

#### Min Heap of size k (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**:
    * Khởi tạo: $O(n \log k)$ với $n$ là kích thước mảng ban đầu.
    * Thêm phần tử: $O(\log k)$ do thao tác thêm/xóa trên heap kích thước $k$.
  * **Space Complexity**: $O(k)$ để lưu trữ tối đa $k$ phần tử trong Min Heap.

### 🔄 Dry Run với ví dụ
* **Thao tác**: `k = 3`, nums = `[4, 5, 8, 2]`
  1. Khởi tạo: Đưa các phần tử vào Heap. Sau khi giữ size = 3, `Heap = {4, 5, 8}`.
  2. `add(3)`: Đẩy 3 vào Heap → `{3, 4, 5, 8}`. Size = 4 > 3 → Pop 3. `Heap = {4, 5, 8}`. Đỉnh là `4` (Kth largest).
  3. `add(5)`: Đẩy 5 vào Heap → `{4, 5, 5, 8}`. Size = 4 > 3 → Pop 4. `Heap = {5, 5, 8}`. Đỉnh là `5` (Kth largest).
  4. `add(10)`: Đẩy 10 → `{5, 5, 8, 10}`. Size = 4 > 3 → Pop 5. `Heap = {5, 8, 10}`. Đỉnh là `8` (Kth largest).

### 💻 Java Clean Code
```java
class KthLargest {
    private final PriorityQueue<Integer> minHeap;
    private final int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>(k);
        for (int num : nums) {
            add(num);
        }
    }
    
    public int add(int val) {
        minHeap.offer(val);
        if (minHeap.size() > k) {
            minHeap.poll(); // Xóa bớt phần tử nhỏ hơn để giữ size = k
        }
        return minHeap.peek(); // Đỉnh heap chính là số lớn thứ k
    }
}
```

---

## 46. Last Stone Weight (LeetCode #1046) - Easy

### 💡 Ý tưởng cốt lõi
Trò chơi đập đá: Mỗi lượt lấy ra hai viên đá nặng nhất có trọng lượng $x$ và $y$ ($x \le y$).
* Nếu $x == y$, cả hai viên đều bị phá hủy hoàn toàn.
* Nếu $x \neq y$, viên đá $y$ còn lại trọng lượng $y - x$.
Trò chơi kết thúc khi còn lại tối đa 1 viên đá.
Ý tưởng tối ưu là đưa tất cả viên đá vào một **Max Heap** (trong Java dùng `PriorityQueue` với bộ so sánh ngược `Collections.reverseOrder()`).
Ở mỗi lượt, ta pop hai phần tử lớn nhất ra khỏi Max Heap, tính toán trọng lượng còn lại và đẩy ngược lại vào heap (nếu trọng lượng khác 0).

### 📊 Hướng tiếp cận tối ưu

#### Max Heap Simulation (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ vì mỗi thao tác lấy ra và đẩy vào heap mất $O(\log n)$ và có tối đa $n$ lượt đập đá.
  * **Space Complexity**: $O(n)$ lưu trữ các viên đá trong Max Heap.

### 💻 Java Clean Code
```java
public int lastStoneWeight(int[] stones) {
    // Khởi tạo Max Heap
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int stone : stones) {
        maxHeap.offer(stone);
    }
    
    while (maxHeap.size() > 1) {
        int y = maxHeap.poll(); // Đá nặng nhất
        int x = maxHeap.poll(); // Đá nặng nhì
        
        if (x != y) {
            maxHeap.offer(y - x); // Đẩy phần đá dư còn lại vào heap
        }
    }
    
    return maxHeap.isEmpty() ? 0 : maxHeap.peek();
}
```

---

## 47. K Closest Points to Origin (LeetCode #973) - Medium

### 💡 Ý tưởng cốt lõi
Tìm $k$ điểm gần gốc tọa độ $(0, 0)$ nhất trên mặt phẳng 2D. Khoảng cách được tính bằng khoảng cách Euclide: $d^2 = x^2 + y^2$.
Chúng ta có thể sử dụng một **Max Heap** có kích thước tối đa là $k$. Max Heap sẽ so sánh các điểm dựa trên khoảng cách của chúng tới gốc tọa độ (điểm có khoảng cách lớn hơn nằm ở đỉnh heap).
* Ta duyệt qua từng điểm và thêm vào Max Heap.
* Nếu kích thước heap vượt quá $k$, ta pop điểm có khoảng cách lớn nhất ở đỉnh ra.
* Cuối cùng, Max Heap sẽ giữ lại đúng $k$ điểm có khoảng cách nhỏ nhất.

### 📊 Các hướng tiếp cận

#### Cách 1: Sắp xếp (Sorting)
* **Mô tả**: Tính khoảng cách cho mọi điểm, sắp xếp mảng điểm tăng dần và lấy $k$ điểm đầu tiên.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$.
  * **Space Complexity**: $O(n)$ để lưu trữ khoảng cách hoặc sắp xếp.

#### Cách 2: Max Heap (Optimal) ⭐
* **Mô tả**: Duy trì Max Heap kích thước $k$ để tìm kiếm trực tiếp trong luồng.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log k)$ vì chỉ duy trì heap kích thước $k$.
  * **Space Complexity**: $O(k)$ lưu trữ $k$ điểm trong heap.

### 💻 Java Clean Code
```java
public int[][] kClosest(int[][] points, int k) {
    // Max Heap so sánh theo khoảng cách Euclide bình phương giảm dần
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare((b[0]*b[0] + b[1]*b[1]), (a[0]*a[0] + a[1]*a[1]))
    );
    
    for (int[] point : points) {
        maxHeap.offer(point);
        if (maxHeap.size() > k) {
            maxHeap.poll(); // Xóa điểm xa nhất
        }
    }
    
    int[][] res = new int[k][2];
    int idx = 0;
    while (!maxHeap.isEmpty()) {
        res[idx++] = maxHeap.poll();
    }
    return res;
}
```

---

## 48. Task Scheduler (LeetCode #621) - Medium

### 💡 Ý tưởng cốt lõi
Lập lịch thực thi các task trên CPU sao cho thời gian hoàn thành là ngắn nhất. Giữa hai task cùng loại phải có khoảng giãn cách tối thiểu là `n` đơn vị thời gian (cooldown).
Bài toán này thực chất có thể giải quyết bằng phương pháp **Toán học & Tham lam (Greedy)**:
1. Đếm tần suất xuất hiện của mỗi task. Tìm tần suất lớn nhất `maxFreq` (ví dụ task A xuất hiện nhiều nhất là 3 lần).
2. Số lượng task có cùng tần suất lớn nhất là `maxFreqTasksCount`.
3. Ta sắp xếp các task có tần suất cao nhất vào các khung giờ. Ví dụ với `n = 2` và 3 task A:
   `A _ _ A _ _ A` (gồm `maxFreq - 1` nhóm, mỗi nhóm có độ dài `n + 1`).
4. Phần trống cuối cùng sau chữ A cuối cùng có độ dài bằng `maxFreqTasksCount`.
5. Công thức tính thời gian tối thiểu dựa trên các khoảng trống này là:
   $$\text{Ans} = (\text{maxFreq} - 1) \times (n + 1) + \text{maxFreqTasksCount}$$
6. Tuy nhiên, nếu số lượng task thực tế lớn hơn công thức trên, CPU sẽ chạy liên tục không nghỉ và kết quả chính là tổng số task ban đầu (`tasks.length`). Do đó kết quả cuối cùng là `max(tasks.length, Ans)`.

### 📊 Hướng tiếp cận tối ưu

#### Greedy / Math (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(tasks.length)$ để đếm tần suất. Thao tác tính toán toán học mất $O(1)$.
  * **Space Complexity**: $O(1)$ vì bảng chữ cái tasks chỉ có 26 ký tự.

### 💻 Java Clean Code
```java
public int leastInterval(char[] tasks, int n) {
    int[] frequencies = new int[26];
    for (char t : tasks) {
        frequencies[t - 'A']++;
    }
    
    Arrays.sort(frequencies);
    int maxFreq = frequencies[25]; // Tần suất của task nhiều nhất
    
    // Đếm xem có bao nhiêu task có cùng tần suất maxFreq
    int maxFreqTasksCount = 0;
    for (int f : frequencies) {
        if (f == maxFreq) {
            maxFreqTasksCount++;
        }
    }
    
    int minTime = (maxFreq - 1) * (n + 1) + maxFreqTasksCount;
    return Math.max(tasks.length, minTime);
}
```

---

## 49. Find Median from Data Stream (LeetCode #295) - Hard

### 💡 Ý tưởng cốt lõi
Thiết kế cấu trúc dữ liệu hỗ trợ thêm số từ luồng dữ liệu và tìm số trung vị (median) trong thời gian thực.
Ý tưởng tối ưu là chia tập số đã nhận thành hai nửa bằng nhau sử dụng **Hai Heap**:
1. **Max Heap (`small`)**: Lưu nửa số nhỏ hơn. Đỉnh heap chứa số lớn nhất của nửa này.
2. **Min Heap (`large`)**: Lưu nửa số lớn hơn. Đỉnh heap chứa số nhỏ nhất của nửa này.
Quy tắc duy trì:
* Đảm bảo mọi số trong Max Heap `small` đều nhỏ hơn hoặc bằng mọi số trong Min Heap `large`.
* Đảm bảo chênh lệch kích thước giữa hai heap không vượt quá 1: `|small.size() - large.size()| <= 1`.
Khi tìm trung vị:
* Nếu số lượng phần tử lẻ: Trung vị chính là đỉnh của heap có kích thước lớn hơn.
* Nếu chẵn: Trung vị bằng trung bình cộng đỉnh của cả hai heap.

### 📊 Hướng tiếp cận tối ưu

#### Two Heaps (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**:
    * `addNum`: $O(\log n)$ do thao tác thêm và cân bằng trên hai heap.
    * `findMedian`: $O(1)$ chỉ lấy giá trị ở đỉnh heap.
  * **Space Complexity**: $O(n)$ để lưu toàn bộ các số.

### 🔄 Dry Run với ví dụ
* **Thao tác**:
  1. `addNum(1)`: Đẩy vào `small` (Max Heap). `small = {1}`, `large = {}`.
  2. `addNum(3)`: Đẩy vào `small` → `{3, 1}`. Cân bằng bằng cách chuyển số lớn nhất sang `large`: `small = {1}`, `large = {3}` (Min Heap).
  3. `findMedian()`: Hai heap bằng size (1 và 1) → `(1 + 3) / 2 = 2.0`.
  4. `addNum(2)`: Đẩy vào `small` (2 > 3? Không, đẩy vào small). `small = {2, 1}`, `large = {3}`.
  5. `findMedian()`: `small.size() = 2 > large.size() = 1` → Trả về đỉnh `small` là `2.0`.

### 💻 Java Clean Code
```java
class MedianFinder {
    private final PriorityQueue<Integer> small; // Max Heap lưu nửa nhỏ
    private final PriorityQueue<Integer> large; // Min Heap lưu nửa lớn

    public MedianFinder() {
        small = new PriorityQueue<>(Collections.reverseOrder());
        large = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        // Bước 1: Thêm vào heap thích hợp
        if (small.isEmpty() || num <= small.peek()) {
            small.offer(num);
        } else {
            large.offer(num);
        }
        
        // Bước 2: Cân bằng kích thước hai heap
        if (small.size() > large.size() + 1) {
            large.offer(small.poll());
        } else if (large.size() > small.size()) {
            small.offer(large.poll());
        }
    }
    
    public double findMedian() {
        if (small.size() > large.size()) {
            return small.peek();
        } else {
            return (double) (small.peek() + large.peek()) / 2;
        }
    }
}
```
