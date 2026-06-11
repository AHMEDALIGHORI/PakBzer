package com.pakbzer.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A single line in the shopping cart. Plain POJO (not persisted) kept inside
 * the session-scoped {@link com.pakbzer.service.Cart}.
 */
public class CartItem implements Serializable {

    private Long productId;
    private String name;
    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Long productId, String name, String imageUrl, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** Line total = unit price * quantity. */
    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
