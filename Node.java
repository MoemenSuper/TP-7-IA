import java.util.Objects;

public class Node implements Comparable<Node> {
    private final String city;
    private final double g;
    private final double h;
    private final double f;
    private final Node parent;

    public Node(String city, double g, double h, Node parent) {
        this.city = city;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
    }

    public String getCity() {
        return city;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public double getF() {
        return f;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public int compareTo(Node other) {
        int byF = Double.compare(this.f, other.f);
        if (byF != 0) {
            return byF;
        }

        int byH = Double.compare(this.h, other.h);
        if (byH != 0) {
            return byH;
        }

        return this.city.compareToIgnoreCase(other.city);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node other = (Node) obj;
        return Objects.equals(this.city, other.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }

    @Override
    public String toString() {
        return city + "(f=" + formatNumber(f) + ")";
    }

    public String fullState() {
        return city
                + " | g=" + formatNumber(g)
                + " h=" + formatNumber(h)
                + " f=" + formatNumber(f);
    }

    public static String formatNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.000001) {
            return Integer.toString((int) Math.rint(value));
        }
        return String.format(java.util.Locale.US, "%.2f", value);
    }
}
