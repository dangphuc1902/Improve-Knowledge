import java.util.*;

/**
 * =============================================
 *  18 - MATH & GEOMETRY
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Rotate Image (LeetCode #48) - Medium
// -----------------------------------------------
// Rotate ma trận n×n 90° clockwise IN-PLACE.
// Ví dụ: [[1,2,3],[4,5,6],[7,8,9]]
//       → [[7,4,1],[8,5,2],[9,6,3]]
//
// Approach: 2 bước:
// 1. Transpose: matrix[i][j] ↔ matrix[j][i]
// 2. Reverse mỗi row
//
// Time: O(n²)
// Space: O(1) - in-place
class RotateImage {
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Bước 1: Transpose (đối xứng qua đường chéo chính)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Bước 2: Reverse mỗi row
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }
}

// -----------------------------------------------
// Bài 2: Spiral Matrix (LeetCode #54) - Medium
// -----------------------------------------------
// Trả về tất cả phần tử của matrix theo thứ tự spiral (xoắn ốc).
// Ví dụ: [[1,2,3],[4,5,6],[7,8,9]]
//       → [1,2,3,6,9,8,7,4,5]
//
// Approach: Dùng 4 biên (top, bottom, left, right).
// Duyệt theo 4 hướng: → ↓ ← ↑, thu hẹp biên sau mỗi hướng.
//
// Time: O(m × n) - visit mỗi phần tử 1 lần
// Space: O(1) - ngoài output
class SpiralMatrix {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // Đi sang phải → trên top row
            for (int c = left; c <= right; c++) {
                result.add(matrix[top][c]);
            }
            top++;

            // Đi xuống ↓ trên right column
            for (int r = top; r <= bottom; r++) {
                result.add(matrix[r][right]);
            }
            right--;

            // Đi sang trái ← trên bottom row (nếu còn row)
            if (top <= bottom) {
                for (int c = right; c >= left; c--) {
                    result.add(matrix[bottom][c]);
                }
                bottom--;
            }

            // Đi lên ↑ trên left column (nếu còn column)
            if (left <= right) {
                for (int r = bottom; r >= top; r--) {
                    result.add(matrix[r][left]);
                }
                left++;
            }
        }

        return result;
    }
}

// -----------------------------------------------
// Bài 3: Pow(x, n) (LeetCode #50) - Medium
// -----------------------------------------------
// Tính x^n (x mũ n). n có thể âm.
//
// Approach: Binary Exponentiation (Fast Power).
// x^n = (x^(n/2))² nếu n chẵn
// x^n = x × (x^(n/2))² nếu n lẻ
// Giảm từ O(n) → O(log n).
//
// Lưu ý: n = Integer.MIN_VALUE → -n overflow → dùng long.
//
// Time: O(log n)
// Space: O(1) iterative
class PowXN {
    public double myPow(double x, int n) {
        long N = n; // Dùng long để tránh overflow khi n = Integer.MIN_VALUE

        // Xử lý số mũ âm: x^(-n) = (1/x)^n
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;

        // Binary exponentiation
        while (N > 0) {
            if ((N & 1) == 1) {
                // N lẻ → nhân thêm x vào kết quả
                result *= x;
            }
            x *= x;    // x = x²
            N >>= 1;   // N = N / 2
        }

        return result;
    }
}
