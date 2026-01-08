package com.example.demo.services;

import com.example.demo.models.auth.LoginAttempt;
import com.example.demo.repositories.auth.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class LoginAttemptService {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    /**
     * Kiểm tra xem IP có bị block không
     */
    public boolean isBlocked(String ipAddress) {
        return loginAttemptRepository.findBlockedAttempt(ipAddress, LocalDateTime.now()).isPresent();
    }

    /**
     * Lấy thời gian còn lại của block (phút)
     */
    public long getRemainingBlockTime(String ipAddress) {
        return loginAttemptRepository.findBlockedAttempt(ipAddress, LocalDateTime.now())
                .map(attempt -> {
                    long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), attempt.getBlockedUntil());
                    return Math.max(0, minutes);
                })
                .orElse(0L);
    }

    /**
     * Ghi nhận lần login thất bại
     */
    @Transactional
    public void recordFailedAttempt(String ipAddress, String email) {
        LoginAttempt attempt = loginAttemptRepository.findByIpAddressAndEmail(ipAddress, email)
                .orElse(LoginAttempt.builder()
                        .ipAddress(ipAddress)
                        .email(email)
                        .attempts(0)
                        .firstAttempt(LocalDateTime.now())
                        .blocked(false)
                        .build());

        attempt.setAttempts(attempt.getAttempts() + 1);
        attempt.setLastAttempt(LocalDateTime.now());

        // Nếu vượt quá số lần cho phép, block IP
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            attempt.setBlockedUntil(LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
            attempt.setBlocked(true);
        }

        loginAttemptRepository.save(attempt);
    }

    /**
     * Reset attempts khi login thành công
     */
    @Transactional
    public void resetAttempts(String ipAddress, String email) {
        loginAttemptRepository.findByIpAddressAndEmail(ipAddress, email)
                .ifPresent(attempt -> loginAttemptRepository.delete(attempt));
    }

    /**
     * Dọn dẹp các attempts cũ (có thể chạy định kỳ)
     */
    @Transactional
    public void cleanupOldAttempts() {
        // Xóa attempts cũ hơn 1 giờ
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        loginAttemptRepository.deleteOldAttempts(cutoff);
    }

    /**
     * Lấy thông tin attempt hiện tại
     */
    public LoginAttempt getCurrentAttempt(String ipAddress, String email) {
        return loginAttemptRepository.findByIpAddressAndEmail(ipAddress, email).orElse(null);
    }
}