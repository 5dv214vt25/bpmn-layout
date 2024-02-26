package ee.ut.cs.pix.bpmn;

import ee.ut.cs.pix.bpmn.layout.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class Main {
    static String help =
            "The program supports up to two arguments:\n"
                    + "* The first required argument is the path to the input file, a BPMN model.\n"
                    + "* The second optional argument is the output path. If the output path is not specified, "
                    + "the input file will be overwritten with an updated content.\n"
                    + "Example: java -jar bpmn-layout.jar input.bpmn";

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());
        String inputPath, outputPath;
        try {
            if (args.length == 1) {
                if (args[0].equals("-h") || args[0].equals("--help")) {
                    logger.info(help);
                    System.exit(1);
                }
                inputPath = args[0];
                addBPMNDI(inputPath);
            } else if (args.length == 2) {
                inputPath = args[0];
                outputPath = args[1];
                addBPMNDI(inputPath, outputPath);
            } else {
                logger.info(help);
                System.exit(1);
            }
            System.exit(0);
        } catch (Exception e) {
            logger.severe(e.getLocalizedMessage());
            e.printStackTrace();
        }
        System.exit(1);
    }

    /**
     * Add BPMN Diagram Interchange to the control flow in the given file by overwriting the file.
     */
    private static void addBPMNDI(String bpmnPath) throws Exception {
        Path bpmnModelPath = Paths.get(bpmnPath);
        InputStream input = Files.newInputStream(bpmnModelPath);
        // write the XML into memory
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        Generator.addDiagramToDefinitions(input, result, new SchaeferLayout());
        input.close();
        // rewrite the BPMN file with the updated XML
        OutputStream output = Files.newOutputStream(bpmnModelPath);
        output.write(result.toByteArray());
        output.close();
    }

    /** Add BPMN Diagram Interchange to the control flow in the given file. */
    private static void addBPMNDI(String inputPath, String outputPath) throws Exception {
        Path bpmnModelPath = Paths.get(inputPath);
        InputStream input = Files.newInputStream(bpmnModelPath);
        OutputStream output =
                Files.newOutputStream(Paths.get(outputPath), StandardOpenOption.CREATE);
        Generator.addDiagramToDefinitions(input, output, new SchaeferLayout());
    }
}
