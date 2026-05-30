import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashMap;

public class AStarSearch {
    public SearchResult search(Graph graph, String start, String goal, Map<String, Double> heuristics) {
        if (!graph.hasCity(start) || !graph.hasCity(goal)) {
            return SearchResult.failure(Collections.emptyList());
        }

        PriorityQueue<Node> open = new PriorityQueue<>();
        Map<String, Double> bestG = new HashMap<>();
        Set<String> closed = new LinkedHashSet<>();
        List<String> explorationOrder = new ArrayList<>();
        List<SearchTraceStep> traceSteps = new ArrayList<>();

        open.add(new Node(start, 0.0, heuristicOf(start, heuristics), null));
        bestG.put(start, 0.0);

        int iteration = 0;
        while (!open.isEmpty()) {
            Node current = open.poll();
            double known = bestG.getOrDefault(current.getCity(), Double.POSITIVE_INFINITY);
            if (current.getG() > known || closed.contains(current.getCity())) {
                continue;
            }

            iteration++;
            explorationOrder.add(current.getCity());

            if (current.getCity().equalsIgnoreCase(goal)) {
                traceSteps.add(new SearchTraceStep(
                        iteration,
                        current.getCity(),
                        current.getG(),
                        current.getH(),
                        current.getF(),
                        snapshotOpen(open, bestG, closed),
                        new ArrayList<>(closed)
                ));
                List<String> path = reconstructPath(current);
                return new SearchResult(true, path, explorationOrder, current.getG(), closed.size(), traceSteps);
            }

            closed.add(current.getCity());
            for (Edge edge : graph.neighborsOf(current.getCity())) {
                String nextCity = edge.getTo();
                if (closed.contains(nextCity)) {
                    continue;
                }

                double gNew = current.getG() + edge.getCost();
                double oldG = bestG.getOrDefault(nextCity, Double.POSITIVE_INFINITY);
                if (gNew < oldG) {
                    bestG.put(nextCity, gNew);
                    open.add(new Node(nextCity, gNew, heuristicOf(nextCity, heuristics), current));
                }
            }

            traceSteps.add(new SearchTraceStep(
                    iteration,
                    current.getCity(),
                    current.getG(),
                    current.getH(),
                    current.getF(),
                    snapshotOpen(open, bestG, closed),
                    new ArrayList<>(closed)
            ));
        }

        return SearchResult.failure(explorationOrder);
    }
    //Gets the heuristic of a node
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

    private static List<String> snapshotOpen(PriorityQueue<Node> open, Map<String, Double> bestG, Set<String> closed) {
        List<Node> snapshot = new ArrayList<>();
        for (Node node : open) {
            double known = bestG.getOrDefault(node.getCity(), Double.POSITIVE_INFINITY);
            if (closed.contains(node.getCity())) {
                continue;
            }
            if (node.getG() > known) {
                continue;
            }
            snapshot.add(node);
        }

        Collections.sort(snapshot);
        List<String> formatted = new ArrayList<>();
        for (Node node : snapshot) {
            formatted.add(node.getCity() + "(f=" + Node.formatNumber(node.getF()) + ")");
        }
        return formatted;
    }
}
