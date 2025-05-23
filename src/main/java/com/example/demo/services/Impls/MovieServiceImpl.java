/*
 * @ (#) MovieServiceImpl.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.services.Impls;

import com.example.demo.config.ResourceNotFoundException;
import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public Page<Movie> getMoviesByPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return movieRepository.findAll(pageable);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie updatedMovie) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
//        updateMovie().setTitle(updatedMovie.getTitle());
        movie.setTitle(updatedMovie.getTitle());
        movie.setTitleByLanguage(updatedMovie.getTitleByLanguage());
        movie.setStatus(updatedMovie.getStatus());
        movie.setDirector(updatedMovie.getDirector());
        movie.setDuration(updatedMovie.getDuration());
        movie.setDescription(updatedMovie.getDescription());
        movie.setRelease_year(updatedMovie.getRelease_year());
        movie.setTrailer(updatedMovie.getTrailer());
        movie.setQuality(updatedMovie.getQuality());
        movie.setVietSub(updatedMovie.isVietSub());
        movie.setLink(updatedMovie.getLink());
        movie.setViews(updatedMovie.getViews());
        movie.setHot(updatedMovie.isHot());
        movie.setActors(updatedMovie.getActors());
        movie.setTotalEpisodes(updatedMovie.getTotalEpisodes());
        movie.setEpisodeLinks(updatedMovie.getEpisodeLinks());

        movie.setThumb_url(updatedMovie.getThumb_url());
        movie.setCountry(updatedMovie.getCountry());
        movie.setMovieTypes(updatedMovie.getMovieTypes());
        movie.setMovieCategories(updatedMovie.getMovieCategories());

        // Cập nhật các thuộc tính khác
        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        movieRepository.deleteMovieById(id);
    }

    @Override
    public int countMovies() {
        return (int)movieRepository.count();
    }

    @Override
    public List<Movie> getMoviesByCategory(String categoryName) {
        return movieRepository.getMoviesByCategory(categoryName);
    }

    @Override
    public List<Movie> searchMovie(String keyword) {
        return movieRepository.searchMovie(keyword);
    }

    @Override
    public List<String> getEpisodeLinks(Long movieId) {
        Optional<Movie> movieOptional = movieRepository.findById(movieId);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            if (movie.getEpisodeLinks() != null) {
                return Arrays.asList(movie.getEpisodeLinks().split(","));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<Movie> getMovieByHot(boolean isHot) {
        return movieRepository.getMovieByHot(isHot);
    }

    @Override
    public List<Movie> getMoviesByCategoryId(Long categoryId) {
        return movieRepository.getMoviesByCategoryId(categoryId);
    }

    @Override
    public List<Movie> findMoviesByCountryAndCategory(String countryName, String categoryName) {
        return movieRepository.findMoviesByCountryAndCategory(countryName, categoryName);
    }
}
