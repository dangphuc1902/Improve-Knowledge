# 13 - Intervals

## 📖 Tổng quan

**Intervals** là bài toán làm việc với các **đoạn [start, end]**. Thường yêu cầu merge, insert, hoặc tìm số lượng intervals không chồng lấp.

> **Ý tưởng cốt lõi:** **Sort by start time** là bước đầu tiên hầu hết mọi bài Intervals. Sau đó duyệt và xử lý overlap.

## 🧠 Kiến thức cốt lõi

### Điều kiện Overlap

```
Hai interval [a, b] và [c, d] OVERLAP nếu: a <= d && c <= b
Hai interval KHÔNG overlap nếu: b < c hoặc d < a
```

### Sort Strategy

```java
// Sort by start time
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

// Sort by end time (cho Greedy — Meeting Rooms, Non-overlapping)
Arrays.sort(intervals, (a, b) -> a[1] - b[1]);
```

## 🔍 Khi nào sử dụng?

- Bài toán liên quan đến **thời gian** (meeting rooms, task scheduling)
- Cần **merge** các đoạn chồng lấp
- **Insert** interval vào tập hợp đã sorted
- Tìm số lượng intervals **minimum** để cover/remove
- Cụm từ: *"interval"*, *"meeting"*, *"overlapping"*, *"schedule"*

## 📝 Các Pattern phổ biến

### Pattern 1: Merge Overlapping Intervals
- **Nó là gì?**: Sort by start, duyệt và merge khi overlap (curr.start <= last.end).
- **Giải quyết bài toán nào?**: Merge Intervals (LC 56), Insert Interval (LC 57).
- **Ưu điểm**: O(n log n) vì sort.
- **Nhược điểm**: Không in-place — cần result list.

```java
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
List<int[]> result = new ArrayList<>();
result.add(intervals[0]);

for (int i = 1; i < intervals.length; i++) {
    int[] last = result.get(result.size() - 1);
    int[] curr = intervals[i];
    if (curr[0] <= last[1]) {
        // Overlap: extend end
        last[1] = Math.max(last[1], curr[1]);
    } else {
        // No overlap: add as new interval
        result.add(curr);
    }
}
```

### Pattern 2: Insert Interval
- **Nó là gì?**: Insert vào sorted list, merge nếu cần. Chia thành 3 phần: trước mới, overlap, sau mới.
- **Giải quyết bài toán nào?**: Insert Interval (LC 57).

```java
// 3 phases:
// Phase 1: intervals hoàn toàn trước newInterval (end < newInterval.start)
// Phase 2: intervals overlap với newInterval → merge
// Phase 3: intervals hoàn toàn sau newInterval (start > newInterval.end)
```

### Pattern 3: Non-overlapping Intervals (Greedy)
- **Nó là gì?**: Tìm số interval tối thiểu cần xóa để không còn overlap. Sort by END time, greedy giữ interval kết thúc sớm nhất.
- **Giải quyết bài toán nào?**: Non-Overlapping Intervals (LC 435), Meeting Rooms II.

```java
// Sort by end time, greedy keep interval ending earliest
Arrays.sort(intervals, (a, b) -> a[1] - b[1]);
int keep = 0, lastEnd = Integer.MIN_VALUE;
for (int[] interval : intervals) {
    if (interval[0] >= lastEnd) {
        keep++;
        lastEnd = interval[1]; // update last kept interval's end
    }
    // else: skip this interval (it overlaps)
}
return intervals.length - keep; // số cần xóa
```

### Pattern 4: Meeting Rooms II (Minimum Rooms)
- **Nó là gì?**: Tìm số phòng tối thiểu. Sort start/end separately, dùng 2-pointer hoặc Min-Heap.
- **Giải quyết bài toán nào?**: Meeting Rooms II (LC 253).

