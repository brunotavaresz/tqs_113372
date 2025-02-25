package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class ProductFinderService {
    private final ISimpleHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Optional<Product> findProductDetails(int id) {
        String url = "https://fakestoreapi.com/products/" + id;
        String jsonResponse = httpClient.doHttpGet(url);

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            if (jsonNode.has("id") && jsonNode.has("title")) {
                Product product = new Product(
                    jsonNode.get("id").asInt(),
                    jsonNode.get("title").asText(),
                    jsonNode.get("price").asDouble(),
                    jsonNode.get("description").asText(),
                    jsonNode.get("category").asText(),
                    jsonNode.get("image").asText()
                );
                return Optional.of(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
