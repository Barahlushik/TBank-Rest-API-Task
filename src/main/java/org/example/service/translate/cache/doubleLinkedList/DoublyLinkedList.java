package org.example.service.translate.cache.doubleLinkedList;

public class DoublyLinkedList<T> {
    private LinkedListNode<T> head;
    private LinkedListNode<T> tail;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public LinkedListNode<T> getTail() {
        return tail;
    }


    public LinkedListNode<T> add(T element) {
        LinkedListNode<T> newNode = new LinkedListNode<>(element);

        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }

        return newNode;
    }


    public LinkedListNode<T> moveToFront(LinkedListNode<T> node) {
        if (node == head) {
            return node;
        }
        detach(node);
        node.next = head;
        if (head != null) {
            head.prev = node;
        }
        head = node;
        if (tail == null) {
            tail = head;
        }
        return head;
    }


    public LinkedListNode<T> updateAndMoveToFront(LinkedListNode<T> node, T newValue) {
        if (node == null || node.isEmpty()) {
            return null;
        }

        node.element = newValue;
        return moveToFront(node);
    }


    public void detach(LinkedListNode<T> node) {
        if (node == null) {
            return;
        }


        if (node == head) {
            head = node.next;
        }

        if (node == tail) {
            tail = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }

        node.prev = null;
        node.next = null;
    }

    public T removeLast() {
        if (tail == null) {
            return null;
        }
        T element = tail.getElement();
        detach(tail);
        return element;
    }
}