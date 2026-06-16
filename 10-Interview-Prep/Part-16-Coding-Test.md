# PART 16 - CODING TEST

> **Format**: Problem · Expected Solution · Time/Space Complexity · Follow-up
> **Language**: Java

---

## ⭐ EASY QUESTIONS (30)

### E1. Two Sum
**Problem**: Given an array of integers and a target, return indices of two numbers that add up to target.
```java
// Input: nums = [2, 7, 11, 15], target = 9
// Output: [0, 1]

public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        map.put(nums[i], i);
    }
    return new int[]{};
}
// Time: O(n) | Space: O(n)
// Follow-up: What if array is sorted? → Two pointers O(1) space
```

---

### E2. Reverse a String
```java
public String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
}

// Manual approach:
public char[] reverseChars(char[] s) {
    int left = 0, right = s.length - 1;
    while (left < right) {
        char temp = s[left];
        s[left++] = s[right];
        s[right--] = temp;
    }
    return s;
}
// Time: O(n) | Space: O(1) for in-place
```

---

### E3. Valid Palindrome
```java
public boolean isPalindrome(String s) {
    String clean = s.toLowerCase().replaceAll("[^a-z0-9]", "");
    int left = 0, right = clean.length() - 1;
    while (left < right) {
        if (clean.charAt(left++) != clean.charAt(right--)) return false;
    }
    return true;
}
// Time: O(n) | Space: O(n) for cleaned string
```

---

### E4. Find Maximum in Array
```java
public int findMax(int[] nums) {
    int max = Integer.MIN_VALUE;
    for (int num : nums) max = Math.max(max, num);
    return max;
}
// Java 8: return Arrays.stream(nums).max().getAsInt();
// Time: O(n) | Space: O(1)
```

---

### E5. Count Occurrences of Character
```java
public int countChar(String s, char c) {
    return (int) s.chars().filter(ch -> ch == c).count();
}
// Time: O(n) | Space: O(1)
```

---

### E6. Check Anagram
```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] count = new int[26];
    for (char c : s.toCharArray()) count[c - 'a']++;
    for (char c : t.toCharArray()) {
        if (--count[c - 'a'] < 0) return false;
    }
    return true;
}
// Time: O(n) | Space: O(1) — fixed 26-char array
```

---

### E7. Remove Duplicates from Sorted Array
```java
public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    int k = 1;
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] != nums[i - 1]) {
            nums[k++] = nums[i];
        }
    }
    return k;
}
// Time: O(n) | Space: O(1) — in-place
```

---

### E8. Reverse a Linked List
```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
// Time: O(n) | Space: O(1)
// Follow-up: Recursive solution
```

---

### E9. Valid Parentheses
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
// Time: O(n) | Space: O(n)
```

---

### E10. FizzBuzz
```java
public List<String> fizzBuzz(int n) {
    List<String> result = new ArrayList<>();
    for (int i = 1; i <= n; i++) {
        if (i % 15 == 0) result.add("FizzBuzz");
        else if (i % 3 == 0) result.add("Fizz");
        else if (i % 5 == 0) result.add("Buzz");
        else result.add(String.valueOf(i));
    }
    return result;
}
// Time: O(n) | Space: O(n)
```

---

### E11. Fibonacci Number
```java
// Memoized
public int fib(int n) {
    if (n <= 1) return n;
    int[] dp = new int[n + 1];
    dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i-1] + dp[i-2];
    }
    return dp[n];
}
// Time: O(n) | Space: O(n) → optimize to O(1) with two variables
```

---

### E12. Contains Duplicate
```java
public boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int num : nums) {
        if (!seen.add(num)) return true;
    }
    return false;
}
// Time: O(n) | Space: O(n)
```

---

### E13. Merge Two Sorted Arrays
```java
public int[] merge(int[] a, int[] b) {
    int[] result = new int[a.length + b.length];
    int i = 0, j = 0, k = 0;
    while (i < a.length && j < b.length) {
        result[k++] = a[i] <= b[j] ? a[i++] : b[j++];
    }
    while (i < a.length) result[k++] = a[i++];
    while (j < b.length) result[k++] = b[j++];
    return result;
}
// Time: O(m+n) | Space: O(m+n)
```

---

### E14. Maximum Subarray Sum (Kadane's Algorithm)
```java
public int maxSubArray(int[] nums) {
    int maxSum = nums[0], currentSum = nums[0];
    for (int i = 1; i < nums.length; i++) {
        currentSum = Math.max(nums[i], currentSum + nums[i]);
        maxSum = Math.max(maxSum, currentSum);
    }
    return maxSum;
}
// Time: O(n) | Space: O(1)
// Follow-up: Return the subarray itself
```

---

### E15. Binary Search
```java
public int binarySearch(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2; // Avoid overflow!
        if (nums[mid] == target) return mid;
        else if (nums[mid] < target) left = mid + 1;
        else right = mid - 1;
    }
    return -1;
}
// Time: O(log n) | Space: O(1)
```

---

### E16. Invert Binary Tree
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);
    root.left = right;
    root.right = left;
    return root;
}
// Time: O(n) | Space: O(h) height of tree
```

