# 17 - Bit Manipulation: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 5 bài toán thuộc chủ đề **Bit Manipulation (Thao tác bit)** từ LeetCode Master Tracker.

---

## 88. Single Number (LeetCode #136) - Easy

### 💡 Ý tưởng cốt lõi
Tìm số xuất hiện đúng 1 lần trong mảng số nguyên `nums` có các phần tử khác đều xuất hiện đúng 2 lần. Yêu cầu thời gian chạy $O(n)$ và bộ nhớ phụ $O(1)$.
Ý tưởng tối ưu là sử dụng phép toán **XOR (`^`)**. Các tính chất của phép XOR:
1. $x \oplus x = 0$ (XOR của một số với chính nó bằng 0).
2. $x \oplus 0 = x$ (XOR của một số với 0 bằng chính nó).
3. Phép XOR có tính chất giao hoán và kết hợp.
Do đó, khi ta XOR toàn bộ các phần tử trong mảng lại với nhau, các cặp số giống nhau sẽ tự triệt tiêu lẫn nhau về 0. Kết quả cuối cùng còn lại chính là số độc nhất xuất hiện lẻ lần (1 lần).

### 📊 Hướng tiếp cận tối ưu

#### Bitwise XOR (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$ chỉ dùng 1 biến tích lũy.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [4, 1, 2, 1, 2]`
* **Khởi tạo**: `res = 0`
* **Xử lý**:
  - `res = 0 ^ 4 = 4`
  - `res = 4 ^ 1 = 5`
  - `res = 5 ^ 2 = 7`
  - `res = 7 ^ 1 = 6`
  - `res = 6 ^ 2 = 4`
* **Giải thích**: Trình tự XOR tương đương:
  $$4 \oplus 1 \oplus 2 \oplus 1 \oplus 2 = 4 \oplus (1 \oplus 1) \oplus (2 \oplus 2) = 4 \oplus 0 \oplus 0 = 4$$
* **Kết quả**: `4`

### 💻 Java Clean Code
```java
public int singleNumber(int[] nums) {
    int res = 0;
    for (int num : nums) {
        res ^= num; // XOR tích lũy
    }
    return res;
}
```

---

## 89. Number of 1 Bits (LeetCode #191) - Easy

### 💡 Ý tưởng cốt lõi
Đếm số lượng bit có giá trị bằng `1` (còn gọi là trọng lượng Hamming) của một số nguyên không âm `n`.
Ý tưởng tối ưu là sử dụng thủ thuật bit:
$$n = n \& (n - 1)$$
Phép toán này có tác dụng **xóa bỏ bit 1 nằm ở vị trí thấp nhất (phải nhất)** của số $n$.
Ta lặp liên tục phép toán trên, mỗi lần lặp ta tăng biến đếm lên 1 cho đến khi $n$ trở về bằng 0. Số lần lặp chính là số lượng bit 1 của $n$. Thủ thuật này chạy nhanh hơn cách dịch bit thông thường vì nó chỉ chạy đúng bằng số lượng bit 1 thực tế có trong số.

### 📊 Hướng tiếp cận tối ưu

#### Brian Kernighan's Algorithm (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(k)$ với $k$ là số lượng bit 1. Trường hợp xấu nhất với số 32-bit là 32 bước lặp, trung bình nhanh hơn nhiều so với dịch bit 32 lần.
  * **Space Complexity**: $O(1)$.

### 💻 Java Clean Code
```java
public int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n = n & (n - 1); // Xóa bit 1 ở vị trí thấp nhất
        count++;
    }
    return count;
}
```

---

## 90. Counting Bits (LeetCode #338) - Easy

### 💡 Ý tưởng cốt lõi
Cho số nguyên `n`, trả về một mảng `ans` có độ dài `n + 1` sao cho `ans[i]` là số lượng bit 1 trong biểu diễn nhị phân của `i`. Yêu cầu thời gian chạy tuyến tính $O(n)$ trong 1 lần duyệt.
Đây là bài toán quy hoạch động trên bit. Số lượng bit 1 của số `i` có mối liên hệ mật thiết với số `i >> 1` (số `i` sau khi dịch phải 1 bit, tức là bỏ đi bit cuối cùng):
$$\text{ans}[i] = \text{ans}[i \gg 1] + (i \& 1)$$
* `i >> 1` chính là phần bit phía trước đã được tính toán từ trước.
* `i & 1` là giá trị của bit cuối cùng (bằng 1 nếu lẻ, 0 nếu chẵn).

