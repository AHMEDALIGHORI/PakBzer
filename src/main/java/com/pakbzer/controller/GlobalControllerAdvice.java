package com.pakbzer.controller;

import com.pakbzer.model.Category;
import com.pakbzer.service.Cart;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds values that every page needs (navbar categories + live cart count)
 * to the model of all controllers.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final Cart cart;

    public GlobalControllerAdvice(Cart cart) {
        this.cart = cart;
    }

    @ModelAttribute("navCategories")
    public Category[] navCategories() {
        return Category.values();
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cart.getTotalItems();
    }
}
