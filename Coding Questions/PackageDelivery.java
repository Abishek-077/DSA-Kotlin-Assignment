import java.util.*;

public class PackageDelivery {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read packages array
        String[] parts = scanner.nextLine().replaceAll("\\[|\\]", "").split(",");
        int[] packages = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            packages[i] = Integer.parseInt(parts[i].trim());
        }
        int n = packages.length;

        // Read roads
        parts = scanner.nextLine().replaceAll("\\[|\\]", "").split(",");
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int i = 0; i < parts.length; i++) {
            String road = parts[i].trim();
            String[] nodes = road.split(" ");
            int a = Integer.parseInt(nodes[0]);
            int b = Integer.parseInt(nodes[1]);
            adj.get(a).add(b);
            adj.get(b).add(a);
        }

        // Precompute distance matrix using BFS for each node
        int[][] dist = new int[n][n];
        for (int u = 0; u < n; u++) {
            Arrays.fill(dist[u], -1);
            Queue<Integer> queue = new LinkedList<>();
            queue.add(u);
            dist[u][u] = 0;
            while (!queue.isEmpty()) {
                int current = queue.poll();
                for (int neighbor : adj.get(current)) {
                    if (dist[u][neighbor] == -1) {
                        dist[u][neighbor] = dist[u][current] + 1;
                        queue.add(neighbor);
                    }
                }
            }
        }

        // Precompute coverage for each node (nodes within 2 steps)
        List<Set<Integer>> coverage = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            Set<Integer> cov = new HashSet<>();
            for (int v = 0; v < n; v++) {
                if (dist[u][v] != -1 && dist[u][v] <= 2) {
                    cov.add(v);
                }
            }
            coverage.add(cov);
        }

        // Precompute coverageNodes for each package p
        Map<Integer, Set<Integer>> coverageNodes = new HashMap<>();
        for (int p = 0; p < n; p++) {
            if (packages[p] == 1) {
                Set<Integer> cov = new HashSet<>();
                for (int v = 0; v < n; v++) {
                    if (dist[p][v] != -1 && dist[p][v] <= 2) {
                        cov.add(v);
                    }
                }
                coverageNodes.put(p, cov);
            }
        }

        // Check if there are no packages
        boolean hasPackage = false;
        for (int p : packages) {
            if (p == 1) {
                hasPackage = true;
                break;
            }
        }
        if (!hasPackage) {
            System.out.println(0);
            return;
        }

        // Iterate over all possible starting nodes to find minimal roads
        int minRoads = Integer.MAX_VALUE;
        for (int u = 0; u < n; u++) {
            List<Integer> packagesNotCovered = new ArrayList<>();
            for (int p = 0; p < n; p++) {
                if (packages[p] == 1 && !coverage.get(u).contains(p)) {
                    packagesNotCovered.add(p);
                }
            }

            if (packagesNotCovered.isEmpty()) {
                minRoads = 0;
                break;
            } else {
                int maxDist = 0;
                for (int p : packagesNotCovered) {
                    Set<Integer> possibleNodes = coverageNodes.get(p);
                    int minDist = Integer.MAX_VALUE;
                    for (int v : possibleNodes) {
                        if (dist[u][v] != -1 && dist[u][v] < minDist) {
                            minDist = dist[u][v];
                        }
                    }
                    if (minDist != Integer.MAX_VALUE && minDist > maxDist) {
                        maxDist = minDist;
                    }
                }
                if (maxDist != 0) {
                    int currentCost = 2 * maxDist;
                    if (currentCost < minRoads) {
                        minRoads = currentCost;
                    }
                }
            }
        }

        System.out.println(minRoads);
    }
}