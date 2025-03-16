package com.budget_tracker.tracker.budget_tracker.controller.categories.dto;

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
public class CreateCategoriesRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    @Size(min = 2, max = 50, message = "Category description must be between 2 and 50 characters")
    private String description;

    @NotNull(message = "Category type is required")
    private CategoryType type;
}
