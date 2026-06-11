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
// ✅ Approach: HashMap {value → index}
// Duyệt mảng từ trái:
//   - Tính complement = target - nums[i]
//   - Kiểm tra complement có trong map → return ngay
//   - Thêm nums[i] vào map
//
// 📊 Trace code với ví dụ:
//   nums = [2, 7, 11, 15], target = 9
//   
//   i=0: nums[0]=2
//     complement = 9-2 = 7
//     map.containsKey(7)? NO
//     map.put(2, 0) → map={2:0}
//   
//   i=1: nums[1]=7
//     complement = 9-7 = 2
//     map.containsKey(2)? YES ✓
//     return [map.get(2), 1] = [0, 1]
//
// ⏱️ Time: O(n) - duyệt mảng 1 lần
// 📦 Space: O(n) - HashMap lưu tối đa n phần tử
//
// 🔄 Comparison:
//   - Brute Force O(n²): check mọi cặp
//   - HashMap O(n): 1 pass, tìm complement trong O(1)
//   ✅ HashMap là BEST approach cho bài này
//
// 🚨 Edge cases:
//   - Duplicate: [1,1,2,3], target=3 → [0,2] hoặc [1,2]
//   - Âm số: [-1,-1], target=-2 → [0,1]
//   - Đề bài guarantee: luôn có đáp án duy nhất
class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            // Kiểm tra complement có từng xuất hiện trước không
            Integer complementIndex = map.get(complement);
            if (complementIndex != null) {
                return new int[]{complementIndex, i};
            }
            
            // Lưu nums[i] với index của nó
            map.put(nums[i], i);
        }
        
        // Theo đề bài, luôn có đáp án, không bao giờ return []
        return new int[]{};
    }
}


// -----------------------------------------------
// Bài 2: Group Anagrams (LeetCode #49) - Medium
// -----------------------------------------------
// Cho mảng string, nhóm các anagram lại với nhau.
// Ví dụ: ["eat","tea","tan","ate","nat","bat"]
//     → [["eat","tea","ate"],["tan","nat"],["bat"]]
//
// 💡 Insight: Hai string là anagram ⟺ sorted form giống nhau
//   "eat" sorted = "aet"
//   "tea" sorted = "aet"
//   "ate" sorted = "aet" → ba cái này cùng nhóm
//
// ✅ Approach: Sorted String làm Key
// 1. Với mỗi string s:
//    - Sắp xếp ký tự → key
//    - Thêm s vào nhóm tương ứng
// 2. Return tất cả nhóm
//
// 📊 Trace code:
//   Input: ["eat", "tea", "ate", "tan", "nat", "bat"]
//   
//   "eat" → sort → "aet" → map={"aet": ["eat"]}
//   "tea" → sort → "aet" → map={"aet": ["eat","tea"]}
//   "ate" → sort → "aet" → map={"aet": ["eat","tea","ate"]}
//   "tan" → sort → "ant" → map={...,"ant": ["tan"]}
//   "nat" → sort → "ant" → map={...,"ant": ["tan","nat"]}
//   "bat" → sort → "abt" → map={...,"abt": ["bat"]}
//   
//   Return: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
//
// ⏱️ Time: O(n * k log k)
//   - n strings
//   - mỗi string dài k → sort O(k log k)
//
// 📦 Space: O(n * k) - lưu tất cả string trong map
//
// 🔄 Alternative Approach: Count Array [26]
//   - Thay vì sort, đếm tần suất chữ cái: O(n * k)
//   - Hơi nhanh hơn nhưng code phức tạp hơn
//
// 🚨 Edge cases:
//   - strs = [""] → [[""]]
//   - strs = ["a"] → [["a"]]
//   - strs = ["a","a","a"] → [["a","a","a"]]
class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String s : strs) {
            // Bước 1: Sort từng string để tạo canonical key
            char[] chars = s.toCharArray();  // String → char[]
            Arrays.sort(chars);              // Sort ký tự
            String key = new String(chars);  // char[] → String key
            
            // Bước 2: Thêm string gốc vào nhóm tương ứng
            // computeIfAbsent: nếu key chưa tồn tại, khởi tạo ArrayList
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        
        // Bước 3: Return tất cả giá trị (các nhóm)
        return new ArrayList<>(map.values());
    }
}


