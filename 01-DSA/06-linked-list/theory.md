# 06 - Linked List

## 📖 Tổng quan

**Linked List** là cấu trúc dữ liệu tuyến tính gồm các **node** liên kết nhau qua con trỏ. Không có random access (O(1)) như Array, nhưng insert/delete ở đầu/giữa là O(1) nếu đã có con trỏ.

> **Ý tưởng cốt lõi:** Với Linked List, hãy luôn **vẽ sơ đồ trước khi code** — theo dõi từng pointer step-by-step để không bị mất liên kết.

## 🧠 Kiến thức cốt lõi

### Node Structure (Java)

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}
```

### Operations Complexity

| Thao tác | Singly Linked List | Array |
|----------|-------------------|-------|
| Truy cập index | O(n) | O(1) |
| Thêm đầu | O(1) | O(n) |
| Thêm cuối | O(n) | O(1) amortized |
| Xóa (đã có con trỏ) | O(1) | O(n) |
| Tìm kiếm | O(n) | O(n) |

### Dummy Node — Kỹ thuật cốt lõi

```java
ListNode dummy = new ListNode(0);
dummy.next = head;
// Thao tác với dummy.next
return dummy.next; // head mới
```
> **Tại sao dùng dummy?** Tránh xử lý trường hợp đặc biệt khi head thay đổi.

## 🔍 Khi nào sử dụng?

- **Bài toán có từ "in-place"**: Đảo ngược, merge, remove không dùng extra space
- **Cycle detection**: Floyd's algorithm (Fast & Slow)
- **Middle finding**: Fast & Slow pointers
- **Merge operations**: Merge two sorted lists, merge k lists
- **Khi thấy pattern**: Thao tác với con trỏ → Linked List

## 📝 Các Pattern phổ biến

### Pattern 1: Reverse — Đảo ngược Linked List
- **Nó là gì?**: Di chuyển 3 con trỏ `prev, curr, next` để đảo chiều liên kết.
- **Giải quyết bài toán nào?**: Reverse Linked List, Reverse in k-Group, Palindrome Linked List.
- **Ưu điểm**: O(n) time, O(1) space — in-place.
- **Nhược điểm**: Dễ mất liên kết nếu không cẩn thận.
- **Sự thay thế**: Đổ vào Stack rồi rebuild (O(n) space).

```java
// Iterative — O(n) time, O(1) space ⭐
ListNode prev = null, curr = head;
while (curr != null) {
    ListNode next = curr.next; // lưu next trước
    curr.next = prev;          // đảo chiều
    prev = curr;               // move prev
    curr = next;               // move curr
}
return prev; // head mới

// Recursive — O(n) time, O(n) space (call stack)
public ListNode reverseRecursive(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode newHead = reverseRecursive(head.next);
    head.next.next = head; // node tiếp theo trỏ ngược lại head
    head.next = null;      // head trỏ null
    return newHead;
}
```

### Pattern 2: Fast & Slow Pointer
- **Nó là gì?**: `slow` đi 1 bước, `fast` đi 2 bước. Khi `fast` đến cuối → `slow` ở giữa.
- **Giải quyết bài toán nào?**: Find Middle, Detect Cycle (LC 141), Cycle Start (LC 142).
- **Ưu điểm**: O(n) time, O(1) space — không cần HashMap.
- **Nhược điểm**: Logic cycle start phức tạp hơn.
- **Sự thay thế**: HashSet lưu visited nodes (O(n) space).

```java
// Detect Cycle — Floyd's Algorithm
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) return true; // cycle!
}
return false;

// Find Middle — slow ở giữa khi fast đến cuối
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
return slow; // middle node
```

### Pattern 3: Merge — Merge Two Sorted Lists
- **Nó là gì?**: So sánh head của 2 list, chọn node nhỏ hơn, advance pointer đó.
- **Giải quyết bài toán nào?**: Merge Two Sorted Lists (LC 21), Merge K Sorted Lists.
- **Ưu điểm**: O(n+m) time, O(1) space với dummy node.
- **Sự thay thế**: Đổ vào mảng, sort, rebuild (O(n log n), O(n) space).

```java
ListNode dummy = new ListNode(0), curr = dummy;
while (l1 != null && l2 != null) {
    if (l1.val <= l2.val) { curr.next = l1; l1 = l1.next; }
    else { curr.next = l2; l2 = l2.next; }
    curr = curr.next;
}
curr.next = (l1 != null) ? l1 : l2; // append remaining
return dummy.next;
```

### Pattern 4: Remove Node — Dummy Node trick
- **Nó là gì?**: Dùng dummy node để đơn giản hóa việc xóa node, kể cả head.
- **Giải quyết bài toán nào?**: Remove Nth Node From End (LC 19), Remove Duplicates (LC 82/83).
- **Trick**: Dùng 2 pointer cách nhau N bước để tìm node cần xóa từ cuối.

```java
// Remove Nth from End — Two Pointer gap technique
ListNode dummy = new ListNode(0);
dummy.next = head;
ListNode fast = dummy, slow = dummy;

