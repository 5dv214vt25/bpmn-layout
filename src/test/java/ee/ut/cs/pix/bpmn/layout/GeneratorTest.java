package ee.ut.cs.pix.bpmn.layout;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class GeneratorTest {

    @Test
    void testSimple() throws Exception {
        String result;

        try (InputStream input =
                        Files.newInputStream(
                                Paths.get("src/test/resources/LoanApp_simplified_nodi.bpmn"),
                                StandardOpenOption.READ);
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Layout layout = new SchaeferLayout();
            Generator.addDiagramToDefinitions(input, output, layout);
            result = output.toString();
        }

        assertTrue(result.contains("BPMNShape"));
        assertTrue(result.contains("BPMNEdge"));

        Files.write(
                Paths.get("src/test/resources/LoanApp_simplified_nodi_layout.bpmn"),
                result.getBytes());
    }

    @Test
    void testTimers() throws Exception {
        String result;

        try (InputStream input =
                        Files.newInputStream(
                                Paths.get("src/test/resources/LoanApp_simplified_train.bpmn"),
                                StandardOpenOption.READ);
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Layout layout = new SchaeferLayout();
            Generator.addDiagramToDefinitions(input, output, layout);
            result = output.toString();
        }

        assertTrue(result.contains("BPMNShape"));
        assertTrue(result.contains("BPMNEdge"));

        Files.write(
                Paths.get("src/test/resources/LoanApp_simplified_train_layout.bpmn"),
                result.getBytes());
    }
}
