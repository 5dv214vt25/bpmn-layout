package ee.ut.cs.pix.bpmn.layout;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutDefaultTest {

    @Test
    void createLayout() throws Exception {
        String bpmnModel =
                new String(
                        Files.readAllBytes(
                                Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn")));

        Coordinator coordinator = new GraphvizCoordinator();
        String result = (new LayoutDefault()).createLayout(bpmnModel, coordinator);
        assertTrue(result.contains("BPMNShape"));
        Files.write(
                Paths.get("src/test/resources/LoanApp_simplified_nodi_layout.bpmn"),
                result.getBytes());
    }
}
