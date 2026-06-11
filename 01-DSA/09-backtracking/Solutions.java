import java.util.*;

/**
 * =============================================
 *  09 - BACKTRACKING
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Subsets (LeetCode #78) - Medium
// -----------------------------------------------
// Cho mảng nums unique, tìm tất cả subsets (power set).
// Ví dụ: [1,2,3] → [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
//
// Approach: Backtracking.
// Tại mỗi bước, quyết định có thêm phần tử nums[i] vào subset hay không.
// Dùng start index để tránh duplicate subsets.
//
// Time: O(n × 2ⁿ) - 2ⁿ subsets, mỗi subset copy O(n)
// Space: O(n) - call stack depth
class Subsets {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start,
                           List<Integer> current, List<List<Integer>> result) {
        // Mọi state đều là 1 subset hợp lệ → thêm vào result
        result.add(new ArrayList<>(current)); // DEEP COPY!

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);                        // Chọn
            backtrack(nums, i + 1, current, result);     // Khám phá (từ i+1)
            current.remove(current.size() - 1);          // Bỏ chọn (backtrack)
        }
    }
}

// -----------------------------------------------
// Bài 2: Combination Sum (LeetCode #39) - Medium
// -----------------------------------------------
// Cho mảng candidates và target, tìm tất cả combinations có tổng = target.
// Mỗi số có thể dùng KHÔNG GIỚI HẠN lần.
// Ví dụ: candidates = [2,3,6,7], target = 7 → [[2,2,3],[7]]
//
// Approach: Backtracking với reuse.
// Khác subsets: cho phép dùng lại phần tử (đệ quy với i thay vì i+1).
// Pruning: dừng khi remaining < 0.
//
// Time: O(n^(target/min)) - worst case
// Space: O(target/min) - call stack depth
class CombinationSum {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates); // Sort để pruning hiệu quả hơn
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                           List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current)); // Tìm thấy combination!
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            // Pruning: nếu candidate > remaining thì các candidate sau cũng >
            if (candidates[i] > remaining) break;

            current.add(candidates[i]);
            // i (không phải i+1) vì cho phép dùng lại cùng phần tử
            backtrack(candidates, remaining - candidates[i], i, current, result);
            current.remove(current.size() - 1); // Backtrack
        }
    }
}

// -----------------------------------------------
// Bài 3: Permutations (LeetCode #46) - Medium
// -----------------------------------------------
// Cho mảng nums distinct, tìm tất cả permutations.
// Ví dụ: [1,2,3] → [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
//
// Approach: Backtracking với boolean[] used.
// Khác subsets: thứ tự quan trọng, mỗi phần tử dùng đúng 1 lần.
// Dùng used[] để track phần tử đã dùng.
//
// Time: O(n × n!) - n! permutations, mỗi cái copy O(n)
// Space: O(n) - used[] + call stack
class Permutations {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used,
                           List<Integer> current, List<List<Integer>> result) {
        // Base case: permutation đủ n phần tử
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue; // Skip phần tử đã dùng

            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, used, current, result);
            current.remove(current.size() - 1); // Backtrack
            used[i] = false;
        }
    }
}
