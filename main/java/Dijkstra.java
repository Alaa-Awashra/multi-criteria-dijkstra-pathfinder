public class Dijkstra {

    // Item stored in the priority queue
    private static class State implements Comparable<State> {
        int node;
        double cost;

        State(int node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        @Override
        public int compareTo(State other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    public PathResult run(Graph graph, int source, int destination, String mode) {

        // Basic checks
        if (graph == null) {
            throw new IllegalArgumentException("Graph is null. Load the file first.");
        }
        if (!graph.containsNode(source)) {
            throw new IllegalArgumentException("Source node not found: " + source);
        }
        if (!graph.containsNode(destination)) {
            throw new IllegalArgumentException("Destination node not found: " + destination);
        }

        boolean useDistance = mode != null && mode.equalsIgnoreCase("Distance");
        boolean useTime = mode != null && mode.equalsIgnoreCase("Time");

        if (!useDistance && !useTime) {
            throw new IllegalArgumentException("Mode must be Distance or Time.");
        }

        int cap = graph.capacity();
        double INF = Double.POSITIVE_INFINITY;

        // Arrays we need
        double[] dist = new double[cap];
        boolean[] visited = new boolean[cap];
        int[] parent = new int[cap];

        for (int i = 0; i < cap; i++) {
            dist[i] = INF;
            visited[i] = false;
            parent[i] = -1;
        }

        // Start from source
        MyPriorityQueue<State> pq = new MyPriorityQueue<>();
        dist[source] = 0;
        pq.add(new State(source, 0));

        // Dijkstra main loop
        while (!pq.isEmpty()) {

            State cur = pq.poll();
            if (cur == null)
                break;

            int u = cur.node;

            // Skip invalid
            if (u < 0 || u >= cap)
                continue;

            // If already done, skip
            if (visited[u])
                continue;

            visited[u] = true;

            // Stop early if we reached destination
            if (u == destination) break;

            // Explore neighbors
            for (Edge e : graph.getNeighbors(u)) {

                int v = e.getTo();
                if (v < 0 || v >= cap) continue;
                if (visited[v]) continue;

                double w = useDistance ? e.getDistance() : e.getTime();
                double newCost = dist[u] + w;

                // if Better path found
                if (newCost < dist[v]) {
                    dist[v] = newCost;
                    parent[v] = u;
                    pq.add(new State(v, newCost));
                }
            }
        }

        // If no path
        if (dist[destination] == INF) {
            return PathResult.noPath();
        }

        // Build main path using parent[]
        MyArrayList<Integer> mainPath = buildMainPath(parent, source, destination);
        if (mainPath.isEmpty()) return PathResult.noPath();

        // Compute totals for main path
        double totalDistance = 0;
        double totalTime = 0;

        for (int i = 0; i < mainPath.size() - 1; i++) {
            int a = mainPath.get(i);
            int b = mainPath.get(i + 1);

            Edge edge = findEdge(graph, a, b);
            if (edge == null) {
                throw new IllegalStateException("Edge missing: " + a + " -> " + b);
            }

            totalDistance += edge.getDistance();
            totalTime += edge.getTime();
        }

        return new PathResult(true, mainPath, totalDistance, totalTime);
    }

    // Build main path from destination -> source then reverse it
    private MyArrayList<Integer> buildMainPath(int[] parent, int source, int destination) {

        MyArrayList<Integer> reversed = new MyArrayList<>(64);
        int cur = destination;

        while (cur != -1) {
            reversed.add(cur);
            if (cur == source) break;
            cur = parent[cur];
        }

        // If we never reached source, path is invalid
        if (reversed.isEmpty()) return new MyArrayList<>(1);
        if (reversed.get(reversed.size() - 1) != source) return new MyArrayList<>(1);

        // Reverse it
        MyArrayList<Integer> path = new MyArrayList<>(reversed.size());
        for (int i = reversed.size() - 1; i >= 0; i--) {
            path.add(reversed.get(i));
        }

        return path;
    }

    // Find the edge from -> to
    private Edge findEdge(Graph graph, int from, int to) {
        for (Edge e : graph.getNeighbors(from)) {
            if (e.getTo() == to) return e;
        }
        return null;
    }
}
