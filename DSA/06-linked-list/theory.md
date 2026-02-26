# 06 - Linked List

## 📖 Tổng quan

**Linked List** là cấu trúc dữ liệu tuyến tính, mỗi node chứa data và pointer đến node tiếp theo. Không cần bộ nhớ liên tiếp như Array.

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}
```

## 🧠 Kiến thức cốt lõi

| Thao tác | Array | Linked List |
|----------|-------|-------------|
| Truy cập `[i]` | O(1) | O(n) |
| Thêm đầu | O(n) | O(1) |
| Thêm cuối | O(1)* | O(n) / O(1)** |
| Xóa node | O(n) | O(1)*** |

> *amortized, **nếu có tail pointer, ***nếu có reference đến node cần xóa

## 🔍 Khi nào sử dụng?

- Bài cho sẵn `ListNode` structure
- Cần **reverse** danh sách
- **Merge** nhiều sorted list
- Phát hiện **cycle** trong danh sách
- Tìm **middle node**, **kth from end**

## 📝 Các Pattern phổ biến

### Pattern 1: Dummy Head
```java
// Dùng dummy node để đơn giản hóa xử lý head
ListNode dummy = new ListNode(0);
dummy.next = head;
// ... xử lý ...
return dummy.next; // Head thực sự
```

### Pattern 2: Fast & Slow Pointer
```java
// Tìm middle node / phát hiện cycle
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
// slow ở giữa list
```

### Pattern 3: Reverse In-place
```java
ListNode prev = null, curr = head;
while (curr != null) {
    ListNode next = curr.next; // Lưu next
    curr.next = prev;          // Đảo chiều
    prev = curr;               // Tiến prev
    curr = next;               // Tiến curr
}
return prev; // New head
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Reverse list | O(n) | O(1) |
| Merge 2 sorted | O(n + m) | O(1) |
| Detect cycle | O(n) | O(1) |
| Find middle | O(n) | O(1) |

## 💡 Tips phỏng vấn

1. **Dummy head**: LUÔN dùng khi head có thể thay đổi (merge, remove, insert)
2. **Draw it out**: Vẽ linked list + pointer trên giấy, trace qua 2-3 bước
3. **Edge cases**: List rỗng, 1 node, 2 nodes
4. **In-place**: Hầu hết bài linked list yêu cầu O(1) space
