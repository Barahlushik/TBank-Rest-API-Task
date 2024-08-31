package org.example.service.translate.cache.impl;
import org.example.service.translate.cache.Cache;
import org.example.service.translate.cache.doubleLinkedList.CacheElement;
import org.example.service.translate.cache.doubleLinkedList.DoublyLinkedList;
import org.example.service.translate.cache.doubleLinkedList.LinkedListNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Component("LRUCache")
public class LRUCache<K, V> implements Cache<K, V> {

    @Value("${yandex.translate.cache.size}")
    private int maxSize;

    private final Map<K, LinkedListNode<CacheElement<K, V>>> linkedListNodeMap;
    private DoublyLinkedList<CacheElement<K,V>> doublyLinkedList;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache() {
        this.linkedListNodeMap = new HashMap<>(maxSize);
        this.doublyLinkedList = new DoublyLinkedList<>();
    }

    @Override
    public boolean put(K key, V value) {
        this.lock.writeLock().lock();
        try {
            CacheElement<K, V> item = new CacheElement<>(key, value);
            LinkedListNode<CacheElement<K, V>> newNode;
            if (this.linkedListNodeMap.containsKey(key)) {
                LinkedListNode<CacheElement<K, V>> node = this.linkedListNodeMap.get(key);
                newNode = doublyLinkedList.updateAndMoveToFront(node, item);
            } else {
                if (this.size() >= this.maxSize) {
                    this.evictElement();
                }
                newNode = this.doublyLinkedList.add(item);
            }
            if (newNode == null) {
                return false;
            }
            this.linkedListNodeMap.put(key, newNode);
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

        @Override
        public Optional<V> get (K key){
            this.lock.readLock().lock();
            try {
                LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
                if (linkedListNode != null) {
                    linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
                    return Optional.of(linkedListNode.getElement().getValue());
                }
                return Optional.empty();
            } finally {
                this.lock.readLock().unlock();
            }
        }

        @Override
        public int size () {
            this.lock.readLock().lock();
            try {
                return linkedListNodeMap.size();
            } finally {
                this.lock.readLock().unlock();
            }
        }

        @Override
        public boolean isEmpty () {
            this.lock.readLock().lock();
            try {
                return linkedListNodeMap.isEmpty();
            } finally {
                this.lock.readLock().unlock();
            }
        }

        @Override
        public void clear() {
            this.lock.writeLock().lock();
            try {
                linkedListNodeMap.clear();
                doublyLinkedList = new DoublyLinkedList<>();
            } finally {
                this.lock.writeLock().unlock();
            }
        }


        private boolean evictElement() {
            this.lock.writeLock().lock();
            try {
                LinkedListNode<CacheElement<K, V>> tail = doublyLinkedList.getTail();
                if (tail != null) {
                    linkedListNodeMap.remove(tail.getElement().getKey());
                    doublyLinkedList.removeLast();
                    return true;
                }
                return false;
            } finally {
                this.lock.writeLock().unlock();
            }
        }

}
