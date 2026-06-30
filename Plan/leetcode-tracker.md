# 📋 LeetCode Master Tracker

> **Target:** 100 bài trong 8 tuần | **Focus:** NeetCode 150 pattern-based

---

## 📊 Summary Dashboard

| Phase | Target | Easy | Medium | Hard | Total |
|:------|:-------|:-----|:-------|:-----|:------|
| Phase 1 (W1-2) | 30 | | | | /30 |
| Phase 2 (W3-5) | 45 | | | | /45 |
| Phase 3 (W6-7) | 15 (maintenance) | | | | /15 |
| Phase 4 (W8) | 10 (maintenance) | | | | /10 |
| **TOTAL** | **100** | | | | **/100** |

---

## 🏷️ Pattern Mastery Checklist

Rate each pattern: 🔴 Weak → 🟡 OK → 🟢 Strong

| # | Pattern | Mastery | Key Insight | Problems Solved |
|:--|:--------|:--------|:------------|:----------------|
| 1 | Two Pointers | 🔴 | | /5 |
| 2 | Sliding Window | 🔴 | | /5 |
| 3 | HashMap/HashSet | 🔴 | | /5 |
| 4 | Binary Search | 🔴 | | /5 |
| 5 | Linked List | 🔴 | | /5 |
| 6 | Stack (Monotonic) | 🔴 | | /5 |
| 7 | Binary Tree DFS | 🔴 | | /8 |
| 8 | Binary Tree BFS | 🔴 | | /4 |
| 9 | BST | 🔴 | | /3 |
| 10 | Trie | 🔴 | | /3 |
| 11 | Heap / Priority Queue | 🔴 | | /5 |
| 12 | Graph BFS/DFS | 🔴 | | /6 |
| 13 | Topological Sort | 🔴 | | /3 |
| 14 | Union Find | 🔴 | | /3 |
| 15 | 1D Dynamic Programming | 🔴 | | /8 |
| 16 | 2D Dynamic Programming | 🔴 | | /5 |
| 17 | Greedy | 🔴 | | /5 |
| 18 | Backtracking | 🔴 | | /5 |
| 19 | Intervals | 🔴 | | /4 |
| 20 | Bit Manipulation | 🔴 | | /3 |
| 21 | Math & Geometry | 🔴 | | /3 |

---

## 📝 All Problems List

