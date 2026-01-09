package com.example.demo.repositories;

import com.example.demo.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // Find category by slug
    Category findBySlug(String slug);

    // Check if slug exists
    boolean existsBySlug(String slug);

    // lâấy lượng phim theo id
    @Query(value = "SELECT COUNT(m.movie_id) FROM movie AS m " +
            "JOIN movie_category AS mc ON mc.movie_id = m.movie_id " +
            "JOIN category AS c ON c.category_id = mc.category_id " +
            "WHERE mc.category_id = :categoryId", nativeQuery = true)
    int countMoviesByCategoryId(@Param("categoryId") UUID categoryId);

}
