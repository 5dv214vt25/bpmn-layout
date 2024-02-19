package ee.ut.cs.pix.bpmn.di;

import java.util.Collection;

/** BPMN Edge of Diagram Interchange (DI) from the BPMN 2.0 specification. */
public class Edge {
    private final String id;
    private final String bpmnElement;
    private final String sourceElement;
    private final String targetElement;
    private final Collection<EdgeWaypoint> waypoints;

    public Edge(
            String id,
            String bpmnElement,
            String sourceElement,
            String targetElement,
            Collection<EdgeWaypoint> waypoints) {
        this.id = id;
        this.bpmnElement = bpmnElement;
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
        this.waypoints = waypoints;
    }

    public String getId() {
        return id;
    }

    public String getBpmnElement() {
        return bpmnElement;
    }

    public String getSourceElement() {
        return sourceElement;
    }

    public String getTargetElement() {
        return targetElement;
    }

    public Collection<EdgeWaypoint> getWaypoints() {
        return waypoints;
    }
}
