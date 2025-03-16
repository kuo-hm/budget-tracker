package com.budget_tracker.tracker.budget_tracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budget_tracker.tracker.budget_tracker.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Number> {

    Categories findByName(String name);

    boolean existsByName(String name);

}
