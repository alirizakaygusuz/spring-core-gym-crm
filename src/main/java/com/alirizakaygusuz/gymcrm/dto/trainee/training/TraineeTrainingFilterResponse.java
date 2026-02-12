package com.alirizakaygusuz.gymcrm.dto.trainee.training;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        name = "TraineeTrainingFilterResponse",
        description = "Response object representing a trainee's training session returned by filtered training queries."
)
public record TraineeTrainingFilterResponse(

        @Schema(
                description = "Name of the training session.",
                example = "Morning Cardio"
        )
        String trainingName,

        @Schema(
                description = "Date of the training session (ISO-8601, yyyy-MM-dd).",
                example = "2024-02-10"
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
                description = "Full name of the trainer who conducted the session.",
                example = "Jane Smith"
        )
        String trainerName

) {
}
