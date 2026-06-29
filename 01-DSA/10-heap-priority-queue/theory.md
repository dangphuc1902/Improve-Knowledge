# 10 - Heap / Priority Queue

## 📖 Tổng quan

**Heap** là cây nhị phân hoàn chỉnh thỏa mãn **heap property**. Min-Heap: parent ≤ children. Max-Heap: parent ≥ children. Cho phép lấy min/max trong **O(1)** và insert/delete trong **O(log n)**.

> **Ý tưởng cốt lõi:** Heap là cấu trúc dữ liệu lý tưởng khi cần liên tục lấy phần tử **nhỏ nhất/lớn nhất** từ tập hợp đang thay đổi.

## 🧠 Kiến thức cốt lõi

### Java Priority Queue

```java
// Min-Heap (default): lấy phần tử NHỎ NHẤT
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-Heap: lấy phần tử LỚN NHẤT
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
// hoặc: new PriorityQueue<>((a, b) -> b - a);

// Custom comparator (objects)
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]); // sort by first element
```

### Operations Complexity

| Thao tác | Time | 
|----------|------|
| `offer(e)` / `add(e)` | O(log n) |
| `poll()` — lấy top | O(log n) |
| `peek()` — xem top | O(1) |
| `contains(e)` | O(n) |
| Build heap từ n phần tử | O(n) |

### Heap Sort

```java
// Heapify: O(n) — không phải O(n log n)!
// Heap Sort tổng thể: O(n log n)
```

## 🔍 Khi nào sử dụng?

- Tìm **Top K** phần tử lớn/nhỏ nhất
- **Streaming data**: Duy trì median, K phần tử nhỏ nhất trong stream
- **Greedy algorithm**: Luôn lấy phần tử tối ưu hiện tại
- **Shortest path**: Dijkstra's algorithm
- **Merge K sorted lists/arrays**
- Cụm từ: *"K largest/smallest"*, *"find median"*, *"merge k sorted"*

## 📝 Các Pattern phổ biến

### Pattern 1: Top K Elements — Min-Heap of size K
- **Nó là gì?**: Dùng Min-Heap kích thước K. Nếu heap lớn hơn K → poll min → cuối cùng heap chứa K phần tử lớn nhất.
- **Giải quyết bài toán nào?**: Top K Frequent Elements (LC 347), K Closest Points to Origin, Kth Largest Element.
- **Ưu điểm**: O(n log k) — tốt hơn O(n log n) sort khi k << n.
- **Nhược điểm**: O(k) space.
- **Sự thay thế**: Quick Select O(n) avg — nếu chỉ cần Kth element, không cần top K.

```java
// Top K Largest — dùng MIN-Heap size K (counterintuitive!)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (int num : nums) {
    minHeap.offer(num);
    if (minHeap.size() > k) minHeap.poll(); // loại phần tử nhỏ nhất
}
// Heap còn lại là K phần tử lớn nhất
```

### Pattern 2: Find Median from Data Stream — Two Heaps
- **Nó là gì?**: Dùng Max-Heap (lower half) + Min-Heap (upper half). Median là top của Max-Heap (hoặc trung bình 2 top).
- **Giải quyết bài toán nào?**: Find Median from Data Stream (LC 295), Sliding Window Median.
- **Ưu điểm**: O(log n) add, O(1) findMedian.
- **Nhược điểm**: O(n) space.

```java
PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
PriorityQueue<Integer> upper = new PriorityQueue<>(); // min-heap

// Invariant: lower.size() == upper.size() hoặc lower.size() == upper.size() + 1
void addNum(int num) {
    lower.offer(num);
    upper.offer(lower.poll()); // balance: đảm bảo upper min >= lower max
    if (lower.size() < upper.size()) lower.offer(upper.poll());
}

double findMedian() {
    if (lower.size() > upper.size()) return lower.peek();
    return (lower.peek() + upper.peek()) / 2.0;
}
```

### Pattern 3: K-Way Merge — Min-Heap with Source Tracking
- **Nó là gì?**: Merge K sorted arrays/lists bằng cách đưa head của mỗi list vào Min-Heap.
- **Giải quyết bài toán nào?**: Merge K Sorted Lists (LC 23), Kth Smallest Element in Sorted Matrix.
- **Ưu điểm**: O(n log k) — hiệu quả hơn merge từng cặp O(n * k).

```java
// Merge K sorted arrays
int[][] arrays; // K arrays
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
// a[0]=value, a[1]=array index, a[2]=element index

for (int i = 0; i < arrays.length; i++) {
    if (arrays[i].length > 0) pq.offer(new int[]{arrays[i][0], i, 0});
}

while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int val = curr[0], arrIdx = curr[1], elemIdx = curr[2];
    result.add(val);
    if (elemIdx + 1 < arrays[arrIdx].length) {
        pq.offer(new int[]{arrays[arrIdx][elemIdx + 1], arrIdx, elemIdx + 1});
    }
}
```

