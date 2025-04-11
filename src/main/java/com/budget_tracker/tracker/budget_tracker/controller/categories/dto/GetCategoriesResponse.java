package com.budget_tracker.tracker.budget_tracker.controller.categories.dto;

import java.util.List;

import com.budget_tracker.tracker.budget_tracker.entity.Categories;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCategoriesResponse {

    private List<CategoryItem> list;
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
    public static class CategoryItem {

        private Long id;
        private String name;
        private String description;
        private String createdAt;
        private String updatedAt;
        private String type;
    }
}
