package com.pakbzer.controller;

import com.pakbzer.config.DebugLog;
import com.pakbzer.service.Cart;
import com.pakbzer.service.EmailService;
import com.pakbzer.service.StripeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class CheckoutController {

    private final Cart cart;
    private final StripeService stripeService;
    private final EmailService emailService;

    public CheckoutController(Cart cart, StripeService stripeService, EmailService emailService) {
        this.cart = cart;
        this.stripeService = stripeService;
        this.emailService = emailService;
    }

    @PostMapping("/checkout")
    public String checkout(RedirectAttributes ra) {
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

        boolean stripeEnabled = stripeService.isEnabled();
        // #region agent log
        DebugLog.write("STRIPE-A", "CheckoutController:checkout", "Checkout started",
                "{\"stripeEnabled\":" + stripeEnabled + ",\"cartItems\":" + cart.getTotalItems() + "}");
        // #endregion

        if (!stripeEnabled) {
            // #region agent log
            DebugLog.write("STRIPE-A", "CheckoutController:checkout", "Demo checkout path",
                    "{\"reason\":\"stripeNotConfigured\"}");
            // #endregion
            ra.addFlashAttribute("demo", true);
            return "redirect:/checkout/success";
        }

        try {
            String url = stripeService.createCheckoutSession(cart.getItems());
            // #region agent log
            DebugLog.write("STRIPE-C", "CheckoutController:checkout", "Stripe session created",
                    "{\"redirectStarted\":true}");
            // #endregion
            return "redirect:" + url;
        } catch (Exception ex) {
            String msg = ex.getMessage() == null ? "unknown" : ex.getMessage();
            if (msg.length() > 200) {
                msg = msg.substring(0, 200);
            }
            // #region agent log
            DebugLog.write("STRIPE-C", "CheckoutController:checkout", "Stripe session failed",
                    "{\"errorType\":\"" + ex.getClass().getSimpleName()
                            + "\",\"errorMessage\":\"" + msg.replace("\"", "'").replace("\n", " ") + "\"}");
            // #endregion
            ra.addFlashAttribute("error", "Payment could not be started: " + ex.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/checkout/success")
    public String success(Model model) {
        int itemCount = cart.getTotalItems();
        BigDecimal total = cart.getTotalPrice();
        model.addAttribute("itemCount", itemCount);
        model.addAttribute("total", total);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            emailService.sendOrderConfirmation(
                    auth.getName(),
                    "Your PakBzer order is confirmed",
                    "Thank you for shopping at PakBzer!\n\n"
                            + "Items: " + itemCount + "\n"
                            + "Total: PKR " + total + "\n\n"
                            + "We hope you enjoy your purchase.");
        }

        cart.clear();
        return "checkout-success";
    }
}
