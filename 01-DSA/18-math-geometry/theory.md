# 18 - Math & Geometry

## 📖 Tổng quan

**Math & Geometry** bao gồm các bài toán sử dụng kiến thức toán học (số học, số học modular, hình học) để giải một cách elegant và hiệu quả.

> **Ý tưởng cốt lõi:** Nhận diện cấu trúc toán học ẩn sau bài toán. Thường là O(n) hoặc O(sqrt(n)) thay vì brute force O(n²).

## 🧠 Kiến thức cốt lõi

### Toán học quan trọng

| Công thức/Định lý | Mô tả | Ứng dụng |
|-------------------|-------|---------|
| GCD (Euclid) | `gcd(a,b) = gcd(b, a%b)` | Fraction simplification |
| Prime Sieve | `isPrime[i*j] = false` | Count primes |
| Modular Arithmetic | `(a+b) % m = ((a%m) + (b%m)) % m` | Overflow prevention |
| Sqrt(n) trick | Loop đến sqrt(n) | Count divisors, factor |
| Bit counting | `n & (n-1)` | Power of 2 |

### Công thức thường dùng

```java
// GCD - Euclid's Algorithm
int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }

// LCM
int lcm(int a, int b) { return a / gcd(a, b) * b; }

// Is Prime
boolean isPrime(int n) {
    if (n < 2) return false;
    for (int i = 2; i * i <= n; i++) {
        if (n % i == 0) return false;
    }
    return true;
}

// Power with modular — O(log p)
long powMod(long base, long exp, long mod) {
    long result = 1;
    base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = result * base % mod;
        base = base * base % mod;
        exp >>= 1;
    }
    return result;
}
```

## 🔍 Khi nào sử dụng?

- Bài toán liên quan **số nguyên tố**, **chia hết**, **GCD/LCM**
- **Matrix rotation/spiral traversal**
- **Happy number** (cycle detection trong số học)
- **Palindrome** với số nguyên (reverse digits)
- Cụm từ: *"rotate matrix"*, *"spiral"*, *"prime"*, *"factorial"*, *"power"*

## 📝 Các Pattern phổ biến

### Pattern 1: Matrix Rotation (In-place)
- **Nó là gì?**: Rotate 90° clockwise = Transpose + Reverse each row.
- **Giải quyết bài toán nào?**: Rotate Image (LC 48), Set Matrix Zeroes (LC 73).
- **Ưu điểm**: O(1) extra space.

```java
// Rotate 90° clockwise (in-place):
// Step 1: Transpose (swap matrix[i][j] và matrix[j][i])
for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        int temp = matrix[i][j];
        matrix[i][j] = matrix[j][i];
        matrix[j][i] = temp;
    }
}
// Step 2: Reverse each row
for (int i = 0; i < n; i++) {
    int left = 0, right = n - 1;
    while (left < right) {
        int temp = matrix[i][left];
        matrix[i][left++] = matrix[i][right];
        matrix[i][right--] = temp;
    }
}
```

### Pattern 2: Spiral Matrix
- **Nó là gì?**: Dùng 4 boundaries (top, bottom, left, right) và thu hẹp dần.
- **Giải quyết bài toán nào?**: Spiral Matrix I & II (LC 54, 59).

```java
// Spiral Matrix — 4 boundaries
int top = 0, bottom = m - 1, left = 0, right = n - 1;
while (top <= bottom && left <= right) {
    for (int i = left; i <= right; i++) result.add(matrix[top][i]); top++;    // →
    for (int i = top; i <= bottom; i++) result.add(matrix[i][right]); right--; // ↓
    if (top <= bottom) {
        for (int i = right; i >= left; i--) result.add(matrix[bottom][i]); bottom--; // ←
    }
    if (left <= right) {
        for (int i = bottom; i >= top; i--) result.add(matrix[i][left]); left++;     // ↑
    }
}
```

### Pattern 3: Happy Number (Cycle Detection)
- **Nó là gì?**: Tính tổng bình phương các chữ số. Lặp cho đến khi = 1 (happy) hoặc cycle.
- **Giải quyết bài toán nào?**: Happy Number (LC 202).
- **Detect cycle**: Floyd's hoặc HashSet.

```java
// Floyd's cycle detection trong số học
public boolean isHappy(int n) {
    int slow = n, fast = getSquareSum(n);
    while (fast != 1 && slow != fast) {
        slow = getSquareSum(slow);
        fast = getSquareSum(getSquareSum(fast));
    }
    return fast == 1;
}

private int getSquareSum(int n) {
    int sum = 0;
    while (n > 0) { int d = n % 10; sum += d * d; n /= 10; }
    return sum;
}
```

### Pattern 4: Count Primes (Sieve of Eratosthenes)
- **Nó là gì?**: Đánh dấu tất cả bội số của số nguyên tố là không phải prime.
- **Giải quyết bài toán nào?**: Count Primes (LC 204).
- **Time**: O(n log log n), **Space**: O(n).

```java
public int countPrimes(int n) {
    boolean[] isComposite = new boolean[n]; // false = prime
    int count = 0;
    for (int i = 2; i < n; i++) {
        if (!isComposite[i]) {
            count++;
            // Mark multiples starting from i*i (smaller already marked)
            for (long j = (long) i * i; j < n; j += i) {
                isComposite[(int) j] = true;
            }
        }
    }
    return count;
}
```

