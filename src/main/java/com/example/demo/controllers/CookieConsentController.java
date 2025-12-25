package com.example.demo.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cookies")
public class CookieConsentController {

    @PostMapping("/preferences")
    public ResponseEntity<?> saveCookiePreferences(
            @RequestBody Map<String, Boolean> preferences,
            HttpServletResponse response) {

        try {
            // Validate preferences
            if (!preferences.containsKey("necessary")) {
                preferences.put("necessary", true); // Always required
            }

            // Convert preferences to JSON string for cookie storage
            String preferencesJson = preferences.toString();

            // Store preferences in HTTP-only cookie for server-side access
            Cookie preferencesCookie = new Cookie("cookiePreferences", preferencesJson);
            preferencesCookie.setHttpOnly(true); // Secure server-side only
            preferencesCookie.setPath("/");
            preferencesCookie.setMaxAge(365 * 24 * 60 * 60); // 1 year
            preferencesCookie.setSecure(true); // HTTPS only in production
            preferencesCookie.setAttribute("SameSite", "Lax");
            response.addCookie(preferencesCookie);

            // Also store in a client-accessible cookie for frontend
            Cookie clientCookie = new Cookie("cookieConsent", preferencesJson);
            clientCookie.setHttpOnly(false); // Allow client-side access
            clientCookie.setPath("/");
            clientCookie.setMaxAge(365 * 24 * 60 * 60);
            clientCookie.setSecure(true);
            clientCookie.setAttribute("SameSite", "Lax");
            response.addCookie(clientCookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Cookie preferences saved successfully");
            responseBody.put("preferences", preferences);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving cookie preferences: " + e.getMessage());
        }
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getCookiePreferences(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            Map<String, Boolean> preferences = new HashMap<>();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("cookiePreferences".equals(cookie.getName())) {
                        // Parse the preferences string back to map
                        String value = cookie.getValue();
                        // Simple parsing - in production you might want more robust JSON parsing
                        preferences.put("necessary", value.contains("necessary=true"));
                        preferences.put("analytics", value.contains("analytics=true"));
                        preferences.put("marketing", value.contains("marketing=true"));
                        break;
                    }
                }
            }

            // Default preferences if none set
            if (preferences.isEmpty()) {
                preferences.put("necessary", false);
                preferences.put("analytics", false);
                preferences.put("marketing", false);
            }

            return ResponseEntity.ok(preferences);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving cookie preferences: " + e.getMessage());
        }
    }

    @DeleteMapping("/preferences")
    public ResponseEntity<?> clearCookiePreferences(HttpServletResponse response) {
        try {
            // Clear server-side cookie
            Cookie serverCookie = new Cookie("cookiePreferences", "");
            serverCookie.setHttpOnly(true);
            serverCookie.setPath("/");
            serverCookie.setMaxAge(0);
            response.addCookie(serverCookie);

            // Clear client-side cookie
            Cookie clientCookie = new Cookie("cookieConsent", "");
            clientCookie.setHttpOnly(false);
            clientCookie.setPath("/");
            clientCookie.setMaxAge(0);
            response.addCookie(clientCookie);

            return ResponseEntity.ok("Cookie preferences cleared");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error clearing cookie preferences: " + e.getMessage());
        }
    }
}