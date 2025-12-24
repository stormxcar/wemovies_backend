package com.example.demo.repositories;

import com.example.demo.models.Watchlist;
import com.example.demo.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, UUID> {
    List<Watchlist> findByUser(User user);
    Optional<Watchlist> findByUserAndMovie(User user, com.example.demo.models.Movie movie);
}