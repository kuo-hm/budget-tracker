package com.budget_tracker.tracker.budget_tracker.controller.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGoalProgressRequest {

    private Double amount;
} 