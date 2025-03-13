package com.budget_tracker.tracker.budget_tracker.controller.user.dto;


import com.budget_tracker.tracker.budget_tracker.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse {
    public String username;
    public String email;
    public String firstName;
    public String lastName;
    @Enumerated(EnumType.STRING)
    private Role role;
}
