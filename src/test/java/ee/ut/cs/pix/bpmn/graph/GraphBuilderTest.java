package ee.ut.cs.pix.bpmn.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class GraphBuilderTest {

    @Test
    void build() throws Exception {
        try (InputStream input =
                Files.newInputStream(
                        Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn"),
                        StandardOpenOption.READ)) {
            BpmnModelInstance model = new BpmnParser().parseModelFromStream(input);
            Graph graph = new GraphBuilder().build(model);

            assertFalse(graph.getNodes().isEmpty());
            assertFalse(graph.getEdges().isEmpty());
        }
    }
}
