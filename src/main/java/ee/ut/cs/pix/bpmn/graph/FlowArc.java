package ee.ut.cs.pix.bpmn.graph;

import ee.ut.cs.pix.bpmn.di.EdgeWaypoint;

import java.util.ArrayList;
import java.util.List;

/** FLowArc represents a sequence flow in the BPMN process. */
public class FlowArc {
    private final String id;
    private final String name;
    private final FlowNode source;
    private final FlowNode target;
    private final FlowElementType type;
    private final List<EdgeWaypoint> waypoints = new ArrayList<>();

    public FlowArc(String id, String name, FlowNode source, FlowNode target, FlowElementType type) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public void addWaypoint(double x, double y) {
        waypoints.add(new EdgeWaypoint(x, y));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FlowNode getSource() {
        return source;
    }

    public FlowNode getTarget() {
        return target;
    }

    public FlowElementType getType() {
        return type;
    }

    public List<EdgeWaypoint> getWaypoints() {
        return waypoints;
    }
}
