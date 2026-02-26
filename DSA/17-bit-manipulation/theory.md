# 17 - Bit Manipulation

## 📖 Tổng quan

**Bit Manipulation** thao tác trực tiếp trên **biểu diễn nhị phân** của số. Cực kỳ nhanh (O(1) per operation) và tiết kiệm memory.

## 🧠 Kiến thức cốt lõi

### Các phép toán cơ bản
| Operator | Ý nghĩa | Ví dụ |
|----------|---------|-------|
| `&` (AND) | Cả 2 bit = 1 → 1 | `5 & 3 = 1` (101 & 011 = 001) |
| `\|` (OR) | Ít nhất 1 bit = 1 → 1 | `5 \| 3 = 7` (101 \| 011 = 111) |
| `^` (XOR) | Khác nhau → 1 | `5 ^ 3 = 6` (101 ^ 011 = 110) |
| `~` (NOT) | Đảo bit | `~5 = -6` |
| `<<` (Left shift) | Nhân 2 | `3 << 1 = 6` |
| `>>` (Right shift) | Chia 2 | `6 >> 1 = 3` |

### Tricks quan trọng
```java
n & (n - 1)    // Xóa bit 1 cuối cùng (rightmost set bit)
n & (-n)       // Lấy bit 1 cuối cùng
n & 1          // Kiểm tra chẵn/lẻ
a ^ a = 0      // XOR với chính nó = 0
a ^ 0 = a      // XOR với 0 = chính nó
```

## 🔍 Khi nào sử dụng?

- Bài yêu cầu **O(1) space** trên mảng có duplicate
- **Single number** (tìm phần tử xuất hiện 1 lần)
- **Counting bits**, **reverse bits**
- Bài liên quan đến **power of 2**
- Cần thao tác **set/clear/toggle** bit cụ thể

## 📝 Các Pattern phổ biến

### Pattern 1: XOR để cancel pairs
```java
// Mọi phần tử xuất hiện 2 lần, 1 phần tử xuất hiện 1 lần
int result = 0;
for (int num : nums) result ^= num;
// result = phần tử duy nhất (pairs tự cancel)
```

### Pattern 2: Count set bits
```java
// Brian Kernighan: n & (n-1) xóa 1 bit mỗi lần
int count = 0;
while (n != 0) {
    n = n & (n - 1);
    count++;
}
```

### Pattern 3: Reverse bits
```java
int result = 0;
for (int i = 0; i < 32; i++) {
    result = (result << 1) | (n & 1);
    n >>= 1;
}
```

## ⏱️ Complexity thường gặp

| Bài | Time | Space |
|-----|------|-------|
| Single Number | O(n) | O(1) |
| Counting Bits | O(n) | O(n) |
| Reverse Bits | O(1) (32 bits) | O(1) |

## 💡 Tips phỏng vấn

1. **XOR**: a^a=0, a^0=a — key cho Single Number
2. **n & (n-1)**: Xóa rightmost set bit — key cho Counting Bits, Power of Two
3. **Java cụ thể**: `Integer.bitCount()`, `Integer.reverse()` có sẵn nhưng nên biết cách tự implement
4. **Signed integers**: Java dùng `>>>` cho unsigned right shift
