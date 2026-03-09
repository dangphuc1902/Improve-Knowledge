import java.util.*;

/**
 * =============================================
 *  12 - 1-D DYNAMIC PROGRAMMING
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Climbing Stairs (LeetCode #70) - Easy
// -----------------------------------------------
// Có n bậc thang. Mỗi lần bước 1 hoặc 2 bậc. Bao nhiêu cách?
//
// Ví dụ: n = 3
//   Cách 1: 1+1+1
//   Cách 2: 1+2
//   Cách 3: 2+1
//   Output: 3
//
// 💡 Insight: Để đến bậc n:
//   - Từ bậc n-1 bước 1 (có dp[n-1] cách)
//   - Từ bậc n-2 bước 2 (có dp[n-2] cách)
//   → dp[n] = dp[n-1] + dp[n-2] ← Pure Fibonacci!
//
// ✅ Approach: Bottom-up DP
// State định nghĩa: dp[i] = số cách lên bậc i
// Recurrence: dp[i] = dp[i-1] + dp[i-2]
// Base case: dp[1] = 1, dp[2] = 2
//
// ⏱️ Time: O(n)
// 📦 Space: O(1) - chỉ need 2 biến thay vì array
//
// 📊 Trace code:
//   n = 5
//   prev2 = 1  (dp[1])
//   prev1 = 2  (dp[2])
//   
//   i=3: curr = 2+1=3, update: prev2=2, prev1=3
//   i=4: curr = 3+2=5, update: prev2=3, prev1=5
//   i=5: curr = 5+3=8, update: prev2=5, prev1=8
//   
//   return 8
//
// 🔄 Comparison: Recursion vs DP
//   |Approach|Time  |Space|Tính|
//   |Recur   |O(2^n)|O(n) |Slow, exponential|
//   |DP Array|O(n)  |O(n) |Standard|
//   |DP 2-var|O(n)  |O(1) |⭐ Best|
//
// 🚨 Edge cases:
//   - n = 1 → 1
//   - n = 2 → 2
//   - n = 0 → ? (LeetCode says n ≥ 1)
class ClimbingStairs {
    public int climbStairs(int n) {
        // Base cases
        if (n <= 2) return n;
        
        // DP với 2 biến (space optimize)
        int prev2 = 1;  // dp[i-2]
        int prev1 = 2;  // dp[i-1]
        
        // Tính từ i=3 đến n
        for (int i = 3; i <= n; i++) {
            // dp[i] = dp[i-1] + dp[i-2]
            int curr = prev1 + prev2;
            
            // Shift: chuẩn bị cho iteration kế tiếp
            prev2 = prev1;
            prev1 = curr;
        }
        
        return prev1;
    }
}


// -----------------------------------------------
// Bài 2: House Robber (LeetCode #198) - Medium
// -----------------------------------------------
// Mảng nums[i] = tiền trong nhà i.
// Ràng buộc: không được cướp 2 nhà liên tiếp.
// Tìm số tiền tối đa có thể cướp.
//
// Ví dụ:
//   nums = [1, 2, 3, 1]
//   Cách tốt nhất: cướp nhà 0 + nhà 2 = 1 + 3 = 4
//   (không cướp nhà 1)
//
// 💡 Insight: Take-or-Skip DP
//   Tại nhà i, 2 lựa chọn:
//   1. Không cướp: rob = rob[i-1]
//   2. Cướp: rob = nums[i] + rob[i-2] (phải skip i-1)
//   → rob[i] = max(2 cách loại trên)
//
// ✅ Approach: Bottom-up DP (Take-or-Skip)
// State: rob[i] = max money từ nhà 0..i
// Recurrence: rob[i] = max(rob[i-1], rob[i-2] + nums[i])
// Base: rob[0] = nums[0]
//
// ⏱️ Time: O(n)
// 📦 Space: O(1)
//
// 📊 Trace code:
//   nums = [5, 3, 4, 11, 2]
//   
//   prev2 = 0 (before house 0)
//   prev1 = 5 (rob[0] = nums[0])
//   
//   i=1: curr = max(5, 0+3) = 5, prev2=5, prev1=5
//   i=2: curr = max(5, 5+4) = 9, prev2=5, prev1=9
//   i=3: curr = max(9, 5+11) = 16, prev2=9, prev1=16
//   i=4: curr = max(16, 9+2) = 16, prev2=16, prev1=16
//   
//   return 16
//
// 🔄 Comparison: Backtracking vs DP
//   |Approach|Time  |Space|Notes|
//   |Backtrack|O(2^n)|O(n) |Check all combinations|
//   |DP Array|O(n)  |O(n) |Standard|
//   |DP 2-var|O(n)  |O(1) |⭐ Best|
//
// 🚨 Edge cases:
//   - nums = [5] → 5
//   - nums = [5, 3] → 5 (take 5, skip 3)
//   - nums = [1, 100, 1, 1, 1] → 101 (take 100, skip neighbors)
//
// 💡 House Robber II (LeetCode #213)
//   Nhà sắp hàng tròn (nhà 0 và n-1 neighbor)
//   → Không cướp nlà 0 HOẶC nhà n-1 → run DP trên 2 array riêng
class HouseRobber {
    public int rob(int[] nums) {
        // Edge case
        if (nums.length == 1) return nums[0];
        
        // DP với 2 biến
        int prev2 = 0;           // rob[i-2]
        int prev1 = nums[0];     // rob[i-1] = nums[0]
        
        // Tính từ nhà 1 đến n-1
        for (int i = 1; i < nums.length; i++) {
            // Lựa chọn: không cướp i HOẶC cướp i (skip i-1)
            int curr = Math.max(
                prev1,              // Không cướp nhà i
                prev2 + nums[i]     // Cướp nhà i (+ rob[i-2])
            );
            
            // Shift: chuẩn bị cho iteration kế tiếp
            prev2 = prev1;
            prev1 = curr;
        }
        
        return prev1;
    }
}


// -----------------------------------------------
// Bài 3: Longest Increasing Subsequence (LeetCode #300) - Medium
// -----------------------------------------------
// Tìm độ dài dãy con tăng dài nhất (LIS).
// Subsequence ≠ subarray (không cần liên tiếp, nhưng phải giữ order)
//
// Ví dụ:
//   nums = [10, 9, 2, 5, 3, 7, 101, 18]
//   LIS = [2, 3, 7, 101] hoặc [2, 5, 7, 101]
//   Output: 4
//
// 💡 Insight: DP + Binary Search
//   Cách 1: DP O(n²) - dễ, nhưng chậm
//     dp[i] = LIS kết thúc tại index i
//     Với j<i mà nums[j]<nums[i]: dp[i] = max(dp[i], dp[j]+1)
//   
//   Cách 2: Binary Search O(n log n) - nhanh, tối ưu
//     Maintain tails[] = phần tử nhỏ nhất kết thúc LIS độ dài k
//     tails là sorted → dùng binary search
//
// ✅ Approach 1: DP O(n²)
// State: dp[i] = LIS kết thúc tại index i
// Base: dp[i] = 1 (mỗi phần tử tự nó là LIS)
//
// ⏱️ Time: O(n²)
// 📦 Space: O(n)
//
// ✅ Approach 2: Binary Search O(n log n) - BETTER
// State: tails[i] = phần tử nhỏ nhất kết thúc LIS độ dài i+1
//
// ⏱️ Time: O(n log n)
// 📦 Space: O(n)
//
// 📊 Trace code Cách 1:
//   nums = [10, 9, 2, 5, 3, 7, 101, 18]
//   
//   i=0: dp[0]=1 ([10])
//   i=1: 10<9? NO → dp[1]=1 ([9])
//   i=2: 10<2? NO, 9<2? NO → dp[2]=1 ([2])
//   i=3: 10<5? NO, 9<5? NO, 2<5? YES → dp[3]=2 ([2,5])
//   i=4: ... 2<3? YES → dp[4]=2 ([2,3])
//   i=5: ... 2<7? YES, 5<7? YES → dp[5]=3 ([2,5,7])
//   i=6: ... 5<101? YES (dp[3]+1=3) → dp[6]=4 ([2,5,7,101])
//   i=7: ... 7<18? YES → dp[7]=4
//   
//   maxLen = 4
//
// 📊 Trace code Cách 2:
//   nums = [10, 9, 2, 5, 3, 7, 101, 18]
//   
//   tails = [] (empty, smallest tail kết thúc LIS độ dài k)
//   
//   num=10: binary_search(10) → pos=0 → tails=[10]
//   num=9:  binary_search(9)  → pos=0 (replace) → tails=[9]
//   num=2:  binary_search(2)  → pos=0 (replace) → tails=[2]
//   num=5:  binary_search(5)  → pos=1 (append) → tails=[2,5]
//   num=3:  binary_search(3)  → pos=1 (replace) → tails=[2,3]
//   num=7:  binary_search(7)  → pos=2 (append) → tails=[2,3,7]
//   num=101: binary_search(101) → pos=3 (append) → tails=[2,3,7,101]
//   num=18: binary_search(18) → pos=3 (replace) → tails=[2,3,7,18]
//   
//   return tails.size() = 4 ✓
//
// 🔄 Comparison
//   |Approach|Time  |Space|Notes|
//   |DP O(n²)|O(n²) |O(n) |Straightforward|
//   |DP+BS ⭐|O(n log n)|O(n)|Optimal|
//   |Greedy+BS = Patience Sorting|
//
// 🚨 Edge cases:
//   - nums = [] → 0
//   - nums = [1] → 1
//   - nums = [5,4,3,2,1] (decreasing) → 1
//   - nums = [1,2,3,4,5] (increasing) → 5
//
// 💡 Key insight Binary Search:
//   tails[k] = phần tử nhỏ nhất có thể kết thúc LIS độ dài k+1
//   Nếu num > all tails → extend tails
//   Nếu num có thể thay tails[k] → thay (giữ tails nhỏ)
//   Tại sao? Vì tails nhỏ → cơ hội tìm num lớn + tạo LIS dài hơn
class LongestIncreasingSubsequence {
    
    // Cách 1: DP O(n²) — dễ hiểu
    public int lengthOfLIS_DP(int[] nums) {
        if (nums.length == 0) return 0;
        
        int n = nums.length;
        int[] dp = new int[n];
        
        // Base: mỗi phần tử tự nó là LIS dài 1
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
        }
        
        int maxLen = 1;
        
        // DP: với mỗi i, check tất cả j < i
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                // Nếu nums[j] < nums[i], có thể extend LIS kết thúc tại j
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }
        
        return maxLen;
    }
    
    // Cách 2: Binary Search O(n log n) — tối ưu ⭐
    public int lengthOfLIS(int[] nums) {
        if (nums.length == 0) return 0;
        
        // tails[i] = phần tử nhỏ nhất kết thúc LIS độ dài i+1
        // tails luôn sorted
        List<Integer> tails = new ArrayList<>();
        
        for (int num : nums) {
            // Tìm vị trí để chèn num
            int pos = Collections.binarySearch(tails, num);
            
            // binarySearch trả về:
            // - index nếu tìm thấy
            // - -(insertionPoint + 1) nếu không tìm thấy
            if (pos < 0) {
                pos = -(pos + 1);  // Convert về vị trí chèn
            }
            
            if (pos == tails.size()) {
                // num lớn hơn tất cả → extend LIS
                tails.add(num);
            } else {
                // Replace tails[pos] = num để giữ tails nhỏ
                tails.set(pos, num);
            }
        }
        
        return tails.size();
    }
}

