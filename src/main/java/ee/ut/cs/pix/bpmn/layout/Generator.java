package ee.ut.cs.pix.bpmn.layout;

import static ee.ut.cs.pix.bpmn.DomUtils.*;

import ee.ut.cs.pix.bpmn.DomUtils;
import ee.ut.cs.pix.bpmn.XmlExporter;
import ee.ut.cs.pix.bpmn.graph.Graph;
import ee.ut.cs.pix.bpmn.graph.GraphBuilder;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.OutputStream;

public class Generator {
    public static void generateControlFlowWithDiagram(
            InputStream process, OutputStream output, Layout layout) throws Exception {
        Document doc = parseXML(process);
        Graph graph = new GraphBuilder().build(doc);
        layout.apply(graph);
        XmlExporter.addDiagramInterchangeToDefinitions(doc, graph);
        output.write(DomUtils.toXML(doc).getBytes());
    }
}
