package com.budget_tracker.tracker.budget_tracker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budget_tracker.tracker.budget_tracker.entity.Budget;
import com.budget_tracker.tracker.budget_tracker.entity.User;

public interface BudgetRepository extends JpaRepository<Budget, Number> {

    List<Budget> findAllByCreatedBy(User user); 

}
