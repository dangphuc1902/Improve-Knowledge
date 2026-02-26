import java.util.*;

/**
 * =============================================
 *  16 - 2-D DYNAMIC PROGRAMMING
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Unique Paths (LeetCode #62) - Medium
// -----------------------------------------------
// Grid m×n, bắt đầu từ (0,0), đến (m-1,n-1). Chỉ đi phải hoặc xuống.
// Đếm số đường đi khác nhau.
//
// Approach: DP 2D.
// dp[i][j] = số đường đến ô (i,j) = dp[i-1][j] + dp[i][j-1]
// (đến từ trên hoặc từ trái)
//
// Tối ưu space: chỉ cần 1 mảng 1D vì dp[i] chỉ phụ thuộc dp[i-1].
//
// Time: O(m × n)
// Space: O(n) - tối ưu từ O(m×n)
class UniquePaths {
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1); // Row đầu tiên: chỉ có 1 cách đến mỗi ô

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                // dp[j] (cũ) = từ trên xuống
                // dp[j-1] = từ trái qua (đã update trong vòng lặp này)
                dp[j] = dp[j] + dp[j - 1];
            }
        }

        return dp[n - 1];
    }
}

// -----------------------------------------------
// Bài 2: Longest Common Subsequence (LeetCode #1143) - Medium
// -----------------------------------------------
// Cho 2 chuỗi text1 và text2, tìm độ dài subsequence chung dài nhất.
// Ví dụ: text1 = "abcde", text2 = "ace" → 3 ("ace")
//
// Approach: DP 2D.
// dp[i][j] = LCS của text1[0..i-1] và text2[0..j-1]
// - Nếu text1[i-1] == text2[j-1]: dp[i][j] = dp[i-1][j-1] + 1
// - Ngược lại: dp[i][j] = max(dp[i-1][j], dp[i][j-1])
//
// Time: O(m × n)
// Space: O(m × n), tối ưu được O(n)
class LongestCommonSubsequence {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        // dp[0][j] = dp[i][0] = 0 (empty string)

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    // Ký tự match → LCS tăng 1
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    // Không match → lấy max bỏ 1 ký tự từ text1 hoặc text2
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }
}

// -----------------------------------------------
// Bài 3: Coin Change (LeetCode #322) - Medium
// -----------------------------------------------
// Cho coins[] và amount, tìm số coin ÍT NHẤT để tạo amount.
// Mỗi coin dùng không giới hạn. Trả -1 nếu không thể.
// Ví dụ: coins = [1,5,11], amount = 15 → 3 (5+5+5)
//
// Approach: DP 1D (có thể coi là 2D nén: items × amount).
// dp[a] = min coins để tạo amount a
// Với mỗi coin c: dp[a] = min(dp[a], dp[a - c] + 1)
//
// Time: O(amount × |coins|)
// Space: O(amount)
class CoinChange {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // Initialize với giá trị "impossible"
        dp[0] = 0; // Base case: 0 coins để tạo amount 0

        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (coin <= a) {
                    // Dùng coin này: 1 + min coins cho phần còn lại
                    dp[a] = Math.min(dp[a], dp[a - coin] + 1);
                }
            }
        }

        // Nếu dp[amount] vẫn > amount → impossible
        return dp[amount] > amount ? -1 : dp[amount];
    }
}
