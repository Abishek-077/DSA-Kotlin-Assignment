/*Question1
2a)
You have a team of n employees, and each employee is assigned a performance rating given in the
integer array ratings. You want to assign rewards to these employees based on the following rules:
Every employee must receive at least one reward.
Employees with a higher rating must receive more rewards than their adjacent colleagues.
Goal:
Determine the minimum number of rewards you need to distribute to the employees.
Input:
ratings: The array of employee performance ratings.
Output:
The minimum number of rewards needed to distribute.
Example 1:
Input: ratings = [1, 0, 2]
Output: 5
Explanation: You can allocate to the first, second and third employee with 2, 1, 2 rewards respectively.
Example 2:
Input: ratings = [1, 2, 2]
Output: 4
Explanation: You can allocate to the first, second and third employee with 1, 2, 1 rewards respectively.
The third employee gets 1 rewards because it satisfies the above two conditions.


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
