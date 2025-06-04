package ee.ut.cs.pix.bpmn.di;

/** BPMN Shape Bounds of Diagram Interchange (DI) from the BPMN 2.0 specification. */
public class ShapeBounds {
    private Double x;
    private Double y;
    private Double width;
    private Double height;

    public ShapeBounds(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        // here we flip the width and height so we get correct arrow placements
        this.width = height;
        this.height = width;
    }

    public static ShapeBounds forTypeName(String type) {
        ShapeBounds bounds;
        type = type.toLowerCase();
        if (type.contains("task")) {
            bounds = ShapeBounds.defaultTaskBounds();
        } else if (type.contains("event")) {
            bounds = ShapeBounds.defaultEventBounds();
        } else if (type.contains("gateway")) {
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
