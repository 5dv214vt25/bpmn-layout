package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.layout.di.BPMNElement;

public class FlowArc {
    public String id;
    public String name;
    public FlowNode source;
    public FlowNode target;
    public BPMNElement type;

    public FlowArc(String id, FlowNode source, FlowNode target, BPMNElement type) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public FlowArc(String id, String name, FlowNode source, FlowNode target, BPMNElement type) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
        this.type = type;
    }
}
