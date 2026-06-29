# 08 - Tries

## 📖 Tổng quan

**Trie** (hay Prefix Tree) là cây với mỗi node đại diện cho một **ký tự**. Cho phép tìm kiếm theo prefix trong **O(m)** (m = độ dài từ), bất kể số lượng từ trong tập.

> **Ý tưởng cốt lõi:** Trie là cây nhiều nhánh (26 con cho alphabet), nơi mỗi đường đi từ root đến node `isEnd=true` là một từ hoàn chỉnh.

## 🧠 Kiến thức cốt lõi

### TrieNode Structure

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26]; // 26 chữ cái
    boolean isEnd = false; // đánh dấu kết thúc từ
}
```

### Operations Complexity

| Thao tác | Time | Space |
|----------|------|-------|
| Insert | O(m) | O(m) worst case |
| Search (exact) | O(m) | O(1) |
| StartsWith (prefix) | O(m) | O(1) |
| Delete | O(m) | O(1) |

> m = độ dài từ. **Trie luôn O(m)** — không phụ thuộc số lượng từ n.

### Khi nào Trie tốt hơn HashMap?

| Tiêu chí | Trie | HashMap |
|----------|------|---------|
| Tìm prefix | O(m) | O(n*m) |
| Exact search | O(m) | O(1) average |
| Auto-complete | Tốt | Kém |
| Space | O(ALPHABET * m * n) | O(n*m) |

## 🔍 Khi nào sử dụng?

- **Prefix matching**: Auto-complete, search suggestions
- **Word dictionary**: Spell checker
- **IP routing**: Longest prefix match
- **Word game**: Boggle, Word Search II
- Khi thấy pattern: *"search by prefix"*, *"autocomplete"*, *"dictionary"*

## 📝 Các Pattern phổ biến

### Pattern 1: Standard Trie Implementation
- **Nó là gì?**: TrieNode với mảng 26 con + boolean `isEnd`. Insert/Search/StartsWith.
- **Giải quyết bài toán nào?**: Implement Trie (LC 208), Design Add and Search Words.
- **Ưu điểm**: O(m) mọi operation, prefix search rất mạnh.
- **Nhược điểm**: Space O(ALPHABET_SIZE × max_word_length × n) — lớn hơn HashMap.
- **Sự thay thế**: HashMap<String, Boolean> cho exact match (O(1) avg nhưng không prefix).

```java
class Trie {
    private TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) curr.children[idx] = new TrieNode();
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEnd;
    }

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
```

### Pattern 2: Wildcard Search (DFS on Trie)
- **Nó là gì?**: Ký tự `.` match bất kỳ ký tự nào → cần DFS/BFS qua tất cả children.
- **Giải quyết bài toán nào?**: Design Add and Search Words (LC 211), Word Search II.
- **Ưu điểm**: Kết hợp Trie + DFS để handle wildcard.
- **Nhược điểm**: Worst case O(m * 26^k) với k wildcard.

```java
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
```

### Pattern 3: HashMap-based Trie (Flexible Alphabet)
- **Nó là gì?**: Dùng `HashMap<Character, TrieNode>` thay vì array[26] — hỗ trợ alphabet bất kỳ.
- **Giải quyết bài toán nào?**: Trie với Unicode, IP routing, general purpose.
- **Ưu điểm**: Linh hoạt, tiết kiệm space nếu sparse.
- **Nhược điểm**: Chậm hơn array[26] do HashMap overhead.

```java
class TrieNodeMap {
    Map<Character, TrieNodeMap> children = new HashMap<>();
    boolean isEnd = false;
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Trie Insert & Search — Dry Run

```
Insert: "apple"
root → a → p → p → l → e (isEnd=true)

Insert: "app"
root → a → p → p (isEnd=true) → l → e (isEnd=true)
(path đã có, chỉ đánh dấu isEnd=true tại node 'p' thứ 3)

Search "apple":
root →[a]→ [p]→ [p]→ [l]→ [e].isEnd=true ✅ → return true

Search "app":
root →[a]→ [p]→ [p].isEnd=true ✅ → return true

Search "ap":
root →[a]→ [p].isEnd=false ❌ → return false

StartsWith "app":
root →[a]→ [p]→ [p] != null ✅ → return true (không cần isEnd)
```

### Ví dụ 2: Word Search II (Trie + DFS on Grid)

```
board = [["o","a","a","n"],
         ["e","t","a","e"],
         ["i","h","k","r"],
         ["i","f","l","v"]]
words = ["oath","pea","eat","rain"]

Build Trie từ words → {o-a-t-h, p-e-a, e-a-t, r-a-i-n}

DFS từ mỗi ô trên board:
- Start (1,0)='e': Trie[e] exists
  - (0,0)='o': Không phải child của 'e' trong Trie
  - (1,1)='t': Trie[e][t] exists? No (chỉ có 'a')
  - (2,0)='i': No
- Start (0,1)='a': Trie[a] exists? Chỉ 'oath' bắt đầu bằng 'o', 'eat' bằng 'e'...
...

✅ Output: ["eat","oath"]
```

## 🔄 So sánh các Approach

### Search: Trie vs HashMap vs Brute Force

| Approach | Insert | Exact Search | Prefix Search |
|----------|--------|--------------|---------------|
| **Trie ⭐** | O(m) | O(m) | O(m) |
| HashMap | O(m) | O(1) avg | O(n*m) |
| Sorted List + BS | O(n log n) | O(log n) | O(log n + k) |

### TrieNode: Array[26] vs HashMap

| | Array[26] | HashMap |
|--|-----------|---------|
| Speed | Faster (O(1)) | Slightly slower |
| Space | O(26 * nodes) | O(actual children * nodes) |
| Best for | Known small alphabet | Variable/large alphabet |

## 🚨 Edge Cases cần chú ý

```java
// Trie:
// 1. Insert empty string "" → root.isEnd = true
// 2. Search prefix = full word → startsWith and search both true
// 3. Duplicate inserts → không sao, isEnd set lại = true
// 4. Case sensitivity → cần toLowerCase() nếu cần

// Word Search II:
// 1. Same word xuất hiện nhiều lần → dùng HashSet result
// 2. Prune đã tìm thấy → set node.isEnd = false sau khi add
// 3. Mark visited cell → thay board[i][j] bằng '#', restore sau DFS
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Implement Trie (insert n words) | O(n * m) | O(n * m * 26) |
| Search/StartsWith | O(m) | O(1) |
| Design Add and Search Words | O(m) search, O(m * 26^k) worst | O(n*m) |
| Word Search II | O(W*m + R*C*4^max_len) | O(n*m) |

## 💡 Tips phỏng vấn

1. **Trie vs HashMap**: Trie thắng khi cần prefix search, HashMap thắng khi chỉ cần exact match.
2. **`children[c - 'a']`**: Index trong array = ký tự - 'a' → chỉ work với lowercase alphabet.
3. **`isEnd` vs `word` field**: Đôi khi lưu cả `String word` trong node để không cần rebuild path.
4. **Space optimization**: Compressed Trie (Radix Tree) — merge nodes có 1 child.
5. **Word Search II trick**: Khi tìm thấy word, set `node.isEnd = false` để tránh duplicate trong result.
