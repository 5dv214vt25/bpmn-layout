package ee.ut.cs.pix.bpmn.layout;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphModelFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;
import ee.ut.cs.pix.bpmn.graph.ConnectingObject;
import ee.ut.cs.pix.bpmn.graph.FlowObject;
import ee.ut.cs.pix.bpmn.graph.Graph;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import java.awt.geom.Rectangle2D;
import java.util.*;

public class JGraphLayout implements Layout {
    @Override
    public void apply(Graph graph) throws Exception {
        // constructing maps to reduce time for finding nodes and edges in the original graph below
        HashMap<String, FlowObject> nodeIdToNodeMap = new HashMap<>();
        // assumption: each element has a unique ID
        graph.getNodes().forEach(n -> nodeIdToNodeMap.put(n.getId(), n));

        GraphModel model = new DefaultGraphModel();
        List<DefaultGraphCell> elements = new ArrayList<>();
        // ConnectionSet connections = new ConnectionSet();

        graph.getEdges()
                .forEach(
                        e -> {
                            DefaultGraphCell source = new DefaultGraphCell(e.getSource().getId());
                            source.addPort();
                            AttributeMap sourceAttributes = source.getAttributes();
                            GraphConstants.setBounds(
                                    sourceAttributes, new Rectangle2D.Double(1, 1, 100, 100));

                            DefaultGraphCell target = new DefaultGraphCell(e.getTarget().getId());
                            target.addPort();
                            AttributeMap targetAttributes = source.getAttributes();
                            GraphConstants.setBounds(
                                    targetAttributes, new Rectangle2D.Double(1, 1, 100, 100));

                            DefaultEdge edge = new DefaultEdge();
                            edge.setSource(source.getChildAt(0));
                            edge.setTarget(target.getChildAt(0));

                            // connections.connect(edge, source, target);

                            elements.add(source);
                            elements.add(target);
                            elements.add(edge);
                        });
        model.beginUpdate();
        model.insert(elements.toArray(), null, null, null, null);
        model.endUpdate();

        JGraph jGraph = new JGraph(model);

        JGraphFacade facade = new JGraphModelFacade(model, new Object[] {elements.get(0)});
        JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
        layout.setDeterministic(true);
        layout.setCompactLayout(true);
        layout.setFineTuning(true);
        layout.setParallelEdgeSpacing(15);
        layout.setFixRoots(true);
        layout.setOrientation(1);
        layout.setInterRankCellSpacing(25);
        layout.run(facade);
        Map<?, ?> nested = facade.createNestedMap(true, true);
        // model.edit(nested, null, null, null);
        jGraph.getGraphLayoutCache().edit(nested);

        Arrays.stream(jGraph.getGraphLayoutCache().getCellViews())
                .forEach(
                        r -> {
                            if (r instanceof VertexView) {
                                String id =
                                        ((String) ((DefaultGraphCell) r.getCell()).getUserObject());
                                double x = r.getBounds().getX();
                                double y = r.getBounds().getY();
                                nodeIdToNodeMap.get(id).getBounds().setX(x);
                                nodeIdToNodeMap.get(id).getBounds().setY(y);
                            }
                        });

        graph.getEdges()
                .forEach(
                        e -> {
                            ShapeBounds sourceBounds =
                                    nodeIdToNodeMap.get(e.getSource().getId()).getBounds();
                            e.addWaypoint(sourceBounds.getX(), sourceBounds.getY());
                            ShapeBounds targetBounds =
                                    nodeIdToNodeMap.get(e.getTarget().getId()).getBounds();
                            e.addWaypoint(targetBounds.getX(), targetBounds.getY());
                        });
    }
}
