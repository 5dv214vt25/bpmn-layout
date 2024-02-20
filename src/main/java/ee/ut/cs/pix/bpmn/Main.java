package ee.ut.cs.pix.bpmn;

import ee.ut.cs.pix.bpmn.layout.Generator;
import ee.ut.cs.pix.bpmn.layout.Layout;
import ee.ut.cs.pix.bpmn.layout.SugiyamaGraphvizLayout;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());
        String bpmnModelPath = args[0];
        String outputFilePath = args[1];
        Layout layout = new SugiyamaGraphvizLayout();
        try (InputStream input = Files.newInputStream(Paths.get(bpmnModelPath));
                OutputStream output = Files.newOutputStream(Paths.get(outputFilePath))) {
            Generator.addDiagramToDefinitions(input, output, layout);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}
