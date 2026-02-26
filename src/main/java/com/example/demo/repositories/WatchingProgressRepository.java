package com.example.demo.repositories;

import com.example.demo.models.WatchingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchingProgressRepository extends JpaRepository<WatchingProgress, UUID> {
    
    /**
     * Tìm progress theo userId và movieId
     */
    Optional<WatchingProgress> findByUserIdAndMovieId(String userId, String movieId);
    
    /**
     * Lấy tất cả progress của user (chưa hoàn thành)
     */
    List<WatchingProgress> findByUserIdAndIsCompletedFalse(String userId);
    
    /**
     * Lấy tất cả progress của user
     */
    List<WatchingProgress> findByUserIdOrderByLastWatchedDesc(String userId);
    
    /**
     * Xóa progress theo userId và movieId
     */
    void deleteByUserIdAndMovieId(String userId, String movieId);
    
    /**
     * Tìm progress cũ để gửi reminder (chưa xem trong X ngày)
     */
    @Query("SELECT wp FROM WatchingProgress wp WHERE wp.lastWatched < :cutoffDate AND wp.isCompleted = false AND wp.percentage BETWEEN 10 AND 90")
    List<WatchingProgress> findStaleProgress(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Lấy danh sách userId active (có watching progress gần đây)
     */
    @Query("SELECT DISTINCT wp.userId FROM WatchingProgress wp WHERE wp.lastWatched >= :recentDate")
    List<String> findActiveUsers(@Param("recentDate") LocalDateTime recentDate);
    
    /**
     * Đếm số phim đang xem của user
     */
    @Query("SELECT COUNT(wp) FROM WatchingProgress wp WHERE wp.userId = :userId AND wp.isCompleted = false")
    Long countCurrentlyWatching(@Param("userId") String userId);
    
    /**
     * Đếm số phim đã hoàn thành của user
     */
    @Query("SELECT COUNT(wp) FROM WatchingProgress wp WHERE wp.userId = :userId AND wp.isCompleted = true")
    Long countCompleted(@Param("userId") String userId);
    
    /**
     * Tính tổng thời gian xem của user
     */
    @Query("SELECT COALESCE(SUM(wp.currentTime), 0) FROM WatchingProgress wp WHERE wp.userId = :userId")
    Long getTotalWatchTime(@Param("userId") String userId);
    
    /**
     * Xóa progress cũ (hoàn thành > 60 ngày)
     */
    @Query("DELETE FROM WatchingProgress wp WHERE wp.isCompleted = true AND wp.lastWatched < :cutoffDate")
    void deleteOldCompletedProgress(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Lấy top phim được xem nhiều nhất (theo số lượng user)
     */
    @Query("SELECT wp.movieId, wp.movieTitle, COUNT(DISTINCT wp.userId) as userCount " +
           "FROM WatchingProgress wp " +
           "WHERE wp.lastWatched >= :recentDate " +
           "GROUP BY wp.movieId, wp.movieTitle " +
           "ORDER BY userCount DESC")
    List<Object[]> findPopularMovies(@Param("recentDate") LocalDateTime recentDate);
}