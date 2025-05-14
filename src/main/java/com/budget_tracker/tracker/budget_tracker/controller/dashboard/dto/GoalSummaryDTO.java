package com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto;

import java.time.LocalDateTime;

import com.budget_tracker.tracker.budget_tracker.enums.GoalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private Double targetAmount;
    private Double currentAmount;
    private Double progressPercentage;  // calculated field: currentAmount/targetAmount * 100
    private GoalStatus status;
    private LocalDateTime targetDate;
    private String categoryName;
    private String userName;  // user who created the goal
} 