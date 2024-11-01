package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    public HANLinkedList() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    @Override
    public void removeFirst() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }
        head = head.next;
        size--;
    }

    @Override
    public T getFirst() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }
        return head.data;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        if (index == 0) {
            addFirst(value);
            return;
        }
        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        Node<T> newNode = new Node<>(value);
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + pos);
        }
        Node<T> current = head;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + pos);
        }
        if (pos == 0) {
            removeFirst();
            return;
        }
        Node<T> current = head;
        for (int i = 0; i < pos - 1; i++) {
            current = current.next;
        }
        current.next = current.next.next;
        size--;
    }
}
