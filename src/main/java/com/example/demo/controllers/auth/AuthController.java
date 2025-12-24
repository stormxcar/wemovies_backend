package com.example.demo.controllers.auth;

import com.example.demo.config.user.JwtUtil;
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.models.auth.GoogleLoginRequest;
import com.example.demo.models.auth.User;
import com.example.demo.repositories.auth.RoleRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.repositories.auth.VerificationTokenRepository;
import com.example.demo.services.AuthService;
import com.example.demo.services.EmailService;
import com.example.demo.services.Impls.CustomUserDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest, HttpServletResponse response) {
        try {

            AuthResponse authResponse = authService.googleLogin(googleLoginRequest.getIdToken());
            addJwtCookie(response, authResponse.getAccessToken(), "jwtToken");
            addJwtCookie(response, authResponse.getRefreshToken(), "refreshToken");
            return ResponseEntity.ok(authResponse);
        } catch (ExpiredJwtException e) {

            return ResponseEntity.status(401)
                    .body(new AuthResponse("Google ID Token expired: " + e.getMessage(), null));
        } catch (Exception e) {

            return ResponseEntity.status(401)
                    .body(new AuthResponse("Google login failed: " + e.getMessage(), null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);
        addJwtCookie(response, authResponse.getAccessToken(), "jwtToken");
        addJwtCookie(response, authResponse.getRefreshToken(), "refreshToken");
        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwtToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // Chỉ sử dụng nếu bạn dùng HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Xóa cookie

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(jwtCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/verifyUser")
    // @PreAuthorize("isAuthenticated()") // Temporarily disabled for debugging
    public ResponseEntity<?> verifyUser(Principal principal) {
        System.out.println("=== VERIFY USER DEBUG ===");
        System.out.println("Principal: " + principal);
        if (principal != null) {
            System.out.println("Principal name: " + principal.getName());
        }

        // Check SecurityContext
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SecurityContext Authentication: " + auth);
        if (auth != null) {
            System.out.println("Authentication name: " + auth.getName());
            System.out.println("Authentication authorities: " + auth.getAuthorities());
        }

        try {
            String email = null;
            if (principal != null && principal.getName() != null) {
                email = principal.getName();
                System.out.println("Using Principal name: " + email);
            } else if (auth != null && auth.getName() != null) {
                email = auth.getName();
                System.out.println("Using SecurityContext name: " + email);
            } else {
                System.out.println("No authentication found");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User found: " + user.getUserName());
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUserName());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().getRoleName());

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            System.out.println("RuntimeException: " + ex.getMessage());
            // Specific handling for user not found
            if (ex.getMessage().contains("User not found")) {
                return ResponseEntity.status(404).body("User not found");
            }
            return ResponseEntity.status(401).body("Authentication failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }


    // Note: This endpoint is in PUBLIC_ROUTES in SecurityConfig as it handles refresh tokens
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookies(request, "refreshToken");
        if (refreshToken != null) {
            try {
                AuthResponse authResponse = authService.refreshToken(refreshToken);
                addJwtCookie(response, authResponse.getAccessToken(), "jwtToken");
                addJwtCookie(response, authResponse.getRefreshToken(), "refreshToken"); // Optional: Update refresh token
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accessToken", authResponse.getAccessToken());
                responseBody.put("refreshToken", authResponse.getRefreshToken());
                responseBody.put("message", "Token đã được làm mới");
                return ResponseEntity.ok(responseBody);
            } catch (RuntimeException e) {
                return ResponseEntity.status(401).body("Refresh Token không hợp lệ hoặc đã hết hạn");
            }
        }
        return ResponseEntity.status(401).body("Refresh Token không tồn tại");
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@Valid @RequestBody RegisterRequest request) {
        authService.requestOtp(request);

        return ResponseEntity.ok("OTP đã được gửi đến email của bạn");
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        AuthResponse response = authService.verifyOtp(email, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP đã được gửi về email của bạn");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                 Principal principal) {
        authService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
        authService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok("Cập nhật hồ sơ thành công");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {
        String avatarUrl = authService.uploadAvatar(principal.getName(), file);
        return ResponseEntity.ok("Upload avatar thành công: " + avatarUrl);
    }


    private void addJwtCookie(HttpServletResponse response, String token, String cookieName) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Always use HTTPS in production
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours

        // Add the cookie to the response
        response.addCookie(cookie);

        // Manually set the SameSite attribute
        String sameSiteCookie = String.format("%s=%s; Path=%s; HttpOnly; Secure; Max-Age=%d; SameSite=Lax",
                cookieName, token, "/", 24 * 60 * 60);
        response.addHeader("Set-Cookie", sameSiteCookie);
    }

    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
