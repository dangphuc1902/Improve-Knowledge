import java.util.*;

/**
 * =============================================
 *  08 - TRIES
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Implement Trie (LeetCode #208) - Medium
// -----------------------------------------------
// Implement Trie với 3 methods: insert, search, startsWith.
//
// Approach: Dùng mảng children[26] cho mỗi node (lowercase letters).
// Mỗi ký tự tương ứng 1 edge trong trie.
//
// Time: O(m) cho mỗi operation - m là độ dài word
// Space: O(n × m) tổng - n words, trung bình dài m
class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;

    public TrieNode() {
        children = new TrieNode[26];
        isEndOfWord = false;
    }
}

class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Thêm word vào trie
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            // Tạo node mới nếu chưa tồn tại
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isEndOfWord = true; // Đánh dấu kết thúc word
    }

    // Tìm word chính xác trong trie
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    // Kiểm tra có word nào bắt đầu bằng prefix không
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    // Helper: tìm node tương ứng với chuỗi s
    private TrieNode findNode(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                return null; // Prefix không tồn tại
            }
            node = node.children[index];
        }
        return node;
    }
}

// -----------------------------------------------
// Bài 2: Word Search II (LeetCode #212) - Hard
// -----------------------------------------------
// Cho board m×n ký tự và danh sách words, tìm tất cả words xuất hiện trên board.
// Có thể đi 4 hướng (lên/xuống/trái/phải), mỗi ô dùng 1 lần.
//
// Approach: Trie + DFS Backtracking.
// 1. Build trie từ danh sách words
// 2. DFS từ mỗi ô trên board, theo các edge trong trie
// 3. Khi đến node có isEndOfWord → thêm vào kết quả
//
// Time: O(m × n × 4^L) - L là max word length
// Space: O(total characters in words)
class WordSearch2 {
    private int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();

        // Bước 1: Build Trie
        TrieNode root = new TrieNode();
        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null)
                    node.children[idx] = new TrieNode();
                node = node.children[idx];
            }
            node.isEndOfWord = true;
        }

        // Bước 2: DFS từ mỗi ô
        int rows = board.length, cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = board[r][c] - 'a';
                if (root.children[idx] != null) {
                    dfs(board, r, c, root, new StringBuilder(), result);
                }
            }
        }

        return result;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node,
                     StringBuilder path, List<String> result) {
        // Boundary và visited check
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length
            || board[r][c] == '#') return;

        char ch = board[r][c];
        int idx = ch - 'a';
        if (node.children[idx] == null) return; // Không có trong trie

        // Di chuyển trong trie
        node = node.children[idx];
        path.append(ch);

        // Tìm thấy word
        if (node.isEndOfWord) {
            result.add(path.toString());
            node.isEndOfWord = false; // Tránh duplicate
        }

        // Mark visited và DFS 4 hướng
        board[r][c] = '#'; // Mark
        for (int[] dir : dirs) {
            dfs(board, r + dir[0], c + dir[1], node, path, result);
        }
        board[r][c] = ch;  // Unmark (backtrack)

        path.deleteCharAt(path.length() - 1); // Backtrack path
    }
}
