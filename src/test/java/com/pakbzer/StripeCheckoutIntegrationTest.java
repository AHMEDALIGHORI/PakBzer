package com.pakbzer;

import com.pakbzer.model.CartItem;
import com.pakbzer.service.StripeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StripeCheckoutIntegrationTest {

    @Autowired
    private StripeService stripeService;

    @Test
    void stripeKeysAreConfigured() {
        assertTrue(stripeService.isEnabled(), "Stripe should be enabled when sk_test and pk_test are in .env");
    }

    @Test
    void createsCheckoutSessionWithPkr() throws Exception {
        CartItem item = new CartItem(1L, "Test Kurta", "http://example.com/img.jpg", BigDecimal.valueOf(4500), 1);
        String url = stripeService.createCheckoutSession(List.of(item));
        assertNotNull(url);
        assertTrue(url.startsWith("https://checkout.stripe.com/"), "Expected Stripe hosted checkout URL");
    }
}
