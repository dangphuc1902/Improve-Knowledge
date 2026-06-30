# 05 - Sliding Window: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 4 bài toán thuộc chủ đề **Sliding Window** từ LeetCode Master Tracker.

---

## 14. Best Time to Buy and Sell Stock (LeetCode #121) - Easy

### 💡 Ý tưởng cốt lõi
Tìm lợi nhuận lớn nhất có thể đạt được bằng cách chọn một ngày để mua một cổ phiếu và chọn một ngày khác trong tương lai để bán cổ phiếu đó.
Ý tưởng tối ưu là dùng kỹ thuật **Sliding Window** hoặc duyệt tuyến tính duy trì một con trỏ ghi nhớ giá mua thấp nhất (`minPrice`) đã thấy cho đến nay. Khi duyệt qua mỗi ngày `i`, ta coi đó là ngày bán:
* Tính lợi nhuận nếu bán ngày hôm nay: `profit = prices[i] - minPrice`.
* Cập nhật lợi nhuận cực đại thu được: `maxProfit = max(maxProfit, profit)`.
* Cập nhật giá mua thấp nhất: `minPrice = min(minPrice, prices[i])`.

### 📊 Hướng tiếp cận tối ưu

#### Min Price Tracking (Optimal) ⭐
* **Mô tả**: Duyệt qua mảng giá cả đúng một lần và cập nhật liên tục giá tối thiểu và lợi nhuận tối đa.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt mảng 1 lần.
  * **Space Complexity**: $O(1)$ chỉ sử dụng hai biến lưu trạng thái.

### 🔄 Dry Run với ví dụ
* **Input**: `prices = [7, 1, 5, 3, 6, 4]`
* **Khởi tạo**: `minPrice = INF`, `maxProfit = 0`
* **Xử lý**:
  - `price = 7`: `minPrice` cập nhật thành 7.
  - `price = 1`: `minPrice` cập nhật thành 1.
  - `price = 5`: `profit = 5 - 1 = 4`. `maxProfit` cập nhật thành 4.
  - `price = 3`: `profit = 3 - 1 = 2`. `maxProfit` vẫn là 4.
  - `price = 6`: `profit = 6 - 1 = 5`. `maxProfit` cập nhật thành 5.
  - `price = 4`: `profit = 4 - 1 = 3`. `maxProfit` vẫn là 5.
* **Kết quả**: `5`

### 💻 Java Clean Code
```java
public int maxProfit(int[] prices) {
    int minPrice = Integer.MAX_VALUE;
    int maxProfit = 0;
    for (int price : prices) {
        if (price < minPrice) {
            minPrice = price; // Cập nhật ngày mua rẻ nhất
        } else {
            maxProfit = Math.max(maxProfit, price - minPrice); // Tính lợi nhuận nếu bán hôm nay
        }
    }
    return maxProfit;
}
```

---

## 15. Longest Substring Without Repeating Characters (LeetCode #3) - Medium

### 💡 Ý tưởng cốt lõi
Tìm độ dài của chuỗi con dài nhất mà không chứa bất kỳ ký tự trùng lặp nào.
Chúng ta duy trì một cửa sổ trượt (sliding window) giới hạn bởi hai con trỏ `left` và `right`.
Để tối ưu hóa thời gian co cửa sổ, ta dùng một **HashMap** lưu trữ ký tự và chỉ số xuất hiện cuối cùng của nó (`lastSeen`).
* Khi duyệt `right` tăng dần, ta đọc ký tự `c = s.charAt(right)`.
* Nếu `c` đã từng xuất hiện VÀ vị trí xuất hiện cũ của nó nằm trong cửa sổ hiện tại (tức là `lastSeen.get(c) >= left`):
  * Ta ngay lập tức dịch biên trái `left` nhảy cóc qua vị trí trùng lặp: `left = lastSeen.get(c) + 1`.
* Cập nhật `lastSeen.put(c, right)`.
* Cập nhật độ dài lớn nhất: `maxLen = max(maxLen, right - left + 1)`.

### 📊 Các hướng tiếp cận

#### Cách 1: Sliding Window với HashSet (Co từ từ)
* **Mô tả**: Dùng HashSet lưu các ký tự trong window. Khi gặp trùng lặp, dùng vòng lặp `while` dịch `left` từng bước và xóa ký tự tại `left` khỏi Set cho đến khi hết trùng.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ vì mỗi con trỏ `left` và `right` di chuyển tối đa $n$ lần.
  * **Space Complexity**: $O(\min(m, n))$ với $m$ là kích thước bảng chữ cái.

#### Cách 2: Sliding Window với HashMap (Nhảy cóc - Optimal) ⭐
* **Mô tả**: Lưu index cuối cùng của mỗi ký tự để dịch thẳng `left` đến vị trí thích hợp mà không cần vòng lặp co dần.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt chuỗi đúng 1 lần.
  * **Space Complexity**: $O(\min(m, n))$.

