
public class Edge {

    private final int to; // Where we are going next
    private final double distance; // Distance to the next node
    private final double time; // Time to the next node

    // Constructor
    public Edge(int to, double distance, double time) {
        this.to = to;
        this.distance = distance;
        this.time = time;
    }

    // Getters
    public int getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }
}
