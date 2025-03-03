/*Question1
a
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
Output: 4 */

class Critical_Temperature_1a {
    public static int findMinTests(int k, int n) {
        // DP table
        int[][] dp = new int[k + 1][n + 1];

        // Base cases
        for (int i = 1; i <= k; i++) dp[i][0] = 0; // 0 levels need 0 tests
        for (int i = 1; i <= k; i++) dp[i][1] = 1; // 1 level needs 1 test
        for (int j = 1; j <= n; j++) dp[1][j] = j; // With 1 sample, we test sequentially

        // Fill DP table
        for (int i = 2; i <= k; i++) {
            for (int j = 2; j <= n; j++) {
                dp[i][j] = Integer.MAX_VALUE;
                int low = 1, high = j;
                
                // Binary search for optimal floor
                while (low <= high) {
                    int mid = (low + high) / 2;
                    int breakCase = dp[i - 1][mid - 1]; // Sample breaks
                    int noBreakCase = dp[i][j - mid]; // Sample does not break
                    int worst = 1 + Math.max(breakCase, noBreakCase);

                    dp[i][j] = Math.min(dp[i][j], worst);

                    if (breakCase > noBreakCase) {
                        high = mid - 1;
                    } else {
                        low = mid + 1;
                    }
                }
            }
        }

        return dp[k][n];
    }

    public static void main(String[] args) {
        System.out.println(findMinTests(1, 2)); // Output: 2
        System.out.println(findMinTests(2, 6)); // Output: 3
        System.out.println(findMinTests(3, 14)); // Output: 4
    }
}
