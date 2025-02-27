package dev.ftb.mods.ftbfiltersystem.util;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K,V> {
    private final Lock lock = new ReentrantLock();
    private final Map<K,LruNode<K,V>> map = new ConcurrentHashMap<>();
    private final Deque<LruNode<K,V>> deque = new ConcurrentLinkedDeque<>();
    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    public V get(K key) {
        try {
            lock.lockInterruptibly();
            if (map.containsKey(key)) {
                LruNode<K,V> node = map.get(key);
                deque.removeFirstOccurrence(node);
                deque.offerLast(node);
                return node.value;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public void put(K key, V value) {
        try {
            lock.lockInterruptibly();
            if (map.containsKey(key)) {
                LruNode<K,V> toRemove = map.remove(key);
                deque.removeFirstOccurrence(toRemove);
            } else if (map.size() == capacity) {
                LruNode<K,V> nodeToBeRemoved = deque.removeFirst();
                map.remove(nodeToBeRemoved.key);
            }
            LruNode<K,V> node = new LruNode<>(key, value);
            map.put(key, node);
            deque.offerLast(node);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        try {
            lock.lockInterruptibly();
            map.clear();
            deque.clear();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<K, LruNode<K,V>> entry : this.map.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" : ").append(entry.getValue().value).append("\n");
        }
        return stringBuilder.toString();
    }

    private record LruNode<K,V>(K key, V value) {}
}
