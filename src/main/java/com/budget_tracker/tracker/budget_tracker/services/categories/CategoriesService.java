package com.budget_tracker.tracker.budget_tracker.services.categories;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.CreateCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.GetCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.GetCategoriesResponse;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.ConflictException;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void createCategory(CreateCategoriesRequest request, String userEmail) {
        log.debug("Creating category with name: {} for user: {}", request.getName(), userEmail);
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        if (categoriesRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw new ConflictException("Category name is already in use");
        }
        
        var category = Categories.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .createdBy(user)
                .build();

        categoriesRepository.save(category);
        log.info("Category created successfully: {}", category.getId());
    }

    @Cacheable(value = "categories", key = "#userEmail + '-' + #param.hashCode()")
    public GetCategoriesResponse getAllCategories(GetCategoriesRequest param, String userEmail) {
        log.debug("Fetching categories for user: {}", userEmail);
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String keyword = (param != null) ? param.getKeyword() : null;
        String type = (param != null && param.getType() != null) ? param.getType().toString() : null;
        int limit = Math.min(param.getLimit(), 100); 
        int page = Math.max(param.getPage() - 1, 0);

        Pageable pageable = PageRequest.of(page, limit);

        if (param.getSortBy() != null && param.getOrderBy() != null) {
            Sort.Direction direction = param.getOrderBy().equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            pageable = PageRequest.of(page, limit, Sort.by(direction, param.getSortBy()));
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

        log.info("Retrieved {} categories for user: {}", categoryItems.size(), userEmail);
        return new GetCategoriesResponse(categoryItems, metadata);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void updateCategory(CreateCategoriesRequest request, String userEmail, Number id) {
        log.debug("Updating category: {} for user: {}", id, userEmail);
        
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
                
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new NotFoundException("Category not found");
        }

        if (categoriesRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw new ConflictException("Category name is already in use");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());
        categoriesRepository.save(category);
        
        log.info("Category updated successfully: {}", category.getId());
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(String userEmail, Number id) {
        log.debug("Deleting category: {} for user: {}", id, userEmail);
        
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
                
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new NotFoundException("Category not found");
        }

        if (!category.getTransaction().isEmpty()) {
            throw new ConflictException("Cannot delete category as it is referenced by transactions");
        }

        categoriesRepository.delete(category);
        log.info("Category deleted successfully: {}", id);
    }

    @Cacheable(value = "categories", key = "#userEmail + '-' + #id")
    public GetCategoriesResponse.CategoryItem getCategory(String userEmail, Number id) {
        log.debug("Fetching category: {} for user: {}", id, userEmail);
        
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
                
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new NotFoundException("Category not found");
        }
        
        log.info("Retrieved category: {} for user: {}", id, userEmail);
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
