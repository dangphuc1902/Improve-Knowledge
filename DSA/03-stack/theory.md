# 03 - Stack

## 📖 Tổng quan

**Stack** là cấu trúc dữ liệu **LIFO** (Last In, First Out) — phần tử vào sau ra trước.

Trong Java, dùng `Deque<T> stack = new ArrayDeque<>()` (khuyến khích hơn class `Stack`).

Stack giải quyết tốt các bài toán cần **theo dõi trạng thái trước đó** và **quay lại** khi gặp điều kiện.

## 🧠 Kiến thức cốt lõi

| Thao tác | Method | Time |
|----------|--------|------|
| Push | `stack.push(x)` | O(1) |
| Pop | `stack.pop()` | O(1) |
| Peek | `stack.peek()` | O(1) |
| IsEmpty | `stack.isEmpty()` | O(1) |

> **Lưu ý Java**: `ArrayDeque` nhanh hơn `Stack` (không synchronized) và nhanh hơn `LinkedList` (cache-friendly).

## 🔍 Khi nào sử dụng?

- Bài có **ngoặc đóng mở** (parentheses matching)
- Cần xử lý theo thứ tự **ngược lại** (reverse)
- **Monotonic stack**: tìm next greater/smaller element
- Bài yêu cầu **evaluate expression** (RPN, calculator)
- Cần **undo/backtrack** trạng thái

## 📝 Các Pattern phổ biến

### Pattern 1: Matching Parentheses
```java
Deque<Character> stack = new ArrayDeque<>();
for (char c : s.toCharArray()) {
    if (c == '(' || c == '{' || c == '[') {
        stack.push(c);
    } else {
        if (stack.isEmpty()) return false;
        char top = stack.pop();
        if (!isMatch(top, c)) return false;
    }
}
return stack.isEmpty();
```

### Pattern 2: Monotonic Stack (Next Greater Element)
```java
// Tìm next greater element cho mỗi phần tử
Deque<Integer> stack = new ArrayDeque<>(); // Lưu index
int[] result = new int[nums.length];
Arrays.fill(result, -1);

for (int i = 0; i < nums.length; i++) {
    while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
        result[stack.pop()] = nums[i];
    }
    stack.push(i);
}
```

### Pattern 3: Min Stack (Track min trong O(1))
```java
// Dùng 2 stack: 1 chính + 1 lưu min tại mỗi thời điểm
Deque<Integer> stack = new ArrayDeque<>();
Deque<Integer> minStack = new ArrayDeque<>();
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Stack matching | O(n) | O(n) |
| Monotonic Stack | O(n) | O(n) |
| Stack-based evaluation | O(n) | O(n) |

## 💡 Tips phỏng vấn

1. **Java**: Dùng `ArrayDeque` thay vì `Stack` — giải thích lý do nếu được hỏi
2. **Empty check**: LUÔN check `isEmpty()` trước khi `pop()` hoặc `peek()`
3. **Monotonic stack**: Rất powerful nhưng khó nhận ra — practice nhiều
4. **Tracing**: Vẽ stack ra giấy để trace qua ví dụ, dễ debug
