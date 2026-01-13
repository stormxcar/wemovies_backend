package com.example.demo.repositories;

import com.example.demo.models.Notification;
import com.example.demo.models.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    // Lấy thông báo của user theo thứ tự mới nhất
    Page<Notification> findByUserOrderBySentAtDesc(User user, Pageable pageable);
    
    // Lấy thông báo chưa đọc của user
    List<Notification> findByUserAndIsReadFalseOrderBySentAtDesc(User user);
    
    // Đếm số thông báo chưa đọc
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    long countUnreadByUser(@Param("user") User user);
    
    // Đánh dấu tất cả thông báo đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user, @Param("readAt") LocalDateTime readAt);
    
    // Xóa thông báo cũ (> 30 ngày)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.sentAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Lấy thông báo theo loại
    List<Notification> findByUserAndTypeOrderBySentAtDesc(User user, Notification.NotificationType type);
    
    // Lấy thông báo trong khoảng thời gian
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.sentAt BETWEEN :startDate AND :endDate ORDER BY n.sentAt DESC")
    List<Notification> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}