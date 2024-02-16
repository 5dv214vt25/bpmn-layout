package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.di.BPMNElement;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;

public class GraphvizLayout implements Layout {
    @Override
    public void apply(Graph graph) throws IOException {
        String dotGraph = graphToDot(graph);
        String dotGraphWithCoordinates = null;
        dotGraphWithCoordinates = nidiGraphviz(dotGraph);

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

    private String nidiGraphviz(String dotGraph) throws IOException {
        MutableGraph g = new Parser().read(dotGraph);
        g.setDirected(true);
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example.png"));
        Graphviz.fromGraph(g).render(Format.XDOT).toFile(new File("example.dot"));
        return Graphviz.fromGraph(g).render(Format.XDOT).toString();
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
}
