package com.budget_tracker.tracker.budget_tracker.controller.categories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.CreateCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.GetCategoriesRequest;
import com.budget_tracker.tracker.budget_tracker.controller.categories.dto.GetCategoriesResponse;
import com.budget_tracker.tracker.budget_tracker.services.categories.CategoriesService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @PostMapping()
    public ResponseEntity<Object> createCategory(
            @Valid @RequestBody CreateCategoriesRequest request,
            HttpServletRequest httpRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }
        String userEmail = (String) httpRequest.getAttribute("userEmail");

        categoriesService.createCategory(request, userEmail);
        return ResponseEntity.ok("Category created successfully");
    }

    @GetMapping()
    public ResponseEntity<GetCategoriesResponse> getCategories(
            @ModelAttribute GetCategoriesRequest param,
            HttpServletRequest httpRequest
    ) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");

        return ResponseEntity.ok(categoriesService.getAllCategories(param, userEmail));
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable String id, @Valid @RequestBody CreateCategoriesRequest request,
            HttpServletRequest httpRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }
        String userEmail = (String) httpRequest.getAttribute("userEmail");

        Number idNumber;
        try {
            idNumber = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format");
        }
        categoriesService.updateCategory(request, userEmail, idNumber);
        return ResponseEntity.ok("Category created successfully");

    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable String id, HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");

        Number idNumber;
        try {
            idNumber = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid ID format");
        }
        categoriesService.deleteCategory(userEmail, idNumber);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("{id}")
    public ResponseEntity<GetCategoriesResponse.CategoryItem> getCategory(@PathVariable String id,
            HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");

        Number idNumber;
        try {
            idNumber = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(categoriesService.getCategory(userEmail, idNumber));
    }

}
