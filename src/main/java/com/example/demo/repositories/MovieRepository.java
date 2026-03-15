package com.example.demo.repositories;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.models.Movie;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Page<Movie> findAll(Pageable pageable);

    List<Movie> findByTitle(String title);

    // Find movie by slug
    Movie findBySlug(String slug);

    // Check if slug exists
    boolean existsBySlug(String slug);

    @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE c.name = :categoryName")
    List<Movie> getMoviesByCategory(@Param("categoryName") String categoryName);

        @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE c.name = :categoryName")
        Page<Movie> getMoviesByCategory(@Param("categoryName") String categoryName, Pageable pageable);

    // delete film by id
    @Modifying
    @Transactional
    @Query("DELETE FROM Movie m WHERE m.id = :id")
    void deleteMovieById(@Param("id") UUID id);

    // search film by title , name actor ,...
    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:keyword%")
    List<Movie> searchMovie(@Param("keyword") String keyword);

        @Query("SELECT m FROM Movie m WHERE m.title LIKE %:keyword%")
        Page<Movie> searchMovie(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.hot = :isHot")
    List<Movie> getMovieByHot(@Param("isHot") boolean isHot);

        @Query("SELECT m FROM Movie m WHERE m.hot = :isHot")
        Page<Movie> getMovieByHot(@Param("isHot") boolean isHot, Pageable pageable);


    @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE c.id = :categoryId")
    List<Movie> getMoviesByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE c.id = :categoryId")
    Page<Movie> getMoviesByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.country.id = :countryId")
    List<Movie> getMoviesByCountryId(@Param("countryId") UUID countryId);

    @Query("SELECT m FROM Movie m WHERE m.country.id = :countryId")
    Page<Movie> getMoviesByCountryId(@Param("countryId") UUID countryId, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE m.country.name = :countryName AND c.name = :categoryName")
    List<Movie> findMoviesByCountryAndCategory(@Param("countryName") String countryName, @Param("categoryName") String categoryName);

    @Query("SELECT m FROM Movie m JOIN m.movieCategories c WHERE m.country.name = :countryName AND c.name = :categoryName")
    Page<Movie> findMoviesByCountryAndCategory(@Param("countryName") String countryName,
                                               @Param("categoryName") String categoryName,
                                               Pageable pageable);
    
    @Query("SELECT m FROM Movie m JOIN m.movieTypes mt WHERE mt.name = :movieTypeName")
    List<Movie> getMoviesByMovieType(@Param("movieTypeName") String movieTypeName);

        @Query("SELECT m FROM Movie m JOIN m.movieTypes mt WHERE mt.name = :movieTypeName")
        Page<Movie> getMoviesByMovieType(@Param("movieTypeName") String movieTypeName, Pageable pageable);
    
    @Query("SELECT m FROM Movie m JOIN m.movieTypes mt WHERE mt.id = :movieTypeId")
    List<Movie> getMoviesByMovieTypeId(@Param("movieTypeId") UUID movieTypeId);

        @Query("SELECT m FROM Movie m JOIN m.movieTypes mt WHERE mt.id = :movieTypeId")
        Page<Movie> getMoviesByMovieTypeId(@Param("movieTypeId") UUID movieTypeId, Pageable pageable);

}
