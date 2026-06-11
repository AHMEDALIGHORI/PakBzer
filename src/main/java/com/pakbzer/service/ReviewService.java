package com.pakbzer.service;

import com.pakbzer.model.Product;
import com.pakbzer.model.Review;
import com.pakbzer.repository.ProductRepository;
import com.pakbzer.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public List<Review> findForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Review addReview(Long productId, String authorName, int rating, String comment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        int safeRating = Math.max(1, Math.min(5, rating));
        Review review = new Review(product, authorName, safeRating, comment);
        return reviewRepository.save(review);
    }
}
