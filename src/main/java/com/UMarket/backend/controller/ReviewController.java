package com.UMarket.backend.controller;

import com.UMarket.backend.model.Review;
import com.UMarket.backend.service.ReviewService;
import com.UMarket.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    // POST /api/reviews?sellerId=2
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Review review,
            @RequestParam Long sellerId) {
        try {
            Long authorId = getAuthenticatedId();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(reviewService.create(review, authorId, sellerId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/reviews/seller/1
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Review>> findBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(reviewService.findBySeller(sellerId));
    }

    // GET /api/reviews/average/1
    @GetMapping("/average/{sellerId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long sellerId) {
        return ResponseEntity.ok(reviewService.getAverageRating(sellerId));
    }

    private Long getAuthenticatedId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }
}
