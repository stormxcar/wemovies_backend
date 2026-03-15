package com.example.demo.services.Impls;

import com.example.demo.models.Movie;
import com.example.demo.models.Notification;
import com.example.demo.models.Review;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.services.NotificationService;
import com.example.demo.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

        Optional<Review> existingReview = reviewRepository.findByUserAndMovieAndParentReviewIsNull(user, movie);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(rating);
            review.setComment(comment);
            Review saved = reviewRepository.save(review);
            publishReviewEvent("review_updated", saved);
        } else {
            Review review = new Review();
            review.setUser(user);
            review.setMovie(movie);
            review.setRating(rating);
            review.setComment(comment);
            Review saved = reviewRepository.save(review);
            publishReviewEvent("review_added", saved);
        }
    }

    @Override
    public void replyToReview(String email, String parentReviewId, String comment) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("User email is required");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Reply comment is required");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        UUID parentId;
        try {
            parentId = UUID.fromString(parentReviewId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid parent review ID format: " + parentReviewId);
        }

        Review parentReview = reviewRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent review not found"));

        // Only allow one-level reply
        if (parentReview.getParentReview() != null) {
            throw new RuntimeException("Only one-level reply is supported");
        }

        Review reply = new Review();
        reply.setUser(user);
        reply.setMovie(parentReview.getMovie());
        reply.setRating(parentReview.getRating());
        reply.setComment(comment.trim());
        reply.setParentReview(parentReview);
        Review savedReply = reviewRepository.save(reply);

        // Realtime event for all clients that subscribed to this movie review topic
        publishReviewEvent("review_reply_added", savedReply);

        // Notify owner of parent review immediately if another user replied
        String parentOwnerId = parentReview.getUser().getId().toString();
        String replierId = user.getId().toString();
        if (!parentOwnerId.equals(replierId)) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("movieId", parentReview.getMovie().getId().toString());
            metadata.put("parentReviewId", parentReview.getId().toString());
            metadata.put("replyReviewId", savedReply.getId().toString());
            metadata.put("replierName", user.getUserName());

            notificationService.sendRealTimeNotification(
                    parentOwnerId,
                    Notification.NotificationType.REVIEW_REPLY,
                    "💬 Co nguoi da tra loi binh luan cua ban",
                    user.getUserName() + " vua phan hoi binh luan cua ban",
                    "/movies/" + parentReview.getMovie().getId(),
                    parentReview.getMovie(),
                    metadata
            );
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

        Review review = reviewRepository.findByUserAndMovieAndParentReviewIsNull(user, movie)
                .orElseThrow(() -> new RuntimeException("Review not found for this user and movie"));
        publishReviewDeletedEvent(review);
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
        return reviewRepository.findByMovieIdAndParentReviewIsNullOrderByCreatedAtDesc(movieUUID);
    }

    @Override
    public double getAverageRating(String movieId) {
        UUID movieUUID;
        try {
            movieUUID = UUID.fromString(movieId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid movie ID format: " + movieId);
        }
        List<Review> reviews = reviewRepository.findByMovieIdAndParentReviewIsNullOrderByCreatedAtDesc(movieUUID);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    private void publishReviewEvent(String eventType, Review review) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("reviewId", review.getId().toString());
            event.put("movieId", review.getMovie().getId().toString());
            event.put("parentReviewId", review.getParentReview() != null ? review.getParentReview().getId().toString() : null);
            event.put("comment", review.getComment());
            event.put("rating", review.getRating());
            event.put("userName", review.getUser().getUserName());
            event.put("createdAt", review.getCreatedAt() != null ? review.getCreatedAt().toString() : null);

            messagingTemplate.convertAndSend("/topic/reviews/" + review.getMovie().getId(), event);
        } catch (Exception e) {
            System.err.println("Failed to publish review event: " + e.getMessage());
        }
    }

    private void publishReviewDeletedEvent(Review review) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "review_deleted");
            event.put("reviewId", review.getId().toString());
            event.put("movieId", review.getMovie().getId().toString());
            event.put("parentReviewId", null);

            messagingTemplate.convertAndSend("/topic/reviews/" + review.getMovie().getId(), event);
        } catch (Exception e) {
            System.err.println("Failed to publish review delete event: " + e.getMessage());
        }
    }
}