// Given an integer x, return true if x is a palindrome, and false otherwise.
// Example 1:
// Input: x = 121
// Output: true
// Explanation: 121 reads as 121 from left to right and from right to left.
// Example 2:
// Input: x = -121
// Output: false
// Explanation: From left to right, it reads -121. From right to left, it becomes 121-. Therefore it is not a palindrome.
// Example 3:
// Input: x = 10
// Output: false
// Explanation: Reads 01 from right to left. Therefore it is not a palindrome.
public class palindrome_num {
    // public boolean isPalindrome(int x) {
    //     if(x < 0) return false;
    //     int nguyen_ban = x;
    //     int dao_nguoc = 0;
    //     while(x > 0){
    //         int a = x % 10;
    //         int b = x / 10;
    //         dao_nguoc = dao_nguoc * 10 + a;
    //         x = b;
    //     }
    //     return nguyen_ban == dao_nguoc;
    // }
    public boolean isPalindrome(int x) {
        // Kiểm tra điều kiện loại trừ
        if (x < 0 || (x % 10 == 0 && x != 0)) return false;
        
        int reversedHalf = 0;  // Biến lưu nửa số đảo ngược
        
        while (x > reversedHalf) {  // Lặp cho đến khi x <= reversedHalf
            reversedHalf = reversedHalf * 10 + x % 10;  // Lấy chữ số cuối của x, thêm vào reversedHalf
            x /= 10;  // Xóa chữ số cuối của x
        }
        
        // x == reversedHalf: cho các số có lẻ chữ số (ví dụ: 12321)
        // x == reversedHalf / 10: cho các số có chẵn chữ số (ví dụ: 1221)
        return x == reversedHalf || x == reversedHalf / 10;
    }


    public static void main(String[] args) {
        palindrome_num palindrome_num = new palindrome_num();
        int x = -121;
        boolean result = palindrome_num.isPalindrome(x);
        System.out.println(result);
    }
}
