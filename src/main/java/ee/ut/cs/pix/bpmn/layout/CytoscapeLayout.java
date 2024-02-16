package ee.ut.cs.pix.bpmn.layout;

import com.google.gson.Gson;

import ee.ut.cs.pix.bpmn.graph.FlowArc;
import ee.ut.cs.pix.bpmn.graph.FlowNode;
import ee.ut.cs.pix.bpmn.graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CytoscapeLayout implements Layout {
    @Override
    public void apply(Graph graph) throws IOException {
        String body = prepareRequestBody(graph);
        CytoscapeResponse response = sendRequest(body);
        for (CytoscapeNode node : response.nodes) {
            graph.getNodes()
                    .forEach(
                            n -> {
                                if (n.id.equals(node.id)) {
                                    n.x = node.x;
                                    n.y = node.y;
                                }
                            });
        }
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
        List<FlowNode> nodes;
        List<FlowArc> edges;

        public CytoscapeRequest(List<FlowNode> nodes, List<FlowArc> edges) {
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
