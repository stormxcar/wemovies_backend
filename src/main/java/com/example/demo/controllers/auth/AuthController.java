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
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.googleLogin(googleLoginRequest.getIdToken());

            // Check if user has consented to necessary cookies
            boolean hasCookieConsent = hasNecessaryCookieConsent(request);

            if (hasCookieConsent) {
                addJwtCookie(response, authResponse.getAccessToken(), "jwtToken");
                addJwtCookie(response, authResponse.getRefreshToken(), "refreshToken");
            } else {
                authResponse.setMessage("Google login successful. Accept cookies for persistent session.");
            }

            return ResponseEntity.ok(authResponse);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse("Google ID Token expired: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse("Google login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/google/debug")
    public ResponseEntity<?> debugGoogleConfig() {
        Map<String, String> info = new HashMap<>();
        try {
            String clientId = authService.getGoogleClientId();
            info.put("googleClientId", clientId != null ? clientId : "null");
            info.put("isConfigured", clientId != null ? "true" : "false");
            info.put("message", "Google OAuth debugging endpoint");
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            info.put("error", e.getMessage());
            return ResponseEntity.ok(info);
        }
    }

    @PostMapping("/google/test")
    public ResponseEntity<?> testGoogleEndpoint(@RequestBody GoogleLoginRequest googleLoginRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test endpoint received token");
        response.put("tokenLength", googleLoginRequest.getIdToken() != null ? googleLoginRequest.getIdToken().length() : 0);
        response.put("googleClientId", authService.getGoogleClientId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        // Lấy IP address của client
        String clientIp = getClientIpAddress(request);

        AuthResponse authResponse = authService.login(loginRequest, clientIp);

        // Check if user has consented to necessary cookies
        boolean hasCookieConsent = hasNecessaryCookieConsent(request);

        if (hasCookieConsent) {
            // Set JWT cookies for persistent authentication
            addJwtCookie(response, authResponse.getAccessToken(), "jwtToken");
            addJwtCookie(response, authResponse.getRefreshToken(), "refreshToken");
            authResponse.setMessage("Login successful with persistent session");
        } else {
            // No cookies set - session only
            authResponse.setMessage("Login successful. Accept cookies for persistent session.");
        }

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
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            var user = authService.getUserByEmail(principal.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy thông tin profile: " + e.getMessage());
        }
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
        // Don't set domain for cross-origin cookies

        // Add the cookie to the response
        response.addCookie(cookie);

        // Manually set the SameSite attribute - use None for cross-site
        String sameSiteCookie = String.format("%s=%s; Path=%s; HttpOnly; Secure; Max-Age=%d; SameSite=None",
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

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Nếu có nhiều IP (thông qua proxy), lấy IP đầu tiên
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam("email") String email) {
        try {
            System.out.println("=== TESTING EMAIL CONFIGURATION ===");
            System.out.println("Sending test email to: " + email);

            String subject = "Test Email - WeMovies";
            String content = "<h2>Test Email</h2><p>This is a test email from WeMovies backend.</p><p>Sent at: " + java.time.LocalDateTime.now() + "</p>";

            emailService.sendEmail(email, subject, content);

            return ResponseEntity.ok("Test email sent successfully to: " + email);
        } catch (Exception e) {
            System.err.println("❌ Test email failed: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to send test email: " + e.getMessage());
        }
    }

    private boolean hasNecessaryCookieConsent(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cookiePreferences".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    // Check if necessary cookies are accepted
                    return value.contains("necessary=true");
                }
            }
        }
        return false;
    }
}
