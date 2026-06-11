import java.util.*;

/**
 * =============================================
 *  02 - TWO POINTERS
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Valid Palindrome (LeetCode #125) - Easy
// -----------------------------------------------
// Kiểm tra string có phải palindrome không.
// Chỉ xét chữ cái và chữ số, bỏ qua case.
//
// Ví dụ:
//   Input: "A man, a plan, a canal: Panama"
//   → Chỉ xét: "amanaplanacanalpanama"
//   → Từ trái: a-m-a-n-a-p-l-a-n-a-c-a-n-a-l-p-a-n-a-m-a
//   → Từ phải: a-m-a-n-a-p-l-a-n-a-c-a-n-a-l-p-a-n-a-m-a
//   → Output: true ✓
//
// ✅ Approach: Two Pointers đối đầu
// left từ đầu, right từ cuối:
// 1. Skip ký tự không phải alphanumeric
// 2. So sánh (case-insensitive)
// 3. Move about inward
//
// ⏱️ Time: O(n) - mỗi ký tự xét tối đa 1 lần
// 📦 Space: O(1) - không dùng thêm bộ nhớ
//
// 🔄 Comparison:
//   - Two Pointers O(n), O(1) ⭐
//   - String Reverse O(n), O(n)
//   - Regex O(n), O(n)
//
// 🚨 Edge cases:
//   - s = "" → true
//   - s = "0P" → false
//   - s = " " → true (không có alphanumeric)
//   - s = ".,;" → true (bỏ hết, empty coi là palindrome)
class ValidPalindrome {
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        
        while (left < right) {
            // Skip ký tự không phải chữ/số từ bên trái
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            
            // Skip ký tự không phải chữ/số từ bên phải
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }
            
            // Kiểm tra xem 2 ký tự có bằng nhau không (case-insensitive)
            if (Character.toLowerCase(s.charAt(left)) != 
                Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            
            // Move inward
            left++;
            right--;
        }
        
        return true;  // Tất cả ký tự match
    }
}


// -----------------------------------------------
// Bài 2: 3Sum (LeetCode #15) - Medium
// -----------------------------------------------
// Tìm tất cả bộ ba [a, b, c] thỏa a + b + c = 0.
// Kết quả không được có bộ ba trùng nhau.
//
// Ví dụ:
//   Input: nums = [-1, 0, 1, 2, -1, -4]
//   Output: [[-1, -1, 2], [-1, 0, 1]]
//
// 💡 Insight: Dấu hiệu dùng Two Pointers
//   - Sorted array → có thể dùng two pointers
//   - Tìm cặp/nhóm thỏa điều kiện → two pointers hoặc hash
//
// ✅ Approach: Sort + Fixed Element + Two Pointers
//
// Bước 1: Sort mảng
// Bước 2: Cố định phần tử i, dùng 2 pointers tìm complement
//   - left = i + 1
//   - right = n - 1
//   - Nếu sum < 0 → tăng left (cần lớn hơn)
//   - Nếu sum > 0 → giảm right (cần nhỏ hơn)
//   - Nếu sum == 0 → tìm được 1 bộ ba
//
// Bước 3: Skip duplicate
//   - Cần skip ở cả i, left, right
//   - ⚠️ QUAN TRỌNG: Skip SAU khi found, KHÔNG phải skip trước check
//
// ⏱️ Time: O(n²)
//   - Sort: O(n log n)
//   - Outer loop: O(n)
//   - Inner loop (2 pointers): O(n)
//   - Vòng lặp: O(n) × O(n) = O(n²)
//
// 📦 Space: O(1)
//   - Sort in-place (không tính output)
//
// 🔄 Comparison 3Sum approaches:
//   | Approach | Time | Space | Pros |
//   |----------|------|-------|------|
//   | Sort + 2 Pointers | O(n²) | O(1) | Tốt nhất ⭐ |
//   | HashMap | O(n²) | O(n) | Cũng được |
//   | Brute Force | O(n³) | O(1) | Quá chậm ✗ |
//
// 🚨 Edge cases:
//   - nums = [] → []
//   - nums = [0] → [] (chỉ 1 phần tử)
//   - nums = [0, 0, 0] → [[0, 0, 0]] (tất cả 0)
//   - nums = [-2, 0, 1, 1, 2] → [[-2, 0, 2], [-2, 1, 1]]
//     phải skip duplicate 1!
class ThreeSum {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        
        // Bước 1: Sort
        Arrays.sort(nums);
        
        // Bước 2: Cố định phần tử i, tìm 2 phần tử còn lại
        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicate cho phần tử đầu tiên
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            
            // Tối ưu: nếu nums[i] > 0, tổng của 3 số sẽ > 0
            // (vì array đã sorted)
            if (nums[i] > 0) {
                break;
            }
            
            // Bước 2.1: Two Pointers tìm 2 số còn lại
            int left = i + 1;
            int right = nums.length - 1;
            
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                
                if (sum == 0) {
                    // Found một bộ ba!
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    
                    // Bước 2.2: Skip duplicate
                    // ⚠️ PHẢI skip TRONG khi left < right
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    
                    // Move để tìm kết quả tiếp theo
                    left++;
                    right--;
                    
                } else if (sum < 0) {
                    // Tổng quá nhỏ, cần tăng giá trị
                    // Tăng left luôn được (left < right, sort)
                    left++;
                    
                } else {
                    // Tổng quá lớn, cần giảm giá trị
                    // Giảm right
                    right--;
                }
            }
        }
        
        return result;
    }
}


// -----------------------------------------------
// Bài 3: Container With Most Water (LeetCode #11) - Medium
// -----------------------------------------------
// Cho mảng height[], tìm 2 đường thẳng tạo container chứa nhiều nước nhất.
// Area = min(height[left], height[right]) × (right - left)
//
// Ví dụ:
//   height = [1,8,6,2,5,4,8,3,7]
//   
//   Visual:
//     8| # . . . . . #
//     6| # . # . . . #
//     4| # . # . # # #
//     2| # . # # # # # # #
//     0| # # # # # # # # #
//       0 1 2 3 4 5 6 7 8
//   
//   Kết nối left=1 (h=8) và right=8 (h=7):
//   Area = min(8,7) × (8-1) = 7 × 7 = 49 ✓
//
// 💡 Insight: Greedy Two Pointers
// Tại sao nó hoạt động?
//   - Diện tích = height × width
//   - Nếu lưu pointer thấp hơn → width giảm, height không thể tăng
//   - Luôn nên move pointer thấp hơn để tìm cơ hội tăng height
//
// ✅ Approach: Two Pointers đối đầu
// 1. left = 0, right = n-1
// 2. Tính area hiện tại
// 3. Move pointer nào có height nhỏ hơn
// 4. Lặp lại
//
// ⏱️ Time: O(n)
//   - Mỗi pointer di chuyển tối đa n lần
//   - Tổng: left + right = n lần
//
// 📦 Space: O(1)
//   - Chỉ dùng 2 con trỏ
//
// 🔄 Comparison:
//   | Approach | Time | Space |
//   |----------|------|-------|
//   | Two Pointers ⭐ | O(n) | O(1) |
//   | Brute Force | O(n²) | O(1) |
//
// 🚨 Edge cases:
//   - height = [1] → 0 (chỉ 1 thanh)
//   - height = [1,1] → 1
//   - height = [1,200,1] → min(1,1) × 2 = 2, NOT 1×200 ✗
//
// 💭 Tại sao greedy hoạt động?
//   Giả sử move pointer cao hơn:
//     area_new = min(short, tall) × (width - 1)
//   Vì short ≤ tall:
//     min(short, tall) = short (không thay đổi)
//     width - 1 < width
//   Vậy area_new ≤ area (không lợi)
//   
//   Chỉ move pointer thấp có cơ hội tăng min (và area)
class ContainerWithMostWater {
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;
        
        while (left < right) {
            // Tính area hiện tại
            int width = right - left;
            int h = Math.min(height[left], height[right]);
            int area = width * h;
            
            // Update max
            maxArea = Math.max(maxArea, area);
            
            // Di chuyển pointer nào có height nhỏ hơn
            // Lý do: move short là cách duy nhất có cơ hội tăng min(h)
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        
        return maxArea;
    }
}

