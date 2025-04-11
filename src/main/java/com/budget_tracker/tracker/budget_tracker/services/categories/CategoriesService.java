package com.budget_tracker.tracker.budget_tracker.services.categories;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.CreateCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.GetCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.GetCategoriesResponse;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.ConflictException;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;

    public void createCategory(CreateCategoriesRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (categoriesRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw new ConflictException("Name is already in use");
        }
        var category = Categories.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .createdBy(user)
                .build();

        categoriesRepository.save(category);
    }

    @Transactional
    public GetCategoriesResponse getAllCategories(GetCategoriesRequest param, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Hibernate.initialize(user.getCategories()); // Explicitly initialize the collection

        String keyword = (param != null) ? param.getKeyword() : null;
        String type = (param != null && param.getType() != null) ? param.getType().toString() : null;
        int limit = param.getLimit();
        int page = param.getPage() - 1;
        if (page < 0) {
            page = 0;
        }
        if (limit < 1) {
            limit = 10;
        }

        Pageable pageable = PageRequest.of(page, limit);

        if (param.getSortBy() != null && param.getOrderBy() != null) {
            pageable = PageRequest.of(page, limit, param.getOrderBy().equalsIgnoreCase("asc") ? Sort.by(param.getSortBy()).ascending() : Sort.by(param.getSortBy()).descending());
        }

        Page<Categories> categoriesPage = categoriesRepository.findByKeywordAndType(keyword, type, pageable, user.getId());
        GetCategoriesResponse.Metadata metadata = new GetCategoriesResponse.Metadata(
                categoriesPage.getTotalElements(),
                categoriesPage.getTotalPages(),
                page + 1,
                limit
        );

        List<GetCategoriesResponse.CategoryItem> categoryItems = categoriesPage.getContent().stream()
                .map(category -> new GetCategoriesResponse.CategoryItem(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt().toString(),
                category.getUpdatedAt().toString(),
                category.getType().toString()
        ))
                .toList();

        return new GetCategoriesResponse(categoryItems, metadata);
    }

    public void updateCategory(CreateCategoriesRequest request, String userEmail, Number id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!category.getCreatedBy().getId().equals(user.getId())) {

            throw new NotFoundException("Category not founds");
        }

        if (categoriesRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw new ConflictException("Name is already in use");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());
        categoriesRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String userEmail, Number id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new NotFoundException("Category not found");
        }
        Hibernate.initialize(category.getTransaction());

        if (!category.getTransaction().isEmpty()) {
            throw new ConflictException("Cannot delete category as it is referenced by transactions");
        }

        categoriesRepository.delete(category);
    }

    public GetCategoriesResponse.CategoryItem getCategory(String userEmail, Number id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new NotFoundException("Category not found");
        }
        return new GetCategoriesResponse.CategoryItem(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt().toString(),
                category.getUpdatedAt().toString(),
                category.getType().toString()
        );
    }

}
