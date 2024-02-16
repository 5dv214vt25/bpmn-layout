package ee.ut.cs.pix.bpmn.layout;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class DagreGraph {
    static String DEFAULT_EDGE_NAME = "\u0000";
    static String GRAPH_NODE = "\u0000";
    static String EDGE_KEY_DELIM = "\u0001";
    private final boolean isDirected;
    private final boolean isMultigraph;
    private final boolean isCompound;
    private final Map<String, Object> nodes;
    private final Map<String, Object> edgeLabels;
    private final Map<String, EdgeObj> edgeObjs;
    private final Map<String, Map<String, Integer>> preds;
    private final Map<String, Map<String, Integer>> sucs;
    private final Map<String, Map<String, EdgeObj>> in;
    private final Map<String, Map<String, EdgeObj>> out;
    private Map<String, String> parent = null;
    private Map<String, Map<String, Object>> children = null;
    private String label;
    private int nodeCount;
    private int edgeCount;
    private Function<Void, String> defaultNodeLabelFn = null;
    private Function<EdgeObj, String> defaultEdgeLabelFn = null;

    public DagreGraph(Options opts) {
        this.isDirected = opts.isDirected;
        this.isMultigraph = opts.isMultigraph;
        this.isCompound = opts.isCompound;

        nodes = new HashMap<>();
        edgeLabels = new HashMap<>();
        edgeObjs = new HashMap<>();
        preds = new HashMap<>();
        sucs = new HashMap<>();
        in = new HashMap<>();
        out = new HashMap<>();

        this.nodeCount = 0;
        this.edgeCount = 0;

        if (isCompound) {
            parent = new HashMap<>();
            children = new HashMap<>();
            children.put(GRAPH_NODE, new HashMap<>());
        }
    }

    private static EdgeObj edgeArgsToObj(boolean isDirected, String v, String w, String name) {
        if (!isDirected && v.compareTo(w) > 0) {
            String tmp = v;
            v = w;
            w = tmp;
        }
        return new EdgeObj(v, w, name);
    }

    public boolean isMultigraph() {
        return isMultigraph;
    }

    public boolean isCompound() {
        return isCompound;
    }

    public DagreGraph setDefaultNodeLabel(Function<Void, String> newDefault) {
        if (newDefault != null) {
            defaultNodeLabelFn = newDefault;
        }
        return this;
    }

    public int nodeCount() {
        return nodeCount;
    }

    /** Gets list of nodes without in-edges. Complexity: O(|V|). */
    public List<String> sources() {
        return nodes().stream()
                .filter(v -> in.get(v).keySet().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Gets all nodes of the graph. Note, the in case of compound graph subnodes are not included in
     * list. Complexity: O(1).
     */
    public Set<String> nodes() {
        return nodes.keySet();
    }

    /** Gets list of nodes without out-edges. Complexity: O(|V|). */
    public List<String> sinks() {
        return nodes().stream()
                .filter(v -> out.get(v).keySet().isEmpty())
                .collect(Collectors.toList());
    }

    /** Invokes setNode method for each node in names list. Complexity: O(|names|). */
    public DagreGraph setNodes(List<String> vs, Object value) {
        vs.forEach(v -> setNode(v, value));
        return this;
    }

    public DagreGraph setNode(String v, Object value) {
        if (nodes.containsKey(v)) {
            if (value != null) nodes.put(v, value);
            return this;
        }
        nodes.put(v, value != null ? value : defaultNodeLabelFn.apply(null));
        if (isCompound) {
            parent.put(v, GRAPH_NODE);
            children.put(v, new HashMap<>());
            children.get(GRAPH_NODE).put(v, true);
        }
        in.put(v, new HashMap<>());
        out.put(v, new HashMap<>());
        preds.put(v, new HashMap<>());
        sucs.put(v, new HashMap<>());
        nodeCount++;
        return this;
    }

    /** Gets the label of node with specified name. Complexity: O(|V|). */
    public Object node(String v) {
        return nodes.get(v);
    }

    /**
     * Remove the node with the name from the graph or do nothing if the node is not in the graph.
     * If the node was removed this function also removes any incident edges. Complexity: O(1).
     */
    public DagreGraph removeNode(String v) {
        if (nodes.containsKey(v)) {
            nodes.remove(v);
            if (isCompound) {
                removeFromParentsChildList(v);
                parent.remove(v);
                children(v).forEach(child -> setParent(child, null));
                children.remove(v);
            }
            in.get(v).keySet().forEach(e -> removeEdge(edgeObjs.get(e)));
            in.remove(v);
            preds.remove(v);
            out.get(v).keySet().forEach(e -> removeEdge(edgeObjs.get(e)));
            out.remove(v);
            sucs.remove(v);
            nodeCount--;
        }
        return this;
    }

    private void removeFromParentsChildList(String v) {
        children.get(parent.get(v)).remove(v);
    }

    /**
     * Sets node p as a parent for node v if it is defined, or removes the parent for v if p is
     * undefined. Method throws an exception in case of invoking it in context of noncompound graph.
     * Average-case complexity: O(1).
     */
    private DagreGraph setParent(String v, String parent) {
        if (!isCompound) {
            throw new RuntimeException("Cannot set parent in a non-compound graph");
        }

        if (parent == null) {
            parent = GRAPH_NODE;
        } else {
            // Coerce parent to string
            parent += "";
            for (String ancestor = parent; ancestor != null; ancestor = this.parent(ancestor)) {
                if (ancestor.equals(v)) {
                    throw new RuntimeException(
                            "Setting " + parent + " as parent of " + v + " would create a cycle");
                }
            }
            this.setNode(parent, null);
        }
        this.setNode(v, null);
        this.removeFromParentsChildList(v);
        this.parent.put(v, parent);
        this.children.get(parent).put(v, true);
        return this;
    }

    /** Gets parent node for node v. Complexity: O(1). */
    public String parent(String v) {
        if (isCompound) {
            String parent = this.parent.get(v);
            if (!parent.equals(GRAPH_NODE)) {
                return parent;
            }
        }
        return null;
    }

    /** Gets list of direct children of node v. Complexity: O(1). */
    public List<String> children(String v) {
        if (v == null) v = GRAPH_NODE;
        if (isCompound) {
            Map<String, Object> children = this.children.get(v);
            if (children != null) {
                return new ArrayList<>(children.keySet());
            }
        } else if (v.equals(GRAPH_NODE)) {
            return new ArrayList<>(nodes());
        } else if (this.hasNode(v)) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    /** Detects whether graph has a node with specified name or not. */
    public boolean hasNode(String v) {
        return nodes.containsKey(v);
    }

    public boolean isLeaf(String v) {
        List<String> neighbors;
        if (this.isDirected()) {
            neighbors = this.successors(v);
        } else {
            neighbors = this.neighbors(v);
        }
        return neighbors.isEmpty();
    }

    public boolean isDirected() {
        return isDirected;
    }

    /**
     * Return all nodes that are successors of the specified node or undefined if node v is not in
     * the graph. Behavior is undefined for undirected graphs - use neighbors instead. Complexity:
     * O(|V|).
     */
    public List<String> successors(String v) {
        Map<String, Integer> sucsV = this.sucs.get(v);
        if (sucsV != null) {
            return new ArrayList<>(sucsV.keySet());
        }
        return new ArrayList<>();
    }

    /**
     * Return all nodes that are predecessors or successors of the specified node or undefined if
     * node v is not in the graph. Complexity: O(|V|).
     */
    public List<String> neighbors(String v) {
        List<String> preds = this.predecessors(v);
        if (preds != null && !preds.isEmpty()) {
            Set<String> union = new HashSet<>(preds);
            union.addAll(this.successors(v));
            return new ArrayList<>(union);
        }
        return new ArrayList<>();
    }

    /**
     * Return all nodes that are predecessors of the specified node or undefined if node v is not in
     * the graph. Behavior is undefined for undirected graphs - use neighbors instead. Complexity:
     * O(|V|).
     */
    public List<String> predecessors(String v) {
        Map<String, Integer> predsV = this.preds.get(v);
        if (predsV != null) {
            return new ArrayList<>(predsV.keySet());
        }
        return new ArrayList<>();
    }

    /**
     * Creates new graph with nodes filtered via filter. Edges incident to rejected node are also
     * removed. In case of compound graph, if parent is rejected by filter, than all its children
     * are rejected too. Average-case complexity: O(|E|+|V|).
     */
    public DagreGraph filterNodes(Function<Object, Boolean> filter) {
        DagreGraph copy = new DagreGraph(new Options(isDirected, isMultigraph, isCompound));
        copy.setGraph(graph());

        nodes.forEach(
                (v, value) -> {
                    if (filter.apply(v)) copy.setNode(v, value);
                });

        edgeObjs.values()
                .forEach(
                        e -> {
                            if (copy.hasNode(e.v) && copy.hasNode(e.w)) {
                                try {
                                    copy.setEdge(e, edge(e));
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });

        HashMap<String, String> parents = new HashMap<>();
        if (isCompound) {
            copy.nodes().forEach(v -> copy.setParent(v, findParent(v, copy, parents)));
        }

        return copy;
    }

    private String findParent(String v, DagreGraph graph, HashMap<String, String> parents) {
        String parent = parent(v);
        if (parent == null || graph.hasNode(parent)) {
            parents.put(v, parent);
            return parent;
        } else if (parents.containsKey(parent)) {
            return parents.get(parent);
        }
        return findParent(parent, graph, parents);
    }

    public DagreGraph setGraph(String label) { // TODO: check why this is called setGraph
        label = label;
        return this;
    }

    public String graph() {
        return label;
    }

    /**
     * Sets the default edge label or factory function. This label will be assigned as default label
     * in case if no label was specified while setting an edge or this function will be invoked each
     * time when setting an edge with no label specified and returned value * will be used as a
     * label for edge. Complexity: O(1).
     */
    public DagreGraph setDefaultEdgeLabel(Function<EdgeObj, String> newDefault) {
        defaultEdgeLabelFn = newDefault;
        return this;
    }

    /** Gets the label for the specified edge. Complexity: O(1). */
    public Object edge(String v, String w, String name) {
        String e = edgeArgsToId(isDirected, v, w, name);
        return edgeLabels.get(e);
    }

    /** Gets the label for the specified edge. Complexity: O(1). */
    public Object edge(EdgeObj v) {
        String e = edgeObjToId(isDirected, v);
        return edgeLabels.get(e);
    }

    /** Gets the label for the specified edge and converts it to an object. Complexity: O(1) */
    public EdgeObj edgeAsObj(String v, String w, String name) {
        String e = edgeArgsToId(isDirected, v, w, name);
        Object edge = edgeLabels.get(e);
        if (edge instanceof EdgeObj) {
            return (EdgeObj) edge;
        }
        return new EdgeObj(v, w, name);
    }

    /**
     * Creates or updates the label for the edge (v, w) with the optionally supplied name. If label
     * is supplied it is set as the value for the edge. If label is not supplied and the edge was
     * created by this call then the default edge label will be assigned. The name parameter is only
     * useful with multigraphs.
     */
    public DagreGraph setEdge(String v, String w, Object value, String name) throws Exception {
        boolean valueSpecified = value != null;

        String e = edgeArgsToId(isDirected, v, w, name);
        if (edgeLabels.containsKey(e)) {
            if (valueSpecified) edgeLabels.put(e, value);
            return this;
        }

        if (name != null && !isMultigraph)
            throw new Exception("Cannot set a named edge when isMultigraph = false");

        // It didn't exist, so we need to create it.
        // First ensure the nodes exist.
        setNode(v, null);
        setNode(w, null);

        edgeLabels.put(
                e, valueSpecified ? value : defaultEdgeLabelFn.apply(new EdgeObj(v, w, name)));

        EdgeObj edgeObj = edgeArgsToObj(isDirected, v, w, name);
        // Ensure we add undirected edges in a consistent way.
        v = edgeObj.v;
        w = edgeObj.w;

        edgeObjs.put(e, edgeObj);
        incrementOrInitEntry(preds.get(w), v);
        incrementOrInitEntry(sucs.get(v), w);
        in.get(w).put(e, edgeObj);
        out.get(v).put(e, edgeObj);
        edgeCount++;
        return this;
    }

    public DagreGraph setEdge(EdgeObj obj, Object value) throws Exception {
        return setEdge(obj.v, obj.w, value, obj.name);
    }

    /**
     * Detects whether the graph contains specified edge or not. No subgraphs are considered.
     * Complexity: O(1).
     */
    public boolean hasEdge(String v, String w, String name) {
        String e = edgeArgsToId(isDirected, v, w, name);
        return edgeLabels.containsKey(e);
    }

    public boolean hasEdge(EdgeObj obj) {
        String e = edgeObjToId(isDirected, obj);
        return edgeLabels.containsKey(e);
    }

    /** Removes the specified edge from the graph. No subgraphs are considered. Complexity: O(1). */
    public DagreGraph removeEdge(String v, String w, String name) {
        String e = edgeArgsToId(isDirected, v, w, name);
        EdgeObj edge = edgeObjs.get(e);
        if (edge != null) {
            v = edge.v;
            w = edge.w;
            edgeLabels.remove(e);
            edgeObjs.remove(e);
            decrementOrRemoveEntry(preds.get(w), v);
            decrementOrRemoveEntry(sucs.get(w), w);
            in.get(w).remove(e);
            out.get(v).remove(e);
            edgeCount--;
        }
        return this;
    }

    public DagreGraph removeEdge(EdgeObj v) {
        String e = edgeObjToId(isDirected, v);
        EdgeObj edge = edgeObjs.get(e);
        if (edge != null) {
            String v_ = edge.v;
            String w_ = edge.w;
            edgeLabels.remove(e);
            edgeObjs.remove(e);
            decrementOrRemoveEntry(preds.get(w_), v_);
            decrementOrRemoveEntry(sucs.get(w_), w_);
            in.get(w_).remove(e);
            out.get(v_).remove(e);
            edgeCount--;
        }
        return this;
    }

    /**
     * Return all edges that point to the node v. Optionally filters those edges down to just those
     * coming from node u. Behavior is undefined for undirected graphs - use nodeEdges instead.
     * Complexity: O(|E|).
     */
    public Collection<EdgeObj> inEdges(String v, String u) {
        Map<String, EdgeObj> inV = in.get(v);

        if (inV != null) {
            Collection<EdgeObj> edges = inV.values();
            if (u == null) {
                return edges;
            }
            return edges.stream().filter(edge -> edge.v.equals(u)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * Return all edges that are pointed at by node v. Optionally filters those edges down to just
     * those point to w. Behavior is undefined for undirected graphs - use nodeEdges instead.
     * Complexity: O(|E|).
     */
    public Collection<EdgeObj> outEdges(String v, String w) {
        Map<String, EdgeObj> outV = out.get(v);

        if (outV != null) {
            Collection<EdgeObj> edges = outV.values();
            if (w == null) {
                return edges;
            }
            return edges.stream().filter(edge -> edge.w.equals(w)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /** Gets the number of edges in the graph. Complexity: O(1). */
    public int edgeCount() {
        return edgeCount;
    }

    /**
     * Gets edges of the graph. In case of compound graph subgraphs are not considered. Complexity:
     * O(|E|).
     */
    public Collection<EdgeObj> edges() {
        return edgeObjs.values();
    }

    /**
     * Establish an edges path over the nodes in nodes list. If some edge is already exists, it will
     * update its label, otherwise it will create an edge between pair of nodes with label provided
     * or default label if no label provided. Complexity: O(|nodes|).
     */
    public DagreGraph setPath(List<String> vs, Object value) throws Exception {
        for (int i = 0; i < vs.size() - 1; i++) {
            String v = vs.get(i);
            String w = vs.get(i + 1);
            setEdge(v, w, value, null);
        }
        return this;
    }

    /**
     * Returns all edges to or from node v regardless of direction. Optionally filters those edges
     * down to just those between nodes v and w regardless of direction. Complexity: O(|E|).
     */
    public Collection<EdgeObj> nodeEdges(String v, String w) {
        Collection<EdgeObj> inEdges = inEdges(v, w);
        if (inEdges != null) {
            Collection<EdgeObj> outEdges = outEdges(v, w);
            if (outEdges != null) {
                inEdges.addAll(outEdges);
            }
            return inEdges;
        }
        return new ArrayList<>();
    }

    private static void incrementOrInitEntry(Map<String, Integer> map, String key) {
        Integer value = map.get(key);
        if (value != null && value > 0) {
            map.put(key, value + 1);
        } else {
            map.put(key, 1);
        }
    }

    private static void decrementOrRemoveEntry(Map<String, Integer> map, String key) {
        Integer value = map.get(key);
        value--;
        map.put(key, value);
        if (value == 0) map.remove(key);
    }

    private static String edgeObjToId(boolean isDirected, EdgeObj edgeObj) {
        return edgeArgsToId(isDirected, edgeObj.v, edgeObj.w, edgeObj.name);
    }

    private static String edgeArgsToId(boolean isDirected, String v, String w, String name) {
        if (!isDirected && v.compareTo(w) > 0) {
            String tmp = v;
            v = w;
            w = tmp;
        }
        return v + EDGE_KEY_DELIM + w + EDGE_KEY_DELIM + (name == null ? DEFAULT_EDGE_NAME : name);
    }

    static class EdgeObj {
        String v;
        String w;
        String name;

        public EdgeObj(String v, String w) {
            this.v = v;
            this.w = w;
        }

        public EdgeObj(String v, String w, String name) {
            this.v = v;
            this.w = w;
            this.name = name;
        }
    }

    static class Options {
        public boolean isDirected;
        public boolean isMultigraph;
        public boolean isCompound;

        public Options(boolean isDirected, boolean isMultigraph, boolean isCompound) {
            this.isDirected = isDirected;
            this.isMultigraph = isMultigraph;
            this.isCompound = isCompound;
        }
    }
}
