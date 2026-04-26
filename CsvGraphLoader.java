import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvGraphLoader {
    public Graph loadGraph(Path edgeCsvPath, boolean undirected) throws IOException {
        Graph graph = new Graph();
        List<String> lines = Files.readAllLines(edgeCsvPath);

        for (int i = 0; i < lines.size(); i++) {
            String raw = lines.get(i).trim();
            if (raw.isEmpty() || raw.startsWith("#")) {
                continue;
            }
            if (i == 0 && raw.toLowerCase().startsWith("from,")) {
                continue;
            }

            String[] parts = raw.split(",");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Ligne invalide dans " + edgeCsvPath + " : " + raw);
            }

            String from = parts[0].trim();
            String to = parts[1].trim();
            double cost = Double.parseDouble(parts[2].trim());

            if (undirected) {
                graph.addUndirectedEdge(from, to, cost);
            } else {
                graph.addDirectedEdge(from, to, cost);
            }
        }

        return graph;
    }

    public Map<String, Double> loadHeuristics(Path heuristicCsvPath) throws IOException {
        Map<String, Double> heuristics = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(heuristicCsvPath);

        for (int i = 0; i < lines.size(); i++) {
            String raw = lines.get(i).trim();
            if (raw.isEmpty() || raw.startsWith("#")) {
                continue;
            }
            if (i == 0 && raw.toLowerCase().startsWith("city,")) {
                continue;
            }

            String[] parts = raw.split(",");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Ligne invalide dans " + heuristicCsvPath + " : " + raw);
            }

            heuristics.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
        }

        return heuristics;
    }
}
