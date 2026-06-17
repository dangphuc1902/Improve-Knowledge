# PART 16 — Coding Test (70 Problems)

---

## 🟢 EASY (30 Problems)

### Arrays & Hashing

**E01. Two Sum** (LeetCode 1)
```
Problem: Given nums and target, return indices of two numbers that add up to target.
Input: nums = [2,7,11,15], target = 9
Output: [0,1]

Solution:
Map<Integer, Integer> seen = new HashMap<>();
for (int i = 0; i < nums.length; i++) {
    int complement = target - nums[i];
    if (seen.containsKey(complement)) return new int[]{seen.get(complement), i};
    seen.put(nums[i], i);
}

Time: O(n)  Space: O(n)
Follow-up: What if multiple answers? → return all pairs
```

**E02. Contains Duplicate** (LeetCode 217)
```
Problem: Return true if any value appears at least twice.

Solution:
Set<Integer> seen = new HashSet<>();
for (int num : nums) {
    if (!seen.add(num)) return true;
}
return false;

Time: O(n)  Space: O(n)
```

**E03. Valid Anagram** (LeetCode 242)
```
Problem: Check if t is an anagram of s.

Solution:
int[] count = new int[26];
for (char c : s.toCharArray()) count[c - 'a']++;
for (char c : t.toCharArray()) count[c - 'a']--;
for (int n : count) if (n != 0) return false;
return true;

Time: O(n)  Space: O(1)
Follow-up: Unicode characters → use HashMap
```

**E04. Best Time to Buy and Sell Stock** (LeetCode 121)
```
Problem: Find max profit from one buy and one sell.

Solution:
int minPrice = Integer.MAX_VALUE, maxProfit = 0;
for (int price : prices) {
    minPrice = Math.min(minPrice, price);
    maxProfit = Math.max(maxProfit, price - minPrice);
}
return maxProfit;

Time: O(n)  Space: O(1)
```

**E05. Majority Element** (LeetCode 169)
```
Problem: Find element appearing more than n/2 times.

Solution (Boyer-Moore Voting):
int count = 0, candidate = 0;
for (int num : nums) {
    if (count == 0) candidate = num;
    count += (num == candidate) ? 1 : -1;
}
return candidate;

Time: O(n)  Space: O(1)
```

**E06. Move Zeroes** (LeetCode 283)
```
Problem: Move all 0s to end while maintaining relative order.

Solution:
int insertPos = 0;
for (int num : nums) {
    if (num != 0) nums[insertPos++] = num;
}
while (insertPos < nums.length) nums[insertPos++] = 0;

Time: O(n)  Space: O(1)
```

**E07. Intersection of Two Arrays** (LeetCode 349)
```
Problem: Return array of unique elements in both arrays.

Solution:
Set<Integer> set1 = new HashSet<>();
for (int n : nums1) set1.add(n);
Set<Integer> result = new HashSet<>();
for (int n : nums2) if (set1.contains(n)) result.add(n);
return result.stream().mapToInt(Integer::intValue).toArray();

Time: O(m+n)  Space: O(m)
```

---

### Strings

**E08. Reverse String** (LeetCode 344)
```
Two pointer approach:
int l = 0, r = s.length - 1;
while (l < r) {
    char temp = s[l]; s[l] = s[r]; s[r] = temp;
    l++; r--;
}

Time: O(n)  Space: O(1)
```

**E09. Valid Palindrome** (LeetCode 125)
```
Solution:
int l = 0, r = s.length() - 1;
while (l < r) {
    while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
    while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
    if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) return false;
    l++; r--;
}
return true;

Time: O(n)  Space: O(1)
```

**E10. First Unique Character** (LeetCode 387)
```
int[] count = new int[26];
for (char c : s.toCharArray()) count[c - 'a']++;
for (int i = 0; i < s.length(); i++)
    if (count[s.charAt(i) - 'a'] == 1) return i;
return -1;

Time: O(n)  Space: O(1)
```

**E11. Roman to Integer** (LeetCode 13)
```
Map<Character, Integer> map = Map.of('I',1,'V',5,'X',10,'L',50,'C',100,'D',500,'M',1000);
int result = 0;
for (int i = 0; i < s.length(); i++) {
    int curr = map.get(s.charAt(i));
    int next = (i + 1 < s.length()) ? map.get(s.charAt(i+1)) : 0;
    result += (curr < next) ? -curr : curr;
}
return result;
```

