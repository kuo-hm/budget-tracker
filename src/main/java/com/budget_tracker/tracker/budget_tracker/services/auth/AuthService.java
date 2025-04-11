package com.budget_tracker.tracker.budget_tracker.services.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.config.JwtService;
import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.AuthenticationResponse;
import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.LoginRequest;
import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.RegisterRequest;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.enums.Role;
import com.budget_tracker.tracker.budget_tracker.exception.common.ConflictException;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already in use");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.USER)
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if (authenticate.isAuthenticated()) {

            var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

}
