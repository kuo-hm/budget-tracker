package com.budget_tracker.tracker.budget_tracker.controller.auth;
import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.AuthenticationResponse;
import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.LoginRequest;
import com.budget_tracker.tracker.budget_tracker.controller.auth.dto.RegisterRequest;
import com.budget_tracker.tracker.budget_tracker.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
    return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(authService.login(request));
    }

}
