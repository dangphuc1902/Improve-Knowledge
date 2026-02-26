import java.util.*;

/**
 * =============================================
 *  12 - 1-D DYNAMIC PROGRAMMING
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Climbing Stairs (LeetCode #70) - Easy
// -----------------------------------------------
// Có n bậc thang, mỗi lần bước 1 hoặc 2 bậc. Có bao nhiêu cách lên đỉnh?
//
// Approach: DP Fibonacci-like.
// dp[i] = số cách đến bậc i = dp[i-1] + dp[i-2]
// (đến bậc i bằng cách bước 1 từ i-1 HOẶC bước 2 từ i-2)
//
// Tối ưu: chỉ cần 2 biến prev1, prev2 thay vì mảng.
//
// Time: O(n)
// Space: O(1) - tối ưu từ O(n)
class ClimbingStairs {
    public int climbStairs(int n) {
        if (n <= 2) return n;

        int prev2 = 1; // dp[1] = 1 cách
        int prev1 = 2; // dp[2] = 2 cách

        for (int i = 3; i <= n; i++) {
            int curr = prev1 + prev2; // dp[i] = dp[i-1] + dp[i-2]
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}

// -----------------------------------------------
// Bài 2: House Robber (LeetCode #198) - Medium
// -----------------------------------------------
// Mảng nums[i] = tiền trong nhà i. Không được cướp 2 nhà liên tiếp.
// Tìm số tiền tối đa có thể cướp.
// Ví dụ: [1,2,3,1] → 4 (cướp nhà 0 và nhà 2)
//
// Approach: DP Take-or-Skip.
// dp[i] = max tiền cướp được từ nhà 0..i
// dp[i] = max(dp[i-1], dp[i-2] + nums[i])
//         = max(không cướp nhà i, cướp nhà i + skip nhà i-1)
//
// Time: O(n)
// Space: O(1)
class HouseRobber {
    public int rob(int[] nums) {
        if (nums.length == 1) return nums[0];

        int prev2 = 0;       // dp[i-2]
        int prev1 = nums[0]; // dp[i-1]

        for (int i = 1; i < nums.length; i++) {
            int curr = Math.max(
                prev1,              // Không cướp nhà i
                prev2 + nums[i]     // Cướp nhà i (skip nhà i-1)
            );
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}

// -----------------------------------------------
// Bài 3: Longest Increasing Subsequence (LeetCode #300) - Medium
// -----------------------------------------------
// Tìm độ dài dãy con tăng dài nhất (LIS).
// Ví dụ: [10,9,2,5,3,7,101,18] → 4 ([2,3,7,101])
//
// Approach 1: DP O(n²)
// dp[i] = LIS kết thúc tại index i
// Với mỗi j < i mà nums[j] < nums[i]: dp[i] = max(dp[i], dp[j] + 1)
//
// Approach 2: Binary Search O(n log n) - tối ưu hơn
// Duy trì mảng tails[]: tails[i] = phần tử nhỏ nhất kết thúc LIS độ dài i+1
//
// Time: O(n²) cho DP, O(n log n) cho Binary Search
// Space: O(n)
class LongestIncreasingSubsequence {

    // Cách 1: DP O(n²) — dễ hiểu
    public int lengthOfLIS_DP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1); // Mỗi phần tử tự nó là LIS dài 1

        int maxLen = 1;
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }

        return maxLen;
    }

    // Cách 2: Binary Search O(n log n) — tối ưu
    public int lengthOfLIS(int[] nums) {
        List<Integer> tails = new ArrayList<>();

        for (int num : nums) {
            int pos = Collections.binarySearch(tails, num);
            if (pos < 0) pos = -(pos + 1); // Vị trí chèn

            if (pos == tails.size()) {
                tails.add(num); // Mở rộng LIS
            } else {
                tails.set(pos, num); // Thay thế để giữ tails nhỏ nhất
            }
        }

        return tails.size();
    }
}
