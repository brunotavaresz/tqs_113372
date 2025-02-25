package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@ExtendWith(MockitoExtension.class)
public class StocksPortfolioTest {

    @Mock
    private IStockmarketService stockmarketService;

    @InjectMocks
    private StocksPortfolio portfolio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTotalValue() {

        when(stockmarketService.lookUpPrice("AAPL")).thenReturn(150.0);
        when(stockmarketService.lookUpPrice("GOOGL")).thenReturn(2800.0);

        portfolio.addStock(new Stock("AAPL", 2));
        portfolio.addStock(new Stock("GOOGL", 1));

        //assertEquals(3100.0, portfolio.totalValue(), 0.01); // 300 + 2800

        assertThat(portfolio.totalValue(), is(3100.0));
        
        verify(stockmarketService).lookUpPrice("AAPL");
        verify(stockmarketService).lookUpPrice("GOOGL");   }

    @Test
    void mostValuableStocks() {
            // Mock de preços
            when(stockmarketService.lookUpPrice("AAPL")).thenReturn(150.0);
            when(stockmarketService.lookUpPrice("GOOGL")).thenReturn(2800.0);
            when(stockmarketService.lookUpPrice("AMZN")).thenReturn(3500.0);
        
            // Adicionar stocks ao portefólio
            portfolio.addStock(new Stock("AAPL", 2));    // 2 * 150 = 300
            portfolio.addStock(new Stock("GOOGL", 1));   // 1 * 2800 = 2800
            portfolio.addStock(new Stock("AMZN", 3));    // 3 * 3500 = 10500
        
            // Obter os top 2 stocks mais valiosos
            List<Stock> topStocks = portfolio.mostValuableStocks(2);
        
            // Verificar que os stocks mais valiosos estão corretos
            assertThat(topStocks.size(), is(2));
            assertThat(topStocks.get(0).getLabel(), is("AMZN"));
            assertThat(topStocks.get(1).getLabel(), is("GOOGL"));
    }


    // sem ai

    @Test
    void testMostValuableStocks_TopNZero() {
        when(stockmarketService.lookUpPrice("AAPL")).thenReturn(150.0);
        when(stockmarketService.lookUpPrice("GOOGL")).thenReturn(2800.0);

        portfolio.addStock(new Stock("AAPL", 2));  // 300
        portfolio.addStock(new Stock("GOOGL", 1)); // 2800

        List<Stock> topStocks = portfolio.mostValuableStocks(0);

        assertThat(topStocks.size(), is(0)); // Deve retornar uma lista vazia
    }

    @Test
    void testMostValuableStocks_TopNExceedsSize() {
        when(stockmarketService.lookUpPrice("AAPL")).thenReturn(150.0);
        when(stockmarketService.lookUpPrice("GOOGL")).thenReturn(2800.0);

        portfolio.addStock(new Stock("AAPL", 2));  // 300
        portfolio.addStock(new Stock("GOOGL", 1)); // 2800

        List<Stock> topStocks = portfolio.mostValuableStocks(3);

        assertThat(topStocks.size(), is(2)); // Deve retornar todos os stocks
    }

    @Test
    void testMostValuableStocks_EmptyPortfolio() {
        List<Stock> topStocks = portfolio.mostValuableStocks(2);

        assertThat(topStocks.size(), is(0)); // Deve retornar uma lista vazia
    }

 }