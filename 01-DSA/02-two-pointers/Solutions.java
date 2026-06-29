import java.util.*;

/**
 * 02 - Two Pointers Solutions
 * Các bài: Valid Palindrome, Two Sum II, 3Sum, Container With Most Water
 */
public class Solutions {

    // ============================================================
    // LC 125 - Valid Palindrome
    // Approach 1: Two Pointers in-place — O(n) time, O(1) space ⭐
    // ============================================================
    static class ValidPalindrome {
        public boolean isPalindrome(String s) {
            int left = 0, right = s.length() - 1;
            while (left < right) {
                // Skip non-alphanumeric
                while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
                while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
                if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                    return false;
                }
                left++;
                right--;
            }
            return true;
        }

        // Approach 2: Brute Force — O(n) time, O(n) space
        public boolean isPalindromeBrute(String s) {
            // Clean string: giữ lại chữ và số, lowercase
            String clean = s.toLowerCase().replaceAll("[^a-z0-9]", "");
            String reversed = new StringBuilder(clean).reverse().toString();
            return clean.equals(reversed);
        }
    }

    // ============================================================
    // LC 167 - Two Sum II (Sorted Array)
    // Approach 1: Two Pointers — O(n) time, O(1) space ⭐
    // Approach 2: Binary Search — O(n log n) time, O(1) space
    // Approach 3: HashMap — O(n) time, O(n) space (general)
    // ============================================================
    static class TwoSumII {
        // ⭐ Optimal: Two Pointers (vì mảng đã sort)
        // Time: O(n), Space: O(1)
        public int[] twoSum(int[] numbers, int target) {
            int left = 0, right = numbers.length - 1;
            while (left < right) {
                int sum = numbers[left] + numbers[right];
                if (sum == target) {
                    return new int[]{left + 1, right + 1}; // 1-indexed
                } else if (sum < target) {
                    left++;  // cần tổng lớn hơn → tăng left
                } else {
                    right--; // cần tổng nhỏ hơn → giảm right
                }
            }
            return new int[]{-1, -1};
        }

        // Approach 2: Binary Search cho từng phần tử
        // Time: O(n log n), Space: O(1)
        public int[] twoSumBinarySearch(int[] numbers, int target) {
            for (int i = 0; i < numbers.length; i++) {
                int complement = target - numbers[i];
                int lo = i + 1, hi = numbers.length - 1;
                while (lo <= hi) {
                    int mid = lo + (hi - lo) / 2;
                    if (numbers[mid] == complement) return new int[]{i + 1, mid + 1};
                    else if (numbers[mid] < complement) lo = mid + 1;
                    else hi = mid - 1;
                }
            }
            return new int[]{-1, -1};
        }
    }

    // ============================================================
    // LC 15 - 3Sum
    // Approach 1: Sort + Two Pointers — O(n²) time, O(1) space ⭐
    // Approach 2: HashSet — O(n²) time, O(n) space
    // Approach 3: Brute Force — O(n³) time, O(1) space
    // ============================================================
    static class ThreeSum {
        // ⭐ Optimal: Sort + Two Pointers
        // Time: O(n log n + n²) = O(n²), Space: O(1) không kể output
        public List<List<Integer>> threeSum(int[] nums) {
            Arrays.sort(nums);
            List<List<Integer>> result = new ArrayList<>();

            for (int i = 0; i < nums.length - 2; i++) {
                // Skip duplicate giá trị cho i
                if (i > 0 && nums[i] == nums[i - 1]) continue;
                // Tối ưu: nếu nums[i] > 0, không thể có tổng = 0
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
                        left++;  // cần tổng lớn hơn
                    } else {
                        right--; // cần tổng nhỏ hơn
                    }
                }
            }
            return result;
        }

        // Approach 2: HashSet — xử lý duplicate phức tạp hơn
        // Time: O(n²), Space: O(n)
        public List<List<Integer>> threeSumHashSet(int[] nums) {
            Arrays.sort(nums);
            Set<List<Integer>> result = new HashSet<>();

            for (int i = 0; i < nums.length - 2; i++) {
                Set<Integer> seen = new HashSet<>();
                for (int j = i + 1; j < nums.length; j++) {
                    int complement = -nums[i] - nums[j];
                    if (seen.contains(complement)) {
                        List<Integer> triplet = Arrays.asList(nums[i], complement, nums[j]);
                        Collections.sort(triplet);
                        result.add(triplet);
                    }
                    seen.add(nums[j]);
                }
            }
            return new ArrayList<>(result);
        }

        // Approach 3: Brute Force O(n³) — chỉ dùng khi học
        public List<List<Integer>> threeSumBrute(int[] nums) {
            Set<List<Integer>> result = new HashSet<>();
            for (int i = 0; i < nums.length - 2; i++) {
                for (int j = i + 1; j < nums.length - 1; j++) {
                    for (int k = j + 1; k < nums.length; k++) {
                        if (nums[i] + nums[j] + nums[k] == 0) {
                            List<Integer> triplet = Arrays.asList(nums[i], nums[j], nums[k]);
                            Collections.sort(triplet);
                            result.add(triplet);
                        }
                    }
                }
            }
            return new ArrayList<>(result);
        }
    }

    // ============================================================
    // LC 11 - Container With Most Water
    // Approach 1: Two Pointers — O(n) time, O(1) space ⭐
    // Approach 2: Brute Force — O(n²) time, O(1) space
    // ============================================================
    static class ContainerWithMostWater {
        // ⭐ Optimal: Two Pointers
        // Time: O(n), Space: O(1)
        // Logic: move pointer có height nhỏ hơn vì đó là bottleneck
        public int maxArea(int[] height) {
            int left = 0, right = height.length - 1;
            int maxArea = 0;
            while (left < right) {
                int h = Math.min(height[left], height[right]);
                int w = right - left;
                maxArea = Math.max(maxArea, h * w);
                // Move pointer có height nhỏ hơn để có cơ hội tìm area lớn hơn
                if (height[left] < height[right]) left++;
                else right--;
            }
            return maxArea;
        }

        // Approach 2: Brute Force O(n²)
        public int maxAreaBrute(int[] height) {
            int maxArea = 0;
            for (int i = 0; i < height.length - 1; i++) {
                for (int j = i + 1; j < height.length; j++) {
                    int area = Math.min(height[i], height[j]) * (j - i);
                    maxArea = Math.max(maxArea, area);
                }
            }
            return maxArea;
        }
    }

    // ============================================================
    // LC 42 - Trapping Rain Water (Bonus — Hard)
    // Approach 1: Two Pointers — O(n) time, O(1) space ⭐
    // Approach 2: Prefix/Suffix Max Arrays — O(n) time, O(n) space
    // Approach 3: Stack — O(n) time, O(n) space
    // ============================================================
    static class TrappingRainWater {
        // ⭐ Optimal: Two Pointers
        // Time: O(n), Space: O(1)
        // Key insight: water[i] = min(maxLeft, maxRight) - height[i]
        public int trap(int[] height) {
            int left = 0, right = height.length - 1;
            int maxLeft = 0, maxRight = 0;
            int total = 0;

            while (left < right) {
                if (height[left] <= height[right]) {
                    // maxLeft là bottleneck
                    if (height[left] >= maxLeft) maxLeft = height[left];
                    else total += maxLeft - height[left];
                    left++;
                } else {
                    // maxRight là bottleneck
                    if (height[right] >= maxRight) maxRight = height[right];
                    else total += maxRight - height[right];
                    right--;
                }
            }
            return total;
        }

        // Approach 2: Prefix/Suffix Max Arrays — dễ hiểu hơn
        // Time: O(n), Space: O(n)
        public int trapPrefixSuffix(int[] height) {
            int n = height.length;
            int[] maxLeft = new int[n];
            int[] maxRight = new int[n];

            // Tính max từ trái
            maxLeft[0] = height[0];
            for (int i = 1; i < n; i++) {
                maxLeft[i] = Math.max(maxLeft[i - 1], height[i]);
            }
            // Tính max từ phải
            maxRight[n - 1] = height[n - 1];
            for (int i = n - 2; i >= 0; i--) {
                maxRight[i] = Math.max(maxRight[i + 1], height[i]);
            }

            int total = 0;
            for (int i = 0; i < n; i++) {
                total += Math.min(maxLeft[i], maxRight[i]) - height[i];
            }
            return total;
        }
    }
}
