package com.example.demo.services;

import com.example.demo.models.Review;

import java.util.List;

public interface ReviewService {
    void addOrUpdateReview(String email, String movieId, Integer rating, String comment);
    void deleteReview(String email, String movieId);
    List<Review> getReviewsByMovie(String movieId);
    double getAverageRating(String movieId);
}