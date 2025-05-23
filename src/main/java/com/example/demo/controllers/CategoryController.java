package com.example.demo.controllers;

import com.example.demo.models.Category;
import com.example.demo.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        Map<Long, Integer> movieCounts = new HashMap<>();

        for (Category category : categories) {
            int countMoviesByCategoryId = categoryService.countMoviesByCategoryId(category.getCategory_id());
            movieCounts.put(category.getCategory_id(), countMoviesByCategoryId);
        }

        model.addAttribute("movieCounts", movieCounts);
        model.addAttribute("categories", categories);
        model.addAttribute("countCategories", categoryService.countCategories());
        model.addAttribute("category", new Category()); // Đảm bảo thêm attribute này cho modal
        return "admin/categories/list";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }
    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoriesByIds(List.of(id)).get(0);
        if (category != null) {
            model.addAttribute("category", category);
            return "admin/categories/edit";
        }
        return "redirect:/admin/categories";
    }
    @PostMapping("/edit")
    public String editCategory(@ModelAttribute Category category) {
        if (category.getCategory_id() != null) {
            // Update existing category
            categoryService.saveCategory(category);
        } else {
            // Handle error if category ID is missing
            throw new IllegalArgumentException("Category ID is missing.");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteMovieCategoryById(id);
        return "redirect:/admin/categories";
    }
}

