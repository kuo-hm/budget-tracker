package com.budget_tracker.tracker.budget_tracker.repositories;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.budget_tracker.tracker.budget_tracker.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Number> {

    @Query(value = "SELECT * FROM transactions c WHERE "
            + "(:keyword IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:type IS NULL OR c.type = :type) AND "
            + "(:userId IS NULL OR c.created_by = :userId) AND "
            + "(:startDate IS NULL OR :endDate IS NULL OR c.transaction_date BETWEEN :startDate AND :endDate)",
            nativeQuery = true)
    Page<Transaction> findByKeywordAndType(String keyword, String type, Pageable pageable, String userId,
            LocalDate startDate, LocalDate endDate);
}
