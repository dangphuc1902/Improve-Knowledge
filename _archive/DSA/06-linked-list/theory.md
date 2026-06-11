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
- **Nó là gì?**: Tạo một node giả (`dummy node`) trỏ đến đầu danh sách (`head`). Điều này giúp ta xử lý đồng nhất mọi node mà không cần viết điều kiện riêng cho `head`.
- **Giải quyết bài toán nào?**: 
    - Xóa các node có giá trị cụ thể (`Remove Linked List Elements`).
    - Hợp nhất hai danh sách đã sắp xếp (`Merge Two Sorted Lists`).
    - Các bài toán mà `head` có thể bị thay đổi hoặc xóa bỏ.
- **Ưu điểm**:
    - Code gọn gàng hơn, giảm bớt các câu lệnh `if (head == null)`.
    - Tránh lỗi `NullPointerException` khi truy cập `head`.
- **Nhược điểm**:
    - Tốn thêm một lượng nhỏ bộ nhớ cho 1 node giả.
- **Sự thay thế**:
    - Xử lý thủ công trường hợp `head` bằng nhiều câu lệnh `if/else`.

```java
// Dùng dummy node để đơn giản hóa xử lý head
ListNode dummy = new ListNode(0);
dummy.next = head;
// ... xử lý ...
return dummy.next; // Head thực sự
```

### Pattern 2: Fast & Slow Pointer (Tortoise and Hare)
- **Nó là gì?**: Sử dụng hai con trỏ di chuyển với tốc độ khác nhau (thường `slow` đi 1 bước, `fast` đi 2 bước).
- **Giải quyết bài toán nào?**: 
    - Tìm node ở giữa danh sách (`Middle of the Linked List`).
    - Phát hiện vòng lặp (`Linked List Cycle`).
    - Tìm node thứ `k` từ cuối lên.
- **Ưu điểm**:
    - Có thể tìm thấy điểm cần thiết chỉ trong một lần duyệt duy nhất.
    - Không tốn thêm bộ nhớ (O(1) space).
- **Nhược điểm**:
    - Cần cẩn thận với điều kiện dừng của vòng lặp (`fast != null && fast.next != null`).
- **Sự thay thế**:
    - Duyệt lần 1 để đếm tổng số node `N`, duyệt lần 2 đến vị trí `N/2`. (Tốn 2 lần duyệt).

```java
// Tìm middle node / phát hiện cycle
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
// slow ở giữa list
```

### Pattern 3: Reverse In-place (Iterative)
- **Nó là gì?**: Thay đổi hướng của các con trỏ `next` của từng node để đảo ngược danh sách mà không dùng thêm mảng hay danh sách phụ.
- **Giải quyết bài toán nào?**: 
    - Đảo ngược danh sách liên kết (`Reverse Linked List`).
    - Kiểm tra danh sách có đối xứng không (`Palindrome Linked List`).
- **Ưu điểm**:
    - Cực kỳ tối ưu về bộ nhớ (O(1) space).
- **Nhược điểm**:
    - Làm thay đổi cấu trúc của danh sách gốc.
- **Sự thay thế**:
    - **Recursive Reverse**: Dễ viết hơn nhưng tốn O(n) không gian stack.
    - **Stack**: Đẩy các node vào stack rồi lấy ra (O(n) space).

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
