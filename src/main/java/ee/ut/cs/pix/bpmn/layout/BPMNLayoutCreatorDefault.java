package ee.ut.cs.pix.bpmn.layout;

import static ee.ut.cs.pix.bpmn.layout.DiagramExporter.addDiagramToDefinitions;
import static ee.ut.cs.pix.bpmn.layout.DomUtils.*;

import ee.ut.cs.pix.bpmn.layout.di.BPMNEdge;
import ee.ut.cs.pix.bpmn.layout.di.BPMNElement;
import ee.ut.cs.pix.bpmn.layout.di.BPMNShape;
import ee.ut.cs.pix.bpmn.layout.di.Bounds;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class BPMNLayoutCreatorDefault implements BPMNLayoutCreator {
    static List<BPMNShape> createShapes(Graph graph) {
        List<BPMNShape> shapes = new ArrayList<>();
        for (FlowNode node : graph.getNodes()) {
            Bounds bounds = createBounds(node);
            shapes.add(new BPMNShape(node.id, false, bounds));
        }
        return shapes;
    }

    private static Bounds createBounds(FlowNode node) {
        Bounds bounds;
        if (node.type == BPMNElement.TASK) {
            bounds = Bounds.defaultTaskBounds();
        } else if (node.type == BPMNElement.STARTEVENT) {
            bounds = Bounds.defaultEventBounds();
        } else if (node.type == BPMNElement.ENDEVENT) {
            bounds = Bounds.defaultEventBounds();
        } else if (node.type == BPMNElement.INCLUSIVEGATEWAY) {
            bounds = Bounds.defaultGatewayBounds();
        } else if (node.type == BPMNElement.EXCLUSIVEGATEWAY) {
            bounds = Bounds.defaultGatewayBounds();
        } else if (node.type == BPMNElement.PARALLELGATEWAY) {
            bounds = Bounds.defaultGatewayBounds();
        } else {
            bounds = Bounds.defaultBounds();
        }
        bounds.x = node.x;
        bounds.y = node.y;
        return bounds;
    }

    static List<BPMNEdge> createEdges(Graph graph) {
        List<BPMNEdge> edges = new ArrayList<>();
        for (FlowArc edge : graph.getEdges()) {
            String id = edge.id + "_edge";
            edges.add(new BPMNEdge(id, edge.id, edge.source.id, edge.target.id));
        }
        return edges;
    }

    @Override
    public String createLayout(String process) throws Exception {
        Document doc = parseXML(process);

        Graph graph = new GraphBuilder().build(doc);

        JungLayout.createLayout(graph);

        List<BPMNShape> shapes = createShapes(graph);
        List<BPMNEdge> edges = createEdges(graph);

        addDiagramToDefinitions(doc, shapes, edges);

        return convertDocumentToString(doc);
    }
}
