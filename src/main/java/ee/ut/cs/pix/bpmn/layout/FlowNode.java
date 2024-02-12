package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.layout.di.BPMNElement;

// FlowNode represents a node in the BPMN process, e.g., a task, event, or gateway.
public class FlowNode {
    public String id;
    public BPMNElement type;
    public Double x;
    public Double y;

    public FlowNode(String id, BPMNElement type) {
        this.id = id;
        this.type = type;
    }
}
