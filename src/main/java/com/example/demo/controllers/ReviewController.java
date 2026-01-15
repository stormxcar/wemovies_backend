package com.example.demo.controllers;

import com.example.demo.models.Review;
import com.example.demo.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{movieId}/review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addOrUpdateReview(@PathVariable String movieId,
                                                    @RequestParam Integer rating,
                                                    @RequestParam(required = false) String comment,
                                                    Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                return ResponseEntity.status(401).body("Authentication required");
            }
            
            String email = principal.getName();
            if (email.trim().isEmpty()) {
                return ResponseEntity.status(401).body("Invalid authentication");
            }
            
            reviewService.addOrUpdateReview(email, movieId, rating, comment);
            return ResponseEntity.ok("Review added/updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{movieId}/review")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteReview(@PathVariable String movieId, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                return ResponseEntity.status(401).body("Authentication required");
            }
            
            String email = principal.getName();
            if (email.trim().isEmpty()) {
                return ResponseEntity.status(401).body("Invalid authentication");
            }
            
            reviewService.deleteReview(email, movieId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByMovie(@PathVariable String movieId) {
        try {
            List<Review> reviews = reviewService.getReviewsByMovie(movieId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{movieId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable String movieId) {
        try {
            double average = reviewService.getAverageRating(movieId);
            return ResponseEntity.ok(average);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}