# 01 - Arrays & Hashing: Detailed Solutions

Tài liệu này cung cấp lý giải lý thuyết, phân tích hướng tiếp cận tối ưu và Dry Run chi tiết cho toàn bộ 8 bài toán thuộc chủ đề **Arrays & Hashing** từ LeetCode Master Tracker.

---

## 1. Two Sum (LeetCode #1) - Easy

### 💡 Ý tưởng cốt lõi
Với mỗi phần tử `nums[i]`, ta cần tìm một phần tử khác `nums[j]` sao cho `nums[i] + nums[j] = target`. Phương trình tương đương với `nums[j] = target - nums[i]`. Bằng cách sử dụng **HashMap**, ta có thể lưu trữ các số đã đi qua dưới dạng `Key` và chỉ số của chúng dưới dạng `Value`. Khi duyệt qua phần tử mới, ta chỉ cần kiểm tra xem `target - nums[i]` đã có trong HashMap chưa với độ phức tạp $O(1)$.

### 📊 Các hướng tiếp cận

#### Cách 1: Brute Force
* **Mô tả**: Dùng 2 vòng lặp lồng nhau duyệt qua mọi cặp `(i, j)` để kiểm tra tổng.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2)$ do duyệt mọi cặp phần tử.
  * **Space Complexity**: $O(1)$ không dùng thêm bộ nhớ phụ.

#### Cách 2: HashMap (Optimal) ⭐
* **Mô tả**: Duyệt mảng một lần. Ở mỗi bước, tính toán giá trị bù (`complement = target - nums[i]`). Kiểm tra trong HashMap, nếu có thì trả về ngay. Ngược lại, thêm số hiện tại vào map.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt qua mảng đúng 1 lần và truy vấn HashMap trung bình mất $O(1)$.
  * **Space Complexity**: $O(n)$ để lưu trữ dữ liệu trong HashMap.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [2, 7, 11, 15]`, `target = 9`
* **Khởi tạo**: `map = {}`
* **Vòng lặp**:
  1. `i = 0`, `nums[0] = 2`:
     * `complement = 9 - 2 = 7`
     * `map` chứa `7` không? **Không**.
     * Đưa `2` vào map: `map = {2: 0}`
  2. `i = 1`, `nums[1] = 7`:
     * `complement = 9 - 7 = 2`
     * `map` chứa `2` không? **Có** (ở index `0`).
     * Trả về kết quả: `[0, 1]`

### 💻 Java Clean Code
```java
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
```

---

## 2. Contains Duplicate (LeetCode #217) - Easy

### 💡 Ý tưởng cốt lõi
Kiểm tra xem một mảng có chứa bất kỳ phần tử nào xuất hiện ít nhất hai lần hay không. Sử dụng **HashSet** giúp lưu trữ các phần tử độc nhất. Khi duyệt qua từng số, nếu số đó đã tồn tại trong HashSet, tức là mảng có phần tử trùng lặp.

### 📊 Các hướng tiếp cận

#### Cách 1: Brute Force
* **Mô tả**: Dùng 2 vòng lặp lồng nhau kiểm tra mọi cặp phần tử xem có bằng nhau không.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n^2)$
  * **Space Complexity**: $O(1)$

#### Cách 2: Sắp xếp (Sorting)
* **Mô tả**: Sắp xếp mảng theo thứ tự tăng dần. Sau đó duyệt một vòng lặp kiểm tra xem hai phần tử kề nhau có bằng nhau không (`nums[i] == nums[i-1]`).
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ do phải sắp xếp.
  * **Space Complexity**: $O(1)$ hoặc $O(n)$ tùy thuộc thuật toán sắp xếp (ở Java, Dual-Pivot Quicksort tốn $O(\log n)$ call stack space).

#### Cách 3: HashSet (Optimal) ⭐
* **Mô tả**: Duyệt qua mảng và thêm từng phần tử vào HashSet. Nếu hàm `add()` trả về `false` (phần tử đã tồn tại), trả về `true` ngay lập tức.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ duyệt mảng 1 lần.
  * **Space Complexity**: $O(n)$ lưu trữ các phần tử trong HashSet.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [1, 2, 3, 1]`
