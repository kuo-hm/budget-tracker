package com.budget_tracker.tracker.budget_tracker.services.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;
import com.budget_tracker.tracker.budget_tracker.services.email.EmailService;
import com.budget_tracker.tracker.budget_tracker.services.gamification.GamificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final GamificationService gamificationService;
    
    @Value("${app.token-expiration-hours:24}")
    private int tokenExpirationHours;
    
    /**
     * Generate a verification token for a user and send verification email
     */
    @Transactional
    public void generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(tokenExpirationHours));
        userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    
    /**
     * Verify a user's account with the provided token
     */
    @Transactional
    public void verifyAccount(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid verification token"));
        
        // Check if token is expired
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }
        
        // Enable the user's account
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        
        // Initialize user progress for gamification
        gamificationService.initializeUserProgress(user);
    }
    
    /**
     * Resend verification email with a new token
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        // Check if already verified
        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }
        
        // Generate new token and send email
        generateVerificationToken(user);
    }
} 