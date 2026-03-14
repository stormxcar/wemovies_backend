package com.example.demo.repositories;

import com.example.demo.models.Review;
import com.example.demo.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByMovieId(UUID movieId);
    List<Review> findByMovieIdAndParentReviewIsNullOrderByCreatedAtDesc(UUID movieId);
    @Modifying
    @Transactional
    void deleteByMovieId(UUID movieId);
    Optional<Review> findByUserAndMovie(User user, com.example.demo.models.Movie movie);
    Optional<Review> findByUserAndMovieAndParentReviewIsNull(User user, com.example.demo.models.Movie movie);
    double findAverageRatingByMovieId(UUID movieId);
}