package com.budget_tracker.tracker.budget_tracker.services.goal;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.goal.dto.CreateGoalRequest;
import com.budget_tracker.tracker.budget_tracker.controller.goal.dto.UpdateGoalRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.entity.Goal;
import com.budget_tracker.tracker.budget_tracker.enums.GoalStatus;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.exception.common.UnauthorizedException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.GoalRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;

    public void createGoal(CreateGoalRequest request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var goal = new Goal();
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : 0.0);
        goal.setTargetDate(request.getTargetDate());
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setCreatedBy(user);

        if (request.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            // Verify the category belongs to the user
            if (!category.getCreatedBy().getId().equals(user.getId())) {
                throw new UnauthorizedException("You are not authorized to use this category");
            }

            goal.setCategory(category);
        }

        goalRepository.save(goal);
    }

    public List<Goal> getAllGoals(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return goalRepository.findAllByCreatedBy(user);
    }

    public Goal getGoalById(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to access this goal");
        }

        return goal;
    }

    public List<Goal> getGoalsByStatus(GoalStatus status, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return goalRepository.findAllByCreatedByAndStatus(user, status);
    }

    public List<Goal> getGoalsByCategory(Long categoryId, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        return goalRepository.findAllByCreatedByAndCategory(user, category);
    }

    public void updateGoal(Long id, UpdateGoalRequest request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this goal");
        }

        if (request.getName() != null) {
            goal.setName(request.getName());
        }

        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getCurrentAmount() != null) {
            goal.setCurrentAmount(request.getCurrentAmount());
            
            // Automatically update status if goal is completed
            if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
                goal.setStatus(GoalStatus.COMPLETED);
            } else if (goal.getStatus() == GoalStatus.COMPLETED) {
                // If it was marked complete but now falls below target
                goal.setStatus(GoalStatus.IN_PROGRESS);
            }
        }

        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        if (request.getStatus() != null) {
            goal.setStatus(request.getStatus());
        }

        if (request.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            // Verify the category belongs to the user
            if (!category.getCreatedBy().getId().equals(user.getId())) {
                throw new UnauthorizedException("You are not authorized to use this category");
            }

            goal.setCategory(category);
        }

        goalRepository.save(goal);
    }

    public void deleteGoal(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this goal");
        }

        goalRepository.delete(goal);
    }

    public Page<Goal> getGoalsByFilters(
            String keyword, String status, Long categoryId, String userEmail, Pageable pageable) {
        
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return goalRepository.findByFilters(
                keyword, status, categoryId, user.getId(), pageable);
    }

    /**
     * Updates the progress of a goal with the provided amount.
     * This can be used to track progress toward a financial goal.
     * 
     * @param goalId The ID of the goal to update
     * @param amount The amount to add to the current progress (can be negative to reduce progress)
     * @param userEmail The email of the user making the update
     * @return The updated goal with new progress
     */
    public Goal updateGoalProgress(Long goalId, Double amount, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this goal");
        }
        
        // Update current amount
        Double newAmount = goal.getCurrentAmount() + amount;
        goal.setCurrentAmount(newAmount);
        
        // Check if goal has been completed
        if (newAmount >= goal.getTargetAmount() && goal.getStatus() != GoalStatus.COMPLETED) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else if (newAmount < goal.getTargetAmount() && goal.getStatus() == GoalStatus.COMPLETED) {
            // Goal was completed but now is not
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }
        
        return goalRepository.save(goal);
    }
} 