public class PathResult {

    private final boolean found; // To check if we reached the destination
    private final MyArrayList<Integer> path; // The sequence of nodes in the path
    private final double totalDistance; // Total distance of the path
    private final double totalTime; // Total time of the path

    // Constructor
    public PathResult(boolean found, MyArrayList<Integer> path, double totalDistance, double totalTime) {
        this.found = found;
        this.path = (path == null) ? new MyArrayList<>(1) : path;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
    }

    // Static method when destination is unreachable
    public static PathResult noPath() {
        return new PathResult(false,
                new MyArrayList<>(1),
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
    }

    // Getters
    public boolean isFound() {
        return found;
    }

    public MyArrayList<Integer> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalTime() {
        return totalTime;
    }

    // Convert main path into a readable string
    public String pathAsString() {
        if (!found || path.isEmpty())
            return "No path";
        return listToString(path);
    }

    // Helper method to convert a list of integers to a string representation
    public static String listToString(MyArrayList<Integer> p) {
        if (p == null || p.isEmpty())
            return "No path";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.size(); i++) {
            sb.append(p.get(i));
            if (i + 1 < p.size())
                sb.append(" -> ");
        }
        return sb.toString();
    }
}