// fast đi trước N+1 bước
for (int i = 0; i <= n; i++) fast = fast.next;

// cùng đi đến cuối
while (fast != null) { slow = slow.next; fast = fast.next; }

// slow.next là node cần xóa
slow.next = slow.next.next;
return dummy.next;
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Reverse Linked List — Dry Run

```
Input: 1 → 2 → 3 → 4 → 5 → null

Initial: prev=null, curr=1

Step 1: next=2, curr.next=null, prev=1, curr=2
  null ← 1    2 → 3 → 4 → 5

Step 2: next=3, curr.next=1, prev=2, curr=3
  null ← 1 ← 2    3 → 4 → 5

Step 3: next=4, curr.next=2, prev=3, curr=4
  null ← 1 ← 2 ← 3    4 → 5

Step 4: next=5, curr.next=3, prev=4, curr=5
  null ← 1 ← 2 ← 3 ← 4    5

Step 5: next=null, curr.next=4, prev=5, curr=null
  null ← 1 ← 2 ← 3 ← 4 ← 5

curr=null → EXIT. return prev=5

✅ Output: 5 → 4 → 3 → 2 → 1 → null
```

### Ví dụ 2: Detect Cycle — Floyd's Algorithm

```
Input: 3 → 2 → 0 → -4 → (back to 2)
Nodes: 3(0) → 2(1) → 0(2) → -4(3) → 2(1) [cycle at pos 1]

slow=3, fast=3

Step 1: slow=2, fast=0
Step 2: slow=0, fast=2 (fast: -4→2)
Step 3: slow=-4, fast=-4 (fast: 0→-4)
  slow == fast → return true ✅

Tại sao fast gặp slow? Sau khi fast vào cycle, nó "đuổi" slow 1 bước/vòng.
```

### Ví dụ 3: Merge Two Sorted Lists

```
l1: 1 → 2 → 4
l2: 1 → 3 → 4

dummy → (curr=dummy)

Step 1: l1.val=1 == l2.val=1 → take l1 (hoặc l2, cả 2 đều đúng)
  dummy → 1(l1). l1=2, curr=1

Step 2: l1.val=2 > l2.val=1 → take l2
  dummy → 1 → 1(l2). l2=3, curr=1

Step 3: l1.val=2 < l2.val=3 → take l1
  dummy → 1 → 1 → 2. l1=4, curr=2

Step 4: l1.val=4 > l2.val=3 → take l2
  dummy → 1 → 1 → 2 → 3. l2=4, curr=3

Step 5: l1.val=4 == l2.val=4 → take l1
  dummy → 1 → 1 → 2 → 3 → 4. l1=null, curr=4

l1=null → curr.next=l2=4 → append 4

✅ Output: 1 → 1 → 2 → 3 → 4 → 4
```

## 🔄 So sánh các Approach

### Reverse: Iterative vs Recursive vs Stack

| Approach | Time | Space | Ưu điểm |
|----------|------|-------|---------|
| **Iterative ⭐** | O(n) | O(1) | In-place, không stack overflow |
| Recursive | O(n) | O(n) | Code ngắn, dễ hiểu |
| Stack | O(n) | O(n) | Dễ nhất nhưng dùng nhiều space |

### Cycle Detection: Floyd's vs HashSet

| Approach | Time | Space |
|----------|------|-------|
| **Floyd's ⭐** | O(n) | O(1) |
| HashSet | O(n) | O(n) |

## 🚨 Edge Cases cần chú ý

```java
// Reverse:
// 1. head = null → null
// 2. head.next = null (1 node) → head (không thay đổi)
// 3. Mọi case khác xử lý tốt với 3-pointer approach

// Cycle:
// 1. head = null → false
// 2. head.next = null → false
// 3. Self-loop: head.next = head → true

// Merge:
// 1. l1 = null → trả về l2
// 2. l2 = null → trả về l1
// 3. Cả 2 null → null

// Remove Nth from End:
// 1. Remove head (n = length) → dummy.next trick xử lý tốt
// 2. n = 1 → remove tail
```

## ⏱️ Complexity thường gặp

| Bài toán | Time | Space |
|----------|------|-------|
| Reverse Linked List | O(n) | O(1) |
| Merge Two Sorted Lists | O(n+m) | O(1) |
| Detect Cycle | O(n) | O(1) |
| Remove Nth from End | O(n) | O(1) |
| Find Middle | O(n) | O(1) |
| Reorder List | O(n) | O(1) |
| Merge K Sorted Lists | O(n log k) | O(k) |

## 💡 Tips phỏng vấn

1. **Luôn vẽ sơ đồ**: Trước khi code, vẽ 3-5 nodes và các pointer ra giấy.
2. **Dummy node**: Dùng khi có thể thay đổi head → tránh edge case.
3. **Three pointers**: `prev, curr, next` — lưu `next` trước khi thay đổi `curr.next`.
4. **Null check**: `fast != null && fast.next != null` — luôn check cả `fast.next`.
5. **Even/Odd length**: Với Find Middle, khi n chẵn `slow` trỏ node giữa 1 (left middle), khi lẻ → đúng giữa.
