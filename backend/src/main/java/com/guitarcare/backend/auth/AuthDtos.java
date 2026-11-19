package com.guitarcare.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 8, max = 72) String password
    ) {}

    public record LoginRequest(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank String password
    ) {}

    public record UserResponse(UUID id, String fullName, String email, Instant createdAt) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getCreatedAt());
        }
    }

    public record MessageResponse(String message) {}
}
