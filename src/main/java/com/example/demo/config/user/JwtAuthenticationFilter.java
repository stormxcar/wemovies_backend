package com.example.demo.config.user;


import com.example.demo.services.Impls.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        
        // Skip JWT processing for WebSocket endpoints
        if (requestURI.startsWith("/ws-notifications/") || requestURI.startsWith("/ws/")) {
            System.out.println("Skipping JWT filter for WebSocket endpoint: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("JWT Filter processing: " + request.getMethod() + " " + requestURI);

        String token = null;

        // Đọc token từ cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println("Found " + cookies.length + " cookies:");
            for (Cookie cookie : cookies) {
                System.out.println("  Cookie: " + cookie.getName() + " = " + (cookie.getName().equals("jwtToken") ? "[HIDDEN]" : cookie.getValue()));
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println("  Found jwtToken cookie with value: " + (token != null ? "[PRESENT]" : "[NULL]"));
                    break;
                }
            }
        } else {
            System.out.println("No cookies found in request");
        }

        // Nếu không tìm thấy token trong cookie, có thể đọc từ header Authorization (tùy chọn)
        if (token == null) {
            final String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                System.out.println("Found token in Authorization header: [PRESENT]");
            } else {
                System.out.println("No Authorization header or invalid format");
            }
        }

        if (token == null) {
            System.out.println("⚠️ No JWT token found for protected endpoint: " + request.getRequestURI());
        }

        // Xử lý token
        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
