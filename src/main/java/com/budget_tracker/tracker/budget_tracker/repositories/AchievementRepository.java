package com.budget_tracker.tracker.budget_tracker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.budget_tracker.tracker.budget_tracker.entity.Achievement;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.enums.AchievementType;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByUser(User user);
    List<Achievement> findByUserAndAchievementType(User user, AchievementType achievementType);
    boolean existsByUserAndAchievementType(User user, AchievementType achievementType);
    boolean existsByUserAndName(User user, String name);
} 