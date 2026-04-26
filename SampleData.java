import java.util.LinkedHashMap;
import java.util.Map;

public class SampleData {
    public static Graph baseGraph() {
        Graph graph = new Graph();
        graph.addUndirectedEdge("Tunis", "Sousse", 140);
        graph.addUndirectedEdge("Tunis", "Kairouan", 160);
        graph.addUndirectedEdge("Sousse", "Kairouan", 90);
        graph.addUndirectedEdge("Sousse", "Sfax", 130);
        graph.addUndirectedEdge("Kairouan", "Gafsa", 200);
        graph.addUndirectedEdge("Sfax", "Gafsa", 150);
        graph.addUndirectedEdge("Gafsa", "Tozeur", 90);
        return graph;
    }

    public static Graph extendedGraph() {
        Graph graph = baseGraph();
        graph.addUndirectedEdge("Sfax", "Gabes", 75);
        graph.addUndirectedEdge("Gabes", "Gafsa", 130);
        graph.addUndirectedEdge("Tunis", "ElKef", 170);
        graph.addUndirectedEdge("ElKef", "Kairouan", 110);
        return graph;
    }

    public static Map<String, Double> baseHeuristics() {
        Map<String, Double> h = new LinkedHashMap<>();
        h.put("Tunis", 400.0);
        h.put("Sousse", 300.0);
        h.put("Kairouan", 250.0);
        h.put("Sfax", 230.0);
        h.put("Gafsa", 100.0);
        h.put("Tozeur", 0.0);
        return h;
    }

    public static Map<String, Double> modifiedGafsaHeuristics() {
        Map<String, Double> h = new LinkedHashMap<>(baseHeuristics());
        h.put("Gafsa", 500.0);
        return h;
    }

    public static Map<String, Double> kairouanLowHeuristics() {
        Map<String, Double> h = new LinkedHashMap<>(baseHeuristics());
        h.put("Kairouan", 50.0);
        return h;
    }

    public static Map<String, Double> extendedHeuristics() {
        Map<String, Double> h = new LinkedHashMap<>(baseHeuristics());
        h.put("Gabes", 180.0);
        h.put("ElKef", 320.0);
        return h;
    }
}
