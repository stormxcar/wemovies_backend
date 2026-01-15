package com.example.demo.services.Impls;

import com.example.demo.models.Movie;
import com.example.demo.models.Review;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void addOrUpdateReview(String email, String movieId, Integer rating, String comment) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("User email is required");
        }
        
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid movie ID format: " + movieId);
        }
        
        Movie movie = movieRepository.findById(movieUUID)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Optional<Review> existingReview = reviewRepository.findByUserAndMovie(user, movie);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(rating);
            review.setComment(comment);
            reviewRepository.save(review);
        } else {
            Review review = new Review();
            review.setUser(user);
            review.setMovie(movie);
            review.setRating(rating);
            review.setComment(comment);
            reviewRepository.save(review);
        }
    }

    @Override
    public void deleteReview(String email, String movieId) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("User email is required");
        }
        
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid movie ID format: " + movieId);
        }
        
        Movie movie = movieRepository.findById(movieUUID)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        Review review = reviewRepository.findByUserAndMovie(user, movie)
                .orElseThrow(() -> new RuntimeException("Review not found for this user and movie"));
        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getReviewsByMovie(String movieId) {
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid movie ID format: " + movieId);
        }
        return reviewRepository.findByMovieId(movieUUID);
    }

    @Override
    public double getAverageRating(String movieId) {
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid movie ID format: " + movieId);
        }
        List<Review> reviews = reviewRepository.findByMovieId(movieUUID);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}