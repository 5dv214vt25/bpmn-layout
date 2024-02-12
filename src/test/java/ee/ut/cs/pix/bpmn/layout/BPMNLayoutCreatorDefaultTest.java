package ee.ut.cs.pix.bpmn.layout;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BPMNLayoutCreatorDefaultTest {

    @Test
    void createLayout() throws Exception {
        String bpmnModel =
                new String(
                        Files.readAllBytes(
                                Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn")));

        String result = (new BPMNLayoutCreatorDefault()).createLayout(bpmnModel);
        assertTrue(result.contains("BPMNShape"));
        Files.write(
                Paths.get("src/test/resources/LoanApp_simplified_nodi_layout.bpmn"),
                result.getBytes());
    }
}
