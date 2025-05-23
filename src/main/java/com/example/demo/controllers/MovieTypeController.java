package com.example.demo.controllers;

import com.example.demo.models.MovieType;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/types")
public class MovieTypeController {
    @Autowired
    private MovieTypeSevice movieTypeSevice;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping()
    public String listMovieType(Model model) {
        List<MovieType> movieTypes = movieTypeSevice.getAllMovieTypes();
        Map<Long, Integer> movieCounts = new HashMap<>();

        for(MovieType movieType : movieTypes) {
            int countMoviesByMovieTypeId = movieTypeSevice.countMoviesByMovieTypeId(movieType.getMovie_type_id());
            movieCounts.put(movieType.getMovie_type_id(), countMoviesByMovieTypeId);
        }

        model.addAttribute("movieCounts", movieCounts);

        model.addAttribute("countTypes", movieTypeSevice.countMovieTypes());
        model.addAttribute("types", movieTypes);
        model.addAttribute("type", new MovieType());
        return "admin/types/list";
    }
    @PostMapping("/add")
    public String addMovieType(@ModelAttribute("type") MovieType movieType) {
        movieTypeSevice.saveMovieType(movieType);
        return "redirect:/admin/types";
    }
    @GetMapping("/edit/{id}")
    public String showEditMovieTypeForm(@PathVariable("id") Long id, Model model) {
        MovieType movieType = movieTypeSevice.getMovieTypeById(id);
        if (movieType != null) {
            model.addAttribute("type", movieType);
            return "admin/types/edit";
        }
        return "redirect:/admin/categories";
    }
    @PostMapping("/edit")
    public String editMovieType(@ModelAttribute MovieType movieType) {
        if (movieType.getMovie_type_id() != null) {
            // Update existing category
            movieTypeSevice.saveMovieType(movieType);
        } else {
            // Handle error if category ID is missing
            throw new IllegalArgumentException("movieType ID is missing.");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/delete/{id}")
    public String deleteMovieType(@PathVariable("id") Long id) {
        movieTypeSevice.deleteMovieTypeById(id);
        return "redirect:/admin/types";
    }
}
