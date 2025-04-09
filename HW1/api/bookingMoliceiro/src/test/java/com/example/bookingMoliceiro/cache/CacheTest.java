package com.example.bookingMoliceiro.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CacheTest {

    private Cache cache;

    @BeforeEach
    void setUp() {
        cache = new Cache();
    }

    @Test
    void addWeatherDataToCache_AddsItemToCache() {
        // Arrange
        String key = "testKey";
        Map<String, Object> data = new HashMap<>();
        data.put("temp", 20.5);
        long ttl = 30000; // 30 segundos

        // Act
        cache.addWeatherDataToCache(key, data, ttl);

        // Assert
        assertEquals(data, cache.getWeatherDataFromCache(key));
        assertEquals(1, cache.getCacheSize());
    }

    @Test
    void getWeatherDataFromCache_ItemExists_ReturnsItemAndIncrementsCounters() {
        // Arrange
        String key = "testKey";
        Map<String, Object> data = new HashMap<>();
        data.put("temp", 20.5);
        cache.addWeatherDataToCache(key, data, 30000);

        // Pre-condition check
        assertEquals(0, cache.getHits());
        assertEquals(0, cache.getRequests());

        // Act
        Object result = cache.getWeatherDataFromCache(key);

        // Assert
        assertEquals(data, result);
        assertEquals(1, cache.getHits());
        assertEquals(1, cache.getRequests());
        assertEquals(0, cache.getMisses());
    }

    @Test
    void getWeatherDataFromCache_ItemDoesNotExist_ReturnsNullAndIncrementsCounters() {
        // Arrange
        String key = "nonExistentKey";

        // Pre-condition check
        assertEquals(0, cache.getMisses());
        assertEquals(0, cache.getRequests());

        // Act
        Object result = cache.getWeatherDataFromCache(key);

        // Assert
        assertNull(result);
        assertEquals(0, cache.getHits());
        assertEquals(1, cache.getRequests());
        assertEquals(1, cache.getMisses());
    }

    @Test
    void getWeatherDataFromCache_ExpiredItem_ReturnsNullAndIncrementsCounters() throws InterruptedException {
        // Arrange
        String key = "expiringKey";
        Map<String, Object> data = new HashMap<>();
        data.put("temp", 20.5);
        cache.addWeatherDataToCache(key, data, 10); // 10 ms TTL

        // Esperar para o item expirar
        TimeUnit.MILLISECONDS.sleep(20);

        // Act
        Object result = cache.getWeatherDataFromCache(key);

        // Assert
        assertNull(result);
        assertEquals(0, cache.getHits());
        assertEquals(1, cache.getRequests());
        assertEquals(1, cache.getMisses());
    }

    @Test
    void clearCache_RemovesAllItemsAndResetsCounters() {
        // Arrange
        cache.addWeatherDataToCache("key1", "data1", 30000);
        cache.addWeatherDataToCache("key2", "data2", 30000);
        cache.getWeatherDataFromCache("key1"); // Incrementar hits e requests
        cache.getWeatherDataFromCache("nonExistent"); // Incrementar misses e requests

        // Pre-condition check
        assertEquals(1, cache.getHits());
        assertEquals(1, cache.getMisses());
        assertEquals(2, cache.getRequests());
        assertEquals(2, cache.getCacheSize());

        // Act
        cache.clearCache();

        // Assert
        assertEquals(0, cache.getHits());
        assertEquals(0, cache.getMisses());
        assertEquals(0, cache.getRequests());
        assertEquals(0, cache.getCacheSize());
        assertNull(cache.getWeatherDataFromCache("key1"));
    }

    @Test
    void getAverageRemainingTtlSeconds_NoItems_ReturnsZero() {
        // Act & Assert
        assertEquals(0, cache.getAverageRemainingTtlSeconds());
    }

    @Test
    void getAverageRemainingTtlSeconds_WithItems_ReturnsCorrectAverage() throws InterruptedException {
        // Arrange
        cache.addWeatherDataToCache("key1", "data1", 10000); // 10 segundos
        cache.addWeatherDataToCache("key2", "data2", 20000); // 20 segundos

        // Act
        long ttlSeconds = cache.getAverageRemainingTtlSeconds();

        // Assert - Verifica se está entre valores esperados (com margem para execução)
        assertTrue(ttlSeconds > 0 && ttlSeconds <= 15, 
                   "Expected TTL between 0 and 15 seconds, but got " + ttlSeconds);
    }

    @Test
    void counters_CorrectlyTrackStatistics() {
        // Arrange & Act
        cache.incrementHits();
        cache.incrementHits();
        cache.incrementMisses();
        cache.incrementRequests();
        cache.incrementRequests();
        cache.incrementRequests();

        // Assert
        assertEquals(2, cache.getHits());
        assertEquals(1, cache.getMisses());
        assertEquals(3, cache.getRequests());
    }
}