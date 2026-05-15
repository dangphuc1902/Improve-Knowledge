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
- **Nó là gì?**: Sử dụng Stack để lưu trữ các dấu ngoặc mở. Khi gặp dấu ngoặc đóng, ta kiểm tra xem nó có khớp với dấu ngoặc ở đỉnh Stack không.
- **Giải quyết bài toán nào?**: 
    - Kiểm tra tính hợp lệ của dấu ngoặc (`Valid Parentheses`).
    - Tính toán giá trị biểu thức toán học.
- **Ưu điểm**:
    - Xử lý hoàn hảo các cấu trúc lồng nhau (nested structures).
    - Tốc độ O(n).
- **Nhược điểm**:
    - Tốn bộ nhớ O(n) để lưu Stack trong trường hợp xấu nhất (toàn dấu mở).
- **Sự thay thế**:
    - **Recursion**: Có thể dùng đệ quy (Implicit Stack) nhưng dễ gây `StackOverflow` nếu độ sâu quá lớn.

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

### Pattern 2: Monotonic Stack
- **Nó là gì?**: Một Stack mà các phần tử luôn được duy trì theo thứ tự tăng dần hoặc giảm dần. Khi một phần tử mới phá vỡ thứ tự này, ta `pop` các phần tử cũ ra để xử lý trước khi thêm phần tử mới vào.
- **Giải quyết bài toán nào?**: 
    - Tìm phần tử lớn hơn/nhỏ hơn gần nhất (`Next Greater Element`, `Daily Temperatures`).
    - Tính diện tích hình chữ nhật lớn nhất trong biểu đồ (`Largest Rectangle in Histogram`).
- **Ưu điểm**:
    - Giảm độ phức tạp từ O(n²) xuống O(n) vì mỗi phần tử chỉ được `push` và `pop` đúng 1 lần.
- **Nhược điểm**:
    - Khó nhận ra và khó cài đặt chính xác trong lần đầu.
- **Sự thay thế**:
    - **Brute Force**: Dùng 2 vòng lặp (O(n²)).

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

### Pattern 3: Min Stack / Auxiliary Stack
- **Nó là gì?**: Sử dụng một Stack phụ (hoặc lưu cặp giá trị) để theo dõi một trạng thái đặc biệt (như giá trị nhỏ nhất) tại mọi thời điểm của Stack chính.
- **Giải quyết bài toán nào?**: 
    - Thiết kế Stack hỗ trợ lấy `min` trong O(1) (`Min Stack`).
- **Ưu điểm**:
    - Truy xuất thuộc tính đặc biệt cực nhanh O(1).
- **Nhược điểm**:
    - Gấp đôi dung lượng bộ nhớ sử dụng.
- **Sự thay thế**:
    - Duyệt toàn bộ Stack mỗi khi cần tìm min (O(n) time).

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
