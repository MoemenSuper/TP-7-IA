import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class GraphAnalysis {
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

    public static Map<String, Double> shortestCostsToGoal(Graph graph, String goal) {
        Map<String, Double> distance = new HashMap<>();
        PriorityQueue<CostNode> open = new PriorityQueue<>();

        distance.put(goal, 0.0);
        open.add(new CostNode(goal, 0.0));

        while (!open.isEmpty()) {
            CostNode current = open.poll();
            double known = distance.getOrDefault(current.city, Double.POSITIVE_INFINITY);
            if (current.cost > known) {
                continue;
            }

            for (Edge edge : graph.neighborsOf(current.city)) {
                double nextCost = current.cost + edge.getCost();
                double old = distance.getOrDefault(edge.getTo(), Double.POSITIVE_INFINITY);
                if (nextCost < old) {
                    distance.put(edge.getTo(), nextCost);
                    open.add(new CostNode(edge.getTo(), nextCost));
                }
            }
        }

        return distance;
    }

    public static Map<String, Boolean> admissibilityTable(
            Graph graph,
            String goal,
            Map<String, Double> heuristics,
            List<String> orderedCities
    ) {
        Map<String, Double> realCosts = shortestCostsToGoal(graph, goal);
        Map<String, Boolean> result = new LinkedHashMap<>();

        for (String city : orderedCities) {
            double h = heuristics.getOrDefault(city, Double.POSITIVE_INFINITY);
            double real = realCosts.getOrDefault(city, Double.POSITIVE_INFINITY);
            result.put(city, h <= real);
        }
        return result;
    }

    public static boolean isConsistent(Graph graph, Map<String, Double> heuristics) {
        for (String city : graph.getCities()) {
            double hCity = heuristics.getOrDefault(city, Double.POSITIVE_INFINITY);
            for (Edge edge : graph.neighborsOf(city)) {
                double hNext = heuristics.getOrDefault(edge.getTo(), Double.POSITIVE_INFINITY);
                if (hCity > edge.getCost() + hNext + 0.000001) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<String> consistencyViolations(Graph graph, Map<String, Double> heuristics) {
        List<String> violations = new ArrayList<>();
        for (String city : graph.getCities()) {
            double hCity = heuristics.getOrDefault(city, Double.POSITIVE_INFINITY);
            for (Edge edge : graph.neighborsOf(city)) {
                double hNext = heuristics.getOrDefault(edge.getTo(), Double.POSITIVE_INFINITY);
                if (hCity > edge.getCost() + hNext + 0.000001) {
                    violations.add(
                            city
                                    + " -> "
                                    + edge.getTo()
                                    + " : "
                                    + Node.formatNumber(hCity)
                                    + " > "
                                    + Node.formatNumber(edge.getCost())
                                    + " + "
                                    + Node.formatNumber(hNext)
                    );
                }
            }
        }
        Collections.sort(violations);
        return violations;
    }
}
