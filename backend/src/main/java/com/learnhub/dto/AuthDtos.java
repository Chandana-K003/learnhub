package com.learnhub.dto;

import com.learnhub.model.Role;
import com.learnhub.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank(message = "Full name is required") @Size(max = 100) String fullName,
            @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
            @Size(min = 6, message = "Password must be at least 6 characters") String password,
            Role role) {
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Password is required") String password) {
    }

    public record UserDto(Long id, String fullName, String email, String role) {
        public static UserDto from(User user) {
            return new UserDto(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
        }
    }

    public record AuthResponse(String token, UserDto user) {
    }
}
