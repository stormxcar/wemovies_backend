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
            "/api/auth/refresh"
    };

    @Bean("mainSecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/countries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/types/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()
                        .requestMatchers(PUBLIC_ROUTES).permitAll()
                        .requestMatchers("/api/cookies/**").permitAll() // Allow cookie preference management
                        // Protected routes with cookie consent check
                        .requestMatchers("/api/reviews/**").access(cookieConsentAuthorizationManager)
                        .requestMatchers("/api/watchlist/**").access(cookieConsentAuthorizationManager)
                        .requestMatchers("/api/schedules/**").access(cookieConsentAuthorizationManager)
                        .requestMatchers("/api/auth/profile", "/api/auth/upload-avatar", "/api/auth/change-password", "/api/auth/verifyUser").access(cookieConsentAuthorizationManager)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://wemovies-frontend.vercel.app",
                "http://localhost:3000",
                "https://localhost:3000",
                "https://wemovies-backend.onrender.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

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
}