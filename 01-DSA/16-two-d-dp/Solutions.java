import java.util.*;

/**
 * 16 - 2D Dynamic Programming Solutions
 * Các bài: Unique Paths, Minimum Path Sum, Longest Common Subsequence,
 *          Edit Distance, Burst Balloons, Partition Equal Subset Sum
 */
public class Solutions {

    // ============================================================
    // LC 62 - Unique Paths
    // Approach 1: 1D DP (space optimized) — O(m*n), O(n) ⭐
    // Approach 2: Math Combinatorics — O(m+n), O(1)
    // ============================================================
    static class UniquePaths {
        // ⭐ Approach 1: 1D DP — dp[j] = ways to reach current row, col j
        public int uniquePaths(int m, int n) {
            int[] dp = new int[n];
            Arrays.fill(dp, 1); // first row: all 1s
            for (int i = 1; i < m; i++) {
                for (int j = 1; j < n; j++) {
                    dp[j] += dp[j - 1]; // from above + from left
                }
            }
            return dp[n - 1];
        }

        // Approach 2: Math — C(m+n-2, m-1) = (m+n-2)! / (m-1)!(n-1)!
        public int uniquePathsMath(int m, int n) {
            long result = 1;
            for (int i = 0; i < m - 1; i++) {
                result = result * (n + i) / (i + 1);
            }
            return (int) result;
        }

        // Approach 3: 2D DP — explicit, easier to understand
        public int uniquePaths2D(int m, int n) {
            int[][] dp = new int[m][n];
            for (int i = 0; i < m; i++) dp[i][0] = 1; // first col
            for (int j = 0; j < n; j++) dp[0][j] = 1; // first row
            for (int i = 1; i < m; i++) {
                for (int j = 1; j < n; j++) {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                }
            }
            return dp[m - 1][n - 1];
        }
    }

    // ============================================================
    // LC 64 - Minimum Path Sum
    // Approach: DP (modify grid in-place or 1D dp) — O(m*n), O(1) ⭐
    // ============================================================
    static class MinimumPathSum {
        // ⭐ Modify grid in-place — O(1) space
        public int minPathSum(int[][] grid) {
            int m = grid.length, n = grid[0].length;
            // Init first row and first col
            for (int j = 1; j < n; j++) grid[0][j] += grid[0][j - 1];
            for (int i = 1; i < m; i++) grid[i][0] += grid[i - 1][0];
            // Fill rest
            for (int i = 1; i < m; i++) {
                for (int j = 1; j < n; j++) {
                    grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
                }
            }
            return grid[m - 1][n - 1];
        }
    }

    // ============================================================
    // LC 1143 - Longest Common Subsequence
    // Approach 1: 2D DP — O(m*n), O(m*n) ⭐
    // Approach 2: Space Optimized 1D — O(m*n), O(n)
    // ============================================================
    static class LongestCommonSubsequence {
        // ⭐ Approach 1: 2D DP
        // dp[i][j] = LCS length của text1[0..i-1] và text2[0..j-1]
        public int longestCommonSubsequence(String text1, String text2) {
            int m = text1.length(), n = text2.length();
            int[][] dp = new int[m + 1][n + 1];

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                        dp[i][j] = dp[i - 1][j - 1] + 1;
                    } else {
                        dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                    }
                }
            }
            return dp[m][n];
        }

        // Approach 2: Space Optimized với 2 rows
        public int longestCommonSubsequenceOpt(String text1, String text2) {
            int m = text1.length(), n = text2.length();
            int[] prev = new int[n + 1], curr = new int[n + 1];

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                        curr[j] = prev[j - 1] + 1;
                    } else {
                        curr[j] = Math.max(prev[j], curr[j - 1]);
                    }
                }
                int[] temp = prev; prev = curr; curr = temp; // swap
                Arrays.fill(curr, 0);
            }
            return prev[n];
        }
    }

    // ============================================================
    // LC 72 - Edit Distance
    // Approach 1: 2D DP — O(m*n), O(m*n) ⭐
    // Approach 2: Space Optimized — O(m*n), O(n)
    // ============================================================
    static class EditDistance {
        // ⭐ 2D DP
        // dp[i][j] = min ops để chuyển word1[0..i-1] → word2[0..j-1]
        // 3 ops: Insert, Delete, Replace
        public int minDistance(String word1, String word2) {
            int m = word1.length(), n = word2.length();
            int[][] dp = new int[m + 1][n + 1];

            // Base cases
            for (int i = 0; i <= m; i++) dp[i][0] = i; // delete i chars
            for (int j = 0; j <= n; j++) dp[0][j] = j; // insert j chars

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                        dp[i][j] = dp[i - 1][j - 1]; // same char, 0 ops
                    } else {
                        dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1], // replace
                            Math.min(dp[i - 1][j], dp[i][j - 1]) // delete, insert
                        );
                    }
                }
            }
            return dp[m][n];
        }
    }

    // ============================================================
    // LC 416 - Partition Equal Subset Sum (0/1 Knapsack)
    // Approach 1: 1D Knapsack DP — O(n * sum) time, O(sum) space ⭐
    // Approach 2: 2D DP — O(n * sum), O(n * sum)
    // ============================================================
    static class PartitionEqualSubsetSum {
        // ⭐ 1D Knapsack DP (traverse backward to avoid reuse)
        // dp[j] = true nếu có thể tạo sum = j từ các nums đã xét
        public boolean canPartition(int[] nums) {
            int total = 0;
            for (int num : nums) total += num;
            if (total % 2 != 0) return false;

            int target = total / 2;
            boolean[] dp = new boolean[target + 1];
            dp[0] = true;

            for (int num : nums) {
                // Traverse backward để tránh dùng num 2 lần (0/1 Knapsack)
                for (int j = target; j >= num; j--) {
                    dp[j] = dp[j] || dp[j - num];
                }
            }
            return dp[target];
        }
    }

    // ============================================================
    // LC 312 - Burst Balloons (Hard — Interval DP)
    // Approach: Interval DP — O(n³) time, O(n²) space ⭐
    // ============================================================
    static class BurstBalloons {
        // ⭐ Interval DP — dp[i][j] = max coins from bursting balloons i..j
        // Key insight: think about LAST balloon to burst (not first)
        public int maxCoins(int[] nums) {
            int n = nums.length;
            int[] balloons = new int[n + 2];
            balloons[0] = balloons[n + 1] = 1;
            for (int i = 0; i < n; i++) balloons[i + 1] = nums[i];

            int m = n + 2;
            int[][] dp = new int[m][m];

            // length = window size
            for (int len = 2; len < m; len++) {
                for (int left = 0; left < m - len; left++) {
                    int right = left + len;
                    // k = last balloon to burst in window (left, right)
                    for (int k = left + 1; k < right; k++) {
                        dp[left][right] = Math.max(dp[left][right],
                            dp[left][k] + dp[k][right] +
                            balloons[left] * balloons[k] * balloons[right]);
                    }
                }
            }
            return dp[0][m - 1];
        }
    }
}