```java
// Two arrays + two pointers
int[] starts = new int[n], ends = new int[n];
for (int i = 0; i < n; i++) { starts[i] = intervals[i][0]; ends[i] = intervals[i][1]; }
Arrays.sort(starts); Arrays.sort(ends);

int rooms = 0, endIdx = 0;
for (int startIdx = 0; startIdx < n; startIdx++) {
    if (starts[startIdx] < ends[endIdx]) rooms++;
    else endIdx++; // một meeting kết thúc, phòng được giải phóng
}
return rooms;
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Merge Intervals — Dry Run

```
intervals = [[1,3],[2,6],[8,10],[15,18]]
Sort by start: [[1,3],[2,6],[8,10],[15,18]] (đã sort)

result = [[1,3]]

i=1, [2,6]: curr.start=2 <= last.end=3 → OVERLAP
  last.end = max(3,6) = 6
  result = [[1,6]]

i=2, [8,10]: curr.start=8 > last.end=6 → NO OVERLAP
  add [8,10]
  result = [[1,6],[8,10]]

i=3, [15,18]: curr.start=15 > last.end=10 → NO OVERLAP
  add [15,18]
  result = [[1,6],[8,10],[15,18]]

✅ Output: [[1,6],[8,10],[15,18]]
```

### Ví dụ 2: Non-Overlapping Intervals — Greedy

```
intervals = [[1,2],[2,3],[3,4],[1,3]]
Sort by END: [[1,2],[2,3],[1,3],[3,4]]

keep=0, lastEnd=-∞

[1,2]: start=1 >= lastEnd=-∞ → KEEP. keep=1, lastEnd=2
[2,3]: start=2 >= lastEnd=2 → KEEP. keep=2, lastEnd=3
[1,3]: start=1 < lastEnd=3 → SKIP (overlap)
[3,4]: start=3 >= lastEnd=3 → KEEP. keep=3, lastEnd=4

Remove = intervals.length - keep = 4 - 3 = 1

✅ Output: 1 (remove [1,3])
```

### Ví dụ 3: Insert Interval

```
intervals = [[1,3],[6,9]], newInterval = [2,5]

Phase 1: [1,3] overlaps với [2,5]? 
  No gap before: [1,3].end=3 >= [2,5].start=2 → overlap

Phase 2: Merge [1,3] và [2,5] → [1,5]
  [6,9].start=6 > [1,5].end=5 → no more overlap

Phase 3: add [6,9]

✅ Output: [[1,5],[6,9]]
```

## 🔄 So sánh các Approach

### Non-overlapping: Greedy vs DP

| Approach | Time | Space | Ưu điểm |
|----------|------|-------|---------|
| **Greedy by end ⭐** | O(n log n) | O(1) | Optimal, intuitive |
| DP (LIS style) | O(n²) | O(n) | More complex |

### Meeting Rooms II: Heap vs Two Arrays

| Approach | Time | Space |
|----------|------|-------|
| **Two Arrays ⭐** | O(n log n) | O(n) |
| Min-Heap | O(n log n) | O(n) |

## 🚨 Edge Cases cần chú ý

```java
// Merge Intervals:
// 1. intervals.length == 1 → return as-is
// 2. [[1,4],[4,5]] → overlap at touching point → [[1,5]]
// 3. [[1,4],[2,3]] → nested → [[1,4]]

// Insert Interval:
// 1. newInterval before all → prepend
// 2. newInterval after all → append
// 3. newInterval covers all → one big interval

// Non-overlapping:
// 1. All non-overlapping → 0 removals
// 2. All same → remove all but 1
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Merge Intervals | O(n log n) | O(n) |
| Insert Interval | O(n) | O(n) |
| Non-overlapping | O(n log n) | O(1) |
| Meeting Rooms II | O(n log n) | O(n) |

## 💡 Tips phỏng vấn

1. **Sort by start**: Bước đầu tiên cho Merge và Insert.
2. **Sort by end**: Cho Greedy problems (Non-overlapping, Meeting Rooms).
3. **Overlap condition**: `a.start <= b.end && b.start <= a.end` — nhớ cả 2 chiều.
4. **Touching = overlap?**: Hỏi interviewer! `[1,4]` và `[4,5]` có merge không? Thường là có.
5. **Meeting Rooms II insight**: Số phòng = số meetings đang diễn ra tại peak time.
