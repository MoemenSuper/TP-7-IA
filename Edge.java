public class Edge {
    private final String from;
    private final String to;
    private final double cost;

    public Edge(String from, String to, double cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return from + " -> " + to + " (" + cost + ")";
    }
}
