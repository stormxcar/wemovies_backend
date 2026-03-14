package com.example.demo.services;

import com.example.demo.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    Page<Movie> getMoviesByPage(int pageNumber, int pageSize);
    Page<Movie> getAllMovies(Pageable pageable);

    List<Movie> getAllMovies();
    Movie getMovieById(UUID id);
    Movie getMovieBySlug(String slug);
    Movie saveMovie(Movie movie);
    Movie updateMovie(UUID id, Movie updatedMovie);
    void deleteMovie(UUID id);
    int countMovies();
    List<Movie> getMoviesByCategory(String categoryName);
    Page<Movie> getMoviesByCategory(String categoryName, Pageable pageable);
    List<Movie> searchMovie(String keyword);
    Page<Movie> searchMovie(String keyword, Pageable pageable);

    List<String> getEpisodeLinks(UUID movieId);
    List<Movie> getMovieByHot(boolean isHot);
    Page<Movie> getMovieByHot(boolean isHot, Pageable pageable);

    List<Movie> getMoviesByCategoryId(UUID categoryId);
    Page<Movie> getMoviesByCategoryId(UUID categoryId, Pageable pageable);
    List<Movie> getMoviesByCountryId(UUID countryId);
    Page<Movie> getMoviesByCountryId(UUID countryId, Pageable pageable);

    List<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName);
    Page<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName, Pageable pageable);
    
    List<Movie> getMoviesByMovieType(String movieTypeName);
    Page<Movie> getMoviesByMovieType(String movieTypeName, Pageable pageable);
    List<Movie> getMoviesByMovieTypeId(UUID movieTypeId);
    Page<Movie> getMoviesByMovieTypeId(UUID movieTypeId, Pageable pageable);

}
