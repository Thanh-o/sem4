package org.example.testlogic.config;

import org.example.testlogic.model.Product;
import org.example.testlogic.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize sample products
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Laptop", "High-performance laptop with 16GB RAM", new BigDecimal("999.99"), 10));
            productRepository.save(new Product("Smartphone", "Latest model with 128GB storage", new BigDecimal("699.99"), 15));
            productRepository.save(new Product("Headphones", "Noise-cancelling wireless headphones", new BigDecimal("199.99"), 20));
            productRepository.save(new Product("Tablet", "10-inch tablet with retina display", new BigDecimal("499.99"), 8));
            productRepository.save(new Product("Smart Watch", "Fitness tracker with heart rate monitor", new BigDecimal("249.99"), 12));
            productRepository.save(new Product("Bluetooth Speaker", "Waterproof portable speaker", new BigDecimal("89.99"), 25));
        }
    }
}
