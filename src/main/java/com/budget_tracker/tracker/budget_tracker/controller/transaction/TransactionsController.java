package com.budget_tracker.tracker.budget_tracker.controller.transaction;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.CreateTransactionRequest;
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
        return ResponseEntity.ok("Transcation created successfully");
    }

}
