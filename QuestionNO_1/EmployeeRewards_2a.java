/*Question1
2a)
You have a material with n temperature levels. You know that there exists a critical temperature f where
0 <= f <= n such that the material will react or change its properties at temperatures higher than f but
remain unchanged at or below f.
Rules:
 You can measure the material's properties at any temperature level once.
 If the material reacts or changes its properties, you can no longer use it for further measurements.
 If the material remains unchanged, you can reuse it for further measurements.
Goal:
Determine the minimum number of measurements required to find the critical temperature.
Input:
 k: The number of identical samples of the material.
 n: The number of temperature levels.
Output:
 The minimum number of measurements required to find the critical temperature.
Example 1:
Input: k = 1, n = 2
Output: 2
Explanation:
Check the material at temperature 1. If its property changes, we know that f = 0.
Otherwise, raise temperature to 2 and check if property changes. If its property changes, we know that f =
1.If its property changes at temperature, then we know f = 2.
Hence, we need at minimum 2 moves to determine with certainty what the value of f is.
Example 2:
Input: k = 2, n = 6
Output: 3
Example 3:
Input: k = 3, n = 14
Output: 4
*/
import java.util.*;

class EmployeeRewards_2a {
    public static int minRewards(int[] ratings) {
        int n = ratings.length;
        int[] rewards = new int[n];
        Arrays.fill(rewards, 1); // Step 1: Everyone gets at least one reward

        // Step 2: Left to Right scan
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Step 3: Right to Left scan
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Step 4: Sum up rewards
        int totalRewards = 0;
        for (int r : rewards) {
            totalRewards += r;
        }

        return totalRewards;
    }

    public static void main(String[] args) {
        System.out.println(minRewards(new int[]{1, 0, 2})); // Output: 5
        System.out.println(minRewards(new int[]{1, 2, 2})); // Output: 4
    }
}
