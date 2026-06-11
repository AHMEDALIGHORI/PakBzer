package com.pakbzer.service;

import com.pakbzer.model.Category;
import com.pakbzer.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Catalog contract. Programming against this interface (not the concrete class)
 * lets us swap implementations freely — classic OOP polymorphism / abstraction.
 */
public interface ProductService {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    List<Product> findByCategory(Category category);

    List<Product> search(String query);

    Product save(Product product);
}
