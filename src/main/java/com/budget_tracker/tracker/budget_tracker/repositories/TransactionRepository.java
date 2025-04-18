package com.budget_tracker.tracker.budget_tracker.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.budget_tracker.tracker.budget_tracker.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Number> {

    @Query(value = "SELECT * FROM transactions c WHERE "
            + "(:keyword IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:type IS NULL OR c.type = :type) AND "
            + "(:userId IS NULL OR c.created_by = :userId) AND "
            // Use standard CAST syntax for both IS NULL checks and COALESCE
            + "((CAST(:startDate AS TIMESTAMP) IS NULL AND CAST(:endDate AS TIMESTAMP) IS NULL) OR "
            + " (c.transaction_date BETWEEN "
            + "    COALESCE(CAST(:startDate AS TIMESTAMP), c.transaction_date) AND "
            + "    COALESCE(CAST(:endDate AS TIMESTAMP), c.transaction_date)))",
            nativeQuery = true)
    Page<Transaction> findByKeywordAndType(
            @Param("keyword") String keyword, // Using @Param for clarity, though often optional if names match
            @Param("type") String type,
            Pageable pageable,
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
