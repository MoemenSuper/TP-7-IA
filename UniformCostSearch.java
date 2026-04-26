import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class UniformCostSearch {
    private static class CostNode implements Comparable<CostNode> {
        private final String city;
        private final double cost;

        private CostNode(String city, double cost) {
            this.city = city;
            this.cost = cost;
        }

        @Override
        public int compareTo(CostNode other) {
            int byCost = Double.compare(this.cost, other.cost);
            if (byCost != 0) {
                return byCost;
            }
            return this.city.compareToIgnoreCase(other.city);
        }
    }

    public SearchResult search(Graph graph, String start, String goal) {
        if (!graph.hasCity(start) || !graph.hasCity(goal)) {
            return SearchResult.failure(Collections.emptyList());
        }

        PriorityQueue<CostNode> open = new PriorityQueue<>();
        Map<String, Double> bestCost = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> closed = new HashSet<>();
        List<String> explorationOrder = new ArrayList<>();

        bestCost.put(start, 0.0);
        open.add(new CostNode(start, 0.0));

        while (!open.isEmpty()) {
            CostNode current = open.poll();
            double known = bestCost.getOrDefault(current.city, Double.POSITIVE_INFINITY);
            if (current.cost > known || closed.contains(current.city)) {
                continue;
            }

            explorationOrder.add(current.city);
            if (current.city.equalsIgnoreCase(goal)) {
                List<String> path = reconstructPath(goal, parent);
                return new SearchResult(true, path, explorationOrder, current.cost, closed.size(), Collections.emptyList());
            }

            closed.add(current.city);
            for (Edge edge : graph.neighborsOf(current.city)) {
                if (closed.contains(edge.getTo())) {
                    continue;
                }

                double tentativeCost = current.cost + edge.getCost();
                double oldCost = bestCost.getOrDefault(edge.getTo(), Double.POSITIVE_INFINITY);
                if (tentativeCost < oldCost) {
                    bestCost.put(edge.getTo(), tentativeCost);
                    parent.put(edge.getTo(), current.city);
                    open.add(new CostNode(edge.getTo(), tentativeCost));
                }
            }
        }

        return SearchResult.failure(explorationOrder);
    }

    private static List<String> reconstructPath(String goal, Map<String, String> parent) {
        List<String> reversed = new ArrayList<>();
        String cursor = goal;
        while (cursor != null) {
            reversed.add(cursor);
            cursor = parent.get(cursor);
        }
        Collections.reverse(reversed);
        return reversed;
    }
}
