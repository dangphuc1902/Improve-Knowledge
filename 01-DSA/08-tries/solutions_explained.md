# 08 - Tries: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 2 bài toán thuộc chủ đề **Tries (Prefix Tree)** từ LeetCode Master Tracker.

---

## 43. Implement Trie - Prefix Tree (LeetCode #208) - Medium

### 💡 Ý tưởng cốt lõi
Thiết kế cấu trúc dữ liệu Trie (Cây tiền tố) hỗ trợ các thao tác `insert`, `search`, và `startsWith` (tìm tiền tố).
Cấu trúc Trie bao gồm các node. Mỗi node `TrieNode` chứa:
* Một mảng các node con đại diện cho bảng chữ cái: `TrieNode[] children = new TrieNode[26]` (cho các chữ cái thường `'a'` đến `'z'`).
* Một biến boolean `isEnd` để đánh dấu từ kết thúc tại node này.
Khi thực hiện:
* **Insert**: Duyệt qua từng ký tự của từ, nếu node con tương ứng với ký tự đó chưa tồn tại, ta tạo mới. Dịch chuyển xuống node con. Khi hết từ, đánh dấu `isEnd = true`.
* **Search**: Duyệt qua các ký tự, nếu gặp node con là `null`, trả về `false`. Hết từ, trả về giá trị của `isEnd`.
* **StartsWith**: Tương tự như `Search`, nhưng khi duyệt hết tiền tố, trả về `true` ngay lập tức mà không cần kiểm tra `isEnd`.

### 📊 Hướng tiếp cận tối ưu

#### Array-based TrieNode (Optimal) ⭐
* **Mô tả**: Sử dụng mảng kích thước 26 cho liên kết con giúp truy cập nhanh trong thời gian $O(1)$.
* **Độ phức tạp**:
  * **Time Complexity**:
    * `insert`: $O(L)$ với $L$ là độ dài từ.
    * `search`: $O(L)$ với $L$ là độ dài từ.
    * `startsWith`: $O(P)$ với $P$ là độ dài tiền tố.
  * **Space Complexity**: $O(N \cdot \Sigma)$ với $N$ là tổng số ký tự của tất cả các từ được chèn và $\Sigma = 26$ là kích thước bảng chữ cái.

### 🔄 Dry Run với ví dụ
* **Thao tác**:
  1. `insert("apple")`: Tạo nhánh `a -> p -> p -> l -> e`. Tại `e`, đặt `isEnd = true`.
  2. `search("apple")`: Đi theo nhánh `a -> p -> p -> l -> e`. Thấy `isEnd = true` → trả về `true`.
  3. `search("app")`: Đi theo nhánh `a -> p -> p`. Đi tới cuối từ nhưng `isEnd = false` → trả về `false`.
  4. `startsWith("app")`: Đi theo nhánh `a -> p -> p`. Nhánh hợp lệ → trả về `true`.

### 💻 Java Clean Code
```java
class Trie {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }
    
    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }
    
    public boolean search(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) {
                return false;
            }
            curr = curr.children[idx];
        }
        return curr.isEnd;
    }
    
    public boolean startsWith(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) {
                return false;
            }
            curr = curr.children[idx];
        }
        return true;
    }
}
```

---

## 44. Word Search II (LeetCode #212) - Hard

### 💡 Ý tưởng cốt lõi
Tìm tất cả các từ trong danh sách `words` xuất hiện trên bảng ký tự 2D `board`. Các chữ cái trong một từ phải là các ô kề nhau (ngang, dọc) và không được dùng lại một ô ký tự nhiều lần cho cùng một từ.
Nếu ta duyệt DFS quay lui (backtracking) cho từng từ riêng biệt, độ phức tạp sẽ rất lớn và bị TLE (Time Limit Exceeded).
Giải pháp tối ưu là **đưa toàn bộ danh sách `words` vào một cây Trie**, sau đó thực hiện **DFS trên bảng 2D kết hợp duyệt đồng thời trên cây Trie**:
* Khi duyệt DFS từ ô `(r, c)`, ta xem ký tự `board[r][c]` có tồn tại trong các node con của node Trie hiện tại không.
* Nếu có, ta đi tiếp sang ô kề cạnh và dịch chuyển xuống node con của Trie.
* Khi chạm tới node có đánh dấu kết thúc từ, ta thêm từ đó vào kết quả và xóa từ đó khỏi Trie (hoặc đánh dấu rỗng) để tránh trùng lặp.
* Để tối ưu hiệu năng (pruning), ta có thể xóa các node lá của Trie khi một từ đã được tìm thấy (Trie pruning).

### 📊 Hướng tiếp cận tối ưu

#### Trie + Grid DFS Backtracking with Pruning (Optimal) ⭐
* **Mô tả**: Tích hợp cây Trie vào quá trình DFS trên bảng chữ cái 2D.
* **Độ phức tạp**:
  * **Time Complexity**: $O(m \cdot n \cdot 4 \cdot 3^{L-1})$ với $m \times n$ là kích thước bảng và $L$ là độ dài lớn nhất của một từ. Cây Trie giúp lọc nhánh sai sớm nhất có thể.
  * **Space Complexity**: $O(W \cdot L)$ với $W$ là số từ và $L$ là độ dài trung bình của từ (bộ nhớ cho Trie).

### 💻 Java Clean Code
```java
public class WordSearchII {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null; // Lưu trữ từ trực tiếp tại node kết thúc để lấy ra nhanh
    }

    public List<String> findWords(char[][] board, String[] words) {
        List<String> res = new ArrayList<>();
        TrieNode root = buildTrie(words);
        
        int m = board.length;
        int n = board[0].length;
        
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                dfs(board, r, c, root, res);
            }
        }
        
        return res;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node, List<String> res) {
        char ch = board[r][c];
        if (ch == '#' || node.children[ch - 'a'] == null) {
            return;
        }
        
        node = node.children[ch - 'a'];
        if (node.word != null) {
            res.add(node.word);
            node.word = null; // Tránh trùng lặp từ trong kết quả
        }
        
        board[r][c] = '#'; // Đánh dấu ô đã đi qua
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length) {
                dfs(board, nr, nc, node, res);
            }
        }
        
        board[r][c] = ch; // Khôi phục lại ô khi backtrack
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode curr = root;
            for (char c : w.toCharArray()) {
                int idx = c - 'a';
                if (curr.children[idx] == null) {
                    curr.children[idx] = new TrieNode();
                }
                curr = curr.children[idx];
            }
            curr.word = w; // lưu từ ở node cuối
        }
        return root;
    }
}
```
