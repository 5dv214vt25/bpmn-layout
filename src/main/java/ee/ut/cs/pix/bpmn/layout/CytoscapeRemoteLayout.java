package ee.ut.cs.pix.bpmn.layout;

import com.google.gson.Gson;

import ee.ut.cs.pix.bpmn.di.ShapeBounds;
import ee.ut.cs.pix.bpmn.graph.ConnectingObject;
import ee.ut.cs.pix.bpmn.graph.FlowObject;
import ee.ut.cs.pix.bpmn.graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class CytoscapeRemoteLayout implements Layout {
    @Override
    public void apply(Graph graph) throws Exception {
        // constructing maps to reduce time for finding nodes and edges in the original graph below
        HashMap<String, FlowObject> nodeIdToNodeMap = new HashMap<>();
        // assumption: each element has a unique ID
        graph.getNodes().forEach(n -> nodeIdToNodeMap.put(n.getId(), n));

        String body = prepareRequestBody(graph);
        CytoscapeResponse response = sendRequest(body);
        for (CytoscapeNode node : response.nodes) {
            FlowObject n = nodeIdToNodeMap.get(node.id);
            n.getBounds().setX(node.x);
            n.getBounds().setY(node.y);
        }

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

    private static String prepareRequestBody(Graph graph) {
        Gson gson = new Gson();
        CytoscapeRequest request = new CytoscapeRequest(graph.getNodes(), graph.getEdges());
        return gson.toJson(request);
    }

    private static CytoscapeResponse sendRequest(String body) throws IOException {
        URL url = new URL("http://localhost:8080");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.getOutputStream().write(body.getBytes());
        con.getOutputStream().flush();
        con.getOutputStream().close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        return gson.fromJson(response.toString(), CytoscapeResponse.class);
    }

    static class CytoscapeRequest {
        List<FlowObject> nodes;
        List<ConnectingObject> edges;

        public CytoscapeRequest(List<FlowObject> nodes, List<ConnectingObject> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    static class CytoscapeResponse {
        List<CytoscapeNode> nodes;
        List<CytoscapeEdge> edges;
    }

    static class CytoscapeNode {
        String id;
        String name;
        double x;
        double y;
    }

    static class CytoscapeEdge {
        String id;
        String name;
        String source;
        String target;
    }
}
