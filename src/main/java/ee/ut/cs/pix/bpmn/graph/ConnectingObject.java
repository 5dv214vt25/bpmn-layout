package ee.ut.cs.pix.bpmn.graph;

import ee.ut.cs.pix.bpmn.di.EdgeWaypoint;

import java.util.ArrayList;
import java.util.List;

/** ConnectingObject represents a sequence flow in the BPMN process. */
public class ConnectingObject {
    private final String id;
    private final String name;
    private final FlowObject source;
    private final FlowObject target;
    private final String typeName;
    private final List<EdgeWaypoint> waypoints = new ArrayList<>();

    public ConnectingObject(
            String id, String name, FlowObject source, FlowObject target, String typeName) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
        this.typeName = typeName.toLowerCase();
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

    public FlowObject getSource() {
        return source;
    }

    public FlowObject getTarget() {
        return target;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<EdgeWaypoint> getWaypoints() {
        return waypoints;
    }
}
