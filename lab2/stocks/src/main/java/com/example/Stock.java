package com.example;

public class Stock {
    private String label;
    private int quantity;

    public Stock(String label, int quantity) {
        this.label = label;
        this.quantity = quantity;
    }

    public String getLabel() {
        return label;
    }

    public int getQuantity() {
        return quantity;
    }
}