package com.pakbzer.controller;

import com.pakbzer.model.Category;
import com.pakbzer.service.Cart;
import com.pakbzer.service.StripeService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds values that every page needs (navbar categories + live cart count)
 * to the model of all controllers.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final Cart cart;
    private final StripeService stripeService;

    public GlobalControllerAdvice(Cart cart, StripeService stripeService) {
        this.cart = cart;
        this.stripeService = stripeService;
    }

    @ModelAttribute("navCategories")
    public Category[] navCategories() {
        return Category.values();
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cart.getTotalItems();
    }

    @ModelAttribute("stripeEnabled")
    public boolean stripeEnabled() {
        return stripeService.isEnabled();
    }
}
