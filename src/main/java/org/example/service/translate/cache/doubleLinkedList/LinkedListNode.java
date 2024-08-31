package org.example.service.translate.cache.doubleLinkedList;

public class LinkedListNode<T> {
    T element;
    LinkedListNode<T> prev;
    LinkedListNode<T> next;

    public LinkedListNode(T element) {
        this.element = element;
    }

    public T getElement() {
        return element;
    }

    public boolean isEmpty() {
        return this.element == null;
    }
}

