package com.budget_tracker.tracker.budget_tracker.services.transaction;

import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.CreateTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Transaction;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.TransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public void createTransaction(CreateTransactionRequest body, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var category = categoriesRepository.findById(body.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new NotFoundException("Name is already in use");

        }
        var transactionEntity = new Transaction();
        transactionEntity.setDescription(body.getDescription());
        transactionEntity.setAmount(body.getAmount());
        transactionEntity.setType(body.getType());
        transactionEntity.setTransactionDate(body.getDate());
        transactionEntity.setTransactionCategory(category);
        transactionEntity.setCreatedBy(user);
        transactionRepository.save(transactionEntity);
    }
}
