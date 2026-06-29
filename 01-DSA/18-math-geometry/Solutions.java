import java.util.*;

/**
 * 18 - Math & Geometry Solutions
 * Các bài: Rotate Image, Spiral Matrix, Set Matrix Zeroes,
 *          Happy Number, Count Primes, Plus One
 */
public class Solutions {

    // ============================================================
    // LC 48 - Rotate Image (90° clockwise, in-place)
    // Approach 1: Transpose + Reverse — O(n²), O(1) ⭐
    // Approach 2: 4-way swap — O(n²), O(1)
    // ============================================================
    static class RotateImage {
        // ⭐ Approach 1: Transpose then Reverse each row
        // Transpose: matrix[i][j] ↔ matrix[j][i]
        // Reverse rows: [1,2,3] → [3,2,1]
        public void rotate(int[][] matrix) {
            int n = matrix.length;

            // Step 1: Transpose
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int temp = matrix[i][j];
                    matrix[i][j] = matrix[j][i];
                    matrix[j][i] = temp;
                }
            }

            // Step 2: Reverse each row
            for (int i = 0; i < n; i++) {
                int left = 0, right = n - 1;
                while (left < right) {
                    int temp = matrix[i][left];
                    matrix[i][left++] = matrix[i][right];
                    matrix[i][right--] = temp;
                }
            }
        }

        // Approach 2: 4-way swap (rotate 4 corners at a time)
        public void rotate4Way(int[][] matrix) {
            int n = matrix.length;
            for (int i = 0; i < n / 2; i++) {
                for (int j = i; j < n - 1 - i; j++) {
                    int temp = matrix[i][j];
                    matrix[i][j] = matrix[n - 1 - j][i];
                    matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j];
                    matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i];
                    matrix[j][n - 1 - i] = temp;
                }
            }
        }
    }

    // ============================================================
    // LC 54 - Spiral Matrix
    // Approach 1: 4 Boundaries — O(m*n), O(1) extra ⭐
    // ============================================================
    static class SpiralMatrix {
        // ⭐ 4 boundaries: top, bottom, left, right
        public List<Integer> spiralOrder(int[][] matrix) {
            List<Integer> result = new ArrayList<>();
            int top = 0, bottom = matrix.length - 1;
            int left = 0, right = matrix[0].length - 1;

            while (top <= bottom && left <= right) {
                // → top row left to right
                for (int i = left; i <= right; i++) result.add(matrix[top][i]);
                top++;

                // ↓ right col top to bottom
                for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
                right--;

                // ← bottom row right to left
                if (top <= bottom) {
                    for (int i = right; i >= left; i--) result.add(matrix[bottom][i]);
                    bottom--;
                }

                // ↑ left col bottom to top
                if (left <= right) {
                    for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
                    left++;
                }
            }
            return result;
        }
    }

    // ============================================================
    // LC 59 - Spiral Matrix II (Generate n×n spiral)
    // ============================================================
    static class SpiralMatrixII {
        public int[][] generateMatrix(int n) {
            int[][] matrix = new int[n][n];
            int top = 0, bottom = n - 1, left = 0, right = n - 1, num = 1;

            while (top <= bottom && left <= right) {
                for (int i = left; i <= right; i++) matrix[top][i] = num++;
                top++;
                for (int i = top; i <= bottom; i++) matrix[i][right] = num++;
                right--;
                if (top <= bottom) { for (int i = right; i >= left; i--) matrix[bottom][i] = num++; bottom--; }
                if (left <= right) { for (int i = bottom; i >= top; i--) matrix[i][left] = num++; left++; }
            }
            return matrix;
        }
    }

    // ============================================================
    // LC 73 - Set Matrix Zeroes
    // Approach 1: O(1) space (use first row/col as markers) ⭐
    // Approach 2: O(m+n) space (store rows/cols to zero)
    // ============================================================
    static class SetMatrixZeroes {
        // ⭐ O(1) space: use first row and first column as markers
        public void setZeroes(int[][] matrix) {
            int m = matrix.length, n = matrix[0].length;
            boolean firstRowZero = false, firstColZero = false;

            // Check if first row/col have zeros
            for (int j = 0; j < n; j++) if (matrix[0][j] == 0) firstRowZero = true;
            for (int i = 0; i < m; i++) if (matrix[i][0] == 0) firstColZero = true;

            // Use first row/col as markers for the rest
            for (int i = 1; i < m; i++) {
                for (int j = 1; j < n; j++) {
                    if (matrix[i][j] == 0) { matrix[i][0] = 0; matrix[0][j] = 0; }
                }
            }

            // Zero out cells based on markers
            for (int i = 1; i < m; i++) {
                for (int j = 1; j < n; j++) {
                    if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
                }
            }

            // Handle first row and first column
            if (firstRowZero) Arrays.fill(matrix[0], 0);
            if (firstColZero) for (int i = 0; i < m; i++) matrix[i][0] = 0;
        }
    }

    // ============================================================
    // LC 202 - Happy Number
    // Approach 1: Floyd's Cycle Detection — O(log n), O(1) ⭐
    // Approach 2: HashSet — O(log n), O(log n)
    // ============================================================
    static class HappyNumber {
        // ⭐ Floyd's — fast và slow converge nếu có cycle
        public boolean isHappy(int n) {
            int slow = n, fast = getSquareSum(n);
            while (fast != 1 && slow != fast) {
                slow = getSquareSum(slow);
                fast = getSquareSum(getSquareSum(fast));
            }
            return fast == 1;
        }

        // Approach 2: HashSet
        public boolean isHappySet(int n) {
            Set<Integer> seen = new HashSet<>();
            while (n != 1) {
                if (!seen.add(n)) return false; // cycle detected
                n = getSquareSum(n);
            }
            return true;
        }

        private int getSquareSum(int n) {
            int sum = 0;
            while (n > 0) {
                int d = n % 10;
                sum += d * d;
                n /= 10;
            }
            return sum;
        }
    }

    // ============================================================
    // LC 204 - Count Primes
    // Approach 1: Sieve of Eratosthenes — O(n log log n), O(n) ⭐
    // Approach 2: Trial division for each — O(n√n)
    // ============================================================
    static class CountPrimes {
        // ⭐ Sieve of Eratosthenes
        public int countPrimes(int n) {
            if (n <= 2) return 0;
            boolean[] isComposite = new boolean[n]; // false = prime
            int count = 0;

            for (int i = 2; i < n; i++) {
                if (!isComposite[i]) {
                    count++;
                    // Start from i*i (all smaller multiples already marked)
                    // Cast to long to avoid overflow
                    for (long j = (long) i * i; j < n; j += i) {
                        isComposite[(int) j] = true;
                    }
                }
            }
            return count;
        }
    }

    // ============================================================
    // LC 66 - Plus One
    // Approach: Simple carry propagation — O(n), O(1) ⭐
    // ============================================================
    static class PlusOne {
        public int[] plusOne(int[] digits) {
            for (int i = digits.length - 1; i >= 0; i--) {
                if (digits[i] < 9) {
                    digits[i]++;
                    return digits; // no carry
                }
                digits[i] = 0; // carry over
            }
            // All digits were 9 → need new leading 1
            int[] result = new int[digits.length + 1];
            result[0] = 1;
            return result;
        }
    }

    // ============================================================
    // Utility: Math functions
    // ============================================================
    static class MathUtils {
        // GCD — Euclid's Algorithm
        public static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }

        // LCM
        public static int lcm(int a, int b) { return a / gcd(a, b) * b; }

        // Fast Power (modular) — O(log n)
        public static long powMod(long base, long exp, long mod) {
            long result = 1;
            base %= mod;
            while (exp > 0) {
                if ((exp & 1) == 1) result = result * base % mod;
                base = base * base % mod;
                exp >>= 1;
            }
            return result;
        }

        // Check if n is prime — O(sqrt(n))
        public static boolean isPrime(int n) {
            if (n < 2) return false;
            for (int i = 2; (long) i * i <= n; i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }
}
