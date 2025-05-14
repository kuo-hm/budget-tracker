package com.budget_tracker.tracker.budget_tracker.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.UnauthorizedException;

@Component
public class UserVerificationChecker {

    /**
     * Check if the user is verified before allowing access to protected resources
     * 
     * @param authentication The current authentication object
     * @throws UnauthorizedException if the user is not verified
     */
    public void checkUserVerified(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if (!user.isEnabled()) {
                throw new UnauthorizedException(
                        "Account not verified. Please verify your email before accessing this resource.");
            }
        }
    }
} 