package ee.ut.cs.pix.bpmn.layout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import ee.ut.cs.pix.bpmn.di.BPMNElement;
import ee.ut.cs.pix.bpmn.graph.FlowArc;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

import java.awt.*;
import java.awt.geom.Point2D;

public class JungCoordinator implements Coordinator {
    @Override
    public void updateCoordinates(Graph graph) {
        DirectedGraph<String, String> jGraph = jungGraph(graph);
        Layout<String, String> layout = new CustomLayout<>(jGraph, 100, 100);
        int w = 1000; // graph.getNodes().size() * 100 * 2;
        layout.setSize(new Dimension(w, w));
        layout.initialize();
        for (String vertex : jGraph.getVertices()) {
            Point2D coord = layout.apply(vertex);
            graph.getNodes()
                    .forEach(
                            node -> {
                                if (node.id.equals(vertex)) {
                                    node.x = coord.getX();
                                    node.y = coord.getY();
                                }
                            });
        }
    }

    static DirectedGraph<String, String> jungGraph(Graph graph) {
        DirectedGraph<String, String> jungGraph = new DirectedSparseMultigraph<>();
        for (FlowNode node : graph.getNodes()) {
            if (node.type != BPMNElement.TASK) { // consider only tasks
                System.out.println("Ignoring " + node.type);
                continue;
            }
            jungGraph.addVertex(node.id);
        }
        for (FlowArc edge : graph.getEdges()) {
            jungGraph.addEdge(edge.id, edge.source.id, edge.target.id, EdgeType.DIRECTED);
        }
        return jungGraph;
    }
}

class CustomLayout<V, E> extends SpringLayout2<V, E> {
    // private final int vertexWidth;
    // private final int vertexHeight;

    public CustomLayout(DirectedGraph<V, E> g, int vertexWidth, int vertexHeight) {
        super(g);
        // this.vertexWidth = vertexWidth;
        // this.vertexHeight = vertexHeight;
    }

    // @Override
    // public void setLocation(V v, Point2D location) {
    //     double x =
    //             Math.max(
    //                     vertexWidth / 2.0,
    //                     Math.min(size.getWidth() - vertexWidth / 2.0, location.getX()));
    //     double y =
    //             Math.max(
    //                     vertexHeight / 2.0,
    //                     Math.min(size.getHeight() - vertexHeight / 2.0, location.getY()));
    //     Point2D adjustedLocation = new Point2D.Double(x, y);
    //     super.setLocation(v, adjustedLocation);
    // }
}
