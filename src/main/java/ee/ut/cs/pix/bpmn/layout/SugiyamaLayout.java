package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.graph.Graph;

import java.awt.geom.Point2D;
import java.util.*;

public class SugiyamaLayout implements Layout {
    @Override
    public void apply(Graph graph) {
        SugiyamaAlgorithm layout = new SugiyamaAlgorithm(graph);
        Map<String, Point2D> positions = layout.getNodePositions();
        graph.getNodes()
                .forEach(
                        n -> {
                            n.x = positions.get(n.id).getX();
                            n.y = positions.get(n.id).getY();
                        });
    }

    static class SugiyamaAlgorithm {
        private final Map<String, Point2D> nodePositions = new HashMap<>();
        private final Map<String, List<String>> adjacencyList = new HashMap<>();
        private final Map<String, Integer> layerAssignment = new HashMap<>();

        public SugiyamaAlgorithm(Graph graph) {
            graph.getNodes().forEach(n -> addNode(n.id));
            graph.getEdges().forEach(e -> addEdge(e.source.id, e.target.id));

            removeCycles();
            assignLayers();
            reduceCrossings();
            assignCoordinates();
        }

        void addNode(String node) {
            adjacencyList.put(node, new ArrayList<>());
        }

        void addEdge(String node1, String node2) {
            adjacencyList.get(node1).add(node2);
        }

        void removeCycles() {
            // Remove cycles using DFS
            Set<String> visited = new HashSet<>();
            Set<String> onStack = new HashSet<>();
            for (String node : adjacencyList.keySet()) {
                if (!visited.contains(node)) {
                    dfsRemove(node, visited, onStack);
                }
            }
        }

        void dfsRemove(String node, Set<String> visited, Set<String> onStack) {
            visited.add(node);
            onStack.add(node);
            for (String neighbor : adjacencyList.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfsRemove(neighbor, visited, onStack);
                } else if (onStack.contains(neighbor)) {
                    adjacencyList.get(node).remove(neighbor);
                }
            }
            onStack.remove(node);
        }

        void assignLayers() {
            // Assign layers using BFS
            Queue<String> queue = new LinkedList<>();
            for (String node : adjacencyList.keySet()) {
                if (!layerAssignment.containsKey(node)) {
                    layerAssignment.put(node, 0);
                    queue.add(node);
                }
                while (!queue.isEmpty()) {
                    String current = queue.poll();
                    for (String neighbor : adjacencyList.get(current)) {
                        if (!layerAssignment.containsKey(neighbor)) {
                            layerAssignment.put(neighbor, layerAssignment.get(current) + 1);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        void reduceCrossings() {
            // Sort nodes within each layer to reduce crossings (barycenter method)
            Map<Integer, List<String>> layers = new HashMap<>();
            for (Map.Entry<String, Integer> entry : layerAssignment.entrySet()) {
                layers.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                        .add(entry.getKey());
            }
            for (List<String> layer : layers.values()) {
                layer.sort(
                        Comparator.comparingDouble(
                                node ->
                                        adjacencyList.get(node).stream()
                                                .mapToDouble(layerAssignment::get)
                                                .average()
                                                .orElse(0)));
            }
        }

        void assignCoordinates() {
            // Assign coordinates to each node (simple method: nodes in the same layer have the same
            // y-coordinate)
            double xSpacing = 125;
            double ySpacing = 125;
            for (Map.Entry<String, Integer> entry : layerAssignment.entrySet()) {
                double x = entry.getValue() * xSpacing;
                double y = adjacencyList.get(entry.getKey()).size() * ySpacing;
                nodePositions.put(entry.getKey(), new Point2D.Double(x, y));
            }
        }

        public Map<String, Point2D> getNodePositions() {
            return nodePositions;
        }
    }
}
