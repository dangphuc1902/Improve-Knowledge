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

### Pattern 1: Top K Elements (Min Heap size K)
- **Nó là gì?**: Duy trì một Heap có kích thước cố định `k`. Khi muốn tìm `k` phần tử **lớn nhất**, ta dùng **Min Heap** để liên tục loại bỏ các phần tử nhỏ nhất ra khỏi tập hợp.
- **Giải quyết bài toán nào?**: 
    - Tìm k số lớn nhất trong mảng (`Kth Largest Element in an Array`).
    - Tìm k từ xuất hiện nhiều nhất (`Top K Frequent Words`).
- **Ưu điểm**:
    - Hiệu suất tốt hơn Sorting (O(n log k) so với O(n log n)).
    - Tiết kiệm bộ nhớ (chỉ lưu `k` phần tử thay vì toàn bộ `n` phần tử).
- **Nhược điểm**:
    - Không thể lấy ra các phần tử theo thứ tự nếu không `poll` hết Heap.
- **Sự thay thế**:
    - **Sorting**: Sắp xếp toàn bộ mảng (O(n log n)).
    - **Quick Select**: Tìm k-th element trong O(n) trung bình.

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (int num : nums) {
    minHeap.offer(num);
    if (minHeap.size() > k) minHeap.poll(); // Bỏ phần tử nhỏ, giữ lại k phần tử lớn nhất
}
```

### Pattern 2: Merge K Sorted Lists
- **Nó là gì?**: Sử dụng Heap để theo dõi phần tử nhỏ nhất hiện tại trong `k` danh sách đã được sắp xếp. Sau khi lấy phần tử nhỏ nhất ra, ta thêm phần tử tiếp theo từ chính danh sách đó vào Heap.
- **Giải quyết bài toán nào?**: 
    - Hợp nhất k danh sách liên kết đã sắp xếp (`Merge k Sorted Lists`).
    - Hợp nhất các luồng dữ liệu (stream) đã được sắp xếp theo thời gian.
- **Ưu điểm**:
    - Đảm bảo lấy ra phần tử nhỏ nhất trong O(log k).
    - Phù hợp với dữ liệu lớn không thể load hết vào bộ nhớ.
- **Nhược điểm**:
    - Cần quản lý con trỏ của từng danh sách một cách cẩn thận.
- **Sự thay thế**:
    - **Divide and Conquer**: Merge từng cặp danh sách (O(n log k)).

```java
PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);
// 1. Thêm head của k lists vào heap
// 2. Poll min node, thêm node.next vào heap
```

### Pattern 3: Two Heaps (Running Median)
- **Nó là gì?**: Chia tập dữ liệu thành hai nửa: nửa nhỏ lưu trong một **Max Heap** và nửa lớn lưu trong một **Min Heap**. Số trung vị sẽ nằm ở đỉnh của một trong hai Heap (hoặc trung bình cộng của hai đỉnh).
- **Giải quyết bài toán nào?**: 
    - Tìm số trung vị trong một luồng dữ liệu (`Find Median from Data Stream`).
- **Ưu điểm**:
    - Truy xuất số trung vị cực nhanh O(1).
    - Thêm phần tử mới trong O(log n).
- **Nhược điểm**:
    - Tốn bộ nhớ để lưu trữ toàn bộ dữ liệu O(n).
    - Logic cân bằng (rebalance) hai Heap cần chính xác.
- **Sự thay thế**:
    - **Insertion Sort**: Giữ mảng luôn sorted (Thêm O(n), Lấy median O(1)).

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