* **Khởi tạo**: `set = {}`
* **Duyệt mảng**:
  * `num = 1`: `set.add(1)` thành công. `set = {1}`
  * `num = 2`: `set.add(2)` thành công. `set = {1, 2}`
  * `num = 3`: `set.add(3)` thành công. `set = {1, 2, 3}`
  * `num = 1`: `set.contains(1)` là `true`, `set.add(1)` trả về `false`.
  * **Kết quả**: `true`

### 💻 Java Clean Code
```java
public boolean containsDuplicate(int[] nums) {
    Set<Integer> set = new HashSet<>();
    for (int num : nums) {
        if (!set.add(num)) {
            return true; // add trả về false nghĩa là phần tử đã tồn tại
        }
    }
    return false;
}
```

---

## 3. Valid Anagram (LeetCode #242) - Easy

### 💡 Ý tưởng cốt lõi
Hai chuỗi $s$ và $t$ là Anagram của nhau nếu chúng có độ dài bằng nhau và số lần xuất hiện của mỗi ký tự trong cả hai chuỗi là hoàn toàn giống nhau. Do tập ký tự thường giới hạn trong 26 chữ cái Latin thường (`'a'` đến `'z'`), ta có thể dùng một mảng tần suất kích thước 26 thay vì dùng HashMap để tối ưu hóa bộ nhớ và tốc độ.

### 📊 Các hướng tiếp cận

#### Cách 1: Sorting
* **Mô tả**: Chuyển cả hai chuỗi thành mảng ký tự, sắp xếp lại và so sánh xem chúng có bằng nhau không.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log n)$ với $n$ là độ dài chuỗi.
  * **Space Complexity**: $O(n)$ để chứa mảng ký tự sau khi chuyển từ String.

#### Cách 2: Frequency Array (Optimal) ⭐
* **Mô tả**: Khởi tạo mảng `count` có kích thước 26. Duyệt qua chuỗi $s$ để cộng tần suất (`count[c - 'a']++`) và chuỗi $t$ để trừ tần suất (`count[c - 'a']--`). Cuối cùng, nếu tất cả các phần tử trong mảng `count` đều bằng 0 thì hai chuỗi là Anagram.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ với $n$ là độ dài chuỗi.
  * **Space Complexity**: $O(1)$ vì kích thước mảng tần suất luôn là hằng số cố định 26.

### 🔄 Dry Run với ví dụ
* **Input**: `s = "anagram"`, `t = "nagaram"`
* **Khởi tạo**: `count = [0, 0, ..., 0]` (26 phần tử)
* **Xử lý chuỗi**:
  * Duyệt chuỗi `s` tăng tần suất:
    * `'a'` → index 0: `count[0]` tăng thành 3
    * `'n'` → index 13: `count[13]` tăng thành 1
    * `'g'` → index 6: `count[6]` tăng thành 1
    * `'r'` → index 17: `count[17]` tăng thành 1
    * `'m'` → index 12: `count[12]` tăng thành 1
  * Duyệt chuỗi `t` giảm tần suất:
    * `'n'` → index 13: `count[13]` giảm về 0
    * `'a'` → index 0: `count[0]` giảm 3 lần về 0
    * `'g'` → index 6: `count[6]` giảm về 0
    * `'r'` → index 17: `count[17]` giảm về 0
    * `'m'` → index 12: `count[12]` giảm về 0
  * Kiểm tra mảng: Tất cả đều là `0`.
  * **Kết quả**: `true`

### 💻 Java Clean Code
```java
public boolean isValidAnagram(String s, String t) {
    if (s.length() != t.length()) {
        return false;
    }
    
    int[] count = new int[26];
    for (int i = 0; i < s.length(); i++) {
        count[s.charAt(i) - 'a']++;
        count[t.charAt(i) - 'a']--;
    }
    
    for (int c : count) {
        if (c != 0) {
            return false;
        }
    }
    return true;
}
```

---

## 4. Group Anagrams (LeetCode #49) - Medium