**E12. Longest Common Prefix** (LeetCode 14)
```
String prefix = strs[0];
for (String s : strs) {
    while (!s.startsWith(prefix)) prefix = prefix.substring(0, prefix.length() - 1);
}
return prefix;
```

---

### Linked List

**E13. Reverse Linked List** (LeetCode 206)
```java
ListNode prev = null, curr = head;
while (curr != null) {
    ListNode next = curr.next;
    curr.next = prev;
    prev = curr;
    curr = next;
}
return prev;
// Time: O(n)  Space: O(1)
```

**E14. Merge Two Sorted Lists** (LeetCode 21)
```java
ListNode dummy = new ListNode(0);
ListNode curr = dummy;
while (l1 != null && l2 != null) {
    if (l1.val <= l2.val) { curr.next = l1; l1 = l1.next; }
    else { curr.next = l2; l2 = l2.next; }
    curr = curr.next;
}
curr.next = (l1 != null) ? l1 : l2;
return dummy.next;
```

**E15. Linked List Cycle** (LeetCode 141)
```java
// Floyd's Cycle Detection (fast/slow pointers)
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) return true;
}
return false;
// Time: O(n)  Space: O(1)
```

**E16. Middle of Linked List** (LeetCode 876)
```java
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
return slow;
```

---

### Stack & Queue

**E17. Valid Parentheses** (LeetCode 20)
```java
Deque<Character> stack = new ArrayDeque<>();
for (char c : s.toCharArray()) {
    if (c == '(' || c == '{' || c == '[') stack.push(c);
    else {
        if (stack.isEmpty()) return false;
        char top = stack.pop();
        if (c == ')' && top != '(') return false;
        if (c == '}' && top != '{') return false;
        if (c == ']' && top != '[') return false;
    }
}
return stack.isEmpty();
```

**E18. Min Stack** (LeetCode 155)
```java
// Use two stacks: main + minStack
Deque<Integer> stack = new ArrayDeque<>();
Deque<Integer> minStack = new ArrayDeque<>();

void push(int val) {
    stack.push(val);
    minStack.push(Math.min(val, minStack.isEmpty() ? val : minStack.peek()));
}
void pop() { stack.pop(); minStack.pop(); }
int top() { return stack.peek(); }
int getMin() { return minStack.peek(); }
```

---

### Tree

**E19. Maximum Depth of Binary Tree** (LeetCode 104)
```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

**E20. Invert Binary Tree** (LeetCode 226)
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode temp = root.left;
    root.left = invertTree(root.right);
    root.right = invertTree(temp);
    return root;
}
```

**E21. Symmetric Tree** (LeetCode 101)
```java
boolean isMirror(TreeNode l, TreeNode r) {
    if (l == null && r == null) return true;
    if (l == null || r == null) return false;
    return l.val == r.val && isMirror(l.left, r.right) && isMirror(l.right, r.left);
}
```

---

### Dynamic Programming (Easy)

**E22. Climbing Stairs** (LeetCode 70)
```java
// f(n) = f(n-1) + f(n-2)  (Fibonacci!)
int a = 1, b = 1;
for (int i = 2; i <= n; i++) {
    int c = a + b;
    a = b;
    b = c;
}
return b;
// Time: O(n)  Space: O(1)
```

**E23. House Robber** (LeetCode 198)
```java
int prev = 0, curr = 0;
for (int num : nums) {
    int temp = Math.max(curr, prev + num);
    prev = curr;
    curr = temp;
}
return curr;
```

