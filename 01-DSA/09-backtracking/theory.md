# 09 - Backtracking

## 📖 Tổng quan

**Backtracking** là kỹ thuật đệ quy **thử tất cả** các lựa chọn, khi gặp ngõ cụt thì **quay lui** (backtrack) và thử lựa chọn khác. Xây dựng **cây quyết định (Decision Tree)** ngầm.

> **Ý tưởng cốt lõi:** "Thử → Đi sâu → Quay lui → Thử cái khác". Template: `make choice → recurse → undo choice`.

## 🧠 Kiến thức cốt lõi

### Template Chuẩn

```java
void backtrack(State state, List<Result> results, ...) {
    // Base case: khi đạt điều kiện dừng
    if (isComplete(state)) {
        results.add(new ArrayList<>(state)); // snapshot!
        return;
    }
    
    for (Choice choice : getChoices()) {
        if (isValid(choice, state)) {
            makeChoice(state, choice);   // 1. Apply
            backtrack(state, results);   // 2. Recurse
            undoChoice(state, choice);   // 3. Undo (backtrack)
        }
    }
}
```

### Decision Tree

```
Subsets([1,2,3]):
            []
          / | \
        [1] [2] [3]
       /  \   \
    [1,2] [1,3] [2,3]
      |
   [1,2,3]
```

## 🔍 Khi nào sử dụng?

- Tìm **tất cả** tập hợp/hoán vị/tổ hợp thỏa điều kiện
- Bài toán **decision making** với nhiều nhánh
- **Constraint satisfaction**: Sudoku, N-Queens
- **Path finding** trong maze/grid
- Cụm từ trong đề: *"all possible"*, *"generate all"*, *"find all combinations/permutations"*

## 📝 Các Pattern phổ biến

### Pattern 1: Subsets — Chọn hay không chọn
- **Nó là gì?**: Với mỗi phần tử, quyết định: chọn (include) hay không chọn (exclude).
- **Giải quyết bài toán nào?**: Subsets (LC 78), Subsets II (có duplicate, LC 90), Combination Sum II.
- **Ưu điểm**: O(n * 2^n) — sinh tất cả 2^n tập hợp.
- **Sự thay thế**: Bitmask O(2^n * n) — dùng số nhị phân để biểu diễn tập hợp.

```java
// Subsets — không duplicate
void dfs(int[] nums, int start, List<Integer> curr, List<List<Integer>> result) {
    result.add(new ArrayList<>(curr)); // add tại mỗi bước (kể cả rỗng)
    for (int i = start; i < nums.length; i++) {
        curr.add(nums[i]);             // choose
        dfs(nums, i + 1, curr, result); // next element (không dùng lại)
        curr.remove(curr.size() - 1);  // unchoose
    }
}
```

### Pattern 2: Permutations — Sắp xếp tất cả
- **Nó là gì?**: Sinh tất cả hoán vị của mảng — dùng `used[]` hoặc swap.
- **Giải quyết bài toán nào?**: Permutations (LC 46), Permutations II (duplicate, LC 47).
- **Ưu điểm**: O(n * n!) — sinh tất cả n! hoán vị.
- **Sự thay thế**: Iterative next permutation (LC 31).

```java
// Permutations — dùng boolean[] used
void dfs(int[] nums, boolean[] used, List<Integer> curr, List<List<Integer>> result) {
    if (curr.size() == nums.length) {
        result.add(new ArrayList<>(curr));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue; // đã dùng rồi
        used[i] = true;
        curr.add(nums[i]);
        dfs(nums, used, curr, result);
        curr.remove(curr.size() - 1);
        used[i] = false;
    }
}

// Permutations — swap (in-place, không dùng extra space)
void dfsSwap(int[] nums, int start, List<List<Integer>> result) {
    if (start == nums.length) {
        // convert array to list
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
```

### Pattern 3: Combination Sum — Tái sử dụng phần tử
- **Nó là gì?**: Tìm các bộ số có tổng bằng target. Cho phép dùng lại cùng phần tử.
- **Giải quyết bài toán nào?**: Combination Sum (LC 39), Combination Sum II (LC 40).
- **Ưu điểm**: Prune sớm khi `remaining < 0`.
- **Sự thay thế**: DP — nếu chỉ cần đếm số cách (không cần liệt kê).

```java
void dfs(int[] candidates, int target, int start, List<Integer> curr, List<List<Integer>> result) {
    if (target == 0) {
        result.add(new ArrayList<>(curr));
        return;
    }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > target) break; // pruning (cần sort trước)
        curr.add(candidates[i]);
        dfs(candidates, target - candidates[i], i, curr, result); // i not i+1 → reuse
        curr.remove(curr.size() - 1);
    }
}
```

### Pattern 4: Grid DFS — Path/Island/Word Search
- **Nó là gì?**: DFS trên lưới 2D, đánh dấu visited và restore.
- **Giải quyết bài toán nào?**: Word Search (LC 79), Path Sum, N-Queens.
- **Ưu điểm**: Khai phá không gian 2D hiệu quả với backtracking.
- **Trick**: Đổi `board[i][j] = '#'` thay vì dùng `visited[][]` để tiết kiệm space.