### 💡 Ý tưởng cốt lõi
Nhóm các chuỗi là Anagram của nhau vào cùng một danh sách. Do các từ là Anagram có chung một dạng chuẩn sau khi sắp xếp (canonical form), ta có thể sử dụng chuỗi đã sắp xếp này làm `Key` cho một **HashMap**, và `Value` là danh sách các chuỗi gốc có cùng `Key` đó.

### 📊 Hướng tiếp cận tối ưu

#### Sắp xếp ký tự làm Key (Optimal) ⭐
* **Mô tả**: Với mỗi chuỗi trong mảng đầu vào, biến đổi nó thành mảng ký tự `char[]`, sắp xếp mảng đó rồi chuyển ngược lại thành một `String key`. Đưa từ gốc vào danh sách tương ứng với `key` này trong HashMap.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \cdot k \log k)$ với $n$ là số lượng chuỗi và $k$ là độ dài lớn nhất của một chuỗi.
  * **Space Complexity**: $O(n \cdot k)$ để lưu trữ dữ liệu nhóm trong HashMap.

### 🔄 Dry Run với ví dụ
* **Input**: `strs = ["eat", "tea", "tan", "ate", "nat", "bat"]`
* **Xử lý**:
  1. `"eat"` → sorted: `"aet"` → `map = {"aet": ["eat"]}`
  2. `"tea"` → sorted: `"aet"` → `map = {"aet": ["eat", "tea"]}`
  3. `"tan"` → sorted: `"ant"` → `map = {"aet": ["eat", "tea"], "ant": ["tan"]}`
  4. `"ate"` → sorted: `"aet"` → `map = {"aet": ["eat", "tea", "ate"], "ant": ["tan"]}`
  5. `"nat"` → sorted: `"ant"` → `map = {"aet": ["eat", "tea", "ate"], "ant": ["tan", "nat"]}`
  6. `"bat"` → sorted: `"abt"` → `map = {"aet": [...], "ant": [...], "abt": ["bat"]}`
* **Kết quả**: `[["eat","tea","ate"], ["tan","nat"], ["bat"]]`

### 💻 Java Clean Code
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
```

---

## 5. Top K Frequent Elements (LeetCode #347) - Medium

### 💡 Ý tưởng cốt lõi
Tìm $k$ phần tử có tần suất xuất hiện cao nhất trong mảng. Thay vì sắp xếp các cặp số theo tần suất mất $O(n \log n)$, ta có thể sử dụng thuật toán **Bucket Sort** để gom nhóm các phần tử có cùng tần suất. Tần suất tối đa của một số là $n$ (độ dài mảng), do đó ta tạo một mảng các danh sách có kích thước $n+1$. Sau đó duyệt ngược từ tần suất cao nhất về thấp nhất để lấy đủ $k$ phần tử.

### 📊 Các hướng tiếp cận

#### Cách 1: Heap (Priority Queue)
* **Mô tả**: Đếm tần suất bằng HashMap, sau đó đẩy các phần tử vào một Min Heap kích thước $k$.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n \log k)$ vì thao tác đẩy vào heap kích thước $k$ mất $O(\log k)$.
  * **Space Complexity**: $O(n)$ lưu trữ map tần suất và heap.

#### Cách 2: Bucket Sort (Optimal) ⭐
* **Mô tả**: Tạo mảng các bucket `List<Integer>[] bucket` với index của mảng đại diện cho tần suất xuất hiện. Duyệt ngược từ cuối mảng để nhặt các số có tần suất lớn nhất.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do chỉ duyệt qua mảng đầu vào và mảng bucket tuyến tính.
  * **Space Complexity**: $O(n)$ chứa map tần suất và mảng các bucket.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [1, 1, 1, 2, 2, 3]`, `k = 2`
* **Xử lý**:
  1. Đếm tần suất: `freqMap = {1: 3, 2: 2, 3: 1}`.
  2. Tạo bucket kích thước 7 (chỉ số 0 đến 6):
     * `bucket[1] = [3]`
     * `bucket[2] = [2]`
     * `bucket[3] = [1]`
     * Các bucket khác rỗng.
  3. Lấy kết quả bằng cách duyệt ngược từ `i = 6` về `0`:
     * `i = 3`: thêm `1` vào kết quả. `res = [1]`, count = 1.
     * `i = 2`: thêm `2` vào kết quả. `res = [1, 2]`, count = 2.
     * Đạt đủ `k = 2` phần tử, dừng lại.
