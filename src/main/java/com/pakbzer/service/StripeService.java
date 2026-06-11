package com.pakbzer.service;

import com.pakbzer.config.StripeProperties;
import com.pakbzer.model.CartItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Wraps the Stripe Checkout API. Builds a hosted checkout session from the
 * contents of the cart and returns the redirect URL.
 */
@Service
public class StripeService {

    private final StripeProperties properties;
    private final String baseUrl;

    public StripeService(StripeProperties properties,
                         @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.properties = properties;
        this.baseUrl = baseUrl;
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    /**
     * Creates a Stripe Checkout Session and returns the URL the browser should
     * be redirected to in order to pay.
     */
    public String createCheckoutSession(List<CartItem> items) throws StripeException {
        Stripe.apiKey = properties.getSecretKey();

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/checkout/success")
                .setCancelUrl(baseUrl + "/cart");

        for (CartItem item : items) {
            // Stripe expects the smallest currency unit (e.g. paisa for PKR).
            long unitAmount = item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(100)).longValueExact();

            builder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(properties.getCurrency())
                                            .setUnitAmount(unitAmount)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.getName())
                                                            .build())
                                            .build())
                            .build());
        }

        Session session = Session.create(builder.build());
        return session.getUrl();
    }
}
