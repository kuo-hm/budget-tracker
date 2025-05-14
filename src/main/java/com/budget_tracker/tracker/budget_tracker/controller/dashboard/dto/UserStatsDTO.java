package com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private String id;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
    private Integer transactionCount;
    private Integer goalCount;
    private Integer budgetCount;
    private Double totalSpent;
    private Double totalIncome;
    private Double savingsRate;
    private LocalDateTime lastActive;
} 