package ee.ut.cs.pix.bpmn.layout;

import static org.junit.jupiter.api.Assertions.*;

import ee.ut.cs.pix.bpmn.graph.Graph;
import ee.ut.cs.pix.bpmn.graph.GraphBuilder;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class SugiyamaGraphvizLayoutTest {

    @Test
    void graphToDot() {
        BpmnModelInstance model =
                Bpmn.readModelFromFile(
                        Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn").toFile());
        Graph graph = new GraphBuilder().build(model);

        String dot = SugiyamaGraphvizLayout.graphToDot(graph);

        assertTrue(dot.contains("digraph G {"));
    }
}
