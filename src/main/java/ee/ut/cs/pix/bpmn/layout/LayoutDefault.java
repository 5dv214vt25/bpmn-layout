package ee.ut.cs.pix.bpmn.layout;

import static ee.ut.cs.pix.bpmn.DiagramExporter.addDiagramToDefinitions;
import static ee.ut.cs.pix.bpmn.DomUtils.*;

import ee.ut.cs.pix.bpmn.di.BPMNEdge;
import ee.ut.cs.pix.bpmn.di.BPMNElement;
import ee.ut.cs.pix.bpmn.di.BPMNShape;
import ee.ut.cs.pix.bpmn.di.Bounds;
import ee.ut.cs.pix.bpmn.graph.FlowArc;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;
import ee.ut.cs.pix.bpmn.graph.GraphBuilder;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class LayoutDefault implements Layout {
    @Override
    public String createLayout(String process, Coordinator coordinator) throws Exception {
        Document doc = parseXML(process);
        Graph graph = new GraphBuilder().build(doc);

        coordinator.updateCoordinates(graph);

        List<BPMNShape> shapes = createShapes(graph);
        List<BPMNEdge> edges = createEdges(graph);
        addDiagramToDefinitions(doc, shapes, edges);

        return convertDocumentToString(doc);
    }

    static List<BPMNShape> createShapes(Graph graph) {
        List<BPMNShape> shapes = new ArrayList<>();
        for (FlowNode node : graph.getNodes()) {
            Bounds bounds = createBounds(node);
            shapes.add(new BPMNShape(node.id, false, bounds));
        }
        return shapes;
    }

    static List<BPMNEdge> createEdges(Graph graph) {
        List<BPMNEdge> edges = new ArrayList<>();
        for (FlowArc edge : graph.getEdges()) {
            String id = edge.id + "_edge";
            edges.add(new BPMNEdge(id, edge.id, edge.source.id, edge.target.id, edge.waypoints));
        }
        return edges;
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
}
