package com.example;

import java.util.ArrayList;
import java.util.List;

public class StocksPortfolio {
    private IStockmarketService stockmarket;
    private List<Stock> stocks;

    public StocksPortfolio(IStockmarketService stockmarket) {
        this.stockmarket = stockmarket;
        this.stocks = new ArrayList<>();
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public double totalValue() {
        double total = 0.0;
        for (Stock stock : stocks) {
            total += stock.getQuantity() * stockmarket.lookUpPrice(stock.getLabel());
        }
        return total;
    }

    public List<Stock> mostValuableStocks(int topN) {
        // Listar todos os stocks ordenados por valor (quantidade * preço)
        stocks.sort((stock1, stock2) -> {
            double value1 = stock1.getQuantity() * stockmarket.lookUpPrice(stock1.getLabel());
            double value2 = stock2.getQuantity() * stockmarket.lookUpPrice(stock2.getLabel());
            return Double.compare(value2, value1);  // Ordena em ordem decrescente
        });
    
        // Retornar apenas os top N
        return stocks.subList(0, Math.min(topN, stocks.size()));
    }

}
