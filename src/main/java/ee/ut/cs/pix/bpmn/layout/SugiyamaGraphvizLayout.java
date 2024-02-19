package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;
import ee.ut.cs.pix.bpmn.graph.FlowElementType;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

import java.io.IOException;
import java.util.HashMap;

/**
 * Sugiyama graph layout algorithm (Warfield, Carpano, and Sugiyama) implemented in Graphviz ("dot"
 * layout).
 */
public class SugiyamaGraphvizLayout implements Layout {
    @Override
    public void apply(Graph graph) throws IOException {
        updateGraphNodes(applyLayout(graphToDot(graph)), graph);
    }

    private void updateGraphNodes(MutableGraph gvGraph, Graph graph) {
        // constructing a map to reduce time for finding nodes in the original graph below
        HashMap<String, FlowNode> nodeIdToNodeMap = new HashMap<>();
        graph.getNodes()
                .forEach(
                        n ->
                                nodeIdToNodeMap.put(
                                        n.getId(), n)); // assumption: each node has a unique ID
        // updating the nodes
        gvGraph.nodes()
                .forEach(
                        n -> {
                            double[] coordinates = nodePosition(n);
                            if (coordinates == null) return;
                            FlowNode node = nodeIdToNodeMap.get(n.name().toString());
                            if (node == null) return;
                            node.getBounds().setX(coordinates[0]);
                            node.getBounds().setY(coordinates[1]);
                        });
    }

    private MutableGraph applyLayout(String dotGraph) throws IOException {
        MutableGraph g = new Parser().read(dotGraph);
        g.setDirected(true);
        // render the graph to a string with coordinates
        String result = Graphviz.fromGraph(g).render(Format.XDOT).toString();
        // read back the graph with coordinates
        return new Parser().read(result);
    }

    static String graphToDot(Graph graph) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("layout=dot;\n");
        dot.append("node [shape=box, width=1, height=1.25, fixedsize=true];\n");
        dot.append("rankdir=LR;\n");
        dot.append("ranksep=1;\n");
        graph.getNodes().forEach(n -> dot.append(graphNodeToDot(n)));
        graph.getEdges()
                .forEach(
                        e ->
                                dot.append(
                                        String.format(
                                                "\"%s\" -> \"%s\";\n",
                                                e.getSource().getId(), e.getTarget().getId())));
        dot.append("}");
        return dot.toString();
    }

    private double[] nodePosition(MutableNode node) {
        String pos = (String) node.attrs().get("pos");
        if (pos == null) return null;
        double[] coordinates = new double[2];
        String[] xy = pos.split(",");
        coordinates[0] = Double.parseDouble(xy[0]);
        coordinates[1] = Double.parseDouble(xy[1]);
        return coordinates;
    }

    private static String graphNodeToDot(FlowNode node) {
        String id = node.getId(), name = node.getName();
        FlowElementType type = node.getType();
        ShapeBounds bounds = node.getBounds();

        StringBuilder dot = new StringBuilder();
        // add label
        dot.append("\"").append(id).append("\" [label=\"");
        if (name == null || name.isEmpty()) {
            dot.append(type.getValue());
        } else {
            dot.append(name);
        }
        dot.append("\"");
        // add shape form
        if (type == FlowElementType.STARTEVENT || type == FlowElementType.ENDEVENT) {
            dot.append(", shape=ellipse");
        } else if (type == FlowElementType.INCLUSIVEGATEWAY
                || type == FlowElementType.EXCLUSIVEGATEWAY
                || type == FlowElementType.PARALLELGATEWAY) {
            dot.append(", shape=diamond");
        }
        // add width and height
        if (bounds != null) {
            dot.append(
                    String.format(
                            ", width=%.2f, height=%.2f",
                            bounds.getWidth() / 100, bounds.getHeight() / 80));
        }
        // end of the attribute list
        dot.append("];\n");
        return dot.toString();
    }
}
