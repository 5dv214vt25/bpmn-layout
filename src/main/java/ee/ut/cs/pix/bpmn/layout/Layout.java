package ee.ut.cs.pix.bpmn.layout;

public interface Layout {
    /**
     * Create layout for the given process
     *
     * @param process BPMN process as string
     * @param coordinator coordinator to update coordinates of the underlying graph
     * @return BPMN definitions as a valid XML string
     * @throws Exception if something goes wrong
     */
    String createLayout(String process, Coordinator coordinator) throws Exception;
}
