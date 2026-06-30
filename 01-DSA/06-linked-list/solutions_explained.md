# 06 - Linked List: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 6 bài toán thuộc chủ đề **Linked List** từ LeetCode Master Tracker.

---

## ListNode Definition (Java)
Tất cả các bài toán sử dụng cấu trúc `ListNode` tiêu chuẩn sau:
```java
public class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
```

---

## 28. Reverse Linked List (LeetCode #206) - Easy

### 💡 Ý tưởng cốt lõi
Đảo ngược một danh sách liên kết đơn.
Ý tưởng tối ưu là dùng cách tiếp cận **Lặp (Iterative)** sử dụng ba con trỏ `prev`, `curr`, và `nextTemp`.
Ta duyệt qua danh sách, tại mỗi node:
* Ghi nhớ node tiếp theo: `nextTemp = curr.next`.
* Đảo hướng liên kết: `curr.next = prev`.
* Dịch chuyển con trỏ: `prev = curr`, `curr = nextTemp`.
Khi kết thúc, `prev` sẽ trỏ vào đầu danh sách mới đã được đảo ngược.

### 📊 Các hướng tiếp cận

#### Cách 1: Iterative (Optimal) ⭐
* **Mô tả**: Sử dụng vòng lặp với 3 biến con trỏ.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt qua danh sách đúng 1 lần.
  * **Space Complexity**: $O(1)$ thay đổi liên kết trực tiếp, không tốn bộ nhớ phụ.

#### Cách 2: Recursive
* **Mô tả**: Gọi đệ quy để đảo ngược phần đuôi danh sách, sau đó móc đầu hiện tại vào cuối phần đuôi đó.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$.
  * **Space Complexity**: $O(n)$ do sử dụng call stack của hệ thống cho đệ quy.

### 🔄 Dry Run với ví dụ
* **Input**: `1 -> 2 -> 3 -> null`
* **Khởi tạo**: `prev = null`, `curr = Node(1)`
* **Xử lý**:
  - **Bước 1**:
    * `nextTemp = curr.next = Node(2)`.
    * Đảo liên kết: `curr.next = prev` tức `Node(1) -> null`.
    * Dịch chuyển: `prev = Node(1)`, `curr = Node(2)`.
  - **Bước 2**:
    * `nextTemp = curr.next = Node(3)`.
    * Đảo liên kết: `curr.next = prev` tức `Node(2) -> 1 -> null`.
    * Dịch chuyển: `prev = Node(2)`, `curr = Node(3)`.
  - **Bước 3**:
    * `nextTemp = curr.next = null`.
    * Đảo liên kết: `curr.next = prev` tức `Node(3) -> 2 -> 1 -> null`.
    * Dịch chuyển: `prev = Node(3)`, `curr = null`.
* **Kết quả**: `3 -> 2 -> 1 -> null` (đầu danh sách mới là `prev`).

### 💻 Java Clean Code
```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode nextTemp = curr.next; // Lưu lại node tiếp theo
        curr.next = prev;              // Đảo ngược con trỏ
        prev = curr;                   // Dịch chuyển prev tiến lên
        curr = nextTemp;               // Dịch chuyển curr tiến lên
    }
    return prev;
}
```

---

## 29. Merge Two Sorted Lists (LeetCode #21) - Easy

### 💡 Ý tưởng cốt lõi
Trộn hai danh sách liên kết đã sắp xếp thành một danh sách liên kết duy nhất được sắp xếp.
Chúng ta sử dụng một **Node giả (Dummy Node)** làm điểm tựa đầu danh sách và một con trỏ `tail` để xây dựng danh sách mới.
* Ta so sánh giá trị tại đầu hai danh sách `list1` và `list2`.
* Trỏ `tail.next` vào node có giá trị nhỏ hơn và tiến con trỏ của danh sách đó lên.
* Dịch chuyển `tail = tail.next`.
* Khi một trong hai danh sách rỗng, ta chỉ cần gắn toàn bộ phần còn lại của danh sách kia vào `tail.next`.

### 📊 Hướng tiếp cận tối ưu

#### Dummy Head Pointer (Optimal) ⭐
* **Mô tả**: Dùng con trỏ lặp qua hai danh sách để nối.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n + m)$ với $n, m$ là số node của hai danh sách.
  * **Space Complexity**: $O(1)$ chỉ sử dụng vài con trỏ phụ.

