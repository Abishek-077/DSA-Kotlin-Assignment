/*Question 3
You have a network of n devices. Each device can have its own communication module installed at a
cost of modules [i - 1]. Alternatively, devices can communicate with each other using direct connections.
The cost of connecting two devices is given by the array connections where each connections[j] =
[device1j, device2j, costj] represents the cost to connect devices device1j and device2j. Connections are
bidirectional, and there could be multiple valid connections between the same two devices with different
costs.
Goal:
Determine the minimum total cost to connect all devices in the network.
Input:
n: The number of devices.
modules: An array of costs to install communication modules on each device.
connections: An array of connections, where each connection is represented as a triplet [device1j,
device2j, costj].
Output:
The minimum total cost to connect all devices.
Example:
Input: n = 3, modules = [1, 2, 2], connections = [[1, 2, 1], [2, 3, 1]] Output: 3
Explanation:
The best strategy is to install a communication module on the first device with cost 1 and connect the
other devices to it with cost 2, resulting in a total cost of 3. */

import java.util.*;

class UnionFind {
    private int[] parent, rank;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX == rootY) return false;
        
        // Union by rank
        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }
}

class MinimumNetworkCost {
    public static int minimumCost(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>();

        // Add module installation as an edge to a virtual node `n`
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{modules[i], i, n});
        }

        // Add actual connections
        for (int[] conn : connections) {
            edges.add(new int[]{conn[2], conn[0] - 1, conn[1] - 1});
        }

        // Sort edges by cost (Greedy approach)
        edges.sort(Comparator.comparingInt(a -> a[0]));

        UnionFind uf = new UnionFind(n + 1); // Union-Find for `n` devices + virtual node
        int totalCost = 0, edgesUsed = 0;

        for (int[] edge : edges) {
            int cost = edge[0], u = edge[1], v = edge[2];
            if (uf.union(u, v)) { // If adding this edge connects new nodes
                totalCost += cost;
                edgesUsed++;
                if (edgesUsed == n) { // Since we added a virtual node, we need `n` edges
                    return totalCost;
                }
            }
        }
        return -1; // If we cannot connect all devices
    }

    public static void main(String[] args) {
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};
        System.out.println(minimumCost(n, modules, connections)); // Output: 3
    }
}


