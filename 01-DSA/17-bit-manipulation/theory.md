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

### Pattern 1: XOR to Cancel Pairs
- **Nó là gì?**: Tận dụng tính chất của phép XOR: `a ^ a = 0` và `a ^ 0 = a`. Khi XOR tất cả các phần tử trong mảng, các cặp số giống nhau sẽ triệt tiêu lẫn nhau, chỉ còn lại số xuất hiện lẻ lần.
- **Giải quyết bài toán nào?**: 
    - Tìm số duy nhất xuất hiện một lần trong mảng các số xuất hiện hai lần (`Single Number`).
    - Tìm ký tự khác biệt giữa hai chuỗi.
- **Ưu điểm**:
    - Độ phức tạp thời gian O(n) và đặc biệt là O(1) space.
- **Nhược điểm**:
    - Chỉ áp dụng được khi các phần tử xuất hiện theo cặp (hoặc số chẵn lần).
- **Sự thay thế**:
    - **HashSet**: O(n) space.
    - **Sorting**: O(n log n) time.

```java
// Mọi phần tử xuất hiện 2 lần, 1 phần tử xuất hiện 1 lần
int result = 0;
for (int num : nums) result ^= num;
// result = phần tử duy nhất (các cặp tự triệt tiêu)
```

### Pattern 2: Brian Kernighan's Algorithm (Counting Set Bits)
- **Nó là gì?**: Phép toán `n & (n - 1)` sẽ xóa đi bit 1 cuối cùng bên phải của số `n`. Bằng cách đếm số lần thực hiện phép toán này cho đến khi `n = 0`, ta biết được số lượng bit 1.
- **Giải quyết bài toán nào?**: 
    - Đếm số lượng bit 1 trong một số nguyên (`Number of 1 Bits`).
    - Kiểm tra một số có phải là lũy thừa của 2 không (`Power of Two`).
- **Ưu điểm**:
    - Hiệu quả hơn việc dịch bit 32 lần vì số lần lặp chỉ bằng số lượng bit 1.
- **Nhược điểm**:
    - Không cung cấp thông tin về vị trí của các bit 1.
- **Sự thay thế**:
    - **Brute Force Shift**: Dịch bit 32 lần và kiểm tra `n & 1`.

```java
// Brian Kernighan: n & (n-1) xóa 1 bit mỗi lần
int count = 0;
while (n != 0) {
    n = n & (n - 1);
    count++;
}
```

### Pattern 3: Bit Masking & Shifting
- **Nó là gì?**: Sử dụng các phép dịch trái (`<<`), dịch phải (`>>`) và mặt nạ bit (`& 1`) để trích xuất, đảo ngược hoặc thay đổi các bit tại vị trí cụ thể.
- **Giải quyết bài toán nào?**: 
    - Đảo ngược các bit của một số 32-bit (`Reverse Bits`).
    - Lấy giá trị của bit tại vị trí thứ `i`.
- **Ưu điểm**:
    - Thao tác ở mức thấp nhất của máy tính, cực kỳ tối ưu về hiệu năng.
- **Nhược điểm**:
    - Khó đọc và dễ gây nhầm lẫn nếu không nắm vững hệ nhị phân.
- **Sự thay thế**:
    - Chuyển số thành chuỗi nhị phân (String) rồi xử lý (O(n) space và chậm hơn nhiều).

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
