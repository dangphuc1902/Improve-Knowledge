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

### Pattern 2: Sweep Line / Event Points
```java
// Tách start/end thành events, sort, sweep
// start = +1, end = -1 → track active count
```

### Pattern 3: Greedy (Non-overlapping)
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
