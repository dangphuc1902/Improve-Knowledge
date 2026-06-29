import java.util.*;

/**
 * 05 - Sliding Window Solutions
 * Các bài: Best Time to Buy/Sell Stock, Longest Substring Without Repeating,
 *          Longest Repeating Character Replacement, Minimum Window Substring,
 *          Sliding Window Maximum
 */
public class Solutions {

    // ============================================================
    // LC 121 - Best Time to Buy and Sell Stock
    // Approach 1: Sliding Window (min price tracking) — O(n) ⭐
    // Approach 2: Brute Force — O(n²)
    // ============================================================
    static class BestTimeBuySell {
        // ⭐ Optimal: Track min price, compute max profit
        // Time: O(n), Space: O(1)
        public int maxProfit(int[] prices) {
            int minPrice = Integer.MAX_VALUE;
            int maxProfit = 0;
            for (int price : prices) {
                if (price < minPrice) {
                    minPrice = price; // update buy day
                } else {
                    maxProfit = Math.max(maxProfit, price - minPrice); // try sell today
                }
            }
            return maxProfit;
        }

        // Approach 2: Brute Force O(n²) — TLE cho n lớn
        public int maxProfitBrute(int[] prices) {
            int maxProfit = 0;
            for (int i = 0; i < prices.length - 1; i++) {
                for (int j = i + 1; j < prices.length; j++) {
                    maxProfit = Math.max(maxProfit, prices[j] - prices[i]);
                }
            }
            return maxProfit;
        }
    }

    // ============================================================
    // LC 3 - Longest Substring Without Repeating Characters
    // Approach 1: HashMap lastSeen — O(n) time, O(min(m,n)) space ⭐
    // Approach 2: HashSet + slow shrink — O(n) amortized
    // Approach 3: Array[128] — O(n) time, O(1) space (ASCII only)
    // ============================================================
    static class LongestSubstringNoRepeat {
        // ⭐ Optimal: HashMap lưu index cuối cùng → jump left trực tiếp
        // Time: O(n), Space: O(min(m,n)) m=charset size
        public int lengthOfLongestSubstring(String s) {
            Map<Character, Integer> lastSeen = new HashMap<>();
            int left = 0, maxLen = 0;
            for (int right = 0; right < s.length(); right++) {
                char c = s.charAt(right);
                // Nếu c đã thấy VÀ vị trí đó >= left (trong window)
                if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                    left = lastSeen.get(c) + 1; // jump left qua duplicate
                }
                lastSeen.put(c, right);
                maxLen = Math.max(maxLen, right - left + 1);
            }
            return maxLen;
        }

        // Approach 2: HashSet — từ từ shrink left (dễ hiểu hơn)
        // Time: O(n), Space: O(min(m,n))
        public int lengthOfLongestSubstringSet(String s) {
            Set<Character> window = new HashSet<>();
            int left = 0, maxLen = 0;
            for (int right = 0; right < s.length(); right++) {
                char c = s.charAt(right);
                while (window.contains(c)) {
                    window.remove(s.charAt(left++)); // shrink left
                }
                window.add(c);
                maxLen = Math.max(maxLen, right - left + 1);
            }
            return maxLen;
        }

