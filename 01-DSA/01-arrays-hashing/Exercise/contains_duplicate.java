// package Exercise;

// Bài **217. Contains Duplicate** là một bài rất quan trọng để học pattern:

// > **HashSet Lookup Pattern**
// >
// > "Đã thấy phần tử này trước đó chưa?"

// Đây là pattern xuất hiện rất nhiều trong các bài:

// * Contains Duplicate
// * Happy Number
// * Longest Consecutive Sequence
// * Detect Cycle
// * Find Duplicate
// * Subarray Sum

// ---

// # 1. Phân tích đề bài

// Cho:

// ```java
// nums = [1,2,3,1]
// ```

// Hỏi:

// > Có tồn tại phần tử nào xuất hiện từ 2 lần trở lên hay không?

// Nếu có:

// ```java
// return true;
// ```

// Nếu tất cả đều khác nhau:

// ```java
// return false;
// ```

// ---

// ## Ví dụ 1

// ```java
// nums = [1,2,3,1]
// ```

// Số:

// ```java
// 1
// ```

// xuất hiện:

// ```java
// index 0
// index 3
// ```

// => Duplicate

// ```java
// return true
// ```

// ---

// ## Ví dụ 2

// ```java
// nums = [1,2,3,4]
// ```

// Mọi phần tử đều khác nhau.

// =>

// ```java
// return false
// ```

// ---

// # Constraint

// ```java
// 1 <= nums.length <= 10^5
// ```

// Đây là dấu hiệu cực mạnh rằng:

// ```java
// O(n²)
// ```

// sẽ không phù hợp.

// ---

// # Cách 1: Brute Force

// ## Ý tưởng

// Kiểm tra mọi cặp.

// ```java
// for i
// for j > i
// if nums[i] == nums[j]
// return true
// ```

// ---

// ## Code

// ```java
// class Solution {
// public boolean containsDuplicate(int[] nums) {

// for(int i = 0; i < nums.length; i++) {

// for(int j = i + 1; j < nums.length; j++) {

// if(nums[i] == nums[j]) {
// return true;
// }
// }
// }

// return false;
// }
// }
// ```

// ---

// # Dry Run

// ```java
// [1,2,3,1]
// ```

// So sánh:

// ```java
// 1 vs 2
// 1 vs 3
// 1 vs 1
// ```

// => tìm thấy

// ```java
// return true
// ```

// ---

// # Big O

// ## Time

// 2 vòng lặp

// ```java
// n(n-1)/2
// ```

// ≈

// ```java
// O(n²)
// ```

// ---

// ## Space

// Không dùng bộ nhớ phụ.

// ```java
// O(1)
// ```

// ---

// # Đánh giá

// | Ưu điểm | Nhược điểm |
// | ------- | ---------- |
// | Dễ code | Chậm |

// ---

// Khi:

// ```java
// n = 100000
// ```

// thì:

// ```java
// 10^10
// ```

// phép so sánh.

// Không thể pass.

// ---

// # Cách 2: Sort rồi kiểm tra

// ## Ý tưởng

// Nếu có duplicate thì sau khi sort:

// ```java
// [1,1,2,3]
// ```

// hai phần tử giống nhau sẽ đứng cạnh nhau.

// ---

// ## Code

// ```java
// class Solution {
// public boolean containsDuplicate(int[] nums) {

// Arrays.sort(nums);

// for(int i = 1; i < nums.length; i++) {

// if(nums[i] == nums[i - 1]) {
// return true;
// }
// }

// return false;
// }
// }
// ```

// ---

// # Dry Run

// ```java
// [1,2,3,1]
// ```

// Sort:

// ```java
// [1,1,2,3]
// ```

// Duyệt:

// ```java
// 1 == 1
// ```

// => true

// ---

// # Big O

// ## Time

// Sort:

// ```java
// O(n log n)
// ```

// Duyệt:

// ```java
// O(n)
// ```

// Tổng:

// ```java
// O(n log n)
// ```

// ---

// ## Space

// Java:

// ```java
// Arrays.sort(int[])
// ```

// sử dụng Dual Pivot QuickSort

// Trung bình:

// ```java
// O(log n)
// ```

// stack recursion

// ---

// # Đánh giá

// | Ưu điểm | Nhược điểm |
// | ----------------- | ------------------ |
// | Không cần HashSet | Mất thứ tự dữ liệu |

// ---

// # Cách 3: HashSet (Best)

// Đây là lời giải phỏng vấn mong đợi.

// ---

