package com.budget_tracker.tracker.budget_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budget_tracker.tracker.budget_tracker.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Number> {

}
