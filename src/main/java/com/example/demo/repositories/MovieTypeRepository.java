package com.example.demo.repositories;

import com.example.demo.models.MovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieTypeRepository extends JpaRepository<MovieType, UUID> {
    // Find movie type by slug
    MovieType findBySlug(String slug);

    // Check if slug exists
    boolean existsBySlug(String slug);

    @Query(value = "SELECT COUNT(m.movie_id) FROM movie AS m\n" +
            "JOIN movie_genre AS mg ON mg.movie_id = m.movie_id\n" +
            "JOIN movie_type AS mt ON mt.movie_type_id = mg.movie_type_id\n" +
            "WHERE mg.movie_type_id = :movieTypeId" , nativeQuery = true)
    int countMoviesByMovieTypeId(@Param("movieTypeId") UUID movieTypeId);
}
