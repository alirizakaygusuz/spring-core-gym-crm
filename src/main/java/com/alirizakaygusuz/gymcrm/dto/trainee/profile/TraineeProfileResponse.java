package com.alirizakaygusuz.gymcrm.dto.trainee.profile;

import com.alirizakaygusuz.gymcrm.dto.trainer.profile.TrainerProfileSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(
        name = "TraineeProfileResponse",
        description = "Response object containing detailed profile information of a trainee."
)
public record TraineeProfileResponse(

        @Schema(
                description = "Trainee's first name.",
                example = "John"
        )
        String firstName,

        @Schema(
                description = "Trainee's last name.",
                example = "Doe"
        )
        String lastName,

        @Schema(
                description = "Trainee's date of birth (ISO-8601, yyyy-MM-dd).",
                example = "1990-01-01",
                required = false
        )
        LocalDate dateOfBirth,

        @Schema(
                description = "Trainee's address.",
                example = "123 Main St, Anytown, USA",
                required = false
        )
        String address,

        @Schema(
                description = "Indicates whether the trainee profile is active.",
                example = "true"
        )
        boolean isActive,

        @Schema(
                description = "List of trainers assigned to the trainee.",
                required = false
        )
        List<TrainerProfileSummaryResponse> trainers
) {
}