### Pattern 4: Task Scheduling — Max-Heap + Cooldown
- **Nó là gì?**: Dùng Max-Heap để luôn execute task phổ biến nhất. Dùng queue để track cooldown.
- **Giải quyết bài toán nào?**: Task Scheduler (LC 621).
- **Ưu điểm**: Greedy optimal — luôn giảm tối đa idle time.

```java
// Task Scheduler — Max-Heap (by frequency) + Queue (cooldown)
int[] freq = new int[26];
for (char task : tasks) freq[task - 'A']++;

PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
for (int f : freq) if (f > 0) maxHeap.offer(f);

Queue<int[]> cooldown = new LinkedList<>(); // [remaining_freq, available_time]
int time = 0;

while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
    time++;
    if (!maxHeap.isEmpty()) {
        int remaining = maxHeap.poll() - 1;
        if (remaining > 0) cooldown.offer(new int[]{remaining, time + n});
    }
    // Release từ cooldown nếu đến thời gian
    if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
        maxHeap.offer(cooldown.poll()[0]);
    }
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Top K Frequent Elements — Min-Heap Dry Run

```
nums = [1,1,1,2,2,3,3,3,3], k = 2

Bước 1: Đếm tần suất
  freq = {1:3, 2:2, 3:4}

Bước 2: Dùng Min-Heap size K=2, sắp xếp theo frequency
  Đưa (1,3) vào → heap=[(1,3)] (size=1)
  Đưa (2,2) vào → heap=[(2,2),(1,3)] (size=2)
  Đưa (3,4) vào → heap=[(2,2),(1,3),(3,4)] size=3>k=2
    poll min freq → remove (2,2)
    heap=[(1,3),(3,4)]
  
  size=2=k → không poll nữa

Bước 3: Lấy tất cả từ heap
✅ Output: [1, 3] (hoặc [3, 1])
```

### Ví dụ 2: Find Median — Two Heaps

```
Stream: [2, 3, 4]

addNum(2):
  lower.offer(2) → lower=[2]
  upper.offer(lower.poll()=2) → upper=[2], lower=[]
  lower.size() < upper.size() → lower.offer(upper.poll()=2)
  lower=[2], upper=[]
  Median = lower.peek() = 2

addNum(3):
  lower.offer(3) → lower=[3,2]
  upper.offer(lower.poll()=3) → upper=[3], lower=[2]
  lower.size() == upper.size() → OK
  Median = (lower.peek()=2 + upper.peek()=3)/2 = 2.5

addNum(4):
  lower.offer(4) → lower=[4,2]
  upper.offer(lower.poll()=4) → upper=[3,4], lower=[2]
  lower.size() < upper.size() → lower.offer(upper.poll()=3)
  lower=[3,2], upper=[4]
  Median = lower.peek() = 3
```

## 🔄 So sánh các Approach

### Kth Largest: Heap vs Sort vs Quick Select

| Approach | Time | Space | Best For |
|----------|------|-------|---------|
| **Min-Heap size k ⭐** | O(n log k) | O(k) | Streaming, k << n |
| Sort | O(n log n) | O(1) | Simple, one-time |
| Quick Select | O(n) avg | O(1) | Best time, single query |

### Top K Frequent: Heap vs Bucket Sort

| Approach | Time | Space |
|----------|------|-------|
| **Min-Heap ⭐** | O(n log k) | O(n + k) |
| Bucket Sort | O(n) | O(n) |

## 🚨 Edge Cases cần chú ý

```java
// Top K:
// 1. k = nums.length → trả về tất cả
// 2. All same frequency → bất kỳ k phần tử nào đều đúng
// 3. k = 1 → tìm max frequency element

// Two Heaps Median:
// 1. n chẵn → trung bình 2 top heaps
// 2. n lẻ → top của lower (larger heap)
// 3. Add cùng value nhiều lần → vẫn work

// Priority Queue Java:
// TRAP: Integer overflow với comparator (a, b) -> a - b
// Dùng Integer.compare(a, b) thay thế
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Top K Elements | O(n log k) | O(k) |
| Find Median (stream) | O(log n) add, O(1) find | O(n) |
| Merge K Sorted Lists | O(n log k) | O(k) |
| Task Scheduler | O(n log 26) = O(n) | O(26) |
| Dijkstra's Algorithm | O((V+E) log V) | O(V) |

## 💡 Tips phỏng vấn

1. **Min-Heap cho K Largest** (ngược đời!): Min-Heap size K → poll min → giữ lại K lớn nhất.
2. **Custom Comparator**: Dùng `Integer.compare(a, b)` thay vì `a - b` để tránh overflow.
3. **Two Heaps**: Pattern kinh điển cho bài toán median — cần nắm rõ invariant (size difference ≤ 1).
4. **Quick Select vs Heap**: Quick Select O(n) avg nhưng O(n²) worst — Heap O(n log k) consistent.
5. **Java PQ poll()**: Không throw exception khi rỗng — trả về null → cần isEmpty() check.
