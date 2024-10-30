package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private Node header;
    private int size;

    private class Node {
        T data;
        Node next;
        Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    public HANLinkedList() {
        this.header = new Node(null, null); // Sentinel node
        this.size = 0;
    }

    @Override
    public void addFirst(T value) {
        header.next = new Node(value, header.next);
        size++;
    }

    @Override
    public void clear() {
        header.next = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        Node current = header;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.next = new Node(value, current.next);
        size++;
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + pos);
        }

        Node current = header;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        current.next = current.next.next;
        size--;
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + pos);
        }

        Node current = header.next;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void removeFirst() {
        if (header.next != null) {
            header.next = header.next.next;
            size--;
        }
    }

    @Override
    public T getFirst() {
        if (header.next == null) {
            throw new IllegalStateException("List is empty");
        }
        return header.next.data;
    }

    @Override
    public int getSize() {
        return size;
    }
}
