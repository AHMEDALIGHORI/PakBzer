package com.pakbzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {

    private String secretKey;
    private String publishableKey;
    private String currency = "pkr";

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isConfigured() {
        return looksLikeRealKey(secretKey, "sk_") && looksLikeRealKey(publishableKey, "pk_");
    }

    private boolean looksLikeRealKey(String key, String prefix) {
        if (key == null) {
            return false;
        }
        String normalized = key.trim();
        if (!normalized.startsWith(prefix)) {
            return false;
        }
        String lower = normalized.toLowerCase();
        return !lower.contains("your_") && !lower.contains("paste") && !lower.contains("example");
    }
}
