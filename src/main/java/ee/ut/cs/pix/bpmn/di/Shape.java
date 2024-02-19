package ee.ut.cs.pix.bpmn.di;

/** BPMN Shape of Diagram Interchange (DI) from the BPMN 2.0 specification. */
public class Shape {
    private final String id;
    private final String bpmnElement;
    private final Boolean isMarkerVisible;
    private final ShapeBounds bounds;

    public Shape(String bpmnElement, Boolean isMarkerVisible, ShapeBounds bounds) {
        this.id = bpmnElement + "_shape";
        this.bpmnElement = bpmnElement;
        this.isMarkerVisible = isMarkerVisible;
        if (bounds == null) {
            this.bounds = ShapeBounds.defaultBounds();
        } else {
            this.bounds = bounds;
        }
    }

    public String getId() {
        return id;
    }

    public String getBpmnElement() {
        return bpmnElement;
    }

    public Boolean getMarkerVisible() {
        return isMarkerVisible;
    }

    public ShapeBounds getBounds() {
        return bounds;
    }
}
