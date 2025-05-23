package com.example.demo.services;

import com.example.demo.models.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {
    Page<Movie> getMoviesByPage(int pageNumber, int pageSize);

    List<Movie> getAllMovies();
    Movie getMovieById(Long id);
    Movie saveMovie(Movie movie);
    Movie updateMovie(Long id, Movie updatedMovie);
    void deleteMovie(Long id);
    int countMovies();
    List<Movie> getMoviesByCategory(String categoryName);
    List<Movie> searchMovie(String keyword);

    List<String> getEpisodeLinks(Long movieId);
    List<Movie> getMovieByHot(boolean isHot);

    List<Movie> getMoviesByCategoryId(Long categoryId);

    List<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName);

}
