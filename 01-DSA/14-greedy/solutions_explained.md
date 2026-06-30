# 14 - Greedy: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 5 bài toán thuộc chủ đề **Greedy (Tham lam)** từ LeetCode Master Tracker.

---

## 76. Maximum Subarray (LeetCode #53) - Medium

### 💡 Ý tưởng cốt lõi
Tìm tổng lớn nhất của một mảng con liên tiếp trong mảng số nguyên `nums`.
Chúng ta sử dụng thuật toán **Kadane's Algorithm**:
* Ta duyệt qua mảng và duy trì tổng tích lũy hiện tại `currSum`.
* Tại mỗi số `nums[i]`, ta quyết định:
  * Hoặc tiếp tục cộng `nums[i]` vào chuỗi con hiện tại: `currSum = currSum + nums[i]`.
  * Hoặc bắt đầu một chuỗi con mới xuất phát từ `nums[i]`: `currSum = nums[i]`.
  * Rút gọn lại: `currSum = max(nums[i], currSum + nums[i])`.
* Cập nhật tổng lớn nhất toàn cục: `maxSum = max(maxSum, currSum)`.

### 📊 Hướng tiếp cận tối ưu

#### Kadane's Algorithm (Optimal) ⭐
* **Mô tả**: Tối ưu không gian lưu trữ trực tiếp trên luồng duyệt tuyến tính.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$ chỉ sử dụng hai biến trạng thái.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]`
* **Khởi tạo**: `currSum = nums[0] = -2`, `maxSum = -2`
* **Xử lý**:
  - `i = 1` (1): `currSum = max(1, -2 + 1) = 1`. `maxSum = max(-2, 1) = 1`.
  - `i = 2` (-3): `currSum = max(-3, 1 - 3) = -2`. `maxSum = 1`.
  - `i = 3` (4): `currSum = max(4, -2 + 4) = 4`. `maxSum = max(1, 4) = 4`.
  - `i = 4` (-1): `currSum = max(-1, 4 - 1) = 3`. `maxSum = 4`.
  - `i = 5` (2): `currSum = max(2, 3 + 2) = 5`. `maxSum = max(4, 5) = 5`.
  - `i = 6` (1): `currSum = max(1, 5 + 1) = 6`. `maxSum = max(5, 6) = 6`.
  - ... tiếp tục duyệt.
* **Kết quả**: `6` (chuỗi con `[4, -1, 2, 1]`).

### 💻 Java Clean Code
```java
public int maxSubArray(int[] nums) {
    int currSum = nums[0];
    int maxSum = nums[0];
    
    for (int i = 1; i < nums.length; i++) {
        currSum = Math.max(nums[i], currSum + nums[i]);
        maxSum = Math.max(maxSum, currSum);
    }
    
    return maxSum;
}
```

---

## 77. Jump Game (LeetCode #55) - Medium

### 💡 Ý tưởng cốt lõi
Bạn xuất phát từ chỉ số `0`. Mỗi phần tử `nums[i]` đại diện cho độ dài bước nhảy tối đa của bạn từ vị trí đó. Hãy xác định xem bạn có thể nhảy tới chỉ số cuối cùng hay không.
Ý tưởng tối ưu là dùng kỹ thuật **Tham lam (Greedy) duyệt ngược từ cuối**:
1. Đặt mục tiêu cần chạm tới là chỉ số cuối cùng: `goal = n - 1`.
2. Duyệt ngược từ phải sang trái từ `i = n - 2` về `0`:
   * Nếu từ vị trí `i` có thể nhảy tới hoặc vượt qua `goal` hiện tại (tức là `i + nums[i] >= goal`):
     * Cập nhật mục tiêu mới cần đạt được chính là `i`: `goal = i`.
3. Sau khi duyệt hết, nếu `goal == 0`, có nghĩa là ta có thể đi từ đầu đến cuối mảng (trả về `true`). Ngược lại là `false`.

### 📊 Hướng tiếp cận tối ưu

#### Greedy Backward Goal Shift (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt ngược mảng 1 lần.
  * **Space Complexity**: $O(1)$.

### 💻 Java Clean Code
```java
public boolean canJump(int[] nums) {
    int goal = nums.length - 1;
    
    for (int i = nums.length - 2; i >= 0; i--) {
        // Nếu từ i có thể nhảy tới goal hiện tại
        if (i + nums[i] >= goal) {
            goal = i; // Dịch goal gần về vị trí xuất phát 0
        }
    }
    
    return goal == 0;
}
```

---

## 78. Jump Game II (LeetCode #45) - Medium

### 💡 Ý tưởng cốt lõi
Tìm số bước nhảy tối thiểu để đi từ chỉ số `0` đến chỉ số cuối cùng. Đề bài đảm bảo luôn có thể đi tới cuối.
Ý tưởng tối ưu là sử dụng kỹ thuật **Tham lam kết hợp trượt khoảng cách (như BFS)**:
* Ta duy trì khoảng nhảy có thể đạt được hiện tại từ `left` đến `right` (ban đầu `left = 0`, `right = 0`).
* Với mỗi bước nhảy:
  * Ta duyệt qua tất cả các vị trí trong khoảng `[left..right]` để tìm điểm có thể nhảy xa nhất tiếp theo: `farthest`.
  * Sau khi duyệt xong khoảng cũ, ta thực hiện bước nhảy: tăng số bước nhảy `jumps++`.
  * Cập nhật khoảng mới: `left = right + 1`, `right = farthest`.
  * Nếu `right >= n - 1`, ta dừng và trả về số bước nhảy.

### 📊 Hướng tiếp cận tối ưu

#### Greedy Range Expansion / BFS (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt mảng tuyến tính.
  * **Space Complexity**: $O(1)$.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [2, 3, 1, 1, 4]`