---

### E17. Maximum Depth of Binary Tree
```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
// Time: O(n) | Space: O(h)
```

---

### E18. Climbing Stairs
```java
// Each step: climb 1 or 2 stairs. Ways to reach n?
public int climbStairs(int n) {
    if (n <= 2) return n;
    int a = 1, b = 2;
    for (int i = 3; i <= n; i++) {
        int c = a + b;
        a = b;
        b = c;
    }
    return b;
}
// Time: O(n) | Space: O(1)
// Same as Fibonacci!
```

---

### E19. Move Zeroes
```java
public void moveZeroes(int[] nums) {
    int insertPos = 0;
    for (int num : nums) {
        if (num != 0) nums[insertPos++] = num;
    }
    while (insertPos < nums.length) nums[insertPos++] = 0;
}
// Time: O(n) | Space: O(1)
```

---

### E20. Single Number (XOR)
```java
// Every element appears twice except one
public int singleNumber(int[] nums) {
    int result = 0;
    for (int num : nums) result ^= num; // XOR cancels duplicates!
    return result;
}
// Time: O(n) | Space: O(1)
```

---

### E21-30. Additional Easy Problems
```java
// E21: Roman to Integer
// E22: Longest Common Prefix
// E23: Count Vowels in String
// E24: Check Power of Two (n & (n-1) == 0)
// E25: Sum of Digits
// E26: Factorial (iterative and recursive)
// E27: Check Balanced Parentheses
// E28: First Non-Repeating Character
// E29: Check Sorted Array
// E30: Find Missing Number in 1..n (use sum formula: n*(n+1)/2 - sum)

// E30 - Find Missing Number
public int missingNumber(int[] nums) {
    int n = nums.length;
    int expectedSum = n * (n + 1) / 2;
    int actualSum = 0;
    for (int num : nums) actualSum += num;
    return expectedSum - actualSum;
}
```

---

## ⭐⭐ MEDIUM QUESTIONS (30)

### M1. Longest Substring Without Repeating Characters (Sliding Window)
```java
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> charIndex = new HashMap<>();
    int maxLen = 0, left = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (charIndex.containsKey(c) && charIndex.get(c) >= left) {
            left = charIndex.get(c) + 1; // Move left past duplicate
        }
        charIndex.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
// Input: "abcabcbb" → Output: 3 ("abc")
// Time: O(n) | Space: O(min(m, n)) where m = charset size
```

---

### M2. LRU Cache
```java
class LRUCache {
    private final int capacity;
    private final Map<Integer, Integer> cache;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        // LinkedHashMap maintains insertion order; accessOrder=true for LRU
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }
    
    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }
    
    public void put(int key, int value) {
        cache.put(key, value);
    }
}
// Time: O(1) for get and put | Space: O(capacity)
```

---

### M3. Group Anagrams
```java
public List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> map = new HashMap<>();
    for (String s : strs) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(map.values());
}
// Input: ["eat","tea","tan","ate","nat","bat"]
// Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
// Time: O(n * k log k) | Space: O(n * k) where k = max string length
```

---

### M4. Top K Frequent Elements
```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) freq.merge(num, 1, Integer::sum);
    
    // Min-heap of size k
    PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(freq::get));
    for (int num : freq.keySet()) {
        pq.offer(num);
        if (pq.size() > k) pq.poll(); // Remove least frequent
    }
    
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) result[i] = pq.poll();
    return result;
}
// Time: O(n log k) | Space: O(n)
// Follow-up: Bucket sort approach O(n)
```

---

### M5. Binary Tree Level Order Traversal (BFS)
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
// Time: O(n) | Space: O(n)
```

---

### M6. Number of Islands (DFS/BFS on Grid)
```java
public int numIslands(char[][] grid) {
    int count = 0;
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == '1') {
                dfs(grid, i, j);
                count++;
            }
        }
    }
    return count;
}

private void dfs(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
    grid[r][c] = '0'; // Mark visited
    dfs(grid, r+1, c);
    dfs(grid, r-1, c);
    dfs(grid, r, c+1);
    dfs(grid, r, c-1);
}
// Time: O(m*n) | Space: O(m*n) for recursion stack
```

---

### M7. Validate Binary Search Tree
```java
public boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

private boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) &&
           validate(node.right, node.val, max);
}
// Time: O(n) | Space: O(h)
```

---

### M8. Product of Array Except Self
```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    
    // Left pass: result[i] = product of all elements left of i
    result[0] = 1;
    for (int i = 1; i < n; i++) result[i] = result[i-1] * nums[i-1];
    
    // Right pass: multiply by product of all elements right of i
    int rightProduct = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= rightProduct;
        rightProduct *= nums[i];
    }
    return result;
}
// Time: O(n) | Space: O(1) (excluding output array)
// No division allowed!
```

---

### M9. Find All Duplicates in Array
```java
public List<Integer> findDuplicates(int[] nums) {
    List<Integer> result = new ArrayList<>();
    for (int num : nums) {
        int idx = Math.abs(num) - 1;
        if (nums[idx] < 0) {
            result.add(idx + 1); // Already negated = duplicate!
        }
        nums[idx] = -nums[idx]; // Mark as visited
    }
    return result;
}
// Time: O(n) | Space: O(1) - modifies input
```

---

### M10. Merge Intervals
```java
public int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
    List<int[]> merged = new ArrayList<>();
    
    for (int[] interval : intervals) {
        if (merged.isEmpty() || merged.get(merged.size()-1)[1] < interval[0]) {
            merged.add(interval);
        } else {
            merged.get(merged.size()-1)[1] = Math.max(
                merged.get(merged.size()-1)[1], interval[1]);
        }
    }
    return merged.toArray(new int[0][]);
}
// Input: [[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]
// Time: O(n log n) | Space: O(n)
```

---

### M11. 3Sum
```java
public List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    
    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i-1]) continue; // Skip duplicates
        int left = i + 1, right = nums.length - 1;
        
        while (left < right) {
            int sum = nums[i] + nums[left] + nums[right];
            if (sum == 0) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                while (left < right && nums[left] == nums[left+1]) left++;
                while (left < right && nums[right] == nums[right-1]) right--;
                left++; right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }
    return result;
}
// Time: O(n²) | Space: O(1)
```

---

### M12. Longest Palindromic Substring
```java
public String longestPalindrome(String s) {
    int start = 0, maxLen = 1;
    for (int i = 0; i < s.length(); i++) {
        // Odd length palindromes
        int len1 = expandAroundCenter(s, i, i);
        // Even length palindromes
        int len2 = expandAroundCenter(s, i, i + 1);
        int len = Math.max(len1, len2);
        if (len > maxLen) {
            maxLen = len;
            start = i - (len - 1) / 2;
        }
    }
    return s.substring(start, start + maxLen);
}

private int expandAroundCenter(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--; right++;
    }
    return right - left - 1;
}
// Time: O(n²) | Space: O(1)
```

---

### M13. Coin Change (DP)
```java
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1); // Initialize with impossible value
    dp[0] = 0;
    
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
// Time: O(amount * coins.length) | Space: O(amount)
```

---

### M14. Detect Cycle in Linked List (Floyd's Algorithm)
```java
public boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
// Time: O(n) | Space: O(1)
// Follow-up: Find cycle entry point
```

---

### M15-30. Additional Medium Problems
```
M15: Rotated Sorted Array Search
M16: Find Peak Element  
M17: Spiral Matrix
M18: Jump Game (Greedy)
M19: Word Search (Backtracking DFS)
M20: Subsets (Power Set)
M21: Permutations (Backtracking)
M22: Combination Sum
M23: House Robber (DP)
M24: Unique Paths (DP)
M25: Min Stack (O(1) getMin)
M26: Queue Using Two Stacks
M27: Rotate Image (Matrix)
M28: Linked List Palindrome
M29: Find First and Last Position (Binary Search)
M30: Course Schedule (Topological Sort)
```

---

## ⭐⭐⭐ HARD QUESTIONS (10)

### H1. Merge K Sorted Lists
```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
    for (ListNode list : lists) {
        if (list != null) pq.offer(list);
    }
    
    ListNode dummy = new ListNode(0), curr = dummy;
    while (!pq.isEmpty()) {
        ListNode node = pq.poll();
        curr.next = node;
        curr = curr.next;
        if (node.next != null) pq.offer(node.next);
    }
    return dummy.next;
}
// Time: O(n log k) | Space: O(k)
```

---

### H2. Trapping Rain Water
```java
public int trap(int[] height) {
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0, water = 0;
    
    while (left < right) {
        if (height[left] < height[right]) {
            if (height[left] >= leftMax) leftMax = height[left];
            else water += leftMax - height[left];
            left++;
        } else {
            if (height[right] >= rightMax) rightMax = height[right];
            else water += rightMax - height[right];
            right--;
        }
    }
    return water;
}
// Time: O(n) | Space: O(1)
```

---

### H3. Word Ladder (BFS - Shortest Path)
```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> wordSet = new HashSet<>(wordList);
    if (!wordSet.contains(endWord)) return 0;
    
    Queue<String> queue = new LinkedList<>();
    queue.offer(beginWord);
    int steps = 1;
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            String word = queue.poll();
            char[] chars = word.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char original = chars[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    chars[j] = c;
                    String next = new String(chars);
                    if (next.equals(endWord)) return steps + 1;
                    if (wordSet.remove(next)) queue.offer(next);
                }
                chars[j] = original;
            }
        }
        steps++;
    }
    return 0;
}
// Time: O(M² × N) | Space: O(M² × N) where M = word length, N = wordList size
```

---

### H4. Serialize and Deserialize Binary Tree
```java
public class Codec {
    public String serialize(TreeNode root) {
        if (root == null) return "#";
        return root.val + "," + serialize(root.left) + "," + serialize(root.right);
    }

    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserialize(queue);
    }
    
    private TreeNode deserialize(Queue<String> queue) {
        String val = queue.poll();
        if ("#".equals(val)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserialize(queue);
        node.right = deserialize(queue);
        return node;
    }
}
// Time: O(n) | Space: O(n)
```

---

### H5. Sliding Window Maximum
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>(); // Stores indices, monotonic decreasing
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // Remove elements outside window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) deque.pollFirst();
        // Remove smaller elements (they'll never be max)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast();
        
        deque.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
    }
    return result;
}
// Time: O(n) | Space: O(k)
```

