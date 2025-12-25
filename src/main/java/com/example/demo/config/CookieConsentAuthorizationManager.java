package com.example.demo.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CookieConsentAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(CookieConsentAuthorizationManager.class);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();

        // First check if user is authenticated
        Authentication auth = authentication.get();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());

        logger.debug("Authorization check for URI: {}, Authenticated: {}", request.getRequestURI(), isAuthenticated);

        // Check cookie consent
        boolean hasCookieConsent = hasNecessaryCookieConsent(request);

        logger.debug("Cookie consent check: {}", hasCookieConsent);

        // Allow access if:
        // 1. User is authenticated AND has cookie consent, OR
        // 2. This is a request to set cookie preferences (to avoid circular dependency), OR
        // 3. This is verifyUser endpoint (to check authentication status)
        boolean isCookiePreferenceRequest = request.getRequestURI().contains("/api/cookies/preferences");
        boolean isVerifyUserRequest = request.getRequestURI().contains("/api/auth/verifyUser");

        if (isCookiePreferenceRequest || isVerifyUserRequest) {
            logger.debug("Allowing access to {} endpoint", isCookiePreferenceRequest ? "cookie preferences" : "verify user");
            return new AuthorizationDecision(true); // Allow setting preferences or verifying user
        }

        // For protected routes, require both authentication and cookie consent
        boolean decision = isAuthenticated && hasCookieConsent;
        logger.debug("Final authorization decision: {}", decision);
        return new AuthorizationDecision(decision);
    }

    private boolean hasNecessaryCookieConsent(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.debug("Found cookie: {} = {}", cookie.getName(), cookie.getValue());
                if ("cookiePreferences".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    // Check if necessary cookies are accepted
                    boolean consent = value.contains("necessary=true");
                    logger.debug("Cookie preferences value: {}, necessary consent: {}", value, consent);
                    return consent;
                }
            }
        } else {
            logger.debug("No cookies found in request");
        }

        // No cookie preferences set - deny access to protected routes
        logger.debug("No cookiePreferences cookie found");
        return false;
    }
}