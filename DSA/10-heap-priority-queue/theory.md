# 10 - Heap / Priority Queue

## 📖 Tổng quan

**Heap** là cấu trúc dữ liệu dạng cây nhị phân đặc biệt:
- **Min Heap**: parent ≤ children → root là min
- **Max Heap**: parent ≥ children → root là max

Java dùng `PriorityQueue<T>` (mặc định Min Heap).

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
```

## 🧠 Kiến thức cốt lõi

| Thao tác | Time |
|----------|------|
| `offer(x)` / push | O(log n) |
| `poll()` / pop min/max | O(log n) |
| `peek()` / xem min/max | O(1) |
| `size()` | O(1) |
| Build heap từ n phần tử | O(n) |

### Khi nào Min vs Max Heap?

| Bài toán | Chọn |
|----------|------|
| K largest elements | **Min Heap** size k (bỏ nhỏ, giữ k lớn nhất) |
| K smallest elements | **Max Heap** size k |
| Merge K sorted lists | **Min Heap** |
| Find median | **Max Heap** + **Min Heap** |

## 🔍 Khi nào sử dụng?

- Cần lấy **min/max** liên tục
- **Top K** elements (largest, most frequent...)
- **Merge K sorted** arrays/lists
- **Stream processing**: median, running max/min
- **Scheduling**: task với priority

## 📝 Các Pattern phổ biến

### Pattern 1: Top K (Min Heap size K)
```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (int num : nums) {
    minHeap.offer(num);
    if (minHeap.size() > k) minHeap.poll(); // Bỏ phần tử nhỏ
}
// minHeap chứa k phần tử lớn nhất
```

### Pattern 2: Merge K Sorted
```java
PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
// Thêm phần tử đầu tiên của mỗi list
// Poll min, thêm phần tử tiếp theo từ list chứa min đó
```

### Pattern 3: Two Heap (Find Median)
```java
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // Nửa nhỏ
PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // Nửa lớn
// Maintain: maxHeap.size() == minHeap.size() hoặc == minHeap.size() + 1
// Median = maxHeap.peek() hoặc (maxHeap.peek() + minHeap.peek()) / 2
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Sort + lấy top K | O(n log n) | O(1) |
| Heap top K | O(n log k) | O(k) |
| Two Heap median | O(log n) per add | O(n) |

## 💡 Tips phỏng vấn

1. **Default**: Java `PriorityQueue` là **Min Heap** — nhớ dùng `reverseOrder()` cho Max
2. **Custom comparator**: `new PriorityQueue<>((a, b) -> a.freq - b.freq)`
3. **Top K largest = Min Heap size K**: Nghe ngược nhưng đúng — bỏ nhỏ giữ lớn
4. **Two Heap**: Pattern mạnh cho running median — học thuộc template
