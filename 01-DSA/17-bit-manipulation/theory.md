# 17 - Bit Manipulation

## 📖 Tổng quan

**Bit Manipulation** là kỹ thuật làm việc trực tiếp với các bit trong số nguyên. Thường cho phép giải các bài toán với **O(1) space** và **O(n) time** hoặc **O(1) time** — nhanh hơn nhiều so với các cách thông thường.

> **Ý tưởng cốt lõi:** Mọi số nguyên đều là dãy bit. Hiểu các **bitwise operators** và dùng chúng để thao tác trực tiếp.

## 🧠 Kiến thức cốt lõi

### Bitwise Operators

| Operator | Symbol | Mô tả | Ví dụ |
|----------|--------|-------|-------|
| AND | `&` | 1 nếu cả 2 đều 1 | `5 & 3 = 1` (101 & 011 = 001) |
| OR | `\|` | 1 nếu có ít nhất 1 là 1 | `5 \| 3 = 7` (101 \| 011 = 111) |
| XOR | `^` | 1 nếu 2 khác nhau | `5 ^ 3 = 6` (101 ^ 011 = 110) |
| NOT | `~` | Đảo tất cả bit | `~5 = -6` |
| Left Shift | `<<` | Nhân 2 | `5 << 1 = 10` |
| Right Shift | `>>` | Chia 2 | `5 >> 1 = 2` |
| Unsigned RS | `>>>` | Chia 2 (fill 0) | `-5 >>> 1 = 2147483645` |

### Các Trick phổ biến

```java
n & (n-1)    // bỏ lowest set bit (check power of 2: n & (n-1) == 0)
n & (-n)     // lấy lowest set bit
n ^ n        // = 0 (XOR với chính nó)
n ^ 0        // = n (XOR với 0)
a ^ b ^ b    // = a (XOR 2 lần bằng nhau = 0)
1 << k       // = 2^k
n >> k       // = n / 2^k
n | (1 << k) // set bit k của n
n & ~(1<<k)  // clear bit k của n
(n >> k) & 1 // get bit k của n
```

## 🔍 Khi nào sử dụng?

- Bài toán yêu cầu **O(1) space** nhưng liên quan đến số xuất hiện lẻ/chẵn lần
- **Power of 2** checks
- **Count bits** (Hamming weight)
- **Subset generation** (bitmask)
- Tìm phần tử **missing** hoặc **duplicate** trong mảng
- Cụm từ: *"single number"*, *"bit"*, *"missing number"*, *"XOR"*

## 📝 Các Pattern phổ biến

### Pattern 1: XOR để tìm phần tử duy nhất
- **Nó là gì?**: `a ^ a = 0` và `a ^ 0 = a`. XOR tất cả phần tử → các cặp triệt tiêu → còn lại phần tử không có cặp.
- **Giải quyết bài toán nào?**: Single Number (LC 136), Missing Number, Find the Duplicate.
- **Ưu điểm**: O(n) time, O(1) space.
- **Sự thay thế**: HashMap để đếm frequency (O(n) space).

```java
// Single Number: tìm phần tử xuất hiện 1 lần
int result = 0;
for (int num : nums) result ^= num;
return result;

// Missing Number: tìm số thiếu trong [0, n]
// XOR với expected XOR actual
int missing = n; // bắt đầu với n
for (int i = 0; i < n; i++) missing ^= i ^ nums[i];
return missing;
```

### Pattern 2: Count Set Bits (Hamming Weight)
- **Nó là gì?**: Đếm số bit 1 trong số nguyên.
- **Giải quyết bài toán nào?**: Number of 1 Bits (LC 191), Counting Bits (LC 338), Hamming Distance.

```java
// Approach 1: n & (n-1) trick — O(number of set bits)
public int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1); // bỏ lowest set bit
        count++;
    }
    return count;
}

// Approach 2: Built-in Java
Integer.bitCount(n); // O(1)

// Counting Bits DP — dp[i] = number of 1 bits in i
int[] dp = new int[n + 1];
for (int i = 1; i <= n; i++) {
    dp[i] = dp[i >> 1] + (i & 1); // dp[i/2] + last bit
}
```

### Pattern 3: Power of 2 / Power of 4
- **Nó là gì?**: Số là lũy thừa của 2 nếu chỉ có đúng 1 bit được set.
- **Điều kiện**: `n > 0 && (n & (n-1)) == 0`.
- **Power of 4**: Thêm điều kiện bit được set ở vị trí chẵn: `(n & 0x55555555) != 0`.

```java
// Power of 2
boolean isPowerOfTwo(int n) { return n > 0 && (n & (n - 1)) == 0; }

// Power of 4: bit phải ở vị trí chẵn (1, 4, 16, 64, ...)
// 0x55555555 = 01010101...01 (bit tại các vị trí chẵn)
boolean isPowerOfFour(int n) {
    return n > 0 && (n & (n - 1)) == 0 && (n & 0x55555555) != 0;
}
```

