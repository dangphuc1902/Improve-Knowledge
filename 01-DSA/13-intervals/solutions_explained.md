# 13 - Intervals: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 4 bài toán thuộc chủ đề **Intervals (Các khoảng)** từ LeetCode Master Tracker.

---

## 81. Merge Intervals (LeetCode #56) - Medium

### 💡 Ý tưởng cốt lõi
Gộp tất cả các khoảng thời gian giao nhau (overlapping intervals) thành các khoảng không chồng lấn.
Ý tưởng tối ưu:
1. **Sắp xếp** các khoảng ban đầu theo điểm bắt đầu tăng dần: `intervals[i][0]`.
2. Khởi tạo danh sách kết quả chứa khoảng đầu tiên.
3. Duyệt qua từng khoảng tiếp theo:
   * So sánh khoảng hiện tại với khoảng cuối cùng trong danh sách kết quả.
   * Nếu điểm bắt đầu của khoảng hiện tại nhỏ hơn hoặc bằng điểm kết thúc của khoảng cuối trong kết quả (`curr[0] <= last[1]`), có nghĩa là chúng chồng lấn. Ta gộp chúng bằng cách cập nhật điểm kết thúc của khoảng cuối thành:
     $$\text{last}[1] = \max(\text{last}[1], \text{curr}[1])$$
   * Nếu không chồng lấn, thêm khoảng hiện tại vào kết quả như một khoảng mới độc lập.

### 📊 Hướng tiếp cận tối ưu

#### Sort and Scan (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ do bước sắp xếp mảng. Quá trình quét tuyến tính chỉ tốn $O(n)$.
  * **Space Complexity**: $O(n)$ hoặc $O(\log n)$ phụ thuộc bộ nhớ tạm của thuật toán sắp xếp.

### 🔄 Dry Run với ví dụ
* **Input**: `intervals = [[1, 3], [2, 6], [8, 10], [15, 18]]`
* **Sắp xếp**: Đã sắp xếp sẵn.
* **Xử lý**:
  - `res = [[1, 3]]`
  - Xét `[2, 6]`: `2 <= 3` (chồng lấn) → Gộp: `res = [[1, max(3, 6)]]` tức `[[1, 6]]`.
  - Xét `[8, 10]`: `8 > 6` (không chồng lấn) → Thêm mới: `res = [[1, 6], [8, 10]]`.
  - Xét `[15, 18]`: `15 > 10` → Thêm mới: `res = [[1, 6], [8, 10], [15, 18]]`.
* **Kết quả**: `[[1, 6], [8, 10], [15, 18]]`

### 💻 Java Clean Code
```java
public int[][] merge(int[][] intervals) {
    if (intervals.length <= 1) return intervals;
    
    // Bước 1: Sắp xếp theo điểm bắt đầu
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    
    List<int[]> merged = new ArrayList<>();
    int[] currentInterval = intervals[0];
    merged.add(currentInterval);
    
    for (int[] nextInterval : intervals) {
        // Nếu khoảng tiếp theo chồng lấn với khoảng hiện tại
        if (nextInterval[0] <= currentInterval[1]) {
            currentInterval[1] = Math.max(currentInterval[1], nextInterval[1]);
        } else {
            currentInterval = nextInterval;
            merged.add(currentInterval);
        }
    }
    
    return merged.toArray(new int[merged.size()][]);
}
```

---

## 82. Insert Interval (LeetCode #57) - Medium

### 💡 Ý tưởng cốt lõi
Chèn một khoảng mới `newInterval` vào danh sách các khoảng không chồng lấn đã được sắp xếp sẵn, gộp các khoảng bị chồng lấn nếu cần.
Chúng ta duyệt danh sách các khoảng chia làm 3 giai đoạn:
1. **Giai đoạn 1 (Trước khoảng mới)**: Các khoảng kết thúc trước khi khoảng mới bắt đầu (`intervals[i][1] < newInterval[0]`). Đưa thẳng các khoảng này vào kết quả.
2. **Giai đoạn 2 (Chồng lấn)**: Các khoảng có sự chồng lấn với khoảng mới (chừng nào `intervals[i][0] <= newInterval[1]`). Ta liên tục gộp chúng vào `newInterval`:
   $$\text{newInterval}[0] = \min(\text{newInterval}[0], \text{intervals}[i][0])$$
   $$\text{newInterval}[1] = \max(\text{newInterval}[1], \text{intervals}[i][1])$$
   Đưa khoảng `newInterval` sau khi gộp xong vào kết quả.
3. **Giai đoạn 3 (Sau khoảng mới)**: Các khoảng bắt đầu sau khi khoảng mới kết thúc. Đưa hết vào kết quả.

### 📊 Hướng tiếp cận tối ưu

#### 3-Phase Linear Scan (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt qua danh sách đúng 1 lần.
  * **Space Complexity**: $O(n)$ chứa danh sách kết quả đầu ra.

### 💻 Java Clean Code
```java
public int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0;
    int n = intervals.length;
    
    // 1. Thêm các khoảng kết thúc trước khi newInterval bắt đầu
    while (i < n && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i]);
        i++;
    }
    
    // 2. Gộp các khoảng chồng lấn vào newInterval
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval); // Thêm newInterval sau khi đã gộp
    
    // 3. Thêm các khoảng bắt đầu sau khi newInterval kết thúc
    while (i < n) {
        result.add(intervals[i]);
        i++;
    }
    
    return result.toArray(new int[result.size()][]);
}
```

