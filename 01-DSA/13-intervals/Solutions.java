import java.util.*;

/**
 * =============================================
 *  13 - INTERVALS
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Merge Intervals (LeetCode #56) - Medium
// -----------------------------------------------
// Cho danh sách intervals, merge tất cả overlapping intervals.
// Ví dụ: [[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]
//
// Approach:
// 1. Sort theo start
// 2. Duyệt: nếu interval hiện tại overlap với interval cuối → merge
//           nếu không → thêm vào kết quả
//
// Time: O(n log n) - sorting
// Space: O(n) - output
class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        // Sort theo start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        List<int[]> merged = new ArrayList<>();
        merged.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] last = merged.get(merged.size() - 1);

            if (intervals[i][0] <= last[1]) {
                // Overlap → merge (mở rộng end)
                last[1] = Math.max(last[1], intervals[i][1]);
            } else {
                // Không overlap → thêm interval mới
                merged.add(intervals[i]);
            }
        }

        return merged.toArray(new int[0][]);
    }
}

// -----------------------------------------------
// Bài 2: Insert Interval (LeetCode #57) - Medium
// -----------------------------------------------
// Cho danh sách intervals đã sorted + 1 newInterval, chèn vào và merge.
// Ví dụ: intervals = [[1,3],[6,9]], newInterval = [2,5] → [[1,5],[6,9]]
//
// Approach: Chia 3 phần:
// 1. Intervals kết thúc TRƯỚC newInterval
// 2. Intervals overlap với newInterval → merge
// 3. Intervals bắt đầu SAU newInterval
//
// Time: O(n) - duyệt 1 lần (input đã sorted)
// Space: O(n) - output
class InsertInterval {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        int n = intervals.length;

        // Phần 1: Intervals kết thúc trước newInterval
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Phần 2: Merge overlapping intervals
        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval); // Thêm interval đã merge

        // Phần 3: Intervals bắt đầu sau newInterval
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[0][]);
    }
}

// -----------------------------------------------
// Bài 3: Non-overlapping Intervals (LeetCode #435) - Medium
// -----------------------------------------------
// Tìm số interval tối thiểu cần xóa để không còn overlap.
// Ví dụ: [[1,2],[2,3],[3,4],[1,3]] → 1 (xóa [1,3])
//
// Approach: Greedy - sort theo end time.
// Luôn giữ interval kết thúc sớm nhất → để lại nhiều "room" nhất cho sau.
// Đếm số interval GIỮA được (non-overlapping) → xóa = total - giữ.
//
// Time: O(n log n) - sorting
// Space: O(1)
class NonOverlappingIntervals {
    public int eraseOverlapIntervals(int[][] intervals) {
        // Sort theo end time (greedy: ưu tiên kết thúc sớm)
        Arrays.sort(intervals, (a, b) -> a[1] - b[1]);

        int kept = 1; // Giữ interval đầu tiên
        int prevEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= prevEnd) {
                // Không overlap → giữ
                kept++;
                prevEnd = intervals[i][1];
            }
            // Overlap → bỏ interval này (vì end lớn hơn không tốt bằng)
        }

        return intervals.length - kept; // Số cần xóa
    }
}
