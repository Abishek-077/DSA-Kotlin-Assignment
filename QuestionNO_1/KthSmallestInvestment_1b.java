/*1b) You have two sorted arrays of investment returns, returns1 and returns2, and a target number k. You
want to find the kth lowest combined return that can be achieved by selecting one investment from each
array.
Rules:
 The arrays are sorted in ascending order.
 You can access any element in the arrays.
Goal:
Determine the kth lowest combined return that can be achieved.
Input:
 returns1: The first sorted array of investment returns.
 returns2: The second sorted array of investment returns.
 k: The target index of the lowest combined return.
Output:
 The kth lowest combined return that can be achieved.
Example 1:
Input: returns1= [2,5], returns2= [3,4], k = 2
Output: 8
Explanation: The 2 smallest investments are are:
- returns1 [0] * returns2 [0] = 2 * 3 = 6
- returns1 [0] * returns2 [1] = 2 * 4 = 8
The 2nd smallest investment is 8.
Example 2:
Input: returns1= [-4,-2,0,3], returns2= [2,4], k = 6
Output: 0
Explanation: The 6 smallest products are:
- returns1 [0] * returns2 [1] = (-4) * 4 = -16
- returns1 [0] * returns2 [0] = (-4) * 2 = -8
- returns1 [1] * returns2 [1] = (-2) * 4 = -8
- returns1 [1] * returns2 [0] = (-2) * 2 = -4
- returns1 [2] * returns2 [0] = 0 * 2 = 0
- returns1 [2] * returns2 [1] = 0 * 4 = 0
The 6th smallest investment is 0.
 */

import java.util.*;

class KthSmallestInvestment {
    static class Pair {
        int i, j, value;

        Pair(int i, int j, int value) {
            this.i = i;
            this.j = j;
            this.value = value;
        }
    }

    public static int findKthSmallestProduct(int[] returns1, int[] returns2, int k) {
        PriorityQueue<Pair> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a.value));
        HashSet<String> visited = new HashSet<>();

        // Initialize the heap with the first possible products
        minHeap.offer(new Pair(0, 0, returns1[0] * returns2[0]));
        visited.add("0,0");

        int result = 0;

        while (k > 0) {
            Pair current = minHeap.poll();
            result = current.value;
            k--;

            int i = current.i, j = current.j;

            // Add (i+1, j) to heap if within bounds
            if (i + 1 < returns1.length && !visited.contains((i + 1) + "," + j)) {
                minHeap.offer(new Pair(i + 1, j, returns1[i + 1] * returns2[j]));
                visited.add((i + 1) + "," + j);
            }

            // Add (i, j+1) to heap if within bounds
            if (j + 1 < returns2.length && !visited.contains(i + "," + (j + 1))) {
                minHeap.offer(new Pair(i, j + 1, returns1[i] * returns2[j + 1]));
                visited.add(i + "," + (j + 1));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(findKthSmallestProduct(new int[]{2, 5}, new int[]{3, 4}, 2)); // Output: 8
        System.out.println(findKthSmallestProduct(new int[]{-4, -2, 0, 3}, new int[]{2, 4}, 6)); // Output: 0
    }
}

