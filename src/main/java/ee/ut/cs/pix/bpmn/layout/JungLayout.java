package ee.ut.cs.pix.bpmn.layout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.awt.*;
import java.awt.geom.Point2D;

public class JungLayout {
    public static void createLayout(ee.ut.cs.pix.bpmn.layout.Graph graph) {
        Graph<String, String> jGraph = jungGraph(graph);
        Layout<String, String> layout = new CustomSpringLayout<>(jGraph, 100, 100);
        int w = graph.getNodes().size() * 100 * 2;
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

    static Graph<String, String> jungGraph(ee.ut.cs.pix.bpmn.layout.Graph graph) {
        Graph<String, String> jungGraph = new DirectedSparseMultigraph<>();
        for (FlowNode node : graph.getNodes()) {
            jungGraph.addVertex(node.id);
        }
        for (FlowArc edge : graph.getEdges()) {
            jungGraph.addEdge(edge.id, edge.source.id, edge.target.id, EdgeType.DIRECTED);
        }
        return jungGraph;
    }

    static Graph<String, String> createGraph() {
        Graph<String, String> graph = new DirectedSparseMultigraph<String, String>();
        String v1 = "Foo";
        String v2 = "Bar";
        String v3 = "Baz";
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addEdge("Edge1", v1, v2, EdgeType.DIRECTED);
        graph.addEdge("Edge2", v2, v3, EdgeType.DIRECTED);
        return graph;
    }
}

class CustomSpringLayout<V, E> extends SpringLayout<V, E> {
    private final int vertexWidth;
    private final int vertexHeight;

    public CustomSpringLayout(Graph<V, E> g, int vertexWidth, int vertexHeight) {
        super(g);
        this.vertexWidth = vertexWidth;
        this.vertexHeight = vertexHeight;
    }

    @Override
    public void setLocation(V v, Point2D location) {
        double x =
                Math.max(
                        vertexWidth / 2.0,
                        Math.min(size.getWidth() - vertexWidth / 2.0, location.getX()));
        double y =
                Math.max(
                        vertexHeight / 2.0,
                        Math.min(size.getHeight() - vertexHeight / 2.0, location.getY()));
        Point2D adjustedLocation = new Point2D.Double(x, y);
        super.setLocation(v, adjustedLocation);
    }
}
