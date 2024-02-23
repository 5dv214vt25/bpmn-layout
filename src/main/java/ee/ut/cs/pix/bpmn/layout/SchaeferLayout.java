package ee.ut.cs.pix.bpmn.layout;

import de.jfschaefer.layeredgraphlayout.gengraph.GenGraph;
import de.jfschaefer.layeredgraphlayout.layout.EdgeSegment;
import de.jfschaefer.layeredgraphlayout.layout.LayoutConfig;
import de.jfschaefer.layeredgraphlayout.layout.Point;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraph;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraphConfig;
import de.jfschaefer.layeredgraphlayout.pgraph.PGraph;

import ee.ut.cs.pix.bpmn.graph.ConnectingObject;
import ee.ut.cs.pix.bpmn.graph.FlowObject;
import ee.ut.cs.pix.bpmn.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SchaeferLayout implements Layout {
    @Override
    public void apply(Graph graph) throws Exception {
        // constructing maps to reduce time for finding nodes and edges in the original graph below
        HashMap<String, FlowObject> nodeIdToNodeMap = new HashMap<>();
        HashMap<String, ConnectingObject> edgeIdToNodeMap = new HashMap<>();
        // assumption: each element has a unique ID
        graph.getNodes().forEach(n -> nodeIdToNodeMap.put(n.getId(), n));
        graph.getEdges().forEach(e -> edgeIdToNodeMap.put(e.getId(), e));

        GenGraph<String, String> ggraph = new GenGraph<>();
        graph.getNodes()
                .forEach(
                        n ->
                                ggraph.addNode(
                                        n.getId(),
                                        n.getBounds().getWidth(),
                                        n.getBounds().getHeight()));
        graph.getEdges()
                .forEach(
                        e ->
                                ggraph.addEdge(
                                        e.getId(), e.getSource().getId(), e.getTarget().getId()));

        PGraph<String, String> pGraph = ggraph.generatePGraph();
        pGraph.runSimulatedAnnealing(1000);

        LGraphConfig lGraphConfig = new LGraphConfig();
        lGraphConfig.setLayerDistance(100);
        lGraphConfig.setGapBetweenNodes(50);
        LGraph<String, String> lGraph = pGraph.generateLGraph(lGraphConfig);
        lGraph.graphPlacement();

        de.jfschaefer.layeredgraphlayout.layout.Layout<String, String> layout =
                lGraph.getLayout(new LayoutConfig());
        Set<String> nodeSet = layout.getNodeSet();
        nodeSet.forEach(
                id -> {
                    Point point = layout.getNodeTopLeft(id);
                    nodeIdToNodeMap.get(id).getBounds().setX(point.x);
                    nodeIdToNodeMap.get(id).getBounds().setY(point.y);
                });

        Set<String> edgeSet = layout.getEdgeSet();
        edgeSet.forEach(
                id -> {
                    ArrayList<EdgeSegment> segments = layout.getEdgePosition(id);
                    segments.forEach(
                            segment -> {
                                edgeIdToNodeMap
                                        .get(id)
                                        .addWaypoint(segment.getStart().x, segment.getStart().y);
                                edgeIdToNodeMap
                                        .get(id)
                                        .addWaypoint(segment.getEnd().x, segment.getEnd().y);
                            });
                });

        // graph.getEdges()
        //         .forEach(
        //                 e -> {
        //                     ShapeBounds sourceBounds =
        //                             nodeIdToNodeMap.get(e.getSource().getId()).getBounds();
        //                     e.addWaypoint(sourceBounds.getX(), sourceBounds.getY());
        //                     ShapeBounds targetBounds =
        //                             nodeIdToNodeMap.get(e.getTarget().getId()).getBounds();
        //                     e.addWaypoint(targetBounds.getX(), targetBounds.getY());
        //                 });
    }
}