* **Kết quả**: `[1, 2]`

### 💻 Java Clean Code
```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freqMap = new HashMap<>();
    for (int num : nums) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }
    
    List<Integer>[] bucket = new List[nums.length + 1];
    for (int i = 0; i < bucket.length; i++) {
        bucket[i] = new ArrayList<>();
    }
    
    for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
        int freq = entry.getValue();
        bucket[freq].add(entry.getKey());
    }
    
    int[] result = new int[k];
    int idx = 0;
    for (int i = bucket.length - 1; i >= 0 && idx < k; i--) {
        for (int num : bucket[i]) {
            result[idx++] = num;
            if (idx == k) break;
        }
    }
    return result;
}
```

---

## 6. Product of Array Except Self (LeetCode #238) - Medium

### 💡 Ý tưởng cốt lõi
Tính mảng kết quả sao cho phần tử tại chỉ số `i` bằng tích của tất cả các phần tử trong mảng ngoại trừ `nums[i]`, không sử dụng phép chia và đạt độ phức tạp thời gian $O(n)$.
Ý tưởng là tích của mọi số ngoại trừ chính nó có thể được tách làm hai phần: **Tích các phần tử bên trái** (prefix product) và **Tích các phần tử bên phải** (suffix product).
$$result[i] = prefix[i] \times suffix[i]$$

### 📊 Hướng tiếp cận tối ưu

