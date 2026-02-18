package com.alirizakaygusuz.gymcrm.dto.trainer.register;

import com.alirizakaygusuz.gymcrm.dto.common.UserRegisterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(
        name = "TrainerRegisterRequest",
        description = "Request body for registering a new trainer, including personal details and specialization."
)
public record TrainerRegisterRequest(

        @Schema(
                description = "Trainer's first name.",
                example = "Jane",
                required = true
        )
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
        String firstName,

        @Schema(
                description = "Trainer's last name.",
                example = "Smith",
                required = true
        )
        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
        String lastName,

        @Schema(
                description = "Identifier of the trainer's specialization (training type).",
                example = "1",
                required = true
        )
        @NotNull(message = "Specialization ID is required")
        @Positive(message = "Specialization ID must be a positive number")
        Long specializationId

) implements UserRegisterRequest {
}
