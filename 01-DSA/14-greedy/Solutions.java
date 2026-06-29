import java.util.*;

/**
 * 14 - Greedy Solutions
 * Các bài: Jump Game I & II, Gas Station, Partition Labels, Meeting Rooms
 */
public class Solutions {

    // ============================================================
    // LC 55 - Jump Game
    // Approach 1: Greedy (maxReach) — O(n) time, O(1) space ⭐
    // Approach 2: DP — O(n²) time, O(n) space
    // Approach 3: BFS — O(n) time, O(n) space
    // ============================================================
    static class JumpGame {
        // ⭐ Optimal: Greedy — track max reachable index
        public boolean canJump(int[] nums) {
            int maxReach = 0;
            for (int i = 0; i < nums.length; i++) {
                if (i > maxReach) return false; // trapped!
                maxReach = Math.max(maxReach, i + nums[i]);
            }
            return true;
        }

        // Approach 2: DP — O(n²), O(n)
        // dp[i] = có thể reach index i không?
        public boolean canJumpDP(int[] nums) {
            boolean[] dp = new boolean[nums.length];
            dp[0] = true;
            for (int i = 1; i < nums.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (dp[j] && j + nums[j] >= i) {
                        dp[i] = true;
                        break;
                    }
                }
            }
            return dp[nums.length - 1];
        }
    }

    // ============================================================
    // LC 45 - Jump Game II (Minimum Jumps)
    // Approach 1: Greedy (BFS-like levels) — O(n) time, O(1) space ⭐
    // Approach 2: DP — O(n²) time, O(n) space
    // ============================================================
    static class JumpGameII {
        // ⭐ Greedy: Track currentEnd (boundary) and farthest (next boundary)
        // Khi i đến currentEnd → phải jump → jumps++, currentEnd = farthest
        public int jump(int[] nums) {
            int jumps = 0, currentEnd = 0, farthest = 0;
            for (int i = 0; i < nums.length - 1; i++) { // stop before last
                farthest = Math.max(farthest, i + nums[i]);
                if (i == currentEnd) { // reached boundary → must jump
                    jumps++;
                    currentEnd = farthest;
                    if (currentEnd >= nums.length - 1) break; // reached end
                }
            }
            return jumps;
        }

        // Approach 2: DP
        public int jumpDP(int[] nums) {
            int n = nums.length;
            int[] dp = new int[n];
            Arrays.fill(dp, Integer.MAX_VALUE);
            dp[0] = 0;
            for (int i = 0; i < n; i++) {
                if (dp[i] == Integer.MAX_VALUE) continue;
                for (int j = i + 1; j <= Math.min(i + nums[i], n - 1); j++) {
                    dp[j] = Math.min(dp[j], dp[i] + 1);
                }
            }
            return dp[n - 1];
        }
    }

    // ============================================================
    // LC 134 - Gas Station
    // Approach 1: Greedy — O(n) time, O(1) space ⭐
    // Approach 2: Brute Force — O(n²) time, O(1) space
    // ============================================================
    static class GasStation {
        // ⭐ Greedy:
        // - If totalTank < 0 → impossible
        // - startStation = vị trí sau segment tổng tank âm cuối
        public int canCompleteCircuit(int[] gas, int[] cost) {
            int totalTank = 0, currentTank = 0, startStation = 0;

            for (int i = 0; i < gas.length; i++) {
                int diff = gas[i] - cost[i];
                totalTank += diff;
                currentTank += diff;
                if (currentTank < 0) {
                    startStation = i + 1; // can't start from here or before
                    currentTank = 0;      // reset
                }
            }
            return totalTank >= 0 ? startStation : -1;
        }
    }

    // ============================================================
    // LC 763 - Partition Labels
    // Approach 1: Greedy (last occurrence) — O(n) time, O(1) space ⭐
    // ============================================================
    static class PartitionLabels {
        // ⭐ Greedy: Track last occurrence, extend partition boundary
        public List<Integer> partitionLabels(String s) {
            int[] last = new int[26];
            for (int i = 0; i < s.length(); i++) {
                last[s.charAt(i) - 'a'] = i;
            }

            List<Integer> result = new ArrayList<>();
            int start = 0, end = 0;
            for (int i = 0; i < s.length(); i++) {
                end = Math.max(end, last[s.charAt(i) - 'a']); // extend boundary
                if (i == end) { // reached boundary → new partition
                    result.add(end - start + 1);
                    start = i + 1;
                }
            }
            return result;
        }
    }

    // ============================================================
    // LC 846 - Hand of Straights (Greedy)
    // Approach: TreeMap + Greedy — O(n log n) ⭐
    // ============================================================
    static class HandOfStraights {
        public boolean isNStraightHand(int[] hand, int groupSize) {
            if (hand.length % groupSize != 0) return false;
            TreeMap<Integer, Integer> freq = new TreeMap<>();
            for (int card : hand) freq.merge(card, 1, Integer::sum);

            while (!freq.isEmpty()) {
                int first = freq.firstKey(); // smallest card
                for (int i = 0; i < groupSize; i++) {
                    int card = first + i;
                    if (!freq.containsKey(card)) return false;
                    freq.merge(card, -1, Integer::sum);
                    if (freq.get(card) == 0) freq.remove(card);
                }
            }
            return true;
        }
    }
}
