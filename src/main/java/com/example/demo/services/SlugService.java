package com.example.demo.services;

import com.example.demo.models.Category;
import com.example.demo.models.Movie;
import com.example.demo.models.MovieType;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.MovieTypeRepository;
import com.example.demo.utils.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlugService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MovieTypeRepository movieTypeRepository;

    /**
     * Generate and set slug for Movie
     */
    public void generateMovieSlug(Movie movie) {
        if (movie.getTitle() != null && !movie.getTitle().trim().isEmpty()) {
            String baseSlug = SlugUtil.generateSlug(movie.getTitle());
            String uniqueSlug = SlugUtil.generateUniqueSlug(baseSlug, movieRepository::existsBySlug);
            movie.setSlug(uniqueSlug);
        }
    }

    /**
     * Generate and set slug for Category
     */
    public void generateCategorySlug(Category category) {
        if (category.getName() != null && !category.getName().trim().isEmpty()) {
            String baseSlug = SlugUtil.generateSlug(category.getName());
            String uniqueSlug = SlugUtil.generateUniqueSlug(baseSlug, categoryRepository::existsBySlug);
            category.setSlug(uniqueSlug);
        }
    }

    /**
     * Generate and set slug for MovieType
     */
    public void generateMovieTypeSlug(MovieType movieType) {
        if (movieType.getName() != null && !movieType.getName().trim().isEmpty()) {
            String baseSlug = SlugUtil.generateSlug(movieType.getName());
            String uniqueSlug = SlugUtil.generateUniqueSlug(baseSlug, movieTypeRepository::existsBySlug);
            movieType.setSlug(uniqueSlug);
        }
    }
}