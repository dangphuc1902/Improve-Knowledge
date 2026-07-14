# 01 - Arrays & Hashing

## 📖 Tổng quan

**Arrays** là cấu trúc dữ liệu cơ bản nhất — lưu trữ phần tử liên tiếp trong bộ nhớ.  
**Hashing** (HashMap/HashSet) cho phép truy xuất O(1) bằng cách ánh xạ key → value thông qua hàm băm.

Đây là nền tảng của hầu hết các bài LeetCode và là topic **bắt buộc phải thành thạo**.

## 🧠 Kiến thức cốt lõi

### Array
| Thao tác | Time Complexity |
|----------|----------------|
| Truy cập `arr[i]` | O(1) |
| Tìm kiếm (unsorted) | O(n) |
| Thêm/Xóa cuối | O(1) amortized |
| Thêm/Xóa giữa | O(n) |

### HashMap / HashSet
| Thao tác | Average | Worst Case |
|----------|---------|------------|
| `put(key, val)` | O(1) | O(n) |
| `get(key)` | O(1) | O(n) |
| `containsKey()` | O(1) | O(n) |
| `remove(key)` | O(1) | O(n) |

> **Worst case O(n)** xảy ra khi có quá nhiều collision. Trong thực tế LeetCode, coi như O(1).

## 🔍 Khi nào sử dụng?

- Cần **đếm tần suất** xuất hiện → `HashMap<Element, Count>`
- Cần **kiểm tra tồn tại** nhanh → `HashSet`
- Cần **tìm cặp/nhóm** phần tử thỏa điều kiện → HashMap lưu complement
- Cần **nhóm phần tử** theo đặc điểm → HashMap với key là đặc trưng

## 📝 Các Pattern phổ biến

### Pattern 1: Frequency Count (Số lần xuất hiện)
- **Nó là gì?**: Sử dụng một cấu trúc dữ liệu (HashMap hoặc mảng tần suất) để lưu trữ số lần xuất hiện của các phần tử trong một tập dữ liệu.
- **Giải quyết bài toán nào?**: 
    - Kiểm tra xem hai chuỗi có phải là Anagram không (`Valid Anagram`).
    - Tìm phần tử xuất hiện nhiều nhất hoặc xuất hiện `k` lần (`Top K Frequent Elements`).
    - Kiểm tra sự tồn tại của các phần tử trùng lặp (`Contains Duplicate`).
- **Ưu điểm**:
    - Tốc độ truy xuất và cập nhật trung bình O(1), giúp tổng thể thuật toán đạt O(n).
    - Rất trực quan và dễ cài đặt.
- **Nhược điểm**:
    - Tốn thêm không gian bộ nhớ O(n) (hoặc O(k) với k là số lượng phần tử duy nhất).
    - HashMap có hằng số thời gian lớn hơn mảng đơn thuần.
- **Sự thay thế**:
    - **Sorting**: Sắp xếp mảng rồi duyệt qua để đếm các phần tử giống nhau liên tiếp (O(n log n) time, O(1) space).
    - **Frequency Array**: Nếu tập dữ liệu giới hạn (vd: chỉ gồm 26 chữ cái), dùng mảng `int[26]` thay vì HashMap để tối ưu tốc độ.

```java
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray()) {
    freq.put(c, freq.getOrDefault(c, 0) + 1);
}
```

### Pattern 2: Two-pass HashMap / Complement Search
- **Nó là gì?**: Duyệt qua mảng và sử dụng HashMap để lưu trữ các giá trị đã đi qua. Với mỗi phần tử mới, ta tìm kiếm xem "phần bù" (complement) cần thiết để thỏa mãn điều kiện đã có trong HashMap chưa.
- **Giải quyết bài toán nào?**: 
    - Tìm hai số có tổng bằng một giá trị cho trước (`Two Sum`).
    - Các bài toán tìm cặp hoặc bộ số thỏa mãn một phương trình logic.