#### Tính trực tiếp Prefix và Suffix in-place (Optimal) ⭐
* **Mô tả**:
  1. Tạo mảng kết quả `res`.
  2. Quét từ trái sang phải, lưu tích lũy của các số đứng trước `i` vào `res[i]`.
  3. Quét ngược từ phải sang trái, nhân dồn tích lũy của các số đứng sau `i` vào `res[i]`.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ do duyệt qua mảng đúng 2 lần.
  * **Space Complexity**: $O(1)$ nếu không tính bộ nhớ của mảng kết quả đầu ra (yêu cầu đề bài).

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [1, 2, 3, 4]`
* **Khởi tạo**: `res = [1, 1, 1, 1]`, tích lũy bên trái `prefix = 1`.
* **Pass 1 (Từ trái sang phải)**:
  * `i = 0`: `res[0] = prefix = 1`. Cập nhật `prefix = prefix * nums[0] = 1 * 1 = 1`.
  * `i = 1`: `res[1] = prefix = 1`. Cập nhật `prefix = prefix * nums[1] = 1 * 2 = 2`.
  * `i = 2`: `res[2] = prefix = 2`. Cập nhật `prefix = prefix * nums[2] = 2 * 3 = 6`.
  * `i = 3`: `res[3] = prefix = 6`. Cập nhật `prefix = prefix * nums[3] = 6 * 4 = 24`.
  * Sau Pass 1: `res = [1, 1, 2, 6]`.
* **Pass 2 (Từ phải sang trái)**: Khởi tạo tích lũy bên phải `suffix = 1`.
  * `i = 3`: `res[3] = res[3] * suffix = 6 * 1 = 6`. Cập nhật `suffix = suffix * nums[3] = 1 * 4 = 4`.
  * `i = 2`: `res[2] = res[2] * suffix = 2 * 4 = 8`. Cập nhật `suffix = suffix * nums[2] = 4 * 3 = 12`.
  * `i = 1`: `res[1] = res[1] * suffix = 1 * 12 = 12`. Cập nhật `suffix = suffix * nums[1] = 12 * 2 = 24`.
  * `i = 0`: `res[0] = res[0] * suffix = 1 * 24 = 24`.
* **Kết quả**: `res = [24, 12, 8, 6]`

### 💻 Java Clean Code
```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] res = new int[n];
    
    // Bước 1: Tính toán prefix product cho mỗi vị trí
    res[0] = 1;
    for (int i = 1; i < n; i++) {
        res[i] = res[i - 1] * nums[i - 1];
    }
    
    // Bước 2: Tính toán suffix product và nhân trực tiếp vào kết quả
    int suffix = 1;
    for (int i = n - 1; i >= 0; i--) {
        res[i] *= suffix;
        suffix *= nums[i]; // Cập nhật tích lũy suffix từ phải qua
    }
    
    return res;
}
```

---

## 7. Longest Consecutive Sequence (LeetCode #128) - Medium

### 💡 Ý tưởng cốt lõi
Tìm độ dài của chuỗi số nguyên liên tiếp dài nhất trong một mảng chưa được sắp xếp. Thời gian chạy yêu cầu là $O(n)$.
Bằng cách đưa tất cả phần tử vào một **HashSet**, ta có thể kiểm tra sự tồn tại của một số trong $O(1)$. Để tìm chuỗi liên tiếp hiệu quả mà không lặp lại kiểm tra các số nằm giữa chuỗi, ta chỉ bắt đầu đếm khi tìm thấy **phần tử bắt đầu của chuỗi** (một số `num` là phần tử bắt đầu nếu `num - 1` không tồn tại trong HashSet).

### 📊 Hướng tiếp cận tối ưu

#### HashSet & Start Checker (Optimal) ⭐
* **Mô tả**:
  1. Đưa tất cả phần tử vào HashSet để loại bỏ trùng lặp và hỗ trợ tìm kiếm $O(1)$.
  2. Duyệt qua từng số `num`. Nếu `num - 1` không có trong Set, có nghĩa là `num` là điểm bắt đầu của một chuỗi liên tiếp tiềm năng.
  3. Từ số bắt đầu này, liên tục kiểm tra `num + 1`, `num + 2`,... trong Set để tính độ dài chuỗi và cập nhật độ dài lớn nhất.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ vì mỗi phần tử chỉ được duyệt tối đa 2 lần (1 lần trong vòng lặp ngoài, và tối đa 1 lần trong vòng lặp `while` tìm chuỗi liên tiếp).
  * **Space Complexity**: $O(n)$ để lưu các phần tử trong HashSet.

### 🔄 Dry Run với ví dụ
* **Input**: `nums = [100, 4, 200, 1, 3, 2]`
* **HashSet**: `set = {100, 4, 200, 1, 3, 2}`
* **Duyệt qua Set**:
  * `num = 100`: `99` có trong Set không? **Không** → `100` là điểm bắt đầu.
    * Check `101`? Không. Độ dài chuỗi = 1. Max = 1.
  * `num = 4`: `3` có trong Set không? **Có** → Bỏ qua vì `4` không phải điểm bắt đầu.
  * `num = 200`: `199` có trong Set không? **Không** → `200` là điểm bắt đầu.
    * Check `201`? Không. Độ dài chuỗi = 1. Max = 1.
  * `num = 1`: `0` có trong Set không? **Không** → `1` là điểm bắt đầu.
    * Check `2`? Có. Check `3`? Có. Check `4`? Có. Check `5`? Không.
    * Chuỗi liên tiếp: `1 -> 2 -> 3 -> 4`. Độ dài = 4. Max = 4.
  * `num = 3`: `2` có trong Set không? **Có** → Bỏ qua.
  * `num = 2`: `1` có trong Set không? **Có** → Bỏ qua.
* **Kết quả**: `4`

### 💻 Java Clean Code
```java
public int longestConsecutive(int[] nums) {
    Set<Integer> set = new HashSet<>();
    for (int num : nums) {
        set.add(num);
    }
    
    int longest = 0;
    for (int num : set) {
        // Chỉ bắt đầu đếm nếu num là điểm bắt đầu của một chuỗi liên tiếp
        if (!set.contains(num - 1)) {
            int currentNum = num;
            int currentStreak = 1;
            
            while (set.contains(currentNum + 1)) {
                currentNum += 1;
                currentStreak += 1;
            }
            
            longest = Math.max(longest, currentStreak);
        }
    }
    return longest;
}
```

---

## 8. Encode and Decode Strings (LeetCode #271) - Medium

### 💡 Ý tưởng cốt lõi
Thiết kế thuật toán mã hóa danh sách các chuỗi thành một chuỗi duy nhất, và giải mã chuỗi đó ngược lại thành danh sách ban đầu.
Khó khăn lớn nhất là các chuỗi gốc có thể chứa bất kỳ ký tự đặc biệt nào (bao gồm cả dấu phẩy, khoảng trắng hay ký tự phân cách).
Giải pháp tối ưu là sử dụng kỹ thuật **Length Prefix (Tiền tố độ dài)** kết hợp với một ký tự phân cách cố định, ví dụ `[length]#[string]`. Ví dụ, từ `"hello"` được mã hóa thành `"5#hello"`. Khi giải mã, ta đọc độ dài trước ký tự `#`, sau đó dịch chuyển con trỏ đúng bằng độ dài đó để trích xuất chuỗi gốc.

