/*
 * @ (#) MovieRepository.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */
package com.example.demo.repositories;

import com.example.demo.models.Movie;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAll(Pageable pageable);
    List<Movie> findByTitle(String title);
    @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE c.name = :categoryName")
    List<Movie> getMoviesByCategory(@Param("categoryName") String categoryName);

    // delete film by id
    @Modifying
    @Transactional
    @Query("DELETE FROM Movie m WHERE m.movie_id = :id")
    void deleteMovieById(@Param("id") Long id);

    // search film by title , name actor ,...
    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:keyword%")
    List<Movie> searchMovie(@Param("keyword") String keyword);

    @Query("SELECT m FROM Movie m WHERE m.hot = :isHot")
    List<Movie> getMovieByHot(@Param("isHot") boolean isHot);


    @Query(value = "SELECT m.* FROM movie AS m " +
            "JOIN movie_category AS mc ON mc.movie_id = m.movie_id " +
            "JOIN category AS c ON c.category_id = mc.category_id " +
            "WHERE mc.category_id = :categoryId", nativeQuery = true)
    List<Movie> getMoviesByCategoryId(@Param("categoryId") Long categoryId);

    // MovieRepository.java
    @Query(value = "SELECT m.* FROM movie AS m " +
            "JOIN movie_category AS mc ON mc.movie_id = m.movie_id " +
            "JOIN category AS c ON c.category_id = mc.category_id " +
            "JOIN country AS co ON co.country_id = m.country_id " +
            "WHERE co.name = :countryName AND c.name = :categoryName", nativeQuery = true)
    List<Movie> findMoviesByCountryAndCategory(@Param("countryName") String countryName, @Param("categoryName") String categoryName);

}
