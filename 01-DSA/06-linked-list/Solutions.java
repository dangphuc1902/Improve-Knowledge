import java.util.*;

/**
 * 06 - Linked List Solutions
 * Các bài: Reverse Linked List, Merge Two Sorted Lists, Linked List Cycle,
 *          Remove Nth Node From End, Reorder List, Merge K Sorted Lists
 */
public class Solutions {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    // ============================================================
    // LC 206 - Reverse Linked List
    // Approach 1: Iterative (3 pointers) — O(n), O(1) ⭐
    // Approach 2: Recursive — O(n), O(n) call stack
    // ============================================================
    static class ReverseLinkedList {
        // ⭐ Optimal: Iterative với prev, curr, next
        public ListNode reverseList(ListNode head) {
            ListNode prev = null, curr = head;
            while (curr != null) {
                ListNode next = curr.next; // 1. save next
                curr.next = prev;          // 2. reverse link
                prev = curr;               // 3. advance prev
                curr = next;               // 4. advance curr
            }
            return prev; // prev là head mới
        }

        // Approach 2: Recursive
        // Time: O(n), Space: O(n) — call stack
        public ListNode reverseListRecursive(ListNode head) {
            if (head == null || head.next == null) return head;
            ListNode newHead = reverseListRecursive(head.next);
            head.next.next = head; // node tiếp theo trỏ ngược lại
            head.next = null;      // cắt liên kết cũ
            return newHead;
        }
    }

    // ============================================================
    // LC 21 - Merge Two Sorted Lists
    // Approach 1: Iterative with Dummy Node — O(n+m), O(1) ⭐
    // Approach 2: Recursive — O(n+m), O(n+m) stack
    // ============================================================
    static class MergeTwoSortedLists {
        // ⭐ Optimal: Iterative với Dummy Node
        public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
            ListNode dummy = new ListNode(0);
            ListNode curr = dummy;

            while (l1 != null && l2 != null) {
                if (l1.val <= l2.val) {
                    curr.next = l1;
                    l1 = l1.next;
                } else {
                    curr.next = l2;
                    l2 = l2.next;
                }
                curr = curr.next;
            }
            curr.next = (l1 != null) ? l1 : l2; // nối phần còn lại
            return dummy.next;
        }

