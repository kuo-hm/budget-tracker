package com.budget_tracker.tracker.budget_tracker.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.entity.Goal;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.enums.GoalStatus;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByCreatedBy(User user);
    
    List<Goal> findAllByCreatedByAndStatus(User user, GoalStatus status);
    
    List<Goal> findAllByCreatedByAndCategory(User user, Categories category);
    
    List<Goal> findByCreatedByOrderByCreatedAtDesc(User user, Pageable pageable);
    
    int countByCreatedBy(User user);
    
    @Query(value = "SELECT * FROM goals g WHERE "
            + "(:keyword IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:status IS NULL OR g.status = :status) AND "
            + "(:categoryId IS NULL OR g.category_id = :categoryId) AND "
            + "(:userId IS NULL OR g.created_by = :userId)",
            nativeQuery = true)
    Page<Goal> findByFilters(
            @Param("keyword") String keyword, 
            @Param("status") String status,
            @Param("categoryId") Long categoryId,
            @Param("userId") String userId,
            Pageable pageable);
} 