package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.di.*;
import ee.ut.cs.pix.bpmn.graph.ConnectingObject;
import ee.ut.cs.pix.bpmn.graph.FlowObject;
import ee.ut.cs.pix.bpmn.graph.Graph;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XmlExporter {
    public static void addDiagramInterchangeToDefinitions(BpmnModelInstance model, Graph graph) {
        List<Shape> shapes = createShapes(graph);
        List<Edge> edges = createEdges(graph);
        addDiagramToDefinitions(model, shapes, edges);
    }

    private static List<Shape> createShapes(Graph graph) {
        List<Shape> shapes = new ArrayList<>();
        for (FlowObject node : graph.getNodes()) {
            shapes.add(new Shape(node.getId(), false, node.getBounds()));
        }
        return shapes;
    }

    private static List<Edge> createEdges(Graph graph) {
        List<Edge> edges = new ArrayList<>();
        for (ConnectingObject edge : graph.getEdges()) {
            String id = edge.getId() + "_edge";
            edges.add(
                    new Edge(
                            id,
                            edge.getId(),
                            edge.getSource().getId(),
                            edge.getTarget().getId(),
                            edge.getWaypoints()));
        }
        return edges;
    }

    private static void addDiagramToDefinitions(
            BpmnModelInstance model, List<Shape> shapes, List<Edge> edges)
            throws IllegalArgumentException {
        Definitions definitions = model.getDefinitions();
        Process process =
                model.getModelElementsByType(Process.class).stream()
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
        String processId = process.getId();

        BpmnDiagram diagram = model.newInstance(BpmnDiagram.class);
        diagram.setId("BPMNDiagram_1");

        BpmnPlane plane = model.newInstance(BpmnPlane.class);
        plane.setId("BPMNPlane_1");
        plane.setAttributeValue("bpmnElement", processId);
        diagram.addChildElement(plane);

        for (Shape shape : shapes) {
            plane.addChildElement(createShape(model, shape));
        }
        for (Edge edge : edges) {
            plane.addChildElement(createEdge(model, edge));
        }

        // remove existing diagrams if present
        Collection<BpmnDiagram> existingDiagrams = definitions.getBpmDiagrams();
        if (existingDiagrams != null && !existingDiagrams.isEmpty())
            existingDiagrams.forEach(definitions::removeChildElement);

        definitions.addChildElement(diagram);
    }

    private static BpmnShape createShape(BpmnModelInstance model, Shape shape) {
        BpmnShape bpmnShape = model.newInstance(BpmnShape.class);
        bpmnShape.setAttributeValue("id", shape.getId());
        bpmnShape.setAttributeValue("bpmnElement", shape.getBpmnElement());
        bpmnShape.setAttributeValue("isMarkerVisible", shape.getMarkerVisible().toString());
        if (shape.getBounds() != null) {
            bpmnShape.addChildElement(createBounds(model, shape.getBounds()));
        }
        return bpmnShape;
    }

    private static BpmnEdge createEdge(BpmnModelInstance model, Edge edge) {
        BpmnEdge bpmnEdge = model.newInstance(BpmnEdge.class);
        bpmnEdge.setId(edge.getId());
        bpmnEdge.setAttributeValue("bpmnElement", edge.getBpmnElement());
        bpmnEdge.setAttributeValue("sourceElement", edge.getSourceElement());
        bpmnEdge.setAttributeValue("targetElement", edge.getTargetElement());
        for (EdgeWaypoint wp : edge.getWaypoints()) {
            Waypoint waypoint = model.newInstance(Waypoint.class);
            waypoint.setX(wp.getX());
            waypoint.setY(wp.getY());
            bpmnEdge.addChildElement(waypoint);
        }
        return bpmnEdge;
    }

    private static Bounds createBounds(BpmnModelInstance model, ShapeBounds shapeBounds) {
        Bounds bounds = model.newInstance(Bounds.class);
        bounds.setX(shapeBounds.getX());
        bounds.setY(shapeBounds.getY());
        bounds.setWidth(shapeBounds.getWidth());
        bounds.setHeight(shapeBounds.getHeight());
        return bounds;
    }
}
