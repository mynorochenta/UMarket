package com.UMarket.backend.service;

import com.UMarket.backend.model.Review;
import com.UMarket.backend.model.User;
import com.UMarket.backend.repository.ReviewRepository;
import com.UMarket.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    public Review create(Review review, Long authorId, Long sellerId) {
        if (authorId.equals(sellerId)) {
            throw new RuntimeException("You cannot review yourself");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        review.setAuthor(author);
        review.setSeller(seller);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public List<Review> findBySeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return reviewRepository.findBySeller(seller);
    }

    public Double getAverageRating(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        Double average = reviewRepository.calculateAverageRating(seller);
        return average != null ? average : 0.0;
    }
}
