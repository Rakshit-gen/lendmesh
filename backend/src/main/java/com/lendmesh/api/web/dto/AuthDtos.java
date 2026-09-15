package com.lendmesh.api.web.dto;

import com.lendmesh.api.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank String displayName,
            @Email @NotBlank String email,
            @Size(min = 8, message = "must be at least 8 characters") String password,
            @NotEmpty Set<UserRole> roles) {
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record AuthResponse(String token) {
    }
}
