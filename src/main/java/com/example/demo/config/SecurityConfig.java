package com.example.demo.config;

import com.example.demo.config.user.JwtAuthenticationFilter;
import com.example.demo.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/*
 * @description: Security configuration for JWT-based authentication and CORS
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CookieConsentAuthorizationManager cookieConsentAuthorizationManager;

    private static final String[] PUBLIC_ROUTES = {
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/request-otp",
            "/api/auth/verify-otp",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/google",
            "/api/auth/refresh",
            "/api/auth/test-email"
    };

    @Bean("mainSecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/countries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/types/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()
                        .requestMatchers("/api/hybrid-watching/**").permitAll() // Unified Hybrid API (Redis + Database)
                        .requestMatchers("/api/view-tracking/**").permitAll() // View tracking and real-time view count
                        .requestMatchers("/api/trending/**").permitAll() // Trending movies and hot content
                        .requestMatchers(PUBLIC_ROUTES).permitAll()
                        .requestMatchers("/api/cookies/**").permitAll() // Allow cookie preference management
                        .requestMatchers("/api/types/**").permitAll() // Allow CRUD operations for types
                        .requestMatchers("/api/categories/**").permitAll() // Allow CRUD operations for categories
                        .requestMatchers("/api/movies/**").permitAll() // Allow CRUD operations for movies
                        .requestMatchers("/api/countries/**").permitAll() // Allow CRUD operations for countries
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll() // Allow GET reviews and ratings publicly
                        .requestMatchers("/ws-notifications/**").permitAll() // Allow WebSocket connections and handshake
                        .requestMatchers("/ws/**").permitAll() // Allow all WebSocket related endpoints
                        // Protected routes with cookie consent check
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated() // Require auth for POST reviews
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated() // Require auth for DELETE reviews
                        .requestMatchers("/api/watchlist/**").authenticated() // Remove cookie consent for watchlist
                        .requestMatchers("/api/schedules/**").authenticated() // Remove cookie consent for schedules
                        .requestMatchers("/api/auth/profile", "/api/auth/upload-avatar", "/api/auth/change-password").authenticated() // Remove cookie consent for profile operations
                        .requestMatchers("/api/auth/verifyUser").access(cookieConsentAuthorizationManager) // Keep for verifyUser (GDPR compliance)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(googleOAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://wemovies-frontend.vercel.app",
                "http://localhost:3000",
                "https://localhost:3000",
                "https://wemovies-backend.onrender.com",
                "https://accounts.google.com",  // Google OAuth
                "https://oauth2.googleapis.com" // Google OAuth token endpoint
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        // Thêm exposed headers cho Google OAuth
        config.setExposedHeaders(List.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Set-Cookie"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public OncePerRequestFilter googleOAuthFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
                    throws ServletException, java.io.IOException {
                
                // Add headers to support Google OAuth popup
                response.setHeader("Cross-Origin-Opener-Policy", "unsafe-none");
                response.setHeader("Cross-Origin-Resource-Policy", "cross-origin");
                
                // Allow third-party cookies for Google OAuth
                response.setHeader("Access-Control-Allow-Private-Network", "true");
                
                filterChain.doFilter(request, response);
            }
        };
    }
}