### 🔄 Dry Run với ví dụ
* **Input**: `L1 = 1 -> 2 -> 4`, `L2 = 1 -> 3 -> 4`
* **Khởi tạo**: `dummy = Node(0)`, `tail = dummy`.
* **Xử lý**:
  - `L1.val = 1`, `L2.val = 1` → chọn `L1`: `tail.next = L1`. `L1 = L1.next` (2). `tail = Node(1)`.
  - `L1.val = 2`, `L2.val = 1` → chọn `L2`: `tail.next = L2`. `L2 = L2.next` (3). `tail = Node(1)`.
  - `L1.val = 2`, `L2.val = 3` → chọn `L1`: `tail.next = L1`. `L1 = L1.next` (4). `tail = Node(2)`.
  - ... ghép tiếp cho đến hết.
* **Kết quả**: `1 -> 1 -> 2 -> 3 -> 4 -> 4` (bắt đầu từ `dummy.next`).

### 💻 Java Clean Code
```java
public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;
    
    while (list1 != null && list2 != null) {
        if (list1.val <= list2.val) {
            tail.next = list1;
            list1 = list1.next;
        } else {
            tail.next = list2;
            list2 = list2.next;
        }
        tail = tail.next;
    }
    
    // Ghép phần còn lại của danh sách chưa rỗng
    tail.next = (list1 != null) ? list1 : list2;
    
    return dummy.next;
}
```

---

## 30. Linked List Cycle (LeetCode #141) - Easy

### 💡 Ý tưởng cốt lõi
Xác định xem một danh sách liên kết có chu trình (cycle) hay không.
Giải pháp tối ưu nhất là sử dụng thuật toán **Floyd's Tortoise and Hare (Rùa và Thỏ - Hai con trỏ nhanh chậm)**.
* Ta đặt hai con trỏ `slow` và `fast` cùng xuất phát từ đầu danh sách.
* Con trỏ `slow` di chuyển 1 bước mỗi lần (`slow = slow.next`).
* Con trỏ `fast` di chuyển 2 bước mỗi lần (`fast = fast.next.next`).
* Nếu danh sách có chu trình, con trỏ `fast` chắc chắn sẽ đuổi kịp và gặp lại con trỏ `slow` (`slow == fast`).
* Nếu danh sách không có chu trình, `fast` sẽ đi tới điểm cuối (`null`).

### 📊 Hướng tiếp cận tối ưu

#### Floyd's Cycle Detection (Optimal) ⭐
* **Mô tả**: Hai con trỏ chạy với tốc độ khác nhau.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ với $n$ là số node trong danh sách.
  * **Space Complexity**: $O(1)$ chỉ sử dụng hai biến con trỏ.

### 🔄 Dry Run với ví dụ
* **Input**: `3 -> 2 -> 0 -> -4 -> (quay lại 2)`
* **Khởi tạo**: `slow = Node(3)`, `fast = Node(3)`
* **Xử lý**:
  - Vòng 1: `slow = Node(2)`, `fast = Node(0)`.
  - Vòng 2: `slow = Node(0)`, `fast = Node(2)`.
  - Vòng 3: `slow = Node(-4)`, `fast = Node(-4)` (hai con trỏ gặp nhau tại Node -4!).
* **Kết quả**: `true`

### 💻 Java Clean Code
```java
public boolean hasCycle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            return true; // Tìm thấy chu trình
        }
    }
    return false;
}
```

---

## 31. Remove Nth Node From End of List (LeetCode #19) - Medium

### 💡 Ý tưởng cốt lõi
Xóa node thứ $n$ tính từ cuối danh sách liên kết và trả về đầu danh sách.
Ý tưởng tối ưu là dùng kỹ thuật **hai con trỏ** tạo một **khoảng cách (gap)** bằng $n$ giữa chúng.
1. Sử dụng một `dummy` node để xử lý trơn tru trường hợp xóa node đầu tiên của danh sách.
2. Đặt `first = dummy`, `second = dummy`.
3. Di chuyển con trỏ `first` tiến lên trước $n + 1$ bước.
4. Di chuyển cả hai con trỏ `first` và `second` đồng thời cho đến khi `first` đi tới `null`. Khi đó, con trỏ `second` sẽ dừng ngay **trước** node cần xóa.
5. Thực hiện xóa node: `second.next = second.next.next`.

