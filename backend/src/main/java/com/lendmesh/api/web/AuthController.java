package com.lendmesh.api.web;

import com.lendmesh.api.service.AuthService;
import com.lendmesh.api.web.dto.AuthDtos.AuthResponse;
import com.lendmesh.api.web.dto.AuthDtos.LoginRequest;
import com.lendmesh.api.web.dto.AuthDtos.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request.displayName(), request.email(), request.password(), request.roles());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
