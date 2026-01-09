package com.example.demo.services;

import com.example.demo.models.Movie;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    Page<Movie> getMoviesByPage(int pageNumber, int pageSize);

    List<Movie> getAllMovies();
    Movie getMovieById(UUID id);
    Movie getMovieBySlug(String slug);
    Movie saveMovie(Movie movie);
    Movie updateMovie(UUID id, Movie updatedMovie);
    void deleteMovie(UUID id);
    int countMovies();
    List<Movie> getMoviesByCategory(String categoryName);
    List<Movie> searchMovie(String keyword);

    List<String> getEpisodeLinks(UUID movieId);
    List<Movie> getMovieByHot(boolean isHot);

    List<Movie> getMoviesByCategoryId(UUID categoryId);
    List<Movie> getMoviesByCountryId(UUID countryId);

    List<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName);

}
