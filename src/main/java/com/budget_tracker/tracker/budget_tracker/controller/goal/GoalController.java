package com.budget_tracker.tracker.budget_tracker.controller.goal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.goal.dto.CreateGoalRequest;
import com.budget_tracker.tracker.budget_tracker.controller.goal.dto.UpdateGoalProgressRequest;
import com.budget_tracker.tracker.budget_tracker.controller.goal.dto.UpdateGoalRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Goal;
import com.budget_tracker.tracker.budget_tracker.enums.GoalStatus;
import com.budget_tracker.tracker.budget_tracker.services.goal.GoalService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping()
    public ResponseEntity<Object> createGoal(
            @RequestBody CreateGoalRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        goalService.createGoal(request, userEmail);
        return ResponseEntity.ok("Goal created successfully");
    }

    @GetMapping()
    public ResponseEntity<List<Goal>> getAllGoals(HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(goalService.getAllGoals(userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(
            @PathVariable Long id, HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(goalService.getGoalById(id, userEmail));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Goal>> getGoalsByStatus(
            @PathVariable GoalStatus status, HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(goalService.getGoalsByStatus(status, userEmail));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Goal>> getGoalsByCategory(
            @PathVariable Long categoryId, HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(goalService.getGoalsByCategory(categoryId, userEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateGoal(
            @PathVariable Long id,
            @RequestBody UpdateGoalRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        goalService.updateGoal(id, request, userEmail);
        return ResponseEntity.ok("Goal updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGoal(
            @PathVariable Long id, HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        goalService.deleteGoal(id, userEmail);
        return ResponseEntity.ok("Goal deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Goal>> searchGoals(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable,
            HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(goalService.getGoalsByFilters(
                keyword, status, categoryId, userEmail, pageable));
    }
    
    @PutMapping("/{id}/progress")
    public ResponseEntity<Goal> updateGoalProgress(
            @PathVariable Long id,
            @RequestBody UpdateGoalProgressRequest request,
            HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        Goal updatedGoal = goalService.updateGoalProgress(id, request.getAmount(), userEmail);
        return ResponseEntity.ok(updatedGoal);
    }
} 