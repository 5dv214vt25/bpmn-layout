package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;
import ee.ut.cs.pix.bpmn.graph.ConnectingObject;
import ee.ut.cs.pix.bpmn.graph.FlowObject;
import ee.ut.cs.pix.bpmn.graph.Graph;

import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Sugiyama graph layout algorithm (Warfield, Carpano, and Sugiyama) implemented in Graphviz ("dot"
 * layout).
 */
public class SugiyamaGraphvizLayout implements Layout {
    private static final Double pointsInInch = 72.0;

    @Override
    public void apply(Graph graph) throws IOException {
        updateGraphNodes(applyLayout(graphToDot(graph)), graph);
    }

    private void updateGraphNodes(MutableGraph gvGraph, Graph graph) {
        // constructing maps to reduce time for finding nodes and edges in the original graph below
        HashMap<String, FlowObject> nodeIdToNodeMap = new HashMap<>();
        HashMap<String, ConnectingObject> edgeIdToNodeMap = new HashMap<>();
        // assumption: each element has a unique ID
        graph.getNodes().forEach(n -> nodeIdToNodeMap.put(n.getId(), n));
        graph.getEdges().forEach(e -> edgeIdToNodeMap.put(e.getId(), e));
        // updating the nodes
        gvGraph.nodes()
                .forEach(
                        n -> {
                            double[] coordinates = nodePosition(n);
                            if (coordinates == null) return;
                            FlowObject node = nodeIdToNodeMap.get(n.name().toString());
                            if (node == null) return;
                            ShapeBounds bounds = node.getBounds();
                            Double width = bounds.getWidth();
                            Double height = bounds.getHeight();
                            bounds.setX(coordinates[0] - width / 2.0);
                            bounds.setY(coordinates[1] - height / 2.0);
                        });
        // updating the edges
        gvGraph.edges()
                .forEach(
                        e -> {
                            double[] coordinates = edgePositions(e);
                            if (coordinates == null) return;
                            String eId = (String) e.attrs().get("id");
                            ConnectingObject edge = edgeIdToNodeMap.get(eId);
                            if (edge == null) return;
                            for (int i = 0; i < coordinates.length; i++) {
                                double x = coordinates[i];
                                double y = coordinates[i + 1];
                                edge.addWaypoint(x, y);
                                i++;
                            }
                        });
    }

    private MutableGraph applyLayout(String dotGraph) throws IOException {
        MutableGraph g = new Parser().read(dotGraph);
        g.setDirected(true);
        // render the graph to a string with coordinates
        String result =
                Graphviz.fromGraph(g)
                        .engine(Engine.DOT)
                        .yInvert(true)
                        .render(Format.XDOT)
                        .toString();
        // read back the graph with coordinates
        return new Parser().read(result);
    }

    static String graphToDot(Graph graph) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("layout=dot;\n");
        dot.append("node [shape=box, fixedsize=true];\n");
        dot.append("rankdir=LR;\n");
        dot.append("ranksep=1;\n");
        graph.getNodes().forEach(n -> dot.append(graphNodeToDot(n)));
        graph.getEdges()
                .forEach(
                        e ->
                                dot.append(
                                        String.format(
                                                "\"%s\" -> \"%s\" [id=\"%s\"];\n",
                                                e.getSource().getId(),
                                                e.getTarget().getId(),
                                                e.getId())));
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

    private double[] edgePositions(Link edge) {
        String pos = (String) edge.attrs().get("pos");
        if (pos == null) return null;
        String[] pairs = pos.split(" ");
        // omitting the first element which is the position of the arrowhead
        pairs = Arrays.copyOfRange(pairs, 1, pairs.length);
        double[] coordinates = new double[pairs.length * 2];
        for (int i = 0; i < pairs.length; i++) {
            String[] coords = pairs[i].split(",");
            coordinates[i * 2] = Double.parseDouble(coords[0]);
            coordinates[i * 2 + 1] = Double.parseDouble(coords[1]);
        }
        return coordinates;
    }

    private static String graphNodeToDot(FlowObject node) {
        String id = node.getId(), name = node.getName();
        String type = node.getTypeName();
        ShapeBounds bounds = node.getBounds();

        StringBuilder dot = new StringBuilder();
        // add label
        dot.append("\"").append(id).append("\" [label=\"");
        if (name == null || name.isEmpty()) {
            dot.append(type);
        } else {
            dot.append(name);
        }
        dot.append("\"");
        // add shape form
        if (type.contains("event")) {
            dot.append(", shape=ellipse");
        } else if (type.contains("gateway")) {
            dot.append(", shape=diamond");
        }
        // add width and height
        if (bounds != null) {
            dot.append(
                    String.format(
                            ", width=%.2f, height=%.2f",
                            bounds.getWidth() / pointsInInch, bounds.getHeight() / pointsInInch));
        }
        // end of the attribute list
        dot.append("];\n");
        return dot.toString();
    }
}
