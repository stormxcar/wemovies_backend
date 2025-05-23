/*
 * @ (#) CategoryService.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */
package com.example.demo.services;

import com.example.demo.models.Category;
import java.util.List;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
public interface CategoryService {
    List<Category> getAllCategory();

    Category getCategoryById(Long id);
    List<Category> getCategoriesByIds(List<Long> ids);

    Category saveCategory(Category category);
    void deleteMovieCategoryById(Long id);

    int countMoviesByCategoryId(Long categoryId);
    int countCategories();
}
