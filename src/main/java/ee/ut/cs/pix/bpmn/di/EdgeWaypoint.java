package ee.ut.cs.pix.bpmn.di;

/** BPMN Edge Waypoint of Diagram Interchange (DI) from the BPMN 2.0 specification. */
public class EdgeWaypoint {
    private final Double x;
    private final Double y;

    public EdgeWaypoint(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}