### 🔄 Dry Run với ví dụ
* **Input**: `s = "abcabcbb"`
* **Khởi tạo**: `lastSeen = {}`, `left = 0`, `maxLen = 0`
* **Xử lý**:
  - `right = 0`, `'a'`: `lastSeen` không chứa `'a'` → `lastSeen = {'a': 0}`, `maxLen = max(0, 0-0+1) = 1`.
  - `right = 1`, `'b'`: `lastSeen` không chứa `'b'` → `lastSeen = {'a': 0, 'b': 1}`, `maxLen = max(1, 1-0+1) = 2`.
  - `right = 2`, `'c'`: `lastSeen = {'a': 0, 'b': 1, 'c': 2}`, `maxLen = max(2, 2-0+1) = 3`.
  - `right = 3`, `'a'`: `lastSeen` chứa `'a'` tại index 0 >= `left` (0) → dịch `left = 0 + 1 = 1`. Cập nhật `lastSeen.put('a', 3)`, `maxLen = max(3, 3-1+1) = 3`.
  - ... lặp tiếp đến hết.
* **Kết quả**: `3`

### 💻 Java Clean Code
```java
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int left = 0, maxLen = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
            left = lastSeen.get(c) + 1; // Nhảy biên trái qua ký tự trùng lặp cũ
        }
        lastSeen.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    
    return maxLen;
}
```

---

## 16. Longest Repeating Character Replacement (LeetCode #424) - Medium

### 💡 Ý tưởng cốt lõi
Tìm độ dài chuỗi con dài nhất chứa toàn ký tự giống nhau sau khi đã thay thế tối đa `k` ký tự bất kỳ.
Chúng ta sử dụng một cửa sổ trượt `[left..right]` và một mảng đếm tần suất chữ cái `count` kích thước 26.
*Ý tưởng mấu chốt*: Một cửa sổ `[left..right]` là hợp lệ nếu:
$$(\text{Độ dài cửa sổ} - \text{Tần suất của ký tự xuất hiện nhiều nhất}) \le k$$
Số ký tự cần thay thế chính là `(right - left + 1) - maxCount`.
Khi duyệt `right`, ta tăng đếm `count[s.charAt(right) - 'A']` và cập nhật tần suất lớn nhất `maxCount`.
* Nếu số ký tự cần thay thế vượt quá `k`, cửa sổ không hợp lệ. Ta co biên trái bằng cách giảm tần suất `count[s.charAt(left) - 'A']--` và tăng `left++`.
* Do ta chỉ quan tâm đến việc tìm cửa sổ **lớn hơn**, ta không cần giảm `maxCount` khi co cửa sổ (vì chỉ có `maxCount` lớn hơn mới tạo ra kết quả tối ưu hơn).

### 📊 Hướng tiếp cận tối ưu

#### Sliding Window với Max Frequency Tracking (Optimal) ⭐
* **Mô tả**: Duy trì mảng đếm tần suất chữ cái và thực hiện trượt cửa sổ kích thước tăng dần.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt chuỗi 1 lần.
  * **Space Complexity**: $O(1)$ vì mảng đếm tần suất luôn có kích thước cố định là 26.

### 🔄 Dry Run với ví dụ
* **Input**: `s = "AABABBA"`, `k = 1`
* **Khởi tạo**: `count = [0..0]`, `left = 0`, `maxCount = 0`, `maxLen = 0`
* **Xử lý**:
  - `right = 0` ('A'): `count['A'] = 1`, `maxCount = 1`. Độ dài = 1. Valid. `maxLen = 1`.
  - `right = 1` ('A'): `count['A'] = 2`, `maxCount = 2`. Độ dài = 2. Valid. `maxLen = 2`.
  - `right = 2` ('B'): `count['B'] = 1`, `maxCount = 2`. Độ dài = 3. Số thay thế: 3 - 2 = 1 <= 1 (Valid). `maxLen = 3`.
  - `right = 3` ('A'): `count['A'] = 3`, `maxCount = 3`. Độ dài = 4. Số thay thế: 4 - 3 = 1 <= 1 (Valid). `maxLen = 4`.
  - `right = 4` ('B'): `count['B'] = 2`, `maxCount = 3`. Độ dài = 5. Số thay thế: 5 - 3 = 2 > 1 (Invalid!).
    * Co biên trái: giảm `count[s[left]]` tức `count['A']` từ 3 về 2. Tăng `left` thành 1.
  - ... lặp tiếp đến hết.
* **Kết quả**: `4`

