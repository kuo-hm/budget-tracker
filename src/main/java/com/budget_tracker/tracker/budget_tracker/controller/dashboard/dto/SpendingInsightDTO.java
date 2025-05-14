package com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpendingInsightDTO {
    private String message;
    private String category;
    private Double amount;
    private Double percentageChange;
    private String comparisonPeriod; // e.g., "last month", "3-month average"
    private String insightType; // e.g., "SPENDING_INCREASE", "SPENDING_DECREASE", "SAVINGS_TREND"
    private Integer priority; // 1-5, with 1 being highest priority
} 