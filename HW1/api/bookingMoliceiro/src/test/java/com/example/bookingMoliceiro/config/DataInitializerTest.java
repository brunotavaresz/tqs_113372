package com.example.bookingMoliceiro.config;

import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.repositories.MealRepository;
import com.example.bookingMoliceiro.repositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    public void testInsertData() throws Exception {
        when(restaurantRepository.count()).thenReturn(0L);

        // Simula saveAll para restaurantes com IDs atribuídos
        when(restaurantRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Restaurant> restaurants = invocation.getArgument(0);
            long id = 1;
            for (Restaurant r : restaurants) {
                r.setId(id++);
            }
            return restaurants;
        });

        // Simula saveAll para refeições
        when(mealRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        dataInitializer.initData(restaurantRepository, mealRepository).run();

        // Verifica se os dados foram salvos
        verify(restaurantRepository, atLeastOnce()).saveAll(anyList());
        verify(mealRepository, atLeastOnce()).saveAll(anyList());
    }


    @Test
    public void testSkipInsertWhenDataExists() throws Exception {
        // Configurar o mock para simular que já existem dados
        when(restaurantRepository.count()).thenReturn(1L);

        // Executar o inicializador
        dataInitializer.initData(restaurantRepository, mealRepository).run();

        // Verificar que saveAll não foi chamado em nenhum repositório
        verify(restaurantRepository, never()).saveAll(anyList());
        verify(mealRepository, never()).saveAll(anyList());
    }
}