package ee.ut.cs.pix.bpmn.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph of the BPMN control-flow where nodes start with the start event and edges are arcs between
 * the nodes.
 */
public class Graph {
    private final List<FlowObject> nodes = new ArrayList<>();
    private final List<ConnectingObject> edges = new ArrayList<>();

    public List<FlowObject> getNodes() {
        return nodes;
    }

    public List<ConnectingObject> getEdges() {
        return edges;
    }

    public void addNode(FlowObject node) {
        nodes.add(node);
    }

    public void addEdge(ConnectingObject edge) {
        edges.add(edge);
    }
}
