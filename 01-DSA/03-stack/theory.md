# 03 - Stack

## 📖 Tổng quan

**Stack** là cấu trúc dữ liệu **LIFO** (Last In, First Out) — phần tử vào sau ra trước.  
Trong Java, dùng `Deque<Integer> stack = new ArrayDeque<>()` (tốt hơn `Stack<>` vì thread-safe).

> **Ý tưởng cốt lõi:** Stack giúp "ghi nhớ" trạng thái trước đó. Mỗi khi cần biết "cái gì đã xảy ra trước?" → nghĩ đến Stack.

## 🧠 Kiến thức cốt lõi

### Stack Operations

| Thao tác | ArrayDeque | Stack (legacy) | Time |
|----------|-----------|----------------|------|
| Push (thêm vào đỉnh) | `push()` / `addFirst()` | `push()` | O(1) |
| Pop (lấy từ đỉnh) | `pop()` / `pollFirst()` | `pop()` | O(1) |
| Peek (xem đỉnh) | `peek()` / `peekFirst()` | `peek()` | O(1) |
| isEmpty | `isEmpty()` | `isEmpty()` | O(1) |

> **Khuyến cáo Java:** Dùng `Deque<Integer> stack = new ArrayDeque<>()` thay vì `Stack<Integer>` vì `Stack` kế thừa `Vector` — synchronized overhead không cần thiết.

### Monotonic Stack

| Loại | Duy trì | Ứng dụng |
|------|---------|----------|
| **Monotonic Increasing** | Phần tử tăng dần từ đáy đến đỉnh | Tìm Next Smaller Element |
| **Monotonic Decreasing** | Phần tử giảm dần từ đáy đến đỉnh | Tìm Next Greater Element, Daily Temperatures |

## 🔍 Khi nào sử dụng?

- **Matching brackets/parentheses**: Mở → push, Đóng → pop & check
- **Undo/Redo mechanism**: History lưu vào stack
- **Expression evaluation**: Toán tử, RPN (Reverse Polish Notation)
- **Next Greater/Smaller Element**: Monotonic Stack
- **DFS iteration**: Thay thế đệ quy bằng explicit stack
- **Khi thấy pattern**: *"cần biết phần tử vừa xử lý"*, *"cần khôi phục trạng thái trước"*

## 📝 Các Pattern phổ biến

### Pattern 1: Bracket Matching
- **Nó là gì?**: Push khi gặp bracket mở, pop & kiểm tra khi gặp bracket đóng.
- **Giải quyết bài toán nào?**: Valid Parentheses, Decode String, Basic Calculator.
- **Ưu điểm**: O(n) time, O(n) space — trực quan, dễ implement.
- **Nhược điểm**: Cần xử lý stack rỗng khi pop.
- **Sự thay thế**: Counter đơn giản (chỉ dùng được khi 1 loại bracket).

```java
Deque<Character> stack = new ArrayDeque<>();
Map<Character, Character> pairs = Map.of(')', '(', '}', '{', ']', '[');
for (char c : s.toCharArray()) {
    if (!pairs.containsKey(c)) {
        stack.push(c); // opening bracket
    } else {
        if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
    }
}
return stack.isEmpty();
```

### Pattern 2: Monotonic Stack — Next Greater Element
- **Nó là gì?**: Duy trì stack đơn điệu để tìm next greater/smaller trong O(n) thay vì O(n²).
- **Giải quyết bài toán nào?**: Daily Temperatures, Next Greater Element, Largest Rectangle in Histogram.
- **Ưu điểm**: O(n) — mỗi phần tử chỉ push/pop 1 lần.
- **Nhược điểm**: Cần tư duy rõ "tại sao pop?" và "lưu index hay value?".
- **Sự thay thế**: Brute force O(n²) — duyệt từng cặp.

```java
// Daily Temperatures: find next warmer day
int[] result = new int[temperatures.length];
Deque<Integer> stack = new ArrayDeque<>(); // stores indices
for (int i = 0; i < temperatures.length; i++) {
    while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
        int idx = stack.pop();
        result[idx] = i - idx;
    }
    stack.push(i);
}
```

### Pattern 3: Min Stack — Augmented Stack
- **Nó là gì?**: Dùng **stack phụ** (hoặc tuple) để track minimum bên cạnh stack chính.
- **Giải quyết bài toán nào?**: Min Stack, Max Stack, Stack with getMin/getMax in O(1).
- **Ưu điểm**: Tất cả operations O(1).
- **Nhược điểm**: Dùng gấp đôi space.
- **Sự thay thế**: Lưu `(value, currentMin)` trong 1 stack → tiết kiệm hơn.

```java
class MinStack {
    private Deque<int[]> stack = new ArrayDeque<>(); // [value, min]
    
    public void push(int val) {
        int min = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
        stack.push(new int[]{val, min});
    }
    
    public void pop() { stack.pop(); }
    public int top() { return stack.peek()[0]; }
    public int getMin() { return stack.peek()[1]; }
}
```

### Pattern 4: Stack-based Expression Evaluation
- **Nó là gì?**: Stack lưu operands, khi gặp operator thì pop và tính.
- **Giải quyết bài toán nào?**: Evaluate RPN, Basic Calculator, Decode String.
- **Ưu điểm**: O(n) linear scan.
- **Nhược điểm**: Cần xử lý precedence và sign cẩn thận.

