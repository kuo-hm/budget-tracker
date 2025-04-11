package com.budget_tracker.tracker.budget_tracker.controller.transaction.dto;

import java.time.LocalDateTime;
import java.util.List;

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
        private CategoryItem transactionCategory;
        private LocalDateTime transactionDate;

    }

    @Data
    @AllArgsConstructor
    public static class CategoryItem {

        private Long id;
        private String name;
        private String description;
        private String createdAt;
        private String updatedAt;
        private String type;
    }
}
