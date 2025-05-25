package com.example.demo.repositories.auth;

import com.example.demo.models.auth.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByEmail(String email);
    Optional<VerificationToken> findByOtp(String otp);
    Optional<VerificationToken> findByEmailAndOtp(String email, String otp);

    boolean existsByEmailAndExpiryDateAfter(String email, LocalDateTime now);
    List<VerificationToken> findAllByExpiryDateBefore(LocalDateTime now);
}
