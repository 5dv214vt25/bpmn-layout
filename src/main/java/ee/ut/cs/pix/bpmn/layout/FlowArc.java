package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.layout.di.BPMNElement;

public class FlowArc {
    public String id;
    public FlowNode source;
    public FlowNode target;
    public BPMNElement type;

    public FlowArc(String id, FlowNode source, FlowNode target, BPMNElement type) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
    }
}
