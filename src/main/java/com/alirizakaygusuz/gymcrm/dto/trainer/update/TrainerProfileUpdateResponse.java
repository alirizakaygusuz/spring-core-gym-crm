package com.alirizakaygusuz.gymcrm.dto.trainer.update;

import com.alirizakaygusuz.gymcrm.dto.trainee.profile.TraineeProfileSummaryResponse;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "TrainerProfileUpdateResponse",
        description = "Response object returned after successfully updating a trainer's profile."
)
public record TrainerProfileUpdateResponse(

        @Schema(
                description = "Username of the trainer.",
                example = "trainer_jane"
        )
        String username,

        @Schema(
                description = "Trainer's first name.",
                example = "Jane"
        )
        String firstName,

        @Schema(
                description = "Trainer's last name.",
                example = "Smith"
        )
        String lastName,

        @Schema(
                description = "Trainer's area of specialization.",
                example = "CARDIO"
        )
        TrainingTypeCode specialization,

        @Schema(
                description = "Indicates whether the trainer profile is active.",
                example = "true"
        )
        boolean isActive,

        @Schema(
                description = "List of trainees currently assigned to the trainer.",
                required = false
        )
        List<TraineeProfileSummaryResponse> trainees
) {
}
