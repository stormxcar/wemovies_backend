package com.example.demo.services.Impls;

import com.example.demo.models.Category;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll(); // Trả về danh sách Category
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElse(null); // Trả về Category theo ID
    }

    @Override
    public List<Category> getCategoriesByIds(List<UUID> ids) {
        return categoryRepository.findAllById(ids);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category); // Lưu Category
    }

    @Override
    public void deleteMovieCategoryById(UUID id) {
        categoryRepository.deleteById(id); // Xóa Category theo ID
    }

    @Override
    public int countMoviesByCategoryId(UUID categoryId) {
        return categoryRepository.countMoviesByCategoryId(categoryId);
    }

    @Override
    public int countCategories() {
        return (int) categoryRepository.count();
    }
}
