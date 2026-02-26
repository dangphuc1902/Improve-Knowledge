import java.util.*;

/**
 * =============================================
 *  01 - ARRAYS & HASHING
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Two Sum (LeetCode #1) - Easy
// -----------------------------------------------
// Cho mảng nums và target, tìm 2 index có tổng = target.
//
// Approach: Dùng HashMap lưu {value -> index}.
// Với mỗi phần tử, kiểm tra complement = target - nums[i] có trong map không.
//
// Time: O(n) - duyệt mảng 1 lần
// Space: O(n) - HashMap lưu tối đa n phần tử
class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }

            map.put(nums[i], i);
        }

        return new int[]{}; // Không tìm thấy (theo đề bài luôn có đáp án)
    }
}

// -----------------------------------------------
// Bài 2: Group Anagrams (LeetCode #49) - Medium
// -----------------------------------------------
// Cho mảng string, nhóm các anagram lại với nhau.
// Ví dụ: ["eat","tea","tan","ate","nat","bat"]
//     → [["eat","tea","ate"],["tan","nat"],["bat"]]
//
// Approach: Dùng sorted string làm key để nhóm.
// Hai string là anagram khi sorted giống nhau.
//
// Time: O(n * k log k) - n string, mỗi string dài k cần sort
// Space: O(n * k) - lưu tất cả string trong map
class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String s : strs) {
            // Sort string để tạo key chung cho các anagram
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            // Thêm string gốc vào nhóm tương ứng
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(map.values());
    }
}

// -----------------------------------------------
// Bài 3: Top K Frequent Elements (LeetCode #347) - Medium
// -----------------------------------------------
// Cho mảng nums và k, trả về k phần tử xuất hiện nhiều nhất.
// Ví dụ: nums = [1,1,1,2,2,3], k = 2 → [1,2]
//
// Approach: Bucket Sort
// 1. Đếm tần suất bằng HashMap
// 2. Dùng mảng bucket[i] = danh sách phần tử xuất hiện i lần
// 3. Duyệt bucket từ cuối lấy k phần tử
//
// Time: O(n) - không cần sort, chỉ dùng bucket
// Space: O(n) - HashMap + bucket array
class TopKFrequentElements {
    public int[] topKFrequent(int[] nums, int k) {
        // Bước 1: Đếm tần suất
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // Bước 2: Tạo bucket - index là tần suất, value là danh sách số
        @SuppressWarnings("unchecked")
        List<Integer>[] bucket = new List[nums.length + 1];
        for (int i = 0; i < bucket.length; i++) {
            bucket[i] = new ArrayList<>();
        }

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            bucket[entry.getValue()].add(entry.getKey());
        }

        // Bước 3: Duyệt từ bucket cao nhất, lấy k phần tử
        int[] result = new int[k];
        int idx = 0;
        for (int i = bucket.length - 1; i >= 0 && idx < k; i--) {
            for (int num : bucket[i]) {
                if (idx >= k) break;
                result[idx++] = num;
            }
        }

        return result;
    }
}
