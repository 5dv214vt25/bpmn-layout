package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.di.BPMNElement;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.IOException;

public class GraphvizLayout implements Layout {
    @Override
    public void apply(Graph graph) throws IOException {
        nidiGraphviz(graphToDot(graph))
                .nodes()
                .forEach(
                        n -> {
                            // getting the node position
                            String pos = (String) n.attrs().get("pos");
                            if (pos == null) return;
                            String[] xy = pos.split(",");
                            double x = Double.parseDouble(xy[0]);
                            double y = Double.parseDouble(xy[1]);
                            // updating the node position in the input graph
                            graph.getNodes().stream()
                                    .filter(node -> node.id.equals(n.name().toString()))
                                    .forEach(
                                            node -> {
                                                node.x = x;
                                                node.y = y;
                                            });
                        });
    }

    private MutableGraph nidiGraphviz(String dotGraph) throws IOException {
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
}
