import java.util.*;

/**
 * =============================================
 *  17 - BIT MANIPULATION
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Single Number (LeetCode #136) - Easy
// -----------------------------------------------
// Mảng nums, mọi phần tử xuất hiện 2 lần, trừ 1 phần tử xuất hiện 1 lần.
// Tìm phần tử đó. Yêu cầu O(1) extra space.
//
// Approach: XOR tất cả.
// a ^ a = 0 (pairs cancel nhau)
// a ^ 0 = a (phần tử lẻ còn lại)
//
// Time: O(n)
// Space: O(1)
class SingleNumber {
    public int singleNumber(int[] nums) {
        int result = 0;

        for (int num : nums) {
            result ^= num; // XOR: pairs tự cancel, chỉ còn single number
        }

        return result;
    }
}

// -----------------------------------------------
// Bài 2: Counting Bits (LeetCode #338) - Easy
// -----------------------------------------------
// Cho n, trả về mảng ans[i] = số bit 1 trong biểu diễn nhị phân của i.
// Ví dụ: n = 5 → [0,1,1,2,1,2]
//
// Approach: DP + Bit manipulation.
// Key insight: i & (i-1) xóa bit 1 cuối cùng
// → ans[i] = ans[i & (i-1)] + 1
//
// Time: O(n)
// Space: O(n) - output
class CountingBits {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            // i & (i-1) = i với rightmost set bit bị xóa
            // Số bit 1 của i = số bit 1 của (i với 1 bit bị xóa) + 1
            ans[i] = ans[i & (i - 1)] + 1;
        }

        return ans;
    }
}

// -----------------------------------------------
// Bài 3: Reverse Bits (LeetCode #190) - Easy
// -----------------------------------------------
// Đảo ngược thứ tự 32 bit của số nguyên unsigned.
// Ví dụ: 00000010100101000001111010011100
//      → 00111001011110000010100101000000
//
// Approach: Duyệt 32 bit, lấy bit cuối của n, dịch vào kết quả.
// Mỗi bước: result dịch trái 1, thêm bit cuối của n, n dịch phải 1.
//
// Time: O(1) - luôn 32 iterations
// Space: O(1)
class ReverseBits {
    public int reverseBits(int n) {
        int result = 0;

        for (int i = 0; i < 32; i++) {
            // Dịch result sang trái để nhường chỗ
            result <<= 1;
            // Lấy bit cuối của n và thêm vào result
            result |= (n & 1);
            // Dịch n sang phải để xử lý bit tiếp theo
            n >>>= 1; // >>> = unsigned right shift (quan trọng trong Java!)
        }

        return result;
    }
}
