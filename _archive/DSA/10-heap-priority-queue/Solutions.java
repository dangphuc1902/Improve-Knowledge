import java.util.*;

/**
 * =============================================
 *  10 - HEAP / PRIORITY QUEUE
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Kth Largest Element in a Stream (LeetCode #703) - Easy
// -----------------------------------------------
// Design class lưu trữ stream số nguyên, hỗ trợ thêm số và trả về
// phần tử lớn thứ k tại mọi thời điểm.
//
// Approach: Min Heap size k.
// Giữ k phần tử lớn nhất trong heap → root (min) chính là kth largest.
//
// Time: O(log k) per add
// Space: O(k)
class KthLargest {
    private PriorityQueue<Integer> minHeap;
    private int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>();

        for (int num : nums) {
            add(num);
        }
    }

    public int add(int val) {
        minHeap.offer(val);

        // Giữ heap size = k (bỏ phần tử nhỏ)
        if (minHeap.size() > k) {
            minHeap.poll();
        }

        // Root của min heap = kth largest
        return minHeap.peek();
    }
}

// -----------------------------------------------
// Bài 2: Top K Frequent Elements (LeetCode #347) - Medium
// -----------------------------------------------
// Cho mảng nums và k, trả về k phần tử xuất hiện nhiều nhất.
// (Giải bằng Heap, khác với giải bằng Bucket Sort ở topic 01)
//
// Approach: HashMap đếm freq + Min Heap size k.
// 1. Đếm frequency qua HashMap
// 2. Dùng min heap size k, so sánh theo frequency
// 3. Heap giữ lại k phần tử có freq cao nhất
//
// Time: O(n log k) - n phần tử, mỗi cái push/pop O(log k)
// Space: O(n) - HashMap + O(k) heap
class TopKFrequentHeap {
    public int[] topKFrequent(int[] nums, int k) {
        // Bước 1: Đếm frequency
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // Bước 2: Min Heap theo frequency, giữ top k
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[1] - b[1]);

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            minHeap.offer(new int[]{entry.getKey(), entry.getValue()});
            if (minHeap.size() > k) {
                minHeap.poll(); // Bỏ phần tử freq thấp nhất
            }
        }

        // Bước 3: Thu thập kết quả
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll()[0];
        }

        return result;
    }
}

// -----------------------------------------------
// Bài 3: Find Median from Data Stream (LeetCode #295) - Hard
// -----------------------------------------------
// Design class nhận stream số, trả về median tại mọi thời điểm.
// Median: giá trị giữa (sorted). Nếu n chẵn = trung bình 2 giá trị giữa.
//
// Approach: Two Heaps.
// - maxHeap: chứa nửa nhỏ (top = max của nửa nhỏ)
// - minHeap: chứa nửa lớn (top = min của nửa lớn)
// - Balance: |maxHeap.size() - minHeap.size()| ≤ 1
// - Median = maxHeap.peek() hoặc average(maxHeap.peek(), minHeap.peek())
//
// Time: O(log n) per addNum, O(1) per findMedian
// Space: O(n) - 2 heaps
class MedianFinder {
    private PriorityQueue<Integer> maxHeap; // Nửa nhỏ (max heap)
    private PriorityQueue<Integer> minHeap; // Nửa lớn (min heap)

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        // Luôn thêm vào maxHeap (nửa nhỏ) trước
        maxHeap.offer(num);

        // Đảm bảo: max của nửa nhỏ ≤ min của nửa lớn
        minHeap.offer(maxHeap.poll());

        // Balance: maxHeap.size >= minHeap.size
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek(); // n lẻ → median ở maxHeap
        }
        // n chẵn → trung bình 2 giá trị giữa
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}
