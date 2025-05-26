package com.example.demo.controllers;

import com.example.demo.models.MovieType;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/types")
public class MovieTypeController {
    @Autowired
    private MovieTypeSevice movieTypeSevice;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping()
    public ResponseEntity<List<MovieType>> movieTypes() {
        try {
            List<MovieType> movieTypes = movieTypeSevice.getAllMovieTypes();
            if (movieTypes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(movieTypes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<MovieType> addMovieType(@RequestBody MovieType category) {
        MovieType savedCategory = movieTypeSevice.saveMovieType(category);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MovieType> editMovieType(@PathVariable("id") Long id, @RequestBody MovieType category) {
        if (id != null && category.getId() != null && id.equals(category.getId())) {
            MovieType updatedCategory = movieTypeSevice.saveMovieType(category);
            return ResponseEntity.ok(updatedCategory);
        } else {
            throw new IllegalArgumentException("Category ID is missing or does not match the path variable.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovieType(@PathVariable("id") Long id) {
        movieTypeSevice.deleteMovieTypeById(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping()
//    public String listMovieType(Model model) {
//        List<MovieType> movieTypes = movieTypeSevice.getAllMovieTypes();
//        Map<Long, Integer> movieCounts = new HashMap<>();
//
//        for(MovieType movieType : movieTypes) {
//            int countMoviesByMovieTypeId = movieTypeSevice.countMoviesByMovieTypeId(movieType.getId());
//            movieCounts.put(movieType.getId(), countMoviesByMovieTypeId);
//        }
//
//        model.addAttribute("movieCounts", movieCounts);
//
//        model.addAttribute("countTypes", movieTypeSevice.countMovieTypes());
//        model.addAttribute("types", movieTypes);
//        model.addAttribute("type", new MovieType());
//        return "admin/types/list";
//    }
//    @PostMapping("/add")
//    public String addMovieType(@ModelAttribute("type") MovieType movieType) {
//        movieTypeSevice.saveMovieType(movieType);
//        return "redirect:/admin/types";
//    }
//    @GetMapping("/edit/{id}")
//    public String showEditMovieTypeForm(@PathVariable("id") Long id, Model model) {
//        MovieType movieType = movieTypeSevice.getMovieTypeById(id);
//        if (movieType != null) {
//            model.addAttribute("type", movieType);
//            return "admin/types/edit";
//        }
//        return "redirect:/admin/categories";
//    }
//    @PostMapping("/edit")
//    public String editMovieType(@ModelAttribute MovieType movieType) {
//        if (movieType.getId() != null) {
//            // Update existing category
//            movieTypeSevice.saveMovieType(movieType);
//        } else {
//            // Handle error if category ID is missing
//            throw new IllegalArgumentException("movieType ID is missing.");
//        }
//        return "redirect:/admin/types";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteMovieType(@PathVariable("id") Long id) {
//        movieTypeSevice.deleteMovieTypeById(id);
//        return "redirect:/admin/types";
//    }
}
