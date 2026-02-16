package com.alirizakaygusuz.gymcrm.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "ChangePasswordRequest",
        description = "Request payload for changing user password"
)
public record ChangePasswordRequest(

        @Schema(
                description = "Username of the user",
                example = "john.doe",
                required = true
        )
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 150, message = "Username must be between 3 and 150 characters")
        String username,

        @Schema(
                description = "Current password of the user",
                example = "password123",
                required = true
        )
        @NotBlank(message = "Old password is required")
        @Size(min = 10, max = 255, message = "Old password must be between 10 and 255 characters")
        String oldPassword,

        @Schema(
                description = "New password for the user",
                example = "newpassword123",
                required = true
        )
        @NotBlank(message = "New password is required")
        @Size(min = 10, max = 255, message = "New password must be between 10 and 255 characters")
        String newPassword
) {
}
