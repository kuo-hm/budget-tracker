package com.budget_tracker.tracker.budget_tracker.services.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.CreateRecurringTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.UpdateRecurringTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.entity.RecurringTransaction;
import com.budget_tracker.tracker.budget_tracker.entity.Transaction;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.exception.common.UnauthorizedException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.RecurringTransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.TransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final TransactionRepository transactionRepository;

    public void createRecurringTransaction(CreateRecurringTransactionRequest request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var category = categoriesRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to use this category");
        }

        var recurringTransaction = new RecurringTransaction();
        recurringTransaction.setAmount(request.getAmount());
        recurringTransaction.setDescription(request.getDescription());
        recurringTransaction.setTransactionCategory(category);
        recurringTransaction.setType(request.getType());
        recurringTransaction.setDayOfMonth(request.getDayOfMonth());
        recurringTransaction.setStartDate(request.getStartDate());
        recurringTransaction.setEndDate(request.getEndDate());
        recurringTransaction.setRecurrenceType(request.getRecurrenceType());
        recurringTransaction.setActive(true);
        recurringTransaction.setCreatedBy(user);

        recurringTransactionRepository.save(recurringTransaction);
    }

    public List<RecurringTransaction> getAllRecurringTransactions(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return recurringTransactionRepository.findAllByCreatedBy(user);
    }

    public RecurringTransaction getRecurringTransactionById(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var recurringTransaction = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recurring transaction not found"));

        if (!recurringTransaction.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to access this recurring transaction");
        }

        return recurringTransaction;
    }

    public void updateRecurringTransaction(Long id, UpdateRecurringTransactionRequest request, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var recurringTransaction = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recurring transaction not found"));

        if (!recurringTransaction.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this recurring transaction");
        }

        if (request.getAmount() != null) {
            recurringTransaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            recurringTransaction.setDescription(request.getDescription());
        }

        if (request.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            // Verify the category belongs to the user
            if (!category.getCreatedBy().getId().equals(user.getId())) {
                throw new UnauthorizedException("You are not authorized to use this category");
            }

            recurringTransaction.setTransactionCategory(category);
        }

        if (request.getType() != null) {
            recurringTransaction.setType(request.getType());
        }

        if (request.getDayOfMonth() != null) {
            recurringTransaction.setDayOfMonth(request.getDayOfMonth());
        }

        if (request.getStartDate() != null) {
            recurringTransaction.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            recurringTransaction.setEndDate(request.getEndDate());
        }

        if (request.getRecurrenceType() != null) {
            recurringTransaction.setRecurrenceType(request.getRecurrenceType());
        }

        if (request.getActive() != null) {
            recurringTransaction.setActive(request.getActive());
        }

        recurringTransactionRepository.save(recurringTransaction);
    }

    public void deleteRecurringTransaction(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var recurringTransaction = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recurring transaction not found"));

        if (!recurringTransaction.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this recurring transaction");
        }

        recurringTransactionRepository.delete(recurringTransaction);
    }

    public Page<RecurringTransaction> getRecurringTransactionsByFilters(
            String keyword, String type, String userEmail, Boolean active, Pageable pageable) {
        
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return recurringTransactionRepository.findByFilters(
                keyword, type, user.getId(), active, pageable);
    }

    @Transactional
    public void processRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
        int currentDay = now.getDayOfMonth();
        
        List<RecurringTransaction> activeTransactions = 
                recurringTransactionRepository.findActiveRecurringTransactionsForDay(currentDay, now);
        
        for (RecurringTransaction recurringTransaction : activeTransactions) {
            // Create a new transaction based on the recurring transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(recurringTransaction.getAmount());
            transaction.setDescription(recurringTransaction.getDescription() + " (Automated)");
            transaction.setTransactionCategory(recurringTransaction.getTransactionCategory());
            transaction.setType(recurringTransaction.getType());
            transaction.setTransactionDate(now);
            transaction.setCreatedBy(recurringTransaction.getCreatedBy());
            
            transactionRepository.save(transaction);
        }
    }
} 