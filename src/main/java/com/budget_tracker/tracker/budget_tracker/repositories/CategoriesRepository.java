package com.budget_tracker.tracker.budget_tracker.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;

public interface CategoriesRepository extends JpaRepository<Categories, Number> {

    Categories findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndType(String name, CategoryType type);

    @Query(value = "SELECT * FROM categories c WHERE "
            + "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:type IS NULL OR c.type = :type) AND "
            + "(:userId IS NULL OR c.created_by = :userId)",
            nativeQuery = true)
    Page<Categories> findByKeywordAndType(String keyword, String type, Pageable pageable, String userId);

}
