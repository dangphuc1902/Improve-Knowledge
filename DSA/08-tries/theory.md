# 08 - Tries

## 📖 Tổng quan

**Trie** (Prefix Tree) là cấu trúc dữ liệu dạng cây dùng để lưu trữ và tìm kiếm **chuỗi** hiệu quả. Mỗi node đại diện cho 1 ký tự, đường từ root đến node tạo thành prefix.

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26]; // a-z
    boolean isEndOfWord = false;
}
```

## 🧠 Kiến thức cốt lõi

| Thao tác | Time | Space |
|----------|------|-------|
| Insert | O(m) | O(m) |
| Search | O(m) | O(1) |
| StartsWith | O(m) | O(1) |

> m = độ dài chuỗi

### So sánh với HashMap
| | HashMap | Trie |
|-|--------|------|
| Search exact | O(m) | O(m) |
| Prefix search | O(n×m) | O(m) |
| Space | O(n×m) | O(n×m) worst, thường ít hơn |
| Autocomplete | Không hỗ trợ | Tự nhiên |

## 🔍 Khi nào sử dụng?

- Tìm kiếm theo **prefix** (autocomplete, word suggestion)
- **Word search** trên board/grid
- Cần kiểm tra **prefix** của nhiều từ
- **Dictionary** với search, insert, và prefix operations
- Bài có nhiều string cần **so sánh prefix** chung

## 📝 Các Pattern phổ biến

### Pattern 1: Trie cơ bản
```java
class Trie {
    TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null)
                node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.isEndOfWord = true;
    }

    boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Insert n words | O(n × m) | O(n × m) |
| Search / prefix | O(m) | O(1) |
| Word Search II | O(m × n × 4^L) | O(total chars) |

## 💡 Tips phỏng vấn

1. **Array vs HashMap**: Dùng `TrieNode[26]` nếu chỉ lowercase letters (nhanh hơn)
2. **Prune**: Trong Word Search II, xóa word khỏi trie sau khi tìm được (tối ưu)
3. **isEndOfWord**: QUAN TRỌNG — phân biệt prefix vs complete word
4. **Memory**: Trie tốn memory, nhưng prefix search cực nhanh
