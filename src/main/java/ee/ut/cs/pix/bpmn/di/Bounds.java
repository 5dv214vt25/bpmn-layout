package ee.ut.cs.pix.bpmn.di;

import ee.ut.cs.pix.bpmn.graph.FlowNode;

public class Bounds {
    public Double x;
    public Double y;
    public Double width;
    public Double height;

    public Bounds(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static Bounds defaultBounds() {
        return new Bounds(0.0, 0.0, 100.0, 80.0);
    }

    public static Bounds defaultGatewayBounds() {
        return new Bounds(0.0, 0.0, 50.0, 50.0);
    }

    public static Bounds defaultEventBounds() {
        return new Bounds(0.0, 0.0, 36.0, 36.0);
    }

    public static Bounds defaultTaskBounds() {
        return new Bounds(0.0, 0.0, 100.0, 80.0);
    }

    public static Bounds forNode(BPMNElement type) {
        Bounds bounds;
        if (type == BPMNElement.TASK) {
            bounds = Bounds.defaultTaskBounds();
        } else if (type == BPMNElement.STARTEVENT) {
            bounds = Bounds.defaultEventBounds();
        } else if (type == BPMNElement.ENDEVENT) {
            bounds = Bounds.defaultEventBounds();
        } else if (type == BPMNElement.INCLUSIVEGATEWAY) {
            bounds = Bounds.defaultGatewayBounds();
        } else if (type == BPMNElement.EXCLUSIVEGATEWAY) {
            bounds = Bounds.defaultGatewayBounds();
        } else if (type == BPMNElement.PARALLELGATEWAY) {
            bounds = Bounds.defaultGatewayBounds();
        } else {
            bounds = Bounds.defaultBounds();
        }
        return bounds;
    }
}