### 💻 Java Clean Code
```java
public int characterReplacement(String s, int k) {
    int[] count = new int[26];
    int left = 0, maxCount = 0, maxLen = 0;
    
    for (int right = 0; right < s.length(); right++) {
        maxCount = Math.max(maxCount, ++count[s.charAt(right) - 'A']);
        
        // Nếu số ký tự phải thay thế vượt quá k, co biên trái
        if ((right - left + 1) - maxCount > k) {
            count[s.charAt(left) - 'A']--;
            left++;
        }
        maxLen = Math.max(maxLen, right - left + 1);
    }
    
    return maxLen;
}
```

---

## 17. Minimum Window Substring (LeetCode #76) - Hard

### 💡 Ý tưởng cốt lõi
Tìm chuỗi con ngắn nhất trong chuỗi $s$ chứa tất cả các ký tự của chuỗi $t$ (bao gồm cả các ký tự trùng lặp).
Chúng ta sử dụng thuật toán **Sliding Window** duy trì 2 HashMap:
* `need`: Đếm tần suất các ký tự cần thiết từ chuỗi $t$.
* `window`: Đếm tần suất các ký tự hiện có trong cửa sổ trượt.
Ta dùng một biến `formed` để theo dõi số lượng ký tự độc nhất đã đạt đủ tần suất yêu cầu. Khi `formed == need.size()`, cửa sổ hiện tại đã hợp lệ (chứa đủ ký tự của $t$).
Khi cửa sổ hợp lệ, ta cố gắng thu nhỏ nó bằng cách dịch `left` sang phải để tìm chuỗi ngắn nhất, cho tới khi cửa sổ không còn hợp lệ.

### 📊 Hướng tiếp cận tối ưu

#### Two Maps Sliding Window (Optimal) ⭐
* **Mô tả**: Duyệt qua chuỗi $s$ mở rộng `right` để tìm cửa sổ hợp lệ đầu tiên, sau đó thu hẹp bằng cách dịch `left` để tối ưu kết quả.
* **Độ phức tạp**:
  * **Time Complexity**: $O(|s| + |t|)$ vì mỗi ký tự trong $s$ và $t$ được duyệt tối đa 2 lần.
  * **Space Complexity**: $O(|s| + |t|)$ để lưu trữ tần suất ký tự.

### 🔄 Dry Run với ví dụ
* **Input**: `s = "ADOBECODEBANC"`, `t = "ABC"`
* **Yêu cầu**: Cần có các chữ cái `'A'`, `'B'`, `'C'` mỗi chữ xuất hiện ít nhất 1 lần.
* **Xử lý**:
  - `right` trượt tới index 5 (`s[5] = 'C'`). Cửa sổ `[0..5]` chứa `"ADOBEC"` có đủ `'A'`, `'B'`, `'C'`. Cửa sổ hợp lệ.
  - Cố gắng thu nhỏ: dịch `left` từ 0 sang phải.
    * `left = 0` ('A'): sau khi bỏ 'A', cửa sổ không còn hợp lệ. Độ dài ghi nhận là 6 ("ADOBEC").
  - `right` tiếp tục trượt sang phải để tìm cửa sổ hợp lệ mới.
    * Trượt tới index 10 (`s[10] = 'B'`). Cửa sổ `[6..10]` là `"CODEB"` chứa đủ `'A'`, `'B'`, `'C'`.
  - ... dịch `left` tiếp để thu nhỏ.
  - Cửa sổ tối ưu nhất được tìm thấy ở cuối: `"BANC"` (index 9 đến 12) có độ dài 4.
* **Kết quả**: `"BANC"`

### 💻 Java Clean Code
```java
public String minWindow(String s, String t) {
    if (s.length() < t.length()) return "";
    
    Map<Character, Integer> need = new HashMap<>();
    for (char c : t.toCharArray()) {
        need.put(c, need.getOrDefault(c, 0) + 1);
    }
    
    Map<Character, Integer> window = new HashMap<>();
    int left = 0, formed = 0;
    int required = need.size();
    
    int minLen = Integer.MAX_VALUE;
    int startIdx = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        window.put(c, window.getOrDefault(c, 0) + 1);
        
        // Kiểm tra xem ký tự c có đạt đủ tần suất yêu cầu chưa
        if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
            formed++;
        }
        
        // Cố gắng thu hẹp cửa sổ khi đã chứa đủ ký tự của t
        while (formed == required) {
            int currentLen = right - left + 1;
            if (currentLen < minLen) {
                minLen = currentLen;
                startIdx = left;
            }
            
            char leftChar = s.charAt(left);
            window.put(leftChar, window.get(leftChar) - 1);
            if (need.containsKey(leftChar) && window.get(leftChar) < need.get(leftChar)) {
                formed--;
            }
            left++;
        }
    }
    
    return minLen == Integer.MAX_VALUE ? "" : s.substring(startIdx, startIdx + minLen);
}
```
