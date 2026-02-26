import java.util.*;

/**
 * =============================================
 *  03 - STACK
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// -----------------------------------------------
// Bài 1: Valid Parentheses (LeetCode #20) - Easy
// -----------------------------------------------
// Kiểm tra chuỗi ngoặc có hợp lệ không.
// Ví dụ: "()[]{}" → true, "(]" → false
//
// Approach: Push ngoặc mở vào stack, gặp ngoặc đóng thì pop và check matching.
//
// Time: O(n) - duyệt chuỗi 1 lần
// Space: O(n) - stack tối đa n/2 phần tử
class ValidParentheses {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            // Gặp ngoặc mở → push ngoặc đóng tương ứng (trick giúp so sánh dễ hơn)
            if (c == '(') stack.push(')');
            else if (c == '{') stack.push('}');
            else if (c == '[') stack.push(']');
            else {
                // Gặp ngoặc đóng → check với top of stack
                if (stack.isEmpty() || stack.pop() != c) {
                    return false;
                }
            }
        }

        // Stack rỗng = tất cả ngoặc đều matched
        return stack.isEmpty();
    }
}

// -----------------------------------------------
// Bài 2: Min Stack (LeetCode #155) - Medium
// -----------------------------------------------
// Thiết kế stack hỗ trợ push, pop, top, và getMin trong O(1).
//
// Approach: Dùng 2 stack song song.
// - mainStack: lưu giá trị bình thường
// - minStack: lưu giá trị min tại thời điểm tương ứng
//
// Time: O(1) cho mọi thao tác
// Space: O(n) - 2 stack
class MinStack {
    private Deque<Integer> mainStack;
    private Deque<Integer> minStack;

    public MinStack() {
        mainStack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    public void push(int val) {
        mainStack.push(val);
        // Min stack: push min giữa val mới và min hiện tại
        int currentMin = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
        minStack.push(currentMin);
    }

    public void pop() {
        mainStack.pop();
        minStack.pop(); // Đồng bộ 2 stack
    }

    public int top() {
        return mainStack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}

// -----------------------------------------------
// Bài 3: Evaluate Reverse Polish Notation (LeetCode #150) - Medium
// -----------------------------------------------
// Tính giá trị biểu thức dạng Reverse Polish Notation (hậu tố).
// Ví dụ: ["2","1","+","3","*"] → (2+1)*3 = 9
//
// Approach: Dùng stack.
// - Gặp số → push vào stack
// - Gặp operator → pop 2 số, tính, push kết quả
//
// Time: O(n) - duyệt mảng 1 lần
// Space: O(n) - stack
class EvaluateRPN {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : tokens) {
            switch (token) {
                case "+":
                case "-":
                case "*":
                case "/":
                    int b = stack.pop(); // Toán hạng phải (pop trước)
                    int a = stack.pop(); // Toán hạng trái (pop sau)
                    stack.push(calculate(a, b, token));
                    break;
                default:
                    stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }

    private int calculate(int a, int b, String operator) {
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b; // Chia lấy phần nguyên (truncate toward zero)
            default: throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
