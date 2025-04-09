package com.example.bookingMoliceiro.config;

import com.example.bookingMoliceiro.models.Restaurant;
import com.example.bookingMoliceiro.models.Meal;
import com.example.bookingMoliceiro.repositories.RestaurantRepository;
import com.example.bookingMoliceiro.repositories.MealRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    private final String[] lunchOptions = {
        "Arroz de Pato", "Bacalhau com Natas", "Carne de Porco à Alentejana", 
        "Feijoada à Transmontana", "Frango Assado", "Lasanha de Carne",
        "Pescada Cozida com Todos", "Vitela Estufada", "Lulas Grelhadas",
        "Empadão de Atum", "Almôndegas com Esparguete", "Sopa de Legumes"
    };
    
    private final String[] dinnerOptions = {
        "Bife à Portuguesa", "Salmão Grelhado", "Bitoque", "Peixe Espada Grelhado",
        "Febras Grelhadas", "Carapaus Grelhados", "Frango Estufado", "Espetada Mista",
        "Hambúrguer Gourmet", "Tortilha de Legumes", "Gratinado de Bacalhau", "Polvo à Lagareiro"
    };
    
    private final String[] vegetarianOptions = {
        "Tofu Grelhado com Legumes", "Lasanha Vegetariana", "Estufado de Legumes",
        "Hambúrguer Vegetariano", "Cogumelos Recheados", "Quiche de Espinafres",
        "Risoto de Legumes", "Caril de Grão", "Massa com Legumes Salteados"
    };
    
    private final double[] prices = {3.50, 4.00, 4.50, 5.00, 5.50, 6.00};
    private final Random random = new Random();

    @Bean
    public CommandLineRunner initData(
            RestaurantRepository restaurantRepository,
            MealRepository mealRepository) {
        
        return args -> {
            // Verifica se já existem dados
            if (restaurantRepository.count() > 0) {
                System.out.println("Banco de dados já populado, pulando inicialização.");
                return;
            }
            
            // Lista de restaurantes
            List<Restaurant> restaurants = new ArrayList<>();
            
            // Criando restaurantes (todos em Aveiro)
            restaurants.add(new Restaurant(null, "Cantina Crasto", "Aveiro", "https://api-assets.ua.pt/v1/image/resizer?imageUrl=https%3A%2F%2Fapi-assets.ua.pt%2Ffiles%2Fimgs%2F000%2F017%2F804%2Foriginal.jpg&width=1200", 150));
            restaurants.add(new Restaurant(null, "Cantina Santiago", "Aveiro", "https://riaprodstorageaccount.blob.core.windows.net/ria-prod/original/158fca66-dc94-4381-a881-c596fe93aa7e", 100));
            restaurants.add(new Restaurant(null, "Campi Grelhados", "Aveiro", "https://api-assets.ua.pt/v1/image/resizer?imageUrl=https%3A%2F%2Fapi-assets.ua.pt%2Ffiles%2Fimgs%2F000%2F017%2F805%2Foriginal.jpg&width=1200", 60));
            restaurants.add(new Restaurant(null, "Campi TrêsDê", "Aveiro", "https://api-assets.ua.pt/v1/image/resizer?imageUrl=https%3A%2F%2Fapi-assets.ua.pt%2Ffiles%2Fimgs%2F000%2F002%2F575%2Foriginal.jpg&width=1200", 70));
            
            // Adicionando o restaurante "Tasco" com capacidade máxima de 1
            Restaurant tasco = new Restaurant(null, "Tasco", "Rio de Janeiro", "https://www.my-deco-shop.com/1354-33198-thickbox/grand-salon-grande-mesa-cafe-carvalho-macico-eco-design.jpg", 1);
            restaurants.add(tasco);
            
            // Salvando restaurantes
            restaurantRepository.saveAll(restaurants);
            System.out.println("Restaurantes criados: " + restaurants.size());
            
            // Lista para armazenar todas as refeições
            List<Meal> allMeals = new ArrayList<>();
            
            // Período: 11/04/2025 até 19/04/2025 (reduzido para apenas dias úteis: 11, 14, 15, 16, 17, 18)
            List<LocalDate> selectedDates = new ArrayList<>();
            selectedDates.add(LocalDate.of(2025, 4, 11)); // Sexta
            selectedDates.add(LocalDate.of(2025, 4, 14)); // Segunda
            selectedDates.add(LocalDate.of(2025, 4, 16)); // Quarta
            selectedDates.add(LocalDate.of(2025, 4, 18)); // Sexta
            
            // Para cada dia selecionado
            for (LocalDate date : selectedDates) {
                // Para cada restaurante
                for (Restaurant restaurant : restaurants) {
                    // Pular o restaurante "Tasco" pois ele terá tratamento especial
                    if (restaurant.getName().equals("Tasco")) {
                        continue;
                    }
                    
                    // Apenas almoço para metade dos restaurantes e apenas jantar para outra metade
                    // nos dias pares para reduzir ainda mais a quantidade
                    if (restaurant.getId() % 2 == 0 || date.getDayOfMonth() % 2 == 0) {
                        // Horários de almoço (12:00 às 14:00) - reduzido o período
                        createMeals(allMeals, date, LocalTime.of(12, 0), LocalTime.of(14, 0), 
                                restaurant, "Almoço", false);
                    }
                    
                    if (restaurant.getId() % 2 != 0 || date.getDayOfMonth() % 2 != 0) {
                        // Horários de jantar (19:00 às 21:00) - reduzido o período
                        createMeals(allMeals, date, LocalTime.of(19, 0), LocalTime.of(21, 0), 
                                restaurant, "Jantar", false);
                    }
                }
            }
            
            // Adicionar as duas refeições específicas para o "Tasco" no dia 12/04/2025
            LocalDate tascoDate = LocalDate.of(2025, 4, 12); // 12/04/2025
            
            // Refeição de almoço para o "Tasco"
            Meal tascoLunch = new Meal(
                null, 
                "Sardinha Assada", 
                7.50, 
                tascoDate, 
                LocalTime.of(12, 30), 
                tasco
            );
            allMeals.add(tascoLunch);
            
            // Refeição de jantar para o "Tasco"
            Meal tascoDinner = new Meal(
                null, 
                "Caldo Verde com Chouriço", 
                6.00, 
                tascoDate, 
                LocalTime.of(20, 0), 
                tasco
            );
            allMeals.add(tascoDinner);
            
            // Salvando todas as refeições
            mealRepository.saveAll(allMeals);
            System.out.println("Refeições criadas: " + allMeals.size());
            
            System.out.println("Dados iniciais carregados com sucesso!");
        };
    }
    
    private void createMeals(List<Meal> mealsList, LocalDate date, LocalTime startTime, 
                            LocalTime endTime, Restaurant restaurant, String mealType, boolean isVegetarian) {
        
        String[] options = isVegetarian ? vegetarianOptions : 
                          (mealType.equals("Almoço") ? lunchOptions : dinnerOptions);
        
        // Número fixo de opções (apenas 1-2 opções por período)
        int numOptions = 1 + (date.getDayOfMonth() % 2); // 1 ou 2 opções dependendo se o dia é par ou ímpar
        
        // Criar opções de refeição para o período
        for (int i = 0; i < numOptions; i++) {
            // Seleciona uma refeição aleatória da lista de opções
            String mealName = options[random.nextInt(options.length)];
            
            // Horário fixo para cada refeição
            LocalTime mealTime;
            if (mealType.equals("Almoço")) {
                mealTime = LocalTime.of(12, 30);
            } else {
                mealTime = LocalTime.of(19, 30);
            }
            
            // Preço aleatório da lista de preços
            double price = prices[random.nextInt(prices.length)];
            
            // Cria a refeição
            Meal meal = new Meal(null, mealName, price, date, mealTime, restaurant);
            mealsList.add(meal);
        }
    }
}