import java.util.*;

/**
 * =============================================
 *  04 - BINARY SEARCH
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Binary Search (LeetCode #704) - Easy
// -----------------------------------------------
// Tìm target trong mảng sorted. Trả về index hoặc -1.
//
// Approach: Binary Search cơ bản, chia đôi phạm vi tìm kiếm mỗi bước.
//
// Time: O(log n)
// Space: O(1)
class BinarySearch {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;  // Tránh integer overflow

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;   // Target nằm bên phải
            } else {
                right = mid - 1;  // Target nằm bên trái
            }
        }

        return -1; // Không tìm thấy
    }
}

// -----------------------------------------------
// Bài 2: Search in Rotated Sorted Array (LeetCode #33) - Medium
// -----------------------------------------------
// Mảng sorted bị rotate (vd: [4,5,6,7,0,1,2]), tìm target.
//
// Approach: Binary Search biến thể.
// Key insight: Sau khi chia đôi, luôn có ít nhất 1 nửa sorted.
// Xác định nửa sorted → check target có nằm trong nửa sorted đó không.
//
// Time: O(log n)
// Space: O(1)
class SearchRotatedArray {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return mid;

            // Xác định nửa nào sorted
            if (nums[left] <= nums[mid]) {
                // Nửa trái [left..mid] sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1; // Target trong nửa trái
                } else {
                    left = mid + 1;  // Target trong nửa phải
                }
            } else {
                // Nửa phải [mid..right] sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;  // Target trong nửa phải
                } else {
                    right = mid - 1; // Target trong nửa trái
                }
            }
        }

        return -1;
    }
}

// -----------------------------------------------
// Bài 3: Find Minimum in Rotated Sorted Array (LeetCode #153) - Medium
// -----------------------------------------------
// Tìm giá trị nhỏ nhất trong mảng sorted bị rotate.
// Ví dụ: [3,4,5,1,2] → 1
//
// Approach: Binary Search.
// So sánh mid với right:
// - nums[mid] > nums[right]: min nằm bên phải (có rotation)
// - nums[mid] <= nums[right]: min nằm bên trái hoặc tại mid
//
// Time: O(log n)
// Space: O(1)
class FindMinRotated {
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Điểm rotation nằm bên phải mid
                // Min chắc chắn ở khoảng [mid+1, right]
                left = mid + 1;
            } else {
                // Mid có thể là min, hoặc min ở bên trái
                // Thu hẹp: [left, mid]
                right = mid;
            }
        }

        // left == right = vị trí của min
        return nums[left];
    }
}
