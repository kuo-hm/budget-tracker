package com.budget_tracker.tracker.budget_tracker.services.budget;

import java.util.List;

import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.budget.dto.CreateBudgetRequest;
import com.budget_tracker.tracker.budget_tracker.controller.budget.dto.UpdateBudgetRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Budget;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.exception.common.UnauthorizedException;
import com.budget_tracker.tracker.budget_tracker.repositories.BudgetRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;

    public void createBudget(CreateBudgetRequest body, String userEmail) {
        System.out.println("Creating budget with name: " + body.getName() + ", description: " + body.getDescription() + ", amount: " + body.getAmount() + ", userEmail: " + userEmail);
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var budgetEntity = new Budget();
        budgetEntity.setName(body.getName());
        budgetEntity.setDescription(body.getDescription());
        budgetEntity.setAmount(body.getAmount());
        budgetEntity.setCreatedBy(user);
        
        if (body.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(body.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            
            // Verify the category belongs to the user
            if (!category.getCreatedBy().getId().equals(user.getId())) {
                throw new UnauthorizedException("You are not authorized to use this category");
            }
            
            budgetEntity.setCategory(category);
        }

        budgetRepository.save(budgetEntity);
    }

    public List<Budget> getBudgets(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return budgetRepository.findAllByCreatedBy(user);
    }
    
    public List<Budget> getBudgetsByCategory(Long categoryId, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        Categories category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
                
        return budgetRepository.findAllByCreatedByAndCategory(user, category);
    }
    
    public Budget getBudgetById(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget not found"));
                
        if (!budget.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to access this budget");
        }
        
        return budget;
    }
    
    public void updateBudget(Long id, UpdateBudgetRequest body, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget not found"));
                
        if (!budget.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this budget");
        }
        
        if (body.getName() != null) {
            budget.setName(body.getName());
        }
        
        if (body.getDescription() != null) {
            budget.setDescription(body.getDescription());
        }
        
        if (body.getAmount() != null) {
            budget.setAmount(body.getAmount());
        }
        
        if (body.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(body.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            
            // Verify the category belongs to the user
            if (!category.getCreatedBy().getId().equals(user.getId())) {
                throw new UnauthorizedException("You are not authorized to use this category");
            }
            
            budget.setCategory(category);
        }
        
        budgetRepository.save(budget);
    }
    
    public void deleteBudget(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget not found"));
                
        if (!budget.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this budget");
        }
        
        budgetRepository.delete(budget);
    }
}
