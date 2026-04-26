import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BestFirstSearch {
    public SearchResult search(Graph graph, String start, String goal, Map<String, Double> heuristics) {
        if (!graph.hasCity(start) || !graph.hasCity(goal)) {
            return SearchResult.failure(Collections.emptyList());
        }

        PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> {
            int byH = Double.compare(a.getH(), b.getH());
            if (byH != 0) {
                return byH;
            }
            return a.getCity().compareToIgnoreCase(b.getCity());
        });
        Set<String> discovered = new HashSet<>();
        Set<String> closed = new HashSet<>();
        List<String> explorationOrder = new ArrayList<>();

        open.add(new Node(start, 0.0, heuristicOf(start, heuristics), null));
        discovered.add(start);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (closed.contains(current.getCity())) {
                continue;
            }

            explorationOrder.add(current.getCity());
            if (current.getCity().equalsIgnoreCase(goal)) {
                List<String> path = reconstructPath(current);
                double cost = graph.computePathCost(path);
                return new SearchResult(true, path, explorationOrder, cost, closed.size(), Collections.emptyList());
            }

            closed.add(current.getCity());
            List<Edge> neighbors = new ArrayList<>(graph.neighborsOf(current.getCity()));
            neighbors.sort((e1, e2) -> {
                int byH = Double.compare(heuristicOf(e1.getTo(), heuristics), heuristicOf(e2.getTo(), heuristics));
                if (byH != 0) {
                    return byH;
                }
                return e1.getTo().compareToIgnoreCase(e2.getTo());
            });

            for (Edge edge : neighbors) {
                String nextCity = edge.getTo();
                if (discovered.contains(nextCity)) {
                    continue;
                }
                discovered.add(nextCity);
                open.add(new Node(nextCity, current.getG() + edge.getCost(), heuristicOf(nextCity, heuristics), current));
            }
        }

        return SearchResult.failure(explorationOrder);
    }

    private static double heuristicOf(String city, Map<String, Double> heuristics) {
        return heuristics.getOrDefault(city, Double.POSITIVE_INFINITY);
    }

    private static List<String> reconstructPath(Node goalNode) {
        List<String> path = new ArrayList<>();
        Node cursor = goalNode;
        while (cursor != null) {
            path.add(cursor.getCity());
            cursor = cursor.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}
