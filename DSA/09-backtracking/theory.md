# 09 - Backtracking

## 📖 Tổng quan

**Backtracking** là kỹ thuật DFS thử tất cả khả năng, quay lại (backtrack) khi phát hiện nhánh hiện tại không dẫn đến lời giải.

Cốt lõi: **Chọn → Khám phá → Bỏ chọn (undo)**

```
backtrack(state):
    if (đạt mục tiêu):
        lưu kết quả
        return
    for (mỗi lựa chọn):
        chọn
        backtrack(state tiếp)
        bỏ chọn  ← BACKTRACK
```

## 🔍 Khi nào sử dụng?

- Tìm **tất cả** combinations/permutations/subsets
- **Constraint satisfaction**: Sudoku, N-Queens
- **Path finding**: tìm tất cả đường đi
- Từ khóa: "all possible", "generate all", "find all"
- Bài yêu cầu liệt kê/đếm tất cả cấu hình hợp lệ

## 📝 Các Pattern phổ biến

### Pattern 1: Subsets (Duyệt qua các tập con)
- **Nó là gì?**: Kỹ thuật liệt kê tất cả các tập con có thể có của một tập hợp. Tại mỗi bước, ta quyết định thêm một phần tử vào tập hiện tại và tiếp tục với các phần tử phía sau.
- **Giải quyết bài toán nào?**: 
    - Tìm tất cả tập con của một mảng (`Subsets`).
    - Tìm tất cả tập con không trùng lặp (`Subsets II`).
- **Ưu điểm**:
    - Giải quyết triệt để các bài toán yêu cầu liệt kê mọi khả năng.
- **Nhược điểm**:
    - Độ phức tạp thời gian tăng theo hàm mũ O(2ⁿ), chỉ áp dụng được với `n` nhỏ (thường n < 20).
- **Sự thay thế**:
    - **Bit Manipulation**: Sử dụng các bit từ 0 đến 2ⁿ-1 để đại diện cho các tập con.

```java
void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current)); // Mọi state đều là kết quả
    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);           // Chọn
        backtrack(nums, i + 1, current, result); // Khám phá
        current.remove(current.size() - 1);      // Bỏ chọn
    }
}
```

### Pattern 2: Permutations (Hoán vị - Thứ tự quan trọng)
- **Nó là gì?**: Liệt kê tất cả các cách sắp xếp các phần tử. Ta sử dụng một mảng `used` hoặc `HashSet` để theo dõi các phần tử đã được chọn trong nhánh hiện tại.
- **Giải quyết bài toán nào?**: 
    - Tìm tất cả hoán vị của một mảng (`Permutations`).
    - Sắp xếp các chữ cái để tạo thành từ có nghĩa.
- **Ưu điểm**:
    - Đảm bảo mỗi phần tử chỉ xuất hiện đúng 1 lần trong mỗi hoán vị.
- **Nhược điểm**:
    - Độ phức tạp cực lớn O(n * n!).
- **Sự thay thế**:
    - **Iterative Permutations**: Sử dụng thuật toán Lexicographical Next Permutation.

```java
void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        used[i] = true;
        current.add(nums[i]);
        backtrack(nums, used, current, result);
        current.remove(current.size() - 1);
        used[i] = false;
    }
}
```

### Pattern 3: Combination Sum (Tổ hợp - Cho phép dùng lại phần tử)
- **Nó là gì?**: Tìm các bộ phần tử có tổng bằng `target`. Khác với Subset, ở đây ta có thể đứng yên tại chỉ số `i` hiện tại để cho phép phần tử đó được chọn nhiều lần.
- **Giải quyết bài toán nào?**: 
    - Tìm các bộ số có tổng bằng target (`Combination Sum`).
    - Bài toán đổi tiền (Coin Change - phần liệt kê cách đổi).
- **Ưu điểm**:
    - Cấu trúc đệ quy linh hoạt cho phép tùy biến việc dùng lại phần tử.
- **Nhược điểm**:
    - Dễ gây ra vòng lặp vô tận nếu không có điều kiện dừng `target < 0`.
- **Sự thay thế**:
    - **Dynamic Programming**: Nếu chỉ cần đếm số cách hoặc tìm số lượng ít nhất (không yêu cầu liệt kê cụ thể các bộ số).

```java
void backtrack(int[] candidates, int target, int start,
               List<Integer> current, List<List<Integer>> result) {
    if (target == 0) { result.add(new ArrayList<>(current)); return; }
    if (target < 0) return;

    for (int i = start; i < candidates.length; i++) {
        current.add(candidates[i]);
        backtrack(candidates, target - candidates[i], i, current, result); // i, không phải i+1
        current.remove(current.size() - 1);
    }
}
```

## ⏱️ Complexity thường gặp

| Bài | Time | Space |
|-----|------|-------|
| Subsets | O(n × 2ⁿ) | O(n) |
| Permutations | O(n × n!) | O(n) |
| Combination Sum | O(2^target) | O(target) |

## 💡 Tips phỏng vấn

1. **Template**: Luôn theo mẫu Chọn → Đệ quy → Bỏ chọn
2. **Pruning**: Cắt tỉa sớm (vd: target < 0) để giảm thời gian
3. **Avoid duplicates**: Sort + skip `nums[i] == nums[i-1]`
4. **Deep copy**: `new ArrayList<>(current)` khi lưu kết quả — đừng lưu reference!
