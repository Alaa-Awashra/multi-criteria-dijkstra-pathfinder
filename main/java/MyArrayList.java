public class MyArrayList<T> {

    // Stores elements inside a normal array
    private Object[] data;

    // How many elements are currently stored
    private int size;

    // Default constructor start with capacity 10
    public MyArrayList() {
        this(10);
    }

    // Constructor with custom starting capacity
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        data = new Object[initialCapacity];
        size = 0;
    }

    // Returns number of elements currently in the list
    public int size() {
        return size;
    }

    // Returns true if list has no elements
    public boolean isEmpty() {
        return size == 0;
    }

    // Removes all elements
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    // Adds a new element at the end of the list
    public void add(T value) {
        ensureCapacity(size + 1); // make sure there is space
        data[size] = value;
        size++;
    }

    // Gets element at a specific index
    @SuppressWarnings("unchecked")
    public T get(int index) {
        rangeCheck(index);
        return (T) data[index];
    }

    // Removes element at index and shifts the rest left
    // This is used for alternative paths
    @SuppressWarnings("unchecked")
    public T removeAt(int index) {
        rangeCheck(index);
        T removed = (T) data[index];

        // shift elements left to fill the gap
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }

        // last element is now duplicated, remove it
        data[size - 1] = null;
        size--;

        return removed;
    }

    // Checks if index is valid
    private void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    // Makes sure array is big enough before adding new element
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) return;

        // Grow capacity
        int newCap = data.length * 2;

        // If still not enough, take minCapacity directly
        if (newCap < minCapacity) newCap = minCapacity;

        // Create bigger array and copy old data
        Object[] newData = new Object[newCap];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }

        data = newData;
    }
}
