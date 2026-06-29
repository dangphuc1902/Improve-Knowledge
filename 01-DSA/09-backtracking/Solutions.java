import java.util.*;

/**
 * 09 - Backtracking Solutions
 * Các bài: Subsets, Permutations, Combination Sum, Word Search, N-Queens
 */
public class Solutions {

    // ============================================================
    // LC 78 - Subsets
    // Approach 1: Backtracking — O(n * 2^n) time ⭐
    // Approach 2: Bitmask — O(n * 2^n) time, O(1) extra space
    // Approach 3: Iterative — O(n * 2^n) time
    // ============================================================
    static class Subsets {
        // ⭐ Approach 1: Backtracking
        public List<List<Integer>> subsets(int[] nums) {
            List<List<Integer>> result = new ArrayList<>();
            dfs(nums, 0, new ArrayList<>(), result);
            return result;
        }

        private void dfs(int[] nums, int start, List<Integer> curr, List<List<Integer>> result) {
            result.add(new ArrayList<>(curr)); // snapshot mỗi state
            for (int i = start; i < nums.length; i++) {
                curr.add(nums[i]);
                dfs(nums, i + 1, curr, result); // i+1: không dùng lại phần tử
                curr.remove(curr.size() - 1);   // backtrack
            }
        }

        // Approach 2: Bitmask — compact, O(2^n * n)
        public List<List<Integer>> subsetsBitmask(int[] nums) {
            List<List<Integer>> result = new ArrayList<>();
            int n = nums.length;
            for (int mask = 0; mask < (1 << n); mask++) {
                List<Integer> subset = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) subset.add(nums[i]);
                }
                result.add(subset);
            }
            return result;
        }

        // Approach 3: Iterative
        public List<List<Integer>> subsetsIterative(int[] nums) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            for (int num : nums) {
                int size = result.size();
                for (int i = 0; i < size; i++) {
                    List<Integer> newSubset = new ArrayList<>(result.get(i));
                    newSubset.add(num);
                    result.add(newSubset);
                }
            }
            return result;
        }
    }

    // ============================================================
    // LC 90 - Subsets II (with duplicates)
    // Approach: Sort + Skip duplicate at same level ⭐
    // ============================================================
    static class SubsetsII {
        public List<List<Integer>> subsetsWithDup(int[] nums) {
            Arrays.sort(nums); // cần sort để detect duplicate
            List<List<Integer>> result = new ArrayList<>();
            dfs(nums, 0, new ArrayList<>(), result);
            return result;
        }

        private void dfs(int[] nums, int start, List<Integer> curr, List<List<Integer>> result) {
            result.add(new ArrayList<>(curr));
            for (int i = start; i < nums.length; i++) {
                // Skip duplicate tại cùng level
                if (i > start && nums[i] == nums[i - 1]) continue;
                curr.add(nums[i]);
                dfs(nums, i + 1, curr, result);
                curr.remove(curr.size() - 1);
            }
        }
    }

    // ============================================================
    // LC 46 - Permutations
    // Approach 1: Used Array — O(n * n!) time ⭐
    // Approach 2: Swap in-place — O(n * n!) time, O(1) extra
    // ============================================================
    static class Permutations {
        // ⭐ Approach 1: boolean[] used
        public List<List<Integer>> permute(int[] nums) {
            List<List<Integer>> result = new ArrayList<>();
            dfs(nums, new boolean[nums.length], new ArrayList<>(), result);
            return result;
        }

        private void dfs(int[] nums, boolean[] used, List<Integer> curr, List<List<Integer>> result) {
            if (curr.size() == nums.length) {
                result.add(new ArrayList<>(curr));
                return;
            }
            for (int i = 0; i < nums.length; i++) {
                if (used[i]) continue;
                used[i] = true;
                curr.add(nums[i]);
                dfs(nums, used, curr, result);
                curr.remove(curr.size() - 1);
                used[i] = false;
            }
        }

        // Approach 2: Swap in-place
        public List<List<Integer>> permuteSwap(int[] nums) {
            List<List<Integer>> result = new ArrayList<>();
            dfsSwap(nums, 0, result);
            return result;
        }

        private void dfsSwap(int[] nums, int start, List<List<Integer>> result) {
            if (start == nums.length) {
                List<Integer> list = new ArrayList<>();
                for (int n : nums) list.add(n);
                result.add(list);
                return;
            }
            for (int i = start; i < nums.length; i++) {
                swap(nums, start, i);
                dfsSwap(nums, start + 1, result);
                swap(nums, start, i); // swap back
            }
        }

        private void swap(int[] nums, int i, int j) {
            int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp;
        }
    }

    // ============================================================
    // LC 39 - Combination Sum (can reuse elements)
    // Approach 1: Backtracking with pruning — O(n^(T/M)) ⭐
    // ============================================================
    static class CombinationSum {
        // ⭐ Optimal: Sort + Backtracking với pruning
        public List<List<Integer>> combinationSum(int[] candidates, int target) {
            Arrays.sort(candidates);
            List<List<Integer>> result = new ArrayList<>();
            dfs(candidates, target, 0, new ArrayList<>(), result);
            return result;
        }

        private void dfs(int[] candidates, int remaining, int start,
                         List<Integer> curr, List<List<Integer>> result) {
            if (remaining == 0) {
                result.add(new ArrayList<>(curr));
                return;
            }
            for (int i = start; i < candidates.length; i++) {
                if (candidates[i] > remaining) break; // pruning (sorted)
                curr.add(candidates[i]);
                dfs(candidates, remaining - candidates[i], i, curr, result); // i: reuse allowed
                curr.remove(curr.size() - 1);
            }
        }
    }

    // ============================================================
    // LC 79 - Word Search
    // Approach 1: DFS + Backtracking on grid — O(R*C * 4 * 3^(L-1)) ⭐
    // ============================================================
    static class WordSearch {
        // ⭐ Optimal: DFS với in-place marking '#'
        public boolean exist(char[][] board, String word) {
            int rows = board.length, cols = board[0].length;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (dfs(board, word, r, c, 0)) return true;
                }
            }
            return false;
        }

        private boolean dfs(char[][] board, String word, int r, int c, int idx) {
            if (idx == word.length()) return true;
            if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
            if (board[r][c] != word.charAt(idx)) return false;

            char temp = board[r][c];
            board[r][c] = '#'; // mark visited

            boolean found = dfs(board, word, r + 1, c, idx + 1)
                         || dfs(board, word, r - 1, c, idx + 1)
                         || dfs(board, word, r, c + 1, idx + 1)
                         || dfs(board, word, r, c - 1, idx + 1);

            board[r][c] = temp; // restore (backtrack)
            return found;
        }
    }

    // ============================================================
    // LC 51 - N-Queens (Hard — Classic Backtracking)
    // Approach: Backtracking với 3 HashSets check conflict ⭐
    // ============================================================
    static class NQueens {
        // ⭐ Optimal: 3 Sets check column, diagonal, anti-diagonal
        // Time: O(n!), Space: O(n)
        public List<List<String>> solveNQueens(int n) {
            List<List<String>> result = new ArrayList<>();
            Set<Integer> cols = new HashSet<>();
            Set<Integer> diag = new HashSet<>();     // row - col
            Set<Integer> antiDiag = new HashSet<>(); // row + col

            dfs(0, n, cols, diag, antiDiag, new ArrayList<>(), result);
            return result;
        }

        private void dfs(int row, int n, Set<Integer> cols, Set<Integer> diag,
                         Set<Integer> antiDiag, List<String> board, List<List<String>> result) {
            if (row == n) {
                result.add(new ArrayList<>(board));
                return;
            }
            for (int col = 0; col < n; col++) {
                if (cols.contains(col) || diag.contains(row - col) || antiDiag.contains(row + col)) {
                    continue; // conflict → skip
                }
                // Place queen
                cols.add(col);
                diag.add(row - col);
                antiDiag.add(row + col);
                char[] rowChars = new char[n];
                Arrays.fill(rowChars, '.');
                rowChars[col] = 'Q';
                board.add(new String(rowChars));

                dfs(row + 1, n, cols, diag, antiDiag, board, result);

                // Remove queen (backtrack)
                cols.remove(col);
                diag.remove(row - col);
                antiDiag.remove(row + col);
                board.remove(board.size() - 1);
            }
        }
    }
}
