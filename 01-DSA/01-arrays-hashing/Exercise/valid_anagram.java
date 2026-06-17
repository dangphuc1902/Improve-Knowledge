package Exercise;

import java.util.Arrays;

// Cho hai chuỗi s và t, hãy xác định xem t có phải là anagram của s không.

// Một anagram của một chuỗi là chuỗi được tạo ra bằng cách sắp xếp lại các ký
// tự của chuỗi gốc.
// Nói cách khác, hai chuỗi là anagram nếu chúng có cùng ký tự với cùng số lần
// xuất hiện.
/*
Cách 1: Brute Force
Ý tưởng

Duyệt từng ký tự trong s.

Tìm ký tự tương ứng trong t.

Sau khi dùng thì đánh dấu.

Ví dụ

s = "rat"
t = "tar"

Tìm:

r

trong t

Tìm:

a

trong t

Tìm:

t

trong t

Code

Ý tưởng:

for each char in s
    search in t
Big O

Time:

O(n²)

Space:

O(1)
Nhược điểm

Rất chậm.

Cách 2: Sort rồi so sánh

Đây là cách phổ biến nhất mà nhiều người nghĩ tới đầu tiên.

Ý tưởng

Nếu là anagram thì sau khi sort:

anagram

↓

aaagmnr
nagaram

↓

aaagmnr

Nếu giống nhau:

return true
*/

// public class sort_valid_anagram {
//     public boolean isAnagram(String s, String t) {
//         if (s.length() != t.length()) {
//             return false;
//         }

//         // Sort both strings and compare them
//         char[] sChars = s.toCharArray();
//         char[] tChars = t.toCharArray();

//         Arrays.sort(sChars);
//         Arrays.sort(tChars);

//         return Arrays.equals(sChars, tChars);
//     }
// }

/*
 * Cách 3: HashMap Frequency Count
 *
 * Pattern quan trọng nhất.
 *
 * Ý tưởng
 *
 * Đếm số lần xuất hiện từng ký tự.
 *
 * Ví dụ:
 *
 * anagram
 *
 * HashMap:
 *
 * a -> 3
 * n -> 1
 * g -> 1
 * r -> 1
 * m -> 1
 *
 * Sau đó trừ dần theo t.
 *
 * Nếu cuối cùng mọi count đều bằng:
 *
 * 0
 *
 * => anagram
 */

import java.util.HashMap;
import java.util.Map;

public class valid_anagram {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        // Create a HashMap to store character counts
        Map<Character, Integer> charCount = new HashMap<>();

        // Count characters in string s
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }

        // Decrement counts based on string t
        for (char c : t.toCharArray()) {
            if (!charCount.containsKey(c)) {
                return false; // Character not present in s
            }
            int count = charCount.get(c);
            if (count == 1) {
                charCount.remove(c);
            } else {
                charCount.put(c, count - 1);
            }
        }

        // If HashMap is empty, all characters matched
        return charCount.isEmpty();
    }
}

/*
 * Cách 4: Frequency Array (BEST)
 * 
 * Đây là lời giải tối ưu nhất cho đề bài hiện tại.
 * 
 * Vì đề nói:
 * 
 * lowercase English letters
 * 
 * Chỉ có:
 * 
 * a-z
 * 
 * =
 * 
 * 26 ký tự
 * Ý tưởng
 * 
 * Tạo mảng:
 * 
 * int[26]
 * 
 * Đếm ký tự của s:
 * 
 * count[c - 'a']++
 * 
 * Trừ ký tự của t:
 * 
 * count[c - 'a']--
 * 
 * Nếu cuối cùng tất cả bằng 0:
 * 
 * true
 */

class Solution {

    public boolean isAnagram(String s, String t) {

        if (s.length() != t.length())
            return false;

        int[] count = new int[26];

        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }

        for (char c : t.toCharArray()) {
            count[c - 'a']--;
        }

        for (int num : count) {

            if (num != 0)
                return false;
        }

        return true;
    }
}