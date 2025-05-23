/*
 * @ (#) MovieService.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.services;

import com.example.demo.models.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
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