```java
// Evaluate Reverse Polish Notation
Deque<Integer> stack = new ArrayDeque<>();
for (String token : tokens) {
    if ("+-*/".contains(token)) {
        int b = stack.pop(), a = stack.pop();
        switch (token) {
            case "+" -> stack.push(a + b);
            case "-" -> stack.push(a - b);
            case "*" -> stack.push(a * b);
            case "/" -> stack.push(a / b);
        }
    } else {
        stack.push(Integer.parseInt(token));
    }
}
return stack.pop();
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Valid Parentheses — Dry Run

```
Input: s = "({[]})"

i=0, c='(': opening → push '('
  stack = ['(']

i=1, c='{': opening → push '{'
  stack = ['(', '{']

i=2, c='[': opening → push '['
  stack = ['(', '{', '[']

i=3, c=']': closing, pairs[']']='['
  stack.peek() = '[' ✓ → pop
  stack = ['(', '{']

i=4, c='}': closing, pairs['}']='{' 
  stack.peek() = '{' ✓ → pop
  stack = ['(']

i=5, c=')': closing, pairs[')']='('
  stack.peek() = '(' ✓ → pop
  stack = []

stack.isEmpty() = true ✅ Output: true
```

**Case thất bại:**
```
Input: s = "([)]"

i=0: push '(' → ['(']
i=1: push '[' → ['(', '[']
i=2: c=')' → pairs[')']='(' ≠ stack.peek()='[' ❌
Output: false
```

### Ví dụ 2: Daily Temperatures — Monotonic Stack Dry Run

```
Input: temperatures = [73, 74, 75, 71, 69, 72, 76, 73]
result = [0, 0, 0, 0, 0, 0, 0, 0]

i=0, t=73: stack=[] → push 0. stack=[0]
i=1, t=74: t[1]=74 > t[stack.peek()=0]=73
  pop 0: result[0] = 1-0 = 1. stack=[]
  push 1. stack=[1]
i=2, t=75: t[2]=75 > t[1]=74
  pop 1: result[1] = 2-1 = 1. stack=[]
  push 2. stack=[2]
i=3, t=71: t[3]=71 < t[2]=75 → push 3. stack=[2,3]
i=4, t=69: t[4]=69 < t[3]=71 → push 4. stack=[2,3,4]
i=5, t=72: t[5]=72 > t[4]=69
  pop 4: result[4] = 5-4 = 1
  t[5]=72 > t[3]=71: pop 3: result[3] = 5-3 = 2
  t[5]=72 < t[2]=75: push 5. stack=[2,5]
i=6, t=76: t[6]=76 > t[5]=72
  pop 5: result[5] = 6-5 = 1
  t[6]=76 > t[2]=75: pop 2: result[2] = 6-2 = 4
  stack=[]: push 6. stack=[6]
i=7, t=73: t[7]=73 < t[6]=76 → push 7. stack=[6,7]

✅ Output: [1, 1, 4, 2, 1, 1, 0, 0]
```

## 🔄 So sánh các Approach

### Valid Parentheses: Stack vs Counter

| Approach | Time | Space | Hỗ trợ nhiều loại bracket |
|----------|------|-------|--------------------------|
| **Stack ⭐** | O(n) | O(n) | Có ✓ |
| Counter | O(n) | O(1) | Chỉ 1 loại bracket |

### Daily Temperatures: Monotonic Stack vs Brute Force

| Approach | Time | Space |
|----------|------|-------|
| **Monotonic Stack ⭐** | O(n) | O(n) |
| Brute Force (2 loops) | O(n²) | O(1) |

## 🚨 Edge Cases cần chú ý

```java
// Valid Parentheses:
// 1. s = "" → true (chuỗi rỗng)
// 2. s = "]" → false (stack rỗng khi pop)
// 3. s = "(((" → false (stack không rỗng sau khi duyệt)
// 4. s = "(]" → false (mismatch loại bracket)

// Min Stack:
// 1. Push nhiều giá trị giống nhau → min vẫn đúng
// 2. pop() đến khi còn 1 phần tử → getMin() vẫn đúng

// Daily Temperatures:
// 1. Mảng giảm dần → tất cả result = 0
// 2. Mảng tăng dần → result[i] = 1 cho mọi i trừ cuối
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Valid Parentheses | O(n) | O(n) |
| Min Stack (mọi operation) | O(1) | O(n) |
| Daily Temperatures | O(n) | O(n) |
| Evaluate RPN | O(n) | O(n) |
| Largest Rectangle in Histogram | O(n) | O(n) |

## 💡 Tips phỏng vấn

1. **Khi nào dùng Stack?** Khi cần "undo" hoặc cần biết phần tử *gần nhất* thỏa điều kiện nào đó.
2. **Min Stack trick**: Lưu `(value, currentMin)` pair thay vì 2 stack riêng biệt.
3. **Monotonic Stack**: Luôn lưu **index** thay vì value (để tính khoảng cách).
4. **Bracket matching**: Luôn kiểm tra `stack.isEmpty()` trước khi `pop()` hoặc `peek()`.
5. **Java**: Ưu tiên `ArrayDeque` thay vì `Stack` — nhớ giải thích lý do trong phỏng vấn!
