package com.example.demo.controllers;

import com.example.demo.dto.ApiResponse;
import com.example.demo.models.*;
import com.example.demo.services.CategoryService;
import com.example.demo.services.CountryService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://wemovies-backend-b74e2422331f.herokuapp.com"})
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MovieTypeSevice movieTypeSevice;

    @Autowired
    private CountryService countryService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Movie>>> movies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            if (movies.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(true, "No movies found", null), HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movies", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/addPage")
    public ResponseEntity<ApiResponse<Object>> addPage() {
        try {
            Object response = new Object() {
                public List<Movie> movies = movieService.getAllMovies();
                public long countMovie = movieService.countMovies();
                public List<Category> categories = categoryService.getAllCategory();
                public List<MovieType> types = movieTypeSevice.getAllMovieTypes();
                public List<Country> countries = countryService.getAllCountries();
            };
            return ResponseEntity.ok(new ApiResponse<>(true, "Page data retrieved successfully", response));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving page data", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Movie>> addMovie(@RequestBody Movie movie,
                                                       @RequestParam("actors") String[] actors,
                                                       @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                                                       @RequestParam("countryId") Long countryId,
                                                       @RequestParam("movieTypeIds") List<Long> movieTypeIds,
                                                       @RequestParam("categoryIds") List<Long> categoryIds) {
        try {
            Set<String> actorSet = new HashSet<>();
            for (String actor : actors) {
                if (!actor.trim().isEmpty()) {
                    actorSet.add(actor);
                }
            }

            if (movie.getTotalEpisodes() != null && episodeLinks != null && !episodeLinks.isEmpty()) {
                movie.setEpisodeLinks(String.join(",", episodeLinks));
                movie.setLink(null);
            } else {
                movie.setEpisodeLinks(null);
            }

            Country country = countryService.getCountryById(countryId);
            movie.setCountry(country);

            Set<MovieType> movieTypes = new HashSet<>();
            for (Long id : movieTypeIds) {
                MovieType movieType = movieTypeSevice.getMovieTypeById(id);
                movieTypes.add(movieType);
            }
            movie.setMovieTypes(movieTypes);

            Set<Category> categories = new HashSet<>();
            for (Long id : categoryIds) {
                Category category = categoryService.getCategoryById(id);
                categories.add(category);
            }
            movie.setMovieCategories(categories);

            movie.setActors(actorSet);
            Movie savedMovie = movieService.saveMovie(movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Movie added successfully", savedMovie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while adding the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/api/{id}")
    public ResponseEntity<ApiResponse<MovieDto>> editMovie(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            MovieDto movieDto = new MovieDto(movie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie retrieved successfully", movieDto));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Movie>> updateMovie(@PathVariable Long id,
                                                          @RequestBody Movie movie,
                                                          @RequestParam("countryId") Long countryId,
                                                          @RequestParam(value = "movieTypeIds", required = false) List<Long> movieTypeIds,
                                                          @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds) {
        try {
            Country country = countryService.getCountryById(countryId);
            movie.setCountry(country);

            if (movieTypeIds != null) {
                Set<MovieType> movieTypes = new HashSet<>();
                for (Long typeId : movieTypeIds) {
                    MovieType movieType = movieTypeSevice.getMovieTypeById(typeId);
                    movieTypes.add(movieType);
                }
                movie.setMovieTypes(movieTypes);
            }

            if (categoryIds != null) {
                Set<Category> categories = new HashSet<>();
                for (Long categoryId : categoryIds) {
                    Category category = categoryService.getCategoryById(categoryId);
                    categories.add(category);
                }
                movie.setMovieCategories(categories);
            }

            Movie updatedMovie = movieService.updateMovie(id, movie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie updated successfully", updatedMovie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while updating the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie deleted successfully", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the movie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Movie>> detailMovie(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie details retrieved successfully", movie));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movie details", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}