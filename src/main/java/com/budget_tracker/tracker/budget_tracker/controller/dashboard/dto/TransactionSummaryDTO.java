package com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto;

import java.time.LocalDateTime;

import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryDTO {
    private Long id;
    private String description;
    private Double amount;
    private CategoryType type;
    private String categoryName;
    private LocalDateTime transactionDate;
    private String userName;  // user who created the transaction
} 