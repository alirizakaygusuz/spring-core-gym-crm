package com.alirizakaygusuz.gymcrm.dto.trainee.training;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(
        name = "TraineeTrainingFilterRequest",
        description = "Query parameters used to filter a trainee's training sessions by date range, trainer name, and training type."
)
public record TraineeTrainingFilterRequest(

        @Schema(
                description = "Start date of the training period (inclusive). ISO-8601 format (yyyy-MM-dd).",
                example = "2024-01-01",
                required = false
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate periodFrom,

        @Schema(
                description = "End date of the training period (inclusive). ISO-8601 format (yyyy-MM-dd).",
                example = "2024-12-31",
                required = false
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate periodTo,

        @Schema(
                description = "Full name of the trainer used to filter training sessions.",
                example = "Jane Smith",
                required = false
        )
        String trainerName,

        @Schema(
                description = "Training type code used to filter sessions.",
                example = "CARDIO",
                required = false
        )
        String trainingType
) {
}
