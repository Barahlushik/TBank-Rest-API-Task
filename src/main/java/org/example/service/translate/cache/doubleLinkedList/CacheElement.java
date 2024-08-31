package org.example.service.translate.cache.doubleLinkedList;


public class CacheElement<K, V> {
    private final K key;
    private V value;
    private LinkedListNode<CacheElement<K, V>> nodeReference;


    public CacheElement(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }


    public LinkedListNode<CacheElement<K, V>> getNodeReference() {
        return nodeReference;
    }

    public void setNodeReference(LinkedListNode<CacheElement<K, V>> nodeReference) {
        this.nodeReference = nodeReference;
    }

    @Override
    public String toString() {
        return "CacheElement{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
