import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchTraceStep {
    private final int iteration;
    private final String currentCity;
    private final double g;
    private final double h;
    private final double f;
    private final List<String> openList;
    private final List<String> closedList;

    public SearchTraceStep(
            int iteration,
            String currentCity,
            double g,
            double h,
            double f,
            List<String> openList,
            List<String> closedList
    ) {
        this.iteration = iteration;
        this.currentCity = currentCity;
        this.g = g;
        this.h = h;
        this.f = f;
        this.openList = new ArrayList<>(openList);
        this.closedList = new ArrayList<>(closedList);
    }

    public int getIteration() {
        return iteration;
    }

    public String getCurrentCity() {
        return currentCity;
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

    public List<String> getOpenList() {
        return Collections.unmodifiableList(openList);
    }

    public List<String> getClosedList() {
        return Collections.unmodifiableList(closedList);
    }
}
