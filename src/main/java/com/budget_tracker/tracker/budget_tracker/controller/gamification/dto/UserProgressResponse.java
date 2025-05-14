package com.budget_tracker.tracker.budget_tracker.controller.gamification.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressResponse {
    private Integer currentXp;
    private Integer level;
    private Integer xpToNextLevel;
    private Integer savingStreak;
    private LocalDateTime lastSavingDate;
    private Double progressPercentage;  // Current XP / XP to next level as a percentage
} 