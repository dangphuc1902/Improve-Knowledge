import java.util.*;

/**
 * 03 - Stack Solutions
 * Các bài: Valid Parentheses, Min Stack, Daily Temperatures, Evaluate RPN
 */
public class Solutions {

    // ============================================================
    // LC 20 - Valid Parentheses
    // Approach 1: Stack — O(n) time, O(n) space ⭐
    // Approach 2: Counter (chỉ 1 loại bracket) — O(n) time, O(1) space
    // ============================================================
    static class ValidParentheses {
        // ⭐ Optimal: Stack + Map
        // Time: O(n), Space: O(n)
        public boolean isValid(String s) {
            Deque<Character> stack = new ArrayDeque<>();
            Map<Character, Character> pairs = Map.of(
                ')', '(',
                '}', '{',
                ']', '['
            );

            for (char c : s.toCharArray()) {
                if (!pairs.containsKey(c)) {
                    // Opening bracket → push
                    stack.push(c);
                } else {
                    // Closing bracket → pop và kiểm tra match
                    if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                        return false;
                    }
                }
            }
            return stack.isEmpty(); // tất cả đã được match
        }

        // Approach 2: Explicit check (không dùng Map, code nhanh hơn)
        public boolean isValidExplicit(String s) {
            Deque<Character> stack = new ArrayDeque<>();
            for (char c : s.toCharArray()) {
                if (c == '(' || c == '{' || c == '[') {
                    stack.push(c);
                } else {
                    if (stack.isEmpty()) return false;
                    char top = stack.pop();
                    if (c == ')' && top != '(') return false;
                    if (c == '}' && top != '{') return false;
                    if (c == ']' && top != '[') return false;
                }
            }
            return stack.isEmpty();
        }
    }

    // ============================================================
    // LC 155 - Min Stack
    // Approach 1: Stack of pairs (value, currentMin) — O(1) all ops ⭐
    // Approach 2: Two Stacks (main + min stack) — O(1) all ops
    // ============================================================
    static class MinStack {
        // ⭐ Approach 1: Lưu pair [value, currentMin] — 1 stack duy nhất
        // Time: O(1) mọi operation, Space: O(n)
        private Deque<int[]> stack; // [value, currentMin]

        public MinStack() {
            stack = new ArrayDeque<>();
        }

        public void push(int val) {
            int currentMin = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
            stack.push(new int[]{val, currentMin});
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

    // Approach 2: Two Stacks
    static class MinStackTwoStack {
        private Deque<Integer> mainStack = new ArrayDeque<>();
        private Deque<Integer> minStack = new ArrayDeque<>(); // luôn có min ở đỉnh

        public void push(int val) {
            mainStack.push(val);
            // Push vào minStack nếu nhỏ hơn hoặc bằng current min
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }

        public void pop() {
            int val = mainStack.pop();
            // Chỉ pop minStack nếu giá trị pop bằng min hiện tại
            if (!minStack.isEmpty() && val == minStack.peek()) {
                minStack.pop();
            }
        }

        public int top() { return mainStack.peek(); }
        public int getMin() { return minStack.peek(); }
    }

    // ============================================================
    // LC 739 - Daily Temperatures
    // Approach 1: Monotonic Stack — O(n) time, O(n) space ⭐
    // Approach 2: Brute Force — O(n²) time, O(1) space
    // ============================================================
    static class DailyTemperatures {
        // ⭐ Optimal: Monotonic Decreasing Stack (lưu index)
        // Time: O(n) — mỗi index push/pop đúng 1 lần
        // Space: O(n)
        public int[] dailyTemperatures(int[] temperatures) {
            int n = temperatures.length;
            int[] result = new int[n]; // default = 0
            Deque<Integer> stack = new ArrayDeque<>(); // lưu index

            for (int i = 0; i < n; i++) {
                // Pop tất cả index có nhiệt độ < nhiệt độ hiện tại
                while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                    int idx = stack.pop();
                    result[idx] = i - idx; // số ngày phải chờ
                }
                stack.push(i);
            }
            return result;
        }

        // Approach 2: Brute Force — dễ hiểu nhưng O(n²)
        public int[] dailyTemperaturesBrute(int[] temperatures) {
            int n = temperatures.length;
            int[] result = new int[n];
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (temperatures[j] > temperatures[i]) {
                        result[i] = j - i;
                        break;
                    }
                }
            }
            return result;
        }
    }

    // ============================================================
    // LC 150 - Evaluate Reverse Polish Notation
    // Approach 1: Stack — O(n) time, O(n) space ⭐
    // ============================================================
    static class EvaluateRPN {
        // ⭐ Optimal: Stack-based evaluation
        // Time: O(n), Space: O(n)
        public int evalRPN(String[] tokens) {
            Deque<Integer> stack = new ArrayDeque<>();

            for (String token : tokens) {
                switch (token) {
                    case "+", "-", "*", "/" -> {
                        int b = stack.pop(); // second operand
                        int a = stack.pop(); // first operand
                        switch (token) {
                            case "+" -> stack.push(a + b);
                            case "-" -> stack.push(a - b);
                            case "*" -> stack.push(a * b);
                            case "/" -> stack.push(a / b); // truncation toward zero
                        }
                    }
                    default -> stack.push(Integer.parseInt(token));
                }
            }
            return stack.pop();
        }
    }

    // ============================================================
    // LC 84 - Largest Rectangle in Histogram (Hard - Bonus)
    // Approach 1: Monotonic Stack — O(n) time, O(n) space ⭐
    // Approach 2: Brute Force — O(n²) time, O(1) space
    // ============================================================
    static class LargestRectangleHistogram {
        // ⭐ Optimal: Monotonic Stack (Increasing)
        // Time: O(n), Space: O(n)
        // Idea: với mỗi bar, tìm boundary trái/phải mà bar là bar thấp nhất
        public int largestRectangleArea(int[] heights) {
            int n = heights.length;
            int maxArea = 0;
            Deque<Integer> stack = new ArrayDeque<>(); // monotonic increasing stack (lưu index)

            for (int i = 0; i <= n; i++) {
                int h = (i == n) ? 0 : heights[i]; // sentinel value = 0 để flush stack
                while (!stack.isEmpty() && h < heights[stack.peek()]) {
                    int height = heights[stack.pop()];
                    int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                    maxArea = Math.max(maxArea, height * width);
                }
                stack.push(i);
            }
            return maxArea;
        }

        // Approach 2: Brute Force — O(n²)
        public int largestRectangleAreaBrute(int[] heights) {
            int maxArea = 0;
            for (int i = 0; i < heights.length; i++) {
                int minHeight = heights[i];
                for (int j = i; j < heights.length; j++) {
                    minHeight = Math.min(minHeight, heights[j]);
                    maxArea = Math.max(maxArea, minHeight * (j - i + 1));
                }
            }
            return maxArea;
        }
    }
}
