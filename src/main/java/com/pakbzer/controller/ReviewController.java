package com.pakbzer.controller;

import com.pakbzer.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review/add")
    public String add(@RequestParam Long productId,
                      @RequestParam int rating,
                      @RequestParam String comment,
                      Principal principal,
                      RedirectAttributes ra) {
        String author = principal != null ? principal.getName() : "Guest";
        reviewService.addReview(productId, author, rating, comment);
        ra.addFlashAttribute("message", "Thanks for your review!");
        return "redirect:/product/" + productId;
    }
}
