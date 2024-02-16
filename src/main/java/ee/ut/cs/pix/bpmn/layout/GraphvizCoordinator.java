package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.di.BPMNElement;
import ee.ut.cs.pix.bpmn.di.Bounds;
import ee.ut.cs.pix.bpmn.di.Waypoint;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphvizCoordinator implements Coordinator {
    @Override
    public void updateCoordinates(Graph graph) {
        String dotGraph = graphToDot(graph);
        String dotGraphWithCoordinates = null;
        try {
            dotGraphWithCoordinates = nidiGraphviz(dotGraph);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert dotGraphWithCoordinates != null;
            MutableGraph g = new Parser().read(dotGraphWithCoordinates);
            g.nodes()
                    .forEach(
                            n -> {
                                String pos = (String) n.attrs().get("pos");
                                if (pos == null) return;
                                String[] xy = pos.split(",");
                                graph.getNodes().stream()
                                        .filter(node -> node.id.equals(n.name().toString()))
                                        .forEach(
                                                node -> {
                                                    node.x = Double.parseDouble(xy[0]);
                                                    node.y = Double.parseDouble(xy[1]);
                                                });
                            });
            g.edges()
                    .forEach(
                            e -> {
                                String[] points = e.attrs().get("pos").toString().split(" ");
                                List<Waypoint> waypoints =
                                        Arrays.stream(points, 1, points.length)
                                                .map(coords -> coords.split(","))
                                                .map(
                                                        coord ->
                                                                new Waypoint(
                                                                        Double.parseDouble(
                                                                                coord[0]),
                                                                        Double.parseDouble(
                                                                                coord[1])))
                                                .collect(Collectors.toList());
                                String[] split = e.name().toString().split("--");
                                // String source = split[0];
                                // String target = split[1];
                                // graph.getEdges().stream()
                                //         .filter(
                                //                 edge ->
                                //                         edge.source.id.equals(source)
                                //                                 && edge.target.id.equals(target))
                                //         .forEach(
                                //                 edge -> {
                                //                     waypoints.forEach(
                                //                             w -> {
                                //                                 w.x =
                                //                                         w.x
                                //                                                 + Bounds.forNode(
                                //
                                //       edge.type)
                                //
                                // .width
                                //                                                         / 3;
                                //                                 w.y =
                                //                                         w.y
                                //                                                 + Bounds.forNode(
                                //
                                //       edge.type)
                                //
                                // .height
                                //                                                         / 3;
                                //                             });
                                //                     edge.waypoints = waypoints;
                                //                 });
                            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String graphToDot(Graph graph) {
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
                                                "\"%s\" -> \"%s\";\n", e.source.id, e.target.id)));
        dot.append("}");
        return dot.toString();
    }

    private static String graphNodeToDot(FlowNode node) {
        StringBuilder dot = new StringBuilder();
        dot.append("\"").append(node.id).append("\" [label=\"");
        if (node.name == null || node.name.isEmpty()) {
            dot.append(node.type.getValue());
        } else {
            dot.append(node.name);
        }
        dot.append("\"");
        if (node.type == BPMNElement.STARTEVENT || node.type == BPMNElement.ENDEVENT) {
            dot.append(", shape=ellipse");
        } else if (node.type == BPMNElement.INCLUSIVEGATEWAY
                || node.type == BPMNElement.EXCLUSIVEGATEWAY
                || node.type == BPMNElement.PARALLELGATEWAY) {
            dot.append(", shape=diamond");
        }
        if (node.bounds != null) {
            dot.append(
                    String.format(
                            ", width=%.2f, height=%.2f",
                            node.bounds.width / 100, node.bounds.height / 80));
        }

        dot.append("];\n");
        return dot.toString();
    }

    private String nidiGraphviz(String dotGraph) throws IOException {
        MutableGraph g = new Parser().read(dotGraph);
        g.setDirected(true);
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example.png"));
        Graphviz.fromGraph(g).render(Format.XDOT).toFile(new File("example.dot"));
        return Graphviz.fromGraph(g).render(Format.XDOT).toString();
    }
}
