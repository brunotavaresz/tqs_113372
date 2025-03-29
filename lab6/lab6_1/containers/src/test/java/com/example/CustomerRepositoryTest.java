package com.example;

import com.example.model.Customer;
import com.example.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        // Prepara dados de teste
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
    }

    @Test
    @Order(1)
    public void testInsertCustomer() {
        // Inserir o cliente no banco de dados
        customerRepository.save(customer);

        // Verificar se o cliente foi inserido
        assertNotNull(customer.getId());
    }

    @Test
    @Order(2)
    public void testRetrieveCustomer() {
        // Recuperar o cliente
        Customer retrievedCustomer = customerRepository.findById(customer.getId()).orElse(null);

        // Verificar se o cliente foi recuperado corretamente
        assertNotNull(retrievedCustomer);
        assertEquals("John Doe", retrievedCustomer.getName());
        assertEquals("john.doe@example.com", retrievedCustomer.getEmail());
    }

    @Test
    @Order(3)
    public void testUpdateCustomer() {
        // Atualizar o cliente
        customer.setEmail("new.email@example.com");
        customerRepository.save(customer);

        // Recuperar e verificar as mudanças
        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElse(null);
        assertNotNull(updatedCustomer);
        assertEquals("new.email@example.com", updatedCustomer.getEmail());
    }

    @Test
    @Order(4)
    public void testDeleteCustomer() {
        // Deletar o cliente
        customerRepository.delete(customer);

        // Verificar se o cliente foi deletado
        assertFalse(customerRepository.findById(customer.getId()).isPresent());
    }
}
