//package com.example.demo.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.example.demo.models.History;
//import com.example.demo.repositories.HistoryRepository;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "https://wemovies-backend-b74e2422331f.herokuapp.com"})
//@RequestMapping("/api/history")
//public class HistoryController {
//
//    @Autowired
//    private HistoryRepository historyRepository;
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @PostMapping("/save")
//    public ResponseEntity<String> saveHistory(
//            @RequestParam(required = false) String userId,
//            @RequestParam(required = false) String deviceId,
//            @RequestParam String movieId,
//            @RequestParam double pauseTime) {
//        if (userId != null) {
//            // Lưu cho người dùng đăng nhập (MariaDB)
//            Optional<History> existingHistory = historyRepository.findByUserIdAndMovieId(userId, movieId);
//            History history;
//            if (existingHistory.isPresent()) {
//                history = existingHistory.get();
//                history.setPauseTime(pauseTime);
//                history.setLastWatched(LocalDateTime.now());
//            } else {
//                history = new History(userId, movieId, pauseTime);
//            }
//            historyRepository.save(history);
//            return ResponseEntity.ok("Lưu lịch sử thành công (MariaDB)");
//        } else if (deviceId != null) {
//            // Lưu cho người dùng không đăng nhập (Redis)
//            String key = "history:device:" + deviceId + ":" + movieId;
//            redisTemplate.opsForValue().set(key, pauseTime, 30, TimeUnit.DAYS); // Lưu 30 ngày
//            return ResponseEntity.ok("Lưu lịch sử thành công (Redis)");
//        } else {
//            return ResponseEntity.badRequest().body("Yêu cầu userId hoặc deviceId");
//        }
//    }
//
//    @GetMapping("/get")
//    public ResponseEntity<Double> getHistory(
//            @RequestParam(required = false) String userId,
//            @RequestParam(required = false) String deviceId,
//            @RequestParam String movieId) {
//        if (userId != null) {
//            // Lấy lịch sử cho người dùng đăng nhập (MariaDB)
//            Optional<History> history = historyRepository.findByUserIdAndMovieId(userId, movieId);
//            return ResponseEntity.ok(history.map(History::getPauseTime).orElse(0.0));
//        } else if (deviceId != null) {
//            // Lấy lịch sử cho người dùng không đăng nhập (Redis)
//            String key = "history:device:" + deviceId + ":" + movieId;
//            Double pauseTime = (Double) redisTemplate.opsForValue().get(key);
//            return ResponseEntity.ok(pauseTime != null ? pauseTime : 0.0);
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//}