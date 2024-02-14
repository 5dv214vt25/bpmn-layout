package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.graph.Graph;

import java.awt.geom.Point2D;
import java.util.*;

public class FruchtermanReingoldCoordinator implements Coordinator {
    @Override
    public void updateCoordinates(Graph graph) {
        runGraphLayout(graph);
    }

    private static void runGraphLayout(Graph graph) {
        GraphLayout layout = new GraphLayout(100, 100, 1, 10, 2);
        graph.getNodes().forEach(n -> layout.addNode(n.id));
        graph.getEdges().forEach(e -> layout.addEdge(e.source.id, e.target.id));

        for (int i = 0; i < 1000; i++) {
            layout.runLayoutIteration();
        }

        Map<String, Point2D> positions = layout.getNodePositions();
        for (String node : positions.keySet()) {
            System.out.println(node + ": " + positions.get(node));
        }

        graph.getNodes()
                .forEach(
                        n -> {
                            n.x = positions.get(n.id).getX();
                            n.y = positions.get(n.id).getY();
                        });
    }

    static class GraphLayout {
        private Map<String, Point2D> nodePositions = new HashMap<>();
        private Map<String, List<String>> adjacencyList = new HashMap<>();
        private double width;
        private double height;
        private double repulsionForce;
        private double attractionForce;
        private double attractionScale;

        public GraphLayout(
                double width,
                double height,
                double repulsionForce,
                double attractionForce,
                double attractionScale) {
            this.width = width;
            this.height = height;
            this.repulsionForce = repulsionForce;
            this.attractionForce = attractionForce;
            this.attractionScale = attractionScale;
        }

        public void addNode(String node) {
            nodePositions.put(
                    node, new Point2D.Double(Math.random() * width, Math.random() * height));
            adjacencyList.put(node, new ArrayList<>());
        }

        public void addEdge(String node1, String node2) {
            adjacencyList.get(node1).add(node2);
            adjacencyList.get(node2).add(node1);
        }

        public void runLayoutIteration() {
            Map<String, Point2D> forces = new HashMap<>();

            for (String node : nodePositions.keySet()) {
                forces.put(node, new Point2D.Double(0, 0));

                for (String other : nodePositions.keySet()) {
                    if (node.equals(other)) continue;

                    Point2D repellingForce =
                            repellingForce(
                                    nodePositions.get(node),
                                    nodePositions.get(other),
                                    repulsionForce);
                    forces.get(node)
                            .setLocation(
                                    forces.get(node).getX() + repellingForce.getX(),
                                    forces.get(node).getY() + repellingForce.getY());

                    if (adjacencyList.get(node).contains(other)) {
                        Point2D attractiveForce =
                                attractiveForce(
                                        nodePositions.get(node),
                                        nodePositions.get(other),
                                        attractionForce,
                                        attractionScale);
                        forces.get(node)
                                .setLocation(
                                        forces.get(node).getX() + attractiveForce.getX(),
                                        forces.get(node).getY() + attractiveForce.getY());
                    }
                }

                // Normalize the force to prevent it from becoming too large
                double forceMagnitude =
                        Math.sqrt(
                                forces.get(node).getX() * forces.get(node).getX()
                                        + forces.get(node).getY() * forces.get(node).getY());
                forces.get(node)
                        .setLocation(
                                forces.get(node).getX() / forceMagnitude,
                                forces.get(node).getY() / forceMagnitude);
            }

            for (String node : nodePositions.keySet()) {
                Point2D pos = nodePositions.get(node);
                Point2D force = forces.get(node);
                pos.setLocation(pos.getX() + force.getX(), pos.getY() + force.getY());
            }
        }

        private Point2D repellingForce(Point2D pos1, Point2D pos2, double repulsionForce) {
            double dx = pos1.getX() - pos2.getX();
            double dy = pos1.getY() - pos2.getY();
            double distance =
                    Math.sqrt(dx * dx + dy * dy)
                            + 1e-7; // add small constant to prevent division by zero
            double force = repulsionForce / (distance * distance);
            return new Point2D.Double(force * dx / distance, force * dy / distance);
        }

        private Point2D attractiveForce(
                Point2D pos1, Point2D pos2, double attractionForce, double attractionScale) {
            double dx = pos1.getX() - pos2.getX();
            double dy = pos1.getY() - pos2.getY();
            double distance =
                    Math.sqrt(dx * dx + dy * dy)
                            + 1e-7; // add small constant to prevent division by zero
            double force = (distance * distance) / attractionForce * attractionScale;
            return new Point2D.Double(force * dx / distance, force * dy / distance);
        }

        public Map<String, Point2D> getNodePositions() {
            return nodePositions;
        }
    }
}
