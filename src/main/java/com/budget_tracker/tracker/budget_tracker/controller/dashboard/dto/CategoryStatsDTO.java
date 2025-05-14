package com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto;

import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsDTO {
    private Long id;
    private String name;
    private String description;
    private CategoryType type;
    private Integer transactionCount;
    private Double totalAmount;
    private Double percentageOfTotal;  // percentage of total for its type (income/expense)
    private Integer userCount;  // how many users use this category
} 