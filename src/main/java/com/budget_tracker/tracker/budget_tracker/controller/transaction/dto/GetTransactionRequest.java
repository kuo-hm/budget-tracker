package com.budget_tracker.tracker.budget_tracker.controller.transaction.dto;

import java.time.LocalDate;

import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionRequest {

    private String keyword;

    private CategoryType type;

    @Pattern(regexp = "^(amount|description|transactionDate|createdAt)$", message = "Invalid sortBy field")
    private String sortBy;

    @Pattern(regexp = "^(ASC|DESC)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "orderBy must be ASC or DESC")
    private String orderBy;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    @Min(1)
    private Integer limit = 10;

    @Builder.Default
    @Min(1)
    private Integer page = 1;

}