### Pattern 4: Reverse Bits
- **Nó là gì?**: Đảo ngược 32 bits của số nguyên.
- **Giải quyết bài toán nào?**: Reverse Bits (LC 190).

```java
public int reverseBits(int n) {
    int result = 0;
    for (int i = 0; i < 32; i++) {
        result = (result << 1) | (n & 1); // shift result left, add LSB of n
        n >>= 1; // shift n right
    }
    return result;
}
```

### Pattern 5: Sum of Two Integers Without +
- **Nó là gì?**: XOR = phép cộng không carry. AND << 1 = carry.
- **Giải quyết bài toán nào?**: Sum of Two Integers (LC 371).

```java
public int getSum(int a, int b) {
    while (b != 0) {
        int carry = (a & b) << 1; // carry
        a = a ^ b;  // sum without carry
        b = carry;  // apply carry
    }
    return a;
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Single Number — XOR Dry Run

```
nums = [4, 1, 2, 1, 2]

result = 0

XOR 4: result = 0 ^ 4 = 4 (0100)
XOR 1: result = 4 ^ 1 = 5 (0101)
XOR 2: result = 5 ^ 2 = 7 (0111)
XOR 1: result = 7 ^ 1 = 6 (0110)  (1 ^ 1 = 0)
XOR 2: result = 6 ^ 2 = 4 (0100)  (2 ^ 2 = 0, khử nhau)

✅ Output: 4
```

### Ví dụ 2: Count Bits — Dry Run

```
n = 5 (101)

n = 5:  n & (n-1) = 101 & 100 = 100 = 4. count=1
n = 4:  n & (n-1) = 100 & 011 = 000 = 0. count=2
n = 0:  exit loop

✅ Output: 2 (5 = 101 có 2 bit 1)
```

### Ví dụ 3: Counting Bits DP

```
n = 5 → dp = [0, 1, 1, 2, 1, 2]

dp[0] = 0 (base)
dp[1] = dp[0] + (1 & 1) = 0 + 1 = 1 (01 → 1 bit)
dp[2] = dp[1] + (2 & 1) = 1 + 0 = 1 (10 → 1 bit)
dp[3] = dp[1] + (3 & 1) = 1 + 1 = 2 (11 → 2 bits)
dp[4] = dp[2] + (4 & 1) = 1 + 0 = 1 (100 → 1 bit)
dp[5] = dp[2] + (5 & 1) = 1 + 1 = 2 (101 → 2 bits)

Tại sao dp[i] = dp[i>>1] + (i&1)?
  i>>1 = i/2 (drop LSB) → đã tính → dp[i>>1]
  (i&1) = LSB của i (0 hoặc 1)
```

## 🔄 So sánh các Approach

### Count Set Bits: n&(n-1) vs Brian Kernighan vs Built-in

| Approach | Time | Space |
|----------|------|-------|
| **n & (n-1) ⭐** | O(k) k=set bits | O(1) |
| Brian Kernighan | O(k) | O(1) |
| `Integer.bitCount()` | O(1) | O(1) |

### Single Number: XOR vs HashMap vs Sort

| Approach | Time | Space |
|----------|------|-------|
| **XOR ⭐** | O(n) | O(1) |
| HashMap | O(n) | O(n) |
| Sort | O(n log n) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// XOR:
// 1. Empty array → 0 (result initialized to 0)
// 2. All same → 0 (if even count)

// Power of 2:
// 1. n = 0 → false (không phải lũy thừa của 2)
// 2. n < 0 → false
// 3. n = 1 = 2^0 → true

// Reverse Bits:
// 1. Phải dùng >>> (unsigned shift) khi in Java với số âm
// 2. n = 0 → return 0

// Sum Without +:
// 1. Overflow: dùng long nếu cần
// 2. Negative numbers: XOR trick vẫn work (two's complement)
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Single Number | O(n) | O(1) |
| Number of 1 Bits | O(1) | O(1) |
| Counting Bits | O(n) | O(n) |
| Reverse Bits | O(1) | O(1) |
| Missing Number | O(n) | O(1) |
| Sum of Two Integers | O(1) | O(1) |

## 💡 Tips phỏng vấn

1. **XOR magic**: `a ^ a = 0`, `a ^ 0 = a` — học thuộc, dùng nhiều.
2. **Lowest set bit**: `n & (-n)` hoặc `n & (n-1)` để bỏ → phổ biến trong Fenwick Tree.
3. **Check bit k**: `(n >> k) & 1` → 0 hoặc 1.
4. **Set/Clear bit**: Set: `n | (1 << k)`. Clear: `n & ~(1 << k)`.
5. **Java `>>>` vs `>>`**: `>>` preserves sign bit, `>>>` fills with 0 — dùng `>>>` khi xử lý số dương.
