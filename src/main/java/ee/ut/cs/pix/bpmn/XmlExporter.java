package ee.ut.cs.pix.bpmn;

import static ee.ut.cs.pix.bpmn.DomUtils.getFirstByTagName;

import ee.ut.cs.pix.bpmn.di.*;
import ee.ut.cs.pix.bpmn.graph.FlowArc;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

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
        for (FlowNode node : graph.getNodes()) {
            shapes.add(new Shape(node.getId(), false, node.getBounds()));
        }
        return shapes;
    }

    private static List<Edge> createEdges(Graph graph) {
        List<Edge> edges = new ArrayList<>();
        for (FlowArc edge : graph.getEdges()) {
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
}
