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
// Grid m×n, từ (0,0) → (m-1,n-1). Chỉ đi phải hoặc xuống. Đếm # đường.
//
// 🔑 Key Insight: Grid DP
// ────────────────────────────────────────────
// dp[i][j] = # paths đến (i,j) = dp[i-1][j] + dp[i][j-1]
// Ô (i,j) chỉ từ: trên (đi xuống) HOẶC trái (đi phải)
//
// 📋 Ví dụ trace m=3, n=4:
// ────────────────────────────────────────────
//   Row 0: [1,   1,   1,   1]    (chỉ 1 cách: đi phải)
//   Row 1: [1,   2,   3,   4]    (dp[j] = top + left)
//                 1+1  1+2  1+3
//   Row 2: [1,   3,   6,  10]
//                 2+1  3+3  4+6
// ✅ Output: 10
//
// 🏆 Space Optimization: O(m*n) → O(n)
// ────────────────────────────────────
// Chỉ cần 1 mảng: dp[j] cũ = from top, dp[j-1] = from left
// dp[j] = dp[j] + dp[j-1] (cập nhật in-place)
//
// Time: O(m×n), Space: O(n) ⭐
class UniquePaths {
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);  // Row 0: tất cả = 1
        
        // Process rows 1 to m-1
        for (int i = 1; i < m; i++) {
            // j=0 không thay, luôn = 1 (column 0)
            for (int j = 1; j < n; j++) {
                // dp[j] sau: từ top (old dp[j]) + left (new dp[j-1])
                dp[j] = dp[j] + dp[j - 1];
            }
        }
        
        return dp[n - 1];
    }
}

// -----------------------------------------------
// Bài 2: Longest Common Subsequence (LeetCode #1143) - Medium
// -----------------------------------------------
// Cho 2 chuỗi text1, text2, tìm LCS dài nhất.
// VD: text1 = "abcde", text2 = "ace" → 3 (LCS = "ace")
//
// 🔑 Key Insight: 2D String Matching DP
// ────────────────────────────────────────
// dp[i][j] = LCS của text1[0..i-1] và text2[0..j-1]
// Base: dp[0][*] = dp[*][0] = 0 (empty string)
//
// Transition:
//   if text1[i-1] == text2[j-1]:
//     dp[i][j] = dp[i-1][j-1] + 1  (character match!)
//   else:
//     dp[i][j] = max(dp[i-1][j], dp[i][j-1])  (skip từ text1 hoặc text2)
//
// 📋 Ví dụ trace text1="abcde", text2="ace":
// ────────────────────────────────────────
//        ""  a  c  e
//    ""  0   0  0  0
//    a   0   1  1  1    (a==a: dp[0][0]+1=1)
//    b   0   1  1  1
//    c   0   1  2  2    (c==c: dp[1][1]+1=2)
//    d   0   1  2  2
//    e   0   1  2  3    (e==e: dp[4][2]+1=3)
// ✅ Output: 3
//
// ⏱️ Time: O(m×n), Space: O(m×n), tối ưu O(n) ⭐
class LongestCommonSubsequence {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        // dp[0][j] = dp[i][0] = 0 (base case: empty string)
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    // ✅ Ký tự match: lấy LCS từ trước + 1
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    // ❌ Không match: bỏ 1 ký tự
                    // - Bỏ text1[i-1]: dp[i-1][j]
                    // - Bỏ text2[j-1]: dp[i][j-1]
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
// Cho coins[], amount. Tìm TỔNG SỐ COIN ÍT NHẤT để tạo amount.
// Mỗi coin dùng KHÔNG GIỚI HẠN (unbounded knapsack).
// Trả -1 nếu impossible. VD: coins=[1,5,11], amount=15 → 3 (5+5+5)
//
// 🔑 Key Insight: Unbounded Knapsack DP
// ──────────────────────────────────────
// dp[a] = min coin count để tạo amount a
// Base: dp[0] = 0 (không cần coin)
//
// Transition: Với mỗi coin:
//   dp[a] = min(dp[a], dp[a - coin] + 1)
//   (lấy coin này → a-coin + 1 coin mới)
//
// 📋 Ví dụ trace coins=[1,5,11], amount=15:
// ─────────────────────────────────────────
// dp[0] = 0
//
// a=1:
//   coin=1: dp[1] = min(INF, dp[0]+1) = 1
// a=2:
//   coin=1: dp[2] = min(INF, dp[1]+1) = 2
// a=5:
//   coin=1: dp[5] = min(INF, dp[4]+1) = 5
//   coin=5: dp[5] = min(5, dp[0]+1) = 1 ⭐ Better!
// a=10:
//   coin=5: dp[10] = min(..., dp[5]+1) = 2 (5+5)
// a=15:
//   coin=1: dp[15] = ... = 15 (1+1+...+1)
//   coin=5: dp[15] = min(15, dp[10]+1) = 3 (5+5+5) ⭐
//   coin=11: dp[15] = min(1, dp[4]+1) = ... = 3
//
// Final: dp = [0, 1, 2, 3, 4, 1, 2, 3, 4, 5, 2, 1, 2, 3, 4, 3]
// ✅ Output: 3
//
// 🚨 Critical: Khởi tạo dp[a] = amount+1 (làm "impossible" marker)
// ────────────────────────────────────
// Dùng amount+1 vì > amount là invalid
// Sau đó check: return dp[amount] > amount ? -1 : dp[amount]
//
// ⏱️ Time: O(amount×|coins|), Space: O(amount)
class CoinChange {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);  // Khởi tạo "impossible" marker
        dp[0] = 0;  // Base case: 0 coin cho amount 0
        
        // Unbounded: mỗi coin có thể dùng nhiều lần
        for (int a = 1; a <= amount; a++) {
            // Thử mỗi coin
            for (int coin : coins) {
                if (coin <= a) {
                    // Dùng coin này, cần dp[a-coin] + 1 coin
                    dp[a] = Math.min(dp[a], dp[a - coin] + 1);
                }
            }
        }
        
        // Nếu vẫn impossible (> amount) → -1
        return dp[amount] > amount ? -1 : dp[amount];
    }
}
