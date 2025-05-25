package com.example.demo.models.auth;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Data
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mã OTP
    private String otp;

    // Hạn sử dụng OTP
    private LocalDateTime expiryDate;

    // Các trường tạm để lưu thông tin đăng ký trước khi xác thực
    private String tempUserName;

    private String tempPassword;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String roleName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String userData; // JSON chuỗi của RegisterRequest
    // Constructors
    public VerificationToken() {}

    public VerificationToken(String otp, LocalDateTime expiryDate) {
        this.otp = otp;
        this.expiryDate = expiryDate;
    }
}