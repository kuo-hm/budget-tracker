package com.budget_tracker.tracker.budget_tracker.services.user;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.user.dto.UpdateRequest;
import com.budget_tracker.tracker.budget_tracker.controller.user.dto.UserDetailResponse;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.ResourceNotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Injected BCryptPasswordEncoder


    public UserDetailResponse me(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserDetailResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }


    public void updateUser(String email, UpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword(), passwordEncoder); // Hash password
        }

        userRepository.save(user);
    }

    public UserDetailResponse[] getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> UserDetailResponse.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole())
                        .build()
                ).toArray(UserDetailResponse[]::new);
    }

}
