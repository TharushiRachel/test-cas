package com.itechro.cas.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTTLCache<K, V> {
    private final Map<K, CacheObject<V>> cacheMap = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public SimpleTTLCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public void put(K key, V value) {
        cacheMap.put(key, new CacheObject<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheObject<V> cacheObject = cacheMap.get(key);
        if (cacheObject == null) return null;
        if (System.currentTimeMillis() - cacheObject.timestamp > ttlMillis) {
            cacheMap.remove(key);
            return null;
        }
        return cacheObject.value;
    }

    private static class CacheObject<V> {
        V value;
        long timestamp;
        CacheObject(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}