### 📊 Hướng tiếp cận tối ưu

#### Length Prefix Pattern (Optimal) ⭐
* **Mô tả**:
  * **Encode**: Duyệt qua danh sách các chuỗi, ghép chuỗi theo định dạng `s.length() + "#" + s`.
  * **Decode**: Quét qua chuỗi đã mã hóa bằng một con trỏ `i`. Tại mỗi điểm, tìm vị trí ký tự `#` tiếp theo, đọc số nằm trước nó để biết độ dài `len` của chuỗi, sau đó cắt chuỗi từ vị trí sau dấu `#` với độ dài `len`. Cập nhật `i` nhảy qua phần đã xử lý.
* **Độ phức tạp**:
  * **Time Complexity**: $O(n)$ cho cả hai quá trình mã hóa và giải mã với $n$ là tổng độ dài các chuỗi.
  * **Space Complexity**: $O(n)$ cho chuỗi trung gian hoặc mảng kết quả.

### 🔄 Dry Run với ví dụ
* **Input**: `strs = ["lint", "code", "love", "you"]`
* **Encode**:
  * `"lint"` → `"4#lint"`
  * `"code"` → `"4#code"`
  * `"love"` → `"4#love"`
  * `"you"` → `"3#you"`
  * Chuỗi mã hóa cuối cùng: `"4#lint4#code4#love3#you"`
* **Decode**:
  * Bắt đầu với `i = 0`. Chuỗi `s = "4#lint4#code..."`
  * Tìm `#` đầu tiên từ `i = 0`: nằm ở index `1`.
  * Đọc độ dài: từ index `0` đến `1` là `"4"`. Độ dài `len = 4`.
  * Lấy chuỗi: cắt từ index `1 + 1 = 2` với độ dài `4` → thu được `"lint"`.
  * Cập nhật `i = 2 + 4 = 6`.
  * Tìm `#` tiếp theo từ `i = 6`: nằm ở index `7`.
  * Đọc độ dài: từ index `6` đến `7` là `"4"`. Độ dài `len = 4`.
  * Lấy chuỗi: cắt từ index `7 + 1 = 8` với độ dài `4` → thu được `"code"`.
  * Cập nhật `i = 8 + 4 = 12`.
  * Lặp lại đến hết chuỗi.
* **Kết quả**: `["lint", "code", "love", "you"]`

### 💻 Java Clean Code
```java
class Codec {
    // Encodes a list of strings to a single string.
    public String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(s.length()).append('#').append(s);
        }
        return sb.toString();
    }

    // Decodes a single string to a list of strings.
    public List<String> decode(String s) {
        List<String> res = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            // Tìm ký tự phân tách '#' để xác định phần độ dài tiền tố kết thúc
            int hashIndex = s.indexOf('#', i);
            int len = Integer.parseInt(s.substring(i, hashIndex));
            
            // Lấy ra chuỗi con dựa vào độ dài vừa đọc được
            int start = hashIndex + 1;
            res.add(s.substring(start, start + len));
            
            // Dịch con trỏ i đến điểm bắt đầu của chuỗi tiếp theo
            i = start + len;
        }
        return res;
    }
}
```
