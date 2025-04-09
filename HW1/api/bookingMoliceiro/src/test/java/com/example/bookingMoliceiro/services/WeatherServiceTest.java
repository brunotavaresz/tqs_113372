package com.example.bookingMoliceiro.services;

import com.example.bookingMoliceiro.cache.Cache;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.models.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Cache cache;

    @InjectMocks
    private WeatherService weatherService;

    @Captor
    private ArgumentCaptor<String> cacheKeyCaptor;

    @Captor
    private ArgumentCaptor<Object> cacheDataCaptor;

    @Captor
    private ArgumentCaptor<Long> cacheTtlCaptor;

    private Meal testMeal;
    private Restaurant testRestaurant;
    private Map<String, Object> mockApiResponse;

    @BeforeEach
    void setUp() {
        // ReflectionTestUtils permite definir valores em campos privados
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherService, "restTemplate", restTemplate);

        // Preparar dados de teste
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Aveiro");

        testMeal = new Meal();
        testMeal.setId(1L);
        testMeal.setName("Test Meal");
        testMeal.setRestaurant(testRestaurant);
        testMeal.setDate(LocalDate.of(2023, 10, 15));
        testMeal.setTime(LocalTime.of(12, 0));

        // Criar mock para a resposta da API
        mockApiResponse = new HashMap<>();
        List<Map<String, Object>> forecastList = new ArrayList<>();
        
        Map<String, Object> forecast1 = new HashMap<>();
        forecast1.put("dt", 1697371200L); // 15/10/2023 12:00
        Map<String, Object> main1 = new HashMap<>();
        main1.put("temp", 20.5);
        forecast1.put("main", main1);
        
        Map<String, Object> forecast2 = new HashMap<>();
        forecast2.put("dt", 1697382000L); // 15/10/2023 15:00
        Map<String, Object> main2 = new HashMap<>();
        main2.put("temp", 22.0);
        forecast2.put("main", main2);
        
        forecastList.add(forecast1);
        forecastList.add(forecast2);
        mockApiResponse.put("list", forecastList);
    }

    @Test
    void getWeatherForecastForMeal_CacheHit_ReturnsCachedData() {
        // Arrange
        Map<String, Object> cachedForecast = new HashMap<>();
        cachedForecast.put("main", Map.of("temp", 20.5));
        
        when(cache.getWeatherDataFromCache(anyString())).thenReturn(cachedForecast);

        // Act
        Map<String, Object> result = weatherService.getWeatherForecastForMeal(testMeal);

        // Assert
        assertEquals(cachedForecast, result);
        verify(cache).getWeatherDataFromCache(anyString());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getWeatherForecastForMeal_CacheMiss_FetchesAndCachesData() {
        // Arrange
        when(cache.getWeatherDataFromCache(anyString())).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockApiResponse);

        // Act
        Map<String, Object> result = weatherService.getWeatherForecastForMeal(testMeal);

        // Assert
        assertNotNull(result);
        verify(cache).getWeatherDataFromCache(anyString());
        verify(restTemplate).getForObject(contains("api.openweathermap.org"), eq(Map.class));
        verify(cache).addWeatherDataToCache(cacheKeyCaptor.capture(), cacheDataCaptor.capture(), anyLong());
        
        String capturedKey = cacheKeyCaptor.getValue();
        assertTrue(capturedKey.startsWith("Aveiro_"));
    }

    @Test
    void getWeatherForecastForMeal_MealWithoutRestaurant_ThrowsException() {
        // Arrange
        testMeal.setRestaurant(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherForecastForMeal(testMeal);
        });
        
        assertEquals("Meal is not associated with a restaurant", exception.getMessage());
        verifyNoInteractions(cache);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getWeatherForecastForMeal_RestaurantWithoutLocation_ThrowsException() {
        // Arrange
        testRestaurant.setLocation(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherForecastForMeal(testMeal);
        });
        
        assertEquals("Restaurant has no defined location", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void findClosestForecast_FindsNearestForecastToTargetTime() {
        // Arrange
        long targetTimestamp = 1697371200L; // 15/10/2023 12:00

        // Act - Usando reflection para testar método privado
        Map<String, Object> result = weatherService.findClosestForecast(mockApiResponse, targetTimestamp);

        // Assert
        assertNotNull(result);
        assertEquals(1697371200L, ((Number) result.get("dt")).longValue());
    }

    @Test
    void findClosestForecast_InvalidApiResponse_ThrowsException() {
        // Arrange
        Map<String, Object> invalidResponse = new HashMap<>(); // Sem a chave "list"
        long targetTimestamp = 1697371200L;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            weatherService.findClosestForecast(invalidResponse, targetTimestamp);
        });
        
        assertEquals("Invalid response from OpenWeather API", exception.getMessage());
    }

    @Test
    void getCacheStatistics_ReturnsCorrectStats() {
        // Arrange
        when(cache.getRequests()).thenReturn(100);
        when(cache.getHits()).thenReturn(70);
        when(cache.getMisses()).thenReturn(30);
        when(cache.getCacheSize()).thenReturn(25);
        when(cache.getAverageRemainingTtlSeconds()).thenReturn(60L);

        // Act
        Map<String, Object> stats = weatherService.getCacheStatistics();

        // Assert
        assertEquals(100, stats.get("totalRequests"));
        assertEquals(70, stats.get("cacheHits"));
        assertEquals(30, stats.get("cacheMisses"));
        assertEquals(0.7, stats.get("hitRate"));
        assertEquals(25, stats.get("cacheSize"));
        assertEquals(60L, stats.get("cacheTtlSeconds"));
    }

    @Test
    void clearCache_CallsCacheClearMethod() {
        // Act
        weatherService.clearCache();

        // Assert
        verify(cache).clearCache();
    }
}