### 📊 Hướng tiếp cận tối ưu

#### DP Bit Manipulation (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ đi qua các số từ 1 đến $n$ đúng 1 lần, mỗi bước mất $O(1)$.
  * **Space Complexity**: $O(1)$ nếu không tính mảng kết quả đầu ra.

### 💻 Java Clean Code
```java
public int[] countBits(int n) {
    int[] ans = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        // ans[i] = số bit 1 của i/2 + bit cuối cùng của i
        ans[i] = ans[i >> 1] + (i & 1);
    }
    return ans;
}
```

---

## 91. Reverse Bits (LeetCode #190) - Easy

### 💡 Ý tưởng cốt lõi
Đảo ngược thứ tự các bit của một số nguyên không dấu 32-bit.
Ta lặp qua 32 vị trí bit từ phải sang trái:
1. Tạo biến kết quả `res = 0`.
2. Trong mỗi bước lặp từ 0 đến 31:
   * Dịch `res` sang trái 1 bit để dành chỗ cho bit mới: `res <<= 1`.
   * Trích xuất bit cuối cùng của `n`: `n & 1`.
   * Gán bit này vào vị trí cuối của `res` bằng phép OR: `res |= (n & 1)`.
   * Dịch phải `n` để chuẩn bị cho lượt tiếp theo: `n >>>= 1` (sử dụng dịch phải không dấu `>>>` trong Java để điền bit 0 vào vị trí trống bên trái).

### 📊 Hướng tiếp cận tối ưu

#### Bitwise Shift & OR (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(1)$ vì số lượng vòng lặp luôn cố định là 32 bước đối với số nguyên 32-bit.
  * **Space Complexity**: $O(1)$.

### 💻 Java Clean Code
```java
public int reverseBits(int n) {
    int res = 0;
    for (int i = 0; i < 32; i++) {
        res <<= 1;          // Dịch trái res để lấy chỗ trống ở bit cuối
        res |= (n & 1);     // Đọc bit cuối của n và đưa vào res
        n >>>= 1;           // Dịch phải không dấu n
    }
    return res;
}
```

---

## 92. Missing Number (LeetCode #268) - Easy

### 💡 Ý tưởng cốt lõi
Cho mảng `nums` chứa $n$ số nguyên độc nhất thuộc khoảng từ `0` đến `n`. Hãy tìm số còn thiếu duy nhất trong khoảng đó.
Có hai hướng tiếp cận tối ưu trong $O(n)$ time và $O(1)$ space:
* **Cách 1 (Toán học)**: Tính tổng tất cả các số từ `0` đến `n` theo công thức:
  $$\text{Expected Sum} = \frac{n \times (n + 1)}{2}$$
  Số còn thiếu chính bằng `Expected Sum` trừ đi tổng thực tế của các phần tử trong mảng.
* **Cách 2 (Bitwise XOR)**: Tương tự bài Single Number. Ta thực hiện XOR tất cả các phần tử trong mảng kết hợp XOR với các chỉ số từ `0` đến `n`. Vì các số có sẵn sẽ bắt cặp với chỉ số của chúng và triệt tiêu về 0, kết quả còn lại duy nhất chính là số bị thiếu.

### 📊 Các hướng tiếp cận

#### Cách 1: Math Sum (Optimal) ⭐
* **Mô tả**: Sử dụng công thức Gauss tính tổng nhanh.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ tính tổng mảng.
  * **Space Complexity**: $O(1)$.

#### Cách 2: XOR (Optimal) ⭐
* **Mô tả**: XOR chỉ số và giá trị phần tử. Tránh được nguy cơ tràn số (overflow) nếu $n$ cực kỳ lớn so với cách tính tổng.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$.
  * **Space Complexity**: $O(1)$.

### 💻 Java Clean Code
```java
// Cách 1: Sử dụng Toán học (Math Sum)
public int missingNumber(int[] nums) {
    int n = nums.length;
    int expectedSum = n * (n + 1) / 2;
    int actualSum = 0;
    for (int num : nums) {
        actualSum += num;
    }
    return expectedSum - actualSum;
}

// Cách 2: Sử dụng XOR
public int missingNumberXOR(int[] nums) {
    int res = nums.length;
    for (int i = 0; i < nums.length; i++) {
        res ^= i ^ nums[i]; // XOR chỉ số và giá trị
    }
    return res;
}
```