### 📊 Hướng tiếp cận tối ưu

#### Two Pointers with Gap (Optimal) ⭐
* **Mô tả**: Con trỏ chạy trước tạo khoảng cách giúp tìm chính xác node đứng trước node cần xóa.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt qua danh sách đúng 1 lần.
  * **Space Complexity**: $O(1)$ biến đổi in-place.

### 🔄 Dry Run với ví dụ
* **Input**: `1 -> 2 -> 3 -> 4 -> 5`, `n = 2` (cần xóa node 4).
* **Khởi tạo**: `dummy -> 1 -> 2 -> 3 -> 4 -> 5`. `first = dummy`, `second = dummy`.
* **Xử lý**:
  - Dịch `first` lên `n+1 = 3` bước: `first` trỏ tới Node(3).
  - Dịch cả hai đồng thời cho tới khi `first == null`:
    * Bước 1: `first` trỏ tới 4, `second` trỏ tới 1.
    * Bước 2: `first` trỏ tới 5, `second` trỏ tới 2.
    * Bước 3: `first` trỏ tới `null`, `second` trỏ tới 3.
  - Lúc này `second` đứng trước Node(4). Thực hiện xóa: `second.next = second.next.next` (3 trỏ tới 5).
* **Kết quả**: `1 -> 2 -> 3 -> 5`

### 💻 Java Clean Code
```java
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode first = dummy;
    ListNode second = dummy;
    
    // Tạo khoảng cách n+1 bước cho con trỏ first
    for (int i = 0; i <= n; i++) {
        first = first.next;
    }
    
    // Dịch chuyển đồng thời đến khi first chạm null
    while (first != null) {
        first = first.next;
        second = second.next;
    }
    
    // Xóa node đích
    second.next = second.next.next;
    
    return dummy.next;
}
```

---

## 32. Reorder List (LeetCode #143) - Medium

### 💡 Ý tưởng cốt lõi
Sắp xếp lại danh sách liên kết theo thứ tự: $L_0 \rightarrow L_n \rightarrow L_1 \rightarrow L_{n-1} \rightarrow L_2 \rightarrow L_{n-2} \rightarrow \dots$
Bài toán này thực chất là sự kết hợp của 3 bài toán nhỏ:
1. **Tìm Node giữa danh sách**: Sử dụng hai con trỏ nhanh/chậm (`slow` & `fast`). Khi `fast` chạm cuối, `slow` sẽ ở giữa.
2. **Đảo ngược nửa sau của danh sách**: Đảo ngược danh sách bắt đầu từ sau `slow`.
3. **Trộn xen kẽ hai nửa**: Trộn xen kẽ danh sách nửa đầu và nửa sau đã đảo ngược.

### 📊 Hướng tiếp cận tối ưu

#### Split + Reverse + Merge (Optimal) ⭐
* **Mô tả**: Tách danh sách tại trung điểm, đảo ngược nửa sau, sau đó chập chéo các node với nhau.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do mỗi bước tìm giữa, đảo ngược, và trộn đều chạy tuyến tính O(n).
  * **Space Complexity**: $O(1)$ hoàn toàn biến đổi liên kết in-place.

### 🔄 Dry Run với ví dụ
* **Input**: `1 -> 2 -> 3 -> 4 -> 5`
* **Xử lý**:
  1. Tìm giữa: `slow` dừng tại `3`. Tách làm hai danh sách: `L1 = 1 -> 2 -> 3 -> null`, `L2 = 4 -> 5 -> null`.
  2. Đảo ngược `L2`: `L2_reversed = 5 -> 4 -> null`.
  3. Trộn xen kẽ `L1` và `L2_reversed`:
     * Ghép `1` với `5` → `1 -> 5`
     * Ghép tiếp `2` với `4` → `1 -> 5 -> 2 -> 4`
     * Ghép tiếp `3` → `1 -> 5 -> 2 -> 4 -> 3 -> null`
* **Kết quả**: `1 -> 5 -> 2 -> 4 -> 3`

