package com.budget_tracker.tracker.budget_tracker.controller.transaction;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.CreateTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.GetTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.GetTransactionsResponse;
import com.budget_tracker.tracker.budget_tracker.services.transaction.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<Object> createNewTransaction(@RequestBody CreateTransactionRequest request, HttpServletRequest httpRequest,
            BindingResult bindingResult) {
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

        transactionService.createTransaction(request, userEmail);
        return ResponseEntity.ok("Transaction created successfully");
    }

    @GetMapping()
    public ResponseEntity<GetTransactionsResponse> getAllTransactions(@ModelAttribute GetTransactionRequest param,
            HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");

        return ResponseEntity.ok(transactionService.getAllTransactions(param, userEmail));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id,
            HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        Number idNumber;
        try {
            idNumber = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format");
        }
        transactionService.deleteTransaction(userEmail, idNumber);
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable String id,
            @RequestBody CreateTransactionRequest request, HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        Number idNumber;
        try {
            idNumber = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format");
        }
        transactionService.updateTransaction(request, userEmail, idNumber);
        return ResponseEntity.ok("Transaction updated successfully");
    }

    @GetMapping("{id}")
    public ResponseEntity<GetTransactionsResponse.TransactionItem> getTransactionById(@PathVariable String id,
            HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        Number idNumber;
        try {
            idNumber = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(transactionService.getTransactionById(userEmail, idNumber));
    }
}