---

### H6. Median of Two Sorted Arrays
```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);
    
    int m = nums1.length, n = nums2.length;
    int left = 0, right = m;
    
    while (left <= right) {
        int partitionX = (left + right) / 2;
        int partitionY = (m + n + 1) / 2 - partitionX;
        
        int maxX = partitionX == 0 ? Integer.MIN_VALUE : nums1[partitionX - 1];
        int minX = partitionX == m ? Integer.MAX_VALUE : nums1[partitionX];
        int maxY = partitionY == 0 ? Integer.MIN_VALUE : nums2[partitionY - 1];
        int minY = partitionY == n ? Integer.MAX_VALUE : nums2[partitionY];
        
        if (maxX <= minY && maxY <= minX) {
            if ((m + n) % 2 == 0) return (Math.max(maxX, maxY) + Math.min(minX, minY)) / 2.0;
            else return Math.max(maxX, maxY);
        } else if (maxX > minY) {
            right = partitionX - 1;
        } else {
            left = partitionX + 1;
        }
    }
    throw new IllegalArgumentException();
}
// Time: O(log(min(m,n))) | Space: O(1)
```

---

### H7. Longest Increasing Subsequence (O(n log n))
```java
public int lengthOfLIS(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    for (int num : nums) {
        int pos = Collections.binarySearch(tails, num);
        if (pos < 0) pos = -(pos + 1);
        if (pos == tails.size()) tails.add(num);
        else tails.set(pos, num);
    }
    return tails.size();
}
// Time: O(n log n) | Space: O(n)
```

---

### H8. Find All Shortest Paths in Graph (Dijkstra)
```java
public int[] dijkstra(int[][] graph, int source) {
    int n = graph.length;
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;
    
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{source, 0});
    
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int node = curr[0], d = curr[1];
        if (d > dist[node]) continue; // Stale entry
        
        for (int neighbor = 0; neighbor < n; neighbor++) {
            if (graph[node][neighbor] > 0) {
                int newDist = dist[node] + graph[node][neighbor];
                if (newDist < dist[neighbor]) {
                    dist[neighbor] = newDist;
                    pq.offer(new int[]{neighbor, newDist});
                }
            }
        }
    }
    return dist;
}
// Time: O((V + E) log V) | Space: O(V)
```

---

### H9. Edit Distance (Levenshtein)
```java
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i-1) == word2.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i-1][j-1],
                                Math.min(dp[i-1][j], dp[i][j-1]));
            }
        }
    }
    return dp[m][n];
}
// Time: O(m*n) | Space: O(m*n) → optimize to O(n) with rolling array
```

---

### H10. Regular Expression Matching
```java
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;
    
    // Handle patterns like a*, a*b*, a*b*c*
    for (int j = 1; j <= n; j++) {
        if (p.charAt(j-1) == '*' && j >= 2) {
            dp[0][j] = dp[0][j-2];
        }
    }
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            char sc = s.charAt(i-1), pc = p.charAt(j-1);
            if (pc == '*') {
                dp[i][j] = dp[i][j-2]; // Zero occurrence of x*
                if (p.charAt(j-2) == '.' || p.charAt(j-2) == sc) {
                    dp[i][j] = dp[i][j] || dp[i-1][j]; // One+ occurrence
                }
            } else if (pc == '.' || pc == sc) {
                dp[i][j] = dp[i-1][j-1];
            }
        }
    }
    return dp[m][n];
}
// Time: O(m*n) | Space: O(m*n)
```

---

*Next: [Part 17 - Mock Interview](./Part-17-Mock-Interview-Guide.md)*
