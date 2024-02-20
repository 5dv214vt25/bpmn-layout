package ee.ut.cs.pix.bpmn.graph;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * GraphBuilder provides API for building a process graph from Camunda's ModelInstance or
 * w3c.Document.
 */
public class GraphBuilder {
    private final HashMap<String, Boolean> visitedNodes = new HashMap<>();
    private final Graph graph = new Graph();
    private BpmnModelInstance model;

    private static FlowElement getTargetFromSequenceFlow(FlowElement element) {
        return ((SequenceFlow) element).getTarget();
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

    private static FlowObject createFlowObject(FlowElement node) {
        String id = node.getId();
        String name = node.getName();
        String typeName = node.getElementType().getTypeName();
        return new FlowObject(id, name, typeName);
    }

    private static ConnectingObject createConnectingObject(FlowElement element) {
        String id = element.getId();
        String name = element.getName();
        SequenceFlow flow = (SequenceFlow) element;
        FlowElement source = flow.getSource();
        FlowElement target = flow.getTarget();
        return new ConnectingObject(
                id, name, createFlowObject(source), createFlowObject(target), "sequenceFlow");
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

        String typeName = element.getElementType().getTypeName().toLowerCase();

        if (typeName.equals("endevent")) {
            graph.addNode(createFlowObject(element));
            return;
        }
        if (typeName.equals("sequenceflow")) {
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

    private boolean isVisited(BaseElement element) {
        return visitedNodes.containsKey(element.getId());
    }

    private void addToVisited(BaseElement element) {
        visitedNodes.put(element.getId(), true);
    }
}
