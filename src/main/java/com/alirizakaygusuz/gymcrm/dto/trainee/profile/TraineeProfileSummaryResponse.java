package com.alirizakaygusuz.gymcrm.dto.trainee.profile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "TraineeProfileSummaryResponse",
        description = "Summary information of a trainee profile, typically used in trainer- or list-related responses."
)
public record TraineeProfileSummaryResponse(

        @Schema(
                description = "Username of the trainee.",
                example = "john.doe"
        )
        String username,

        @Schema(
                description = "Trainee's first name.",
                example = "John"
        )
        String firstName,

        @Schema(
                description = "Trainee's last name.",
                example = "Doe"
        )
        String lastName
) {
}