// ## Ý tưởng

// Set chỉ chứa phần tử duy nhất.

// Nếu:

// ```java
// set.contains(x)
// ```

// là true

// => đã xuất hiện trước đó.

// => duplicate.

// ---

// # Dry Run

// ```java
// nums = [1,2,3,1]
// ```

// ---

// Ban đầu:

// ```java
// set = {}
// ```

// ---

// Gặp:

// ```java
// 1
// ```

// chưa có

// ```java
// add(1)
// ```

// ---

// Set:

// ```java
// {1}
// ```

// ---

// Gặp:

// ```java
// 2
// ```

// chưa có

// ```java
// add(2)
// ```

// ---

// Set:

// ```java
// {1,2}
// ```

// ---

// Gặp:

// ```java
// 3
// ```

// chưa có

// ```java
// add(3)
// ```

// ---

// Set:

// ```java
// {1,2,3}
// ```

// ---

// Gặp:

// ```java
// 1
// ```

// Set đã có:

// ```java
// contains(1)
// ```

// => true

// ---

// # Code

// ```java
// class Solution {

// public boolean containsDuplicate(int[] nums) {

// Set<Integer> seen = new HashSet<>();

// for(int num : nums) {

// if(seen.contains(num)) {
// return true;
// }

// seen.add(num);
// }

// return false;
// }
// }
// ```

// ---

// # Big O

// ## Time

// Mỗi phần tử:

// ```java
// contains()
// add()
// ```

// HashSet trung bình:

// ```java
// O(1)
// ```

// ---

// Tổng:

// ```java
// O(n)
// ```

// ---

// ## Space

// Trường hợp xấu nhất:

// ```java
// [1,2,3,4,5]
// ```

// Set chứa toàn bộ phần tử.

// ```java
// O(n)
// ```

// ---

// # Cách 4: So sánh kích thước Set

// Đây là cách ngắn gọn nhất.

// ---

// ## Ý tưởng

// Tạo Set từ mảng.

// Nếu:

// ```java
// set.size() < nums.length
// ```

// => Có phần tử bị trùng.

// ---

// ## Code

// ```java
// class Solution {

// public boolean containsDuplicate(int[] nums) {

// Set<Integer> set = new HashSet<>();

// for(int num : nums) {
// set.add(num);
// }

// return set.size() != nums.length;
// }
// }
// ```

// ---

// Hoặc:

// ```java
// class Solution {

// public boolean containsDuplicate(int[] nums) {

// return Arrays.stream(nums)
// .boxed()
// .collect(Collectors.toSet())
// .size() != nums.length;
// }
// }
// ```

// ---

// # So sánh các cách

// | Approach | Time | Space |
// | ---------------- | ---------- | -------- |
// | Brute Force | O(n²) | O(1) |
// | Sort | O(n log n) | O(log n) |
// | HashSet | O(n) | O(n) |
// | Set Size Compare | O(n) | O(n) |

// ---

// # Pattern DSA học được

// Bài này là ví dụ kinh điển của:

// ## Pattern 1: HashSet Lookup

// ```java
// if(set.contains(x))
// ```

// ---

// ## Pattern 2: Seen Before

// ```java
// if(alreadySeen)
// ```

// ---

// ## Pattern 3: Deduplication

// Set tự động loại bỏ phần tử trùng.

// ```java
// 1 2 3 1 2 3
// ```

// ↓

// ```java
// 1 2 3
// ```

// ---

// # Khi nào nhận ra phải dùng HashSet?

// Nếu đề bài chứa các từ:

// * duplicate
// * distinct
// * unique
// * repeated
// * already seen
// * visited
// * exists before

// thì 90% bạn nên nghĩ ngay tới:

// ```java
// HashSet
// ```

// vì Set được thiết kế chính xác để giải quyết bài toán **kiểm tra tồn tại và
// loại bỏ trùng lặp** với chi phí trung bình:

// ```java
// Lookup : O(1)
// Insert : O(1)
// Delete : O(1)
// ```

// Đây là lý do lời giải tối ưu của bài **217. Contains Duplicate** là **HashSet
// Pattern (Seen Before Pattern)** với:

// ```java
// Time : O(n)
// Space : O(n)
// ```
import java.util.HashSet;
import java.util.Set;

class Solution {

    public boolean containsDuplicate(int[] nums) {

        Set<Integer> seen = new HashSet<>();

        for (int num : nums) {

            if (seen.contains(num)) {
                return true;
            }

            seen.add(num);
        }

        return false;
    }
}