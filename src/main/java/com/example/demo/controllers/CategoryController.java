package com.example.demo.controllers;

import com.example.demo.dto.ApiResponse;
import com.example.demo.models.Category;
import com.example.demo.services.CategoryService;
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
@CrossOrigin(origins = {"http://localhost:3000", "https://wemovies-backend-b74e2422331f.herokuapp.com"})
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Category>>> categories() {
        try {
            List<Category> categories = categoryService.getAllCategory();
            if (categories.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(true, "No categories found", null), HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(new ApiResponse<>(true, "Categories retrieved successfully", categories), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving categories", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Category>> addCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(true, "Category added successfully", savedCategory));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while adding the category", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Category>> editCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        try {
            if (id != null && category.getId() != null && id.equals(category.getId())) {
                Category updatedCategory = categoryService.saveCategory(category);
                return ResponseEntity.ok(new ApiResponse<>(true, "Category updated successfully", updatedCategory));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "Category ID is missing or does not match the path variable", null), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while updating the category", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id) {
        try {
            categoryService.deleteMovieCategoryById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Category deleted successfully", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the category", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

