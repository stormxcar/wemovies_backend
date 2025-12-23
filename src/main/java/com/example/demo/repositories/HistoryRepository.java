package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.History;

import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {
    Optional<History> findByUserIdAndMovieId(String userId, String movieId);
}
