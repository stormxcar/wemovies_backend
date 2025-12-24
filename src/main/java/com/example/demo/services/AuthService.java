package com.example.demo.services;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.AuthResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.security.GeneralSecurityException;

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

    public void updateProfile(String email, UpdateProfileRequest request);

    public String uploadAvatar(String email, MultipartFile file);

    AuthResponse googleLogin(String idToken) throws GeneralSecurityException, IOException;

}