```java
// Word Search
boolean dfs(char[][] board, String word, int r, int c, int idx) {
    if (idx == word.length()) return true;
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
    if (board[r][c] != word.charAt(idx)) return false;

    char temp = board[r][c];
    board[r][c] = '#'; // mark visited
    boolean found = dfs(board, word, r+1, c, idx+1)
                 || dfs(board, word, r-1, c, idx+1)
                 || dfs(board, word, r, c+1, idx+1)
                 || dfs(board, word, r, c-1, idx+1);
    board[r][c] = temp; // restore
    return found;
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Subsets — Decision Tree

```
nums = [1, 2, 3]

dfs(start=0, curr=[]):
  add [] → result=[[]]
  
  i=0: choose 1 → curr=[1]
    dfs(start=1, curr=[1]):
      add [1] → result=[[], [1]]
      
      i=1: choose 2 → curr=[1,2]
        dfs(start=2, curr=[1,2]):
          add [1,2] → result=[...,[1,2]]
          
          i=2: choose 3 → curr=[1,2,3]
            dfs(start=3): add [1,2,3]
          unchoose 3 → curr=[1,2]
        
      unchoose 2 → curr=[1]
      
      i=2: choose 3 → curr=[1,3]
        dfs(start=3): add [1,3]
      unchoose 3 → curr=[1]
    
  unchoose 1 → curr=[]
  
  i=1: choose 2 → curr=[2]
    dfs(start=2, curr=[2]):
      add [2]
      i=2: choose 3 → [2,3] → add [2,3] ...
  
  i=2: choose 3 → curr=[3]
    add [3]

✅ Output: [[], [1], [1,2], [1,2,3], [1,3], [2], [2,3], [3]]
```

### Ví dụ 2: Combination Sum — Pruning

```
candidates = [2,3,6,7] (sorted), target = 7

dfs(target=7, start=0):
  i=0: choose 2, target=5
    dfs(target=5, start=0):
      i=0: choose 2, target=3
        dfs(target=3, start=0):
          i=0: choose 2, target=1
            dfs(target=1):
              i=0: 2>1 → PRUNE (break)
          i=1: choose 3, target=0 → ✅ add [2,2,3]
          i=2: 6>3 → PRUNE
      i=1: choose 3, target=2
        dfs(target=2):
          i=1: choose 3 → 3>2 → PRUNE
          (chỉ 2 là valid, nhưng start=1 nên không thể chọn)
      i=2: 6>5 → PRUNE
  i=1: choose 3, target=4
    dfs(target=4, start=1):
      i=1: choose 3, target=1
        dfs(target=1): PRUNE
      i=2: 6>4 → PRUNE
  i=2: choose 6, target=1: PRUNE
  i=3: choose 7, target=0 → ✅ add [7]

✅ Output: [[2,2,3], [7]]
```

## 🔄 So sánh các Approach

### Subsets: Backtracking vs Bitmask vs Iterative

| Approach | Time | Space | Code |
|----------|------|-------|------|
| **Backtracking ⭐** | O(n * 2^n) | O(n) recursion | Trực quan |
| Bitmask | O(n * 2^n) | O(1) | Compact nhưng khó đọc |
| Iterative | O(n * 2^n) | O(2^n) | Dễ hiểu |

### Permutations: Used Array vs Swap

| Approach | Time | Space |
|----------|------|-------|
| **Used Array ⭐** | O(n * n!) | O(n) |
| Swap in-place | O(n * n!) | O(1) extra |

## 🚨 Edge Cases cần chú ý

```java
// Subsets:
// 1. nums = [] → [[]] (chỉ tập rỗng)
// 2. nums = [1] → [[], [1]]
// 3. Duplicate: [1,2,2] → cần sort + skip dup
//    if (i > start && nums[i] == nums[i-1]) continue;

// Permutations II (với duplicate):
// Cần sort + skip: if (used[i] || (i > 0 && nums[i]==nums[i-1] && !used[i-1])) continue;

// Combination Sum:
// 1. candidates = [2], target = 1 → [] (không có tổng hợp lệ)
// 2. target = 0 → [[]] (tập rỗng)
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space (result excluded) |
|----------|------|------------------------|
| Subsets | O(n * 2^n) | O(n) |
| Permutations | O(n * n!) | O(n) |
| Combination Sum | O(n^(T/M)) | O(T/M) |
| Word Search | O(R * C * 4 * 3^(L-1)) | O(L) |
| N-Queens | O(n!) | O(n) |

## 💡 Tips phỏng vấn

1. **Luôn vẽ Decision Tree**: Trước khi code, phác thảo cây quyết định 2-3 cấp.
2. **`new ArrayList<>(curr)`**: **Luôn** copy snapshot khi add vào result — không add reference!
3. **Pruning**: Sort trước để prune sớm (`if (candidates[i] > target) break`).
4. **Duplicate handling**: Sort + `if (i > start && nums[i] == nums[i-1]) continue`.
5. **Reuse vs No-reuse**: `dfs(i, ...)` cho phép reuse, `dfs(i+1, ...)` không cho phép.
