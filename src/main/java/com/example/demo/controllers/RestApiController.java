package com.example.demo.controllers;

import com.example.demo.dto.ApiResponse;
import com.example.demo.models.*;
import com.example.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://wemovies-backend-b74e2422331f.herokuapp.com"})
public class RestApiController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private MovieTypeSevice movieTypeSevice;

    @GetMapping("/movies/{movieId}/episodes")
    public ResponseEntity<ApiResponse<List<String>>> getEpisodeLinks(@PathVariable Long movieId) {
        try {
            List<String> episodeLinks = movieService.getEpisodeLinks(movieId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Episode links retrieved successfully", episodeLinks));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving episode links", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<Movie>> movieById(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie not found", null), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie retrieved successfully", movie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/category/{categoryName}")
    public ResponseEntity<ApiResponse<List<Movie>>> getMoviesByCategory(@PathVariable String categoryName) {
        try {
            List<Movie> movies = movieService.getMoviesByCategory(categoryName);
            if (movies.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the category", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/category/id/{categoryId}")
    public ResponseEntity<ApiResponse<List<Movie>>> getMoviesByCategoryId(@PathVariable Long categoryId) {
        try {
            List<Movie> movies = movieService.getMoviesByCategoryId(categoryId);
            if (movies.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the category ID", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/category/count/{categoryId}")
    public ResponseEntity<ApiResponse<Integer>> countMoviesByCategoryId(@PathVariable Long categoryId) {
        try {
            int count = categoryService.countMoviesByCategoryId(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie count retrieved successfully", count));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while counting movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/country/{countryName}/category/{categoryName}")
    public ResponseEntity<ApiResponse<List<Movie>>> getMoviesByCountryAndCategory(@PathVariable String countryName, @PathVariable String categoryName) {
        try {
            List<Movie> movies = movieService.findMoviesByCountryAndCategory(countryName, categoryName);
            if (movies.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movies found for the country and category", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/search")
    public ResponseEntity<ApiResponse<List<Movie>>> searchMovie(@RequestParam(required = false) String keyword) {
        try {
            List<Movie> movies = (keyword == null || keyword.trim().isEmpty()) ?
                    movieService.getAllMovies() : movieService.searchMovie(keyword);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while searching for movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/hot")
    public ResponseEntity<ApiResponse<List<Movie>>> getMovieByHot() {
        try {
            List<Movie> movies = movieService.getMovieByHot(true);
            if (movies.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No hot movies found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Hot movies retrieved successfully", movies));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving hot movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}