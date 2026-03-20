package com.alirizakaygusuz.gymcrm.dto.trainer.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        name = "TrainerTrainingFilterResponse",
        description = "Response object representing a trainer's training session returned after applying filter criteria."
)
public record TrainerTrainingFilterResponse(

        @Schema(
                description = "Name of the training session.",
                example = "Morning Cardio"
        )
        String trainingName,

        @Schema(
                description = "Date when the training session took place. ISO-8601 format (yyyy-MM-dd).",
                example = "2024-05-20"
        )
        LocalDate trainingDate,

        @Schema(
                description = "Type of the training session.",
                example = "CARDIO"
        )
        TrainingTypeCode trainingType,

        @Schema(
                description = "Duration of the training session in minutes.",
                example = "60"
        )
        int trainingDuration,

        @Schema(
                description = "Full name of the trainee who attended the training.",
                example = "John Doe"
        )
        String traineeName
) {
}
