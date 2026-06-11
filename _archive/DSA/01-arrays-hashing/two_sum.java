// two sum (LeetCode #1)
// Cho một mảng các số nguyên nums và một số nguyên target, hãy trả về chỉ số của hai số đó sao cho tổng của chúng bằng target.
// Bạn có thể giả định rằng mỗi đầu vào sẽ có chính xác một lời giải và bạn không được sử dụng cùng một phần tử hai lần.
// Bạn có thể trả về câu trả lời theo bất kỳ thứ tự nào.

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class TwoSumBruteForce {
    public int[] twoSum(int[] nums, int target) {
        // for (int i = 0; i < nums.length; i++) {
        //     for (int j = i + 1; j < nums.length; j++) {
        //         if (nums[i] + nums[j] == target) {
        //             return new int[] { i, j };
        //         }
        //     }
        // }
        // return new int[] {};
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            Integer complementIndex = map.get(complement);
            if(complementIndex != null) {
                return new int [] { complementIndex, i };
            }
            map.put(nums[i], i);
        }
        return new int [] {};
    }

    public static void main(String[] args) {
        TwoSumBruteForce twoSum = new TwoSumBruteForce();
        int[] nums = { 2, 7, 11, 15 };
        int target = 18;
        int[] result = twoSum.twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }
}