        // Approach 3: Array[128] cho ASCII — fastest
        public int lengthOfLongestSubstringArray(String s) {
            int[] lastIndex = new int[128]; // ASCII
            Arrays.fill(lastIndex, -1);
            int left = 0, maxLen = 0;
            for (int right = 0; right < s.length(); right++) {
                int c = s.charAt(right);
                if (lastIndex[c] >= left) {
                    left = lastIndex[c] + 1;
                }
                lastIndex[c] = right;
                maxLen = Math.max(maxLen, right - left + 1);
            }
            return maxLen;
        }
    }

    // ============================================================
    // LC 424 - Longest Repeating Character Replacement
    // Approach 1: Sliding Window + freq array — O(n * 26) = O(n) ⭐
    // ============================================================
    static class LongestRepeatingCharReplacement {
        // ⭐ Optimal: Sliding Window
        // Insight: window valid nếu (window_size - max_freq) <= k
        // Time: O(n * 26) ≈ O(n), Space: O(26) = O(1)
        public int characterReplacement(String s, int k) {
            int[] count = new int[26];
            int left = 0, maxCount = 0, maxLen = 0;

            for (int right = 0; right < s.length(); right++) {
                count[s.charAt(right) - 'A']++;
                maxCount = 0;
                for (int c : count) maxCount = Math.max(maxCount, c); // find max freq

                // Window size - maxCount = số ký tự cần thay thế
                if ((right - left + 1) - maxCount > k) {
                    count[s.charAt(left) - 'A']--;
                    left++;
                }
                maxLen = Math.max(maxLen, right - left + 1);
            }
            return maxLen;
        }

        // Optimized: không cần recompute maxCount từ đầu (chỉ cần track global max)
        public int characterReplacementOpt(String s, int k) {
            int[] count = new int[26];
            int left = 0, maxCount = 0, maxLen = 0;

            for (int right = 0; right < s.length(); right++) {
                maxCount = Math.max(maxCount, ++count[s.charAt(right) - 'A']);
                // Nếu window invalid, shrink left (maxCount không giảm → window không co)
                if ((right - left + 1) - maxCount > k) {
                    count[s.charAt(left++) - 'A']--;
                }
                maxLen = Math.max(maxLen, right - left + 1);
            }
            return maxLen;
        }
    }

    // ============================================================
    // LC 76 - Minimum Window Substring (Hard)
    // Approach 1: Sliding Window với need/window maps — O(n+m) ⭐
    // ============================================================
    static class MinimumWindowSubstring {
        // ⭐ Optimal: Sliding Window với 2 HashMaps
        // Time: O(n + m), Space: O(n + m)
        public String minWindow(String s, String t) {
            if (s.isEmpty() || t.isEmpty()) return "";

            Map<Character, Integer> need = new HashMap<>();
            for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

            Map<Character, Integer> window = new HashMap<>();
            int left = 0;
            int formed = 0; // số ký tự đã đủ số lượng yêu cầu
            int required = need.size();
            int[] ans = {-1, 0, 0}; // [window_length, left, right]

            for (int right = 0; right < s.length(); right++) {
                char c = s.charAt(right);
                window.merge(c, 1, Integer::sum);

                // Kiểm tra c có đủ số lượng cần không
                if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
                    formed++;
                }

                // Shrink window khi đã thoả yêu cầu
                while (formed == required && left <= right) {
                    // Update answer
                    if (ans[0] == -1 || right - left + 1 < ans[0]) {
                        ans[0] = right - left + 1;
                        ans[1] = left;
                        ans[2] = right;
                    }
                    // Remove left char
                    char lc = s.charAt(left++);
                    window.merge(lc, -1, Integer::sum);
                    if (need.containsKey(lc) && window.get(lc) < need.get(lc)) {
                        formed--;
                    }
                }
            }
            return ans[0] == -1 ? "" : s.substring(ans[1], ans[2] + 1);
        }
    }

    // ============================================================
    // LC 239 - Sliding Window Maximum (Hard)
    // Approach 1: Monotonic Deque — O(n) time, O(k) space ⭐
    // Approach 2: Max Heap — O(n log k) time, O(k) space
    // Approach 3: Brute Force — O(n*k) time, O(1) space
    // ============================================================
    static class SlidingWindowMaximum {
        // ⭐ Optimal: Monotonic Decreasing Deque
        // Invariant: deque front luôn là index của max trong window hiện tại
        // Time: O(n), Space: O(k)
        public int[] maxSlidingWindow(int[] nums, int k) {
            int n = nums.length;
            int[] result = new int[n - k + 1];
            Deque<Integer> deque = new ArrayDeque<>(); // lưu index, monotonic decreasing

            for (int i = 0; i < n; i++) {
                // 1. Bỏ index đã nằm ngoài window
                while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                    deque.pollFirst();
                }
                // 2. Bỏ các index có giá trị nhỏ hơn nums[i] (không bao giờ là max)
                while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                    deque.pollLast();
                }
                deque.offerLast(i);
                // 3. Khi window đủ k phần tử, ghi result
                if (i >= k - 1) {
                    result[i - k + 1] = nums[deque.peekFirst()];
                }
            }
            return result;
        }

        // Approach 2: Max Heap (TreeMap để handle duplicate)
        public int[] maxSlidingWindowHeap(int[] nums, int k) {
            int n = nums.length;
            int[] result = new int[n - k + 1];
            TreeMap<Integer, Integer> map = new TreeMap<>(Collections.reverseOrder());

            for (int i = 0; i < k; i++) map.merge(nums[i], 1, Integer::sum);
            result[0] = map.firstKey();

            for (int i = k; i < n; i++) {
                // Add new element
                map.merge(nums[i], 1, Integer::sum);
                // Remove oldest element
                int old = nums[i - k];
                map.merge(old, -1, Integer::sum);
                if (map.get(old) == 0) map.remove(old);
                result[i - k + 1] = map.firstKey();
            }
            return result;
        }
    }
}
