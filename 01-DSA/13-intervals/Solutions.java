import java.util.*;

/**
 * 13 - Intervals Solutions
 * Các bài: Merge Intervals, Insert Interval, Non-Overlapping, Meeting Rooms II
 */
public class Solutions {

    // ============================================================
    // LC 56 - Merge Intervals
    // Approach 1: Sort + Linear Scan — O(n log n) time ⭐
    // ============================================================
    static class MergeIntervals {
        // ⭐ Optimal: Sort by start, merge overlapping
        public int[][] merge(int[][] intervals) {
            Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
            List<int[]> result = new ArrayList<>();
            result.add(intervals[0]);

            for (int i = 1; i < intervals.length; i++) {
                int[] last = result.get(result.size() - 1);
                int[] curr = intervals[i];
                if (curr[0] <= last[1]) {
                    last[1] = Math.max(last[1], curr[1]); // extend end
                } else {
                    result.add(curr);
                }
            }
            return result.toArray(new int[result.size()][]);
        }
    }

    // ============================================================
    // LC 57 - Insert Interval
    // Approach 1: 3-phase scan — O(n) time ⭐
    // Approach 2: Binary Search + Merge — O(n) time
    // ============================================================
    static class InsertInterval {
        // ⭐ Optimal: 3 phases (before, overlap, after)
        public int[][] insert(int[][] intervals, int[] newInterval) {
            List<int[]> result = new ArrayList<>();
            int i = 0, n = intervals.length;

            // Phase 1: intervals hoàn toàn TRƯỚC newInterval
            while (i < n && intervals[i][1] < newInterval[0]) {
                result.add(intervals[i++]);
            }

            // Phase 2: intervals OVERLAP với newInterval → merge
            while (i < n && intervals[i][0] <= newInterval[1]) {
                newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
                newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
                i++;
            }
            result.add(newInterval);

            // Phase 3: intervals hoàn toàn SAU newInterval
            while (i < n) {
                result.add(intervals[i++]);
            }

            return result.toArray(new int[result.size()][]);
        }
    }

    // ============================================================
    // LC 435 - Non-overlapping Intervals
    // Approach 1: Greedy (sort by end) — O(n log n) time ⭐
    // Approach 2: Sort by start + DP — O(n²)
    // ============================================================
    static class NonOverlappingIntervals {
        // ⭐ Greedy: Sort by end, greedily keep intervals ending earliest
        public int eraseOverlapIntervals(int[][] intervals) {
            Arrays.sort(intervals, (a, b) -> a[1] - b[1]); // sort by END
            int keep = 1, lastEnd = intervals[0][1];

            for (int i = 1; i < intervals.length; i++) {
                if (intervals[i][0] >= lastEnd) { // no overlap
                    keep++;
                    lastEnd = intervals[i][1];
                }
                // else: overlap → skip (remove) this interval
            }
            return intervals.length - keep;
        }
    }

    // ============================================================
    // LC 252/253 - Meeting Rooms I & II
    // ============================================================
    static class MeetingRooms {
        // Meeting Rooms I: Can one person attend all? O(n log n) ⭐
        public boolean canAttendMeetings(int[][] intervals) {
            Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
            for (int i = 1; i < intervals.length; i++) {
                if (intervals[i][0] < intervals[i - 1][1]) return false;
            }
            return true;
        }

        // Meeting Rooms II: Min rooms needed — O(n log n) ⭐
        // Approach 1: Sort starts/ends separately
        public int minMeetingRooms(int[][] intervals) {
            int n = intervals.length;
            int[] starts = new int[n], ends = new int[n];
            for (int i = 0; i < n; i++) { starts[i] = intervals[i][0]; ends[i] = intervals[i][1]; }
            Arrays.sort(starts);
            Arrays.sort(ends);

            int rooms = 0, endIdx = 0;
            for (int startIdx = 0; startIdx < n; startIdx++) {
                if (starts[startIdx] < ends[endIdx]) {
                    rooms++; // cần thêm phòng
                } else {
                    endIdx++; // một phòng được giải phóng
                }
            }
            return rooms;
        }

        // Approach 2: Min-Heap (track end time của mỗi phòng)
        public int minMeetingRoomsHeap(int[][] intervals) {
            Arrays.sort(intervals, (a, b) -> a[0] - b[0]); // sort by start
            PriorityQueue<Integer> heap = new PriorityQueue<>(); // min-heap of end times

            for (int[] interval : intervals) {
                if (!heap.isEmpty() && heap.peek() <= interval[0]) {
                    heap.poll(); // phòng này available → reuse
                }
                heap.offer(interval[1]); // assign room, end = interval[1]
            }
            return heap.size(); // số phòng đang sử dụng
        }
    }
}
