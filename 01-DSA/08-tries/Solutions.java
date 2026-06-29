import java.util.*;

/**
 * 08 - Tries Solutions
 * Các bài: Implement Trie, Design Add and Search Words, Word Search II
 */
public class Solutions {

    // ============================================================
    // LC 208 - Implement Trie (Prefix Tree)
    // Array-based TrieNode — O(m) all ops ⭐
    // ============================================================
    static class Trie {
        private TrieNode root;

        static class TrieNode {
            TrieNode[] children = new TrieNode[26];
            boolean isEnd = false;
        }

        public Trie() {
            root = new TrieNode();
        }

        // Insert word — O(m)
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

        // Search exact word — O(m)
        public boolean search(String word) {
            TrieNode node = searchPrefix(word);
            return node != null && node.isEnd;
        }

        // Search prefix — O(m)
        public boolean startsWith(String prefix) {
            return searchPrefix(prefix) != null;
        }

        private TrieNode searchPrefix(String prefix) {
            TrieNode curr = root;
            for (char c : prefix.toCharArray()) {
                int idx = c - 'a';
                if (curr.children[idx] == null) return null;
                curr = curr.children[idx];
            }
            return curr;
        }
    }

    // ============================================================
    // LC 211 - Design Add and Search Words Data Structure
    // Wildcard '.' match any character — DFS on Trie ⭐
    // ============================================================
    static class WordDictionary {
        private TrieNode root = new TrieNode();

        static class TrieNode {
            TrieNode[] children = new TrieNode[26];
            boolean isEnd = false;
        }

        // Add word — O(m)
        public void addWord(String word) {
            TrieNode curr = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (curr.children[idx] == null) curr.children[idx] = new TrieNode();
                curr = curr.children[idx];
            }
            curr.isEnd = true;
        }

        // Search with wildcard '.' — O(m * 26^k) worst case
        public boolean search(String word) {
            return dfs(word, 0, root);
        }

        private boolean dfs(String word, int idx, TrieNode node) {
            if (idx == word.length()) return node.isEnd;

            char c = word.charAt(idx);
            if (c == '.') {
                // Try tất cả 26 children
                for (TrieNode child : node.children) {
                    if (child != null && dfs(word, idx + 1, child)) return true;
                }
                return false;
            } else {
                TrieNode child = node.children[c - 'a'];
                return child != null && dfs(word, idx + 1, child);
            }
        }
    }

    // ============================================================
    // LC 212 - Word Search II (Hard)
    // Approach 1: Trie + DFS on Board — O(W*m + R*C*4*3^(L-1)) ⭐
    // Approach 2: Brute Force DFS per word — O(W * R*C * 4*3^(L-1))
    // ============================================================
    static class WordSearchII {
        private static class TrieNode {
            TrieNode[] children = new TrieNode[26];
            String word = null; // lưu word thay vì rebuild path
        }

        // ⭐ Optimal: Build Trie from words, DFS on board
        public List<String> findWords(char[][] board, String[] words) {
            // Build Trie
            TrieNode root = new TrieNode();
            for (String word : words) {
                TrieNode curr = root;
                for (char c : word.toCharArray()) {
                    int idx = c - 'a';
                    if (curr.children[idx] == null) curr.children[idx] = new TrieNode();
                    curr = curr.children[idx];
                }
                curr.word = word; // lưu word tại node cuối
            }

            List<String> result = new ArrayList<>();
            int rows = board.length, cols = board[0].length;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    dfs(board, r, c, root, result);
                }
            }
            return result;
        }

        private void dfs(char[][] board, int r, int c, TrieNode node, List<String> result) {
            if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return;
            char curr = board[r][c];
            if (curr == '#' || node.children[curr - 'a'] == null) return;

            TrieNode next = node.children[curr - 'a'];

            // Found a word!
            if (next.word != null) {
                result.add(next.word);
                next.word = null; // avoid duplicate — prune!
            }

            // Mark visited
            board[r][c] = '#';
            // DFS 4 directions
            dfs(board, r + 1, c, next, result);
            dfs(board, r - 1, c, next, result);
            dfs(board, r, c + 1, next, result);
            dfs(board, r, c - 1, next, result);
            // Restore
            board[r][c] = curr;
        }
    }

    // ============================================================
    // Bonus: HashMap-based Trie — flexible alphabet
    // ============================================================
    static class TrieHashMap {
        private TrieNodeMap root = new TrieNodeMap();

        static class TrieNodeMap {
            Map<Character, TrieNodeMap> children = new HashMap<>();
            boolean isEnd = false;
        }

        public void insert(String word) {
            TrieNodeMap curr = root;
            for (char c : word.toCharArray()) {
                curr.children.putIfAbsent(c, new TrieNodeMap());
                curr = curr.children.get(c);
            }
            curr.isEnd = true;
        }

        public boolean search(String word) {
            TrieNodeMap node = find(word);
            return node != null && node.isEnd;
        }

        public boolean startsWith(String prefix) {
            return find(prefix) != null;
        }

        private TrieNodeMap find(String s) {
            TrieNodeMap curr = root;
            for (char c : s.toCharArray()) {
                if (!curr.children.containsKey(c)) return null;
                curr = curr.children.get(c);
            }
            return curr;
        }
    }
}
