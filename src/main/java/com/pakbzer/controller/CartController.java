package com.pakbzer.controller;

import com.pakbzer.model.Product;
import com.pakbzer.service.Cart;
import com.pakbzer.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final Cart cart;
    private final ProductService productService;

    public CartController(Cart cart, ProductService productService) {
        this.cart = cart;
        this.productService = productService;
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes ra) {
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            ra.addFlashAttribute("error", "Product not found.");
            return "redirect:/";
        }
        cart.add(product, Math.max(1, quantity));
        ra.addFlashAttribute("message", product.getName() + " added to your cart.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String update(@RequestParam Long productId, @RequestParam int quantity) {
        cart.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String remove(@RequestParam Long productId) {
        cart.remove(productId);
        return "redirect:/cart";
    }
}