*(Easy problems E24–E30: Pascal's Triangle, Count Bits, Power of Two, Missing Number, Single Number, Ransom Note, Fizz Buzz — all solvable with basic loops/math)*

---

## 🟡 MEDIUM (30 Problems)

**M01. Group Anagrams** (LeetCode 49)
```java
Map<String, List<String>> map = new HashMap<>();
for (String s : strs) {
    char[] arr = s.toCharArray();
    Arrays.sort(arr);
    String key = new String(arr);
    map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
}
return new ArrayList<>(map.values());
// Time: O(n * k log k)  Space: O(n*k)
```

**M02. Top K Frequent Elements** (LeetCode 347)
```java
// Bucket sort approach O(n)
Map<Integer, Integer> freq = new HashMap<>();
for (int n : nums) freq.merge(n, 1, Integer::sum);

List<Integer>[] bucket = new List[nums.length + 1];
freq.forEach((num, count) -> {
    if (bucket[count] == null) bucket[count] = new ArrayList<>();
    bucket[count].add(num);
});

List<Integer> result = new ArrayList<>();
for (int i = bucket.length - 1; i >= 0 && result.size() < k; i--) {
    if (bucket[i] != null) result.addAll(bucket[i]);
}
return result.stream().mapToInt(Integer::intValue).toArray();
```

**M03. Longest Substring Without Repeating Characters** (LeetCode 3)
```java
Map<Character, Integer> map = new HashMap<>();
int max = 0, left = 0;
for (int right = 0; right < s.length(); right++) {
    char c = s.charAt(right);
    if (map.containsKey(c)) left = Math.max(left, map.get(c) + 1);
    map.put(c, right);
    max = Math.max(max, right - left + 1);
}
return max;
// Time: O(n)  Space: O(min(n,m)) where m = charset size
```

**M04. Binary Search** (LeetCode 704)
```java
int l = 0, r = nums.length - 1;
while (l <= r) {
    int mid = l + (r - l) / 2;  // avoid overflow!
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) l = mid + 1;
    else r = mid - 1;
}
return -1;
```

**M05. Product of Array Except Self** (LeetCode 238)
```java
int n = nums.length;
int[] result = new int[n];
result[0] = 1;
// Left pass
for (int i = 1; i < n; i++) result[i] = result[i-1] * nums[i-1];
// Right pass
int right = 1;
for (int i = n - 1; i >= 0; i--) {
    result[i] *= right;
    right *= nums[i];
}
return result;
// Time: O(n)  Space: O(1) excluding output
```

**M06. 3Sum** (LeetCode 15)
```java
Arrays.sort(nums);
List<List<Integer>> result = new ArrayList<>();
for (int i = 0; i < nums.length - 2; i++) {
    if (i > 0 && nums[i] == nums[i-1]) continue; // skip dups
    int l = i + 1, r = nums.length - 1;
    while (l < r) {
        int sum = nums[i] + nums[l] + nums[r];
        if (sum == 0) {
            result.add(Arrays.asList(nums[i], nums[l], nums[r]));
            while (l < r && nums[l] == nums[l+1]) l++;
            while (l < r && nums[r] == nums[r-1]) r--;
            l++; r--;
        } else if (sum < 0) l++;
        else r--;
    }
}
return result;
// Time: O(n²)  Space: O(1)
```

**M07. Minimum Window Substring** (LeetCode 76)
```java
// Sliding window with character frequency map
// Time: O(s+t)  Space: O(s+t)
// (Full implementation is ~30 lines — practice this carefully)
```

**M08. Coin Change** (LeetCode 322) — DP
```java
int[] dp = new int[amount + 1];
Arrays.fill(dp, amount + 1);
dp[0] = 0;
for (int i = 1; i <= amount; i++) {
    for (int coin : coins) {
        if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
    }
}
return dp[amount] > amount ? -1 : dp[amount];
// Time: O(amount * coins)  Space: O(amount)
```

**M09. Number of Islands** (LeetCode 200) — BFS/DFS
```java
int count = 0;
for (int i = 0; i < grid.length; i++) {
    for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == '1') {
            count++;
            dfs(grid, i, j); // flood fill
        }
    }
}
return count;

void dfs(char[][] grid, int i, int j) {
    if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] != '1') return;
    grid[i][j] = '0'; // mark visited
    dfs(grid, i+1, j); dfs(grid, i-1, j);
    dfs(grid, i, j+1); dfs(grid, i, j-1);
}
// Time: O(m*n)  Space: O(m*n) recursion stack
```

**M10. LRU Cache** (LeetCode 146)
```java
// LinkedHashMap solution
class LRUCache extends LinkedHashMap<Integer, Integer> {
    private int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // accessOrder = true
        this.capacity = capacity;
    }

    public int get(int key) { return getOrDefault(key, -1); }
    public void put(int key, int value) { super.put(key, value); }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
// Time: O(1)  Space: O(capacity)
```

*(Medium M11–M30: Rotate Image, Spiral Matrix, Jump Game, Longest Palindromic Substring, Decode Ways, Course Schedule, Word Search, Meeting Rooms II, Find Duplicate Number, K Closest Points to Origin — practice these on LeetCode)*

---

## 🔴 HARD (10 Problems)

**H01. Median of Two Sorted Arrays** (LeetCode 4)
```
Approach: Binary search on smaller array
Time: O(log(min(m,n)))  Space: O(1)
Key insight: partition both arrays such that left halves ≤ right halves
```

**H02. Trapping Rain Water** (LeetCode 42)
```java
int l = 0, r = height.length - 1;
int leftMax = 0, rightMax = 0, water = 0;
while (l < r) {
    if (height[l] < height[r]) {
        if (height[l] >= leftMax) leftMax = height[l];
        else water += leftMax - height[l];
        l++;
    } else {
        if (height[r] >= rightMax) rightMax = height[r];
        else water += rightMax - height[r];
        r--;
    }
}
return water;
// Time: O(n)  Space: O(1)
```

**H03. Word Ladder** (LeetCode 127) — BFS
```
BFS: level-by-level exploration
Time: O(M² * N) where M = word length, N = wordList size
Key: transform each character, check if in wordList
```

**H04. Serialize and Deserialize Binary Tree** (LeetCode 297)
```java
// BFS serialization
// "1,2,3,null,null,4,5"
// Deserialize: queue-based level-order reconstruction
```

**H05. Largest Rectangle in Histogram** (LeetCode 84)
```java
// Monotonic stack approach
Deque<Integer> stack = new ArrayDeque<>();
int maxArea = 0;
for (int i = 0; i <= heights.length; i++) {
    int h = (i == heights.length) ? 0 : heights[i];
    while (!stack.isEmpty() && h < heights[stack.peek()]) {
        int height = heights[stack.pop()];
        int width = stack.isEmpty() ? i : i - stack.peek() - 1;
        maxArea = Math.max(maxArea, height * width);
    }
    stack.push(i);
}
return maxArea;
// Time: O(n)  Space: O(n)
```

**H06. Edit Distance** (LeetCode 72) — DP
```java
// dp[i][j] = min operations to convert word1[0..i] to word2[0..j]
int m = word1.length(), n = word2.length();
int[][] dp = new int[m+1][n+1];
for (int i = 0; i <= m; i++) dp[i][0] = i;
for (int j = 0; j <= n; j++) dp[0][j] = j;
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        if (word1.charAt(i-1) == word2.charAt(j-1)) dp[i][j] = dp[i-1][j-1];
        else dp[i][j] = 1 + Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
    }
}
return dp[m][n];
// Time: O(m*n)  Space: O(m*n)
```

**H07. Merge K Sorted Lists** (LeetCode 23)
```java
// PriorityQueue (min heap)
PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
for (ListNode list : lists) if (list != null) pq.offer(list);
ListNode dummy = new ListNode(0), curr = dummy;
while (!pq.isEmpty()) {
    curr.next = pq.poll();
    curr = curr.next;
    if (curr.next != null) pq.offer(curr.next);
}
return dummy.next;
// Time: O(N log k) where N = total nodes, k = lists
```

**H08. Regular Expression Matching** (LeetCode 10) — DP
```
dp[i][j] = s[0..i-1] matches p[0..j-1]
Time: O(m*n)
```

**H09. Sliding Window Maximum** (LeetCode 239)
```java
// Monotonic deque
Deque<Integer> dq = new ArrayDeque<>(); // stores indices
int[] result = new int[nums.length - k + 1];
for (int i = 0; i < nums.length; i++) {
    while (!dq.isEmpty() && dq.peek() < i - k + 1) dq.poll();
    while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
    dq.offer(i);
    if (i >= k - 1) result[i - k + 1] = nums[dq.peek()];
}
return result;
// Time: O(n)  Space: O(k)
```

**H10. Find Median from Data Stream** (LeetCode 295)
```java
// Two heaps: maxHeap (lower half) + minHeap (upper half)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

void addNum(int num) {
    maxHeap.offer(num);
    minHeap.offer(maxHeap.poll());
    if (maxHeap.size() < minHeap.size()) maxHeap.offer(minHeap.poll());
}

double findMedian() {
    if (maxHeap.size() > minHeap.size()) return maxHeap.peek();
    return (maxHeap.peek() + minHeap.peek()) / 2.0;
}
// Time: O(log n) per add  Space: O(n)
```
