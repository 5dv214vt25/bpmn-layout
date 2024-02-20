package ee.ut.cs.pix.bpmn.graph;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;

/** FlowObject represents a node in the BPMN process, e.g., a task, event, or gateway. */
public class FlowObject {
    private final String id;
    private final String name;
    private final FlowElementType type;
    private final ShapeBounds bounds;

    public FlowObject(String id, String name, FlowElementType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bounds = ShapeBounds.forNode(type);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FlowElementType getType() {
        return type;
    }

    public ShapeBounds getBounds() {
        return bounds;
    }
}
