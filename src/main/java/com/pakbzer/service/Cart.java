package com.pakbzer.service;

import com.pakbzer.model.CartItem;
import com.pakbzer.model.Product;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Session-scoped shopping cart. Encapsulates all cart mutation logic so
 * controllers only ask the cart to "add", "update" or "remove".
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Cart implements Serializable {

    private final List<CartItem> items = new ArrayList<>();

    public synchronized void add(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProductId().equals(product.getId())) {
                item.increaseQuantity(quantity);
                return;
            }
        }
        items.add(new CartItem(product.getId(), product.getName(),
                product.getImageUrl(), product.getPrice(), quantity));
    }

    public synchronized void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            remove(productId);
            return;
        }
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public synchronized void remove(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public synchronized void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
