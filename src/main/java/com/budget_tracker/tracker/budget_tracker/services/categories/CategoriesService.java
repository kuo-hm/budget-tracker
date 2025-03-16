package com.budget_tracker.tracker.budget_tracker.services.categories;

import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.CreateCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.exception.DuplicateEmailException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    public void createCategory(CreateCategoriesRequest request) {
        if (categoriesRepository.existsByName(request.getName())) {
            throw new DuplicateEmailException("Name is already in use");
        }
        var category = Categories.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .build();

        categoriesRepository.save(category);
    }

}
