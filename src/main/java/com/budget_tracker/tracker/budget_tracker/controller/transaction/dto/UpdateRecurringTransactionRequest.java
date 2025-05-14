package com.budget_tracker.tracker.budget_tracker.controller.transaction.dto;

import java.time.LocalDateTime;

import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;
import com.budget_tracker.tracker.budget_tracker.enums.RecurrenceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecurringTransactionRequest {

    private Double amount;
    
    private String description;
    
    private Long categoryId;
    
    private CategoryType type;
    
    private Integer dayOfMonth;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private RecurrenceType recurrenceType;
    
    private Boolean active;
} 