package com.example.demo.models.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime firstAttempt;

    @Column
    private LocalDateTime lastAttempt;

    @Column
    private LocalDateTime blockedUntil;

    @Column(nullable = false)
    private boolean blocked = false;
}