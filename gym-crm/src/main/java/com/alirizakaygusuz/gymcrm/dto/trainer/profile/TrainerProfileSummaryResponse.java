package com.alirizakaygusuz.gymcrm.dto.trainer.profile;

import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TrainerProfileSummaryResponse",
        description = "Summary information of a trainer profile, typically used in trainee-related responses."
)
public record TrainerProfileSummaryResponse(

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
                description = "Trainer's specialization area.",
                example = "FITNESS"
        )
        TrainingTypeCode specialization
) {
}