* **Khởi tạo**: `jumps = 0`, `left = 0`, `right = 0`
* **Xử lý**:
  - **Lượt 1**: duyệt trong khoảng `[0..0]`.
    * `farthest = max(0 + nums[0]) = 2`.
    * Kết thúc lượt 1: thực hiện nhảy `jumps = 1`. Khoảng mới: `left = 1`, `right = 2`.
  - **Lượt 2**: duyệt trong khoảng `[1..2]`.
    * `i = 1`: `1 + nums[1] = 4`. `farthest = 4`.
    * `i = 2`: `2 + nums[2] = 3`. `farthest = max(4, 3) = 4`.
    * Kết thúc lượt 2: thực hiện nhảy `jumps = 2`. Khoảng mới: `left = 3`, `right = 4`.
  - `right = 4 >= 4` → dừng.
* **Kết quả**: `2`

### 💻 Java Clean Code
```java
public int jump(int[] nums) {
    int jumps = 0;
    int currentEnd = 0;
    int farthest = 0;
    
    // Ta không cần duyệt qua phần tử cuối cùng vì khi đạt đến đó đã kết thúc
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        
        // Khi đi hết khoảng bước nhảy của lượt hiện tại
        if (i == currentEnd) {
            jumps++;
            currentEnd = farthest; // Cập nhật mốc giới hạn mới
            
            if (currentEnd >= nums.length - 1) {
                break;
            }
        }
    }
    
    return jumps;
}
```

---

## 79. Gas Station (LeetCode #134) - Medium

### 💡 Ý tưởng cốt lõi
Có $n$ trạm xăng xếp trên một vòng tròn. Bạn có lượng xăng `gas[i]` tại mỗi trạm và tốn `cost[i]` xăng để đi từ trạm `i` đến trạm tiếp theo `i+1`. Tìm trạm xuất phát có thể giúp bạn đi trọn một vòng.
Bài toán có hai định lý quan trọng:
1. Nếu tổng lượng xăng nhỏ hơn tổng lượng chi phí cần dùng (`totalGas < totalCost`), chắc chắn không có lời giải (trả về `-1`).
2. Nếu xuất phát từ trạm `start` và bị hết xăng tại trạm `i`, thì bất kỳ trạm nào nằm giữa `start` và `i` cũng không thể làm trạm xuất phát được. Ta phải thử bắt đầu lại từ trạm `i + 1`.

### 📊 Hướng tiếp cận tối ưu

#### Greedy Station Reset (Optimal) ⭐
* **Mô tả**: Duyệt tuyến tính qua các trạm xăng. Duy trì lượng xăng thực tế trong bình `tank`. Nếu `tank < 0`, đặt trạm xuất phát mới là `i + 1` và reset `tank = 0`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$.

### 💻 Java Clean Code
```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int totalGas = 0, totalCost = 0;
    for (int i = 0; i < gas.length; i++) {
        totalGas += gas[i];
        totalCost += cost[i];
    }
    
    // Nếu tổng lượng xăng không đủ tổng chi phí, không có lời giải
    if (totalGas < totalCost) return -1;
    
    int tank = 0;
    int start = 0;
    for (int i = 0; i < gas.length; i++) {
        tank += gas[i] - cost[i];
        if (tank < 0) {
            start = i + 1; // Đổi trạm xuất phát
            tank = 0;      // Reset lại bình xăng
        }
    }
    
    return start;
}
```

---

## 80. Hand of Straights (LeetCode #846) - Medium

### 💡 Ý tưởng cốt lõi
Chia bộ bài `hand` thành các nhóm có kích thước `groupSize`, sao cho mỗi nhóm chứa các số liên tiếp nhau.
Ý tưởng tối ưu là đếm tần suất xuất hiện của các lá bài, sau đó liên tục ghép nhóm từ lá bài có giá trị nhỏ nhất:
1. Sử dụng một **TreeMap** để tự động sắp xếp các lá bài theo thứ tự tăng dần của giá trị và lưu trữ tần suất của chúng.
2. Lấy ra lá bài nhỏ nhất hiện tại `first` có tần suất $>0$.
3. Cố gắng tạo một nhóm liên tiếp từ `first` đến `first + groupSize - 1`.
4. Nếu bất kỳ lá bài nào trong nhóm đó không đủ số lượng trong Map, trả về `false`.
5. Giảm tần suất của các lá bài đó trong Map.
6. Lặp lại cho tới khi hết bài.

### 📊 Hướng tiếp cận tối ưu

#### TreeMap Greedy Grouping (Optimal) ⭐
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ do thao tác thêm và truy vấn trên TreeMap mất $O(\log n)$.
  * **Space Complexity**: $O(n)$ lưu trữ các lá bài trong TreeMap.

### 💻 Java Clean Code
```java
public boolean isNStraightHand(int[] hand, int groupSize) {
    if (hand.length % groupSize != 0) return false;
    
    // Đếm tần suất dùng TreeMap để tự động sort key
    TreeMap<Integer, Integer> cardCounts = new TreeMap<>();
    for (int card : hand) {
        cardCounts.put(card, cardCounts.getOrDefault(card, 0) + 1);
    }
    
    while (!cardCounts.isEmpty()) {
        int first = cardCounts.firstKey(); // Lấy lá bài nhỏ nhất hiện tại
        
        for (int i = 0; i < groupSize; i++) {
            int card = first + i;
            if (!cardCounts.containsKey(card)) {
                return false; // Không có quân bài tiếp theo để tạo chuỗi
            }
            
            int count = cardCounts.get(card);
            if (count == 1) {
                cardCounts.remove(card);
            } else {
                cardCounts.put(card, count - 1);
            }
        }
    }
    
    return true;
}
```
