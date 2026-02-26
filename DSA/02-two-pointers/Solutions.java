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
// Kiểm tra string có phải palindrome không (chỉ xét alphanumeric, ignore case).
// Ví dụ: "A man, a plan, a canal: Panama" → true
//
// Approach: Hai con trỏ đối đầu, skip ký tự không phải alphanumeric.
//
// Time: O(n) - mỗi ký tự được xét tối đa 1 lần
// Space: O(1) - không dùng thêm bộ nhớ
class ValidPalindrome {
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            // Skip ký tự không phải chữ/số
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            // So sánh (ignore case)
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }
}

// -----------------------------------------------
// Bài 2: 3Sum (LeetCode #15) - Medium
// -----------------------------------------------
// Tìm tất cả bộ ba [a, b, c] trong mảng sao cho a + b + c = 0.
// Không được có bộ ba trùng nhau.
//
// Approach:
// 1. Sort mảng
// 2. Cố định phần tử đầu tiên (i), dùng Two Pointers tìm 2 phần tử còn lại
// 3. Skip duplicate để tránh kết quả trùng
//
// Time: O(n²) - sort O(n log n) + vòng lặp O(n) × Two Pointers O(n)
// Space: O(1) - không tính output (sort in-place)
class ThreeSum {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicate cho phần tử đầu tiên
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            // Tối ưu: nếu nums[i] > 0 thì không thể tìm được bộ ba = 0
            if (nums[i] > 0) break;

            int left = i + 1, right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // Skip duplicate cho left và right
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;

                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;   // Cần tổng lớn hơn → tăng left
                } else {
                    right--;  // Cần tổng nhỏ hơn → giảm right
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
// Diện tích = min(height[left], height[right]) × (right - left)
//
// Approach: Two Pointers đối đầu.
// Greedy: luôn di chuyển pointer có height nhỏ hơn (vì giữ nó không thể tăng area).
//
// Time: O(n) - mỗi pointer di chuyển tối đa n lần
// Space: O(1)
class ContainerWithMostWater {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            // Tính diện tích hiện tại
            int width = right - left;
            int h = Math.min(height[left], height[right]);
            maxArea = Math.max(maxArea, width * h);

            // Di chuyển pointer có height nhỏ hơn
            // Lý do: giữ pointer thấp + thu hẹp width → area chỉ có giảm
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxArea;
    }
}
