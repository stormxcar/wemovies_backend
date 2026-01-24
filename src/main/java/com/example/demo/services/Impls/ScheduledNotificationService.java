package com.example.demo.services.Impls;

import com.example.demo.models.Movie;
import com.example.demo.models.Notification;
import com.example.demo.models.Watchlist;
import com.example.demo.repositories.WatchlistRepository;
import com.example.demo.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service cho việc gửi notification tự động theo lịch trình
 */
@Service
public class ScheduledNotificationService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Nhắc nhở xem phim trong watchlist - chạy mỗi ngày 8 PM
     */
    @Scheduled(cron = "0 0 20 * * *")
    public void sendWatchlistReminders() {
        try {
            List<Watchlist> oldWatchlistItems = watchlistRepository.findOldWatchlistItems(LocalDateTime.now().minusDays(3));
            
            for (Watchlist item : oldWatchlistItems) {
                String userEmail = item.getUser().getEmail();
                Movie movie = item.getMovie();
                
                notificationService.sendRealTimeNotification(
                    userEmail,
                    Notification.NotificationType.WATCHLIST_REMINDER,
                    "📽️ Nhắc nhở xem phim",
                    "Bạn đã thêm '" + movie.getTitle() + "' vào danh sách xem từ " + 
                    item.getAddedAt().toLocalDate() + ". Đến lúc thưởng thức rồi!",
                    "/watchlist", // actionUrl
                    movie, // relatedMovie
                    new HashMap<>() // metadata
                );
            }
            
            System.out.println("✅ Sent " + oldWatchlistItems.size() + " watchlist reminder notifications");
        } catch (Exception e) {
            System.err.println("❌ Error sending watchlist reminders: " + e.getMessage());
        }
    }

    /**
     * Nhắc nhở xem tiếp phim dở dang - chạy mỗi tối 7 PM
     */
    @Scheduled(cron = "0 0 19 * * *")
    public void sendContinueWatchingReminders() {
        try {
            Set<String> userKeys = redisTemplate.keys("watching_list:*");
            int sentCount = 0;
            
            if (userKeys != null) {
                for (String listKey : userKeys) {
                    String userId = listKey.replace("watching_list:", "");
                    Set<Object> movieIds = redisTemplate.opsForSet().members(listKey);
                    
                    if (movieIds != null && !movieIds.isEmpty()) {
                        for (Object movieId : movieIds) {
                            String detailKey = "watching_detail:" + userId + ":" + movieId;
                            Map<String, Object> detail = (Map<String, Object>) redisTemplate.opsForValue().get(detailKey);
                            
                            if (detail != null) {
                                String lastWatchedStr = (String) detail.get("lastWatched");
                                Double percentage = (Double) detail.get("percentage");
                                
                                // Nhắc nhở nếu xem được 10-90% và không xem trong 2 ngày
                                if (percentage != null && percentage >= 10 && percentage < 90) {
                                    if (lastWatchedStr != null) {
                                        LocalDateTime lastWatched = LocalDateTime.parse(lastWatchedStr);
                                        if (lastWatched.isBefore(LocalDateTime.now().minusDays(2))) {
                                            String movieTitle = (String) detail.get("movieTitle");
                                            
                                            notificationService.sendRealTimeNotification(
                                                userId,
                                                Notification.NotificationType.WATCH_PROGRESS,
                                                "⏸️ Phim dở dang",
                                                "Bạn đã xem " + String.format("%.1f", percentage) + "% phim '" + 
                                                movieTitle + "'. Tiếp tục xem để biết kết thúc nhé!",
                                                "/watching/" + movieId, // actionUrl
                                                null, // relatedMovie
                                                new HashMap<>() // metadata
                                            );
                                            sentCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            System.out.println("✅ Sent " + sentCount + " continue watching reminder notifications");
        } catch (Exception e) {
            System.err.println("❌ Error sending continue watching reminders: " + e.getMessage());
        }
    }

    /**
     * Weekly digest phim hot - chạy mỗi Chủ nhật 6 PM
     */
    @Scheduled(cron = "0 0 18 * * SUN")
    public void sendWeeklyDigest() {
        try {
            notificationService.sendBroadcastToAllUsers(
                Notification.NotificationType.WEEKLY_DIGEST,
                "📊 Phim hot tuần này",
                "Khám phá những bộ phim được yêu thích nhất tuần này! " +
                "Cập nhật xu hướng và những tác phẩm không thể bỏ lỡ.",
                "/movies/trending", // actionUrl
                null, // relatedMovie
                new HashMap<>() // metadata
            );
            
            System.out.println("✅ Sent weekly digest to all users");
        } catch (Exception e) {
            System.err.println("❌ Error sending weekly digest: " + e.getMessage());
        }
    }

    /**
     * Maintenance notification - khi cần thiết
     */
    public void sendMaintenanceNotification(String message, LocalDateTime scheduledTime) {
        try {
            notificationService.sendBroadcastToAllUsers(
                Notification.NotificationType.MAINTENANCE,
                "⚠️ Thông báo bảo trì hệ thống",
                "Hệ thống sẽ tạm ngưng hoạt động vào " + scheduledTime.toLocalDate() + 
                " lúc " + scheduledTime.toLocalTime() + " để bảo trì. " + message,
                "/maintenance", // actionUrl  
                null, // relatedMovie
                new HashMap<>() // metadata
            );
            
            System.out.println("✅ Sent maintenance notification to all users");
        } catch (Exception e) {
            System.err.println("❌ Error sending maintenance notification: " + e.getMessage());
        }
    }
}