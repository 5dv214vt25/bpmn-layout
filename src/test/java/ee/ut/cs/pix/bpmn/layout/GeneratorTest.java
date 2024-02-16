package ee.ut.cs.pix.bpmn.layout;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {

    @Test
    void createLayout() throws Exception {
        String result;

        try (InputStream input =
                        Files.newInputStream(
                                Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn"),
                                StandardOpenOption.READ);
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Layout layout = new GraphvizLayout();
            Generator.generateControlFlowWithDiagram(input, output, layout);
            result = output.toString();
        }

        assertTrue(result.contains("BPMNShape"));
        assertTrue(result.contains("BPMNEdge"));

        Files.write(
                Paths.get("src/test/resources/LoanApp_simplified_nodi_layout.bpmn"),
                result.getBytes());
    }
}
