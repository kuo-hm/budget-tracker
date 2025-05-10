package com.budget_tracker.tracker.budget_tracker.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
} 