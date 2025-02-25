package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductFinderServiceTest {

    @Test
    void findProductDetails_validId_returnsProduct() {
        ISimpleHttpClient httpClientMock = Mockito.mock(ISimpleHttpClient.class);
        ProductFinderService service = new ProductFinderService(httpClientMock);

        String jsonResponse = """
            {
                "id": 3,
                "title": "Mens Cotton Jacket",
                "price": 55.99,
                "description": "A great jacket",
                "category": "men's clothing",
                "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg"
            }
        """;

        when(httpClientMock.doHttpGet("https://fakestoreapi.com/products/3")).thenReturn(jsonResponse);

        Optional<Product> product = service.findProductDetails(3);

        assertTrue(product.isPresent());
        assertEquals(3, product.get().getId());
        assertEquals("Mens Cotton Jacket", product.get().getTitle());
    }

    @Test
    void findProductDetails_invalidId_returnsEmptyOptional() {
        ISimpleHttpClient httpClientMock = Mockito.mock(ISimpleHttpClient.class);
        ProductFinderService service = new ProductFinderService(httpClientMock);

        when(httpClientMock.doHttpGet("https://fakestoreapi.com/products/300")).thenReturn("{}");

        Optional<Product> product = service.findProductDetails(300);

        assertTrue(product.isEmpty());
    }
}
