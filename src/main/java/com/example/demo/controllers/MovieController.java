package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.services.CategoryService;
import com.example.demo.services.CountryService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
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
    public ResponseEntity<List<Movie>> movies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            if (movies.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/addPage")
    public ResponseEntity<Object> addPage() {
        return ResponseEntity.ok().body(new Object() {
            public List<Movie> movies = movieService.getAllMovies();
            public long countMovie = movieService.countMovies();
            public List<Category> categories = categoryService.getAllCategory();
            public List<MovieType> types = movieTypeSevice.getAllMovieTypes();
            public List<Country> countries = countryService.getAllCountries();
        });
    }

    @PostMapping("/add")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie,
                                          @RequestParam("actors") String[] actors,
                                          @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                                          @RequestParam("countryId") Long countryId,
                                          @RequestParam("movieTypeIds") List<Long> movieTypeIds,
                                          @RequestParam("categoryIds") List<Long> categoryIds) {

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
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    @GetMapping("/admin/api/{id}")
    public ResponseEntity<MovieDto> editMovie(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        MovieDto movieDto = new MovieDto(movie);
        return ResponseEntity.ok(movieDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id,
                                             @RequestBody Movie movie,
                                             @RequestParam("countryId") Long countryId,
                                             @RequestParam(value = "movieTypeIds", required = false) List<Long> movieTypeIds,
                                             @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds) {

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
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Movie> detailMovie(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }
}