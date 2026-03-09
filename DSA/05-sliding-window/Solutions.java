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
// Cho mảng giá cổ phiếu, tìm lợi nhuận tối đa: bán > mua (bán sau).
// Ví dụ: prices = [7, 1, 5, 3, 6, 4]
//        → Mua ngày 1 (price=1), bán ngày 4 (price=6) → profit = 5
//
// 💡 Insight: Lợi nhuận tối đa = max(price - min_price_so_far)
//   Không cần kiểm tra tất cả cặp → O(n²)
//   Chỉ track min từ trái → O(n)
//
// ✅ Approach: One Pass
// Duyệt từ trái:
//   1. Track giá mua thấp nhất từ trước đó
//   2. Tại mỗi vị trí, tính lợi nhuận = current_price - min_price
//   3. Update max profit
//
// ⏱️ Time: O(n) - 1 lần duyệt
// 📦 Space: O(1) - chỉ 2 biến
//
// 📊 Trace code:
//   prices = [7, 1, 5, 3, 6, 4]
//   
//   i=0: price=7, minPrice=7, profit=0, maxProfit=0
//   i=1: price=1, minPrice=1, profit=0, maxProfit=0  ← mua giá rẻ
//   i=2: price=5, minPrice=1, profit=4, maxProfit=4 ← bán được lãi
//   i=3: price=3, minPrice=1, profit=2, maxProfit=4
//   i=4: price=6, minPrice=1, profit=5, maxProfit=5 ← lợi nhuận tốt nhất ✓
//   i=5: price=4, minPrice=1, profit=3, maxProfit=5
//
// 🔄 Comparison:
//   |Approach|Time |Space| Notes |
//   |One Pass|O(n)|O(1)| ⭐ Best |
//   |Brute   |O(n²)|O(1)| Check all pairs |
//
// 🚨 Edge cases:
//   - prices = [7,6,4,3,1] → 0 (giảm liên tục)
//   - prices = [1] → 0 (1 ngày)
//   - prices = [1,2,3] → 2 (mua 1, bán 3)
class BestTimeBuySell {
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        
        for (int price : prices) {
            // Cập nhật giá mua thấp nhất khi duyệt
            minPrice = Math.min(minPrice, price);
            
            // Tính lợi nhuận nếu bán hôm nay với giá mua tốt nhất trước đó
            int profit = price - minPrice;
            
            // Update lợi nhuận tối đa
            maxProfit = Math.max(maxProfit, profit);
        }
        
        return maxProfit;
    }
}


