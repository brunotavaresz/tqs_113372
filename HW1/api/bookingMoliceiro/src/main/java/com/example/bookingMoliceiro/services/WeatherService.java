package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final Cache cache;

    public WeatherService(Cache cache) {
        this.restTemplate = new RestTemplate();
        this.cache = cache;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWeatherForecastForMeal(Meal meal) {
        logger.info("Requesting weather forecast for meal with ID: {}", meal.getId());

        Restaurant restaurant = meal.getRestaurant();
        if (restaurant == null) {
            logger.warn("Meal with ID: {} is not associated with a restaurant.", meal.getId());
            throw new RuntimeException("Meal is not associated with a restaurant");
        }

        String city = restaurant.getLocation();
        if (city == null || city.isEmpty()) {
            logger.warn("Meal with ID: {} has no associated city.", meal.getId());
            throw new RuntimeException("Restaurant has no defined location");
        }

        LocalDateTime dateTime = LocalDateTime.of(meal.getDate(), meal.getTime());
        long timestamp = dateTime.toEpochSecond(ZoneOffset.UTC);
        long roundedTimestamp = Math.round(timestamp / 10800.0) * 10800;
        String cacheKey = city + "_" + roundedTimestamp;

        logger.debug("Generated cache key: {}", cacheKey);

        Map<String, Object> cachedData = (Map<String, Object>) cache.getWeatherDataFromCache(cacheKey);
        if (cachedData != null) {
            logger.info("Cache hit for key: {}", cacheKey);
            return cachedData;
        }

        logger.info("Cache miss for key: {}", cacheKey);

        String url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric", city, apiKey);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> forecast = findClosestForecast(response, timestamp);

        cache.addWeatherDataToCache(cacheKey, forecast, 120000); // Cache TTL 2 minutes
        logger.info("Weather data fetched and cached for key: {}", cacheKey);
        return forecast;
    }

    public Map<String, Object> findClosestForecast(Map<String, Object> apiResponse, long targetTimestamp) {
        logger.debug("Finding closest forecast for timestamp: {}", targetTimestamp);

        if (apiResponse == null || !apiResponse.containsKey("list")) {
            logger.error("Invalid response from OpenWeather API.");
            throw new RuntimeException("Invalid response from OpenWeather API");
        }

        List<Map<String, Object>> forecasts = (List<Map<String, Object>>) apiResponse.get("list");

        Map<String, Object> closestForecast = null;
        long closestDiff = Long.MAX_VALUE;

        for (Map<String, Object> forecast : forecasts) {
            Number dt = (Number) forecast.get("dt");
            if (dt != null) {
                long forecastTimestamp = dt.longValue();
                long diff = Math.abs(forecastTimestamp - targetTimestamp);
                if (diff < closestDiff) {
                    closestDiff = diff;
                    closestForecast = forecast;
                }
            }
        }

        logger.debug("Closest forecast found: {}", closestForecast);
        return closestForecast != null ? closestForecast : new HashMap<>();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", cache.getRequests());
        stats.put("cacheHits", cache.getHits());
        stats.put("cacheMisses", cache.getMisses());
        stats.put("hitRate", cache.getRequests() > 0 ? (double) cache.getHits() / cache.getRequests() : 0);
        stats.put("cacheSize", cache.getCacheSize());
        stats.put("cacheTtlSeconds", cache.getAverageRemainingTtlSeconds());

        logger.info("Cache statistics: totalRequests = {}, cacheHits = {}, cacheMisses = {}", 
                    cache.getRequests(), cache.getHits(), cache.getMisses());
        return stats;
    }

    @Transactional
    public void clearCache() {
        logger.info("Clearing the weather cache.");
        cache.clearCache();
    }
}
