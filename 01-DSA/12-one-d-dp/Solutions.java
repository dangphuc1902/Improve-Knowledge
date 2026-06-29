import java.util.*;

/**
 * 12 - 1D Dynamic Programming Solutions
 * Các bài: Climbing Stairs, Coin Change, House Robber, Longest Palindromic Substring,
 *          Word Break, Fibonacci, Decode Ways
 */
public class Solutions {

    // ============================================================
    // LC 70 - Climbing Stairs
    // Approach 1: Space Optimized DP — O(n), O(1) ⭐
    // Approach 2: Tabulation — O(n), O(n)
    // Approach 3: Top-Down Memoization — O(n), O(n)
    // ============================================================
    static class ClimbingStairs {
        // ⭐ Approach 1: Space Optimized (Fibonacci style)
        public int climbStairs(int n) {
            if (n <= 2) return n;
            int prev2 = 1, prev1 = 2;
            for (int i = 3; i <= n; i++) {
                int curr = prev1 + prev2;
                prev2 = prev1;
                prev1 = curr;
            }
            return prev1;
        }

        // Approach 2: Tabulation
        public int climbStairsTab(int n) {
            int[] dp = new int[n + 1];
            dp[1] = 1; dp[2] = 2;
            for (int i = 3; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
            return dp[n];
        }

        // Approach 3: Top-Down Memoization
        private int[] memo;
        public int climbStairsMemo(int n) {
            memo = new int[n + 1];
            return helper(n);
        }
        private int helper(int n) {
            if (n <= 2) return n;
            if (memo[n] != 0) return memo[n];
            memo[n] = helper(n - 1) + helper(n - 2);
            return memo[n];
        }
    }

    // ============================================================
    // LC 322 - Coin Change
    // Approach 1: Bottom-Up DP — O(amount * coins), O(amount) ⭐
    // Approach 2: Top-Down Memoization — O(amount * coins), O(amount)
    // Approach 3: BFS — O(amount * coins), O(amount)
    // ============================================================
    static class CoinChange {
        // ⭐ Approach 1: Bottom-Up Tabulation
        // dp[i] = min coins để đổi amount = i
        public int coinChange(int[] coins, int amount) {
            int[] dp = new int[amount + 1];
            Arrays.fill(dp, amount + 1); // infinity
            dp[0] = 0;

            for (int i = 1; i <= amount; i++) {
                for (int coin : coins) {
                    if (coin <= i) {
                        dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                    }
                }
            }
            return dp[amount] > amount ? -1 : dp[amount];
        }

        // Approach 2: Top-Down Memoization
        public int coinChangeMemo(int[] coins, int amount) {
            int[] memo = new int[amount + 1];
            Arrays.fill(memo, -1);
            int result = dfs(coins, amount, memo);
            return result == Integer.MAX_VALUE ? -1 : result;
        }

        private int dfs(int[] coins, int amount, int[] memo) {
            if (amount == 0) return 0;
            if (amount < 0) return Integer.MAX_VALUE;
            if (memo[amount] != -1) return memo[amount];

            int min = Integer.MAX_VALUE;
            for (int coin : coins) {
                int sub = dfs(coins, amount - coin, memo);
                if (sub != Integer.MAX_VALUE) min = Math.min(min, sub + 1);
            }
            return memo[amount] = min;
        }

        // Approach 3: BFS (shortest path in graph where each node = remaining amount)
        public int coinChangeBFS(int[] coins, int amount) {
            if (amount == 0) return 0;
            boolean[] visited = new boolean[amount + 1];
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(amount);
            visited[amount] = true;
            int steps = 0;

            while (!queue.isEmpty()) {
                steps++;
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    int curr = queue.poll();
                    for (int coin : coins) {
                        int next = curr - coin;
                        if (next == 0) return steps;
                        if (next > 0 && !visited[next]) {
                            visited[next] = true;
                            queue.offer(next);
                        }
                    }
                }
            }
            return -1;
        }
    }

    // ============================================================
    // LC 198 - House Robber
    // Approach 1: Space Optimized — O(n), O(1) ⭐
    // Approach 2: Tabulation — O(n), O(n)
    // ============================================================
    static class HouseRobber {
        // ⭐ Approach 1: Space Optimized
        // dp[i] = max(skip house i = dp[i-1], rob house i = dp[i-2] + nums[i])
        public int rob(int[] nums) {
            int rob = 0, skip = 0;
            for (int num : nums) {
                int newRob = skip + num;
                int newSkip = Math.max(skip, rob);
                rob = newRob;
                skip = newSkip;
            }
            return Math.max(rob, skip);
        }

