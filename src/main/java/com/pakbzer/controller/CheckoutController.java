package com.pakbzer.controller;

import com.pakbzer.service.Cart;
import com.pakbzer.service.StripeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    private final Cart cart;
    private final StripeService stripeService;

    public CheckoutController(Cart cart, StripeService stripeService) {
        this.cart = cart;
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public String checkout(RedirectAttributes ra) {
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

        if (!stripeService.isEnabled()) {
            // No Stripe keys configured yet -> run a demo (no real charge).
            ra.addFlashAttribute("demo", true);
            return "redirect:/checkout/success";
        }

        try {
            String url = stripeService.createCheckoutSession(cart.getItems());
            return "redirect:" + url;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Payment could not be started: " + ex.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/checkout/success")
    public String success(Model model) {
        model.addAttribute("itemCount", cart.getTotalItems());
        model.addAttribute("total", cart.getTotalPrice());
        cart.clear();
        return "checkout-success";
    }
}
