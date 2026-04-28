
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader {

    public static class LoadResult {
        public final Graph graph;
        public final Integer source;      // may be null if file does not contain it
        public final Integer destination; // may be null if file does not contain it
        public final Integer choice;      // 1 distance, 2 time, 3 both (may be null)

        public LoadResult(Graph graph, Integer source, Integer destination, Integer choice) {
            this.graph = graph;
            this.source = source;
            this.destination = destination;
            this.choice = choice;
        }
    }


    public LoadResult load(File file) throws IOException {
        Graph graph = new Graph();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String header = nextNonEmptyLine(br);
            if (header == null) throw new IllegalArgumentException("Empty file.");

            Integer source = null;
            Integer destination = null;
            Integer choice = null;

            String[] h = header.trim().split("\\s+");

            // Project format: source destination choice  (3 tokens)
            if (h.length >= 3) {
                source = parseIntStrict(h[0], "source");
                destination = parseIntStrict(h[1], "destination");
                choice = parseIntStrict(h[2], "choice");
                validateChoice(choice);
            }
            // Some datasets may start with only one integer
            else if (h.length == 1) {
                choice = parseIntStrict(h[0], "choice");
                if (choice != 1 && choice != 2 && choice != 3) {
                    choice = null;
                }
            } else {
                throw new IllegalArgumentException("First line is invalid.");
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split("\\s+");
                if (p.length < 4) continue;

                int u = parseIntStrict(p[0], "node1");
                int v = parseIntStrict(p[1], "node2");
                double dist = parseDoubleStrict(p[2], "distance");
                double time = parseDoubleStrict(p[3], "time");

                if (dist < 0 || time < 0) {
                    throw new IllegalArgumentException("Negative weights are not allowed for Dijkstra.");
                }

                graph.addEdge(u, v, dist, time);
            }

            return new LoadResult(graph, source, destination, choice);
        }
    }

    private String nextNonEmptyLine(BufferedReader br) throws IOException {
        String s;
        while ((s = br.readLine()) != null) {
            if (!s.trim().isEmpty()) return s;
        }
        return null;
    }

    private void validateChoice(int c) {
        if (c != 1 && c != 2 && c != 3) {
            throw new IllegalArgumentException("Choice must be 1 (distance), 2 (time), or 3 (both).");
        }
    }

    private int parseIntStrict(String s, String field) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid integer for " + field + ": " + s);
        }
    }

    private double parseDoubleStrict(String s, String field) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid number for " + field + ": " + s);
        }
    }
}
