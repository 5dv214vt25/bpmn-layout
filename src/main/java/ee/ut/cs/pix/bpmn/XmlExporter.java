package ee.ut.cs.pix.bpmn;

import static ee.ut.cs.pix.bpmn.DomUtils.getFirstByTagName;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class XmlExporter {
    public static void addDiagramInterchangeToDefinitions(Document doc, Graph graph) {
        List<Shape> shapes = createShapes(graph);
        List<Edge> edges = createEdges(graph);
        addDiagramToDefinitions(doc, shapes, edges);
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

    private static void addDiagramToDefinitions(Document doc, List<Shape> shapes, List<Edge> edges)
            throws IllegalArgumentException {
        // register dc and di namespaces
        Element definitionsEl = (Element) doc.getElementsByTagName("definitions").item(0);
        definitionsEl.setAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
        definitionsEl.setAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");

        Node processElement = getFirstByTagName(doc, "process");
        Element diagram = createDiagramXML(doc);
        Element plane =
                createPlaneXML(
                        doc, processElement.getAttributes().getNamedItem("id").getNodeValue());
        diagram.appendChild(plane);
        for (Shape shape : shapes) {
            Element shapeXML = convertShapeToXML(doc, shape);
            plane.appendChild(shapeXML);
        }
        for (Edge edge : edges) {
            Element edgeXML = convertEdgeToXML(doc, edge);
            plane.appendChild(edgeXML);
        }
        Node definitions = getFirstByTagName(doc, "definitions");
        definitions.appendChild(diagram);
    }

    private static Element createDiagramXML(Document doc) {
        Element root = doc.createElement("bpmndi:BPMNDiagram");
        root.setAttribute("id", "BPMNDiagram_1");
        return root;
    }

    private static Element createPlaneXML(Document doc, String processId) {
        Element root = doc.createElement("bpmndi:BPMNPlane");
        root.setAttribute("id", "BPMNPlane_1");
        root.setAttribute("bpmnElement", processId);
        return root;
    }

    private static Element convertShapeToXML(Document doc, Shape shape) {
        Element root = doc.createElement("bpmndi:BPMNShape");
        root.setAttribute("id", shape.getId());
        root.setAttribute("bpmnElement", shape.getBpmnElement());
        root.setAttribute("isMarkerVisible", shape.getMarkerVisible().toString());
        if (shape.getBounds() != null) {
            Element bounds = convertBoundsToXML(doc, shape.getBounds());
            root.appendChild(bounds);
        }
        return root;
    }

    private static Element convertEdgeToXML(Document doc, Edge edge) {
        Element root = doc.createElement("bpmndi:BPMNEdge");
        root.setAttribute("id", edge.getId());
        root.setAttribute("bpmnElement", edge.getBpmnElement());
        if (edge.getSourceElement() != null) {
            root.setAttribute("sourceElement", edge.getSourceElement());
        }
        if (edge.getTargetElement() != null) {
            root.setAttribute("targetElement", edge.getTargetElement());
        }
        for (EdgeWaypoint waypoint : edge.getWaypoints()) {
            Element wp = convertWaypointToXML(doc, waypoint);
            root.appendChild(wp);
        }
        return root;
    }

    private static Element convertBoundsToXML(Document doc, ShapeBounds bounds) {
        Element root = doc.createElement("dc:Bounds");
        root.setAttribute("x", bounds.getX().toString());
        root.setAttribute("y", bounds.getY().toString());
        root.setAttribute("width", bounds.getWidth().toString());
        root.setAttribute("height", bounds.getHeight().toString());
        return root;
    }

    private static Element convertWaypointToXML(Document doc, EdgeWaypoint waypoint) {
        Element root = doc.createElement("di:waypoint");
        root.setAttribute("x", waypoint.getX().toString());
        root.setAttribute("y", waypoint.getY().toString());
        return root;
    }

    public static void addDiagramInterchangeToDefinitions(BpmnModelInstance model, Graph graph) {
        List<Shape> shapes = createShapes(graph);
        List<Edge> edges = createEdges(graph);
        addDiagramToDefinitions(model, shapes, edges);
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