- **Ưu điểm**:
    - Giảm độ phức tạp từ O(n²) xuống O(n).
    - Có thể giải quyết trong 1 lần duyệt (One-pass).
- **Nhược điểm**:
    - Yêu cầu thêm không gian O(n).
- **Sự thay thế**:
    - **Brute Force**: Dùng 2 vòng lặp lồng nhau (O(n²)).
    - **Two Pointers**: Nếu mảng đã được sắp xếp, dùng 2 con trỏ ở 2 đầu (Time O(n), Space O(1)).

```java
Map<Integer, Integer> map = new HashMap<>();
for (int i = 0; i < nums.length; i++) {
    int complement = target - nums[i];
    if (map.containsKey(complement)) { 
        return new int[]{map.get(complement), i};
    }
    map.put(nums[i], i);
}
```

### Pattern 3: Grouping by Key (Signature Pattern)
- **Nó là gì?**: Tạo ra một "chữ ký" (key) đại diện cho một nhóm các phần tử có chung đặc điểm, sau đó dùng HashMap để nhóm chúng lại.
- **Giải quyết bài toán nào?**: 
    - Nhóm các từ là Anagram của nhau (`Group Anagrams`).
    - Nhóm các điểm có cùng khoảng cách hoặc cùng tọa độ.
- **Ưu điểm**:
    - Phân loại dữ liệu cực kỳ nhanh chóng.
- **Nhược điểm**:
    - Việc tạo "key" (ví dụ: sắp xếp chuỗi hoặc tạo string từ mảng tần suất) có thể tốn thời gian.
- **Sự thay thế**:
    - So sánh từng cặp phần tử (O(n² * k)) - rất chậm khi n lớn.

```java
Map<String, List<String>> groups = new HashMap<>();
for (String s : strs) {
    char[] chars = s.toCharArray();
    Arrays.sort(chars);
    String key = new String(chars);
    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
}
```

## 🎯 Các ví dụ chi tiết

### Ví dụ 1: Two Sum - step by step
```
Input: nums = [2, 7, 11, 15], target = 9

Bước 1: i=0, nums[0]=2
  complement = 9 - 2 = 7
  map.containsKey(7)? NO
  thêm {2: 0} vào map
  map = {2: 0}

Bước 2: i=1, nums[1]=7
  complement = 9 - 7 = 2
  map.containsKey(2)? YES! ✓
  return [map.get(2), 1] = [0, 1]
  
✅ Output: [0, 1] (nums[0] + nums[1] = 2 + 7 = 9)
```

**Tại sao HashMap hiệu quả?**
- Brute force O(n²): kiểm tra mọi cặp → [2,7], [2,11], [2,15], [7,11], [7,15], [11,15]
- HashMap O(n): 1 lần duyệt, mỗi phần tử check complement trong O(1)

---

### Ví dụ 2: Group Anagrams - step by step
```
Input: ["eat", "tea", "ate", "tan", "nat", "bat"]

Xử lý từng string:
1. "eat" → toCharArray: [e,a,t] → sort: [a,e,t] → key="aet"
   map = {"aet": ["eat"]}

2. "tea" → sort: [a,e,t] → key="aet"
   map = {"aet": ["eat", "tea"]}

3. "ate" → sort: [a,e,t] → key="aet"
   map = {"aet": ["eat", "tea", "ate"]}

4. "tan" → sort: [a,n,t] → key="ant"
   map = {"aet": ["eat", "tea", "ate"], "ant": ["tan"]}

5. "nat" → sort: [a,n,t] → key="ant"
   map = {"aet": [...], "ant": ["tan", "nat"]}

6. "bat" → sort: [a,b,t] → key="abt"
   map = {"aet": [...], "ant": [...], "abt": ["bat"]}

✅ Output: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
```

**Insight:** Hai string là anagram ⟺ sorted form giống nhau!

---

