package com.budget_tracker.tracker.budget_tracker.services.budget;

import java.util.List;

import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.budget.dto.CreateBudgetRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Budget;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.BudgetRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public void createBudget(CreateBudgetRequest body, String userEmail) {
        System.out.println("Creating budget with name: " + body.getName() + ", description: " + body.getDescription() + ", amount: " + body.getAmount() + ", userEmail: " + userEmail);
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var budgetEntity = new Budget();
        budgetEntity.setName(body.getName());
        budgetEntity.setDescription(body.getDescription());
        budgetEntity.setAmount(body.getAmount());
        budgetEntity.setCreatedBy(user);

        budgetRepository.save(budgetEntity);
    }

    public List<Budget> getBudgets(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return budgetRepository.findAllByCreatedBy(user);
    }
}
