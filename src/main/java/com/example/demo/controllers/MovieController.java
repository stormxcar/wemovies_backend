/*
 * @ (#) MovieController.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.services.CategoryService;
import com.example.demo.services.CountryService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MovieTypeSevice movieTypeSevice;

    @Autowired
    private CountryService countryService;

    @GetMapping
    public String listMovie(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Page<Movie> moviePage = movieService.getMoviesByPage(page, size);
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("movie", new Movie());
        model.addAttribute("countMovie", movieService.countMovies());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("types", movieTypeSevice.getAllMovieTypes());
        model.addAttribute("countries", countryService.getAllCountries());
        return "admin/movies/list";
    }

    @GetMapping("/addPage")
    public String addPage(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("movie", new Movie());
        model.addAttribute("countMovie", movieService.countMovies());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("types", movieTypeSevice.getAllMovieTypes());
        model.addAttribute("countries", countryService.getAllCountries());
        return "admin/movies/add";
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public String addMovie(@ModelAttribute("movie") Movie movie,
                           @RequestParam("actors") String[] actors,
                           @RequestParam(value = "episodeLinks", required = false) List<String> episodeLinks,
                           @RequestParam("countryId") Long countryId,
                           @RequestParam("movieTypeIds") List<Long> movieTypeIds,
                           @RequestParam("categoryIds") List<Long> categoryIds) {

        Set<String> actorSet = new HashSet<>();
        for(String actor : actors) {
            if(!actor.trim().isEmpty()){
                actorSet.add(actor);
            }
        }

        if (movie.getTotalEpisodes() != null && !episodeLinks.isEmpty()) {
            movie.setEpisodeLinks(String.join(",", episodeLinks));
            movie.setLink(null); // Clear the single link field
        } else {
            movie.setEpisodeLinks(null); // Clear the episode links field
        }

        // Lấy quốc gia theo ID
        Country country = countryService.getCountryById(countryId);
        movie.setCountry(country);

        // Lấy các thể loại theo ID
        Set<MovieType> movieTypes = new HashSet<>();
        for (Long id : movieTypeIds) {
            MovieType movieType = movieTypeSevice.getMovieTypeById(id);
            movieTypes.add(movieType);
        }
        movie.setMovieTypes(movieTypes);

        // Lấy các danh mục theo ID
        Set<Category> categories = new HashSet<>();
        for (Long id : categoryIds) {
            Category category = categoryService.getCategoryById(id);
            categories.add(category);
        }
        movie.setMovieCategories(categories);

        movie.setActors(actorSet);
        // Lưu bộ phim
        movieService.saveMovie(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/admin/api/{id}")
    @ResponseBody
    public ResponseEntity<MovieDto> editMovie(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        MovieDto movieDto = new MovieDto(movie);
        return ResponseEntity.ok(movieDto);
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("types", movieTypeSevice.getAllMovieTypes());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("countries", countryService.getAllCountries());
        return "admin/movies/update";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable Long id,
                              @ModelAttribute("movie") Movie movie,
                              @RequestParam("countryId") Long countryId,
                              @RequestParam(value = "movieTypeIds", required = false) List<Long> movieTypeIds,
                              @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds) {

        System.out.println("Received movie data: " + movie);

        // Lấy quốc gia theo ID
        Country country = countryService.getCountryById(countryId);
        movie.setCountry(country);

        // Lấy các thể loại theo ID nếu có
        if (movieTypeIds != null) {
            Set<MovieType> movieTypes = new HashSet<>();
            for (Long typeId : movieTypeIds) {
                MovieType movieType = movieTypeSevice.getMovieTypeById(typeId);
                movieTypes.add(movieType);
            }
            movie.setMovieTypes(movieTypes);
        }

        // Lấy các danh mục theo ID nếu có
        if (categoryIds != null) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : categoryIds) {
                Category category = categoryService.getCategoryById(categoryId);
                categories.add(category);
            }
            movie.setMovieCategories(categories);
        }

        // Cập nhật bộ phim
        movieService.updateMovie(id, movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }

    @GetMapping("/detail/{id}")
    public String detailMovie(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        return "admin/movies/detail";
    }
}
