import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TP7App {
    private static final String DEFAULT_START = "Tunis";
    private static final String DEFAULT_GOAL = "Tozeur";

    private Graph graph;
    private Map<String, Double> heuristics;

    public static void main(String[] args) {
        TP7App app = new TP7App();
        app.start();
    }

    private void start() {
        loadData();
        Scanner scanner = new Scanner(System.in);
        runMainMenu(scanner);
        scanner.close();
    }

    private void loadData() {
        try {
            CsvGraphLoader loader = new CsvGraphLoader();
            this.graph = loader.loadGraph(Paths.get("tp7_edges.csv"), true);
            this.heuristics = loader.loadHeuristics(Paths.get("tp7_heuristics.csv"));
            System.out.println("Donnees chargees depuis CSV");
        } catch (Exception e) {
            this.graph = SampleData.baseGraph();
            this.heuristics = SampleData.baseHeuristics();
            System.out.println("Utilisation des donnees par defaut");
        }
    }

    private void runMainMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n==== Menu TP7 ====");
            System.out.println("1. Lancer A*");
            System.out.println("2. Comparer UCS / Best-First / A*");
            System.out.println("3. Tester h(Gafsa)=500");
            System.out.println("4. Tester extension du graphe");
            System.out.println("5. Executer tous les cas TP7");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                runAStarInteractive(scanner);
            } else if (choice.equals("2")) {
                compareAlgorithms();
            } else if (choice.equals("3")) {
                testModifiedHeuristic();
            } else if (choice.equals("4")) {
                testExtendedGraph();
            } else if (choice.equals("5")) {
                runAllTpCases();
            } else if (choice.equals("0")) {
                System.out.println("Ciao");
                return;
            } else {
                System.out.println("Choix non valide");
            }
        }
    }

    private void runAStarInteractive(Scanner scanner) {
        System.out.print("Ville de depart [" + DEFAULT_START + "]: ");
        String start = scanner.nextLine().trim();
        if (start.isEmpty()) {
            start = DEFAULT_START;
        }

        System.out.print("Ville d'arrivee [" + DEFAULT_GOAL + "]: ");
        String goal = scanner.nextLine().trim();
        if (goal.isEmpty()) {
            goal = DEFAULT_GOAL;
        }

        AStarSearch aStar = new AStarSearch();
        SearchResult result = aStar.search(graph, start, goal, heuristics);
        printTrace(result);
        printResult("A*", result);
    }

    private void compareAlgorithms() {
        BestFirstSearch bestFirst = new BestFirstSearch();
        UniformCostSearch ucs = new UniformCostSearch();
        AStarSearch aStar = new AStarSearch();

        SearchResult resultBest = bestFirst.search(graph, DEFAULT_START, DEFAULT_GOAL, heuristics);
        SearchResult resultUcs = ucs.search(graph, DEFAULT_START, DEFAULT_GOAL);
        SearchResult resultAStar = aStar.search(graph, DEFAULT_START, DEFAULT_GOAL, heuristics);

        System.out.println("\n--- Comparaison experimentale ---");
        printComparisonLine("UCS", resultUcs);
        printComparisonLine("Best-First", resultBest);
        printComparisonLine("A*", resultAStar);
    }

    private void testModifiedHeuristic() {
        AStarSearch aStar = new AStarSearch();
        Map<String, Double> modified = SampleData.modifiedGafsaHeuristics();
        SearchResult result = aStar.search(graph, DEFAULT_START, DEFAULT_GOAL, modified);

        System.out.println("\n--- Test h(Gafsa)=500 ---");
        printTrace(result);
        printResult("A* heuristique modifiee", result);
    }

    private void testExtendedGraph() {
        Graph extendedGraph = SampleData.extendedGraph();
        Map<String, Double> extendedHeuristics = SampleData.extendedHeuristics();
        AStarSearch aStar = new AStarSearch();
        SearchResult result = aStar.search(extendedGraph, DEFAULT_START, DEFAULT_GOAL, extendedHeuristics);

        System.out.println("\n--- Extension du graphe ---");
        printTrace(result);
        printResult("A* graphe etendu", result);

        System.out.println("Premiere iteration attendue depuis Tunis :");
        System.out.println("Kairouan f=410, Sousse f=440, ElKef f=490");
    }

    private void runAllTpCases() {
        AStarSearch aStar = new AStarSearch();
        SearchResult baseResult = aStar.search(graph, DEFAULT_START, DEFAULT_GOAL, heuristics);

        System.out.println("\n=== Question 1 : Trace A* ===");
        printTrace(baseResult);
        printResult("A* base", baseResult);

        System.out.println("\n=== Question 1c : si h(Kairouan)=50 ===");
        SearchResult lowKairouan = aStar.search(graph, DEFAULT_START, DEFAULT_GOAL, SampleData.kairouanLowHeuristics());
        printTrace(lowKairouan);
        System.out.println("Premier noeud choisi apres Tunis : " + firstExpandedAfterStart(lowKairouan));

        System.out.println("\n=== Question 2a : admissibilite ===");
        printAdmissibilityTable(graph, heuristics, DEFAULT_GOAL);

        System.out.println("\n=== Question 2b : h(Gafsa)=500 ===");
        SearchResult modifiedResult = aStar.search(graph, DEFAULT_START, DEFAULT_GOAL, SampleData.modifiedGafsaHeuristics());
        printResult("A* avec h(Gafsa)=500", modifiedResult);

        System.out.println("\n=== Question 2c : consistance ===");
        boolean consistent = GraphAnalysis.isConsistent(graph, heuristics);
        System.out.println("Heuristique consistante ? " + (consistent ? "oui" : "non"));
        for (String violation : GraphAnalysis.consistencyViolations(graph, heuristics)) {
            System.out.println("Violation: " + violation);
        }

        System.out.println("\n=== Question 3 : comparaison ===");
        compareAlgorithms();

        System.out.println("\n=== Question 4 : extension ===");
        testExtendedGraph();

        System.out.println("\n=== Question 5 : remarques ===");
        System.out.println("A* ne developpe pas forcement moins de noeuds que UCS.");
        System.out.println("Si h(n)=0 partout alors A* devient equivalent a UCS.");
        System.out.println("Applications possibles : jeux video, robotique mobile.");
    }

    private String firstExpandedAfterStart(SearchResult result) {
        List<String> order = result.getExplorationOrder();
        if (order.size() < 2) {
            return "(aucun)";
        }
        return order.get(1);
    }

    private void printTrace(SearchResult result) {
        for (SearchTraceStep step : result.getTraceSteps()) {
            System.out.println("=== Iteration " + step.getIteration() + " ===");
            System.out.println(
                    "Noeud courant : "
                            + step.getCurrentCity()
                            + " | g="
                            + Node.formatNumber(step.getG())
                            + " h="
                            + Node.formatNumber(step.getH())
                            + " f="
                            + Node.formatNumber(step.getF())
            );
            System.out.println("Open list  : " + step.getOpenList());
            System.out.println("Closed list: " + step.getClosedList());
        }
    }

    private void printResult(String title, SearchResult result) {
        System.out.println("\n--- " + title + " ---");
        if (!result.isSuccess()) {
            System.out.println("Pas de chemin trouve");
            return;
        }

        System.out.println("Chemin: " + result.pathAsString());
        System.out.println("Cout total: " + Node.formatNumber(result.getPathCost()));
        System.out.println("Ordre exploration: " + result.explorationAsString());
        System.out.println("Noeuds developpes: " + result.getDevelopedNodes());
    }

    private void printComparisonLine(String algorithm, SearchResult result) {
        System.out.println(
                algorithm
                        + " | chemin="
                        + result.pathAsString()
                        + " | cout="
                        + Node.formatNumber(result.getPathCost())
                        + " | noeuds="
                        + result.getDevelopedNodes()
        );
    }

    private void printAdmissibilityTable(Graph graph, Map<String, Double> heuristics, String goal) {
        List<String> order = Arrays.asList("Tunis", "Sousse", "Kairouan", "Sfax", "Gafsa", "Tozeur");
        Map<String, Double> realCosts = GraphAnalysis.shortestCostsToGoal(graph, goal);
        Map<String, Boolean> admissible = GraphAnalysis.admissibilityTable(graph, goal, heuristics, order);

        for (String city : order) {
            double h = heuristics.getOrDefault(city, Double.POSITIVE_INFINITY);
            double real = realCosts.getOrDefault(city, Double.POSITIVE_INFINITY);
            System.out.println(
                    city
                            + " | h="
                            + Node.formatNumber(h)
                            + " | cout reel minimal="
                            + Node.formatNumber(real)
                            + " | admissible="
                            + (admissible.get(city) ? "oui" : "non")
            );
        }
    }
}
