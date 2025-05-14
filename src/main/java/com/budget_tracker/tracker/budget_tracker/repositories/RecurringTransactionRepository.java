package com.budget_tracker.tracker.budget_tracker.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.budget_tracker.tracker.budget_tracker.entity.RecurringTransaction;
import com.budget_tracker.tracker.budget_tracker.entity.User;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findAllByCreatedBy(User user);
    
    @Query("SELECT rt FROM RecurringTransaction rt WHERE rt.active = true AND rt.dayOfMonth = :day " +
           "AND (rt.endDate IS NULL OR rt.endDate >= :currentDate)")
    List<RecurringTransaction> findActiveRecurringTransactionsForDay(@Param("day") Integer day, 
                                                                  @Param("currentDate") LocalDateTime currentDate);
    
    @Query(value = "SELECT * FROM recurring_transactions rt WHERE "
            + "(:keyword IS NULL OR LOWER(rt.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:type IS NULL OR rt.type = :type) AND "
            + "(:userId IS NULL OR rt.created_by = :userId) AND "
            + "(:active IS NULL OR rt.active = :active)",
            nativeQuery = true)
    Page<RecurringTransaction> findByFilters(
            @Param("keyword") String keyword, 
            @Param("type") String type,
            @Param("userId") String userId,
            @Param("active") Boolean active,
            Pageable pageable);
} 