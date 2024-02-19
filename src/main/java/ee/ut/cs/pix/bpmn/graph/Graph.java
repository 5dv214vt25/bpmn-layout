package ee.ut.cs.pix.bpmn.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph of the BPMN control-flow where nodes start with the start event and edges are arcs between
 * the nodes.
 */
public class Graph {
    private final List<FlowNode> nodes = new ArrayList<>();
    private final List<FlowArc> edges = new ArrayList<>();

    public List<FlowNode> getNodes() {
        return nodes;
    }

    public List<FlowArc> getEdges() {
        return edges;
    }

    public void addNode(FlowNode node) {
        nodes.add(node);
    }

    public void addEdge(FlowArc edge) {
        edges.add(edge);
    }
}
