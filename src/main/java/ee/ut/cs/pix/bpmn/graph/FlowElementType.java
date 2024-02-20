package ee.ut.cs.pix.bpmn.graph;

/** FlowElementType is an enumeration of supported flow element types of the BPMN control-flow. */
public enum FlowElementType {
    STARTEVENT("startEvent"),
    ENDEVENT("endEvent"),
    INTERMEDIATECATCHEVENT("intermediateCatchEvent"),
    TASK("task"),
    INCLUSIVEGATEWAY("inclusiveGateway"),
    EXCLUSIVEGATEWAY("exclusiveGateway"),
    PARALLELGATEWAY("parallelGateway"),
    SEQUENCEFLOW("sequenceFlow");

    private final String value;

    FlowElementType(String value) {
        this.value = value;
    }

    public static FlowElementType fromValue(String value) {
        return FlowElementType.valueOf(value.toUpperCase());
    }

    public String getValue() {
        return value;
    }
}
