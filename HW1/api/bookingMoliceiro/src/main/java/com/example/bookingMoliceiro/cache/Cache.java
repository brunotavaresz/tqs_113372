package com.example.bookingMoliceiro.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
//hash map
import java.util.HashMap;

@Component
public class Cache {
    private static Logger logger = LogManager.getLogger(Cache.class);
    private final HashMap<String, CacheItem> cacheMap = new HashMap<>();
    private int hits = 0;
    private int misses = 0;
    private int requests = 0;

    public Cache() {}

    public int getHits() {
        return hits;
    }

    public void incrementHits() {
        this.hits++;
    }

    public int getMisses() {
        return misses;
    }

    public void incrementMisses() {
        this.misses++;
    }

    public int getRequests() {
        return requests;
    }

    public void incrementRequests() {
        this.requests++;
    }

    public void addWeatherDataToCache(String key, Object data, long ttl) {
        cacheMap.put(key, new CacheItem(data, ttl));
        logger.info("Added to cache: {} with TTL of {}ms", key, ttl);
    }

    // Movido do WeatherService
    public Object getWeatherDataFromCache(String key) {
        CacheItem item = cacheMap.get(key);
        if (item != null && !item.isExpired()) {
            incrementHits();
            incrementRequests();
            logger.info("Cache hit for key: {}", key);
            return item.value;
        } else {
            incrementMisses();
            incrementRequests();
            logger.info("Cache miss for key: {}", key);
            return null;
        }
    }

    public void clearCache() {
        cacheMap.clear();
        hits = 0;
        misses = 0;
        requests = 0;
        logger.info("Cache cleared: hits = {}, misses = {}, requests = {}", hits, misses, requests);
    }

    public int getCacheSize() {
        return cacheMap.size();
    }

    public long getAverageRemainingTtlSeconds() {
        if (cacheMap.isEmpty()) {
            return 0;
        }

        long totalRemainingMs = 0;
        int count = 0;

        for (CacheItem item : cacheMap.values()) {
            if (!item.isExpired()) {
                totalRemainingMs += item.getRemainingTtl();
                count++;
            }
        }

        return count > 0 ? totalRemainingMs / count / 1000 : 0;
    }

    private static class CacheItem {
        Object value;
        long expirationTime;

        CacheItem(Object value, long ttl) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttl;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        long getRemainingTtl() {
            return Math.max(0, expirationTime - System.currentTimeMillis());
        }
    }
}