// -----------------------------------------------
// Bài 3: Top K Frequent Elements (LeetCode #347) - Medium
// -----------------------------------------------
// Cho mảng nums và k, trả về k phần tử xuất hiện nhiều nhất.
// Ví dụ: nums = [1,1,1,2,2,3], k = 2 → [1,2]
//
// 💡 Insight: Có 3 cách, nhưng Bucket Sort là tốt nhất!
//   1. Min Heap: O(n log k) - tốt khi k nhỏ
//   2. TreeMap: O(n log n) - cân bằng
//   3. Bucket Sort: O(n) - BEST! ⭐
//
// ✅ Approach: Bucket Sort
// Tại sao dùng bucket sort?
//   - Max frequency = n (nếu 1 phần tử lặp n lần)
//   - frequency nhỏ hơn n → ta có thể dùng array bucket
//   - Không cần sort → O(n) thay vì O(n log n)
//
// Các bước:
// 1. Đếm tần suất mỗi phần tử bằng HashMap
// 2. Tạo bucket: bucket[i] = danh sách các phần tử xuất hiện i lần
// 3. Duyệt bucket từ cuối → lấy k phần tử
//
// 📊 Trace code:
//   Input: nums = [1,1,1,2,2,3,3,3,3], k = 2
//   
//   Bước 1: Đếm tần suất
//   freqMap = {1:3, 2:2, 3:4}
//   
//   Bước 2: Tạo bucket
//   bucket[0] = []
//   bucket[1] = []
//   bucket[2] = [2]        ← 2 xuất hiện 2 lần
//   bucket[3] = [1]        ← 1 xuất hiện 3 lần
//   bucket[4] = [3]        ← 3 xuất hiện 4 lần
//   bucket[5] = []
//   ...
//   bucket[9] = []
//   
//   Bước 3: Duyệt ngược lấy k phần tử
//   i=9 to 5: all empty
//   i=4: bucket[4]=[3] → result[0]=3, idx=1
//   i=3: bucket[3]=[1] → result[1]=1, idx=2
//   idx == k? → stop
//   
//   Output: [3, 1]
//
// ⏱️ Time: O(n)
//   - HashMap: O(n)
//   - Bucket: O(n) - mỗi phần tử thêm 1 lần vào bucket
//   - Duyệt: O(n) - tối đa n bucket
//
// 📦 Space: O(n) - HashMap + bucket array
//
// 🔄 So sánh 3 approach:
//   | Approach | Time | Space | Pros |
//   |----------|------|-------|------|
//   | Bucket Sort | O(n) | O(n) | Tốt nhất, O(n) time |
//   | Min Heap | O(n log k) | O(k) | Tốt nếu k << n |
//   | TreeMap | O(n log n) | O(n) | Cân bằng |
//
// 🚨 Edge cases:
//   - nums = [1], k = 1 → [1]
//   - k = size(unique) → return tất cả
//   - Duplicate frequency: [1,1,2,2,3], k=2 → [1,2] hoặc [2,1]
//   - k > size(unique) → handled tự động
class TopKFrequentElements {
    public int[] topKFrequent(int[] nums, int k) {
        // Bước 1: Đếm tần suất mỗi phần tử
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }
        
        // Bước 2: Tạo bucket array
        // bucket[i] = danh sách phần tử xuất hiện i lần
        // Index: 0 → n, Value: List<Integer>
        @SuppressWarnings("unchecked")
        List<Integer>[] bucket = new List[nums.length + 1];
        for (int i = 0; i < bucket.length; i++) {
            bucket[i] = new ArrayList<>();
        }
        
        // Đổ phần tử vào bucket theo tần suất
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            int num = entry.getKey();
            int freq = entry.getValue();
            bucket[freq].add(num);
        }
        
        // Bước 3: Duyệt bucket từ cao nhất, lấy k phần tử
        int[] result = new int[k];
        int idx = 0;
        for (int i = bucket.length - 1; i >= 0 && idx < k; i--) {
            for (int num : bucket[i]) {
                if (idx >= k) break;  // Đủ k phần tử rồi
                result[idx++] = num;
            }
        }
        
        return result;
    }
}

