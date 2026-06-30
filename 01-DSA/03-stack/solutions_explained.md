# 03 - Stack: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 4 bài toán thuộc chủ đề **Stack** từ LeetCode Master Tracker.

---

## 18. Valid Parentheses (LeetCode #20) - Easy

### 💡 Ý tưởng cốt lõi
Kiểm tra tính hợp lệ của chuỗi các dấu ngoặc gồm `(`, `)`, `{`, `}`, `[`, `]`. Một chuỗi hợp lệ khi các ngoặc mở phải được đóng bằng các ngoặc đóng cùng loại theo đúng thứ tự (LIFO - Last In, First Out).
Chúng ta sử dụng một **Stack** (Ngăn xếp) để ghi nhớ thứ tự của các ngoặc mở.
* Khi duyệt gặp ngoặc mở, ta đẩy nó vào Stack.
* Khi gặp ngoặc đóng, ta lấy (pop) phần tử ở đỉnh Stack ra để so sánh. Nếu Stack rỗng hoặc phần tử đỉnh không tương ứng với ngoặc đóng hiện tại, chuỗi không hợp lệ.
* Sau khi duyệt hết chuỗi, Stack phải rỗng (tất cả ngoặc mở đều được đóng).

### 📊 Hướng tiếp cận tối ưu

#### Stack matching (Optimal) ⭐
* **Mô tả**: Sử dụng cấu trúc dữ liệu Stack (trong Java khuyến nghị sử dụng `ArrayDeque` thay vì `Stack` cổ điển vì an toàn luồng và tốc độ tốt hơn).
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt qua chuỗi $n$ ký tự.
  * **Space Complexity**: $O(n)$ trong trường hợp xấu nhất khi chuỗi toàn ngoặc mở.

### 🔄 Dry Run với ví dụ
* **Input**: `s = "()[]{}"`
* **Khởi tạo**: `stack = []`
* **Xử lý**:
  - `i = 0`, `'('` (ngoặc mở) → `stack.push('(')`. `stack = ['(']`
  - `i = 1`, `')'` (ngoặc đóng) → pop được `'('`. Vì `'('` khớp với `')'` → Tiếp tục. `stack = []`
  - `i = 2`, `'['` (ngoặc mở) → `stack.push('[')`. `stack = ['[']`
  - `i = 3`, `']'` (ngoặc đóng) → pop được `'['`. Khớp với `']'` → Tiếp tục. `stack = []`
  - Tương tự cho `{}`.
* **Kết quả**: `true`

### 💻 Java Clean Code
```java
public boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c);
        } else {
            if (stack.isEmpty()) return false;
            char top = stack.pop();
            if (c == ')' && top != '(') return false;
            if (c == ']' && top != '[') return false;
            if (c == '}' && top != '{') return false;
        }
    }
    return stack.isEmpty();
}
```

---

## 19. Min Stack (LeetCode #155) - Medium

### 💡 Ý tưởng cốt lõi
Thiết kế cấu trúc dữ liệu Stack hỗ trợ các thao tác `push`, `pop`, `top`, và truy vấn phần tử nhỏ nhất `getMin` trong thời gian $O(1)$.
Để lấy phần tử nhỏ nhất trong $O(1)$ mà không cần duyệt toàn bộ Stack, ta cần lưu giữ thông tin giá trị nhỏ nhất tương ứng với mỗi mức độ cao của Stack.
Giải pháp tối ưu là sử dụng **một Stack phụ** (hoặc tích hợp lưu trữ theo cặp `(giá trị, giá trị nhỏ nhất hiện tại)`) để ghi nhớ trạng thái nhỏ nhất tại thời điểm đẩy phần tử vào.

### 📊 Hướng tiếp cận tối ưu

#### Two Stacks hoặc Pair Stack (Optimal) ⭐
* **Mô tả**: Sử dụng một Stack chính lưu giá trị và một Stack phụ lưu giá trị min tương ứng. Hoặc sử dụng một Stack duy nhất lưu mảng 2 phần tử `[val, min_value_so_far]`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(1)$ cho tất cả các phương thức `push`, `pop`, `top`, `getMin`.
  * **Space Complexity**: $O(n)$ do lưu trữ nhân đôi thông tin.

