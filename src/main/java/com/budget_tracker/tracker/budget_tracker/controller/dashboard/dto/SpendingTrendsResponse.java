package com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpendingTrendsResponse {
    // Key insights about spending
    private List<SpendingInsightDTO> insights;
    
    // Monthly savings data
    private Double currentMonthSavings;
    private Double previousMonthSavings;
    private Double averageMonthlySavings;
    private Double savingsRateCurrentMonth; // percentage of income saved this month
    private Double savingsRateAverage; // average percentage of income saved
    
    // Category spending trends
    private Map<String, Double> topIncreasingCategories; // categories with biggest spending increases
    private Map<String, Double> topDecreasingCategories; // categories with biggest spending decreases
    
    // Monthly spending trends by category
    private Map<String, Map<String, Double>> categorySpendingByMonth; // category -> month -> amount
    
    // Overall statistics
    private Double totalSpendingCurrentMonth;
    private Double totalSpendingPreviousMonth;
    private Double percentageChangeInSpending;
    private Double averageMonthlySpending;
} 