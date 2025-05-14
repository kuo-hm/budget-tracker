package com.budget_tracker.tracker.budget_tracker.controller.transaction;

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

import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.CreateRecurringTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.UpdateRecurringTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.entity.RecurringTransaction;
import com.budget_tracker.tracker.budget_tracker.services.transaction.RecurringTransactionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recurring-transaction")
@RequiredArgsConstructor
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    @PostMapping()
    public ResponseEntity<Object> createRecurringTransaction(
            @RequestBody CreateRecurringTransactionRequest request,
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
        recurringTransactionService.createRecurringTransaction(request, userEmail);
        return ResponseEntity.ok("Recurring transaction created successfully");
    }

    @GetMapping()
    public ResponseEntity<List<RecurringTransaction>> getAllRecurringTransactions(HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(recurringTransactionService.getAllRecurringTransactions(userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransaction> getRecurringTransactionById(
            @PathVariable Long id, HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransactionById(id, userEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRecurringTransaction(
            @PathVariable Long id,
            @RequestBody UpdateRecurringTransactionRequest request,
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
        recurringTransactionService.updateRecurringTransaction(id, request, userEmail);
        return ResponseEntity.ok("Recurring transaction updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRecurringTransaction(
            @PathVariable Long id, HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        recurringTransactionService.deleteRecurringTransaction(id, userEmail);
        return ResponseEntity.ok("Recurring transaction deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RecurringTransaction>> searchRecurringTransactions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean active,
            Pageable pageable,
            HttpServletRequest httpRequest) {
        
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransactionsByFilters(
                keyword, type, userEmail, active, pageable));
    }
} 