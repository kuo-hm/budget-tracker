package com.budget_tracker.tracker.budget_tracker.controller.transaction.dto;

import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;

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

    private String sortBy;

    private String orderBy;

    @Builder.Default
    private Integer limit = 10;

    @Builder.Default
    private Integer page = 1;

}
