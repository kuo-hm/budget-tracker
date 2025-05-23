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
public class DateRangeDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
} 