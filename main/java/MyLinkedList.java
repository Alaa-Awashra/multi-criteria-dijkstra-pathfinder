import java.util.Iterator;

public class MyLinkedList<T> implements Iterable<T> {

    // One node in the linked list stores data and pointer to next node
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    // First node in the list
    private Node<T> head;

    // Last node
    private Node<T> tail;

    public MyLinkedList() {
        head = null;
        tail = null;
    }

    // Add element to the end of the list
    // Used to store edges in adjacency list
    public void add(T value) {
        Node<T> n = new Node<>(value);

        if (head == null) {
            head = n;
            tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
    }

    // Check if list is empty
    public boolean isEmpty() {
        return head == null;
    }

    // Allows for-each loop:
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            private Node<T> cur = head;

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public T next() {
                T val = cur.data;
                cur = cur.next;
                return val;
            }
        };
    }
}
