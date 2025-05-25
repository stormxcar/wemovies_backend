package com.example.demo.scheduler;

import com.example.demo.models.auth.VerificationToken;
import com.example.demo.repositories.auth.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OtpCleanupScheduler {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Scheduled(fixedRate = 60000) // mỗi 1 phút
    public void cleanExpiredOtps() {
        List<VerificationToken> expired = tokenRepository.findAllByExpiryDateBefore(LocalDateTime.now());
        tokenRepository.deleteAll(expired);
    }
}