---

## 83. Non-overlapping Intervals (LeetCode #435) - Medium

### 💡 Ý tưởng cốt lõi
Tìm số lượng khoảng tối thiểu cần loại bỏ để các khoảng còn lại không chồng lấn nhau.
Bài toán này tương đương với: *Tìm số lượng khoảng tối đa không chồng lấn có thể giữ lại*, sau đó lấy tổng số khoảng trừ đi số lượng đó.
Áp dụng thuật toán tham lam **Greedy (Lập lịch công việc tối ưu)**:
1. Sắp xếp các khoảng theo **điểm kết thúc** tăng dần: `intervals[i][1]`.
2. Ta luôn ưu tiên giữ lại khoảng kết thúc sớm nhất để dành nhiều không gian thời gian nhất cho các khoảng phía sau.
3. Duyệt qua các khoảng, nếu khoảng hiện tại bắt đầu sau khi khoảng đã chọn trước đó kết thúc (`curr[0] >= end`), ta chọn khoảng hiện tại và cập nhật `end = curr[1]`.
4. Nếu chồng lấn, ta phải loại bỏ khoảng hiện tại, tăng biến đếm số khoảng bị xóa.

### 📊 Hướng tiếp cận tối ưu

#### Greedy on End Time (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ do sắp xếp mảng.
  * **Space Complexity**: $O(1)$ hoặc $O(\log n)$ bộ nhớ thuật toán sắp xếp.

### 💻 Java Clean Code
```java
public int eraseOverlapIntervals(int[][] intervals) {
    if (intervals.length == 0) return 0;
    
    // Sắp xếp theo điểm kết thúc tăng dần
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
    
    int end = intervals[0][1];
    int count = 0; // Số lượng khoảng bị loại bỏ
    
    for (int i = 1; i < intervals.length; i++) {
        // Nếu khoảng i bắt đầu trước khi khoảng trước đó kết thúc -> chồng lấn
        if (intervals[i][0] < end) {
            count++; // Cần loại bỏ khoảng này
        } else {
            end = intervals[i][1]; // Cập nhật mốc kết thúc mới
        }
    }
    
    return count;
}
```

---

## 84. Meeting Rooms II (LeetCode #253) - Medium

### 💡 Ý tưởng cốt lõi
Tìm số lượng phòng họp tối thiểu cần thiết để phục vụ tất cả các cuộc họp có khoảng thời gian `intervals = [start, end]`.
Bài toán này thực chất là tìm **số lượng cuộc họp đồng thời lớn nhất** tại bất kỳ thời điểm nào.
Có hai cách tiếp cận:
* **Cách 1**: Sử dụng **Min Heap** chứa thời gian kết thúc của các cuộc họp đang diễn ra. Khi có cuộc họp mới, ta giải phóng các phòng họp đã kết thúc (có `end <= new_start`). Số lượng phòng họp tối thiểu chính là kích thước lớn nhất của Heap trong quá trình duyệt.
* **Cách 2**: Tách biệt mảng `starts` và `ends`, sắp xếp độc lập hai mảng này. Sử dụng hai con trỏ duyệt song song. Nếu bắt đầu một cuộc họp mới trước khi cuộc họp cũ kết thúc, ta tăng số lượng phòng.

### 📊 Hướng tiếp cận tối ưu

#### Double Array Sorting (Optimal) ⭐
* **Mô tả**: Tách mảng start/end rồi sắp xếp độc lập giúp đạt hiệu năng cao hơn và tránh overhead của cấu trúc dữ liệu Heap.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ do sắp xếp hai mảng kích thước $n$.
  * **Space Complexity**: $O(n)$ để lưu hai mảng `starts` và `ends`.

### 🔄 Dry Run với ví dụ
* **Input**: `intervals = [[0, 30], [5, 10], [15, 20]]`
* **Tách mảng**:
  * `starts = [0, 5, 15]`
  * `ends = [10, 20, 30]`
* **Khởi tạo**: `startPtr = 0`, `endPtr = 0`, `rooms = 0`
* **Xử lý**:
  - `startPtr = 0`: `starts[0] = 0 < ends[0] = 10` → Cần thêm phòng. `rooms = 1`. `startPtr = 1`.
  - `startPtr = 1`: `starts[1] = 5 < ends[0] = 10` → Cần thêm phòng. `rooms = 2`. `startPtr = 2`.
  - `startPtr = 2`: `starts[2] = 15 >= ends[0] = 10` → Một phòng đã trống. Dịch `endPtr = 1`. `startPtr = 3` (hết).
* **Kết quả**: `2`

### 💻 Java Clean Code
```java
public int minMeetingRooms(int[][] intervals) {
    int n = intervals.length;
    int[] starts = new int[n];
    int[] ends = new int[n];
    
    for (int i = 0; i < n; i++) {
        starts[i] = intervals[i][0];
        ends[i] = intervals[i][1];
    }
    
    Arrays.sort(starts);
    Arrays.sort(ends);
    
    int startPtr = 0, endPtr = 0;
    int rooms = 0;
    
    while (startPtr < n) {
        // Nếu một phòng đã trống trước khi cuộc họp tiếp theo bắt đầu
        if (starts[startPtr] >= ends[endPtr]) {
            endPtr++; // Giải phóng phòng
        } else {
            rooms++; // Cần cấp thêm phòng
        }
        startPtr++;
    }
    
    return rooms;
}
```
