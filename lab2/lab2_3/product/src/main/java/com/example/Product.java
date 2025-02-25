package com.example;

import java.util.Optional;

public class Product {
    private int id;
    private String title;
    private double price;
    private String description;
    private String category;
    private String image;

    public Product(int id, String title, double price, String description, String category, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
}
