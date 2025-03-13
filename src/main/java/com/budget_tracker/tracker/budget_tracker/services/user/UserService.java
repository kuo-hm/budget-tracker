package com.budget_tracker.tracker.budget_tracker.services.user;

import com.budget_tracker.tracker.budget_tracker.controller.user.dto.UserDetailResponse;
import com.budget_tracker.tracker.budget_tracker.exception.ResourceNotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;

    public UserDetailResponse me(String userEmail) {


        var user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return UserDetailResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
