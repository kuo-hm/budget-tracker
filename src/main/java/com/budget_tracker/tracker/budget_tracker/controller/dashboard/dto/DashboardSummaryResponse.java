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
public class DashboardSummaryResponse {
    // Overall finance summary
    private Double totalIncome;
    private Double totalExpenses;
    private Double netBalance;
    private Double savingsRate;  // percentage of income saved
    
    // Budget summary
    private Integer totalBudgets;
    private Integer budgetsOnTrack;
    private Integer budgetsOverBudget;
    private Double budgetUtilizationPercentage;  // overall percentage of budgets used
    
    // Goal summary
    private Integer totalGoals;
    private Integer completedGoals;
    private Integer inProgressGoals;
    private Double averageGoalCompletion;  // average progress percentage across all goals
    
    // Recurring transactions summary
    private Integer totalRecurringTransactions;
    private Double monthlyRecurringIncome;
    private Double monthlyRecurringExpenses;
    
    // Category statistics
    private Map<String, Double> topExpenseCategories;  // category name to amount
    private Map<String, Double> topIncomeCategories;  // category name to amount
    
    // Time-based analysis
    private Map<String, Double> expensesByMonth;  // month to amount
    private Map<String, Double> incomeByMonth;  // month to amount
    
    // Recent activity
    private List<TransactionSummaryDTO> recentTransactions;
    private List<GoalSummaryDTO> recentGoals;
} 