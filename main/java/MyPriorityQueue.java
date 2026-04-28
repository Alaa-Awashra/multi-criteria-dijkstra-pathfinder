public class MyPriorityQueue<T extends Comparable<T>> {

    // Heap array (stores elements as a binary heap)
    private Object[] heap;

    // Number of elements currently in the heap
    private int size;

    // Default constructor
    public MyPriorityQueue() {
        this(16);
    }

    // Constructor with custom capacity
    public MyPriorityQueue(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        heap = new Object[initialCapacity];
        size = 0;
    }

    // Returns true if no elements exist
    public boolean isEmpty() {
        return size == 0;
    }

    // Adds an element to the min-heap
    public void add(T value) {
        ensureCapacity(size + 1);
        heap[size] = value;
        heapifyUp(size);
        size++;
    }

    // Removes and returns the minimum element (root)
    public T poll() {
        if (size == 0) return null;
        T root = (T) heap[0];
        T last = (T) heap[size - 1];
        heap[size - 1] = null;
        size--;

        // If still has elements, put last at root and fix heap
        if (size > 0) {
            heap[0] = last;
            siftDown(0);
        }

        return root;
    }

    // Moves a node up until heap order is correct
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            T cur = (T) heap[index];
            T par = (T) heap[parent];

            // If current >= parent, heap is correct
            if (cur.compareTo(par) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    // Moves a node down until heap order is correct
    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && compare(left, smallest) < 0) {
                smallest = left;
            }
            if (right < size && compare(right, smallest) < 0) {
                smallest = right;
            }

            // If smallest is still index, stop
            if (smallest == index) break;

            swap(index, smallest);
            index = smallest;
        }
    }

    // Compare heap[i] with heap[j]
    private int compare(int i, int j) {
        T a = (T) heap[i];
        T b = (T) heap[j];
        return a.compareTo(b);
    }

    // Swap two elements in the heap
    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    // Grow array if needed
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= heap.length) return;

        int newCap = heap.length * 2;
        if (newCap < minCapacity) newCap = minCapacity;

        Object[] newHeap = new Object[newCap];
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }
}
