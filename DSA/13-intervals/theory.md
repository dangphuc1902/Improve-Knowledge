# 13 - Intervals

## 📖 Tổng quan

**Interval problems** xử lý các khoảng `[start, end]`. Bài toán thường yêu cầu merge, insert, tìm overlap, hoặc đếm non-overlapping intervals.

**Key insight**: Hầu hết bài intervals giải bằng **sort theo start** rồi xử lý tuần tự.

## 🧠 Kiến thức cốt lõi

### Quan hệ giữa 2 intervals
```
[a, b] và [c, d]:
- Không overlap: b < c hoặc d < a
- Overlap:       a <= d && c <= b
- Merge:         [min(a,c), max(b,d)]
```

## 🔍 Khi nào sử dụng?

- Bài có **intervals/ranges** `[start, end]`
- **Merge** overlapping intervals
- **Insert** interval vào danh sách sorted
- Tìm **minimum rooms/platforms** (meeting rooms)
- **Non-overlapping** intervals (greedy)

## 📝 Các Pattern phổ biến

### Pattern 1: Sort + Merge
- **Nó là gì?**: Sắp xếp các khoảng theo thời gian bắt đầu (`start`), sau đó duyệt qua từng khoảng để kiểm tra xem nó có chồng lấn (`overlap`) với khoảng trước đó không. Nếu có, ta gộp chúng lại bằng cách cập nhật thời gian kết thúc (`end`).
- **Giải quyết bài toán nào?**: 
    - Hợp nhất các khoảng chồng lấn (`Merge Intervals`).
    - Chèn một khoảng mới vào danh sách đã sắp xếp (`Insert Interval`).
- **Ưu điểm**:
    - Xử lý triệt để việc gộp nhiều khoảng liên tiếp.
    - Độ phức tạp O(n log n) chủ yếu do bước sắp xếp.
- **Nhược điểm**:
    - Cần thêm bộ nhớ để lưu trữ danh sách kết quả.
- **Sự thay thế**:
    - **Brute Force**: So sánh từng cặp khoảng (O(n²)).

```java
Arrays.sort(intervals, (a, b) -> a[0] - b[0]); // Sort theo start
List<int[]> merged = new ArrayList<>();
merged.add(intervals[0]);
for (int i = 1; i < intervals.length; i++) {
    int[] last = merged.get(merged.size() - 1);
    if (intervals[i][0] <= last[1]) {
        last[1] = Math.max(last[1], intervals[i][1]); // Merge
    } else {
        merged.add(intervals[i]); // Không overlap
    }
}
```

### Pattern 2: Sweep Line (Event Processing)
- **Nó là gì?**: Tách mỗi khoảng thành hai sự kiện: `start` (ví dụ +1) và `end` (ví dụ -1). Sắp xếp tất cả sự kiện theo thời gian và duyệt qua chúng để theo dõi số lượng khoảng đang "hoạt động" tại mỗi thời điểm.
- **Giải quyết bài toán nào?**: 
    - Tìm số lượng phòng họp tối thiểu cần thiết (`Meeting Rooms II`).
    - Tìm thời điểm có nhiều sự kiện xảy ra nhất.
- **Ưu điểm**:
    - Rất mạnh mẽ cho các bài toán đếm sự chồng lấn tại một thời điểm bất kỳ.
- **Nhược điểm**:
    - Cần xử lý cẩn thận trường hợp `start` và `end` xảy ra tại cùng một thời điểm.
- **Sự thay thế**:
    - **Min-Heap**: Theo dõi thời gian kết thúc sớm nhất của các khoảng đang hoạt động.

```java
// Tách start/end thành events, sort, sweep
// start = +1, end = -1 → track active count
```

### Pattern 3: Greedy (Non-overlapping Selection)
- **Nó là gì?**: Sắp xếp các khoảng theo thời gian **kết thúc** (`end`). Luôn chọn khoảng kết thúc sớm nhất để để lại nhiều không gian nhất có thể cho các khoảng phía sau.
- **Giải quyết bài toán nào?**: 
    - Tìm số lượng khoảng tối thiểu cần xóa để không còn chồng lấn (`Non-overlapping Intervals`).
    - Lập lịch công việc tối đa.
- **Ưu điểm**:
    - Tối ưu hóa số lượng khoảng chọn được.
- **Nhược điểm**:
    - Chỉ hoạt động khi mục tiêu là tối đa hóa số lượng hoặc tối thiểu hóa số lần xóa.
- **Sự thay thế**:
    - **Dynamic Programming**: Dùng khi các khoảng có trọng số khác nhau.

```java
// Sort theo end, greedy chọn interval kết thúc sớm nhất
Arrays.sort(intervals, (a, b) -> a[1] - b[1]);
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Sort + merge | O(n log n) | O(n) |
| Sort + greedy | O(n log n) | O(1) |
| Sweep line | O(n log n) | O(n) |

## 💡 Tips phỏng vấn

1. **Sort first**: 90% bài intervals bắt đầu bằng sort
2. **Sort by what**: start (merge), end (greedy non-overlap)
3. **Overlap condition**: `a.start <= b.end && b.start <= a.end`
4. **Edge case**: intervals rỗng, 1 interval, identical intervals
