package ee.ut.cs.pix.bpmn.graph;

import ee.ut.cs.pix.bpmn.di.BPMNElement;
import ee.ut.cs.pix.bpmn.di.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class FlowArc {
    public String id;
    public String name;
    public FlowNode source;
    public FlowNode target;
    public BPMNElement type;
    public List<Waypoint> waypoints = new ArrayList<>();

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

    public void addWaypoint(double x, double y) {
        waypoints.add(new Waypoint(x, y));
    }
}
