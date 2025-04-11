package com.budget_tracker.tracker.budget_tracker.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.user.dto.UpdateRequest;
import com.budget_tracker.tracker.budget_tracker.controller.user.dto.UserDetailResponse;
import com.budget_tracker.tracker.budget_tracker.services.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> me(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        return ResponseEntity.ok(userService.me(userEmail));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody UpdateRequest updateRequest) {
        String userEmail = (String) request.getAttribute("userEmail");
        userService.updateUser(userEmail, updateRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    @GetMapping()
    public ResponseEntity<UserDetailResponse[]> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
