public class Graph {

    // Adjacency List where adj[u] contains a linked list of all edges going OUT from node u
    private MyLinkedList<Edge>[] adj;

    // present[id] = true if this node id actually exists in the file
    private boolean[] present;

    // Total number of edges added to the graph
    private int edgesCount;

    // Total number of unique nodes that appeared in the file
    private int nodesCount;

    // Smallest node ID found in the graph used for GUI spinner range
    private int minNodeId;

    // Largest node ID found in the graph used for GUI spinner range
    private int maxNodeId;

    // Create graph with a default starting capacity
    @SuppressWarnings("unchecked")
    public Graph() {
        this(1024);
    }

    // Create graph with custom initial capacity
    @SuppressWarnings("unchecked")
    public Graph(int initialCapacity) {
        if (initialCapacity < 1)
            initialCapacity = 1;

        // Array of linked lists  where each index is a node, inside there its neighbors/edges
        adj = (MyLinkedList<Edge>[]) new MyLinkedList[initialCapacity];

        // Track which node IDs exist
        present = new boolean[initialCapacity];

        // Start counts at 0
        edgesCount = 0;
        nodesCount = 0;

        // Initialize min/max
        minNodeId = Integer.MAX_VALUE;
        maxNodeId = 0;
    }

    // Make sure arrays are big enough to store this nodeId
    // If nodeId is larger than current size, we double the arrays until it fits
    private void ensureCapacity(int nodeId) {
        if (nodeId < 0) {
            throw new IllegalArgumentException("Node id must be >= 0");
        }

        // If already fits, do nothing
        if (nodeId < adj.length)
            return;

        // Increase capacity by doubling
        int newCap = adj.length;
        while (nodeId >= newCap) {
            newCap *= 2;
        }

        // Create new larger arrays
        @SuppressWarnings("unchecked")
        MyLinkedList<Edge>[] newAdj = (MyLinkedList<Edge>[]) new MyLinkedList[newCap];
        boolean[] newPresent = new boolean[newCap];

        // Copy old data into new arrays
        for (int i = 0; i < adj.length; i++) {
            newAdj[i] = adj[i];
            newPresent[i] = present[i];
        }

        // Replace old arrays with new ones
        adj = newAdj;
        present = newPresent;
    }

    // Mark this node as existing in the graph
    // Also updates nodesCount and min/max node IDs for GUI
    private void touchNode(int nodeId) {
        ensureCapacity(nodeId);

        // If node is new
        if (!present[nodeId]) {
            present[nodeId] = true;
            nodesCount++;

            // Update min/max IDs
            if (nodeId < minNodeId) minNodeId = nodeId;
            if (nodeId > maxNodeId) maxNodeId = nodeId;
        }
    }

    // Add a directed edge: from -> to with distance and time weights
    // This is called by FileLoader while reading the file
    public void addEdge(int from, int to, double distance, double time) {
        // Make sure both nodes exist in arrays and are marked as present
        touchNode(from);
        touchNode(to);

        // If this node has no adjacency list yet, create it
        if (adj[from] == null) {
            adj[from] = new MyLinkedList<>();
        }

        // Add the edge into the linked list of neighbors
        adj[from].add(new Edge(to, distance, time));
        edgesCount++;
    }

    // Return all outgoing edges from a node
    // If node has no neighbors or nodeId out of range, return empty list cuz it's safe for for-each
    public MyLinkedList<Edge> getNeighbors(int nodeId) {
        if (nodeId < 0 || nodeId >= adj.length) {
            return new MyLinkedList<>();
        }
        if (adj[nodeId] == null) {
            return new MyLinkedList<>();
        }
        return adj[nodeId];
    }

    // True if this node exists in the file
    public boolean containsNode(int nodeId) {
        return nodeId >= 0 && nodeId < present.length && present[nodeId];
    }

    // Same as containsNode (extra name to make code more readable)
    public boolean isPresent(int nodeId) {
        return containsNode(nodeId);
    }

    // Return number of unique nodes in the graph
    public int nodesCount() {
        return nodesCount;
    }

    // Return number of edges in the graph
    public int edgesCount() {
        return edgesCount;
    }

    // Smallest node ID found (used for GUI spinner min)
    public int getMinNodeId() {
        return nodesCount == 0 ? 0 : minNodeId;
    }

    // Largest node ID found (used for GUI spinner max)
    public int getMaxNodeId() {
        return nodesCount == 0 ? 0 : maxNodeId;
    }

    // Current size of arrays (how many indices we can store)
    public int capacity() {
        return present.length;
    }
}
