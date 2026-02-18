package com.alirizakaygusuz.gymcrm.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "LoginRequest",
        description = "Request body used to authenticate a user with username and password."
)
public record LoginRequest(

        @Schema(
                description = "Username used for authentication.",
                example = "john.doe",
                required = true
        )
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 150, message = "Username must be between 3 and 150 characters")
        String username,

        @Schema(
                description = "Password used for authentication.",
                example = "password123",
                required = true
        )
        @NotBlank(message = "Password is required")
        @Size(min = 10, max = 255, message = "Password must be between 10 and 255 characters")
        String password
) {
}
