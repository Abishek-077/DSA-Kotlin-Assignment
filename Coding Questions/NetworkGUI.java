import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NetworkGUI extends JFrame {
    private GraphPanel graphPanel;
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private JLabel costLabel, latencyLabel;
    private final Color PRIMARY_COLOR = new Color(30, 136, 229);
    private final Color SECONDARY_COLOR = new Color(255, 152, 0);

    public NetworkGUI() {
        setTitle("Network Topology Optimizer");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Graph visualization panel
        graphPanel = new GraphPanel();
        graphPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(graphPanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Controls"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Buttons
        JButton addNodeButton = createStyledButton("Add Node", PRIMARY_COLOR);
        JButton addEdgeButton = createStyledButton("Add Edge", PRIMARY_COLOR);
        JButton optimizeButton = createStyledButton("Optimize", SECONDARY_COLOR);
        JButton shortestPathButton = createStyledButton("Find Path", SECONDARY_COLOR);

        // Metrics display
        costLabel = createMetricLabel("Total Cost: 0");
        latencyLabel = createMetricLabel("Average Latency: 0.00 ms");

        // Add components to control panel
        controlPanel.add(addNodeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(addEdgeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(optimizeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(shortestPathButton);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(costLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(latencyLabel);

        mainPanel.add(controlPanel, BorderLayout.EAST);
        add(mainPanel);

        // Event listeners
        addNodeButton.addActionListener(e -> addNode());
        addEdgeButton.addActionListener(e -> addEdge());
        optimizeButton.addActionListener(e -> optimizeNetwork());
        shortestPathButton.addActionListener(e -> findShortestPath());

        // Window resize handler
        graphPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                recalculateNodePositions();
                graphPanel.repaint();
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker()),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.brighter()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private JLabel createMetricLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(69, 90, 100));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(0, 0, 2, 0)
        );
        return label;
    }

    // Node class
    class Node {
        int x, y;
        String id;
        boolean isServer;

        Node(int x, int y, String id, boolean isServer) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.isServer = isServer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return id.equals(node.id);
        }

        @Override
        public int hashCode() { return id.hashCode(); }
    }

    // Edge class with neutral direction
    class Edge {
        Node a, b;
        int cost, bandwidth;

        Edge(Node a, Node b, int cost, int bandwidth) {
            this.a = a;
            this.b = b;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }

        boolean connects(Node node) { return a == node || b == node; }
    }

    // Graph visualization panel
    class GraphPanel extends JPanel {
        private List<Edge> shortestPathEdges = new ArrayList<>();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw edges
            for (Edge edge : edges) {
                g2d.setColor(shortestPathEdges.contains(edge) ? Color.RED : new Color(30, 30, 30));
                g2d.setStroke(new BasicStroke(shortestPathEdges.contains(edge) ? 3f : 2f));
                g2d.drawLine(edge.a.x, edge.a.y, edge.b.x, edge.b.y);

                // Edge labels
                String label = String.format("%d | %d", edge.cost, edge.bandwidth);
                FontMetrics fm = g2d.getFontMetrics();
                int midX = (edge.a.x + edge.b.x)/2 - fm.stringWidth(label)/2;
                int midY = (edge.a.y + edge.b.y)/2 + fm.getHeight()/2;
                
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(midX - 5, midY - fm.getHeight() + 3, 
                                fm.stringWidth(label) + 10, fm.getHeight(), 5, 5);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(label, midX, midY);
            }

            // Draw nodes
            for (Node node : nodes) {
                GradientPaint gradient = new GradientPaint(
                    node.x - 15, node.y - 15, PRIMARY_COLOR,
                    node.x + 15, node.y + 15, PRIMARY_COLOR.darker()
                );
                g2d.setPaint(node.isServer ? gradient : new Color(76, 175, 80));
                g2d.fillOval(node.x - 15, node.y - 15, 30, 30);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2d.drawString(node.id, node.x - 10, node.y + 5);
            }
        }

        void setShortestPath(List<Edge> pathEdges) {
            shortestPathEdges = pathEdges;
        }
    }

    private void addNode() {
        String id = JOptionPane.showInputDialog("Enter unique node ID:");
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Node ID cannot be empty!");
            return;
        }
        if (nodes.stream().anyMatch(n -> n.id.equals(id))) {
            JOptionPane.showMessageDialog(this, "Node ID must be unique!");
            return;
        }

        boolean isServer = JOptionPane.showConfirmDialog(null, 
            "Is this a server node?", "Node Type", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        Point pos = calculateNodePosition(nodes.size());
        nodes.add(new Node(pos.x, pos.y, id, isServer));
        graphPanel.repaint();
    }

    private Point calculateNodePosition(int index) {
        int centerX = graphPanel.getWidth() / 2;
        int centerY = graphPanel.getHeight() / 2;
        int radius = Math.max(Math.min(graphPanel.getWidth(), graphPanel.getHeight()) / 4, 50);

        if (index == 0) return new Point(centerX, centerY);

        double angle = 2 * Math.PI * index / Math.max(6, nodes.size() + 1) - Math.PI/2;
        return new Point(
            centerX + (int)(radius * Math.cos(angle)),
            centerY + (int)(radius * Math.sin(angle))
        );
    }

    private void recalculateNodePositions() {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Point pos = calculateNodePosition(i);
            node.x = pos.x;
            node.y = pos.y;
        }
    }

    private void addEdge() {
        if (nodes.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 nodes!");
            return;
        }

        String[] nodeIds = nodes.stream().map(n -> n.id).toArray(String[]::new);
        String fromId = (String) JOptionPane.showInputDialog(this, "From node:", 
            "Add Connection", JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);
        String toId = (String) JOptionPane.showInputDialog(this, "To node:", 
            "Add Connection", JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[1]);

        if (fromId == null || toId == null || fromId.equals(toId)) {
            JOptionPane.showMessageDialog(this, "Invalid node selection!");
            return;
        }

        try {
            Node from = nodes.stream().filter(n -> n.id.equals(fromId)).findFirst().orElse(null);
            Node to = nodes.stream().filter(n -> n.id.equals(toId)).findFirst().orElse(null);

            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this, "One or both nodes not found!");
                return;
            }

            int cost = Integer.parseInt(JOptionPane.showInputDialog("Connection cost:"));
            int bandwidth = Integer.parseInt(JOptionPane.showInputDialog("Bandwidth (Mbps):"));
            
            edges.add(new Edge(from, to, cost, bandwidth));
            updateMetrics();
            graphPanel.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid numeric input!");
        }
    }

    private void optimizeNetwork() {
        if (nodes.isEmpty()) return;

        UnionFind uf = new UnionFind();
        nodes.forEach(uf::makeSet);

        ArrayList<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(e -> e.cost));

        ArrayList<Edge> mst = new ArrayList<>();
        for (Edge edge : sortedEdges) {
            if (uf.find(edge.a) != uf.find(edge.b)) {
                mst.add(edge);
                uf.union(edge.a, edge.b);
            }
        }

        edges = mst;
        updateMetrics();
        graphPanel.repaint();
    }

    private void findShortestPath() {
        if (nodes.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 nodes!");
            return;
        }

        String[] nodeIds = nodes.stream().map(n -> n.id).toArray(String[]::new);
        String startId = (String) JOptionPane.showInputDialog(this, "Start node:", 
            "Shortest Path", JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[0]);
        String endId = (String) JOptionPane.showInputDialog(this, "End node:", 
            "Shortest Path", JOptionPane.PLAIN_MESSAGE, null, nodeIds, nodeIds[1]);

        Node start = nodes.stream().filter(n -> n.id.equals(startId)).findFirst().orElse(null);
        Node end = nodes.stream().filter(n -> n.id.equals(endId)).findFirst().orElse(null);

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Invalid node selection!");
            return;
        }

        // Dijkstra's algorithm
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> distances.get(n)));

        nodes.forEach(n -> distances.put(n, Double.MAX_VALUE));
        distances.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current.equals(end)) break;

            for (Edge edge : edges) {
                if (!edge.connects(current)) continue;
                
                Node neighbor = edge.a.equals(current) ? edge.b : edge.a;
                double weight = 1000.0 / edge.bandwidth;
                double newDist = distances.get(current) + weight;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        if (distances.get(end) == Double.MAX_VALUE) {
            JOptionPane.showMessageDialog(this, "No path exists!");
            return;
        }

        // Reconstruct path
        LinkedList<Node> path = new LinkedList<>();
        Node current = end;
        while (current != null) {
            path.addFirst(current);
            current = previous.get(current);
        }

        // Highlight path
        List<Edge> pathEdges = new ArrayList<>();
        Node prev = null;
        for (Node node : path) {
            if (prev != null) {
                for (Edge edge : edges) {
                    if (edge.connects(prev) && edge.connects(node)) {
                        pathEdges.add(edge);
                        break;
                    }
                }
            }
            prev = node;
        }

        // Show results
        String pathString = String.join(" → ", path.stream().map(n -> n.id).toArray(String[]::new));
        JOptionPane.showMessageDialog(this, 
            "Optimal Path: " + pathString + "\n" +
            String.format("Total Latency: %.2f ms", distances.get(end)));

        graphPanel.setShortestPath(pathEdges);
        graphPanel.repaint();
    }

    private void updateMetrics() {
        int totalCost = edges.stream().mapToInt(e -> e.cost).sum();
        double avgLatency = edges.stream()
            .mapToDouble(e -> 1000.0 / e.bandwidth)
            .average().orElse(0);
        
        costLabel.setText("Total Cost: " + totalCost);
        latencyLabel.setText(String.format("Avg Latency: %.2f ms", avgLatency));
    }

    // Union-Find implementation for Kruskal's algorithm
    class UnionFind {
        private Map<Node, Node> parent = new HashMap<>();

        void makeSet(Node node) { parent.put(node, node); }
        
        Node find(Node node) {
            if (parent.get(node) != node) {
                parent.put(node, find(parent.get(node)));
            }
            return parent.get(node);
        }
        
        void union(Node a, Node b) {
            Node rootA = find(a);
            Node rootB = find(b);
            if (rootA != rootB) parent.put(rootB, rootA);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkGUI().setVisible(true));
    }
}