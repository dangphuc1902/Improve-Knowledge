import java.util.*;

/**
 * 17 - Bit Manipulation Solutions
 * Các bài: Single Number, Number of 1 Bits, Counting Bits, Reverse Bits,
 *          Missing Number, Sum of Two Integers, Hamming Distance
 */
public class Solutions {

    // ============================================================
    // LC 136 - Single Number
    // Approach 1: XOR — O(n) time, O(1) space ⭐
    // Approach 2: HashMap — O(n) time, O(n) space
    // ============================================================
    static class SingleNumber {
        // ⭐ XOR: a^a=0, a^0=a. Tất cả cặp triệt tiêu → chỉ còn single
        public int singleNumber(int[] nums) {
            int result = 0;
            for (int num : nums) result ^= num;
            return result;
        }

        // Approach 2: HashMap
        public int singleNumberHashMap(int[] nums) {
            Map<Integer, Integer> freq = new HashMap<>();
            for (int num : nums) freq.merge(num, 1, Integer::sum);
            for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                if (e.getValue() == 1) return e.getKey();
            }
            return -1;
        }
    }

    // ============================================================
    // LC 191 - Number of 1 Bits (Hamming Weight)
    // Approach 1: n & (n-1) — O(k) k=number of set bits ⭐
    // Approach 2: Bit shifting — O(32) = O(1)
    // Approach 3: Java built-in
    // ============================================================
    static class NumberOf1Bits {
        // ⭐ n & (n-1) bỏ lowest set bit mỗi lần — O(k)
        public int hammingWeight(int n) {
            int count = 0;
            while (n != 0) {
                n &= (n - 1); // remove lowest set bit
                count++;
            }
            return count;
        }

        // Approach 2: Shift right 32 times — O(32) = O(1)
        public int hammingWeightShift(int n) {
            int count = 0;
            for (int i = 0; i < 32; i++) {
                count += (n & 1);
                n >>>= 1; // unsigned right shift
            }
            return count;
        }

        // Approach 3: Java built-in
        public int hammingWeightBuiltIn(int n) {
            return Integer.bitCount(n);
        }
    }

    // ============================================================
    // LC 338 - Counting Bits
    // Approach 1: DP — O(n) time, O(n) space ⭐
    // Approach 2: For each num use bitCount — O(n log n)
    // ============================================================
    static class CountingBits {
        // ⭐ DP: dp[i] = dp[i >> 1] + (i & 1)
        // dp[i/2] = bits count of i without LSB, (i&1) = LSB
        public int[] countBits(int n) {
            int[] dp = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                dp[i] = dp[i >> 1] + (i & 1);
            }
            return dp;
        }

        // Approach 2: dp[i] = dp[i & (i-1)] + 1 (same idea, different form)
        public int[] countBitsAlt(int n) {
            int[] dp = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                dp[i] = dp[i & (i - 1)] + 1; // dp[i without lowest set bit] + 1
            }
            return dp;
        }
    }

    // ============================================================
    // LC 190 - Reverse Bits
    // Approach 1: Bit by bit — O(32) = O(1) ⭐
    // Approach 2: Divide and Conquer — O(1)
    // ============================================================
    static class ReverseBits {
        // ⭐ Shift and build result bit by bit
        public int reverseBits(int n) {
            int result = 0;
            for (int i = 0; i < 32; i++) {
                result = (result << 1) | (n & 1); // append LSB of n to result
                n >>= 1;
            }
            return result;
        }

        // Approach 2: Divide & Conquer (swap halves recursively)
        public int reverseBitsDivide(int n) {
            n = ((n & 0xffff0000) >>> 16) | ((n & 0x0000ffff) << 16);
            n = ((n & 0xff00ff00) >>> 8)  | ((n & 0x00ff00ff) << 8);
            n = ((n & 0xf0f0f0f0) >>> 4)  | ((n & 0x0f0f0f0f) << 4);
            n = ((n & 0xcccccccc) >>> 2)  | ((n & 0x33333333) << 2);
            n = ((n & 0xaaaaaaaa) >>> 1)  | ((n & 0x55555555) << 1);
            return n;
        }
    }

    // ============================================================
    // LC 268 - Missing Number
    // Approach 1: XOR — O(n) time, O(1) space ⭐
    // Approach 2: Math (Gauss formula) — O(n) time, O(1) space
    // Approach 3: Sort — O(n log n)
    // ============================================================
    static class MissingNumber {
        // ⭐ Approach 1: XOR
        // XOR [0..n] với [nums] → cặp nào trùng triệt tiêu, còn missing
        public int missingNumber(int[] nums) {
            int missing = nums.length;
            for (int i = 0; i < nums.length; i++) {
                missing ^= i ^ nums[i];
            }
            return missing;
        }

        // Approach 2: Math — expected sum - actual sum
        public int missingNumberMath(int[] nums) {
            int n = nums.length;
            int expected = n * (n + 1) / 2;
            int actual = 0;
            for (int num : nums) actual += num;
            return expected - actual;
        }
    }

    // ============================================================
    // LC 371 - Sum of Two Integers (No + or -)
    // Approach: XOR + Carry — O(1) time, O(1) space ⭐
    // ============================================================
    static class SumOfTwoIntegers {
        // ⭐ XOR = sum without carry, AND<<1 = carry
        public int getSum(int a, int b) {
            while (b != 0) {
                int carry = (a & b) << 1;
                a = a ^ b;  // sum without carry
                b = carry;  // propagate carry
            }
            return a;
        }
    }

    // ============================================================
    // LC 461 - Hamming Distance
    // Approach: XOR then count 1s — O(1) ⭐
    // ============================================================
    static class HammingDistance {
        // ⭐ XOR x ^ y → bit 1 ở vị trí 2 số khác nhau. Đếm bit 1.
        public int hammingDistance(int x, int y) {
            return Integer.bitCount(x ^ y);
        }
    }

    // ============================================================
    // Bonus: Power of Two / Power of Four
    // ============================================================
    static class PowerChecks {
        // LC 231 - Power of Two
        public boolean isPowerOfTwo(int n) {
            return n > 0 && (n & (n - 1)) == 0;
        }

        // LC 342 - Power of Four
        // Power of 4: power of 2 AND bit is at even position
        // 0x55555555 = ...01010101 (odd positions = 0, even = 1)
        public boolean isPowerOfFour(int n) {
            return n > 0 && (n & (n - 1)) == 0 && (n & 0x55555555) != 0;
        }
    }
}
