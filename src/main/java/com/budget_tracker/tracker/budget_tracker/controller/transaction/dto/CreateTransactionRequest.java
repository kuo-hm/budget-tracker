package com.budget_tracker.tracker.budget_tracker.controller.transaction.dto;

import java.time.LocalDateTime;

import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequest {

    @NotBlank(message = "Transaction name is required")
    @Size(min = 2, max = 50, message = "Transaction name must be between 2 and 50 characters")
    private String name;

    @Size(min = 2, max = 50, message = "Transaction description must be between 2 and 50 characters")
    private String description;

    @NotNull(message = "Transaction amount is required")
    private Double amount;

    @NotNull(message = "Transaction type is required")
    private CategoryType type;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime date;

    @NotNull(message = "Transaction category is required")
    private Long categoryId;
}
