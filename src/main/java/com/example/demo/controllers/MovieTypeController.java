package com.example.demo.controllers;

import com.example.demo.dto.ApiResponse;
import com.example.demo.models.MovieType;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.MovieTypeSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types")
public class MovieTypeController {
    @Autowired
    private MovieTypeSevice movieTypeSevice;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<MovieType>>> movieTypes() {
        try {
            List<MovieType> movieTypes = movieTypeSevice.getAllMovieTypes();
            if (movieTypes.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No movie types found", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie types retrieved successfully", movieTypes));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving movie types", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<MovieType>> addMovieType(@RequestBody MovieType category) {
        try {
            MovieType savedCategory = movieTypeSevice.saveMovieType(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Movie type added successfully", savedCategory));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while adding the movie type", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MovieType>> editMovieType(@PathVariable("id") Long id, @RequestBody MovieType category) {
        try {
            if (id != null && category.getId() != null && id.equals(category.getId())) {
                MovieType updatedCategory = movieTypeSevice.saveMovieType(category);
                return ResponseEntity.ok(new ApiResponse<>(true, "Movie type updated successfully", updatedCategory));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "Movie type ID is missing or does not match the path variable", null), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while updating the movie type", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovieType(@PathVariable("id") Long id) {
        try {
            movieTypeSevice.deleteMovieTypeById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie type deleted successfully", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the movie type", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}