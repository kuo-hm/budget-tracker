package com.budget_tracker.tracker.budget_tracker.services.gamification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budget_tracker.tracker.budget_tracker.entity.Achievement;
import com.budget_tracker.tracker.budget_tracker.entity.Transaction;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.entity.UserProgress;
import com.budget_tracker.tracker.budget_tracker.enums.AchievementType;
import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;
import com.budget_tracker.tracker.budget_tracker.repositories.AchievementRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserProgressRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {
    
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final AchievementRepository achievementRepository;
    
    /**
     * Initialize user's progress when they first sign up
     */
    @Transactional
    public void initializeUserProgress(User user) {
        UserProgress progress = UserProgress.builder()
            .user(user)
            .currentXp(0)
            .level(1)
            .xpToNextLevel(100) // Initial XP needed for level 2
            .savingStreak(0)
            .lastSavingDate(null)
            .build();
        
        userProgressRepository.save(progress);
    }
    
    /**
     * Get user's current progress
     */
    public UserProgress getUserProgress(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userProgressRepository.findByUser(user)
            .orElseGet(() -> {
                // Create progress if it doesn't exist
                UserProgress newProgress = initializeUserProgressObject(user);
                return userProgressRepository.save(newProgress);
            });
    }
    
    /**
     * Get all achievements for a user
     */
    public List<Achievement> getUserAchievements(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return achievementRepository.findByUser(user);
    }
    
    /**
     * Process a transaction for gamification
     */
    @Transactional
    public void processTransaction(Transaction transaction) {
        User user = transaction.getCreatedBy();
        UserProgress progress = getUserProgressOrCreate(user);
        
        // Award XP for transaction logging
        addXp(progress, 5);
        
        // Check for savings
        if (transaction.getType() == CategoryType.INCOME || 
            transaction.getTransactionCategory().getName().toLowerCase().contains("saving")) {
            processSavingTransaction(transaction, user, progress);
        }
        
        userProgressRepository.save(progress);
    }
    
    private void processSavingTransaction(Transaction transaction, User user, UserProgress progress) {
        LocalDateTime transactionDate = transaction.getTransactionDate();
        
        // Check and update saving streak
        if (progress.getLastSavingDate() != null) {
            LocalDateTime lastDate = progress.getLastSavingDate();
            long daysBetween = ChronoUnit.DAYS.between(lastDate.toLocalDate(), transactionDate.toLocalDate());
            
            if (daysBetween == 1) {
                // Consecutive day - update streak
                progress.updateSavingStreak(transactionDate);
                checkForStreakAchievements(user, progress.getSavingStreak());
            } else if (daysBetween > 1) {
                // Streak broken
                progress.resetSavingStreak();
                progress.updateSavingStreak(transactionDate);
            }
        } else {
            // First saving transaction
            progress.updateSavingStreak(transactionDate);
            awardAchievement(user, AchievementType.FIRST_TRANSACTION, "First Saver", 
                    "Made your first saving transaction", 20);
        }
    }
    
    private void checkForStreakAchievements(User user, int streak) {
        // Check for streak-based achievements
        if (streak == 3) {
            awardAchievement(user, AchievementType.SAVING_STREAK, "Saving Spree", 
                    "3-day saving streak achieved", 30);
        } else if (streak == 7) {
            awardAchievement(user, AchievementType.SAVING_STREAK, "Weekly Saver", 
                    "7-day saving streak achieved", 75);
        } else if (streak == 30) {
            awardAchievement(user, AchievementType.SAVING_STREAK, "Saving Champion", 
                    "30-day saving streak achieved", 300);
        }
    }
    
    /**
     * Award an achievement to a user if they don't already have it
     */
    @Transactional
    public void awardAchievement(User user, AchievementType type, String name, String description, int xpAmount) {
        // Check if user already has this achievement
        if (achievementRepository.existsByUserAndName(user, name)) {
            return;
        }
        
        Achievement achievement = Achievement.builder()
            .user(user)
            .achievementType(type)
            .name(name)
            .description(description)
            .pointsAwarded(xpAmount)
            .earnedAt(LocalDateTime.now())
            .build();
        
        achievementRepository.save(achievement);
        
        // Award XP for the achievement
        UserProgress progress = getUserProgressOrCreate(user);
        addXp(progress, xpAmount);
        userProgressRepository.save(progress);
        
        log.info("Awarded achievement: {} to user: {}", name, user.getEmail());
    }
    
    /**
     * Add XP to a user's progress
     */
    private void addXp(UserProgress progress, int amount) {
        progress.addXp(amount);
    }
    
    /**
     * Get or create user progress
     */
    private UserProgress getUserProgressOrCreate(User user) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUser(user);
        
        return existingProgress.orElseGet(() -> {
            UserProgress newProgress = initializeUserProgressObject(user);
            return userProgressRepository.save(newProgress);
        });
    }
    
    private UserProgress initializeUserProgressObject(User user) {
        return UserProgress.builder()
            .user(user)
            .currentXp(0)
            .level(1)
            .xpToNextLevel(100)
            .savingStreak(0)
            .lastSavingDate(null)
            .build();
    }
} 