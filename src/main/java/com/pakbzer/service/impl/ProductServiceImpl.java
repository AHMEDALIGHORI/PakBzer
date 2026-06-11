package com.pakbzer.service.impl;

import com.pakbzer.model.Category;
import com.pakbzer.model.Product;
import com.pakbzer.repository.ProductRepository;
import com.pakbzer.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> search(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        String q = query.trim();
        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase(q, q, q);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