### 01. Arrays & Hashing ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/01-arrays-hashing/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 1 | Two Sum | [LC #1](https://leetcode.com/problems/two-sum/) | Easy | ✅ | | | HashMap complement |
| 2 | Contains Duplicate | [LC #217](https://leetcode.com/problems/contains-duplicate/) | Easy | ✅ | | | HashSet |
| 3 | Valid Anagram | [LC #242](https://leetcode.com/problems/valid-anagram/) | Easy | ✅ | | | Char count array |
| 4 | Group Anagrams | [LC #49](https://leetcode.com/problems/group-anagrams/) | Medium | ⬜ | | | Sorted key → HashMap |
| 5 | Top K Frequent Elements | [LC #347](https://leetcode.com/problems/top-k-frequent-elements/) | Medium | ⬜ | | | Bucket sort |
| 6 | Product of Array Except Self | [LC #238](https://leetcode.com/problems/product-of-array-except-self/) | Medium | ⬜ | | | Prefix * Suffix |
| 7 | Longest Consecutive Sequence | [LC #128](https://leetcode.com/problems/longest-consecutive-sequence/) | Medium | ⬜ | | | HashSet + check start |
| 8 | Encode and Decode Strings | [LC #271](https://leetcode.com/problems/encode-and-decode-strings/) | Medium | ⬜ | | | Length prefix |

### 02. Two Pointers ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/02-two-pointers/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 9 | Valid Palindrome | [LC #125](https://leetcode.com/problems/valid-palindrome/) | Easy | ⬜ | | | Two pointers, isAlphaNum |
| 10 | Two Sum II | [LC #167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium | ⬜ | | | Sorted → shrink window |
| 11 | 3Sum | [LC #15](https://leetcode.com/problems/3sum/) | Medium | ⬜ | | | Sort + 2 pointers, skip dups |
| 12 | Container With Most Water | [LC #11](https://leetcode.com/problems/container-with-most-water/) | Medium | ⬜ | | | Move shorter side |
| 13 | Trapping Rain Water | [LC #42](https://leetcode.com/problems/trapping-rain-water/) | Hard | ⬜ | | | Two pointers / stack |

### 03. Sliding Window ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/05-sliding-window/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 14 | Best Time Buy/Sell Stock | [LC #121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy | ⬜ | | | Track min, calc profit |
| 15 | Longest Substr Without Repeat | [LC #3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium | ⬜ | | | HashSet expand/shrink |
| 16 | Longest Repeating Char Replace | [LC #424](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium | ⬜ | | | Window - maxFreq ≤ k |
| 17 | Minimum Window Substring | [LC #76](https://leetcode.com/problems/minimum-window-substring/) | Hard | ⬜ | | | Two maps + formed count |

### 04. Stack ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/03-stack/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 18 | Valid Parentheses | [LC #20](https://leetcode.com/problems/valid-parentheses/) | Easy | ⬜ | | | Map close→open |
| 19 | Min Stack | [LC #155](https://leetcode.com/problems/min-stack/) | Medium | ⬜ | | | Auxiliary min stack |
| 20 | Daily Temperatures | [LC #739](https://leetcode.com/problems/daily-temperatures/) | Medium | ⬜ | | | Monotonic decreasing stack |
| 21 | Largest Rectangle in Histogram | [LC #84](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard | ⬜ | | | Monotonic stack |

### 05. Binary Search ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/04-binary-search/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 22 | Binary Search | [LC #704](https://leetcode.com/problems/binary-search/) | Easy | ⬜ | | | Template: lo ≤ hi |
| 23 | Search a 2D Matrix | [LC #74](https://leetcode.com/problems/search-a-2d-matrix/) | Medium | ⬜ | | | Flatten to 1D BS |
| 24 | Koko Eating Bananas | [LC #875](https://leetcode.com/problems/koko-eating-bananas/) | Medium | ⬜ | | | BS on answer |
| 25 | Find Min in Rotated Array | [LC #153](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Medium | ⬜ | | | BS compare with right |
| 26 | Search in Rotated Array | [LC #33](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium | ⬜ | | | Find sorted half first |
| 27 | Median of Two Sorted Arrays | [LC #4](https://leetcode.com/problems/median-of-two-sorted-arrays/) | Hard | ⬜ | | | BS on shorter array |

### 06. Linked List ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/06-linked-list/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 28 | Reverse Linked List | [LC #206](https://leetcode.com/problems/reverse-linked-list/) | Easy | ⬜ | | | prev, curr, next |
| 29 | Merge Two Sorted Lists | [LC #21](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy | ⬜ | | | Dummy head |
| 30 | Linked List Cycle | [LC #141](https://leetcode.com/problems/linked-list-cycle/) | Easy | ⬜ | | | Floyd's fast/slow |
| 31 | Remove Nth From End | [LC #19](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | Medium | ⬜ | | | Two pointers gap n |
| 32 | Reorder List | [LC #143](https://leetcode.com/problems/reorder-list/) | Medium | ⬜ | | | Find mid → reverse → merge |
| 33 | Merge K Sorted Lists | [LC #23](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | ⬜ | | | Min heap / divide conquer |

### 07. Trees ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/07-trees/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 34 | Invert Binary Tree | [LC #226](https://leetcode.com/problems/invert-binary-tree/) | Easy | ⬜ | | | Swap left/right DFS |
| 35 | Maximum Depth | [LC #104](https://leetcode.com/problems/maximum-depth-of-binary-tree/) | Easy | ⬜ | | | DFS return 1+max |
| 36 | Same Tree | [LC #100](https://leetcode.com/problems/same-tree/) | Easy | ⬜ | | | DFS compare both |
| 37 | Subtree of Another Tree | [LC #572](https://leetcode.com/problems/subtree-of-another-tree/) | Easy | ⬜ | | | isSame at each node |
| 38 | Level Order Traversal | [LC #102](https://leetcode.com/problems/binary-tree-level-order-traversal/) | Medium | ⬜ | | | BFS with queue.size() |
| 39 | Validate BST | [LC #98](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | ⬜ | | | Pass min/max range |
| 40 | Kth Smallest in BST | [LC #230](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | ⬜ | | | Inorder traversal |
| 41 | Lowest Common Ancestor | [LC #236](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/) | Medium | ⬜ | | | DFS split point |
| 42 | Binary Tree Max Path Sum | [LC #124](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | Hard | ⬜ | | | DFS, update global max |

### 08. Tries ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/08-tries/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 43 | Implement Trie | [LC #208](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium | ⬜ | | | children[26], isEnd |
| 44 | Word Search II | [LC #212](https://leetcode.com/problems/word-search-ii/) | Hard | ⬜ | | | Trie + DFS backtrack |

### 09. Heap / Priority Queue ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/10-heap-priority-queue/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 45 | Kth Largest in Stream | [LC #703](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | Easy | ⬜ | | | Min heap of size k |
| 46 | Last Stone Weight | [LC #1046](https://leetcode.com/problems/last-stone-weight/) | Easy | ⬜ | | | Max heap simulation |
| 47 | K Closest Points | [LC #973](https://leetcode.com/problems/k-closest-points-to-origin/) | Medium | ⬜ | | | Max heap of size k |
| 48 | Task Scheduler | [LC #621](https://leetcode.com/problems/task-scheduler/) | Medium | ⬜ | | | Greedy + PQ |
| 49 | Find Median from Data Stream | [LC #295](https://leetcode.com/problems/find-median-from-data-stream/) | Hard | ⬜ | | | Two heaps |

### 10. Backtracking ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/09-backtracking/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 50 | Subsets | [LC #78](https://leetcode.com/problems/subsets/) | Medium | ⬜ | | | Include/exclude at each |
| 51 | Combination Sum | [LC #39](https://leetcode.com/problems/combination-sum/) | Medium | ⬜ | | | Reuse allowed, sort prune |
| 52 | Permutations | [LC #46](https://leetcode.com/problems/permutations/) | Medium | ⬜ | | | Swap or used[] array |
| 53 | Word Search | [LC #79](https://leetcode.com/problems/word-search/) | Medium | ⬜ | | | DFS grid backtrack |
| 54 | N-Queens | [LC #51](https://leetcode.com/problems/n-queens/) | Hard | ⬜ | | | Col/diag/anti-diag sets |

### 11. Graphs ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/11-graphs/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 55 | Number of Islands | [LC #200](https://leetcode.com/problems/number-of-islands/) | Medium | ⬜ | | | DFS/BFS flood fill |
| 56 | Clone Graph | [LC #133](https://leetcode.com/problems/clone-graph/) | Medium | ⬜ | | | BFS + HashMap old→new |
| 57 | Course Schedule | [LC #207](https://leetcode.com/problems/course-schedule/) | Medium | ⬜ | | | Topo sort, cycle detect |
| 58 | Course Schedule II | [LC #210](https://leetcode.com/problems/course-schedule-ii/) | Medium | ⬜ | | | Topo sort with order |
| 59 | Pacific Atlantic Water Flow | [LC #417](https://leetcode.com/problems/pacific-atlantic-water-flow/) | Medium | ⬜ | | | Reverse DFS from edges |
| 60 | Graph Valid Tree | [LC #261](https://leetcode.com/problems/graph-valid-tree/) | Medium | ⬜ | | | n-1 edges + connected |
| 61 | Number of Connected Components | [LC #323](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | Medium | ⬜ | | | Union Find |

### 12. 1D Dynamic Programming ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/12-one-d-dp/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 62 | Climbing Stairs | [LC #70](https://leetcode.com/problems/climbing-stairs/) | Easy | ⬜ | | | dp[i] = dp[i-1]+dp[i-2] |
| 63 | House Robber | [LC #198](https://leetcode.com/problems/house-robber/) | Medium | ⬜ | | | max(skip, rob+prev2) |
| 64 | House Robber II | [LC #213](https://leetcode.com/problems/house-robber-ii/) | Medium | ⬜ | | | Circular: two passes |
| 65 | Longest Palindromic Substring | [LC #5](https://leetcode.com/problems/longest-palindromic-substring/) | Medium | ⬜ | | | Expand from center |
| 66 | Coin Change | [LC #322](https://leetcode.com/problems/coin-change/) | Medium | ⬜ | | | BFS or bottom-up DP |
| 67 | Maximum Product Subarray | [LC #152](https://leetcode.com/problems/maximum-product-subarray/) | Medium | ⬜ | | | Track min & max |
| 68 | Word Break | [LC #139](https://leetcode.com/problems/word-break/) | Medium | ⬜ | | | DP + HashSet dict |
| 69 | Longest Increasing Subsequence | [LC #300](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium | ⬜ | | | DP O(n²) or BS O(nlogn) |
| 70 | Decode Ways | [LC #91](https://leetcode.com/problems/decode-ways/) | Medium | ⬜ | | | DP with 1-char & 2-char |

### 13. 2D Dynamic Programming ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/16-two-d-dp/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 71 | Unique Paths | [LC #62](https://leetcode.com/problems/unique-paths/) | Medium | ⬜ | | | dp[i][j] = up + left |
| 72 | Longest Common Subsequence | [LC #1143](https://leetcode.com/problems/longest-common-subsequence/) | Medium | ⬜ | | | Match → diag+1 |
| 73 | Target Sum | [LC #494](https://leetcode.com/problems/target-sum/) | Medium | ⬜ | | | 0/1 knapsack variant |
| 74 | Edit Distance | [LC #72](https://leetcode.com/problems/edit-distance/) | Medium | ⬜ | | | Insert/delete/replace |
| 75 | Partition Equal Subset Sum | [LC #416](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium | ⬜ | | | 0/1 knapsack, sum/2 |

### 14. Greedy ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/14-greedy/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 76 | Maximum Subarray | [LC #53](https://leetcode.com/problems/maximum-subarray/) | Medium | ⬜ | | | Kadane's algorithm |
| 77 | Jump Game | [LC #55](https://leetcode.com/problems/jump-game/) | Medium | ⬜ | | | Track farthest reach |
| 78 | Jump Game II | [LC #45](https://leetcode.com/problems/jump-game-ii/) | Medium | ⬜ | | | BFS levels = jumps |
| 79 | Gas Station | [LC #134](https://leetcode.com/problems/gas-station/) | Medium | ⬜ | | | Reset start when tank<0 |
| 80 | Hand of Straights | [LC #846](https://leetcode.com/problems/hand-of-straights/) | Medium | ⬜ | | | TreeMap + greedy groups |

### 15. Intervals ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/13-intervals/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 81 | Merge Intervals | [LC #56](https://leetcode.com/problems/merge-intervals/) | Medium | ⬜ | | | Sort by start, extend end |
| 82 | Insert Interval | [LC #57](https://leetcode.com/problems/insert-interval/) | Medium | ⬜ | | | 3 phases: before/overlap/after |
| 83 | Non-overlapping Intervals | [LC #435](https://leetcode.com/problems/non-overlapping-intervals/) | Medium | ⬜ | | | Sort by end, count overlaps |
| 84 | Meeting Rooms II | [LC #253](https://leetcode.com/problems/meeting-rooms-ii/) | Medium | ⬜ | | | Min heap for end times |

### 16. Advanced Graphs ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/15-advanced-graphs/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 85 | Cheapest Flights K Stops | [LC #787](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | Medium | ⬜ | | | Bellman-Ford K iterations |
| 86 | Network Delay Time | [LC #743](https://leetcode.com/problems/network-delay-time/) | Medium | ⬜ | | | Dijkstra's algorithm |
| 87 | Alien Dictionary | [LC #269](https://leetcode.com/problems/alien-dictionary/) | Hard | ⬜ | | | Build graph + topo sort |

### 17. Bit Manipulation ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/17-bit-manipulation/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 88 | Single Number | [LC #136](https://leetcode.com/problems/single-number/) | Easy | ⬜ | | | XOR all |
| 89 | Number of 1 Bits | [LC #191](https://leetcode.com/problems/number-of-1-bits/) | Easy | ⬜ | | | n & (n-1) clears bit |
| 90 | Counting Bits | [LC #338](https://leetcode.com/problems/counting-bits/) | Easy | ⬜ | | | dp[i] = dp[i>>1] + (i&1) |
| 91 | Reverse Bits | [LC #190](https://leetcode.com/problems/reverse-bits/) | Easy | ⬜ | | | Shift and OR |
| 92 | Missing Number | [LC #268](https://leetcode.com/problems/missing-number/) | Easy | ⬜ | | | XOR or math sum |

### 18. Math & Geometry ([Detailed Explanations](file:///d:/WorkSpace/Document/Improve-Knowledge/01-DSA/18-math-geometry/solutions_explained.md))
| # | Problem | Link | Difficulty | Status | Solve Time | Attempts | Key Insight |
|:--|:--------|:-----|:-----------|:-------|:-----------|:---------|:------------|
| 93 | Rotate Image | [LC #48](https://leetcode.com/problems/rotate-image/) | Medium | ⬜ | | | Transpose + reverse rows |
| 94 | Spiral Matrix | [LC #54](https://leetcode.com/problems/spiral-matrix/) | Medium | ⬜ | | | 4 boundaries shrink |
| 95 | Set Matrix Zeroes | [LC #73](https://leetcode.com/problems/set-matrix-zeroes/) | Medium | ⬜ | | | Use first row/col as marker |

---

## 📈 Progress Chart

```
Week 1:  [░░░░░░░░░░░░░░░░░░░░] 0/15
Week 2:  [░░░░░░░░░░░░░░░░░░░░] 0/12
Week 3:  [░░░░░░░░░░░░░░░░░░░░] 0/13
Week 4:  [░░░░░░░░░░░░░░░░░░░░] 0/15
Week 5:  [░░░░░░░░░░░░░░░░░░░░] 0/13
Week 6:  [░░░░░░░░░░░░░░░░░░░░] 0/10 (review)
Week 7+: [░░░░░░░░░░░░░░░░░░░░] maintenance
TOTAL:   [░░░░░░░░░░░░░░░░░░░░] 0/95 core + extras
```
