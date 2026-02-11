package com.alirizakaygusuz.gymcrm.dto.training;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record TrainingCreateRequest(
        @NotBlank(message = "Trainee username is required")
        @Size(min = 3, max = 150, message = "Trainee username must be between 3 and 150 characters")
        String traineeUsername,

        @NotBlank(message = "Trainer username is required")
        @Size(min = 3, max = 150, message = "Trainer username must be between 3 and 150 characters")
        String trainerUsername,

        @NotBlank(message = "Training name is required")
        @Size(min = 3, max = 100, message = "Training name must be between 3 and 200 characters")
        String trainingName ,

        @NotNull(message = "Training date is required")
        LocalDate trainingDate,

        @NotNull(message = "Training duration is required")
        @Positive(message = "Training duration must be a positive number")
        int trainingDuration
) {
}
