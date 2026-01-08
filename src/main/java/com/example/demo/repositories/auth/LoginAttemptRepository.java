package com.example.demo.repositories.auth;

import com.example.demo.models.auth.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String> {

    Optional<LoginAttempt> findByIpAddressAndEmail(String ipAddress, String email);

    @Query("SELECT la FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.blocked = true AND la.blockedUntil > :now")
    Optional<LoginAttempt> findBlockedAttempt(@Param("ipAddress") String ipAddress, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM LoginAttempt la WHERE la.lastAttempt < :cutoffDate")
    void deleteOldAttempts(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.lastAttempt > :since")
    long countRecentAttempts(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}