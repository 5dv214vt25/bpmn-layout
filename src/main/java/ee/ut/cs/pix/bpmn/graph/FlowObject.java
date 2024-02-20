package ee.ut.cs.pix.bpmn.graph;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;

/** FlowObject represents a node in the BPMN process, e.g., a task, event, or gateway. */
public class FlowObject {
    private final String id;
    private final String name;
    private final String typeName; // e.g., task, startEvent, endEvent, inclusiveGateway, etc.
    private final ShapeBounds bounds;

    public FlowObject(String id, String name, String typeName) {
        this.id = id;
        this.name = name;
        this.typeName = typeName.toLowerCase();
        this.bounds = ShapeBounds.forTypeName(typeName);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public ShapeBounds getBounds() {
        return bounds;
    }
}
