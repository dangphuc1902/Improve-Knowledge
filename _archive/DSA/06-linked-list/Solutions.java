import java.util.*;

/**
 * =============================================
 *  06 - LINKED LIST
 *  Các bài LeetCode tiêu biểu
 * =============================================
 */

// Definition cho ListNode (dùng chung cho các bài)
class ListNode {
    int val;
    ListNode next;
    ListNode()  {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

// -----------------------------------------------
// Bài 1: Reverse Linked List (LeetCode #206) - Easy
// -----------------------------------------------
// Đảo ngược linked list.
// Ví dụ: 1→2→3→4→5 → 5→4→3→2→1
//
// Approach: Iterative - dùng 3 pointer: prev, curr, next.
// Tại mỗi bước: lưu next, đảo chiều curr.next = prev, tiến prev và curr.
//
// Time: O(n)
// Space: O(1)
class ReverseLinkedList {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next; // Lưu node tiếp theo
            curr.next = prev;          // Đảo chiều pointer
            prev = curr;               // Tiến prev
            curr = next;               // Tiến curr
        }

        return prev; // prev trở thành head mới
    }
}

// -----------------------------------------------
// Bài 2: Merge Two Sorted Lists (LeetCode #21) - Easy
// -----------------------------------------------
// Merge 2 sorted linked list thành 1 sorted list.
// Ví dụ: 1→2→4 + 1→3→4 → 1→1→2→3→4→4
//
// Approach: Dùng dummy head, so sánh node hiện tại của 2 list,
// chọn node nhỏ hơn nối vào kết quả.
//
// Time: O(n + m) - n, m là độ dài 2 list
// Space: O(1) - chỉ thay đổi pointer, không tạo node mới
class MergeTwoSortedLists {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0); // Dummy head
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // Nối phần còn lại (1 trong 2 list đã hết)
        current.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }
}

// -----------------------------------------------
// Bài 3: Linked List Cycle (LeetCode #141) - Easy
// -----------------------------------------------
// Kiểm tra linked list có cycle không.
//
// Approach: Floyd's Cycle Detection (Fast & Slow pointer).
// - Slow đi 1 bước, Fast đi 2 bước
// - Nếu có cycle, fast sẽ gặp slow
// - Nếu fast đến null, không có cycle
//
// Time: O(n) - fast sẽ gặp slow trong tối đa n bước (nếu có cycle)
// Space: O(1) - chỉ dùng 2 pointer
class LinkedListCycle {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;       // Đi 1 bước
            fast = fast.next.next;  // Đi 2 bước

            if (slow == fast) {
                return true; // Gặp nhau → có cycle
            }
        }

        return false; // Fast đến null → không có cycle
    }
}
