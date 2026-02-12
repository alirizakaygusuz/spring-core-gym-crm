package com.alirizakaygusuz.gymcrm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(
        name = "TrainingCreateRequest",
        description = "Request body for creating a new training session between a trainee and a trainer."
)
public record TrainingCreateRequest(

        @Schema(
                description = "Username of the trainee who will attend the training.",
                example = "john_doe",
                required = true
        )
        @NotBlank(message = "Trainee username is required")
        @Size(min = 3, max = 150, message = "Trainee username must be between 3 and 150 characters")
        String traineeUsername,

        @Schema(
                description = "Username of the trainer who will conduct the training.",
                example = "trainer_jane",
                required = true
        )
        @NotBlank(message = "Trainer username is required")
        @Size(min = 3, max = 150, message = "Trainer username must be between 3 and 150 characters")
        String trainerUsername,

        @Schema(
                description = "Name/title of the training session.",
                example = "Morning Cardio",
                required = true
        )
        @NotBlank(message = "Training name is required")
        @Size(min = 3, max = 100, message = "Training name must be between 3 and 200 characters")
        String trainingName,

        @Schema(
                description = "Date when the training session will take place. ISO-8601 format (yyyy-MM-dd).",
                example = "2024-06-15",
                required = true
        )
        @NotNull(message = "Training date is required")
        LocalDate trainingDate,

        @Schema(
                description = "Duration of the training session in minutes.",
                example = "60",
                required = true
        )
        @NotNull(message = "Training duration is required")
        @Positive(message = "Training duration must be a positive number")
        int trainingDuration
) {
}
