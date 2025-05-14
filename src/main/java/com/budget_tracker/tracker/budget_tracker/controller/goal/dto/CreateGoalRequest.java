package com.budget_tracker.tracker.budget_tracker.controller.goal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateGoalRequest {

    private String name;
    
    private String description;
    
    private Double targetAmount;
    
    private Double currentAmount;
    
    private LocalDateTime targetDate;
    
    private Long categoryId;
} 