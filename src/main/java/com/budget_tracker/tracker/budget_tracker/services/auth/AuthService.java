package com.budget_tracker.tracker.budget_tracker.services.auth;

import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.RefreshTokenRequest;
import com.budget_tracker.tracker.budget_tracker.entity.TokenPair;
import com.budget_tracker.tracker.budget_tracker.services.jwt.JwtService;
import com.budget_tracker.tracker.budget_tracker.services.user.EmailUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;
    private final EmailUserDetailsService userDetailsService;


    @Transactional
    public TokenPair register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already in use");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(false)
                .build();

        User savedUser = userRepository.save(user);

        verificationService.generateVerificationToken(savedUser);

        // Auto-authenticate the newly registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword() // Use raw password before encoding
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtService.generateTokenPair(authentication);
    }


    public TokenPair login(LoginRequest request) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtService.generateTokenPair(authentication);
    }

    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();
        if(!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String accessToken = jwtService.generateAccessToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }
}
