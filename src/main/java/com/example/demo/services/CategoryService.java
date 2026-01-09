
package com.example.demo.services;

import com.example.demo.models.Category;
import java.util.List;
import java.util.UUID;


public interface CategoryService {
    List<Category> getAllCategory();

    Category getCategoryById(UUID id);
    Category getCategoryBySlug(String slug);
    List<Category> getCategoriesByIds(List<UUID> ids);

    Category saveCategory(Category category);
    void deleteMovieCategoryById(UUID id);

    int countMoviesByCategoryId(UUID categoryId);
    int countCategories();
}
