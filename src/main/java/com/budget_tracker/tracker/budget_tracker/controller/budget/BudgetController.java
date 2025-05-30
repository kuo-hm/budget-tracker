package com.budget_tracker.tracker.budget_tracker.controller.budget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.budget.dto.CreateBudgetRequest;
import com.budget_tracker.tracker.budget_tracker.controller.budget.dto.UpdateBudgetRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Budget;
import com.budget_tracker.tracker.budget_tracker.services.budget.BudgetService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping()
    public ResponseEntity<Object> createNewBudget(@RequestBody CreateBudgetRequest request, BindingResult bindingResult, HttpServletRequest httpRequest) {
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
        budgetService.createBudget(request, userEmail);
        return ResponseEntity.ok("Budget created successfully");
    }

    @GetMapping()
    public ResponseEntity<List<Budget>> getAllBudgets(HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(budgetService.getBudgets(userEmail));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Budget>> getBudgetsByCategory(@PathVariable Long categoryId, HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(budgetService.getBudgetsByCategory(categoryId, userEmail));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id, HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(budgetService.getBudgetById(id, userEmail));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBudget(@PathVariable Long id, @RequestBody UpdateBudgetRequest request, 
                                               BindingResult bindingResult, HttpServletRequest httpRequest) {
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
        budgetService.updateBudget(id, request, userEmail);
        return ResponseEntity.ok("Budget updated successfully");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBudget(@PathVariable Long id, HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        budgetService.deleteBudget(id, userEmail);
        return ResponseEntity.ok("Budget deleted successfully");
    }
}
