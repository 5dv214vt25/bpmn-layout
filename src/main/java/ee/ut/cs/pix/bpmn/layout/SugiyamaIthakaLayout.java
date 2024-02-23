package ee.ut.cs.pix.bpmn.layout;

import de.odysseus.ithaka.digraph.SimpleDigraph;
import de.odysseus.ithaka.digraph.SimpleDigraphAdapter;
import de.odysseus.ithaka.digraph.layout.DigraphLayout;
import de.odysseus.ithaka.digraph.layout.DigraphLayoutDimension;
import de.odysseus.ithaka.digraph.layout.DigraphLayoutDimensionProvider;
import de.odysseus.ithaka.digraph.layout.DigrpahLayoutBuilder;
import de.odysseus.ithaka.digraph.layout.sugiyama.SugiyamaBuilder;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;
import ee.ut.cs.pix.bpmn.graph.FlowObject;
import ee.ut.cs.pix.bpmn.graph.Graph;

import java.util.HashMap;

public class SugiyamaIthakaLayout implements Layout {

    @Override
    public void apply(Graph graph) {
        SimpleDigraph<String> digraph = new SimpleDigraphAdapter<>();
        graph.getEdges().forEach(e -> digraph.add(e.getSource().getId(), e.getTarget().getId()));

        DigraphLayoutDimensionProvider<String> dimensionProvider =
                node -> new DigraphLayoutDimension(100, 100);
        DigrpahLayoutBuilder<String, Boolean> builder = new SugiyamaBuilder<>(25, 25);
        DigraphLayout<String, Boolean> layout = builder.build(digraph, dimensionProvider);

        // update the original graph

        // constructing maps to reduce time for finding nodes and edges in the original graph below
        HashMap<String, FlowObject> nodeIdToNodeMap = new HashMap<>();
        // assumption: each element has a unique ID
        graph.getNodes().forEach(n -> nodeIdToNodeMap.put(n.getId(), n));

        layout.getLayoutGraph()
                .vertices()
                .forEach(
                        v -> {
                            String id = v.getVertex();
                            int x = v.getPoint().x;
                            int y = v.getPoint().y;
                            nodeIdToNodeMap.get(id).getBounds().setX((double) x);
                            nodeIdToNodeMap.get(id).getBounds().setY((double) y);
                        });

        graph.getEdges()
                .forEach(
                        e -> {
                            ShapeBounds sourceBounds =
                                    nodeIdToNodeMap.get(e.getSource().getId()).getBounds();
                            e.addWaypoint(
                                    sourceBounds.getX() + sourceBounds.getWidth() / 2,
                                    sourceBounds.getY() + sourceBounds.getHeight());
                            ShapeBounds targetBounds =
                                    nodeIdToNodeMap.get(e.getTarget().getId()).getBounds();
                            e.addWaypoint(
                                    targetBounds.getX() + targetBounds.getWidth() / 2,
                                    targetBounds.getY());
                        });
    }
}
