package com.alirizakaygusuz.gymcrm.dto.trainer.training;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(
        name = "TrainerTrainingFilterRequest",
        description = "Query parameters used to filter a trainer's training sessions by date range and trainee name."
)
public record TrainerTrainingFilterRequest(

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
                description = "Full name of the trainee used to filter training sessions.",
                example = "John Doe",
                required = false
        )
        String traineeName
) {
}
