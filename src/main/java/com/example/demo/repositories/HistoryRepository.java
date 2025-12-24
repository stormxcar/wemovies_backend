package com.example.demo.repositories;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.History;
import com.example.demo.models.Movie;

import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {
    Optional<History> findByUserAndMovie(com.example.demo.models.auth.User user, Movie movie);

    // Thống kê lượt xem phim theo period
    @Query("SELECT DATE(h.viewedAt) as date, COUNT(h) as views FROM History h WHERE h.movie.id = :movieId AND h.viewedAt >= :start GROUP BY DATE(h.viewedAt) ORDER BY DATE(h.viewedAt)")
    List<Object[]> getMovieViewsByPeriod(@Param("movieId") UUID movieId, @Param("start") LocalDateTime start);

    // Thống kê lượt xem tất cả phim
    @Query("SELECT DATE(h.viewedAt) as date, COUNT(h) as views FROM History h WHERE h.viewedAt >= :start GROUP BY DATE(h.viewedAt) ORDER BY DATE(h.viewedAt)")
    List<Object[]> getAllMovieViewsByPeriod(@Param("start") LocalDateTime start);

    // Thống kê lượt xem theo thể loại
    @Query("SELECT DATE(h.viewedAt) as date, COUNT(h) as views FROM History h JOIN h.movie.movieCategories c WHERE c.id = :categoryId AND h.viewedAt >= :start GROUP BY DATE(h.viewedAt) ORDER BY DATE(h.viewedAt)")
    List<Object[]> getCategoryViewsByPeriod(@Param("categoryId") UUID categoryId, @Param("start") LocalDateTime start);

    // Thống kê hoạt động người dùng
    @Query("SELECT DATE(h.viewedAt) as date, COUNT(DISTINCT h.user.id) as activeUsers, COUNT(h) as totalViews FROM History h WHERE h.viewedAt >= :start GROUP BY DATE(h.viewedAt) ORDER BY DATE(h.viewedAt)")
    List<Object[]> getUserActivityByPeriod(@Param("start") LocalDateTime start);

    // Tổng lượt xem trong khoảng thời gian
    @Query("SELECT COUNT(h) FROM History h WHERE h.viewedAt BETWEEN :start AND :end")
    long countViewsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