### Pattern 5: Set Matrix Zeroes
- **Nó là gì?**: Dùng first row/col làm "marker" để tránh dùng extra space.
- **Giải quyết bài toán nào?**: Set Matrix Zeroes (LC 73).
- **Trick**: Lưu marker vào row 0 và col 0, xử lý chúng last.

```java
// O(1) space approach
boolean firstRowZero = false, firstColZero = false;
// Check if first row/col has zeros initially
for (int j = 0; j < n; j++) if (matrix[0][j] == 0) firstRowZero = true;
for (int i = 0; i < m; i++) if (matrix[i][0] == 0) firstColZero = true;

// Use first row/col as markers
for (int i = 1; i < m; i++)
    for (int j = 1; j < n; j++)
        if (matrix[i][j] == 0) { matrix[i][0] = 0; matrix[0][j] = 0; }

// Zero out cells based on markers
for (int i = 1; i < m; i++)
    for (int j = 1; j < n; j++)
        if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;

// Zero first row/col if needed
if (firstRowZero) Arrays.fill(matrix[0], 0);
if (firstColZero) for (int i = 0; i < m; i++) matrix[i][0] = 0;
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Rotate Matrix 90° — Dry Run

```
Input:
[1, 2, 3]
[4, 5, 6]
[7, 8, 9]

Step 1: Transpose (swap matrix[i][j] with matrix[j][i])
[1, 4, 7]
[2, 5, 8]
[3, 6, 9]

Step 2: Reverse each row
[7, 4, 1]
[8, 5, 2]
[9, 6, 3]

✅ Output:
[7, 4, 1]
[8, 5, 2]
[9, 6, 3]
```

### Ví dụ 2: Spiral Matrix — Boundary Shrinking

```
matrix = [[1,2,3],[4,5,6],[7,8,9]]
top=0, bottom=2, left=0, right=2

Round 1:
  → top=0: 1,2,3 (top++)→ top=1
  ↓ right=2: 6,9 (right--)→ right=1
  ← bottom=2: 8,7 (bottom--)→ bottom=1
  ↑ left=0: 4 (left++)→ left=1

Round 2:
  top=1, bottom=1, left=1, right=1
  → top=1: 5 (top++)→ top=2
  top>bottom → exit

✅ Output: [1,2,3,6,9,8,7,4,5]
```

### Ví dụ 3: Happy Number — Floyd's Cycle Detection

```
n = 19

getSquareSum(19) = 1² + 9² = 1 + 81 = 82
getSquareSum(82) = 8² + 2² = 64 + 4 = 68
getSquareSum(68) = 6² + 8² = 36 + 64 = 100
getSquareSum(100) = 1² + 0² + 0² = 1

slow/fast eventually meet at 1 → return true ✅

n = 2
getSquareSum(2) = 4
getSquareSum(4) = 16
getSquareSum(16) = 37
getSquareSum(37) = 58
...eventually cycles: 4 → 16 → 37 → 58 → 89 → 145 → 42 → 20 → 4 (cycle!)
slow == fast != 1 → return false ❌
```

## 🔄 So sánh các Approach

### Rotate Matrix: In-place vs Extra Space

| Approach | Time | Space |
|----------|------|-------|
| **Transpose + Reverse ⭐** | O(n²) | O(1) |
| Extra array | O(n²) | O(n²) |

### Count Primes: Sieve vs Trial Division

| Approach | Time | Space |
|----------|------|-------|
| **Sieve of Eratosthenes ⭐** | O(n log log n) | O(n) |
| Trial division for each | O(n√n) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// Rotate Matrix:
// 1. n=1 → no change needed
// 2. n=2 → works correctly

// Spiral Matrix:
// 1. Single row → traverse left to right only
// 2. Single col → traverse top to bottom only
// 3. Single element → return it

// Happy Number:
// 1. n=1 → immediately happy
// 2. Cycle always involves {4,16,37,58,89,145,42,20}

// Count Primes:
// 1. n <= 2 → 0 primes
// 2. j = i*i overflow → cast to long: (long)i*i
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Rotate Matrix | O(n²) | O(1) |
| Spiral Matrix | O(m*n) | O(1) |
| Happy Number | O(log n) | O(1) Floyd's |
| Count Primes | O(n log log n) | O(n) |
| Power (modular) | O(log n) | O(1) |
| Set Matrix Zeroes | O(m*n) | O(1) |

## 💡 Tips phỏng vấn

1. **Rotate 90°**: Nhớ công thức = Transpose → Reverse rows. Rotate 270° (counter-clockwise) = Reverse rows → Transpose.
2. **Spiral**: Hỏi interviewer có thể thêm check `if (top <= bottom)` và `if (left <= right)` không để tránh duplicate.
3. **Overflow**: Với large numbers, luôn dùng `long` hoặc modular arithmetic.
4. **GCD ứng dụng**: Simplify fractions, LCM, Euclidean algorithm.
5. **Matrix (i,j) rotated**: `(row, col) → (col, n-1-row)` for 90° clockwise.
