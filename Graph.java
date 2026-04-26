import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private final Map<String, List<Edge>> adjacency = new HashMap<>();

    public void addCity(String city) {
        adjacency.computeIfAbsent(city, key -> new ArrayList<>());
    }

    public void addDirectedEdge(String from, String to, double cost) {
        addCity(from);
        addCity(to);
        adjacency.get(from).add(new Edge(from, to, cost));
    }

    public void addUndirectedEdge(String cityA, String cityB, double cost) {
        addDirectedEdge(cityA, cityB, cost);
        addDirectedEdge(cityB, cityA, cost);
    }

    public List<Edge> neighborsOf(String city) {
        List<Edge> edges = adjacency.get(city);
        if (edges == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(edges);
    }

    public Set<String> getCities() {
        return new HashSet<>(adjacency.keySet());
    }

    public boolean hasCity(String city) {
        return adjacency.containsKey(city);
    }

    public double getCost(String from, String to) {
        for (Edge edge : neighborsOf(from)) {
            if (edge.getTo().equalsIgnoreCase(to)) {
                return edge.getCost();
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    public double computePathCost(List<String> path) {
        if (path == null || path.size() < 2) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            double edgeCost = getCost(path.get(i), path.get(i + 1));
            if (Double.isInfinite(edgeCost)) {
                return Double.POSITIVE_INFINITY;
            }
            total += edgeCost;
        }
        return total;
    }
}