### 💻 Java Clean Code
```java
public void reorderList(ListNode head) {
    if (head == null || head.next == null) return;
    
    // Bước 1: Tìm trung điểm danh sách bằng slow/fast pointer
    ListNode slow = head;
    ListNode fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    // Bước 2: Đảo ngược nửa sau danh sách
    ListNode prev = null;
    ListNode curr = slow.next;
    slow.next = null; // Tách danh sách làm đôi
    while (curr != null) {
        ListNode nextTemp = curr.next;
        curr.next = prev;
        prev = curr;
        curr = nextTemp;
    }
    
    // Bước 3: Trộn xen kẽ hai danh sách (head và prev)
    ListNode first = head;
    ListNode second = prev;
    while (second != null) {
        ListNode tmp1 = first.next;
        ListNode tmp2 = second.next;
        
        first.next = second;
        second.next = tmp1;
        
        first = tmp1;
        second = tmp2;
    }
}
```

---

## 33. Merge k Sorted Lists (LeetCode #23) - Hard

### 💡 Ý tưởng cốt lõi
Trộn $k$ danh sách liên kết đã sắp xếp thành một danh sách liên kết duy nhất được sắp xếp.
Ý tưởng tối ưu là sử dụng một **Min Heap (Priority Queue)** để luôn lấy ra node có giá trị nhỏ nhất trong số các node đầu tiên của $k$ danh sách:
1. Đưa tất cả các node đầu tiên (nếu không rỗng) của $k$ danh sách vào Min Heap.
2. Lấy ra node có giá trị nhỏ nhất từ Heap, nối vào danh sách kết quả.
3. Nếu node vừa lấy ra có node tiếp theo (`node.next != null`), đưa `node.next` vào Heap.
4. Lặp lại cho tới khi Heap rỗng.

### 📊 Các hướng tiếp cận

#### Cách 1: So sánh từng cặp (Divide and Conquer)
* **Mô tả**: Trộn từng cặp danh sách liên kết bằng thuật toán trộn 2 danh sách. Chia để trị.
* **Độ phức tạp**:
  * **Time Complexity**: $O(N \log k)$ với $N$ là tổng số node của tất cả các danh sách.
  * **Space Complexity**: $O(1)$ nếu làm in-place, hoặc $O(\log k)$ call stack.

#### Cách 2: Min Heap / Priority Queue (Optimal) ⭐
* **Mô tả**: Duy trì một Heap chứa tối đa $k$ node đầu tiên.
* **Độ phức tạp**:
  * **Time Complexity**: $O(N \log k)$ vì mỗi thao tác lấy và thêm vào heap kích thước $k$ mất $O(\log k)$.
  * **Space Complexity**: $O(k)$ để lưu trữ các node trong Priority Queue.

### 🔄 Dry Run với ví dụ
* **Input**: `lists = [[1->4->5], [1->3->4], [2->6]]`
* **Khởi tạo**: Đưa đầu các danh sách vào Heap: `Heap = {1 (từ L1), 1 (từ L2), 2 (từ L3)}`. `dummy = Node(0)`, `tail = dummy`.
* **Xử lý**:
  - Pop được `1` (từ L1). Nối vào tail. Đưa `4` (từ L1) vào Heap. `Heap = {1 (từ L2), 2 (từ L3), 4 (từ L1)}`.
  - Pop được `1` (từ L2). Nối vào tail. Đưa `3` (từ L2) vào Heap. `Heap = {2 (từ L3), 3 (từ L2), 4 (từ L1)}`.
  - Pop được `2` (từ L3). Nối vào tail. Đưa `6` (từ L3) vào Heap. `Heap = {3 (từ L2), 4 (từ L1), 6 (từ L3)}`.
  - Tiếp tục cho đến khi Heap rỗng.
* **Kết quả**: `1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6`

### 💻 Java Clean Code
```java
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    
    // Khởi tạo Min Heap so sánh theo giá trị ListNode
    PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
    
    // Đưa tất cả các node đầu tiên vào Heap
    for (ListNode head : lists) {
        if (head != null) {
            pq.offer(head);
        }
    }
    
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;
    
    while (!pq.isEmpty()) {
        ListNode minNode = pq.poll();
        tail.next = minNode;
        tail = tail.next;
        
        // Nếu node này còn phần tử tiếp theo, đưa vào Heap
        if (minNode.next != null) {
            pq.offer(minNode.next);
        }
    }
    
    return dummy.next;
}
```