### Ví dụ 3: Top K Frequent Elements - step by step
```
Input: nums = [1,1,1,2,2,3,3,3,3], k = 2

Bước 1: Đếm tần suất
  freqMap = {1: 3, 2: 2, 3: 4}

Bước 2: Tạo Bucket Sort
  bucket[i] = danh sách số xuất hiện i lần
  
  bucket[0] = []
  bucket[1] = []
  bucket[2] = [2]        ← 2 xuất hiện 2 lần
  bucket[3] = [1]        ← 1 xuất hiện 3 lần
  bucket[4] = [3]        ← 3 xuất hiện 4 lần
  bucket[5] = []
  bucket[6] = []
  bucket[7] = []
  bucket[8] = []
  bucket[9] = []

Bước 3: Duyệt từ bucket cao nhất, lấy k=2 phần tử
  i=9: bucket[9]=[] (empty)
  i=8: bucket[8]=[] (empty)
  ...
  i=4: bucket[4]=[3] → result[0]=3 ✓ (idx=0<k)
  i=3: bucket[3]=[1] → result[1]=1 ✓ (idx=1<k)
  i=2: đủ k phần tử, dừng

✅ Output: [3, 1]  (hoặc [1, 3] - order không quan trọng)
```

**Tại sao Bucket Sort O(n)?**
- Heap approach: O(n log k)
- Bucket: O(n) vì ta biết max frequency = n

---

## 🔄 So sánh các Approach

### Two Sum: HashMap vs Sorting vs Brute Force
| Approach | Time | Space | Ưu điểm | Nhược điểm |
|----------|------|-------|---------|------------|
| HashMap ⭐ | O(n) | O(n) | 1 pass, tìm ngay | Dùng thêm space |
| Sorting + 2 Pointers | O(n log n) | O(1) | Không dùng space | Mất order |
| Brute Force | O(n²) | O(1) | Đơn giản | Quá chậm, failed |

### Group Anagrams: Sorted vs Count Array
| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Sorted String (current) | O(n*k log k) | O(n*k) | Simple, easy to understand |
| Count Array [26] | O(n*k) | O(n*k) | Slightly faster, same space |

> k = độ dài string.

### Top K: Bucket Sort vs Heap vs Quick Select
| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| Bucket Sort ⭐ | O(n) | O(n) | k bất kỳ, simple |
| Min Heap | O(n log k) | O(k) | k nhỏ << n |
| Quick Select | O(n) avg | O(1) | Cân bằng, trên practice |

---

## 🚨 Edge Cases cần chú ý

```java
// Two Sum Edge Cases:
// 1. nums = [3], target = 6 → [] (chỉ 1 phần tử)
// 2. nums = [-1, -1], target = -2 → [0, 1] (âm số)
// 3. nums = [1, 1, 1, 1], target = 2 → [0, 1] (duplicate)

// Group Anagrams Edge Cases:
// 1. strs = [""] → [[""]] (chuỗi rỗng)
// 2. strs = ["a"] → [["a"]] (1 phần tử)
// 3. strs = ["a", "a", "a"] → [["a", "a", "a"]] (tất cả giống)

// Top K Edge Cases:
// 1. nums = [1], k = 1 → [1]
// 2. k = size(unique) → return tất cả
// 3. Duplicate frequency: nums = [1,1,2,2,3], k=2 → [1,2] hoặc [2,1]
```

## ⏱️ Complexity thường gặp

| Approach | Time | Space |
|----------|------|-------|
| Brute force (2 vòng lặp) | O(n²) | O(1) |
| HashMap 1 pass | O(n) | O(n) |
| Sorting + scan | O(n log n) | O(1) |

## 💡 Tips phỏng vấn

1. **Luôn hỏi**: Input có sorted không? Có duplicate không? Có số âm không?
2. **Trade-off**: Giải thích rõ bạn đang đánh đổi space để được time tốt hơn
3. **Edge cases**: Array rỗng, 1 phần tử, tất cả giống nhau
4. **Java cụ thể**: Dùng `getOrDefault()`, `computeIfAbsent()` để code gọn hơn
