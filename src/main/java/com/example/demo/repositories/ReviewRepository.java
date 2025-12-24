package com.example.demo.repositories;

import com.example.demo.models.Review;
import com.example.demo.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByMovieId(UUID movieId);
    Optional<Review> findByUserAndMovie(User user, com.example.demo.models.Movie movie);
    double findAverageRatingByMovieId(UUID movieId);
}