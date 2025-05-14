package com.budget_tracker.tracker.budget_tracker.controller.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBudgetRequest {

    private String name;

    private String description;

    private Double amount;
    
    private Long categoryId;
}
