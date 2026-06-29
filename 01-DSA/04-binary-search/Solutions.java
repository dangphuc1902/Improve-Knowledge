import java.util.*;

/**
 * 04 - Binary Search Solutions
 * Các bài: Binary Search, Search in Rotated Array, Find Minimum in Rotated Array,
 *          Koko Eating Bananas, Find First and Last Position
 */
public class Solutions {

    // ============================================================
    // LC 704 - Binary Search
    // Approach 1: Iterative — O(log n) time, O(1) space ⭐
    // Approach 2: Recursive — O(log n) time, O(log n) space
    // ============================================================
    static class BinarySearch {
        // ⭐ Optimal: Iterative
        public int search(int[] nums, int target) {
            int lo = 0, hi = nums.length - 1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2; // tránh overflow
                if (nums[mid] == target) return mid;
                else if (nums[mid] < target) lo = mid + 1;
                else hi = mid - 1;
            }
            return -1;
        }

        // Approach 2: Recursive
        public int searchRecursive(int[] nums, int target) {
            return bsHelper(nums, target, 0, nums.length - 1);
        }

        private int bsHelper(int[] nums, int target, int lo, int hi) {
            if (lo > hi) return -1;
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            else if (nums[mid] < target) return bsHelper(nums, target, mid + 1, hi);
            else return bsHelper(nums, target, lo, mid - 1);
        }
    }

    // ============================================================
    // LC 33 - Search in Rotated Sorted Array
    // Approach 1: One-pass BS — O(log n) time, O(1) space ⭐
    // Approach 2: Find Pivot + BS — O(log n), O(1)
    // ============================================================
    static class SearchRotated {
        // ⭐ Optimal: One-pass Binary Search
        // Key: xác định nửa nào sorted, rồi check target thuộc đó không
        public int search(int[] nums, int target) {
            int lo = 0, hi = nums.length - 1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (nums[mid] == target) return mid;

                // Xác định nửa trái có sorted không
                if (nums[lo] <= nums[mid]) {
                    // Nửa trái [lo..mid] sorted
                    if (nums[lo] <= target && target < nums[mid]) {
                        hi = mid - 1; // target ở nửa trái
                    } else {
                        lo = mid + 1; // target ở nửa phải
                    }
                } else {
                    // Nửa phải [mid..hi] sorted
                    if (nums[mid] < target && target <= nums[hi]) {
                        lo = mid + 1; // target ở nửa phải
                    } else {
                        hi = mid - 1; // target ở nửa trái
                    }
                }
            }
            return -1;
        }

        // Approach 2: Find Pivot trước, rồi BS trong đúng phần
        public int searchWithPivot(int[] nums, int target) {
            int pivot = findPivot(nums);
            // Sau pivot, mảng chia thành 2 phần sorted
            if (target >= nums[pivot] && target <= nums[nums.length - 1]) {
                return binarySearch(nums, target, pivot, nums.length - 1);
            } else {
                return binarySearch(nums, target, 0, pivot - 1);
            }
        }

        private int findPivot(int[] nums) {
            int lo = 0, hi = nums.length - 1;
            while (lo < hi) {
                int mid = lo + (hi - lo) / 2;
                if (nums[mid] > nums[hi]) lo = mid + 1;
                else hi = mid;
            }
            return lo;
        }

        private int binarySearch(int[] nums, int target, int lo, int hi) {
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (nums[mid] == target) return mid;
                else if (nums[mid] < target) lo = mid + 1;
                else hi = mid - 1;
            }
            return -1;
        }
    }

    // ============================================================
    // LC 153 - Find Minimum in Rotated Sorted Array
    // Approach 1: Binary Search — O(log n) time, O(1) space ⭐
    // Approach 2: Linear Scan — O(n) time, O(1) space
    // ============================================================
    static class FindMinRotated {
        // ⭐ Optimal: Binary Search
        // Key: nếu nums[mid] > nums[hi] → minimum ở RIGHT
        //      ngược lại → minimum ở LEFT (bao gồm mid)
        public int findMin(int[] nums) {
            int lo = 0, hi = nums.length - 1;
            while (lo < hi) { // lo < hi: kết thúc khi lo==hi (1 phần tử)
                int mid = lo + (hi - lo) / 2;
                if (nums[mid] > nums[hi]) {
                    lo = mid + 1; // minimum ở right half
                } else {
                    hi = mid;     // minimum ở left half (inclusive mid)
                }
            }
            return nums[lo];
        }
    }

    // ============================================================
    // LC 875 - Koko Eating Bananas (Binary Search on Answer)
    // Approach 1: Binary Search on Speed — O(n log m) time ⭐
    // Approach 2: Linear Search — O(n * m) time
    // ============================================================
    static class KokoEatingBananas {
        // ⭐ Optimal: Binary Search on Answer
        // Search space: [1, max(piles)] — tốc độ ăn
        // isValid(k) = có thể ăn hết trong h giờ với tốc độ k không?
        public int minEatingSpeed(int[] piles, int h) {
            int lo = 1, hi = Arrays.stream(piles).max().getAsInt();
            while (lo < hi) {
                int mid = lo + (hi - lo) / 2;
                if (canFinish(piles, mid, h)) {
                    hi = mid;     // có thể → thử tốc độ nhỏ hơn
                } else {
                    lo = mid + 1; // không thể → cần tốc độ lớn hơn
                }
            }
            return lo;
        }

        private boolean canFinish(int[] piles, int speed, int h) {
            int hours = 0;
            for (int pile : piles) {
                hours += (pile + speed - 1) / speed; // ceiling division
            }
            return hours <= h;
        }
    }

    // ============================================================
    // LC 34 - Find First and Last Position of Element
    // Approach 1: Two Binary Searches — O(log n) time ⭐
    // Approach 2: Linear Scan — O(n) time
    // ============================================================
    static class FindFirstLastPosition {
        // ⭐ Optimal: Two Binary Searches (left bound + right bound)
        public int[] searchRange(int[] nums, int target) {
            return new int[]{findLeft(nums, target), findRight(nums, target)};
        }

        // Tìm vị trí đầu tiên của target
        private int findLeft(int[] nums, int target) {
            int lo = 0, hi = nums.length - 1, result = -1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (nums[mid] == target) {
                    result = mid;
                    hi = mid - 1; // tiếp tục tìm bên trái
                } else if (nums[mid] < target) lo = mid + 1;
                else hi = mid - 1;
            }
            return result;
        }

        // Tìm vị trí cuối cùng của target
        private int findRight(int[] nums, int target) {
            int lo = 0, hi = nums.length - 1, result = -1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (nums[mid] == target) {
                    result = mid;
                    lo = mid + 1; // tiếp tục tìm bên phải
                } else if (nums[mid] < target) lo = mid + 1;
                else hi = mid - 1;
            }
            return result;
        }
    }

    // ============================================================
    // LC 74 - Search a 2D Matrix
    // Approach 1: Binary Search trên matrix phẳng — O(log m*n) ⭐
    // Approach 2: Row + Column BS — O(log m + log n)
    // ============================================================
    static class Search2DMatrix {
        // ⭐ Optimal: Treat matrix as 1D sorted array
        // Row i, Col j → index = i * cols + j
        public boolean searchMatrix(int[][] matrix, int target) {
            int rows = matrix.length, cols = matrix[0].length;
            int lo = 0, hi = rows * cols - 1;

            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                int midVal = matrix[mid / cols][mid % cols];
                if (midVal == target) return true;
                else if (midVal < target) lo = mid + 1;
                else hi = mid - 1;
            }
            return false;
        }

        // Approach 2: BS row, then BS column
        public boolean searchMatrixTwoBs(int[][] matrix, int target) {
            int row = findRow(matrix, target);
            if (row == -1) return false;
            return Arrays.binarySearch(matrix[row], target) >= 0;
        }

        private int findRow(int[][] matrix, int target) {
            int lo = 0, hi = matrix.length - 1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (matrix[mid][0] <= target && target <= matrix[mid][matrix[mid].length - 1]) return mid;
                else if (matrix[mid][0] > target) hi = mid - 1;
                else lo = mid + 1;
            }
            return -1;
        }
    }
}
