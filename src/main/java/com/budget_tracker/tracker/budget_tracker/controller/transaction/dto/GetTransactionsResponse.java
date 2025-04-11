package com.budget_tracker.tracker.budget_tracker.controller.transaction.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.budget_tracker.tracker.budget_tracker.entity.Categories;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTransactionsResponse {

    private List<TransactionItem> list;
    private Metadata metadata;

    @Data
    @AllArgsConstructor
    public static class Metadata {

        private long totalItems;
        private int totalPages;
        private int currentPage;
        private int limit;
    }

    @Data
    @AllArgsConstructor
    public static class TransactionItem {

        private Long id;

        private Double amount;

        private String description;
        private String createdAt;
        private String updatedAt;
        private String type;
        private Categories transactionCategory;
        private LocalDateTime transactionDate;

    }
}