### 🔄 Dry Run với ví dụ
* **Thao tác**:
  1. `push(-2)`: stack rỗng → min = -2. Stack lưu: `[(-2, -2)]`
  2. `push(0)`: min = min(0, -2) = -2. Stack lưu: `[(-2, -2), (0, -2)]`
  3. `push(-3)`: min = min(-3, -2) = -3. Stack lưu: `[(-2, -2), (0, -2), (-3, -3)]`
  4. `getMin()` → trả về `-3` (phần tử min ở đỉnh stack).
  5. `pop()` → pop đỉnh stack `(-3, -3)`. Stack còn: `[(-2, -2), (0, -2)]`
  6. `getMin()` → trả về `-2`.

### 💻 Java Clean Code
```java
class MinStack {
    private Deque<int[]> stack; // lưu cặp [giá trị, min hiện tại]

    public MinStack() {
        stack = new ArrayDeque<>();
    }
    
    public void push(int val) {
        if (stack.isEmpty()) {
            stack.push(new int[]{val, val});
        } else {
            int currentMin = Math.min(val, stack.peek()[1]);
            stack.push(new int[]{val, currentMin});
        }
    }
    
    public void pop() {
        stack.pop();
    }
    
    public int top() {
        return stack.peek()[0];
    }
    
    public int getMin() {
        return stack.peek()[1];
    }
}
```

---

## 20. Daily Temperatures (LeetCode #739) - Medium

### 💡 Ý tưởng cốt lõi
Cho mảng nhiệt độ `temperatures`, tìm số ngày phải chờ cho đến khi có một ngày ấm hơn. Nếu không có ngày nào ấm hơn, trả về `0`.
Cách tiếp cận Brute Force mất $O(n^2)$. Để tối ưu về $O(n)$, ta sử dụng một **Monotonic Decreasing Stack** (Ngăn xếp đơn điệu giảm dần). Stack sẽ lưu trữ **các index** của các ngày có nhiệt độ giảm dần.
Khi duyệt đến ngày `i` có nhiệt độ `temperatures[i]` cao hơn nhiệt độ ở index trên đỉnh Stack:
* Ta liên tục lấy (pop) index ở đỉnh Stack ra.
* Khoảng cách ngày chờ sẽ là `i - popped_index`.
* Lưu khoảng cách này vào mảng kết quả tại `popped_index`.
* Sau cùng, đẩy `i` vào Stack.

### 📊 Hướng tiếp cận tối ưu

#### Monotonic Stack (Optimal) ⭐
* **Mô tả**: Lưu trữ index để tính khoảng cách dễ dàng.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ vì mỗi phần tử chỉ được đẩy vào và lấy ra khỏi Stack tối đa đúng 1 lần.
  * **Space Complexity**: $O(n)$ cho bộ nhớ lưu trữ Stack.

### 🔄 Dry Run với ví dụ
* **Input**: `temperatures = [73, 74, 75, 71, 69, 72, 76, 73]`
* **Khởi tạo**: `res = [0, 0, 0, 0, 0, 0, 0, 0]`, `stack = []`
* **Xử lý**:
  - `i = 0` (73): stack rỗng → push `0`. `stack = [0]`
  - `i = 1` (74): `74 > temp[0]` (73) → pop `0`, `res[0] = 1 - 0 = 1`. push `1`. `stack = [1]`
  - `i = 2` (75): `75 > temp[1]` (74) → pop `1`, `res[1] = 2 - 1 = 1`. push `2`. `stack = [2]`
  - `i = 3` (71): `71 < temp[2]` (75) → push `3`. `stack = [2, 3]`
  - `i = 4` (69): `69 < temp[3]` (71) → push `4`. `stack = [2, 3, 4]`
  - `i = 5` (72):
    * `72 > temp[4]` (69) → pop `4`, `res[4] = 5 - 4 = 1`.
    * `72 > temp[3]` (71) → pop `3`, `res[3] = 5 - 3 = 2`.
    * `72 < temp[2]` (75) → dừng pop, push `5`. `stack = [2, 5]`
  - ... lặp tiếp đến hết.
