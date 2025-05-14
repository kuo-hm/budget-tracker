package com.budget_tracker.tracker.budget_tracker.controller.gamification;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.gamification.dto.AchievementResponse;
import com.budget_tracker.tracker.budget_tracker.controller.gamification.dto.UserProgressResponse;
import com.budget_tracker.tracker.budget_tracker.entity.Achievement;
import com.budget_tracker.tracker.budget_tracker.entity.UserProgress;
import com.budget_tracker.tracker.budget_tracker.services.gamification.GamificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/gamification")
@RequiredArgsConstructor
public class GamificationController {
    
    private final GamificationService gamificationService;
    
    @GetMapping("/progress")
    public ResponseEntity<UserProgressResponse> getUserProgress(Authentication authentication) {
        String userEmail = authentication.getName();
        UserProgress progress = gamificationService.getUserProgress(userEmail);
        
        Double progressPercentage = calculateProgressPercentage(progress);
        
        UserProgressResponse response = UserProgressResponse.builder()
                .currentXp(progress.getCurrentXp())
                .level(progress.getLevel())
                .xpToNextLevel(progress.getXpToNextLevel())
                .savingStreak(progress.getSavingStreak())
                .lastSavingDate(progress.getLastSavingDate())
                .progressPercentage(progressPercentage)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementResponse>> getUserAchievements(Authentication authentication) {
        String userEmail = authentication.getName();
        List<Achievement> achievements = gamificationService.getUserAchievements(userEmail);
        
        List<AchievementResponse> response = achievements.stream()
                .map(achievement -> AchievementResponse.builder()
                        .id(achievement.getId())
                        .name(achievement.getName())
                        .description(achievement.getDescription())
                        .icon(achievement.getIcon())
                        .pointsAwarded(achievement.getPointsAwarded())
                        .earnedAt(achievement.getEarnedAt())
                        .achievementType(achievement.getAchievementType())
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    private Double calculateProgressPercentage(UserProgress progress) {
        if (progress.getXpToNextLevel() == 0) {
            return 100.0;
        }
        return (progress.getCurrentXp() * 100.0) / progress.getXpToNextLevel();
    }
} 