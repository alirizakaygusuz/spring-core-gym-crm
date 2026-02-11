package com.alirizakaygusuz.gymcrm.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 150, message = "Username must be between 3 and 150 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 10, max = 255, message = "Password must be between 10 and 255 characters")
        String password) {
}
