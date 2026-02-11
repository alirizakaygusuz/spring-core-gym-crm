package com.alirizakaygusuz.gymcrm.dto.trainer.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TrainerRegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @NotNull(message = "Specialization ID is required")
        @Positive(message = "Specialization ID must be a positive number")
        Long specializationId

) {
}