        // Approach 2: Tabulation — clearer state definition
        public int robTab(int[] nums) {
            int n = nums.length;
            if (n == 1) return nums[0];
            int[] dp = new int[n];
            dp[0] = nums[0];
            dp[1] = Math.max(nums[0], nums[1]);
            for (int i = 2; i < n; i++) {
                dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
            }
            return dp[n - 1];
        }
    }

    // ============================================================
    // LC 213 - House Robber II (circular array)
    // Approach: Run House Robber twice (skip first or last) — O(n) ⭐
    // ============================================================
    static class HouseRobberII {
        public int rob(int[] nums) {
            if (nums.length == 1) return nums[0];
            // Case 1: skip last house, rob from [0, n-2]
            // Case 2: skip first house, rob from [1, n-1]
            return Math.max(
                robRange(nums, 0, nums.length - 2),
                robRange(nums, 1, nums.length - 1)
            );
        }

        private int robRange(int[] nums, int start, int end) {
            int rob = 0, skip = 0;
            for (int i = start; i <= end; i++) {
                int newRob = skip + nums[i];
                int newSkip = Math.max(skip, rob);
                rob = newRob;
                skip = newSkip;
            }
            return Math.max(rob, skip);
        }
    }

    // ============================================================
    // LC 139 - Word Break
    // Approach 1: Bottom-Up DP — O(n² * L), O(n) ⭐
    // Approach 2: Top-Down Memoization — O(n² * L), O(n)
    // ============================================================
    static class WordBreak {
        // ⭐ Approach 1: Bottom-Up
        // dp[i] = s[0..i-1] có thể partition thành words trong dict không?
        public boolean wordBreak(String s, List<String> wordDict) {
            Set<String> dict = new HashSet<>(wordDict);
            boolean[] dp = new boolean[s.length() + 1];
            dp[0] = true; // empty string

            for (int i = 1; i <= s.length(); i++) {
                for (int j = 0; j < i; j++) {
                    if (dp[j] && dict.contains(s.substring(j, i))) {
                        dp[i] = true;
                        break;
                    }
                }
            }
            return dp[s.length()];
        }
    }

    // ============================================================
    // LC 5 - Longest Palindromic Substring
    // Approach 1: Expand Around Center — O(n²), O(1) ⭐
    // Approach 2: DP — O(n²), O(n²)
    // Approach 3: Manacher's — O(n), O(n)
    // ============================================================
    static class LongestPalindromicSubstring {
        // ⭐ Approach 1: Expand Around Center — best O(1) space
        public String longestPalindrome(String s) {
            int start = 0, maxLen = 1;
            for (int i = 0; i < s.length(); i++) {
                // Odd length (center at i)
                int len1 = expandAroundCenter(s, i, i);
                // Even length (center between i and i+1)
                int len2 = expandAroundCenter(s, i, i + 1);
                int len = Math.max(len1, len2);
                if (len > maxLen) {
                    maxLen = len;
                    start = i - (len - 1) / 2;
                }
            }
            return s.substring(start, start + maxLen);
        }

        private int expandAroundCenter(String s, int left, int right) {
            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
            }
            return right - left - 1;
        }

        // Approach 2: DP — dp[i][j] = true nếu s[i..j] là palindrome
        public String longestPalindromeDP(String s) {
            int n = s.length();
            boolean[][] dp = new boolean[n][n];
            String result = "";

            for (int len = 1; len <= n; len++) {
                for (int i = 0; i <= n - len; i++) {
                    int j = i + len - 1;
                    if (len == 1) dp[i][j] = true;
                    else if (len == 2) dp[i][j] = s.charAt(i) == s.charAt(j);
                    else dp[i][j] = s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1];

                    if (dp[i][j] && len > result.length()) {
                        result = s.substring(i, j + 1);
                    }
                }
            }
            return result;
        }
    }

    // ============================================================
    // LC 91 - Decode Ways
    // Approach: Bottom-Up DP — O(n), O(1) ⭐
    // ============================================================
    static class DecodeWays {
        public int numDecodings(String s) {
            if (s.charAt(0) == '0') return 0;
            int prev2 = 1, prev1 = 1;

            for (int i = 1; i < s.length(); i++) {
                int curr = 0;
                // Decode 1 digit
                if (s.charAt(i) != '0') curr += prev1;
                // Decode 2 digits (10-26)
                int twoDigit = Integer.parseInt(s.substring(i - 1, i + 1));
                if (twoDigit >= 10 && twoDigit <= 26) curr += prev2;
                prev2 = prev1;
                prev1 = curr;
            }
            return prev1;
        }
    }
}
