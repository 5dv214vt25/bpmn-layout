package ee.ut.cs.pix.bpmn.layout;

import ee.ut.cs.pix.bpmn.graph.Graph;

/** A layout algorithm for arranging nodes and, optionally, edges, in a graph. */
public interface Layout {
    /** Apply the layout algorithm to the given graph. */
    void apply(Graph graph) throws Exception;
}
