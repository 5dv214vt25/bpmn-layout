package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.graph.Graph;
import ee.ut.cs.pix.bpmn.graph.GraphBuilder;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;

import java.io.InputStream;
import java.io.OutputStream;

/** A class for generating BPMN diagrams from control flow in XML format. */
public class Generator {
    /**
     * Parse the given control flow in XML format, add a BPMN diagram interchange section, and write
     * the result to the output stream.
     */
    public static void addDiagramToDefinitions(
            InputStream input, OutputStream output, Layout layout) throws Exception {
        BpmnModelInstance model = new BpmnParser().parseModelFromStream(input);
        Graph graph = new GraphBuilder().build(model);
        layout.apply(graph);
        XmlExporter.addDiagramInterchangeToDefinitions(model, graph);
        Bpmn.writeModelToStream(output, model);
    }
}
