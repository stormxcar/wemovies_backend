/*
 * @ (#) CategoryServiceImpl.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.services.Impls;

import com.example.demo.models.Category;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll(); // Trả về danh sách Category
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null); // Trả về Category theo ID
    }

    @Override
    public List<Category> getCategoriesByIds(List<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category); // Lưu Category
    }

    @Override
    public void deleteMovieCategoryById(Long id) {
        categoryRepository.deleteById(id); // Xóa Category theo ID
    }

    @Override
    public int countMoviesByCategoryId(Long categoryId) {
        return categoryRepository.countMoviesByCategoryId(categoryId);
    }

    @Override
    public int countCategories() {
        return (int) categoryRepository.count();
    }
}
