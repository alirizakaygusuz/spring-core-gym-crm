package com.alirizakaygusuz.gymcrm.dto.trainer.update;

import com.alirizakaygusuz.gymcrm.dto.common.UserUpdateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(
        name = "TrainerProfileUpdateRequest",
        description = "Request body for updating a trainer's profile information."
)
public record TrainerProfileUpdateRequest(

        @Schema(
                description = "Trainer's first name.",
                example = "Jane",
                required = true
        )
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @Schema(
                description = "Trainer's last name.",
                example = "Smith",
                required = true
        )
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @Schema(
                description = "Identifier of the trainer's specialization (training type).",
                example = "2",
                required = true
        )
        @NotNull(message = "Specialization ID cannot be null")
        @Positive(message = "Specialization ID must be a positive number")
        Long specializationId,

        @Schema(
                description = "Indicates whether the trainer profile is active.",
                example = "true",
                required = true
        )
        @NotNull(message = "Active status cannot be null")
        Boolean isActive
) implements UserUpdateRequest {
}
