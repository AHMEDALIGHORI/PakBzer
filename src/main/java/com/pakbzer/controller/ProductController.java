package com.pakbzer.controller;

import com.pakbzer.model.Category;
import com.pakbzer.model.Product;
import com.pakbzer.service.ProductService;
import com.pakbzer.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @GetMapping("/category/{category}")
    public String byCategory(@PathVariable String category, Model model, RedirectAttributes ra) {
        Category resolved;
        try {
            resolved = Category.valueOf(category.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", "Unknown category: " + category);
            return "redirect:/";
        }
        model.addAttribute("category", resolved);
        model.addAttribute("products", productService.findByCategory(resolved));
        return "category";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            ra.addFlashAttribute("error", "Product not found.");
            return "redirect:/";
        }
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.findForProduct(id));
        return "product";
    }
}
