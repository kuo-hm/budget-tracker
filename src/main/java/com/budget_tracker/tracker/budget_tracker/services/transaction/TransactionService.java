package com.budget_tracker.tracker.budget_tracker.services.transaction;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.CreateTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.GetTransactionRequest;
import com.budget_tracker.tracker.budget_tracker.controller.transaction.dto.GetTransactionsResponse;
import com.budget_tracker.tracker.budget_tracker.entity.Transaction;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.TransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public void createTransaction(CreateTransactionRequest body, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
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

    @Transactional
    public GetTransactionsResponse getAllTransactions(GetTransactionRequest param, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Hibernate.initialize(user.getCategories()); // Explicitly initialize the collection

        String keyword = (param != null) ? param.getKeyword() : null;
        String type = (param != null && param.getType() != null) ? param.getType().toString() : null;
        int limit = (param != null && param.getLimit() != null) ? param.getLimit() : 10; // Added null check for param.getLimit()
        int page = (param != null && param.getPage() != null) ? param.getPage() - 1 : 0; // Added null check for param.getPage()
        if (page < 0) {
            page = 0;
        }

        System.out.println(param);

        Pageable pageable = PageRequest.of(page, limit);

        if (param.getSortBy() != null && param.getOrderBy() != null) {
            pageable = PageRequest.of(page, limit, param.getOrderBy().equalsIgnoreCase("asc") ? Sort.by(param.getSortBy()).ascending() : Sort.by(param.getSortBy()).descending());
        }

        Page<Transaction> transactionsPage = transactionRepository.findByKeywordAndType(keyword, type, pageable, user.getId());
        GetTransactionsResponse.Metadata metadata = new GetTransactionsResponse.Metadata(
                transactionsPage.getTotalElements(),
                transactionsPage.getTotalPages(),
                page + 1,
                limit
        );

        List<GetTransactionsResponse.TransactionItem> transactionItems = transactionsPage.getContent().stream()
                .map(transaction -> {
                    Hibernate.initialize(transaction.getTransactionCategory()); // Ensure transactionCategory is initialized
                    return new GetTransactionsResponse.TransactionItem(
                            transaction.getId(),
                            transaction.getAmount(),
                            transaction.getDescription(),
                            transaction.getCreatedAt().toString(),
                            transaction.getType().toString(),
                            transaction.getTransactionDate().toString(),
                            transaction.getTransactionCategory(),
                            transaction.getCreatedAt() // Assuming this is the LocalDateTime field
                    );
                }).toList();

        return new GetTransactionsResponse(transactionItems, metadata);
    }
}
