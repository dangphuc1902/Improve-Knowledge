import java.util.*;

/**
 * 10 - Heap / Priority Queue Solutions
 * Các bài: Kth Largest Element, Top K Frequent, Find Median from Data Stream,
 *          Task Scheduler, K Closest Points to Origin
 */
public class Solutions {

    // ============================================================
    // LC 215 - Kth Largest Element in an Array
    // Approach 1: Min-Heap size k — O(n log k) time, O(k) space ⭐
    // Approach 2: Quick Select — O(n) avg time, O(1) space
    // Approach 3: Sort — O(n log n) time, O(1) space
    // ============================================================
    static class KthLargestElement {
        // ⭐ Approach 1: Min-Heap size k
        // Idea: Heap chứa k phần tử lớn nhất, top là phần tử nhỏ nhất trong đó = kth largest
        public int findKthLargest(int[] nums, int k) {
            PriorityQueue<Integer> minHeap = new PriorityQueue<>();
            for (int num : nums) {
                minHeap.offer(num);
                if (minHeap.size() > k) minHeap.poll(); // loại min nếu size > k
            }
            return minHeap.peek(); // kth largest
        }

        // Approach 2: Quick Select — O(n) avg, O(n²) worst
        public int findKthLargestQuickSelect(int[] nums, int k) {
            int target = nums.length - k; // kth largest = (n-k)th smallest
            return quickSelect(nums, 0, nums.length - 1, target);
        }

        private int quickSelect(int[] nums, int lo, int hi, int target) {
            int pivot = partition(nums, lo, hi);
            if (pivot == target) return nums[pivot];
            else if (pivot < target) return quickSelect(nums, pivot + 1, hi, target);
            else return quickSelect(nums, lo, pivot - 1, target);
        }

        private int partition(int[] nums, int lo, int hi) {
            int pivot = nums[hi], i = lo;
            for (int j = lo; j < hi; j++) {
                if (nums[j] <= pivot) { swap(nums, i++, j); }
            }
            swap(nums, i, hi);
            return i;
        }

        private void swap(int[] nums, int i, int j) {
            int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp;
        }
    }

    // ============================================================
    // LC 347 - Top K Frequent Elements
    // Approach 1: Min-Heap — O(n log k) time ⭐
    // Approach 2: Bucket Sort — O(n) time
    // ============================================================
    static class TopKFrequentElements {
        // ⭐ Approach 1: HashMap + Min-Heap
        public int[] topKFrequent(int[] nums, int k) {
            Map<Integer, Integer> freq = new HashMap<>();
            for (int num : nums) freq.merge(num, 1, Integer::sum);

            // Min-Heap sắp xếp theo frequency
            PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

            for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                minHeap.offer(entry);
                if (minHeap.size() > k) minHeap.poll();
            }

            int[] result = new int[k];
            for (int i = k - 1; i >= 0; i--) result[i] = minHeap.poll().getKey();
            return result;
        }

        // Approach 2: Bucket Sort — O(n)
        // Idea: bucket[i] = list of numbers appearing i times
        public int[] topKFrequentBucket(int[] nums, int k) {
            Map<Integer, Integer> freq = new HashMap<>();
            for (int num : nums) freq.merge(num, 1, Integer::sum);

            @SuppressWarnings("unchecked")
            List<Integer>[] buckets = new List[nums.length + 1];
            for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                int f = entry.getValue();
                if (buckets[f] == null) buckets[f] = new ArrayList<>();
                buckets[f].add(entry.getKey());
            }

            int[] result = new int[k];
            int idx = 0;
            for (int i = buckets.length - 1; i >= 0 && idx < k; i--) {
                if (buckets[i] != null) {
                    for (int num : buckets[i]) {
                        if (idx < k) result[idx++] = num;
                    }
                }
            }
            return result;
        }
    }

    // ============================================================
    // LC 295 - Find Median from Data Stream (Hard)
    // Approach: Two Heaps — O(log n) add, O(1) findMedian ⭐
    // ============================================================
    static class MedianFinder {
        private PriorityQueue<Integer> lower; // max-heap: lower half
        private PriorityQueue<Integer> upper; // min-heap: upper half

        public MedianFinder() {
            lower = new PriorityQueue<>(Collections.reverseOrder());
            upper = new PriorityQueue<>();
        }

        // O(log n)
        public void addNum(int num) {
            lower.offer(num);
            // Balance: ensure lower.max <= upper.min
            upper.offer(lower.poll());
            // Maintain: lower.size() >= upper.size()
            if (lower.size() < upper.size()) lower.offer(upper.poll());
        }

        // O(1)
        public double findMedian() {
            if (lower.size() > upper.size()) return lower.peek();
            return (lower.peek() + upper.peek()) / 2.0;
        }
    }

    // ============================================================
    // LC 973 - K Closest Points to Origin
    // Approach 1: Max-Heap size k — O(n log k) time ⭐
    // Approach 2: Sort — O(n log n) time
    // Approach 3: Quick Select — O(n) avg time
    // ============================================================
    static class KClosestPoints {
        // ⭐ Approach 1: Max-Heap size k (by distance)
        // Heap giữ k điểm gần nhất, dùng Max-Heap để loại điểm xa nhất
        public int[][] kClosest(int[][] points, int k) {
            PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
                (a, b) -> dist(b) - dist(a) // max by distance
            );

            for (int[] point : points) {
                maxHeap.offer(point);
                if (maxHeap.size() > k) maxHeap.poll(); // loại điểm xa nhất
            }

            return maxHeap.toArray(new int[k][]);
        }

        private int dist(int[] point) {
            return point[0] * point[0] + point[1] * point[1]; // không cần sqrt
        }

        // Approach 2: Sort by distance — O(n log n)
        public int[][] kClosestSort(int[][] points, int k) {
            Arrays.sort(points, (a, b) -> dist(a) - dist(b));
            return Arrays.copyOfRange(points, 0, k);
        }
    }

    // ============================================================
    // LC 621 - Task Scheduler
    // Approach 1: Math formula — O(n) time ⭐
    // Approach 2: Simulation with Max-Heap — O(n log 26)
    // ============================================================
    static class TaskScheduler {
        // ⭐ Approach 1: Math formula
        // Idea: task nhiều nhất (maxFreq) quyết định số lần idle
        // result = max(tasks.length, (maxFreq - 1) * (n + 1) + countOfMaxFreq)
        public int leastInterval(char[] tasks, int n) {
            int[] freq = new int[26];
            for (char task : tasks) freq[task - 'A']++;
            Arrays.sort(freq);

            int maxFreq = freq[25];
            int countOfMax = 0;
            for (int f : freq) if (f == maxFreq) countOfMax++;

            int minTime = (maxFreq - 1) * (n + 1) + countOfMax;
            return Math.max(minTime, tasks.length);
        }

        // Approach 2: Simulation with Max-Heap + Queue
        public int leastIntervalSimulation(char[] tasks, int n) {
            int[] freq = new int[26];
            for (char task : tasks) freq[task - 'A']++;

            PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
            for (int f : freq) if (f > 0) maxHeap.offer(f);

            Queue<int[]> cooldown = new LinkedList<>(); // [remaining, availableTime]
            int time = 0;

            while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
                time++;
                if (!maxHeap.isEmpty()) {
                    int remaining = maxHeap.poll() - 1;
                    if (remaining > 0) cooldown.offer(new int[]{remaining, time + n});
                }
                if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
                    maxHeap.offer(cooldown.poll()[0]);
                }
            }
            return time;
        }
    }
}
