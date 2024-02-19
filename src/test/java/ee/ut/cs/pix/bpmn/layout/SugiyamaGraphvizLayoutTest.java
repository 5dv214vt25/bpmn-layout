package ee.ut.cs.pix.bpmn.layout;

import static org.junit.jupiter.api.Assertions.*;

import ee.ut.cs.pix.bpmn.graph.Graph;
import ee.ut.cs.pix.bpmn.graph.GraphBuilder;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

class SugiyamaGraphvizLayoutTest {

    @Test
    void graphToDot() throws Exception {
        String bpmnModel =
                new String(
                        Files.readAllBytes(
                                Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn")));

        Graph graph = GraphBuilder.buildFromString(bpmnModel);

        String dot = SugiyamaGraphvizLayout.graphToDot(graph);

        assertTrue(dot.contains("digraph G {"));
    }
}
