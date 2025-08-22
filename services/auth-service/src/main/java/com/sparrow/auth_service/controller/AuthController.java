package com.sparrow.auth_service.controller;

import com.sparrow.auth_service.dto.*;
import com.sparrow.auth_service.model.User;
import com.sparrow.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User saved = userService.register(request);
        return ResponseEntity.ok(new AuthResponse(saved.getId(), saved.getUsername(), "Registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "username", auth.getName(),
                        "authorities", auth.getAuthorities()
                )
        );
    }
}
