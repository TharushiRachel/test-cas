package com.itechro.cas.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TTLCache<K, V> {
    
    private static class CacheEntry<V> {
        private final V value;
        private final long expiryTime;
        
        public CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + ttlMillis;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
        
        public V getValue() {
            return value;
        }
    }
    
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final long defaultTTL;
    private final ScheduledExecutorService cleanupExecutor;
    
    public TTLCache(long defaultTTLMinutes) {
        this.cache = new ConcurrentHashMap<>();
        this.defaultTTL = TimeUnit.MINUTES.toMillis(defaultTTLMinutes);
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        
        // Schedule cleanup every 5 minutes
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanup, 5, 5, TimeUnit.MINUTES
        );
    }
    
    public void put(K key, V value) {
        put(key, value, defaultTTL);
    }
    
    public void put(K key, V value, long ttlMillis) {
        cache.put(key, new CacheEntry<>(value, ttlMillis));
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        return entry.getValue();
    }
    
    public void invalidate(K key) {
        cache.remove(key);
    }
    
    public void invalidateAll() {
        cache.clear();
    }
    
    private void cleanup() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public void shutdown() {
        cleanupExecutor.shutdown();
    }
    
    public int size() {
        return cache.size();
    }
}