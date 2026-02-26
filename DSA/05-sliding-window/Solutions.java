import java.util.*;

/**
 * =============================================
 *  05 - SLIDING WINDOW
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Best Time to Buy and Sell Stock (LeetCode #121) - Easy
// -----------------------------------------------
// Cho mảng giá cổ phiếu theo ngày, tìm lợi nhuận tối đa khi mua 1 ngày và bán sau đó.
// Ví dụ: [7,1,5,3,6,4] → 5 (mua ngày 2, bán ngày 5)
//
// Approach: Sliding Window / One Pass.
// Track giá min từ trái, tại mỗi vị trí tính profit = price - minPrice.
//
// Time: O(n) - duyệt 1 lần
// Space: O(1)
class BestTimeBuySell {
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            // Cập nhật giá mua thấp nhất
            minPrice = Math.min(minPrice, price);
            // Tính lợi nhuận nếu bán hôm nay
            maxProfit = Math.max(maxProfit, price - minPrice);
        }

        return maxProfit;
    }
}

// -----------------------------------------------
// Bài 2: Longest Substring Without Repeating Characters (LeetCode #3) - Medium
// -----------------------------------------------
// Tìm chuỗi con dài nhất không có ký tự lặp.
// Ví dụ: "abcabcbb" → 3 ("abc")
//
// Approach: Sliding Window + HashSet.
// Mở rộng right, nếu gặp duplicate thì thu hẹp left cho đến khi hết duplicate.
//
// Time: O(n) - mỗi ký tự thêm/xóa khỏi set tối đa 1 lần
// Space: O(min(n, 26)) - HashSet chứa ký tự trong window
class LongestSubstringNoRepeat {
    public int lengthOfLongestSubstring(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            // Thu hẹp window cho đến khi không còn duplicate
            while (window.contains(c)) {
                window.remove(s.charAt(left));
                left++;
            }

            // Thêm ký tự mới vào window
            window.add(c);
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}

// -----------------------------------------------
// Bài 3: Minimum Window Substring (LeetCode #76) - Hard
// -----------------------------------------------
// Tìm chuỗi con ngắn nhất của s chứa tất cả ký tự của t.
// Ví dụ: s = "ADOBECODEBANC", t = "ABC" → "BANC"
//
// Approach: Sliding Window + HashMap đếm frequency.
// 1. Đếm frequency ký tự cần có (từ t)
// 2. Mở rộng right, khi window chứa đủ → thu hẹp left tìm window nhỏ nhất
//
// Time: O(n + m) - n = len(s), m = len(t)
// Space: O(m) - HashMap
class MinWindowSubstring {
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";

        // Đếm frequency ký tự cần có
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }

        int left = 0;
        int formed = 0;           // Số ký tự đã thỏa mãn frequency
        int required = need.size(); // Số ký tự unique cần thỏa

        Map<Character, Integer> window = new HashMap<>();
        int[] result = {-1, 0, 0}; // {length, left, right}

        for (int right = 0; right < s.length(); right++) {
            // Mở rộng window
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);

            // Kiểm tra ký tự này đã đủ frequency chưa
            if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
                formed++;
            }

            // Thu hẹp window khi đã chứa đủ tất cả ký tự
            while (left <= right && formed == required) {
                // Cập nhật kết quả nếu window hiện tại nhỏ hơn
                if (result[0] == -1 || right - left + 1 < result[0]) {
                    result[0] = right - left + 1;
                    result[1] = left;
                    result[2] = right;
                }

                // Bỏ ký tự bên trái
                char leftChar = s.charAt(left);
                window.put(leftChar, window.get(leftChar) - 1);
                if (need.containsKey(leftChar) &&
                    window.get(leftChar) < need.get(leftChar)) {
                    formed--;
                }
                left++;
            }
        }

        return result[0] == -1 ? "" : s.substring(result[1], result[2] + 1);
    }
}
