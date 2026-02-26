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

### Pattern 1: Frequency Count
```java
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray()) {
    freq.put(c, freq.getOrDefault(c, 0) + 1);
}
```

### Pattern 2: Two-pass HashMap (TwoSum)
```java
// Pass 1: Lưu tất cả vào map
// Pass 2: Tìm complement trong map
Map<Integer, Integer> map = new HashMap<>();
for (int i = 0; i < nums.length; i++) {
    int complement = target - nums[i];
    if (map.containsKey(complement)) {
        return new int[]{map.get(complement), i};
    }
    map.put(nums[i], i);
}
```

### Pattern 3: Grouping by key
```java
// Nhóm anagram bằng sorted string làm key
Map<String, List<String>> groups = new HashMap<>();
for (String s : strs) {
    char[] chars = s.toCharArray();
    Arrays.sort(chars);
    String key = new String(chars);
    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
}
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
