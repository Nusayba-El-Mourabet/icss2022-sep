package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {

    private Node top;
    private int size;

    private class Node {
        T data;
        Node next;

        Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    public HANStack() {
        this.top = null; // Initialize an empty stack
        this.size = 0;
    }

    @Override
    public void push(T value) {
        top = new Node(value, top);
        size++;
    }

    @Override
    public T pop() {
        if (top == null) {
            throw new IllegalStateException("Stack is empty");
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    @Override
    public T peek() {
        if (top == null) {
            throw new IllegalStateException("Stack is empty");
        }
        return top.data;
    }

    /**
     * Returns the current size of the stack
     * @return size of the stack
     */
    public int getSize() {
        return size;
    }

    /**
     * Checks if the stack is empty
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
