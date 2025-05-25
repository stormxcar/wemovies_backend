package com.example.demo.services;

import com.example.demo.dto.*;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    public AuthResponse login(LoginRequest loginRequest);
//    public AuthResponse register(RegisterRequest registerRequest);
    public UserDetails verifyToken(String token);
    public AuthResponse refreshToken(String refreshToken);

    AuthResponse verifyOtp(String email, String otp);
    public void requestOtp(RegisterRequest request);
    public void forgotPassword(String email);
    public void resetPassword(ResetPasswordRequest request);
    public void changePassword(String usernameOrEmail, ChangePasswordRequest request);

}
