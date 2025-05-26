package com.example.demo.controllers;

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

//    @GetMapping
//    public String listCategories(Model model) {
//        List<Category> categories = categoryService.getAllCategory();
//        Map<Long, Integer> movieCounts = new HashMap<>();
//
//        for (Category category : categories) {
//            int countMoviesByCategoryId = categoryService.countMoviesByCategoryId(category.getCategory_id());
//            movieCounts.put(category.getCategory_id(), countMoviesByCategoryId);
//        }
//
//        model.addAttribute("movieCounts", movieCounts);
//        model.addAttribute("categories", categories);
//        model.addAttribute("countCategories", categoryService.countCategories());
//        model.addAttribute("category", new Category()); // Đảm bảo thêm attribute này cho modal
//        return "admin/categories/list";
//    }

    @GetMapping()
    public ResponseEntity<List<Category>> categories() {
        try {
            List<Category> categories = categoryService.getAllCategory();
            if (categories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> editCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        if (id != null && category.getId() != null && id.equals(category.getId())) {
            Category updatedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(updatedCategory);
        } else {
            throw new IllegalArgumentException("Category ID is missing or does not match the path variable.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteMovieCategoryById(id);
        return ResponseEntity.noContent().build();
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<Category> showEditCategoryForm(@PathVariable("id") Long id, Model model) {
//        Category category = categoryService.getCategoriesByIds(List.of(id)).get(0);
//        if (category == null) {
//            return ResponseEntity.notFound().build();
//        }
//        model.addAttribute("category", category);
//        return ResponseEntity.ok(category);
//    }


//    @PutMapping("/update/{id}")
//    public String editCategory(@ModelAttribute Category category) {
//        if (category.getCategory_id() != null) {
//            // Update existing category
//            categoryService.saveCategory(category);
//        } else {
//            // Handle error if category ID is missing
//            throw new IllegalArgumentException("Category ID is missing.");
//        }
//        return "redirect:/admin/categories";
//    }
////
//    @DeleteMapping("/{id}")
//    public String deleteCategory(@PathVariable("id") Long id) {
//        categoryService.deleteMovieCategoryById(id);
//        return "redirect:/admin/categories";
//    }
}

