import java.util.*;

/**
 * =============================================
 *  14 - GREEDY
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Maximum Subarray (LeetCode #53) - Medium
// -----------------------------------------------
// Tìm subarray liên tiếp có tổng lớn nhất.
// Ví dụ: [-2,1,-3,4,-1,2,1,-5,4] → 6 ([4,-1,2,1])
//
// Approach: Kadane's Algorithm (Greedy).
// Tại mỗi vị trí: quyết định mở rộng subarray cũ hay bắt đầu mới.
// currentSum = max(num, currentSum + num)
// Nếu currentSum + num < num → bỏ prefix, bắt đầu lại từ num.
//
// Time: O(n)
// Space: O(1)
class MaximumSubarray {
    public int maxSubArray(int[] nums) {
        int maxSum = nums[0];
        int currentSum = 0;

        for (int num : nums) {
            // Nếu currentSum âm → bỏ, bắt đầu lại từ num
            currentSum = Math.max(num, currentSum + num);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }
}

// -----------------------------------------------
// Bài 2: Jump Game (LeetCode #55) - Medium
// -----------------------------------------------
// Mảng nums[i] = bước nhảy tối đa từ vị trí i.
// Hỏi: có thể nhảy đến vị trí cuối không?
// Ví dụ: [2,3,1,1,4] → true, [3,2,1,0,4] → false
//
// Approach: Greedy - track vị trí xa nhất có thể đến.
// Nếu tại bất kỳ i mà i > farthest → không thể đến i → false.
//
// Time: O(n)
// Space: O(1)
class JumpGame {
    public boolean canJump(int[] nums) {
        int farthest = 0;

        for (int i = 0; i < nums.length; i++) {
            // Không thể đến vị trí i
            if (i > farthest) return false;

            // Cập nhật vị trí xa nhất có thể đến
            farthest = Math.max(farthest, i + nums[i]);

            // Đã đến được cuối mảng
            if (farthest >= nums.length - 1) return true;
        }

        return true;
    }
}

// -----------------------------------------------
// Bài 3: Gas Station (LeetCode #134) - Medium
// -----------------------------------------------
// N trạm xăng vòng tròn. gas[i] = xăng nhận, cost[i] = xăng tiêu thụ đến trạm tiếp.
// Tìm trạm xuất phát để đi hết vòng. Trả về index hoặc -1.
//
// Approach: Greedy.
// 1. Nếu tổng gas < tổng cost → impossible (-1)
// 2. Nếu possible, duyệt: khi currentSurplus < 0 → trạm xuất phát = i+1
//    (vì bất kỳ trạm nào trước đó cũng sẽ thất bại)
//
// Time: O(n)
// Space: O(1)
class GasStation {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int totalSurplus = 0;   // Tổng xăng dư/thiếu toàn bộ
        int currentSurplus = 0; // Xăng dư/thiếu từ start đến hiện tại
        int start = 0;          // Trạm xuất phát ứng viên

        for (int i = 0; i < gas.length; i++) {
            int surplus = gas[i] - cost[i];
            totalSurplus += surplus;
            currentSurplus += surplus;

            // Không thể đi tiếp từ start → thử start mới
            if (currentSurplus < 0) {
                start = i + 1;
                currentSurplus = 0; // Reset
            }
        }

        // Nếu tổng xăng đủ → start hợp lệ, ngược lại → -1
        return totalSurplus >= 0 ? start : -1;
    }
}
