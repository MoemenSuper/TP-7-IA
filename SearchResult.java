import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResult {
    private final boolean success;
    private final List<String> path;
    private final List<String> explorationOrder;
    private final double pathCost;
    private final int developedNodes;
    private final List<SearchTraceStep> traceSteps;

    public SearchResult(
            boolean success,
            List<String> path,
            List<String> explorationOrder,
            double pathCost,
            int developedNodes,
            List<SearchTraceStep> traceSteps
    ) {
        this.success = success;
        this.path = new ArrayList<>(path);
        this.explorationOrder = new ArrayList<>(explorationOrder);
        this.pathCost = pathCost;
        this.developedNodes = developedNodes;
        this.traceSteps = new ArrayList<>(traceSteps);
    }

    public static SearchResult failure(List<String> explorationOrder) {
        return new SearchResult(
                false,
                Collections.emptyList(),
                explorationOrder,
                Double.POSITIVE_INFINITY,
                explorationOrder.size(),
                Collections.emptyList()
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getPath() {
        return Collections.unmodifiableList(path);
    }

    public List<String> getExplorationOrder() {
        return Collections.unmodifiableList(explorationOrder);
    }

    public double getPathCost() {
        return pathCost;
    }

    public int getDevelopedNodes() {
        return developedNodes;
    }

    public List<SearchTraceStep> getTraceSteps() {
        return Collections.unmodifiableList(traceSteps);
    }

    public String pathAsString() {
        if (path.isEmpty()) {
            return "(aucun chemin)";
        }
        return String.join(" -> ", path);
    }

    public String explorationAsString() {
        if (explorationOrder.isEmpty()) {
            return "(aucune exploration)";
        }
        return String.join(" -> ", explorationOrder);
    }
}
