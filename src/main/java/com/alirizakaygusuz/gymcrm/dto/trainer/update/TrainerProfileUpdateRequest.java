package com.alirizakaygusuz.gymcrm.dto.trainer.update;

import com.alirizakaygusuz.gymcrm.dto.common.UserUpdateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TrainerProfileUpdateRequest(

        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName ,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName ,

        @NotNull(message = "Specialization ID cannot be null")
        @Positive(message = "Specialization ID must be a positive number")
        Long specializationId ,

        @NotNull(message = "Active status cannot be null")
        Boolean isActive
) implements UserUpdateRequest {
}