        // Approach 2: Recursive (elegant nhưng O(n+m) stack)
        public ListNode mergeTwoListsRecursive(ListNode l1, ListNode l2) {
            if (l1 == null) return l2;
            if (l2 == null) return l1;
            if (l1.val <= l2.val) {
                l1.next = mergeTwoListsRecursive(l1.next, l2);
                return l1;
            } else {
                l2.next = mergeTwoListsRecursive(l1, l2.next);
                return l2;
            }
        }
    }

    // ============================================================
    // LC 141 - Linked List Cycle
    // Approach 1: Floyd's Fast & Slow — O(n), O(1) ⭐
    // Approach 2: HashSet visited — O(n), O(n)
    // ============================================================
    static class LinkedListCycle {
        // ⭐ Optimal: Floyd's Cycle Detection Algorithm
        public boolean hasCycle(ListNode head) {
            ListNode slow = head, fast = head;
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) return true; // meeting point → cycle
            }
            return false;
        }

        // Approach 2: HashSet — đơn giản, dễ hiểu
        public boolean hasCycleHashSet(ListNode head) {
            Set<ListNode> visited = new HashSet<>();
            ListNode curr = head;
            while (curr != null) {
                if (!visited.add(curr)) return true; // đã thấy rồi
                curr = curr.next;
            }
            return false;
        }
    }

    // ============================================================
    // LC 142 - Linked List Cycle II — Find Cycle Start
    // Approach 1: Floyd's + Math — O(n), O(1) ⭐
    // Approach 2: HashSet — O(n), O(n)
    // ============================================================
    static class LinkedListCycleII {
        // ⭐ Floyd's Algorithm + Mathematical Proof:
        // Sau khi fast==slow: reset slow=head, cả 2 đi 1 bước/lần
        // Chúng gặp nhau tại cycle start
        public ListNode detectCycle(ListNode head) {
            ListNode slow = head, fast = head;

            // Phase 1: Detect cycle
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) break;
            }

            // Không có cycle
            if (fast == null || fast.next == null) return null;

            // Phase 2: Find cycle start
            slow = head;
            while (slow != fast) {
                slow = slow.next;
                fast = fast.next; // cả 2 đi 1 bước
            }
            return slow; // cycle start
        }
    }

    // ============================================================
    // LC 19 - Remove Nth Node From End of List
    // Approach 1: One-pass with 2 pointers gap n — O(n), O(1) ⭐
    // Approach 2: Two-pass (find length first) — O(n), O(1)
    // ============================================================
    static class RemoveNthFromEnd {
        // ⭐ Optimal: One-pass với 2 pointers cách nhau n+1 bước
        public ListNode removeNthFromEnd(ListNode head, int n) {
            ListNode dummy = new ListNode(0);
            dummy.next = head;
            ListNode fast = dummy, slow = dummy;

            // fast đi trước n+1 bước
            for (int i = 0; i <= n; i++) fast = fast.next;

            // Cả 2 đi cho đến khi fast = null
            while (fast != null) {
                slow = slow.next;
                fast = fast.next;
            }

            // slow.next là node cần xóa
            slow.next = slow.next.next;
            return dummy.next;
        }

        // Approach 2: Two-pass
        public ListNode removeNthFromEndTwoPass(ListNode head, int n) {
            // Pass 1: đếm length
            int length = 0;
            ListNode curr = head;
            while (curr != null) { length++; curr = curr.next; }

            // Pass 2: đi đến node thứ (length - n - 1) và xóa node tiếp theo
            ListNode dummy = new ListNode(0);
            dummy.next = head;
            curr = dummy;
            for (int i = 0; i < length - n; i++) curr = curr.next;
            curr.next = curr.next.next;
            return dummy.next;
        }
    }

    // ============================================================
    // LC 143 - Reorder List (Medium — Comprehensive)
    // Steps: Find Middle → Reverse Second Half → Merge
    // Time: O(n), Space: O(1) ⭐
    // ============================================================
    static class ReorderList {
        public void reorderList(ListNode head) {
            if (head == null || head.next == null) return;

            // Step 1: Find middle (slow/fast)
            ListNode slow = head, fast = head;
            while (fast.next != null && fast.next.next != null) {
                slow = slow.next;
                fast = fast.next.next;
            }
            ListNode secondHalf = slow.next;
            slow.next = null; // Cut first half

            // Step 2: Reverse second half
            ListNode prev = null, curr = secondHalf;
            while (curr != null) {
                ListNode next = curr.next;
                curr.next = prev;
                prev = curr;
                curr = next;
            }
            secondHalf = prev;

            // Step 3: Merge two halves
            ListNode first = head, second = secondHalf;
            while (second != null) {
                ListNode tmp1 = first.next, tmp2 = second.next;
                first.next = second;
                second.next = tmp1;
                first = tmp1;
                second = tmp2;
            }
        }
    }

    // ============================================================
    // LC 23 - Merge K Sorted Lists (Hard)
    // Approach 1: Min Heap — O(n log k) time, O(k) space ⭐
    // Approach 2: Divide & Conquer — O(n log k) time, O(log k) space
    // Approach 3: Brute Force merge — O(n*k) time
    // ============================================================
    static class MergeKSortedLists {
        // ⭐ Approach 1: Min Heap (Priority Queue)
        public ListNode mergeKLists(ListNode[] lists) {
            PriorityQueue<ListNode> pq = new PriorityQueue<>(
                (a, b) -> a.val - b.val // sort by value ascending
            );

            // Add all non-null heads
            for (ListNode node : lists) {
                if (node != null) pq.offer(node);
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

        // Approach 2: Divide & Conquer — merge lists pair by pair
        public ListNode mergeKListsDivide(ListNode[] lists) {
            if (lists.length == 0) return null;
            return mergeRange(lists, 0, lists.length - 1);
        }

        private ListNode mergeRange(ListNode[] lists, int lo, int hi) {
            if (lo == hi) return lists[lo];
            int mid = lo + (hi - lo) / 2;
            ListNode left = mergeRange(lists, lo, mid);
            ListNode right = mergeRange(lists, mid + 1, hi);
            return mergeTwoLists(left, right);
        }

        private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
            ListNode dummy = new ListNode(0), curr = dummy;
            while (l1 != null && l2 != null) {
                if (l1.val <= l2.val) { curr.next = l1; l1 = l1.next; }
                else { curr.next = l2; l2 = l2.next; }
                curr = curr.next;
            }
            curr.next = (l1 != null) ? l1 : l2;
            return dummy.next;
        }
    }
}
