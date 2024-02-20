package ee.ut.cs.pix.bpmn.graph;

import static ee.ut.cs.pix.bpmn.DomUtils.*;

import ee.ut.cs.pix.bpmn.DomUtils;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GraphBuilder provides API for building a process graph from Camunda's ModelInstance or
 * w3c.Document.
 */
public class GraphBuilder {
    private final HashMap<String, Boolean> visitedNodes = new HashMap<>();
    private final Graph graph = new Graph();
    private BpmnModelInstance model;

    public static Graph buildFromString(String xml) throws Exception {
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        Document doc = parseXML(xmlStream);
        return new GraphBuilder().build(doc);
    }

    private static String getOptionalName(Node node) {
        String name;
        try {
            name = node.getAttributes().getNamedItem("name").getNodeValue();
        } catch (NullPointerException e) {
            name = "";
        }
        return name;
    }

    private static Node getSourceFromSequenceFlow(Node sequenceFlow) {
        String sourceRef = getAttributeValue(sequenceFlow, "sourceRef");
        return getNodeById(sequenceFlow.getOwnerDocument(), sourceRef);
    }

    private static Node getTargetFromSequenceFlow(Node sequenceFlow) {
        String targetRef = getAttributeValue(sequenceFlow, "targetRef");
        return getNodeById(sequenceFlow.getOwnerDocument(), targetRef);
    }

    private static FlowElement getTargetFromSequenceFlow(FlowElement element) {
        return ((SequenceFlow) element).getTarget();
    }

    private static String getAttributeValue(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    private static List<Node> getOutgoingFlows(Node node) {
        return getChildrenByTagName(node, "outgoing");
    }

    private static Collection<SequenceFlow> getOutgoingFlows(FlowElement element) {
        FlowNode node = (FlowNode) element;
        return node.getOutgoing();
    }

    private static Collection<SequenceFlow> getSequenceForElement(
            BpmnModelInstance model, FlowElement element) {
        Collection<SequenceFlow> flows = model.getModelElementsByType(SequenceFlow.class);
        return flows.stream()
                .filter(flow -> flow.getSource().getId().equals(element.getId()))
                .collect(Collectors.toList());
    }

    private static List<Node> getIncomingNodes(Node node) {
        return getChildrenByTagName(node, "incoming");
    }

    private static FlowObject createFlowObject(Node node) {
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        String name = getOptionalName(node);
        String nodeName = node.getNodeName();
        return new FlowObject(id, name, FlowElementType.fromValue(nodeName));
    }

    private static FlowObject createFlowObject(FlowElement node) {
        String id = node.getId();
        String name = node.getName();
        String typeName = node.getElementType().getTypeName();
        return new FlowObject(id, name, FlowElementType.fromValue(typeName));
    }

    private static ConnectingObject createConnectingObject(Node node) {
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        String name = getOptionalName(node);
        Node source = getSourceFromSequenceFlow(node);
        Node target = getTargetFromSequenceFlow(node);
        return new ConnectingObject(
                id,
                name,
                createFlowObject(source),
                createFlowObject(target),
                FlowElementType.SEQUENCEFLOW);
    }

    private static ConnectingObject createConnectingObject(FlowElement element) {
        String id = element.getId();
        String name = element.getName();
        SequenceFlow flow = (SequenceFlow) element;
        FlowElement source = flow.getSource();
        FlowElement target = flow.getTarget();
        return new ConnectingObject(
                id,
                name,
                createFlowObject(source),
                createFlowObject(target),
                FlowElementType.SEQUENCEFLOW);
    }

    public Graph build(Document doc) {
        Node start = DomUtils.getFirstByTagName(doc, "startEvent");
        if (start == null) throw new IllegalArgumentException("No start event found");
        traverseNode(start);
        return graph;
    }

    public Graph build(BpmnModelInstance model) {
        this.model = model;
        Collection<StartEvent> startEvents = model.getModelElementsByType(StartEvent.class);
        StartEvent start =
                startEvents.stream().findFirst().orElseThrow(IllegalArgumentException::new);
        traverseFlowElement(start);
        return graph;
    }

    private void traverseFlowElement(FlowElement element) {
        if (isVisited(element)) return;
        addToVisited(element);

        String typeName = element.getElementType().getTypeName();

        if (FlowElementType.fromValue(typeName) == FlowElementType.ENDEVENT) {
            graph.addNode(createFlowObject(element));
            return;
        }
        if (FlowElementType.fromValue(typeName) == FlowElementType.SEQUENCEFLOW) {
            FlowElement next = getTargetFromSequenceFlow(element);
            if (next != null) {
                graph.addEdge(createConnectingObject(element));
                traverseFlowElement(next);
            } else {
                throw new IllegalArgumentException(
                        "All sequences must have the source and target values");
            }
        } else {
            graph.addNode(createFlowObject(element));
            Collection<SequenceFlow> flows = getOutgoingFlows(element);
            // some nodes may have no outgoing flows, so check the corresponding sequence flows
            if (flows.isEmpty()) flows = getSequenceForElement(model, element);
            flows.forEach(this::traverseFlowElement);
        }
    }

    private void traverseNode(Node node) {
        if (node == null) throw new IllegalArgumentException("Node is null");
        if (isVisited(node)) return;
        addToVisited(node);

        String nodeName = node.getNodeName();
        String id = node.getAttributes().getNamedItem("id").getNodeValue();

        if (FlowElementType.fromValue(nodeName) == FlowElementType.ENDEVENT) {
            graph.addNode(createFlowObject(node));
            return;
        }
        if (FlowElementType.fromValue(nodeName) == FlowElementType.SEQUENCEFLOW) {
            Node next = getTargetFromSequenceFlow(node);
            if (next != null) {
                graph.addEdge(createConnectingObject(node));
                traverseNode(next);
            } else {
                System.out.println("No next node found for sequence flow: " + id);
            }
        } else {
            graph.addNode(createFlowObject(node));
            getOutgoingFlows(node).forEach(this::traverseNode);
        }
    }

    private boolean isVisited(Node node) {
        return visitedNodes.containsKey(node.getAttributes().getNamedItem("id").getNodeValue());
    }

    private boolean isVisited(BaseElement element) {
        return visitedNodes.containsKey(element.getId());
    }

    private void addToVisited(Node node) {
        visitedNodes.put(node.getAttributes().getNamedItem("id").getNodeValue(), true);
    }

    private void addToVisited(BaseElement element) {
        visitedNodes.put(element.getId(), true);
    }
}
