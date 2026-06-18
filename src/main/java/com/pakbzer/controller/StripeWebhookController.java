package com.pakbzer.controller;

import com.pakbzer.config.StripeProperties;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final StripeProperties stripeProperties;

    public StripeWebhookController(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {

        if (!stripeProperties.isWebhookConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Stripe webhook secret not configured");
        }
        if (signature == null || signature.isBlank()) {
            return ResponseEntity.badRequest().body("Missing Stripe-Signature header");
        }

        try {
            Event event = Webhook.constructEvent(
                    payload, signature, stripeProperties.getWebhookSecret());

            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);
                    if (session != null) {
                        log.info("Stripe checkout completed: sessionId={} customerEmail={}",
                                session.getId(), session.getCustomerDetails() != null
                                        ? session.getCustomerDetails().getEmail() : "n/a");
                    }
                }
                case "payment_intent.payment_failed" ->
                        log.warn("Stripe payment failed: {}", event.getId());
                default -> log.debug("Unhandled Stripe event: {}", event.getType());
            }

            return ResponseEntity.ok("ok");
        } catch (SignatureVerificationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }
}
