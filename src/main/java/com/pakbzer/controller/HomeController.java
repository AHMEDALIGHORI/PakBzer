package com.pakbzer.controller;

import com.pakbzer.model.Category;
import com.pakbzer.model.Product;
import com.pakbzer.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Build a small preview (first 4 items) for each category section.
        Map<Category, List<Product>> sections = new LinkedHashMap<>();
        for (Category category : Category.values()) {
            List<Product> preview = productService.findByCategory(category).stream()
                    .limit(4)
                    .collect(Collectors.toList());
            sections.put(category, preview);
        }
        model.addAttribute("sections", sections);
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("query", q);
        model.addAttribute("results", productService.search(q));
        return "search";
    }
}
