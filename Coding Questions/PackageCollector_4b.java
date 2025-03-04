/*b)
You have a map of a city represented by a graph with n nodes (representing locations) and edges where
edges[i] = [ai, bi] indicates a road between locations ai and bi. Each location has a value of either 0 or 1,
indicating whether there is a package to be delivered. You can start at any location and perform the
following actions:
Collect packages from all locations within a distance of 2 from your current location.
Move to an adjacent location.
Your goal is to collect all packages and return to your starting location.
Goal:
Determine the minimum number of roads you need to traverse to collect all packages.
Input:
packages: An array of package values for each location.
roads: A 2D array representing the connections between locations.
Output:
The minimum number of roads to traverse.
Note that if you pass a roads several times, you need to count it into the answer several times.
Input: packages = [1, 0, 0, 0, 0, 1], roads = [[0, 1], [1, 2], [2, 3], [3, 4], [4, 5]]
Output:2
Explanation: Start at location 2, collect the packages at location 0, move to location 3, collect the
packages at location 5 then move back to location 2.
Input: packages = [0,0,0,1,1,0,0,1], roads = [[0,1],[0,2],[1,3],[1,4],[2,5],[5,6],[5,7]]
Output: 2
Explanation: Start at location 0, collect the package at l */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class PackageCollector_4b {

    // primary technique for using sample inputs to test the solution.
    public static void main(String[] args) {
        // Example 1
        int[] packages1 = {0,0,0,1,1,0,0,1};
        int[][] roads1 = {
            {0, 1},
            {0, 2},
            {1, 3},
            {1, 4},
            {2, 5},
            {5, 6},
            {5, 7}
        };
        System.out.println("Output (Example 1): " + minRoads(packages1, roads1)); // Expected 2
        
    }

    public static int minRoads(int[] packages, int[][] roads) {
        int n = packages.length;
        // No roads need to be traveled if there are no packa


        boolean anyPackage = false;
        for (int p : packages) {
            if (p == 1) {
                anyPackage = true;
                break;
            }}
        if (!anyPackage) return 0;
        // Construct the undirected graph.
        List<Integer>[] graph = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int[] edge : roads) {
            int u = edge[0], v = edge[1];
            graph[u].add(v);
            graph[v].add(u);
        }
        // Compute all-pairs shortest distances.
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
            bfs(i, graph, dist[i]);
        }
        int minAns = Integer.MAX_VALUE;

        int totalSubsets = 1 << n;
        for (int mask = 1; mask < totalSubsets; mask++) {
            // Check if S (the set of stops given by 'mask') covers every package.
            if (!coversPackages(mask, packages, dist)) {
                continue;
            }
            // Build a list of the nodes (stops) in this subset.
            List<Integer> stops = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (((mask >> j) & 1) == 1) {
                    stops.add(j);
                }}
            int tourCost = tspTour(stops, dist);
            minAns = Math.min(minAns, tourCost);
        }
        return minAns == Integer.MAX_VALUE ? -1 : minAns;
    }
    
    /**
     To calculate the shortest distances in an unweighted graph, use the standard BFS from source's'. fills the array of distances 'd' (d[i] = s-to-i distance).

     */
    private static void bfs(int s, List<Integer>[] graph, int[] d) {
        Queue<Integer> q = new LinkedList<>();
        d[s] = 0;
        q.offer(s);
        while (!q.isEmpty()) {
            int cur = q.poll();
            for (int nei : graph[cur]) {
                if (d[nei] == Integer.MAX_VALUE) {
                    d[nei] = d[cur] + 1;
                    q.offer(nei);
                }}}}
    private static boolean coversPackages(int mask, int[] packages, int[][] dist) {
        int n = packages.length;
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                boolean covered = false;
                for (int j = 0; j < n; j++) {
                    if (((mask >> j) & 1) == 1) {
                        if (dist[i][j] <= 2) {
                            covered = true;
                            break;
                        }}}
                if (!covered) return false;
            }}
        return true;
    }
    private static int tspTour(List<Integer> stops, int[][] dist) {
        int k = stops.size();
        int fullMask = (1 << k) - 1;
        int[][] dp = new int[1 << k][k];
        for (int i = 0; i < (1 << k); i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }
        dp[1 << 0][0] = 0;
        for (int mask = 0; mask < (1 << k); mask++) {
            for (int i = 0; i < k; i++) {
                if (((mask >> i) & 1) == 1 && dp[mask][i] != Integer.MAX_VALUE) {
                    for (int j = 0; j < k; j++) {
                        if (((mask >> j) & 1) == 0) { // j not yet visited
                            int nextMask = mask | (1 << j);
                            int cost = dp[mask][i] + dist[stops.get(i)][stops.get(j)];
                            dp[nextMask][j] = Math.min(dp[nextMask][j], cost);
                        }}}}}
        // Return to the starting stop to finish the cycle.
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            if (dp[fullMask][i] != Integer.MAX_VALUE) {
                best = Math.min(best, dp[fullMask][i] + dist[stops.get(i)][stops.get(0)]);
            }}
        return best;
        }}