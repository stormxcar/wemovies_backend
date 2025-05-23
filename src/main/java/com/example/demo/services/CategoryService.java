
package com.example.demo.services;

import com.example.demo.models.Category;
import java.util.List;


public interface CategoryService {
    List<Category> getAllCategory();

    Category getCategoryById(Long id);
    List<Category> getCategoriesByIds(List<Long> ids);

    Category saveCategory(Category category);
    void deleteMovieCategoryById(Long id);

    int countMoviesByCategoryId(Long categoryId);
    int countCategories();
}
