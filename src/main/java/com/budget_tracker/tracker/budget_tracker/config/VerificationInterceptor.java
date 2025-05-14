package com.budget_tracker.tracker.budget_tracker.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.budget_tracker.tracker.budget_tracker.exception.common.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationInterceptor implements HandlerInterceptor {

    private final UserVerificationChecker verificationChecker;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        
        // Skip verification check for auth endpoints and public resources
        if (isPublicPath(requestPath)) {
            return true;
        }
        
        // Check verification status for all other endpoints
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                verificationChecker.checkUserVerified(authentication);
                return true;
            } catch (UnauthorizedException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isPublicPath(String path) {
        return path.startsWith("/auth") || 
               path.startsWith("/swagger-ui") || 
               path.startsWith("/api-docs") || 
               path.startsWith("/v3") ||
               path.startsWith("/error") ||
               path.startsWith("/webjars");
    }
} 