package com.budget_tracker.tracker.budget_tracker.controller.gamification.dto;

import java.time.LocalDateTime;

import com.budget_tracker.tracker.budget_tracker.enums.AchievementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private Integer pointsAwarded;
    private LocalDateTime earnedAt;
    private AchievementType achievementType;
} 