package ee.ut.cs.pix.bpmn.di;

import ee.ut.cs.pix.bpmn.graph.FlowElementType;

/** BPMN Shape Bounds of Diagram Interchange (DI) from the BPMN 2.0 specification. */
public class ShapeBounds {
    private Double x;
    private Double y;
    private Double width;
    private Double height;

    public ShapeBounds(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static ShapeBounds forNode(FlowElementType type) {
        ShapeBounds bounds;
        if (type == FlowElementType.TASK) {
            bounds = ShapeBounds.defaultTaskBounds();
        } else if (type == FlowElementType.STARTEVENT) {
            bounds = ShapeBounds.defaultEventBounds();
        } else if (type == FlowElementType.ENDEVENT) {
            bounds = ShapeBounds.defaultEventBounds();
        } else if (type == FlowElementType.INCLUSIVEGATEWAY) {
            bounds = ShapeBounds.defaultGatewayBounds();
        } else if (type == FlowElementType.EXCLUSIVEGATEWAY) {
            bounds = ShapeBounds.defaultGatewayBounds();
        } else if (type == FlowElementType.PARALLELGATEWAY) {
            bounds = ShapeBounds.defaultGatewayBounds();
        } else {
            bounds = ShapeBounds.defaultBounds();
        }
        return bounds;
    }

    public static ShapeBounds defaultTaskBounds() {
        return new ShapeBounds(0.0, 0.0, 100.0, 80.0);
    }

    public static ShapeBounds defaultEventBounds() {
        return new ShapeBounds(0.0, 0.0, 36.0, 36.0);
    }

    public static ShapeBounds defaultGatewayBounds() {
        return new ShapeBounds(0.0, 0.0, 50.0, 50.0);
    }

    public static ShapeBounds defaultBounds() {
        return new ShapeBounds(0.0, 0.0, 100.0, 80.0);
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