* **Kết quả**: `[1, 1, 4, 2, 1, 1, 0, 0]`

### 💻 Java Clean Code
```java
public int[] dailyTemperatures(int[] temperatures) {
    int n = temperatures.length;
    int[] res = new int[n];
    Deque<Integer> stack = new ArrayDeque<>(); // Lưu index
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
            int prevIndex = stack.pop();
            res[prevIndex] = i - prevIndex;
        }
        stack.push(i);
    }
    return res;
}
```

---

## 21. Largest Rectangle in Histogram (LeetCode #84) - Hard

### 💡 Ý tưởng cốt lõi
Tìm diện tích hình chữ nhật lớn nhất có thể tạo ra trong biểu đồ cột.
Với mỗi cột `i` có chiều cao `h = heights[i]`, hình chữ nhật lớn nhất chứa toàn bộ cột `i` sẽ kéo dài từ cột đầu tiên bên trái thấp hơn `i` đến cột đầu tiên bên phải thấp hơn `i`.
Chúng ta dùng một **Monotonic Increasing Stack** để lưu trữ các index của cột có chiều cao tăng dần.
Khi gặp một cột `i` thấp hơn cột đỉnh Stack:
* Ta pop cột ở đỉnh Stack ra để tính diện tích hình chữ nhật lớn nhất lấy cột vừa pop làm chiều cao.
* Cạnh bên phải giới hạn là `i`.
* Cạnh bên trái giới hạn là index nằm ở đỉnh Stack mới (sau khi pop).
* Chiều rộng hình chữ nhật: `width = i - left_limit - 1`.
* Diện tích: `area = height * width`. Cập nhật `maxArea`.

### 📊 Hướng tiếp cận tối ưu

#### Monotonic Stack (Optimal) ⭐
* **Mô tả**: Duyệt qua các cột từ trái sang phải. Để xử lý hết các cột còn lại trong Stack sau khi duyệt xong, ta có thể thêm một cột giả có độ cao `0` ở cuối mảng.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do mỗi cột chỉ được đẩy vào và pop ra khỏi Stack tối đa 1 lần.
  * **Space Complexity**: $O(n)$ dùng cho bộ nhớ Stack.

### 🔄 Dry Run với ví dụ
* **Input**: `heights = [2, 1, 5, 6, 2, 3]`
* **Thêm cột 0 ở cuối**: `heights = [2, 1, 5, 6, 2, 3, 0]`
* **Xử lý**:
  - `i = 0` (h = 2) → push `0`. `stack = [0]`
  - `i = 1` (h = 1): `1 < heights[0]` (2) → pop `0` (chiều cao 2).
    * Stack rỗng → cạnh trái là `-1`.
    * `width = 1 - (-1) - 1 = 1`. `area = 2 * 1 = 2`. Max = 2.
    * Push `1`. `stack = [1]`
  - `i = 2` (h = 5) → push `2`. `stack = [1, 2]`
  - `i = 3` (h = 6) → push `3`. `stack = [1, 2, 3]`
  - `i = 4` (h = 2): `2 < heights[3]` (6) → pop `3` (chiều cao 6).
    * Đỉnh mới là `2`. `width = 4 - 2 - 1 = 1`. `area = 6 * 1 = 6`. Max = 6.
    * Vẫn có `2 < heights[2]` (5) → pop `2` (chiều cao 5).
    * Đỉnh mới là `1`. `width = 4 - 1 - 1 = 2`. `area = 5 * 2 = 10`. Max = 10.
    * Dừng pop vì `2 > heights[1]` (1) → push `4`. `stack = [1, 4]`
  - ... lặp tiếp đến cột `0` cuối cùng sẽ giải phóng toàn bộ Stack.
* **Kết quả**: `10`

### 💻 Java Clean Code
```java
public int largestRectangleArea(int[] heights) {
    int n = heights.length;
    Deque<Integer> stack = new ArrayDeque<>();
    int maxArea = 0;
    
    for (int i = 0; i <= n; i++) {
        // Cột giả có chiều cao bằng 0 ở cuối để clear stack
        int currentHeight = (i == n) ? 0 : heights[i];
        
        while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    
    return maxArea;
}
```