// -----------------------------------------------
// Bài 2: Longest Substring Without Repeating Characters (LeetCode #3) - Medium
// -----------------------------------------------
// Tìm độ dài chuỗi con dài nhất mà không có ký tự lặp.
// Ví dụ: s = "abcabcbb"
//        → "abc" → length = 3 (indices 0-2, 1-3, hoặc 3-5 all work)
//        → "b" lặp từ index 1,3,5,6,7 → phải skip
//
// 💡 Insight: Dùng sliding window + HashSet
//   Expand right khi có ký tự mới
//   Shrink left khi detect duplicate
//
// ✅ Approach: Sliding Window + HashSet
// 1. Maintain HashSet của ký tự hiện tại trong window
// 2. Expand window (right++)
// 3. Nếu gặp duplicate → shrink từ left cho đến khi hết duplicate
// 4. Update maxLen
//
// ⏱️ Time: O(n)
//   - Mỗi ký tự add/remove khỏi set tối đa 1 lần
//   - Total: left + right = O(n)
//
// 📦 Space: O(min(n, 26))
//   - Set chứa tối đa 26 chữ cái (hoặc alphabet size)
//   - Không phụ thuộc vào n
//
// 📊 Trace code (xem trên file theory)
//
// 🔄 Comparison:
//   |Approach|Time  |Space| Notes |
//   |Sliding |O(n)  |O(k) |⭐ Best |
//   |Brute   |O(n³) |O(k) | Check all substrings |
//
// 🚨 Edge cases:
//   - s = "" → 0
//   - s = "a" → 1
//   - s = "au" → 2
//   - s = "dvdf" → 3 (substring "vdf")
//   - s = "aab" → 2 (substring "ab")
class LongestSubstringNoRepeat {
    public int lengthOfLongestSubstring(String s) {
        // Set để track ký tự trong window hiện tại
        Set<Character> window = new HashSet<>();
        int left = 0;
        int maxLen = 0;
        
        // Expand window từ right
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            // Nếu gặp duplicate, shrink từ left
            // Lặp cho đến khi ký tự không còn duplicate
            while (window.contains(c)) {
                window.remove(s.charAt(left));
                left++;
            }
            
            // Thêm ký tự mới vào window
            window.add(c);
            
            // Update max length
            // Window size = right - left + 1
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
}


// -----------------------------------------------
// Bài 3: Minimum Window Substring (LeetCode #76) - Hard
// -----------------------------------------------
// Tìm chuỗi con ngắn nhất của s chứa tất cả ký tự của t.
// Ví dụ: s = "ADOBECODEBANC", t = "ABC"
//        → "BANC" (chứa A, B, C)
//
// 💡 Insight: Cần tìm window nhỏ nhất chứa đủ tất cả char
//   Không dùng fixed window, dùng variable window
//   Expand → shrink → repeat
//
// ✅ Approach: Sliding Window + 2 HashMaps
// Setup:
//   - need: frequency của ký tự cần (từ t)
//   - window: frequency của ký tự trong window hiện tại
//   - formed: số unique char đã đạt frequency required
//
// Bước:
// 1. Expand window (right++) cho đến khi formed == required
// 2. Thu hẹp từ left tìm window nhỏ nhất
// 3. Repeat
//
// ⏱️ Time: O(n + m) where n=len(s), m=len(t)
//   - Right pointer: O(n)
//   - Left pointer: O(n) tổng
//   - HashMap operations: O(1) average
//
// 📦 Space: O(m) - HashMap lưu tối đa m unique chars
//
// 📊 Trace code (xem trên file theory)
//
// 🔄 Comparison:
//   |Approach|Time   |Space| Notes |
//   |Sliding |O(n+m) |O(m) |⭐ Best |
//   |Brute   |O(n²*m)|O(m) | Check all substrings |
//
// 🚨 Edge cases & mistakes:
//   - s = "", t = "a" → "" (empty)
//   - s = "a", t = "b" → "" (no match)
//   - len(s) < len(t) → "" (impossible)
//   - ⚠️ MISTAKE: Kiểm tra window.get(c) == need.get(c) without type cast
//     → Dùng .intValue() hoặc .equals()
//   - ⚠️ MISTAKE: Quên kiểm tra need.containsKey(c)
//     → Nếu không, formed sẽ tăng cho char không cần
class MinWindowSubstring {
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";
        
        // Bước 1: Đếm frequency ký tự cần có (từ t)
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }
        
        // Init variables
        int left = 0;
        int formed = 0;           // Số unique char đã đạt frequency
        int required = need.size(); // Số unique char cần thỏa
        
        // Window freq
        Map<Character, Integer> window = new HashMap<>();
        
        // Result: {length, left, right}
        // Dùng array để lưu vì string immutable
        int[] result = {-1, 0, 0};
        
        // Bước 2: Duyệt s với right pointer
        for (int right = 0; right < s.length(); right++) {
            // Mở rộng window: thêm ký tự right vào
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);
            
            // Kiểm tra ký tự này đã đủ frequency chưa
            // ⚠️ Chỉ count nếu ký tự này CÓ trong need
            if (need.containsKey(c) && 
                window.get(c).intValue() == need.get(c).intValue()) {
                formed++;
            }
            
            // Bước 3: Thu hẹp window khi đã chứa đủ
            // Nếu formed == required → window là valid
            while (left <= right && formed == required) {
                // Cập nhật kết quả nếu window hiện tại nhỏ hơn
                if (result[0] == -1 || right - left + 1 < result[0]) {
                    result[0] = right - left + 1;
                    result[1] = left;
                    result[2] = right;
                }
                
                // Bỏ ký tự ở left, xem còn valid không
                char leftChar = s.charAt(left);
                window.put(leftChar, window.get(leftChar) - 1);
                
                // Nếu bỏ ký tự này làm frequency < need → không valid nữa
                if (need.containsKey(leftChar) &&
                    window.get(leftChar) < need.get(leftChar)) {
                    formed--;
                }
                
                left++;
            }
        }
        
        // Trả về kết quả
        return result[0] == -1 ? "" : s.substring(result[1], result[2] + 1);
    }
}

